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
package org.dyn4j.collision;

import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link AxisAlignedBounds} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 3.1.1
 */
public class AxisAlignedBoundsTest {
	/**
	 * Tests the constructor and the width and height getters.
	 */
	@Test
	public void create() {
		AxisAlignedBounds ab = new AxisAlignedBounds(10.0, 7.0);
		TestCase.assertEquals(10.0, ab.getWidth());
		TestCase.assertEquals(7.0, ab.getHeight());
	}
	
	/**
	 * Tests creating one with zero width.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroWidth() {
		new AxisAlignedBounds(0.0, 7.0);
	}

	/**
	 * Tests creating one with zero height.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroHeight() {
		new AxisAlignedBounds(10.0, 0.0);
	}
	
	/**
	 * Tests creating one with negative width.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeWidth() {
		new AxisAlignedBounds(-10.0, 7.0);
	}
	
	/**
	 * Tests creating one with negative height.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeHeight() {
		new AxisAlignedBounds(10.0, -40.0);
	}
	
	/**
	 * Tests the getBounds method.
	 */
	@Test
	public void getBounds() {
		AxisAlignedBounds bounds = new AxisAlignedBounds(20.0, 20.0);
		
		AABB aabb = bounds.getBounds();
		// should be centered about the origin
		TestCase.assertEquals(-10.0, aabb.getMinX());
		TestCase.assertEquals(-10.0, aabb.getMinY());
		TestCase.assertEquals(10.0, aabb.getMaxX());
		TestCase.assertEquals(10.0, aabb.getMaxY());
		
		// move it a bit
		bounds.translate(1.0, -2.0);
		aabb = bounds.getBounds();
		TestCase.assertEquals(-9.0, aabb.getMinX());
		TestCase.assertEquals(-12.0, aabb.getMinY());
		TestCase.assertEquals(11.0, aabb.getMaxX());
		TestCase.assertEquals(8.0, aabb.getMaxY());
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void print() {
		AxisAlignedBounds bounds = new AxisAlignedBounds(20.0, 20.0);
		
		String str = bounds.toString();
		TestCase.assertNotNull(str);
		TestCase.assertTrue(str.length() > 0);
	}
	
	/**
	 * Tests the isOutside method on a {@link Circle}.
	 */
	@Test
	public void isOutsideCircle() {
		AxisAlignedBounds bounds = new AxisAlignedBounds(20.0, 20.0);
		
		// create some shapes
		Circle c = new Circle(1.0);
		TestCollisionBody ct = new TestCollisionBody(c);
		
		// should be in
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test half way in and out
		ct.transform.translate(9.5, 0.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.6, 0.0);
		TestCase.assertTrue(bounds.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-1.5, 9.5);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test moving the bounds
		bounds.translate(2.0, 1.0);
		
		// test half way in and out
		ct.transform.translate(2.0, 0.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.6, 0.0);
		TestCase.assertTrue(bounds.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-1.5, 1.5);
		TestCase.assertFalse(bounds.isOutside(ct));
	}
	
	/**
	 * Tests the isOutside method on a {@link Ellipse}.
	 */
	@Test
	public void isOutsideEllipse() {
		AxisAlignedBounds bounds = new AxisAlignedBounds(20.0, 20.0);
		
		// create some shapes
		Ellipse c = new Ellipse(1.0, 0.5);
		TestCollisionBody ct = new TestCollisionBody(c);
		
		// should be in
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test half way in and out
		ct.transform.translate(9.5, 0.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.6, 0.0);
		TestCase.assertTrue(bounds.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-1.5, 9.5);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test moving the bounds
		bounds.translate(2.0, 1.0);
		
		// test half way in and out
		ct.transform.translate(2.0, 0.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.6, 0.0);
		TestCase.assertTrue(bounds.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-1.5, 1.5);
		TestCase.assertFalse(bounds.isOutside(ct));
	}
	
	/**
	 * Tests the isOutside method on a {@link Rectangle}.
	 */
	@Test
	public void isOutsideRectangle() {
		AxisAlignedBounds bounds = new AxisAlignedBounds(20.0, 20.0);
		
		// create some shapes
		Rectangle r = new Rectangle(1.0, 1.0);
		TestCollisionBody ct = new TestCollisionBody(r);
		
		// should be in
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test half way in and out
		ct.transform.translate(10.0, 0.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(0.6, 0.0);
		TestCase.assertTrue(bounds.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-0.6, 10.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test moving the bounds
		bounds.translate(2.0, 1.0);
		
		// test half way in and out
		ct.transform.translate(2.0, 0.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.6, 0.0);
		TestCase.assertTrue(bounds.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-1.5, 1.5);
		TestCase.assertFalse(bounds.isOutside(ct));
	}
	
	/**
	 * Tests the isOutside method on a {@link Polygon}.
	 */
	@Test
	public void isOutsidePolygon() {
		AxisAlignedBounds bounds = new AxisAlignedBounds(20.0, 20.0);
		
		// create some shapes
		Polygon p = Geometry.createUnitCirclePolygon(6, 0.5);
		TestCollisionBody ct = new TestCollisionBody(p);
		
		// should be in
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test half way in and out
		ct.transform.translate(10.0, 0.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(0.6, 0.0);
		TestCase.assertTrue(bounds.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-0.6, 10.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test moving the bounds
		bounds.translate(2.0, 1.0);
		
		// test half way in and out
		ct.transform.translate(2.0, 0.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.6, 0.0);
		TestCase.assertTrue(bounds.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-1.5, 1.0);
		TestCase.assertFalse(bounds.isOutside(ct));
	}
	
	/**
	 * Tests the isOutside method on a {@link Triangle}.
	 */
	@Test
	public void isOutsideTriangle() {
		AxisAlignedBounds bounds = new AxisAlignedBounds(20.0, 20.0);
		
		// create some shapes
		Triangle t = new Triangle(
				new Vector2( 0.0,  0.5),
				new Vector2(-0.5, -0.5),
				new Vector2( 0.5, -0.5)
			);
		TestCollisionBody ct = new TestCollisionBody(t);
		
		// should be in
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test half way in and out
		ct.transform.translate(10.0, 0.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(0.6, 0.0);
		TestCase.assertTrue(bounds.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-0.6, 10.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test moving the bounds
		bounds.translate(2.0, 1.0);
		
		// test half way in and out
		ct.transform.translate(2.0, 0.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.6, 0.0);
		TestCase.assertTrue(bounds.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-1.5, 1.5);
		TestCase.assertFalse(bounds.isOutside(ct));
	}
	
	/**
	 * Tests the isOutside method on a {@link Segment}.
	 */
	@Test
	public void isOutsideSegment() {
		AxisAlignedBounds bounds = new AxisAlignedBounds(20.0, 20.0);
		
		// create some shapes
		Segment s = new Segment(new Vector2(0.5, -0.5), new Vector2(-0.5, 0.5));
		TestCollisionBody ct = new TestCollisionBody(s);
		
		// should be in
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test half way in and out
		ct.transform.translate(10.0, 0.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(0.6, 0.0);
		TestCase.assertTrue(bounds.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-0.6, 10.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test moving the bounds
		bounds.translate(2.0, 1.0);
		
		// test half way in and out
		ct.transform.translate(2.0, 0.0);
		TestCase.assertFalse(bounds.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.6, 0.0);
		TestCase.assertTrue(bounds.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-1.5, 1.5);
		TestCase.assertFalse(bounds.isOutside(ct));
	}
	
	/**
	 * Tests the isOutside method on a {@link AABB}.
	 * @since 4.0.0
	 */
	@Test
	public void isOutsideAABB() {
		AxisAlignedBounds bounds = new AxisAlignedBounds(20.0, 20.0);
		
		// create some shapes
		AABB aabb = new AABB(0,0,1,1);
		
		// should be in
		TestCase.assertFalse(bounds.isOutside(aabb));
		
		// test half way in and out
		aabb.translate(9.5, 0.0);
		TestCase.assertFalse(bounds.isOutside(aabb));
		
		// test all the way out
		aabb.translate(1.5, 0.0);
		TestCase.assertTrue(bounds.isOutside(aabb));
		
		// test half way out a corner
		aabb.translate(-1.5, 9.5);
		TestCase.assertFalse(bounds.isOutside(aabb));
		
		// test moving the bounds
		bounds.translate(2.0, 1.0);
		
		// test half way in and out
		aabb.translate(2.0, 0.0);
		TestCase.assertFalse(bounds.isOutside(aabb));
		
		// test all the way out
		aabb.translate(1.0, 0.0);
		TestCase.assertTrue(bounds.isOutside(aabb));
		
		// test half way out a corner
		aabb.translate(-0.5, 1.0);
		TestCase.assertFalse(bounds.isOutside(aabb));
	}
}
