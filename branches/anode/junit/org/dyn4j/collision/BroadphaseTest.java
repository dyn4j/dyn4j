/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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

import java.util.List;

import junit.framework.TestCase;

import org.dyn4j.collision.broadphase.AbstractBroadphaseDetector;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.BroadphaseItem;
import org.dyn4j.collision.broadphase.BroadphasePair;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.broadphase.Sap;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Class used to test the {@link BroadphaseDetector} methods.
 * @author William Bittle
 * @version 3.1.0
 * @since 3.0.0
 */
public class BroadphaseTest {
	/** The sap algorithm */
	protected Sap<CollidableTest, Fixture> sap = new Sap<CollidableTest, Fixture>();
	
	/** The dynamic aabb algorithm */
	protected DynamicAABBTree<CollidableTest, Fixture> dyn = new DynamicAABBTree<CollidableTest, Fixture>();
	
	/**
	 * Sets up for each test method.
	 */
	@Before
	public void setup() {
		// clear the broadphases
		this.sap.clear();
		this.dyn.clear();
	}
	
	/**
	 * Tests the add method.
	 */
	@Test
	public void add() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		
		// make sure its not there first
		TestCase.assertFalse(this.sap.contains(ct));
		TestCase.assertFalse(this.dyn.contains(ct));
		
		// add the item to the broadphases
		this.sap.add(ct);
		this.dyn.add(ct);
		
