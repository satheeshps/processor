package com.carinov.processor;

import java.io.Serializable;

import javax.persistence.*;

import com.carinov.processor.utils.ProcessorUtil;

@Entity(name="ProcessorData")
public class ProcessorData implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="request_id")
	private long requestId;
	@Column(name="processor")
	private String processor;
	@Column(name="processor_data")
	private Serializable data;
	private boolean isPersist = false;
	private boolean isPassed = false;
	
	public ProcessorData() {
		this.requestId = ProcessorUtil.getUtil().getUniqueID();
	}
	
	public boolean isPersist() {
		return isPersist;
	}
	
	public void setPersist(boolean isPersist) {
		this.isPersist = isPersist;
	}
	
	public long getRequestId() {
		return requestId;
	}
	
	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}
	
	public String getProcessor() {
		return processor;
	}
	
	public void setProcessor(String processor) {
		this.processor = processor;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = (Serializable)data;
	}

	public void setPassed(boolean isPassed) {
		this.isPassed = isPassed;
	}
	
	public boolean isPassed() {
		return isPassed;
	}
}
