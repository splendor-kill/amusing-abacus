package com.tentacle.login.persist;

import java.sql.Connection;

import com.tentacle.common.persist.DbService;
import com.tentacle.common.persist.DbConnPoolManager;

public class LoginDbService extends DbService {
    protected Connection getConn() {
        return DbConnPoolManager.getInst().getLoginDbConn();
    }
}
