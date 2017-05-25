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
package org.dyn4j.geometry;

/**
 * Implementation of an edge {@link Feature} of a {@link Shape}.
 * <p>
 * An {@link EdgeFeature} represents a <strong>linear</strong> edge of a {@link Shape} connecting
 * two vertices.  It's not the intent of this class to represent curved edges.
 * <p>
 * The index is the index of the edge in the {@link Shape}.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public final class EdgeFeature extends Feature {
	/** The first vertex of the edge */
	final PointFeature vertex1;
	
	/** The second vertex of the edge */
	final PointFeature vertex2;
	
	/** The vertex of maximum projection along a {@link Vector2} */
	final PointFeature max;

	/** The edge vector */
	final Vector2 edge;
	
	/**
	 * Creates an edge feature.
	 * @param vertex1 the first vertex of the edge
	 * @param vertex2 the second vertex of the edge
	 * @param max the maximum point
	 * @param edge the vector representing the edge
	 * @param index the index of the edge
	 */
	public EdgeFeature(PointFeature vertex1, PointFeature vertex2, PointFeature max, Vector2 edge, int index) {
		super(index);
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.edge = edge;
		this.max = max;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EdgeFeature[Vertex1=").append(this.vertex1)
		.append("|Vertex2=").append(this.vertex2)
		.append("|Edge=").append(this.edge)
		.append("|Max=").append(this.max)
		.append("|Index=").append(this.index)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the first vertex of the edge.
	 * @return {@link PointFeature}
	 */
	public PointFeature getVertex1() {
		return this.vertex1;
	}
	
	/**
	 * Returns the second vertex of the edge.
	 * @return {@link PointFeature}
	 */
	public PointFeature getVertex2() {
		return this.vertex2;
	}
	
	/**
	 * Returns the vector representing this edge in
	 * counter-clockwise winding.
	 * @return {@link Vector2}
	 */
	public Vector2 getEdge() {
		return this.edge;
	}
	
	/**
	 * Returns the maximum point.
	 * @return {@link PointFeature}
	 */
	public PointFeature getMaximum() {
		return this.max;
	}
}
