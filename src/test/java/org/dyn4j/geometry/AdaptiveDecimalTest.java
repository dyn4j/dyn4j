/*
 * Copyright (c) 2010-2017 William Bittle  http://www.dyn4j.org/
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

import java.util.Random;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test cases and randomized versions for the methods in {@link AdaptiveDecimal} class.
 * @author Manolis Tsamis
 * @version 3.4.0
 * @since 3.4.0
 */
public class AdaptiveDecimalTest {
	
	/**
	 * Test the various ways to create new {@link AdaptiveDecimal} instances
	 */
	@Test
	public void create() {
		AdaptiveDecimal ed = new AdaptiveDecimal(2);
		TestCase.assertEquals(0, ed.size());
		TestCase.assertEquals(2, ed.capacity());
		
		ed.append(1.0);
		
		AdaptiveDecimal ed2 = new AdaptiveDecimal(ed);
		TestCase.assertEquals(1, ed.size());
		TestCase.assertEquals(2, ed.capacity());
		
		ed2.clear();
		ed2.append(2.0);
		
		TestCase.assertEquals(1.0, ed.get(0));
		
		AdaptiveDecimal ed3 = AdaptiveDecimal.valueOf(10.0);
		TestCase.assertEquals(10.0, ed3.get(0));
		
		AdaptiveDecimal ed4 = AdaptiveDecimal.fromSum(0.1, 0.2);
		TestCase.assertEquals(0.1 + 0.2, ed4.getEstimation());
		
		AdaptiveDecimal ed5 = AdaptiveDecimal.fromDiff(0.1, 0.2);
		TestCase.assertEquals(0.1 - 0.2, ed5.getEstimation());
		
		AdaptiveDecimal ed6 = AdaptiveDecimal.fromProduct(0.1, 0.2);
		TestCase.assertEquals(0.1 * 0.2, ed6.getEstimation());
	}
	
	/**
	 * Tests for methods that check the logical/physical size of a {@link AdaptiveDecimal}
	 */
	@Test
	public void size() {
		AdaptiveDecimal ed = new AdaptiveDecimal(2);
		TestCase.assertEquals(0, ed.size());
		TestCase.assertEquals(2, ed.capacity());
		
		ed.append(1.0);
		TestCase.assertEquals(1, ed.size());
		TestCase.assertEquals(2, ed.capacity());
	}
	
	/**
	 * Tests for appending new components to a {@link AdaptiveDecimal}
	 */
	@Test
	public void append() {
		AdaptiveDecimal ed = new AdaptiveDecimal(5);
		
		ed.append(1.0);
		TestCase.assertEquals(1, ed.size());
		TestCase.assertEquals(1.0, ed.get(0));

		ed.append(15.5);
		TestCase.assertEquals(2, ed.size());
		TestCase.assertEquals(15.5, ed.get(1));
		
		ed.appendNonZero(0.0);
		TestCase.assertEquals(2, ed.size());
		
		ed.appendNonZero(111.0);
		TestCase.assertEquals(3, ed.size());
		TestCase.assertEquals(111.0, ed.get(2));
	}
	
	/**
	 * Tests the various ways to copy {@link AdaptiveDecimal}s
	 */
	@Test
	public void copy() {
		AdaptiveDecimal ed = new AdaptiveDecimal(2);
		ed.append(0.1).append(15.0);
		
		AdaptiveDecimal copy = ed.copy();
		
		TestCase.assertEquals(0.1, copy.get(0));
		TestCase.assertEquals(15.0, copy.get(1));
		
		copy.clear();
		copy.append(0.2);
		
		TestCase.assertEquals(0.1, ed.get(0));
		TestCase.assertEquals(15.0, ed.get(1));
		
		AdaptiveDecimal copy2 = new AdaptiveDecimal(5);
		copy2.copyFrom(ed);
		
		TestCase.assertEquals(0.1, copy2.get(0));
		TestCase.assertEquals(15.0, copy2.get(1));
		TestCase.assertEquals(5, copy2.capacity());
		
		copy2.clear();
		copy2.append(0.2);
		
		TestCase.assertEquals(0.1, ed.get(0));
		TestCase.assertEquals(15.0, ed.get(1));
	}
	
