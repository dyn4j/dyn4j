/*
 * Copyright (c) 2010-2017 William Bittle  http://www.dyn4j.org/
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
 * @version 3.3.1
 * @since 3.0.0
 */
public class AABB implements Translatable {
	/** The minimum extent */
	protected double minX, minY;
	
	/** The maximum extent */
	protected double maxX, maxY;
	
	/**
	 * Method to create the valid AABB defined by the two points point1 and point2.
	 * 
	 * @param point1 the first point
	 * @param point2 the second point
	 * @return The one and only one valid AABB formed by point1 and point2
	 */
	public static AABB createAABBFromPoints(Vector2 point1, Vector2 point2) {
		return createAABBFromPoints(point1.x, point1.y, point2.x, point2.y);
	}
	
	/**
	 * Method to create the valid AABB defined by the two points A(point1x, point1y) and B(point2x, point2y).
	 * 
	 * @param point1x The x coordinate of point A
	 * @param point1y The y coordinate of point A
	 * @param point2x The x coordinate of point B
	 * @param point2y The y coordinate of point B
	 * @return The one and only one valid AABB formed by A and B
	 */
	public static AABB createAABBFromPoints(double point1x, double point1y, double point2x, double point2y) {
		if (point2x < point1x) {
			double temp = point1x;
			point1x = point2x;
			point2x = temp;
		}
		
		if (point2y < point1y) {
			double temp = point1y;
			point1y = point2y;
			point2y = temp;
		}
		
		return new AABB(point1x, point1y, point2x, point2y);
	}
	
	/**
	 * Full constructor.
	 * @param minX the minimum x extent
	 * @param minY the minimum y extent
	 * @param maxX the maximum x extent
	 * @param maxY the maximum y extent
	 */
	public AABB(double minX, double minY, double maxX, double maxY) {
		// check the min and max
		if (minX > maxX || minY > maxY) throw new IllegalArgumentException(Messages.getString("geometry.aabb.invalidMinMax"));
		
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	/**
	 * Full constructor.
	 * @param min the minimum extent
	 * @param max the maximum extent
	 * @throws IllegalArgumentException if either coordinate of the given min is greater than the given max
	 */
	public AABB(Vector2 min, Vector2 max) {
		this(min.x, min.y, max.x, max.y);
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
			this.minX = -radius;
			this.minY = -radius;
			this.maxX =  radius;
			this.maxY =  radius;
		} else {
			this.minX = center.x - radius;
			this.minY = center.y - radius;
			this.maxX = center.x + radius;
			this.maxY = center.y + radius;
		}
	}
	
	/**
	 * Copy constructor.
	 * @param aabb the {@link AABB} to copy
	 * @since 3.1.1
	 */
	public AABB(AABB aabb) {
		this.minX = aabb.minX;
		this.minY = aabb.minY;
		this.maxX = aabb.maxX;
		this.maxY = aabb.maxY;
	}
	
