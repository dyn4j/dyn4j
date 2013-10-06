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
package org.dyn4j.collision;

import junit.framework.TestCase;

import org.dyn4j.geometry.Geometry;
import org.junit.Test;

/**
 * Test case for the default filter.
 * @author William Bittle
 * @version 3.1.0
 * @since 3.1.0
 */
public class DefaultFilterTest {
	/**
	 * Tests the default filter (should always return true).
	 * <p>
	 * Filtering the other way around, i.e. TypeFilter.filter(DefaultFilter), may
	 * return different results depending on the implementation of the filter.
	 */
	@Test
	public void filter() {
		Fixture f1 = new Fixture(Geometry.createCircle(1.0));
		Fixture f2 = new Fixture(Geometry.createRectangle(1.0, 1.0));
		f2.setFilter(new CategoryFilter());
		TestCase.assertTrue(f1.filter.isAllowed(f2.filter));
		f2.setFilter(new TypeFilter() {});
		TestCase.assertTrue(f1.filter.isAllowed(f2.filter));
	}
}
