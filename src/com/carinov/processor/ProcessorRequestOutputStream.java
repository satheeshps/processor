package com.carinov.processor;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.carinov.processor.persistence.PersistenceStorage;

public class ProcessorRequestOutputStream extends OutputStream {
	private Logger logger;
	private PersistenceStorage persistence;
	private ProcessorDataBucket bucket;

	public ProcessorRequestOutputStream(ProcessorDataBucket bucket) {
		this.bucket = bucket;
		persistence = PersistenceStorage.getStorage();
		logger = Logger.getLogger(ProcessorRequestOutputStream.class);
	}

	@Override
	public void write(int arg0) throws IOException {
	}

	private void save(ProcessorData data) {
		persistence.store(data);
	}
	
	public void writeProcessorData(ProcessorData data) throws IOException, InterruptedException {
		if(data != null) {
			try {
				bucket.add(data);
//				save(data);
//				controller.allot(data);
			} catch(Exception ex) {
				logger.error("writeProcessorData, ", ex);
			}
		}
	}
}
