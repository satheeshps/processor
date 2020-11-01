package com.carinov.processor.distribution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.carinov.processor.ProcessorData;
import com.carinov.processor.distribution.algorithm.LoadBalancerAlgorithm;
import com.carinov.processor.distribution.algorithm.WeightBasedLoadBalancerAlgorithm;
import com.carinov.processor.distribution.process.ProcessLoadBalancer;
import com.carinov.processor.distribution.process.ProcessLoadBalancerConfig;
import com.carinov.processor.distribution.thread.ThreadLoadBalancer;
import com.carinov.processor.distribution.thread.ThreadLoadBalancerConfig;
import com.carinov.processor.management.DataMeter;
import com.carinov.processor.utils.ProcessorUtil;

public class TaskDistributor {
	private static TaskDistributor controller;
	private static Object lock = new Object();
	private List<LoadBalancer<?>> connections;
	private LoadBalancerAlgorithm alg;

	private TaskDistributor() {
		load();
	}

	public void setAlgorithm(LoadBalancerAlgorithm alg) {
		this.alg = alg;
	}

	private void load() {
		this.connections = new ArrayList<LoadBalancer<?>>();
		this.connections.add(new ThreadLoadBalancer(new ThreadLoadBalancerConfig()));
		this.connections.add(new ProcessLoadBalancer(new ProcessLoadBalancerConfig()));
		
		setAlgorithm(new WeightBasedLoadBalancerAlgorithm());
	}

	public static void init() {
		if(controller == null) {
			synchronized (lock) {
				if(controller == null)
					controller = new TaskDistributor();
			}
		}
	}

	public static TaskDistributor getTaskDistributor() {
		if(controller == null)
			init();
		return controller;
	}

	public void allot(ProcessorData data) throws IOException {
		byte[] bdata = ProcessorUtil.getUtil().marshall(data);
		if(bdata != null) {
			if(alg != null) {
				LoadBalancer<?> choosen = alg.evaluate(connections, data);
				if(choosen != null) {
					choosen.delegate(data);
				}
			}
		}
	}
}
