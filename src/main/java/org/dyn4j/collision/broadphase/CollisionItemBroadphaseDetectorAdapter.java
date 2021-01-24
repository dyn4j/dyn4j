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

import org.dyn4j.collision.BasicCollisionItem;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;

/**
 * Wraps any {@link BroadphaseDetector} for use as a {@link CollisionItemBroadphaseDetector}.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public final class CollisionItemBroadphaseDetectorAdapter<T extends CollisionBody<E>, E extends Fixture> extends BroadphaseDetectorDecoratorAdapter<CollisionItem<T, E>> implements CollisionItemBroadphaseDetector<T, E> {
	/** A reusable collision item to avoid allocation as much as possible */
	private final BroadphaseItem<T, E> reusableItem = new BroadphaseItem<T, E>(null, null);
	
	/**
	 * Minimal constructor.
	 * @param detector the broad-phase detector to wrap
	 */
	public CollisionItemBroadphaseDetectorAdapter(BroadphaseDetector<CollisionItem<T, E>> detector) {
		super(detector);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#add(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public void add(T body) {
		int size = body.getFixtureCount();
		for (int i = 0; i < size; i++) {
			CollisionItem<T, E> item = new BasicCollisionItem<T, E>(body, body.getFixture(i));
			this.detector.add(item);
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#add(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void add(T body, E fixture) {
		CollisionItem<T, E> item = new BasicCollisionItem<T, E>(body, fixture);
		this.detector.add(item);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#contains(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public boolean contains(T body) {
		this.reusableItem.body = body;
		int size = body.getFixtureCount();
		if (size == 0) return false;
		for (int i = 0; i < size; i++) {
			this.reusableItem.fixture = body.getFixture(i);
			if (!this.detector.contains(this.reusableItem)) {
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#contains(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean contains(T body, E fixture) {
		this.reusableItem.body = body;
		this.reusableItem.fixture = fixture;
		return this.detector.contains(this.reusableItem);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#detect(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.CollisionBody)
	 */
	public boolean detect(T body1, T body2) {
		// attempt to use this broadphase's cache
		AABB aAABB = this.getAABB(body1);
		AABB bAABB = this.getAABB(body2);
		// perform the test
		if (aAABB.overlaps(bAABB)) {
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#detect(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture, org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean detect(T body1, E fixture1, T body2, E fixture2) {
		CollisionItem<T, E> item1 = new BasicCollisionItem<T, E>(body1, fixture1);
		CollisionItem<T, E> item2 = new BasicCollisionItem<T, E>(body2, fixture2);
		return this.detector.detect(item1, item2);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#getAABB(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public AABB getAABB(T body, E fixture) {
		this.reusableItem.body = body;
		this.reusableItem.fixture = fixture;
		return this.detector.getAABB(this.reusableItem);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#getAABB(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public AABB getAABB(T body) {
		int size = body.getFixtureCount();
		if (size <= 0) {
			return new AABB(0,0,0,0);
		}
		
		this.reusableItem.body = body;
		this.reusableItem.fixture = body.getFixture(0);
		AABB aabb = this.detector.getAABB(this.reusableItem);
		
		for (int i = 1; i < size; i++) {
			this.reusableItem.fixture = body.getFixture(i);
			aabb.union(this.detector.getAABB(this.reusableItem));
		}
		
		return aabb;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#isUpdated(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public boolean isUpdated(T body) {
		boolean updated = false;
		this.reusableItem.body = body;
		int size = body.getFixtureCount();
		for (int i = 0; i < size; i++) {
			this.reusableItem.fixture = body.getFixture(i);
			updated |= this.detector.isUpdated(this.reusableItem);
		}
		return updated;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#isUpdated(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean isUpdated(T body, E fixture) {
		this.reusableItem.body = body;
		this.reusableItem.fixture = fixture;
		return this.detector.isUpdated(this.reusableItem);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#remove(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public boolean remove(T body) {
		boolean removed = true;
		this.reusableItem.body = body;
		int size = body.getFixtureCount();
		for (int i = 0; i < size; i++) {
			this.reusableItem.fixture = body.getFixture(i);
			removed &= this.detector.remove(this.reusableItem);
		}
		return removed;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#remove(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean remove(T body, E fixture) {
		this.reusableItem.body = body;
		this.reusableItem.fixture = fixture;
		return this.detector.remove(this.reusableItem);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#setUpdated(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public void setUpdated(T body) {
		int size = body.getFixtureCount();
		for (int i = 0; i < size; i++) {
			CollisionItem<T, E> item = new BroadphaseItem<T, E>(body, body.getFixture(i));
			this.detector.setUpdated(item);
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#setUpdated(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void setUpdated(T body, E fixture) {
		CollisionItem<T, E> item = new BroadphaseItem<T, E>(body, fixture);
		this.detector.setUpdated(item);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#update(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public void update(T body) {
		int size = body.getFixtureCount();
		for (int i = 0; i < size; i++) {
			CollisionItem<T, E> item = new BroadphaseItem<T, E>(body, body.getFixture(i));
			this.detector.update(item);
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BodyFixtureBroadphaseDetector#update(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void update(T body, E fixture) {
		CollisionItem<T, E> item = new BroadphaseItem<T, E>(body, fixture);
		this.detector.update(item);
	}
}
