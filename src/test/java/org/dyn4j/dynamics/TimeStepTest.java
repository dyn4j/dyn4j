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

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Class used to test the {@link TimeStep} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.2
 */
public class TimeStepTest {
	/**
	 * Tests successful creation.
	 */
	@Test
	public void createSuccess() {
		TimeStep ts = new TimeStep(0.3);
		
		TestCase.assertEquals(0.3, ts.dt);
		TestCase.assertEquals(0.3, ts.dt0);
		TestCase.assertEquals(1.0, ts.dtRatio);
		TestCase.assertEquals(1.0 / 0.3, ts.invdt);
		TestCase.assertEquals(1.0 / 0.3, ts.invdt0);
		
		TestCase.assertEquals(0.3, ts.getDeltaTime());
		TestCase.assertEquals(0.3, ts.getPrevousDeltaTime());
		TestCase.assertEquals(1.0, ts.getDeltaTimeRatio());
		TestCase.assertEquals(1.0 / 0.3, ts.getInverseDeltaTime());
		TestCase.assertEquals(1.0 / 0.3, ts.getPreviousInverseDeltaTime());
	}
	
	/**
	 * Tests creation w/ a zero delta time.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroDeltaTime() {
		new TimeStep(0.0);
	}
	
	/**
	 * Tests creation w/ a negative delta time.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeDeltaTime() {
		new TimeStep(-1.0);
	}
	
	/**
	 * Tests the set methods successfully.
	 */
	@Test
	public void update() {
		TimeStep ts = new TimeStep(0.3);
		
		TestCase.assertEquals(0.3, ts.getDeltaTime());
		TestCase.assertEquals(0.3, ts.getPrevousDeltaTime());
		TestCase.assertEquals(1.0, ts.getDeltaTimeRatio());
		TestCase.assertEquals(1.0 / 0.3, ts.getInverseDeltaTime());
		TestCase.assertEquals(1.0 / 0.3, ts.getPreviousInverseDeltaTime());
		
		ts.update(0.4);
		
		TestCase.assertEquals(0.4, ts.getDeltaTime());
		TestCase.assertEquals(0.3, ts.getPrevousDeltaTime());
		TestCase.assertEquals(0.4 / 0.3, ts.getDeltaTimeRatio());
		TestCase.assertEquals(1.0 / 0.4, ts.getInverseDeltaTime());
		TestCase.assertEquals(1.0 / 0.3, ts.getPreviousInverseDeltaTime());
		
		ts.update(0.2);
		
		TestCase.assertEquals(0.2, ts.getDeltaTime());
		TestCase.assertEquals(0.4, ts.getPrevousDeltaTime());
		TestCase.assertEquals(0.2 / 0.4, ts.getDeltaTimeRatio());
		TestCase.assertEquals(1.0 / 0.2, ts.getInverseDeltaTime());
		TestCase.assertEquals(1.0 / 0.4, ts.getPreviousInverseDeltaTime());
	}
	
	/**
	 * Tests update w/ a zero delta time.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void updateZeroDeltaTime() {
		TimeStep ts = new TimeStep(0.3);
		ts.update(0.0);
	}
	
	/**
	 * Tests update w/ a negative delta time.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void updateNegativeDeltaTime() {
		TimeStep ts = new TimeStep(0.3);
		ts.update(-1.0);
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void tostring() {
		TimeStep ts = new TimeStep(0.3);
		
		TestCase.assertNotNull(ts.toString());
		
		ts.update(0.2);
		TestCase.assertNotNull(ts.toString());
	}
}
