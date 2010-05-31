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

import org.dyn4j.game2d.dynamics.contact.ContactConstraintSolver;

/**
 * Responsible for housing all of the dynamics engine's settings.
 * <p>
 * Attempting to set any setting to a value outside the range in effects sets
 * the setting to the min or max of the range.
 * @author William Bittle
 */
public class Settings {
	/** The default step frequency of the dynamics engine; in seconds */
	public static final double DEFAULT_STEP_FREQUENCY = 1.0 / 60.0;
	
	/** The default maximum velocity a {@link Body} can have; in meters/second */
	public static final double DEFAULT_MAX_VELOCITY = 200.0;
	
	/** The default maximum angular velocity a {@link Body} can have; in radians/second */
	public static final double DEFAULT_MAX_ANGULAR_VELOCITY = Math.toRadians(250.0);
	
	/** The default maximum velocity for a {@link Body} to go to sleep; in meters/second */
	public static final double DEFAULT_SLEEP_VELOCITY = 0.01;
	
	/** The default maximum angular velocity for a {@link Body} to go to sleep; in radians/second */
	public static final double DEFAULT_SLEEP_ANGULAR_VELOCITY = Math.toRadians(2.0);
	
	/** The default required time a {@link Body} must maintain small motion so that its put to sleep; in seconds */
	public static final double DEFAULT_SLEEP_TIME = 0.5;

	/** The default number of SI solver iterations */
	public static final int DEFAULT_SI_SOLVER_ITERATIONS = 10;

	/** The default warm starting distance; in meters<sup>2</sup> */
	public static final double DEFAULT_WARM_START_DISTANCE = 1.0e-2;
	
	/** The default restitution velocity; in meters/second */
	public static final double DEFAULT_RESTITUTION_VELOCITY = 1.0;
	
	/** The default linear tolerance; in meters */
	public static final double DEFAULT_LINEAR_TOLERANCE = 0.005;

	/** The default maximum linear correction; in meters */
	public static final double DEFAULT_MAX_LINEAR_CORRECTION = 0.2;
	
	/** The default baumgarte */
	public static final double DEFAULT_BAUMGARTE = 0.2;

	/** The step frequency of the dynamics engine */
	private double stepFequency = Settings.DEFAULT_STEP_FREQUENCY;
	
	/** The maximum velocity a {@link Body} can have */
	private double maxVelocity = Settings.DEFAULT_MAX_VELOCITY;
	
	/** The maximum angular velocity a {@link Body} can have */
	private double maxAngularVelocity = Settings.DEFAULT_MAX_ANGULAR_VELOCITY;

	/** Whether on an engine level {@link Body}s can sleep */
	private boolean sleep = true;
	
	/** The maximum velocity before a {@link Body} is considered to sleep */
	private double sleepVelocity = Settings.DEFAULT_SLEEP_VELOCITY;
	
	/** The maximum angular velocity before a {@link Body} is considered to sleep */
	private double sleepAngularVelocity = Settings.DEFAULT_SLEEP_ANGULAR_VELOCITY;
	
	/** The time required for a {@link Body} to stay motionless before going to sleep */
	private double sleepTime = Settings.DEFAULT_SLEEP_TIME;
	
	/** The number of SI solver iterations */
	private int siSolverIterations = Settings.DEFAULT_SI_SOLVER_ITERATIONS;
	
	/** The warm start distance */
	private double warmStartDistance = Settings.DEFAULT_WARM_START_DISTANCE;
	
	/** The restitution velocity */
	private double restitutionVelocity = Settings.DEFAULT_RESTITUTION_VELOCITY;
	
	/** The allowed penetration */
	private double linearTolerance = Settings.DEFAULT_LINEAR_TOLERANCE;

	/** The maximum linear correction */
	private double maxLinearCorrection = Settings.DEFAULT_MAX_LINEAR_CORRECTION;
	
