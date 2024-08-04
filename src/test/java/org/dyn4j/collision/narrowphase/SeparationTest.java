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
 * Test case for the {@link Separation} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class SeparationTest {
	/**
	 * Tests the constructor
	 */
	@Test
	public void create() {
		Separation sep = new Separation();
		
		TestCase.assertEquals(0.0, sep.getDistance());
		TestCase.assertEquals(0.0, sep.getNormal().x);
		TestCase.assertEquals(0.0, sep.getNormal().y);
		TestCase.assertEquals(0.0, sep.getPoint1().x);
		TestCase.assertEquals(0.0, sep.getPoint1().y);
		TestCase.assertEquals(0.0, sep.getPoint2().x);
		TestCase.assertEquals(0.0, sep.getPoint2().y);
		
		Vector2 n = new Vector2(1.0, 1.0);
		Vector2 p1 = new Vector2(2.0, 2.0);
		Vector2 p2 = new Vector2(3.0, 3.0);
		sep = new Separation(n, 5.0, p1, p2);
		
		TestCase.assertEquals(5.0, sep.getDistance());
		TestCase.assertEquals(1.0, sep.getNormal().x);
		TestCase.assertEquals(1.0, sep.getNormal().y);
		TestCase.assertNotSame(n, sep.getNormal());
		TestCase.assertEquals(2.0, sep.getPoint1().x);
		TestCase.assertEquals(2.0, sep.getPoint1().y);
		TestCase.assertNotSame(p1, sep.getPoint1());
		TestCase.assertEquals(3.0, sep.getPoint2().x);
		TestCase.assertEquals(3.0, sep.getPoint2().y);
		TestCase.assertNotSame(p2, sep.getPoint2());
		
		TestCase.assertNotNull(sep.toString());
	}
	
	/**
	 * Tests the getters/setters.
	 */
	@Test
	public void getSet() {
		Separation sep = new Separation();
		
		TestCase.assertEquals(0.0, sep.getDistance());
		TestCase.assertEquals(0.0, sep.getNormal().x);
		TestCase.assertEquals(0.0, sep.getNormal().y);
		TestCase.assertEquals(0.0, sep.getPoint1().x);
		TestCase.assertEquals(0.0, sep.getPoint1().y);
		TestCase.assertEquals(0.0, sep.getPoint2().x);
		TestCase.assertEquals(0.0, sep.getPoint2().y);
		
		Vector2 n = new Vector2(1.0, 1.0);
		sep.setNormal(n);
		
		TestCase.assertEquals(0.0, sep.getDistance());
		TestCase.assertEquals(1.0, sep.getNormal().x);
		TestCase.assertEquals(1.0, sep.getNormal().y);
		TestCase.assertNotSame(n, sep.getNormal());
		TestCase.assertEquals(0.0, sep.getPoint1().x);
		TestCase.assertEquals(0.0, sep.getPoint1().y);
		TestCase.assertEquals(0.0, sep.getPoint2().x);
		TestCase.assertEquals(0.0, sep.getPoint2().y);
		
		Vector2 p1 = new Vector2(2.0, 2.0);
		sep.setPoint1(p1);
		
		TestCase.assertEquals(0.0, sep.getDistance());
		TestCase.assertEquals(1.0, sep.getNormal().x);
		TestCase.assertEquals(1.0, sep.getNormal().y);
		TestCase.assertNotSame(n, sep.getNormal());
		TestCase.assertEquals(2.0, sep.getPoint1().x);
		TestCase.assertEquals(2.0, sep.getPoint1().y);
		TestCase.assertNotSame(p1, sep.getPoint1());
		TestCase.assertEquals(0.0, sep.getPoint2().x);
		TestCase.assertEquals(0.0, sep.getPoint2().y);
		
		Vector2 p2 = new Vector2(3.0, 3.0);
		sep.setPoint2(p2);
		
		TestCase.assertEquals(0.0, sep.getDistance());
		TestCase.assertEquals(1.0, sep.getNormal().x);
		TestCase.assertEquals(1.0, sep.getNormal().y);
		TestCase.assertNotSame(n, sep.getNormal());
		TestCase.assertEquals(2.0, sep.getPoint1().x);
		TestCase.assertEquals(2.0, sep.getPoint1().y);
		TestCase.assertNotSame(p1, sep.getPoint1());
		TestCase.assertEquals(3.0, sep.getPoint2().x);
		TestCase.assertEquals(3.0, sep.getPoint2().y);
		TestCase.assertNotSame(p2, sep.getPoint2());
		
		sep.setDistance(5.0);
		
		TestCase.assertEquals(5.0, sep.getDistance());
		TestCase.assertEquals(1.0, sep.getNormal().x);
		TestCase.assertEquals(1.0, sep.getNormal().y);
		TestCase.assertNotSame(n, sep.getNormal());
		TestCase.assertEquals(2.0, sep.getPoint1().x);
		TestCase.assertEquals(2.0, sep.getPoint1().y);
		TestCase.assertNotSame(p1, sep.getPoint1());
		TestCase.assertEquals(3.0, sep.getPoint2().x);
		TestCase.assertEquals(3.0, sep.getPoint2().y);
		TestCase.assertNotSame(p2, sep.getPoint2());
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		Vector2 n = new Vector2(1.0, 1.0);
		Vector2 p1 = new Vector2(2.0, 2.0);
		Vector2 p2 = new Vector2(3.0, 3.0);
		Separation sep = new Separation(n, 5.0, p1, p2);
		
		TestCase.assertEquals(5.0, sep.getDistance());
		TestCase.assertEquals(1.0, sep.getNormal().x);
		TestCase.assertEquals(1.0, sep.getNormal().y);
		TestCase.assertNotSame(n, sep.getNormal());
		TestCase.assertEquals(2.0, sep.getPoint1().x);
		TestCase.assertEquals(2.0, sep.getPoint1().y);
		TestCase.assertNotSame(p1, sep.getPoint1());
		TestCase.assertEquals(3.0, sep.getPoint2().x);
		TestCase.assertEquals(3.0, sep.getPoint2().y);
		TestCase.assertNotSame(p2, sep.getPoint2());
		
		sep.clear();
		
		TestCase.assertEquals(0.0, sep.getDistance());
		TestCase.assertEquals(0.0, sep.getNormal().x);
		TestCase.assertEquals(0.0, sep.getNormal().y);
		TestCase.assertEquals(0.0, sep.getPoint1().x);
		TestCase.assertEquals(0.0, sep.getPoint1().y);
		TestCase.assertEquals(0.0, sep.getPoint2().x);
		TestCase.assertEquals(0.0, sep.getPoint2().y);
	}
	
	/**
	 * Tests the copy methods.
	 */
	@Test
	public void copy() {
		Vector2 n = new Vector2(1.0, 1.0);
		Vector2 p1 = new Vector2(2.0, 2.0);
		Vector2 p2 = new Vector2(3.0, 3.0);
		Separation sep1 = new Separation(n, 5.0, p1, p2);
		Separation sep2 = new Separation();
		
		TestCase.assertEquals(5.0, sep1.getDistance());
		TestCase.assertEquals(1.0, sep1.getNormal().x);
		TestCase.assertEquals(1.0, sep1.getNormal().y);
		TestCase.assertNotSame(n, sep1.getNormal());
		TestCase.assertEquals(2.0, sep1.getPoint1().x);
		TestCase.assertEquals(2.0, sep1.getPoint1().y);
		TestCase.assertNotSame(p1, sep1.getPoint1());
		TestCase.assertEquals(3.0, sep1.getPoint2().x);
		TestCase.assertEquals(3.0, sep1.getPoint2().y);
		TestCase.assertNotSame(p2, sep1.getPoint2());
		
		sep2.set(sep1);
		TestCase.assertEquals(5.0, sep2.getDistance());
		TestCase.assertEquals(1.0, sep2.getNormal().x);
		TestCase.assertEquals(1.0, sep2.getNormal().y);
		TestCase.assertNotSame(n, sep2.getNormal());
		TestCase.assertNotSame(sep1.getNormal(), sep2.getNormal());
		TestCase.assertEquals(2.0, sep2.getPoint1().x);
		TestCase.assertEquals(2.0, sep2.getPoint1().y);
		TestCase.assertNotSame(p1, sep2.getPoint1());
		TestCase.assertNotSame(sep1.getPoint1(), sep2.getPoint1());
		TestCase.assertEquals(3.0, sep2.getPoint2().x);
		TestCase.assertEquals(3.0, sep2.getPoint2().y);
		TestCase.assertNotSame(p2, sep2.getPoint2());
		TestCase.assertNotSame(sep1.getPoint2(), sep2.getPoint2());
		
		Separation sep3 = sep1.copy();
		TestCase.assertEquals(5.0, sep3.getDistance());
		TestCase.assertEquals(1.0, sep3.getNormal().x);
		TestCase.assertEquals(1.0, sep3.getNormal().y);
		TestCase.assertNotSame(n, sep3.getNormal());
		TestCase.assertNotSame(sep1.getNormal(), sep3.getNormal());
		TestCase.assertEquals(2.0, sep3.getPoint1().x);
		TestCase.assertEquals(2.0, sep3.getPoint1().y);
		TestCase.assertNotSame(p1, sep3.getPoint1());
		TestCase.assertNotSame(sep1.getPoint1(), sep3.getPoint1());
		TestCase.assertEquals(3.0, sep3.getPoint2().x);
		TestCase.assertEquals(3.0, sep3.getPoint2().y);
		TestCase.assertNotSame(p2, sep3.getPoint2());
		TestCase.assertNotSame(sep1.getPoint2(), sep3.getPoint2());
	}
	
	/**
	 * Tests the shift method.
	 */
	@Test
	public void shift() {
		Vector2 n = new Vector2(1.0, 1.0);
		Vector2 p1 = new Vector2(2.0, 2.0);
		Vector2 p2 = new Vector2(3.0, 3.0);
		Separation sep = new Separation(n, 5.0, p1, p2);
		
		TestCase.assertEquals(5.0, sep.getDistance());
		TestCase.assertEquals(1.0, sep.getNormal().x);
		TestCase.assertEquals(1.0, sep.getNormal().y);
		TestCase.assertNotSame(n, sep.getNormal());
		TestCase.assertEquals(2.0, sep.getPoint1().x);
		TestCase.assertEquals(2.0, sep.getPoint1().y);
		TestCase.assertNotSame(p1, sep.getPoint1());
		TestCase.assertEquals(3.0, sep.getPoint2().x);
		TestCase.assertEquals(3.0, sep.getPoint2().y);
		TestCase.assertNotSame(p2, sep.getPoint2());
		
		// points should change
		sep.shift(new Vector2(3.0, -2.0));

		TestCase.assertEquals(5.0, sep.getDistance());
		TestCase.assertEquals(1.0, sep.getNormal().x);
		TestCase.assertEquals(1.0, sep.getNormal().y);
		TestCase.assertNotSame(n, sep.getNormal());
		TestCase.assertEquals(5.0, sep.getPoint1().x);
		TestCase.assertEquals(0.0, sep.getPoint1().y);
		TestCase.assertNotSame(p1, sep.getPoint1());
		TestCase.assertEquals(6.0, sep.getPoint2().x);
		TestCase.assertEquals(1.0, sep.getPoint2().y);
		TestCase.assertNotSame(p2, sep.getPoint2());
	}
}
