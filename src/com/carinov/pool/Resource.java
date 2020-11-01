package com.carinov.pool;

public abstract class Resource {
	private int id = -1;
	private ResourceState state = ResourceState.IDLE;
	
	public Resource() {
		generateId(this);
	}
	
	private void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	protected void setResourceState(ResourceState state) {
		this.state = state;
	}
	
	protected ResourceState getResourceState() {
		return state;
	}
	
	public boolean isActive() {
		return state == ResourceState.ACTIVE;
	}
	
	public boolean isIdle() {
		return state == ResourceState.IDLE;
	}
	
	protected void generateId(Resource resource) {
		resource.setId(System.identityHashCode(resource));
	}
	
	public abstract boolean isReady();
}
