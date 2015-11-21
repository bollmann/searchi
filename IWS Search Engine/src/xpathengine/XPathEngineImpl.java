/*
 * Written by Shreejit Gangadharan
 */
package edu.upenn.cis455.xpathengine;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class XPathEngineImpl.
 */
public class XPathEngineImpl implements XPathEngine {
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass());
	
	/** The x paths. */
	String[] xPaths;
	
	/** The parsed queries. */
	List<XPathQuery> parsedQueries;
	
	/** The parsed query map. */
	Map<String, XPathQuery> parsedQueryMap;

	/**
	 * Instantiates a new x path engine impl.
	 */
	public XPathEngineImpl() {
		// Do NOT add arguments to the constructor!!
		parsedQueries = new ArrayList<XPathQuery>();
		parsedQueryMap = new HashMap<String, XPathQuery>();
	}
	
	/**
	 * Gets the parsed queries.
	 *
	 * @return the parsed queries
	 */
	public List<XPathQuery> getParsedQueries() {
		return parsedQueries;
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathEngine#setXPaths(java.lang.String[])
	 */
	public void setXPaths(String[] s) {
		xPaths = s;
		for (String query : xPaths) {
			try {
				List<XPathQuery> result = XPathQueryParser.recurParseXQuery(
						new ArrayList<XPathQuery>(), null, query, 0);
				parsedQueryMap.put(query, result.get(0));
				parsedQueries.addAll(result);
			} catch (ParseException e) {
				logger.info("Got error for query:" + query);
			}
		}
	}

	/**
	 * Gets the x paths.
	 *
	 * @return the x paths
	 */
	public String[] getXPaths() {
		return xPaths;
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathEngine#isValid(int)
	 */
	public boolean isValid(int i) {
		if(parsedQueryMap.get(xPaths[i]) != null) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathEngine#evaluate(org.w3c.dom.Document)
	 */
	public boolean[] evaluate(Document d) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Source xmlSource = new DOMSource(d);
		Result outputTarget = new StreamResult(outputStream);
		try {
			TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
		} catch (TransformerException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
		InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
		XPathHandler handler = XPathEngineFactory.getSAXHandler();
		handler.setXPaths(xPaths);
		return handler.evaluateSAX(is, handler);
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathEngine#isSAX()
	 */
	@Override
	public boolean isSAX() {
		return true;
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathEngine#evaluateSAX(java.io.InputStream, org.xml.sax.helpers.DefaultHandler)
	 */
	@Override
	public boolean[] evaluateSAX(InputStream document, DefaultHandler handler) {
		boolean[] results = new boolean[xPaths.length];
		SAXParser parser = null;
		try {
			parser = SAXParserFactory.newInstance().newSAXParser();
			
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
		try {
			parser.parse(document, handler);
		} catch (SAXException | IOException e) {
			logger.error("Got error on evaluate Sax:" + e.getMessage());
			e.printStackTrace();
			return results;
		}
		
		for(int i=0;i<xPaths.length;i++) {
			String xPath = xPaths[i];
			if(parsedQueryMap.get(xPath) != null) {
				results[i] = parsedQueryMap.get(xPath).validate();
			} else {
				results[i] = false;
			}
		}
		logger.debug(parsedQueryMap);
		return results;
	}

}
