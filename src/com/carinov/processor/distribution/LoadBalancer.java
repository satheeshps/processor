package com.carinov.processor.distribution;

import com.carinov.processor.ProcessorData;

public abstract class LoadBalancer<T extends LoadBalancerConfig> {
	protected T cfg;
	
	public LoadBalancer(T cfg) {
		this.cfg = cfg;
	}
	
	public abstract void delegate(ProcessorData data);
	public abstract int getRank(ProcessorData data);
}
