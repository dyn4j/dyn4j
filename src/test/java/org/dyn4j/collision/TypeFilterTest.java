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
 * Test case for the {@link TypeFilter} class.
 * <p>
 * This test case builds the following structure.
 * <pre>
 *             All Categories
 *             /           \
 *        Category1     Category2
 *        /      \
 * Category 3  Category 4
 * </pre>
 * @author William Bittle
 * @version 6.0.0
 * @since 3.0.2
 */
public class TypeFilterTest {
	// the list of categories and their tree
	
	/** The root node of the categories (allowed with all) */
	private static class All extends TypeFilter {}
	
	/** Category 1 (allowed with Category 1 and All) */
	private static class Category1 extends All {}
	
	/** Category 2 (allowed with Category 2 and All) */
	private static class Category2 extends All {}
	
	/** Category 3 (allowed with Category 3, Category 1, and All) */
	private static class Category3 extends Category1 {}
	
	/** Category 4 (allowed with Category 4, Category 1, and All) */
	private static class Category4 extends Category1 {}
	
	// static instances of the classes
	
	// this is just to save memory, use the static variables instead of
	// creating a new one for each filter
	
	/** The All category */
	private static final TypeFilter ALL = new All();
	
	/** Category 1 */
	private static final TypeFilter CATEGORY_1 = new Category1();
	
	/** Category 2 */
	private static final TypeFilter CATEGORY_2 = new Category2();
	
	/** Category 3 */
	private static final TypeFilter CATEGORY_3 = new Category3();
	
	/** Category 4 */
	private static final TypeFilter CATEGORY_4 = new Category4();
	
	/**
	 * Tests the filter method of the TypeFilter class.
	 */
	@Test
	public void filter() {
		// all should let all of them work
		TestCase.assertTrue(ALL.isAllowed(ALL));
		TestCase.assertTrue(ALL.isAllowed(CATEGORY_1));
		TestCase.assertTrue(ALL.isAllowed(CATEGORY_2));
		TestCase.assertTrue(ALL.isAllowed(CATEGORY_3));
		TestCase.assertTrue(ALL.isAllowed(CATEGORY_4));
		TestCase.assertTrue(CATEGORY_1.isAllowed(ALL));
		TestCase.assertTrue(CATEGORY_2.isAllowed(ALL));
		TestCase.assertTrue(CATEGORY_3.isAllowed(ALL));
		TestCase.assertTrue(CATEGORY_4.isAllowed(ALL));
		
		// since these are not in the same branch they should not be allowed
		TestCase.assertFalse(CATEGORY_1.isAllowed(CATEGORY_2));
		TestCase.assertFalse(CATEGORY_3.isAllowed(CATEGORY_2));
		TestCase.assertFalse(CATEGORY_4.isAllowed(CATEGORY_2));
		TestCase.assertFalse(CATEGORY_3.isAllowed(CATEGORY_4));
		// try the reverse
		TestCase.assertFalse(CATEGORY_2.isAllowed(CATEGORY_1));
		TestCase.assertFalse(CATEGORY_2.isAllowed(CATEGORY_3));
		TestCase.assertFalse(CATEGORY_2.isAllowed(CATEGORY_4));
		TestCase.assertFalse(CATEGORY_4.isAllowed(CATEGORY_3));
		
		TestCase.assertTrue(CATEGORY_1.isAllowed(CATEGORY_3));
		TestCase.assertTrue(CATEGORY_1.isAllowed(CATEGORY_4));
		TestCase.assertTrue(CATEGORY_3.isAllowed(CATEGORY_1));
		TestCase.assertTrue(CATEGORY_4.isAllowed(CATEGORY_1));
		
		// null and any other type should return false
		TestCase.assertFalse(ALL.isAllowed(null));
		TestCase.assertFalse(ALL.isAllowed(new CategoryFilter()));
		
		TestCase.assertNotNull(ALL.toString());
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		TypeFilter copy = ALL.copy();
		TestCase.assertSame(ALL, copy);
		
		copy = CATEGORY_3.copy();
		TestCase.assertSame(CATEGORY_3, copy);
	}
}
