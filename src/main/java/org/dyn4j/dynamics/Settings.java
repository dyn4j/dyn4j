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

import org.dyn4j.resources.Messages;

/**
 * Responsible for housing all of the dynamics engine's settings.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 */
public class Settings {
	/** The default step frequency of the dynamics engine; in seconds */
	public static final double DEFAULT_STEP_FREQUENCY = 1.0 / 60.0;
	
	/** The default maximum translation a {@link PhysicsBody} can have in one time step; in meters */
	public static final double DEFAULT_MAXIMUM_TRANSLATION = 2.0;
	
	/** The default maximum rotation a {@link PhysicsBody} can have in one time step; in radians */
	public static final double DEFAULT_MAXIMUM_ROTATION = 0.5 * Math.PI;
	
	/** 
	 * The default maximum velocity for a {@link PhysicsBody} to be flagged as at-rest; in meters/second
	 * @since 4.0.0
	 */
	public static final double DEFAULT_MAXIMUM_AT_REST_LINEAR_VELOCITY = 0.01;
	
	/** 
	 * The default maximum angular velocity for a {@link PhysicsBody} to be flagged as at-rest; in radians/second
	 * @since 4.0.0
	 */
	public static final double DEFAULT_MAXIMUM_AT_REST_ANGULAR_VELOCITY = Math.toRadians(2.0);
	
	/** 
	 * The default required time a {@link PhysicsBody} must maintain small motion to be flagged as at-rest; in seconds
	 * @since 4.0.0
	 */
	public static final double DEFAULT_MINIMUM_AT_REST_TIME = 0.5;

	/** 
	 * The default maximum velocity for a {@link PhysicsBody} to go to sleep; in meters/second
	 * @deprecated Deprecated in 4.0.0. Use {@link #DEFAULT_MAXIMUM_AT_REST_LINEAR_VELOCITY} instead.
	 */
	@Deprecated
	public static final double DEFAULT_SLEEP_LINEAR_VELOCITY = DEFAULT_MAXIMUM_AT_REST_LINEAR_VELOCITY;
	
	/** 
	 * The default maximum angular velocity for a {@link PhysicsBody} to go to sleep; in radians/second 
	 * @deprecated Deprecated in 4.0.0. Use {@link #DEFAULT_MAXIMUM_AT_REST_ANGULAR_VELOCITY} instead.
	 */
	@Deprecated
	public static final double DEFAULT_SLEEP_ANGULAR_VELOCITY = DEFAULT_MAXIMUM_AT_REST_ANGULAR_VELOCITY;
	
	/** 
	 * The default required time a {@link PhysicsBody} must maintain small motion so that its put to sleep; in seconds
	 * @deprecated Deprecated in 4.0.0. Use {@link #DEFAULT_MINIMUM_AT_REST_TIME} instead.
	 */
	@Deprecated
	public static final double DEFAULT_SLEEP_TIME = 0.5;
	
	/** The default number of solver iterations */
	public static final int DEFAULT_SOLVER_ITERATIONS = 10;

	/** 
	 * The default warm starting distance; in meters<sup>2</sup> 
	 * @deprecated Deprecated in 4.0.0. Use {@link #DEFAULT_MAXIMUM_WARM_START_DISTANCE} instead.
	 */
	@Deprecated
	public static final double DEFAULT_WARM_START_DISTANCE = 1.0e-2;

	/** 
	 * The default warm starting distance; in meters<sup>2</sup> 
	 * @since 4.0.0
	 */
	public static final double DEFAULT_MAXIMUM_WARM_START_DISTANCE = 1.0e-2;
	
	/** The default restitution velocity; in meters/second */
	public static final double DEFAULT_RESTITUTION_VELOCITY = 1.0;
	
	/** The default linear tolerance; in meters */
	public static final double DEFAULT_LINEAR_TOLERANCE = 0.005;

	/** The default angular tolerance; in radians */
	public static final double DEFAULT_ANGULAR_TOLERANCE = Math.toRadians(2.0);
	
	/** The default maximum linear correction; in meters */
	public static final double DEFAULT_MAXIMUM_LINEAR_CORRECTION = 0.2;
	
	/** The default maximum angular correction; in radians */
	public static final double DEFAULT_MAXIMUM_ANGULAR_CORRECTION = Math.toRadians(8.0);
	
	/** The default baumgarte */
	public static final double DEFAULT_BAUMGARTE = 0.2;
	
	/** The step frequency of the dynamics engine */
	private double stepFrequency = Settings.DEFAULT_STEP_FREQUENCY;
	
	/** The maximum translation a {@link PhysicsBody} can have in one time step */
	private double maximumTranslation = Settings.DEFAULT_MAXIMUM_TRANSLATION;
	
	/** The squared value of {@link #maximumTranslation} */
	private double maximumTranslationSquared = Settings.DEFAULT_MAXIMUM_TRANSLATION * Settings.DEFAULT_MAXIMUM_TRANSLATION;
	
	/** The maximum rotation a {@link PhysicsBody} can have in one time step */
	private double maximumRotation = Settings.DEFAULT_MAXIMUM_ROTATION;