	/** The baumgarte factor */
	private double baumgarte = Settings.DEFAULT_BAUMGARTE;

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
		.append(this.maxVelocity).append("|")
		.append(this.maxAngularVelocity).append("|")
		.append(this.sleep).append("|")
		.append(this.sleepVelocity).append("|")
		.append(this.sleepAngularVelocity).append("|")
		.append(this.sleepTime).append("|")
		.append(this.siSolverIterations).append("|")
		.append(this.warmStartDistance).append("|")
		.append(this.restitutionVelocity).append("|")
		.append(this.linearTolerance).append("|")
		.append(this.maxLinearCorrection).append("|")
		.append(this.baumgarte).append("]");
		return sb.toString();
	}
	
	/**
	 * Resets the settings back to defaults.
	 */
	public void reset() {
		this.stepFequency = Settings.DEFAULT_STEP_FREQUENCY;
		this.maxVelocity = Settings.DEFAULT_MAX_VELOCITY;
		this.maxAngularVelocity = Settings.DEFAULT_MAX_ANGULAR_VELOCITY;
		this.sleep = true;
		this.sleepVelocity = Settings.DEFAULT_SLEEP_VELOCITY;
		this.sleepAngularVelocity = Settings.DEFAULT_SLEEP_ANGULAR_VELOCITY;
		this.sleepTime = Settings.DEFAULT_SLEEP_TIME;
		this.siSolverIterations = Settings.DEFAULT_SI_SOLVER_ITERATIONS;
		this.warmStartDistance = Settings.DEFAULT_WARM_START_DISTANCE;
		this.restitutionVelocity = Settings.DEFAULT_RESTITUTION_VELOCITY;
		this.linearTolerance = Settings.DEFAULT_LINEAR_TOLERANCE;
		this.maxLinearCorrection = Settings.DEFAULT_MAX_LINEAR_CORRECTION;
		this.baumgarte = Settings.DEFAULT_BAUMGARTE;
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
	 */
	public void setStepFrequency(double stepFrequency) {
		if (stepFrequency < 30.0) {
			this.stepFequency = 1.0 / 30.0;
		} else {
			this.stepFequency = 1.0 / stepFrequency;
		}
	}
	
	/**
	 * Returns the maximum velocity a {@link Body} can have.
	 * @return double the maximum velocity
	 * @see #setMaxVelocity(double)
	 */
	public double getMaxVelocity() {
		return this.maxVelocity;
	}

	/**
	 * Sets the maximum velocity a {@link Body} can have.
	 * <p>
	 * Valid values are in the range [0, &infin;] meters/second
	 * @param maxVelocity the maximum velocity
	 */
	public void setMaxVelocity(double maxVelocity) {
		if (maxVelocity < 0) {
			this.maxVelocity = 0;
		} else {
			this.maxVelocity = maxVelocity;
		}
	}

	/**
	 * Returns the maximum angular velocity a {@link Body} can have.
	 * @return double the maximum angular velocity
	 * @see #setMaxAngularVelocity(double)
	 */
	public double getMaxAngularVelocity() {
		return this.maxAngularVelocity;
	}
	
	/**
	 * Sets the maximum angular velocity a {@link Body} can have.
	 * <p>
	 * Valid values are in the range [0, &infin;] radians/second
	 * @param maxAngularVelocity the maximum angular velocity
	 */
	public void setMaxAngularVelocity(double maxAngularVelocity) {
		if (maxAngularVelocity < 0) {
			this.maxAngularVelocity = 0;
		} else {
			this.maxAngularVelocity = maxAngularVelocity;
		}
	}

	/**
	 * Returns true if the engine allows sleeping {@link Body}s.
	 * @return boolean true if allowed
	 */
	public boolean canSleep() {
		return this.sleep;
	}
	
	/**
	 * Sets whether the engine allows sleeping {@link Body}s.
	 * @param flag true if allowed
	 */
	public void setSleep(boolean flag) {
		this.sleep = flag;
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
	 * Sets the sleep velocity.
	 * <p>
	 * The sleep velocity is the maximum velocity a {@link Body} can have
	 * to be put to sleep.
	 * <p>
	 * Valid values are in the range [0, &infin;] meters/second
	 * @param sleepVelocity the sleep velocity
	 */
	public void setSleepVelocity(double sleepVelocity) {
		if (sleepVelocity < 0) {
			this.sleepVelocity = 0;
		} else {
			this.sleepVelocity = sleepVelocity;
		}
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
	 * Sets the sleep angular velocity.
	 * <p>
	 * The sleep angular velocity is the maximum angular velocity a {@link Body} can have
	 * to be put to sleep.
	 * <p>
	 * Valid values are in the range [0, &infin;] radians/second
	 * @param sleepAngularVelocity the sleep angular velocity
	 */
	public void setSleepAngularVelocity(double sleepAngularVelocity) {
		if (sleepAngularVelocity < 0) {
			this.sleepAngularVelocity = 0;
		} else {
			this.sleepAngularVelocity = sleepAngularVelocity;
		}
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
	 */
	public void setSleepTime(double sleepTime) {
		if (sleepTime < 0) {
			this.sleepTime = 0;
		} else {
			this.sleepTime = sleepTime;
		}
	}

	/**
	 * Returns the number of SI solver iterations.
	 * @return int the number of solver iterations
	 * @see #setSiSolverIterations(int)
	 */
	public int getSiSolverIterations() {
		return this.siSolverIterations;
	}

	/**
	 * Sets the number of SI solver iterations.
	 * <p>
	 * The number of SI solver iterations is the number that controls the 
	 * accuracy of the {@link ContactConstraintSolver}.  The higher the number
	 * the more accurate the solution.
	 * <p>
	 * Valid values are in the range [5, &infin;]
	 * @param siSolverIterations the number of SI solver iterations
	 */
	public void setSiSolverIterations(int siSolverIterations) {
		if (siSolverIterations < 5) {
			this.siSolverIterations = 5;
		} else {
			this.siSolverIterations = siSolverIterations;
		}
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
	 * Sets the warm start distance.
	 * <p>
	 * The maximum distance from one point to another to consider the points to be the
	 * same.  This distance is used to determine if the points can carry over another
	 * points accumulated impulses to be used for warm starting the {@link ContactConstraintSolver}.
	 * <p>
	 * Valid values are in the range [0, &infin;] meters<sup>2</sup>
	 * @param warmStartDistance the warm start distance
	 */
	public void setWarmStartDistance(double warmStartDistance) {
		if (warmStartDistance < 0) {
			this.warmStartDistance = 0;
		} else {
			this.warmStartDistance = warmStartDistance;
		}
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
	 * Sets the restitution velocity.
	 * <p>
	 * The relative velocity in the direction of the contact normal which determines
	 * whether to handle the collision as an inelastic or elastic collision.
	 * <p>
	 * Valid values are in the range [0, &infin;] meters/second
	 * @param restitutionVelocity the restitution velocity
	 */
	public void setRestitutionVelocity(double restitutionVelocity) {
		if (restitutionVelocity < 0) {
			this.restitutionVelocity = 0;
		} else {
			this.restitutionVelocity = restitutionVelocity;
		}
	}

	/**
	 * Returns the allowed penetration.
	 * @return double the allowed penetration
	 * @see #setLinearTolerance(double)
	 */
	public double getLinearTolerance() {
		return this.linearTolerance;
	}

	/**
	 * Sets the allowed penetration.
	 * <p>
	 * The maximum allowed penetration of objects to avoid jitter and facilitate stacking.
	 * <p>
	 * Valid values are in the range (0, &infin;] meters
	 * @param allowedPenetration the allowed penetration
	 */
	public void setLinearTolerance(double allowedPenetration) {
		if (allowedPenetration < 0) {
			this.linearTolerance = 0;
		} else {
			this.linearTolerance = allowedPenetration;
		}
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
	 * Sets the maximum linear correction.
	 * <p>
	 * The maximum linear correction used when estimating the current penetration depth
	 * during the position constraint solving step of the {@link ContactConstraintSolver}.
	 * <p>
	 * This is used to avoid large corrections.
	 * <p>
	 * Valid values are in the range (0, &infin;] meters
	 * @param maxLinearCorrection the maximum linear correction
	 */
	public void setMaxLinearCorrection(double maxLinearCorrection) {
		if (maxLinearCorrection < 0) {
			this.maxLinearCorrection = 0;
		} else {
			this.maxLinearCorrection = maxLinearCorrection;
		}
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
	 * The position correction bias factor.
	 * <p>
	 * Valid values are in the range [0, &infin;].
	 * @param baumgarte the baumgarte factor
	 */
	public void setBaumgarte(double baumgarte) {
		if (baumgarte < 0) {
			this.baumgarte = 0;
		} else {
			this.baumgarte = baumgarte;
		}
	}
}
