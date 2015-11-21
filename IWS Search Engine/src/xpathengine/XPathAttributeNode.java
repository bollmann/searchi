/*
 * Written by Shreejit Gangadharan
 */
package edu.upenn.cis455.xpathengine;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class XPathAttributeNode.
 */
public class XPathAttributeNode extends XPathNode {
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass());
	
	/**
	 * Instantiates a new x path attribute node.
	 *
	 * @param name the name
	 * @param value the value
	 * @param level the level
	 */
	public XPathAttributeNode(String name, String value, int level) {
		super(name, level, XPathNodeType.ATTRIBUTE);
		setValue(value);
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathNode#isStepValid(java.lang.String, java.lang.String, int, edu.upenn.cis455.xpathengine.XPathNodeType, boolean)
	 */
	@Override
	public boolean isStepValid(String key, String value, int level, XPathNodeType type, boolean caseSensitiveMatch) {
		logger.info("Step check "
				+ " key:" + key.equals(getKey())
				+ " value:" + getValue().equals(value)
				+ " level:" + (getLevel() == level)
				+ " type:" + (type == XPathNodeType.ATTRIBUTE)
				+ " caseSensitiveMatch? " + caseSensitiveMatch
				);
		boolean keyMatch = true;
		if(caseSensitiveMatch) {
			keyMatch = keyMatch && getKey().equals(key);
			keyMatch = keyMatch && getValue().equals(value);
			logger.info("Case sensitive match. keyMatch result:" + keyMatch);
		} else {
			keyMatch = keyMatch && getKey().equalsIgnoreCase(key);
			keyMatch = keyMatch && getValue().equals(value);
			logger.info("Case insensitive match. keyMatch result:" + keyMatch);
		}
		if (type == XPathNodeType.ATTRIBUTE
				 && getLevel() == level && keyMatch) {
			return true;
		} else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.upenn.cis455.xpathengine.XPathNode#clone()
	 */
	@Override
	public XPathNode clone() {
		XPathNode node = new XPathAttributeNode(getKey(), getValue(), getLevel());
		node.setFinal(isFinal());
		node.setValid(isValid());
//		node.setDeps(getDeps());
		return node;
	}

}
