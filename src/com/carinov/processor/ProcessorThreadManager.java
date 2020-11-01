package com.carinov.processor;

import com.carinov.pool.threads.ThreadPoolExecutor;

public class ProcessorThreadManager extends ThreadPoolExecutor {
	private static ProcessorThreadManager manager;
	private static Object lock = new Object();

	private ProcessorThreadManager() {
		this("processor-pool", 100, 30, 10);
	}

	private ProcessorThreadManager(String name, int max, int act, int idl) {
		super(name, max, act, idl);
	}

	public static ProcessorThreadManager getProcessorThreadManager() {
		if(manager == null) {
			synchronized (lock) {
				if(manager == null)
					manager = new ProcessorThreadManager();
			}
		}
		return manager;
	}

	public <T,E> boolean assign(ProcessorData pdata) throws Exception {
		if(pdata != null) {
			String pName = pdata.getProcessor();
			if(pName != null) {
				Processor<?,?> processor = ProcessorRegistry.getRegistry().getProcessor(pName);
				if(processor != null) {
					processor.out.writeProcessorData(pdata);
					super.assign(processor);
					return true;
				}
			}
		}
		return false;
	}
}
