/*
 * Written by Shreejit Gangadharan
 */
package edu.upenn.cis455.xpathengine;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class XPathElementNode.
 */
public class XPathElementNode extends XPathNode {
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass());
	
	/**
	 * Instantiates a new x path element node.
	 *
	 * @param name the name
	 * @param level the level
	 */
	public XPathElementNode(String name, int level) {
		super(name, level, XPathNodeType.ELEMENT);
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathNode#isStepValid(java.lang.String, java.lang.String, int, edu.upenn.cis455.xpathengine.XPathNodeType, boolean)
	 */
	@Override
	public boolean isStepValid(String key, String value, int level, XPathNodeType type, boolean caseSensitiveMatch) {
		logger.info("Step check "
				+ " key:" + getKey().equals(key)
				+ " level:" + (getLevel() == level)
				+ " type:" + (type == XPathNodeType.ELEMENT )
				+ " caseSensitiveMatch? " + caseSensitiveMatch
				);
		boolean keyMatch = true;
		if(caseSensitiveMatch) {
			keyMatch = keyMatch && getKey().equals(key);
			logger.info("Case sensitive match. keyMatch result:" + keyMatch);
		} else {
			keyMatch = keyMatch && getKey().equalsIgnoreCase(key);
			logger.info("Case insensitive match. keyMatch result:" + keyMatch);
		}
		if (type == XPathNodeType.ELEMENT && keyMatch && getLevel() == level) {
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathNode#clone()
	 */
	public XPathNode clone() {
		XPathNode node = new XPathElementNode(getKey(), getLevel());
		node.setFinal(isFinal());
		node.setValid(isValid());
		node.setPrevNode(null);
		node.setPrevNode(null);
		return node;
	}
}
