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
import org.dyn4j.geometry.Vector2;

/**
 * Leaf node for the {@link SegmentTree} representing a line segment.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.2.0
 */
final class SegmentTreeLeaf extends SegmentTreeNode {
	/** The first segment point */
	final Vector2 point1;
	
	/** The second segment point */
	final Vector2 point2;

	/** The first segment point index */
	final int index1;
	
	/** The second segment point index */
	final int index2;
	
	/**
	 * Minimal constructor.
	 * @param point1 the first segment point
	 * @param point2 the second segment point
	 * @param index1 the first segment point index
	 * @param index2 the second segment point index
	 */
	public SegmentTreeLeaf(Vector2 point1, Vector2 point2, int index1, int index2) {
		AABB.setFromPoints(point1, point2, this.aabb);
		this.point1 = point1;
		this.point2 = point2;
		this.index1 = index1;
		this.index2 = index2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RTreeLeaf[P1=").append(this.point1)
		  .append("|P2=").append(this.point2)
		  .append("|AABB=").append(this.aabb.toString())
		  .append("|Height=").append(this.height)
		  .append("]");
		return sb.toString();
	}
}