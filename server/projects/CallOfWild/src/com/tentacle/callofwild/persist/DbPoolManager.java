package com.tentacle.callofwild.persist;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DbPoolManager {
	private static final Logger logger = Logger.getLogger(DbPoolManager.class);	
    private static final String INBORN_DB_CONFIG = "config/c3p0Mysql.properties";
	
	private static ComboPooledDataSource gameDataSource;
	private static ComboPooledDataSource loginDataSource;
	
	private static class LazyHolder {
        public static final DbPoolManager INSTANCE = new DbPoolManager();
    }
	
    private DbPoolManager() {
        resetGameDbConnProperties();
        resetLoginDbConnProperties();
    }

    public static DbPoolManager getInst() {        
        return LazyHolder.INSTANCE;
    }
	
    private void resetGameDbConnProperties() {
        Properties prop = new Properties();
        FileInputStream fis = null;
        try {
            gameDataSource = null;
            fis = new FileInputStream(INBORN_DB_CONFIG);
            prop.load(fis);
            gameDataSource = new ComboPooledDataSource();
            gameDataSource.setDriverClass(prop.getProperty("game.driverClass"));
            gameDataSource.setJdbcUrl(prop.getProperty("game.url"));
            gameDataSource.setUser(prop.getProperty("game.userName"));
            gameDataSource.setPassword(prop.getProperty("game.password"));
            gameDataSource.setMaxPoolSize(60);
            gameDataSource.setMinPoolSize(5);
            gameDataSource.setInitialPoolSize(10);
            gameDataSource.setAcquireIncrement(5);
            gameDataSource.setIdleConnectionTestPeriod(5 * 60);
            gameDataSource.setMaxIdleTime(60 * 10);
            gameDataSource.setMaxStatements(1000);
            gameDataSource.setBreakAfterAcquireFailure(false);
        } catch (Exception e) {
            logger.error("check the config of data source for the game-server", e);
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (Exception ex) {
                logger.error(ex);
            }
        }
        
    }
	
    private void resetLoginDbConnProperties() {
        Properties prop = new Properties();
        FileInputStream fis = null;
        try {
            loginDataSource = null;
            fis = new FileInputStream(INBORN_DB_CONFIG);
            prop.load(fis);
            loginDataSource = new ComboPooledDataSource();
            loginDataSource.setDriverClass(prop.getProperty("login.driverClass"));
            loginDataSource.setJdbcUrl(prop.getProperty("login.url"));
            loginDataSource.setUser(prop.getProperty("login.userName"));
            loginDataSource.setPassword(prop.getProperty("login.password"));
            loginDataSource.setMaxPoolSize(10);
            loginDataSource.setMinPoolSize(5);
            loginDataSource.setInitialPoolSize(5);
            loginDataSource.setAcquireIncrement(5);
            loginDataSource.setIdleConnectionTestPeriod(5 * 60);
            loginDataSource.setMaxIdleTime(60 * 10);
            loginDataSource.setMaxStatements(1000);
            loginDataSource.setBreakAfterAcquireFailure(false);            
        } catch (Exception e) {
            logger.error("check the config of data source for the login-server", e);
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (Exception ex) {
                logger.error(ex);
            }
        }
    }
	
    public Connection getGameDbConn() {
        Connection conn = null;
        try {
            conn = gameDataSource.getConnection();
        } catch (SQLException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
        return conn;
    }

    public Connection getLoginDbConn() {
        Connection conn = null;
        try {
            conn = loginDataSource.getConnection();
        } catch (SQLException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
        return conn;
    }
    
    public static void close(Connection conn) {
        try {
            if (null != conn) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            conn = null;
            logger.error(e);
        }
    }
       
}
