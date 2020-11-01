package com.carinov.pool.threads;

import com.carinov.pool.Pool;

public class ThreadPool extends Pool<ThreadResource> {
	private ThreadResourceListener listener = null;
	
	public ThreadPool(String name) {
		super(name);
	}
	
	public void setThreadResourceListener(ThreadResourceListener listener) {
		this.listener = listener;
	}

	@Override
	protected ThreadResource create(String name) {
		ThreadResource resource = new ThreadResource(name);
		resource.setThreadResourceListener(listener);
		return resource;
	}

	@Override
	protected boolean expire(ThreadResource resource) {
		return false;
	}

	@Override
	protected boolean validate(ThreadResource resource) {
		return true;
	}
}
