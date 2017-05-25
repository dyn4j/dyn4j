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

import org.dyn4j.resources.Messages;

/**
 * Implementation of an Axis-Align Bounding Box.
 * <p>
 * An {@link AABB} has minimum and maximum coordinates that define the box.
 * <p>
 * An {@link AABB} can be unioned or intersected with other {@link AABB}s to combine
 * them into another {@link AABB}.  If an intersection produces no result, a degenerate {@link AABB}
 * is returned.  A degenerate {@link AABB} can be tested by the {@link #isDegenerate()} methods and 
 * is defined as an {@link AABB} who's maximum and minimum are equal.
 * <p>
 * {@link AABB}s can also be tested for overlap and (full) containment using the {@link #overlaps(AABB)} 
 * and {@link #contains(AABB)} method.
 * <p>
 * The {@link #expand(double)} method can be used to expand the bounds of the {@link AABB} by some amount.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.0.0
 */
public class AABB implements Translatable {
	/** The minimum extent */
	protected final Vector2 min;
	
	/** The maximum extent */
	protected final Vector2 max;
	
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
	 * @throws IllegalArgumentException if either coordinate of the given min is greater than the given max
	 */
	public AABB(Vector2 min, Vector2 max) {
		// check the min and max
		if (min.x > max.x || min.y > max.y) throw new IllegalArgumentException(Messages.getString("geometry.aabb.invalidMinMax"));
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Full constructor.
	 * @param radius the radius of a circle fitting inside an AABB
	 * @since 3.1.5
	 */
	public AABB(double radius) {
		this(null, radius);
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Creates an AABB for a circle with the given center and radius.
	 * @param center the center of the circle
	 * @param radius the radius of the circle
	 * @since 3.1.5
	 * @throws IllegalArgumentException if the given radius is less than zero
	 */
	public AABB(Vector2 center, double radius) {
		if (radius < 0) throw new IllegalArgumentException(Messages.getString("geometry.aabb.invalidRadius"));
		if (center == null) {
			this.min = new Vector2(-radius, -radius);
			this.max = new Vector2( radius,  radius);
		} else {
			this.min = new Vector2(center.x - radius, center.y - radius);
			this.max = new Vector2(center.x + radius, center.y + radius);
		}
	}
	
	/**
	 * Copy constructor.
	 * @param aabb the {@link AABB} to copy
	 * @since 3.1.1
	 */
	public AABB(AABB aabb) {
		this.min = aabb.min.copy();
		this.max = aabb.max.copy();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AABB[Min=").append(this.min)
		.append("|Max=").append(this.max)
		.append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Translatable#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		this.max.add(x, y);
		this.min.add(x, y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Translatable#translate(org.dyn4j.geometry.Vector2)
	 */
	public void translate(Vector2 translation) {
		this.max.add(translation);
		this.min.add(translation);
	}
	
	/**
	 * Returns a new AABB of this AABB translated by the
	 * given translation amount.
	 * @param translation the translation
	 * @return AABB
	 * @since 3.1.1
	 */
	public AABB getTranslated(Vector2 translation) {
		return new AABB(
				this.min.sum(translation),
				this.max.sum(translation));
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
	 * Performs the intersection of this {@link AABB} and the given {@link AABB} placing
	 * the result into this {@link AABB}.
	 * <p>
	 * If the given {@link AABB} does not overlap this {@link AABB}, this {@link AABB} is
	 * set to a zero {@link AABB}.
	 * @param aabb the {@link AABB} to intersect
	 * @since 3.1.1
	 */
	public void intersection(AABB aabb) {
		this.min.x = Math.max(this.min.x, aabb.min.x);
		this.min.y = Math.max(this.min.y, aabb.min.y);
		this.max.x = Math.min(this.max.x, aabb.max.x);
		this.max.y = Math.min(this.max.y, aabb.max.y);
		
		// check for a bad AABB
		if (this.min.x > this.max.x || this.min.y > this.max.y) {
			// the two AABBs were not overlapping
			// set this AABB to a degenerate one
			this.min.x = 0.0;
			this.min.y = 0.0;
			this.max.x = 0.0;
			this.max.y = 0.0;
		}
	}
	
	/**
	 * Performs the intersection of this {@link AABB} and the given {@link AABB} returning
	 * the result in a new {@link AABB}.
	 * <p>
	 * If the given {@link AABB} does not overlap this {@link AABB}, a zero {@link AABB} is
	 * returned.
	 * @param aabb the {@link AABB} to intersect
	 * @return {@link AABB}
	 * @since 3.1.1
	 */
	public AABB getIntersection(AABB aabb) {
		Vector2 min = new Vector2();
		Vector2 max = new Vector2();
		
		min.x = Math.max(this.min.x, aabb.min.x);
		min.y = Math.max(this.min.y, aabb.min.y);
		max.x = Math.min(this.max.x, aabb.max.x);
		max.y = Math.min(this.max.y, aabb.max.y);
		
		// check for a bad AABB
		if (min.x > max.x || min.y > max.y) {
			// the two AABBs were not overlapping
			// return a degenerate one
			return new AABB(new Vector2(), new Vector2());
		}
		return new AABB(min, max);
	}
	
	/**
	 * Expands this {@link AABB} by half the given expansion in each direction.
	 * <p>
	 * The expansion can be negative to shrink the {@link AABB}.  However, if the expansion is
	 * greater than the current width/height, the {@link AABB} can become invalid.  In this 
	 * case, the AABB will become a degenerate AABB at the mid point of the min and max for 
	 * the respective coordinates.
	 * @param expansion the expansion amount
	 */
	public void expand(double expansion) {
		double e = expansion * 0.5;
		this.min.x -= e;
		this.min.y -= e;
		this.max.x += e;
		this.max.y += e;
		// we only need to verify the new aabb if the expansion
		// was inwardly
		if (expansion < 0.0) {
			// if the aabb is invalid then set the min/max(es) to
			// the middle value of their current values
			if (this.min.x > this.max.x) {
				double mid = (this.min.x + this.max.x) * 0.5;
				this.min.x = mid;
				this.max.x = mid;
			}
			if (this.min.y > this.max.y) {
				double mid = (this.min.y + this.max.y) * 0.5;
				this.min.y = mid;
				this.max.y = mid;
			}
		}
	}
	
	/**
	 * Returns a new {@link AABB} of this AABB expanded by half the given expansion
	 * in both the x and y directions.
	 * <p>
	 * The expansion can be negative to shrink the {@link AABB}.  However, if the expansion is
	 * greater than the current width/height, the {@link AABB} can become invalid.  In this 
	 * case, the AABB will become a degenerate AABB at the mid point of the min and max for 
	 * the respective coordinates.
	 * @param expansion the expansion amount
	 * @return {@link AABB}
	 * @since 3.1.1
	 */
	public AABB getExpanded(double expansion) {
		double e = expansion * 0.5;
		double minx = this.min.x - e;
		double miny = this.min.y - e;
		double maxx = this.max.x + e;
		double maxy = this.max.y + e;
		// we only need to verify the new aabb if the expansion
		// was inwardly
		if (expansion < 0.0) {
			// if the aabb is invalid then set the min/max(es) to
			// the middle value of their current values
			if (minx > maxx) {
				double mid = (minx + maxx) * 0.5;
				minx = mid;
				maxx = mid;
			}
			if (miny > maxy) {
				double mid = (miny + maxy) * 0.5;
				miny = mid;
				maxy = mid;
			}
		}
		return new AABB(
				new Vector2(minx, miny), 
				new Vector2(maxx, maxy));
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
		}
		
		// check for overlap along the y-axis
		if (this.min.y > aabb.max.y || this.max.y < aabb.min.y) {
			// the aabbs do not overlap along the y-axis
			return false;
		}
		
		return true;
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
	 * Returns true if the given point is contained within this {@link AABB}.
	 * @param point the point to test
	 * @return boolean
	 * @since 3.1.1
	 */
	public boolean contains(Vector2 point) {
		return this.contains(point.x, point.y);
	}
	
	/**
	 * Returns true if the given point's coordinates are contained within this {@link AABB}.
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
	 * @return boolean
	 * @since 3.1.1
	 */
	public boolean contains(double x, double y) {
		if (this.min.x <= x && this.max.x >= x) {
			if (this.min.y <= y && this.max.y >= y) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if this {@link AABB} is degenerate.
	 * <p>
	 * A degenerate {@link AABB} is one where its min and max x or y
	 * coordinates are equal.
	 * @return boolean
	 * @since 3.1.1
	 */
	public boolean isDegenerate() {
		return this.min.x == this.max.x || this.min.y == this.max.y;
	}
	
	/**
	 * Returns true if this {@link AABB} is degenerate given
	 * the specified error.
	 * <p>
	 * An {@link AABB} is degenerate given some error if
	 * max - min &lt;= error for either the x or y coordinate.
	 * @param error the allowed error
	 * @return boolean
	 * @since 3.1.1
	 * @see #isDegenerate()
	 */
	public boolean isDegenerate(double error) {
		return Math.abs(this.max.x - this.min.x) <= error || Math.abs(this.max.y - this.min.y) <= error;
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
