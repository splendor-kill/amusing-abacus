package com.tentacle.callofwild.persist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

public class BaseDao {
    private static final Logger logger = Logger.getLogger(BaseDao.class);
    
    protected Connection conn = null;
    protected Statement stmt = null;
    protected PreparedStatement pstmt = null;
    protected ResultSet rs = null;

    protected void close(Connection con) {
        try {
            if (null != con && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            con = null;
            logger.error(e.getMessage(), e);
        }
    }

    protected void close(PreparedStatement pstmt) {
        try {
            if (null != pstmt) {
                pstmt.close();
            }
        } catch (SQLException e) {
            pstmt = null;
            logger.error(e.getMessage(), e);
        }
    }

    protected void close(ResultSet rs) {
        try {
            if (null != rs) {
                rs.close();
            }
        } catch (SQLException e) {
            rs = null;
            logger.error(e.getMessage(), e);
        }
    }

    protected ResultSet exeQuery(String sql, Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = stmt.executeQuery(sql);
            }
        } catch (SQLException e) {
            if (con != null) {
                close(con);
            }
            logger.error(e);
        } catch (Exception e) {
            if (con != null) {
                close(con);
            }
            logger.error(e);
        }
        return rs;
    }

    protected int exeUpdate(String sql, Connection con) {
        int retVal = -1;
        try {
            con.setAutoCommit(false);
            if (con != null && !con.isClosed()) {
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                retVal = stmt.executeUpdate(sql);
            }
            con.commit();
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                logger.error(e1.getMessage(), e);
            }
            logger.error(e.getMessage(), e);
            if (con != null) {
                close(con);
            }
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                logger.error(e1.getMessage(), e1);
            }
            logger.error(e.getMessage(), e);
            if (con != null) {
                close(con);
            }
        }
        return retVal;
    }

    protected Timestamp convertTo(java.util.Date date) {
        if (date == null)
            date = new Date();
        java.sql.Timestamp t = new Timestamp(date.getTime());
        return t;
    }

    protected String getString(String str) {
        return str == null ? " " : str;
    }

}
