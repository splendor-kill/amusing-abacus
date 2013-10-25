package com.tentacle.game.persist;

import java.sql.Connection;

import com.tentacle.common.persist.DbConnPoolManager;
import com.tentacle.common.persist.DbThread;
import com.tentacle.game.config.GameServerConfig;

public class GameDbThread extends DbThread {    
    @Override
    public Connection getConn() {
        return DbConnPoolManager.getInst().getGameDbConn();
    }
    
    @Override
    public int getBatchCommitSize() {
        return GameServerConfig.getInst().getDbBatchCommitSize();
    }
    
    private static class LazyHolder {
        public static final GameDbThread INSTANCE = new GameDbThread();
    }
        
    public static GameDbThread getInst() {
        return LazyHolder.INSTANCE;
    }
    
    private GameDbThread() {
        //singleton
    }
}
