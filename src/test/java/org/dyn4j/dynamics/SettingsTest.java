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
package org.dyn4j.dynamics;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the methods of the {@link Settings} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 */
public class SettingsTest {
	/** The test settings */
	private Settings settings = new Settings();
	
	/**
	 * Setup the test case.
	 */
	@Before
	public void setup() {
		settings.reset();
	}
	
	/**
	 * Tests the set step frequency method.
	 */
	@Test
	public void setValidFrequency() {
		settings.setStepFrequency(1.0 / 70.0);
		TestCase.assertEquals(1.0 / 70.0, settings.getStepFrequency());
	}
	
	/**
	 * Tests the set step frequency method passing a negative frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeFrequency() {
		settings.setStepFrequency(-1.0 / 30.0);
	}
	
	/**
	 * Tests the set step frequency method passing a zero frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setZeroFrequency() {
		settings.setStepFrequency(0.0);
	}
	
	/**
	 * Tests the set maximum translation method.
	 */
	@Test
	public void setValidMaximumTranslation() {
		settings.setMaximumTranslation(3.0);
		TestCase.assertEquals(3.0, settings.getMaximumTranslation());
		TestCase.assertEquals(9.0, settings.getMaximumTranslationSquared());
	}
	
	/**
	 * Tests the set maximum translation method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaximumTranslation() {
		settings.setMaximumTranslation(-3.0);
	}
	
	/**
	 * Tests the set maximum angular velocity method.
	 */
	@Test
	public void setMaximumRotation() {
		settings.setMaximumRotation(3.0);
		TestCase.assertEquals(3.0, settings.getMaximumRotation());
		TestCase.assertEquals(9.0, settings.getMaximumRotationSquared());
	}

	/**
	 * Tests the set maximum translation method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaximumRotation() {
		settings.setMaximumRotation(Math.toRadians(-3.0));
	}
	
	/**
	 * Tests the set sleep method.
	 */
	@Test
	public void setSleep() {
		settings.reset();
		settings.setAtRestDetectionEnabled(false);
		TestCase.assertFalse(settings.isAtRestDetectionEnabled());
		settings.setAtRestDetectionEnabled(true);
		TestCase.assertTrue(settings.isAtRestDetectionEnabled());
	}
	
	/**
	 * Tests the set sleep linear velocity method.
	 */
	@Test
	public void setValidSleepLinearVelocity() {
		settings.setMaximumAtRestLinearVelocity(3.0);
		TestCase.assertEquals(3.0, settings.getMaximumAtRestLinearVelocity());
		TestCase.assertEquals(9.0, settings.getMaximumAtRestLinearVelocitySquared());
	}
	
	/**
	 * Tests the set sleep linear velocity method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeSleepLinearVelocity() {
		settings.setMaximumAtRestLinearVelocity(-1.0);
	}
	
	/**
	 * Tests the set sleep angular velocity method.
	 */
	@Test
	public void setValidSleepAngularVelocity() {
		settings.setMaximumAtRestAngularVelocity(2.0);
		TestCase.assertEquals(2.0, settings.getMaximumAtRestAngularVelocity());
		TestCase.assertEquals(4.0, settings.getMaximumAtRestAngularVelocitySquared());
	}
	
	/**
	 * Tests the set sleep angular velocity method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeSleepAngularVelocity() {
		settings.setMaximumAtRestAngularVelocity(-1.0);
	}
	
	/**
	 * Tests the set sleep time method.
	 */
	@Test
	public void setValidSleepTime() {
		settings.setMinimumAtRestTime(12.0);
		TestCase.assertEquals(12.0, settings.getMinimumAtRestTime());
	}
	
	/**
	 * Tests the set sleep time method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeSleepTime() {
		settings.setMinimumAtRestTime(-1.0);
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
	 * Tests the set warm start distance method.
	 */
	@Test
	public void setValidWarmStartDistance() {
		settings.setMaximumWarmStartDistance(2.0);
		TestCase.assertEquals(2.0, settings.getMaximumWarmStartDistance());
		TestCase.assertEquals(4.0, settings.getMaximumWarmStartDistanceSquared());
	}
	
	/**
	 * Tests the set warm start distance method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeWarmStartDistance() {
		settings.setMaximumWarmStartDistance(-2.0);
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
	 * Tests the set maximum linear correction method.
	 */
	@Test
	public void setValidMaximumLinearCorrection() {
		settings.setMaximumLinearCorrection(2.0);
		TestCase.assertEquals(2.0, settings.getMaximumLinearCorrection());
		TestCase.assertEquals(4.0, settings.getMaximumLinearCorrectionSquared());
	}

	/**
	 * Tests the set maximum linear correction method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaximumLinearCorrection() {
		settings.setMaximumLinearCorrection(-3.0);
	}

	/**
	 * Tests the set maximum angular correction method.
	 */
	@Test
	public void setValidMaximumAngularCorrection() {
		settings.setMaximumAngularCorrection(2.0);
		TestCase.assertEquals(2.0, settings.getMaximumAngularCorrection());
		TestCase.assertEquals(4.0, settings.getMaximumAngularCorrectionSquared());
	}

	/**
	 * Tests the set maximum angular correction method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaximumAngularCorrection() {
		settings.setMaximumAngularCorrection(-3.0);
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
	
	/**
	 * Tests the set continuous collision detection mode.
	 * @since 2.2.3
	 */
	@Test
	public void setContinuousDetectionMode() {
		settings.setContinuousDetectionMode(ContinuousDetectionMode.ALL);
		TestCase.assertEquals(ContinuousDetectionMode.ALL, settings.getContinuousDetectionMode());
		settings.setContinuousDetectionMode(ContinuousDetectionMode.NONE);
		TestCase.assertEquals(ContinuousDetectionMode.NONE, settings.getContinuousDetectionMode());
	}
}
