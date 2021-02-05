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

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests the {@link ExpandingSimplex} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class ExpandingSimplexTest {
	/**
	 * Tests the creation of an expanding simplex.
	 */
	@Test
	public void createSuccess() {
		List<Vector2> simplex = new ArrayList<Vector2>();
		simplex.add(new Vector2(-1.0, -1.0));
		simplex.add(new Vector2(2.0, -1.0));
		simplex.add(new Vector2(0.0, 2.0));
		
		ExpandingSimplex es = new ExpandingSimplex(simplex);
		
		TestCase.assertEquals(1, es.getWinding());
		TestCase.assertEquals(3, es.size());
		
		ExpandingSimplexEdge edge = es.getClosestEdge();
		TestCase.assertNotNull(edge);
		
		TestCase.assertEquals(0.0, edge.point1.x, 1e-3);
		TestCase.assertEquals(2.0, edge.point1.y, 1e-3);
		TestCase.assertEquals(-1.0, edge.point2.x, 1e-3);
		TestCase.assertEquals(-1.0, edge.point2.y, 1e-3);
		TestCase.assertEquals(0.632, edge.distance, 1e-3);
		TestCase.assertEquals(-0.948, edge.normal.x, 1e-3);
		TestCase.assertEquals(0.316, edge.normal.y, 1e-3);
	}
	
	/**
	 * Tests the winding detection.
	 */
	@Test
	public void getWinding() {
		List<Vector2> simplex = new ArrayList<Vector2>();
		simplex.add(new Vector2(-1.0, -1.0));
		simplex.add(new Vector2(2.0, -1.0));
		simplex.add(new Vector2(0.0, 2.0));
		
		ExpandingSimplex es = new ExpandingSimplex(simplex);
		
		TestCase.assertEquals(1, es.getWinding());
		TestCase.assertEquals(3, es.size());
		
		simplex = new ArrayList<Vector2>();
		simplex.add(new Vector2(0.0, 2.0));
		simplex.add(new Vector2(2.0, -1.0));
		simplex.add(new Vector2(-1.0, -1.0));
		
		es = new ExpandingSimplex(simplex);
		
		TestCase.assertEquals(-1, es.getWinding());
		TestCase.assertEquals(3, es.size());
		
		simplex = new ArrayList<Vector2>();
		simplex.add(new Vector2(0.0, 2.0));
		simplex.add(new Vector2(2.0, -1.0));
		
		es = new ExpandingSimplex(simplex);
		
		TestCase.assertEquals(-1, es.getWinding());
		
		simplex = new ArrayList<Vector2>();
		simplex.add(new Vector2(2.0, -1.0));
		simplex.add(new Vector2(0.0, 2.0));
		
		es = new ExpandingSimplex(simplex);
		
		TestCase.assertEquals(1, es.getWinding());
		TestCase.assertEquals(2, es.size());
		
		// one point
		simplex = new ArrayList<Vector2>();
		simplex.add(new Vector2(2.0, -1.0));
		
		es = new ExpandingSimplex(simplex);
		
		TestCase.assertEquals(0, es.getWinding());
		TestCase.assertEquals(1, es.size());
		
		// same point
		simplex = new ArrayList<Vector2>();
		simplex.add(new Vector2(2.0, -1.0));
		simplex.add(new Vector2(2.0, -1.0));
		
		es = new ExpandingSimplex(simplex);
		
		TestCase.assertEquals(0, es.getWinding());
		TestCase.assertEquals(2, es.size());
	}
	
	/**
	 * Tests the expansion of the closest edge.
	 */
	@Test
	public void expandAndGetClosest() {
		List<Vector2> simplex = new ArrayList<Vector2>();
		simplex.add(new Vector2(-1.0, -1.0));
		simplex.add(new Vector2(2.0, -1.0));
		simplex.add(new Vector2(0.0, 2.0));
		
		ExpandingSimplex es = new ExpandingSimplex(simplex);
		
		TestCase.assertEquals(1, es.getWinding());
		TestCase.assertEquals(3, es.size());
		
		ExpandingSimplexEdge edge = es.getClosestEdge();
		TestCase.assertNotNull(edge);
		
		TestCase.assertEquals(0.0, edge.point1.x, 1e-3);
		TestCase.assertEquals(2.0, edge.point1.y, 1e-3);
		TestCase.assertEquals(-1.0, edge.point2.x, 1e-3);
		TestCase.assertEquals(-1.0, edge.point2.y, 1e-3);
		TestCase.assertEquals(0.632, edge.distance, 1e-3);
		TestCase.assertEquals(-0.948, edge.normal.x, 1e-3);
		TestCase.assertEquals(0.316, edge.normal.y, 1e-3);
		
		es.expand(new Vector2(-1.0, 1.0));
		
		TestCase.assertEquals(4, es.size());
		
		edge = es.getClosestEdge();
		TestCase.assertNotNull(edge);
		
		TestCase.assertEquals(-1.0, edge.point1.x, 1e-3);
		TestCase.assertEquals(-1.0, edge.point1.y, 1e-3);
		TestCase.assertEquals(2.0, edge.point2.x, 1e-3);
		TestCase.assertEquals(-1.0, edge.point2.y, 1e-3);
		TestCase.assertEquals(1.0, edge.distance, 1e-3);
		TestCase.assertEquals(0.0, edge.normal.x, 1e-3);
		TestCase.assertEquals(-1.0, edge.normal.y, 1e-3);
		
		es.expand(new Vector2(1.0, -2.0));
		
		TestCase.assertEquals(5, es.size());
		
		edge = es.getClosestEdge();
		TestCase.assertNotNull(edge);
		
		TestCase.assertEquals(-1.0, edge.point1.x, 1e-3);
		TestCase.assertEquals(1.0, edge.point1.y, 1e-3);
		TestCase.assertEquals(-1.0, edge.point2.x, 1e-3);
		TestCase.assertEquals(-1.0, edge.point2.y, 1e-3);
		TestCase.assertEquals(1.0, edge.distance, 1e-3);
		TestCase.assertEquals(-1.0, edge.normal.x, 1e-3);
		TestCase.assertEquals(0.0, edge.normal.y, 1e-3);
	}
}
