package com.tentacle.login.config;

public class RedisConfig {

    public static int getMaxActive() {
        return 1000 * 60 * 60 * 24 * 10;
    }

    public static int getMaxIdle() {
        return 1000 * 60 * 60 * 24 * 10;
    }

    public static long getMaxWait() {
        return 1000 * 60;
    }
    
    public static int getTimeout() {
        return 1000 * 60 * 5;
    }
    
    public static int getExpireTime() {// in second
        return 60;// * 60 * 24 * 10;
    }
    
}
