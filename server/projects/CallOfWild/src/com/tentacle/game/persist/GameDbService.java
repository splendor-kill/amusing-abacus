package com.tentacle.game.persist;

import java.sql.Connection;

import com.tentacle.common.persist.DbService;
import com.tentacle.common.persist.DbConnPoolManager;

public class GameDbService extends DbService {
    protected Connection getConn() {
        return DbConnPoolManager.getInst().getGameDbConn();
    }
}
