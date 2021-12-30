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
package org.dyn4j.collision;

import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link AbstractBounds} class.
 * @author William Bittle
 * @version 4.2.1
 * @since 4.0.0
 */
public class AbstractBoundsTest {
	private class TestBounds extends AbstractBounds {
		public TestBounds() {
			super();
		}

		public TestBounds(double x, double y) {
			super(x, y);
		}

		public TestBounds(Vector2 translation) {
			super(translation);
		}

		@Override
		public boolean isOutside(AABB aabb) {
			return false;
		}
		
		@Override
		public boolean isOutside(CollisionBody<?> body) {
			return false;
		}
		
		@Override
		public boolean isOutside(AABB aabb, Transform transform, Fixture fixture) {
			return false;
		}
	}
	
	/**
	 * Tests the constructors.
	 */
	@Test
	public void create() {
		Bounds b = new TestBounds();
		TestCase.assertEquals(0.0, b.getTranslation().x);
		TestCase.assertEquals(0.0, b.getTranslation().y);
		
		b = new TestBounds(1.0, 2.0);
		TestCase.assertEquals(1.0, b.getTranslation().x);
		TestCase.assertEquals(2.0, b.getTranslation().y);
		
		b = new TestBounds(new Vector2(1.0, 2.0));
		TestCase.assertEquals(1.0, b.getTranslation().x);
		TestCase.assertEquals(2.0, b.getTranslation().y);
	}
	
	/**
	 * Tests the translate and getTranslation methods.
	 */
	@Test
	public void translation() {
		Bounds b = new TestBounds();
		TestCase.assertEquals(0.0, b.getTranslation().x);
		TestCase.assertEquals(0.0, b.getTranslation().y);
		
		b.translate(1.0, 0.0);
		TestCase.assertEquals(1.0, b.getTranslation().x);
		TestCase.assertEquals(0.0, b.getTranslation().y);
		
		b.translate(5.0, 6.0);
		TestCase.assertEquals(6.0, b.getTranslation().x);
		TestCase.assertEquals(6.0, b.getTranslation().y);
		
		b.translate(new Vector2(-2.0, 1.5));
		TestCase.assertEquals(4.0, b.getTranslation().x);
		TestCase.assertEquals(7.5, b.getTranslation().y);
	}
	
	/**
	 * Tests shifting the coordinates of the bounds.
	 */
	@Test
	public void shiftCoordinates() {
		Bounds b = new TestBounds();
		TestCase.assertEquals(0.0, b.getTranslation().x);
		TestCase.assertEquals(0.0, b.getTranslation().y);
		
		// test the shifting which is really just a translation
		b.shift(new Vector2(1.0, 1.0));
		TestCase.assertEquals(1.0, b.getTranslation().x);
		TestCase.assertEquals(1.0, b.getTranslation().y);
	}
}
