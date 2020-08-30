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
package org.dyn4j;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link Reference} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 */
public class ReferenceTest {
	/**
	 * Tests the constructors.
	 */
	@Test
	public void testCreate() {
		// neither should throw an exception
		new Reference<Integer>();
		new Reference<Integer>(5);
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void testToString() {
		TestCase.assertEquals("null", new Reference<Integer>().toString());
		TestCase.assertEquals(Integer.valueOf(5).toString(), new Reference<Integer>(5).toString());
	}
	
	/**
	 * Tests the equals method.
	 */
	@Test
	public void testEquals() {
		Reference<Integer> test = new Reference<Integer>(5);
		TestCase.assertEquals(new Reference<Integer>(), new Reference<Integer>());
		TestCase.assertEquals(new Reference<Integer>(5), new Reference<Integer>(5));
		TestCase.assertEquals(test, new Reference<Integer>(5));
		TestCase.assertEquals(test, test);
		
		Object obj = new Object();
		Reference<Object> test2 = new Reference<Object>(obj);
		TestCase.assertEquals(new Reference<Object>(), new Reference<Object>());
		TestCase.assertEquals(new Reference<Object>(obj), new Reference<Object>(obj));
		TestCase.assertEquals(test2, new Reference<Object>(obj));
		TestCase.assertEquals(test2, test2);
		
		TestCase.assertFalse(new Reference<Integer>(1).equals(new Reference<Integer>()));
		TestCase.assertFalse(new Reference<Integer>().equals(new Reference<Integer>(1)));
		TestCase.assertFalse(new Reference<Integer>(1).equals(new Reference<Integer>(5)));
		TestCase.assertFalse(test.equals(new Reference<Integer>(4)));
		TestCase.assertFalse(test.equals(null));
	}
	
	/**
	 * Tests the hashcode method.
	 */
	@Test
	public void testHashcode() {
		Reference<Integer> test = new Reference<Integer>(5);
		TestCase.assertEquals(new Reference<Integer>().hashCode(), new Reference<Integer>().hashCode());
		TestCase.assertEquals(new Reference<Integer>(5).hashCode(), new Reference<Integer>(5).hashCode());
		TestCase.assertEquals(test.hashCode(), new Reference<Integer>(5).hashCode());
		TestCase.assertEquals(test.hashCode(), test.hashCode());
		
		Object obj = new Object();
		Reference<Object> test2 = new Reference<Object>(obj);
		TestCase.assertEquals(new Reference<Object>().hashCode(), new Reference<Object>().hashCode());
		TestCase.assertEquals(new Reference<Object>(obj).hashCode(), new Reference<Object>(obj).hashCode());
		TestCase.assertEquals(test2.hashCode(), new Reference<Object>(obj).hashCode());
		TestCase.assertEquals(test2.hashCode(), test2.hashCode());
		
		TestCase.assertFalse(new Reference<Integer>(1).hashCode() == new Reference<Integer>().hashCode());
		TestCase.assertFalse(new Reference<Integer>().hashCode() == new Reference<Integer>(1).hashCode());
		TestCase.assertFalse(new Reference<Integer>(1).hashCode() == new Reference<Integer>(5).hashCode());
		TestCase.assertFalse(test.hashCode() == new Reference<Integer>(4).hashCode());
	}
}
