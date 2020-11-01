package com.carinov.processor.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ProcessorUtil {
	private static Object lock = new Object();
	private static ProcessorUtil util;

	private ProcessorUtil() {
	}

	public static ProcessorUtil getUtil() {
		if(util == null) {
			synchronized (lock) {
				if(util == null)
					util = new ProcessorUtil();
			}
		}
		return util;
	}

	public long getUniqueID() {
		return System.nanoTime();
	}

	public byte[] marshall(Serializable data) throws IOException {
		if(data != null) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bout);
			out.writeObject(data);
			return bout.toByteArray();
		}
		return null;
	}

	public Object unmarshall(byte[] data) throws Exception {
		if(data != null) {
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			ObjectInputStream oin = new ObjectInputStream(in);
			return oin.readObject();
		}
		return null;
	}
	
	public String getClassPath() {
		return System.getProperty("java.class.path");
	}
}
