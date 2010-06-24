/*
 * Copyright (c) 2010, William Bittle
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
package org.dyn4j.game2d.geometry;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test case for the {@link Geometry} class.
 * @author William Bittle
 */
public class GeometryTest {
	/**
	 * Tests the getAverageCenter method.
	 * <p>
	 * This test also shows that the average method can produce an incorrect
	 * center of mass when vertices are more dense at any place along the perimeter.
	 */
	@Test
	public void getAverageCenter() {
		Vector2[] vertices = new Vector2[6];
		vertices[0] = new Vector2(-2.0, 1.0);
		vertices[1] = new Vector2(-1.0, 2.0);
		vertices[2] = new Vector2(1.2, 0.5);
		vertices[3] = new Vector2(1.3, 0.3);
		vertices[4] = new Vector2(1.4, 0.2);
		vertices[5] = new Vector2(0.0, -1.0);
		
		Vector2 c = Geometry.getAverageCenter(vertices);
		
		TestCase.assertEquals(0.150, c.x, 1.0e-3);
		TestCase.assertEquals(0.500, c.y, 1.0e-3);
	}
	
	/**
	 * Tests the getAreaWeightedCenter method.
	 */
	@Test
	public void getAreaWeightedCenter() {
		Vector2[] vertices = new Vector2[6];
		vertices[0] = new Vector2(-2.0, 1.0);
		vertices[1] = new Vector2(-1.0, 2.0);
		// test dense area of points
		vertices[2] = new Vector2(1.2, 0.5);
		vertices[3] = new Vector2(1.3, 0.3);
		vertices[4] = new Vector2(1.4, 0.2);
		vertices[5] = new Vector2(0.0, -1.0);
		
		Vector2 c = Geometry.getAreaWeightedCenter(vertices);
		
		// note the x is closer to the "real" center of the object
		TestCase.assertEquals(-0.318, c.x, 1.0e-3);
		TestCase.assertEquals( 0.527, c.y, 1.0e-3);
	}
	
	/**
	 * Test case for the unitCirclePolygon methods.
	 */
	@Test
	public void unitCirclePolygon() {
		Polygon p = Geometry.createUnitCirclePolygon(5, 0.5);
		// no exception indicates the generated polygon is valid
		// test that the correct vertices are created
		TestCase.assertEquals( 0.154, p.vertices[4].x, 1.0e-3);
		TestCase.assertEquals(-0.475, p.vertices[4].y, 1.0e-3);
		TestCase.assertEquals(-0.404, p.vertices[3].x, 1.0e-3);
		TestCase.assertEquals(-0.293, p.vertices[3].y, 1.0e-3);
		TestCase.assertEquals(-0.404, p.vertices[2].x, 1.0e-3);
		TestCase.assertEquals( 0.293, p.vertices[2].y, 1.0e-3);
		TestCase.assertEquals( 0.154, p.vertices[1].x, 1.0e-3);
		TestCase.assertEquals( 0.475, p.vertices[1].y, 1.0e-3);
		TestCase.assertEquals( 0.500, p.vertices[0].x, 1.0e-3);
		TestCase.assertEquals( 0.000, p.vertices[0].y, 1.0e-3);
		
		Vector2 v11 = p.vertices[0];
		
		p = Geometry.createUnitCirclePolygon(5, 0.5, Math.PI / 2.0);
		// no exception indicates the generated polygon is valid
		// test that the correct vertices are created
		TestCase.assertEquals( 0.475, p.vertices[4].x, 1.0e-3);
		TestCase.assertEquals( 0.154, p.vertices[4].y, 1.0e-3);
		TestCase.assertEquals( 0.293, p.vertices[3].x, 1.0e-3);
		TestCase.assertEquals(-0.404, p.vertices[3].y, 1.0e-3);
		TestCase.assertEquals(-0.293, p.vertices[2].x, 1.0e-3);
		TestCase.assertEquals(-0.404, p.vertices[2].y, 1.0e-3);
		TestCase.assertEquals(-0.475, p.vertices[1].x, 1.0e-3);
		TestCase.assertEquals( 0.154, p.vertices[1].y, 1.0e-3);
		TestCase.assertEquals( 0.000, p.vertices[0].x, 1.0e-3);
		TestCase.assertEquals( 0.500, p.vertices[0].y, 1.0e-3);
		
		Vector2 v21 = p.vertices[0];
		
		// the angle between any two vertices of the two polygons should be PI / 2
		double angle = v11.getAngleBetween(v21);
		TestCase.assertEquals(Math.PI / 2.0, angle, 1.0e-3);
	}
}
