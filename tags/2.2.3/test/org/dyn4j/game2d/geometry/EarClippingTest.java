/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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

import java.util.List;

import junit.framework.TestCase;

import org.dyn4j.game2d.geometry.decompose.EarClipping;
import org.junit.Test;

/**
 * Test case for the {@link EarClipping} class.
 * @author William Bittle
 * @version 2.2.0
 * @since 2.2.0
 */
public class EarClippingTest {
	/** The ear clipping algorithm */
	private EarClipping algo = new EarClipping();
	
	/**
	 * Tests passing a null array.
	 */
	@Test(expected = NullPointerException.class)
	public void nullArray() {
		this.algo.decompose((Vector2[])null);
	}
	
	/**
	 * Tests passing an array of vertices with less than 4 elements.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void lessThan4Vertices() {
		Vector2[] vertices = new Vector2[3];
		this.algo.decompose(vertices);
	}
	
	/**
	 * Tests passing an array of vertices that contains a null vertex.
	 */
	@Test(expected = NullPointerException.class)
	public void nullVertex() {
		Vector2[] vertices = new Vector2[5];
		vertices[0] = new Vector2(1.0, 2.0);
		vertices[1] = new Vector2(-1.0, 2.0);
		vertices[2] = null;
		vertices[3] = new Vector2(-1.0, 0.5);
		vertices[4] = new Vector2(0.5, -1.0);
		this.algo.decompose(vertices);
	}
	
	/**
	 * Tests passing an array of vertices that contains two vertices that
	 * are coincident.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void coincidentVertex() {
		Vector2[] vertices = new Vector2[5];
		vertices[0] = new Vector2(1.0, 2.0);
		vertices[1] = new Vector2(-1.0, 2.0);
		vertices[2] = new Vector2(-1.0, 2.0);
		vertices[3] = new Vector2(-1.0, 0.5);
		vertices[4] = new Vector2(0.5, -1.0);
		this.algo.decompose(vertices);
	}
	
	/**
	 * Tests the ear clipping implementation against a 10 vertex
	 * non-convex polygon.
	 */
	@Test
	public void success1() {
		Vector2[] vertices = new Vector2[10];
		vertices[0] = new Vector2(2.0, 0.5);
		vertices[1] = new Vector2(1.0, 1.0);
		vertices[2] = new Vector2(-0.25, 0.25);
		vertices[3] = new Vector2(-0.75, 1.5);
		vertices[4] = new Vector2(-1.0, 2.0);
		vertices[5] = new Vector2(-1.0, 0.0);
		vertices[6] = new Vector2(-0.5, -0.75);
		vertices[7] = new Vector2(0.25, -0.4);
		vertices[8] = new Vector2(1.0, 0.3);
		vertices[9] = new Vector2(0.25, -0.5);
		
		// decompose the poly
		List<Convex> result = this.algo.decompose(vertices);
		
		// the result should have less than or equal to n - 2 convex shapes
		TestCase.assertTrue(result.size() <= 8);
	}
}
