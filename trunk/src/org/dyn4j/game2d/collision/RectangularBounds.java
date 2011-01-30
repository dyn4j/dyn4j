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
package org.dyn4j.game2d.collision;

import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Interval;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Transformable;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Represents a {@link Bounds} object that is rectangular.
 * <p>
 * This class performs AABB collision detection to determine if the given {@link Collidable}
 * is inside or outside the bounds.
 * <p>
 * If the {@link Collidable} and the {@link Bounds} AABBs are overlapping, then the {@link Collidable}
 * is considered to be inside, if they are not overlapping, the {@link Collidable} is considered
 * outside.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class RectangularBounds extends AbstractBounds implements Bounds, Transformable {
	/** The x-axis */
	protected static final Vector2 X_AXIS = new Vector2(1.0, 0.0);
	
	/** The y-axis */
	protected static final Vector2 Y_AXIS = new Vector2(0.0, 1.0);
	
	/** The bounding rectangle */
	protected Rectangle bounds;
	
	/**
	 * Creates a new bounds object.
	 * @param bounds the rectangular bounds
	 * @throws NullPointerException if bounds is null
	 */
	public RectangularBounds(Rectangle bounds) {
		if (bounds == null) throw new NullPointerException("The bounds rectangle cannot be null.");
		this.bounds = bounds;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RECTANGULAR_BOUNDS[").append(this.bounds).append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.Bounds#isOutside(org.dyn4j.game2d.collision.Collidable)
	 */
	@Override
	public boolean isOutside(Collidable collidable) {
		// project the collidable on the x and y axes
		Transform transform = collidable.getTransform();
		int size = collidable.getFixtureCount();
		
		// check for zero fixtures
		if (size == 0) {
			return true;
		}
		
		Fixture fixture;
		Convex convex;
		
		// perform the projection the first time to create an interval
		fixture = collidable.getFixture(0);
		convex = fixture.getShape();
		Interval x = convex.project(RectangularBounds.X_AXIS, transform);
		Interval y = convex.project(RectangularBounds.Y_AXIS, transform);
		// loop through the rest, union the resulting intervals
		for (int i = 1; i < size; i++) {
			fixture = collidable.getFixture(i);
			convex = fixture.getShape();
			x.union(convex.project(RectangularBounds.X_AXIS, transform));
			y.union(convex.project(RectangularBounds.Y_AXIS, transform));
		}
		
		// project the bounds
		Interval bx = this.bounds.project(RectangularBounds.X_AXIS, this.transform);
		Interval by = this.bounds.project(RectangularBounds.Y_AXIS, this.transform);
		
		// test the projections for overlap
		if (x.overlaps(bx) && y.overlaps(by)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Sets the bounds.
	 * @param bounds the bounds
	 * @throws NullPointerException if bounds is null
	 */
	public void setBounds(Rectangle bounds) {
		if (bounds == null) throw new NullPointerException("The bounds rectangle cannot be null.");
		this.bounds = bounds;
	}
	
	/**
	 * Returns the bounds.
	 * @return {@link Rectangle} the bounds
	 */
	public Rectangle getBounds() {
		return this.bounds;
	}
}
