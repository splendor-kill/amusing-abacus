package com.tentacle.callofwild.persist;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBPoolManager {
	private static final Logger logger = Logger.getLogger(DBPoolManager.class);
	private static ComboPooledDataSource sd_dataSource;
	private static ComboPooledDataSource login_dataSource;
	
	private static DBPoolManager instance = null;
	private static Object lock = new Object();	
	
	private DBPoolManager() {
		resetProperties();
		resetLoginProperties();
	}

    public static DBPoolManager getInstance() {
        if (null == instance) {
            synchronized (lock) {
                if (null == instance) {
                    instance = new DBPoolManager();
                }
            }
        }
        return instance;
    }
	
    private void resetProperties() {
        Properties prop = new Properties();
        try {
            sd_dataSource = null;
            FileInputStream fis = new FileInputStream("c3p0Mysql.properties");
            prop.load(fis);
            sd_dataSource = new ComboPooledDataSource();
            sd_dataSource.setDriverClass(prop.getProperty("sd.driverclass"));
            sd_dataSource.setJdbcUrl(prop.getProperty("sd.url"));
            sd_dataSource.setUser(prop.getProperty("sd.username"));
            sd_dataSource.setPassword(prop.getProperty("sd.password"));
            sd_dataSource.setMaxPoolSize(60);
            sd_dataSource.setMinPoolSize(5);
            sd_dataSource.setInitialPoolSize(10);
            sd_dataSource.setAcquireIncrement(5);
            sd_dataSource.setIdleConnectionTestPeriod(5 * 60);
            sd_dataSource.setMaxIdleTime(60 * 10);
            sd_dataSource.setMaxStatements(1000);
            sd_dataSource.setBreakAfterAcquireFailure(false);
            fis.close();
        } catch (Exception e) {
            logger.error("check the config of data source for the game-server ", e);
        }
    }
	
    private void resetLoginProperties() {
        Properties prop = new Properties();
        try {
            login_dataSource = null;
            FileInputStream fis = new FileInputStream("c3p0Mysql.properties");
            prop.load(fis);
            login_dataSource = new ComboPooledDataSource();
            login_dataSource.setDriverClass(prop.getProperty("login.driverclass"));
            login_dataSource.setJdbcUrl(prop.getProperty("login.url"));
            login_dataSource.setUser(prop.getProperty("login.username"));
            login_dataSource.setPassword(prop.getProperty("login.password"));
            login_dataSource.setMaxPoolSize(10);
            login_dataSource.setMinPoolSize(5);
            login_dataSource.setInitialPoolSize(5);
            login_dataSource.setAcquireIncrement(5);
            login_dataSource.setIdleConnectionTestPeriod(5 * 60);
            login_dataSource.setMaxIdleTime(60 * 10);
            login_dataSource.setMaxStatements(1000);
            login_dataSource.setBreakAfterAcquireFailure(false);
            fis.close();
        } catch (Exception e) {
            logger.error("check the config of data source for the login-server ", e);
        }
    }
	
    public Connection getConnection() {
        Connection con = null;
        try {
            con = sd_dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("***** getConnection SQLException ******", e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return con;
    }

    public Connection getGmAccountConnection() {
        Connection con = null;
        try {
            con = login_dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("***** getConnection SQLException ******", e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return con;
    }

    public void close(Connection con) {
        try {
            if (null != con && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            con = null;
            logger.error(e.getMessage(), e);
        }
    }
    
}
