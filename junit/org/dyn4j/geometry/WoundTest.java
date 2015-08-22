/*
 * Copyright (c) 2010-2015 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.geometry;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test case for the {@link Wound} class.
 * @author William Bittle
 * @version 3.1.0
 * @since 3.1.0
 */
public class WoundTest {
	/**
	 * Tests the getRadius method.
	 */
	@Test
	public void getRadius() {
		Polygon p = Geometry.createUnitCirclePolygon(5, 0.5);
		TestCase.assertEquals(0.500, p.getRadius(), 1.0e-3);
		
		Segment s = Geometry.createHorizontalSegment(4.0);
		TestCase.assertEquals(2.000, s.getRadius(), 1.0e-3);
		
		Rectangle r = Geometry.createRectangle(3.0, 2.0);
		TestCase.assertEquals(1.802, r.getRadius(), 1.0e-3);
		
		Triangle t = Geometry.createEquilateralTriangle(2.0);
		TestCase.assertEquals(1.333, t.getRadius(), 1.0e-3);
	}
	
	/**
	 * Tests the getRadius(Vector2) method.
	 */
	@Test
	public void getRadiusCenter() {
		Vector2 c = new Vector2(1.0, 0.0);
		
		Polygon p = Geometry.createUnitCirclePolygon(5, 0.5);
		TestCase.assertEquals(1.434, p.getRadius(c), 1.0e-3);
		
		Segment s = Geometry.createHorizontalSegment(4.0);
		TestCase.assertEquals(3.000, s.getRadius(c), 1.0e-3);
		
		Rectangle r = Geometry.createRectangle(3.0, 2.0);
		TestCase.assertEquals(2.692, r.getRadius(c), 1.0e-3);
		
		Triangle t = Geometry.createEquilateralTriangle(2.0);
		TestCase.assertEquals(2.255, t.getRadius(c), 1.0e-3);
	}
}
