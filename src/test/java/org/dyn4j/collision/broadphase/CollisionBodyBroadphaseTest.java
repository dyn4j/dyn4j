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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.TestCollisionBody;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import junit.framework.TestCase;

/**
 * Class used to test the {@link BroadphaseDetector} methods.
 * @author William Bittle
 * @version 4.1.0
 * @since 3.0.0
 */
@RunWith(Parameterized.class)
public class CollisionBodyBroadphaseTest {
	private static final double EXPANSION = 0.2;
	
	/**
	 * We will be testing all broadphases one by one
	 * @return Collection&lt;Object[]&gt;
	 */
	@Parameters(name = "{0}")
    public static Collection<Object[]> data() {
    	
    	BroadphaseFilter<TestCollisionBody> broadphaseFilter = new CollisionBodyBroadphaseFilter<TestCollisionBody>();
    	AABBProducer<TestCollisionBody> aabbProducer = new CollisionBodyAABBProducer<TestCollisionBody>();
    	AABBExpansionMethod<TestCollisionBody> aabbExpansionMethod = new StaticValueAABBExpansionMethod<TestCollisionBody>(EXPANSION);
    	
    	// SAP without update tracking
    	BroadphaseDetector<TestCollisionBody> sap1 = new Sap<TestCollisionBody>(broadphaseFilter, aabbProducer, aabbExpansionMethod); 
    	sap1.setUpdateTrackingEnabled(false);
    	
    	// SAP with update tracking
    	BroadphaseDetector<TestCollisionBody> sap2 = new Sap<TestCollisionBody>(broadphaseFilter, aabbProducer, aabbExpansionMethod);
    	sap2.setUpdateTrackingEnabled(true);

    	// DynamicAABBTree without update tracking
    	BroadphaseDetector<TestCollisionBody> tree1 = new DynamicAABBTree<TestCollisionBody>(broadphaseFilter, aabbProducer, aabbExpansionMethod);
    	tree1.setUpdateTrackingEnabled(false);
    	
    	// DynamicAABBTree with update tracking
    	BroadphaseDetector<TestCollisionBody> tree2 = new DynamicAABBTree<TestCollisionBody>(broadphaseFilter, aabbProducer, aabbExpansionMethod);
    	tree2.setUpdateTrackingEnabled(true);
    	
    	// BruteForce
    	BroadphaseDetector<TestCollisionBody> bf1 = new BruteForceBroadphase<TestCollisionBody>(broadphaseFilter, aabbProducer);
    	
    	return Arrays.asList(
			new Object[] { sap1 },
			new Object[] { sap2 },
			new Object[] { tree1 },
			new Object[] { tree2 },
			new Object[] { bf1 }
		);
    }
	
    /** Stores the current broadphase being tested */
    protected BroadphaseDetector<TestCollisionBody> broadphase;
    
    /**
     * @param broadphase One broadphase instance to test
     */
    public CollisionBodyBroadphaseTest(BroadphaseDetector<TestCollisionBody> broadphase) {
    	this.broadphase = broadphase;
    }
    
	/**
	 * Sets up for each test method.
	 */
	@Before
	public void setup() {
		this.broadphase.clear();
	}
	
	/**
	 * Tests the add and contains methods.
	 */
	@Test
	public void addContainsBody() {
		TestCollisionBody ct = new TestCollisionBody(Geometry.createCircle(1.0));
		
		// make sure its not there first
		TestCase.assertEquals(0, this.broadphase.size());
		TestCase.assertFalse(this.broadphase.contains(ct));
		
		// add the item to the broadphases
		this.broadphase.add(ct);
		
		// make sure they are there
		TestCase.assertEquals(1, this.broadphase.size());
		TestCase.assertTrue(this.broadphase.contains(ct));
		
		ct.addFixture(Geometry.createCircle(0.5));
		
		// add the item again (should do an update)
		this.broadphase.add(ct);
		
		// make sure they are there
		TestCase.assertEquals(1, this.broadphase.size());
		TestCase.assertTrue(this.broadphase.contains(ct));
		
		// test the contains method with an empty body
		TestCase.assertFalse(this.broadphase.contains(new TestCollisionBody()));
		
		// test adding another
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(1.0));
		this.broadphase.add(ct2);
		
		// make sure they are there
		TestCase.assertEquals(2, this.broadphase.size());
		TestCase.assertTrue(this.broadphase.contains(ct));
		TestCase.assertTrue(this.broadphase.contains(ct2));
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.broadphase.add(ct1);
		this.broadphase.add(ct2);
		
