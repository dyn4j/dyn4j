/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.broadphase.SapBruteForce;
import org.dyn4j.collision.broadphase.SapIncremental;
import org.dyn4j.collision.broadphase.SapTree;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Class used to test the {@link BroadphaseDetector} methods.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public class BroadphaseTest {
	/** The list implementation of incremental SAP */
	private SapIncremental<CollidableTest> sapI = new SapIncremental<CollidableTest>();
	
	/** The list implementation of brute force SAP */
	private SapBruteForce<CollidableTest> sapBF = new SapBruteForce<CollidableTest>();
	
	/** The tree implementation of incremental SAP */
	private SapTree<CollidableTest> sapT = new SapTree<CollidableTest>();
	
	/** The dynamic aabb tree */
	private DynamicAABBTree<CollidableTest> dynT = new DynamicAABBTree<CollidableTest>();
	
	/**
	 * Sets up for each test method.
	 */
	@Before
	public void setup() {
		// clear the broadphases
		this.sapI.clear();
		this.sapBF.clear();
		this.sapT.clear();
		this.dynT.clear();
	}
	
	/**
	 * Tests the add method.
	 */
	@Test
	public void add() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		
		// make sure its not there first
		TestCase.assertNull(this.sapI.getAABB(ct));
		TestCase.assertNull(this.sapBF.getAABB(ct));
		TestCase.assertNull(this.sapT.getAABB(ct));
		TestCase.assertNull(this.dynT.getAABB(ct));
		
		// add the item to the broadphases
		this.sapI.add(ct);
		this.sapBF.add(ct);
		this.sapT.add(ct);
		this.dynT.add(ct);
		
		// make sure they are there
		TestCase.assertNotNull(this.sapI.getAABB(ct));
		TestCase.assertNotNull(this.sapBF.getAABB(ct));
		TestCase.assertNotNull(this.sapT.getAABB(ct));
		TestCase.assertNotNull(this.dynT.getAABB(ct));
	}
	
	/**
	 * Tests the remove method.
	 */
	@Test
	public void remove() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.sapI.add(ct);
		this.sapBF.add(ct);
		this.sapT.add(ct);
		this.dynT.add(ct);
		
		// make sure they are there
		TestCase.assertNotNull(this.sapI.getAABB(ct));
		TestCase.assertNotNull(this.sapBF.getAABB(ct));
		TestCase.assertNotNull(this.sapT.getAABB(ct));
		TestCase.assertNotNull(this.dynT.getAABB(ct));
		
		// then remove them from the broadphases
		this.sapI.remove(ct);
		this.sapBF.remove(ct);
		this.sapT.remove(ct);
		this.dynT.remove(ct);
		
		// make sure they aren't there any more
		TestCase.assertNull(this.sapI.getAABB(ct));
		TestCase.assertNull(this.sapBF.getAABB(ct));
		TestCase.assertNull(this.sapT.getAABB(ct));
		TestCase.assertNull(this.dynT.getAABB(ct));
	}
	
	/**
	 * Tests the update method where the collidable moves very little.
	 */
	@Test
	public void updateSmall() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.sapI.add(ct);
		this.sapBF.add(ct);
		this.sapT.add(ct);
		this.dynT.add(ct);
		
		// make sure they are there
		AABB aabbSapI = this.sapI.getAABB(ct);
		AABB aabbSapBF = this.sapBF.getAABB(ct);
		AABB aabbSapT = this.sapT.getAABB(ct);
		AABB aabbDynT = this.dynT.getAABB(ct);
		TestCase.assertNotNull(aabbSapI);
		TestCase.assertNotNull(aabbSapBF);
		TestCase.assertNotNull(aabbSapT);
		TestCase.assertNotNull(aabbDynT);
		
		// move the collidable a bit
		ct.translate(0.05, 0.0);
		
		// update the broadphases
		this.sapI.update(ct);
		this.sapBF.update(ct);
		this.sapT.update(ct);
		this.dynT.update(ct);
		
		// the aabbs should not have been updated because of the expansion code
		TestCase.assertSame(aabbSapI, this.sapI.getAABB(ct));
		TestCase.assertSame(aabbSapBF, this.sapBF.getAABB(ct));
		TestCase.assertSame(aabbSapT, this.sapT.getAABB(ct));
		TestCase.assertSame(aabbDynT, this.dynT.getAABB(ct));
	}
	
	/**
	 * Tests the update method where the collidable moves enough to update the AABB.
	 */
	@Test
	public void updateLarge() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.sapI.add(ct);
		this.sapBF.add(ct);
		this.sapT.add(ct);
		this.dynT.add(ct);
		
		// make sure they are there
		AABB aabbSapI = this.sapI.getAABB(ct);
		AABB aabbSapBF = this.sapBF.getAABB(ct);
		AABB aabbSapT = this.sapT.getAABB(ct);
		AABB aabbDynT = this.dynT.getAABB(ct);
		TestCase.assertNotNull(aabbSapI);
		TestCase.assertNotNull(aabbSapBF);
		TestCase.assertNotNull(aabbSapT);
		TestCase.assertNotNull(aabbDynT);
		
		// move the collidable a bit
		ct.translate(0.5, 0.0);
		
		// update the broadphases
		this.sapI.update(ct);
		this.sapBF.update(ct);
		this.sapT.update(ct);
		this.dynT.update(ct);
		
		// the aabbs should not have been updated because of the expansion code
		TestCase.assertNotSame(aabbSapI, this.sapI.getAABB(ct));
		TestCase.assertNotSame(aabbSapBF, this.sapBF.getAABB(ct));
		TestCase.assertNotSame(aabbSapT, this.sapT.getAABB(ct));
		TestCase.assertNotSame(aabbDynT, this.dynT.getAABB(ct));
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.sapI.add(ct);
		this.sapBF.add(ct);
		this.sapT.add(ct);
		this.dynT.add(ct);
		
		// clear all the broadphases
		this.sapI.clear();
		this.sapBF.clear();
		this.sapT.clear();
		this.dynT.clear();
		
		// check for the aabb
		TestCase.assertNull(this.sapI.getAABB(ct));
		TestCase.assertNull(this.sapBF.getAABB(ct));
		TestCase.assertNull(this.sapT.getAABB(ct));
		TestCase.assertNull(this.dynT.getAABB(ct));
	}
	
	/**
	 * Tests the getAABB method.
	 */
	@Test
	public void getAABB() {
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.sapI.add(ct);
		this.sapBF.add(ct);
		this.sapT.add(ct);
		this.dynT.add(ct);
		
		// make sure they are there
		AABB aabbSapI = this.sapI.getAABB(ct);
		AABB aabbSapBF = this.sapBF.getAABB(ct);
		AABB aabbSapT = this.sapT.getAABB(ct);
		AABB aabbDynT = this.dynT.getAABB(ct);
		
		AABB aabb = ct.createAABB();
		// don't forget that the aabb is expanded
		aabb.expand(BroadphaseDetector.DEFAULT_AABB_EXPANSION);
		TestCase.assertTrue(isEqual(aabbSapI, aabb));
		TestCase.assertTrue(isEqual(aabbSapBF, aabb));
		TestCase.assertTrue(isEqual(aabbSapT, aabb));
		TestCase.assertTrue(isEqual(aabbDynT, aabb));
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
		this.sapI.add(ct1); this.sapI.add(ct2); this.sapI.add(ct3); this.sapI.add(ct4);
		this.sapBF.add(ct1); this.sapBF.add(ct2); this.sapBF.add(ct3); this.sapBF.add(ct4);
		this.sapT.add(ct1); this.sapT.add(ct2); this.sapT.add(ct3); this.sapT.add(ct4);
		this.dynT.add(ct1); this.dynT.add(ct2); this.dynT.add(ct3); this.dynT.add(ct4);
		
		// this aabb should include:
		// ct3 and ct4
		AABB aabb = new AABB(0.0, -2.0, 1.0, 1.0);
		List<CollidableTest> list;
		
		list = this.sapI.detect(aabb);
		TestCase.assertEquals(2, list.size());
		TestCase.assertTrue(list.contains(ct3));
		TestCase.assertTrue(list.contains(ct4));
		list = this.sapBF.detect(aabb);
		TestCase.assertEquals(2, list.size());
		TestCase.assertTrue(list.contains(ct3));
		TestCase.assertTrue(list.contains(ct4));
		list = this.sapT.detect(aabb);
		TestCase.assertEquals(2, list.size());
		TestCase.assertTrue(list.contains(ct3));
		TestCase.assertTrue(list.contains(ct4));
		list = this.dynT.detect(aabb);
		TestCase.assertEquals(2, list.size());
		TestCase.assertTrue(list.contains(ct3));
		TestCase.assertTrue(list.contains(ct4));
		
		// should include:
		// ct2, ct3, and ct4
		aabb = new AABB(-0.75, -3.0, 2.0, 1.0);
		
		list = this.sapI.detect(aabb);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(ct2));
		TestCase.assertTrue(list.contains(ct3));
		TestCase.assertTrue(list.contains(ct4));
		list = this.sapBF.detect(aabb);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(ct2));
		TestCase.assertTrue(list.contains(ct3));
		TestCase.assertTrue(list.contains(ct4));
		list = this.sapT.detect(aabb);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(ct2));
		TestCase.assertTrue(list.contains(ct3));
		TestCase.assertTrue(list.contains(ct4));
		list = this.dynT.detect(aabb);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(ct2));
		TestCase.assertTrue(list.contains(ct3));
		TestCase.assertTrue(list.contains(ct4));
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
		this.sapI.add(ct1); this.sapI.add(ct2); this.sapI.add(ct3); this.sapI.add(ct4);
		this.sapBF.add(ct1); this.sapBF.add(ct2); this.sapBF.add(ct3); this.sapBF.add(ct4);
		this.sapT.add(ct1); this.sapT.add(ct2); this.sapT.add(ct3); this.sapT.add(ct4);
		this.dynT.add(ct1); this.dynT.add(ct2); this.dynT.add(ct3); this.dynT.add(ct4);
		
		List<CollidableTest> list;
		
		// ray that points in the positive x direction and starts at the origin
		Ray r = new Ray(new Vector2(1.0, 0.0));
		// infinite length
		double l = 0.0;
		
		list = this.sapI.raycast(r, l);
		TestCase.assertEquals(0, list.size());
		list = this.sapBF.raycast(r, l);
		TestCase.assertEquals(0, list.size());
		list = this.sapT.raycast(r, l);
		TestCase.assertEquals(0, list.size());
		list = this.dynT.raycast(r, l);
		TestCase.assertEquals(0, list.size());
		
		// try a different ray
		r = new Ray(new Vector2(-3.0, 0.75), new Vector2(1.0, 0.0));
		list = this.sapI.raycast(r, l);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(ct1));
		TestCase.assertTrue(list.contains(ct2));
		TestCase.assertTrue(list.contains(ct4));
		list = this.sapBF.raycast(r, l);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(ct1));
		TestCase.assertTrue(list.contains(ct2));
		TestCase.assertTrue(list.contains(ct4));
		list = this.sapT.raycast(r, l);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(ct1));
		TestCase.assertTrue(list.contains(ct2));
		TestCase.assertTrue(list.contains(ct4));
		list = this.dynT.raycast(r, l);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(ct1));
		TestCase.assertTrue(list.contains(ct2));
		TestCase.assertTrue(list.contains(ct4));
		
		// try one more ray
		r = new Ray(new Vector2(-1.0, -1.0), new Vector2(0.85, 0.35));
		list = this.sapI.raycast(r, l);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(ct1));
		TestCase.assertTrue(list.contains(ct2));
		TestCase.assertTrue(list.contains(ct4));
		list = this.sapBF.raycast(r, l);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(ct1));
		TestCase.assertTrue(list.contains(ct2));
		TestCase.assertTrue(list.contains(ct4));
		list = this.sapT.raycast(r, l);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(ct1));
		TestCase.assertTrue(list.contains(ct2));
		TestCase.assertTrue(list.contains(ct4));
		list = this.dynT.raycast(r, l);
		TestCase.assertEquals(3, list.size());
		TestCase.assertTrue(list.contains(ct1));
		TestCase.assertTrue(list.contains(ct2));
		TestCase.assertTrue(list.contains(ct4));
	}
	
	/**
	 * Tests the get/set expansion methods.
	 */
	@Test
	public void expansion() {
		// test the default
		TestCase.assertEquals(BroadphaseDetector.DEFAULT_AABB_EXPANSION, this.sapI.getAABBExpansion());
		TestCase.assertEquals(BroadphaseDetector.DEFAULT_AABB_EXPANSION, this.sapBF.getAABBExpansion());
		TestCase.assertEquals(BroadphaseDetector.DEFAULT_AABB_EXPANSION, this.sapT.getAABBExpansion());
		TestCase.assertEquals(BroadphaseDetector.DEFAULT_AABB_EXPANSION, this.dynT.getAABBExpansion());
		
		// test changing the expansion
		this.sapI.setAABBExpansion(0.3);
		this.sapBF.setAABBExpansion(0.3);
		this.sapT.setAABBExpansion(0.3);
		this.dynT.setAABBExpansion(0.3);
		TestCase.assertEquals(0.3, this.sapI.getAABBExpansion());
		TestCase.assertEquals(0.3, this.sapBF.getAABBExpansion());
		TestCase.assertEquals(0.3, this.sapT.getAABBExpansion());
		TestCase.assertEquals(0.3, this.dynT.getAABBExpansion());
		
		// test the new expansion value
		CollidableTest ct = new CollidableTest(Geometry.createCircle(1.0));
		
		// add the item to the broadphases
		this.sapI.add(ct);
		this.sapBF.add(ct);
		this.sapT.add(ct);
		this.dynT.add(ct);
		
		// make sure they are there
		AABB aabbSapI = this.sapI.getAABB(ct);
		AABB aabbSapBF = this.sapBF.getAABB(ct);
		AABB aabbSapT = this.sapT.getAABB(ct);
		AABB aabbDynT = this.dynT.getAABB(ct);
		
		AABB aabb = ct.createAABB();
		// don't forget that the aabb is expanded
		aabb.expand(0.3);
		TestCase.assertTrue(isEqual(aabbSapI, aabb));
		TestCase.assertTrue(isEqual(aabbSapBF, aabb));
		TestCase.assertTrue(isEqual(aabbSapT, aabb));
		TestCase.assertTrue(isEqual(aabbDynT, aabb));
	}
}
