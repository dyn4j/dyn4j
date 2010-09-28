/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d.geometry;

/**
 * Represents an edge feature of a {@link Shape}.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public class Edge extends Feature {
	/** The first vertex of the edge */
	protected Vertex vertex1;
	
	/** The second vertex of the edge */
	protected Vertex vertex2;
	
	/** The edge vector */
	protected Vector2 edge;
	
	/** The vertex of maximum projection along a {@link Vector2} */
	protected Vertex max;
	
	/** The index of the edge on the shape */
	protected int index;
	
	/**
	 * Creates an edge feature.
	 * @param vertex1 the first vertex of the edge
	 * @param vertex2 the second vertex of the edge
	 * @param edge the vector representing the edge
	 * @param max the maximum point
	 * @param index the index of the edge
	 */
	public Edge(Vertex vertex1, Vertex vertex2, Vertex max, Vector2 edge, int index) {
		super(Feature.Type.EDGE);
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.edge = edge;
		this.max = max;
		this.index = index;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EDGE[")
		.append(this.vertex1).append("|")
		.append(this.vertex2).append("|")
		.append(this.edge).append("|")
		.append(this.max).append("|")
		.append(this.index).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the first vertex of the edge.
	 * @return {@link Vertex}
	 */
	public Vertex getVertex1() {
		return vertex1;
	}
	
	/**
	 * Returns the second vertex of the edge.
	 * @return {@link Vertex}
	 */
	public Vertex getVertex2() {
		return vertex2;
	}
	
	/**
	 * Returns the vector representing this edge.
	 * @return {@link Vector2}
	 */
	public Vector2 getEdge() {
		return this.edge;
	}
	
	/**
	 * Returns the maximum point.
	 * @return {@link Vertex}
	 */
	public Vertex getMaximum() {
		return this.max;
	}
	
	/**
	 * Returns the edge index.
	 * @return int
	 */
	public int getIndex() {
		return this.index;
	}
}
