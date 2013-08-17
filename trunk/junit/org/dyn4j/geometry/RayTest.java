/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
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

import org.junit.Test;

/**
 * Test case for the {@link Ray} class.
 * @author William Bittle
 * @version 3.0.2
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
		new Ray(new Vector2(), Math.toRadians(10));
	}
	
	/**
	 * Tests the set null start.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullStart() {
		Ray ray = new Ray(new Vector2(), Math.toRadians(10));
		ray.setStart(null);
	}
	
	/**
	 * Tests the set null direction.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullDirection() {
		Ray ray = new Ray(new Vector2(), Math.toRadians(10));
		ray.setDirection(null);
	}
	
	/**
	 * Tests the set zero direction.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setZeroDirection() {
		Ray ray = new Ray(new Vector2(), Math.toRadians(10));
		ray.setDirection(new Vector2());
	}
}
