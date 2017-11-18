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
package org.dyn4j.collision.narrowphase;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Vector2;

/**
 * Represents a {@link Penetration} of one {@link Convex} {@link Shape} into another.
 * <p>
 * The penetration normal should always be a normalized vector that points from the first
 * {@link Convex} {@link Shape} to the second.
 * @author William Bittle
 * @version 3.0.2
 * @since 1.0.0
 */
public class Penetration {
	/** The normalized axis of projection */
	protected Vector2 normal;
	
	/** The penetration amount on this axis */
	protected double depth;
	
	/**
	 * Default constructor.
	 */
	public Penetration() {}
	
	/**
	 * Full constructor.
	 * @param normal the penetration normal from {@link Convex}1 to {@link Convex}2
	 * @param depth the penetration depth
	 */
	public Penetration(Vector2 normal, double depth) {
		this.normal = normal;
		this.depth = depth;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Penetration[Normal=").append(this.normal)
		.append("|Depth=").append(this.depth)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Clears the penetration information.
	 */
	public void clear() {
		this.normal = null;
		this.depth = 0;
	}
	
	/**
	 * Returns the penetration normal.
	 * @return {@link Vector2}
	 */
	public Vector2 getNormal() {
		return this.normal;
	}
	
	/**
	 * Returns the penetration depth.
	 * @return double
	 */
	public double getDepth() {
		return this.depth;
	}
	
	/**
	 * Sets the penetration normal.
	 * <p>
	 * Must be normalized.
	 * @param normal the penetration normal
	 */
	public void setNormal(Vector2 normal) {
		this.normal = normal;
	}
	
	/**
	 * Sets the penetration depth.
	 * @param depth the penetration depth
	 */
	public void setDepth(double depth) {
		this.depth = depth;
	}
}
