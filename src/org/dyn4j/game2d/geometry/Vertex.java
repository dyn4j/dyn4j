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
 * Represents a point on the edge of a {@link Shape}.
 * @author William Bittle
 */
public class Vertex extends Feature {
	/** The vertex or point */
	protected Vector point;
	
	/** The index in the  */
	protected int index;
	
	/**
	 * Optional constructor.
	 * <p>
	 * Assumes the given point is not indexed.
	 * @param point the vertex point
	 */
	public Vertex(Vector point) {
		this(point, Feature.NOT_INDEXED);
	}
	
	/**
	 * Full constructor.
	 * @param point the vertex point
	 * @param index the index 
	 */
	public Vertex(Vector point, int index) {
		super(Feature.Type.VERTEX);
		this.point = point;
		this.index = index;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("VERTEX").append("[");
		sb.append(this.point).append("|");
		sb.append(this.index).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the point.
	 * @return {@link Vector}
	 */
	public Vector getPoint() {
		return this.point;
	}
	
	/**
	 * Returns the index of this vertex.
	 * @return int
	 */
	public int getIndex() {
		return this.index;
	}
}
