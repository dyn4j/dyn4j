/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision.broadphase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Collisions;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Abstract implementation of a {@link BroadphaseDetector}.
 * @author William Bittle
 * @version 4.1.0
 * @since 1.0.0
 * @param <T> the object type
 */
public abstract class AbstractBroadphaseDetector<T> implements BroadphaseDetector<T> {
	/** The AABB producer */
	protected final AABBProducer<T> aabbProducer;
	
	/** The AABB expansion method */
	protected final AABBExpansionMethod<T> aabbExpansionMethod;
	
	/** The broadphase filter to cull pairs */
	protected final BroadphaseFilter<T> broadphaseFilter;

	/** True if update tracking is enabled */
	protected boolean updateTrackingEnabled;
	
	/**
	 * Minimal constructor.
	 * @param broadphaseFilter the broadphase filter
	 * @param aabbProducer the AABB producer
	 * @param aabbExpansionMethod the AABB expansion method
	 * @throws NullPointerException if broadphaseFilter, aabbProducer or aabbExpansionMethod are null
	 */
	public AbstractBroadphaseDetector(
			BroadphaseFilter<T> broadphaseFilter, 
			AABBProducer<T> aabbProducer,
			AABBExpansionMethod<T> aabbExpansionMethod) {
		
		if (broadphaseFilter == null) throw new NullPointerException(Messages.getString("collision.broadphase.nullBroadphaseFilter"));
		if (aabbProducer == null) throw new NullPointerException(Messages.getString("collision.broadphase.nullAABBProducer"));
		if (aabbExpansionMethod == null) throw new NullPointerException(Messages.getString("collision.broadphase.nullAABBExpansionMethod"));
		
		this.aabbProducer = aabbProducer;
		this.aabbExpansionMethod = aabbExpansionMethod;
		this.broadphaseFilter = broadphaseFilter;
		this.updateTrackingEnabled = false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean detect(T a, T b) {
		// attempt to use this broadphase's cache
		AABB aAABB = this.getAABB(a);
		AABB bAABB = this.getAABB(b);
		// perform the test
		if (aAABB.overlaps(bAABB)) {
			return true;
		}
		return false;
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect()
	 */
	@Override
	public List<CollisionPair<T>> detect() {
		return this.detect(true);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(boolean)
	 */
	@Override
	public List<CollisionPair<T>> detect(boolean forceFullDetection) {
		int eSize = Collisions.getEstimatedCollisionPairs(this.size());
		List<CollisionPair<T>> items = new ArrayList<CollisionPair<T>>(eSize);
		Iterator<CollisionPair<T>> it = this.detectIterator(forceFullDetection);
		while (it.hasNext()) {
			CollisionPair<T> item = it.next();
			items.add(item.copy());
		}
		return items;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detectIterator()
	 */
	@Override
	public Iterator<CollisionPair<T>> detectIterator() {
		return this.detectIterator(false);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.geometry.AABB)
	 */
	@Override
	public List<T> detect(AABB aabb) {
		int eSize = Collisions.getEstimatedCollisionsPerObject();
		List<T> items = new ArrayList<T>(eSize);
		Iterator<T> it = this.detectIterator(aabb);
		while (it.hasNext()) {
			T item = it.next();
			items.add(item);
		}
		return items;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#raycast(org.dyn4j.geometry.Ray, double)
	 */
	@Override
	public List<T> raycast(Ray ray, double length) {
		int eSize = Collisions.getEstimatedRaycastCollisions(this.size());
		List<T> items = new ArrayList<T>(eSize);
		Iterator<T> it = this.raycastIterator(ray, length);
		while (it.hasNext()) {
			T item = it.next();
			items.add(item);
		}
		return items;
	}

	/**
	 * Returns true if the ray and AABB intersect.
	 * <p>
	 * This method is ideally called for a number of AABBs where the invDx and invDy can
	 * be computed once.
	 * <p>
	 * <a href="http://tavianator.com/2011/05/fast-branchless-raybounding-box-intersections/">http://tavianator.com/2011/05/fast-branchless-raybounding-box-intersections/</a>
	 * @param start the start position of the ray
	 * @param length the length of the ray
	 * @param invDx the inverse of the x component of the ray direction
	 * @param invDy the inverse of the y component of the ray direction
	 * @param aabb the AABB to test
	 * @return true if the AABB and ray intersect
	 */
	static boolean raycast(Vector2 start, double length, double invDx, double invDy, AABB aabb) {
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
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABBProducer()
	 */
	@Override
	public AABBProducer<T> getAABBProducer() {
		return this.aabbProducer;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABBExpansionMethod()
	 */
	@Override
	public AABBExpansionMethod<T> getAABBExpansionMethod() {
		return this.aabbExpansionMethod;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getBroadphaseFilter()
	 */
	@Override
	public BroadphaseFilter<T> getBroadphaseFilter() {
		return this.broadphaseFilter;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdateTrackingEnabled()
	 */
	@Override
	public boolean isUpdateTrackingEnabled() {
		return this.updateTrackingEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#setUpdateTrackingEnabled(boolean)
	 */
	@Override
	public void setUpdateTrackingEnabled(boolean flag) {
		this.updateTrackingEnabled = flag;
	}
}
