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
package org.dyn4j.game2d.collision.broadphase;

import org.dyn4j.game2d.collision.Collidable;
import org.dyn4j.game2d.collision.Fixture;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Interval;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Abstract implementation of a {@link BroadphaseDetector} providing AABB
 * (Axis Aligned Bounding Box) detection methods.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public abstract class AbstractAABBDetector implements BroadphaseDetector {
	/** Constant for the x-axis {@link Vector2} */
	protected static final Vector2 X_AXIS = new Vector2(1, 0);
	
	/** Constant for the y-axis {@link Vector2} */
	protected static final Vector2 Y_AXIS = new Vector2(0, 1);
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.game2d.collision.Collidable, org.dyn4j.game2d.collision.Collidable)
	 */
	@Override
	public boolean detect(Collidable collidable1, Collidable collidable2) {
		int size1 = collidable1.getFixtureCount();
		int size2 = collidable2.getFixtureCount();
		
		// see if either collidable has zero fixtures
		if (size1 == 0 || size2 == 0) {
			return false;
		}
		
		// get the transforms
		Transform transform1 = collidable1.getTransform();
		Transform transform2 = collidable2.getTransform();
		
		Fixture fixture;
		Convex convex;
		
		// project all the shapes of collidable1
		fixture = collidable1.getFixture(0);
		convex = fixture.getShape();
		Interval x1 = convex.project(AbstractAABBDetector.X_AXIS, transform1);
		Interval y1 = convex.project(AbstractAABBDetector.Y_AXIS, transform1);
		for (int i = 1; i < size1; i++) {
			fixture = collidable1.getFixture(i);
			convex = fixture.getShape();
			x1.union(convex.project(AbstractAABBDetector.X_AXIS, transform1));
			y1.union(convex.project(AbstractAABBDetector.Y_AXIS, transform1));
		}
		
		// project all the shapes of collidable2
		fixture = collidable2.getFixture(0);
		convex = fixture.getShape();
		Interval x2 = convex.project(AbstractAABBDetector.X_AXIS, transform2);
		Interval y2 = convex.project(AbstractAABBDetector.Y_AXIS, transform2);
		for (int i = 1; i < size2; i++) {
			fixture = collidable2.getFixture(i);
			convex = fixture.getShape();
			x2.union(convex.project(AbstractAABBDetector.X_AXIS, transform2));
			y2.union(convex.project(AbstractAABBDetector.Y_AXIS, transform2));
		}
		
		// if both sets of intervals overlap then we have a possible intersection
		if (x1.overlaps(x2) && y1.overlaps(y2)) {
			return true;
		}
		// otherwise they definitely do not intersect
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.game2d.geometry.Convex, org.dyn4j.game2d.geometry.Transform, org.dyn4j.game2d.geometry.Convex, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public boolean detect(Convex convex1, Transform transform1, Convex convex2, Transform transform2) {
		// project both convex shapes onto the x and y axes
		Interval x1 = convex1.project(AbstractAABBDetector.X_AXIS, transform1);
		Interval x2 = convex2.project(AbstractAABBDetector.X_AXIS, transform2);
		Interval y1 = convex1.project(AbstractAABBDetector.Y_AXIS, transform1);
		Interval y2 = convex2.project(AbstractAABBDetector.Y_AXIS, transform2);
		
		// if both sets of intervals overlap then we have a possible intersection
		if (x1.overlaps(x2) && y1.overlaps(y2)) {
			return true;
		}
		// otherwise they definitely do not intersect
		return false;
	}
}
