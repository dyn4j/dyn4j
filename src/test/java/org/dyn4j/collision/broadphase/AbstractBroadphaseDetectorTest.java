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

import java.util.Iterator;

import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.TestCollisionBody;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Class used to test the {@link AbstractBroadphaseDetector} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class AbstractBroadphaseDetectorTest {
	private final class BP extends AbstractBroadphaseDetector<TestCollisionBody> {
		public BP(
				BroadphaseFilter<TestCollisionBody> broadphaseFilter, 
				AABBProducer<TestCollisionBody> aabbProducer,
				AABBExpansionMethod<TestCollisionBody> aabbExpansionMethod) {
			super(broadphaseFilter, aabbProducer, aabbExpansionMethod);
		}
		
		@Override
		public void add(TestCollisionBody body) {}
		@Override
		public void clear() {}
		@Override
		public void clearUpdates() {}
		@Override
		public boolean contains(TestCollisionBody body) { return false; }
		@Override
		public Iterator<TestCollisionBody> detectIterator(AABB aabb) { return null; }
		@Override
		public Iterator<CollisionPair<TestCollisionBody>> detectIterator(boolean forceFullDetection) { return null; }
		@Override
		public AABB getAABB(TestCollisionBody body) { return body.createAABB(); }
		@Override
		public boolean isUpdated(TestCollisionBody body) { return true; }
		@Override
		public boolean isUpdateTrackingSupported() { return false; }
		@Override
		public void optimize() {}
		@Override
		public Iterator<TestCollisionBody> raycastIterator(Ray ray, double length) { return null; }
		@Override
		public boolean remove(TestCollisionBody body) { return false; }
		@Override
		public void setUpdated(TestCollisionBody body) {}
		@Override
		public void shift(Vector2 shift) {}
		@Override
		public int size() { return 0; }
		@Override
		public void update() {}
		@Override
		public void update(TestCollisionBody body) {}
	}
	
	private final BroadphaseFilter<TestCollisionBody> broadphaseFilter = new CollisionBodyBroadphaseFilter<TestCollisionBody>();
	private final AABBProducer<TestCollisionBody> aabbProducer = new CollisionBodyAABBProducer<TestCollisionBody>();
	private final AABBExpansionMethod<TestCollisionBody> aabbExpansionMethod = new NullAABBExpansionMethod<TestCollisionBody>();
	
	/**
	 * Tests the constructor with a null filter.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullFilter() {
		new BP(null, this.aabbProducer, this.aabbExpansionMethod);
	}

	/**
	 * Tests the constructor with a null AABB producer.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullAABBProducer() {
		new BP(this.broadphaseFilter, null, this.aabbExpansionMethod);
	}

	/**
	 * Tests the constructor with a null AABB expansion method.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullAABBExpansionMethod() {
		new BP(this.broadphaseFilter, this.aabbProducer, null);
	}
	
	/**
	 * Tests the constructor.
	 */
	@Test
	public void createSuccess() {
		BP bp = new BP(this.broadphaseFilter, this.aabbProducer, this.aabbExpansionMethod);
		
		TestCase.assertFalse(bp.isUpdateTrackingEnabled());
		TestCase.assertEquals(this.broadphaseFilter, bp.getBroadphaseFilter());
		TestCase.assertEquals(this.aabbProducer, bp.getAABBProducer());
		TestCase.assertEquals(this.aabbExpansionMethod, bp.getAABBExpansionMethod());
	}
	
	/**
	 * Tests the toggling of update tracking.
	 */
	@Test
	public void updateTracking() {
		BP bp = new BP(this.broadphaseFilter, this.aabbProducer, this.aabbExpansionMethod);
		
		TestCase.assertFalse(bp.isUpdateTrackingEnabled());
		
		bp.setUpdateTrackingEnabled(true);
		TestCase.assertTrue(bp.isUpdateTrackingEnabled());
		
		bp.setUpdateTrackingEnabled(false);
		TestCase.assertFalse(bp.isUpdateTrackingEnabled());
	}

	/**
	 * Tests the {@link AbstractBroadphaseDetector} detect methods.
	 */
	@Test
	public void detectConvexAndTransform() {
		BP bp = new BP(this.broadphaseFilter, this.aabbProducer, this.aabbExpansionMethod);
		
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createUnitCirclePolygon(5, 0.5));
		ct1.translate(-2.0, 0.0);
		ct2.translate(-1.0, 1.0);
		
		TestCase.assertTrue(bp.detect(ct1, ct2));
		TestCase.assertTrue(bp.detect(ct1.getFixture(0).getShape(), ct1.getTransform(), ct2.getFixture(0).getShape(), ct2.getTransform()));
		
		ct1.translate(-1.0, 0.0);
		TestCase.assertFalse(bp.detect(ct1, ct2));
		TestCase.assertFalse(bp.detect(ct1.getFixture(0).getShape(), ct1.getTransform(), ct2.getFixture(0).getShape(), ct2.getTransform()));
	}
}
