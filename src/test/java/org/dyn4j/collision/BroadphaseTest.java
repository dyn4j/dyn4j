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
package org.dyn4j.collision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.dyn4j.collision.broadphase.AbstractBroadphaseDetector;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.BroadphaseItem;
import org.dyn4j.collision.broadphase.BroadphasePair;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.broadphase.LazyAABBTree;
import org.dyn4j.collision.broadphase.PlainBroadphase;
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
 * @version 3.3.1
 * @since 3.0.0
 */
@RunWith(Parameterized.class)
public class BroadphaseTest {
	/**
	 * We will be testing all broadphases one by one
	 */
	@Parameters
    public static Collection<Object[]> data() {
    	return Arrays.asList(
    			/** The sap algorithm */
    			new Object[]{new Sap<CollidableTest, Fixture>()},
    			/** The dynamic aabb algorithm */
    			new Object[]{new DynamicAABBTree<CollidableTest, Fixture>()},
    			/** The lazy aabb algorithm */
    			new Object[]{new LazyAABBTree<CollidableTest, Fixture>()},
    			/** The plain brute-force broadphase */
    			new Object[]{new PlainBroadphase<CollidableTest, Fixture>()}
    			);
    }
	
    /** Stores the current broadphase being tested */
    protected BroadphaseDetector<CollidableTest, Fixture> broadphase;
    
    /**
     * @param broadphase One broadphase instance to test
     */
    public BroadphaseTest(BroadphaseDetector<CollidableTest, Fixture> broadphase) {
    	this.broadphase = broadphase;
    }
    
	/**
	 * Sets up for each test method.
	 */
	@Before
	public void setup() {
		// clear the broadphases
		this.broadphase.clear();
	}
	
	/**
	 * Tests the add method.
	 */
	@Test
	public void add() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		
		// make sure its not there first
		TestCase.assertFalse(this.broadphase.contains(ct));
		
		// add the item to the broadphases
		this.broadphase.add(ct);
		
