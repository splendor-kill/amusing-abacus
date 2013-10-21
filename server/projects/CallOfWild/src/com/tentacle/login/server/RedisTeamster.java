package com.tentacle.login.server;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.procedure.TObjectIntProcedure;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.tentacle.login.config.LoginServerConfig;
import com.tentacle.login.config.RedisConfig;
import com.tentacle.login.persist.UserService;

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
        LoginServerConfig cfg = LoginServerConfig.getInst();
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
    
        
    public void loadUserName() {
        Jedis jedis = getJedis();
        if (jedis.get("username.cached").equals("yes")) return;
        
        List<String> allUserName = UserService.queryAllUserName();
        for (String un : allUserName) {
            jedis.sadd("all_user_name", un); 
        }
        jedis.set("username.cached", "yes");
    }
    
    public void loadImeiCount() {
        final Jedis jedis = getJedis();
        if (jedis.get("imei_count.cached").equals("yes")) return;
        
        Map<String, Integer> imeiCount = UserService.queryImei();
        for (Entry<String, Integer> e : imeiCount.entrySet()) {
            jedis.hset("imei_count", e.getKey(), e.getValue().toString());
        }
        jedis.set("imei_count.cached", "yes");
    }
    
    public boolean isUserNameDuplicated(String name) {
        return getJedis().sismember("all_user_name", name);
    }
    
    
}
