package com.tentacle.login.server;

import java.util.Collections;
import java.util.List;

import com.tentacle.common.contract.IReloadable;
import com.tentacle.common.domain.baseinfo.ServerConfigInfo;
import com.tentacle.login.persist.ServerConfigService;

public class GameServerMonitor implements IReloadable {  
    private List<ServerConfigInfo> serversInfo;

    @Override
    public boolean reload() {
        if (serversInfo != null)
            serversInfo.clear();
        serversInfo = ServerConfigService.queryServerList();
        Collections.reverse(serversInfo);
        return true;
    }

    @Override
    public String getName() {        
        return "game-server-monitor";
    }

    private static class LazyHolder {
        public static final GameServerMonitor INSTANCE = new GameServerMonitor();
    }
        
    public static GameServerMonitor getInst() {
        return LazyHolder.INSTANCE;
    }
    
    private GameServerMonitor() {
        // singleton
    }

    public List<ServerConfigInfo> getServersInfo() {
        return serversInfo;
    }
    
}
