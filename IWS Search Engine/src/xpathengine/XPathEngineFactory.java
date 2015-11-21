/*
 * Written by Shreejit Gangadharan
 */
package edu.upenn.cis455.xpathengine;


// TODO: Auto-generated Javadoc
/**
 * Implement this factory to produce your XPath engine
 * and SAX handler as necessary.  It may be called by
 * the test/grading infrastructure.
 * 
 * @author cis455
 *
 */
public class XPathEngineFactory {
	
	/**
	 * Gets the x path engine.
	 *
	 * @return the x path engine
	 */
	public static XPathEngineImpl getXPathEngine() {
		return new XPathEngineImpl();
	}
	
	/**
	 * Gets the SAX handler.
	 *
	 * @return the SAX handler
	 */
	public static XPathHandler getSAXHandler() {
		return new XPathHandler();
	}
}
