package com.tentacle.common.persist;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

public class DbService {
    private static final Logger logger = Logger.getLogger(DbService.class);

    protected Connection getConn() {
        return null;
    }

    public void saveOrUpdate(DatVector o) {
        Connection conn = null;
        try {
            conn = getConn();
            if (conn == null) return;
            conn.setAutoCommit(false);
            DbHelper.saveOrUpdate(o.getSql(), o.getObjects(), o.getSql1(), o.getListObjects(), conn);
            conn.commit();
        } catch (Exception e) {
            logger.error(o.getMsg(), e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        } finally {
            DbConnPoolManager.close(conn);
//            if (o.getListObjects() != null) {
//                o.getListObjects().clear();
//                o.setListObjects(null);
//            }
//            o.setObjects(null);
//            o = null;
        }
    }

    public void batchSaveOrUpdate(List<DatVector> olist) {
        Connection conn = null;        
        try {
            conn = getConn();
            if (conn == null) return;
            conn.setAutoCommit(false);
            for (DatVector o : olist) {
                DbHelper.saveOrUpdate(o.getSql(), o.getObjects(), o.getSql1(), o.getListObjects(), conn);
            }
            conn.commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        } finally {
            DbConnPoolManager.close(conn);
        }
    }
    
}
