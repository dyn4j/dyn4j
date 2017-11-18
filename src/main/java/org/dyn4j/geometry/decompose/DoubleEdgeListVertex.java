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
package org.dyn4j.geometry.decompose;

import org.dyn4j.geometry.Vector2;

/**
 * Represents a vertex in the {@link DoubleEdgeList}.
 * @author William Bittle
 * @version 3.2.0
 * @since 2.2.0
 */
final class DoubleEdgeListVertex {
	/** The comparable data for this node */
	final Vector2 point;
	
	/** The the leaving edge */
	DoubleEdgeListHalfEdge leaving;
	
	/**
	 * Minimal constructor.
	 * @param point the vertex point
	 */
	public DoubleEdgeListVertex(Vector2 point) {
		this.point = point;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.point.toString();
	}

	/**
	 * Returns the edge from this node to the given node.
	 * @param node the node to find an edge to
	 * @return {@link DoubleEdgeListHalfEdge}
	 */
	public DoubleEdgeListHalfEdge getEdgeTo(DoubleEdgeListVertex node) {
		if (leaving != null) {
			if (leaving.twin.origin == node) {
				return leaving;
			} else {
				DoubleEdgeListHalfEdge edge = leaving.twin.next;
				while (edge != leaving) {
					if (edge.twin.origin == node) {
						return edge;
					} else {
						edge = edge.twin.next;
					}
				}
			}
		}
		return null;
	}
}
	
