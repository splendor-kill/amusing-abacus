package com.tentacle.callofwild.persist;

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

import com.tentacle.callofwild.persist.base.DatVector;
import com.tentacle.callofwild.persist.base.DaoLoginService;
import com.tentacle.callofwild.persist.base.DatVector.OPT_TYPE;

public class LoginDbThread {
    private static final Logger logger = Logger.getLogger(LoginDbThread.class);

    public LinkedBlockingQueue<DatVector> queue = new LinkedBlockingQueue<DatVector>();
    private Worker worker;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private Future<?> future;
    private volatile AtomicBoolean willDie = new AtomicBoolean();

    private static LoginDbThread inst = new LoginDbThread();

    public static LoginDbThread getInst() {
        return LoginDbThread.inst;
    }

    private LoginDbThread() {
    }
		
	public void start() {
		worker = new Worker();
		future = executor.submit(worker);
	}
	
	public void awaitTerm() {
		try {
			willDie.set(true);
			DatVector obj = new DatVector();
			obj.setOptType(OPT_TYPE.FOR_TERM);
			addObject(obj);
			executor.shutdown();
			
			try {
				logger.debug("DB-thread isDone[" + future.isDone() + "], isCancelled[" + future.isCancelled() + "]");
				future.get(30, TimeUnit.SECONDS);
			} catch (ExecutionException e) {
			    logger.error(e);
			} catch (TimeoutException e) {
			    logger.error(e);
			}
			
			logger.debug("the DB-thread will be closed after 2 min.");
			boolean isTerm = executor.awaitTermination(2 * 60, TimeUnit.SECONDS);
			logger.debug("await termination[" + isTerm + "]");
			if (!isTerm) {
				List<Runnable> lr = executor.shutdownNow();
				logger.debug("num of awaiting execution tasks[" + lr.size() + "].");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	class Worker implements Runnable {
		private DaoLoginService daoService = new DaoLoginService();
		
		@Override
		public void run() {
			logger.debug("data storing thread id["+Thread.currentThread().getId()+"] at work.");
			List<DatVector> olist = new ArrayList<DatVector>();
			while (true) {
				DatVector o = getDAOBject();
				if (o != null && o.getOptType() == OPT_TYPE.BATCH_OP) {
					olist.add(o);
					if (olist.size() >= 200) {
						daoService.batchSaveOrUpdate(olist);
						olist.clear();
					}
				} else if (o != null && o.getOptType() == OPT_TYPE.FOR_TERM) {
					logger.debug("when DB-thread meet terminator!");
				} else if (o != null) {
					daoService.saveOrUpdate(o);
				}
				
				if (willDie.get()) {
					logger.info("db queue size[" + queue.size() + "], ready to close...");
					if (queue.isEmpty())
						break;
				} else {
					Thread.yield();
				}
			}
			
			if (!olist.isEmpty()) {
				daoService.batchSaveOrUpdate(olist);
				olist.clear();
			}
		}
	}
	
	
    private DatVector getDAOBject() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            logger.error(e);
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
            logger.error(e);
        }
    }
}
