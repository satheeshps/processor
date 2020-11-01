package com.carinov.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.carinov.processor.ProcessorData;

public class ProcessorDataBucket {
	private BlockingQueue<ProcessorData> queue;
	
	public ProcessorDataBucket() {
		queue = new LinkedBlockingQueue<ProcessorData>();
	}

	public ProcessorDataBucket(int size) {
		queue = new LinkedBlockingQueue<ProcessorData>(size);
	}
	
	public synchronized void add(ProcessorData data) {
		queue.add(data);
	}

	public synchronized ProcessorData remove() {
		return queue.remove();
	}

	public synchronized boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public synchronized boolean isFull() {
		return queue.remainingCapacity() == 0;
	}
	
	public synchronized int available() {
		return queue.size();
	}
}
