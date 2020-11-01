package com.carinov.processor;

import org.apache.log4j.Logger;

import com.carinov.processor.management.DataMeter;

public abstract class Processor<T,E> extends DataMeter<T> implements Runnable {
	private String name;
	private ProcessorListener<E> listener;
	protected ProcessorRequestInputStream in;
	protected ProcessorRequestOutputStream out;
	private ProcessorDataBucket bucket;
	private Logger logger;
	protected boolean isPersist = true;

	public Processor(String name) throws ProcessorAlreadyPresentException {
		this(name, 1);
	}

	public Processor(String name, int jobCnt) throws ProcessorAlreadyPresentException {
		this.name = name;
		this.logger = Logger.getLogger(Processor.class);
		this.bucket = new ProcessorDataBucket();
		this.in = new ProcessorRequestInputStream(bucket);
		this.out = new ProcessorRequestOutputStream(bucket);
		register(name, this);
		init();
	}

	public String getName() {
		return name;
	}

	public void addProcessorListener(ProcessorListener<E> listener) {
		this.listener = listener;
	}

	protected void post(T data) {
		try {
			ProcessorEngine.getEngine().distribute(this, data);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void run() {
		E result = null;
		try {
			ProcessorData data = null;
			while(in.available() > 0) {
				data = in.readProcessorData();
				if(data != null && data.getData() != null) {
					this.logger.debug("run, the task is executing");
					T inData = (T)data.getData();
					measure(inData);
					result = process(inData);
					if(listener != null)
						listener.onSuccess(data.getRequestId(), result);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected <V> void emit(String name, V data) {
		Processor processor = ProcessorRegistry.getRegistry().getProcessor(name);
		if(processor != null) {
			processor.post(data);
		}
	}

	protected ProcessorDataBucket getBucket() {
		return bucket;
	}

	protected void init() {
	}

	protected abstract E process(T data);
}
