/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.geometry.simplify;

import org.dyn4j.geometry.AABB;

/**
 * A generic {@link SegmentTree} node representing an AABB
 * that encompasses all {@link SegmentTreeNode}s under this
 * node.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.2.0
 */
class SegmentTreeNode {
	/** The left child */
	SegmentTreeNode left;
	
	/** The right child */
	SegmentTreeNode right;
	
	/** The parent node */
	SegmentTreeNode parent;
	
	/** The height of this subtree */
	int height;
	
	/** The aabb containing all children */
	final AABB aabb;
	
	/**
	 * Default constructor.
	 */
	public SegmentTreeNode() {
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
