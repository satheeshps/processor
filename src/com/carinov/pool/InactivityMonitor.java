package com.carinov.pool;

public class InactivityMonitor extends Thread {
	private static InactivityMonitor monitor = new InactivityMonitor();

	private InactivityMonitor() {
	}

	public void begin() {
		try {
			monitor.start();
		} catch (Exception e) {
		}
	}

	public void end() {
		try {
			monitor.interrupt();
		} catch (Exception e) {
		}
	}

	@Override
	public void run() {
		try {
		} catch (Exception e) {
		}
	}
}
