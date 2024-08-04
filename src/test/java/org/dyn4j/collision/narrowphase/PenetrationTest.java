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
 * Test case for the {@link Penetration} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class PenetrationTest {
	/**
	 * Tests the constructor
	 */
	@Test
	public void create() {
		Penetration pen = new Penetration();
		
		TestCase.assertEquals(0.0, pen.getDepth());
		TestCase.assertEquals(0.0, pen.getNormal().x);
		TestCase.assertEquals(0.0, pen.getNormal().y);
		
		Vector2 n = new Vector2(1.0, 1.0);
		pen = new Penetration(n, 2.0);
		
		TestCase.assertEquals(2.0, pen.getDepth());
		TestCase.assertNotSame(n, pen.getNormal());
		TestCase.assertEquals(1.0, pen.getNormal().x);
		TestCase.assertEquals(1.0, pen.getNormal().y);
		
		TestCase.assertNotNull(pen.toString());
	}
	
	/**
	 * Tests the getters/setters.
	 */
	@Test
	public void getSet() {
		Penetration pen = new Penetration();
		
		TestCase.assertEquals(0.0, pen.getDepth());
		TestCase.assertEquals(0.0, pen.getNormal().x);
		TestCase.assertEquals(0.0, pen.getNormal().y);
		
		Vector2 n = new Vector2(1.0, 1.0);
		pen.setNormal(n);
		
		TestCase.assertEquals(0.0, pen.getDepth());
		TestCase.assertNotSame(n, pen.getNormal());
		TestCase.assertEquals(1.0, pen.getNormal().x);
		TestCase.assertEquals(1.0, pen.getNormal().y);
		
		pen.setDepth(3.0);
		
		TestCase.assertEquals(3.0, pen.getDepth());
		TestCase.assertNotSame(n, pen.getNormal());
		TestCase.assertEquals(1.0, pen.getNormal().x);
		TestCase.assertEquals(1.0, pen.getNormal().y);
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		Vector2 n = new Vector2(1.0, 1.0);
		Penetration pen = new Penetration(n, 2.0);
		
		TestCase.assertEquals(2.0, pen.getDepth());
		TestCase.assertEquals(1.0, pen.getNormal().x);
		TestCase.assertEquals(1.0, pen.getNormal().y);
		
		pen.clear();
		
		TestCase.assertEquals(0.0, pen.getDepth());
		TestCase.assertEquals(0.0, pen.getNormal().x);
		TestCase.assertEquals(0.0, pen.getNormal().y);
	}
	
	/**
	 * Tests the copy methods.
	 */
	@Test
	public void copy() {
		Vector2 n = new Vector2(1.0, 1.0);
		Penetration pen1 = new Penetration(n, 2.0);
		Penetration pen2 = new Penetration();
		
		pen2.set(pen1);
		TestCase.assertEquals(2.0, pen2.getDepth());
		TestCase.assertNotSame(n, pen2.getNormal());
		TestCase.assertNotSame(pen1.getNormal(), pen2.getNormal());
		TestCase.assertEquals(1.0, pen2.getNormal().x);
		TestCase.assertEquals(1.0, pen2.getNormal().y);
		
		Penetration pen3 = pen1.copy();
		TestCase.assertEquals(2.0, pen3.getDepth());
		TestCase.assertNotSame(n, pen3.getNormal());
		TestCase.assertNotSame(pen1.getNormal(), pen3.getNormal());
		TestCase.assertEquals(1.0, pen3.getNormal().x);
		TestCase.assertEquals(1.0, pen3.getNormal().y);
	}
	
	/**
	 * Tests the shift method.
	 */
	@Test
	public void shift() {
		Vector2 n = new Vector2(1.0, 1.0);
		Penetration pen = new Penetration(n, 2.0);
		
		TestCase.assertEquals(2.0, pen.getDepth());
		TestCase.assertEquals(1.0, pen.getNormal().x);
		TestCase.assertEquals(1.0, pen.getNormal().y);
		
		// nothing should change
		pen.shift(new Vector2(3.0, -2.0));
		TestCase.assertEquals(2.0, pen.getDepth());
		TestCase.assertEquals(1.0, pen.getNormal().x);
		TestCase.assertEquals(1.0, pen.getNormal().y);
	}
}
