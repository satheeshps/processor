package com.carinov.pool;

public interface ResourceListener<T> {
	public void onRelease(T res);
	public void onAcquire(T res);
	public void onCreate(T res);
	public void onDestroy(T res);
}
