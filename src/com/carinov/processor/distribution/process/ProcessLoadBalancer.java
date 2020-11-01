package com.carinov.processor.distribution.process;

import java.util.Collection;

import com.carinov.processor.ProcessorData;
import com.carinov.processor.cluster.ProcessorClusterNode;
import com.carinov.processor.cluster.ProcessorClusterNodeData;
import com.carinov.processor.cluster.ProcessorClusterNodeManager;
import com.carinov.processor.distribution.LoadBalancer;

public class ProcessLoadBalancer extends LoadBalancer<ProcessLoadBalancerConfig> {
	public ProcessLoadBalancer(ProcessLoadBalancerConfig cfg) {
		super(cfg);
	}

	private ProcessorClusterNode findBest() {
		ProcessorClusterNode best = null;
		Collection<ProcessorClusterNode> nodes = ProcessorClusterNodeManager.getNodeManager().getClusterNodes();
		if(nodes != null) {
			for(ProcessorClusterNode node : nodes) {
				return node;
			}
		}
		return best;
	}

	private int findBestRank() {
		int best = -1;
		Collection<ProcessorClusterNode> nodes = ProcessorClusterNodeManager.getNodeManager().getClusterNodes();
		if(nodes != null) {
			int rank = best;
			for(ProcessorClusterNode node : nodes) {
				rank = calculateWeight(node);
				if(rank > best)
					best = rank;
			}
		}
		return best;
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
	public void delegate(ProcessorData data) {
		ProcessorClusterNode node = findBest();
		if(node != null) {
			try {
				node.post(data);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public int getRank(ProcessorData data) {
		int rank = data != null && data.isPassed() ? -1 : findBestRank();
		return rank;
	}
}
