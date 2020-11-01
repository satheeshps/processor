package com.carinov.processor;

import java.io.IOException;

import com.carinov.processor.cluster.ProcessorClusterNodeManager;
import com.carinov.processor.distribution.TaskDistributor;
import com.carinov.processor.management.DataMeter;
import com.carinov.processor.persistence.PersistenceStorage;
import com.carinov.processor.processes.ProcessLauncher;
import com.carinov.processor.utils.ContextController;

public class ProcessorEngine extends DataMeter<ProcessorData> implements ProcessorRequestListener {
	private static ProcessorEngine engine = null;
	private static Object lock = new Object();

	private ProcessorEngine() throws Exception {
		register("processor-engine", this);
		PersistenceStorage.init();
		TaskDistributor.init();
//		ProcessLauncher.getLauncher().launch();
		ProcessorJobListenerDaemon.init(this);
		ProcessorClusterNodeManager.init();
	}

	public static ProcessorEngine getEngine() throws Exception {
		if(engine == null) {
			synchronized (lock) {
				if(engine == null) {
					engine = new ProcessorEngine();
				}
			}
		}
		return engine;
	}

	public void add(Processor<?,?> processor) throws ProcessorAlreadyPresentException {
		ProcessorRegistry.getRegistry().addProcessor(processor);
	}

	protected void distribute(Processor<?,?> processor, Object data) {
		try {
			if(data != null) {
				ProcessorData pdata = null;
				if(!(data instanceof ProcessorData)) {
					pdata = new ProcessorData();
					if(pdata != null) {
						pdata.setProcessor(processor.getName());
						pdata.setData(data);
						pdata.setPersist(processor.isPersist);
						//					rid = pdata.getRequestId();
					}
				} else {
					pdata = (ProcessorData)data;
				}
				if(pdata != null) {
					TaskDistributor.getTaskDistributor().allot(pdata);
					measure(pdata);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public void suspend() {
		try {
			synchronized (lock) {
				lock.wait();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public void close() {
		try {
			ContextController.getContextController().getZeroMQContext().shutdown();
			ProcessLauncher.getLauncher().shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onArrival(ProcessorData data) {
		Processor<?,?> processor = ProcessorRegistry.getRegistry().getProcessor(data.getProcessor());
		if(processor != null)
			distribute(processor, data);
	}
}
