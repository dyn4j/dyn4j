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
package org.dyn4j.collision.continuous;

import org.dyn4j.collision.narrowphase.Separation;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test class for the {@link TimeOfImpact} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class TimeOfImpactTest {
	/**
	 * Tests the constructor.
	 */
	@Test
	public void create() {
		Separation sep = new Separation();
		TimeOfImpact toi = new TimeOfImpact(0.0, sep);
		
		TestCase.assertEquals(0.0, toi.getTime());
		TestCase.assertNotNull(toi.getSeparation());
		TestCase.assertNotSame(sep, toi.getSeparation());
	}
	
	/**
	 * Tests the getter/setters.
	 */
	@Test
	public void getSet() {
		Separation sep = new Separation();
		TimeOfImpact toi = new TimeOfImpact();
		
		toi.setTime(1.0);
		toi.setSeparation(sep);
		
		TestCase.assertEquals(1.0, toi.getTime());
		TestCase.assertNotNull(toi.getSeparation());
		TestCase.assertNotSame(sep, toi.getSeparation());
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		Separation sep = new Separation();
		TimeOfImpact toi = new TimeOfImpact();
		
		toi.setTime(1.0);
		toi.setSeparation(sep);
		
		TimeOfImpact copy = toi.copy();
		
		TestCase.assertEquals(1.0, copy.getTime());
		TestCase.assertNotSame(sep, copy.getSeparation());
	}
	
	/**
	 * Tests the toString method
	 */
	@Test
	public void tostring() {
		Separation sep = new Separation();
		TimeOfImpact toi = new TimeOfImpact();
		
		toi.setTime(1.0);
		toi.setSeparation(sep);
		
		TestCase.assertNotNull(toi.toString());
	}
}
