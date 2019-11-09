/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.collision.broadphase;

import org.dyn4j.geometry.AABB;

/**
 * Represents a basic node in a {@link LazyAABBTree}.
 * <p>
 * The AABB of the node should be the union of all the AABBs below this node.
 * @author Manolis Tsamis
 * @version 3.4.0
 * @since 3.4.0
 */
class LazyAABBTreeNode {
	/** The left child */
	LazyAABBTreeNode left;
	
	/** The right child */
	LazyAABBTreeNode right;
	
	/** The parent node */
	LazyAABBTreeNode parent;
	
	/** The height of this subtree */
	int height;
	
	/** The aabb containing all children */
	public AABB aabb;
	
	/**
	 * Replace oldChild with newChild. oldChild must be a child of this node before the replacement.
	 * Children are compared with the equality operator.
	 * 
	 * @param oldChild The child to replace in this node
	 * @param newChild The replacement
	 * @throws IllegalArgumentException if oldChild is not a child of this node
	 */
	public void replaceChild(LazyAABBTreeNode oldChild, LazyAABBTreeNode newChild) {
		if (left == oldChild) {
			left = newChild;
		} else if (right == oldChild) {
			right = newChild;
		} else {
			throw new IllegalArgumentException(oldChild.toString() + " is not a child of node " + this.toString());
		}
	}
	
	/**
	 * Returns the sibling of this node, that is the other child of this node's parent.
	 * 
	 * @return The sibling node
	 * @throws NullPointerException if this node has no parent
	 * @throws IllegalStateException if this node is not a child of it's parent
	 */
	public LazyAABBTreeNode getSibling() {
		if (parent.left == this) {
			return parent.right;
		} else if (parent.right == this) {
			return parent.left;
		} else {
			throw new IllegalStateException("Invalid parent pointer for node " + this.toString());
		}
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
		sb.append("LazyAABBTreeNode[AABB=").append(this.aabb.toString())
		  .append("|Height=").append(this.height)
		  .append("]");
		return sb.toString();
	}
}