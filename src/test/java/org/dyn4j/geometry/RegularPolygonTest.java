/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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

import java.util.Iterator;

import org.junit.Test;

/**
 * Test case for the {@link RegularPolygon} class.
 * @author William Bittle
 * @version 3.1.0
 * @since 1.0.0
 */
public class RegularPolygonTest {
	/**
	 * Tests the count &lt; 3.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCountLessThanThree() {
		new RegularPolygon(2, 1);
	}

	/**
	 * Tests the constructor.
	 */
	@Test
	public void create() {
		new RegularPolygon(5, 1.0);
	}
	
	/**
	 * Tests an invalid radius.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createInvalidRadius() {
		new RegularPolygon(10, 0.0);
	}
	
	/**
	 * Tests the dynamic generation of normals.
	 */
	@Test
	public void getNormals() {
		int size = 10;
		RegularPolygon p = new RegularPolygon(size, 1);
		Vector2[] normals = p.getNormals();
		TestCase.assertNotNull(normals);
		TestCase.assertEquals(size, normals.length);
		for (int i = 0; i < size; i++) {
			TestCase.assertNotNull(normals[i]);
		}
	}
	
	/**
	 * Tests the dynamic generation of normals.
	 */
	@Test
	public void normalIterator() {
		int size = 10;
		RegularPolygon p = new RegularPolygon(size, 1);
		Iterator<Vector2> it = p.getNormalIterator();
		TestCase.assertNotNull(it);
		int count = 1;
		while (it.hasNext()) {
			Vector2 n = it.next();
			TestCase.assertNotNull(n);
			count++;
		}
		TestCase.assertEquals(size, count);
	}
}
