package com.carinov.processor.utils;

import java.lang.management.ManagementFactory;

import javax.management.Attribute;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import com.carinov.processor.management.ProcessorMBean;
import com.carinov.processor.management.ProcessorMBean.ProcessorAttrs;

public class JMXContext {
	private MBeanServer server;

	JMXContext() {
		server = ManagementFactory.getPlatformMBeanServer();
	}

	public ObjectInstance register(Object observe) throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		if(observe != null) {
			String name = observe.getClass().getName();
			name = name.substring(0, name.lastIndexOf("."));
			name += ":type=" + observe.getClass().getSimpleName();
			ObjectName objName = new ObjectName(name);
			return server.registerMBean(observe, objName);
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		ProcessorMBean observe = new ProcessorMBean();
		new JMXContext().register(observe);
		
		Thread.sleep(10000);
		observe.setAttribute(new Attribute(ProcessorAttrs.DATA_RATE.name(), 1300));
		Thread.sleep(60000);
	}
}
