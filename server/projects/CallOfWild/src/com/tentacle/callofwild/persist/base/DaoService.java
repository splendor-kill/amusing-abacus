package com.tentacle.callofwild.persist.base;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import com.tentacle.callofwild.persist.DbPoolManager;
import com.tentacle.callofwild.persist.StaticDao;

public class DaoService {
    private static final Logger logger = Logger.getLogger(DaoService.class);
    private Connection conn = null;

    public void saveOrUpdate(DatVector o) {
        
        try {
            conn = DbPoolManager.getInst().getGameDbConn();
            conn.setAutoCommit(false);
            StaticDao.saveOrUpdate(o.getSql(), o.getObjects(), o.getSql1(), o.getListObjects(), conn);
            conn.commit();
        } catch (Exception e) {
            logger.error(o.getMsg() + "===>" + e.getMessage(), e);
            System.out.println("DaoService::saveOrUpdate error!!!!!!!!!!!!!!!!!!!!" + o.getMsg());
            try {
                conn.rollback();
            } catch (Exception e1) {
                logger.error(e1.getMessage(), e1);
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
        try {
            conn = DbPoolManager.getInst().getGameDbConn();
            conn.setAutoCommit(false);
            for (DatVector o : olist) {
                StaticDao.saveOrUpdate(o.getSql(), o.getObjects(), o.getSql1(), o.getListObjects(), conn);
            }
            conn.commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            try {
                conn.rollback();
            } catch (Exception e1) {
                logger.error(e1.getMessage(), e1);
            }
        } finally {
            DbPoolManager.close(conn);
        }
    }
}
