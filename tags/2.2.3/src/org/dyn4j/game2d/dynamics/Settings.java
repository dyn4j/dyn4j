/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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

/**
 * Responsible for housing all of the dynamics engine's settings.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class Settings {
	/**
	 * Enumeration of Continuous Collision Detection modes.
	 * @author William Bittle
	 * @version 2.2.3
	 * @since 2.2.3
	 */
	public static enum ContinuousDetectionMode {
		/** CCD is not performed at all */
		NONE,
		/** 
		 * CCD is only performed on the following pairs:
		 * <ul>
		 * <li>Bullet vs. Dynamic</li>
		 * <li>Bullet vs. Static</li>
		 * </ul>
		 */
		BULLETS_ONLY,
		/** 
		 * CCD is performed on the following pairs:
		 * <ul>
		 * <li>Dynamic vs. Static</li>
		 * <li>Bullet vs. Static</li>
		 * <li>Bullet vs. Dynamic</li>
		 * </ul> 
		 */
		ALL
	}
	
	/** The number of CPUs available */
	public static final int NUMBER_OF_CPUS = Runtime.getRuntime().availableProcessors();
	
	/** The default step frequency of the dynamics engine; in seconds */
	public static final double DEFAULT_STEP_FREQUENCY = 1.0 / 60.0;
	
	/** The default maximum translation a {@link Body} can have in one time step; in meters */
	public static final double DEFAULT_MAX_TRANSLATION = 2.0;
	
	/** The default maximum rotation a {@link Body} can have in one time step; in radians */
	public static final double DEFAULT_MAX_ROTATION = 0.5 * Math.PI;
	
	/** The default maximum velocity for a {@link Body} to go to sleep; in meters/second */
	public static final double DEFAULT_SLEEP_VELOCITY = 0.01;
	
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
	public static final double DEFAULT_MAX_LINEAR_CORRECTION = 0.2;
	
	/** The default maximum angular correction; in radians */
	public static final double DEFAULT_MAX_ANGULAR_CORRECTION = Math.toRadians(8.0);
	
	/** The default baumgarte */
	public static final double DEFAULT_BAUMGARTE = 0.2;
	
	/** The default load factor for multithreaded tasks */
	public static final int DEFAULT_LOAD_FACTOR = 4;

	/** The step frequency of the dynamics engine */
	private double stepFequency = Settings.DEFAULT_STEP_FREQUENCY;
	
	/** The maximum translation a {@link Body} can have in one time step */
	private double maxTranslation = Settings.DEFAULT_MAX_TRANSLATION;
	
	/** The squared value of {@link #maxTranslation} */
	private double maxTranslationSquared = Settings.DEFAULT_MAX_TRANSLATION * Settings.DEFAULT_MAX_TRANSLATION;
	
	/** The maximum rotation a {@link Body} can have in one time step */
	private double maxRotation = Settings.DEFAULT_MAX_ROTATION;

	/** The squared value of {@link #maxRotation} */
	private double maxRotationSquared = Settings.DEFAULT_MAX_ROTATION * Settings.DEFAULT_MAX_ROTATION;
	
	/** Whether on an engine level {@link Body}s are automatically put to sleep */
	private boolean autoSleepingEnabled = true;
	
	/** The maximum velocity before a {@link Body} is considered to sleep */
	private double sleepVelocity = Settings.DEFAULT_SLEEP_VELOCITY;
	
	/** The squared value of {@link #sleepVelocity} */
	private double sleepVelocitySquared = Settings.DEFAULT_SLEEP_VELOCITY * Settings.DEFAULT_SLEEP_VELOCITY;
	
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
	private double maxLinearCorrection = Settings.DEFAULT_MAX_LINEAR_CORRECTION;
	
	/** The squared value of {@link #maxLinearCorrection} */
	private double maxLinearCorrectionSquared = Settings.DEFAULT_MAX_LINEAR_CORRECTION * Settings.DEFAULT_MAX_LINEAR_CORRECTION;
	
	/** The maximum angular correction */
	private double maxAngularCorrection = Settings.DEFAULT_MAX_ANGULAR_CORRECTION;
	
	/** The squared value of {@link #maxAngularCorrection} */
	private double maxAngularCorrectionSquared = Settings.DEFAULT_MAX_ANGULAR_CORRECTION * Settings.DEFAULT_MAX_ANGULAR_CORRECTION;
	
	/** The baumgarte factor */
	private double baumgarte = Settings.DEFAULT_BAUMGARTE;
	
	/** The continuous collision detection flag */
	private ContinuousDetectionMode continuousDetectionMode = ContinuousDetectionMode.ALL;
	
	/** Whether multithreading is enabled or not */
	private boolean multithreadingEnabled = false;
	
	/** The load factor for each task; higher = less load, lower = more load */
	private int loadFactor = Settings.DEFAULT_LOAD_FACTOR;
	
	/** The settings singleton instance */
	private static final Settings instance = new Settings();
	
	/**
	 * Returns the singleton instance of the {@link Settings} object.
	 * @return {@link Settings} the singleton
	 */
	public static Settings getInstance() {
		return Settings.instance;
	}
	
	/** Constructor */
	private Settings() {}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SETTINGS[")
		.append(this.stepFequency).append("|")
		.append(this.maxTranslation).append("|")
		.append(this.maxRotation).append("|")
		.append(this.autoSleepingEnabled).append("|")
		.append(this.sleepVelocity).append("|")
		.append(this.sleepAngularVelocity).append("|")
		.append(this.sleepTime).append("|")
		.append(this.velocityConstraintSolverIterations).append("|")
		.append(this.positionConstraintSolverIterations).append("|")
		.append(this.warmStartDistance).append("|")
		.append(this.restitutionVelocity).append("|")
		.append(this.linearTolerance).append("|")
		.append(this.angularTolerance).append("|")
		.append(this.maxLinearCorrection).append("|")
		.append(this.maxAngularCorrection).append("|")
		.append(this.baumgarte).append("|")
		.append(this.continuousDetectionMode).append("|")
		.append(this.multithreadingEnabled).append("|")
		.append(this.loadFactor)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Resets the settings back to defaults.
	 */
	public void reset() {
		this.stepFequency = Settings.DEFAULT_STEP_FREQUENCY;
		this.maxTranslation = Settings.DEFAULT_MAX_TRANSLATION;
		this.maxTranslationSquared = Settings.DEFAULT_MAX_TRANSLATION * Settings.DEFAULT_MAX_TRANSLATION;
		this.maxRotation = Settings.DEFAULT_MAX_ROTATION;
		this.maxRotationSquared = Settings.DEFAULT_MAX_ROTATION * Settings.DEFAULT_MAX_ROTATION;
		this.autoSleepingEnabled = true;
		this.sleepVelocity = Settings.DEFAULT_SLEEP_VELOCITY;
		this.sleepVelocitySquared = Settings.DEFAULT_SLEEP_VELOCITY * Settings.DEFAULT_SLEEP_VELOCITY;
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
		this.maxLinearCorrection = Settings.DEFAULT_MAX_LINEAR_CORRECTION;
		this.maxLinearCorrectionSquared = Settings.DEFAULT_MAX_LINEAR_CORRECTION * Settings.DEFAULT_MAX_LINEAR_CORRECTION;
		this.angularTolerance = Settings.DEFAULT_ANGULAR_TOLERANCE;
		this.angularToleranceSquared = Settings.DEFAULT_ANGULAR_TOLERANCE * Settings.DEFAULT_ANGULAR_TOLERANCE;
		this.baumgarte = Settings.DEFAULT_BAUMGARTE;
		this.continuousDetectionMode = ContinuousDetectionMode.ALL;
		this.multithreadingEnabled = true;
		this.loadFactor = Settings.DEFAULT_LOAD_FACTOR;
	}
	
	/**
	 * Returns the step frequency of the dynamics engine.
	 * <p>
	 * The returned value is:
	 * <pre>1.0 / frequency</pre>
	 * in seconds.
	 * <p>
	 * @return double the step frequency
	 * @see #setStepFrequency(double)
	 */
	public double getStepFrequency() {
		return this.stepFequency;
	}
	
	/**
	 * Sets the step frequency of the dynamics engine.
	 * <p>
	 * Valid values are in the range [30, &infin;] seconds<sup>-1</sup>
	 * @param stepFrequency the step frequency
	 * @throws IllegalArgumentException if stepFrequency is less than 30
	 */
	public void setStepFrequency(double stepFrequency) {
		if (stepFrequency < 30.0) throw new IllegalArgumentException("The step frequency must be 30.0 hz or greater.");
		this.stepFequency = 1.0 / stepFrequency;
	}
	
	/**
	 * Returns the maximum translation a {@link Body} can have in one time step.
	 * @return double the maximum translation in meters
	 * @see #setMaxTranslation(double)
	 */
	public double getMaxTranslation() {
		return this.maxTranslation;
	}
	
	/**
	 * Returns the maximum translation squared.
	 * @see #getMaxTranslation()
	 * @see #setMaxTranslation(double)
	 * @return double
	 */
	public double getMaxTranslationSquared() {
		return this.maxTranslationSquared;
	}
	
	/**
	 * Sets the maximum translation a {@link Body} can have in one time step.
	 * <p>
	 * Valid values are in the range [0, &infin;] meters
	 * @param maxTranslation the maximum translation
	 * @throws IllegalArgumentException if maxTranslation is less than zero
	 */
	public void setMaxTranslation(double maxTranslation) {
		if (maxTranslation < 0) throw new IllegalArgumentException("The max translation cannot be negative.");
		this.maxTranslation = maxTranslation;
		this.maxTranslationSquared = maxTranslation * maxTranslation;
	}

	/**
	 * Returns the maximum rotation a {@link Body} can have in one time step.
	 * @return double the maximum rotation in radians
	 * @see #setMaxRotation(double)
	 */
	public double getMaxRotation() {
		return this.maxRotation;
	}
	
	/**
	 * Returns the max rotation squared.
	 * @see #getMaxRotation()
	 * @see #setMaxRotation(double)
	 * @return double
	 */
	public double getMaxRotationSquared() {
		return this.maxRotationSquared;
	}
	
	/**
	 * Sets the maximum rotation a {@link Body} can have in one time step.
	 * <p>
	 * Valid values are in the range [0, &infin;] radians
	 * @param maxRotation the maximum rotation
	 * @throws IllegalArgumentException if maxRotation is less than zero
	 */
	public void setMaxRotation(double maxRotation) {
		if (maxRotation < 0) throw new IllegalArgumentException("The max rotation cannot be negative.");
		this.maxRotation = maxRotation;
		this.maxRotationSquared = maxRotation * maxRotation;
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
	 * Returns the sleep velocity.
	 * @return double the sleep velocity.
	 * @see #setSleepVelocity(double)
	 */
	public double getSleepVelocity() {
		return this.sleepVelocity;
	}
	
	/**
	 * Returns the sleep velocity squared.
	 * @see #getSleepVelocity()
	 * @see #setSleepVelocity(double)
	 * @return double
	 */
	public double getSleepVelocitySquared() {
		return this.sleepVelocitySquared;
	}

	/**
	 * Sets the sleep velocity.
	 * <p>
	 * The sleep velocity is the maximum velocity a {@link Body} can have
	 * to be put to sleep.
	 * <p>
	 * Valid values are in the range [0, &infin;] meters/second
	 * @param sleepVelocity the sleep velocity
	 * @throws IllegalArgumentException if sleepVelocity is less than zero
	 */
	public void setSleepVelocity(double sleepVelocity) {
		if (sleepVelocity < 0) throw new IllegalArgumentException("The sleep velocity cannot be negative.");
		this.sleepVelocity = sleepVelocity;
		this.sleepVelocitySquared = sleepVelocity * sleepVelocity;
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
		if (sleepAngularVelocity < 0) throw new IllegalArgumentException("The sleep angular velocity cannot be negative.");
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
		if (sleepTime < 0) throw new IllegalArgumentException("The sleep time cannot be negative.");
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
	 * Valid values are in the range [5, &infin;]
	 * @param velocityConstraintSolverIterations the number of iterations used to solve velocity constraints
	 * @throws IllegalArgumentException if velocityConstraintSolverIterations is less than 5
	 */
	public void setVelocityConstraintSolverIterations(int velocityConstraintSolverIterations) {
		if (velocityConstraintSolverIterations < 5) throw new IllegalArgumentException("The minimum number of iterations is 5.");
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
	 * Valid values are in the range [5, &infin;]
	 * @param positionConstraintSolverIterations the number of iterations used to solve position constraints
	 * @throws IllegalArgumentException if positionConstraintSolverIterations is less than 5
	 */
	public void setPositionConstraintSolverIterations(int positionConstraintSolverIterations) {
		if (positionConstraintSolverIterations < 5) throw new IllegalArgumentException("The minimum number of iterations is 5.");
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
		if (warmStartDistance < 0) throw new IllegalArgumentException("The warm start distance cannot be negative.");
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
		if (restitutionVelocity < 0) throw new IllegalArgumentException("The restitution velocity cannot be negative.");
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
		if (linearTolerance < 0) throw new IllegalArgumentException("The linear tolerance cannot be negative.");
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
		if (angularTolerance < 0) throw new IllegalArgumentException("The angular tolerance cannot be negative.");
		this.angularTolerance = angularTolerance;
		this.angularToleranceSquared = angularTolerance * angularTolerance;
	}
	
	/**
	 * Returns the maximum linear correction.
	 * @return double the maximum linear correction
	 * @see #setMaxLinearCorrection(double)
	 */
	public double getMaxLinearCorrection() {
		return this.maxLinearCorrection;
	}
	
	/**
	 * Returns the maximum linear correction squared.
	 * @see #getMaxLinearCorrection()
	 * @see #setMaxLinearCorrection(double)
	 * @return double
	 */
	public double getMaxLinearCorrectionSquared() {
		return maxLinearCorrectionSquared;
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
	 * @param maxLinearCorrection the maximum linear correction
	 * @throws IllegalArgumentException if maxLinearCorrection is less than zero
	 */
	public void setMaxLinearCorrection(double maxLinearCorrection) {
		if (maxLinearCorrection < 0) throw new IllegalArgumentException("The maximum linear correction cannot be negative.");
		this.maxLinearCorrection = maxLinearCorrection;
		this.maxLinearCorrectionSquared = maxLinearCorrection * maxLinearCorrection;
	}
	
	/**
	 * Returns the maximum angular correction.
	 * @see #setMaxAngularCorrection(double)
	 * @return double
	 */
	public double getMaxAngularCorrection() {
		return maxAngularCorrection;
	}
	
	/**
	 * Returns the maximum angular correction squared.
	 * @see #getMaxAngularCorrection()
	 * @see #setMaxAngularCorrection(double)
	 * @return double
	 */
	public double getMaxAngularCorrectionSquared() {
		return maxAngularCorrectionSquared;
	}
	
	/**
	 * Sets the maximum angular correction.
	 * <p>
	 * This is used to prevent large angular corrections.
	 * <p>
	 * Valid values are in the range [0, &infin;] radians
	 * @param maxAngularCorrection the maximum angular correction
	 * @throws IllegalArgumentException if maxAngularCorrection is less than zero
	 */
	public void setMaxAngularCorrection(double maxAngularCorrection) {
		if (maxAngularCorrection < 0) throw new IllegalArgumentException("The maximum angular correction cannot be negative.");
		this.maxAngularCorrection = maxAngularCorrection;
		this.maxAngularCorrectionSquared = maxAngularCorrection * maxAngularCorrection;
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
		if (baumgarte < 0) throw new IllegalArgumentException("The baumgarte factor cannot be negative.");
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
		if (mode == null) throw new NullPointerException("The continuous collision detection mode cannot be null.");
		// set the mode
		this.continuousDetectionMode = mode;
	}
	
	/**
	 * Returns true if multithreading is enabled.
	 * @return boolean
	 * @since 2.1.0
	 */
	public boolean isMultithreadingEnabled() {
		return this.multithreadingEnabled;
	}
	
	/**
	 * Sets the multithreading enabled flag.
	 * @param flag true if multithreading should be enabled
	 * @since 2.1.0
	 */
	public void setMultithreadingEnabled(boolean flag) {
		this.multithreadingEnabled = flag;
	}
	
	/**
	 * Returns the multithreading load factor.
	 * @return int the load factor
	 * @since 2.1.0
	 */
	public int getLoadFactor() {
		return this.loadFactor;
	}
	
	/**
	 * Sets the multithreading load factor.
	 * <p>
	 * Higher values decrease the load per task and increase the number of tasks whereas
	 * lower values increase the load per task and decrease the number of tasks.
	 * @param loadFactor the load factor in the sequence 2<sup>1</sup>, 2<sup>2</sup>, ... , 2<sup>n</sup>
	 * @throws IllegalArgumentException if loadFactor is not a power of two integer greater than zero
	 * @since 2.1.0
	 */
	public void setLoadFactor(int loadFactor) {
		// check for greater than zero
		if (loadFactor > 0) {
			// check for power of 2
			if ((loadFactor & (loadFactor - 1)) == 0) {
				this.loadFactor = loadFactor;
			} else {
				throw new IllegalArgumentException("The load factor must be a power of two.");
			}
		} else {
			throw new IllegalArgumentException("The load factor must be greater than zero.");
		}
	}
}
