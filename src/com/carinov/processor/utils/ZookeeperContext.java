package com.carinov.processor.utils;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.carinov.commons.Configuration;

public class ZookeeperContext implements Watcher {
	private ZooKeeper zk;
	private Watcher watcher;
	private boolean isShutdown;

	ZookeeperContext() throws IOException {
		String port = (String) Configuration.get("processor.zookeeper-port");
		setShutdown(false);
		this.zk = new ZooKeeper("localhost:" + port, 3000, this);
	}

	public ZooKeeper getZooKeeper() {
		if(!isShutdown())
			return zk;
		return null;
	}

	public void addWatch(Watcher watcher) {
		this.watcher = watcher;
	}

	@Override
	public void process(WatchedEvent event) {
		if(watcher != null)
			watcher.process(event);
	}

	public boolean isShutdown() {
		return isShutdown;
	}

	public void setShutdown(boolean isShutdown) {
		this.isShutdown = isShutdown;
	}

	public void shutdown() {
		try {
			if(!isShutdown()) {
				zk.close();
				zk = null;
				setShutdown(true);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
