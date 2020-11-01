package com.carinov.processor;

public interface ProcessorListener<T> {
	public void onSuccess(long id, T result);
	public void onFailure(long id, T result);
}
