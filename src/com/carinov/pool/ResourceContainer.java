package com.carinov.pool;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public abstract class ResourceContainer<T> {
	protected static final int MAX_RESOURCES = 10;
	protected static final int MAX_ACTIVE_RESOURCES = 7;
	protected static final int MIN_IDLE_RESOURCES = 2;
	private int maxResource = 0;
	private int maxActive = 0;
	private int minIdle = 0;
	private int total = 0;
	protected Semaphore full = null;
	protected Semaphore empty = null;
	protected Semaphore mutex = null;

	protected ConcurrentHashMap<Integer,T> active = null;
	protected ConcurrentHashMap<Integer,T> idle = null;

	public ResourceContainer() {
		this.active = new ConcurrentHashMap<Integer, T>();
		this.idle = new ConcurrentHashMap<Integer, T>();
		full = new Semaphore(0);
		mutex = new Semaphore(1);
	}

	public int getActiveCount() {
		return active.size();
	}

	public int getIdleCount() {
		return idle.size();
	}

	public void setMaxResource(int max) {
		this.maxResource = max;
		if(empty == null)
			empty = new Semaphore(maxResource);
	}

	public void setMaxActive(int active) {
		this.maxActive = active;
	}

	public void setMinIdle(int idle) {
		this.minIdle = idle;
	}

	public int getMaxResource() {
		return maxResource;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public int getTotal() {
		return total;
	}
	
	public void setTotal(int total) {
		this.total = total;
	}
}
