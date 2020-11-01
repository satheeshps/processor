package com.carinov.processor.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import com.carinov.processor.ProcessorData;
import com.carinov.processor.utils.ContextController;
import com.carinov.processor.utils.ProcessorUtil;

public class ProcessorClusterNode implements StatCallback {
	protected String prefix = "/";
	protected String topic = null;
	protected ZooKeeper zk;
	protected ProcessorClusterNodeData nodeData;
	private Socket pub;
	protected ProcessorClusterNode parent;
	protected List<ProcessorClusterNode> children;

	public ProcessorClusterNode(String topic, ZooKeeper zk, ProcessorClusterNode node) throws Exception {
		this.parent = node;
		this.prefix = getPrefix();
		this.children = new ArrayList<ProcessorClusterNode>();
		this.topic = topic;
		this.zk = zk;
		init();
	}

	public ProcessorClusterNode(String topic, ZooKeeper zk) throws Exception {
		this(topic, zk, null);
	}

	protected String getPrefix() {
		String pf = "/";
		ProcessorClusterNode node = parent;
		while(node != null) {
			pf += node.getTopic() + "/";
			node = node.parent;
		}
		return pf;
	}

	public Object getAbsolutePath() {
		return prefix + topic;
	}

	protected void init() throws Exception {
		zk.exists(prefix + topic, true, this, null);
		updateData(true);
		this.pub = ContextController.getContextController().getZeroMQContext().getContext().socket(ZMQ.PUSH);
		if(nodeData != null)
			this.pub.connect("tcp://" + nodeData.getHost() + ":" + nodeData.getPort());
	}

	protected String getTopic() {
		return topic;
	}

	public ProcessorClusterNodeData getClusterNodeData() {
		return this.nodeData;
	}

	@Override
	public void processResult(int rc, String path, Object ctx, Stat stat) {
		boolean exists;
		if(path.equals(prefix + topic)) {
			switch (rc) {
			case Code.Ok:
				exists = true;
				break;
			case Code.NoNode:
				exists = false;
				break;
			case Code.SessionExpired:
			case Code.NoAuth:
				//            dead = true;
				//            listener.closing(rc);
				return;
			default:
				// Retry errors
				zk.exists(prefix + topic, true, this, null);
				return;
			}

			if (exists) {
				updateData(false);
			}
		}
	}
	
	protected void updateData(boolean sync) {
		try {
			byte data[] = zk.getData(prefix + topic, sync, null);
			Object obj = ProcessorUtil.getUtil().unmarshall(data);
			if(obj instanceof ProcessorClusterNodeData)
				nodeData = (ProcessorClusterNodeData)obj;
			System.out.println("data: " + obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update() {
	}

	public void join(ProcessorClusterNode node) {
	}

	public void leave(ProcessorClusterNode node) {
	}

	public void post(ProcessorData data) throws IOException {
		if(this.pub != null) {
			data.setPassed(true);
			this.pub.send(ProcessorUtil.getUtil().marshall(data), 0);
		}
	}
}
