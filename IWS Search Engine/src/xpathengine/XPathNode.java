/*
 * Written by Shreejit Gangadharan
 */
package edu.upenn.cis455.xpathengine;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class XPathNode.
 */
public abstract class XPathNode {
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass());
	
	/** The type. */
	private XPathNodeType type;
	
	/** The level. */
	private int level;
	
	/** The key. */
	private String key;
	
	/** The value. */
	private String value = "";

	/** The is valid. */
	private boolean isValid;
	
	/** The is final. */
	private boolean isFinal;
	
	/** The next node. */
	private XPathNode prevNode = null, nextNode = null;
	
	/** The deps. */
	private List<XPathQuery> deps;

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Instantiates a new x path node.
	 *
	 * @param key the key
	 * @param level the level
	 * @param type the type
	 */
	public XPathNode(String key, int level, XPathNodeType type) {
		this.key = key;
		this.level = level;
		this.type = type;
		this.isValid = false;
		this.prevNode = null;
		this.nextNode = null;
		deps = new ArrayList<XPathQuery>();
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key.
	 *
	 * @param key the new key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Checks if is valid.
	 *
	 * @return true, if is valid
	 */
	public boolean isValid() {
		return isValid;
	}

	/**
	 * Sets the valid.
	 *
	 * @param isValid the new valid
	 */
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	/**
	 * Checks if is final.
	 *
	 * @return true, if is final
	 */
	public boolean isFinal() {
		return isFinal;
	}

	/**
	 * Sets the final.
	 *
	 * @param isFinal the new final
	 */
	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	/**
	 * Gets the prev node.
	 *
	 * @return the prev node
	 */
	public XPathNode getPrevNode() {
		return prevNode;
	}

	/**
	 * Sets the prev node.
	 *
	 * @param prevNode the new prev node
	 */
	public void setPrevNode(XPathNode prevNode) {
		this.prevNode = prevNode;
	}

	/**
	 * Gets the next node.
	 *
	 * @return the next node
	 */
	public XPathNode getNextNode() {
		return nextNode;
	}

	/**
	 * Sets the next node.
	 *
	 * @param nextNode the new next node
	 */
	public void setNextNode(XPathNode nextNode) {
		this.nextNode = nextNode;
	}

	/**
	 * Gets the deps.
	 *
	 * @return the deps
	 */
	public List<XPathQuery> getDeps() {
		return deps;
	}

	/**
	 * Sets the deps.
	 *
	 * @param deps the new deps
	 */
	public void setDeps(List<XPathQuery> deps) {
		this.deps = deps;
	}

	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Sets the level.
	 *
	 * @param level the new level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public XPathNodeType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(XPathNodeType type) {
		this.type = type;
	}

	/**
	 * Adds the dep.
	 *
	 * @param query the query
	 */
	public void addDep(XPathQuery query) {
		deps.add(query);
		query.setType(XPathQueryType.SECONDARY);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
//		logger.info("toString on " + getKey());
		return "code:" + hashCode() + " " + level + " " + type + " " + key + "=" + value + " " + " ["
				+ (isValid ? "X" : "") + "] [" + (isFinal ? "X" : "") + "] " 
//				+ "("  ")"
				+ "-> " + deps;
	}

	/**
	 * Invalidate.
	 */
	public void invalidate() {
		/*
		 * for (XPathQuery query : deps) { query.invalidate(); }
		 */
		isValid = false;
	}

	/**
	 * Validate.
	 *
	 * @return true, if successful
	 */
	public boolean validate() {
		boolean result = true;
		result = result && isValid;
		for (XPathQuery query : deps) {
			result = result && query.validate();
		}
		return result;
	}

	/**
	 * Checks if is step valid.
	 *
	 * @param name the name
	 * @param value the value
	 * @param level the level
	 * @param type the type
	 * @param caseSensitiveMatch the case sensitive match
	 * @return true, if is step valid
	 */
	public abstract boolean isStepValid(String name, String value, int level,
			XPathNodeType type, boolean caseSensitiveMatch);

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public abstract XPathNode clone();

	/**
	 * Equals.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	public boolean equals(XPathNode node) {
		boolean result = true;
		result = result && structEquals(node);
		if (getDeps().size() == node.getDeps().size()) {
			for (int i = 0; i < getDeps().size(); i++) {
				result = result
						&& getDeps().get(i).equals(node.getDeps().get(i));
				logger.info("After checking dep:" + getDeps().get(i)
						+ " result:" + result);
			}
		} else {
			logger.info("Dependencies not of same size n1:" + getDeps().size() 
					+ " n2:" + node.getDeps().size() + ". Nodes are not equal!");
			return false;
		}
		return result;
	}
	
	/**
	 * Struct equals.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	public boolean structEquals(XPathNode node) {
		boolean result = true;
		result = result
				&& (getKey().equals(node.getKey())
						&& getValue().equals(node.getValue())
						&& getLevel() == node.getLevel()
						&& getType() == node.getType()
						&& isFinal() == node.isFinal() 
						&& isValid() == node.isValid()
						);
		logger.info("Key check:" + (getKey().equals(node.getKey())) + " val check:"
				+ (getValue().equals(node.getValue())) + " level check:"
				+ (getLevel() == node.getLevel()) + " type check:"
				+ (getType() == node.getType()) + " final check:"
				+ (isFinal() == node.isFinal()) + " valid check:"
				+ (isValid() == node.isValid()));
		logger.info("After node content of " + getKey() + " and "
				+ node.getKey() + ", result:" + result);
		return result;
	}

}
