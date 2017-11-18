/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.resources.Messages;

/**
 * Responsible for housing all of the dynamics engine's settings.
 * @author William Bittle
 * @version 3.1.1
 * @since 1.0.0
 */
public class Settings {
	/** The default step frequency of the dynamics engine; in seconds */
	public static final double DEFAULT_STEP_FREQUENCY = 1.0 / 60.0;
	
	/** The default maximum translation a {@link Body} can have in one time step; in meters */
	public static final double DEFAULT_MAXIMUM_TRANSLATION = 2.0;
	
	/** The default maximum rotation a {@link Body} can have in one time step; in radians */
	public static final double DEFAULT_MAXIMUM_ROTATION = 0.5 * Math.PI;
	
	/** The default maximum velocity for a {@link Body} to go to sleep; in meters/second */
	public static final double DEFAULT_SLEEP_LINEAR_VELOCITY = 0.01;
	
	/** The default maximum angular velocity for a {@link Body} to go to sleep; in radians/second */
	public static final double DEFAULT_SLEEP_ANGULAR_VELOCITY = Math.toRadians(2.0);
	
	/** The default required time a {@link Body} must maintain small motion so that its put to sleep; in seconds */
	public static final double DEFAULT_SLEEP_TIME = 0.5;

	/** The default number of solver iterations */
	public static final int DEFAULT_SOLVER_ITERATIONS = 10;

	/** The default warm starting distance; in meters<sup>2</sup> */
	public static final double DEFAULT_WARM_START_DISTANCE = 1.0e-2;
	
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
	
	/** The maximum translation a {@link Body} can have in one time step */
	private double maximumTranslation = Settings.DEFAULT_MAXIMUM_TRANSLATION;
	
	/** The squared value of {@link #maximumTranslation} */
	private double maximumTranslationSquared = Settings.DEFAULT_MAXIMUM_TRANSLATION * Settings.DEFAULT_MAXIMUM_TRANSLATION;
	
	/** The maximum rotation a {@link Body} can have in one time step */
	private double maximumRotation = Settings.DEFAULT_MAXIMUM_ROTATION;

	/** The squared value of {@link #maximumRotation} */
	private double maximumRotationSquared = Settings.DEFAULT_MAXIMUM_ROTATION * Settings.DEFAULT_MAXIMUM_ROTATION;
	
	/** Whether on an engine level {@link Body}s are automatically put to sleep */
	private boolean autoSleepingEnabled = true;
	
	/** The maximum linear velocity before a {@link Body} is considered to sleep */
	private double sleepLinearVelocity = Settings.DEFAULT_SLEEP_LINEAR_VELOCITY;
	
	/** The squared value of {@link #sleepLinearVelocity} */
	private double sleepLinearVelocitySquared = Settings.DEFAULT_SLEEP_LINEAR_VELOCITY * Settings.DEFAULT_SLEEP_LINEAR_VELOCITY;
	
	/** The maximum angular velocity before a {@link Body} is considered to sleep */
	private double sleepAngularVelocity = Settings.DEFAULT_SLEEP_ANGULAR_VELOCITY;
	
	/** The squared value of {@link #sleepAngularVelocity} */
	private double sleepAngularVelocitySquared = Settings.DEFAULT_SLEEP_ANGULAR_VELOCITY * Settings.DEFAULT_SLEEP_ANGULAR_VELOCITY;
	
	/** The time required for a {@link Body} to stay motionless before going to sleep */
	private double sleepTime = Settings.DEFAULT_SLEEP_TIME;
	
	/** The number of iterations used to solve velocity constraints */
	private int velocityConstraintSolverIterations = Settings.DEFAULT_SOLVER_ITERATIONS;
	
	/** The maximum number of iterations used to solve position constraints */
	private int positionConstraintSolverIterations = Settings.DEFAULT_SOLVER_ITERATIONS;
	
	/** The warm start distance */
	private double warmStartDistance = Settings.DEFAULT_WARM_START_DISTANCE;
	
