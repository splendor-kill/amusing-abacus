package com.tentacle.callofwild.logic;


import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.tentacle.callofwild.protocol.MyCodec.Cocoon;

public class GameServerHandler extends SimpleChannelUpstreamHandler {
	private static final Logger logger = Logger.getLogger(GameServerHandler.class);

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		Channel ch = e.getChannel();
		
		/**记录用户退出**/
//        LoginServerInfo info = new LoginServerInfo();
//        info.setLogoutTime(new Date());
//        info.setRemoteIp(ch.getRemoteAddress().toString());
//        LoginServerService.asynSave(info);
		
		World.getInstance().removeChannel(ch);
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		logger.debug("some message received by thread id["+Thread.currentThread().getId()+"]");
		
		if (!(e.getMessage() instanceof Cocoon)) {
		    logger.debug("it isnt my type");
			ctx.sendUpstream(e);
			return;
		}
			
		Cocoon cocoon = (Cocoon) e.getMessage();
		try {
			GameServer.getInstance().addMsg(new GameServer.ReqDat(e.getChannel(), cocoon));
			logger.debug("queue size["+GameServer.getInstance().getReqQueueSize()+"]");
		} catch (InterruptedException e1) {			
		} catch (Exception ex) {			
		} 	
		
	}

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {
        GameServer.getInstance().getCarrier().addChannel(e.getChannel());
    }
	
	

}
