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
package org.dyn4j.dynamics;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the methods of the {@link Settings} class.
 * @author William Bittle
 * @version 4.1.0
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
	 * Tests the get/set of warm starting enabled flag.
	 */
	@Test
	public void getSetWarmStartingEnabled() {
		TestCase.assertTrue(settings.isWarmStartingEnabled());
		
		settings.setWarmStartingEnabled(false);
		TestCase.assertFalse(settings.isWarmStartingEnabled());
		
		settings.setWarmStartingEnabled(true);
		TestCase.assertTrue(settings.isWarmStartingEnabled());
	}
	
	/**
	 * Tests the set restitution velocity method.
	 */
	@Test
	@Deprecated
	public void setValidRestitutionVelocity() {
		settings.setRestitutionVelocity(3.0);
		TestCase.assertEquals(3.0, settings.getRestitutionVelocity());
		TestCase.assertEquals(9.0, settings.getRestitutionVelocitySquared());
	}
	
	/**
	 * Tests the set restitution velocity method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	@Deprecated
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
	public void getSetContinuousDetectionMode() {
		settings.setContinuousDetectionMode(ContinuousDetectionMode.ALL);
		TestCase.assertEquals(ContinuousDetectionMode.ALL, settings.getContinuousDetectionMode());
		settings.setContinuousDetectionMode(ContinuousDetectionMode.NONE);
		TestCase.assertEquals(ContinuousDetectionMode.NONE, settings.getContinuousDetectionMode());
	}

	/**
	 * Tests the set continuous collision detection mode w/ null.
	 * @since 4.0.0
	 */
	@Test(expected = NullPointerException.class)
	public void setNullContinuousDetectionMode() {
		settings.setContinuousDetectionMode(null);
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void tostring() {
		TestCase.assertNotNull(settings.toString());
	}
	
	/**
	 * Tests the getter/setter methods.
	 */
	@Test
	public void getSet() {
		settings.setAngularTolerance(Settings.DEFAULT_ANGULAR_TOLERANCE * 2);
		settings.setAtRestDetectionEnabled(false);
		settings.setBaumgarte(Settings.DEFAULT_BAUMGARTE * 2);
		settings.setContinuousDetectionMode(ContinuousDetectionMode.NONE);
		settings.setLinearTolerance(Settings.DEFAULT_LINEAR_TOLERANCE * 2);
		settings.setMaximumAngularCorrection(Settings.DEFAULT_MAXIMUM_ANGULAR_CORRECTION * 2);
		settings.setMaximumAtRestAngularVelocity(Settings.DEFAULT_MAXIMUM_AT_REST_ANGULAR_VELOCITY * 2);
		settings.setMaximumAtRestLinearVelocity(Settings.DEFAULT_MAXIMUM_AT_REST_LINEAR_VELOCITY * 2);
		settings.setMaximumLinearCorrection(Settings.DEFAULT_MAXIMUM_LINEAR_CORRECTION * 2);
		settings.setMaximumRotation(Settings.DEFAULT_MAXIMUM_ROTATION * 2);
		settings.setMaximumTranslation(Settings.DEFAULT_MAXIMUM_TRANSLATION * 2);
		settings.setMaximumWarmStartDistance(Settings.DEFAULT_MAXIMUM_WARM_START_DISTANCE * 2);
		settings.setMinimumAtRestTime(Settings.DEFAULT_MINIMUM_AT_REST_TIME * 2);
		settings.setPositionConstraintSolverIterations(Settings.DEFAULT_SOLVER_ITERATIONS * 2);
		settings.setStepFrequency(Settings.DEFAULT_STEP_FREQUENCY * 2);
		settings.setVelocityConstraintSolverIterations(Settings.DEFAULT_SOLVER_ITERATIONS * 2);
		settings.setWarmStartingEnabled(false);
		
		TestCase.assertEquals(Settings.DEFAULT_ANGULAR_TOLERANCE * 2, settings.getAngularTolerance());
		TestCase.assertEquals(false, settings.isAtRestDetectionEnabled());
		TestCase.assertEquals(Settings.DEFAULT_BAUMGARTE * 2, settings.getBaumgarte());
		TestCase.assertEquals(ContinuousDetectionMode.NONE, settings.getContinuousDetectionMode());
		TestCase.assertEquals(Settings.DEFAULT_LINEAR_TOLERANCE * 2, settings.getLinearTolerance());
		TestCase.assertEquals(Settings.DEFAULT_MAXIMUM_ANGULAR_CORRECTION * 2, settings.getMaximumAngularCorrection());
		TestCase.assertEquals(Settings.DEFAULT_MAXIMUM_AT_REST_ANGULAR_VELOCITY * 2, settings.getMaximumAtRestAngularVelocity());
		TestCase.assertEquals(Settings.DEFAULT_MAXIMUM_AT_REST_LINEAR_VELOCITY * 2, settings.getMaximumAtRestLinearVelocity());
		TestCase.assertEquals(Settings.DEFAULT_MAXIMUM_LINEAR_CORRECTION * 2, settings.getMaximumLinearCorrection());
		TestCase.assertEquals(Settings.DEFAULT_MAXIMUM_ROTATION * 2, settings.getMaximumRotation());
		TestCase.assertEquals(Settings.DEFAULT_MAXIMUM_TRANSLATION * 2, settings.getMaximumTranslation());
		TestCase.assertEquals(Settings.DEFAULT_MAXIMUM_WARM_START_DISTANCE * 2, settings.getMaximumWarmStartDistance());
		TestCase.assertEquals(Settings.DEFAULT_MINIMUM_AT_REST_TIME * 2, settings.getMinimumAtRestTime());
		TestCase.assertEquals(Settings.DEFAULT_SOLVER_ITERATIONS * 2, settings.getPositionConstraintSolverIterations());
		TestCase.assertEquals(Settings.DEFAULT_STEP_FREQUENCY * 2, settings.getStepFrequency());
		TestCase.assertEquals(Settings.DEFAULT_SOLVER_ITERATIONS * 2, settings.getVelocityConstraintSolverIterations());
		TestCase.assertEquals(false, settings.isWarmStartingEnabled());
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		settings.setAngularTolerance(Settings.DEFAULT_ANGULAR_TOLERANCE * 2);
		settings.setAtRestDetectionEnabled(false);
		settings.setBaumgarte(Settings.DEFAULT_BAUMGARTE * 2);
		settings.setContinuousDetectionMode(ContinuousDetectionMode.NONE);
		settings.setLinearTolerance(Settings.DEFAULT_LINEAR_TOLERANCE * 2);
		settings.setMaximumAngularCorrection(Settings.DEFAULT_MAXIMUM_ANGULAR_CORRECTION * 2);
		settings.setMaximumAtRestAngularVelocity(Settings.DEFAULT_MAXIMUM_AT_REST_ANGULAR_VELOCITY * 2);
		settings.setMaximumAtRestLinearVelocity(Settings.DEFAULT_MAXIMUM_AT_REST_LINEAR_VELOCITY * 2);
		settings.setMaximumLinearCorrection(Settings.DEFAULT_MAXIMUM_LINEAR_CORRECTION * 2);
		settings.setMaximumRotation(Settings.DEFAULT_MAXIMUM_ROTATION * 2);
		settings.setMaximumTranslation(Settings.DEFAULT_MAXIMUM_TRANSLATION * 2);
		settings.setMaximumWarmStartDistance(Settings.DEFAULT_MAXIMUM_WARM_START_DISTANCE * 2);
		settings.setMinimumAtRestTime(Settings.DEFAULT_MINIMUM_AT_REST_TIME * 2);
		settings.setPositionConstraintSolverIterations(Settings.DEFAULT_SOLVER_ITERATIONS * 2);
		settings.setStepFrequency(Settings.DEFAULT_STEP_FREQUENCY * 2);
		settings.setVelocityConstraintSolverIterations(Settings.DEFAULT_SOLVER_ITERATIONS * 2);
		settings.setWarmStartingEnabled(false);
		
		Settings copy = settings.copy();
		
		TestCase.assertEquals(settings.getAngularTolerance(), copy.getAngularTolerance());
		TestCase.assertEquals(settings.isAtRestDetectionEnabled(), copy.isAtRestDetectionEnabled());
		TestCase.assertEquals(settings.getBaumgarte(), copy.getBaumgarte());
		TestCase.assertEquals(settings.getContinuousDetectionMode(), copy.getContinuousDetectionMode());
		TestCase.assertEquals(settings.getLinearTolerance(), copy.getLinearTolerance());
		TestCase.assertEquals(settings.getMaximumAngularCorrection(), copy.getMaximumAngularCorrection());
		TestCase.assertEquals(settings.getMaximumAtRestAngularVelocity(), copy.getMaximumAtRestAngularVelocity());
		TestCase.assertEquals(settings.getMaximumAtRestLinearVelocity(), copy.getMaximumAtRestLinearVelocity());
		TestCase.assertEquals(settings.getMaximumLinearCorrection(), copy.getMaximumLinearCorrection());
		TestCase.assertEquals(settings.getMaximumRotation(), copy.getMaximumRotation());
		TestCase.assertEquals(settings.getMaximumTranslation(), copy.getMaximumTranslation());
		TestCase.assertEquals(settings.getMaximumWarmStartDistance(), copy.getMaximumWarmStartDistance());
		TestCase.assertEquals(settings.getMinimumAtRestTime(), copy.getMinimumAtRestTime());
		TestCase.assertEquals(settings.getPositionConstraintSolverIterations(), copy.getPositionConstraintSolverIterations());
		TestCase.assertEquals(settings.getStepFrequency(), copy.getStepFrequency());
		TestCase.assertEquals(settings.getVelocityConstraintSolverIterations(), copy.getVelocityConstraintSolverIterations());
		TestCase.assertEquals(settings.isWarmStartingEnabled(), copy.isWarmStartingEnabled());
	}
}
