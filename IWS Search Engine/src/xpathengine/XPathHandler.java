/*
 * Written by Shreejit Gangadharan
 */
package edu.upenn.cis455.xpathengine;

import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class XPathHandler.
 */
public class XPathHandler extends DefaultHandler implements XPathEngine {
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass());
	
	/** The queries. */
	List<XPathQuery> queries;
	
	/** The engine. */
	XPathEngineImpl engine;
	
	/** The case sensitive match. */
	boolean caseSensitiveMatch = true;
	
	/** The level. */
	int level;
	
	/**
	 * Instantiates a new x path handler.
	 */
	public XPathHandler() {
		super();
		engine = XPathEngineFactory.getXPathEngine();
		level = 0;
	}
	
	
	/**
	 * Instantiates a new x path handler.
	 *
	 * @param queries the queries
	 */
	public XPathHandler(List<XPathQuery> queries) {
		this.queries = queries;
		level = 0;
	}
	
	/**
	 * Sets the queries.
	 *
	 * @param queries the new queries
	 */
	public void setQueries(List<XPathQuery> queries) {
		this.queries = queries;
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String name, String qName,
			Attributes atts) {
		level++;
		if(level == 1 && qName.equals("html")) {
			caseSensitiveMatch = false;
		}
		logger.debug("url:" + uri + " name:" + name + " qName:" + qName + " level:" + level);
		moveForwardTo(qName, null, level, XPathNodeType.ELEMENT, caseSensitiveMatch);
		
		level++;
		for(int i=0;i<atts.getLength();i++) {
			logger.debug("Now getting attribute key:" + atts.getQName(i) + " val:" + atts.getValue(i) + " level:" + level);
			moveForwardTo(atts.getQName(i), atts.getValue(i), level, XPathNodeType.ATTRIBUTE, caseSensitiveMatch);
		}
		level--;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String name, String qName) {
		level--;
		
		logger.debug("url:" + uri + " name:" + name + " qName:" + qName + " level:" + level);
		try {
			moveBackwardTo(level);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char ch[], int start, int length) {
		level++;
		StringBuilder string = new StringBuilder();
		for (int i = start; i < start + length; i++) {
			string.append(ch[i]);
		}
		
		moveForwardTo(string.toString(), null, level, XPathNodeType.TEXT, caseSensitiveMatch);
		level--;
		
	}
	
	/**
	 * Move forward to.
	 *
	 * @param key the key
	 * @param value the value
	 * @param level the level
	 * @param type the type
	 * @param caseSensitiveMatch the case sensitive match
	 */
	public void moveForwardTo(String key, String value, int level, XPathNodeType type, boolean caseSensitiveMatch) {
		for(XPathQuery query : queries) {
			query.moveForwardTo(key, value, level, type, caseSensitiveMatch);
		}
	}
	
	/**
	 * Move backward to.
	 *
	 * @param level the level
	 * @throws IllegalStateException the illegal state exception
	 */
	public void moveBackwardTo(int level) throws IllegalStateException {
		for(XPathQuery query : queries) {
			query.moveBackwardTo(level);
		}
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathEngine#setXPaths(java.lang.String[])
	 */
	@Override
	public void setXPaths(String[] expressions) {
		engine.setXPaths(expressions);
		queries = engine.getParsedQueries();
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathEngine#isValid(int)
	 */
	@Override
	public boolean isValid(int i) {
		return engine.isValid(i);
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathEngine#isSAX()
	 */
	@Override
	public boolean isSAX() {
		return engine.isSAX();
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathEngine#evaluate(org.w3c.dom.Document)
	 */
	@Override
	public boolean[] evaluate(Document d) {
		return engine.evaluate(d);
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathEngine#evaluateSAX(java.io.InputStream, org.xml.sax.helpers.DefaultHandler)
	 */
	@Override
	public boolean[] evaluateSAX(InputStream document, DefaultHandler handler) {
		return engine.evaluateSAX(document, handler);
	}

	/**
	 * Gets the queries.
	 *
	 * @return the queries
	 */
	public List<XPathQuery> getQueries() {
		return engine.getParsedQueries();
	}

}
