package com.tentacle.game.persist;

import com.tentacle.common.persist.DbService;
import com.tentacle.common.persist.DbThread;

public class GameDbThread extends DbThread {
    protected DbService newDbService() {
        return new GameDbService();
    }
    
    protected int getBatchCommitSize() {
        return 200;
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
