/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision.narrowphase;

import org.dyn4j.geometry.Convex;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link FallbackNarrowphaseDetector} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class AbstractFallbackConditionTest {
	private class TestCondition extends AbstractFallbackCondition {
		public TestCondition(int index) {
			super(index);
		}
		@Override
		public boolean isMatch(Convex convex1, Convex convex2) { return false; }
	};
	
	private class TestTypedFallbackCondition extends TypedFallbackCondition {
		@Override
		public boolean isMatch(Class<? extends Convex> type1, Class<? extends Convex> type2) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	/**
	 * Tests the constructor
	 */
	@Test
	public void create() {
		TestCondition tc1 = new TestCondition(1);
		
		TestCase.assertEquals(1, tc1.getSortIndex());
		
		TestTypedFallbackCondition tc2 = new TestTypedFallbackCondition();
		
		TestCase.assertEquals(0, tc2.getSortIndex());
	}
	
	/**
	 * Tests the sortIndex getter
	 */
	@Test
	public void getSortIndex() {
		TestCondition tc1 = new TestCondition(1);
		TestCondition tc2 = new TestCondition(2);
		TestCondition tc3 = new TestCondition(5);
		TestCondition tc4 = new TestCondition(-1);
		TestCondition tc5 = new TestCondition(2);
		
		TestCase.assertEquals(1, tc1.getSortIndex());
		TestCase.assertEquals(2, tc2.getSortIndex());
		TestCase.assertEquals(5, tc3.getSortIndex());
		TestCase.assertEquals(-1, tc4.getSortIndex());
		TestCase.assertEquals(2, tc5.getSortIndex());
	}
	
	/**
	 * Tests the compareTo method
	 */
	@Test
	public void compareTo() {
		TestCondition tc1 = new TestCondition(1);
		TestCondition tc2 = new TestCondition(2);
		TestCondition tc3 = new TestCondition(5);
		TestCondition tc4 = new TestCondition(-1);
		TestCondition tc5 = new TestCondition(2);
		
		TestCase.assertTrue(tc1.compareTo(tc4) > 0);
		TestCase.assertTrue(tc4.compareTo(tc1) < 0);
		TestCase.assertEquals(0, tc2.compareTo(tc5));
		TestCase.assertEquals(0, tc5.compareTo(tc2));
		TestCase.assertTrue(tc1.compareTo(tc3) < 0);
		TestCase.assertTrue(tc3.compareTo(tc1) > 0);
	}
}
