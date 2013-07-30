package com.tentacle.callofwild.domain.baseinfo;

import java.util.Date;

/**
 * 
 * @author spfu
 * 
 */
public class LoginServerInfo {

	private int id;
	private int serverConfigId;
	private int UsersId;
	private Date loginTime = new Date();
	private Date logoutTime = new Date();
	private String remoteIp;
	private String sessionKey;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getServerConfigId() {
		return serverConfigId;
	}

	public void setServerConfigId(int serverConfigId) {
		this.serverConfigId = serverConfigId;
	}

	public int getUsersId() {
		return UsersId;
	}

	public void setUsersId(int usersId) {
		UsersId = usersId;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Date getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(Date logoutTime) {
		this.logoutTime = logoutTime;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

}
