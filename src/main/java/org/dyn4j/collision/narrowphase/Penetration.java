/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.collision.narrowphase;

import org.dyn4j.Copyable;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Vector2;

/**
 * Represents a {@link Penetration} of one {@link Convex} {@link Shape} into another.
 * <p>
 * The penetration normal should always be a normalized vector that points from the first
 * {@link Convex} {@link Shape} to the second.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 */
public class Penetration implements Shiftable, Copyable<Penetration> {
	/** The normalized axis of projection */
	protected final Vector2 normal;
	
	/** The penetration amount on this axis */
	protected double depth;
	
	/**
	 * Default constructor.
	 */
	public Penetration() {
		this.normal = new Vector2();
	}
	
	/**
	 * Full constructor.
	 * @param normal the penetration normal from {@link Convex}1 to {@link Convex}2
	 * @param depth the penetration depth
	 */
	protected Penetration(Vector2 normal, double depth) {
		this.normal = normal.copy();
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
		this.normal.zero();
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
		this.normal.x = normal.x;
		this.normal.y = normal.y;
	}
	
	/**
	 * Sets the penetration depth.
	 * @param depth the penetration depth
	 */
	public void setDepth(double depth) {
		this.depth = depth;
	}
	
	/**
	 * Copies (deep) the given {@link Penetration} information to this {@link Penetration}.
	 * @param penetration the penetration to copy
	 * @since 4.0.0
	 */
	public void copy(Penetration penetration) {
		this.depth = penetration.depth;
		this.normal.x = penetration.normal.x;
		this.normal.y = penetration.normal.y;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		// no-op
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Copyable#copy()
	 */
	@Override
	public Penetration copy() {
		return new Penetration(this.normal, this.depth);
	}
}
