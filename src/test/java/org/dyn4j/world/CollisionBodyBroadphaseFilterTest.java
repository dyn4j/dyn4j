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
package org.dyn4j.world;

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.Filter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link CollisionBodyBroadphaseCollisionDataFilter} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 */
public class CollisionBodyBroadphaseFilterTest {
	/**
	 * Tests the enabled/disable part of the filter.
	 */
	@Test
	public void enabled() {
		CollisionBodyBroadphaseCollisionDataFilter<Body, BodyFixture> filter = new CollisionBodyBroadphaseCollisionDataFilter<Body, BodyFixture>();
		
		Body b1 = new Body();
		Body b2 = new Body();
		BodyFixture b1f1 = b1.addFixture(Geometry.createCircle(0.5));
		BodyFixture b2f1 = b2.addFixture(Geometry.createCircle(0.5));
		
		TestCase.assertTrue(filter.isAllowed(b1, b1f1, b2, b2f1));
		
		b1.setEnabled(false);
		TestCase.assertFalse(filter.isAllowed(b1, b1f1, b2, b2f1));
		
		b2.setEnabled(false);
		TestCase.assertFalse(filter.isAllowed(b1, b1f1, b2, b2f1));
		
		b1.setEnabled(true);
		b2.setEnabled(true);
		TestCase.assertTrue(filter.isAllowed(b1, b1f1, b2, b2f1));
	}
	
	/**
	 * Tests the filters part of the filter.
	 */
	@Test
	public void filters() {
		CollisionBodyBroadphaseCollisionDataFilter<Body, BodyFixture> filter = new CollisionBodyBroadphaseCollisionDataFilter<Body, BodyFixture>();
		
		Body b1 = new Body();
		Body b2 = new Body();
		BodyFixture b1f1 = b1.addFixture(Geometry.createCircle(0.5));
		BodyFixture b2f1 = b2.addFixture(Geometry.createCircle(0.5));
		
		long g1 = 1;
		long g2 = 2;
		long g3 = 4;
		
		CategoryFilter cf1 = new CategoryFilter(g1, g1 | g2);
		CategoryFilter cf2 = new CategoryFilter(g2, g2);
		CategoryFilter cf3 = new CategoryFilter(g2, g1 | g2 | g3);
		
		b1f1.setFilter(cf1);
		b2f1.setFilter(cf1);
		TestCase.assertTrue(filter.isAllowed(b1, b1f1, b2, b2f1));
		
		b1f1.setFilter(cf1);
		b2f1.setFilter(cf2);
		TestCase.assertFalse(filter.isAllowed(b1, b1f1, b2, b2f1));
		
		b1f1.setFilter(cf1);
		b2f1.setFilter(cf3);
		TestCase.assertTrue(filter.isAllowed(b1, b1f1, b2, b2f1));
		
		b1f1.setFilter(Filter.DEFAULT_FILTER);
		b2f1.setFilter(cf3);
		TestCase.assertTrue(filter.isAllowed(b1, b1f1, b2, b2f1));
		
		b1f1.setFilter(cf2);
		b2f1.setFilter(cf3);
		TestCase.assertTrue(filter.isAllowed(b1, b1f1, b2, b2f1));
	}
}
