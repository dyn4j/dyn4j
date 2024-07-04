/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test case for the {@link CategoryFilter} class.
 * @author William Bittle
 * @version 6.0.0
 * @since 1.0.0
 */
public class CategoryFilterTest {
	/**
	 * Tests the create method with no args.
	 */
	@Test
	public void create() {
		CategoryFilter f = new CategoryFilter();
		TestCase.assertEquals(1, f.category);
		TestCase.assertEquals(Long.MAX_VALUE, f.mask);
		
		TestCase.assertEquals(1, f.getCategory());
		TestCase.assertEquals(Long.MAX_VALUE, f.getMask());
	}
	
	/**
	 * Tests the equals and hashCode methods.
	 */
	@Test
	public void equalsAndHashCode() {
		CategoryFilter f1 = new CategoryFilter();
		CategoryFilter f2 = new CategoryFilter();
		
		TestCase.assertTrue(f1.equals(f2));
		TestCase.assertEquals(f1.hashCode(), f2.hashCode());
		
		CategoryFilter f3 = new CategoryFilter(1, 2);
		
		TestCase.assertFalse(f1.equals(f3));
		TestCase.assertFalse(f1.hashCode() == f3.hashCode());
		
		CategoryFilter f4 = new CategoryFilter(1, 2);
		
		TestCase.assertTrue(f3.equals(f4));
		TestCase.assertEquals(f3.hashCode(), f4.hashCode());
		
		CategoryFilter f5 = new CategoryFilter(1, 3);
		
		TestCase.assertFalse(f3.equals(f5));
		
		TestCase.assertTrue(f1.equals(f1));
		TestCase.assertFalse(f1.equals(null));
		TestCase.assertFalse(f1.equals(new Object()));
	}
	
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
		
		// test null
		TestCase.assertTrue(f1.isAllowed(null));
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void tostring() {
		CategoryFilter f1 = new CategoryFilter();
		TestCase.assertNotNull(f1.toString());
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		CategoryFilter filter = new CategoryFilter(1, 5);
		CategoryFilter copy = filter.copy();
		
		TestCase.assertNotSame(filter, copy);
		TestCase.assertEquals(filter.category, copy.category);
		TestCase.assertEquals(filter.mask, copy.mask);
	}
}
