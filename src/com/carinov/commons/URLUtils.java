package com.carinov.commons;

public class URLUtils {
	public static String getQueryValue(String key,String query) {
		if(query != null) {
			String[] params = query.split("&");
			if(params != null) {
				for(String param : params) {
					String item = param.split("=")[0];
					String value = param.split("=")[1];

					if(item.compareTo(key) == 0)
						return value;
				}
			}
		}
		return null;
	}
}
