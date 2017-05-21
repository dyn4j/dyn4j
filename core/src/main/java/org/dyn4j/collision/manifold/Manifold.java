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
package org.dyn4j.collision.manifold;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Vector2;

/**
 * Represents a contact {@link Manifold} for a collision between two {@link Convex} {@link Shape}s.
 * <p>
 * A {@link Manifold} has a list of {@link ManifoldPoint}s for a given {@link Penetration} normal. In
 * two dimensions there will only be 1 or 2 contact points.
 * <p>
 * All {@link ManifoldPoint}s are in world space coordinates.
 * @author William Bittle
 * @version 3.0.2
 * @since 1.0.0
 */
public class Manifold {
	/** The {@link ManifoldPoint} in world space */
	protected List<ManifoldPoint> points;
	
	/** The penetration normal */
	protected Vector2 normal;
	
	/**
	 * Default constructor.
	 */
	public Manifold() {
		this.points = new ArrayList<ManifoldPoint>(2);
	}
	
	/**
	 * Full constructor.
	 * @param points the manifold points
	 * @param normal the manifold normal
	 */
	public Manifold(List<ManifoldPoint> points, Vector2 normal) {
		this.points = points;
		this.normal = normal;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Manifold[Normal=").append(this.normal);
		sb.append("|Points={");
		int size = this.points.size();
		for (int i = 0; i < size; i++) {
			if (i != 0) sb.append(",");
			sb.append(this.points.get(i));
		}
		sb.append("}]");
		return sb.toString();
	}
	
	/**
	 * Clears the {@link Manifold} information.
	 */
	public void clear() {
		this.points.clear();
		this.normal = null;
	}
	
	/**
	 * Returns the list of manifold points.
	 * @return List&lt;{@link ManifoldPoint}&gt;
	 */
	public List<ManifoldPoint> getPoints() {
		return this.points;
	}
	
	/**
	 * Returns the normal.
	 * @return {@link Vector2}
	 */
	public Vector2 getNormal() {
		return this.normal;
	}
	
	/**
	 * Sets the point list of this {@link Manifold}.
	 * @param points the point list
	 */
	public void setPoints(List<ManifoldPoint> points) {
		this.points = points;
	}
	
	/**
	 * Sets the manifold normal.
	 * <p>
	 * Must be normalized.
	 * @param normal the manifold normal
	 */
	public void setNormal(Vector2 normal) {
		this.normal = normal;
	}
}
