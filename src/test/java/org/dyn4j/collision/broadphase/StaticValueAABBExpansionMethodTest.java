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
package org.dyn4j.collision.broadphase;

import org.dyn4j.geometry.AABB;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Class used to test the {@link StaticValueAABBExpansionMethod} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class StaticValueAABBExpansionMethodTest {
	/**
	 * Tests valid input to the filter.
	 */
	@Test
	public void expand() {
		StaticValueAABBExpansionMethod<Object> method = new StaticValueAABBExpansionMethod<Object>(4);
		
		TestCase.assertEquals(4.0, method.getExpansion());
		
		AABB aabb = new AABB(0,0,0,0);
		method.expand(null, aabb);
		
		TestCase.assertEquals(-2.0, aabb.getMinX());
		TestCase.assertEquals(-2.0, aabb.getMinY());
		TestCase.assertEquals(2.0, aabb.getMaxX());
		TestCase.assertEquals(2.0, aabb.getMaxY());
		
		aabb = new AABB(-3, -2, 1, 5);
		method.expand(null, aabb);
		
		TestCase.assertEquals(-5.0, aabb.getMinX());
		TestCase.assertEquals(-4.0, aabb.getMinY());
		TestCase.assertEquals(3.0, aabb.getMaxX());
		TestCase.assertEquals(7.0, aabb.getMaxY());
	}
}
