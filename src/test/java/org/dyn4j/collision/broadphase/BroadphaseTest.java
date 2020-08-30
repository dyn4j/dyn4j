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

import org.dyn4j.collision.BasicCollisionItem;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.TestCollisionBody;
import org.dyn4j.collision.broadphase.AbstractBroadphaseDetector;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.BruteForceBroadphase;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.broadphase.LazyAABBTree;
import org.dyn4j.collision.broadphase.Sap;
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
 * @version 4.0.0
 * @since 3.0.0
 */
@RunWith(Parameterized.class)
@SuppressWarnings("deprecation")
public class BroadphaseTest {
	/**
	 * We will be testing all broadphases one by one
	 * @return Collection&lt;Object[]&gt;
	 */
	@Parameters(name = "{0}")
    public static Collection<Object[]> data() {
    	
    	BroadphaseDetector<TestCollisionBody, Fixture> sap1 = new Sap<TestCollisionBody, Fixture>(); sap1.setUpdateTrackingEnabled(false);
    	BroadphaseDetector<TestCollisionBody, Fixture> sap2 = new Sap<TestCollisionBody, Fixture>(); sap2.setUpdateTrackingEnabled(true);
    	BroadphaseDetector<TestCollisionBody, Fixture> tree1 = new DynamicAABBTree<TestCollisionBody, Fixture>(); tree1.setUpdateTrackingEnabled(false);
    	BroadphaseDetector<TestCollisionBody, Fixture> tree2 = new DynamicAABBTree<TestCollisionBody, Fixture>(); tree2.setUpdateTrackingEnabled(true);
    	BroadphaseDetector<TestCollisionBody, Fixture> bf1 = new BruteForceBroadphase<TestCollisionBody, Fixture>();
    	BroadphaseDetector<TestCollisionBody, Fixture> lazy1 = new LazyAABBTree<TestCollisionBody, Fixture>();
    	
    	return Arrays.asList(
			new Object[] { sap1 },
			new Object[] { sap2 },
			new Object[] { tree1 },
			new Object[] { tree2 },
			new Object[] { bf1 },
			new Object[] { lazy1 }
		);
    }
	
    /** Stores the current broadphase being tested */
    protected BroadphaseDetector<TestCollisionBody, Fixture> broadphase;
    
    /**
     * @param broadphase One broadphase instance to test
     */
    public BroadphaseTest(BroadphaseDetector<TestCollisionBody, Fixture> broadphase) {
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
		BasicCollisionItem<TestCollisionBody, Fixture> item = new BasicCollisionItem<TestCollisionBody, Fixture>(ct, ct.getFixture(0));
		
		// make sure its not there first
		TestCase.assertEquals(0, this.broadphase.size());
		TestCase.assertFalse(this.broadphase.contains(ct));
		TestCase.assertFalse(this.broadphase.contains(item));
		
		// add the item to the broadphases
		this.broadphase.add(ct);
		
		// make sure they are there
		TestCase.assertEquals(1, this.broadphase.size());
		TestCase.assertTrue(this.broadphase.contains(ct));
		TestCase.assertTrue(this.broadphase.contains(ct, ct.getFixture(0)));
		TestCase.assertTrue(this.broadphase.contains(item));
		
		ct.addFixture(Geometry.createCircle(0.5));
		
		// add the item again (should do an update)
		this.broadphase.add(ct);
		
		// make sure they are there
		TestCase.assertEquals(2, this.broadphase.size());
		TestCase.assertTrue(this.broadphase.contains(ct));
		TestCase.assertTrue(this.broadphase.contains(ct, ct.getFixture(0)));
		TestCase.assertTrue(this.broadphase.contains(ct, ct.getFixture(1)));
		TestCase.assertTrue(this.broadphase.contains(item));
		
		// test the contains method with an empty body
		TestCase.assertFalse(this.broadphase.contains(new TestCollisionBody()));
	}
	
