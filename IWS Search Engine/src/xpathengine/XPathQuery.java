/*
 * Written by Shreejit Gangadharan
 */
package edu.upenn.cis455.xpathengine;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class XPathQuery.
 */
public class XPathQuery {
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass());
	
	/** The curr node. */
	private XPathNode head, currNode;
	
	/** The is valid. */
	private boolean isValid;
	
	/** The type. */
	private XPathQueryType type;
	
	/** The size. */
	private int size;

	/**
	 * Instantiates a new x path query.
	 */
	public XPathQuery() {
		XPathNode node = new XPathElementNode("", 0);
		logger.debug("Adding node:" + node);
		head = node;
		currNode = head;
		node.setValid(true);
		size = 1;
		type = XPathQueryType.PRIMARY;
		node.setFinal(true);
	}

	/**
	 * Instantiates a new x path query.
	 *
	 * @param node the node
	 */
	public XPathQuery(XPathNode node) {
		logger.debug("Adding node:" + node);
		head = node;
		currNode = head;
		node.setValid(true);
		size = 1;
		type = XPathQueryType.PRIMARY;
		node.setFinal(true);
	}

	/**
	 * Gets the head.
	 *
	 * @return the head
	 */
	public XPathNode getHead() {
		return head;
	}

	/**
	 * Gets the curr node.
	 *
	 * @return the curr node
	 */
	public XPathNode getCurrNode() {
		return currNode;
	}

	/**
	 * Checks if is valid.
	 *
	 * @return true, if is valid
	 */
	// TODO probably should get rid of this
	public boolean isValid() {
		return validate();
	}

	/**
	 * Adds the node.
	 *
	 * @param node the node
	 */
	public void addNode(XPathNode node) {
		logger.debug("Adding node:" + node);
		size++;
		XPathNode pnode = head;
		while (pnode.getNextNode() != null) {
			pnode = pnode.getNextNode();
		}

		pnode.setNextNode(node);
		pnode.setFinal(false);
		node.setPrevNode(pnode);
		node.setFinal(true);
		logger.debug("Query now:" + toString());
		return;
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the size.
	 *
	 * @param size the new size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	// TODO probably should make an illegal state machine exception
	/**
	 * Move forward to.
	 *
	 * @param key the key
	 * @param value the value
	 * @param level the level
	 * @param type            (type of XPathNodeType)
	 * @param caseSensitiveMatch the case sensitive match
	 * @return null
	 */
	public void moveForwardTo(String key, String value, int level,
			XPathNodeType type, boolean caseSensitiveMatch) {
		if (currNode == null || head == null) {
			logger.error("Incorrectly initialized state machine for xpath query!");
			return;
		}
		if (!currNode.isFinal()) {
			XPathNode nextExpectedNode = currNode.getNextNode();
			logger.debug("Trying to move to node:" + nextExpectedNode.getKey() + ". Validating with:"
					+ "key:" + key + " value:" + value + " level:" + level + " type:" + type);
			if (nextExpectedNode.isStepValid(key, value, level, type, caseSensitiveMatch)) {
				logger.debug("Checking successful. Moving to:"
						+ nextExpectedNode);
				currNode = nextExpectedNode;
				currNode.setValid(true);
			}
			logger.info("Query now:" + toString());
		}
	}

	/**
	 * Move backward to.
	 *
	 * @param level the level
	 * @throws IllegalStateException the illegal state exception
	 */
	public void moveBackwardTo(int level) throws IllegalStateException {
		if (currNode == null || head == null) {
			logger.error("Incorrectly initialized state machine for xpath query!");
			return;
		}
		
		if(level < 0 || currNode == head) {
			logger.debug("Attempt to move back beyond root node!");
			throw new IllegalStateException("Attempt to move back beyond root node!");
		}

//		logger.info("Moving from currNode:" + currNode + " to prev:"
//				+ currNode.getPrevNode() + " with given level:" + level + " if not final");
		// once the final node is reached, you don't go back
		if (!validate() && currNode.getPrevNode().getLevel() == level) {
			// otherwise invalidate the child of this node
			XPathNode child = currNode.getNextNode();
			if(child != null) {
				child.invalidate();
			}
//			currNode.invalidate();
			currNode = currNode.getPrevNode();
		}
		logger.debug("After trying to move backward, query now:" + toString());
		// TODO invalidate current node's children
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		XPathNode node = head;
		while (node != null) {
			if (type == XPathQueryType.SECONDARY)
				sb.append("\t\t");
//			logger.info("Query calling toString on " + node.getKey() + type);
			if(node == currNode) 
				sb.append(" ->");
			sb.append(node.toString());
			sb.append("\n");
			node = node.getNextNode();
		}
		return sb.toString();
	}

	/**
	 * Invalidate.
	 */
	public void invalidate() {
		XPathNode node = currNode;
		while (node != null) {
			node.invalidate();
			node = node.getNextNode();
		}
	}

	/**
	 * Validate.
	 *
	 * @return true, if successful
	 */
	public boolean validate() {
		boolean result = true;
		XPathNode node = head;
		while (node != null) {
			result = result && node.validate();
			node = node.getNextNode();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public XPathQuery clone() {
		if (head == null) {
			return null;
		}
		XPathNode headClone = head.clone();
		XPathQuery query = new XPathQuery(headClone);
		query.type = XPathQueryType.SECONDARY;
		XPathNode node = head.getNextNode();
		while (node != null) {
			XPathNode cloneNode = node.clone();
			query.addNode(cloneNode);
			node = node.getNextNode();
		}
		return query;
	}

	/**
	 * Equals.
	 *
	 * @param query2 the query2
	 * @return true, if successful
	 */
	public boolean equals(XPathQuery query2) {
		boolean result = true;
		XPathNode n1 = getHead(), n2 = query2.getHead();
		while (n1 != null && n2 != null) {
			logger.info("Query comparing n1:" + n1 + " and n2:" + n2);
			result = result && n1.equals(n2);
			if (n1.getDeps().size() == n2.getDeps().size()) {
				for (int i = 0; i < n1.getDeps().size(); i++) {
					result = result
							&& n1.getDeps().get(i).equals(n2.getDeps().get(i));
				}
				n1 = n1.getNextNode();
				n2 = n2.getNextNode();
			} else {
				return false;
			}
		}

		if (n1 == null && n2 == null) {
		} else {
			return false;
		}
		return result;
	}
	
	/**
	 * Struct equals.
	 *
	 * @param query2 the query2
	 * @return true, if successful
	 */
	public boolean structEquals(XPathQuery query2) {
		boolean result = true;
		XPathNode n1 = getHead(), n2 = query2.getHead();
		while (n1 != null && n2 != null) {
//			logger.info("Query comparing n1:" + n1 + " and n2:" + n2);
			result = result && n1.structEquals(n2);
			n1 = n1.getNextNode();
			n2 = n2.getNextNode();
		}

		if (n1 == null && n2 == null) {
		} else {
			return false;
		}
		return result;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public XPathQueryType getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(XPathQueryType type) {
		this.type = type;
	}
}
