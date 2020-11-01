package com.carinov.pool.threads;

public interface ThreadResourceListener {
	public void onTaskBegin(ThreadResource resource);
	public void onTaskComplete(ThreadResource resource);
}
