package com.carinov.pool.threads;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

public class ThreadPoolExecutor {
	private class ThreadResourceAssigner extends Thread implements ThreadResourceListener {
		private ConcurrentLinkedQueue<Runnable> tasks = null;
		private Logger logger = Logger.getLogger(ThreadResourceAssigner.class);
		private ThreadPool pool = null;
		private Semaphore full = null;
		private Semaphore mutex = null;

		ThreadResourceAssigner(ThreadPool pool) {
			this.setName("thread-resource-assigner");
			this.pool = pool;
			this.mutex = new Semaphore(1);
			this.full = new Semaphore(0);
			tasks = new ConcurrentLinkedQueue<Runnable>();
		}

		@Override
		public void run() {
			while(true) {
				ThreadResource resource = null;
				try {
					resource = pool.acquire();
					if(resource != null) {
						if(logger.isDebugEnabled())
							logger.debug("run, acquiring the full lock : " + full.availablePermits());
						full.acquire();
						if(logger.isDebugEnabled())
							logger.debug("run, acquired the full lock : " + full.availablePermits());
						
						mutex.acquire();
						if(logger.isDebugEnabled())
							logger.debug("run, acquired the lock");
						Runnable task = tasks.remove();
						resource.execute(task);
					} else
						logger.debug("run, resource is null");
				} catch (Exception e) {
					logger.error("run, ", e);
				} finally {
					if(resource != null) {
						mutex.release();
						if(logger.isDebugEnabled())
							logger.debug("run, released the lock");
					}
				}
			}
		}

		public void push(Runnable task) {
			try {
				if(task != null) {
					if(logger.isDebugEnabled())
						logger.debug("push, aquiring the lock");

					mutex.acquire();
					
					if(logger.isDebugEnabled())
						logger.debug("push, acquired the lock");
					this.tasks.add(task);
				}
			} catch (Exception e) {
				logger.error("push, ", e);
			} finally {
				mutex.release();
				if(logger.isDebugEnabled())
					logger.debug("push, released the lock");
				full.release();
				if(logger.isDebugEnabled())
					logger.debug("push, released the full lock : " + full.availablePermits());
			}
		}

		@Override
		public void onTaskBegin(ThreadResource resource) {
		}

		@Override
		public void onTaskComplete(ThreadResource resource) {
			try {
				if(resource != null) {
					pool.release(resource);
				}
			} catch (Exception e) {
				logger.error("onTaskComplete, ", e);
			}
		}
	}

	private ThreadResourceAssigner assigner = null;
	private ThreadPool pool = null;

	public ThreadPoolExecutor(String name,int max,int act,int idl) {
		init(name,max,act,idl);
	}

	private void init(String name,int max,int act,int idl) {
		pool = new ThreadPool(name);
		pool.setMaxResource(max);
		pool.setMaxActive(act);
		pool.setMinIdle(idl);
		this.assigner = new ThreadResourceAssigner(pool);
		pool.setThreadResourceListener(assigner);
		this.assigner.start();
	}

	public void assign(Runnable task) {
		assigner.push(task);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Max resources: " + pool.getMaxResource());
		buffer.append("\nTotal resources used: " + pool.getTotal());
		buffer.append("\nTotal resources active: " + pool.getActiveCount());
		buffer.append("\nTotal resources idle: " + pool.getIdleCount());
		return  buffer.toString();
	}
}