	/**
	 * Sets this aabb to the given aabb's value and returns
	 * this AABB.
	 * @param aabb the aabb to copy
	 * @return {@link AABB}
	 * @since 3.2.5
	 */
	public AABB set(AABB aabb) {
		this.minX = aabb.minX;
		this.minY = aabb.minY;
		this.maxX = aabb.maxX;
		this.maxY = aabb.maxY;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AABB[Min=")
		.append("(")
		.append(this.minX)
		.append(", ")
		.append(this.minY)
		.append(")")
		.append("|Max=")
		.append("(")
		.append(this.maxX)
		.append(", ")
		.append(this.maxY)
		.append(")")
		.append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Translatable#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		this.minX += x;
		this.minY += y;
		this.maxX += x;
		this.maxY += y;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Translatable#translate(org.dyn4j.geometry.Vector2)
	 */
	public void translate(Vector2 translation) {
		translate(translation.x, translation.y);
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
				this.minX + translation.x,
				this.minY + translation.y,
				this.maxX + translation.x,
				this.maxY + translation.y);
	}
	
	/**
	 * Returns the width of this {@link AABB}.
	 * @return double
	 * @since 3.0.1
	 */
	public double getWidth() {
		return this.maxX - this.minX;
	}
	
	/**
	 * Returns the height of this {@link AABB}.
	 * @return double
	 * @since 3.0.1
	 */
	public double getHeight() {
		return this.maxY - this.minY;
	}
	
	/**
	 * Returns the perimeter of this {@link AABB}.
	 * @return double
	 */
	public double getPerimeter() {
		return 2 * (this.maxX - this.minX + this.maxY - this.minY);
	}
	
	/**
	 * Returns the area of this {@link AABB};.
	 * @return double
	 */
	public double getArea() {
		return (this.maxX - this.minX) * (this.maxY - this.minY);
	}
	
	/**
	 * Performs a union of this {@link AABB} and the given {@link AABB} placing
	 * the result of the union into this {@link AABB} and then returns
	 * this {@link AABB}
	 * @param aabb the {@link AABB} to union
	 * @return {@link AABB}
	 */
	public AABB union(AABB aabb) {
		this.minX = Math.min(this.minX, aabb.minX);
		this.minY = Math.min(this.minY, aabb.minY);
		this.maxX = Math.max(this.maxX, aabb.maxX);
		this.maxY = Math.max(this.maxY, aabb.maxY);
		return this;
	}
	
	/**
	 * Performs a union of this {@link AABB} and the given {@link AABB} returning
	 * a new {@link AABB} containing the result.
	 * @param aabb the {@link AABB} to union
	 * @return {@link AABB} the resulting union
	 */
	public AABB getUnion(AABB aabb) {
		return new AABB(
				Math.min(this.minX, aabb.minX),
				Math.min(this.minY, aabb.minY),
				Math.max(this.maxX, aabb.maxX),
				Math.max(this.maxY, aabb.maxY));
	}
	
	/**
	 * Performs the intersection of this {@link AABB} and the given {@link AABB} placing
	 * the result into this {@link AABB} and then returns this {@link AABB}.
	 * <p>
	 * If the given {@link AABB} does not overlap this {@link AABB}, this {@link AABB} is
	 * set to a zero {@link AABB}.
	 * @param aabb the {@link AABB} to intersect
	 * @return {@link AABB}
	 * @since 3.1.1
	 */
	public AABB intersection(AABB aabb) {
		this.minX = Math.max(this.minX, aabb.minX);
		this.minY = Math.max(this.minY, aabb.minY);
		this.maxX = Math.min(this.maxX, aabb.maxX);
		this.maxY = Math.min(this.maxY, aabb.maxY);
		
		// check for a bad AABB
		if (this.minX > this.maxX || this.minY > this.maxY) {
			// the two AABBs were not overlapping
			// set this AABB to a degenerate one
			this.minX = 0.0;
			this.minY = 0.0;
			this.maxX = 0.0;
			this.maxY = 0.0;
		}
		
		return this;
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
		double minx = Math.max(this.minX, aabb.minX);
		double miny = Math.max(this.minY, aabb.minY);
		double maxx = Math.min(this.maxX, aabb.maxX);
		double maxy = Math.min(this.maxY, aabb.maxY);
		
		// check for a bad AABB
		if (minx > maxx || miny > maxy) {
			// the two AABBs were not overlapping
			// return a degenerate one
			return new AABB(0.0, 0.0, 0.0, 0.0);
		}
		return new AABB(minx, miny, maxx, maxy);
	}
	
	/**
	 * Expands this {@link AABB} by half the given expansion in each direction and
	 * then returns this {@link AABB}.
	 * <p>
	 * The expansion can be negative to shrink the {@link AABB}.  However, if the expansion is
	 * greater than the current width/height, the {@link AABB} can become invalid.  In this 
	 * case, the AABB will become a degenerate AABB at the mid point of the min and max for 
	 * the respective coordinates.
	 * @param expansion the expansion amount
	 * @return {@link AABB}
	 */
	public AABB expand(double expansion) {
		double e = expansion * 0.5;
		this.minX -= e;
		this.minY -= e;
		this.maxX += e;
		this.maxY += e;
		// we only need to verify the new aabb if the expansion
		// was inwardly
		if (expansion < 0.0) {
			// if the aabb is invalid then set the min/max(es) to
			// the middle value of their current values
			if (this.minX > this.maxX) {
				double mid = (this.minX + this.maxX) * 0.5;
				this.minX = mid;
				this.maxX = mid;
			}
			if (this.minY > this.maxY) {
				double mid = (this.minY + this.maxY) * 0.5;
				this.minY = mid;
				this.maxY = mid;
			}
		}
		return this;
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
		double minx = this.minX - e;
		double miny = this.minY - e;
		double maxx = this.maxX + e;
		double maxy = this.maxY + e;
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
		return new AABB(minx, miny, maxx, maxy);
	}
	
	/**
	 * Returns true if the given {@link AABB} and this {@link AABB} overlap.
	 * @param aabb the {@link AABB} to test
	 * @return boolean true if the {@link AABB}s overlap
	 */
	public boolean overlaps(AABB aabb) {
		return this.minX <= aabb.maxX &&
				this.maxX >= aabb.minX &&
				this.minY <= aabb.maxY &&
				this.maxY >= aabb.minY;
	}
	
	/**
	 * Returns true if the given {@link AABB} is contained within this {@link AABB}.
	 * @param aabb the {@link AABB} to test
	 * @return boolean
	 */
	public boolean contains(AABB aabb) {
		return this.minX <= aabb.minX &&
				this.maxX >= aabb.maxX &&
				this.minY <= aabb.minY &&
				this.maxY >= aabb.maxY;
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
		return this.minX <= x &&
				this.maxX >= x &&
				this.minY <= y &&
				this.maxY >= y;
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
		return this.minX == this.maxX || this.minY == this.maxY;
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
		return Math.abs(this.maxX - this.minX) <= error || Math.abs(this.maxY - this.minY) <= error;
	}
	
	/**
	 * Returns the minimum x extent.
	 * @return double
	 */
	public double getMinX() {
		return this.minX;
	}
	
	/**
	 * Returns the maximum x extent.
	 * @return double
	 */
	public double getMaxX() {
		return this.maxX;
	}
	
	/**
	 * Returns the maximum y extent.
	 * @return double
	 */
	public double getMaxY() {
		return this.maxY;
	}
	
	/**
	 * Returns the minimum y extent.
	 * @return double
	 */
	public double getMinY() {
		return this.minY;
	}
}
