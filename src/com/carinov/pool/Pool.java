package com.carinov.pool;

public abstract class Pool<T extends Resource> extends ResourceContainer<T> {
	private String name;

	protected Pool(String name) {
		this(name,MAX_RESOURCES,MAX_ACTIVE_RESOURCES,MIN_IDLE_RESOURCES);
	}

	protected Pool(String name,int max,int maxAct,int minIdle) {
		this.name = name;

		setMaxResource(max);
		setMaxActive(maxAct);
		setMinIdle(minIdle);
	}

	public String getName() {
		return name;
	}

	private void setActive(T resource) {
		try {
			if(resource != null) {
				int id = resource.getId();
				if(idle.containsKey(id))
					idle.remove(id);
				active.put(id, resource);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setIdle(T resource) {
		try {
			if(resource != null) {
				int id = resource.getId();
				if(active.containsKey(id))
					active.remove(id);
				idle.put(id, resource);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private T getIdle() {
		T ret = null;
		for(T resource : idle.values()) {
			if(!validate(resource)) {
				if(expire(resource))
					removeResource(resource);
			} else {
				ret = resource;
				break;
			}
		}
		if(ret != null)
			setActive(ret);
		return ret;
	}

	private boolean isSpawnAble() {
		return this.getMaxResource() > this.getTotal();
	}

	private void addResource(T resource) {
		if(resource != null) {
			int id = resource.getId();
			if(!active.containsKey(id)) {
				active.put(id, resource);
				if(getMaxResource() > getTotal())
					setTotal(getTotal() + 1);
			}
		}
	}

	private void removeResource(T resource) {
		if(resource != null) {
			int id = resource.getId();
			if(!idle.containsKey(id)) {
				idle.put(id, resource);
				if(getTotal() > 0)
					setTotal(getTotal() - 1);
			}
		}
	}

	public T acquire() {
		T resource = null;
		//boolean isAcquired = false;
		try {
			//isAcquired = empty.tryAcquire();
			//if(isAcquired) {
				empty.acquire();
				mutex.acquire();
				if(isSpawnAble()) {
					resource = create(name);
					addResource(resource);
				} else {
					resource = getIdle();
				}
			//}
		} catch (Exception e) {
		} finally {
			//if(isAcquired) {
				mutex.release();
				full.release();
			//}
		}
		return resource;
	}

	public void release(T resource) {
		try {
			full.acquire();
			mutex.acquire();
			if(resource != null)
				setIdle(resource);
		} catch (Exception e) {
		} finally {
			mutex.release();
			empty.release();
		}
	}

	protected abstract <V,E> T create(String name);
	protected abstract boolean validate(T resource);
	protected abstract boolean expire(T resource);
}
