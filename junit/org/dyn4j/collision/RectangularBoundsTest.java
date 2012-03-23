/*
 * Copyright (c) 2012 William Bittle  http://www.dyn4j.org/
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

import junit.framework.TestCase;

import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.RectangularBounds;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for the {@link RectangularBounds} class.
 * @author William Bittle
 * @version 3.0.4
 * @since 1.0.0
 */
public class RectangularBoundsTest {
	/** The {@link Bounds} to test with */
	private RectangularBounds rb;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		// create some bounds [-10, 10]
		this.rb = new RectangularBounds(new Rectangle(20.0, 20.0));
	}

	/**
	 * Tests creating a {@link RectangularBounds} with null bounds.
	 * @since 3.0.4
	 */
	@Test(expected = NullPointerException.class)
	public void createNullBounds() {
		new RectangularBounds(null);
	}
	
	/**
	 * Tests the isOutside method on a {@link Circle}.
	 */
	@Test
	public void isOutsideCircle() {
		// create some shapes
		Circle c = new Circle(1.0);
		CollidableTest ct = new CollidableTest(c);
		
		// should be in
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test half way in and out
		ct.transform.translate(9.5, 0.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.6, 0.0);
		TestCase.assertTrue(rb.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-1.5, 9.5);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test moving the bounds
		rb.translate(2.0, 1.0);
		
		// test half way in and out
		ct.transform.translate(2.0, 0.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.6, 0.0);
		TestCase.assertTrue(rb.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-1.5, 1.5);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test rotating the bounds
		rb.rotate(Math.toRadians(45.0), 2.0, 1.0);
		
		// test half way out of a corner
		ct.transform.setTranslation(0.0, 0.0);
		ct.transform.translate(16.5, 0.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.0, 0.0);
		TestCase.assertTrue(rb.isOutside(ct));
		
		// test half way out of a side
		ct.transform.setTranslation(10.25, 9.25);
		TestCase.assertFalse(rb.isOutside(ct));
	}
	
	/**
	 * Tests the isOutside method on a {@link Rectangle}.
	 */
	@Test
	public void isOutsideRectangle() {
		// create some shapes
		Rectangle r = new Rectangle(1.0, 1.0);
		CollidableTest ct = new CollidableTest(r);
		
		// should be in
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test half way in and out
		ct.transform.translate(10.0, 0.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(0.6, 0.0);
		TestCase.assertTrue(rb.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-0.6, 10.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test moving the bounds
		rb.translate(2.0, 1.0);
		
		// test half way in and out
		ct.transform.translate(2.0, 0.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.6, 0.0);
		TestCase.assertTrue(rb.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-1.5, 1.5);
		TestCase.assertFalse(rb.isOutside(ct));

		// test rotating the bounds
		rb.rotate(Math.toRadians(45.0), 2.0, 1.0);
		
		// test half way out of a corner
		ct.transform.setTranslation(0.0, 0.0);
		ct.transform.translate(16.5, 0.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.0, 0.0);
		TestCase.assertTrue(rb.isOutside(ct));
		
		// test half way out of a side
		ct.transform.setTranslation(10.25, 9.25);
		TestCase.assertFalse(rb.isOutside(ct));
	}
	
	/**
	 * Tests the isOutside method on a {@link Polygon}.
	 */
	@Test
	public void isOutsidePolygon() {
		// create some shapes
		Polygon p = Geometry.createUnitCirclePolygon(6, 0.5);
		CollidableTest ct = new CollidableTest(p);
		
		// should be in
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test half way in and out
		ct.transform.translate(10.0, 0.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(0.6, 0.0);
		TestCase.assertTrue(rb.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-0.6, 10.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test moving the bounds
		rb.translate(2.0, 1.0);
		
		// test half way in and out
		ct.transform.translate(2.0, 0.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.6, 0.0);
		TestCase.assertTrue(rb.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-1.5, 1.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test rotating the bounds
		rb.rotate(Math.toRadians(45.0), 2.0, 1.0);
		
		// test half way out of a corner
		ct.transform.setTranslation(0.0, 0.0);
		ct.transform.translate(16.5, 0.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.0, 0.0);
		TestCase.assertTrue(rb.isOutside(ct));
		
		// test half way out of a side
		ct.transform.setTranslation(10.25, 9.25);
		TestCase.assertFalse(rb.isOutside(ct));
	}
	
	/**
	 * Tests the isOutside method on a {@link Triangle}.
	 */
	@Test
	public void isOutsideTriangle() {
		// create some shapes
		Triangle t = new Triangle(
				new Vector2( 0.0,  0.5),
				new Vector2(-0.5, -0.5),
				new Vector2( 0.5, -0.5)
			);
		CollidableTest ct = new CollidableTest(t);
		
		// should be in
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test half way in and out
		ct.transform.translate(10.0, 0.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(0.6, 0.0);
		TestCase.assertTrue(rb.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-0.6, 10.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test moving the bounds
		rb.translate(2.0, 1.0);
		
		// test half way in and out
		ct.transform.translate(2.0, 0.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.6, 0.0);
		TestCase.assertTrue(rb.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-1.5, 1.5);
		TestCase.assertFalse(rb.isOutside(ct));

		// test rotating the bounds
		rb.rotate(Math.toRadians(45.0), 2.0, 1.0);
		
		// test half way out of a corner
		ct.transform.setTranslation(0.0, 0.0);
		ct.transform.translate(16.5, 0.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.0, 0.0);
		TestCase.assertTrue(rb.isOutside(ct));
		
		// test half way out of a side
		ct.transform.setTranslation(10.25, 9.25);
		TestCase.assertFalse(rb.isOutside(ct));
	}
	
	/**
	 * Tests the isOutside method on a {@link Segment}.
	 */
	@Test
	public void isOutsideSegment() {
		// create some shapes
		Segment s = new Segment(new Vector2(0.5, -0.5), new Vector2(-0.5, 0.5));
		CollidableTest ct = new CollidableTest(s);
		
		// should be in
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test half way in and out
		ct.transform.translate(10.0, 0.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(0.6, 0.0);
		TestCase.assertTrue(rb.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-0.6, 10.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test moving the bounds
		rb.translate(2.0, 1.0);
		
		// test half way in and out
		ct.transform.translate(2.0, 0.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.6, 0.0);
		TestCase.assertTrue(rb.isOutside(ct));
		
		// test half way out a corner
		ct.transform.translate(-1.5, 1.5);
		TestCase.assertFalse(rb.isOutside(ct));

		// test rotating the bounds
		rb.rotate(Math.toRadians(45.0), 2.0, 1.0);
		
		// test half way out of a corner
		ct.transform.setTranslation(0.0, 0.0);
		ct.transform.translate(16.5, 0.0);
		TestCase.assertFalse(rb.isOutside(ct));
		
		// test all the way out
		ct.transform.translate(1.0, 0.0);
		TestCase.assertTrue(rb.isOutside(ct));
		
		// test half way out of a side
		ct.transform.setTranslation(10.25, 9.25);
		TestCase.assertFalse(rb.isOutside(ct));
	}
	
	/**
	 * Tests setting the bounds to null.
	 * @since 3.0.4
	 */
	@Test(expected = NullPointerException.class)
	public void setNullBounds() {
		rb.setBounds(null);
	}
}
