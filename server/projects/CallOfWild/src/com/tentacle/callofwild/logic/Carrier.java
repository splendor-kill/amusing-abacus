package com.tentacle.callofwild.logic;

import static org.jboss.netty.channel.Channels.pipeline;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.tentacle.callofwild.protocol.MyCodec;
import com.tentacle.callofwild.util.Utils;

public class Carrier {
	private static final Logger logger = Logger.getLogger(Carrier.class);
	
//	public static final int MIN_READ_BUFFER_SIZE = 64;
//	public static final int INITIAL_READ_BUFFER_SIZE = 16384;
//	public static final int MAX_READ_BUFFER_SIZE = 65536;

	private ChannelGroup allChannels = new DefaultChannelGroup();
	private ServerBootstrap bootstrap;
	private ClientBootstrap bootstrapToLogin;
	private InetSocketAddress loginServerAddress;
	private Channel channel2LoginServer;
	private int numOfConnectionToLoginServer = 0;
	
	public Carrier() throws IOException {
        ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline p = pipeline();
                p.addLast("decoder", new MyCodec.MyDecoder());
                p.addLast("encoder", new MyCodec.MyEncoder());
                p.addLast("handler", new GameServerHandler());
                return p;
            }
        };
	    
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(pipelineFactory);
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
//		bootstrap.setOption("child.receiveBufferSizePredictorFactory",
//				new AdaptiveReceiveBufferSizePredictorFactory(
//						MIN_READ_BUFFER_SIZE, 
//						INITIAL_READ_BUFFER_SIZE,
//						MAX_READ_BUFFER_SIZE));
				
		int listeningPort = 0;
		try {
		    String str = Utils.getConfig().getProperty("game_server.listening_port", "");
			listeningPort = Integer.parseInt(str);			
			String loginServerHost = Utils.getConfig().getProperty("login_server.ipv4", "127.0.0.1");
			str = Utils.getConfig().getProperty("login_server.listening_port", "");
			int loginServerPort = Integer.parseInt(str);
		    loginServerAddress = new InetSocketAddress(loginServerHost, loginServerPort);
		} catch (NumberFormatException e) {
			logger.error("the game server port number cfg error.");
		}		
		Channel serverChannel = bootstrap.bind(new InetSocketAddress(listeningPort));
		allChannels.add(serverChannel);      
		
		bootstrapToLogin = new ClientBootstrap(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));
		bootstrapToLogin.setPipelineFactory(pipelineFactory);		
//		bootstrapToLogin.setOption("remoteAddress", loginServerAddress);
		bootstrapToLogin.setOption("tcpNoDelay", true);
		bootstrapToLogin.setOption("keepAlive", true);
		
		System.out.println("network init done.");
	}
	
	public void close() { 
		allChannels.close().awaitUninterruptibly();
		bootstrap.releaseExternalResources();		
		bootstrapToLogin.releaseExternalResources();
	}
	
	public boolean connectToLoginServer() {
	    final int timeout_in_ms = 1000 * 90;
	    ChannelFuture future = bootstrapToLogin.connect(loginServerAddress);
	    boolean isCompleted = future.awaitUninterruptibly(timeout_in_ms);
	    if (isCompleted && future.isSuccess()) {
	        channel2LoginServer = future.getChannel();
	        addChannel(channel2LoginServer);
	        return true;
	    }
	    return false;
	}
	
	public Channel getChannel2LoginServer() {
	    if (channel2LoginServer != null && channel2LoginServer.isConnected())
	        return channel2LoginServer;
        if (channel2LoginServer != null)
            channel2LoginServer.close();
        boolean isOk = connectToLoginServer();
        logger.info("connect to login-server is["+isOk+"], it has been connect[" + (numOfConnectionToLoginServer++) + "] times");
        return channel2LoginServer;
	}
	
	public void addChannel(Channel ch) {
	    allChannels.add(ch);
	}
	
	public boolean isFromLoginServer(Channel ch) {
	    return channel2LoginServer.compareTo(ch) == 0;
	}
	
}
