/*
 * Copyright (c) 2010, William Bittle
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

import java.util.List;

import org.dyn4j.game2d.collision.Collidable;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Interval;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Abstract implementation of a {@link BroadphaseDetector} providing AABB
 * (Axis Aligned Bounding Box) detection methods.
 * @author William Bittle
 */
public abstract class AbstractAABBDetector implements BroadphaseDetector {
	/** Constant for the x-axis {@link Vector} */
	protected static final Vector X_AXIS = new Vector(1, 0);
	
	/** Constant for the y-axis {@link Vector} */
	protected static final Vector Y_AXIS = new Vector(0, 1);
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.game2d.collision.Collidable, org.dyn4j.game2d.collision.Collidable)
	 */
	@Override
	public boolean detect(Collidable c1, Collidable c2) {
		// get the shapes
		List<Convex> shapes1 = c1.getShapes();
		List<Convex> shapes2 = c2.getShapes();
		int size1 = shapes1.size();
		int size2 = shapes2.size();
		
		// get the transforms
		Transform t1 = c1.getTransform();
		Transform t2 = c2.getTransform();
		
		// project all the shapes of collidable1
		Convex s1 = shapes1.get(0);
		Interval x1 = s1.project(Sap.X_AXIS, t1);
		Interval y1 = s1.project(Sap.Y_AXIS, t1);
		for (int j = 1; j < size1; j++) {
			s1 = shapes1.get(j);
			x1.union(s1.project(Sap.X_AXIS, t1));
			y1.union(s1.project(Sap.Y_AXIS, t1));
		}
		
		// project all the shapes of collidable2
		Convex s2 = shapes2.get(0);
		Interval x2 = s2.project(Sap.X_AXIS, t2);
		Interval y2 = s2.project(Sap.Y_AXIS, t2);
		for (int j = 1; j < size2; j++) {
			s2 = shapes2.get(j);
			x2.union(s2.project(Sap.X_AXIS, t2));
			y2.union(s2.project(Sap.Y_AXIS, t2));
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
	public boolean detect(Convex c1, Transform t1, Convex c2, Transform t2) {
		// project both convex shapes onto the x and y axes
		Interval x1 = c1.project(Sap.X_AXIS, t1);
		Interval x2 = c2.project(Sap.X_AXIS, t2);
		Interval y1 = c1.project(Sap.Y_AXIS, t1);
		Interval y2 = c2.project(Sap.Y_AXIS, t2);
		
		// if both sets of intervals overlap then we have a possible intersection
		if (x1.overlaps(x2) && y1.overlaps(y2)) {
			return true;
		}
		// otherwise they definitely do not intersect
		return false;
	}
}
