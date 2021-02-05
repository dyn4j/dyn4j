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

import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests the {@link MinkowskiSumPoint} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class MinkowskiSumPointTest {
	/**
	 * Tests the creation of a {@link MinkowskiSumPoint}.
	 */
	@Test
	public void createSuccess() {
		Vector2 p1 = new Vector2(0.0, 0.0);
		Vector2 p2 = new Vector2(1.0, 1.0);
		
		MinkowskiSumPoint msp = new MinkowskiSumPoint(p1, p2);
		
		TestCase.assertEquals(p1, msp.getSupportPoint1());
		TestCase.assertEquals(p2, msp.getSupportPoint2());
		
		TestCase.assertEquals(p1.x, msp.getSupportPoint1().x);
		TestCase.assertEquals(p1.y, msp.getSupportPoint1().y);
		TestCase.assertEquals(p2.x, msp.getSupportPoint2().x);
		TestCase.assertEquals(p2.y, msp.getSupportPoint2().y);
		TestCase.assertEquals(-1.0, msp.getPoint().x);
		TestCase.assertEquals(-1.0, msp.getPoint().y);
		
		TestCase.assertNotNull(msp.toString());
	}
}
