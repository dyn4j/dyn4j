/*
 * Copyright (c) 2010, William Bittle
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
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.game2d.dynamics;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Tests the methods of the {@link Settings} class.
 * @author William Bittle
 */
public class SettingsTest {
	/**
	 * Tests that the {@link Settings} class is
	 * performing as expected as a singleton.
	 */
	@Test
	public void getInstance() {
		Settings s1 = Settings.getInstance();
		Settings s2 = Settings.getInstance();
		// should be the same instance
		TestCase.assertEquals(s1, s2);
	}
	
	/**
	 * Tests the set step frequency method.
	 */
	@Test
	public void setFrequency() {
		Settings s = Settings.getInstance();
		s.reset();
		// invalid values
		s.setStepFrequency(10);
		TestCase.assertEquals(1.0 / 30.0, s.getStepFrequency());
		s.setStepFrequency(0);
		TestCase.assertEquals(1.0 / 30.0, s.getStepFrequency());
		s.setStepFrequency(-1.0);
		TestCase.assertEquals(1.0 / 30.0, s.getStepFrequency());
		
		// valid value
		s.setStepFrequency(40);
		TestCase.assertEquals(1.0 / 40.0, s.getStepFrequency());
	}
	
	/**
	 * Tests the set max velocity method.
	 */
	@Test
	public void setMaxVelocity() {
		Settings s = Settings.getInstance();
		s.reset();
		// invalid values
		s.setMaxVelocity(-1.0);
		TestCase.assertEquals(0.0, s.getMaxVelocity());
		// valid values
		s.setMaxVelocity(12.0);
		TestCase.assertEquals(12.0, s.getMaxVelocity());
	}
	
	/**
	 * Tests the set max angular velocity method.
	 */
	@Test
	public void setMaxAngularVelocity() {
		Settings s = Settings.getInstance();
		s.reset();
		// invalid values
		s.setMaxAngularVelocity(-1.0);
		TestCase.assertEquals(0.0, s.getMaxAngularVelocity());
		// valid values
		s.setMaxAngularVelocity(12.0);
		TestCase.assertEquals(12.0, s.getMaxAngularVelocity());
	}
	
	/**
	 * Tests the set sleep method.
	 */
	@Test
	public void setSleep() {
		Settings s = Settings.getInstance();
		s.reset();
		s.setSleep(false);
		TestCase.assertFalse(s.canSleep());
		s.setSleep(true);
		TestCase.assertTrue(s.canSleep());
	}
	
	/**
	 * Tests the set sleep velocity method.
	 */
	@Test
	public void setSleepVelocity() {
		Settings s = Settings.getInstance();
		s.reset();
		// invalid values
		s.setSleepVelocity(-1.0);
		TestCase.assertEquals(0.0, s.getSleepVelocity());
		// valid values
		s.setSleepVelocity(12.0);
		TestCase.assertEquals(12.0, s.getSleepVelocity());
	}
	
	/**
	 * Tests the set sleep angular velocity method.
	 */
	@Test
	public void setSleepAngularVelocity() {
		Settings s = Settings.getInstance();
		s.reset();
		// invalid values
		s.setSleepAngularVelocity(-1.0);
		TestCase.assertEquals(0.0, s.getSleepAngularVelocity());
		// valid values
		s.setSleepAngularVelocity(12.0);
		TestCase.assertEquals(12.0, s.getSleepAngularVelocity());
	}
	
	/**
	 * Tests the set sleep time method.
	 */
	@Test
	public void setSleepTime() {
		Settings s = Settings.getInstance();
		s.reset();
		// invalid values
		s.setSleepTime(-1.0);
		TestCase.assertEquals(0.0, s.getSleepTime());
		// valid values
		s.setSleepTime(12.0);
		TestCase.assertEquals(12.0, s.getSleepTime());
	}
	
	/**
	 * Tests the set solver iterations method.
	 */
	@Test
	public void setSolverIterations() {
		Settings s = Settings.getInstance();
		s.reset();
		// invalid values
		s.setSiSolverIterations(-1);
		TestCase.assertEquals(5, s.getSiSolverIterations());
		s.setSiSolverIterations(0);
		TestCase.assertEquals(5, s.getSiSolverIterations());
		s.setSiSolverIterations(4);
		TestCase.assertEquals(5, s.getSiSolverIterations());
		// valid values
		s.setSiSolverIterations(10);
		TestCase.assertEquals(10, s.getSiSolverIterations());
	}
	
	/**
	 * Tests the set warm start distance method.
	 */
	@Test
	public void setWarmStartDistance() {
		Settings s = Settings.getInstance();
		s.reset();
		// invalid values
		s.setWarmStartDistance(-1.0);
		TestCase.assertEquals(0.0, s.getWarmStartDistance());
		// valid values
		s.setWarmStartDistance(10);
		TestCase.assertEquals(10.0, s.getWarmStartDistance());
	}
	
	/**
	 * Tests the set restitution velocity method.
	 */
	@Test
	public void setRestitutionVelocity() {
		Settings s = Settings.getInstance();
		s.reset();
		// invalid values
		s.setRestitutionVelocity(-1.0);
		TestCase.assertEquals(0.0, s.getRestitutionVelocity());
		// valid values
		s.setRestitutionVelocity(10);
		TestCase.assertEquals(10.0, s.getRestitutionVelocity());
	}
	
	/**
	 * Tests the set allowed penetration method.
	 */
	@Test
	public void setAllowedPenetration() {
		Settings s = Settings.getInstance();
		s.reset();
		// invalid values
		s.setLinearTolerance(-1.0);
		TestCase.assertEquals(0.0, s.getLinearTolerance());
		// valid values
		s.setLinearTolerance(10);
		TestCase.assertEquals(10.0, s.getLinearTolerance());
	}
	
	/**
	 * Tests the set max linear correction method.
	 */
	@Test
	public void setMaxLinearCorrection() {
		Settings s = Settings.getInstance();
		s.reset();
		// invalid values
		s.setMaxLinearCorrection(-1.0);
		TestCase.assertEquals(0.0, s.getMaxLinearCorrection());
		// valid values
		s.setMaxLinearCorrection(10);
		TestCase.assertEquals(10.0, s.getMaxLinearCorrection());
	}
	
	/**
	 * Tests the set baumgarte method.
	 */
	@Test
	public void setBaumgarte() {
		Settings s = Settings.getInstance();
		s.reset();
		// invalid values
		s.setBaumgarte(-1.0);
		TestCase.assertEquals(0.0, s.getBaumgarte());
		// valid values
		s.setBaumgarte(10);
		TestCase.assertEquals(10.0, s.getBaumgarte());
	}
}
