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

import junit.framework.TestCase;

import org.dyn4j.geometry.Interval;
import org.junit.Test;

/**
 * Test case for the {@link Interval} class.
 * @author William Bittle
 * @version 3.1.0
 * @since 1.0.0
 */
public class IntervalTest {
	/**
	 * Tests the min > max.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createMinGreaterThanMax() {
		new Interval(0.0, -1.0);
	}

	/**
	 * Tests the constructor.
	 */
	@Test
	public void create() {
		new Interval(0.0, 2.0);
	}
	
	/**
	 * Tests the copy constructor.
	 */
	@Test
	public void createCopy() {
		Interval i1 = new Interval(-1.0, 2.0);
		Interval i2 = new Interval(i1);
		
		TestCase.assertNotSame(i2, i1);
		TestCase.assertEquals(i1.min, i2.min);
		TestCase.assertEquals(i1.max, i2.max);
	}
	
	/**
	 * Tests an invalid max.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setInvalidMax() {
		Interval i = new Interval(0.0, 2.0);
		i.setMax(-1.0);
	}
	
	/**
	 * Tests a valid max.
	 */
	@Test
	public void setMax() {
		Interval i = new Interval(0.0, 2.0);
		i.setMax(1.5);
	}
	
	/**
	 * Tests an invalid min.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setInvalidMin() {
		Interval i = new Interval(0.0, 2.0);
		i.setMin(3.0);
	}

	/**
	 * Tests a valid max.
	 */
	@Test
	public void setMin() {
		Interval i = new Interval(0.0, 2.0);
		i.setMin(1.5);
	}
	
	/**
	 * Tests the includes methods.
	 */
	@Test
	public void includes() {
		Interval i = new Interval(-2.5, 100.521);
		
		TestCase.assertTrue(i.includesExclusive(50.0));
		TestCase.assertTrue(!i.includesExclusive(100.521));
		TestCase.assertTrue(!i.includesExclusive(-3.0));
		
		TestCase.assertTrue(i.includesInclusive(50.0));
		TestCase.assertTrue(i.includesInclusive(-2.5));
		TestCase.assertTrue(!i.includesInclusive(-3.0));
		
		TestCase.assertTrue(i.includesInclusiveMax(50.0));
		TestCase.assertTrue(i.includesInclusiveMax(100.521));
		TestCase.assertTrue(!i.includesInclusiveMax(-2.5));
		TestCase.assertTrue(!i.includesInclusiveMax(200.0));
		
		TestCase.assertTrue(i.includesInclusiveMin(50.0));
		TestCase.assertTrue(i.includesInclusiveMin(-2.5));
		TestCase.assertTrue(!i.includesInclusiveMin(100.521));
		TestCase.assertTrue(!i.includesInclusiveMin(-3.0));
	}
	
	/**
	 * Tests the overlap methods.
	 */
	@Test
	public void overlaps() {
		Interval i1 = new Interval(-2.0, 5.0);
		Interval i2 = new Interval(-4.0, 1.0);
		
		TestCase.assertTrue(i1.overlaps(i2));
		// the reverse should work also
		TestCase.assertTrue(i2.overlaps(i1));
		
		// distance should return zero
		TestCase.assertEquals(0.0, i1.distance(i2));
		TestCase.assertEquals(0.0, i2.distance(i1));
		
		// contains should return false
		TestCase.assertTrue(!i1.contains(i2));
		TestCase.assertTrue(!i2.contains(i1));
		
		double ov1 = i1.getOverlap(i2);
		double ov2 = i2.getOverlap(i1);
		
		TestCase.assertEquals(3.0, ov1);
		TestCase.assertEquals(3.0, ov2);
	}
	
	/**
	 * Tests the clamp methods.
	 */
	@Test
	public void clamp() {
		Interval i = new Interval(-1.0, 6.5);
		
		TestCase.assertEquals(2.0, i.clamp(2.0));
		TestCase.assertEquals(2.0, Interval.clamp(2.0, -1.0, 6.5));
		TestCase.assertEquals(-1.0, i.clamp(-2.0));
		TestCase.assertEquals(6.5, i.clamp(7.0));
	}
	
	/**
	 * Tests the degenerate interval methods.
	 */
	@Test
	public void degenerate() {
		Interval i = new Interval(2.0, 2.0);
		
		TestCase.assertTrue(i.isDegenerate());
		
		i.expand(0.000001);
		
		TestCase.assertEquals(1.9999995, i.min);
		TestCase.assertEquals(2.0000005, i.max);
		
		TestCase.assertTrue(!i.isDegenerate());
		TestCase.assertTrue(i.isDegenerate(0.01));
	}
	
