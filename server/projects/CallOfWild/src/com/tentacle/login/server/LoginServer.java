package com.tentacle.login.server;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.tentacle.common.contract.IReloadable;
import com.tentacle.common.protocol.MyCodec;
import com.tentacle.common.util.Utils;
import com.tentacle.login.config.LoginServerConfig;
import com.tentacle.login.designer.VersionConfig;
import com.tentacle.login.persist.LoginDbThread;

public class LoginServer {
    private static final Logger logger = Logger.getLogger(LoginServer.class);
    
    private ReentrantLock mLoopLock = new ReentrantLock();
    private Condition wailCondition = mLoopLock.newCondition();
    private ChannelGroup allChannels = new DefaultChannelGroup();
    private boolean mIsStop;
    private ServerBootstrap bootstrap;
    // channel --> the moment of the channel open
    private ConcurrentMap<Channel, Long> liveChannel = new ConcurrentHashMap<Channel, Long>();
    
    private Set<IReloadable> reloadableFiles = new HashSet<IReloadable>();

    
    private void init() {
        reloadableFiles.add(LoginServerConfig.getInst());
        reloadableFiles.add(VersionConfig.getInst());
        reloadableFiles.add(GameServerMonitor.getInst());
        
        for (IReloadable r : reloadableFiles) {
            if (!r.reload())
                return;
        }
    
        boolean isOk = RedisTeamster.getInst().init();
        if (!isOk) {
            logger.error("init redis failed.");
            return;
        }
        LoginDbThread.getInst().start();
        startNet();
        startSlaughterZombie();
    }
    
    public void addChannel(Channel ch) {
        allChannels.add(ch);
        logger.debug("add channel" + ch + ", cur num[" + getChannelSize() + "]");
    }
    
    public int getChannelSize() {
        return allChannels.size();
    }
    
    public boolean setReadable(boolean readable) {
        ChannelGroupFuture f = allChannels.setReadable(readable);
        try {
            return f.await(LoginServerConfig.getInst().getFreezeNetworkTimeInMs());
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }
 
    public void stop() {
        mIsStop = true;
        try {
            mLoopLock.lock();
            wailCondition.signal();
            logger.debug("login-server stop signal...");
        } catch (Exception e) {
            logger.error("login-server stop excption...", e);
        } finally {
            mLoopLock.unlock();
        }
    }
    
    public void MainLoop() {
        while (!mIsStop) {
            try {
                mLoopLock.lock();
                logger.debug("login-server loopLock wait enter...");
                wailCondition.await();
                logger.debug("login-server loopLock wait out...");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                mLoopLock.unlock();
            }
        }
        logger.debug("login-server MainLoop stop...");
    }
    
    public void brand(Channel ch) {
        liveChannel.putIfAbsent(ch, System.currentTimeMillis());
    }
    
    public void kill(Channel ch) {
        liveChannel.remove(ch);
    }
    
    // kill zombie who long time no action 
    public void slaughter() {
        int before = liveChannel.size();
        long now = System.currentTimeMillis();
        Iterator<Entry<Channel, Long>> it = liveChannel.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Channel, Long> cur = it.next();
            if (now - cur.getValue() > LoginServerConfig.getInst().getConnectionOssifyMs()) {
                cur.getKey().close();
                it.remove();
            }
        }
        logger.debug("channel num: before["+before+"], survive["+liveChannel.size()+"]");
    }
    
    public void exit() {
        logger.info("waiting for releaseExternalResources close...");
        allChannels.close().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
        logger.info("waiting for DB-thread close...");
        LoginDbThread.getInst().awaitTerm();
        logger.info("login-server safe exit system...");
    }
    
    private void startSlaughterZombie() {
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                slaughter();
                logger.debug("channel num["+ getChannelSize()+"], zombie["+liveChannel.size()+"]");
            }
        }, 1, LoginServerConfig.getInst().getConnectionClearMinute(), TimeUnit.MINUTES);
    }
    
    private void startNet() {
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline p = pipeline();
                p.addLast("decoder", new MyCodec.MyDecoder());
                p.addLast("encoder", new MyCodec.MyEncoder());
                p.addLast("handler", new LoginServerHandler(LoginServer.this));
                return p;
            }
        });
        
        Channel ch = bootstrap.bind(new InetSocketAddress(LoginServerConfig.getInst().getListenPort()));
        addChannel(ch);
        logger.info("login-server init completed, wait your command...");
    }
    
    public IReloadable getReloadable(String name) {
        for (IReloadable r : reloadableFiles) {
            if (r.getName().equals(name))
                return r;
        }
        return null;
    }
    
    public static void main(String[] args) {
        PropertyConfigurator.configure(Utils.INBORN_LOG_CONFIG);
        LoginServer loginServer = new LoginServer();
        try {
            loginServer.init();
            loginServer.MainLoop();
            loginServer.exit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        System.exit(0);
    }

    
}
