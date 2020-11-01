package com.carinov.pool;

public class ResourceException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message = "";
	
	public ResourceException(String string) {
		this.message = string;
	}
	
	@Override
	public String getMessage() {
		if(message != null && message.length() > 0)
			return message;
		return null;
	}
}
