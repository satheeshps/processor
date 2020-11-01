package com.carinov.processor.processes;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class JavaProcess {
	private int pid;
	private String[] args;
	private String[] jvmArgs;
	private File[] classPath;
	private String className;
	private File workingDir;
	private File jarFile;
	private boolean isRunning;
	private ProcessBuilder builder;
	private Process proc;

	public JavaProcess() {
		setRunning(false);
	}

	public int startWait() throws IOException, InterruptedException {
		start();
		return join();
	}

	public void start() throws IOException, InterruptedException {
		List<String> cmd = compile();
		if(cmd != null && cmd.size() > 0) {
			if(builder == null) {
				builder = new ProcessBuilder(cmd);
				if(workingDir != null)
					builder.directory(workingDir);
				proc = builder.start();
			}
		}
	}

	public int join() throws InterruptedException {
		if(proc != null)
			return proc.waitFor();
		return Integer.MIN_VALUE;
	}

	private List<String> compile() {
		List<String> list = new ArrayList<String>();
		try {
			setupJavaExe(list);
			setupJvmArgs(list);
			setupClasspath(list);
			setupJarOrClassName(list);
			setupArgs(list);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	private String fixIn(String cmd) {
		if(cmd != null && cmd.length() > 0) {
			if(cmd.contains(" ")) {
				int index = cmd.indexOf(" ", 0);
				if(!(cmd.indexOf("\"") < index && cmd.lastIndexOf("\"") > index))
					cmd = "\"" + cmd + "\"";
			}
		}
		return cmd;
	}

	private void setupArgs(List<String> list) {
		String cmd = "";
		if(args != null && args.length > 0) {
			for(String arg : args) {
				cmd = arg;
				if(cmd != null && cmd.length() > 0) {
					cmd = fixIn(cmd);
					list.add(cmd);
					cmd = "";
				}
			}
		}
	}

	private void setupJarOrClassName(List<String> list) {
		String cmd = "";
		list.add("-jar");
		if(jarFile != null) {
			if(jarFile.isAbsolute()) {
				cmd = jarFile.getAbsolutePath();
			} else {
				if(workingDir != null) {
					cmd = workingDir.getAbsolutePath() + File.separator + jarFile.getName();
				} else
					cmd = jarFile.getAbsolutePath();
			}
		} else {
			if(className != null && className.length() > 0) {
				cmd = className;
			}
		}
		cmd = fixIn(cmd);
		list.add(cmd);
	}

	private void setupClasspath(List<String> list) throws URISyntaxException {
		String cmd = "";
		list.add("-cp");
		if(classPath != null && classPath.length > 0) {
			for(File file : classPath) {
				String subPath = "";
				if(!file.isAbsolute()) {
					if(workingDir != null) {
						subPath = workingDir.getAbsolutePath() + File.separator + file.getPath();
					} else {
						subPath = file.getAbsolutePath();
					}
				} else {
					subPath = file.getAbsolutePath();
				}
//				subPath = fixIn(subPath) + File.pathSeparator;
				cmd += subPath + File.pathSeparator;
			}
			cmd = cmd.substring(0, cmd.length() - 1);
			cmd = fixIn(cmd);
			list.add(cmd);
		}
	}

	private void setupJvmArgs(List<String> list) {
		String cmd = "";
		if(jvmArgs != null && jvmArgs.length > 0) {
			for(String jvmArg : jvmArgs) {
				cmd = jvmArg;
				if(cmd != null && cmd.length() > 0) {
					cmd = fixIn(cmd);
					list.add(cmd);
					cmd = "";
				}
			}
		}
	}

	private void setupJavaExe(List<String> list) {
		String cmd = "";
		String javaHome = System.getProperty("JAVA_HOME");
		if(javaHome != null && javaHome.length() > 0)
			cmd = javaHome + File.separator + "bin" + File.separator + "java.exe";
		else
			cmd = "java";
		if(cmd != null && cmd.length() > 0)
			list.add(cmd);
	}

	public int terminate() throws InterruptedException {
		int ret = 0;
		if(proc != null) {
			proc.destroy();
			ret = join();
		}
		return ret;
	}

	public int waitFor() {
		int ret = -1;
		return ret;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public String[] getJvmArgs() {
		return jvmArgs;
	}

	public void setJvmArgs(String[] jvmArgs) {
		this.jvmArgs = jvmArgs;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public File getJarFile() {
		return jarFile;
	}

	public void setJarFile(File jarFile) {
		this.jarFile = jarFile;
	}

	public File[] getClassPath() {
		return classPath;
	}

	public void setClassPath(File[] classPath) {
		this.classPath = classPath;
	}

	public File getWorkingDir() {
		return workingDir;
	}

	public void setWorkingDir(File workingDir) {
		this.workingDir = workingDir;
	}

	private void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public boolean isRunning() {
		return isRunning;
	}
}