		// make sure they are there
		TestCase.assertTrue(this.sap.contains(ct));
		TestCase.assertTrue(this.dyn.contains(ct));
	}
	
	/**
	 * Tests the add method with fixtures.
	 */
	@Test
	public void add2() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		ct.addFixture(Geometry.createCircle(0.5));
		
		// make sure its not there first
		TestCase.assertFalse(this.sap.contains(ct));
		TestCase.assertFalse(this.dyn.contains(ct));
		
		// add the item to the broadphases
		this.sap.add(ct);
		this.dyn.add(ct);
		
		// make sure they are there
		TestCase.assertTrue(this.sap.contains(ct));
		TestCase.assertTrue(this.dyn.contains(ct));
		TestCase.assertTrue(this.sap.contains(ct, ct.getFixture(0)));
		TestCase.assertTrue(this.dyn.contains(ct, ct.getFixture(0)));
		
		this.sap.remove(ct, ct.getFixture(1));
		this.dyn.remove(ct, ct.getFixture(1));
		
		TestCase.assertFalse(this.sap.contains(ct));
		TestCase.assertFalse(this.dyn.contains(ct));
		TestCase.assertTrue(this.sap.contains(ct, ct.getFixture(0)));
		TestCase.assertTrue(this.dyn.contains(ct, ct.getFixture(0)));
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
		this.sap.add(ct1);
		this.sap.add(ct2);
		this.dyn.add(ct1);
		this.dyn.add(ct2);
		
		// make sure they are there
		TestCase.assertTrue(this.sap.contains(ct1));
		TestCase.assertTrue(this.sap.contains(ct2));
		TestCase.assertTrue(this.dyn.contains(ct1));
		TestCase.assertTrue(this.dyn.contains(ct2));
		
		// then remove them from the broadphases
		this.sap.remove(ct1);
		this.dyn.remove(ct1);
		
		// make sure they aren't there any more
		TestCase.assertFalse(this.sap.contains(ct1));
		TestCase.assertFalse(this.dyn.contains(ct1));
		TestCase.assertFalse(this.sap.contains(ct1, ct1.getFixture(0)));
		TestCase.assertFalse(this.dyn.contains(ct1, ct1.getFixture(0)));
		TestCase.assertFalse(this.sap.contains(ct1, ct1.getFixture(1)));
		TestCase.assertFalse(this.dyn.contains(ct1, ct1.getFixture(1)));
	}
	
	/**
	 * Tests the add method with fixtures.
	 */
	@Test
	public void remove2() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		ct.addFixture(Geometry.createCircle(0.5));
		
		// add the item to the broadphases
		this.sap.add(ct);
		this.dyn.add(ct);
		
		// make sure they are there
		TestCase.assertTrue(this.sap.contains(ct));
		TestCase.assertTrue(this.dyn.contains(ct));
		TestCase.assertTrue(this.sap.contains(ct, ct.getFixture(0)));
		TestCase.assertTrue(this.dyn.contains(ct, ct.getFixture(0)));
		
		this.sap.remove(ct, ct.getFixture(1));
		this.dyn.remove(ct, ct.getFixture(1));
		
		TestCase.assertFalse(this.sap.contains(ct));
		TestCase.assertFalse(this.dyn.contains(ct));
		TestCase.assertFalse(this.sap.contains(ct, ct.getFixture(1)));
		TestCase.assertFalse(this.dyn.contains(ct, ct.getFixture(1)));
		TestCase.assertTrue(this.sap.contains(ct, ct.getFixture(0)));
		TestCase.assertTrue(this.dyn.contains(ct, ct.getFixture(0)));
	}
		
	/**
	 * Tests the update method where the collidable moves very little.
	 */
	@Test
	public void updateSmall() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		Fixture f = ct.getFixture(0);
		
		// add the item to the broadphases
		this.sap.add(ct);
		this.dyn.add(ct);
		
		// get the current aabb
		AABB aabbSap = this.sap.getAABB(ct, f);
		AABB aabbDyn = this.dyn.getAABB(ct, f);
		
		// move the collidable a bit
		ct.translate(0.05, 0.0);
		
		// update the broadphases
		this.sap.update(ct, f);
		this.dyn.update(ct, f);
		
		// the aabbs should not have been updated because of the expansion code
		TestCase.assertSame(aabbSap, this.sap.getAABB(ct, f));
		TestCase.assertSame(aabbDyn, this.dyn.getAABB(ct, f));
	}
	
	/**
	 * Tests the update method where the collidable moves enough to update the AABB.
	 */
	@Test
	public void updateLarge() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		Fixture f = ct.getFixture(0);
		
		// add the item to the broadphases
		this.sap.add(ct);
		this.dyn.add(ct);
		
		// make sure they are there
		AABB aabbSap = this.sap.getAABB(ct, f);
		AABB aabbDyn = this.dyn.getAABB(ct, f);
		
		// move the collidable a bit
		ct.translate(0.5, 0.0);
		
		// update the broadphases
		this.sap.update(ct);
		this.dyn.update(ct);
		
		// the aabbs should not have been updated because of the expansion code
		TestCase.assertNotSame(aabbSap, this.sap.getAABB(ct, f));
		TestCase.assertNotSame(aabbDyn, this.dyn.getAABB(ct, f));
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.sap.add(ct);
		this.dyn.add(ct);
		
		TestCase.assertEquals(1, this.sap.size());
		TestCase.assertEquals(1, this.dyn.size());
		
		// clear all the broadphases
		this.sap.clear();
		this.dyn.clear();
		
		// check for the aabb
		TestCase.assertEquals(0, this.sap.size());
		TestCase.assertEquals(0, this.dyn.size());
	}
	
	/**
	 * Tests the getAABB method.
	 */
	@Test
	public void getAABB() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.sap.add(ct);
		this.dyn.add(ct);
		
		AABB aabbSap = this.sap.getAABB(ct);
		AABB aabbDyn = this.dyn.getAABB(ct);
		
		AABB aabb = ct.createAABB();
		// don't forget that the aabb is expanded
		aabb.expand(BroadphaseDetector.DEFAULT_AABB_EXPANSION);
		TestCase.assertTrue(isEqual(aabbSap, aabb));
		TestCase.assertTrue(isEqual(aabbDyn, aabb));
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
		
		TestCase.assertTrue(this.dyn.detect(ct1, ct2));
		TestCase.assertTrue(this.dyn.detect(ct1.getFixture(0).shape, ct1.transform, ct2.getFixture(0).shape, ct2.transform));
		
		ct1.translate(-1.0, 0.0);
		TestCase.assertFalse(this.dyn.detect(ct1, ct2));
		TestCase.assertFalse(this.dyn.detect(ct1.getFixture(0).shape, ct1.transform, ct2.getFixture(0).shape, ct2.transform));
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
		this.sap.add(ct1); this.sap.add(ct2); this.sap.add(ct3); this.sap.add(ct4);
		this.dyn.add(ct1); this.dyn.add(ct2); this.dyn.add(ct3); this.dyn.add(ct4);
		
		List<BroadphasePair<CollidableTest, Fixture>> pairs = this.sap.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.dyn.detect();
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
		this.sap.add(ct1); this.sap.add(ct2); this.sap.add(ct3); this.sap.add(ct4);
		this.dyn.add(ct1); this.dyn.add(ct2); this.dyn.add(ct3); this.dyn.add(ct4);
		
		// this aabb should include:
		// ct3 and ct4
		AABB aabb = new AABB(0.0, -2.0, 1.0, 1.0);
		List<BroadphaseItem<CollidableTest, Fixture>> list;
		
		list = this.sap.detect(aabb);
		TestCase.assertEquals(2, list.size());
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct3, ct3.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct4, ct4.getFixture(0))));
		list = this.dyn.detect(aabb);
		TestCase.assertEquals(2, list.size());
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct3, ct3.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct4, ct4.getFixture(0))));
		
		// should include:
		// ct2, ct3, and ct4
		aabb = new AABB(-0.75, -3.0, 2.0, 1.0);
		
		list = this.sap.detect(aabb);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct2, ct2.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct3, ct3.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct4, ct4.getFixture(0))));
		list = this.dyn.detect(aabb);
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
		this.sap.add(ct1); this.sap.add(ct2); this.sap.add(ct3); this.sap.add(ct4);
		this.dyn.add(ct1); this.dyn.add(ct2); this.dyn.add(ct3); this.dyn.add(ct4);
		
		List<BroadphaseItem<CollidableTest, Fixture>> list;
		
		// ray that points in the positive x direction and starts at the origin
		Ray r = new Ray(new Vector2(1.0, 0.0));
		// infinite length
		double l = 0.0;
		
		list = this.sap.raycast(r, l);
		TestCase.assertEquals(0, list.size());
		list = this.dyn.raycast(r, l);
		TestCase.assertEquals(0, list.size());
		
		// try a different ray
		r = new Ray(new Vector2(-3.0, 0.75), new Vector2(1.0, 0.0));
		list = this.sap.raycast(r, l);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct1, ct1.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct2, ct2.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct4, ct4.getFixture(0))));
		list = this.dyn.raycast(r, l);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct1, ct1.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct2, ct2.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct4, ct4.getFixture(0))));
		
		// try one more ray
		r = new Ray(new Vector2(-3.0, -2.0), new Vector2(1.0, 2.0).getNormalized());
		list = this.sap.raycast(r, l);
		TestCase.assertEquals(2, list.size());
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct1, ct1.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct2, ct2.getFixture(0))));
		list = this.dyn.raycast(r, l);
		TestCase.assertEquals(2, list.size());
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct1, ct1.getFixture(0))));
		TestCase.assertTrue(list.contains(new BroadphaseItem<CollidableTest, Fixture>(ct2, ct2.getFixture(0))));
	}
	
	/**
	 * Tests the get/set expansion methods.
	 */
	@Test
	public void expansion() {
		// test the default
		TestCase.assertEquals(BroadphaseDetector.DEFAULT_AABB_EXPANSION, this.sap.getAABBExpansion());
		TestCase.assertEquals(BroadphaseDetector.DEFAULT_AABB_EXPANSION, this.dyn.getAABBExpansion());
		
		// test changing the expansion
		this.sap.setAABBExpansion(0.3);
		this.dyn.setAABBExpansion(0.3);
		TestCase.assertEquals(0.3, this.sap.getAABBExpansion());
		TestCase.assertEquals(0.3, this.dyn.getAABBExpansion());
		
		// test the new expansion value
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.sap.add(ct);
		this.dyn.add(ct);
		
		AABB aabbSap = this.sap.getAABB(ct);
		AABB aabbDyn = this.dyn.getAABB(ct);
		
		AABB aabb = ct.createAABB();
		// don't forget that the aabb is expanded
		aabb.expand(0.3);
		TestCase.assertTrue(isEqual(aabbSap, aabb));
		TestCase.assertTrue(isEqual(aabbDyn, aabb));
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
		this.sap.add(ct1); this.sap.add(ct2); this.sap.add(ct3); this.sap.add(ct4);
		this.dyn.add(ct1); this.dyn.add(ct2); this.dyn.add(ct3); this.dyn.add(ct4);
		
		// perform a detect on the whole broadphase
		List<BroadphasePair<CollidableTest, Fixture>> pairs = this.sap.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.dyn.detect();
		TestCase.assertEquals(1, pairs.size());
		
		// shift the broadphases
		Vector2 shift = new Vector2(1.0, -2.0);
		this.sap.shift(shift);
		this.dyn.shift(shift);
		
		// the number of pairs detected should be identical
		pairs = this.sap.detect();
		TestCase.assertEquals(1, pairs.size());
		pairs = this.dyn.detect();
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
}
