package com.tentacle.callofwild.domain.baseinfo;

import com.tentacle.callofwild.protocol.ProtoLogin.GameServer;
import com.tentacle.callofwild.util.Consts;

/**
 * 
 * @author spfu
 * 
 *
 */
public class ServerConfigInfo {
	private int id;
	private int port;
	private String ip;
	private String name;
	private boolean isnew;
	private String doaminName;
	//同时游戏服务器状态
	private int curGameSrvStatus = Consts.GAMESRVSTATUS.RUN_WELL;
	//当前在线时间
	private long curSrvTime;
	private String helpUrl;
	private String activityUrl;	

	public void setGameSrvStatus(int srvStatus) {
		curGameSrvStatus = srvStatus;
	}
	
	public int getGameSrvStatus() {
		return curGameSrvStatus;
	}
	
	public void setSrvTime(long srvTime) {
		curSrvTime = srvTime;
	}
	
	public long getSrvTime() {
		return curSrvTime;
	}
	
	public boolean isIsnew() {
		return isnew;
	}

	public void setIsnew(boolean isnew) {
		this.isnew = isnew;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

    public String getIp() {
        return ip == null ? "" : ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name == null ? "" : name;
    }

	public void setName(String name) {
		this.name = name;
	}

	public String getDoaminName() {
		return doaminName == null ? "" : doaminName;
	}

	public void setDoaminName(String doaminName) {
		this.doaminName = doaminName;
	}
	
    public GameServer.Builder toNet() {
        return GameServer.newBuilder().setId(id).setIpv4(ip).setName(name)
                .setBusyDegree(curGameSrvStatus).setPort(port)
                .setIsNew(isIsnew()).setDomainName(getDoaminName())
                .setUrl(getHelpUrl()).setActivityUrl(getActivityUrl());
    }

    public String getHelpUrl() {
        return helpUrl == null ? "" : helpUrl;
    }

    public void setHelpUrl(String helpUrl) {
        this.helpUrl = helpUrl;
    }

    public String getActivityUrl() {
        return activityUrl == null ? "" : activityUrl;
    }

    public void setActivityUrl(String activityUrl) {
        this.activityUrl = activityUrl;
    }

}
