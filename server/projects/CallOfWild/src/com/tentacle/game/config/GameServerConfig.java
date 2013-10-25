package com.tentacle.game.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.tentacle.common.contract.IReloadable;
import com.tentacle.common.util.Utils;
import com.tentacle.game.designer.Glossary;

public class GameServerConfig implements IReloadable {
    private static final Logger logger = Logger.getLogger(GameServerConfig.class);
    private static final String INBORN_GAME_SERVER_CONFIG = "config/config.properties";
    
    private String adminName;
    private String adminPwd;
    private String scriptDir;
    private int cultureLang;
    private int theServerId;
    private int msgQueueSize;
    private int dbBatchCommitSize;
    private int gameServerListeningPort;
    private String portalIp;
    private int portalListeningPort;
    
    private static class LazyHolder {
        public static final GameServerConfig INSTANCE = new GameServerConfig();
    }
        
    public static GameServerConfig getInst() {
        return LazyHolder.INSTANCE;
    }
    
    private GameServerConfig() {
        // singleton
    }

    public static void main(String[] args) {
        PropertyConfigurator.configure(Utils.INBORN_LOG_CONFIG);
        GameServerConfig inst = GameServerConfig.getInst();
        boolean isOk = inst.reload();
        if (!isOk) {
            System.out.println("cannot reload["+inst.getName()+"].");
            return;
        }
        System.out.println(inst.getAdminName());
        System.out.println(inst.getAdminPwd());
        System.out.println(inst.getCultureLang());
        System.out.println(inst.getDbBatchCommitSize());
        System.out.println(inst.getMsgQueueSize());
        System.out.println(inst.getName());
        System.out.println(inst.getScriptDir());
        System.out.println(inst.getTheServerId());
    }

    @Override
    public boolean reload() {
        try {
            Properties p = read();
            parse(p);
        } catch (IOException ioex) {
            logger.error(ioex.getMessage(), ioex);
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return "game-server-config";
    }
    
    private Properties read() throws IOException {
        Properties p = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(INBORN_GAME_SERVER_CONFIG);
            p.load(fis);
        } finally {
            if (fis != null)
                fis.close();
        }
        return p;
    }
    
    private void parse(Properties p) {
        adminName = p.getProperty("game_server.admin_name", "admin");
        adminPwd = p.getProperty("game_server.admin_key", "");
        scriptDir = p.getProperty("game_server.script_dir", ".");
        
        String str = p.getProperty("game_server.culture_lang", "chs");
        if (str.equals("cht")) {
            cultureLang = Glossary.CULTURE_LANG_CHT;
        } else if (str.equals("english")) {
            cultureLang = Glossary.CULTURE_LANG_EN;
        } else {
            cultureLang = Glossary.CULTURE_LANG_CHS;
        }

        str = p.getProperty("game_server.id", "1");
        theServerId = Integer.parseInt(str);
        str = p.getProperty("game_server.db_batch_commit_size", "100");
        dbBatchCommitSize = Integer.parseInt(str);
        str = p.getProperty("game_server.msg_queue_size", "1000");
        msgQueueSize = Integer.parseInt(str);        
        str = p.getProperty("game_server.listening_port", "");
        gameServerListeningPort = Integer.parseInt(str);        
        portalIp = p.getProperty("game_server.portal_ip", "localhost");
        str = p.getProperty("game_server.portal_listening_port", "");
        portalListeningPort = Integer.parseInt(str);
       
    }

    public String getAdminName() {
        return adminName;
    }

    public String getAdminPwd() {
        return adminPwd;
    }

    public String getScriptDir() {
        return scriptDir;
    }

    public int getCultureLang() {
        return cultureLang;
    }

    public int getTheServerId() {
        return theServerId;
    }

    public int getMsgQueueSize() {
        return msgQueueSize;
    }

    public int getDbBatchCommitSize() {
        return dbBatchCommitSize;
    }

    public int getGameServerListeningPort() {
        return gameServerListeningPort;
    }

    public String getPortalIp() {
        return portalIp;
    }

    public int getPortalListeningPort() {
        return portalListeningPort;
    }

}
