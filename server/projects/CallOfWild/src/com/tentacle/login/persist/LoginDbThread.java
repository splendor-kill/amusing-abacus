package com.tentacle.login.persist;

import com.tentacle.common.persist.DbService;
import com.tentacle.common.persist.DbThread;

public class LoginDbThread extends DbThread {
    protected DbService newDbService() {
        return new LoginDbService();
    }
    
    protected int getBatchCommitSize() {
        return 200;
    }

    private static class LazyHolder {
        public static final LoginDbThread INSTANCE = new LoginDbThread();
    }
        
    public static LoginDbThread getInst() {
        return LazyHolder.INSTANCE;
    }
    
    private LoginDbThread() {
        //singleton
    }
}
