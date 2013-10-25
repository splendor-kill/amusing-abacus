package com.tentacle.login.persist;

import java.sql.Connection;

import com.tentacle.common.persist.DbConnPoolManager;
import com.tentacle.common.persist.DbThread;
import com.tentacle.login.config.LoginServerConfig;

public class LoginDbThread extends DbThread {
    @Override
    public Connection getConn() {
        return DbConnPoolManager.getInst().getLoginDbConn();
    }
    
    @Override
    public int getBatchCommitSize() {
        return LoginServerConfig.getInst().getDbBatchCommitSize();
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
