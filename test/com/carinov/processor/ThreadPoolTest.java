package com.carinov.processor;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.carinov.pool.threads.ThreadPoolExecutor;

public class ThreadPoolTest {
	private static Logger logger = Logger.getLogger(ThreadPoolTest.class);
	static Object lock = new Object();
	static int cnt = 0;
	static Runnable runnable = null;
	static ThreadPoolExecutor exec = new ThreadPoolExecutor("tmp",10,1,1);
	
	private static void loadtest() {
		try {
			final int loop = 10000;
			runnable = new Runnable() {
				private long start = 0;
				private long stop = 0;

				@Override
				public void run() {
					synchronized (lock) {
						cnt++;
						logger.debug(cnt + " > " + Thread.currentThread().getId());
						if(cnt == 1) {
							start = System.currentTimeMillis();
						} else if(cnt == loop) {
							stop = System.currentTimeMillis();
							logger.info("Duration: " + (stop - start)/1000);
							logger.info("Status: " + exec.toString());
							cnt = 0;
							//							System.exit(1);
						}
					}
				}
			};

			for(int i = 0;i < loop;i++) {
				exec.assign(runnable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DOMConfigurator.configure("config\\logging.xml");
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				loadtest();
			}
		},0,30000);
	}
}
