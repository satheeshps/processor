package com.carinov.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Bean implements Serializable {
	public Bean(String name) {
		_root = _focus = new NameValue<String,Object>(_lock = new ReentrantReadWriteLock(), name);
		_prefix = name; 
		_index = new ConcurrentHashMap<String, NameValue<String,Object>>();
		_index.put(name, _root);
	}

	public <T> Bean(String name, T... value) {
		_root = _focus = new NameValue<String,Object>(_lock = new ReentrantReadWriteLock(), name, value);
		_prefix = name; 
		_index = new ConcurrentHashMap<String, NameValue<String,Object>>();
		_index.put(name, _root);
	}

	protected Bean(Bean data) {
		_root = _focus = data._focus;
		_prefix = data._prefix;
		_index = data._index;
		_lock = data._lock;		
	}

	private Bean(NameValue<String,Object> root, NameValue<String,Object> focus, String prefix, Map<String, NameValue<String,Object>> index, ReentrantReadWriteLock lock) {
		_root = root;
		_focus = focus;
		_prefix = prefix;
		_index = index;
		_lock = lock;
	}

	public String name() {
		return _focus._k;
	}

	public Value value() {
		return _focus.value();
	}

	public Value value(String name) {
		NameValue<String,Object> nv = getNode(name, false);
		return (nv != null) ? nv.value() : null;
	}

	// Setters
	public <T> void set(T... value) {
		_focus.set(value);
	}

	public <T> void set(Enum<?> e, T value) {
		set(e.name(), value);
	}

	public <T> void set(String name, T value) {
		NameValue<String,Object> nv = getNode(name, true);
		nv.set(value);
	}

	public <T> void set(Enum<?> e, T... value) {
		set(e.name(), value);
	}

	public <T> void set(String name, T... value) {
		NameValue<String,Object> nv = getNode(name, true);
		nv.set(value);
	}

	public <T> void add(T... value) {
		_focus.add(value);
	}

	public <T> void add(Enum<?> e, T value) {
		add(e.name(), value);
	}

	public <T> void add(String name, T value) {
		NameValue<String,Object> nv = getNode(name, true);
		nv.add(value);
	}

	public <T> void add(Enum<?> e, T... value) {
		add(e.name(), value);
	}

	public <T> void add(String name, T... value) {
		NameValue<String,Object> nv = getNode(name, true);
		nv.add(value);
	}

	public <T> void addFirst(Enum<?> e, T... value) {
		addFirst(e.name(), value);
	}

	public <T> void addFirst(String name, T... value) {
		NameValue<String,Object> nv = getNode(name, true);
		nv.addFirst(value);
	}

	// Getters
	@SuppressWarnings("unchecked")
	public <T> T[] peekAll() {
		return (T[]) _focus.peekAll();
	}

	public <T> T peekSelf() {
		return (T) _focus;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T peek() {
		return (T) _focus.peek();
	}

	public <T> T peek(Enum<?> e) {
		return (T) peek(e.name());
	}

	@SuppressWarnings("unchecked")
	public <T> T peek(String name) {
		NameValue nv = getNode(name, false);
		return ((nv != null) ? (T) nv.peek() : null);
	}

	@SuppressWarnings("unchecked")
	public <T> T[] peek(String... name) {
		Object r[] = new Object[name.length];
		NameValue nv; int i=0;
		for (String n : name) {
			nv = _index.get(path(n, _prefix));
			r[i++] = (nv != null) ? (T) nv.peek() : null;
		}
		return (T[]) r;
	}

	public <T> T peek(Class<T> c) {
		return Value.make(peek(), c);
	}

	public <T> T peek(Class<T> c, String name) {
		return Value.make(peek(name), c);
	}

	@SuppressWarnings("unchecked")
	public <T> T[] peek(Class<T> c, String... name) {
		Object r[] = peek(name);
		for (int i=0; i<r.length; i++)
			r[i] = Value.make(r[i], c);
		return (T[]) r;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] peek(Class<T>[] c, String... name) {
		if (c.length >= name.length) {
			Object r[] = peek(name);
			for (int i=0; i<r.length; i++)
				r[i] = Value.make(r[i], c[i]);
			return (T[]) r;
		}
		throw new RuntimeException("Length of <Class> array is shorter than <name> array.");
	}

	@SuppressWarnings("unchecked")
	public <T> T poll() {
		return (T) _focus.poll();
	}

	public <T> T poll(Enum<?> e) {
		return (T) poll(e.name());
	}

	@SuppressWarnings("unchecked")
	public <T> T poll(String name) {
		NameValue nv = getNode(name, false);
		return (nv != null) ? (T) nv.poll() : null;
	}

	public <T> T poll(Class<T> c) {
		return Value.make(poll(), c);
	}

	public <T> T poll(Class<T> c, Enum<?> e) {
		return Value.make(poll(e.name()), c);
	}

	public <T> T poll(Class<T> c, String name) {
		return Value.make(poll(name), c);
	}

	public Object[] toArray() {
		return _focus.toArray();
	}

	public Object[] toArray(Enum<?> e) {
		return toArray(e.name());
	}

	public Object[] toArray(String name) {
		NameValue<String,Object> nv = getNode(name, false);
		return (nv != null) ? nv.toArray() : new Object[0];
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(Class<T[]> c) {
		Object o[] = toArray();
		for (int i=0; i<o.length; i++)
			o[i] = Value.make(o[i], c);
		return (T[]) o;
	}

	public <T> T[] toArray(Class<T[]> c, Enum<?> e) {
		return toArray(c, e.name());
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(Class<T[]> c, String name) {
		Object o[] = toArray(name);
		for (int i=0; i<o.length; i++)
			o[i] = Value.make(o[i], c);
		return (T[]) o;
	}

	// Utilities
	public void clear() {
		_focus.clear();
	}

	public void remove(String name) {
		NameValue<String,Object> nv = getNode(name, false);
		if (nv != null) {
			nv.clear();
			_index.remove(path(name, _prefix));
		}
	}

	// Data
	public Bean focusOf(String name) {
		NameValue<String,Object> nv = getNode(name, true);
		return new Bean(_root, nv, path(name, _prefix), _index, _lock);
	}

	@SuppressWarnings("unchecked")
	public Bean[] dataOf(String name) {
		NameValue<String,Object> nv = getNode(name, false);
		if (nv != null) {
			Object o[] = nv.toArray();
			if (o != null && o.length>0) {
				Bean d[] = new Bean[o.length];
				for (int i=0; i<o.length; i++) {
					if (o[i] instanceof NameValue<?,?>)
						d[i] = new Bean(_root, (NameValue<String,Object>)o[i], path(name, _prefix), _index, _lock);
					else if (o[i] instanceof Bean)
						d[i] = (Bean) o[i];
				}
				return d;
			}
		}
		return null;
	}

	public Bean copyOf() {
		return Bean.make(this.toString());
	}

	private NameValue<String,Object> getNode(String name, boolean createIfNotFound) {
		NameValue<String,Object> nv = _index.get(path(name, _prefix));
		return (nv != null) ? nv : (createIfNotFound) ? createNode(_focus, _prefix, name, _index, _lock) : null;
	}

	static private String path(final String name, String prefix) {
		return (name != null && name.length() > 0) ? prefix.concat(".").concat(name) : prefix;
	}

	//	private void createIndex(NameValue node, String prefix, final Map<String, NameValue> index) {
	//		_lock.readLock().lock();
	//		try {
	//			if (node != null && prefix != null && prefix.length() > 0 && index != null) {
	//				if (node._l != null) {
	//					for (NameValue nv : node._l) {
	//						createIndex(nv, path(nv._k, prefix), index);
	//					}
	//				} else if (node._v != null) {
	//					if (node._v instanceof NameValue) {
	//						NameValue nv  = (NameValue)node._v;
	//						createIndex(nv, path(nv._k, prefix), index);
	//					}
	//				}
	//				index.put(prefix, node);
	//			}
	//		} finally {
	//			_lock.readLock().unlock();
	//		}
	//	}

	@SuppressWarnings("unchecked")
	static private NameValue<String,Object> createNode(NameValue<String,Object> focus, String prefix, String name, final Map<String, NameValue<String,Object>> index, ReentrantReadWriteLock lock) {
		NameValue<String,Object> nv = null;

		String path = prefix;
		for (String n : name.split("\\.")) {
			path = path.concat(".").concat(n);

			if (focus._v != null) {
				if (focus._v instanceof NameValue<?,?>) {
					nv = (NameValue<String,Object>)focus._v;
					if (!nv._k.equals(n)) {
						nv = new NameValue<String,Object>(lock, n);
						focus.add(nv);
						index.put(path, nv);
					}
				} else {
					nv = new NameValue<String,Object>(lock, n);
					focus.set(nv);
					index.put(path, nv);
				}
			} else if (focus._l != null) {
				for (Object obj : focus._l) {
					if (((NameValue<String,Object>)obj).name().equals(n)) {
						nv = ((NameValue<String,Object>)obj);
						break;
					}
				}
				if (nv == null) {
					nv = new NameValue(lock, n);
					focus.add(nv);
					index.put(path, nv);
				}
			} else {
				nv = new NameValue(lock, n);
				focus.set(nv);
				index.put(path, nv);
			}
			focus = nv;
			nv = null;
		}
		return focus;
	}

	public boolean contain(Enum<?> name) {
		return contain(name.name());
	}

	public boolean contain(String name) {
		NameValue<String,Object> nv = getNode(name, false);
		return (nv != null && nv.peek() != null) ? true : false;
	}

	public int size(String name) {
		NameValue<String,Object> nv = getNode(name, false);
		return (nv != null) ? nv.size() : 0;
	}

	public int size() {
		return _focus.size();
	}

	//	@SuppressWarnings("unchecked")
	//	@Override
	//	protected Object clone() throws CloneNotSupportedException {
	//		Bean result = null;
	//		try {
	//			result = make(this.toString(), null, _listClass, (Class<Map<String, NameValue>>)_index.getClass());
	//			result._prefix = _prefix;
	//			result._focus = result._index.get(_prefix);
	//		} catch (CharacterCodingException ignore) {
	//		}
	//		return result;
	//	}

	@Override
	public boolean equals(Object obj) {
		return (this != obj) ? this.toString().equals(obj.toString()) : true;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public String toString() {
		return _focus.toString();
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {

	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {

	}

	@SuppressWarnings("unchecked")
	static private List<NameValue<String, Object>> parse(final DataStreamTokenizer t, String path, Map<String, NameValue<String,Object>> index, ReentrantReadWriteLock lock) {
		ArrayList<NameValue<String, Object>> result = null;

		try {
			result = new ArrayList<NameValue<String,Object>>();

			String name = null;
			while (t.nextToken() != StreamTokenizer.TT_EOF) {
				switch (t.ttype) {
				case '{' :
					if (name != null) {
						String p = path(name, path);
						if (p.charAt(0) == '.') p = p.substring(1);
						List<?> pairs = parse(t, p, index, lock);
						if (pairs != null && pairs.size() > 0) {
							NameValue<String,Object> nv = new NameValue<String,Object>(lock, name);
							if (pairs.size() > 1) {
								nv._l = (List<NameValue<?, ?>>) pairs;
							} else if (pairs.size() == 1) {
								nv._v = pairs.remove(0);
							} else {
								break;
							}
							result.add(nv);
						}
						name = null;
					}
					break;
				case '}' :
					for (NameValue<String,Object> nv : result) {
						String p = path(nv._k, path);
						index.put(p, nv);
					}
					return result;

				case 13:
				case StreamTokenizer.TT_EOL:
					break;

					//					case StreamTokenizer.TT_NUMBER :
					//					case StreamTokenizer.TT_WORD :
				default :
					if (name == null && t.ttype != StreamTokenizer.TT_NUMBER) {
						name = t.sval;
					} else {
						NameValue<String,Object> nv = new NameValue<String,Object>(lock, name);
						if (t.ttype == StreamTokenizer.TT_NUMBER)
							nv.add(t.nval);
						else {
							try {
								if (t.sval.toLowerCase().equals("true") || t.sval.toLowerCase().equals("false"))
									nv.add(Boolean.parseBoolean(t.sval));
								else
									nv.add(Integer.parseInt(t.sval));
							} catch (NumberFormatException e) {nv.add(t.sval);}
						}

						result.add(nv);
						index.put(path(name, path), nv);
						name = null;
					}
					break;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return result;
	}

	static public Bean fromXml(String data) {
		String xml = XMLtoData.toString(new StringBufferInputStream(data));
		return Bean.make(xml);
	}

	static public Bean fromXml(File file) {
		try {
			FileInputStream in = new FileInputStream(file);
			String data = XMLtoData.toString(in);
			return Bean.make(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static public Bean make(final String s) {
		Bean result = null;
		StringReader r = new StringReader(s);
		DataStreamTokenizer t = new DataStreamTokenizer(r);

		Map<String, NameValue<String,Object>> index = new ConcurrentHashMap<String, NameValue<String,Object>>();
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

		List<NameValue<String,Object>> pairs = parse(t, "", index, lock);
		if (pairs!= null && pairs.size() == 1) {
			NameValue<String,Object> nv = pairs.remove(0);
			result = new Bean(nv, nv, nv._k, index, lock);
			index.put(nv._k, nv);
		}

		return result;
	}

	private String stringize(Document doc) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance(); 
			Transformer transformer = transformerFactory.newTransformer(); 
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            
			DOMSource source = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			Result output = new StreamResult(writer); 
			transformer.transform(source, output);
			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String toXml() {
		try {
			Element root = null;
			Queue<Element> equeue = new LinkedList<Element>();
			Queue<NameValue> queue = new LinkedList<NameValue>();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			equeue.add(root = doc.createElement(this._focus.name()));
			doc.appendChild(root);
			queue.add(this._focus);
			while(!queue.isEmpty()) {
				Element eparent = equeue.remove(); 
				NameValue parent = queue.remove();
				if(parent != null) {
					if(parent.size() > 0) {
						if(parent.peekAll() != null) {
							for(Object obj : parent.peekAll()) {
								if(obj != null) {
									if(obj instanceof NameValue) {
										NameValue nv = (NameValue)obj;
										if(nv.peek() instanceof NameValue) {
											queue.add(nv);
											Element child = doc.createElement(nv.name());
											eparent.appendChild(child);
											equeue.add(child);
										} else {
											Element child = doc.createElement(nv.name());
											child.setTextContent(nv.peek().toString());
											eparent.appendChild(child);
										}
									}
								}
							}
						} else {
							Object obj = parent.peek();
							if(obj instanceof NameValue) {
								NameValue nv = (NameValue)obj;
								if(nv.peek() instanceof NameValue) {
									queue.add(nv);
									Element child = doc.createElement(nv.name());
									eparent.appendChild(child);
									equeue.add(child);
								} else {
									Element child = doc.createElement(nv.name());
									child.setTextContent(nv.peek().toString());
									eparent.appendChild(child);
								}
							}
						}
					}
				}
			}
			return stringize(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Document toXmlDocument() {
		try {
			Element root = null;
			Queue<Element> equeue = new LinkedList<Element>();
			Queue<NameValue> queue = new LinkedList<NameValue>();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			equeue.add(root = doc.createElement(this._focus.name()));
			doc.appendChild(root);
			queue.add(this._focus);
			while(!queue.isEmpty()) {
				Element eparent = equeue.remove(); 
				NameValue parent = queue.remove();
				if(parent != null) {
					if(parent.size() > 0) {
						if(parent.peekAll() != null) {
							for(Object obj : parent.peekAll()) {
								if(obj != null) {
									if(obj instanceof NameValue) {
										NameValue nv = (NameValue)obj;
										if(nv.peek() instanceof NameValue) {
											queue.add(nv);
											Element child = doc.createElement(nv.name());
											eparent.appendChild(child);
											equeue.add(child);
										} else {
											Element child = doc.createElement(nv.name());
											child.setTextContent(nv.peek().toString());
											eparent.appendChild(child);
										}
									}
								}
							}
						} else {
							Object obj = parent.peek();
							if(obj instanceof NameValue) {
								NameValue nv = (NameValue)obj;
								if(nv.peek() instanceof NameValue) {
									queue.add(nv);
									Element child = doc.createElement(nv.name());
									eparent.appendChild(child);
									equeue.add(child);
								} else {
									Element child = doc.createElement(nv.name());
									child.setTextContent(nv.peek().toString());
									eparent.appendChild(child);
								}
							}
						}
					}
				}
			}
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static class DataStreamTokenizer extends java.io.StreamTokenizer {
		private DataStreamTokenizer(final StringReader r) {
			super(r);

			// ignore any text following # on the same line
			commentChar('#');

			//treat following as single character tokens
			ordinaryChar('{');
			ordinaryChar('}');

			// treat characters enclosed inside following quote chars as a single token
			quoteChar(29); // ASCII code 29 (Group Seperator)
			quoteChar('"');

			//treat following characters as token constituents. They form parts of a token.

			wordChars('.', '.');
			wordChars('%', '%');
			wordChars('-', '-');
			wordChars('0', '9');
			wordChars('\\', '\\');
			wordChars('_', '_');
			wordChars('@', '@');
			wordChars(';', ';');
			wordChars(':', ':');
			wordChars('_', '_');
			wordChars('[', '[');
			wordChars(']', ']');
			wordChars('^', '^');
			wordChars('<', '<');
			wordChars('>', '>');
			wordChars('=', '=');
			wordChars('/', '/');
			wordChars('*', '*');
			wordChars('~', '~');
			wordChars(',',',');
			wordChars('+', '+');
			wordChars('(', '(');
			wordChars(')', ')');

			//			wordChars('.', '.');
			//			wordChars(',', ',');
			//			wordChars('-', '-');
			//			wordChars('_', '_');
			//			wordChars('+', '+');

			//			wordChars('\u0035', '\u0064');
			//			wordChars('\u0091', '\u0096');
			//			wordChars('|', '|');
			//			wordChars('~', '~');

		}

		public void parseNumbers() {
			// This space intentionally left blank.
		}
	}

	private NameValue<String,Object> _root = null;
	private NameValue<String,Object> _focus = null;
	private String _prefix = null;
	protected ReentrantReadWriteLock _lock = null;
	protected Map<String, NameValue<String,Object>> _index = null;

	private static final long serialVersionUID = -248498621243755153L;

//	public Object[] toValueArray() {
//		List<Object> items = new ArrayList<Object>();
//		try {
//			Queue<NameValue> queue = new LinkedList<NameValue>();
//			queue.add(this._focus);
//			while(!queue.isEmpty()) {
//				NameValue parent = queue.remove();
//				if(parent != null) {
//					if(parent.size() > 0) {
//						if(parent.peekAll() != null) {
//							for(Object obj : parent.peekAll()) {
//								if(obj != null) {
//									if(obj instanceof NameValue) {
//										NameValue nv = (NameValue)obj;
//										if(nv.peek() instanceof NameValue) {
//											queue.add(nv);
//										} else {
//											items.add(nv.peek().toString());
//										}
//									}
//								}
//							}
//						} else {
//							Object obj = parent.peek();
//							if(obj instanceof NameValue) {
//								NameValue nv = (NameValue)obj;
//								if(nv.peek() instanceof NameValue) {
//									queue.add(nv);
//								} else {
//									items.add(nv.peek().toString());
//								}
//							}
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return items.toArray();
//	}
}
