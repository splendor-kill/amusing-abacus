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

/**
 * 通用Dao,重构代码区
 * @author spf
 *
 * @param <T>
 */
public class Dao {
	private static final Logger logger = Logger.getLogger(BaseDao.class);

	protected Statement stmt = null;
	protected PreparedStatement pstmt = null;
	protected ResultSet rs = null;
	
	protected void close(Connection con) {
		try {
			if (null != con && !con.isClosed()) {
				con.close();
				con = null;
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
				pstmt = null;
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
		int returnvalue = -1;

		try {
			con.setAutoCommit(false);
			if (con != null && !con.isClosed()) {
				stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
				returnvalue = stmt.executeUpdate(sql);
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
		return returnvalue;
	}
	
	protected Timestamp convertTo(java.util.Date date) {
		if(date == null)
			date = new Date();
		java.sql.Timestamp t = new Timestamp(date.getTime());
		return t;
		
	}
	
	protected String getString(String str){
		return str == null ? " " : str;
	}
	
	public static void main(String[] args) throws Exception {

	}
	
	public void saveOrUpdate(String sql1, Object[] objects, String sql2,
			List<Object[]> listObject, Connection conn) {
		PreparedStatement pstmt1 = null;
		try {
			if (sql1 != null && !"".equals(sql1.trim())) {
				pstmt = conn.prepareStatement(sql1);
				for (int i = 0; i < objects.length; i++) {
					pstmt = preparedStatement(objects, pstmt);
				}
				pstmt.executeUpdate();
			}

			if (sql2 != null && !"".equals(sql2.trim())) {
				pstmt1 = conn.prepareStatement(sql2);
				for (Object[] os : listObject) {
					for (int i = 0; i < os.length; i++) {
						pstmt1 = preparedStatement(os, pstmt1);
					}
					pstmt1.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
		
    public PreparedStatement preparedStatement(Object[] objects, PreparedStatement pstmt) throws SQLException {
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
                pstmt.setString(i + 1, (String) objects[i]);
            } else if (objects[i].getClass() == Date.class) {
                pstmt.setTimestamp(i + 1, convertTo((Date) objects[i]));
            } else if (objects[i].getClass() == java.sql.Date.class) {
                pstmt.setTimestamp(i + 1, convertTo((Date) objects[i]));
            } else if (objects[i].getClass() == java.sql.Timestamp.class) {
                pstmt.setTimestamp(i + 1, (Timestamp) objects[i]);
            } else if (objects[i].getClass() == boolean.class) {
                pstmt.setBoolean(i + 1, (Boolean) objects[i]);
            } else if (objects[i].getClass() == Boolean.class) {
                pstmt.setBoolean(i + 1, (Boolean) objects[i]);
            }
        }
        return pstmt;
    }
	
	public PreparedStatement preparedStatement(Object t,Class<?> type,
				PreparedStatement pstmt,Method m,int index) throws Exception{
		if(type == int.class){
			pstmt.setInt(index,(Integer)m.invoke(t));
			System.out.println("int");
		}else if(type == float.class){
			pstmt.setFloat(index,(Float)m.invoke(t));
			System.out.println("float");
		}else if(type == short.class){
			pstmt.setShort(index,(Short)m.invoke(t));
			System.out.println("short");
		}else if(type == long.class){
			pstmt.setLong(index,(Long)m.invoke(t));
			System.out.println("long");
		}else if(type == double.class){
			pstmt.setDouble(index,(Double)m.invoke(t));
			System.out.println("double");
		}else if(type == byte.class){
			pstmt.setByte(index,(Byte)m.invoke(t));
			System.out.println("Byte");
		}else if(type == char.class){
			 
		}else if(type == boolean.class){
			pstmt.setBoolean(index,(Boolean)m.invoke(t));
			System.out.println("Byte");
		}else if(type == String.class){
			pstmt.setString(index,(String)m.invoke(t));
			System.out.println("Byte");
		}else if(type == Date.class){
			pstmt.setTimestamp(index, convertTo((Date)m.invoke(t)));
			System.out.println("Byte");
		}
		return pstmt;
	}
}
