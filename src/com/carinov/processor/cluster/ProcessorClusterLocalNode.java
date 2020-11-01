package com.carinov.processor.cluster;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import com.carinov.commons.Configuration;
import com.carinov.processor.utils.ProcessorUtil;

public class ProcessorClusterLocalNode extends ProcessorClusterNode {
	public ProcessorClusterLocalNode(String topic, ZooKeeper zk) throws Exception {
		super(topic, zk);
	}

	public ProcessorClusterLocalNode(String topic, ZooKeeper zk, ProcessorClusterNode node) throws Exception {
		super(topic, zk, node);
	}

	@Override
	protected void init() throws Exception {
		nodeData = new ProcessorClusterNodeData();
		nodeData.setHost(InetAddress.getLocalHost().getHostAddress());
		nodeData.setPort((String)Configuration.get("processor.listen-port"));
		Stat stat = zk.exists(prefix + topic, false);
		if(stat == null) {
			byte[] data = ProcessorUtil.getUtil().marshall((Serializable)nodeData);
			zk.create(prefix + topic, data, (List<ACL>) Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		}
		zk.exists(prefix + topic, true, this, null);
	}

	@Override
	protected void updateData(boolean sync) {
	}
	
	public void addChild(ProcessorClusterLocalNode child) {
		if(!this.children.contains(child)) {
			this.children.add(child);
		}
	}
}
