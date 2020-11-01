package com.carinov.processor.cluster;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.carinov.processor.utils.ContextController;

public class ProcessorClusterNodeManager implements Watcher {
	private static ProcessorClusterNodeManager manager = null;
	private static Object lock = new Object();
	private Map<String,ProcessorClusterNode> nodes;
	private ProcessorClusterLocalNode local;
	private String localTopic = "";

	private ProcessorClusterNodeManager() throws Exception {
		this.nodes = new HashMap<String, ProcessorClusterNode>();
		this.localTopic = getLocalTopic();
		zookeeperInit();
	}

	private void zookeeperInit() {
		try {
			ZooKeeper zk = ContextController.getContextController().getZookeeperContext().getZooKeeper();
			ContextController.getContextController().getZookeeperContext().addWatch(this);
			zk.getChildren("/", true);
			create();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void init() throws Exception {
		if(manager == null) {
			synchronized (lock) {
				if(manager == null) {
					manager = new ProcessorClusterNodeManager();
				}
			}
		}
	}

	public static ProcessorClusterNodeManager getNodeManager() {
		if(manager == null) {
			try {
				init();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return manager;
	}

	private String getLocalTopic() throws UnknownHostException {
		String machine = InetAddress.getLocalHost().getHostName();
		String process = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		return process + "@" + machine;
	}

	private void create() throws Exception {
		ZooKeeper zk = ContextController.getContextController().getZookeeperContext().getZooKeeper();
		if(zk != null)
			local = new ProcessorClusterLocalNode(localTopic, zk);
	}

	@Override
	public void process(WatchedEvent event) {
		switch(event.getType()) {
		case None:
			switch(event.getState()) {
			case SyncConnected:
				System.out.println("sync connected");
				break;
			case Expired:
				System.out.println("expired");
				break;
			case AuthFailed:
				System.out.println("auth failed");
				break;
			case ConnectedReadOnly:
				System.out.println("connected readonly");
				break;
			case Disconnected:
				System.out.println("disconnected");
				break;
			case SaslAuthenticated:
				System.out.println("sasl");
				break;
			default:
				break;
			}
			break;
		case NodeChildrenChanged:
			nodeChildrenChanged();
			break;
		case NodeCreated:
			nodeCreated();
			break;
		case NodeDataChanged:
			nodeDataChanged(event.getPath());
			break;
		case NodeDeleted:
			nodeDeleted(event.getPath());
			break;
		}
	}

	private synchronized void nodeDeleted(String path) {
		System.out.println("node deleted");
		if(path != null && path.length() > 0) {
			if(nodes.containsKey(path))
				nodes.remove(path);
		}		
	}

	private synchronized void nodeDataChanged(String path) {
		System.out.println("node data changed");
		if(path != null && path.length() > 0) {
			if(nodes.containsKey(path)) {
				ProcessorClusterNode node = nodes.get(path);
				node.update();
			}
		}
	}

	private synchronized void nodeChildrenChanged() {
		try {
			ZooKeeper zk = ContextController.getContextController().getZookeeperContext().getZooKeeper();
			List<String> paths = zk.getChildren("/", true);
			System.out.println("children changed: " + paths);
			for(String npath : paths) {
				addNode(npath, false);					
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void nodeCreated() {
		System.out.println("node created");
	}

	private void addNode(String npath, boolean isLocal) throws Exception {
		ZooKeeper zk = ContextController.getContextController().getZookeeperContext().getZooKeeper();
		if(npath != null && npath.length() > 0) {
			if(!nodes.containsKey(npath) && !npath.equals("zookeeper") && !npath.equals(localTopic)) {
				ProcessorClusterNode node = null;
				if(isLocal)
					node = new ProcessorClusterLocalNode(npath, zk);
				else
					node = new ProcessorClusterNode(npath, zk);
				if(node != null)
					nodes.put(npath, node);
			}
		}
	}

	public Collection<ProcessorClusterNode> getClusterNodes() {
		return nodes.values();
	}

	public ProcessorClusterNode getLocalClusterNode() {
		return local;
	}
}
