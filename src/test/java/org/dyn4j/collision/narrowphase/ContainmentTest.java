/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link Containment} class.
 * @author William Bittle
 * @version 4.2.1
 * @since 4.2.1
 */
public class ContainmentTest {
	/**
	 * Tests the constructor
	 */
	@Test
	public void create() {
		Containment con = new Containment();
		
		TestCase.assertFalse(con.aContainedInB);
		TestCase.assertFalse(con.bContainedInA);
		TestCase.assertFalse(con.isAContainedInB());
		TestCase.assertFalse(con.isBContainedInA());
		
		con = new Containment(true, false);
		
		TestCase.assertTrue(con.aContainedInB);
		TestCase.assertFalse(con.bContainedInA);
		TestCase.assertTrue(con.isAContainedInB());
		TestCase.assertFalse(con.isBContainedInA());
		
		TestCase.assertNotNull(con.toString());
	}
	
	/**
	 * Tests the getters/setters.
	 */
	@Test
	public void getSet() {
		Containment con = new Containment();
		
		TestCase.assertFalse(con.isAContainedInB());
		TestCase.assertFalse(con.isBContainedInA());
		
		con.setAContainedInB(true);
		
		TestCase.assertTrue(con.isAContainedInB());
		TestCase.assertFalse(con.isBContainedInA());
		
		con.setBContainedInA(true);
		
		TestCase.assertTrue(con.isAContainedInB());
		TestCase.assertTrue(con.isBContainedInA());
		

		con.setAContainedInB(false);
		
		TestCase.assertFalse(con.isAContainedInB());
		TestCase.assertTrue(con.isBContainedInA());
		

		con.setBContainedInA(false);
		
		TestCase.assertFalse(con.isAContainedInB());
		TestCase.assertFalse(con.isBContainedInA());
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		Containment con = new Containment(true, true);
		
		TestCase.assertTrue(con.isAContainedInB());
		TestCase.assertTrue(con.isBContainedInA());
		
		con.clear();
		
		TestCase.assertFalse(con.isAContainedInB());
		TestCase.assertFalse(con.isBContainedInA());
	}
	
	/**
	 * Tests the copy methods.
	 */
	@Test
	public void copy() {
		Containment con = new Containment(true, true);
		
		TestCase.assertTrue(con.isAContainedInB());
		TestCase.assertTrue(con.isBContainedInA());
		
		Containment con2 = new Containment();
		con2.copy(con);
		
		TestCase.assertTrue(con2.isAContainedInB());
		TestCase.assertTrue(con2.isBContainedInA());
		
		Containment con3 = con.copy();
		
		TestCase.assertTrue(con3.isAContainedInB());
		TestCase.assertTrue(con3.isBContainedInA());
	}
}
