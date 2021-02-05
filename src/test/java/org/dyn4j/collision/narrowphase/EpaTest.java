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

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test cases for the {@link Epa} class (NOTE: testing of the getPenetration method is
 * performed in the varied shape vs. shape tests).
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class EpaTest {
	/**
	 * Tests a successful creation of Epa and the getter/setter methods.
	 */
	@Test
	public void createGetSet() {
		Epa epa = new Epa();
		
		TestCase.assertEquals(Epa.DEFAULT_DISTANCE_EPSILON, epa.getDistanceEpsilon());
		TestCase.assertEquals(Epa.DEFAULT_MAX_ITERATIONS, epa.getMaxIterations());
		
		epa.setDistanceEpsilon(0.1);
		epa.setMaxIterations(20);
		
		TestCase.assertEquals(0.1, epa.getDistanceEpsilon());
		TestCase.assertEquals(20, epa.getMaxIterations());
	}
	
	/**
	 * Tests setting too small max iterations.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setSmallMaxIterations() {
		Epa epa = new Epa();
		epa.setMaxIterations(3);
	}
	
	/**
	 * Tests setting zero max iterations.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setZeroMaxIterations() {
		Epa epa = new Epa();
		epa.setMaxIterations(0);
	}
	
	/**
	 * Tests setting negative max iterations.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaxIterations() {
		Epa epa = new Epa();
		epa.setMaxIterations(-3);
	}
	
	/**
	 * Tests setting zero distance epsilon.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setZeroDistanceEpsilon() {
		Epa epa = new Epa();
		epa.setDistanceEpsilon(0.0);
	}
	
	/**
	 * Tests setting negative distance epsilon.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDistanceEpsilon() {
		Epa epa = new Epa();
		epa.setDistanceEpsilon(-0.11);
	}
}
