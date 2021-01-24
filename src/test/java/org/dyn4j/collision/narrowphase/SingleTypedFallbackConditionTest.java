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
 * Test case for the {@link SingleTypedFallbackCondition} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class SingleTypedFallbackConditionTest {
	/**
	 * Tests the constructor
	 */
	@Test
	public void create() {
		SingleTypedFallbackCondition cond = new SingleTypedFallbackCondition(Ellipse.class);
		TestCase.assertEquals(Ellipse.class, cond.getType());
		TestCase.assertEquals(true, cond.isStrict());
		TestCase.assertEquals(0, cond.getSortIndex());
		TestCase.assertNotNull(cond.toString());
		
		cond = new SingleTypedFallbackCondition(Ellipse.class, 1);
		TestCase.assertEquals(Ellipse.class, cond.getType());
		TestCase.assertEquals(true, cond.isStrict());
		TestCase.assertEquals(1, cond.getSortIndex());
		TestCase.assertNotNull(cond.toString());
		
		cond = new SingleTypedFallbackCondition(Ellipse.class, false);
		TestCase.assertEquals(Ellipse.class, cond.getType());
		TestCase.assertEquals(false, cond.isStrict());
		TestCase.assertEquals(0, cond.getSortIndex());
		TestCase.assertNotNull(cond.toString());
		
		cond = new SingleTypedFallbackCondition(Ellipse.class, 1, false);
		TestCase.assertEquals(Ellipse.class, cond.getType());
		TestCase.assertEquals(false, cond.isStrict());
		TestCase.assertEquals(1, cond.getSortIndex());
		TestCase.assertNotNull(cond.toString());
	}
	
	/**
	 * Tests the isMatch method.
	 */
	@Test
	public void isMatch() {
		// NOTE: that order doesn't matter
		
		SingleTypedFallbackCondition cond1 = new SingleTypedFallbackCondition(Polygon.class);
		SingleTypedFallbackCondition cond2 = new SingleTypedFallbackCondition(Polygon.class, false);
		SingleTypedFallbackCondition cond3 = new SingleTypedFallbackCondition(Polygon.class, true);
		
		TestCase.assertTrue(cond1.isMatch(Polygon.class, Polygon.class));
		TestCase.assertFalse(cond1.isMatch(Rectangle.class, Rectangle.class));
		TestCase.assertTrue(cond1.isMatch(Rectangle.class, Polygon.class));
		TestCase.assertTrue(cond1.isMatch(Polygon.class, Rectangle.class));
		TestCase.assertTrue(cond1.isMatch(Polygon.class, Circle.class));
		TestCase.assertTrue(cond1.isMatch(Circle.class, Polygon.class));
		TestCase.assertFalse(cond1.isMatch(Circle.class, Rectangle.class));
		TestCase.assertFalse(cond1.isMatch(Rectangle.class, Circle.class));
		TestCase.assertFalse(cond1.isMatch(Circle.class, Circle.class));
		
		TestCase.assertTrue(cond2.isMatch(Polygon.class, Polygon.class));
		TestCase.assertTrue(cond2.isMatch(Rectangle.class, Rectangle.class));
		TestCase.assertTrue(cond2.isMatch(Rectangle.class, Polygon.class));
		TestCase.assertTrue(cond2.isMatch(Polygon.class, Rectangle.class));
		TestCase.assertTrue(cond2.isMatch(Polygon.class, Circle.class));
		TestCase.assertTrue(cond2.isMatch(Circle.class, Polygon.class));
		TestCase.assertTrue(cond2.isMatch(Circle.class, Rectangle.class));
		TestCase.assertTrue(cond2.isMatch(Rectangle.class, Circle.class));
		TestCase.assertFalse(cond2.isMatch(Circle.class, Circle.class));
		
		TestCase.assertTrue(cond3.isMatch(Polygon.class, Polygon.class));
		TestCase.assertFalse(cond3.isMatch(Rectangle.class, Rectangle.class));
		TestCase.assertTrue(cond3.isMatch(Rectangle.class, Polygon.class));
		TestCase.assertTrue(cond3.isMatch(Polygon.class, Rectangle.class));
		TestCase.assertTrue(cond3.isMatch(Polygon.class, Circle.class));
		TestCase.assertTrue(cond3.isMatch(Circle.class, Polygon.class));
		TestCase.assertFalse(cond3.isMatch(Circle.class, Rectangle.class));
		TestCase.assertFalse(cond3.isMatch(Rectangle.class, Circle.class));
		TestCase.assertFalse(cond3.isMatch(Circle.class, Circle.class));
	}
}