	/**
	 * Test the clear method
	 */
	@Test
	public void clear() {
		AdaptiveDecimal ed = new AdaptiveDecimal(2);
		ed.append(0.1).append(15.0);
		
		TestCase.assertEquals(2, ed.size());
		
		ed.clear();
		
		TestCase.assertEquals(0, ed.size());
	}
	
	/**
	 * Test the method that removes unnecessary zeros from a {@link AdaptiveDecimal}
	 */
	@Test
	public void removeZeros() {
		AdaptiveDecimal ed = new AdaptiveDecimal(6);
		
		ed.removeZeros();
		TestCase.assertEquals(0, ed.size());
		
		ed.append(0.0);
		ed.removeZeros();
		TestCase.assertEquals(0, ed.size());

		ed.append(0.1).append(2.0).append(0.0).append(10.5).append(0.0);
		ed.removeZeros();
		TestCase.assertEquals(3, ed.size());
	}
	
	/**
	 * Test the method that negates the value of a {@link AdaptiveDecimal}
	 */
	@Test
	public void negate() {
		AdaptiveDecimal ed = new AdaptiveDecimal(5);
		
		ed.negate();
		
		ed.append(1.0);
		ed = ed.sum(AdaptiveDecimal.valueOf(2.0));
		ed = ed.sum(AdaptiveDecimal.valueOf(3.0));
		ed = ed.sum(AdaptiveDecimal.valueOf(5.0));
		ed.negate();
		
		TestCase.assertEquals(-11.0, ed.getEstimation());
		
		ed.clear();
		ed.append(-1.0);
		ed = ed.sum(AdaptiveDecimal.valueOf(-2.0));
		ed = ed.sum(AdaptiveDecimal.valueOf(-7.0));
		ed.negate();
		
		TestCase.assertEquals(10.0, ed.getEstimation());
		ed.negate();
		TestCase.assertEquals(-10.0, ed.getEstimation());
	}
	
	/**
	 * Test the method that estimates a {@link AdaptiveDecimal}'s value with a double
	 */
	@Test
	public void getEstimation() {
		AdaptiveDecimal ed = new AdaptiveDecimal(5);
		
		TestCase.assertEquals(0.0, ed.getEstimation());
		
		ed.append(0.0);
		ed = ed.sum(AdaptiveDecimal.valueOf(1.0e-5));
		ed = ed.sum(AdaptiveDecimal.valueOf(1.0e2));
		ed = ed.sum(AdaptiveDecimal.valueOf(1.0e100));
		
		TestCase.assertEquals(1.0e100, ed.getEstimation());
		
		ed.clear();
		ed.append(1.0);
		ed = ed.sum(AdaptiveDecimal.valueOf(2.0));
		ed = ed.sum(AdaptiveDecimal.valueOf(3.0));
		ed = ed.sum(AdaptiveDecimal.valueOf(-5.0));
		
		TestCase.assertEquals(1.0, ed.getEstimation());
	}
	
	/**
	 * Test the normalize method that ensures a {@link AdaptiveDecimal} has at least one component
	 */
	@Test
	public void normalize() {
		AdaptiveDecimal ed = new AdaptiveDecimal(2);
		TestCase.assertEquals(0, ed.size());
		ed.normalize();
		
		TestCase.assertEquals(1, ed.size());
		
		AdaptiveDecimal ed2 = new AdaptiveDecimal(2);
		ed2.append(1.0).append(-4.0);
		ed2.normalize();
		
		TestCase.assertEquals(2, ed2.size());
		TestCase.assertEquals(1.0, ed2.get(0));
		TestCase.assertEquals(-4.0, ed2.get(1));
	}
	
	/**
	 * Test the addition method. The sum method is also throughly tested below with more randomized tests.
	 */
	@Test
	public void sum() {
		AdaptiveDecimal ed1 = AdaptiveDecimal.valueOf(10.0);
		AdaptiveDecimal ed2 = AdaptiveDecimal.valueOf(3.0);
		
		AdaptiveDecimal result = ed1.sum(ed2);
		TestCase.assertEquals(13.0, result.getEstimation(), 1.0e-9);
		
		AdaptiveDecimal ed3 = AdaptiveDecimal.valueOf(0.35);
		
		AdaptiveDecimal result2 = result.sum(ed3);
		TestCase.assertEquals(13.35, result2.getEstimation(), 1.0e-9);
	}
	
	/** Seed for the randomized test. Can be any value */
	private static final int SEED = 0;
	
