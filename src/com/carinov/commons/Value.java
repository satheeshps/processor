package com.carinov.commons;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.sql.Date;
import java.util.List;

final public class Value  {
	
	private NameValue<?,?> _nv;
	
	protected Value(NameValue<?,?> nv) {
		_nv = nv;
	}
	
	public int size() {
		return _nv.size();
	}
	
	public <T> T as(Class<T> c) {
		Object o[] = _nv.toArray();
		return (o.length > 0) ? (o.length > 1) ? make(o, c) : make(o[0], c) : null;
	}
	
	@SuppressWarnings("unchecked")
	static protected <T> T make(Object e, Class<T> c) {
		if (c.getName().equals("java.lang.Class")) {
			return (T) e.getClass();
		} else if (e instanceof String) {
			if (c == java.lang.String.class)
				return (T)e;
			else if (c.isPrimitive()) {
				String n = c.getSimpleName();
				if (n.equals("int"))
					return (T) Integer.decode(e.toString());
				else if (n.equals("boolean"))
					return (T) Boolean.valueOf(e.toString());
				else if (n.equals("long"))
					return (T) Long.valueOf(e.toString());
				else if (n.equals("float"))
					return (T) Float.valueOf(e.toString());
				else if (n.equals("short"))
					return (T) Short.valueOf(e.toString());
				else if (n.equals("double"))
					return (T) Double.valueOf(e.toString());
				else if (n.equals("byte"))
					return (T) Byte.valueOf(e.toString());
//				else if (t == Character.TYPE)
			} else if (c.isArray()) {
//				Type t[] = c.getTypeParameters();
			} else if (c.isEnum()) {
				c.getEnumConstants();
			} else {
				if (c == java.sql.Date.class)
					return (T) Date.valueOf(e.toString());
				else {
					Class<?> cls[]; Constructor<?> cstr[] = c.getConstructors();
					for (Constructor<?> cs : cstr) {
						cls = cs.getParameterTypes();
						if (cls.length == 1 && cls[0] == String.class) {
							Object initargs[] = new Object[1];
							initargs[0] = e.toString();
							try {
								return (T) cs.newInstance(initargs);
							} catch (Exception ignore) {}
						}
					}
				}
			}
		} else if (e instanceof Number) {
			if (c.isPrimitive()) {
				String n = c.getSimpleName();
				if (n.equals("int"))
					return (T) new Integer(((Number)e).intValue());
				else if (n.equals("long"))
					return (T) new Long(((Number)e).longValue());
				else if (n.equals("float"))
					return (T) new Float(((Number)e).floatValue());
				else if (n.equals("short"))
					return (T) new Short(((Number)e).shortValue());
				else if (n.equals("double"))
					return (T) new Double(((Number)e).doubleValue());
				else if (n.equals("byte"))
					return (T) new Byte(((Number)e).byteValue());
//				else if (t == Character.TYPE)
			} else if (c.getCanonicalName().equals("java.lang.String")) {
				return (T)((Number)e).toString();
			}
		} else if (c.isArray()) {
			if (e instanceof List<?>) {
				String n = c.getCanonicalName();
				n = n.substring(0,n.length()-2);
				
				Class<?> cc = null;
				Object r = null;
		
				int l = ((List<?>)e).size();
				try {
					cc = Class.forName(n);
					r = Array.newInstance(cc, l);
				} catch (ClassNotFoundException ignore) {
					if (n.equals("int"))			r = Array.newInstance(int.class, l);
					else if (n.equals("double"))	r = Array.newInstance(double.class, l);
					else if (n.equals("boolean"))	r = Array.newInstance(boolean.class, l);
					else if (n.equals("long"))		r = Array.newInstance(long.class, l);
					else if (n.equals("float"))		r = Array.newInstance(float.class, l);
					else if (n.equals("short"))		r = Array.newInstance(short.class, l);
					else if (n.equals("byte"))		r = Array.newInstance(byte.class, l);			
					else if (n.equals("char"))		r = Array.newInstance(char.class, l);
				}

				int i=0;;
				for (Object o : (List<?>)e) {
					if (n.equals("int")) 			Array.setInt(r, i++, make(o, int.class));
					else if (n.equals("boolean"))	Array.setBoolean(r, i++, make(o, boolean.class));
					else if (n.equals("double"))	Array.setDouble(r, i++, make(o, double.class));
					else if (n.equals("long"))		Array.setLong(r, i++, make(o, long.class));
					else if (n.equals("float"))		Array.setFloat(r, i++, make(o, float.class));
					else if (n.equals("short"))		Array.setShort(r, i++, make(o, short.class));
					else if (n.equals("byte"))		Array.setByte(r, i++, make(o, byte.class));
//					else if (n.equals("char"))		Array.setChar(r, i++, Character.p((E)o, char.class));			
					else 							Array.set(r, i++, make(o, cc));
				}
				return (T)r;				
			} else if (e.getClass().isArray()) {
				int l = Array.getLength(e);
				String cln = c.getName();
				cln = cln.substring(2, cln.length()-1);
				Class<?> cl;
				try {
					cl = Class.forName(cln);
					Object o, r = Array.newInstance(cl, l);
					for (int i=0; i<l; i++) {
						o = Array.get(e, i);
						Array.set(r, i, make(o, cl));
					}
					return (T)r;
				} catch (ClassNotFoundException ignore) {}
			}
		} else if (c.getName().equals("java.lang.String")) {
			return (e != null) ? (T) e.toString() : null;
		}
		return (T)e;
	}

	// ----- Object -----
	@Override
	public boolean equals(Object obj) {
		return (_nv!=null) ? _nv.equals(obj) : super.equals(obj);
	}

	@Override
	public int hashCode() {
		return _nv.hashCode();
	}

	@Override
	public String toString() {
		return _nv.toString();
	}

}
