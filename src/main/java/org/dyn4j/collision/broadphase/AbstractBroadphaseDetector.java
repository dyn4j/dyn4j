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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Collisions;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * Abstract implementation of a {@link BroadphaseDetector}.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public abstract class AbstractBroadphaseDetector<T extends CollisionBody<E>, E extends Fixture> implements BroadphaseDetector<T, E> {
	/** The {@link AABB} expansion value */
	protected double expansion = BroadphaseDetector.DEFAULT_AABB_EXPANSION;
	
	/** True if update tracking is enabled */
	protected boolean updateTrackingEnabled = true;
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#add(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public void add(T body) {
		int size = body.getFixtureCount();
		// iterate over the new list
		for (int i = 0; i < size; i++) {
			E fixture = body.getFixture(i);
			this.add(body, fixture);
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public void remove(T body) {
		int size = body.getFixtureCount();
		if (size == 0) return;
		// create an item to reuse so we don't allocate a bunch of these
		BroadphaseItem<T, E> item = new BroadphaseItem<T, E>(body, null);
		for (int i = 0; i < size; i++) {
			E fixture = body.getFixture(i);
			item.fixture = fixture;
			this.remove(item);
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public void update(T body) {
		int size = body.getFixtureCount();
		// iterate over the new list
		for (int i = 0; i < size; i++) {
			E fixture = body.getFixture(i);
			this.update(body, fixture);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#setUpdated(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public void setUpdated(T body) {
		int size = body.getFixtureCount();
		// iterate over the new list
		for (int i = 0; i < size; i++) {
			E fixture = body.getFixture(i);
			this.setUpdated(body, fixture);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdated(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public boolean isUpdated(T body) {
		int size = body.getFixtureCount();
		if (size == 0) return false;
		boolean updated = false;
		// create an item to reuse so we don't allocate a bunch of these
		BroadphaseItem<T, E> item = new BroadphaseItem<T, E>(body, null);
		// iterate over the new list
		for (int i = 0; i < size; i++) {
			E fixture = body.getFixture(i);
			item.fixture = fixture;
			updated |= this.isUpdated(item);
		}
		return updated;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public boolean detect(T a, T b) {
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
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public AABB getAABB(T body) {
		int size = body.getFixtureCount();
		if (size == 0) {
			return new AABB(0, 0, 0, 0);
		}
		// create an item to reuse so we don't allocate a bunch of these
		BroadphaseItem<T, E> item = new BroadphaseItem<T, E>(body, body.getFixture(0));
		AABB union = this.getAABB(item).copy();
		for (int i = 1; i < size; i++) {
			item.fixture = body.getFixture(i);
			AABB aabb = this.getAABB(item);
			union.union(aabb);
		}
		return union;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public AABB getAABB(T body, E fixture) {
		CollisionItem<T, E> key = new BroadphaseItem<T, E>(body, fixture);
		return this.getAABB(key);
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
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#contains(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public boolean contains(T body) {
		int size = body.getFixtureCount();
		if (size == 0) return false;
		// create an item to reuse so we don't allocate a bunch of these
		BroadphaseItem<T, E> item = new BroadphaseItem<T, E>(body, null);
		for (int i = 0; i < size; i++) {
			E fixture = body.getFixture(i);
			item.fixture = fixture;
			if (!this.contains(item)) {
				return false;
			}
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect()
	 */
	@Override
	public List<CollisionPair<T, E>> detect() {
		int eSize = Collisions.getEstimatedCollisionPairs(this.size());
		List<CollisionPair<T, E>> items = new ArrayList<CollisionPair<T,E>>(eSize);
		Iterator<CollisionPair<T, E>> it = this.detectIterator(true);
		while (it.hasNext()) {
			CollisionPair<T, E> item = it.next();
			items.add(item.copy());
		}
		return items;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(boolean)
	 */
	@Override
	public List<CollisionPair<T, E>> detect(boolean forceFullDetection) {
		int eSize = Collisions.getEstimatedCollisionPairs(this.size());
		List<CollisionPair<T, E>> items = new ArrayList<CollisionPair<T,E>>(eSize);
		Iterator<CollisionPair<T, E>> it = this.detectIterator(forceFullDetection);
		while (it.hasNext()) {
			CollisionPair<T, E> item = it.next();
			items.add(item.copy());
		}
		return items;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.collision.broadphase.BroadphaseFilter)
	 */
	@Deprecated
	@Override
	public List<CollisionPair<T, E>> detect(BroadphaseFilter<T, E> filter) {
		int eSize = Collisions.getEstimatedCollisionPairs(this.size());
		List<CollisionPair<T, E>> items = new ArrayList<CollisionPair<T,E>>(eSize);
		Iterator<CollisionPair<T, E>> it = this.detectIterator(true);
		while (it.hasNext()) {
			CollisionPair<T, E> item = it.next();
			if (filter.isAllowed(item.getBody1(), item.getFixture1(), item.getBody2(), item.getFixture2())) {
				items.add(item.copy());
			}
		}
		return items;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.geometry.AABB)
	 */
	@Override
	public List<CollisionItem<T, E>> detect(AABB aabb) {
		int eSize = Collisions.getEstimatedCollisionsPerObject();
		List<CollisionItem<T, E>> items = new ArrayList<CollisionItem<T,E>>(eSize);
		Iterator<CollisionItem<T, E>> it = this.detectIterator(aabb);
		while (it.hasNext()) {
			CollisionItem<T, E> item = it.next();
			items.add(item.copy());
		}
		return items;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.geometry.AABB, org.dyn4j.collision.broadphase.BroadphaseFilter)
	 */
	@Deprecated
	@Override
	public List<CollisionItem<T, E>> detect(AABB aabb, BroadphaseFilter<T, E> filter) {
		int eSize = Collisions.getEstimatedCollisionsPerObject();
		List<CollisionItem<T, E>> items = new ArrayList<CollisionItem<T,E>>(eSize);
		Iterator<CollisionItem<T, E>> it = this.detectIterator(aabb);
		while (it.hasNext()) {
			CollisionItem<T, E> item = it.next();
			if (filter.isAllowed(aabb, item.getBody(), item.getFixture())) {
				items.add(item.copy());
			}
		}
		return items;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.geometry.Ray, double)
	 */
	@Override
	public List<CollisionItem<T, E>> detect(Ray ray, double length) {
		int eSize = Collisions.getEstimatedRaycastCollisions(this.size());
		List<CollisionItem<T, E>> items = new ArrayList<CollisionItem<T,E>>(eSize);
		Iterator<CollisionItem<T, E>> it = this.detectIterator(ray, length);
		while (it.hasNext()) {
			CollisionItem<T, E> item = it.next();
			items.add(item.copy());
		}
		return items;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#raycast(org.dyn4j.geometry.Ray, double)
	 */
	@Deprecated
	@Override
	public List<CollisionItem<T, E>> raycast(Ray ray, double length) {
		return this.detect(ray, length);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.geometry.Ray, length, org.dyn4j.collision.broadphase.BroadphaseFilter)
	 */
	@Deprecated
	@Override
	public List<CollisionItem<T, E>> raycast(Ray ray, double length, BroadphaseFilter<T, E> filter) {
		int eSize = Collisions.getEstimatedRaycastCollisions(this.size());
		List<CollisionItem<T, E>> items = new ArrayList<CollisionItem<T,E>>(eSize);
		Iterator<CollisionItem<T, E>> it = this.detectIterator(ray, length);
		while (it.hasNext()) {
			CollisionItem<T, E> item = it.next();
			if (filter.isAllowed(ray, length, item.getBody(), item.getFixture())) {
				items.add(item.copy());
			}
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
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#supportsAABBExpansion()
	 */
	@Override
	@Deprecated
	public boolean supportsAABBExpansion() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isAABBExpansionSupported()
	 */
	@Override
	public boolean isAABBExpansionSupported() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdateTrackingSupported()
	 */
	@Override
	public boolean isUpdateTrackingSupported() {
		return true;
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
