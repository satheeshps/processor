package com.carinov.processor;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class ProcessorCallable<T extends Serializable,E extends Serializable> implements Callable<E> {
	private ProcessorData data;

	ProcessorCallable(ProcessorData data) {
		this.data = data;
	}

	public long getRequestID() {
		return data.getRequestId();
	}

	@Override
	public E call() throws Exception {
		E result = null;
		try {
			if(data != null) {
				Processor<T,E> processor = ProcessorRegistry.getRegistry().getProcessor(data.getProcessor());
				if(processor != null)
					result = processor.process((T)data.getData());
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
}
