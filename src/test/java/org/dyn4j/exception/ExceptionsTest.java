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
package org.dyn4j.exception;

import junit.framework.TestCase;

import org.dyn4j.ExceptionMessageFactory;
import org.junit.Test;

/**
 * Test case for the {@link ExceptionMessageFactory} class.
 * @author William Bittle
 * @version 5.0.0
 * @since 5.0.0
 */
public class ExceptionsTest {
	/**
	 * Tests the newCannotBeEqualException method.
	 */
	@Test
	public void newCannotBeEqualException() {
		Exception ex = new ValueOutOfRangeException("test", 0.0);
		TestCase.assertNotNull(ex);
		TestCase.assertEquals("test cannot be 0.000000", ex.getMessage());
	}
	
	/**
	 * Tests the newCannotBeEqualException method.
	 */
	@Test
	public void newCannotBeEqualException2() {
		Exception ex = new ValueOutOfRangeException("test", 2);
		TestCase.assertNotNull(ex);
		TestCase.assertEquals("test cannot be 2", ex.getMessage());
	}
	
	/**
	 * Tests the newEmptyCollectionException method.
	 */
	@Test
	public void newEmptyCollectionException() {
		Exception ex = new EmptyCollectionException("test");
		TestCase.assertNotNull(ex);
		TestCase.assertEquals("test cannot be empty", ex.getMessage());
	}
	
	/**
	 * Tests the newGreaterThanException method.
	 */
	@Test
	public void newGreaterThanException() {
		Exception ex = new ValueOutOfRangeException("test", -1.0, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		TestCase.assertNotNull(ex);
		TestCase.assertEquals("-1.000000 was supplied for test: test must be greater than 0.000000", ex.getMessage());
	}
	
	/**
	 * Tests the newGreaterThanOrEqualException method.
	 */
	@Test
	public void newGreaterThanOrEqualException() {
		Exception ex = new ValueOutOfRangeException("test", -1.0, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		TestCase.assertNotNull(ex);
		TestCase.assertEquals("-1.000000 was supplied for test: test must be greater than or equal to 0.000000", ex.getMessage());
	}
	
	/**
	 * Tests the newIndexOutOfBoundsException method.
	 */
	@Test
	public void newIndexOutOfBoundsException() {
		Exception ex = new InvalidIndexException(5);
		TestCase.assertNotNull(ex);
		TestCase.assertEquals("The index 5 is out of bounds", ex.getMessage());
	}
	
	/**
	 * Tests the newLessThanException method.
	 */
	@Test
	public void newLessThanException() {
		Exception ex = new ValueOutOfRangeException("test", 5.0, ValueOutOfRangeException.MUST_BE_LESS_THAN, 3.0);
		TestCase.assertNotNull(ex);
		TestCase.assertEquals("5.000000 was supplied for test: test must be less than 3.000000", ex.getMessage());
	}
	
	/**
	 * Tests the newLessThanOrEqualException method.
	 */
	@Test
	public void newLessThanOrEqualException() {
		Exception ex = new ValueOutOfRangeException("test", 5.0, ValueOutOfRangeException.MUST_BE_LESS_THAN_OR_EQUAL_TO, 0.0);
		TestCase.assertNotNull(ex);
		TestCase.assertEquals("5.000000 was supplied for test: test must be less than or equal to 0.000000", ex.getMessage());
	}
	
	/**
	 * Tests the newNullElementException method.
	 */
	@Test
	public void newNullElementException() {
		Exception ex = new NullElementException("test");
		TestCase.assertNotNull(ex);
		TestCase.assertEquals("test contains a null element", ex.getMessage());
	}

	/**
	 * Tests the newNullElementException method.
	 */
	@Test
	public void newNullElementExceptionWithIndex() {
		Exception ex = new NullElementException("test", 2);
		TestCase.assertNotNull(ex);
		TestCase.assertEquals("test contains a null element at index 2", ex.getMessage());
	}
	
	/**
	 * Tests the newNullPointerException method.
	 */
	@Test
	public void newNullPointerException() {
		Exception ex = new ArgumentNullException("test");
		TestCase.assertNotNull(ex);
		TestCase.assertEquals("test cannot be null", ex.getMessage());
	}

	/**
	 * Tests the newObjectsEqualException method.
	 */
	@Test
	public void newObjectsEqualException() {
		Exception ex = new SameObjectException("test1", "test2", "hello");
		TestCase.assertNotNull(ex);
		TestCase.assertEquals("test1 and test2 cannot be the same object. Both were: hello", ex.getMessage());
	}

	/**
	 * Tests the newValueGreaterThanValueException method.
	 */
	@Test
	public void newValueLessThanValueException() {
		Exception ex = new ValueOutOfRangeException("test1", 5.0, ValueOutOfRangeException.MUST_BE_LESS_THAN, "test2", 3.0);
		TestCase.assertNotNull(ex);
		TestCase.assertEquals("test1 must be less than test2: test1 was 5.000000 and test2 was 3.000000", ex.getMessage());
	}

	/**
	 * Tests the newValueLessThanValueException method.
	 */
	@Test
	public void newValueGreaterThanValueException() {
		Exception ex = new ValueOutOfRangeException("test1", 5, ValueOutOfRangeException.MUST_BE_GREATER_THAN, "test2", 3);
		TestCase.assertNotNull(ex);
		TestCase.assertEquals("test1 must be greater than test2: test1 was 5 and test2 was 3", ex.getMessage());
	}
}
