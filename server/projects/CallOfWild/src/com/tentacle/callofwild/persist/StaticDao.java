package com.tentacle.callofwild.persist;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;


public class StaticDao {
    private static final Logger logger = Logger.getLogger(StaticDao.class);

    public static void close(PreparedStatement pstmt) {
        try {
            if (null != pstmt) {
                pstmt.close();
                pstmt = null;
            }
        } catch (SQLException e) {
            pstmt = null;
            logger.error(e.getMessage(), e);
        }
    }

    public static void close(ResultSet rs) {
        try {
            if (null != rs) {
                rs.close();
                rs = null;
            }
        } catch (SQLException e) {
            rs = null;
            logger.error(e.getMessage(), e);
        }
    }

    public static ResultSet exeQuery(String sql, Connection conn) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            if (conn != null) {
                stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = stmt.executeQuery(sql);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            DbPoolManager.close(conn);
        }
        return rs;
    }

    public static int exeUpdate(String sql, Connection con) {
        int retVal = -1;
        Statement stmt = null;
        try {
            con.setAutoCommit(false);
            if (con != null) {
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                retVal = stmt.executeUpdate(sql);
            }
            con.commit();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                logger.error(e1.getMessage(), e1);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            try {
                con.rollback();
            } catch (SQLException e1) {
                logger.error(e1.getMessage(), e1);
            }
        } finally {
            DbPoolManager.close(con);
        }
        return retVal;
    }

    public static Timestamp convertTo(java.util.Date date) {
        if (date == null)
            date = new Date();
        java.sql.Timestamp t = new Timestamp(date.getTime());
        return t;
    }

    public static String getString(String str) {
        return str == null ? "" : str;
    }

    public static void saveOrUpdate(String sql1, Object[] objects, String sql2,
            List<Object[]> listObject, Connection conn) {
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        try {
            if (sql1 != null && !sql1.trim().isEmpty()) {
                pstmt = conn.prepareStatement(sql1);
                pstmt = preparedStatement(objects, pstmt);                
                pstmt.executeUpdate();
            }

            if (sql2 != null && !sql2.trim().isEmpty()) {
                pstmt1 = conn.prepareStatement(sql2);
                for (Object[] os : listObject) {
                    pstmt1 = preparedStatement(os, pstmt1);
                    pstmt1.executeUpdate();
                }
            }
        } catch (SQLException e) {
            logger.error(sql1);
            logger.error(sql2);
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(sql1);
            logger.error(sql2);
            logger.error(e.getMessage(), e);
        } finally {
            close(pstmt);
            close(pstmt1);
        }
    }

    public static PreparedStatement preparedStatement(Object[] objects,
            PreparedStatement pstmt) throws SQLException {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i].getClass() == int.class) {
                pstmt.setInt(i + 1, (Integer) objects[i]);
            } else if (objects[i].getClass() == Integer.class) {
                pstmt.setInt(i + 1, (Integer) objects[i]);
            } else if (objects[i].getClass() == float.class) {
                pstmt.setFloat(i + 1, (Float) objects[i]);
            } else if (objects[i].getClass() == Float.class) {
                pstmt.setFloat(i + 1, (Float) objects[i]);
            } else if (objects[i].getClass() == Long.class) {
                pstmt.setLong(i + 1, (Long) objects[i]);
            } else if (objects[i].getClass() == long.class) {
                pstmt.setLong(i + 1, (Long) objects[i]);
            } else if (objects[i].getClass() == double.class) {
                pstmt.setDouble(i + 1, (Double) objects[i]);
            } else if (objects[i].getClass() == Double.class) {
                pstmt.setDouble(i + 1, (Double) objects[i]);
            } else if (objects[i].getClass() == byte.class) {
                pstmt.setByte(i + 1, (Byte) objects[i]);
            } else if (objects[i].getClass() == byte[].class) {
                pstmt.setBytes(i + 1, (byte[]) objects[i]);
            } else if (objects[i].getClass() == Byte.class) {
                pstmt.setByte(i + 1, (Byte) objects[i]);
            } else if (objects[i].getClass() == Byte[].class) {
                pstmt.setBytes(i + 1, (byte[]) objects[i]);
            } else if (objects[i].getClass() == String.class) {
                pstmt.setString(i + 1, getString((String) objects[i]));
            } else if (objects[i].getClass() == Date.class) {
                pstmt.setTimestamp(i + 1, convertTo((Date) objects[i]));
            } else if (objects[i].getClass() == java.sql.Date.class) {
                pstmt.setTimestamp(i + 1, convertTo((Date) objects[i]));
            } else if (objects[i].getClass() == boolean.class) {
                pstmt.setBoolean(i + 1, (Boolean) objects[i]);
            } else if (objects[i].getClass() == Boolean.class) {
                pstmt.setBoolean(i + 1, (Boolean) objects[i]);
            }
        }
        return pstmt;
    }

    public static PreparedStatement preparedStatement(Object t, Class<?> type,
            PreparedStatement pstmt, Method m, int index) throws Exception {
        if (type == int.class) {
            pstmt.setInt(index, (Integer) m.invoke(t));
        } else if (type == float.class) {
            pstmt.setFloat(index, (Float) m.invoke(t));
        } else if (type == short.class) {
            pstmt.setShort(index, (Short) m.invoke(t));
        } else if (type == long.class) {
            pstmt.setLong(index, (Long) m.invoke(t));
        } else if (type == double.class) {
            pstmt.setDouble(index, (Double) m.invoke(t));
        } else if (type == byte.class) {
            pstmt.setByte(index, (Byte) m.invoke(t));
        } else if (type == char.class) {

        } else if (type == boolean.class) {
            pstmt.setBoolean(index, (Boolean) m.invoke(t));
        } else if (type == String.class) {
            pstmt.setString(index, getString((String) m.invoke(t)));
        } else if (type == Date.class) {
            pstmt.setTimestamp(index, convertTo((Date) m.invoke(t)));
        }
        return pstmt;
    }
    
}
