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
 * Implementation of a wheel joint.
 * <p>
 * A wheel joint is used to simulate a vehicle's wheel and suspension.  The 
 * wheel is allowed to rotate freely about the given anchor point.  The 
 * suspension is allowed to translate freely along the given axis.  The whole
 * system can translate and rotate freely.
 * <p>
 * The given axis fixed to the first body (frame) given.  So as the first body
 * rotates, the axis will rotate with it.  The given anchor point represents
 * the center of rotation of the second body (wheel), typically the wheel's 
 * world space center of mass.
 * <p>
 * By default the frequency and damping ratio are set to 8.0 and 0.0 
 * respectively.  Unlike other joints, the spring-damper for this joint is 
 * enabled by default.  You can enable/disable the various spring-damper 
 * features using {@link #setSpringEnabled(boolean)}, 
 * {@link #setSpringDamperEnabled(boolean)}, 
 * {@link #setMaximumSpringForceEnabled(boolean)} methods.  As with all spring
 * -damper enabled joints, the spring must be enabled for the damper to be
 * applied.  Also note that when the damper is disabled, the spring still 
 * experiences "damping" aka energy loss - this is a side effect of the solver
 * and intended behavior.  
 * <p>
 * This joint has an added spring feature called the rest offset.  Similar to
 * the rest distance in the distance joint, but only applied when the spring
 * is enabled.  When you build the joint, the default distance between the two
 * bodies is their initial distance.  You can use the 
 * {@link #setSpringRestOffset(double)} to raise or lower the distance between
 * the bodies when the spring is active.
 * <p>
 * This joint also supports a motor.  The motor is an angular motor about the
 * anchor point.  The motor speed can be positive or negative to indicate a
 * clockwise or counter-clockwise rotation.  The maximum motor torque must be 
 * greater than zero for the motor to apply any motion.  The motor must be
 * enabled using {@link #setMotorEnabled(boolean)}.
 * <p>
 * The joint also supports upper and lower limits. The limits represent the
 * maximum displacement from the anchor point along the given axis.  This means
 * that the limits are typically negative for the lower limit and positive for
 * the upper limit.  The limits are solved relative to the given axis's
 * direction.  The limits can be enabled separately using 
 * {@link #setLowerLimitEnabled(boolean)} and
 * {@link #setUpperLimitEnabled(boolean)}.  This also means that the limits
 * are relative to the initial starting position of the bodies.
 * <p>
 * NOTE: In versions of dyn4j before 5.0.0, the body arguments in the 
 * constructor were reversed, you would specify the wheel first, then frame. It
 * was changed to accept the frame first, then the wheel to make things more 
 * natural.
 * @author William Bittle
 * @version 5.0.0
 * @since 3.0.0
 * @see <a href="https://www.dyn4j.org/pages/joints#Wheel_Joint" target="_blank">Documentation</a>
 * @param <T> the {@link PhysicsBody} type
 */
public class WheelJoint<T extends PhysicsBody> extends AbstractPairedBodyJoint<T> implements LinearLimitsJoint, LinearSpringJoint, AngularMotorJoint, PairedBodyJoint<T>, Joint<T>, Shiftable, DataContainer, Ownable {
	/** The local anchor point on the first {@link PhysicsBody} */
	protected final Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link PhysicsBody} */
	protected final Vector2 localAnchor2;

	/** The local space x axis representing the allowed linear motion */
	protected final Vector2 xAxis;
	
	/** The local space y axis representing the constrained linear motion */
	protected final Vector2 yAxis;
	
	// limits
	
	/** True if the upper limit is enabled */
	protected boolean upperLimitEnabled;
	
	/** True if the lower limit is enabled */
	protected boolean lowerLimitEnabled;
	
	/** the upper limit in meters */
	protected double upperLimit;
	
	/** the lower limit in meters */
	protected double lowerLimit;
	
	// motor
	
	/** Whether the motor is enabled or not */
	protected boolean motorEnabled;
	
	/** The target velocity in radians / second */
	protected double motorSpeed;
	
	/** True if the motor maximum torque is enabled */
	protected boolean motorMaximumTorqueEnabled;
	
	/** The maximum torque the motor can apply in newton-meters */
	protected double motorMaximumTorque;
	
	// spring damper
	
	/** True if the spring is enabled */
	protected boolean springEnabled;
	
	/** True if the spring-damper is enabled */
	protected boolean springDamperEnabled;
	
	/** The spring mode (frequency or stiffness) */
	protected int springMode;
	
	/** The oscillation frequency in hz */
	protected double springFrequency;

	/** The stiffness of the spring */
	protected double springStiffness;
	
	/** The damping ratio */
	protected double springDampingRatio;
	
	/** True if the spring maximum force is enabled */
	protected boolean springMaximumForceEnabled;
	
	/** The maximum force the spring will apply */
	protected double springMaximumForce;

	/** The rest offset of the spring */
	protected double springRestOffset;
	
	// current state

	/** The damping coefficient */
	private double damping;
	
	/** The bias for adding work to the constraint (simulating a spring) */
	private double bias;
	
	/** The damping portion of the constraint */
	private double gamma;
	
	/** The current translation along the allowed line of motion */
	private double translation;
	
	/** The point-on-line constraint mass; K = J * Minv * Jtrans */
	private double invK;
	
	/** The mass along the axis of allowed motion */
	private double axialMass;
	
	/** The spring/damper constraint mass */
	private double springMass;
	
	/** The mass of the motor */
	private double motorMass;
	
	/** The world space yAxis from body1's transform */
	private Vector2 wyAxis;
	
	/** The world space xAxis from body1's transform */
	private Vector2 wxAxis;
	
	/** s1y = (r1 + d).cross(yaxis) */
	private double s1y;

	/** s2y = r2.cross(yaxis) */
	private double s2y;
	
	/** s1x = (r1 + d).cross(xaxis) */
	private double s1x;

	/** s2x = r2.cross(xaxis) */
	private double s2x;

	// output
	
	/** The accumulated impulse for warm starting */
	private double impulse;
	
	/** The lower limit impulse */
	private double lowerLimitImpulse;
	
	/** The upper limit impulse */
	private double upperLimitImpulse;
	
	/** The impulse applied by the spring/damper */
	private double springImpulse;
	
	/** The impulse applied by the motor */
	private double motorImpulse;
	
	/**
	 * Minimal constructor.
	 * @param frame the frame {@link PhysicsBody}
	 * @param wheel the wheel {@link PhysicsBody}
	 * @param anchor the anchor point in world coordinates
	 * @param axis the axis of allowed motion
	 * @throws NullPointerException if body1, body2, anchor, or axis is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public WheelJoint(T frame, T wheel, Vector2 anchor, Vector2 axis) {
		super(frame, wheel);
		
		// check for a null anchor
		if (anchor == null) 
			throw new ArgumentNullException("anchor");
		
		// check for a null axis
		if (axis == null) 
			throw new ArgumentNullException("axis");
		
		if (axis.getMagnitude() <= Epsilon.E) 
			throw new IllegalArgumentException("The axis cannot be a zero vector.");
		
		// set the anchor point
		this.localAnchor1 = frame.getLocalPoint(anchor);
		this.localAnchor2 = wheel.getLocalPoint(anchor);
		// make sure the axis is normalized
		Vector2 n = axis.getNormalized();
		// get the axis in local coordinates
		this.xAxis = frame.getLocalVector(n);
		// get the perpendicular axis
		this.yAxis = this.xAxis.getRightHandOrthogonalVector();
		
		this.springRestOffset = 0.0;
		
		// limits
		this.lowerLimitEnabled = false;
		this.upperLimitEnabled = false;
		this.lowerLimit = 0.0;
		this.upperLimit = 0.0;
		
		// motor
		this.motorEnabled = false;
		this.motorSpeed = 0.0;
		this.motorMaximumTorqueEnabled = false; 
		this.motorMaximumTorque = 1000.0;
		
		// spring
		this.springMode = SPRING_MODE_FREQUENCY;
		this.springEnabled = true;
		this.springDamperEnabled = true;
		this.springMaximumForce = 1000.0;
		this.springMaximumForceEnabled = false;
		this.springStiffness = 0.0;
		this.springFrequency = 8.0;
		this.springDampingRatio = 0.3;
		
		// initialize
		this.damping = 0.0;
		this.gamma = 0.0;
		this.bias = 0.0;
		
		this.translation = 0.0;
		
		this.invK = 0.0;
		this.axialMass = 0.0;
		this.motorMass = 0.0;
		this.springMass = 0.0;

		this.wyAxis = null;
		this.wxAxis = null;
		this.s1x = 0.0;
		this.s2x = 0.0;
		this.s2y = 0.0;
		this.s1y = 0.0;
		
		this.impulse = 0.0;
		this.lowerLimitImpulse = 0.0;
		this.upperLimitImpulse = 0.0;
		this.motorImpulse = 0.0;
		this.springImpulse = 0.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WheelJoint[").append(super.toString())
		  .append("|WorldAnchor=").append(this.getAnchor1())
		  .append("|Axis=").append(this.getAxis())
		  .append("|IsMotorEnabled=").append(this.motorEnabled)
		  .append("|MotorSpeed=").append(this.motorSpeed)
		  .append("|MaximumMotorTorque=").append(this.motorMaximumTorque)
		  .append("|Frequency=").append(this.springFrequency)
		  .append("|DampingRatio=").append(this.springDampingRatio)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void initializeConstraints(TimeStep step, Settings settings) {
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		// compute the vector between the two world space anchor points
		Vector2 d = this.body2.getWorldCenter().sum(r2).subtract(this.body1.getWorldCenter().sum(r1));
		
		// get the world vectors of the axes
		this.wxAxis = this.body1.getWorldVector(this.xAxis);
		this.wyAxis = this.body1.getWorldVector(this.yAxis);
		
		// s1y = (r1 + d).cross(yaxis)
		this.s1y = r1.sum(d).cross(this.wyAxis);
		// s2y = r2.cross(yaxis)
		this.s2y = r2.cross(this.wyAxis);
		this.invK = invM1 + invM2 + this.s1y * this.s1y * invI1 + this.s2y * this.s2y * invI2;
		// make sure we don't divide by zero
		if (this.invK > Epsilon.E) {
			this.invK = 1.0 / this.invK;
		} else {
			this.invK = 0.0;
		}
		
		// s1x = (r1 + d).cross(xaxis)
		this.s1x = r1.sum(d).cross(this.wxAxis);
		// s2x = r2.cross(xaxis)
		this.s2x = r2.cross(this.wxAxis);
		double invMass = invM1 + invM2 + this.s1x * this.s1x * invI1 + this.s2x * this.s2x * invI2;
		if (invMass > Epsilon.E) {
			this.axialMass = 1.0 / invMass;
		} else {
			this.axialMass = 0.0;
		}
		
		// compute the spring mass for the spring constraint
		if (this.springEnabled && invMass > 0.0) {
			// recompute spring reduced mass (m), stiffness (k), and damping (d)
			// since frequency, dampingRatio, or the masses of the joined bodies
			// could change
			this.updateSpringCoefficients();
			
			this.springMass = this.axialMass;
			
			// compute the current spring extension (we are solving for zero here)
			// the spring extension is distance between the anchor points translated into
			// world space.
			double c = d.dot(this.wxAxis) + this.springRestOffset;
			// get the delta time
			double dt = step.getDeltaTime();

			this.gamma = getConstraintImpulseMixing(dt, this.springStiffness, this.damping);
			double erp = getErrorReductionParameter(dt, this.springStiffness, this.damping);
			
			this.bias = c * erp;
			
			// compute the effective mass			
			this.springMass = invMass + this.gamma;
			// check for zero before inverting
			if (this.springMass > Epsilon.E) {
				this.springMass = 1.0 / this.springMass;
			}
		} else {
			this.springMass = 0.0;
			this.springImpulse = 0.0;
			this.gamma = 0.0;
			this.bias = 0.0;
			this.damping = 0.0;
		}
		
		if (this.lowerLimitEnabled || this.upperLimitEnabled) {
			this.translation = this.wxAxis.dot(d);
		}
		
		if (!this.lowerLimitEnabled) {
			this.lowerLimitImpulse = 0.0;
		}
		if (!this.upperLimitEnabled) {
			this.upperLimitImpulse = 0.0;
		}
		
		// check if the motor is enabled
		if (this.motorEnabled) {
			// compute the motor mass
			this.motorMass = invI1 + invI2;
			if (this.motorMass > Epsilon.E) {
				this.motorMass = 1.0 / this.motorMass;
			}
		} else {
			// clear the previous motor impulse
			this.motorMass = 0.0;
			this.motorImpulse = 0.0;
		}
		
		// warm start
		if (settings.isWarmStartingEnabled()) {
			// account for variable time step
			this.impulse *= step.getDeltaTimeRatio();
			this.springImpulse *= step.getDeltaTimeRatio();
			this.motorImpulse *= step.getDeltaTimeRatio();
			
			double axialImpulse = this.springImpulse + this.lowerLimitImpulse - this.upperLimitImpulse;
			
			// we only compute the impulse for body1 since body2's impulse is
			// just the negative of body1's impulse
			Vector2 P = new Vector2();
			// perp.product(impulse) + axis.product(springImpulse)
			P.x = this.wyAxis.x * this.impulse + axialImpulse * this.wxAxis.x;
			P.y = this.wyAxis.y * this.impulse + axialImpulse * this.wxAxis.y;
			
			double la = this.impulse * this.s1y + axialImpulse * this.s1x + this.motorImpulse;
			double lb = this.impulse * this.s2y + axialImpulse * this.s2x + this.motorImpulse;
			
			// apply the impulses
			this.body1.getLinearVelocity().subtract(P.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() - invI1 * la);
			this.body2.getLinearVelocity().add(P.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * lb);
		} else {
			this.impulse = 0.0;
			this.lowerLimitImpulse = 0.0;
			this.upperLimitImpulse = 0.0;
			this.motorImpulse = 0.0;
			this.springImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void solveVelocityConstraints(TimeStep step, Settings settings) {
		Mass m1 = this.body1.getMass();
		Mass m2 = this.body2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		Vector2 v1 = this.body1.getLinearVelocity();
		Vector2 v2 = this.body2.getLinearVelocity();
		double w1 = this.body1.getAngularVelocity();
		double w2 = this.body2.getAngularVelocity();
		
		// solve the spring constraint
		if (this.springEnabled) {
			double Cdt = this.wxAxis.dot(v2.difference(v1)) + this.s2x * w2 - this.s1x * w1;
			// compute the impulse
			double stepImpulse = -this.springMass * (Cdt + this.bias + this.gamma * this.springImpulse);
			
			// clamp to max force (if enabled)
			if (this.springMaximumForceEnabled) {
				double currentAccumulatedImpulse = this.springImpulse;
				double maxImpulse = step.getDeltaTime() * this.springMaximumForce;
				// clamp the accumulated impulse
				this.springImpulse = Interval.clamp(this.springImpulse + stepImpulse, -maxImpulse, maxImpulse);
				// if we clamped, then override the step impulse with the difference
				stepImpulse = this.springImpulse - currentAccumulatedImpulse;
			} else {
				// otherwise accumulate normally
				this.springImpulse += stepImpulse;
			}
			
			// compute the applied impulses
			// Pc = Jtrans * lambda
			Vector2 P = this.wxAxis.product(stepImpulse);
			double l1 = stepImpulse * this.s1x;
			double l2 = stepImpulse * this.s2x;
			
			v1.subtract(P.product(invM1));
			w1 -= l1 * invI1;
			v2.add(P.product(invM2));
			w2 += l2 * invI2;
		}
		
		// solve the motor constraint
		if (this.motorEnabled) {
			// compute Jv + b
			double Cdt = w2 - w1 - this.motorSpeed;
			// compute lambda = Kinv * (Jv + b)
			double stepImpulse = -this.motorMass * Cdt;
			
			// clamp the impulse between the max torque
			if (this.motorMaximumTorqueEnabled) {
				double currentAccumulatedImpulse = this.motorImpulse;
				double maxImpulse = this.motorMaximumTorque * step.getDeltaTime();
				this.motorImpulse = Interval.clamp(this.motorImpulse + stepImpulse, -maxImpulse, maxImpulse);
				stepImpulse = this.motorImpulse - currentAccumulatedImpulse;
			} else {
				this.motorImpulse += stepImpulse;
			}
			
			// apply the impulse
			w1 -= stepImpulse * invI1;
			w2 += stepImpulse * invI2;
		}
		
		// lower limit
		if (this.lowerLimitEnabled) {
			double C = this.translation - this.lowerLimit;
			double Cdot = this.wxAxis.dot(v2.difference(v1)) + this.s2x * w2 - this.s1x * w1;
			double stepImpulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * step.getInverseDeltaTime());
			
			double currentAccumulatedImpulse = this.lowerLimitImpulse;
			this.lowerLimitImpulse = Math.max(this.lowerLimitImpulse + stepImpulse, 0.0);
			stepImpulse = this.lowerLimitImpulse - currentAccumulatedImpulse;
			
			// compute the applied impulses
			// Pc = Jtrans * lambda
			Vector2 P = this.wxAxis.product(stepImpulse);
			double l1 = stepImpulse * this.s1x;
			double l2 = stepImpulse * this.s2x;
			
			v1.subtract(P.product(invM1));
			w1 -= l1 * invI1;
			v2.add(P.product(invM2));
			w2 += l2 * invI2;
		}
		
		// upper limit
		if (this.upperLimitEnabled) {
			double C = this.upperLimit - this.translation;
			double Cdot = this.wxAxis.dot(v1.difference(v2)) + this.s1x * w1 - this.s2x * w2;
			double stepImpulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * step.getInverseDeltaTime());
			
			double currentAccumulatedImpulse = this.upperLimitImpulse;
			this.upperLimitImpulse = Math.max(this.upperLimitImpulse + stepImpulse, 0.0);
			stepImpulse = this.upperLimitImpulse - currentAccumulatedImpulse;
			
			// compute the applied impulses
			// Pc = Jtrans * lambda
			Vector2 P = this.wxAxis.product(stepImpulse);
			double l1 = stepImpulse * this.s1x;
			double l2 = stepImpulse * this.s2x;
			
			v1.add(P.product(invM1));
			w1 += l1 * invI1;
			v2.subtract(P.product(invM2));
			w2 -= l2 * invI2;
		}
		
		// finally, solve the point-on-line constraint
		{
			double Cdt = this.wyAxis.dot(v2.difference(v1)) + this.s2y * w2 - this.s1y * w1;
			double stepImpulse = -this.invK * Cdt;
			// accumulate the impulse
			this.impulse += stepImpulse;

			// compute the applied impulses
			// Pc = Jtrans * lambda
			Vector2 P = this.wyAxis.product(stepImpulse);
			double l1 = stepImpulse * this.s1y;
			double l2 = stepImpulse * this.s2y;
			
			v1.subtract(P.product(invM1));
			w1 -= l1 * invI1;
			v2.add(P.product(invM2));
			w2 += l2 * invI2;
		}
		
		// finally set the velocities
		// NOTE we dont have to update v1 or v2 because they are references
		this.body1.setAngularVelocity(w1);
		this.body2.setAngularVelocity(w2);
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
		
		double linearError = 0.0;
		
		if (this.lowerLimitEnabled || this.upperLimitEnabled) {
			Vector2 c1 = this.body1.getWorldCenter();
			Vector2 c2 = this.body2.getWorldCenter();
			
			Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
			Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
			
			Vector2 d = c2.sum(r2).subtract(c1.sum(r1));
			
			Vector2 axis = this.body1.getWorldVector(this.xAxis);
			double s1x = r1.sum(d).cross(axis);
			double s2x = r2.cross(axis);
			
			double C = 0.0;
			double translation = axis.dot(d);
			
			if (this.upperLimitEnabled && this.lowerLimitEnabled && Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * linearTolerance) {
				C = translation;
			} else if (this.lowerLimitEnabled && translation <= this.lowerLimit) {
				C = Math.min(translation - this.lowerLimit, 0.0);
			} else if (this.upperLimitEnabled && translation >= this.upperLimit) {
				C = Math.max(translation - this.upperLimit, 0.0);
			}
			
			if (C != 0.0) {
				double invMass = invM1 + invM2 + s1x * s1x * invI1 + s2x * s2x * invI2;
				double impulse = 0.0;
				if (invMass > Epsilon.E) {
					impulse = -C / invMass;
				}
				
				// compute the applied impulses
				// Pc = Jtrans * lambda
				Vector2 P = axis.product(impulse);
				double l1 = impulse * s1x;
				double l2 = impulse * s2x;
				
				this.body1.translate(P.product(-invM1));
				this.body1.rotateAboutCenter(-l1 * invI1);
				
				this.body2.translate(P.product(invM2));
				this.body2.rotateAboutCenter(l2 * invI2);
				
				linearError = Math.abs(C);
			}
			
		}
		
		// NOTE: we have recompute centers (c1, c2), local anchor vectors (r1, r2), d 
		// basically everything if limits were applied since they translate/rotate the bodies
		
		// solve the point on line constraint
		Vector2 c1 = this.body1.getWorldCenter();
		Vector2 c2 = this.body2.getWorldCenter();
		
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		Vector2 d = c2.sum(r2).subtract(c1.sum(r1));
		
		Vector2 ay = this.body1.getWorldVector(this.yAxis);
		double say = r1.sum(d).cross(ay);
		double sby = r2.cross(ay);
		
		double Cx = ay.dot(d);
		
		double invMass = invM1 + invM2 + say * say * invI1 + sby * sby * invI2;
		double impulse = 0.0;
		
		// make sure k is not zero
		if (invMass > Epsilon.E) {
			impulse = -Cx / invMass;
		}
		
		// apply the impulse
		Vector2 P = new Vector2();
		P.x = ay.x * impulse;
		P.y = ay.y * impulse;
		double l1 = say * impulse;
		double l2 = sby * impulse;
		
		this.body1.translate(P.product(-invM1));
		this.body1.rotateAboutCenter(-l1 * invI1);
		
		this.body2.translate(P.product(invM2));
		this.body2.rotateAboutCenter(l2 * invI2);
		
		linearError = Math.max(linearError, Math.abs(Cx));
		
		// return if we corrected the error enough
		return linearError <= linearTolerance;
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
	 * The anchor point in world coordinates for the frame (body1).
	 * @return {@link Vector2}
	 */
	public Vector2 getAnchor1() {
		return this.body1.getWorldPoint(this.localAnchor1);
	}
	
	/**
	 * The anchor point in world coordinates for the wheel (body2).
	 * @return {@link Vector2}
	 */
	public Vector2 getAnchor2() {
		return this.body2.getWorldPoint(this.localAnchor2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionForce(double)
	 */
	@Override
	public Vector2 getReactionForce(double invdt) {
		Vector2 force = new Vector2();
		// compute the impulse
		force.x = this.impulse * this.wyAxis.x + (this.springImpulse + this.lowerLimitImpulse - this.upperLimitImpulse) * this.wxAxis.x;
		force.y = this.impulse * this.wyAxis.y + (this.springImpulse + this.lowerLimitImpulse - this.upperLimitImpulse) * this.wxAxis.y;
		// multiply by invdt to obtain the force
		force.multiply(invdt);
		return force;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionTorque(double)
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return this.motorImpulse * invdt;
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
	 * Returns the linear speed along the axis between the two joined bodies
	 * @return double
	 * @since 3.2.1
	 */
	public double getLinearSpeed() {
		Transform t1 = this.body1.getTransform();
		Transform t2 = this.body2.getTransform();
		
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		// get the world vectors of the axis
		Vector2 axis = this.body1.getWorldVector(this.xAxis);
		
		Vector2 p1 = this.body1.getWorldCenter().sum(r1);
		Vector2 p2 = this.body2.getWorldCenter().sum(r2);
		Vector2 d = p2.subtract(p1);
		
		// compute the velocities along the vectors pointing to the world space anchor points
		Vector2 v1 = r1.cross(this.body1.getAngularVelocity()).add(this.body1.getLinearVelocity());
		Vector2 v2 = r2.cross(this.body2.getAngularVelocity()).add(this.body2.getLinearVelocity());
		
		// compute the relative linear velocity along the axis
		double te1 = axis.dot(v2.subtract(v1));
		// compute the linear velocity along the separation of the anchor points
		double te2 = d.dot(axis.cross(this.body1.getAngularVelocity()));
		
		// the sum is the linear velocity
		return te1 + te2;
	}
	
	/**
	 * Returns the current angular speed between the two joined bodies.
	 * @return double
	 * @since 3.2.1
	 */
	public double getAngularSpeed() {
		double a1 = this.body1.getAngularVelocity();
		double a2 = this.body2.getAngularVelocity();
		return a2 - a1;
	}
	
	/**
	 * Returns the current linear translation along the joint axis.
	 * @return double
	 * @since 3.2.1
	 */
	public double getLinearTranslation() {
		Vector2 p1 = this.body1.getWorldPoint(this.localAnchor1);
		Vector2 p2 = this.body2.getWorldPoint(this.localAnchor2);
		Vector2 d = p2.difference(p1);
		Vector2 axis = this.body1.getWorldVector(this.xAxis);
		return d.dot(axis);
	}
	
	/**
	 * Returns the current angular translation between the joined bodies.
	 * @return double
	 * @since 3.2.1
	 */
	public double getAngularTranslation() {
		double a1 = this.body1.getTransform().getRotationAngle();
		double a2 = this.body2.getTransform().getRotationAngle();
		return a2 - a1;
	}

	/**
	 * Returns the axis in world coordinates for the frame (body1).
	 * @return {@link Vector2}
	 */
	public Vector2 getAxis() {
		return this.body1.getWorldVector(this.xAxis);
	}
	
	/**
	 * Set's the spring rest offset.
	 * <p>
	 * This can be any value and is used to offset the spring's rest
	 * distance.  This is only applicable when the spring is enabled.
	 * @param offset the offset
	 * @since 5.0.0
	 */
	public void setSpringRestOffset(double offset) {
		if (this.springRestOffset != offset) {
			this.springRestOffset = offset;
			
			// wake if necessary
			if (this.springEnabled) {
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
		}
	}
	
	/**
	 * Returns the spring's rest offset.
	 * @return double
	 * @since 5.0.0
	 */
	public double getSpringRestOffset() {
		return this.springRestOffset;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#isMotorEnabled()
	 */
	public boolean isMotorEnabled() {
		return motorEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#setMotorEnabled(boolean)
	 */
	public void setMotorEnabled(boolean motorEnabled) {
		if (this.motorEnabled != motorEnabled) {
			// wake up the joined bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the new value
			this.motorEnabled = motorEnabled;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#getMotorSpeed()
	 */
	public double getMotorSpeed() {
		return this.motorSpeed;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#setMotorSpeed(double)
	 */
	public void setMotorSpeed(double motorSpeed) {
		if (this.motorSpeed != motorSpeed) {
			if (this.motorEnabled) {
				// wake up the joined bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new value
			this.motorSpeed = motorSpeed;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#isMotorMaximumTorqueEnabled()
	 */
	@Override
	public boolean isMaximumMotorTorqueEnabled() {
		return this.motorMaximumTorqueEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#setMotorMaximumTorqueEnabled(boolean)
	 */
	@Override
	public void setMaximumMotorTorqueEnabled(boolean enabled) {
		if (this.motorMaximumTorqueEnabled != enabled) {
			if (this.motorEnabled) {
				// wake up the joined bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new value
			this.motorMaximumTorqueEnabled = enabled;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#getMotorMaximumTorque()
	 */
	public double getMaximumMotorTorque() {
		return this.motorMaximumTorque;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#setMotorMaximumTorque(double)
	 */
	public void setMaximumMotorTorque(double maximumMotorTorque) {
		// make sure its greater than or equal to zero
		if (maximumMotorTorque < 0) 
			throw new ValueOutOfRangeException("maximumMotorTorque", maximumMotorTorque, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
		if (this.motorMaximumTorque != maximumMotorTorque) {
			if (this.motorEnabled) {
				// wake up the joined bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new value
			this.motorMaximumTorque = maximumMotorTorque;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularMotorJoint#getMotorTorque(double)
	 */
	public double getMotorTorque(double invdt) {
		return this.motorImpulse * invdt;
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
		if (dampingRatio < 0.0) 
			throw new ValueOutOfRangeException("dampingRatio", dampingRatio, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
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
		if (frequency < 0)
			throw new ValueOutOfRangeException("frequency", frequency, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
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
		if (stiffness < 0)
			throw new ValueOutOfRangeException("stiffness", stiffness, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
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
		if (maximum < 0)
			throw new ValueOutOfRangeException("maximum", maximum, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0.0);
		
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
		return this.springImpulse * invdt;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearSpringJoint#getSpringMode()
	 */
	@Override
	public int getSpringMode() {
		return this.springMode;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearLimitsJoint#getUpperLimit()
	 */
	public double getUpperLimit() {
		return this.upperLimit;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearLimitsJoint#setUpperLimit(double)
	 */
	public void setUpperLimit(double upperLimit) {
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearLimitsJoint#setLowerLimit(double)
	 */
	public void setLowerLimit(double lowerLimit) {
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LimitsJoint#setLimits(double, double)
	 */
	public void setLimits(double lowerLimit, double upperLimit) {
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

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LimitsJoint#setLimitsEnabled(double, double)
	 */
	public void setLimitsEnabled(double lowerLimit, double upperLimit) {
		// enable the limits
		this.setLimitsEnabled(true);
		// set the values
		this.setLimits(lowerLimit, upperLimit);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LimitsJoint#setLimitsEnabled(boolean)
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearLimitsJoint#setLimits(double)
	 */
	public void setLimits(double limit) {
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearLimitsJoint#setLimitsEnabled(double)
	 */
	public void setLimitsEnabled(double limit) {
		// enable the limits
		this.setLimitsEnabled(true);
		// set the values
		this.setLimits(limit);
	}
}
