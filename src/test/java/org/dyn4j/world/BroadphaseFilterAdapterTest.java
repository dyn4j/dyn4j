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
package org.dyn4j.world;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link BroadphaseCollisionDataFilterAdapter} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 */
public class BroadphaseFilterAdapterTest {
	/**
	 * Tests the default behavior of the adapter.
	 */
	@Test
	public void alwaysAllowed() {
		BroadphaseCollisionDataFilterAdapter<Body, BodyFixture> filter = new BroadphaseCollisionDataFilterAdapter<Body, BodyFixture>();
		
		Body b1 = new Body();
		Body b2 = new Body();
		BodyFixture b1f1 = b1.addFixture(Geometry.createCircle(0.5));
		BodyFixture b2f1 = b2.addFixture(Geometry.createCircle(0.5));
		
		// always returns true
		TestCase.assertTrue(filter.isAllowed(b1, b1f1, b2, b2f1));
		TestCase.assertTrue(filter.isAllowed(null, null, null, null));
	}
}