	/**
	 * Tests the add method with fixtures.
	 */
	@Test
	public void addContainsFixture() {
		TestCollisionBody ct = new TestCollisionBody(Geometry.createCircle(1.0));
		ct.addFixture(Geometry.createCircle(0.5));
		
		// make sure its not there first
		TestCase.assertEquals(0, this.broadphase.size());
		TestCase.assertFalse(this.broadphase.contains(ct));
		
		// add the item to the broadphases
		this.broadphase.add(ct, ct.getFixture(0));
		this.broadphase.add(ct, ct.getFixture(1));
		
		// make sure they are there
		TestCase.assertEquals(2, this.broadphase.size());
		TestCase.assertTrue(this.broadphase.contains(ct));
		TestCase.assertTrue(this.broadphase.contains(ct, ct.getFixture(0)));
		TestCase.assertTrue(this.broadphase.contains(ct, ct.getFixture(1)));
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
		TestCase.assertTrue(this.broadphase.contains(ct1, ct1.getFixture(0)));
		TestCase.assertTrue(this.broadphase.contains(ct2));
		TestCase.assertTrue(this.broadphase.contains(ct2, ct2.getFixture(0)));
		
		// clear all the broadphases
		this.broadphase.clear();
		
		// check for the aabb
		TestCase.assertEquals(0, this.broadphase.size());
	}
	
	/**
	 * Tests the update, isUpdated, and clearUpdates methods.
	 */
	@Test
	public void update() {
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		ct1.addFixture(Geometry.createCircle(0.5));
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(1.0));
		
		BasicCollisionItem<TestCollisionBody, Fixture> item = new BasicCollisionItem<TestCollisionBody, Fixture>(ct1, ct1.getFixture(0));
		
		// add the item to the broadphases
		this.broadphase.add(ct1);
		this.broadphase.add(ct2);
		
