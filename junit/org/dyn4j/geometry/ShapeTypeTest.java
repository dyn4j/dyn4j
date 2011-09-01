/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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

import junit.framework.TestCase;

import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Shape;
import org.junit.Test;

/**
 * Test case for the {@link Shape.Type} class.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public class ShapeTypeTest {
	/**
	 * Tests the is method of the {@link Shape.Type} class.
	 */
	@Test
	public void is() {
		// base types
		Shape.Type t1 = new Shape.Type("type1");
		Shape.Type t2 = new Shape.Type("type2");
		
		// sub types
		Shape.Type t3 = new Shape.Type(t1, "type3");
		Shape.Type t4 = new Shape.Type(t3, "type4");
		
		// not the same
		TestCase.assertFalse(t1.is(t2));
		
		// sub type
		TestCase.assertTrue(t3.is(t1));
		
		// sub sub type
		TestCase.assertTrue(t4.is(t1));
		
		// sub type
		TestCase.assertTrue(t4.is(t3));
		
		Circle c = new Circle(1.0);
		TestCase.assertTrue(c.isType(Circle.TYPE));
		
		Rectangle r = new Rectangle(1.0, 1.0);
		TestCase.assertTrue(r.isType(Rectangle.TYPE));
		TestCase.assertTrue(r.isType(Polygon.TYPE));
	}
}