	/** The squared value of {@link #maximumRotation} */
	private double maximumRotationSquared = Settings.DEFAULT_MAXIMUM_ROTATION * Settings.DEFAULT_MAXIMUM_ROTATION;
	
	/** Whether on an engine level {@link PhysicsBody}s are automatically flagged at-rest */
	private boolean atRestDetectionEnabled = true;
	
	/** The maximum linear velocity before a {@link PhysicsBody} is considered to be at-rest */
	private double maximumAtRestLinearVelocity = Settings.DEFAULT_MAXIMUM_AT_REST_LINEAR_VELOCITY;
	
	/** The squared value of {@link #maximumAtRestLinearVelocity} */
	private double maximumAtRestLinearVelocitySquared = Settings.DEFAULT_MAXIMUM_AT_REST_LINEAR_VELOCITY * Settings.DEFAULT_MAXIMUM_AT_REST_LINEAR_VELOCITY;
	
	/** The maximum angular velocity before a {@link PhysicsBody} is considered to be at-rest */
	private double maximumAtRestAngularVelocity = Settings.DEFAULT_MAXIMUM_AT_REST_ANGULAR_VELOCITY;
	
	/** The squared value of {@link #maximumAtRestAngularVelocity} */
	private double maximumAtRestAngularVelocitySquared = Settings.DEFAULT_MAXIMUM_AT_REST_ANGULAR_VELOCITY * Settings.DEFAULT_MAXIMUM_AT_REST_ANGULAR_VELOCITY;
	
	/** The time required for a {@link PhysicsBody} to stay motionless before being flagged as at-rest */
	private double minimumAtRestTime = Settings.DEFAULT_MINIMUM_AT_REST_TIME;
	
	/** The number of iterations used to solve velocity constraints */
	private int velocityConstraintSolverIterations = Settings.DEFAULT_SOLVER_ITERATIONS;
	
	/** The maximum number of iterations used to solve position constraints */
	private int positionConstraintSolverIterations = Settings.DEFAULT_SOLVER_ITERATIONS;
	
	/** True if contact warm starting is enabled */
	private boolean warmStartingEnabled = true;
	
	/** The warm start distance */
	private double maximumWarmStartDistance = Settings.DEFAULT_MAXIMUM_WARM_START_DISTANCE;
	
	/** The squared value of {@link #maximumWarmStartDistance} */
	private double maximumWarmStartDistanceSquared = Settings.DEFAULT_MAXIMUM_WARM_START_DISTANCE * Settings.DEFAULT_MAXIMUM_WARM_START_DISTANCE;
	
	/** The restitution velocity */
	private double restitutionVelocity = Settings.DEFAULT_RESTITUTION_VELOCITY;
	
	/** The squared value of {@link #restitutionVelocity} */
	private double restitutionVelocitySquared = Settings.DEFAULT_RESTITUTION_VELOCITY * Settings.DEFAULT_RESTITUTION_VELOCITY;
	
	/** The allowed linear tolerance */
	private double linearTolerance = Settings.DEFAULT_LINEAR_TOLERANCE;
	
	/** The squared value of {@link #linearTolerance} */
	private double linearToleranceSquared = Settings.DEFAULT_LINEAR_TOLERANCE * Settings.DEFAULT_LINEAR_TOLERANCE;
	
	/** The allowed angular tolerance */
	private double angularTolerance = Settings.DEFAULT_ANGULAR_TOLERANCE;
	
	/** The squared value of {@link #angularTolerance} */
	private double angularToleranceSquared = Settings.DEFAULT_ANGULAR_TOLERANCE * Settings.DEFAULT_ANGULAR_TOLERANCE;
	
	/** The maximum linear correction */
	private double maximumLinearCorrection = Settings.DEFAULT_MAXIMUM_LINEAR_CORRECTION;
	
	/** The squared value of {@link #maximumLinearCorrection} */
	private double maximumLinearCorrectionSquared = Settings.DEFAULT_MAXIMUM_LINEAR_CORRECTION * Settings.DEFAULT_MAXIMUM_LINEAR_CORRECTION;
	
	/** The maximum angular correction */
	private double maximumAngularCorrection = Settings.DEFAULT_MAXIMUM_ANGULAR_CORRECTION;
	
	/** The squared value of {@link #maximumAngularCorrection} */
	private double maximumAngularCorrectionSquared = Settings.DEFAULT_MAXIMUM_ANGULAR_CORRECTION * Settings.DEFAULT_MAXIMUM_ANGULAR_CORRECTION;
	
	/** The baumgarte factor */
	private double baumgarte = Settings.DEFAULT_BAUMGARTE;
	
	/** The continuous collision detection flag */
	private ContinuousDetectionMode continuousDetectionMode = ContinuousDetectionMode.ALL;
	
