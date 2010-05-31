/*
 * Copyright (c) 2010, William Bittle
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
 */
public class Edge extends Feature {
	/** The vertices making the edge */
	protected Vertex[] vertices;
	
	/** The edge vector */
	protected Vector edge;
	
	/** The vertex of maximum projection along a {@link Vector} */
	protected Vertex max;
	
	/** The index of the edge on the shape */
	protected int index;
	
	/**
	 * Creates an edge feature.
	 * @param vertices the vertices making the edge
	 * @param edge the vector representing the edge
	 * @param max the maximum point
	 * @param index the index of the edge
	 */
	public Edge(Vertex[] vertices, Vector edge, Vertex max, int index) {
		super(Feature.Type.EDGE);
		this.vertices = vertices;
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
		sb.append("EDGE[");
		sb.append("{").append(this.vertices[0]).append(this.vertices[1]).append("}|");
		sb.append(this.edge).append("|");
		sb.append(this.max).append("|");
		sb.append(this.index).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the edge vertices.
	 * @return {@link Vertex}[]
	 */
	public Vertex[] getVertices() {
		return this.vertices;
	}
	
	/**
	 * Returns the vector represeting this edge.
	 * @return {@link Vector}
	 */
	public Vector getEdge() {
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
