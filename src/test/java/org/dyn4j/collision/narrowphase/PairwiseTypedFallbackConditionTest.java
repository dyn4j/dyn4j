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

import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link PairwiseTypedFallbackCondition} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class PairwiseTypedFallbackConditionTest {
	/**
	 * Tests the constructor
	 */
	@Test
	public void create() {
		PairwiseTypedFallbackCondition cond = new PairwiseTypedFallbackCondition(Ellipse.class, Circle.class);
		TestCase.assertEquals(Ellipse.class, cond.getType1());
		TestCase.assertEquals(Circle.class, cond.getType2());
		TestCase.assertEquals(true, cond.isStrict1());
		TestCase.assertEquals(true, cond.isStrict2());
		TestCase.assertEquals(0, cond.getSortIndex());
		TestCase.assertNotNull(cond.toString());
		
		cond = new PairwiseTypedFallbackCondition(Ellipse.class, Circle.class, 1);
		TestCase.assertEquals(Ellipse.class, cond.getType1());
		TestCase.assertEquals(Circle.class, cond.getType2());
		TestCase.assertEquals(true, cond.isStrict1());
		TestCase.assertEquals(true, cond.isStrict2());
		TestCase.assertEquals(1, cond.getSortIndex());
		TestCase.assertNotNull(cond.toString());
		
		cond = new PairwiseTypedFallbackCondition(Ellipse.class, Circle.class, false);
		TestCase.assertEquals(Ellipse.class, cond.getType1());
		TestCase.assertEquals(Circle.class, cond.getType2());
		TestCase.assertEquals(false, cond.isStrict1());
		TestCase.assertEquals(false, cond.isStrict2());
		TestCase.assertEquals(0, cond.getSortIndex());
		TestCase.assertNotNull(cond.toString());
		
		cond = new PairwiseTypedFallbackCondition(Ellipse.class, Circle.class, false, 1);
		TestCase.assertEquals(Ellipse.class, cond.getType1());
		TestCase.assertEquals(Circle.class, cond.getType2());
		TestCase.assertEquals(false, cond.isStrict1());
		TestCase.assertEquals(false, cond.isStrict2());
		TestCase.assertEquals(1, cond.getSortIndex());
		TestCase.assertNotNull(cond.toString());
		
		cond = new PairwiseTypedFallbackCondition(Ellipse.class, false, Circle.class, true);
		TestCase.assertEquals(Ellipse.class, cond.getType1());
		TestCase.assertEquals(Circle.class, cond.getType2());
		TestCase.assertEquals(false, cond.isStrict1());
		TestCase.assertEquals(true, cond.isStrict2());
		TestCase.assertEquals(0, cond.getSortIndex());
		TestCase.assertNotNull(cond.toString());
		
		cond = new PairwiseTypedFallbackCondition(Ellipse.class, false, Circle.class, true, 1);
		TestCase.assertEquals(Ellipse.class, cond.getType1());
		TestCase.assertEquals(Circle.class, cond.getType2());
		TestCase.assertEquals(false, cond.isStrict1());
		TestCase.assertEquals(true, cond.isStrict2());
		TestCase.assertEquals(1, cond.getSortIndex());
		TestCase.assertNotNull(cond.toString());
	}
	
	/**
	 * Tests the isMatch method.
	 */
	@Test
	public void isMatch() {
		// NOTE: that order doesn't matter
		
		PairwiseTypedFallbackCondition cond1 = new PairwiseTypedFallbackCondition(Polygon.class, Polygon.class);
		PairwiseTypedFallbackCondition cond2 = new PairwiseTypedFallbackCondition(Polygon.class, false, Polygon.class, false);
		PairwiseTypedFallbackCondition cond3 = new PairwiseTypedFallbackCondition(Polygon.class, false, Polygon.class, true);
		PairwiseTypedFallbackCondition cond4 = new PairwiseTypedFallbackCondition(Polygon.class, true, Polygon.class, false);
		
		TestCase.assertTrue(cond1.isMatch(Polygon.class, Polygon.class));
		TestCase.assertFalse(cond1.isMatch(Rectangle.class, Rectangle.class));
		TestCase.assertFalse(cond1.isMatch(Rectangle.class, Polygon.class));
		TestCase.assertFalse(cond1.isMatch(Polygon.class, Rectangle.class));
		TestCase.assertFalse(cond1.isMatch(Polygon.class, Circle.class));
		TestCase.assertFalse(cond1.isMatch(Circle.class, Polygon.class));
		
		TestCase.assertTrue(cond2.isMatch(Polygon.class, Polygon.class));
		TestCase.assertTrue(cond2.isMatch(Rectangle.class, Rectangle.class));
		TestCase.assertTrue(cond2.isMatch(Rectangle.class, Polygon.class));
		TestCase.assertTrue(cond2.isMatch(Polygon.class, Rectangle.class));
		TestCase.assertFalse(cond2.isMatch(Polygon.class, Circle.class));
		TestCase.assertFalse(cond2.isMatch(Circle.class, Polygon.class));
		
		TestCase.assertTrue(cond3.isMatch(Polygon.class, Polygon.class));
		TestCase.assertTrue(cond3.isMatch(Rectangle.class, Polygon.class));
		TestCase.assertTrue(cond3.isMatch(Polygon.class, Rectangle.class));
		TestCase.assertFalse(cond3.isMatch(Rectangle.class, Rectangle.class));
		TestCase.assertFalse(cond3.isMatch(Polygon.class, Circle.class));
		TestCase.assertFalse(cond3.isMatch(Circle.class, Polygon.class));
		
		TestCase.assertTrue(cond4.isMatch(Polygon.class, Polygon.class));
		TestCase.assertTrue(cond4.isMatch(Polygon.class, Rectangle.class));
		TestCase.assertTrue(cond4.isMatch(Rectangle.class, Polygon.class));
		TestCase.assertFalse(cond4.isMatch(Rectangle.class, Rectangle.class));
		TestCase.assertFalse(cond4.isMatch(Polygon.class, Circle.class));
		TestCase.assertFalse(cond4.isMatch(Circle.class, Polygon.class));
	}
}
