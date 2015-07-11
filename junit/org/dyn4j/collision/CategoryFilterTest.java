/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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

import org.junit.Test;

/**
 * Test case for the {@link CategoryFilter} class.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public class CategoryFilterTest {
	/**
	 * Tests the {@link CategoryFilter}.
	 */
	@Test
	public void filter() {
		// category 1
		// can collide with any category
		CategoryFilter f1 = new CategoryFilter(1, Long.MAX_VALUE);
		// category 2
		// can collide with category 1 and 2
		CategoryFilter f2 = new CategoryFilter(2, 3);
		// category 3
		// can collide with category 1, 2, and 3
		CategoryFilter f3 = new CategoryFilter(4, 7);
		
		// allowed
		TestCase.assertTrue(f1.isAllowed(f2));
		TestCase.assertTrue(f1.isAllowed(f3));
		TestCase.assertTrue(f2.isAllowed(f1));
		TestCase.assertTrue(f3.isAllowed(f1));
		
		// allowed, not same type
		TestCase.assertTrue(f1.isAllowed(Filter.DEFAULT_FILTER));
		TestCase.assertTrue(f2.isAllowed(Filter.DEFAULT_FILTER));
		TestCase.assertTrue(f3.isAllowed(Filter.DEFAULT_FILTER));
		
		// not allowed
		TestCase.assertFalse(f2.isAllowed(f3));
		TestCase.assertFalse(f3.isAllowed(f2));
	}
}
