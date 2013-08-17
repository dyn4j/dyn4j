/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
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
 * Represents an object that is defined by vertices, and has counter-clockwise winding.
 * @author William Bittle
 * @version 3.0.2
 * @since 1.0.0
 */
public abstract class Wound extends AbstractShape implements Shape, Transformable {
	/** The array of vertices */
	protected Vector2[] vertices;
	
	/** The edge normals */
	protected Vector2[] normals;
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shape#getRadius(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public double getRadius(Vector2 center) {
		// find the maximum radius from the center
		int size = this.vertices.length;
		double r2 = 0.0;
		for (int i = 0; i < size; i++) {
			double r2t = center.distanceSquared(this.vertices[i]);
			// keep the largest
			r2 = Math.max(r2, r2t);
		}
		// set the radius
		return Math.sqrt(r2);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("|Vertices{");
		for (int i = 0; i < this.vertices.length; i++) {
			if (i != 0) sb.append(",");
			sb.append(this.vertices[i]);
		}
		sb.append("}|Normals{");
		for (int i = 0; i < this.normals.length; i++) {
			if (i != 0) sb.append(",");
			sb.append(this.normals[i]);
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Returns the array of vertices in local coordinates.
	 * @return {@link Vector2}[]
	 */
	public Vector2[] getVertices() {
		return this.vertices;
	}
	
	/**
	 * Returns the array of edge normals in local coordinates.
	 * @return {@link Vector2}[]
	 */
	public Vector2[] getNormals() {
		return this.normals;
	}
}