	/**
	 * Test the methods that checks whether a particular {@link AdaptiveDecimal} honors the
	 * invariants described in the corresponding class.
	 * Also checks that the result of various operations continues to satisfy those invariants (as they should). 
	 */
	@Test
	public void checkInvariants() {
		AdaptiveDecimal ed1 = new AdaptiveDecimal(3);
		ed1.append(1.0).append(0.0).append(0.2);
		TestCase.assertFalse(ed1.checkInvariants());

		AdaptiveDecimal ed2 = new AdaptiveDecimal(5);
		ed2.append(-1.0).append(0.0).append(0.0).append(0.2).append(0.0);
		TestCase.assertFalse(ed2.checkInvariants());

		AdaptiveDecimal ed3 = new AdaptiveDecimal(2);
		ed3.append(1.0e-2).append(1.0e2 + 1.0e-2);
		TestCase.assertFalse(ed3.checkInvariants());

		AdaptiveDecimal ed4 = new AdaptiveDecimal(5);
		ed4.append(-1.0 / 4.0).append(0.0).append(0.0).append(4.0 + 1.0 / 4.0).append(0.0);
		TestCase.assertFalse(ed4.checkInvariants());
		
		AdaptiveDecimal ed5 = new AdaptiveDecimal(5);
		ed5.append(0.0).append(1.0 / 4.0).append(0.0).append(4.0 + 32.0).append(32.0 * 32.0);
		TestCase.assertTrue(ed5.checkInvariants());
		ed5.ensureInvariants();
		
		AdaptiveDecimal ed6 = new AdaptiveDecimal(3);
		ed6.append(0.0).append(0.0).append(0.0);
		TestCase.assertTrue(ed6.checkInvariants());
		ed5.ensureInvariants();
		
		// Constant seed so we always get the same sequence of randoms
		Random random = new Random(SEED);
		final int iterations = 10000;
		
		AdaptiveDecimal acc = AdaptiveDecimal.valueOf(0);
		
		for (int i=0;i<iterations;i++) {
			AdaptiveDecimal test = AdaptiveDecimal.valueOf(random.nextDouble() * 10 - 5);
			acc = acc.sum(test);
			
			TestCase.assertTrue(acc.checkInvariants());
		}
		
		for (int i=0;i<iterations;i++) {
			double a = random.nextDouble() * 10 - 5;
			double b = random.nextDouble() * 10 - 5;
			AdaptiveDecimal test;

			test = AdaptiveDecimal.fromSum(a, b);
			TestCase.assertTrue(test.checkInvariants());
			
			test = AdaptiveDecimal.fromDiff(a, b);
			TestCase.assertTrue(test.checkInvariants());
			
			test = AdaptiveDecimal.fromProduct(a, b);
			TestCase.assertTrue(test.checkInvariants());

			double c = random.nextDouble() * 10 - 5;
			double d = random.nextDouble() * 10 - 5;
			
			// Also check with the related RobustGeometry.cross method
			test = RobustGeometry.cross(a, b, c, d);
			TestCase.assertTrue(test.checkInvariants());
		}
	}
	
	/**
	 * Check {@link IndexOutOfBoundsException} for negative index.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testIndex1() {
		new AdaptiveDecimal(1).get(-1);
	}
	
	/**
	 * Check {@link IndexOutOfBoundsException} for index bigger than capacity.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testIndex2() {
		new AdaptiveDecimal(2).get(2);
	}
	
	/**
	 * Check {@link IndexOutOfBoundsException} for adding more components than the capacity.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testIndex3() {
		new AdaptiveDecimal(2).append(0.0).append(0.0).append(0.0);
	}
	
	/**
	 * Check {@link IllegalArgumentException} for creating a {@link AdaptiveDecimal} with negative capacity.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testArgument1() {
		new AdaptiveDecimal(-5);
	}
	
	/**
	 * Check {@link IllegalArgumentException} for creating a {@link AdaptiveDecimal} with zero capacity.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testArgument2() {
		new AdaptiveDecimal(0);
	}
	
	/**
	 * Check that ensureInvariants fires the corresponding {@link IllegalStateException} if
	 * the invariants do not hold.
	 */
	@Test(expected = IllegalStateException.class)
	public void testState() {
		AdaptiveDecimal ed = new AdaptiveDecimal(2);
		ed.append(1.0).append(0.5);
		ed.ensureInvariants();
	}
	
}
