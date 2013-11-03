package com.tentacle.login.server;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.tentacle.common.util.Utils;
import com.tentacle.login.config.LoginServerConfig;
import com.tentacle.login.config.RedisConfig;

public class RedisTeamster {
    private static final Logger logger = Logger.getLogger(RedisTeamster.class);
        
    private JedisPool pool;

    private static class LazyHolder {
        public static final RedisTeamster INSTANCE = new RedisTeamster();
    }
        
    public static RedisTeamster getInst() {
        return LazyHolder.INSTANCE;
    }
    
    private RedisTeamster() {
        //singleton
    }    
    
    public boolean init() {
        JedisPool pool = initPool();
        return pool != null;
    }
        
    private JedisPool initPool() {
        JedisPoolConfig config = new JedisPoolConfig();      
        config.setMaxActive(RedisConfig.getMaxActive());
        config.setMaxIdle(RedisConfig.getMaxIdle());
        config.setMaxWait(RedisConfig.getMaxWait());
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        try {
            LoginServerConfig cfg = LoginServerConfig.getInst();
            pool = new JedisPool(config, cfg.getRedisIp(), cfg.getRedisPort(), RedisConfig.getTimeout());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return pool;
    }
    
    public Jedis getJedis() {
        Jedis jedis = null;
        int count = 0;
        do {
            try {
                jedis = pool.getResource();
            } catch (Exception e) {
                logger.error("get redis failed!", e);
                pool.returnBrokenResource(jedis);
            }
            count++;
        } while (jedis == null && count < LoginServerConfig.getInst().getRedisRetryNum());
        return jedis;
    }

    public void closeJedis(Jedis jedis, String ip, int port) {
        if (jedis != null) {
            pool.returnResource(jedis);
        }
    }


    

    
    public boolean existUserName(String name) {
        return getJedis().hexists("all_user_name", name);
    }
    
    public int getImeiNum(String imei) {
        if (!getJedis().hexists("imei_to_num", imei)) {
            return 0;
        }                
        return Integer.parseInt(getJedis().hget("imei_to_num", imei));
    }
    
    public void setImeiNum(String imei) {
        Jedis j = getJedis();
        j.hsetnx("imei_to_num", imei, "0");
        j.hincrBy("imei_to_num", imei, 1);
    }
    
    public int getNextUserId() {
        return getJedis().incr("max_user_id").intValue();
    }
    
    public void saveUserName(final String userName, int userId) {
        final String strUserId = Integer.toString(userId);
        getJedis().hmset("all_user_name", new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;            
            {
                put(userName, strUserId);
            }
        });
    }
    
    public void cacheUserPwd(int userId, final String pwd) {
        Jedis j = getJedis();
        final String strUserId = Integer.toString(userId);
        j.hmset(strUserId, new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;
            {
                // put("user_name", userName);
                put("user_pwd", pwd);
            }
        });
        j.expire(strUserId, RedisConfig.getExpireTime());
    }
    
    public String getUserId(String userName) {
        return getJedis().hget("all_user_name", userName);
    }
    
    public String getUserPwd(String strUserId) {
        return getJedis().hmget(strUserId, "user_pwd").get(0);
    }
    
    public void renewExpireTime(String key) {
        getJedis().expire(key, RedisConfig.getExpireTime());
    }
    
  
    public String getSessionKey(String strUserId) {
        Jedis j = getJedis();
        String handler = "session_key" + strUserId;
        int expireSeconds = LoginServerConfig.getInst().getSessionExpireSec();
        String sessionKey = j.get(handler);
        if (sessionKey != null) {
            j.expire(handler, expireSeconds);
            return sessionKey;
        } else {
            j.setex(handler, expireSeconds, "" + Utils.RAND.nextLong());
            return j.get(handler);
        }

    }
    
}
