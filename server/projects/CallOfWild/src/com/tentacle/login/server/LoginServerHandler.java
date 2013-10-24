package com.tentacle.login.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tentacle.common.contract.IReloadable;
import com.tentacle.common.domain.baseinfo.ServerConfigInfo;
import com.tentacle.common.domain.baseinfo.UserInfo;
import com.tentacle.common.protocol.MyCodec.Cocoon;
import com.tentacle.common.protocol.ProtoAdmin.SysCommonReq;
import com.tentacle.common.protocol.ProtoAdmin.SysGetSession;
import com.tentacle.common.protocol.ProtoAdmin.SysPwdReset;
import com.tentacle.common.protocol.ProtoAdmin.SysRefreshServerStatus;
import com.tentacle.common.protocol.ProtoAdmin.SysReloadCfg;
import com.tentacle.common.protocol.ProtoAdmin.Warrant;
import com.tentacle.common.protocol.ProtoBasis.CommonAns;
import com.tentacle.common.protocol.ProtoBasis.Instruction;
import com.tentacle.common.protocol.ProtoBasis.InstructionOrBuilder;
import com.tentacle.common.protocol.ProtoBasis.eCommand;
import com.tentacle.common.protocol.ProtoBasis.eErrorCode;
import com.tentacle.common.protocol.ProtoLogin.Account;
import com.tentacle.common.protocol.ProtoLogin.AccountAns;
import com.tentacle.common.protocol.ProtoLogin.AccountReq;
import com.tentacle.common.protocol.ProtoLogin.Authentication;
import com.tentacle.common.protocol.ProtoLogin.VersionInfo;
import com.tentacle.common.util.Utils;
import com.tentacle.login.config.LoginServerConfig;
import com.tentacle.login.designer.VersionConfig;
import com.tentacle.login.persist.UserService;

public class LoginServerHandler extends SimpleChannelUpstreamHandler {
	private static final Logger logger = Logger.getLogger(LoginServerHandler.class);
		
    private static Map<String, String> channel2Platform = new HashMap<String, String>() {
        private static final long serialVersionUID = 8761755010342812032L;
        {
        }
    };
    
    private LoginServer loginServer;

