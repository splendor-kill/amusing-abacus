package com.tentacle.callofwild.persist.base;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import com.tentacle.callofwild.persist.DBPoolManager;
import com.tentacle.callofwild.persist.Dao;

public class DaoService {
    private static final Logger logger = Logger.getLogger(DaoService.class);
    private Connection conn = null;

    public void saveOrUpdate(DAOBject o) {
        Dao dao = new Dao();
        try {
            conn = DBPoolManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            dao.saveOrUpdate(o.getSql(), o.getObjects(), o.getSql1(), o.getListObjects(), conn);
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
            DBPoolManager.getInstance().close(conn);
            if (o.getListObjects() != null) {
                o.getListObjects().clear();
                o.setListObjects(null);
            }
            o.setObjects(null);
            o = null;
        }
    }

    public void batchSaveOrUpdate(List<DAOBject> olist) {
        Dao dao = new Dao();
        try {
            conn = DBPoolManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            for (DAOBject o : olist) {
                dao.saveOrUpdate(o.getSql(), o.getObjects(), o.getSql1(), o.getListObjects(), conn);
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
            DBPoolManager.getInstance().close(conn);
        }
    }
}
