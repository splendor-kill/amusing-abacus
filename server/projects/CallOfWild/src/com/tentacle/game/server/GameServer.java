package com.tentacle.game.server;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tentacle.common.net.Carrier;
import com.tentacle.common.persist.DbThread;
import com.tentacle.common.protocol.MyCodec.Cocoon;
import com.tentacle.common.protocol.ProtoAdmin.SysRefreshServerStatus;
import com.tentacle.common.protocol.ProtoAdmin.Warrant;
import com.tentacle.common.protocol.ProtoBasis.Instruction;
import com.tentacle.common.protocol.ProtoBasis.eCommand;
import com.tentacle.common.protocol.ProtoBasis.eErrorCode;
import com.tentacle.common.script.LoadScriptFile;
import com.tentacle.game.config.GameServerConfig;
import com.tentacle.game.persist.GameDbThread;
import com.tentacle.login.persist.LoginDbThread;

public class GameServer {
	private static final Logger logger = Logger.getLogger(GameServer.class);

	private Carrier carrier;   //network
	private LinkedBlockingQueue<ReqDat> reqQ;	//request queue
	private World world;	
	private DbThread dbThread;
	private boolean isRunning = true;
	
    public static class ReqDat {
        public ReqDat(Channel channel, Cocoon cocoon) {
            this.channel = channel;
            this.cocoon = cocoon;
        }
        public Channel channel;
        public Cocoon cocoon;
    }

    private static class LazyHolder {
        public static final GameServer INSTANCE = new GameServer();
    }
        
    public static GameServer getInst() {
        return LazyHolder.INSTANCE;
    }
    
    private GameServer() {
        world = World.getInstance();
        world.server = this;
    }
	
	public boolean init() {
        try {
            GameServerConfig.getInst().reload();
            LoadScriptFile.loadScriptFile(GameServerConfig.getInst().getScriptDir());
        } catch (Exception e) {
            return false;
        }

        world.init();
        
		dbThread = GameDbThread.getInst();
	    dbThread.start();
	    		
		reqQ = new LinkedBlockingQueue<ReqDat>(GameServerConfig.getInst().getMsgQueueSize());
		
		world.setupTimer();
		world.load();		
		

		LoginDbThread.getInst().start();
		
		try {
			carrier = new Carrier();
			boolean isOk = carrier.connectToLoginServer();
			logger.info("connect to login-server is["+isOk+"]");
		} catch (IOException e) {
			logger.error(e);
			return false;
		}
		
	
        startSrvStatus();
  
        
		return true;
	}
	
	public void stop() {
		isRunning = false;
	}
	
	public void work() {
		long lastTime = System.currentTimeMillis();
		
		long curTime = lastTime;
		
		while (isRunning) {
			lastTime = curTime;
			curTime = System.currentTimeMillis();
			long deltaTime = curTime - lastTime;
			
			try {
				world.progress(deltaTime);
				long offTime = System.currentTimeMillis() - curTime;
				//出现超过一秒的定时业务逻辑
				if (offTime > 1000) {
					String recordMsg = "***timeout world.progress [" + "***timeout:" + offTime + "ms***" +"]";
					logger.info(recordMsg);
				}
				//处理命令
				handle();
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.error("workex died.", ex);
			} catch (Error er) {
				er.printStackTrace();
				logger.error("worker died.", er);
			}
			Thread.yield();
		}
	}

	protected void startSrvStatus() {
		World world = World.getInstance();
		SysRefreshServerStatus.Builder sb = SysRefreshServerStatus.newBuilder()
                 .setCmd(Instruction.newBuilder().setCmd(eCommand.SYS_REFRESH_SERVER_STATUS))
                 .setServerId(GameServerConfig.getInst().getTheServerId())
                 .setProof(Warrant.newBuilder().setAdminName(GameServerConfig.getInst().getAdminName())
                 .setCachet(GameServerConfig.getInst().getAdminPwd()));
//		sb.setBusyDegree(Consts.GAMESRVSTATUS.RUN_WELL);
        
		world.send(getCarrier().getChannel2LoginServer(), sb.getCmd(), eErrorCode.OK, sb.build());   
	}
	
	protected void stopSrvStatus() {
		World world = World.getInstance();
		SysRefreshServerStatus.Builder sb = SysRefreshServerStatus.newBuilder()
                 .setCmd(Instruction.newBuilder().setCmd(eCommand.SYS_REFRESH_SERVER_STATUS))
                 .setServerId(GameServerConfig.getInst().getTheServerId())
                 .setProof(Warrant.newBuilder().setAdminName(GameServerConfig.getInst().getAdminName()).setCachet(GameServerConfig.getInst().getAdminPwd()));
//		sb.setBusyDegree(Consts.GAMESRVSTATUS.HALTED);
        
		world.send(getCarrier().getChannel2LoginServer(), sb.getCmd(), eErrorCode.OK, sb.build());   
	}
	
	public void exit() {
		logger.debug("exiting...");
		logger.debug("stop network...");
		//向登录服务器发送维护
		stopSrvStatus();
		if (carrier != null)
			carrier.close();
		logger.debug("drian out the remaining requests[" + reqQ.size() + "]...");
		if (reqQ != null)
			reqQ.clear();
		
		logger.debug("statistician rest, cardiac arrest.");


        logger.debug("waiting for DB-thread close...");
        dbThread.awaitTerm();
        LoginDbThread.getInst().awaitTerm();
        logger.debug("waiting for ranking close...");

        
        logger.debug("game over.");
	}

	private void handle() throws InterruptedException, InvalidProtocolBufferException {
		final int max_cmd_per_frame = 100; 
		int counter = 0;
		while (!reqQ.isEmpty()) {
			if (counter++ > max_cmd_per_frame)
				break;
			ReqDat reqdat = reqQ.poll(500L, TimeUnit.MILLISECONDS);		
			//为空时
			if (null == reqdat) {
				continue;
			}
			Channel ch = reqdat.channel;			
			Cocoon cocoon = reqdat.cocoon;
			
			eCommand cmd = eCommand.valueOf(cocoon.cmdType);
			if (cmd == null) continue;
//			if (ch != null)
			    logger.debug("accept cmd: ["+cmd+"] from ["+ch.getRemoteAddress()+"]");
			long startTime = System.currentTimeMillis();
			switch (cmd) {
				
                
			default:
				// no impl yet
				break;			
			}
			
			long offTime = System.currentTimeMillis() - startTime;
			//出现超过一秒的命令
			if (offTime > 1000) {
				String recordMsg = "***timeout cmd: [" +cmd+ "***timeout:" + offTime + "ms***" + "] from ["+ch.getRemoteAddress()+"]";
				logger.debug(recordMsg);
			}
			cocoon = null;
		}
	}
	
	public Carrier getCarrier() {
	    return this.carrier;
	}
	

	
	public static void main(String[] args) throws IOException {	
//		PropertyConfigurator.configure(Consts.LOG_FILE_PATH);

        final GameServer server = getInst();
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        
		boolean isOk = server.init();
		if (!isOk) {
			logger.error("init failed.");
			return;
		}
		logger.info("init completed, wait your command...");
		
        logger.info("server ready.");
        server.work();

        server.exit();
        logger.info("server exit.");
	}


    
    public boolean addMsg(ReqDat req) throws InterruptedException {
        return reqQ.offer(req, 500L, TimeUnit.MILLISECONDS);
    }
    
    public int getReqQueueSize() {
        return reqQ.size();
    }

}
