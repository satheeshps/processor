package com.carinov.processor.management;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class Meter<T> {
	private long avgReading = -1;
	private long totalItems = 0;
	private long currentItems = 0;
	private Date begin;
	private Date current;
	private Date end;
	private Map<String,Meter<?>> meters = null;

	protected Meter() {
		meters = new HashMap<String, Meter<?>>();
	}
	
	protected void register(String key, Meter<?> value) {
		if(!meters.containsKey(key))
			meters.put(key, value);
	}

	private long getDuration() {
		return (end.getTime() - begin.getTime())/1000;
	}

	protected synchronized void measure(T in) {
		if(!isStartTime()) {
			setStartTime();
		}
		currentTime();
		incrItems();
		updateTime();
	}

	private void currentTime() {
		if(current == null)
			current = new Date();
		else {
			Date now = new Date();
			if(current.getSeconds() < now.getSeconds())
				current = now;
		}
	}

	private void updateTime() {
		end = new Date();
	}

	private boolean isStartTime() {
		return begin != null;
	}

	private void setStartTime() {
		begin = new Date();
	}

	private synchronized void incrItems() {
		totalItems++;
		if(currentItems == 1000)
			currentItems = 0;
		currentItems++;
	}

	public synchronized long getCurrentItems() {
		return totalItems;
	}
	
	public synchronized long getTotalItems() {
		return totalItems;
	}
	
	public long reading() {
		return (getCurrentItems() * 1000)/(end.getTime() - current.getTime());
	}

	public long averageReading() {
		return getTotalItems()/getDuration();
	}
}
