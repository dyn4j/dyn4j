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
package org.dyn4j.collision.narrowphase;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests the {@link MinkowskiSum} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class MinkowskiSumTest {
	/**
	 * Tests the creation of a {@link MinkowskiSum}.
	 */
	@Test
	public void createSuccess() {
		Convex c1 = Geometry.createCircle(1);
		Convex c2 = Geometry.createCircle(1);
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		tx2.translate(1.0, 0.0);
		
		MinkowskiSum sum = new MinkowskiSum(c1, tx1, c2, tx2);
		
		TestCase.assertEquals(c1, sum.getConvex1());
		TestCase.assertEquals(tx1, sum.getTransform1());
		TestCase.assertEquals(c2, sum.getConvex2());
		TestCase.assertEquals(tx2, sum.getTransform2());
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void tostring() {
		Convex c1 = Geometry.createCircle(1);
		Convex c2 = Geometry.createCircle(1);
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		MinkowskiSum sum = new MinkowskiSum(c1, tx1, c2, tx2);
		
		TestCase.assertNotNull(sum.toString());
	}
	
	/**
	 * Tests getting a support point.
	 */
	@Test
	public void getSupportPoint() {
		Convex c1 = Geometry.createCircle(1);
		Convex c2 = Geometry.createCircle(1);
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		tx2.translate(1.0, 0.0);
		
		MinkowskiSum sum = new MinkowskiSum(c1, tx1, c2, tx2);
		
		Vector2 p = sum.getSupportPoint(new Vector2(0.0, 1.0));
		TestCase.assertEquals(-1.000, p.x, 1e-3);
		TestCase.assertEquals(2.000, p.y, 1e-3);
		
		p = sum.getSupportPoint(new Vector2(0.0, -1.0));
		TestCase.assertEquals(-1.000, p.x, 1e-3);
		TestCase.assertEquals(-2.000, p.y, 1e-3);
		
		p = sum.getSupportPoint(new Vector2(1.0, 0.0));
		TestCase.assertEquals(1.000, p.x, 1e-3);
		TestCase.assertEquals(0.000, p.y, 1e-3);
		
		p = sum.getSupportPoint(new Vector2(-1.0, 0.0));
		TestCase.assertEquals(-3.000, p.x, 1e-3);
		TestCase.assertEquals(0.000, p.y, 1e-3);
	}
	
	/**
	 * Tests getting support points.
	 */
	@Test
	public void getSupportPoints() {
		Convex c1 = Geometry.createCircle(1);
		Convex c2 = Geometry.createCircle(1);
		Transform tx1 = new Transform();
		Transform tx2 = new Transform();
		
		tx2.translate(1.0, 0.0);
		
		MinkowskiSum sum = new MinkowskiSum(c1, tx1, c2, tx2);
		
		MinkowskiSumPoint p = sum.getSupportPoints(new Vector2(0.0, 1.0));
		TestCase.assertEquals(-1.000, p.point.x, 1e-3);
		TestCase.assertEquals(2.000, p.point.y, 1e-3);
		TestCase.assertEquals(0.000, p.supportPoint1.x, 1e-3);
		TestCase.assertEquals(1.000, p.supportPoint1.y, 1e-3);
		TestCase.assertEquals(1.000, p.supportPoint2.x, 1e-3);
		TestCase.assertEquals(-1.000, p.supportPoint2.y, 1e-3);
		
		p = sum.getSupportPoints(new Vector2(0.0, -1.0));
		TestCase.assertEquals(-1.000, p.point.x, 1e-3);
		TestCase.assertEquals(-2.000, p.point.y, 1e-3);
		TestCase.assertEquals(0.000, p.supportPoint1.x, 1e-3);
		TestCase.assertEquals(-1.000, p.supportPoint1.y, 1e-3);
		TestCase.assertEquals(1.000, p.supportPoint2.x, 1e-3);
		TestCase.assertEquals(1.000, p.supportPoint2.y, 1e-3);
		
		p = sum.getSupportPoints(new Vector2(1.0, 0.0));
		TestCase.assertEquals(1.000, p.point.x, 1e-3);
		TestCase.assertEquals(0.000, p.point.y, 1e-3);
		TestCase.assertEquals(1.000, p.supportPoint1.x, 1e-3);
		TestCase.assertEquals(0.000, p.supportPoint1.y, 1e-3);
		TestCase.assertEquals(0.000, p.supportPoint2.x, 1e-3);
		TestCase.assertEquals(0.000, p.supportPoint2.y, 1e-3);
		
		p = sum.getSupportPoints(new Vector2(-1.0, 0.0));
		TestCase.assertEquals(-3.000, p.point.x, 1e-3);
		TestCase.assertEquals(0.000, p.point.y, 1e-3);
		TestCase.assertEquals(-1.000, p.supportPoint1.x, 1e-3);
		TestCase.assertEquals(0.000, p.supportPoint1.y, 1e-3);
		TestCase.assertEquals(2.000, p.supportPoint2.x, 1e-3);
		TestCase.assertEquals(0.000, p.supportPoint2.y, 1e-3);
	}
}
