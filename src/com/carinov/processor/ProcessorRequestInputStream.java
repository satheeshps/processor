package com.carinov.processor;

import java.io.IOException;
import java.io.InputStream;

import com.carinov.processor.persistence.PersistenceStorage;

public class ProcessorRequestInputStream extends InputStream {
	private ProcessorDataBucket bucket;

	public ProcessorRequestInputStream(ProcessorDataBucket bucket) {
		this.bucket = bucket;
	}

	@Override
	public int available() throws IOException {
		return this.bucket.available();
	}

	private void delete(ProcessorData data) {
		PersistenceStorage.getStorage().delete(data);
	}

	public ProcessorData readProcessorData() throws IOException, InterruptedException {
		ProcessorData ret = null;
		if(!bucket.isEmpty()) {
			ret = bucket.remove();
			if(ret != null)
				delete(ret);
		}
		return ret;
	}

	@Override
	public int read() throws IOException {
		return 0;
	}
}
