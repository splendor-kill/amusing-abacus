package com.tentacle.common.persist;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.tentacle.common.persist.DatVector.Type;

public abstract class DbThread {
    private static final Logger logger = Logger.getLogger(DbThread.class);

    private LinkedBlockingQueue<DatVector> queue = new LinkedBlockingQueue<DatVector>();
    private Worker worker;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> future;
    private volatile AtomicBoolean willDie = new AtomicBoolean();
    
    public abstract Connection getConn();    
    public abstract int getBatchCommitSize();
    
    public void start() {
        worker = new Worker();
        future = executor.submit(worker);
    }
    
    public void awaitTerm() {
        try {
            willDie.set(true);
            DatVector obj = new DatVector();
            obj.setOptType(Type.FOR_TERM);
            addObject(obj);
            executor.shutdown();

            try {
                logger.info("DB-thread isDone[" + future.isDone() + "], isCancelled[" + future.isCancelled() + "]");
                future.get(30, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                logger.error(e.getMessage(), e);
            } catch (TimeoutException e) {
                logger.error(e.getMessage(), e);
            }

            logger.info("the DB-thread will be closed after 2 min.");
            boolean isTerm = executor.awaitTermination(2 * 60, TimeUnit.SECONDS);
            logger.info("awaitTermination[" + isTerm + "]");
            if (!isTerm) {
                List<Runnable> lr = executor.shutdownNow();
                logger.info("num of awaiting execution tasks[" + lr.size() + "].");
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private class Worker implements Runnable {        
        private final int dbBatchCommitSize = getBatchCommitSize();

        @Override
        public void run() {            
            logger.info("data storing thread id[" + Thread.currentThread().getId() + "] at work.");
            List<DatVector> olist = new ArrayList<DatVector>();
            int prevSize = 0;
            while (true) {
                DatVector o = getObject();
                if (o == null) {
                    if (!olist.isEmpty()) {
                        batchSaveOrUpdate(olist);
                        olist.clear();
                    }
                    Thread.yield();
                    continue;
                }

                if (o.getOptType() == Type.FOR_TERM) {
                    logger.info("when DB-thread meet terminator!");
                } else {
                    olist.add(o);
                    if (olist.size() >= dbBatchCommitSize) {
                        batchSaveOrUpdate(olist);
                        olist.clear();
                    }
                }

                int curSize = queue.size() >> 10;
                if (curSize > 0) {
                    if (curSize > prevSize) {
                        logger.info("data flood[" + curSize + "]K");
                        prevSize = curSize;
                    } else if (prevSize - curSize > 2) {
                        prevSize = curSize;
                    }
                } else {
                    prevSize = 0;
                }

                if (willDie.get()) {
                    logger.error("db queue size[" + queue.size() + "], ready to close...");
                    if (queue.isEmpty())
                        break;
                }
            }

            if (!olist.isEmpty()) {
                batchSaveOrUpdate(olist);
                olist.clear();
            }
        }
    }

    private DatVector getObject() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public void addObject(DatVector o) {
        if (o == null) {
            return;
        }
        try {
            queue.add(o);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void batchSaveOrUpdate(List<DatVector> olist) {
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