		// make sure they are there
		TestCase.assertTrue(this.broadphase.contains(ct));
	}
	
	/**
	 * Tests the add method with fixtures.
	 */
	@Test
	public void add2() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		ct.addFixture(Geometry.createCircle(0.5));
		
		// make sure its not there first
		TestCase.assertFalse(this.broadphase.contains(ct));
		
		// add the item to the broadphases
		this.broadphase.add(ct);
		
		// make sure they are there
		TestCase.assertTrue(this.broadphase.contains(ct));
		TestCase.assertTrue(this.broadphase.contains(ct, ct.getFixture(0)));
		
		this.broadphase.remove(ct, ct.getFixture(1));
		
		TestCase.assertFalse(this.broadphase.contains(ct));
		TestCase.assertTrue(this.broadphase.contains(ct, ct.getFixture(0)));
	}
	
	/**
	 * Tests the remove method.
	 */
	@Test
	public void remove() {
		CollidableTest ct1 = new CollidableTest(Geometry.createCircle(1.0));
		ct1.addFixture(Geometry.createCircle(3.0));
		CollidableTest ct2 = new CollidableTest(Geometry.createCircle(2.0));
		
		// add the item to the broadphases
		this.broadphase.add(ct1);
		this.broadphase.add(ct2);
		
		// make sure they are there
		TestCase.assertTrue(this.broadphase.contains(ct1));
		TestCase.assertTrue(this.broadphase.contains(ct2));
		
		// then remove them from the broadphases
		this.broadphase.remove(ct1);
		
		// make sure they aren't there any more
		TestCase.assertFalse(this.broadphase.contains(ct1));
		TestCase.assertFalse(this.broadphase.contains(ct1, ct1.getFixture(0)));
		TestCase.assertFalse(this.broadphase.contains(ct1, ct1.getFixture(1)));
	}
	
	/**
	 * Tests the add method with fixtures.
	 */
	@Test
	public void remove2() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		ct.addFixture(Geometry.createCircle(0.5));
		
		// add the item to the broadphases
		this.broadphase.add(ct);
		
		// make sure they are there
		TestCase.assertTrue(this.broadphase.contains(ct));
		TestCase.assertTrue(this.broadphase.contains(ct, ct.getFixture(0)));
		
		this.broadphase.remove(ct, ct.getFixture(1));
		
		TestCase.assertFalse(this.broadphase.contains(ct));
		TestCase.assertFalse(this.broadphase.contains(ct, ct.getFixture(1)));
		TestCase.assertTrue(this.broadphase.contains(ct, ct.getFixture(0)));
	}
		
	/**
	 * Tests the update method where the collidable moves very little.
	 */
	@Test
	public void updateSmall() {
		if (this.broadphase.supportsAABBExpansion()) {
			CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
			Fixture f = ct.getFixture(0);
			
			// add the item to the broadphases
			this.broadphase.add(ct);
			
			// get the current aabb
			AABB aabbSap = this.broadphase.getAABB(ct, f);
			
			// move the collidable a bit
			ct.translate(0.05, 0.0);
			
			// update the broadphases
			this.broadphase.update(ct, f);
			
			// the aabbs should not have been updated because of the expansion code
			TestCase.assertSame(aabbSap, this.broadphase.getAABB(ct, f));
		}
	}
	
	/**
	 * Tests the update method where the collidable moves enough to update the AABB.
	 */
	@Test
	public void updateLarge() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		Fixture f = ct.getFixture(0);
		
		// add the item to the broadphases
		this.broadphase.add(ct);
		
		// make sure they are there
		AABB aabbSap = this.broadphase.getAABB(ct, f);
		
		// move the collidable enough so its AABB goes out of the expanded AABB
		ct.translate(0.5, 0.0);
		
		// update the broadphases
		this.broadphase.update(ct);
		
		// the aabbs should have been updated because the translation was large enough
		TestCase.assertNotSame(aabbSap, this.broadphase.getAABB(ct, f));
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.broadphase.add(ct);
		
		TestCase.assertEquals(1, this.broadphase.size());
		
		// clear all the broadphases
		this.broadphase.clear();
		
		// check for the aabb
		TestCase.assertEquals(0, this.broadphase.size());
	}
	
	/**
	 * Tests the getAABB method.
	 */
	@Test
	public void getAABB() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.broadphase.add(ct);
		
		AABB aabbSap = this.broadphase.getAABB(ct);
		
		AABB aabb = ct.createAABB();
		
		if (this.broadphase.supportsAABBExpansion()) {
			// don't forget that the aabb could be expanded
			aabb.expand(this.broadphase.getAABBExpansion());	
		}
		
		TestCase.assertTrue(isEqual(aabbSap, aabb));
	}
	
	/**
	 * Helper method for the getAABB test method.
	 * @param aabb1 the first aabb
	 * @param aabb2 the second aabb
	 * @return boolean true if they are basically the same
	 */
	private boolean isEqual(AABB aabb1, AABB aabb2) {
		if (Math.abs(aabb1.getMinX() - aabb2.getMinX()) >= 1.0E-8) return false;
		if (Math.abs(aabb1.getMinY() - aabb2.getMinY()) >= 1.0E-8) return false;
		if (Math.abs(aabb1.getMaxX() - aabb2.getMaxX()) >= 1.0E-8) return false;
		if (Math.abs(aabb1.getMaxY() - aabb2.getMaxY()) >= 1.0E-8) return false;
		return true;
	}
	
	/**
	 * Tests the {@link AbstractBroadphaseDetector} detect methods.
	 * @since 3.1.0
	 */
	@Test
	public void detectAbstract() {
		CollidableTest ct1 = new CollidableTest(Geometry.createCircle(1.0));
		CollidableTest ct2 = new CollidableTest(Geometry.createUnitCirclePolygon(5, 0.5));
		ct1.translate(-2.0, 0.0);
		ct2.translate(-1.0, 1.0);
		
		TestCase.assertTrue(this.broadphase.detect(ct1, ct2));
		TestCase.assertTrue(this.broadphase.detect(ct1.getFixture(0).shape, ct1.transform, ct2.getFixture(0).shape, ct2.transform));
		
		ct1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.broadphase.detect(ct1, ct2));
		TestCase.assertFalse(this.broadphase.detect(ct1.getFixture(0).shape, ct1.transform, ct2.getFixture(0).shape, ct2.transform));
	}
	
	/**
	 * Tests the detect method.
	 * @since 3.1.0
	 */
	@Test
	public void detect() {
		CollidableTest ct1 = new CollidableTest(Geometry.createCircle(1.0));
		CollidableTest ct2 = new CollidableTest(Geometry.createUnitCirclePolygon(5, 0.5));
		CollidableTest ct3 = new CollidableTest(Geometry.createRectangle(1.0, 0.5));
		CollidableTest ct4 = new CollidableTest(Geometry.createVerticalSegment(2.0));
		
		ct1.translate(-2.0, 0.0);
		ct2.translate(-1.0, 1.0);
		ct3.translate(0.5, -2.0);
		ct4.translate(1.0, 1.0);
		
		// add the items to the broadphases
		this.broadphase.add(ct1); this.broadphase.add(ct2); this.broadphase.add(ct3); this.broadphase.add(ct4);
		
		List<BroadphasePair<CollidableTest, Fixture>> pairs = this.broadphase.detect();
		TestCase.assertEquals(1, pairs.size());
	}
	
	/**
	 * Tests the detect method using an AABB.
	 */
	@Test
	public void detectAABB() {
		CollidableTest ct1 = new CollidableTest(Geometry.createCircle(1.0));
		CollidableTest ct2 = new CollidableTest(Geometry.createUnitCirclePolygon(5, 0.5));
		CollidableTest ct3 = new CollidableTest(Geometry.createRectangle(1.0, 0.5));
		CollidableTest ct4 = new CollidableTest(Geometry.createVerticalSegment(2.0));
		
		ct1.translate(-2.0, 0.0);
		ct2.translate(-1.0, 1.0);
		ct3.translate(0.5, -2.0);
		ct4.translate(1.0, 1.0);
		
		// add the items to the broadphases
		this.broadphase.add(ct1); this.broadphase.add(ct2); this.broadphase.add(ct3); this.broadphase.add(ct4);
		
		// this aabb should include:
		// ct3 and ct4
		AABB aabb = new AABB(0.0, -2.0, 1.0, 1.0);
		List<BroadphaseItem<CollidableTest, Fixture>> list;
		
		list = this.broadphase.detect(aabb);
		TestCase.assertEquals(2, list.size());
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct3, ct3.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct4, ct4.getFixture(0))));
		
		// should include:
		// ct2, ct3, and ct4
		aabb = new AABB(-0.75, -3.0, 2.0, 1.0);
		
		list = this.broadphase.detect(aabb);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct2, ct2.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct3, ct3.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct4, ct4.getFixture(0))));
	}
	
	/**
	 * Tests the raycast method.
	 */
	@Test
	public void raycast() {
		CollidableTest ct1 = new CollidableTest(Geometry.createCircle(1.0));
		CollidableTest ct2 = new CollidableTest(Geometry.createUnitCirclePolygon(5, 0.5));
		CollidableTest ct3 = new CollidableTest(Geometry.createRectangle(1.0, 0.5));
		CollidableTest ct4 = new CollidableTest(Geometry.createVerticalSegment(2.0));
		
		ct1.translate(-2.0, 0.0);
		ct2.translate(-1.0, 1.0);
		ct3.translate(0.5, -2.0);
		ct4.translate(1.0, 1.2);
		
		// add the items to the broadphases
		this.broadphase.add(ct1); this.broadphase.add(ct2); this.broadphase.add(ct3); this.broadphase.add(ct4);
		
		List<BroadphaseItem<CollidableTest, Fixture>> list;
		
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
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct1, ct1.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct2, ct2.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct4, ct4.getFixture(0))));
		
		// try one more ray
		r = new Ray(new Vector2(-3.0, -2.0), new Vector2(1.0, 2.0).getNormalized());
		list = this.broadphase.raycast(r, l);
		TestCase.assertEquals(2, list.size());
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct1, ct1.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct2, ct2.getFixture(0))));
	}
	
	/**
	 * Tests the get/set expansion methods.
	 */
	@Test
	public void expansion() {
		if (this.broadphase.supportsAABBExpansion()) {
			// test the default
			TestCase.assertEquals(BroadphaseDetector.DEFAULT_AABB_EXPANSION, this.broadphase.getAABBExpansion());
			
			// test changing the expansion
			this.broadphase.setAABBExpansion(0.3);
			TestCase.assertEquals(0.3, this.broadphase.getAABBExpansion());
			
			// test the new expansion value
			CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
			
			// add the item to the broadphases
			this.broadphase.add(ct);
			
			AABB aabbSap = this.broadphase.getAABB(ct);
			
			AABB aabb = ct.createAABB();
			// don't forget that the aabb is expanded
			aabb.expand(0.3);
			TestCase.assertTrue(isEqual(aabbSap, aabb));
		}
	}
	
	/**
	 * Tests the shiftCoordinates method.
	 */
	@Test
	public void shiftCoordinates() {
		CollidableTest ct1 = new CollidableTest(Geometry.createCircle(1.0));
		CollidableTest ct2 = new CollidableTest(Geometry.createUnitCirclePolygon(5, 0.5));
		CollidableTest ct3 = new CollidableTest(Geometry.createRectangle(1.0, 0.5));
		CollidableTest ct4 = new CollidableTest(Geometry.createVerticalSegment(2.0));
		
		ct1.translate(-2.0, 0.0);
		ct2.translate(-1.0, 1.0);
		ct3.translate(0.5, -2.0);
		ct4.translate(1.0, 1.0);
		
		// add the items to the broadphases
		this.broadphase.add(ct1); this.broadphase.add(ct2); this.broadphase.add(ct3); this.broadphase.add(ct4);
		
		// perform a detect on the whole broadphase
		List<BroadphasePair<CollidableTest, Fixture>> pairs = this.broadphase.detect();
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
	public void SapNegativeInitialCapacity() {
		new Sap<CollidableTest, Fixture>(-10);
	}
	
	/**
	 * Tests creating a DynamicAABBTree detector using a negative capacity.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void DynamicAABBTreeNegativeInitialCapacity() {
		new DynamicAABBTree<CollidableTest, Fixture>(-10);
	}
	
	/** Seed for the randomized test. Can be any value */
	private static final int SEED = 0;
	
	/**
	 * Deterministic randomized test, used to test various core functionalities of all broad-phase detectors, simulating some complex scenarios.
	 * This test uses a {@link PlainBroadphase} for reference and tests the output of the broad-phase against the reference broad-phase.
	 * For all queries (detect, detect(AABB) and raycast) the output of the broad-phase must contain all pairs/items returned by the reference broad-phase (and maybe some more),
	 * because {@link PlainBroadphase} always returns the minimal answer set.
	 * <p>
	 * The {@link PlainBroadphase} implements only simple brute force algorithms so we can consider it's implementation bug free.
	 */
	@Test
	public void randomizedTest() {
		// The reference broad-phase is {@link PlainBroadphase}
		BroadphaseDetector<CollidableTest, Fixture> reference = new PlainBroadphase<CollidableTest, Fixture>();
		List<CollidableTest> collidables = new ArrayList<CollidableTest>();
		
		// Constant seed so we always get the same sequence of randoms
		Random random = new Random(SEED);
		
		// Pick some iterations and query count
		final int iterations = 100;
		final int queries = 10;
		
		for (int i = 0; i < iterations; i++) {
			// Create a random rectangle
			Rectangle randomRectangle = Geometry.createRectangle(random.nextDouble() + 0.1, random.nextDouble() + 0.1);
			CollidableTest collidable = new CollidableTest(randomRectangle);
			
			// And apply a random translation
			collidable.translate(random.nextDouble() * 10 - 5, random.nextDouble() * 10 - 5);
			
			// Add the new collidable to both broadphases
			collidables.add(collidable);
			this.broadphase.add(collidable);
			reference.add(collidable);
			
			// Also remove one existing collidable with 25% chance
			if (random.nextDouble() <= 0.25) {
				CollidableTest forRemoval = collidables.remove(random.nextInt(collidables.size()));
				
				this.broadphase.remove(forRemoval);
				reference.remove(forRemoval);
			}
			
			// Now start querying
			// First test detect
			List<BroadphasePair<CollidableTest, Fixture>> referenceDetect = reference.detect();
			List<BroadphasePair<CollidableTest, Fixture>> otherDetect = this.broadphase.detect();
			
			// The pairs returned from {@link PlainBroadphase} are the minimum possible
			// if any of those is missing from the broadphase being tested, something is wrong
			for (BroadphasePair<CollidableTest, Fixture> pair : referenceDetect) {
				// Be careful to have correct pair equality here. See pairExists
				if (!pairExists(pair, otherDetect)) {
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
				List<BroadphaseItem<CollidableTest, Fixture>> referenceAABBDetect = reference.detect(aabb);
				List<BroadphaseItem<CollidableTest, Fixture>> otherAABBDetect = this.broadphase.detect(aabb);
				
				// Again, because the items returned from {@link PlainBroadphase} are the minimum possible
				// if any of those are missing from the broadphase being tested, something is wrong
				for (BroadphaseItem<CollidableTest, Fixture> item : referenceAABBDetect) {
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
				
				List<BroadphaseItem<CollidableTest, Fixture>> referenceRaycast = reference.raycast(randomRay, rayLength);
				List<BroadphaseItem<CollidableTest, Fixture>> otherRaycast = this.broadphase.raycast(randomRay, rayLength);
				
				for (BroadphaseItem<CollidableTest, Fixture> item : referenceRaycast) {
					if (!otherRaycast.contains(item)) {
						TestCase.fail("raycast() is missing items");
					}
				}
			}
		}
	}
	
	/**
	 * Simple helper method that finds if a {@link BroadphasePair} or it's reverse pair is
	 * contained in a list of {@link BroadphasePair}s.
	 * 
	 * @param pair The pair to search
	 * @param pairs The list of pairs
	 * @return true if pair or it's reverse is contained in the list, false otherwise
	 */
	private boolean pairExists(BroadphasePair<CollidableTest, Fixture> pair, List<BroadphasePair<CollidableTest, Fixture>> pairs) {
		if (pairs.contains(pair)) {
			return true;
		} else {
			BroadphasePair<CollidableTest, Fixture> reversePair = new BroadphasePair<CollidableTest, Fixture>(
					pair.getCollidable2(), pair.getFixture2(), pair.getCollidable1(), pair.getFixture1());
			
			return pairs.contains(reversePair);
		}
	}
	
}