/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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
 * Represents an axis aligned bounding box.
 * @author William Bittle
 * @version 3.0.1
 * @since 3.0.0
 */
public class AABB {
	/** The minimum extent */
	protected Vector2 min;
	
	/** The maximum extent */
	protected Vector2 max;
	
	/**
	 * Full constructor.
	 * @param minX the minimum x extent
	 * @param minY the minimum y extent
	 * @param maxX the maximum x extent
	 * @param maxY the maximum y extent
	 */
	public AABB(double minX, double minY, double maxX, double maxY) {
		this(new Vector2(minX, minY), new Vector2(maxX, maxY));
	}
	
	/**
	 * Full constructor.
	 * @param min the minimum extent
	 * @param max the maximum extent
	 */
	public AABB(Vector2 min, Vector2 max) {
		// check the min and max
		if (min.x > max.x || min.y > max.y) throw new IllegalArgumentException("The min cannot be greater than the max.");
		this.min = min;
		this.max = max;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AABB[(" + this.min.x + ", " + this.min.y + ")|(" + this.max.x + ", " + this.max.y + ")]";
	}
	
	/**
	 * Returns the width of this {@link AABB}.
	 * @return double
	 * @since 3.0.1
	 */
	public double getWidth() {
		return this.max.x - this.min.x;
	}
	
	/**
	 * Returns the height of this {@link AABB}.
	 * @return double
	 * @since 3.0.1
	 */
	public double getHeight() {
		return this.max.y - this.min.y;
	}
	
	/**
	 * Returns the perimeter of this {@link AABB}.
	 * @return double
	 */
	public double getPerimeter() {
		return 2 * (this.max.x - this.min.x + this.max.y - this.min.y);
	}
	
	/**
	 * Returns the area of this {@link AABB};.
	 * @return double
	 */
	public double getArea() {
		return (this.max.x - this.min.x) * (this.max.y - this.min.y);
	}
	
	/**
	 * Performs a union of this {@link AABB} and the given {@link AABB} placing
	 * the result of the union into this {@link AABB}.
	 * @param aabb the {@link AABB} to union
	 */
	public void union(AABB aabb) {
		this.min.x = Math.min(this.min.x, aabb.min.x);
		this.min.y = Math.min(this.min.y, aabb.min.y);
		this.max.x = Math.max(this.max.x, aabb.max.x);
		this.max.y = Math.max(this.max.y, aabb.max.y);
	}
	
	/**
	 * Performs a union of this {@link AABB} and the given {@link AABB} returning
	 * a new {@link AABB} containing the result.
	 * @param aabb the {@link AABB} to union
	 * @return {@link AABB} the resulting union
	 */
	public AABB getUnion(AABB aabb) {
		Vector2 min = new Vector2();
		Vector2 max = new Vector2();
		
		min.x = Math.min(this.min.x, aabb.min.x);
		min.y = Math.min(this.min.y, aabb.min.y);
		max.x = Math.max(this.max.x, aabb.max.x);
		max.y = Math.max(this.max.y, aabb.max.y);
		
		return new AABB(min, max);
	}
	
	/**
	 * Expands this {@link AABB} by half the given expansion in each direction.
	 * @param expansion the expansion amount
	 */
	public void expand(double expansion) {
		double e = expansion * 0.5;
		this.min.x -= e;
		this.min.y -= e;
		this.max.x += e;
		this.max.y += e;
	}
	
	/**
	 * Returns true if the given {@link AABB} and this {@link AABB} overlap.
	 * @param aabb the {@link AABB} to test
	 * @return boolean true if the {@link AABB}s overlap
	 */
	public boolean overlaps(AABB aabb) {
		// check for overlap along the x-axis
		if (this.min.x > aabb.max.x || this.max.x < aabb.min.x) {
			// the aabbs do not overlap along the x-axis
			return false;
		} else {
			// check for overlap along the y-axis
			if (this.min.y > aabb.max.y || this.max.y < aabb.min.y) {
				// the aabbs do not overlap along the y-axis
				return false;
			} else {
				return true;
			}
		}
	}
	
	/**
	 * Returns true if the given {@link AABB} is contained within this {@link AABB}.
	 * @param aabb the {@link AABB} to test
	 * @return boolean
	 */
	public boolean contains(AABB aabb) {
		if (this.min.x <= aabb.min.x && this.max.x >= aabb.max.x) {
			if (this.min.y <= aabb.min.y && this.max.y >= aabb.max.y) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the minimum x extent.
	 * @return double
	 */
	public double getMinX() {
		return this.min.x;
	}
	
	/**
	 * Returns the maximum x extent.
	 * @return double
	 */
	public double getMaxX() {
		return this.max.x;
	}
	
	/**
	 * Returns the maximum y extent.
	 * @return double
	 */
	public double getMaxY() {
		return this.max.y;
	}
	
	/**
	 * Returns the minimum y extent.
	 * @return double
	 */
	public double getMinY() {
		return this.min.y;
	}
}
