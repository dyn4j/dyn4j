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
 * Test case for the {@link DetectFilter} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 */
public class DetectFilterTest {
	/**
	 * Tests creation of a filter
	 */
	@Test
	public void create() {
		DetectFilter<Body, BodyFixture> filter = new DetectFilter<Body, BodyFixture>(
				false,
				false,
				null);

		TestCase.assertFalse(filter.isIgnoreSensorsEnabled());
		TestCase.assertFalse(filter.IsIgnoreDisabledEnabled());
		TestCase.assertNull(filter.getFilter());

		filter = new DetectFilter<Body, BodyFixture>(
				true,
				false,
				null);

		TestCase.assertTrue(filter.isIgnoreSensorsEnabled());
		TestCase.assertFalse(filter.IsIgnoreDisabledEnabled());
		TestCase.assertNull(filter.getFilter());
		
		filter = new DetectFilter<Body, BodyFixture>(
				false,
				true,
				null);

		TestCase.assertFalse(filter.isIgnoreSensorsEnabled());
		TestCase.assertTrue(filter.IsIgnoreDisabledEnabled());
		TestCase.assertNull(filter.getFilter());
		
		filter = new DetectFilter<Body, BodyFixture>(
				false,
				false,
				Filter.DEFAULT_FILTER);

		TestCase.assertFalse(filter.isIgnoreSensorsEnabled());
		TestCase.assertFalse(filter.IsIgnoreDisabledEnabled());
		TestCase.assertNotNull(filter.getFilter());
		TestCase.assertEquals(Filter.DEFAULT_FILTER, filter.getFilter());
	}
	
	/**
	 * Tests filtering by sensor.
	 */
	@Test
	public void sensor() {
		Body body = new Body(); 
		BodyFixture fixture = body.addFixture(Geometry.createCircle(0.5));

		DetectFilter<Body, BodyFixture> filter = new DetectFilter<Body, BodyFixture>(
				true,
				false,
				null);
		
		// default behavior
		TestCase.assertTrue(filter.isAllowed(body, fixture));
		
		// when the fixture is flagged as a sensor
		fixture.setSensor(true);
		TestCase.assertFalse(filter.isAllowed(body, fixture));
		
		// when the fixture is re-flagged as not a sensor
		fixture.setSensor(false);
		TestCase.assertTrue(filter.isAllowed(body, fixture));
		
		// when the filter is set to ignore the sensor check
		filter = new DetectFilter<Body, BodyFixture>(
				false,
				false,
				null);
		fixture.setSensor(true);
		TestCase.assertTrue(filter.isAllowed(body, fixture));
	}
	
	/**
	 * Tests filtering by disabled.
	 */
	@Test
	public void disabled() {
		Body body = new Body(); 
		BodyFixture fixture = body.addFixture(Geometry.createCircle(0.5));

		DetectFilter<Body, BodyFixture> filter = new DetectFilter<Body, BodyFixture>(
				false,
				true,
				null);
		
		// default behavior
		TestCase.assertTrue(filter.isAllowed(body, fixture));
		
		// when the body is disabled
		body.setEnabled(false);
		TestCase.assertFalse(filter.isAllowed(body, fixture));
		
		// when the body is re-enabled
		body.setEnabled(true);
		TestCase.assertTrue(filter.isAllowed(body, fixture));
		
		// when the disabled filter is turned off
		filter = new DetectFilter<Body, BodyFixture>(
				false,
				false,
				null);
		body.setEnabled(false);
		TestCase.assertTrue(filter.isAllowed(body, fixture));
	}

	/**
	 * Tests filtering by a filter.
	 */
	@Test
	public void filter() {
		Body body = new Body(); 
		BodyFixture fixture = body.addFixture(Geometry.createCircle(0.5));
		
		long m1 = 1;
		long m2 = 2;
		long m3 = 4;
		
		CategoryFilter cf = new CategoryFilter(m1, m1 | m2 | m3);
		CategoryFilter cfa = new CategoryFilter(m3, m3);
		CategoryFilter cfb = new CategoryFilter(m3, m1 | m2);
		
		DetectFilter<Body, BodyFixture> filter = new DetectFilter<Body, BodyFixture>(
				false,
				false,
				null);

		// test no filter (null-DEFAULT)
		TestCase.assertTrue(filter.isAllowed(body, fixture));
		
		// test filter type mis-match (CFA-DEFAULT)
		filter = new DetectFilter<Body, BodyFixture>(
				false,
				false,
				cfa);
		TestCase.assertTrue(filter.isAllowed(body, fixture));
		
		// test filter match (CFB-CF)
		fixture.setFilter(cf);
		filter = new DetectFilter<Body, BodyFixture>(
				false,
				false,
				cfb);
		TestCase.assertTrue(filter.isAllowed(body, fixture));
		
		// test filter no match (CFA-CF)
		fixture.setFilter(cf);
		filter = new DetectFilter<Body, BodyFixture>(
				false,
				false,
				cfa);
		TestCase.assertFalse(filter.isAllowed(body, fixture));
	}
}
