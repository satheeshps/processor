package com.carinov.pool.threads;

import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import com.carinov.pool.Resource;
import com.carinov.pool.ResourceException;

public class ThreadResource extends Resource {
	private static class WorkerThread extends Thread {
		private static ThreadResourceListener listener = null;
		private Logger logger = Logger.getLogger(WorkerThread.class);
		private ThreadResource resource = null;
		private Runnable task = null;
		private Object lock = new Object();
		private Semaphore full = null;

		public WorkerThread(String name,ThreadResource resource) {
			this(name, resource, null);
			this.setName(name);
		}

		public WorkerThread(String name,ThreadResource resource, ThreadGroup group) {
			super(group,name);
			this.resource = resource;
			full = new Semaphore(0);
		}

		public static void setThreadResourceListener(ThreadResourceListener tlistener) {
			listener = tlistener;
		}

		public void assign(Runnable run) {
			if(run != null) {
				try {
					synchronized (lock) {
						if(logger.isDebugEnabled())
							logger.debug("assign, acquired lock for : " + run);
						task = run;
					}
					if(logger.isDebugEnabled())
						logger.debug("assign, released lock for : " + run);
				} catch (Exception e) {
					logger.error("assign, ", e);
				} finally {
					full.release();
					if(logger.isDebugEnabled())
						logger.debug("assign, released full lock : " + full.availablePermits());
				}
			}
		}

		private void execute() {
			try {
				if(task != this && task != null) {
					if(logger.isDebugEnabled())
						logger.debug("execute, the task is about to run");
					task.run();
				} else {
					if(logger.isDebugEnabled())
						logger.debug("execute, the task cannot execute self");
				}
			} catch (Exception e) {
				logger.error("execute, ", e);
			} finally {
				task = null;
			}
		}

		@Override
		public void run() {
			while(true) {
				try {
					if(logger.isDebugEnabled())
						logger.debug("run, acquiring full lock: " + full.availablePermits());
					
					full.acquire();
					
					if(logger.isDebugEnabled())
						logger.debug("run, acquired full lock: " + full.availablePermits());

					synchronized (lock) {
						if(logger.isDebugEnabled())
							logger.debug("run, acquired lock");
						if(task != null) {
							if(listener != null)
								listener.onTaskBegin(resource);
							execute();
							if(listener != null)
								listener.onTaskComplete(resource);
						}
					}
					if(logger.isDebugEnabled())
						logger.debug("run, released lock");
				} catch (Exception e) {
					logger.error("run, ", e);
				}
			}
		}

		public boolean isReady() {
			State state = this.getState();
			return (state == State.RUNNABLE || state == State.BLOCKED || state == State.WAITING); 
		}
	}

	private WorkerThread worker = null;
	private Logger logger = Logger.getLogger(ThreadResource.class);

	public ThreadResource(String name) {
		init(name);
	}

	protected void setThreadResourceListener(ThreadResourceListener listener) {
		WorkerThread.setThreadResourceListener(listener);
	}

	public void init(String name) {
		if(worker == null) {
			worker = new WorkerThread(name + "-worker-" + this.getId(),this);
			worker.start();
//			while (!isReady());
		}
	}

	public void execute(Runnable run) throws ResourceException {
		try {
			if(run != null)
				worker.assign(run);
		} catch (Exception e) {
			logger.error("execute, ", e);
		}
	}

	@Override
	public boolean isReady() {
		return worker.isReady();
	}
}
