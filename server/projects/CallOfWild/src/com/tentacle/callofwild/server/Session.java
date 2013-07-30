package com.tentacle.callofwild.server;

public class Session {
    public static final long timespan_session_key_expire = 1000 * 60 * 10;
    private int userId;
    private String sessionKey;
    private long timeStamp;
    
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getSessionKey() {
        return sessionKey;
    }
    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }
    public long getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() - timeStamp >= timespan_session_key_expire;
    }
}
