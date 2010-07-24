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

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the methods of the {@link Settings} class.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public class SettingsTest {
	/** The test settings */
	private Settings settings = Settings.getInstance();
	
	/**
	 * Setup the test case.
	 */
	@Before
	public void setup() {
		settings.reset();
	}
	
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
	public void setValidFrequency() {
		settings.setStepFrequency(70.0);
		TestCase.assertEquals(1.0 / 70.0, settings.getStepFrequency());
	}
	
	/**
	 * Tests the set step frequency method passing a negative frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeFrequency() {
		settings.setStepFrequency(-30.0);
	}
	
	/**
	 * Tests the set step frequency method passing a zero frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setZeroFrequency() {
		settings.setStepFrequency(0.0);
	}
	
	/**
	 * Tests the set step frequency method passing a frequency value &lt; 30.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLessThan30Frequency() {
		settings.setStepFrequency(22.0);
	}
	
	/**
	 * Tests the set max translation method.
	 */
	@Test
	public void setValidMaxTranslation() {
		settings.setMaxTranslation(3.0);
		TestCase.assertEquals(3.0, settings.getMaxTranslation());
		TestCase.assertEquals(9.0, settings.getMaxTranslationSquared());
	}
	
	/**
	 * Tests the set max translation method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaxTranslation() {
		settings.setMaxTranslation(-3.0);
	}
	
	/**
	 * Tests the set max angular velocity method.
	 */
	@Test
	public void setMaxRotation() {
		settings.setMaxRotation(3.0);
		TestCase.assertEquals(3.0, settings.getMaxRotation());
		TestCase.assertEquals(9.0, settings.getMaxRotationSquared());
	}

	/**
	 * Tests the set max translation method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaxRotation() {
		settings.setMaxRotation(Math.toRadians(-3.0));
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
	public void setValidSleepVelocity() {
		settings.setSleepVelocity(3.0);
		TestCase.assertEquals(3.0, settings.getSleepVelocity());
		TestCase.assertEquals(9.0, settings.getSleepVelocitySquared());
	}
	
	/**
	 * Tests the set sleep velocity method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeSleepVelocity() {
		settings.setSleepVelocity(-1.0);
	}
	
	/**
	 * Tests the set sleep angular velocity method.
	 */
	@Test
	public void setValidSleepAngularVelocity() {
		settings.setSleepAngularVelocity(2.0);
		TestCase.assertEquals(2.0, settings.getSleepAngularVelocity());
		TestCase.assertEquals(4.0, settings.getSleepAngularVelocitySquared());
	}
	
	/**
	 * Tests the set sleep angular velocity method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeSleepAngularVelocity() {
		settings.setSleepAngularVelocity(-1.0);
	}
	
	/**
	 * Tests the set sleep time method.
	 */
	@Test
	public void setValidSleepTime() {
		settings.setSleepTime(12.0);
		TestCase.assertEquals(12.0, settings.getSleepTime());
	}
	
	/**
	 * Tests the set sleep time method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeSleepTime() {
		settings.setSleepTime(-1.0);
	}
	
	/**
	 * Tests the set velocity constraint solver iterations method.
	 */
	@Test
	public void setValidVelocityConstraintSolverIterations() {
		settings.setVelocityConstraintSolverIterations(17);
		TestCase.assertEquals(17, settings.getVelocityConstraintSolverIterations());
	}
	
	/**
	 * Tests the set velocity constraint solver iterations method passing
	 * a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeVelocityConstraintSolverIterations() {
		settings.setVelocityConstraintSolverIterations(-3);
	}
	
	/**
	 * Tests the set velocity constraint solver iterations method passing
	 * a zero value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setZeroVelocityConstraintSolverIterations() {
		settings.setVelocityConstraintSolverIterations(0);
	}
	
	/**
	 * Tests the set velocity constraint solver iterations method passing
	 * a value less than 5.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLessThan5VelocityConstraintSolverIterations() {
		settings.setVelocityConstraintSolverIterations(2);
	}
	
	/**
	 * Tests the set position constraint solver iterations method.
	 */
	@Test
	public void setValidPositionConstraintSolverIterations() {
		settings.setPositionConstraintSolverIterations(17);
		TestCase.assertEquals(17, settings.getPositionConstraintSolverIterations());
	}
	
	/**
	 * Tests the set position constraint solver iterations method passing
	 * a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativePositionConstraintSolverIterations() {
		settings.setPositionConstraintSolverIterations(-3);
	}
	
	/**
	 * Tests the set position constraint solver iterations method passing
	 * a zero value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setZeroPositionConstraintSolverIterations() {
		settings.setPositionConstraintSolverIterations(0);
	}
	
	/**
	 * Tests the set position constraint solver iterations method passing
	 * a value less than 5.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLessThan5PositionConstraintSolverIterations() {
		settings.setPositionConstraintSolverIterations(2);
	}
	
	/**
	 * Tests the set warm start distance method.
	 */
	@Test
	public void setValidWarmStartDistance() {
		settings.setWarmStartDistance(2.0);
		TestCase.assertEquals(2.0, settings.getWarmStartDistance());
		TestCase.assertEquals(4.0, settings.getWarmStartDistanceSquared());
	}
	
	/**
	 * Tests the set warm start distance method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeWarmStartDistance() {
		settings.setWarmStartDistance(-2.0);
	}
	
	/**
	 * Tests the set restitution velocity method.
	 */
	@Test
	public void setValidRestitutionVelocity() {
		settings.setRestitutionVelocity(3.0);
		TestCase.assertEquals(3.0, settings.getRestitutionVelocity());
		TestCase.assertEquals(9.0, settings.getRestitutionVelocitySquared());
	}
	
	/**
	 * Tests the set restitution velocity method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeRestitutionVelocity() {
		settings.setRestitutionVelocity(-2.0);
	}
	
	/**
	 * Tests the set linear tolerance method.
	 */
	@Test
	public void setValidLinearTolerance() {
		settings.setLinearTolerance(2.0);
		TestCase.assertEquals(2.0, settings.getLinearTolerance());
		TestCase.assertEquals(4.0, settings.getLinearToleranceSquared());
	}

	/**
	 * Tests the set linear tolerance method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeLinearTolerance() {
		settings.setLinearTolerance(-0.3);
	}

	/**
	 * Tests the set angular tolerance method.
	 */
	@Test
	public void setValidAngularTolerance() {
		settings.setAngularTolerance(2.0);
		TestCase.assertEquals(2.0, settings.getAngularTolerance());
		TestCase.assertEquals(4.0, settings.getAngularToleranceSquared());
	}

	/**
	 * Tests the set angular tolerance method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeAngularTolerance() {
		settings.setAngularTolerance(-2.0);
	}
	
	/**
	 * Tests the set max linear correction method.
	 */
	@Test
	public void setValidMaxLinearCorrection() {
		settings.setMaxLinearCorrection(2.0);
		TestCase.assertEquals(2.0, settings.getMaxLinearCorrection());
		TestCase.assertEquals(4.0, settings.getMaxLinearCorrectionSquared());
	}

	/**
	 * Tests the set max linear correction method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaxLinearCorrection() {
		settings.setMaxLinearCorrection(-3.0);
	}

	/**
	 * Tests the set max angular correction method.
	 */
	@Test
	public void setValidMaxAngularCorrection() {
		settings.setMaxAngularCorrection(2.0);
		TestCase.assertEquals(2.0, settings.getMaxAngularCorrection());
		TestCase.assertEquals(4.0, settings.getMaxAngularCorrectionSquared());
	}

	/**
	 * Tests the set max angular correction method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaxAngularCorrection() {
		settings.setMaxAngularCorrection(-3.0);
	}
	
	/**
	 * Tests the set baumgarte method.
	 */
	@Test
	public void setValidBaumgarte() {
		settings.setBaumgarte(0.3);
		TestCase.assertEquals(0.3, settings.getBaumgarte());
	}

	/**
	 * Tests the set baumgarte method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeBaumgarte() {
		settings.setBaumgarte(-0.3);
	}
}
