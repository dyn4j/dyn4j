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
package org.dyn4j.dynamics.contact;

import org.dyn4j.collision.manifold.ManifoldPointId;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests the methods of the {@link SolvableContact} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class SolvableContactTest {
	/**
	 * Tests the creation.
	 */
	@Test
	public void create() {
		ManifoldPointId id = ManifoldPointId.DISTANCE;
		Vector2 p = new Vector2(1, 3);
		double d = 5.0;
		Vector2 p1 = new Vector2(3, 4);
		Vector2 p2 = new Vector2(5, 6);
		SolvableContact c = new SolvableContact(id, p, d, p1, p2);
		
		TestCase.assertEquals(id, c.getId());
		TestCase.assertEquals(p.x, c.getPoint().x);
		TestCase.assertEquals(p.y, c.getPoint().y);
		TestCase.assertEquals(p1.x, c.p1.x);
		TestCase.assertEquals(p1.y, c.p1.y);
		TestCase.assertEquals(p2.x, c.p2.x);
		TestCase.assertEquals(p2.y, c.p2.y);
		TestCase.assertEquals(d, c.getDepth());
		TestCase.assertEquals(0.0, c.getNormalImpulse());
		TestCase.assertEquals(0.0, c.getTangentialImpulse());
		TestCase.assertEquals(0.0, c.jp);
		TestCase.assertEquals(0.0, c.massN);
		TestCase.assertEquals(0.0, c.massT);
		TestCase.assertEquals(0.0, c.vb);
		TestCase.assertEquals(false, c.ignored);
		TestCase.assertEquals(null, c.r1);
		TestCase.assertEquals(null, c.r2);
		TestCase.assertEquals(true, c.isSolved());
		
		
	}
	
	/**
	 * Tests the isSolved method.
	 */
	@Test
	public void isSolved() {
		ManifoldPointId id = ManifoldPointId.DISTANCE;
		Vector2 p = new Vector2(1, 3);
		double d = 5.0;
		Vector2 p1 = new Vector2(3, 4);
		Vector2 p2 = new Vector2(5, 6);
		SolvableContact c = new SolvableContact(id, p, d, p1, p2);
		
		TestCase.assertEquals(false, c.ignored);
		TestCase.assertEquals(true, c.isSolved());
		
		c.ignored = true;
		TestCase.assertEquals(false, c.isSolved());
	}
}
