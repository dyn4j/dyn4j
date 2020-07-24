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
package org.dyn4j.dynamics;

import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Class used to test the {@link Force} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.2
 */
public class ForceTest {
	/**
	 * Tests successful creation.
	 */
	@Test
	public void createSuccess() {
		Force f = new Force();
		f = new Force(0.3, 2.0);
		TestCase.assertEquals(0.3, f.force.x);
		TestCase.assertEquals(2.0, f.force.y);
		TestCase.assertEquals(0.3, f.getForce().x);
		TestCase.assertEquals(2.0, f.getForce().y);
		
		Force f2 = new Force(f);
		TestCase.assertEquals(0.3, f2.force.x);
		TestCase.assertEquals(2.0, f2.force.y);
		TestCase.assertNotSame(f.force, f2.force);
		TestCase.assertEquals(0.3, f2.getForce().x);
		TestCase.assertEquals(2.0, f2.getForce().y);
		TestCase.assertNotSame(f.getForce(), f2.getForce());
		
		f = new Force(new Vector2(2.0, 1.0));
		TestCase.assertEquals(2.0, f.force.x);
		TestCase.assertEquals(1.0, f.force.y);
		TestCase.assertEquals(2.0, f.getForce().x);
		TestCase.assertEquals(1.0, f.getForce().y);
	}
	
	/**
	 * Tests the set methods successfully.
	 */
	@Test
	public void setSuccess() {
		Force f = new Force();
		f.set(3.0, -2.0);
		TestCase.assertEquals(3.0, f.force.x);
		TestCase.assertEquals(-2.0, f.force.y);
		
		f.set(new Vector2(2.0, 1.0));
		TestCase.assertEquals(2.0, f.force.x);
		TestCase.assertEquals(1.0, f.force.y);
		
		Force f2 = new Force(0.5, -2.0);
		f.set(f2);
		TestCase.assertEquals(0.5, f.force.x);
		TestCase.assertEquals(-2.0, f.force.y);
		TestCase.assertNotSame(f.force, f2.force);
	}
	
	/**
	 * Tests creation using a null force.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullForce() {
		new Force((Force) null);
	}
	
	/**
	 * Tests creation using a null force vector.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullVector() {
		new Force((Vector2) null);
	}
	
	/**
	 * Tests setting the force using a null force.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullForce() {
		Force f = new Force();
		f.set((Force) null);
	}
	
	/**
	 * Tests setting the force using a null force vector.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullVector() {
		Force f = new Force();
		f.set((Vector2) null);
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void tostring() {
		Force f = new Force();
		
		TestCase.assertNotNull(f.toString());
		
		f.set(2.0, 0.4);
		TestCase.assertNotNull(f.toString());
	}
	
	/**
	 * Tests the default isComplete method.
	 */
	@Test
	public void isComplete() {
		Force f = new Force();
		
		// by default it should be true
		TestCase.assertTrue(f.isComplete(0.0));
	}
}