	/** The squared value of {@link #warmStartDistance} */
	private double warmStartDistanceSquared = Settings.DEFAULT_WARM_START_DISTANCE * Settings.DEFAULT_WARM_START_DISTANCE;
	
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
		.append("|AutoSleepingEnabled=").append(this.autoSleepingEnabled)
		.append("|SleepLinearVelocity=").append(this.sleepLinearVelocity)
		.append("|SleepAngularVelocity=").append(this.sleepAngularVelocity)
		.append("|SleepTime=").append(this.sleepTime)
		.append("|VelocityConstraintSolverIterations=").append(this.velocityConstraintSolverIterations)
		.append("|PositionConstraintSolverIterations=").append(this.positionConstraintSolverIterations)
		.append("|WarmStartDistance=").append(this.warmStartDistance)
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
		this.autoSleepingEnabled = true;
		this.sleepLinearVelocity = Settings.DEFAULT_SLEEP_LINEAR_VELOCITY;
		this.sleepLinearVelocitySquared = Settings.DEFAULT_SLEEP_LINEAR_VELOCITY * Settings.DEFAULT_SLEEP_LINEAR_VELOCITY;
		this.sleepAngularVelocity = Settings.DEFAULT_SLEEP_ANGULAR_VELOCITY;
		this.sleepAngularVelocitySquared = Settings.DEFAULT_SLEEP_ANGULAR_VELOCITY * Settings.DEFAULT_SLEEP_ANGULAR_VELOCITY;
		this.sleepTime = Settings.DEFAULT_SLEEP_TIME;
		this.velocityConstraintSolverIterations = Settings.DEFAULT_SOLVER_ITERATIONS;
		this.positionConstraintSolverIterations = Settings.DEFAULT_SOLVER_ITERATIONS;
		this.warmStartDistance = Settings.DEFAULT_WARM_START_DISTANCE;
		this.warmStartDistanceSquared = Settings.DEFAULT_WARM_START_DISTANCE * Settings.DEFAULT_WARM_START_DISTANCE;
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
	 * Returns the maximum translation a {@link Body} can have in one time step.
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
	 * Sets the maximum translation a {@link Body} can have in one time step.
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
	 * Returns the maximum rotation a {@link Body} can have in one time step.
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
	 * Sets the maximum rotation a {@link Body} can have in one time step.
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
	 * Returns true if the engine automatically puts {@link Body}s to sleep.
	 * @return boolean
	 */
	public boolean isAutoSleepingEnabled() {
		return this.autoSleepingEnabled;
	}
	
	/**
	 * Sets whether the engine automatically puts {@link Body}s to sleep.
	 * @param flag true if {@link Body}s should be put to sleep automatically
	 */
	public void setAutoSleepingEnabled(boolean flag) {
		this.autoSleepingEnabled = flag;
	}
	
	/**
	 * Returns the sleep linear velocity.
	 * @return double the sleep velocity.
	 * @see #setSleepLinearVelocity(double)
	 */
	public double getSleepLinearVelocity() {
		return this.sleepLinearVelocity;
	}
	
	/**
	 * Returns the sleep linear velocity squared.
	 * @see #getSleepLinearVelocity()
	 * @see #setSleepLinearVelocity(double)
	 * @return double
	 */
	public double getSleepLinearVelocitySquared() {
		return this.sleepLinearVelocitySquared;
	}

	/**
	 * Sets the sleep linear velocity.
	 * <p>
	 * The sleep linear velocity is the maximum velocity a {@link Body} can have
	 * to be put to sleep.
	 * <p>
	 * Valid values are in the range [0, &infin;] meters/second
	 * @param sleepLinearVelocity the sleep linear velocity
	 * @throws IllegalArgumentException if sleepLinearVelocity is less than zero
	 */
	public void setSleepLinearVelocity(double sleepLinearVelocity) {
		if (sleepLinearVelocity < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidSleepLinearVelocity"));
		this.sleepLinearVelocity = sleepLinearVelocity;
		this.sleepLinearVelocitySquared = sleepLinearVelocity * sleepLinearVelocity;
	}
	
	/**
	 * Returns the sleep angular velocity.
	 * @return double the sleep angular velocity.
	 * @see #setSleepAngularVelocity(double)
	 */
	public double getSleepAngularVelocity() {
		return this.sleepAngularVelocity;
	}
	
	/**
	 * Returns the sleep angular velocity squared.
	 * @see #getSleepAngularVelocity()
	 * @see #setSleepAngularVelocity(double)
	 * @return double
	 */
	public double getSleepAngularVelocitySquared() {
		return this.sleepAngularVelocitySquared;
	}

	/**
	 * Sets the sleep angular velocity.
	 * <p>
	 * The sleep angular velocity is the maximum angular velocity a {@link Body} can have
	 * to be put to sleep.
	 * <p>
	 * Valid values are in the range [0, &infin;] radians/second
	 * @param sleepAngularVelocity the sleep angular velocity
	 * @throws IllegalArgumentException if sleepAngularVelocity is less than zero
	 */
	public void setSleepAngularVelocity(double sleepAngularVelocity) {
		if (sleepAngularVelocity < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidSleepAngularVelocity"));
		this.sleepAngularVelocity = sleepAngularVelocity;
		this.sleepAngularVelocitySquared = sleepAngularVelocity * sleepAngularVelocity;
	}

	/**
	 * Returns the sleep time.
	 * @return double the sleep time
	 * @see #setSleepTime(double)
	 */
	public double getSleepTime() {
		return this.sleepTime;
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
	 */
	public void setSleepTime(double sleepTime) {
		if (sleepTime < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidSleepTime"));
		this.sleepTime = sleepTime;
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
	 */
	public double getWarmStartDistance() {
		return this.warmStartDistance;
	}
	
	/**
	 * Returns the warm start distance squared.
	 * @see #getWarmStartDistance()
	 * @see #setWarmStartDistance(double)
	 * @return double
	 */
	public double getWarmStartDistanceSquared() {
		return warmStartDistanceSquared;
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
	 */
	public void setWarmStartDistance(double warmStartDistance) {
		if (warmStartDistance < 0) throw new IllegalArgumentException(Messages.getString("dynamics.settings.invalidWarmStartDistance"));
		this.warmStartDistance = warmStartDistance;
		this.warmStartDistanceSquared = this.warmStartDistance * this.warmStartDistance;
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
