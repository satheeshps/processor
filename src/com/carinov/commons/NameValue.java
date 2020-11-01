package com.carinov.commons;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NameValue<K,V> implements Serializable {

	private static final long serialVersionUID = -4500978736920109805L;

	protected K _k = null;

	protected Object _v = null; // V, List, NameValue
	protected List<NameValue<?,?>> _l = null; // List<NameValue>

	protected ReentrantReadWriteLock _lock = null;

	public NameValue(K k, V v) {
		_k = k;
		_v = v;
	}

	public <T> NameValue(K k, V... v) {
		this(null, k, v);
	}

	protected NameValue(ReentrantReadWriteLock lock, K k) {
		_k = k;
		_lock = lock;
	}

	protected NameValue(ReentrantReadWriteLock lock, K k, V... v) {
		_k = k;
		_lock = lock;
		_v = (v.length > 0) ? 
				(v.length > 1) ? new ArrayList<Object>(Arrays.asList(v)) : v[0] 
             : null;
	}

	final public String name() {
		return (_k instanceof Enum<?>) ? ((Enum<?>) _k).name() : _k.toString();
	}
	
	public boolean isImmutable() {
		return false;
	}
	
	public Value value() {
		_lock.readLock().lock();
		try {
			return new Value(this);
		} finally {
			_lock.readLock().unlock();
		}
	}

	@SuppressWarnings("unchecked")
	public Object peek() {
		_lock.readLock().lock();
		try {
			if (_v != null) {
				if (_v instanceof List<?>)
					return ((List<Object>)_v).get(0);
				else
					return _v;
			} else if (_l != null)
				return _l.get(0);
			return null;
		} finally {
			_lock.readLock().unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object[] peekAll() {
		_lock.readLock().lock();
		try {
			if (_v != null) {
				if (_v instanceof List<?>)
					return ((List<Object>)_v).toArray();
			} else if (_l != null)
				return _l.toArray();
			return null;
		} finally {
			_lock.readLock().unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object peekLast() {
		_lock.readLock().lock();
		try {
			if (_v != null) {
				if (_v instanceof List<?>) {
					List<Object> l = (List<Object>) _v;
					return l.get(l.size()-1);
				}
				else
					return _v;
			} else if (_l != null)
				return _l.get(_l.size()-1);
			return null;
		} finally {
			_lock.readLock().unlock();
		}		
	}
	
	@SuppressWarnings("unchecked")
	protected Object poll() {
		_lock.writeLock().lock();
		try {
			Object r = null;
			if (_v != null) {
				if (_v instanceof List<?>) {
					List<Object> l = (List<Object>)_v;
					r = l.remove(0);
					if (l.size() == 0)
						_v = null;
				} else {
					r = _v;
					_v = null;
				}
			} else if (_l != null) {
				r = _l.get(0);
				if (_l.size() == 0)
					_l = null;
			}
			return r;
		} finally {
			_lock.writeLock().unlock();
		}
	}

	protected void clear() {
		_lock.writeLock().lock();
		try {
			_v = null; _l = null;
		} finally {
			_lock.writeLock().unlock();
		}
	}

	public int size() {
		_lock.readLock().lock();
		try {
			if (_v != null)
				return (_v instanceof List<?>) ? ((List<?>) _v).size() : 1;
			else if (_l != null)
				return _l.size();
			return 0;
		} finally {
			_lock.readLock().unlock();
		}
	}

	protected void set(Object... value) {
		_lock.writeLock().lock();
		try {
			_v = _l = null;
			_v = (value.length > 1) ? new ArrayList<Object>(Arrays.asList(value))
					: value[0];
		} finally {
			_lock.writeLock().unlock();
		}
	}

	protected void set(NameValue<K,V> nv) {
		_lock.writeLock().lock();
		try {
			_l = null;
			_v = nv;
		} finally {
			_lock.writeLock().unlock();
		}
	}

	@SuppressWarnings("unchecked")
	protected void add(Object... value) {
		_lock.writeLock().lock();
		try {
			_l = null;
			if (_v != null) {
				if (_v instanceof List<?>)
					((List<Object>) _v).addAll(Arrays.asList(value));
				else if (_v instanceof NameValue)
					_v = new ArrayList<Object>(Arrays.asList(value));
				else {
					Object t = _v;
					_v = new ArrayList<Object>(Arrays.asList(value));
					((List<Object>) _v).add(0, t);
				}
			} else
				_v = new ArrayList<Object>(Arrays.asList(value));
		} finally {
			_lock.writeLock().unlock();
		}
	}

	@SuppressWarnings("unchecked")
	protected void addFirst(Object... value) {
		_lock.writeLock().lock();
		try {
			_l = null;
			if (_v != null) {
				if (_v instanceof List<?>) {
					List<?> l = Arrays.asList(value);
					for (int i = l.size()-1; i>=0; i--)
						((List<Object>) _v).add(0, l.get(i));
				}
				else if (_v instanceof NameValue)
					_v = new ArrayList<Object>(Arrays.asList(value));
				else {
					Object t = _v;
					_v = new ArrayList<Object>(Arrays.asList(value));
					((List<Object>) _v).add(t);
				}
			} else
				_v = new ArrayList<Object>(Arrays.asList(value));
		} finally {
			_lock.writeLock().unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void addFirst(NameValue<K,V> nv) {
		_lock.writeLock().lock();
		try {
			if (_l != null)
				_l.add(0, nv);
			else if (_v != null && _v instanceof NameValue<?,?>) {
				_l = new ArrayList<NameValue<?,?>>();
				_l.add((NameValue<K,V>) nv);
				_l.add((NameValue<K,V>) _v);
				_v = null;
			} else
				_v = nv;
		} finally {
			_lock.writeLock().unlock();
		}
	}

	@SuppressWarnings("unchecked")
	protected void add(NameValue<K,V> nv) {
		_lock.writeLock().lock();
		try {
			if (_l != null)
				_l.add(nv);
			else if (_v != null && _v instanceof NameValue<?,?>) {
				_l = new ArrayList<NameValue<?,?>>();
				_l.add((NameValue<K,V>) _v);
				_l.add(nv);
				_v = null;
			} else
				_v = nv;
		} finally {
			_lock.writeLock().unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object[] toArray() {
		_lock.readLock().lock();
		try {
			if (_v != null) {
				if (_v instanceof List<?>)
					return ((List<Object>)_v).toArray();
				Object o[] = new Object[1]; o[0] = _v;
				return o;
				
			} else if (_l != null)
				return _l.toArray();
			return new Object[0];
		} finally {
			_lock.readLock().unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(Class<T[]> c) {
		_lock.readLock().lock();
		try {
			Object o[] = toArray();
			for (int i=0; i<o.length; i++)
				o[i] = Value.make(o[i], c);
			return (T[]) o;
		} finally {
			_lock.readLock().unlock();
		}
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		_lock.readLock().lock();
		try {
			out.writeObject(_k);
			out.writeObject(_v);
			out.writeObject(_l.getClass());
			out.writeInt(_l.size());
			for (Object obj : _l) {
				out.writeObject(obj);
			}
			out.flush();
		} finally {
			_lock.readLock().unlock();
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		_k = (K) in.readObject();
		_v = in.readObject();
		Class<Object> listClass = (Class<Object>) in.readObject();
		try {
			_l = (List<NameValue<?,?>>) listClass.newInstance();
			int len = in.readInt();
			for (int i = 0; i < len;)
				_l.add((NameValue) in.readObject());
		} catch (Exception ignore) {}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object arg0) {
		_lock.readLock().lock();
		try {
			boolean result = this.toString().equals(arg0.toString());
			return result;
		} finally {
			_lock.readLock().unlock();
		}
	}

	@Override
	public int hashCode() {
		_lock.readLock().lock();
		try {
			int result = this.toString().hashCode();
			return result;
		} finally {
			_lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		_lock.readLock().lock();
		try {
			String result = name();
			if (_v != null) {
				if (_v instanceof List<?>) {
					if (((List<?>) _v).get(0) instanceof Bean)
						result += " {";
					else
						result += " \"{";
					for (Object o : ((List<?>) _v)) {
						if (o instanceof Bean)
							result += o + " ";
						else
							result += o + ",";
					}
					result = result.substring(0, result.length() - 1);
					if (((List<?>) _v).get(0) instanceof Bean)
						result += " {";
					else
						result += "}\"";
				} else if (_v instanceof NameValue<?,?>)
					result += " {".concat(_v.toString()).concat("}");
				else
					result += " \"".concat(_v.toString()).concat("\"");
			} else if (_l != null) {
				result += " {";
				for (NameValue<?,?> nv : _l)
					result += nv.toString().concat(" ");
				result = result.substring(0, result.length() - 1);
				result += "}";
			} else
				result += " \"null\"";
			return result;
		} finally {
			_lock.readLock().unlock();
		}
	}

	public static void main(String[] args) {
	}
}
