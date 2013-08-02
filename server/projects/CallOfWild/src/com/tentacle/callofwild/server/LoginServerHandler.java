package com.tentacle.callofwild.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tentacle.callofwild.contact.IReloadable;
import com.tentacle.callofwild.designer.VersionCfg;
import com.tentacle.callofwild.domain.baseinfo.ServerConfigInfo;
import com.tentacle.callofwild.domain.baseinfo.UsersInfo;
import com.tentacle.callofwild.persist.baseservice.UsersService;
import com.tentacle.callofwild.protocol.MyCodec.Cocoon;
import com.tentacle.callofwild.protocol.ProtoAdmin.SysCommonReq;
import com.tentacle.callofwild.protocol.ProtoAdmin.SysGetSession;
import com.tentacle.callofwild.protocol.ProtoAdmin.SysGetSessionAns;
import com.tentacle.callofwild.protocol.ProtoAdmin.SysPwdReset;
import com.tentacle.callofwild.protocol.ProtoAdmin.SysRefreshServerStatus;
import com.tentacle.callofwild.protocol.ProtoAdmin.SysReloadCfg;
import com.tentacle.callofwild.protocol.ProtoAdmin.Warrant;
import com.tentacle.callofwild.protocol.ProtoBasis.CommonAnswer;
import com.tentacle.callofwild.protocol.ProtoBasis.CommonReq;
import com.tentacle.callofwild.protocol.ProtoBasis.Instruction;
import com.tentacle.callofwild.protocol.ProtoBasis.InstructionOrBuilder;
import com.tentacle.callofwild.protocol.ProtoBasis.eCommand;
import com.tentacle.callofwild.protocol.ProtoBasis.eErrorCode;
import com.tentacle.callofwild.protocol.ProtoLogin.Account;
import com.tentacle.callofwild.protocol.ProtoLogin.AccountAnswer;
import com.tentacle.callofwild.protocol.ProtoLogin.AccountReq;
import com.tentacle.callofwild.protocol.ProtoLogin.Authentication;
import com.tentacle.callofwild.protocol.ProtoLogin.ServerListAnswer;
import com.tentacle.callofwild.protocol.ProtoLogin.VersionInfo;
import com.tentacle.callofwild.util.MD5;
import com.tentacle.callofwild.util.Utils;

public class LoginServerHandler extends SimpleChannelUpstreamHandler {
	private static final Logger logger = Logger.getLogger(LoginServerHandler.class);
	
//	private static ServerConfigService serverConfigService = new ServerConfigService();
	private static List<ServerConfigInfo> serversInfo;
	
	private static int maxNumOfUsersOnSameDevice = 5;
    // file name --> config file
    private static Set<IReloadable> reloadableFiles = new HashSet<IReloadable>();
	private static String defaultChannelId;
    private static String adminName;
	private static String adminKey;	
    // user id --> session    
    private static ConcurrentMap<Integer, Session> sessionMgr = new ConcurrentHashMap<Integer, Session>();
    private static List<String> whiteDevices;
    private static List<String> prepaidCardPartner;
    private static long prepaidCardOpenTime;
    private static long prepaidCardCloseTime;
    
    private static Map<String, String> channel2Platform = new HashMap<String, String>() {
        private static final long serialVersionUID = 8761755010342812032L;
        {

        }
    };
    
    private LoginServer loginServer;
    
    static {
        reloadableFiles.add(VersionCfg.getInstance());
        adminName = Utils.getConfig().getProperty("admin_name", "admin");
        adminKey = Utils.getConfig().getProperty("admin_key", "");
        adminKey = MD5.Md5(adminKey);
        defaultChannelId = Utils.getConfig().getProperty("default_channel_id", "201203");
        String tmp = Utils.getConfig().getProperty("max_num_of_users_on_same_device", "5");
        try {
            maxNumOfUsersOnSameDevice = Integer.parseInt(tmp);
            tmp = Utils.getConfig().getProperty("device_white_list", "");
            whiteDevices = Arrays.asList(tmp.split("[;,]"));
            tmp = Utils.getConfig().getProperty("prepaid_card_partner", "");
            prepaidCardPartner = Arrays.asList(tmp.split("[;,]"));
            tmp = Utils.getConfig().getProperty("prepaid_card_time_open", "");
            prepaidCardOpenTime = (long) Utils.getTimePeriodInMs(tmp);
            tmp = Utils.getConfig().getProperty("prepaid_card_time_close", "");
            prepaidCardCloseTime = (long) Utils.getTimePeriodInMs(tmp);            
        } catch (RuntimeException e) {
        }
//        serversInfo = serverConfigService.queryServerList();
        Collections.reverse(serversInfo);
    }

