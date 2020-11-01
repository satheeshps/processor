package com.carinov.processor.utils;

import java.io.IOException;

public class ContextController {
	private static ContextController context = null;
	private static Object lock = new Object();
	private ZookeeperContext zCxt;
	private ZeroMQContext pCxt;
	private JMXContext jmxCxt;

	private ContextController() throws IOException {
		zCxt = new ZookeeperContext();
		pCxt = new ZeroMQContext();
		jmxCxt = new JMXContext();
	}

	public static ContextController getContextController() throws IOException {
		if(context == null) {
			synchronized (lock) {
				if(context == null) {
					context = new ContextController();
				}
			}
		}
		return context;
	}
	
	public ZookeeperContext getZookeeperContext() {
		return zCxt;
	}
	
	public ZeroMQContext getZeroMQContext() {
		return pCxt;
	}
	
	public JMXContext getJMXContext() {
		return jmxCxt;
	}
}
