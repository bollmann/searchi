/*
 * Written by Shreejit Gangadharan
 */
package edu.upenn.cis455.xpathengine;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.upenn.cis.cis455.parsers.Parser;

// TODO: Auto-generated Javadoc
/**
 * The Class XPathQueryParser.
 */
public class XPathQueryParser {
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(XPathQueryParser.class);

	/**
	 * Recur parse x query.
	 *
	 * @param result the result
	 * @param currQuery the curr query
	 * @param xPathQueryString the x path query string
	 * @param level the level
	 * @return the list
	 * @throws ParseException the parse exception
	 */
	public static List<XPathQuery> recurParseXQuery(List<XPathQuery> result,
			XPathQuery currQuery, String xPathQueryString, int level)
			throws ParseException {
//		xPathQueryString = xPathQueryString.replace(" ", "");
		logger.debug("Parsing xPatthQueryString:" + xPathQueryString + " level:"
				+ level + " currQuery:" + currQuery + " result:" + result);
		xPathQueryString = xPathQueryString.concat("/");
		StringBuilder nodeName = new StringBuilder();
		boolean added = false;
		XPathNode node = null; // no node created for now
		for (int i = 0; i < xPathQueryString.length(); i++) {
			logger.debug("Now examining character:" + i + ":" + xPathQueryString.charAt(i) + " out of:"
					+ xPathQueryString.length() + " with level:" + level);
			char curr = xPathQueryString.charAt(i);
			if (curr == '/') {
				// only create new node if no existing node
				if (node == null) {
					// examine nodeName for type
					node = createNode(nodeName.toString(), level);
					if (currQuery == null) {
						currQuery = new XPathQuery(node);

					} else {
						currQuery.addNode(node);
					}
				} else {
					// if we have an existing node and we reach the / this node needs to be discarded
					node = null;
				}
				
				if (!added) {
					result.add(currQuery);
					added = true;
				}
				level++;
				nodeName.delete(0, nodeName.length());
				node = null;
			} else if (curr == '[') {
				if (node == null) {
					node = createNode(nodeName.toString(), level);
					currQuery.addNode(node);
					if (!added) {
						result.add(currQuery);
						added = true;
					}
//					level++;
					nodeName.delete(0, nodeName.length());
				}
				// find corresponding closing bracket, extract string and send
				// it recursively with
				// current query clone

				int tempLevel = level + 1;
				for (int j = i + 1; j < xPathQueryString.length(); j++) {
					if (xPathQueryString.charAt(j) == '[') {
						tempLevel++;
					} else if (xPathQueryString.charAt(j) == ']') {
						tempLevel--;
						List<XPathQuery> deps = null;
						if (tempLevel == level) {
							// recur with (i,j) and skip i to j+1, the list that
							// it returns will be dep of current node
							logger.debug("Preparing to recur for substring:"
									+ xPathQueryString.substring(i + 1, j)
									+ " belong to node:" + node
									+ ". Now cloning array.");
							deps = recurParseXQuery(
									new ArrayList<XPathQuery>(),
									currQuery.clone(),
									xPathQueryString.substring(i + 1, j), level+1);
							i = j;
							logger.debug("Adding deps:" + deps + " to node:"
									+ node + " and to result:" + result);
							for (XPathQuery dep : deps) {
								node.addDep(dep);
							}
							logger.debug("Added deps");
							logger.debug("Current query:" + currQuery);
							logger.debug("Afer adding deps, node now:"
									+ node.getKey());
							result.addAll(deps);
							break;
						}

					}
				}
			} else {
				nodeName.append(xPathQueryString.charAt(i));
			}
		}
		logger.info("Exiting query parser with result:" + result);
		return result;
	}

	/**
	 * Creates the node.
	 *
	 * @param pElementString the element string
	 * @param level the level
	 * @return the x path node
	 * @throws ParseException the parse exception
	 */
	public static XPathNode createNode(String pElementString, int level)
			throws ParseException {
		XPathNode node = null;
		String elementString = Parser.removeSpacesOutsideQuotes(pElementString);
		logger.debug("Creating node:" + pElementString + " after parse:"
				+ elementString);

		try {
			if (elementString.contains("contains(text(),")) {
				logger.info("Creating containstext node with :" + elementString);
				// containstextnode
				String text = elementString.split("\"")[1];
				node = new XPathContainsTextNode(text, level);

			} else if (elementString.contains("text()=")) {
				logger.debug("Creating text node with :" + elementString);
				// text node
				String text = elementString.split("\"")[1];
				node = new XPathTextNode(text, level);
			} else if (elementString.contains("@")) {
				logger.debug("Creating attribute node with :" + elementString);
				// attribute node
				String key = elementString.split("@")[1].split("=")[0];
//				if(!Parser.isValidXPathNodeName(elementString)) {
//					throw new ParseException("Node name incorrect!", 1);
//				}
				String value = elementString.split("@")[1].split("=")[1]
						.replace("\"", "").replace("'", "");
				node = new XPathAttributeNode(key, value, level);
			} else {
				logger.debug("Creating element node with :" + elementString);
				if(!Parser.isValidXPathNodeName(elementString)) {
					throw new ParseException("Node name incorrect!", 1);
				}
				node = new XPathElementNode(elementString, level);
			}
		} catch (Exception e) {
			logger.error("Failed while parsing:" + elementString);
			e.printStackTrace();
			throw new ParseException(e.getMessage(), 0);
		}
		logger.debug("Created node:" + node);
		return node;
	}
}
