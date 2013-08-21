package com.tentacle.callofwild.logic;

import com.tentacle.callofwild.contract.IReloadable;

public final class GameServerConfig implements IReloadable {

    private String adminName;
    private String adminKey;
    private String scriptDir;
    private int cultureLang;
    private int theServerId;
    private int msgQueueSize;
    private int dbBatchCommitSize;
    private boolean isConsoleEnabled = false;
    private boolean isLoggerEnabled = false;
    
    {
        int aa = 1;
        
    }
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean reload() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getName() {
        return "game-server-config";
    }

}
