package com.tentacle.callofwild.lobby;

import static org.jboss.netty.channel.Channels.pipeline;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.tentacle.callofwild.domain.baseinfo.UsersInfo;
import com.tentacle.callofwild.persist.base.DaoLoginTread;
import com.tentacle.callofwild.persist.baseservice.UsersService;
import com.tentacle.callofwild.protocol.MyCodec;
import com.tentacle.callofwild.util.Utils;

public class LoginServer {
    private static final Logger logger = Logger.getLogger(LoginServer.class);
    
    private ReentrantLock mLoopLock = new ReentrantLock();
    private Condition wailCondition = mLoopLock.newCondition();
    private ChannelGroup allChannels = new DefaultChannelGroup();
    private boolean mIsStop = false;
    private int listenPort;
    private int connectionClearMinute;
    private long connectionOssifyMs;
    private ServerBootstrap bootstrap;
    // channel --> the moment of the channel open
    private ConcurrentMap<Channel, Long> liveChannel = new ConcurrentHashMap<Channel, Long>();
    
    public void init() {
        parseConfig();
        loadUserInfo();
        DaoLoginTread.getInstance().startDaoThread();
        startNet();
        startSlaughterZombie();
    }
    
    private void parseConfig() {
        String str = Utils.getConfig().getProperty("login_server.listening_port");
        listenPort = Integer.parseInt(str);
        str = Utils.getConfig().getProperty("login_server.connection_clear_minute", "30");
        connectionClearMinute = Integer.parseInt(str);
        str = Utils.getConfig().getProperty("login_server.connection_ossify_minute", "5");
        connectionOssifyMs = Integer.parseInt(str) * 60 * 1000;
    }
    
    public void addChannel(Channel ch) {
        allChannels.add(ch);
        logger.debug("add channel" + ch + ", cur num[" + getChannelSize() + "]");
    }
    
    public int getChannelSize() {
        return allChannels.size();
    }
 
    public void stop() {
        mIsStop = true;
        try {
            mLoopLock.lock();
            wailCondition.signal();
            logger.debug("login-server stop signal...");
        } catch (Exception e) {
            logger.error("login-server stop excption..." + e.getMessage());
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
                logger.error("login-server loopLock excption..." + e.getMessage());
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
            if (now - cur.getValue() > connectionOssifyMs) {
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
        DaoLoginTread.getInstance().awaitTerm();
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
        }, 1, connectionClearMinute, TimeUnit.MINUTES);
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
        
        Channel ch = bootstrap.bind(new InetSocketAddress(listenPort));
        addChannel(ch);
        logger.info("login-server init completed, wait your command...");
    }
    
    /**
     * 载入用户信息
     */
    private void loadUserInfo() {
        logger.info("load pending UserInfo...");
        long snap = System.currentTimeMillis();
        // 载入所有的用户信息
        ArrayList<UsersInfo> retList = new ArrayList<UsersInfo>();
        UsersService.synQueryAll(retList);
        String imei = null;
        for (UsersInfo usersInfo : retList) {
            // 放入到内存中
            UserInfoManager.inst().putUsersInfo(usersInfo);
            // IME号数目
            imei = usersInfo.getPhoneImei();
            // 有效的IMEI号
            if (imei != null && imei.length() > UserInfoManager.IMEI_MAX_LENGTH) {
                UserInfoManager.inst().putImei(usersInfo.getPhoneImei());
            }
        }

        int maxId = UsersService.synQueryMaxId();
        UserInfoManager.inst().setCurMaxUserId(maxId);
        // 清理
        retList.clear();
        logger.debug("load pending UserInfo spend [" + (System.currentTimeMillis() - snap) + "] ms");
    }

    
    public static void main(String[] args) {
//        PropertyConfigurator.configure(Consts.LOG_FILE_PATH);
        LoginServer loginServer = new LoginServer();
        try {
            loginServer.init();
            loginServer.MainLoop();
            loginServer.exit();
        } catch (Exception e) {
            logger.error(e);
        }
        System.exit(0);
    }

    
}
