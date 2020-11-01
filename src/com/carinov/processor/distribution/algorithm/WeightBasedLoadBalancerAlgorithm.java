package com.carinov.processor.distribution.algorithm;

import java.util.List;

import com.carinov.processor.ProcessorData;
import com.carinov.processor.distribution.LoadBalancer;

public class WeightBasedLoadBalancerAlgorithm implements LoadBalancerAlgorithm {
	@Override
	public LoadBalancer<?> evaluate(List<LoadBalancer<?>> data, ProcessorData pdata) {
		int rank = 0;
		int greatest = rank;
		LoadBalancer<?> choosen = null;
		for(LoadBalancer<?> connection : data) {
			rank = connection.getRank(pdata);
			if(rank > greatest) {
				greatest = rank;
				choosen = connection;
			}
		}
		return choosen;
	}
}
