package com.carinov.processor;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import com.carinov.commons.Configuration;
import com.carinov.processor.utils.ContextController;
import com.carinov.processor.utils.ZeroMQContext;
import com.carinov.processor.utils.ProcessorUtil;

public class ProcessorJobListenerDaemon implements Runnable {
	private static ProcessorJobListenerDaemon daemon;
	private static Object lock = new Object();
	private Context ctx;
	private Socket subscriber;
	private Thread thread;
	private ProcessorRequestListener listener;

	private ProcessorJobListenerDaemon() {
		try {
			thread = new Thread(this);
			thread.setName("processor-job-assigner");
			thread.setDaemon(true);

			try {
				String host = InetAddress.getLocalHost().getHostAddress();
				String port = (String)Configuration.get("processor.listen-port");
				ctx = ContextController.getContextController().getZeroMQContext().getContext();
				subscriber = ctx.socket(ZMQ.PULL);
				subscriber.bind("tcp://" + host + ":" + port);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void start() {
		thread.start();
	}

	public static void init(ProcessorRequestListener listener) {
		if(daemon == null) {
			synchronized (lock) {
				if(daemon == null) {
					daemon = new ProcessorJobListenerDaemon();
					daemon.listener = listener;
					daemon.start();
				}
			}
		}
	}

	@Override
	public void run() {
		while(true) {
			try {
				byte[] in = subscriber.recv(0);
				if(in != null) {
					Object data = ProcessorUtil.getUtil().unmarshall(in);
					if(data != null) {
						if(data instanceof ProcessorData)
							listener.onArrival((ProcessorData)data);
					}
				}
			} catch(Throwable th) {
				th.printStackTrace();
			} finally {
				//				try {
				//					subscriber.send(ProcessorUtil.getUtil().marshall("DONE"), 0);
				//				} catch(Exception ex) {
				//					ex.printStackTrace();
				//				}
			}
		}
	}

	public void shutdown() {
		if(thread != null) {
			thread.interrupt();
		}
	}
}
