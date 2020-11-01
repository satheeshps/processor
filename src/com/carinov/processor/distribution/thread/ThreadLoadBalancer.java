package com.carinov.processor.distribution.thread;

import com.carinov.processor.ProcessorData;
import com.carinov.processor.ProcessorThreadManager;
import com.carinov.processor.cluster.ProcessorClusterNode;
import com.carinov.processor.cluster.ProcessorClusterNodeData;
import com.carinov.processor.cluster.ProcessorClusterNodeManager;
import com.carinov.processor.distribution.LoadBalancer;

public class ThreadLoadBalancer extends LoadBalancer<ThreadLoadBalancerConfig> {
	public ThreadLoadBalancer(ThreadLoadBalancerConfig cfg) {
		super(cfg);
	}

	@Override
	public void delegate(ProcessorData data) {
		try {
			ProcessorThreadManager.getProcessorThreadManager().assign(data);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private int calculateWeight(ProcessorClusterNode node) {
		int rank = -1;
		if(node != null) {
			ProcessorClusterNodeData data = node.getClusterNodeData();
			if(data != null) {
				long availableThreads = data.getMaximumThreads() - data.getActiveThreads() - data.getMinimumThreads();
				int memUtil = data.getMemoryUtilization();
				int procUtil = data.getProcessorUtilization();
				rank = ((int)availableThreads * 20 + memUtil * 40 + (100 - procUtil) * 40)/100;
			}
		}
		return rank;
	}

	@Override
	public int getRank(ProcessorData pdata) {
		int rank = calculateWeight(ProcessorClusterNodeManager.getNodeManager().getLocalClusterNode());
		return pdata != null && pdata.isPassed() ? Integer.MAX_VALUE : rank;
	}
}
