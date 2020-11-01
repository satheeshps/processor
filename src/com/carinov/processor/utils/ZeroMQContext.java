package com.carinov.processor.utils;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

public class ZeroMQContext {
	private Context ctx;

	ZeroMQContext() {
		ctx = ZMQ.context(1);
	}
	
	public Context getContext() {
		return ctx;
	}
	
	public void shutdown() {
		ctx.term();
	}
}
