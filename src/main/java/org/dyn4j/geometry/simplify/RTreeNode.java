package org.dyn4j.geometry.simplify;

import org.dyn4j.geometry.AABB;

class RTreeNode {
	/** The left child */
	RTreeNode left;
	
	/** The right child */
	RTreeNode right;
	
	/** The parent node */
	RTreeNode parent;
	
	/** The height of this subtree */
	int height;
	
	/** The aabb containing all children */
	final AABB aabb;
	
	/**
	 * Default constructor.
	 */
	public RTreeNode() {
		this.aabb = new AABB(0,0,0,0);
	}
	
	/**
	 * Returns true if this node is a leaf node.
	 * @return boolean true if this node is a leaf node
	 */
	public boolean isLeaf() {
		return this.left == null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RTreeNode[AABB=").append(this.aabb.toString())
		  .append("|Height=").append(this.height)
		  .append("]");
		return sb.toString();
	}
}