    private static IReloadable getReloadable(String name) {
        for (IReloadable r : reloadableFiles) {
            if (r.getName().equals(name))
                return r;
        }
        return null;
    }

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
			case GET_SERVER_LIST:
				handleGetServerList(ch, CommonReq.parseFrom(cocoon.dat));
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
				handleSysGetSession(ch, SysGetSession.parseFrom(cocoon.dat));
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
				logger.debug("what are you doing?");
				ch.close();
//				ctx.sendUpstream(e);
				return;
			}
		} catch (InvalidProtocolBufferException ex) {
			logger.error("decode", ex);
		}  catch (Exception ex) {
            logger.error(ex);            
        } finally {
			long offTime = System.currentTimeMillis() - startTime;
			//出现超过一秒的命令
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
		if (proof != null && proof.getAdminName().equals(adminName)
				&& proof.getCachet().equals(adminKey)) {
			return true;
		}
        logger.error("no unauthorized access");
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
		        channel = defaultChannelId;
		    }
			VersionCfg verCfg = VersionCfg.getInstance().getVersionInfo(channel, data.getPlatformDesc());
			if (verCfg == null) {
				ans.setErrCode(eErrorCode.YOU_DONT_HAVE_SATISFY_ME);
				break;
			}
			
			ans.setMajorVer(verCfg.getMajorVer())
				.setMinorVer(verCfg.getMinorVer())
				.setAppOrRes(verCfg.getAppRes())
				.setIsCompulsive((verCfg.getCompulsive() == 1));
			
			String str = verCfg.getPkgUrl();
			if (str != null) ans.setPkgUrl(str);
			str = verCfg.getAppUrl();
			if (str != null) ans.setAppUrl(str);
			str = verCfg.getHelpUrl();
			if (str != null) ans.setHelpUrl(str);
			str = verCfg.getActivityUrl();
			if (str != null) ans.setActivityUrl(str);
			str = verCfg.getRechargeNotifyUrl();
			if (str != null) ans.setRechargeNotifyUrl(str);
			str = verCfg.getTempCredentialUrl();
			if (str != null) ans.setTempCredentialUrl(str);
			
            if (prepaidCardPartner.contains(channel)) {
                boolean enable = Utils.timeInPeriod(System.currentTimeMillis(), prepaidCardOpenTime, prepaidCardCloseTime);
                ans.setTokenCredentialUrl(enable ? "open" : "closed");
            } else {
                str = verCfg.getTokenCredentialUrl();
                if (str != null) ans.setTokenCredentialUrl(str);
            }
			
		} while (false);

		Instruction ins = ans.getCmd();
		eCommand cmd = ins.getCmd();
		long cmdId = ins.getId();
		Cocoon wrapper = new Cocoon(cmd.getNumber(), cmdId, ans.build().toByteArray());
		ch.write(wrapper);
		logger.debug("[" + cmd + "]: send[" + ans.getErrCode() + "] to [" + ch.getRemoteAddress() + "]");
	}
	
	 
	
	public void handleAuth(Channel ch, Authentication data) {
		CommonAnswer.Builder ans = CommonAnswer.newBuilder()
				.setErrCode(eErrorCode.OK)
				.setCmd(data.getCmd());
		
		String channel = data.getAccount().getChannel();
		if (channel == null || channel.trim().isEmpty()) {
            channel = defaultChannelId;
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
		UsersInfo userInfo = new UsersInfo();
		String name = acc.getName();		
		//有重名
		if (UserInfoManager.inst().getUsersInfo(name) != null) {
			return eErrorCode.NAME_DUPLICATED;
		}
		
		String imei = acc.getPhoneImei();
		//有效的IMEI
		if (imei != null && imei.length() > UserInfoManager.IMEI_MAX_LENGTH) {
		    if (!whiteDevices.contains(imei) && UserInfoManager.inst().getImeiNum(imei) >= maxNumOfUsersOnSameDevice) {
	            return eErrorCode.TOO_MANY_GHOST_PROFILE;
			}
			//IMEI计数
			UserInfoManager.inst().putImei(imei);
		}
        //设置新用户ID 
		userInfo.setId(UserInfoManager.inst().getNextUserId());
		userInfo.setUserName(name);
		userInfo.setPassword(MD5.Md5(acc.getPassword()));
		userInfo.setEmail(acc.getEmail());
		userInfo.setCardId(acc.getIdCardNo());
		userInfo.setPhoneNumber(acc.getPhoneNo());
		userInfo.setRegeditDate(new Date());
		userInfo.setPlatform(acc.getPlatform());
		userInfo.setUid(acc.getUid());
		userInfo.setAuthCode(acc.getAuthCode());
		userInfo.setChannel(acc.getChannel());
		userInfo.setPhoneModel(acc.getPhoneModel());
		userInfo.setPhoneResolution(acc.getPhoneResolution());
		userInfo.setPhoneOs(acc.getPhoneOs());
		userInfo.setPhoneManufacturer(acc.getPhoneManufacturer());
		userInfo.setPhoneImei(acc.getPhoneImei());
		userInfo.setPhoneMac(acc.getPhoneMac());
		//放入用户到内存中
		UserInfoManager.inst().putUsersInfo(userInfo);
		//保存用户到数据库
		UsersService.asynSave(userInfo);
		
		return eErrorCode.OK;
	}
	
	public void handleLogin(Channel ch, AccountReq accountReqDat) {
		Account acc = accountReqDat.getAccount();
		String name = acc.getName();
		String pass = acc.getPassword();	
		String clientVer = acc.getClientVersion();

		AccountAnswer.Builder ans = AccountAnswer.newBuilder()
				.setCmd(makeCmd(accountReqDat.getCmd()))
				.setErrCode(eErrorCode.OK);
		
		do {
            UsersInfo user = UserInfoManager.inst().getUsersInfo(name);
            //无此用户
            if (null == user) {
                ans.setErrCode(eErrorCode.ERR_NAME_OR_PASSWORD).setName(name);
                break;
            }
            //错误密码
            String password = MD5.Md5(pass);
            if (!password.equals(user.getPassword())) {
            	ans.setErrCode(eErrorCode.ERR_NAME_OR_PASSWORD).setName(name);
                break;
            }
            
            int userId = user.getId();
            Session ss = acquireSession(userId);
            String sessionKey = ss.getSessionKey();
            if (clientVer == null || clientVer.isEmpty()) {
//                sessionKey = Consts.str_old_ver_do_not_support_session_key;
                ss.setSessionKey(sessionKey);
            } else {
                double ver = 0.0;
                try {
                    ver = Double.parseDouble(clientVer);
                } catch (NumberFormatException e) {
                    ans.setErrCode(eErrorCode.YOU_DONT_HAVE_SATISFY_ME);
                    break;
                }
//                if (ver < Consts.client_version_start_support_session_key) {
//                    sessionKey = Consts.str_old_ver_do_not_support_session_key;
//                    ss.setSessionKey(sessionKey);
//                }
            }

            ans.setSessionKey(sessionKey).setName(name).setUserId(userId);
            
            if (serversInfo == null) {
                ans.setErrCode(eErrorCode.FAILED);
                break;
            }
            for (ServerConfigInfo cfg : serversInfo) {
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
		AccountAnswer.Builder ans = AccountAnswer.newBuilder()
				.setCmd(makeCmd(accountReqDat.getCmd()))
				.setErrCode(ec);
		
		Instruction ins = ans.getCmd();
		eCommand cmd = ins.getCmd();
		long cmdId = ins.getId();
		Cocoon wrapper = new Cocoon(cmd.getNumber(), cmdId, ans.build().toByteArray());
		ch.write(wrapper);
		logger.debug("[" + cmd + "]: send[" + ans.getErrCode() + "] to [" + ch.getRemoteAddress() + "]");
	}
	
	public void handleGetServerList(Channel ch, CommonReq data) {
		ServerListAnswer.Builder ans = ServerListAnswer.newBuilder()
				.setCmd(makeCmd(data.getCmd()))
				.setErrCode(eErrorCode.OK);

		do {
			if (serversInfo == null) {
				ans.setErrCode(eErrorCode.FAILED);
				break;
			}
			for (ServerConfigInfo cfg : serversInfo) {
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
	
	public void handleSysReloadCfg(Channel ch, SysReloadCfg data) {
		if (!isTheGuyReliable(data.getProof())) {
			ch.close();
			return;
		}

		do {
			String which = data.getWhichCfg();
			IReloadable cfg = getReloadable(which);
			if (cfg != null) {
				boolean retVal = cfg.reload();
                logger.debug("[" + data.getProof().getAdminName() + "] reload[" + which + "] " 
                        + (retVal ? "succ" : "failed") + " at " + (new Date()) + ".");
            }
			
		} while (false);
		
	}
	
	public void handleSrvStatus(Channel ch, SysRefreshServerStatus data) {
		if (data != null) {
			for (ServerConfigInfo cfg : serversInfo) {
				if (cfg.getId() == data.getServerId()) {
					cfg.setSrvTime(System.currentTimeMillis());
					cfg.setGameSrvStatus(data.getBusyDegree());
					break;
				}
			}
		}
	}
	
	public void handleSysGetSession(Channel ch, SysGetSession data) {
//		if (!isTheGuyReliable(data.getProof())) {
//			ch.close();
//			return;
//		}

		SysGetSessionAns.Builder ans = SysGetSessionAns.newBuilder()
				.setCmd(makeCmd(data.getCmd())).setErrCode(eErrorCode.OK);

		int userId = data.getUserId();
		int serverId = data.getServerId();
		do {
			Session ss = sessionMgr.get(userId);
			if (ss == null || ss.isExpired()) {
				ans.setErrCode(eErrorCode.SESSION_KEY_ERR);
				break;
			}
			ans.setUserId(userId).setSessionKey(ss.getSessionKey()).setTimeStamp(ss.getTimeStamp());
		} while (false);

		logger.debug("user[" + userId + "] from[" + serverId + "] acquire session.");

		Instruction ins = ans.getCmd();
		eCommand cmd = ins.getCmd();
		Cocoon wrapper = new Cocoon(cmd.getNumber(), ins.getId(), ans.build().toByteArray());
		ch.write(wrapper);
		logger.debug("[" + cmd + "]: send[" + ans.getErrCode() + "] to [" + ch.getRemoteAddress() + "]");
	}
	
    private Session acquireSession(int userId) {
        Session s = sessionMgr.get(userId);
        long now = System.currentTimeMillis();
        if (s == null) {
            s = new Session();
            s.setUserId(userId);
            s.setSessionKey("" + Utils.rand.nextLong());
            s.setTimeStamp(now);
            sessionMgr.put(userId, s);
        } else if (s.isExpired()) {
            // expired, regenerate
            s.setSessionKey("" + Utils.rand.nextLong());
            s.setTimeStamp(now);
        }
        assert s != null;
        return s;
    }
    
    private static Instruction.Builder makeCmd(InstructionOrBuilder cmd) {
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
        String md5val = MD5.Md5ClientVersion(data.getNewPwd());
        md5val = MD5.Md5(md5val);
        UsersService.resetPwd(userId, md5val);
        UsersInfo user = UserInfoManager.inst().getUsersInfo(userId);
        user.setPassword(md5val);
        logger.error("["+data.getProof().getAdminName()+"] reset password of user["+userId+"]");
    }
	
}
	
