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
package org.dyn4j.collision.broadphase;

import java.util.List;

import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * Abstract implementation of a {@link BroadphaseDetector}.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 * @param <E> the {@link Collidable} type
 * @param <T> the {@link Fixture} type
 */
public abstract class AbstractBroadphaseDetector<E extends Collidable<T>, T extends Fixture> implements BroadphaseDetector<E, T> {
	/** The default broadphase filter object */
	protected final BroadphaseFilter<E, T> defaultFilter = new DefaultBroadphaseFilter<E, T>();
	
	/** The {@link AABB} expansion value */
	protected double expansion = BroadphaseDetector.DEFAULT_AABB_EXPANSION;
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#add(org.dyn4j.collision.Collidable)
	 */
	@Override
	public void add(E collidable) {
		int size = collidable.getFixtureCount();
		// iterate over the new list
		for (int i = 0; i < size; i++) {
			T fixture = collidable.getFixture(i);
			this.add(collidable, fixture);
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(org.dyn4j.collision.Collidable)
	 */
	@Override
	public void remove(E collidable) {
		int size = collidable.getFixtureCount();
		if (size == 0) return;
		for (int i = 0; i < size; i++) {
			T fixture = collidable.getFixture(i);
			this.remove(collidable, fixture);
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update(org.dyn4j.collision.Collidable)
	 */
	@Override
	public void update(E collidable) {
		int size = collidable.getFixtureCount();
		// iterate over the new list
		for (int i = 0; i < size; i++) {
			T fixture = collidable.getFixture(i);
			this.update(collidable, fixture);
		}
	}
		
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.collision.Collidable, org.dyn4j.collision.Collidable)
	 */
	@Override
	public boolean detect(E a, E b) {
		// attempt to use this broadphase's cache
		AABB aAABB = this.getAABB(a);
		AABB bAABB = this.getAABB(b);
		// check for null
		if (aAABB == null || bAABB == null) return false;
		// perform the test
		if (aAABB.overlaps(bAABB)) {
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(org.dyn4j.collision.Collidable)
	 */
	@Override
	public AABB getAABB(E collidable) {
		int size = collidable.getFixtureCount();
		if (size == 0) return new AABB(0, 0, 0, 0);
		AABB union = this.getAABB(collidable, collidable.getFixture(0));
		for (int i = 1; i < size; i++) {
			AABB aabb = this.getAABB(collidable, collidable.getFixture(i));
			union.union(aabb);
		}
		return union;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform)
	 */
	@Override
	public boolean detect(Convex convex1, Transform transform1, Convex convex2, Transform transform2) {
		// compute the shape's aabbs
		AABB a = convex1.createAABB(transform1);
		AABB b = convex2.createAABB(transform2);
		
		// if both sets of intervals overlap then we have a possible intersection
		if (a.overlaps(b)) {
			return true;
		}
		// otherwise they definitely do not intersect
		return false;
	}
	
	/**
	 * Returns true if the ray and AABB intersect.
	 * <p>
	 * This method is ideally called for a number of AABBs where the invDx and invDy can
	 * be computed once.
	 * @param start the start position of the ray
	 * @param length the length of the ray
	 * @param invDx the inverse of the x component of the ray direction
	 * @param invDy the inverse of the y component of the ray direction
	 * @param aabb the AABB to test
	 * @return true if the AABB and ray intersect
	 */
	protected boolean raycast(Vector2 start, double length, double invDx, double invDy, AABB aabb) {
		// see here for implementation details
		// http://tavianator.com/2011/05/fast-branchless-raybounding-box-intersections/
		double tx1 = (aabb.getMinX() - start.x) * invDx;
		double tx2 = (aabb.getMaxX() - start.x) * invDx;

		double tmin = Math.min(tx1, tx2);
		double tmax = Math.max(tx1, tx2);

		double ty1 = (aabb.getMinY() - start.y) * invDy;
		double ty2 = (aabb.getMaxY() - start.y) * invDy;

		tmin = Math.max(tmin, Math.min(ty1, ty2));
		tmax = Math.min(tmax, Math.max(ty1, ty2));
		// the ray is pointing in the opposite direction
		if (tmax < 0) return false;
		// consider the ray length
		if (tmin > length) return false;
		// along the ray, tmax should be larger than tmin
		return tmax >= tmin;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect()
	 */
	@Override
	public List<BroadphasePair<E, T>> detect() {
		return this.detect(this.defaultFilter);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.geometry.AABB)
	 */
	@Override
	public List<BroadphaseItem<E, T>> detect(AABB aabb) {
		return this.detect(aabb, this.defaultFilter);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#raycast(org.dyn4j.geometry.Ray, double)
	 */
	@Override
	public List<BroadphaseItem<E, T>> raycast(Ray ray, double length) {
		return this.raycast(ray, length, this.defaultFilter);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABBExpansion()
	 */
	@Override
	public double getAABBExpansion() {
		return this.expansion;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#setAABBExpansion(double)
	 */
	@Override
	public void setAABBExpansion(double expansion) {
		this.expansion = expansion;
	}
}
