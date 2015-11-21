/*
 * Written by Shreejit Gangadharan
 */
package edu.upenn.cis455.xpathengine;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class XPathContainsTextNode.
 */
public class XPathContainsTextNode extends XPathNode {
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass());
	
	/**
	 * Instantiates a new x path contains text node.
	 *
	 * @param name the name
	 * @param level the level
	 */
	public XPathContainsTextNode(String name, int level) {
		super(name, level, XPathNodeType.CONTAINSTEXT);
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathNode#isStepValid(java.lang.String, java.lang.String, int, edu.upenn.cis455.xpathengine.XPathNodeType, boolean)
	 */
	@Override
	public boolean isStepValid(String key, String value, int level, XPathNodeType type, boolean caseSensitiveMatch) {
		// if you have xpath [contains(text(), "substring")], this translates to this node
		// for a step further, you need to provide it a text that contains the key value of this node
		// hence key.contains(getKey())
		logger.info("Step check "
				+ " key:" + key.contains(getKey())
				+ " level:" + (getLevel() == level)
				+ " type:" + (type == XPathNodeType.CONTAINSTEXT || type == XPathNodeType.TEXT)
				+ " caseSensitiveMatch? " + caseSensitiveMatch
				);
		boolean keyMatch = true;
		if(caseSensitiveMatch) {
			keyMatch = keyMatch && key.contains(getKey());
			logger.info("Case sensitive match. keyMatch result:" + keyMatch);
		} else {
			keyMatch = keyMatch && getKey().toLowerCase().equalsIgnoreCase(key.toLowerCase());
			logger.info("Case insensitive match. keyMatch result:" + keyMatch);
		}
		if((type == XPathNodeType.CONTAINSTEXT || type == XPathNodeType.TEXT) 
				&& keyMatch && getLevel() == level) {
			return true;
		} else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathNode#clone()
	 */
	public XPathNode clone() {
		XPathNode node = new XPathContainsTextNode(getKey(), getLevel());
		node.setFinal(isFinal());
		node.setValid(isValid());
		node.setPrevNode(null);
		node.setPrevNode(null);
		return node;
	}

}
