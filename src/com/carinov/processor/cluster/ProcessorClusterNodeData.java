package com.carinov.processor.cluster;

import java.io.Serializable;

import com.carinov.processor.management.SystemResourceMonitor;

public class ProcessorClusterNodeData implements Serializable {
	private static final long serialVersionUID = 1L;
	private long maximumThreads;
	private long minimumThreads;
	private long inactiveThreads;
	private long activeThreads;
	private int processorUtilization;
	private int memoryUtilization;
	private String host;
	private String port;
	private transient SystemResourceMonitor gauge;

	public ProcessorClusterNodeData() {
		gauge = SystemResourceMonitor.getGauge();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public long getMaximumThreads() {
		if(gauge != null)
			return maximumThreads = gauge.getTotalStartedThreadCount();
		return maximumThreads;
	}

	public long getMinimumThreads() {
		return minimumThreads = 0;
	}

	public long getInactiveThreads() {
		if(gauge != null)
			return inactiveThreads = gauge.getTotalStartedThreadCount() - gauge.getActiveThreadCount();
		return inactiveThreads;
	}

	public long getActiveThreads() {
		if(gauge != null)
			return activeThreads = gauge.getActiveThreadCount();
		return activeThreads;
	}

	public int getProcessorUtilization() {
		if(gauge != null)
			return processorUtilization = (int)gauge.getCpuPercentage();
		return processorUtilization;
	}

	public int getMemoryUtilization() {
		if(gauge != null)
			return memoryUtilization = (int)gauge.getMemoryFreePercentage();
		return memoryUtilization;
	}
}