		// this should always be true even if the broadphase doesn't support update tracking
		TestCase.assertTrue(this.broadphase.isUpdated(ct1));
		TestCase.assertTrue(this.broadphase.isUpdated(ct2));
		TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
		TestCase.assertTrue(this.broadphase.isUpdated(item));
		TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
		TestCase.assertTrue(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
		
		// clear all the updates
		this.broadphase.clearUpdates();
		
		if (this.broadphase.isUpdateTrackingEnabled()) {
			// the state will be cleared for those that have it enabled/support it
			TestCase.assertFalse(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
			TestCase.assertFalse(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertFalse(this.broadphase.isUpdated(item));
			TestCase.assertFalse(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
		} else {
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertTrue(this.broadphase.isUpdated(item));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
		}
		
		// test updating only part of a collidable
		ct1.addFixture(Geometry.createCircle(0.25));
		this.broadphase.update(ct1);
		
		if (this.broadphase.isUpdateTrackingEnabled()) {
			// the state will be cleared for those that have it enabled/support it
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
			TestCase.assertFalse(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertFalse(this.broadphase.isUpdated(item));
			TestCase.assertFalse(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(2)));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
		} else {
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertTrue(this.broadphase.isUpdated(item));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(2)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
		}
		
		// clear all the updates
		this.broadphase.clearUpdates();
		
		if (this.broadphase.isUpdateTrackingEnabled()) {
			// the state will be cleared for those that have it enabled/support it
			TestCase.assertFalse(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
			TestCase.assertFalse(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertFalse(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertFalse(this.broadphase.isUpdated(ct1, ct1.getFixture(2)));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
		} else {
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(2)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
		}
		
		// normal update
		double expansion = 0.0;
		if (this.broadphase.isAABBExpansionSupported()) {
			expansion = this.broadphase.getAABBExpansion();
		}
		ct1.translate(expansion * 2.0, 0.0);
		this.broadphase.update(ct1);

		if (this.broadphase.isUpdateTrackingEnabled()) {
			// the state will be cleared for those that have it enabled/support it
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(2)));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
		} else {
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(2)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
		}
		
		// update a single fixture
		this.broadphase.clearUpdates();
		
		ct1.translate(expansion * 2.0, 0.0);
		this.broadphase.update(ct1, ct1.getFixture(0));
		
		if (this.broadphase.isUpdateTrackingEnabled()) {
			// the state will be cleared for those that have it enabled/support it
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertFalse(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertFalse(this.broadphase.isUpdated(ct1, ct1.getFixture(2)));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
		} else {
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(2)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
		}
		
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
		TestCase.assertTrue(this.broadphase.contains(ct1, ct1.getFixture(0)));
		TestCase.assertTrue(this.broadphase.contains(ct1, ct1.getFixture(1)));
		TestCase.assertFalse(this.broadphase.contains(ct2));
		TestCase.assertFalse(this.broadphase.contains(ct2, ct2.getFixture(0)));

		// test remove with no fixtures (shouldn't throw or do anything really)
		this.broadphase.remove(new TestCollisionBody());
		
		TestCase.assertTrue(this.broadphase.contains(ct1));
		TestCase.assertTrue(this.broadphase.contains(ct1, ct1.getFixture(0)));
		TestCase.assertTrue(this.broadphase.contains(ct1, ct1.getFixture(1)));
		TestCase.assertFalse(this.broadphase.contains(ct2));
		TestCase.assertFalse(this.broadphase.contains(ct2, ct2.getFixture(0)));
		
		// then remove them from the broadphases
		this.broadphase.remove(ct1);
		
		TestCase.assertFalse(this.broadphase.contains(ct1));
		TestCase.assertFalse(this.broadphase.contains(ct1, ct1.getFixture(0)));
		TestCase.assertFalse(this.broadphase.contains(ct1, ct1.getFixture(1)));
		TestCase.assertFalse(this.broadphase.contains(ct2));
		TestCase.assertFalse(this.broadphase.contains(ct2, ct2.getFixture(0)));
		
		// remove non-existing
		
		TestCase.assertFalse(this.broadphase.remove(new BasicCollisionItem<TestCollisionBody, Fixture>(ct1, new Fixture(Geometry.createCircle(0.5)))));
	}
	
	/**
	 * Tests the add method with fixtures.
	 */
	@Test
	public void removeContainsFixture() {
		TestCollisionBody ct = new TestCollisionBody(Geometry.createCircle(1.0));
		ct.addFixture(Geometry.createCircle(0.5));
		ct.addFixture(Geometry.createCircle(0.25));
		
		CollisionItem<TestCollisionBody, Fixture> item = new BasicCollisionItem<TestCollisionBody, Fixture>(ct, ct.getFixture(2));
		
		// add the item to the broadphases
		this.broadphase.add(ct);
		
		// make sure they are there
		TestCase.assertTrue(this.broadphase.contains(ct));
		TestCase.assertTrue(this.broadphase.contains(ct, ct.getFixture(0)));
		TestCase.assertTrue(this.broadphase.contains(ct, ct.getFixture(1)));
		TestCase.assertTrue(this.broadphase.contains(item));
		
		this.broadphase.remove(ct, ct.getFixture(1));
		
		TestCase.assertFalse(this.broadphase.contains(ct));
		TestCase.assertTrue(this.broadphase.contains(ct, ct.getFixture(0)));
		TestCase.assertFalse(this.broadphase.contains(ct, ct.getFixture(1)));
		TestCase.assertTrue(this.broadphase.contains(item));
		
		ct.removeFixture(1);
		TestCase.assertTrue(this.broadphase.contains(ct));
		
		// test remove via CollisionItem
		this.broadphase.remove(item);
		
		TestCase.assertFalse(this.broadphase.contains(ct));
		TestCase.assertTrue(this.broadphase.contains(ct, ct.getFixture(0)));
		TestCase.assertFalse(this.broadphase.contains(item));
		
		ct.removeFixture(1);
		
		TestCase.assertTrue(this.broadphase.contains(ct));
		TestCase.assertTrue(this.broadphase.contains(ct, ct.getFixture(0)));
		TestCase.assertFalse(this.broadphase.contains(item));
		
		// remove something that doesn't exist
		TestCase.assertFalse(this.broadphase.remove(new TestCollisionBody(), ct.getFixture(0)));
	}

	/**
	 * Tests the getAABB method.
	 */
	@Test
	public void getAABB() {
		TestCollisionBody ct = new TestCollisionBody(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.broadphase.add(ct);
		
		double expansion = 0.0;
		if (this.broadphase.isAABBExpansionSupported()) {
			expansion = this.broadphase.getAABBExpansion();
		}
		
		// test by body
		AABB bpAABB = this.broadphase.getAABB(ct);
		TestCase.assertNotNull(bpAABB);
		
		// account for expansion
		AABB expectedAABB = ct.createAABB();
		expectedAABB.expand(expansion);
		
		TestCase.assertEquals(expectedAABB, bpAABB);
		
		// test by fixture
		
		bpAABB = this.broadphase.getAABB(ct, ct.getFixture(0));
		TestCase.assertNotNull(bpAABB);
		
		// account for expansion
		expectedAABB = ct.getFixture(0).getShape().createAABB(ct.getTransform());
		expectedAABB.expand(expansion);
		
		TestCase.assertEquals(expectedAABB, bpAABB);
		
		// test by CollisionItem
		
		bpAABB = this.broadphase.getAABB(new BasicCollisionItem<TestCollisionBody, Fixture>(ct, ct.getFixture(0)));
		TestCase.assertNotNull(bpAABB);
		
		// account for expansion
		expectedAABB = ct.getFixture(0).getShape().createAABB(ct.getTransform());
		expectedAABB.expand(expansion);
		
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
		TestCase.assertEquals(1.0 + expansion * 0.5, bpAABB.getMaxX());
		TestCase.assertEquals(-1.0 - expansion * 0.5, bpAABB.getMinX());
		TestCase.assertEquals(1.0 + expansion * 0.5, bpAABB.getMaxY());
		TestCase.assertEquals(-1.0 - expansion * 0.5, bpAABB.getMinY());
		
		// getAABB of a multifixture body at the body level (added)
		this.broadphase.add(ct2);
		
		bpAABB = this.broadphase.getAABB(ct2);
		
		TestCase.assertNotNull(bpAABB);
		TestCase.assertEquals(1.0 + expansion * 0.5, bpAABB.getMaxX());
		TestCase.assertEquals(-1.0 - expansion * 0.5, bpAABB.getMinX());
		TestCase.assertEquals(1.0 + expansion * 0.5, bpAABB.getMaxY());
		TestCase.assertEquals(-1.0 - expansion * 0.5, bpAABB.getMinY());
	}

	/**
	 * Tests the get/set expansion methods.
	 */
	@Test
	public void getSetAABBExpansion() {
		if (this.broadphase.isAABBExpansionSupported()) {
			// test the default
			TestCase.assertEquals(BroadphaseDetector.DEFAULT_AABB_EXPANSION, this.broadphase.getAABBExpansion());
			
			// test changing the expansion
			this.broadphase.setAABBExpansion(0.3);
			TestCase.assertEquals(0.3, this.broadphase.getAABBExpansion());
			
			// test the new expansion value
			TestCollisionBody ct = new TestCollisionBody(Geometry.createCircle(1.0));
			
			// add the item to the broadphases
			this.broadphase.add(ct);
			
			AABB bpAABB = this.broadphase.getAABB(ct);
			
			AABB aabb = ct.createAABB();
			// don't forget that the aabb is expanded
			aabb.expand(0.3);
			TestCase.assertEquals(aabb, bpAABB);
		}
	}
	
	/**
	 * Tests the isAABBExpansionSupported methods.
	 */
	@Test
	public void isAABBExpansionSupported() {
		if (this.broadphase instanceof DynamicAABBTree ||
			this.broadphase instanceof Sap) {
			TestCase.assertTrue(this.broadphase.isAABBExpansionSupported());
		} else {
			TestCase.assertFalse(this.broadphase.isAABBExpansionSupported());
		}
	}
	
	/**
	 * Tests the update method where the collidable moves very little.
	 */
	@Test
	public void updateSmall() {
		if (this.broadphase.isAABBExpansionSupported()) {
			TestCollisionBody ct = new TestCollisionBody(Geometry.createCircle(1.0));
			Fixture f = ct.getFixture(0);
			
			// add the item to the broadphases
			this.broadphase.add(ct);
			this.broadphase.clearUpdates();
			
			// get the current aabb
			AABB aabb = this.broadphase.getAABB(ct, f).copy();
			
			// move the collidable a bit
			double dx = this.broadphase.getAABBExpansion() / 2.0;
			if (dx <= 0.0) {
				dx = 0.05;
			}
			ct.translate(dx, 0.0);
			
			// update the broadphases
			this.broadphase.update(ct, f);
			
			// the aabbs should not have been updated because of the expansion code
			TestCase.assertEquals(aabb, this.broadphase.getAABB(ct, f));
			
			// check for update
			if (this.broadphase.isUpdateTrackingEnabled()) {
				TestCase.assertFalse(this.broadphase.isUpdated(ct));
				TestCase.assertFalse(this.broadphase.isUpdated(new BasicCollisionItem<TestCollisionBody, Fixture>(ct, f)));
				TestCase.assertFalse(this.broadphase.isUpdated(ct, f));
			}
		}
	}
	
	/**
	 * Tests the update method where the collidable moves enough to update the AABB.
	 */
	@Test
	public void updateLarge() {
		TestCollisionBody ct = new TestCollisionBody(Geometry.createCircle(1.0));
		Fixture f = ct.getFixture(0);
		
		// add the item to the broadphases
		this.broadphase.add(ct);
		
		// make sure they are there
		AABB aabb = this.broadphase.getAABB(ct, f).copy();
		
		// move the collidable enough so its AABB goes out of the expanded AABB
		double dx = this.broadphase.getAABBExpansion() * 2.0;
		if (dx <= 0.0) {
			dx = 1.0;
		}
		ct.translate(dx, 0.0);
		
		// update the broadphases
		this.broadphase.update(ct);
		
		// the aabbs should have been updated because the translation was large enough
		TestCase.assertFalse(aabb.equals(this.broadphase.getAABB(ct, f)));
		
		// this will always return true for some detectors, but for those that
		// it doesn't, it should return true because of the update above
		TestCase.assertTrue(this.broadphase.isUpdated(ct));
		TestCase.assertTrue(this.broadphase.isUpdated(new BasicCollisionItem<TestCollisionBody, Fixture>(ct, f)));
		TestCase.assertTrue(this.broadphase.isUpdated(ct, f));
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
		if (this.broadphase instanceof DynamicAABBTree ||
			this.broadphase instanceof Sap) {
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
			
			BasicCollisionItem<TestCollisionBody, Fixture> item = new BasicCollisionItem<TestCollisionBody, Fixture>(ct1, ct1.getFixture(0));
			
			// add the item to the broadphases
			this.broadphase.add(ct1);
			this.broadphase.add(ct2);
			
			// this should always be true even if the broadphase doesn't support update tracking
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertTrue(this.broadphase.isUpdated(item));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
			
			this.broadphase.clearUpdates();
			
			TestCase.assertFalse(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
			TestCase.assertFalse(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertFalse(this.broadphase.isUpdated(item));
			TestCase.assertFalse(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
			
			this.broadphase.setUpdated(ct1);
			
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertTrue(this.broadphase.isUpdated(item));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
			
			this.broadphase.clearUpdates();
			this.broadphase.setUpdated(ct2);
			
			TestCase.assertFalse(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
			TestCase.assertFalse(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertFalse(this.broadphase.isUpdated(item));
			TestCase.assertFalse(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
			
			this.broadphase.clearUpdates();
			this.broadphase.setUpdated(ct1, ct1.getFixture(0));
			
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertTrue(this.broadphase.isUpdated(item));
			TestCase.assertFalse(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
			
			this.broadphase.clearUpdates();
			this.broadphase.setUpdated(ct1, ct1.getFixture(1));
			
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2));
			TestCase.assertFalse(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertFalse(this.broadphase.isUpdated(item));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertFalse(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
		} else {
			// if it doesn't support update tracking then these methods should be no-ops
			
			TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
			ct1.addFixture(Geometry.createCircle(0.5));
			TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(1.0));
			
			BasicCollisionItem<TestCollisionBody, Fixture> item = new BasicCollisionItem<TestCollisionBody, Fixture>(ct1, ct1.getFixture(0));
			
			// add the item to the broadphases
			this.broadphase.add(ct1);
			this.broadphase.add(ct2);
			
			// this should always be true even if the broadphase doesn't support update tracking
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(0)));
			TestCase.assertTrue(this.broadphase.isUpdated(item));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1, ct1.getFixture(1)));
			TestCase.assertTrue(this.broadphase.isUpdated(ct2, ct2.getFixture(0)));
			
			this.broadphase.clearUpdates();
			this.broadphase.setUpdated(ct1);
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
			
			this.broadphase.setUpdated(ct1, ct1.getFixture(0));
			TestCase.assertTrue(this.broadphase.isUpdated(ct1));
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
		TestCase.assertEquals(3, this.broadphase.size());
		
		this.broadphase.remove(ct1, ct1.getFixture(0));
		if (this.broadphase instanceof LazyAABBTree) {
			((LazyAABBTree<?, ?>)this.broadphase).doPendingRemoves();
		}
		
		TestCase.assertEquals(2, this.broadphase.size());
		
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
		
		Iterator<CollisionPair<TestCollisionBody, Fixture>> it = this.broadphase.detectIterator(false);
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
		Iterator<CollisionItem<TestCollisionBody, Fixture>> it = this.broadphase.raycastIterator(ray, 3.0);
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
		
		Iterator<CollisionItem<TestCollisionBody, Fixture>> it = this.broadphase.detectIterator(aabb);
		
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
		Iterator<CollisionPair<TestCollisionBody, Fixture>> itPairs = this.broadphase.detectIterator(true);
		TestCase.assertNotNull(itPairs);
		TestCase.assertFalse(itPairs.hasNext());
		
		// aabb
		AABB aabb = new AABB(0.0, -2.0, 1.0, 1.0);
		Iterator<CollisionItem<TestCollisionBody, Fixture>> itAABB = this.broadphase.detectIterator(aabb);
		TestCase.assertNotNull(itAABB);
		TestCase.assertFalse(itAABB.hasNext());
		
		// raycast
		Ray ray = new Ray(new Vector2(-2.0, -2.0), (new Vector2(1.0, 1.0).getNormalized()));
		Iterator<CollisionItem<TestCollisionBody, Fixture>> itRaycast = this.broadphase.raycastIterator(ray, 3.0);
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
		
		List<CollisionPair<TestCollisionBody, Fixture>> pairs = this.broadphase.detect();
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
		List<CollisionItem<TestCollisionBody, Fixture>> list;
		
		list = this.broadphase.detect(aabb);
		TestCase.assertEquals(2, list.size());
		TestCase.assertTrue(this.containsItem(ct3, ct3.getFixture(0), list));
		TestCase.assertTrue(this.containsItem(ct4, ct4.getFixture(0), list));
		
		// should include:
		// ct2, ct3, and ct4
		aabb = new AABB(-0.75, -3.0, 2.0, 1.0);
		
		list = this.broadphase.detect(aabb);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(this.containsItem(ct2, ct2.getFixture(0), list));
		TestCase.assertTrue(this.containsItem(ct3, ct3.getFixture(0), list));
		TestCase.assertTrue(this.containsItem(ct4, ct4.getFixture(0), list));
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
		
		List<CollisionItem<TestCollisionBody, Fixture>> list;
		
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
		TestCase.assertTrue(this.containsItem(ct1, ct1.getFixture(0), list));
		TestCase.assertTrue(this.containsItem(ct2, ct2.getFixture(0), list));
		TestCase.assertTrue(this.containsItem(ct4, ct4.getFixture(0), list));
		
		// try one more ray
		r = new Ray(new Vector2(-3.0, -2.0), new Vector2(1.0, 2.0).getNormalized());
		list = this.broadphase.raycast(r, l);
		TestCase.assertEquals(2, list.size());
		TestCase.assertTrue(this.containsItem(ct1, ct1.getFixture(0), list));
		TestCase.assertTrue(this.containsItem(ct2, ct2.getFixture(0), list));
		
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
		List<CollisionPair<TestCollisionBody, Fixture>> pairs = this.broadphase.detect();
		TestCase.assertEquals(1, pairs.size());
		
		// shift the broadphases
		Vector2 shift = new Vector2(1.0, -2.0);
		this.broadphase.shift(shift);
		
		// the number of pairs detected should be identical
		pairs = this.broadphase.detect();
		TestCase.assertEquals(1, pairs.size());
	}
	
	/**
	 * Tests creating a Sap detector using a negative capacity.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void sapNegativeInitialCapacity() {
		new Sap<TestCollisionBody, Fixture>(-10);
	}
	
	/**
	 * Tests creating a DynamicAABBTree detector using a negative capacity.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void dynamicAABBTreeNegativeInitialCapacity() {
		new DynamicAABBTree<TestCollisionBody, Fixture>(-10);
	}
	
	/**
	 * Tests methods specific to the DynamicAABBTree detector.
	 */
	@Test
	public void dynamicAABBSpecial() {
		DynamicAABBTree<TestCollisionBody, Fixture> tree = new DynamicAABBTree<TestCollisionBody, Fixture>();
		
		TestCase.assertEquals(0, tree.getHeight());
		TestCase.assertEquals(0.0, tree.getPerimeterRatio());
		
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		tree.add(ct1);
		
		TestCase.assertEquals(0, tree.getHeight());
		TestCase.assertTrue(tree.getPerimeterRatio() > 0);
		
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(1.0));
		ct2.addFixture(Geometry.createCircle(0.5));
		tree.add(ct2);
		
		TestCase.assertTrue(tree.getHeight() > 0);
		TestCase.assertTrue(tree.getPerimeterRatio() > 0);
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
		// The reference broad-phase is {@link PlainBroadphase}
		BroadphaseDetector<TestCollisionBody, Fixture> reference = new BruteForceBroadphase<TestCollisionBody, Fixture>();
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
			List<CollisionPair<TestCollisionBody, Fixture>> referenceDetect = reference.detect();
			List<CollisionPair<TestCollisionBody, Fixture>> otherDetect = this.broadphase.detect();
			
			// The pairs returned from {@link PlainBroadphase} are the minimum possible
			// if any of those is missing from the broadphase being tested, something is wrong
			for (CollisionPair<TestCollisionBody, Fixture> pair : referenceDetect) {
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
				List<CollisionItem<TestCollisionBody, Fixture>> referenceAABBDetect = reference.detect(aabb);
				List<CollisionItem<TestCollisionBody, Fixture>> otherAABBDetect = this.broadphase.detect(aabb);
				
				// Again, because the items returned from {@link PlainBroadphase} are the minimum possible
				// if any of those are missing from the broadphase being tested, something is wrong
				for (CollisionItem<TestCollisionBody, Fixture> item : referenceAABBDetect) {
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
				
				List<CollisionItem<TestCollisionBody, Fixture>> referenceRaycast = reference.raycast(randomRay, rayLength);
				List<CollisionItem<TestCollisionBody, Fixture>> otherRaycast = this.broadphase.raycast(randomRay, rayLength);
				
				for (CollisionItem<TestCollisionBody, Fixture> item : referenceRaycast) {
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
	private boolean containsItem(TestCollisionBody collidable, Fixture fixture, List<CollisionItem<TestCollisionBody, Fixture>> items) {
		for (CollisionItem<TestCollisionBody, Fixture> item : items) {
			if (item.getBody() == collidable && item.getFixture() == fixture) {
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
	private boolean containsPair(CollisionPair<TestCollisionBody, Fixture> pair, List<CollisionPair<TestCollisionBody, Fixture>> pairs) {
		for (CollisionPair<TestCollisionBody, Fixture> test : pairs) {
			if (pair.equals(test)) {
				return true;
			}
		}
		
		return false;
	}
}