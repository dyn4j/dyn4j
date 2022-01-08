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
package org.dyn4j.geometry;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link Ray} class.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.0.2
 */
public class RayTest {
	/**
	 * Tests the null start.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullStart() {
		new Ray(null, Math.toRadians(10));
	}
	
	/**
	 * Tests the null direction.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullDirection() {
		new Ray(new Vector2(), null);
	}
	
	/**
	 * Tests the zero direction.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroDirection() {
		new Ray(new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the constructor.
	 */
	@Test
	public void create() {
		Vector2 s = new Vector2(1.0, 2.0);
		Ray r = new Ray(s, Math.toRadians(10));
		
		TestCase.assertSame(s, r.getStart());
		TestCase.assertEquals(s.x, r.getStart().x, 1e-3);
		TestCase.assertEquals(s.y, r.getStart().y, 1e-3);
		TestCase.assertEquals(Math.toRadians(10), r.getDirection(), 1e-3);
		TestCase.assertEquals(0.984, r.getDirectionVector().x, 1e-3);
		TestCase.assertEquals(0.173, r.getDirectionVector().y, 1e-3);
		TestCase.assertNotNull(r.toString());
	}

	/**
	 * Tests the constructor.
	 */
	@Test
	public void createFromOrigin() {
		Ray r = new Ray(Math.toRadians(10));
		
		TestCase.assertEquals(0.0, r.getStart().x, 1e-3);
		TestCase.assertEquals(0.0, r.getStart().y, 1e-3);
		TestCase.assertEquals(Math.toRadians(10), r.getDirection(), 1e-3);
		TestCase.assertEquals(0.984, r.getDirectionVector().x, 1e-3);
		TestCase.assertEquals(0.173, r.getDirectionVector().y, 1e-3);
		TestCase.assertNotNull(r.toString());
	}

	/**
	 * Tests setting a null start.
	 */
	@Test(expected = NullPointerException.class)
	public void setStartNull() {
		Ray r = new Ray(Math.toRadians(30));
		r.setStart(null);
	}

	/**
	 * Tests setting a null direction.
	 */
	@Test(expected = NullPointerException.class)
	public void setDirectionNull() {
		Ray r = new Ray(Math.toRadians(30));
		r.setDirection(null);
	}

	/**
	 * Tests setting a zero direction.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setDirectionZero() {
		Ray r = new Ray(Math.toRadians(30));
		r.setDirection(new Vector2());
	}
	
	/**
	 * Tests setting the direction vector of the ray.
	 */
	@Test
	public void setDirection() {
		Vector2 s = new Vector2(1.0, 2.0);
		Ray r = new Ray(s, Math.toRadians(10));
		
		TestCase.assertSame(s, r.getStart());
		TestCase.assertEquals(s.x, r.getStart().x, 1e-3);
		TestCase.assertEquals(s.y, r.getStart().y, 1e-3);
		TestCase.assertEquals(Math.toRadians(10), r.getDirection(), 1e-3);
		TestCase.assertEquals(0.984, r.getDirectionVector().x, 1e-3);
		TestCase.assertEquals(0.173, r.getDirectionVector().y, 1e-3);
		
		r.setDirection(Math.toRadians(30));
		
		TestCase.assertSame(s, r.getStart());
		TestCase.assertEquals(s.x, r.getStart().x, 1e-3);
		TestCase.assertEquals(s.y, r.getStart().y, 1e-3);
		TestCase.assertEquals(Math.toRadians(30), r.getDirection(), 1e-3);
		TestCase.assertEquals(0.866, r.getDirectionVector().x, 1e-3);
		TestCase.assertEquals(0.499, r.getDirectionVector().y, 1e-3);
		
		r.setDirection(new Vector2(5, 7));
		
		TestCase.assertSame(s, r.getStart());
		TestCase.assertEquals(s.x, r.getStart().x, 1e-3);
		TestCase.assertEquals(s.y, r.getStart().y, 1e-3);
		TestCase.assertEquals(0.950, r.getDirection(), 1e-3);
		TestCase.assertEquals(5, r.getDirectionVector().x, 1e-3);
		TestCase.assertEquals(7, r.getDirectionVector().y, 1e-3);
	}
}
