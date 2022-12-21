/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.dynamics.joint;

import org.dyn4j.DataContainer;
import org.dyn4j.Epsilon;
import org.dyn4j.Ownable;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.exception.ArgumentNullException;
import org.dyn4j.exception.ValueOutOfRangeException;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * Implementation of a fixed length distance joint with optional, spring-damper
 * and limits.
 * <p>
 * Given the two world space anchor points a distance is computed and used to 
 * constrain the attached {@link PhysicsBody}s at that distance.  The bodies 
 * can rotate freely about the anchor points and the whole system can move and
 * rotate freely, but the distance between the two anchor points is fixed.  
 * The rest distance determines the fixed distance and can be changed using 
 * {@link #setRestDistance(double)}.  The rest distance must be zero or 
 * greater.
 * <p>
 * This joint doubles as a spring/damper distance joint where the length can
 * change but is constantly approaching the rest distance.  Enable the spring-
 * damper by setting the frequency and damping ratio values and using the 
 * {@link #setSpringEnabled(boolean)} and 
 * {@link #setSpringDamperEnabled(boolean)} methods.  A good starting point is
 * a frequency of 8.0 and damping ratio of 0.3 then adjust as necessary.  You
 * can also impose a maximum force the spring will apply by using 
 * {@link #setMaximumSpringForce(double)}. The maximum force needs to be 
 * enabled using {@link #setMaximumSpringForceEnabled(boolean)} method.  As 
 * with all spring-damper enabled joints, the spring must be enabled for the 
 * damper to be applied.  Also note that when the damper is disabled, the 
 * spring still experiences "damping" aka energy loss - this is a side effect
 * of the solver and intended behavior.
 * <p>
 * This joint also supports limits.  The joint will only accept positive limits
 * (in contrast to other joints).  A lower limit of zero (the minimum valid 
 * lower limit) means that the distance can reach zero, where the anchor points
 * overlap.  Setting the limits to the same value will create a fixed length 
 * distance joint, but this is not recommended as you will get a less stable 
 * result - instead disable the limits and set the rest distance using the
 * {@link #setRestDistance(double)} method.  You can enable the upper and lower
 * limits independently using the {@link #setLowerLimitEnabled(boolean)} and 
 * {@link #setUpperLimitEnabled(boolean)} methods.  When limits are enabled, 
 * the rest distance is ignored.  You can combine limits and spring-damper by
 * enabling as mentioned earlier.  In this case, the rest distance is used for
 * the spring-damper and the limits control the maximum and minimum extension 
 * of the spring.  The limit interval does not have to include the rest 
 * distance.  If the lower and upper limits are equal or near equal (2 times
 * the {@link Settings#getLinearTolerance()}) the joint will be treated as a
 * fixed length joint.
 * @author William Bittle
 * @version 5.0.0 
 * @since 1.0.0
 * @see <a href="https://www.dyn4j.org/pages/joints#Distance_Joint" target="_blank">Documentation</a>
 * @see <a href="https://www.dyn4j.org/2010/09/distance-constraint/" target="_blank">Distance Constraint</a>
 * @param <T> the {@link PhysicsBody} type
 */
public class DistanceJoint<T extends PhysicsBody> extends AbstractPairedBodyJoint<T> implements LinearLimitsJoint, LinearSpringJoint, PairedBodyJoint<T>, Joint<T>, Shiftable, DataContainer, Ownable {
	/** The local anchor point on the first {@link PhysicsBody} */
	protected final Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link PhysicsBody} */
	protected final Vector2 localAnchor2;
	
	// distance constraint
	
	/** The rest distance */
	protected double restDistance;
	
	// spring-damper constraint
	
	/** True if the spring is enabled */
	protected boolean springEnabled;
	
	/** The current spring mode */
	protected int springMode;
	
	/** The oscillation frequency in hz */
	protected double springFrequency;

	/** The stiffness (k) of the spring */
	protected double springStiffness;
	
	/** True if the spring's damper is enabled */
	protected boolean springDamperEnabled;
	
	/** The damping ratio */
	protected double springDampingRatio;
	
	/** True if the spring maximum force is enabled */
	protected boolean springMaximumForceEnabled;
	
	/** The maximum force the spring can apply */
	protected double springMaximumForce;
	
	// limit constraints
	
	/** The maximum distance between the two world space anchor points */
	protected double upperLimit;
	
	/** The minimum distance between the two world space anchor points */
	protected double lowerLimit;
	
	/** Whether the maximum distance is enabled */
	protected boolean upperLimitEnabled;
	
	/** Whether the minimum distance is enabled */
	protected boolean lowerLimitEnabled;
	
	// current state

	/** The current distance as of constraint initialization */
	private double currentDistance;

	/** The damping coefficient of the spring-damper */
	private double damping;

	/** The normal */
	private Vector2 n;
	
	/** The damping portion of the constraint */
	private double gamma;

	/** The bias for adding work to the constraint (simulating a spring) */
	private double bias;

	/** The effective mass of the two body system (Kinv = J * Minv * Jtrans) */
	private double mass;
	
	/** The effective mass of the soft constraint of the two body system */
	private double softMass;
	
	// output
	
	/** The accumulated impulse from the previous time step */
	private double impulse;

	/** The accumulated upper limit impulse */
	private double upperLimitImpulse;
	
	/** The accumulated lower limit impulse */
	private double lowerLimitImpulse;
	
	/**
	 * Minimal constructor.
	 * <p>
	 * Creates a fixed distance {@link Joint} where the joined 
	 * {@link PhysicsBody}s do not participate in collision detection and
	 * resolution.
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @param anchor1 in world coordinates
	 * @param anchor2 in world coordinates
	 * @throws NullPointerException if body1, body2, anchor1, or anchor2 is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public DistanceJoint(T body1, T body2, Vector2 anchor1, Vector2 anchor2) {
		super(body1, body2);
		
		// verify the anchor points are not null
		if (anchor1 == null) 
			throw new ArgumentNullException("anchor1");
		
		if (anchor2 == null) 
			throw new ArgumentNullException("anchor2");
		
		this.collisionAllowed = false;
		// get the local anchor points
		this.localAnchor1 = body1.getLocalPoint(anchor1);
		this.localAnchor2 = body2.getLocalPoint(anchor2);
		// compute the initial distance
		this.restDistance = anchor1.distance(anchor2);
		this.currentDistance = this.restDistance;
		this.upperLimit = this.restDistance;
		this.lowerLimit = this.restDistance;
		this.upperLimitEnabled = false;
		this.lowerLimitEnabled = false;
		
		this.springMode = SPRING_MODE_FREQUENCY;
		this.springEnabled = false;
		this.springFrequency = 8.0;
		this.springStiffness = 0.0;
		this.springDamperEnabled = false;
		this.springDampingRatio = 0.3;
		this.springMaximumForceEnabled = false;
		this.springMaximumForce = 1000.0;
		
		this.damping = 0.0;
		this.n = null;
		
		this.gamma = 0.0;
		this.bias = 0.0;
		this.mass = 0.0;
		
		this.lowerLimitImpulse = 0.0;
		this.upperLimitImpulse = 0.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DistanceJoint[").append(super.toString())
		  .append("|Anchor1=").append(this.getAnchor1())
		  .append("|Anchor2=").append(this.getAnchor2())
		  .append("|Frequency=").append(this.springFrequency)
		  .append("|DampingRatio=").append(this.springDampingRatio)
		  .append("|RestDistance=").append(this.restDistance)
		  .append("|LowerLimit=").append(this.lowerLimit)
		  .append("|UpperLimit=").append(this.upperLimit)
		  .append("|LowerLimitEnabled=").append(this.lowerLimitEnabled)
		  .append("|UpperLimitEnabled=").append(this.upperLimitEnabled)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void initializeConstraints(TimeStep step, Settings settings) {
		double linearTolerance = settings.getLinearTolerance();
		
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// compute the normal
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		this.n = r1.sum(this.body1.getWorldCenter()).subtract(r2.sum(this.body2.getWorldCenter()));
		
		// get the current length
		this.currentDistance = this.n.getMagnitude();
		// check for the tolerance
		if (this.currentDistance < linearTolerance) {
			this.n.zero();
			this.upperLimitImpulse = 0.0;
			this.lowerLimitImpulse = 0.0;
			return;
		} else {
			// normalize it
			this.n.multiply(1.0 / this.currentDistance);
		}

		if (!this.upperLimitEnabled) {
			this.upperLimitImpulse = 0.0;
		}
		if (!this.lowerLimitEnabled) {
			this.lowerLimitImpulse = 0.0;
		}
		
		// compute K inverse
		double cr1n = r1.cross(this.n);
		double cr2n = r2.cross(this.n);
		double invMass = 
				invM1 + invI1 * cr1n * cr1n + 
				invM2 + invI2 * cr2n * cr2n;
		
		// check for zero before inverting
		this.mass = invMass <= Epsilon.E ? 0.0 : 1.0 / invMass;
		
		// see if we need to compute spring damping
		if (this.springEnabled) {
			// recompute spring reduced mass (m), stiffness (k), and damping (d)
			// since frequency, dampingRatio, or the masses of the joined bodies
			// could change
			this.updateSpringCoefficients();
			
			double dt = step.getDeltaTime();
			// get the current compression/extension of the spring
			double x = this.currentDistance - this.restDistance;
			
			// compute the CIM
			this.gamma = getConstraintImpulseMixing(dt, this.springStiffness, this.damping);
			
			// compute the ERP
			double erp = getErrorReductionParameter(dt, this.springStiffness, this.damping);
			
			// compute the bias
			// b = C * ERP
			this.bias = x * erp;
			
			// compute the effective mass
			invMass += this.gamma;
			this.softMass = invMass <= Epsilon.E ? 0.0 : 1.0 / invMass;
			
		} else {
			this.gamma = 0.0;
			this.bias = 0.0;
			this.damping = 0.0;
			this.softMass = this.mass;
		}
		
		if (settings.isWarmStartingEnabled()) {
			// warm start
			this.impulse *= step.getDeltaTimeRatio();
			this.upperLimitImpulse *= step.getDeltaTimeRatio();
			this.lowerLimitImpulse *= step.getDeltaTimeRatio();
			
			Vector2 J = this.n.product(this.impulse + this.lowerLimitImpulse - this.upperLimitImpulse);
			this.body1.getLinearVelocity().add(J.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * r1.cross(J));
			this.body2.getLinearVelocity().subtract(J.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * r2.cross(J));
		} else {
			this.impulse = 0.0;
			this.upperLimitImpulse = 0.0;
			this.lowerLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void solveVelocityConstraints(TimeStep step, Settings settings) {
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// compute r1 and r2
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		// compute the relative velocity
		Vector2 v1 = this.body1.getLinearVelocity().sum(r1.cross(this.body1.getAngularVelocity()));
		Vector2 v2 = this.body2.getLinearVelocity().sum(r2.cross(this.body2.getAngularVelocity()));

		double invdt = step.getInverseDeltaTime();		
		if (this.lowerLimit < this.upperLimit) {
			if (this.springEnabled) {
				// compute Jv
				double Jv = n.dot(v1.difference(v2));
				
				// compute lambda (the magnitude of the impulse)
				double stepImpulse = -this.softMass * (Jv + this.bias + this.gamma * this.impulse);
				
				// clamp to max force (if enabled)
				if (this.springMaximumForceEnabled) {
					double currentAccumulatedImpulse = this.impulse;
					double maxImpulse = step.getDeltaTime() * this.springMaximumForce;
					// clamp the accumulated impulse
					this.impulse = Interval.clamp(this.impulse + stepImpulse, -maxImpulse, maxImpulse);
					// if we clamped, then override the step impulse with the difference
					stepImpulse = this.impulse - currentAccumulatedImpulse;
				} else {
					// otherwise accumulate normally
					this.impulse += stepImpulse;
				}
				
				// apply the impulse
				Vector2 J = this.n.product(stepImpulse);
				this.body1.getLinearVelocity().add(J.product(invM1));
				this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * r1.cross(J));
				this.body2.getLinearVelocity().subtract(J.product(invM2));
				this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * r2.cross(J));
			}
			
			// upper limit (max length)
			if (this.lowerLimitEnabled) {
				double d = this.currentDistance - this.lowerLimit;
				double Jv = this.n.dot(v1.difference(v2));
				
				// compute lambda (the magnitude of the impulse)
				double stepImpulse = -this.mass * (Jv + Math.max(d, 0.0) * invdt);
				double currentAccumulatedImpulse = this.lowerLimitImpulse;
				this.lowerLimitImpulse = Math.max(0.0, this.lowerLimitImpulse + stepImpulse);
				stepImpulse = this.lowerLimitImpulse - currentAccumulatedImpulse;
				
				// apply the impulse
				Vector2 J = this.n.product(stepImpulse);
				this.body1.getLinearVelocity().add(J.product(invM1));
				this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * r1.cross(J));
				this.body2.getLinearVelocity().subtract(J.product(invM2));
				this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * r2.cross(J));
			}
			
			// lower limit (min length)
			if (this.upperLimitEnabled) {
				double d = this.upperLimit - this.currentDistance;
				double Jv = this.n.dot(v2.difference(v1));
				
				// compute lambda (the magnitude of the impulse)
				double stepImpulse = -this.mass * (Jv + Math.max(d, 0.0) * invdt);
				double currentAccumulatedImpulse = this.upperLimitImpulse;
				this.upperLimitImpulse = Math.max(0.0, this.upperLimitImpulse + stepImpulse);
				stepImpulse = this.upperLimitImpulse - currentAccumulatedImpulse;
				
				// apply the impulse
				Vector2 J = this.n.product(stepImpulse);
				this.body1.getLinearVelocity().subtract(J.product(invM1));
				this.body1.setAngularVelocity(this.body1.getAngularVelocity() - invI1 * r1.cross(J));
				this.body2.getLinearVelocity().add(J.product(invM2));
				this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * r2.cross(J));
			}
		} else {
			// compute Jv
			double Jv = n.dot(v1.difference(v2));
			
			// compute lambda (the magnitude of the impulse)
			double stepImpulse = -this.softMass * (Jv + this.bias + this.gamma * this.impulse);
			
			// clamp to max force (if enabled)
			if (this.springEnabled && this.springMaximumForceEnabled) {
				double currentAccumulatedImpulse = this.impulse;
				double maxImpulse = step.getDeltaTime() * this.springMaximumForce;
				// clamp the accumulated impulse
				this.impulse = Interval.clamp(this.impulse + stepImpulse, -maxImpulse, maxImpulse);
				// if we clamped, then override the step impulse with the difference
				stepImpulse = this.impulse - currentAccumulatedImpulse;
			} else {
				// otherwise accumulate normally
				this.impulse += stepImpulse;
			}
			
			// apply the impulse
			Vector2 J = this.n.product(stepImpulse);
			this.body1.getLinearVelocity().add(J.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * r1.cross(J));
			this.body2.getLinearVelocity().subtract(J.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * r2.cross(J));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(TimeStep step, Settings settings) {
		double linearTolerance = settings.getLinearTolerance();
		
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		Vector2 c1 = this.body1.getWorldCenter();
		Vector2 c2 = this.body2.getWorldCenter();
		
		// recompute n since it may have changed after integration
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		this.n = r1.sum(this.body1.getWorldCenter()).subtract(r2.sum(this.body2.getWorldCenter()));
		
		double l = this.n.normalize();
		double C = 0.0;
		
		if (this.upperLimitEnabled && this.lowerLimitEnabled && Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * linearTolerance) {
			// upper and lower limits enabled, but the same value
			// treat it like a fixed length joint
			C = l - this.lowerLimit;
		} else if (this.lowerLimitEnabled && l < this.lowerLimit) {
			// lower limit only
			C = l - this.lowerLimit;
		} else if (this.upperLimitEnabled && l > this.upperLimit) {
			// upper limit only
			C = l - this.upperLimit;
		} else if (!this.upperLimitEnabled && !this.lowerLimitEnabled && !this.springEnabled) {
			// fixed length joint (no spring and no limits)
			C = l - this.restDistance;
		} else {
			// no limits, or not outside the limits, or spring joint
			return true;
		}

		double impulse = -this.mass * C;
		
		Vector2 J = this.n.product(impulse);
		
		// translate and rotate the objects
		this.body1.translate(J.product(invM1));
		this.body1.rotate(invI1 * r1.cross(J), c1);
		
		this.body2.translate(J.product(-invM2));
		this.body2.rotate(-invI2 * r2.cross(J), c2);
		
		return Math.abs(C) < linearTolerance;
	}
	
	/**
	 * Computes the spring coefficients from the current state of the joint.
	 * <p>
	 * This method is intended to set the springStiffness OR springFrequency and
	 * damping for use during constraint solving.
	 */
	protected void updateSpringCoefficients() {
		double lm = this.getReducedMass();
		double nf = 0.0;
		
		if (this.springMode == SPRING_MODE_FREQUENCY) {
			// compute the stiffness based on the frequency
			nf = getNaturalFrequency(this.springFrequency);
			this.springStiffness = getSpringStiffness(lm, nf);
		} else if (this.springMode == SPRING_MODE_STIFFNESS) {
			// compute the frequency based on the stiffness
			nf = getNaturalFrequency(this.springStiffness, lm);
			this.springFrequency = getFrequency(nf);
		}
		
		if (this.springDamperEnabled) {
			this.damping = getSpringDampingCoefficient(lm, nf, this.springDampingRatio);
		} else {
			this.damping = 0.0;
		}
	}
	
	/**
	 * Returns the world-space anchor point on the first body.
	 * @return {@link Vector2}
	 */
	public Vector2 getAnchor1() {
		return body1.getWorldPoint(this.localAnchor1);
	}
	
	/**
	 * Returns the world-space anchor point on the second body.
	 * @return {@link Vector2}
	 */
	public Vector2 getAnchor2() {
		return body2.getWorldPoint(this.localAnchor2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public Vector2 getReactionForce(double invdt) {
		return this.n.product((this.impulse + this.lowerLimitImpulse - this.upperLimitImpulse) * invdt);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Not applicable to this joint. Always returns zero.
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return 0.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		// nothing to translate here since the anchor points are in local coordinates
		// they will move with the bodies
	}
	
	/**
	 * Returns the rest distance between the two constrained {@link PhysicsBody}s in meters.
	 * @return double
	 * @since 4.2.0
	 */
	public double getRestDistance() {
		return this.restDistance;
	}
	
	/**
	 * Sets the rest distance between the two constrained {@link PhysicsBody}s in meters.
	 * @param distance the distance in meters
	 * @throws IllegalArgumentException if distance is less than zero
	 * @since 4.2.0
	 */
	public void setRestDistance(double distance) {
		// make sure the distance is greater than zero
		if (distance < 0.0) 
			throw new ValueOutOfRangeException("distance", distance, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		if (this.restDistance != distance) {
			// wake up both bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the new target distance
			this.restDistance = distance;
		}
	}

	/**
	 * Returns the current distance between the anchor points.
	 * @return double
	 * @since 4.2.0
	 */
	public double getCurrentDistance() {
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		Vector2 p1 = t1.getTransformed(this.localAnchor1);
		Vector2 p2 = t2.getTransformed(this.localAnchor2);
		return p1.distance(p2);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringDampingRatio()
	 */
	@Override
	public double getSpringDampingRatio() {
		return this.springDampingRatio;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringDampingRatio(double)
	 */
	@Override
	public void setSpringDampingRatio(double dampingRatio) {
		// make sure its within range
		if (dampingRatio <= 0.0) 
			throw new ValueOutOfRangeException("dampingRatio", dampingRatio, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		if (dampingRatio > 1.0) 
			throw new ValueOutOfRangeException("dampingRatio", dampingRatio, ValueOutOfRangeException.MUST_BE_LESS_THAN_OR_EQUAL_TO, 1.0);
		
		// did it change?
		if (this.springDampingRatio != dampingRatio) {
			// set the damping ratio
			this.springDampingRatio = dampingRatio;
			// only wake if the damper would be applied
			if (this.springEnabled && this.springDamperEnabled) {
				// wake the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringFrequency()
	 */
	@Override
	public double getSpringFrequency() {
		return this.springFrequency;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringStiffness()
	 */
	@Override
	public double getSpringStiffness() {
		return this.springStiffness;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringFrequency(double)
	 */
	@Override
	public void setSpringFrequency(double frequency) {
		// check for valid value
		if (frequency <= 0) 
			throw new ValueOutOfRangeException("frequency", frequency, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		// set the spring mode
		this.springMode = SPRING_MODE_FREQUENCY;
		// check for change
		if (this.springFrequency != frequency) {
			// make the change
			this.springFrequency = frequency;
			// check if the spring is enabled
			if (this.springEnabled) {
				// wake the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringStiffness(double)
	 */
	@Override
	public void setSpringStiffness(double stiffness) {
		// check for valid value
		if (stiffness <= 0) 
			throw new ValueOutOfRangeException("stiffness", stiffness, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		// set the spring mode
		this.springMode = SPRING_MODE_STIFFNESS;
		// only update if necessary
		if (this.springStiffness != stiffness) {
			this.springStiffness = stiffness;
			// wake up the bodies
			if (this.springEnabled) {
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringMaximumForce()
	 */
	@Override
	public double getMaximumSpringForce() {
		return this.springMaximumForce;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringMaximumForce(double)
	 */
	@Override
	public void setMaximumSpringForce(double maximum) {
		// check for valid value
		if (maximum <= 0) 
			throw new ValueOutOfRangeException("maximum", maximum, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		// check if changed
		if (this.springMaximumForce != maximum) {
			this.springMaximumForce = maximum;
			// wake up the bodies
			if (this.springEnabled && this.springMaximumForceEnabled) {
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#isSpringMaximumForceEnabled()
	 */
	@Override
	public boolean isMaximumSpringForceEnabled() {
		return this.springMaximumForceEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringMaximumForceEnabled(boolean)
	 */
	@Override
	public void setMaximumSpringForceEnabled(boolean enabled) {
		if (this.springMaximumForceEnabled != enabled) {
			this.springMaximumForceEnabled = enabled;
			
			if (this.springEnabled) {
				// wake the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#isSpringEnabled()
	 */
	public boolean isSpringEnabled() {
		return this.springEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringEnabled(boolean)
	 */
	@Override
	public void setSpringEnabled(boolean enabled) {
		if (this.springEnabled != enabled) {
			// update the flag
			this.springEnabled = enabled;
			// wake the bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#isSpringDamperEnabled()
	 */
	public boolean isSpringDamperEnabled() {
		return this.springDamperEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#setSpringDamperEnabled(boolean)
	 */
	@Override
	public void setSpringDamperEnabled(boolean enabled) {
		if (this.springDamperEnabled != enabled) {
			// update the flag
			this.springDamperEnabled = enabled;
			
			if (this.springEnabled) {
				// wake the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringForce(double)
	 */
	@Override
	public double getSpringForce(double invdt) {
		if (this.springEnabled) {
			return this.impulse * invdt;
		}
		return 0.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringMode()
	 */
	@Override
	public int getSpringMode() {
		return this.springMode;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearLimitsJoint#getUpperLimit()
	 */
	public double getUpperLimit() {
		return this.upperLimit;
	}
	
	/**
	 * {@inheritDoc}
	 * @param upperLimit the upper limit in meters; must be zero or greater
	 * @throws IllegalArgumentException if upperLimit is less than zero or upperLimit is less than the current lower limit
	 */
	public void setUpperLimit(double upperLimit) {
		// make sure the distance is greater than zero
		if (upperLimit < 0.0) 
			throw new ValueOutOfRangeException("upperLimit", upperLimit, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		// make sure the minimum is less than or equal to the maximum
		if (upperLimit < this.lowerLimit) 
			throw new ValueOutOfRangeException("upperLimit", upperLimit, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, "lowerLimit", this.lowerLimit);
		
		if (this.upperLimit != upperLimit) {
			// make sure its changed and enabled before waking the bodies
			if (this.upperLimitEnabled) {
				// wake up both bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new target distance
			this.upperLimit = upperLimit;
			// clear the accumulated impulse
			this.upperLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearLimitsJoint#setUpperLimitEnabled(boolean)
	 */
	public void setUpperLimitEnabled(boolean flag) {
		if (this.upperLimitEnabled != flag) {
			// wake up both bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the flag
			this.upperLimitEnabled = flag;
			// clear the accumulated impulse
			this.upperLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearLimitsJoint#isUpperLimitEnabled()
	 */
	public boolean isUpperLimitEnabled() {
		return this.upperLimitEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearLimitsJoint#getLowerLimit()
	 */
	public double getLowerLimit() {
		return this.lowerLimit;
	}
	
	/**
	 * {@inheritDoc}
	 * @param lowerLimit the lower limit in meters; must be zero or greater
	 * @throws IllegalArgumentException if lowerLimit is less than zero or lowerLimit is greater than the current upper limit
	 */
	public void setLowerLimit(double lowerLimit) {
		// make sure the distance is greater than zero
		if (lowerLimit < 0.0)
			throw new ValueOutOfRangeException("lowerLimit", lowerLimit, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		// make sure the minimum is less than or equal to the maximum
		if (lowerLimit > this.upperLimit) 
			throw new ValueOutOfRangeException("lowerLimit", lowerLimit, ValueOutOfRangeException.MUST_BE_LESS_THAN_OR_EQUAL_TO, "upperLimit", this.upperLimit);
		
		if (this.lowerLimit != lowerLimit) {
			// make sure its changed and enabled before waking the bodies
			if (this.lowerLimitEnabled) {
				// wake up both bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new target distance
			this.lowerLimit = lowerLimit;
			// clear the accumulated impulse
			this.lowerLimitImpulse = 0.0;
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearLimitsJoint#setLowerLimitEnabled(boolean)
	 */
	public void setLowerLimitEnabled(boolean flag) {
		if (this.lowerLimitEnabled != flag) {
			// wake up both bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the flag
			this.lowerLimitEnabled = flag;
			// clear the accumulated impulse
			this.lowerLimitImpulse = 0.0;
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearLimitsJoint#isLowerLimitEnabled()
	 */
	public boolean isLowerLimitEnabled() {
		return this.lowerLimitEnabled;
	}
	
	/**
	 * {@inheritDoc}
	 * @param lowerLimit the lower limit in meters; must be zero or greater
	 * @param upperLimit the upper limit in meters; must be zero or greater
	 * @throws IllegalArgumentException if lowerLimit is less than zero, the upperLimit is less than zero, or lowerLimit is greater than the upperLimit
	 */
	public void setLimits(double lowerLimit, double upperLimit) {
		// make sure the minimum distance is greater than zero
		if (lowerLimit < 0.0) 
			throw new ValueOutOfRangeException("lowerLimit", lowerLimit, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		// make sure the maximum distance is greater than zero
		if (upperLimit < 0.0) 
			throw new ValueOutOfRangeException("upperLimit", upperLimit, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		// make sure the min < max
		if (lowerLimit > upperLimit) 
			throw new ValueOutOfRangeException("lowerLimit", lowerLimit, ValueOutOfRangeException.MUST_BE_LESS_THAN_OR_EQUAL_TO, "upperLimit", upperLimit);
		
		if (this.lowerLimit != lowerLimit || this.upperLimit != upperLimit) {
			// make sure one of the limits is enabled and has changed before waking the bodies
			if (this.lowerLimitEnabled || this.upperLimitEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the limits
			this.upperLimit = upperLimit;
			this.lowerLimit = lowerLimit;
			// clear the accumulated impulse
			this.upperLimitImpulse = 0.0;
			this.lowerLimitImpulse = 0.0;
		}
	}

	/**
	 * {@inheritDoc}
	 * @param lowerLimit the lower limit in meters; must be zero or greater
	 * @param upperLimit the upper limit in meters; must be zero or greater
	 * @throws IllegalArgumentException if lowerLimit is less than zero, the upperLimit is less than zero, or lowerLimit is greater than the upperLimit
	 */
	public void setLimitsEnabled(double lowerLimit, double upperLimit) {
		// enable the limits
		this.setLimitsEnabled(true);
		// set the values
		this.setLimits(lowerLimit, upperLimit);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearLimitsJoint#setLimitsEnabled(boolean)
	 */
	public void setLimitsEnabled(boolean flag) {
		if (this.upperLimitEnabled != flag || this.lowerLimitEnabled != flag) {
			this.upperLimitEnabled = flag;
			this.lowerLimitEnabled = flag;
			// wake up the bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// clear the accumulated impulse
			this.upperLimitImpulse = 0.0;
			this.lowerLimitImpulse = 0.0;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @param limit the lower and upper limit in meters; must be zero or greater
	 * @throws IllegalArgumentException if limit is less than zero
	 */
	public void setLimits(double limit) {
		// make sure the distance is greater than zero
		if (limit < 0.0) 
			throw new ValueOutOfRangeException("limit", limit, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		if (this.lowerLimit != limit || this.upperLimit != limit) {
			// make sure one of the limits is enabled and has changed before waking the bodies
			if (this.lowerLimitEnabled || this.upperLimitEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the limits
			this.upperLimit = limit;
			this.lowerLimit = limit;
			// clear the accumulated impulse
			this.upperLimitImpulse = 0.0;
			this.lowerLimitImpulse = 0.0;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * @param limit the lower and upper limit in meters; must be zero or greater
	 * @throws IllegalArgumentException if limit is less than zero
	 */
	public void setLimitsEnabled(double limit) {
		// enable the limits
		this.setLimitsEnabled(true);
		// set the values
		this.setLimits(limit);
	}
}
