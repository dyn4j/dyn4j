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
 * Test case for the {@link Raycast} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class RaycastTest {
	/**
	 * Tests the constructor
	 */
	@Test
	public void create() {
		Raycast raycast = new Raycast();
		
		TestCase.assertEquals(0.0, raycast.getDistance());
		TestCase.assertEquals(0.0, raycast.getNormal().x);
		TestCase.assertEquals(0.0, raycast.getNormal().y);
		TestCase.assertEquals(0.0, raycast.getPoint().x);
		TestCase.assertEquals(0.0, raycast.getPoint().y);
		
		Vector2 p = new Vector2(2.0, 2.0);
		Vector2 n = new Vector2(1.0, 1.0);
		raycast = new Raycast(p, n, 2.0);
		
		TestCase.assertEquals(2.0, raycast.getDistance());
		TestCase.assertEquals(1.0, raycast.getNormal().x);
		TestCase.assertEquals(1.0, raycast.getNormal().y);
		TestCase.assertNotSame(n, raycast.getNormal());
		TestCase.assertEquals(2.0, raycast.getPoint().x);
		TestCase.assertEquals(2.0, raycast.getPoint().y);
		TestCase.assertNotSame(p, raycast.getPoint());
		
		TestCase.assertNotNull(raycast.toString());
	}
	
	/**
	 * Tests the getters/setters.
	 */
	@Test
	public void getSet() {
		Raycast raycast = new Raycast();
		
		TestCase.assertEquals(0.0, raycast.getDistance());
		TestCase.assertEquals(0.0, raycast.getNormal().x);
		TestCase.assertEquals(0.0, raycast.getNormal().y);
		TestCase.assertEquals(0.0, raycast.getPoint().x);
		TestCase.assertEquals(0.0, raycast.getPoint().y);
		
		Vector2 n = new Vector2(1.0, 1.0);
		raycast.setNormal(n);
		
		TestCase.assertEquals(0.0, raycast.getDistance());
		TestCase.assertEquals(1.0, raycast.getNormal().x);
		TestCase.assertEquals(1.0, raycast.getNormal().y);
		TestCase.assertNotSame(n, raycast.getNormal());
		TestCase.assertEquals(0.0, raycast.getPoint().x);
		TestCase.assertEquals(0.0, raycast.getPoint().y);
		
		Vector2 p = new Vector2(2.0, 2.0);
		raycast.setPoint(p);
		
		TestCase.assertEquals(0.0, raycast.getDistance());
		TestCase.assertEquals(1.0, raycast.getNormal().x);
		TestCase.assertEquals(1.0, raycast.getNormal().y);
		TestCase.assertNotSame(n, raycast.getNormal());
		TestCase.assertEquals(2.0, raycast.getPoint().x);
		TestCase.assertEquals(2.0, raycast.getPoint().y);
		TestCase.assertNotSame(p, raycast.getPoint());
		
		raycast.setDistance(4.0);
		
		TestCase.assertEquals(4.0, raycast.getDistance());
		TestCase.assertEquals(1.0, raycast.getNormal().x);
		TestCase.assertEquals(1.0, raycast.getNormal().y);
		TestCase.assertNotSame(n, raycast.getNormal());
		TestCase.assertEquals(2.0, raycast.getPoint().x);
		TestCase.assertEquals(2.0, raycast.getPoint().y);
		TestCase.assertNotSame(p, raycast.getPoint());
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		Vector2 p = new Vector2(2.0, 2.0);
		Vector2 n = new Vector2(1.0, 1.0);
		Raycast raycast = new Raycast(p, n, 2.0);
		
		TestCase.assertEquals(2.0, raycast.getDistance());
		TestCase.assertEquals(1.0, raycast.getNormal().x);
		TestCase.assertEquals(1.0, raycast.getNormal().y);
		TestCase.assertNotSame(n, raycast.getNormal());
		TestCase.assertEquals(2.0, raycast.getPoint().x);
		TestCase.assertEquals(2.0, raycast.getPoint().y);
		TestCase.assertNotSame(p, raycast.getPoint());
		
		raycast.clear();
		
		TestCase.assertEquals(0.0, raycast.getDistance());
		TestCase.assertEquals(0.0, raycast.getNormal().x);
		TestCase.assertEquals(0.0, raycast.getNormal().y);
		TestCase.assertEquals(0.0, raycast.getPoint().x);
		TestCase.assertEquals(0.0, raycast.getPoint().y);
	}
	
	/**
	 * Tests the copy methods.
	 */
	@Test
	public void copy() {
		Vector2 p = new Vector2(2.0, 2.0);
		Vector2 n = new Vector2(1.0, 1.0);
		Raycast raycast1 = new Raycast(p, n, 2.0);
		Raycast raycast2 = new Raycast();
		
		TestCase.assertEquals(2.0, raycast1.getDistance());
		TestCase.assertEquals(1.0, raycast1.getNormal().x);
		TestCase.assertEquals(1.0, raycast1.getNormal().y);
		TestCase.assertNotSame(n, raycast1.getNormal());
		TestCase.assertEquals(2.0, raycast1.getPoint().x);
		TestCase.assertEquals(2.0, raycast1.getPoint().y);
		TestCase.assertNotSame(p, raycast1.getPoint());
		
		raycast2.set(raycast1);
		TestCase.assertEquals(2.0, raycast2.getDistance());
		TestCase.assertEquals(1.0, raycast2.getNormal().x);
		TestCase.assertEquals(1.0, raycast2.getNormal().y);
		TestCase.assertNotSame(n, raycast2.getNormal());
		TestCase.assertNotSame(raycast1.getNormal(), raycast2.getNormal());
		TestCase.assertEquals(2.0, raycast2.getPoint().x);
		TestCase.assertEquals(2.0, raycast2.getPoint().y);
		TestCase.assertNotSame(p, raycast2.getPoint());
		TestCase.assertNotSame(raycast1.getPoint(), raycast2.getPoint());
		
		Raycast raycast3 = raycast1.copy();
		TestCase.assertEquals(2.0, raycast3.getDistance());
		TestCase.assertEquals(1.0, raycast3.getNormal().x);
		TestCase.assertEquals(1.0, raycast3.getNormal().y);
		TestCase.assertNotSame(n, raycast3.getNormal());
		TestCase.assertNotSame(raycast1.getNormal(), raycast3.getNormal());
		TestCase.assertEquals(2.0, raycast3.getPoint().x);
		TestCase.assertEquals(2.0, raycast3.getPoint().y);
		TestCase.assertNotSame(p, raycast3.getPoint());
		TestCase.assertNotSame(raycast1.getPoint(), raycast3.getPoint());
	}
	
	/**
	 * Tests the shift method.
	 */
	@Test
	public void shift() {
		Vector2 p = new Vector2(2.0, 2.0);
		Vector2 n = new Vector2(1.0, 1.0);
		Raycast raycast = new Raycast(p, n, 2.0);
		
		TestCase.assertEquals(2.0, raycast.getDistance());
		TestCase.assertEquals(1.0, raycast.getNormal().x);
		TestCase.assertEquals(1.0, raycast.getNormal().y);
		TestCase.assertNotSame(n, raycast.getNormal());
		TestCase.assertEquals(2.0, raycast.getPoint().x);
		TestCase.assertEquals(2.0, raycast.getPoint().y);
		TestCase.assertNotSame(p, raycast.getPoint());
		
		// only the point should change
		raycast.shift(new Vector2(3.0, -2.0));
		
		TestCase.assertEquals(2.0, raycast.getDistance());
		TestCase.assertEquals(1.0, raycast.getNormal().x);
		TestCase.assertEquals(1.0, raycast.getNormal().y);
		TestCase.assertNotSame(n, raycast.getNormal());
		TestCase.assertEquals(5.0, raycast.getPoint().x);
		TestCase.assertEquals(0.0, raycast.getPoint().y);
		TestCase.assertNotSame(p, raycast.getPoint());
	}
}