	/**
	 * Tests the union methods.
	 */
	@Test
	public void union() {
		Interval i1 = new Interval(-2.0, 3.0);
		Interval i2 = new Interval(-1.0, 4.0);
		
		Interval u = i1.getUnion(i2);
		TestCase.assertEquals(-2.0, u.min);
		TestCase.assertEquals(4.0, u.max);
		
		// test cumulativity
		u = i2.getUnion(i1);
		TestCase.assertEquals(-2.0, u.min);
		TestCase.assertEquals(4.0, u.max);
		
		// test intervals that dont overlap
		Interval i3 = new Interval(-3.0, -2.5);
		u = i1.getUnion(i3);
		TestCase.assertEquals(-3.0, u.min);
		TestCase.assertEquals(3.0, u.max);
	}
	
	/**
	 * Test the intersection methods.
	 */
	@Test
	public void intersection() {
		Interval i1 = new Interval(-2.0, 3.0);
		Interval i2 = new Interval(-1.0, 4.0);
		
		Interval u = i1.getIntersection(i2);
		TestCase.assertEquals(-1.0, u.min);
		TestCase.assertEquals(3.0, u.max);
		
		// test cumulativity
		u = i2.getIntersection(i1);
		TestCase.assertEquals(-1.0, u.min);
		TestCase.assertEquals(3.0, u.max);
		
		// test intervals that dont overlap
		Interval i3 = new Interval(-3.0, -2.5);
		u = i1.getIntersection(i3);
		TestCase.assertEquals(0.0, u.min);
		TestCase.assertEquals(0.0, u.max);
	}
	
	/**
	 * Tests the distance method.
	 * @since 3.1.0
	 */
	@Test
	public void distance() {
		Interval i1 = new Interval(-2.0, 3.0);
		Interval i2 = new Interval(-1.0, 4.0);
		
		// overlapping intervals should return 0
		TestCase.assertEquals(0.0, i1.distance(i2));
		
		i2 = new Interval(4.0, 6.0);
		
		TestCase.assertEquals(1.0, i1.distance(i2));
		TestCase.assertEquals(1.0, i2.distance(i1));
	}
	
	/**
	 * Tests the expand method.
	 * @since 3.1.0
	 */
	@Test
	public void expand() {
		Interval i = new Interval(-2.0, 2.0);
		Interval ci = null;
		
		// test a normal expansion
		ci = i.getExpanded(2.0);
		TestCase.assertEquals(-3.0, ci.min, 1.0e-3);
		TestCase.assertEquals( 3.0, ci.max, 1.0e-3);
		i.expand(2.0);
		TestCase.assertEquals(-3.0, i.min, 1.0e-3);
		TestCase.assertEquals( 3.0, i.max, 1.0e-3);
		
		// test no expansion
		ci = i.getExpanded(0.0);
		TestCase.assertEquals(-3.0, ci.min, 1.0e-3);
		TestCase.assertEquals( 3.0, ci.max, 1.0e-3);
		i.expand(0.0);
		TestCase.assertEquals(-3.0, i.min, 1.0e-3);
		TestCase.assertEquals( 3.0, i.max, 1.0e-3);
		
		// test negative expansion
		ci = i.getExpanded(-1.0);
		TestCase.assertEquals(-2.5, ci.min, 1.0e-3);
		TestCase.assertEquals( 2.5, ci.max, 1.0e-3);
		i.expand(-1.0);
		TestCase.assertEquals(-2.5, i.min, 1.0e-3);
		TestCase.assertEquals( 2.5, i.max, 1.0e-3);
		
		// test large negative expansion (this should
		// make the interval invalid, so the interval
		// is instead reduced down to a degenerate
		// interval at the mid point
		ci = i.getExpanded(-6.0);
		TestCase.assertEquals(0.0, ci.min, 1.0e-3);
		TestCase.assertEquals(0.0, ci.max, 1.0e-3);
		i.expand(-6.0);
		TestCase.assertEquals(0.0, i.min, 1.0e-3);
		TestCase.assertEquals(0.0, i.max, 1.0e-3);

		// make a copy
		i = new Interval(-2.5, 1.5);
		ci = i.getExpanded(-6.0);
		TestCase.assertEquals(-0.5, ci.min, 1.0e-3);
		TestCase.assertEquals(-0.5, ci.max, 1.0e-3);
		i.expand(-6.0);
		TestCase.assertEquals(-0.5, i.min, 1.0e-3);
		TestCase.assertEquals(-0.5, i.max, 1.0e-3);
	}
	
	/**
	 * Returns the length of the interval.
	 */
	@Test
	public void getLength() {
		Interval i = new Interval(-2.0, 2.0);
		TestCase.assertEquals(4.0, i.getLength());
		
		i = new Interval(-1.0, 2.0);
		TestCase.assertEquals(3.0, i.getLength());
		
		i = new Interval(-3.0, -1.0);
		TestCase.assertEquals(2.0, i.getLength());
		
		i = new Interval(2.0, 3.0);
		TestCase.assertEquals(1.0, i.getLength());
		
		i = new Interval(-1.0, 2.0);
		i.expand(-4.0);
		TestCase.assertEquals(0.0, i.getLength());
		
		i = new Interval(-1.0, -1.0);
		TestCase.assertEquals(0.0, i.getLength());
	}
}
