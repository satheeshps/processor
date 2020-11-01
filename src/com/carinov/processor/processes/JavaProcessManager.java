package com.carinov.processor.processes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class JavaProcessManager {
	private static JavaProcessManager manager = null;
	private static Object lock = new Object();
	private Map<Integer, JavaProcess> processes = null;

	private JavaProcessManager() {
		processes = new HashMap<Integer, JavaProcess>();
	}

	public static JavaProcessManager getProcessorManager() {
		if(manager == null) {
			synchronized (lock) {
				if(manager == null) {
					manager = new JavaProcessManager();
				}				
			}
		}
		return manager;
	}

	public JavaProcess createProcess(File wd, File jarFile, File[] cp, String[] jvm, String[] args) {
		JavaProcess proc = null;
		if(!processes.containsKey("")) {
			proc = new JavaProcess();
			proc.setArgs(args);
			proc.setJarFile(jarFile);
			proc.setJvmArgs(jvm);
			proc.setClassPath(cp);
			proc.setWorkingDir(wd);
			processes.put(proc.getPid(), proc);
		}
		return proc;
	}

	public void shutDownAll() {
		for(JavaProcess process : processes.values()) {
			try {
				process.terminate();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
