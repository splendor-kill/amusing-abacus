package com.tentacle.callofwild.logic;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tentacle.callofwild.designer.Glossary;
import com.tentacle.callofwild.persist.base.DaoLoginTread;
import com.tentacle.callofwild.persist.base.DaoThread;
import com.tentacle.callofwild.protocol.MyCodec.Cocoon;
import com.tentacle.callofwild.protocol.ProtoAdmin.SysRefreshServerStatus;
import com.tentacle.callofwild.protocol.ProtoAdmin.Warrant;
import com.tentacle.callofwild.protocol.ProtoBasis.Instruction;
import com.tentacle.callofwild.protocol.ProtoBasis.eCommand;
import com.tentacle.callofwild.protocol.ProtoBasis.eErrorCode;
import com.tentacle.callofwild.script.LoadScriptFile;
import com.tentacle.callofwild.util.Utils;

public class GameServer {
	private static final Logger logger = Logger.getLogger(GameServer.class);
	private static GameServer server;

	private String adminName;
	private String adminKey;
	private String scriptDir;
	private int cultureLang;
	private int theServerId;
	private int msgQueueSize;
    private int dbBatchCommitSize;
	private boolean isConsoleEnabled = false;
	private boolean isLoggerEnabled = false;

	private Carrier carrier;   //network
	private LinkedBlockingQueue<ReqDat> reqQ;	//request queue
	private World world;	
	private DaoThread dbThread;
	private boolean isRunning = true;
	
    public static class ReqDat {
        public ReqDat(Channel channel, Cocoon cocoon) {
            this.channel = channel;
            this.cocoon = cocoon;
        }
        public Channel channel;
        public Cocoon cocoon;
    }

    public static GameServer getInstance() {
        if (server == null) {
            synchronized (GameServer.class) {
                GameServer inst = server;
                if (inst == null) {
                    synchronized (GameServer.class) {
                        server = new GameServer();
                    }
                }
            }
        }
        return server;
    }

    private GameServer() {
        world = World.getInstance();
        world.server = this;
    }
	
	private void parseConfig() {
        String strTmp = Utils.getConfig().getProperty("logger_debug", "no");
//        if (Consts.LOGGER_DEBUG.equals(strTmp))
            isConsoleEnabled = true;
        strTmp = Utils.getConfig().getProperty("console_debug", "yes");
//        if (Consts.CONSOLE_DEBUG.equals(strTmp))
            isLoggerEnabled = true;
	    
		strTmp = Utils.getConfig().getProperty("culture_lang", "chs");
        if (strTmp.equals("cht"))
            cultureLang = Glossary.culture_lang_cht;
        else if (strTmp.equals("english"))
            cultureLang = Glossary.culture_lang_en;
        else
            cultureLang = Glossary.culture_lang_chs;
		
        adminName = Utils.getConfig().getProperty("admin_name", "admin");
        adminKey = Utils.getConfig().getProperty("admin_key", "");
        scriptDir = Utils.getConfig().getProperty("script_dir", ".");

        strTmp = Utils.getConfig().getProperty("game_server.id", "1");
        theServerId = Integer.parseInt(strTmp);
        strTmp = Utils.getConfig().getProperty("db_batch_commit_size", "500");
        dbBatchCommitSize = Integer.parseInt(strTmp);
        strTmp = Utils.getConfig().getProperty("msg_queue.size", "50000");
        msgQueueSize = Integer.parseInt(strTmp);
	}
	
	public boolean init() {
        try {
            parseConfig();
            LoadScriptFile.loadScriptFile(scriptDir);
        } catch (Exception e) {
            return false;
        }

        world.init();
        
		dbThread = DaoThread.getInstance();
	    dbThread.startDaoThread();
	    		
		reqQ = new LinkedBlockingQueue<ReqDat>(msgQueueSize);
		
		world.setupTimer();
		world.load();		
		

		DaoLoginTread.getInstance().startDaoThread();
		
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
                 .setServerId(getTheServerId())
                 .setProof(Warrant.newBuilder().setAdminName(world.server.getAdminName()).setCachet(world.server.getAdminKey()));
//		sb.setBusyDegree(Consts.GAMESRVSTATUS.RUN_WELL);
        
		world.send(getCarrier().getChannel2LoginServer(), sb.getCmd(), eErrorCode.OK, sb.build());   
	}
	
	protected void stopSrvStatus() {
		World world = World.getInstance();
		SysRefreshServerStatus.Builder sb = SysRefreshServerStatus.newBuilder()
                 .setCmd(Instruction.newBuilder().setCmd(eCommand.SYS_REFRESH_SERVER_STATUS))
                 .setServerId(getTheServerId())
                 .setProof(Warrant.newBuilder().setAdminName(world.server.getAdminName()).setCachet(world.server.getAdminKey()));
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
        DaoLoginTread.getInstance().awaitTerm();
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
	
	public int getTheServerId() {
	    return theServerId;
	}
	
	public static void main(String[] args) throws IOException {	
//		PropertyConfigurator.configure(Consts.LOG_FILE_PATH);

        final GameServer server = getInstance();
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

    public int getCultureLang() {
        return cultureLang;
    }

    public boolean isConsoleEnabled() {
        return isConsoleEnabled;
    }

    public boolean isLoggerEnabled() {
        return isLoggerEnabled;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getAdminKey() {
        return adminKey;
    }
    
    public boolean addMsg(ReqDat req) throws InterruptedException {
        return reqQ.offer(req, 500L, TimeUnit.MILLISECONDS);
    }
    
    public int getReqQueueSize() {
        return reqQ.size();
    }

    public int getDbBatchCommitSize() {
        return dbBatchCommitSize;
    }
    
}
