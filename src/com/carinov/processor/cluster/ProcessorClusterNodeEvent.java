package com.carinov.processor.cluster;

import java.io.Serializable;

public class ProcessorClusterNodeEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int ADD_NODE = 1;
    public static final int UPDATE_NODE = 2;
    public static final int REMOVE_NODE = 3;
    public static final int FAILED_NODE = 4;
    public static final int ELECTED_COORDINATOR = 5;
    
    private transient ProcessorClusterNode cluster;
    private ProcessorClusterNode node;
    private int type;

    public ProcessorClusterNodeEvent() {
    }
    
    public ProcessorClusterNodeEvent(ProcessorClusterNode source, ProcessorClusterNode node, int type) {
        this.cluster = source;
        this.node = node;
        this.type = type;
    }

    public ProcessorClusterNode getCluster() {
        return cluster;
    }

    public void setCluster(ProcessorClusterNode source){
        this.cluster = source;
    }

    public ProcessorClusterNode getNode() {
        return node;
    }

    public int getType() {
        return type;
    }

    public String toString() {
        return "ClusterEvent[" + getTypeAsString() + " : " + node + "]";
    }

    private String getTypeAsString() {
        String result = "unknown type";
        if (type == ADD_NODE) {
            result = "ADD_NODE";
        }
        else if (type == REMOVE_NODE) {
            result = "REMOVE_NODE";
        }
        else if (type == UPDATE_NODE) {
            result = "UPDATE_NODE";
        }
        else if (type == FAILED_NODE) {
            result = "FAILED_NODE";
        }
        return result;
    }
}