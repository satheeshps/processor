package com.carinov.processor.management;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.List;

import org.hyperic.sigar.Sigar;

public class SystemResourceMonitor {
	private static SystemResourceMonitor gauge;
	private static Object lock = new Object();
	private ThreadMXBean tb;
	private List<MemoryPoolMXBean> memPools;
	private Sigar sigar;
	
	private SystemResourceMonitor() {
		sigar = new Sigar();
		tb = ManagementFactory.getThreadMXBean();
		memPools = ManagementFactory.getMemoryPoolMXBeans();
	}

	public static SystemResourceMonitor getGauge() {
		if(gauge == null) {
			synchronized (lock) {
				if(gauge == null) {
					gauge = new SystemResourceMonitor();
				}
			}
		}
		return gauge;
	}
	
	public long getActiveThreadCount() {
		return tb.getPeakThreadCount();
	}
	
	public long getTotalStartedThreadCount() {
		return tb.getTotalStartedThreadCount();
	}
	
	public long getDaemonThreadCount() {
		return tb.getDaemonThreadCount();
	}
	
	public double getCpuPercentage() {
		try {
			return sigar.getCpuPerc().getCombined() * 100;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public double getMemoryUsedPercentage() {
		try {
			return sigar.getMem().getUsedPercent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public double getMemoryFreePercentage() {
		try {
			return sigar.getMem().getFreePercent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public double getTotalPhysicalMemory() {
		try {
			return sigar.getMem().getRam();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public double getTotalMemory() {
		try {
			return sigar.getMem().getTotal();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	@Override
	public String toString() {
		try {
			StringWriter writer = new StringWriter();
			PrintWriter out = new PrintWriter(writer);
			out.printf("Current thread count: %d%n", tb.getThreadCount());
			out.printf("Peak thread count: %d%n", tb.getPeakThreadCount());
			out.printf("Total started thread count: %d%n", tb.getTotalStartedThreadCount());
			out.printf("Daemon thread count: %d%n", tb.getDaemonThreadCount());

			for (MemoryPoolMXBean pool : memPools) {
				MemoryUsage peak = pool.getPeakUsage();
				out.printf("Peak %s memory used: %,d%n", pool.getName(), peak.getUsed());
				out.printf("Peak %s memory reserved: %,d%n", pool.getName(), peak.getCommitted());
			}
			out.close();
			return writer.toString();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
