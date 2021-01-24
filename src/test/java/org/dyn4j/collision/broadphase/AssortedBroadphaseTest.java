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

import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.TestCollisionBody;
import org.dyn4j.geometry.Geometry;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * A collection of broadphase tests that are specific to the implementations.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class AssortedBroadphaseTest {
	/**
	 * Tests creating a broadphase adapter detector successfully.
	 */
	public void adapterSuccess() {
		BroadphaseFilter<CollisionItem<TestCollisionBody, Fixture>> bf = new CollisionItemBroadphaseFilter<TestCollisionBody, Fixture>();
		AABBProducer<CollisionItem<TestCollisionBody, Fixture>> p = new CollisionItemAABBProducer<TestCollisionBody, Fixture>();
		AABBExpansionMethod<CollisionItem<TestCollisionBody, Fixture>> e = new NullAABBExpansionMethod<CollisionItem<TestCollisionBody, Fixture>>();
		
		BroadphaseDetector<CollisionItem<TestCollisionBody, Fixture>> bp = new DynamicAABBTree<CollisionItem<TestCollisionBody, Fixture>>(bf, p, e);
		CollisionItemBroadphaseDetector<TestCollisionBody, Fixture> cibd = new CollisionItemBroadphaseDetectorAdapter<TestCollisionBody, Fixture>(bp);
		
		TestCase.assertNotNull(cibd);
		TestCase.assertNotNull(cibd.getBroadphaseFilter());
		TestCase.assertNotNull(cibd.getAABBProducer());
		TestCase.assertNotNull(cibd.getAABBExpansionMethod());
		TestCase.assertNotNull(cibd.getDecoratedBroadphaseDetector());
		
		TestCase.assertEquals(bf, cibd.getBroadphaseFilter());
		TestCase.assertEquals(p, cibd.getAABBProducer());
		TestCase.assertEquals(e, cibd.getAABBExpansionMethod());
		TestCase.assertEquals(bp, cibd.getDecoratedBroadphaseDetector());
	}
	
	/**
	 * Tests methods specific to the DynamicAABBTree detector.
	 */
	@Test
	public void dynamicAABBSpecial() {
		BroadphaseFilter<CollisionItem<TestCollisionBody, Fixture>> broadphaseFilter = new CollisionItemBroadphaseFilter<TestCollisionBody, Fixture>();
    	AABBProducer<CollisionItem<TestCollisionBody, Fixture>> aabbProducer = new CollisionItemAABBProducer<TestCollisionBody, Fixture>();
    	AABBExpansionMethod<CollisionItem<TestCollisionBody, Fixture>> aabbExpansionMethod = new StaticValueAABBExpansionMethod<CollisionItem<TestCollisionBody, Fixture>>(0.2);
    	
		DynamicAABBTree<CollisionItem<TestCollisionBody, Fixture>> broadphase = new DynamicAABBTree<CollisionItem<TestCollisionBody, Fixture>>(
				broadphaseFilter,
				aabbProducer,
				aabbExpansionMethod);
		
		TestCase.assertTrue(broadphase.isValid());
		TestCase.assertEquals(0, broadphase.getHeight());
		TestCase.assertEquals(0.0, broadphase.getPerimeterRatio());
		
		TestCollisionBody ct1 = new TestCollisionBody(Geometry.createCircle(1.0));
		broadphase.add(new BroadphaseItem<TestCollisionBody, Fixture>(ct1, ct1.getFixture(0)));
		
		TestCase.assertTrue(broadphase.isValid());
		TestCase.assertEquals(0, broadphase.getHeight());
		TestCase.assertTrue(broadphase.getPerimeterRatio() > 0);
		
		TestCollisionBody ct2 = new TestCollisionBody(Geometry.createCircle(1.0));
		ct2.addFixture(Geometry.createCircle(0.5));
		broadphase.add(new BroadphaseItem<TestCollisionBody, Fixture>(ct2, ct2.getFixture(0)));
		broadphase.add(new BroadphaseItem<TestCollisionBody, Fixture>(ct2, ct2.getFixture(1)));
		
		TestCase.assertTrue(broadphase.isValid());
		TestCase.assertTrue(broadphase.getHeight() > 0);
		TestCase.assertTrue(broadphase.getPerimeterRatio() > 0);
		
		TestCollisionBody ct3 = new TestCollisionBody(Geometry.createRectangle(1.0, 0.5));
		TestCollisionBody ct4 = new TestCollisionBody(Geometry.createVerticalSegment(2.0));
		TestCollisionBody ct5 = new TestCollisionBody(Geometry.createCircle(1.0));
		TestCollisionBody ct6 = new TestCollisionBody(Geometry.createCircle(1.0));
		
		ct3.translate(0.5, -2.0);
		ct4.translate(1.0, 1.0);
		ct5.translate(1.0, 3.0);
		ct6.translate(3.0, 1.0);
		
		// add the items to the broadphases
		broadphase.add(new BroadphaseItem<TestCollisionBody, Fixture>(ct3, ct3.getFixture(0))); 
		broadphase.add(new BroadphaseItem<TestCollisionBody, Fixture>(ct4, ct4.getFixture(0)));
		broadphase.add(new BroadphaseItem<TestCollisionBody, Fixture>(ct5, ct5.getFixture(0)));
		broadphase.add(new BroadphaseItem<TestCollisionBody, Fixture>(ct6, ct6.getFixture(0)));
		
		TestCase.assertTrue(broadphase.isValid());
	}
	
	/**
	 * Tests behavior specific to the brute force detector.
	 */
	@Test
	public void bruteForceSpecial() {
		BroadphaseFilter<CollisionItem<TestCollisionBody, Fixture>> bf = new CollisionItemBroadphaseFilter<TestCollisionBody, Fixture>();
    	AABBProducer<CollisionItem<TestCollisionBody, Fixture>> p = new CollisionItemAABBProducer<TestCollisionBody, Fixture>();
    	
		BruteForceBroadphase<CollisionItem<TestCollisionBody, Fixture>> bp = new BruteForceBroadphase<CollisionItem<TestCollisionBody, Fixture>>(bf, p);
		
		TestCase.assertNotNull(bp);
		TestCase.assertNotNull(bp.getBroadphaseFilter());
		TestCase.assertNotNull(bp.getAABBProducer());
		TestCase.assertNotNull(bp.getAABBExpansionMethod());
		TestCase.assertEquals(NullAABBExpansionMethod.class, bp.getAABBExpansionMethod().getClass());
		
		TestCase.assertEquals(bf, bp.getBroadphaseFilter());
		TestCase.assertEquals(p, bp.getAABBProducer());
		
		TestCase.assertFalse(bp.isUpdateTrackingEnabled());
		TestCase.assertFalse(bp.isUpdateTrackingSupported());
		
		bp.setUpdateTrackingEnabled(true);
		TestCase.assertFalse(bp.isUpdateTrackingEnabled());
	}
	
	/**
	 * Tests the helper classes.
	 */
	@Test
	public void helpers() {
		DynamicAABBTreeLeaf<TestCollisionBody> leaf = new DynamicAABBTreeLeaf<TestCollisionBody>(new TestCollisionBody());
		TestCase.assertTrue(leaf.isLeaf());
		TestCase.assertNotNull(leaf.toString());
		
		DynamicAABBTreeNode node = new DynamicAABBTreeNode();
		node.left = new DynamicAABBTreeNode();
		node.right = new DynamicAABBTreeNode();
		TestCase.assertFalse(node.isLeaf());
		TestCase.assertNotNull(node.toString());
	}
}
