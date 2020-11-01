package com.carinov.processor;

import java.util.concurrent.LinkedBlockingQueue;

public class ProcessorDataBuffer extends LinkedBlockingQueue<ProcessorData> {
	private static final long serialVersionUID = 1L;

	public ProcessorDataBuffer() {
	}

	public ProcessorDataBuffer(int size) {
		super(size);
	}
	
	public boolean isFull() {
		return this.remainingCapacity() == 0;
	}
}
