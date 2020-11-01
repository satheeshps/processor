package com.carinov.processor;

import java.util.HashMap;
import java.util.Map;

class ProcessorRegistry {
	private static ProcessorRegistry registry;
	private static Object lock = new Object();
	private Map<String, Processor<?,?>> processors;
	
	private ProcessorRegistry() {
		processors = new HashMap<String, Processor<?,?>>();
	}
	
	static ProcessorRegistry getRegistry() {
		if(registry == null) {
			synchronized (lock) {
				if(registry == null)
					registry = new ProcessorRegistry();
			}
		}
		return registry;
	}
	
	void addProcessor(Processor<?,?> processor) throws ProcessorAlreadyPresentException {
		if(!processors.containsKey(processor.getName()))
			processors.put(processor.getName(), processor);
		else
			throw new ProcessorAlreadyPresentException();
	}
	
	void removeProcessor(String name) {
		if(processors.containsKey(name))
			processors.remove(name);
	}
	
	<T, E> Processor<T,E> getProcessor(String name) {
		return (Processor<T, E>) processors.get(name);
	}
}
