/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test case for the AbstractShape class.
 * @author William Bittle
 * @version 4.2.1
 * @since 3.1.1
 */
public class AbstractShapeTest {
	/**
	 * Test shape class.
	 * @author William Bittle
	 * @version 4.1.0
	 * @since 3.1.1
	 */
	private class TestShape extends AbstractShape {
		public TestShape(Vector2 c, double r) {
			super(c, r);
		}
		@Override
		public boolean contains(Vector2 point, Transform transform) { return false; }	
		@Override
		public boolean contains(Vector2 point, Transform transform, boolean inclusive) { return false; }	
		@Override
		public Mass createMass(double density) { return new Mass(); }
		@Override
		public double getRadius(Vector2 center) { return 0.0; }
		@Override
		public Interval project(Vector2 n, Transform transform) { return null; }
		@Override
		public void computeAABB(Transform transform, AABB aabb) { }
		@Override
		public double getArea() { return 0; }
	}
	
	/**
	 * Tests the create and radius/center getters.
	 */
	@Test
	public void create() {
		Vector2 c = new Vector2(1.0, 2.0);
		double r = 2.0;
		Shape s = new TestShape(c, r);
		
		TestCase.assertEquals(r, s.getRadius());
		TestCase.assertEquals(c.x, s.getCenter().x);
		TestCase.assertEquals(c.y, s.getCenter().y);
	}
	
	/**
	 * Make sure storage of user data is working.
	 */
	@Test
	public void setUserData() {
		Shape s = new TestShape(null, 0.0);
		// should be initial null
		TestCase.assertNull(s.getUserData());
		
		String obj = "hello";
		s.setUserData(obj);
		TestCase.assertNotNull(s.getUserData());
		TestCase.assertSame(obj, s.getUserData());
	}
}
