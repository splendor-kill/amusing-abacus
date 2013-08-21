package com.tentacle.callofwild.persist.base;

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

import com.tentacle.callofwild.logic.GameServer;
import com.tentacle.callofwild.persist.base.DAOBject.OPT_TYPE;

public class DaoThread {
	private static final Logger logger = Logger.getLogger(DaoThread.class);
	
	private LinkedBlockingQueue<DAOBject> queue = new LinkedBlockingQueue<DAOBject>();
	private Worker worker;
	private ExecutorService executor = Executors.newCachedThreadPool();
	private Future<?> dbFuture;
	private volatile AtomicBoolean willDie = new AtomicBoolean();
	
	private static DaoThread instance = new DaoThread();	
	public static DaoThread getInstance() {
		return instance;
	}
	private DaoThread() {}
	
	
	public void startDaoThread() {
		worker = new Worker();
		dbFuture = executor.submit(worker);
	}
	
	public void awaitTerm() {
		try {
			willDie.set(true);
			DAOBject obj = new DAOBject();
			obj.setOptType(OPT_TYPE.FOR_TERM);
			addObject(obj);
			executor.shutdown();
			
			try {
				logger.debug("DB-thread isDone[" + dbFuture.isDone() + "], isCancelled[" + dbFuture.isCancelled() + "]");
				dbFuture.get(30, TimeUnit.SECONDS);
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
			
			logger.debug("the DB-thread will be closed after 2 min.");
			boolean isTerm = executor.awaitTermination(2 * 60, TimeUnit.SECONDS);
			logger.debug("awaitTermination[" + isTerm + "]");
			if (!isTerm) {
				List<Runnable> lr = executor.shutdownNow();
				logger.debug("num of awaiting execution tasks[" + lr.size() + "].");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	class Worker implements Runnable {
		private DaoService daoService = new DaoService();
        private final int dbBatchCommitSize = GameServer.getInstance().getDbBatchCommitSize();
		
		@Override
		public void run() {
			logger.info("data storing thread id["+Thread.currentThread().getId()+"] at work.");
			List<DAOBject> olist = new ArrayList<DAOBject>();
			int prevSize = 0;			
			while (true) {
				DAOBject o = getDAOBject();
				if (o == null) {
				    if (!olist.isEmpty()) {
		                daoService.batchSaveOrUpdate(olist);
		                olist.clear();
		            }
				    Thread.yield();
				    continue;
				}
				
                if (o.getOptType() == OPT_TYPE.FOR_TERM) {
                    logger.info("when DB-thread meet terminator!");
                } else {
                    olist.add(o);
                    if (olist.size() >= dbBatchCommitSize) {
                        daoService.batchSaveOrUpdate(olist);
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
				daoService.batchSaveOrUpdate(olist);
				olist.clear();
			}
		}
	}
	
	
	private DAOBject getDAOBject() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public void addObject(DAOBject o) {
		if (o == null) {
			return;
		}
		try {
			queue.add(o);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
