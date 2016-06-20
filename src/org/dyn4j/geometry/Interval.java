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
 * Represents a one dimensional numeric {@link Interval}.
 * @author William Bittle
 * @version 3.1.9
 * @since 1.0.0
 */
public class Interval {
	/** The minimum value */
	protected double min;
	
	/** The maximum value */
	protected double max;
	
	/**
	 * Full constructor.
	 * @param min the minimum value
	 * @param max the maximum value
	 * @throws IllegalArgumentException if min &gt; max
	 */
	public Interval(double min, double max) {
		if (min > max) throw new IllegalArgumentException(Messages.getString("geometry.interval.invalid"));
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Copy constructor.
	 * @param interval the {@link Interval} to copy
	 * @since 3.1.1
	 */
	public Interval(Interval interval) {
		this.min = interval.min;
		this.max = interval.max;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(this.min).append(", ").append(this.max).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the minimum value for this {@link Interval}.
	 * @return double
	 */
	public double getMin() {
		return this.min;
	}
	
	/**
	 * Returns the maximum value for this {@link Interval}.
	 * @return double
	 */
	public double getMax() {
		return this.max;
	}
	
	/**
	 * Sets the minimum for this interval.
	 * @param min the minimum value
	 * @throws IllegalArgumentException if min &gt; max
	 */
	public void setMin(double min) {
		if (min > this.max) throw new IllegalArgumentException(Messages.getString("geometry.interval.invalidMinimum"));
		this.min = min;
	}
	
	/**
	 * Sets the maximum for this interval.
	 * @param max the maximum value
	 * @throws IllegalArgumentException if max &lt; min
	 */
	public void setMax(double max) {
		if (max < this.min) throw new IllegalArgumentException(Messages.getString("geometry.interval.invalidMaximum"));
		this.max = max;
	}

	/**
	 * Returns true if the given value is within this {@link Interval}
	 * including the maximum and minimum.
	 * @param value the test value
	 * @return boolean
	 */
	public boolean includesInclusive(double value) {
		return value <= this.max && value >= this.min;
	}
	
	/**
	 * Returns true if the given value is within this {@link Interval}
	 * exlcuding the maximum and minimum.
	 * @param value the test value
	 * @return boolean
	 */
	public boolean includesExclusive(double value) {
		return value < this.max && value > this.min;
	}
	
	/**
	 * Returns true if the given value is within this {@link Interval}
	 * including the minimum and excluding the maximum.
	 * @param value the test value
	 * @return boolean
	 */
	public boolean includesInclusiveMin(double value) {
		return value < this.max && value >= this.min;
	}
	
	/**
	 * Returns true if the given value is within this {@link Interval}
	 * including the maximum and excluding the minimum.
	 * @param value the test value
	 * @return boolean
	 */
	public boolean includesInclusiveMax(double value) {
		return value <= this.max && value > this.min;
	}
	
	/**
	 * Returns true if the two {@link Interval}s overlap.
	 * @param interval the {@link Interval}
	 * @return boolean
	 */
	public boolean overlaps(Interval interval) {
		return !(this.min > interval.max || interval.min > this.max);
	}
	
	/**
	 * Returns the amount of overlap between this {@link Interval} and the given
	 * {@link Interval}.
	 * <p>
	 * This method tests to if the {@link Interval}s overlap first.  If they do then
	 * the overlap is returned, if they do not then 0 is returned.
	 * @param interval the {@link Interval}
	 * @return double
	 */
	public double getOverlap(Interval interval) {
		// make sure they overlap
		if (this.overlaps(interval)) {
			return Math.min(this.max, interval.max) - Math.max(this.min, interval.min);
		}
		return 0;
	}
	
	/**
	 * If the value is within this {@link Interval}, inclusive, then return the value, else
	 * return either the max or minimum value.
	 * @param value the value to clamp
	 * @return double
	 */
	public double clamp(double value) {
		return Interval.clamp(value, this.min, this.max);
	}
	
	/**
	 * Returns a number clamped between two other numbers.
	 * <p>
	 * This method assumes that min &le; max.
	 * @param value the value to clamp
	 * @param min the min value
	 * @param max the max value
	 * @return double
	 */
	public static double clamp(double value, double min, double max) {
		if (value <= max && value >= min) {
			return value;
		} else if (max < value) {
			return max;
		} else {
			return min;
		}
	}
	
	/**
	 * Returns true if this {@link Interval} is degenerate.
	 * <p>
	 * An {@link Interval} is degenerate if it equals [a, a].
	 * @return boolean
	 */
	public boolean isDegenerate() {
		return this.min == this.max;
	}
	
	/**
	 * Returns true if this {@link Interval} is degenerate
	 * given the allowed error.
	 * <p>
	 * An {@link Interval} is degenerate given some error if
	 * max - min &lt;= error.
	 * @param error the allowed error
	 * @return boolean
	 */
	public boolean isDegenerate(double error) {
		return Math.abs(this.max - this.min) <= error;
	}
	
	/**
	 * Returns true if the given {@link Interval} is contained in this {@link Interval}.
	 * @param interval the {@link Interval}
	 * @return boolean
	 */
	public boolean contains(Interval interval) {
		return interval.min > this.min && interval.max < this.max;
	}
	
	/**
	 * Sets this {@link Interval} to the union of this {@link Interval} and the given {@link Interval}.
	 * <p>
	 * If the two {@link Interval}s are not overlapping then this method will
	 * return one {@link Interval} that represents an {@link Interval} enclosing both
	 * {@link Interval}s.
	 * @param interval the {@link Interval}
	 */
	public void union(Interval interval) {
		this.min = Math.min(interval.min, this.min);
		this.max = Math.max(interval.max, this.max);
	}
	
	/**
	 * Returns the union of the given {@link Interval} and this {@link Interval}.
	 * @see Interval#union(Interval)
	 * @param interval the {@link Interval}
	 * @return {@link Interval}
	 */
	public Interval getUnion(Interval interval) {
		return new Interval(Math.min(interval.min, this.min), Math.max(interval.max, this.max));
	}
	
	/**
	 * Sets this {@link Interval} to the intersection of this {@link Interval} and the given {@link Interval}.
	 * <p>
	 * If the two {@link Interval}s are not overlapping then this method will make this {@link Interval}
	 * the a zero degenerate {@link Interval}, [0, 0].
	 * @param interval the {@link Interval}
	 */
	public void intersection(Interval interval) {
		if (this.overlaps(interval)) {
			this.min = Math.max(interval.min, this.min);
			this.max = Math.min(interval.max, this.max);
		} else {
			this.min = 0;
			this.max = 0;
		}
	}
	
	/**
	 * Returns the intersection of the given {@link Interval} and this {@link Interval}.
	 * @see Interval#intersection(Interval)
	 * @param interval the {@link Interval}
	 * @return {@link Interval}
	 */
	public Interval getIntersection(Interval interval) {
		if (this.overlaps(interval)) {
			return new Interval(Math.max(interval.min, this.min), Math.min(interval.max, this.max));
		}
		return new Interval(0, 0);
	}
	
	/**
	 * Returns the distance between the two {@link Interval}s.
	 * <p>
	 * If the given interval overlaps this interval, zero is returned.
	 * @param interval the {@link Interval}
	 * @return double
	 */
	public double distance(Interval interval) {
		// make sure they arent overlapping
		if (!this.overlaps(interval)) {
			// the distance is calculated by taking the max of one - the min of the other
			// the interval whose max will be used is determined by the interval with the max
			// less than the other's min
			if (this.max < interval.min) {
				return interval.min - this.max;
			} else {
				return this.min - interval.max;
			}
		}
		// if they are overlapping then return 0
		return 0;
	}
	
	/**
	 * Expands this {@link Interval} by half the given amount in both directions.
	 * <p>
	 * The value can be negative to shrink the interval.  However, if the value is
	 * greater than the current length of the interval, the interval can become
	 * invalid.  In this case, the interval will become a degenerate interval at
	 * the mid point of the min and max.
	 * @param value the value
	 */
	public void expand(double value) {
		double e = value * 0.5;
		this.min -= e;
		this.max += e;
		// verify the interval is still valid
		if (value < 0.0 && this.min > this.max) {
			// if its not then set the min/max to
			// the middle value of their current values
			double p = (this.min + this.max) * 0.5;
			this.min = p;
			this.max = p;
		}
	}
	
	/**
	 * Returns a new {@link Interval} of this interval expanded by half the given amount
	 * in both directions.
	 * <p>
	 * The value can be negative to shrink the interval.  However, if the value is
	 * greater than the current length of the interval, the interval will be 
	 * invalid.  In this case, the interval returned will be a degenerate interval at
	 * the mid point of the min and max.
	 * @param value the value
	 * @return {@link Interval}
	 * @since 3.1.1
	 */
	public Interval getExpanded(double value) {
		double e = value * 0.5;
		double min = this.min - e;
		double max = this.max + e;
		// verify the interval is still valid
		if (value < 0.0 && min > max) {
			// if its not then set the min/max to
			// the middle value of their current values
			double p = (min + max) * 0.5;
			min = p;
			max = p;
		}
		return new Interval(min, max);
	}
	
	/**
	 * Returns the length of this interval from its min to its max.
	 * @return double
	 * @since 3.1.1
	 */
	public double getLength() {
		return this.max - this.min;
	}
}
