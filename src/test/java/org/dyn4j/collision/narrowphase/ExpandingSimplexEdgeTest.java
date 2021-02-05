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

import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests the {@link ExpandingSimplexEdge} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class ExpandingSimplexEdgeTest {
	/**
	 * Tests the creation of an simplex edge.
	 */
	@Test
	public void createSuccess() {
		Vector2 a = new Vector2(-1.0, -1.0);
		Vector2 b = new Vector2(2.0, -1.0);
		Vector2 c = new Vector2(0.0, 2.0);
		
		ExpandingSimplexEdge edge = new ExpandingSimplexEdge(a, b, 1);
		
		TestCase.assertEquals(-1.0, edge.point1.x, 1e-3);
		TestCase.assertEquals(-1.0, edge.point1.y, 1e-3);
		TestCase.assertEquals(2.0, edge.point2.x, 1e-3);
		TestCase.assertEquals(-1.0, edge.point2.y, 1e-3);
		TestCase.assertEquals(1.0, edge.distance, 1e-3);
		TestCase.assertEquals(0.0, edge.normal.x, 1e-3);
		TestCase.assertEquals(-1.0, edge.normal.y, 1e-3);
		TestCase.assertEquals(1.000, edge.normal.getMagnitude(), 1e-3);
		
		edge = new ExpandingSimplexEdge(b, c, 1);
		
		TestCase.assertEquals(2.0, edge.point1.x, 1e-3);
		TestCase.assertEquals(-1.0, edge.point1.y, 1e-3);
		TestCase.assertEquals(0.0, edge.point2.x, 1e-3);
		TestCase.assertEquals(2.0, edge.point2.y, 1e-3);
		TestCase.assertEquals(1.109, edge.distance, 1e-3);
		TestCase.assertEquals(0.832, edge.normal.x, 1e-3);
		TestCase.assertEquals(0.554, edge.normal.y, 1e-3);
		TestCase.assertEquals(1.000, edge.normal.getMagnitude(), 1e-3);
		
		edge = new ExpandingSimplexEdge(c, a, 1);
		
		TestCase.assertEquals(0.0, edge.point1.x, 1e-3);
		TestCase.assertEquals(2.0, edge.point1.y, 1e-3);
		TestCase.assertEquals(-1.0, edge.point2.x, 1e-3);
		TestCase.assertEquals(-1.0, edge.point2.y, 1e-3);
		TestCase.assertEquals(0.632, edge.distance, 1e-3);
		TestCase.assertEquals(-0.948, edge.normal.x, 1e-3);
		TestCase.assertEquals(0.316, edge.normal.y, 1e-3);
		TestCase.assertEquals(1.000, edge.normal.getMagnitude(), 1e-3);
		
		// test opposite winding
		edge = new ExpandingSimplexEdge(a, c, -1);
		
		TestCase.assertEquals(-1.0, edge.point1.x, 1e-3);
		TestCase.assertEquals(-1.0, edge.point1.y, 1e-3);
		TestCase.assertEquals(0.0, edge.point2.x, 1e-3);
		TestCase.assertEquals(2.0, edge.point2.y, 1e-3);
		TestCase.assertEquals(0.632, edge.distance, 1e-3);
		TestCase.assertEquals(-0.948, edge.normal.x, 1e-3);
		TestCase.assertEquals(0.316, edge.normal.y, 1e-3);
		TestCase.assertEquals(1.000, edge.normal.getMagnitude(), 1e-3);
	}
	
	/**
	 * Tests the compareTo method.
	 */
	@Test
	public void compareTo() {
		Vector2 a = new Vector2(-1.0, -1.0);
		Vector2 b = new Vector2(2.0, -1.0);
		Vector2 c = new Vector2(0.0, 2.0);
		
		ExpandingSimplexEdge edge1 = new ExpandingSimplexEdge(a, b, 1);
		ExpandingSimplexEdge edge2 = new ExpandingSimplexEdge(b, c, 1);
		ExpandingSimplexEdge edge3 = new ExpandingSimplexEdge(c, a, 1);
		
		TestCase.assertEquals(-1, edge1.compareTo(edge2));
		TestCase.assertEquals(1, edge2.compareTo(edge1));
		TestCase.assertEquals(1, edge1.compareTo(edge3));
		TestCase.assertEquals(-1, edge3.compareTo(edge1));
		
		TestCase.assertEquals(0, edge1.compareTo(edge1));
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void tostring() {
		Vector2 a = new Vector2(-1.0, -1.0);
		Vector2 b = new Vector2(2.0, -1.0);
		
		ExpandingSimplexEdge edge1 = new ExpandingSimplexEdge(a, b, 1);
		
		TestCase.assertNotNull(edge1.toString());
	}
}
