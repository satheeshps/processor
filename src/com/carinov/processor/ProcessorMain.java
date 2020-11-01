package com.carinov.processor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.carinov.commons.Configuration;

public class ProcessorMain {
	private static Logger logger = null;

	private static class ParseProcessor extends Processor<String, String[]> {
		public ParseProcessor(int workers) throws ProcessorAlreadyPresentException {
			super("parser", workers);
		}

		@Override
		protected String[] process(String data) {
			String[] arr = null;
			try {
				logger.info("apphome: " + System.getProperty("apphome"));
				arr = data.split(" ");
				logger.info("parse > " + data);
				for(String item : arr) {
					this.emit("counter", item);
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			return arr;
		}
	}

	private static class CountProcessor extends Processor<String, Integer> {
		private Map<String, Integer> count;

		public CountProcessor(int workers) throws ProcessorAlreadyPresentException {
			super("counter", workers);
		}

		@Override
		public void init() {
			count = new HashMap<String, Integer>();
		}

		@Override
		protected Integer process(String data) {
			int cnt = 1;
			logger.info("count > " + data);
			if(!count.containsKey(data))
				count.put(data, cnt);
			else {
				cnt = count.get(data);
				count.put(data, ++cnt);
			}
			logger.debug(data);
			this.emit("printer", data + "#" + cnt);
			return cnt;
		}
	}

	private static class PrintProcessor extends Processor<String, String> {
		public PrintProcessor(int workers) throws ProcessorAlreadyPresentException {
			super("printer", workers);
		}

		@Override
		protected String process(String data) {
			logger.info("print > " + data);
			return data;
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration.load("config/app.cfg");
		DOMConfigurator.configure("config/logging.xml");
		logger = Logger.getLogger(ProcessorMain.class);
		
		Configuration.set("processor.listen-port", args[0]);
		Configuration.set("processor.zookeeper-port", args[1]);
		Configuration.set("application.home", System.getProperty("apphome"));
		System.out.println("+ starting");
		final ProcessorEngine engine = ProcessorEngine.getEngine();
		ParseProcessor src = new ParseProcessor(1);
		engine.add(src);
		engine.add(new CountProcessor(5));
		engine.add(new PrintProcessor(5));

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("sample.in")));
		String line = "";
		String lines = "";
		while((line = reader.readLine()) != null)
			lines += line;
		reader.close();
		
		src.post(lines);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				engine.close();
			}
		});
		engine.suspend();
	}
}
