package com.carinov.commons;

public interface ConfigListener {
	public void setConfigListener();
	public void removeConfigListener();
	public void onConfigChange();
}