		TestCase.assertEquals(2, this.broadphase.size());
		TestCase.assertTrue(this.broadphase.contains(ct1));
		TestCase.assertTrue(this.broadphase.contains(ct2));
		
		// clear all the broadphases
		this.broadphase.clear();
		
		// check for the aabb
		TestCase.assertEquals(0, this.broadphase.size());
	}
	
	/**
	 * Tests the update, isUpdated, and clearUpdates methods.
	 */
	@Test
	public void updateTargeted() {
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		ct1.addFixture(Geometry.createCircle(0.5));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.broadphase.add(ct1);
		this.broadphase.add(ct2);
		
		// this should always be true even if the broadphase doesn't support update tracking
		TestCase.assertTrue(this.broadphase.isUpdated(ct1));
		TestCase.assertTrue(this.broadphase.isUpdated(ct2));
		
		// clear all the updates
		this.broadphase.clearUpdates();
		
		if (this.broadphase.isUpdateTrackingEnabled()) {
			// the state will be cleared for those that have it enabled/support it
			TestCase.assertFalse(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
		} else {
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
		}
		
		// test updating only part of a collidable
		Fixture f = ct1.addFixture(Geometry.createCircle(0.25));
		f.getShape().translate(1.0, 1.0);
		this.broadphase.update(ct1);
		
		if (this.broadphase.isUpdateTrackingEnabled()) {
			// the state will be cleared for those that have it enabled/support it
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
		} else {
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
		}
		
		// clear all the updates
		this.broadphase.clearUpdates();
		
		if (this.broadphase.isUpdateTrackingEnabled()) {
			// the state will be cleared for those that have it enabled/support it
			TestCase.assertFalse(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
		} else {
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
		}
		
		// normal update
		ct1.translate(EXPANSION * 2.0, 0.0);
		this.broadphase.update(ct1);

		if (this.broadphase.isUpdateTrackingEnabled()) {
			// the state will be cleared for those that have it enabled/support it
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
		} else {
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
		}
		
		this.broadphase.clearUpdates();
		
		// test isUpdated for a body with no fixtures
		TestCase.assertFalse(this.broadphase.isUpdated(new TestCollisionBody()));
	}
	
	/**
	 * Tests the update, isUpdated, and clearUpdates methods.
	 */
	@Test
	public void update() {
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		ct1.addFixture(Geometry.createCircle(0.5));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.broadphase.add(ct1);
		this.broadphase.add(ct2);
		
		// this should always be true even if the broadphase doesn't support update tracking
		TestCase.assertTrue(this.broadphase.isUpdated(ct1));
		TestCase.assertTrue(this.broadphase.isUpdated(ct2));
		
		// clear all the updates
		this.broadphase.clearUpdates();
		
		if (this.broadphase.isUpdateTrackingEnabled()) {
			// the state will be cleared for those that have it enabled/support it
			TestCase.assertFalse(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
		} else {
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
		}
		
		// test updating only part of a collidable
		Fixture f = ct1.addFixture(Geometry.createCircle(0.25));
		f.getShape().translate(1.0, 1.0);
		this.broadphase.update();
		
		if (this.broadphase.isUpdateTrackingEnabled()) {
			// the state will be cleared for those that have it enabled/support it
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
		} else {
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
		}
		
		// clear all the updates
		this.broadphase.clearUpdates();
		
		if (this.broadphase.isUpdateTrackingEnabled()) {
			// the state will be cleared for those that have it enabled/support it
			TestCase.assertFalse(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
		} else {
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
		}
		
		// normal update
		ct1.translate(EXPANSION * 2.0, 0.0);
		this.broadphase.update();

		if (this.broadphase.isUpdateTrackingEnabled()) {
			// the state will be cleared for those that have it enabled/support it
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
		} else {
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
		}
		
		this.broadphase.clearUpdates();
		
		// test isUpdated for a body with no fixtures
		TestCase.assertFalse(this.broadphase.isUpdated(new TestCollisionBody()));
	}
	
	/**
	 * Tests the remove and contains methods.
	 */
	@Test
	public void removeContainsBody() {
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		ct1.addFixture(Geometry.createCircle(3.0));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(2.0));
		
		// add the item to the broadphases
		this.broadphase.add(ct1);
		this.broadphase.add(ct2);
		
		// make sure they are there
		TestCase.assertTrue(this.broadphase.contains(ct1));
		TestCase.assertTrue(this.broadphase.contains(ct2));
		
		// then remove them from the broadphases
		this.broadphase.remove(ct2);
		
		// make sure they aren't there any more
		TestCase.assertTrue(this.broadphase.contains(ct1));
		TestCase.assertFalse(this.broadphase.contains(ct2));

		// test remove with no fixtures (shouldn't throw or do anything really)
		this.broadphase.remove(new TestCollisionBody());
		
		TestCase.assertTrue(this.broadphase.contains(ct1));
		TestCase.assertFalse(this.broadphase.contains(ct2));
		
		// then remove them from the broadphases
		this.broadphase.remove(ct1);
		
		TestCase.assertFalse(this.broadphase.contains(ct1));
		TestCase.assertFalse(this.broadphase.contains(ct2));
		
		// remove non-existing
		
		TestCase.assertFalse(this.broadphase.remove(new TestCollisionBody(Geometry.createCircle(1.0))));
	}
	
	/**
	 * Tests the getAABB method.
	 */
	@Test
	public void getAABB() {
		AABBProducer<TestCollisionBody> aabbProducer = this.broadphase.getAABBProducer();
		AABBExpansionMethod<TestCollisionBody> aabbExpansionMethod = this.broadphase.getAABBExpansionMethod();
		
		TestCollisionBody ct = new TestCollisionBody(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.broadphase.add(ct);
		
		// test by body
		AABB bpAABB = this.broadphase.getAABB(ct);
		TestCase.assertNotNull(bpAABB);
		
		// account for expansion
		AABB expectedAABB = aabbProducer.compute(ct);
		aabbExpansionMethod.expand(ct, expectedAABB);
		
		TestCase.assertEquals(expectedAABB, bpAABB);
		
		// getAABB of no fixture body
		bpAABB = this.broadphase.getAABB(new TestCollisionBody());
		TestCase.assertNotNull(bpAABB);
		TestCase.assertTrue(bpAABB.isDegenerate());
		TestCase.assertEquals(0.0, bpAABB.getMaxX());
		TestCase.assertEquals(0.0, bpAABB.getMinX());
		TestCase.assertEquals(0.0, bpAABB.getMaxY());
		TestCase.assertEquals(0.0, bpAABB.getMinY());
		
		// getAABB of a multifixture body at the body level (not added)
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(1.0));
		ct2.addFixture(Geometry.createCircle(0.5));
		
		bpAABB = this.broadphase.getAABB(ct2);
		TestCase.assertNotNull(bpAABB);
		
		// account for expansion
		expectedAABB = aabbProducer.compute(ct2);
		aabbExpansionMethod.expand(ct2, expectedAABB);
		
		TestCase.assertEquals(expectedAABB, bpAABB);
	}

	/**
	 * Tests the get expansion methods.
	 */
	@Test
	public void getAABBExpansionMethod() {
		AABBExpansionMethod<TestCollisionBody> method = this.broadphase.getAABBExpansionMethod();
		TestCase.assertNotNull(method);
	}
	
	/**
	 * Tests the update method where the collidable moves very little.
	 */
	@Test
	public void updateSmall() {
		AABBExpansionMethod<?> method = this.broadphase.getAABBExpansionMethod();
		if (!(method instanceof NullAABBExpansionMethod)) {
			TestCollisionBody ct = new TestCollisionBody(Geometry.createCircle(1.0));
			
			// add the item to the broadphases
			this.broadphase.add(ct);
			this.broadphase.clearUpdates();
			
			// get the current aabb
			AABB aabb = this.broadphase.getAABB(ct).copy();
			
			// move the collidable a bit
			double dx = EXPANSION / 2.0;
			if (dx <= 0.0) {
				dx = 0.05;
			}
			ct.translate(dx, 0.0);
			
			// update the broadphases
			this.broadphase.update(ct);
			
			// the aabbs should not have been updated because of the expansion code
			TestCase.assertEquals(aabb, this.broadphase.getAABB(ct));
			
			// check for update
			if (this.broadphase.isUpdateTrackingEnabled()) {
				TestCase.assertFalse(this.broadphase.isUpdated(ct));
			}
		}
	}
	
	/**
	 * Tests the update method where the collidable moves enough to update the AABB.
	 */
	@Test
	public void updateLarge() {
		TestCollisionBody ct = new TestCollisionBody(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.broadphase.add(ct);
		
		// make sure they are there
		AABB aabb = this.broadphase.getAABB(ct).copy();
		
		// move the collidable enough so its AABB goes out of the expanded AABB
		double dx = EXPANSION * 2.0;
		if (dx <= 0.0) {
			dx = 1.0;
		}
		ct.translate(dx, 0.0);
		
		// update the broadphases
		this.broadphase.update(ct);
		
		// the aabbs should have been updated because the translation was large enough
		TestCase.assertFalse(aabb.equals(this.broadphase.getAABB(ct)));
		
		// this will always return true for some detectors, but for those that
		// it doesn't, it should return true because of the update above
		TestCase.assertTrue(this.broadphase.isUpdated(ct));
	}

	/**
	 * Tests setting the update tracking flag.
	 */
	@Test
	public void getSetUpdateTrackingEnabled() {
		if (this.broadphase.isUpdateTrackingSupported()) {
			this.broadphase.setUpdateTrackingEnabled(true);
			this.broadphase.setUpdateTrackingEnabled(false);
			TestCase.assertFalse(this.broadphase.isUpdateTrackingEnabled());
			this.broadphase.setUpdateTrackingEnabled(true);
			TestCase.assertTrue(this.broadphase.isUpdateTrackingEnabled());
		} else {
			TestCase.assertFalse(this.broadphase.isUpdateTrackingEnabled());
			this.broadphase.setUpdateTrackingEnabled(true);
			TestCase.assertFalse(this.broadphase.isUpdateTrackingEnabled());
		}
	}

	/**
	 * Tests the isUpdateTrackingSupported method.
	 */
	@Test
	public void isUpdateTrackingSupported() {
		BroadphaseDetector<TestCollisionBody> detector = this.broadphase;
		if (detector instanceof DynamicAABBTree ||
			detector instanceof Sap) {
			TestCase.assertTrue(this.broadphase.isUpdateTrackingSupported());
		} else {
			TestCase.assertFalse(this.broadphase.isUpdateTrackingSupported());
		}
	}
	
	/**
	 * Tests that the optimize method doesn't throw.
	 */
	@Test
	public void optimize() {
		// optimizing an empty broadphase should work
		this.broadphase.optimize();
		
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		ct1.addFixture(Geometry.createCircle(3.0));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(2.0));
		
		// add the item to the broadphases
		this.broadphase.add(ct1);
		this.broadphase.add(ct2);
		
		this.broadphase.optimize();
	}
	
	/**
	 * Tests the setUpdated methods.
	 */
	@Test
	public void setUpdated() {
		if (this.broadphase.isUpdateTrackingSupported() && this.broadphase.isUpdateTrackingEnabled()) {
			TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
			ct1.addFixture(Geometry.createCircle(0.5));
			TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(1.0));
			
			// add the item to the broadphases
			this.broadphase.add(ct1);
			this.broadphase.add(ct2);
			
			// this should always be true even if the broadphase doesn't support update tracking
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
			
			this.broadphase.clearUpdates();
			
			TestCase.assertFalse(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
			
			this.broadphase.setUpdated(ct1);
			
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
			
			this.broadphase.clearUpdates();
			this.broadphase.setUpdated(ct2);
			
			TestCase.assertFalse(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
		} else {
			// if it doesn't support update tracking then these methods should be no-ops
			
			TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
			ct1.addFixture(Geometry.createCircle(0.5));
			TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(1.0));
			
			// add the item to the broadphases
			this.broadphase.add(ct1);
			this.broadphase.add(ct2);
			
			// this should always be true even if the broadphase doesn't support update tracking
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
			
			this.broadphase.clearUpdates();
			this.broadphase.setUpdated(ct1);
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			
			// even if we clear all the updates, both should report that they were updated
			this.broadphase.clearUpdates();
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
		}
	}
	
	/**
	 * Tests the size method.
	 */
	@Test
	public void size() {
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		ct1.addFixture(Geometry.createCircle(0.5));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(1.0));
		
		TestCase.assertEquals(0, this.broadphase.size());
		
		// add the item to the broadphases
		this.broadphase.add(ct1);
		this.broadphase.add(ct2);
		
		// the size is a function of the number of fixtures
		TestCase.assertEquals(2, this.broadphase.size());
		
		this.broadphase.remove(ct1);

		TestCase.assertEquals(1, this.broadphase.size());
		
		this.broadphase.clear();
		
		TestCase.assertEquals(0, this.broadphase.size());
	}
	
	/**
	 * Tests the {@link AbstractBroadphaseDetector} detect methods.
	 * @since 3.1.0
	 */
	@Test
	public void detectConvexAndTransform() {
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createUnitCirclePolygon(5, 0.5));
		ct1.translate(-2.0, 0.0);
		ct2.translate(-1.0, 1.0);
		
		TestCase.assertTrue(this.broadphase.detect(ct1, ct2));
		TestCase.assertTrue(this.broadphase.detect(ct1.getFixture(0).getShape(), ct1.getTransform(), ct2.getFixture(0).getShape(), ct2.getTransform()));
		
		ct1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.broadphase.detect(ct1, ct2));
		TestCase.assertFalse(this.broadphase.detect(ct1.getFixture(0).getShape(), ct1.getTransform(), ct2.getFixture(0).getShape(), ct2.getTransform()));
	}

	/**
	 * Tests the detectIterator method with failures.
	 * @since 3.1.0
	 */
	@Test
	public void detectIteratorFailure() {
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createUnitCirclePolygon(5, 0.5));
		TestCollisionBody ct3 = new TestCollisionBody(Geometry.createRectangle(1.0, 0.5));
		TestCollisionBody ct4 = new TestCollisionBody(Geometry.createVerticalSegment(2.0));
		
		ct1.translate(-2.0, 0.0);
		ct2.translate(-1.0, 1.0);
		ct3.translate(0.5, -2.0);
		ct4.translate(1.0, 1.0);
		
		// add the items to the broadphases
		this.broadphase.add(ct1); 
		this.broadphase.add(ct2); 
		this.broadphase.add(ct3); 
		this.broadphase.add(ct4);
		
		Iterator<CollisionPair<TestCollisionBody>> it = this.broadphase.detectIterator(false);
		TestCase.assertTrue(it.hasNext());
		try {
			it.next();
			it.next();
			TestCase.fail();
		} catch (NoSuchElementException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		it = this.broadphase.detectIterator(false);
		try {
			it.next();
			it.remove();
		} catch (UnsupportedOperationException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
	}
	
	/**
	 * Tests the raycastIterator method with failures.
	 * @since 3.1.0
	 */
	@Test
	public void raycastIteratorFailure() {
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createUnitCirclePolygon(5, 0.5));
		TestCollisionBody ct3 = new TestCollisionBody(Geometry.createRectangle(1.0, 0.5));
		TestCollisionBody ct4 = new TestCollisionBody(Geometry.createVerticalSegment(2.0));
		
		ct1.translate(-2.0, 0.0);
		ct2.translate(-1.0, 1.0);
		ct3.translate(0.5, -2.0);
		ct4.translate(1.0, 1.0);
		
		// add the items to the broadphases
		this.broadphase.add(ct1); 
		this.broadphase.add(ct2); 
		this.broadphase.add(ct3); 
		this.broadphase.add(ct4);
		
		Ray ray = new Ray(new Vector2(-2.0, -2.0), (new Vector2(1.0, 1.0).getNormalized()));
		Iterator<TestCollisionBody> it = this.broadphase.raycastIterator(ray, 3.0);
		TestCase.assertTrue(it.hasNext());
		try {
			it.next();
			it.next();
			TestCase.fail();
		} catch (NoSuchElementException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		it = this.broadphase.raycastIterator(ray, 0.0);
		try {
			it.next();
			it.remove();
		} catch (UnsupportedOperationException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
	}

	/**
	 * Tests the detectAABBIterator method with failure cases.
	 */
	@Test
	public void detectAABBIteratorFailure() {
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createUnitCirclePolygon(5, 0.5));
		TestCollisionBody ct3 = new TestCollisionBody(Geometry.createRectangle(1.0, 0.5));
		TestCollisionBody ct4 = new TestCollisionBody(Geometry.createVerticalSegment(2.0));
		
		ct1.translate(-2.0, 0.0);
		ct2.translate(-1.0, 1.0);
		ct3.translate(0.5, -2.0);
		ct4.translate(1.0, 1.0);
		
		// add the items to the broadphases
		this.broadphase.add(ct1); 
		this.broadphase.add(ct2); 
		this.broadphase.add(ct3); 
		this.broadphase.add(ct4);
		
		// this aabb should include:
		// ct3 and ct4
		AABB aabb = new AABB(0.0, -2.0, 1.0, 1.0);
		
		Iterator<TestCollisionBody> it = this.broadphase.detectIterator(aabb);
		
		TestCase.assertTrue(it.hasNext());
		try {
			it.next();
			it.next();
			it.next();
			TestCase.fail();
		} catch (NoSuchElementException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
		
		it = this.broadphase.detectIterator(aabb);
		try {
			it.next();
			it.remove();
		} catch (UnsupportedOperationException ex) {
		} catch (Exception ex) {
			TestCase.fail();
		}
	}
	
	/**
	 * Tests the iterator methods with empty results.
	 */
	@Test
	public void detectEmptyIterator() {
		// normal detection
		Iterator<CollisionPair<TestCollisionBody>> itPairs = this.broadphase.detectIterator(true);
		TestCase.assertNotNull(itPairs);
		TestCase.assertFalse(itPairs.hasNext());
		
		// aabb
		AABB aabb = new AABB(0.0, -2.0, 1.0, 1.0);
		Iterator<TestCollisionBody> itAABB = this.broadphase.detectIterator(aabb);
		TestCase.assertNotNull(itAABB);
		TestCase.assertFalse(itAABB.hasNext());
		
		// raycast
		Ray ray = new Ray(new Vector2(-2.0, -2.0), (new Vector2(1.0, 1.0).getNormalized()));
		Iterator<TestCollisionBody> itRaycast = this.broadphase.raycastIterator(ray, 3.0);
		TestCase.assertNotNull(itRaycast);
		TestCase.assertFalse(itRaycast.hasNext());
	}
	
	/**
	 * Tests the detect method.
	 * @since 3.1.0
	 */
	@Test
	public void detect() {
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createUnitCirclePolygon(5, 0.5));
		TestCollisionBody ct3 = new TestCollisionBody(Geometry.createRectangle(1.0, 0.5));
		TestCollisionBody ct4 = new TestCollisionBody(Geometry.createVerticalSegment(2.0));
		
		ct1.translate(-2.0, 0.0);
		ct2.translate(-1.0, 1.0);
		ct3.translate(0.5, -2.0);
		ct4.translate(1.0, 1.0);
		
		// add the items to the broadphases
		this.broadphase.add(ct1); this.broadphase.add(ct2); this.broadphase.add(ct3); this.broadphase.add(ct4);
		
		List<CollisionPair<TestCollisionBody>> pairs = this.broadphase.detect();
		TestCase.assertEquals(1, pairs.size());
	}
	
	/**
	 * Tests the detect method using an AABB.
	 */
	@Test
	public void detectAABB() {
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createUnitCirclePolygon(5, 0.5));
		TestCollisionBody ct3 = new TestCollisionBody(Geometry.createRectangle(1.0, 0.5));
		TestCollisionBody ct4 = new TestCollisionBody(Geometry.createVerticalSegment(2.0));
		
		ct1.translate(-2.0, 0.0);
		ct2.translate(-1.0, 1.0);
		ct3.translate(0.5, -2.0);
		ct4.translate(1.0, 1.0);
		
		// add the items to the broadphases
		this.broadphase.add(ct1); this.broadphase.add(ct2); this.broadphase.add(ct3); this.broadphase.add(ct4);
		
		// this aabb should include:
		// ct3 and ct4
		AABB aabb = new AABB(0.0, -2.0, 1.0, 1.0);
		List<TestCollisionBody> list;
		
		list = this.broadphase.detect(aabb);
		TestCase.assertEquals(2, list.size());
		TestCase.assertTrue(this.containsItem(ct3, list));
		TestCase.assertTrue(this.containsItem(ct4, list));
		
		// should include:
		// ct2, ct3, and ct4
		aabb = new AABB(-0.75, -3.0, 2.0, 1.0);
		
		list = this.broadphase.detect(aabb);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(this.containsItem(ct2, list));
		TestCase.assertTrue(this.containsItem(ct3, list));
		TestCase.assertTrue(this.containsItem(ct4, list));
	}
	
	/**
	 * Tests the raycast method.
	 */
	@Test
	public void detectRay() {
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createUnitCirclePolygon(5, 0.5));
		TestCollisionBody ct3 = new TestCollisionBody(Geometry.createRectangle(1.0, 0.5));
		TestCollisionBody ct4 = new TestCollisionBody(Geometry.createVerticalSegment(2.0));
		
		ct1.translate(-2.0, 0.0);
		ct2.translate(-1.0, 1.0);
		ct3.translate(0.5, -2.0);
		ct4.translate(1.0, 1.2);
		
		// add the items to the broadphases
		this.broadphase.add(ct1); 
		this.broadphase.add(ct2); 
		this.broadphase.add(ct3); 
		this.broadphase.add(ct4);
		
		List<TestCollisionBody> list;
		
		// ray that points in the positive x direction and starts at the origin
		Ray r = new Ray(new Vector2(1.0, 0.0));
		// infinite length
		double l = 0.0;
		
		list = this.broadphase.raycast(r, l);
		TestCase.assertEquals(0, list.size());
		
		// try a different ray
		r = new Ray(new Vector2(-3.0, 0.75), new Vector2(1.0, 0.0));
		list = this.broadphase.raycast(r, l);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(this.containsItem(ct1, list));
		TestCase.assertTrue(this.containsItem(ct2, list));
		TestCase.assertTrue(this.containsItem(ct4, list));
		
		// try one more ray
		r = new Ray(new Vector2(-3.0, -2.0), new Vector2(1.0, 2.0).getNormalized());
		list = this.broadphase.raycast(r, l);
		TestCase.assertEquals(2, list.size());
		TestCase.assertTrue(this.containsItem(ct1, list));
		TestCase.assertTrue(this.containsItem(ct2, list));
		
		// try a shorter ray
		l = 0.4;
		r = new Ray(new Vector2(-2.0, -1.5), new Vector2(1.0, 1.0).getNormalized());
		list = this.broadphase.raycast(r, l);
		TestCase.assertEquals(0, list.size());
		
		// try in the opposite direction
		l = 0.0;
		r = new Ray(new Vector2(-5.0, -3.0), new Vector2(-1.0, -2.0).getNormalized());
		list = this.broadphase.raycast(r, l);
		TestCase.assertEquals(0, list.size());
	}
	
	/**
	 * Tests the shiftCoordinates method.
	 */
	@Test
	public void shiftCoordinates() {
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createUnitCirclePolygon(5, 0.5));
		TestCollisionBody ct3 = new TestCollisionBody(Geometry.createRectangle(1.0, 0.5));
		TestCollisionBody ct4 = new TestCollisionBody(Geometry.createVerticalSegment(2.0));
		
		ct1.translate(-2.0, 0.0);
		ct2.translate(-1.0, 1.0);
		ct3.translate(0.5, -2.0);
		ct4.translate(1.0, 1.0);
		
		// add the items to the broadphases
		this.broadphase.add(ct1); this.broadphase.add(ct2); this.broadphase.add(ct3); this.broadphase.add(ct4);
		
		// perform a detect on the whole broadphase
		List<CollisionPair<TestCollisionBody>> pairs = this.broadphase.detect();
		TestCase.assertEquals(1, pairs.size());
		
		// shift the broadphases
		Vector2 shift = new Vector2(1.0, -2.0);
		this.broadphase.shift(shift);
		
		// the number of pairs detected should be identical
		pairs = this.broadphase.detect();
		TestCase.assertEquals(1, pairs.size());
	}
	
	/**
	 * Tests the getBroadphaseFilter method.
	 */
	@Test
	public void getBroadphaseFilter() {
		BroadphaseFilter<TestCollisionBody> filter = this.broadphase.getBroadphaseFilter();
		TestCase.assertNotNull(filter);
	}
	
	/** Seed for the randomized test. Can be any value */
	private static final int SEED = 0;
	
	/**
	 * Deterministic randomized test, used to test various core functionalities of all broad-phase detectors, simulating some complex scenarios.
	 * This test uses a {@link BruteForceBroadphase} for reference and tests the output of the broad-phase against the reference broad-phase.
	 * For all queries (detect, detect(AABB) and raycast) the output of the broad-phase must contain all pairs/items returned by the reference broad-phase (and maybe some more),
	 * because {@link BruteForceBroadphase} always returns the minimal answer set.
	 * <p>
	 * The {@link BruteForceBroadphase} implements only simple brute force algorithms so we can consider it's implementation bug free.
	 */
	@Test
	public void randomizedTest() {
		// The reference broad-phase is bruteforce
		BroadphaseDetector<TestCollisionBody> reference = new BruteForceBroadphase<TestCollisionBody>(
				new CollisionBodyBroadphaseFilter<TestCollisionBody>(),
				new CollisionBodyAABBProducer<TestCollisionBody>());
		
		List<TestCollisionBody> collidables = new ArrayList<TestCollisionBody>();
		
		// Constant seed so we always get the same sequence of randoms
		Random random = new Random(SEED);
		
		// Pick some iterations and query count
		final int iterations = 100;
		final int queries = 10;
		
		for (int i = 0; i < iterations; i++) {
			// Create a random rectangle
			Rectangle randomRectangle = Geometry.createRectangle(random.nextDouble() + 0.1, random.nextDouble() + 0.1);
			TestCollisionBody collidable = new TestCollisionBody(randomRectangle);
			
			// And apply a random translation
			collidable.translate(random.nextDouble() * 10 - 5, random.nextDouble() * 10 - 5);
			
			// Add the new collidable to both broadphases
			collidables.add(collidable);
			this.broadphase.add(collidable);
			reference.add(collidable);
			
			// Also remove one existing collidable with 25% chance
			if (random.nextDouble() <= 0.25) {
				TestCollisionBody forRemoval = collidables.remove(random.nextInt(collidables.size()));
				
				this.broadphase.remove(forRemoval);
				reference.remove(forRemoval);
			}
			
			// Now start querying
			// First test detect
			List<CollisionPair<TestCollisionBody>> referenceDetect = reference.detect();
			List<CollisionPair<TestCollisionBody>> otherDetect = this.broadphase.detect();
			
			// The pairs returned from {@link PlainBroadphase} are the minimum possible
			// if any of those is missing from the broadphase being tested, something is wrong
			for (CollisionPair<TestCollisionBody> pair : referenceDetect) {
				// Be careful to have correct pair equality here. See pairExists
				if (!containsPair(pair, otherDetect)) {
					TestCase.fail("detect() is missing pairs");
				}
			}
			
			// Now test detect against 10 random AABBs
			for (int d = 0; d < queries; d++) {
				// Generate a random AABB inside the rectangle [(-5, -5), (5, -5), (5, 5), (-5, 5)]
				double aabbWidth = random.nextDouble() * 9.5 + 0.5;
				double aabbHeight = random.nextDouble() * 9.5 + 0.5;
				double aabbX = -5 + random.nextDouble() * (10 - aabbWidth);
				double aabbY = -5 + random.nextDouble() * (10 - aabbHeight);
				
				AABB aabb = new AABB(aabbX, aabbY, aabbX + aabbWidth, aabbY + aabbHeight);
				List<TestCollisionBody> referenceAABBDetect = reference.detect(aabb);
				List<TestCollisionBody> otherAABBDetect = this.broadphase.detect(aabb);
				
				// Again, because the items returned from {@link PlainBroadphase} are the minimum possible
				// if any of those are missing from the broadphase being tested, something is wrong
				for (TestCollisionBody item : referenceAABBDetect) {
					// Since we don't have pairs here we can rely on BroadphaseItem#equals
					if (!otherAABBDetect.contains(item)) {
						TestCase.fail("detect(AABB) is missing items");
					}
				}
			}
			
			// and 10 random Rays
			for (int d = 0; d < queries; d++) {
				//choose either an infinite or finite ray with 50% chance
				double rayLength = (random.nextBoolean())? (random.nextDouble() * 10) : 0.0;
				
				// Generate a random starting point in the interval [-5, -5)
				Vector2 start = new Vector2(random.nextDouble() * 10 - 5, random.nextDouble() * 10 - 5);
				Ray randomRay = new Ray(start, random.nextDouble() * Geometry.TWO_PI);
				
				List<TestCollisionBody> referenceRaycast = reference.raycast(randomRay, rayLength);
				List<TestCollisionBody> otherRaycast = this.broadphase.raycast(randomRay, rayLength);
				
				for (TestCollisionBody item : referenceRaycast) {
					if (!otherRaycast.contains(item)) {
						TestCase.fail("raycast() is missing items");
					}
				}
			}
		}
	}
	
	/**
	 * Simple helper method that finds if the given {@link CollisionBody} and {@link Fixture} is
	 * contained in a list of {@link CollisionItem}s.
	 * @param collidable the body
	 * @param fixture the fixture
	 * @param items the items
	 * @return boolean
	 */
	private boolean containsItem(TestCollisionBody collidable, List<TestCollisionBody> items) {
		for (TestCollisionBody item : items) {
			if (item == collidable) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Simple helper method that finds if a {@link CollisionPair} or it's reverse pair is
	 * contained in a list of {@link CollisionPair}s.
	 * 
	 * @param pair The pair to search
	 * @param pairs The list of pairs
	 * @return true if pair or it's reverse is contained in the list, false otherwise
	 */
	private boolean containsPair(CollisionPair<TestCollisionBody> pair, List<CollisionPair<TestCollisionBody>> pairs) {
		for (CollisionPair<TestCollisionBody> test : pairs) {
			if (pair.equals(test)) {
				return true;
			}
		}
		
		return false;
	}
}