	/** Default constructor */
	public Settings() {}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Settings[StepFrequency=").append(this.stepFrequency)
		.append("|MaximumTranslation=").append(this.maximumTranslation)
		.append("|MaximumRotation=").append(this.maximumRotation)
		.append("|AutoSleepingEnabled=").append(this.atRestDetectionEnabled)
		.append("|AtRestLinearVelocity=").append(this.maximumAtRestLinearVelocity)
		.append("|AtRestAngularVelocity=").append(this.maximumAtRestAngularVelocity)
		.append("|AtRestTime=").append(this.minimumAtRestTime)
		.append("|VelocityConstraintSolverIterations=").append(this.velocityConstraintSolverIterations)
		.append("|PositionConstraintSolverIterations=").append(this.positionConstraintSolverIterations)
		.append("|WarmStartingEnabled=").append(this.warmStartingEnabled)
		.append("|MaximumWarmStartDistance=").append(this.maximumWarmStartDistance)
		.append("|RestitutionVelocity=").append(this.restitutionVelocity)
		.append("|LinearTolerance=").append(this.linearTolerance)
		.append("|AngularTolerance=").append(this.angularTolerance)
		.append("|MaximumLinearCorrection=").append(this.maximumLinearCorrection)
		.append("|MaximumAngularCorrection=").append(this.maximumAngularCorrection)
		.append("|Baumgarte=").append(this.baumgarte)
		.append("|ContinuousDetectionMode=").append(this.continuousDetectionMode)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Resets the settings back to defaults.
	 */
	public void reset() {
		this.stepFrequency = Settings.DEFAULT_STEP_FREQUENCY;
		this.maximumTranslation = Settings.DEFAULT_MAXIMUM_TRANSLATION;
		this.maximumTranslationSquared = Settings.DEFAULT_MAXIMUM_TRANSLATION * Settings.DEFAULT_MAXIMUM_TRANSLATION;
		this.maximumRotation = Settings.DEFAULT_MAXIMUM_ROTATION;
		this.maximumRotationSquared = Settings.DEFAULT_MAXIMUM_ROTATION * Settings.DEFAULT_MAXIMUM_ROTATION;
		this.atRestDetectionEnabled = true;
		this.maximumAtRestLinearVelocity = Settings.DEFAULT_MAXIMUM_AT_REST_LINEAR_VELOCITY;
		this.maximumAtRestLinearVelocitySquared = Settings.DEFAULT_MAXIMUM_AT_REST_LINEAR_VELOCITY * Settings.DEFAULT_MAXIMUM_AT_REST_LINEAR_VELOCITY;
		this.maximumAtRestAngularVelocity = Settings.DEFAULT_MAXIMUM_AT_REST_ANGULAR_VELOCITY;
		this.maximumAtRestAngularVelocitySquared = Settings.DEFAULT_MAXIMUM_AT_REST_ANGULAR_VELOCITY * Settings.DEFAULT_MAXIMUM_AT_REST_ANGULAR_VELOCITY;
		this.minimumAtRestTime = Settings.DEFAULT_MINIMUM_AT_REST_TIME;
		this.velocityConstraintSolverIterations = Settings.DEFAULT_SOLVER_ITERATIONS;
		this.positionConstraintSolverIterations = Settings.DEFAULT_SOLVER_ITERATIONS;
		this.warmStartingEnabled = true;
		this.maximumWarmStartDistance = Settings.DEFAULT_MAXIMUM_WARM_START_DISTANCE;
		this.maximumWarmStartDistanceSquared = Settings.DEFAULT_MAXIMUM_WARM_START_DISTANCE * Settings.DEFAULT_MAXIMUM_WARM_START_DISTANCE;
		this.restitutionVelocity = Settings.DEFAULT_RESTITUTION_VELOCITY;
		this.restitutionVelocitySquared = Settings.DEFAULT_RESTITUTION_VELOCITY * Settings.DEFAULT_RESTITUTION_VELOCITY;
		this.linearTolerance = Settings.DEFAULT_LINEAR_TOLERANCE;
		this.linearToleranceSquared = Settings.DEFAULT_LINEAR_TOLERANCE * Settings.DEFAULT_LINEAR_TOLERANCE;
		this.maximumLinearCorrection = Settings.DEFAULT_MAXIMUM_LINEAR_CORRECTION;
		this.maximumLinearCorrectionSquared = Settings.DEFAULT_MAXIMUM_LINEAR_CORRECTION * Settings.DEFAULT_MAXIMUM_LINEAR_CORRECTION;
		this.angularTolerance = Settings.DEFAULT_ANGULAR_TOLERANCE;
		this.angularToleranceSquared = Settings.DEFAULT_ANGULAR_TOLERANCE * Settings.DEFAULT_ANGULAR_TOLERANCE;
		this.baumgarte = Settings.DEFAULT_BAUMGARTE;
		this.continuousDetectionMode = ContinuousDetectionMode.ALL;
	}
	
	/**
	 * Returns the step frequency of the dynamics engine in seconds.
	 * <p>
	 * @return double the step frequency
	 * @see #setStepFrequency(double)
	 */
	public double getStepFrequency() {
		return this.stepFrequency;
	}
	
	/**
	 * Sets the step frequency of the dynamics engine.  This value determines how often to 
	 * update the dynamics engine in seconds (every 1/60th of a second for example).
	 * <p>
	 * Valid values are in the range (0, &infin;] seconds.
	 * <p>
	 * Versions before 3.1.1 would convert the stepFrequency parameter from seconds<sup>-1</sup> to
	 * seconds (60 to 1/60 for example) automatically.  This automatic conversion has been removed 
	 * in versions 3.1.1 and higher.  Instead pass in the value in seconds (1/60 for example).
	 * @param stepFrequency the step frequency
	 * @throws IllegalArgumentException if stepFrequency is less than or equal to zero
	 */
	public void setStepFrequency(double stepFrequency) {
		if (stepFrequency <= 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidStepFrequency"));
		this.stepFrequency = stepFrequency;
	}
	
	/**
	 * Returns the maximum translation a {@link PhysicsBody} can have in one time step.
	 * @return double the maximum translation in meters
	 * @see #setMaximumTranslation(double)
	 */
	public double getMaximumTranslation() {
		return this.maximumTranslation;
	}
	
	/**
	 * Returns the maximum translation squared.
	 * @see #getMaximumTranslation()
	 * @see #setMaximumTranslation(double)
	 * @return double
	 */
	public double getMaximumTranslationSquared() {
		return this.maximumTranslationSquared;
	}
	
	/**
	 * Sets the maximum translation a {@link PhysicsBody} can have in one time step.
	 * <p>
	 * Valid values are in the range [0, &infin;] meters
	 * @param maximumTranslation the maximum translation
	 * @throws IllegalArgumentException if maxTranslation is less than zero
	 */
	public void setMaximumTranslation(double maximumTranslation) {
		if (maximumTranslation < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidMaximumTranslation"));
		this.maximumTranslation = maximumTranslation;
		this.maximumTranslationSquared = maximumTranslation * maximumTranslation;
	}

	/**
	 * Returns the maximum rotation a {@link PhysicsBody} can have in one time step.
	 * @return double the maximum rotation in radians
	 * @see #setMaximumRotation(double)
	 */
	public double getMaximumRotation() {
		return this.maximumRotation;
	}
	
	/**
	 * Returns the max rotation squared.
	 * @see #getMaximumRotation()
	 * @see #setMaximumRotation(double)
	 * @return double
	 */
	public double getMaximumRotationSquared() {
		return this.maximumRotationSquared;
	}
	
	/**
	 * Sets the maximum rotation a {@link PhysicsBody} can have in one time step.
	 * <p>
	 * Valid values are in the range [0, &infin;] radians
	 * @param maximumRotation the maximum rotation
	 * @throws IllegalArgumentException if maxRotation is less than zero
	 */
	public void setMaximumRotation(double maximumRotation) {
		if (maximumRotation < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidMaximumRotation"));
		this.maximumRotation = maximumRotation;
		this.maximumRotationSquared = maximumRotation * maximumRotation;
	}

	/**
	 * Returns true if the engine automatically puts {@link PhysicsBody}s to sleep.
	 * @return boolean
	 * @deprecated Deprecated in 4.0.0. Use {@link #isAtRestDetectionEnabled()} instead.
	 */
	@Deprecated
	public boolean isAutoSleepingEnabled() {
		return this.atRestDetectionEnabled;
	}
	
	/**
	 * Sets whether the engine automatically puts {@link PhysicsBody}s to sleep.
	 * @param flag true if {@link PhysicsBody}s should be put to sleep automatically
	 * @deprecated Deprecated in 4.0.0. Use {@link #setAtRestDetectionEnabled(boolean)} instead.
	 */
	@Deprecated
	public void setAutoSleepingEnabled(boolean flag) {
		this.atRestDetectionEnabled = flag;
	}
	
	/**
	 * Returns true if the engine automatically flags {@link PhysicsBody}s as at-rest.
	 * @return boolean
	 * @since 4.0.0
	 */
	public boolean isAtRestDetectionEnabled() {
		return this.atRestDetectionEnabled;
	}
	
	/**
	 * Sets whether the engine automatically flags {@link PhysicsBody}s as at-rest.
	 * @param flag true if {@link PhysicsBody}s should be flagged as at-rest automatically
	 * @since 4.0.0
	 */
	public void setAtRestDetectionEnabled(boolean flag) {
		this.atRestDetectionEnabled = flag;
	}
	
	/**
	 * Returns the sleep linear velocity.
	 * @return double the sleep velocity.
	 * @see #setSleepLinearVelocity(double)
	 * @deprecated Deprecated in 4.0.0. Use {@link #getMaximumAtRestLinearVelocity()} instead.
	 */
	@Deprecated
	public double getSleepLinearVelocity() {
		return this.maximumAtRestLinearVelocity;
	}
	
	/**
	 * Returns the sleep linear velocity squared.
	 * @see #getSleepLinearVelocity()
	 * @see #setSleepLinearVelocity(double)
	 * @return double
	 * @deprecated Deprecated in 4.0.0. Use {@link #getMaximumAtRestLinearVelocitySquared()} instead.
	 */
	@Deprecated
	public double getSleepLinearVelocitySquared() {
		return this.maximumAtRestLinearVelocitySquared;
	}

	/**
	 * Sets the sleep linear velocity.
	 * <p>
	 * The sleep linear velocity is the maximum velocity a {@link PhysicsBody} can have
	 * to be put to sleep.
	 * <p>
	 * Valid values are in the range [0, &infin;] meters/second
	 * @param sleepLinearVelocity the sleep linear velocity
	 * @throws IllegalArgumentException if sleepLinearVelocity is less than zero
	 * @deprecated Deprecated in 4.0.0. Use {@link #setMaximumAtRestLinearVelocity(double)} instead.
	 */
	@Deprecated
	public void setSleepLinearVelocity(double sleepLinearVelocity) {
		if (sleepLinearVelocity < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidSleepLinearVelocity"));
		this.maximumAtRestLinearVelocity = sleepLinearVelocity;
		this.maximumAtRestLinearVelocitySquared = sleepLinearVelocity * sleepLinearVelocity;
	}
	
	/**
	 * Returns the maximum at-rest linear velocity.
	 * @return double the maximum at-rest velocity
	 * @see #setSleepLinearVelocity(double)
	 * @since 4.0.0
	 */
	public double getMaximumAtRestLinearVelocity() {
		return this.maximumAtRestLinearVelocity;
	}
	
	/**
	 * Returns the maximum at-rest linear velocity squared.
	 * @see #getMaximumAtRestLinearVelocity()
	 * @see #setSleepLinearVelocity(double)
	 * @return double
	 * @since 4.0.0
	 */
	public double getMaximumAtRestLinearVelocitySquared() {
		return this.maximumAtRestLinearVelocitySquared;
	}

	/**
	 * Sets the maximum at-rest linear velocity.
	 * <p>
	 * The maximum at-rest linear velocity is the maximum linear velocity a {@link PhysicsBody} can have
	 * to be flagged as at-rest.
	 * <p>
	 * Valid values are in the range [0, &infin;] meters/second
	 * @param maximumAtRestLinearVelocity the maximum at-rest linear velocity
	 * @throws IllegalArgumentException if maximumAtRestLinearVelocity is less than zero
	 * @since 4.0.0
	 */
	public void setMaximumAtRestLinearVelocity(double maximumAtRestLinearVelocity) {
		if (maximumAtRestLinearVelocity < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidMaximumAtRestLinearVelocity"));
		this.maximumAtRestLinearVelocity = maximumAtRestLinearVelocity;
		this.maximumAtRestLinearVelocitySquared = maximumAtRestLinearVelocity * maximumAtRestLinearVelocity;
	}
	
	/**
	 * Returns the sleep angular velocity.
	 * @return double the sleep angular velocity.
	 * @see #setSleepAngularVelocity(double)
	 * @deprecated Deprecated in 4.0.0. Use {@link #getMaximumAtRestAngularVelocity()} instead.
	 */
	@Deprecated
	public double getSleepAngularVelocity() {
		return this.maximumAtRestAngularVelocity;
	}
	
	/**
	 * Returns the sleep angular velocity squared.
	 * @see #getSleepAngularVelocity()
	 * @see #setSleepAngularVelocity(double)
	 * @return double
	 * @deprecated Deprecated in 4.0.0. Use {@link #getMaximumAtRestAngularVelocitySquared()} instead.
	 */
	@Deprecated
	public double getSleepAngularVelocitySquared() {
		return this.maximumAtRestAngularVelocitySquared;
	}

	/**
	 * Sets the sleep angular velocity.
	 * <p>
	 * The sleep angular velocity is the maximum angular velocity a {@link PhysicsBody} can have
	 * to be put to sleep.
	 * <p>
	 * Valid values are in the range [0, &infin;] radians/second
	 * @param sleepAngularVelocity the sleep angular velocity
	 * @throws IllegalArgumentException if sleepAngularVelocity is less than zero
	 * @deprecated Deprecated in 4.0.0. Use {@link #setMaximumAtRestAngularVelocity(double)} instead.
	 */
	@Deprecated
	public void setSleepAngularVelocity(double sleepAngularVelocity) {
		if (sleepAngularVelocity < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidSleepAngularVelocity"));
		this.maximumAtRestAngularVelocity = sleepAngularVelocity;
		this.maximumAtRestAngularVelocitySquared = sleepAngularVelocity * sleepAngularVelocity;
	}
	
	/**
	 * Returns the maximum at-rest angular velocity.
	 * @return double the maximum at-rest velocity
	 * @see #setMaximumAtRestAngularVelocity(double)
	 * @since 4.0.0
	 */
	public double getMaximumAtRestAngularVelocity() {
		return this.maximumAtRestAngularVelocity;
	}
	
	/**
	 * Returns the maximum at-rest angular velocity squared.
	 * @see #getMaximumAtRestAngularVelocity()
	 * @see #setMaximumAtRestAngularVelocity(double)
	 * @return double
	 * @since 4.0.0
	 */
	public double getMaximumAtRestAngularVelocitySquared() {
		return this.maximumAtRestAngularVelocitySquared;
	}

	/**
	 * Sets the maximum at-rest angular velocity.
	 * <p>
	 * The maximum at-rest angular velocity is the maximum angular velocity a {@link PhysicsBody} can have
	 * to be flagged as at-rest.
	 * <p>
	 * Valid values are in the range [0, &infin;] meters/second
	 * @param maximumAtRestAngularVelocity the maximum at-rest angular velocity
	 * @throws IllegalArgumentException if maximumAtRestAngularVelocity is less than zero
	 * @since 4.0.0
	 */
	public void setMaximumAtRestAngularVelocity(double maximumAtRestAngularVelocity) {
		if (maximumAtRestAngularVelocity < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidMaximumAtRestAngularVelocity"));
		this.maximumAtRestAngularVelocity = maximumAtRestAngularVelocity;
		this.maximumAtRestAngularVelocitySquared = maximumAtRestAngularVelocity * maximumAtRestAngularVelocity;
	}
	
	/**
	 * Returns the sleep time.
	 * @return double the sleep time
	 * @see #setSleepTime(double)
	 * @deprecated Deprecated in 4.0.0. Use {@link #getMinimumAtRestTime()} instead.
	 */
	@Deprecated
	public double getSleepTime() {
		return this.minimumAtRestTime;
	}

	/**
	 * Sets the sleep time.
	 * <p>
	 * The sleep time is the amount of time a body must be motionless
	 * before being put to sleep.
	 * <p>
	 * Valid values are in the range [0, &infin;] seconds
	 * @param sleepTime the sleep time
	 * @throws IllegalArgumentException if sleepTime is less than zero
	 * @deprecated Deprecated in 4.0.0. Use {@link #setMinimumAtRestTime(double)} instead.
	 */
	@Deprecated
	public void setSleepTime(double sleepTime) {
		if (sleepTime < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidSleepTime"));
		this.minimumAtRestTime = sleepTime;
	}

	/**
	 * Returns the minimum time that a {@link PhysicsBody} is motionless before being flagged as at-rest.
	 * @return double the minimum at-rest time
	 * @see #setMinimumAtRestTime(double)
	 * @since 4.0.0
	 */
	public double getMinimumAtRestTime() {
		return this.minimumAtRestTime;
	}

	/**
	 * Sets the minimum at-rest time.
	 * <p>
	 * This is the minimum time a body must be motionless before being flagged as at-rest.
	 * <p>
	 * Valid values are in the range [0, &infin;] seconds
	 * @param minimumAtRestTime the minimum at-rest time
	 * @throws IllegalArgumentException if minimumAtRestTime is less than zero
	 * @since 4.0.0
	 */
	public void setMinimumAtRestTime(double minimumAtRestTime) {
		if (minimumAtRestTime < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidMinimumAtRestTime"));
		this.minimumAtRestTime = minimumAtRestTime;
	}
	
	/**
	 * Returns the number of iterations used to solve velocity constraints.
	 * @return int
	 */
	public int getVelocityConstraintSolverIterations() {
		return this.velocityConstraintSolverIterations;
	}
	
	/**
	 * Sets the number of iterations used to solve velocity constraints.
	 * <p>
	 * Increasing the number will increase accuracy but decrease performance.
	 * <p>
	 * Valid values are in the range [1, &infin;]
	 * @param velocityConstraintSolverIterations the number of iterations used to solve velocity constraints
	 * @throws IllegalArgumentException if velocityConstraintSolverIterations is less than 5
	 */
	public void setVelocityConstraintSolverIterations(int velocityConstraintSolverIterations) {
		if (velocityConstraintSolverIterations < 1) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidVelocityIterations"));
		this.velocityConstraintSolverIterations = velocityConstraintSolverIterations;
	}
	
	/**
	 * Returns the number of iterations used to solve position constraints.
	 * @return int
	 */
	public int getPositionConstraintSolverIterations() {
		return this.positionConstraintSolverIterations;
	}
	
	/**
	 * Sets the number of iterations used to solve position constraints.
	 * <p>
	 * Increasing the number will increase accuracy but decrease performance.
	 * <p>
	 * Valid values are in the range [1, &infin;]
	 * @param positionConstraintSolverIterations the number of iterations used to solve position constraints
	 * @throws IllegalArgumentException if positionConstraintSolverIterations is less than 5
	 */
	public void setPositionConstraintSolverIterations(int positionConstraintSolverIterations) {
		if (positionConstraintSolverIterations < 1) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidPositionIterations"));
		this.positionConstraintSolverIterations = positionConstraintSolverIterations;
	}
	
	/**
	 * Returns the warm start distance.
	 * @return double the warm start distance
	 * @see #setWarmStartDistance(double)
	 * @deprecated Deprecated in 4.0.0. Use {@link #getMaximumWarmStartDistance()} instead.
	 */
	@Deprecated
	public double getWarmStartDistance() {
		return this.maximumWarmStartDistance;
	}
	
	/**
	 * Returns the warm start distance squared.
	 * @see #getWarmStartDistance()
	 * @see #setWarmStartDistance(double)
	 * @return double
	 * @deprecated Deprecated in 4.0.0. Use {@link #getMaximumWarmStartDistanceSquared()} instead.
	 */
	@Deprecated
	public double getWarmStartDistanceSquared() {
		return maximumWarmStartDistanceSquared;
	}

	/**
	 * Sets the warm start distance.
	 * <p>
	 * The maximum distance from one point to another to consider the points to be the
	 * same.  This distance is used to determine if the points can carry over another
	 * point's accumulated impulses to be used for warm starting the constraint solver.
	 * <p>
	 * Valid values are in the range [0, &infin;] meters
	 * @param warmStartDistance the warm start distance
	 * @throws IllegalArgumentException if warmStartDistance is less than zero
	 * @deprecated Deprecated in 4.0.0. Use {@link #setMaximumWarmStartDistance(double)} instead.
	 */
	@Deprecated
	public void setWarmStartDistance(double warmStartDistance) {
		if (warmStartDistance < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidWarmStartDistance"));
		this.maximumWarmStartDistance = warmStartDistance;
		this.maximumWarmStartDistanceSquared = this.maximumWarmStartDistance * this.maximumWarmStartDistance;
	}

	/**
	 * Returns the maximum warm start distance.
	 * @return double the warm start distance
	 * @see #setMaximumWarmStartDistance(double)
	 */
	public double getMaximumWarmStartDistance() {
		return this.maximumWarmStartDistance;
	}
	
	/**
	 * Returns the maximum warm start distance squared.
	 * @see #getMaximumWarmStartDistance()
	 * @see #setMaximumWarmStartDistance(double)
	 * @return double
	 */
	public double getMaximumWarmStartDistanceSquared() {
		return maximumWarmStartDistanceSquared;
	}

	/**
	 * Sets the maximum warm start distance.
	 * <p>
	 * The maximum distance from one point to another to consider the points to be the
	 * same.  This distance is used to determine if the points can carry over another
	 * point's accumulated impulses to be used for warm starting the constraint solver.
	 * <p>
	 * Valid values are in the range [0, &infin;] meters
	 * @param maximumWarmStartDistance the maximum warm start distance
	 * @throws IllegalArgumentException if warmStartDistance is less than zero
	 */
	public void setMaximumWarmStartDistance(double maximumWarmStartDistance) {
		if (maximumWarmStartDistance < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidMaximumWarmStartDistance"));
		this.maximumWarmStartDistance = maximumWarmStartDistance;
		this.maximumWarmStartDistanceSquared = this.maximumWarmStartDistance * this.maximumWarmStartDistance;
	}
	
	/**
	 * Returns true if warm starting of contacts is enabled.
	 * @return boolean
	 * @since 4.0.0
	 * @see #setWarmStartingEnabled(boolean)
	 */
	public boolean isWarmStartingEnabled() {
		return this.warmStartingEnabled;
	}

	/**
	 * Sets the warm starting of contacts to enabled.
	 * <p>
	 * Warm starting of contacts is used to increase performance of
	 * contact constraint solving. Warm starting is the process of using
	 * the last frame's solver information to jump start the next.
	 * @param flag true if warm starting should be enabled
	 * @since 4.0.0
	 */
	public void setWarmStartingEnabled(boolean flag) {
		this.warmStartingEnabled = flag;
	}
	
	/**
	 * Returns the restitution velocity.
	 * @return double the restitution velocity
	 * @see #setRestitutionVelocity(double)
	 */
	public double getRestitutionVelocity() {
		return this.restitutionVelocity;
	}
	
	/**
	 * Returns the restitution velocity squared.
	 * @see #getRestitutionVelocity()
	 * @see #setRestitutionVelocity(double)
	 * @return double
	 */
	public double getRestitutionVelocitySquared() {
		return restitutionVelocitySquared;
	}
	
	/**
	 * Sets the restitution velocity.
	 * <p>
	 * The relative velocity in the direction of the contact normal which determines
	 * whether to handle the collision as an inelastic or elastic collision.
	 * <p>
	 * Valid values are in the range [0, &infin;] meters/second
	 * @param restitutionVelocity the restitution velocity
	 * @throws IllegalArgumentException if restitutionVelocity is less than zero
	 */
	public void setRestitutionVelocity(double restitutionVelocity) {
		if (restitutionVelocity < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidRestitutionVelocity"));
		this.restitutionVelocity = restitutionVelocity;
		this.restitutionVelocitySquared = restitutionVelocity * restitutionVelocity;
	}

	/**
	 * Returns the linear tolerance.
	 * @return double the allowed penetration
	 * @see #setLinearTolerance(double)
	 */
	public double getLinearTolerance() {
		return this.linearTolerance;
	}
	
	/**
	 * Returns the linear tolerance squared.
	 * @see #getLinearTolerance()
	 * @see #setLinearTolerance(double)
	 * @return double
	 */
	public double getLinearToleranceSquared() {
		return linearToleranceSquared;
	}
	
	/**
	 * Sets the linear tolerance.
	 * <p>
	 * Used to avoid jitter and facilitate stacking.
	 * <p>
	 * Valid values are in the range (0, &infin;] meters
	 * @param linearTolerance the linear tolerance
	 * @throws IllegalArgumentException if linearTolerance is less than zero
	 */
	public void setLinearTolerance(double linearTolerance) {
		if (linearTolerance < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidLinearTolerance"));
		this.linearTolerance = linearTolerance;
		this.linearToleranceSquared = linearTolerance * linearTolerance;
	}
	
	/**
	 * Returns the angular tolerance.
	 * @see #setAngularTolerance(double)
	 * @return double
	 */
	public double getAngularTolerance() {
		return angularTolerance;
	}
	
	/**
	 * Returns the angular tolerance squared.
	 * @see #getAngularTolerance()
	 * @see #setAngularTolerance(double)
	 * @return double
	 */
	public double getAngularToleranceSquared() {
		return angularToleranceSquared;
	}
	
	/**
	 * Sets the angular tolerance.
	 * <p>
	 * Used to avoid jitter and facilitate stacking.
	 * <p>
	 * Valid values are in the range (0, &infin;] radians
	 * @param angularTolerance the angular tolerance
	 * @throws IllegalArgumentException if angularTolerance is less than zero
	 */
	public void setAngularTolerance(double angularTolerance) {
		if (angularTolerance < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidAngularTolerance"));
		this.angularTolerance = angularTolerance;
		this.angularToleranceSquared = angularTolerance * angularTolerance;
	}
	
	/**
	 * Returns the maximum linear correction.
	 * @return double the maximum linear correction
	 * @see #setMaximumLinearCorrection(double)
	 */
	public double getMaximumLinearCorrection() {
		return this.maximumLinearCorrection;
	}
	
	/**
	 * Returns the maximum linear correction squared.
	 * @see #getMaximumLinearCorrection()
	 * @see #setMaximumLinearCorrection(double)
	 * @return double
	 */
	public double getMaximumLinearCorrectionSquared() {
		return maximumLinearCorrectionSquared;
	}

	/**
	 * Sets the maximum linear correction.
	 * <p>
	 * The maximum linear correction used when estimating the current penetration depth
	 * during the position constraint solving step.
	 * <p>
	 * This is used to avoid large corrections.
	 * <p>
	 * Valid values are in the range (0, &infin;] meters
	 * @param maximumLinearCorrection the maximum linear correction
	 * @throws IllegalArgumentException if maxLinearCorrection is less than zero
	 */
	public void setMaximumLinearCorrection(double maximumLinearCorrection) {
		if (maximumLinearCorrection < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidMaximumLinearCorrection"));
		this.maximumLinearCorrection = maximumLinearCorrection;
		this.maximumLinearCorrectionSquared = maximumLinearCorrection * maximumLinearCorrection;
	}
	
	/**
	 * Returns the maximum angular correction.
	 * @see #setMaximumAngularCorrection(double)
	 * @return double
	 */
	public double getMaximumAngularCorrection() {
		return maximumAngularCorrection;
	}
	
	/**
	 * Returns the maximum angular correction squared.
	 * @see #getMaximumAngularCorrection()
	 * @see #setMaximumAngularCorrection(double)
	 * @return double
	 */
	public double getMaximumAngularCorrectionSquared() {
		return maximumAngularCorrectionSquared;
	}
	
	/**
	 * Sets the maximum angular correction.
	 * <p>
	 * This is used to prevent large angular corrections.
	 * <p>
	 * Valid values are in the range [0, &infin;] radians
	 * @param maximumAngularCorrection the maximum angular correction
	 * @throws IllegalArgumentException if maxAngularCorrection is less than zero
	 */
	public void setMaximumAngularCorrection(double maximumAngularCorrection) {
		if (maximumAngularCorrection < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidMaximumAngularCorrection"));
		this.maximumAngularCorrection = maximumAngularCorrection;
		this.maximumAngularCorrectionSquared = maximumAngularCorrection * maximumAngularCorrection;
	}
	
	/**
	 * Returns the baumgarte factor.
	 * @return double baumgarte
	 * @see #setBaumgarte(double)
	 */
	public double getBaumgarte() {
		return this.baumgarte;
	}

	/**
	 * Sets the baumgarte factor.
	 * <p>
	 * The position correction bias factor that determines the rate at which the position constraints are solved.
	 * <p>
	 * Valid values are in the range [0, &infin;].
	 * @param baumgarte the baumgarte factor
	 * @throws IllegalArgumentException if baumgarte is less than zero
	 */
	public void setBaumgarte(double baumgarte) {
		if (baumgarte < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidBaumgarte"));
		this.baumgarte = baumgarte;
	}
	
	/**
	 * Returns the continuous collision detection mode.
	 * @return {@link ContinuousDetectionMode}
	 * @since 2.2.3
	 */
	public ContinuousDetectionMode getContinuousDetectionMode() {
		return this.continuousDetectionMode;
	}
	
	/**
	 * Sets the continuous collision detection mode.
	 * @param mode the CCD mode
	 * @throws NullPointerException if mode is null
	 * @since 2.2.3
	 */
	public void setContinuousDetectionMode(ContinuousDetectionMode mode) {
		// make sure its not null
		if (mode == null) throw new NullPointerException(Messages.getString("dynamics.settings.invalidCCDMode"));
		// set the mode
		this.continuousDetectionMode = mode;
	}
}
