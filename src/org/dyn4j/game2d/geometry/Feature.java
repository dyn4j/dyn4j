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
 * Represents a feature of a {@link Shape}. In two dimensions there are 
 * two types of features: vertex and edge.
 * <p>
 * This class does not handle curved edges.
 * @author William Bittle
 */
public abstract class Feature {
	/**
	 * Enumeration of {@link Feature} types.
	 * @author William Bittle
	 * @version $Revision: 440 $
	 */
	public static enum Type {
		/** A vertex feature */
		VERTEX,
		/** An edge feature */
		EDGE
	}
	
	/** The {@link Feature.Type} */
	protected Feature.Type type;
	
	/**
	 * Full constructor.
	 * @param type the feature type
	 */
	public Feature(Feature.Type type) {
		this.type = type;
	}
	
	/**
	 * Returns true if this {@link Feature} is an edge.
	 * @return boolean
	 */
	public boolean isEdge() {
		return this.type == Feature.Type.EDGE;
	}
	
	/**
	 * Returns true if this {@link Feature} is a vertex.
	 * @return boolean
	 */
	public boolean isVertex() {
		return this.type == Feature.Type.VERTEX;
	}
	
	/**
	 * Represents a straight edge comprised of
	 * two points.
	 * @author William Bittle
	 */
	public static class Edge extends Feature {
		/** The vertices making the edge */
		protected Vector[] vertices;
		
		/** The vertex of maximum projection along a {@link Vector} */
		protected Vector max;
		
		/** The index of the edge on the shape */
		protected int index;
		
		/**
		 * Creates an edge feature.
		 * <p>
		 * Assumes the index is zero.
		 * @param vertices the vertices making the edge
		 * @param max the maximum point
		 */
		public Edge(Vector[] vertices, Vector max) {
			this(vertices, max, 0);
		}
		
		/**
		 * Creates an edge feature.
		 * @param vertices the vertices making the edge
		 * @param max the maximum point
		 * @param index the index of the edge
		 */
		public Edge(Vector[] vertices, Vector max, int index) {
			super(Feature.Type.EDGE);
			this.vertices = vertices;
			this.max = max;
			this.index = index;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("FEATURE").append("[");
			sb.append(this.type).append("|");
			sb.append("{").append(this.vertices[0]).append(this.vertices[1]).append("}|");
			sb.append(this.max).append("]");
			return sb.toString();
		}
		
		/**
		 * Returns the edge vertices.
		 * @return {@link Vector}[]
		 */
		public Vector[] getVertices() {
			return this.vertices;
		}
		
		/**
		 * Returns the maximum point.
		 * @return {@link Vector}
		 */
		public Vector getMaximum() {
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
	
	/**
	 * Represents a vertex feature.
	 * @author William Bittle
	 */
	public static class Vertex extends Feature {
		/** The vertex or point */
		protected Vector point;
		
		/**
		 * Full constructor.
		 * @param point the vertex or point
		 */
		public Vertex(Vector point) {
			super(Feature.Type.VERTEX);
			this.point = point;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("FEATURE").append("[");
			sb.append(this.type).append("|");
			sb.append(this.point).append("]");
			return sb.toString();
		}
		
		/**
		 * Returns the point.
		 * @return {@link Vector}
		 */
		public Vector getPoint() {
			return this.point;
		}
	}
}
