package com.carinov.processor.distribution.algorithm;

import java.util.List;

import com.carinov.processor.ProcessorData;
import com.carinov.processor.distribution.LoadBalancer;

public interface LoadBalancerAlgorithm {
	public LoadBalancer<?> evaluate(List<LoadBalancer<?>> data, ProcessorData pdata);
}