    public LoginServerHandler(LoginServer loginServer) {
        super();
        this.loginServer = loginServer;
    }
    
    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {
        loginServer.addChannel(e.getChannel());
    }
    
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        ctx.sendUpstream(e);
        loginServer.kill(e.getChannel());
    }    
    
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		if (!(e.getMessage() instanceof Cocoon)) {
			logger.debug("it isnt my type");
			ctx.sendUpstream(e);
			return;
		}
	
		Cocoon cocoon = (Cocoon) e.getMessage();
		eCommand cmd = eCommand.valueOf(cocoon.cmdType);
		if (cmd == null) {
			logger.debug("what are you doing?");
			ctx.sendUpstream(e);
			return;
		}
		logger.debug("accept cmd: " + cmd);
		long startTime = System.currentTimeMillis();
		Channel ch = e.getChannel();
		try {
			switch (cmd) {
			case LOGIN:
				handleLogin(ch, AccountReq.parseFrom(cocoon.dat));
				loginServer.brand(ch);
				break;
			case REGISTER:
				handleRegister(ch, AccountReq.parseFrom(cocoon.dat));
				loginServer.brand(ch);
				break;
			case GET_VERSION_INFO:
				handleVersionInfo(ch, VersionInfo.parseFrom(cocoon.dat));
				loginServer.brand(ch);
				break;
			case AUTHENTICATE:
				handleAuth(ch, Authentication.parseFrom(cocoon.dat));
				loginServer.brand(ch);
				break;
			case SYS_GET_SESSION:
//				handleSysGetSession(ch, SysGetSession.parseFrom(cocoon.dat));
				break;
			case SYS_RELOAD_CFG:
				handleSysReloadCfg(ch, SysReloadCfg.parseFrom(cocoon.dat));				
				break;
			case SYS_EXIT:
				handleSysExit(ch, SysCommonReq.parseFrom(cocoon.dat));
				break;
			case SYS_REFRESH_SERVER_STATUS:
				handleSrvStatus(ch, SysRefreshServerStatus.parseFrom(cocoon.dat));
				break;
			case SYS_RESET_PASSWORD:
			    handleSysPwdReset(ch, SysPwdReset.parseFrom(cocoon.dat));
			    break;
			default:
				logger.debug("ingore you.");
				ch.close();
				return;
			}
		} catch (InvalidProtocolBufferException ex) {
			logger.error("decode", ex);
		}  catch (Exception ex) {
            logger.error(ex.getMessage(), ex);            
        } finally {
			long offTime = System.currentTimeMillis() - startTime;

			if (offTime > 1000) {
				String recordMsg = "***timeout cmd: [" +cmd+ "***timeout:" + offTime + "ms***" + "] from ["+ch.getRemoteAddress()+"]";
				logger.debug(recordMsg);
			}
			cocoon.dat = null;
			cocoon = null;
		}
	}
	
    public void handleSysExit(Channel ch, SysCommonReq req) {
        if (!isTheGuyReliable(req.getProof())) {
            ch.close();
            return;
        }
        loginServer.stop();
    }
	
	public static boolean isTheGuyReliable(Warrant proof) {
		if (proof != null && proof.getAdminName().equals(LoginServerConfig.getInst().getAdminName())
				&& proof.getCachet().equals(LoginServerConfig.getInst().getAdminPwd())) {
			return true;
		}
        logger.debug("no unauthorized access");
		return false;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		logger.debug("channel" + ctx.getChannel() + " exception, cause["+e.getCause()+"], close it! channel num["+
	loginServer.getChannelSize()+"]exceptionCaught");
		e.getChannel().close();
	}
	
	public void handleVersionInfo(Channel ch, VersionInfo data) {
		VersionInfo.Builder ans = data.toBuilder()
				.setErrCode(eErrorCode.OK);

		do {
		    String channel = data.getChannel();
		    if (channel == null || channel.trim().isEmpty()) {
		        channel = LoginServerConfig.getInst().getDefaultChannelId();
		    }
			VersionConfig verCfg = VersionConfig.getInst().getVersionInfo(channel, data.getPlatformDesc());
			if (verCfg == null) {
				ans.setErrCode(eErrorCode.YOU_DONT_HAVE_SATISFY_ME);
				break;
			}
			
			ans.setVerNum(String.valueOf(verCfg.getMajorVer()))
				.setIsCompulsive((verCfg.getCompulsive() == 1));
			
		} while (false);

		Instruction ins = ans.getCmd();
		eCommand cmd = ins.getCmd();
		long cmdId = ins.getId();
		Cocoon wrapper = new Cocoon(cmd.getNumber(), cmdId, ans.build().toByteArray());
		ch.write(wrapper);
		logger.debug("[" + cmd + "]: send[" + ans.getErrCode() + "] to [" + ch.getRemoteAddress() + "]");
	}
	
	 
	
	public void handleAuth(Channel ch, Authentication data) {
		CommonAns.Builder ans = CommonAns.newBuilder()
				.setErrCode(eErrorCode.OK)
				.setCmd(data.getCmd());
		
		String channel = data.getAccount().getChannelId();
		if (channel == null || channel.trim().isEmpty()) {
            channel = LoginServerConfig.getInst().getDefaultChannelId();
        }
		
		String platform = channel2Platform.get(channel);

		Instruction ins = ans.getCmd();
		eCommand cmd = ins.getCmd();
		long cmdId = ins.getId();
		Cocoon wrapper = new Cocoon(cmd.getNumber(), cmdId, ans.build().toByteArray());
		ch.write(wrapper);
		logger.debug("[" + cmd + "]: send[" + ans.getErrCode() + "] to [" + ch.getRemoteAddress() + "]");
	}

	private eErrorCode saveUser(Account acc) {
		UserInfo userInfo = new UserInfo();
		String name = Utils.normalizeWord(acc.getName());

		if (RedisTeamster.getInst().existUserName(name)) {
			return eErrorCode.NAME_DUPLICATED;
		}
		
        String imei = acc.getPhoneImei();
		int imeiNum = RedisTeamster.getInst().getImeiNum(imei);

		if (imei != null && imei.length() > LoginServerConfig.getInst().getMinLengthOfImei()) {
		    if (!LoginServerConfig.getInst().getWhiteDevices().contains(imei) 
		            && imeiNum >= LoginServerConfig.getInst().getMaxNumOfUsersOnSameDevice()) {
	            return eErrorCode.TOO_MANY_GHOST_PROFILE;
			}
			
		    RedisTeamster.getInst().setImeiNum(imei);
		}

		int userId = RedisTeamster.getInst().getNextUserId();
		userInfo.setId(userId);
		userInfo.setName(name);
		String pwd = Utils.md5(acc.getPassword());
		userInfo.setPwd(pwd);
		userInfo.setEmail(acc.getEmail());
		userInfo.setCardId(acc.getCardId());
		userInfo.setPhoneNo(acc.getPhoneNo());
		userInfo.setRegisterDate(new Date());
		userInfo.setPlatform(acc.getPlatform());
		userInfo.setUid(acc.getUid());
		userInfo.setAuthCode(acc.getAuthCode());
		userInfo.setChannelId(acc.getChannelId());
		userInfo.setPhoneModel(acc.getPhoneModel());
		userInfo.setPhoneResolution(acc.getPhoneResolution());
		userInfo.setPhoneOs(acc.getPhoneOs());
		userInfo.setPhoneManufacturer(acc.getPhoneManufacturer());
		userInfo.setPhoneImei(acc.getPhoneImei());
		userInfo.setPhoneMac(acc.getPhoneMac());
		
		RedisTeamster.getInst().saveUserName(name, userId);
		RedisTeamster.getInst().cacheUserPwd(userId, pwd);
		UserService.asynSave(userInfo);
		
		return eErrorCode.OK;
	}
	
	public void handleLogin(Channel ch, AccountReq accountReqDat) {
		Account acc = accountReqDat.getAccount();
		String name = Utils.normalizeWord(acc.getName());
		String pwd = Utils.md5(acc.getPassword());	
		String clientVer = acc.getClientVersion();

		AccountAns.Builder ans = AccountAns.newBuilder()
				.setCmd(makeCmd(accountReqDat.getCmd()))
				.setErrCode(eErrorCode.OK);
		
		do {		    
            if (!RedisTeamster.getInst().existUserName(name)) {
                ans.setErrCode(eErrorCode.ERR_NAME_OR_PASSWORD).setName(name);
                break;
            }
            
            String strUserId = RedisTeamster.getInst().getUserId(name);
            if (strUserId == null) {
                ans.setErrCode(eErrorCode.ERR_NAME_OR_PASSWORD).setName(name);
                break;
            }
            
            int userId = Integer.parseInt(strUserId);
            
            String userPwd = RedisTeamster.getInst().getUserPwd(strUserId);
            if (userPwd != null) {                
                RedisTeamster.getInst().renewExpireTime(strUserId);
            } else {
                UserInfo info = UserService.queryByUserId(userId);
                if (info == null) {
                    ans.setErrCode(eErrorCode.ERR_NAME_OR_PASSWORD).setName(name);
                    break;
                }
                userPwd = info.getPwd();
                RedisTeamster.getInst().cacheUserPwd(userId, userPwd);
            }
       
            if (!pwd.equals(userPwd)) {
                ans.setErrCode(eErrorCode.ERR_NAME_OR_PASSWORD).setName(name);
                break;
            }
            
            String sessionKey = RedisTeamster.getInst().getSessionKey(strUserId);
            

            ans.setSessionKey(sessionKey).setName(name).setUserId(userId);
            
            for (ServerConfigInfo cfg : GameServerMonitor.getInst().getServersInfo()) {
                ans.addServers(cfg.toNet());
            }
		} while (false);
		
		Instruction ins = ans.getCmd();
		eCommand cmd = ins.getCmd();
		long cmdId = ins.getId();
		Cocoon wrapper = new Cocoon(cmd.getNumber(), cmdId, ans.build().toByteArray());
		ch.write(wrapper);
		logger.debug("[" + cmd + "]: send[" + ans.getErrCode() + "] to [" + ch.getRemoteAddress() + "]");
	}
	
	public void handleRegister(Channel ch, AccountReq accountReqDat) {
		Account acc = accountReqDat.getAccount();
		eErrorCode ec = saveUser(acc);	
		AccountAns.Builder ans = AccountAns.newBuilder()
				.setCmd(makeCmd(accountReqDat.getCmd()))
				.setErrCode(ec);
		
		Instruction ins = ans.getCmd();
		eCommand cmd = ins.getCmd();
		long cmdId = ins.getId();
		Cocoon wrapper = new Cocoon(cmd.getNumber(), cmdId, ans.build().toByteArray());
		ch.write(wrapper);
		logger.debug("[" + cmd + "]: send[" + ans.getErrCode() + "] to [" + ch.getRemoteAddress() + "]");
	}
	
	public void handleSysReloadCfg(Channel ch, SysReloadCfg data) {
        CommonAns.Builder ans = CommonAns.newBuilder()
                .setCmd(makeCmd(data.getCmd())).setErrCode(eErrorCode.OK);
	    
        do {
            if (!isTheGuyReliable(data.getProof())) {
                ans.setErrCode(eErrorCode.UNAUTHORIZED_ACCESS);
                break;
            }

            String which = data.getWhichCfg();
            IReloadable cfg = loginServer.getReloadable(which);
            if (cfg == null) {
                ans.setErrCode(eErrorCode.CONFIG_FILE_NOT_FOUND);
                break;
            }
            if (!loginServer.setReadable(false)) {
                ans.setErrCode(eErrorCode.NETWORK_TOO_HOT_TO_FREEZE);
                return;
            }
            boolean retVal = cfg.reload();
            logger.debug("[" + data.getProof().getAdminName() + "] reload["
                    + which + "] " + (retVal ? "succ" : "failed") + " at "
                    + (new Date()) + ".");
            loginServer.setReadable(true);

        } while (false);
    
        Instruction ins = ans.getCmd();
        eCommand cmd = ins.getCmd();
        long cmdId = ins.getId();
        Cocoon wrapper = new Cocoon(cmd.getNumber(), cmdId, ans.build().toByteArray());
        ch.write(wrapper);
        logger.debug("[" + cmd + "]: send[" + ans.getErrCode() + "] to [" + ch.getRemoteAddress() + "]");
	}
	
	public void handleSrvStatus(Channel ch, SysRefreshServerStatus data) {
		if (data == null)
		    return;
        for (ServerConfigInfo cfg : GameServerMonitor.getInst().getServersInfo()) {
            if (cfg.getId() == data.getServerId()) {
                cfg.setSrvTime(System.currentTimeMillis());
                cfg.setGameSrvStatus(data.getBusyDegree());
                break;
            }
        }
	}
	
	public void handleSysGetSession(Channel ch, SysGetSession data) {
//		if (!isTheGuyReliable(data.getProof())) {
//			ch.close();
//			return;
//		}

//		SysGetSessionAns.Builder ans = SysGetSessionAns.newBuilder()
//				.setCmd(makeCmd(data.getCmd())).setErrCode(eErrorCode.OK);
//
//		int userId = data.getUserId();
//		int serverId = data.getServerId();
//		do {
//			Session ss = sessionMgr.get(userId);
//			if (ss == null || ss.isExpired()) {
//				ans.setErrCode(eErrorCode.SESSION_KEY_ERR);
//				break;
//			}
//			ans.setUserId(userId).setSessionKey(ss.getSessionKey()).setTimeStamp(ss.getTimeStamp());
//		} while (false);
//
//		logger.debug("user[" + userId + "] from[" + serverId + "] acquire session.");
//
//		Instruction ins = ans.getCmd();
//		eCommand cmd = ins.getCmd();
//		Cocoon wrapper = new Cocoon(cmd.getNumber(), ins.getId(), ans.build().toByteArray());
//		ch.write(wrapper);
//		logger.debug("[" + cmd + "]: send[" + ans.getErrCode() + "] to [" + ch.getRemoteAddress() + "]");
	}
	
