package com.carinov.commons;

import java.io.File;

public class Configuration {
	private static Configuration config = null;
	private static Object lock = new Object();
	private Bean cfg = null;

	private Configuration(String file) {
		init(file);
	}

	private void init(String file) {
		try {
			cfg = Bean.fromXml(new File(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Configuration load(String file) {
		if(config == null) {
			synchronized (lock) {
				if(config == null) {
					config = new Configuration(file);
				}
			}
		}
		return config;
	}

	public static Object get(String key) {
		Object ret = null;
		if(config != null) {
			ret = config.cfg.peek(key);
			if(ret instanceof NameValue<?, ?>)
				ret = config.cfg.dataOf(key);
		}
		return ret;
	}

	public static void set(String key, Object value) {
		if(config != null) {
			if(config.cfg.peek(key) == null) {
				config.cfg.set(key, value);
			} else
				throw new IllegalArgumentException();
		}
	}
}
