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
 * Implementation of a point {@link Feature} of a {@link Shape}.
 * <p>
 * A {@link PointFeature} can represent either a vertex or an arbitrary point
 * on a {@link Shape}'s edge.
 * <p>
 * The index of a {@link PointFeature} is the index of the vertex in the {@link Shape}
 * or {@link Feature#NOT_INDEXED} if its an arbitrary point.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public final class PointFeature extends Feature {
	/** The vertex or point */
	final Vector2 point;
	
	/**
	 * Optional constructor.
	 * <p>
	 * Assumes the given point is not indexed.
	 * @param point the vertex point
	 */
	public PointFeature(Vector2 point) {
		this(point, Feature.NOT_INDEXED);
	}
	
	/**
	 * Full constructor.
	 * @param point the vertex point
	 * @param index the index 
	 */
	public PointFeature(Vector2 point, int index) {
		super(index);
		this.point = point;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PointFeature[Point=").append(this.point)
		.append("|Index=").append(this.index)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the point.
	 * @return {@link Vector2}
	 */
	public Vector2 getPoint() {
		return this.point;
	}
}