//    private String acquireSession(String strUserId) {
//        RedisTeamster.getInst().
//        String sessionKey = RedisTeamster.getInst().getSessionKey(strUserId, "" + Utils.RAND.nextLong());
//        
//        long now = System.currentTimeMillis();
//        if (sessionKey == null) {
//        
//            s.setSessionKey("" + Utils.RAND.nextLong());
//            s.setTimeStamp(now);
//            sessionMgr.put(userId, s);
//        } else if (s.isExpired()) {
//            // expired, regenerate
//            s.setSessionKey("" + Utils.RAND.nextLong());
//            s.setTimeStamp(now);
//        }
//        assert s != null;
//        return s;
//    }
    
    private Instruction.Builder makeCmd(InstructionOrBuilder cmd) {
        eCommand newCmd;
        long cmdId;
        try {
            newCmd = cmd.getCmd();
            cmdId = cmd.getId();
        } catch (RuntimeException e) {
            return null;
        }
        return Instruction.newBuilder().setCmd(newCmd).setId(cmdId);
    }
    
    public void handleSysPwdReset(Channel ch, SysPwdReset data) {
        if (!isTheGuyReliable(data.getProof())) {
//            ch.close();
            logger.warn("this guy unreliable");
            return;
        }
        int userId = data.getUserId();
        String md5val = Utils.md5(data.getNewPwd());
        RedisTeamster.getInst().cacheUserPwd(userId, md5val);       
        UserService.resetPwd(userId, md5val);
        logger.error("["+data.getProof().getAdminName()+"] reset password of user["+userId+"]");
    }
	
}
	
