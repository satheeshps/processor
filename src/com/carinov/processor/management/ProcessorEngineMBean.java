package com.carinov.processor.management;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ReflectionException;

public class ProcessorEngineMBean implements DynamicMBean, NotificationEmitter {
	@Override
	public void addNotificationListener(NotificationListener arg0,
			NotificationFilter arg1, Object arg2)
			throws IllegalArgumentException {
	}

	@Override
	public MBeanNotificationInfo[] getNotificationInfo() {
		return null;
	}

	@Override
	public void removeNotificationListener(NotificationListener arg0)
			throws ListenerNotFoundException {
	}

	@Override
	public void removeNotificationListener(NotificationListener arg0,
			NotificationFilter arg1, Object arg2)
			throws ListenerNotFoundException {
	}

	@Override
	public Object getAttribute(String arg0) throws AttributeNotFoundException,
			MBeanException, ReflectionException {
		return null;
	}

	@Override
	public AttributeList getAttributes(String[] arg0) {
		return null;
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		return null;
	}

	@Override
	public Object invoke(String arg0, Object[] arg1, String[] arg2)
			throws MBeanException, ReflectionException {
		return null;
	}

	@Override
	public void setAttribute(Attribute arg0) throws AttributeNotFoundException,
			InvalidAttributeValueException, MBeanException, ReflectionException {
	}

	@Override
	public AttributeList setAttributes(AttributeList arg0) {
		return null;
	}
}
