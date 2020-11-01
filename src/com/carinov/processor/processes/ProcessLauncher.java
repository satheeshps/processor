package com.carinov.processor.processes;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

import com.carinov.commons.Configuration;

public class ProcessLauncher implements Runnable {
	private static ProcessLauncher launcher = null;
	private static Object lock = new Object();
	private Thread thread = null;

	private ProcessLauncher() {
		thread = new Thread(this);
		thread.setName("process-launcher");
		thread.setDaemon(true);
	}

	public static ProcessLauncher getLauncher() {
		if(launcher == null) {
			synchronized (lock) {
				if(launcher == null) {
					launcher = new ProcessLauncher();
				}
			}
		}
		return launcher;
	}

	public void launch() {
		thread.start();
		for(int i = 0;i < 1;i++) {
			createInst(i);
		}
	}

	private File createInstanceHome(int i) {
		String home = (String)Configuration.get("application.home");
		File dir = new File(home + File.separator + "instances" + File.separator + "instance_" + i);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	private File[] getClassPath() throws URISyntaxException {
		File[] files = null;
		URL[] urls = ((URLClassLoader)Thread.currentThread().getContextClassLoader()).getURLs();
		if(urls != null && urls.length > 0) {
			int index = 0;
			files = new File[urls.length];
			for(URL url : urls) {
				files[index++] = new File(url.toURI());
			}
		}
		return files;
	}

	private void createInst(int i) {
		try {
			File home = createInstanceHome(i);
			File masterHome = home.getParentFile().getParentFile();
			File jarFile = new File(masterHome.getAbsoluteFile() + File.separator + "processor-sample.jar");
			File[] classPath = getClassPath();
			JavaProcess proc = JavaProcessManager.getProcessorManager().createProcess(home, jarFile, classPath, new String[]{"-Dapphome=" + home, "-javaagent:..\\..\\lib\\openjpa-2.2.1.jar","-Djava.library.path=..\\..\\lib"}, new String[]{"9999","5678"});
			if(proc != null)
				proc.start();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(true) {
			try {
				//				for(JavaProcess process : processes.values()) {
				//					if(!process.isRunning())
				//						;
				//					else
				//						;
				//				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void shutdown() {
		JavaProcessManager.getProcessorManager().shutDownAll();
	}
}
