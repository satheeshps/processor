package com.carinov.processor.cluster;

import java.util.EventListener;

public interface ProcessorClusterNodeListener extends EventListener {
	public void onNodeAdd(ProcessorClusterNodeEvent event);
    public void onNodeUpdate(ProcessorClusterNodeEvent event);
    public void onNodeRemoved(ProcessorClusterNodeEvent event);
    public void onNodeFailed(ProcessorClusterNodeEvent event);
    public void onCoordinatorChanged(ProcessorClusterNodeEvent event);
}
