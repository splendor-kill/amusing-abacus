package com.tentacle.callofwild.persist.base;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import com.tentacle.callofwild.persist.DbPoolManager;
import com.tentacle.callofwild.persist.StaticDao;


public class DaoLoginService {
    private static final Logger logger = Logger.getLogger(DaoLoginService.class);

    public Connection getConn() {
        return DbPoolManager.getInst().getLoginDbConn();
    }
    
    public void saveOrUpdate(DatVector o) {
        Connection conn = null;        
        try {
            conn = getConn();
            conn.setAutoCommit(false);
            StaticDao.saveOrUpdate(o.getSql(), o.getObjects(), o.getSql1(), o.getListObjects(), conn);
            conn.commit();
        } catch (Exception e) {
            logger.error(o.getMsg(), e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                logger.error(ex);
            }
        } finally {
            DbPoolManager.close(conn);
            if (o.getListObjects() != null) {
                o.getListObjects().clear();
                o.setListObjects(null);
            }
            o.setObjects(null);
            o = null;
        }
    }
	
    public void batchSaveOrUpdate(List<DatVector> olist) {
        Connection conn = null;        
        try {
            conn = getConn();
            conn.setAutoCommit(false);
            for (DatVector o : olist) {
                StaticDao.saveOrUpdate(o.getSql(), o.getObjects(), o.getSql1(), o.getListObjects(), conn);
            }
            conn.commit();
        } catch (Exception e) {
            logger.error(e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                logger.error(ex);
            }
        } finally {
            DbPoolManager.close(conn);
        }
    }
    
}
