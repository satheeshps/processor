package com.carinov.commons;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class XMLtoData extends DefaultHandler {
	public XMLtoData(final InputStream in, final Writer ou) {
		super(); _in = in; _ou = ou;

		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(_in, this);
		}
		catch (Throwable t) {t.printStackTrace();}
	}

	//===========================================================
	// Static Utility method
	//===========================================================
	static public String toString(final InputStream in) {
		Writer ou = new StringWriter();
		new XMLtoData(in, ou);
		return ou.toString();
	}

	//===========================================================
	// SAX DocumentHandler methods
	//===========================================================
	//	@Override
	//	public void startDocument() throws SAXException {
	//		emit("Bean { version 2.0 encoding UTF-8 }");
	//		nl();
	//	}

	@Override
	public void endDocument() throws SAXException {
		try {
			nl();
			_ou.flush();
		}
		catch (IOException e) {throw new SAXException("I/O error", e);}
	}

	@Override
	public void startElement(	final String namespaceURI, 
			final String sName, // simple name
			final String qName, // qualified name
			final Attributes attrs) throws SAXException {

		echoText();
		String eName = sName; // element name
		if ("".equals(eName))
			eName = qName; // not namespaceAware

		if (elemCk)
			emit("{\n");

		if (eName.charAt(0) == '_') {
			try {
				Integer.decode(eName.substring(1));
				eName = eName.substring(1);
			} catch (NumberFormatException ignore) {}
		}
		emit(eName + " ");
		elemCk = true;
	}

	@Override
	public void endElement(	final String namespaceURI, 
			final String sName, // simple name
			final String qName // qualified name
	) throws SAXException {
		echoText();
		String eName = sName; // element name
		if ("".equals(eName))
			eName = qName; // not namespaceAware

		if (valCk) {
			elemCk = false;
			valCk = false;
		}
		else
			emit("}\n");
	}

	public void characters(final char buf[], final int offset, final int len) throws SAXException {
		String s = new String(buf, offset, len);

		if (s.length() > 0) {
			switch (s.charAt(0)) {
			case '\n': case '\r': case '\t':
				break;
			default:
				if (s.indexOf(' ') > 0 )
					s = "\"" + s.concat("\"");
				if (s.charAt(0) == '_') {
					try {
						Integer.decode(s.substring(1));
						s = s.substring(1);
					} catch (NumberFormatException ignore) {}
				}
				valCk = true;
				break;
			}
		}

		if (textBuffer == null)
			textBuffer = new StringBuffer(s);
		else
			textBuffer.append(s);	
	}

	// Display text accumulated in the character buffer
	private void echoText() throws SAXException {
		if (textBuffer == null) {
			return;
		}
		String s = "" + textBuffer;
		emit(s);
		textBuffer = null;
	}

	// Wrap I/O exceptions in SAX exceptions, to
	// suit handler signature requirements
	private void emit(final String s) throws SAXException {
		try {
			_ou.write(s);
			_ou.flush();
		} catch (IOException e) {throw new SAXException("I/O error", e);}
	}

	// Start a new line
	private void nl() throws SAXException {
		String lineEnd = System.getProperty("line.separator");
		try {
			_ou.write(lineEnd);
		} catch (IOException e) {throw new SAXException("I/O error", e);}
	}

	//------Class declaration start here
	private Writer _ou;
	private InputStream _in;
	private boolean elemCk = false, valCk = false;;
	private StringBuffer textBuffer;
}