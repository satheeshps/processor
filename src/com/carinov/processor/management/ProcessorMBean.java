package com.carinov.processor.management;

import java.util.ArrayList;
import java.util.List;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class ProcessorMBean implements DynamicMBean, NotificationEmitter {
	private MBeanInfo info = null;
	private List<ObjectName> observed = null;

	public static enum ProcessorAttrs {
		DATA_RATE;

		private Object data;
		public Object getValue() {
			return data;
		}

		public void setValue(Object value) {
			this.data = value;
		}
	}

	public ProcessorMBean() throws Exception {
		observed = new ArrayList<ObjectName>();
		MBeanAttributeInfo ainfo = new MBeanAttributeInfo(ProcessorAttrs.DATA_RATE.name(), "Rate of Data", this.getClass().getMethod("getDataRate"), null);
		MBeanNotificationInfo ninfo = new MBeanNotificationInfo(new String[]{"DATA_RATE_CHANGED"},ProcessorMBean.class.getName(),"Data rate of data processed" );
		info = new MBeanInfo("com.carinov.processor.management.ProcessorMBean", "Processor Info", new MBeanAttributeInfo[]{ainfo}, null, null, new MBeanNotificationInfo[]{ninfo});
	}

	public void setDataRate(Integer rate) {
		ProcessorAttrs.DATA_RATE.setValue(rate);
	}

	public Integer getDataRate() {
		return (Integer) ProcessorAttrs.DATA_RATE.getValue();
	}

	@Override
	public Object getAttribute(String attribute)
			throws AttributeNotFoundException, MBeanException,
			ReflectionException {
		Object data = null;
		System.out.println("get attribute");
		ProcessorAttrs attr = ProcessorAttrs.valueOf(attribute);
		switch (attr) {
		case DATA_RATE:
			data = getDataRate();
			break;
		}
		return data;
	}

	@Override
	public AttributeList getAttributes(String[] attributes) {
		System.out.println("get attributes");
		AttributeList list = new AttributeList();

		for(String attribute : attributes) {
			try {
				Object value = getAttribute(attribute);
				Attribute attr = new Attribute(attribute, value);
				if(attr != null)
					list.add(attr);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return list;
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		return info;
	}

	@Override
	public Object invoke(String actionName, Object[] params, String[] signature)
			throws MBeanException, ReflectionException {
		System.out.println("invoke");
		return null;
	}

	@Override
	public void setAttribute(Attribute attribute)
			throws AttributeNotFoundException, InvalidAttributeValueException,
			MBeanException, ReflectionException {
		System.out.println("set attribute");
		ProcessorAttrs attr = ProcessorAttrs.valueOf(attribute.getName());
		switch(attr) {
		case DATA_RATE:
			setDataRate((Integer)attribute.getValue());
			break;
		}
	}

	@Override
	public AttributeList setAttributes(AttributeList attributes) {
		System.out.println("set attributes");
		AttributeList list = new AttributeList();

		for(Attribute attribute : attributes.asList()) {
			try {
				setAttribute(attribute);
				list.add(attribute);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return list;
	}

	@Override
	public void addNotificationListener(NotificationListener arg0,
			NotificationFilter arg1, Object arg2)
					throws IllegalArgumentException {
	}

	@Override
	public MBeanNotificationInfo[] getNotificationInfo() {
		return info.getNotifications();
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
}
