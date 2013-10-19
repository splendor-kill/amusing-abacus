package com.tentacle.login.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.tentacle.common.contract.IReloadable;
import com.tentacle.common.util.Utils;


public final class LoginServerConfig implements IReloadable {
    private static final Logger logger = Logger.getLogger(LoginServerConfig.class);
    private static final String INBORN_PORTAL_CONFIG = "config/config.properties";
    
    private int listenPort;
    private int connectionClearMinute;
    private long connectionOssifyMs;
    private String adminName;
    private String adminKey;
    private String defaultChannelId;
    private int maxNumOfUsersOnSameDevice;
    private List<String> whiteDevices;
    private List<String> prepaidCardPartner;
    private long prepaidCardOpenTime;
    private long prepaidCardCloseTime;

    private static class LazyHolder {
        public static final LoginServerConfig INSTANCE = new LoginServerConfig();
    }
        
    public static LoginServerConfig getInst() {
        return LazyHolder.INSTANCE;
    }
    
    private LoginServerConfig() {
        //singleton
    }
   
    public static void main(String[] args) {
        PropertyConfigurator.configure(Utils.INBORN_LOG_CONFIG);
        LoginServerConfig inst = LoginServerConfig.getInst();
        boolean isOk = inst.reload();
        if (!isOk) {
            System.out.println("cannot reload["+inst.getName()+"].");
            return;
        }
        System.out.println(inst.getAdminKey());
        System.out.println(inst.getAdminName());
        System.out.println(inst.getConnectionClearMinute());
        System.out.println(inst.getConnectionOssifyMs());
        System.out.println(inst.getDefaultChannelId());
        System.out.println(inst.getListenPort());
        System.out.println(inst.getMaxNumOfUsersOnSameDevice());
        System.out.println(inst.getPrepaidCardCloseTime());
        System.out.println(inst.getPrepaidCardOpenTime());
        System.out.println(inst.getPrepaidCardPartner());
        System.out.println(inst.getWhiteDevices());
    }

    private Properties read() throws IOException {
        Properties p = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(INBORN_PORTAL_CONFIG);
            p.load(fis);
        } finally {
            if (fis != null)
                fis.close();
        }
        return p;
    }
    
    private void parse(Properties p) {
        String str = p.getProperty("portal.listening_port");
        listenPort = Integer.parseInt(str);
        str = p.getProperty("portal.connection_clear_minute", "10");
        connectionClearMinute = Integer.parseInt(str);
        str = p.getProperty("portal.connection_ossify_minute", "5");
        connectionOssifyMs = Integer.parseInt(str) * 60 * 1000;            
        adminName = p.getProperty("portal.admin_name", "admin");
        adminKey = p.getProperty("portal.admin_key", "");            
        defaultChannelId = p.getProperty("portal.default_channel_id", "1234567890");            
        str = p.getProperty("portal.max_num_of_users_on_same_device", "5");
        maxNumOfUsersOnSameDevice = Integer.parseInt(str);
        str = p.getProperty("portal.device_white_list", "");
        whiteDevices = Arrays.asList(str.split("[;,]"));
        str = p.getProperty("portal.prepaid_card_partner", "");
        prepaidCardPartner = Arrays.asList(str.split("[;,]"));
        str = p.getProperty("portal.prepaid_card_time_open", "");
        prepaidCardOpenTime = (long) Utils.getTimePeriodInMs(str);
        str = p.getProperty("portal.prepaid_card_time_close", "");
        prepaidCardCloseTime = (long) Utils.getTimePeriodInMs(str);
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
        return "portal-config";
    }
        
    public int getListenPort() {
        return listenPort;
    }

    public int getConnectionClearMinute() {
        return connectionClearMinute;
    }

    public long getConnectionOssifyMs() {
        return connectionOssifyMs;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getAdminKey() {
        return adminKey;
    }

    public String getDefaultChannelId() {
        return defaultChannelId;
    }

    public int getMaxNumOfUsersOnSameDevice() {
        return maxNumOfUsersOnSameDevice;
    }

    public List<String> getWhiteDevices() {
        return whiteDevices;
    }

    public List<String> getPrepaidCardPartner() {
        return prepaidCardPartner;
    }

    public long getPrepaidCardOpenTime() {
        return prepaidCardOpenTime;
    }

    public long getPrepaidCardCloseTime() {
        return prepaidCardCloseTime;
    }

}
