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
package org.dyn4j.dynamics.joint;

import org.dyn4j.DataContainer;
import org.dyn4j.Epsilon;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.Step;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Implementation of a wheel joint.
 * <p>
 * A wheel joint is used to simulate a vehicle's wheel and suspension.  The 
 * wheel is allowed to rotate freely about the given anchor point.  The 
 * suspension is allowed to translate freely along the given axis.  The whole
 * system can translate and rotate freely.
 * <p>
 * By default the frequency and damping ratio are set to 8.0 and 0.0 
 * respectively.  By definition this joint requires a frequency greater than 
 * zero to perform properly.  If a wheel without suspension is required, use 
 * a {@link RevoluteJoint} instead.
 * <p>
 * This joint also supports a motor.  The motor is an angular motor about the
 * anchor point.  The motor speed can be positive or negative to indicate a
 * clockwise or counter-clockwise rotation.  The maximum motor torque must be 
 * greater than zero for the motor to apply any motion.
 * @author William Bittle
 * @version 3.2.1
 * @since 3.0.0
 * @see <a href="http://www.dyn4j.org/documentation/joints/#Wheel_Joint" target="_blank">Documentation</a>
 */
public class WheelJoint extends Joint implements Shiftable, DataContainer {
	/** The local anchor point on the first {@link Body} */
	protected Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link Body} */
	protected Vector2 localAnchor2;
	
	/** Whether the motor is enabled or not */
	protected boolean motorEnabled;
	
	/** The target velocity in radians / second */
	protected double motorSpeed;
	
	/** The maximum torque the motor can apply in newton-meters */
	protected double maximumMotorTorque;
	
	/** The oscillation frequency in hz */
	protected double frequency;
	
	/** The damping ratio */
	protected double dampingRatio;

	// internal
	
	/** The axis representing the allowed line of motion */
	private final Vector2 xAxis;
	
	/** The perpendicular axis of the line of motion */
	private final Vector2 yAxis;
	
	// current state
	
	/** The bias for adding work to the constraint (simulating a spring) */
	private double bias;
	
	/** The damping portion of the constraint */
	private double gamma;
	
	/** The point-on-line constraint mass; K = J * Minv * Jtrans */
	private double invK;
	
	/** The spring/damper constraint mass */
	private double springMass;
	
	/** The mass of the motor */
	private double motorMass;
	
	/** The world space yAxis  */
	private Vector2 perp;
	
	/** The world space xAxis */
	private Vector2 axis;
	
	/** s1 = (r1 + d).cross(perp) */
	private double s1;
	
	/** s2 = r2.cross(perp) */
	private double s2;
	
	/** a1 = (r1 + d).cross(axis) */
	private double a1;

	/** a2 = r2.cross(axis) */
	private double a2;

	// output
	
	/** The accumulated impulse for warm starting */
	private double impulse;
	
	/** The impulse applied by the spring/damper */
	private double springImpulse;
	
	/** The impulse applied by the motor */
	private double motorImpulse;
	
	/**
	 * Minimal constructor.
	 * @param body1 the first {@link Body}
	 * @param body2 the second {@link Body}
	 * @param anchor the anchor point in world coordinates
	 * @param axis the axis of allowed motion
	 * @throws NullPointerException if body1, body2, anchor, or axis is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public WheelJoint(Body body1, Body body2, Vector2 anchor, Vector2 axis) {
		super(body1, body2, false);
		// verify the bodies are not the same instance
		if (body1 == body2) throw new IllegalArgumentException(Messages.getString("dynamics.joint.sameBody"));
		// check for a null anchor
		if (anchor == null) throw new NullPointerException(Messages.getString("dynamics.joint.nullAnchor"));
		// check for a null axis
		if (axis == null) throw new NullPointerException(Messages.getString("dynamics.joint.nullAxis"));
		// set the anchor point
		this.localAnchor1 = body1.getLocalPoint(anchor);
		this.localAnchor2 = body2.getLocalPoint(anchor);
		// make sure the axis is normalized
		Vector2 n = axis.getNormalized();
		// get the axis in local coordinates
		this.xAxis = body2.getLocalVector(n);
		// get the perpendicular axis
		this.yAxis = this.xAxis.cross(1.0);
		
		// initialize
		this.invK = 0.0;
		this.impulse = 0.0;
		this.motorMass = 0.0;
		this.motorImpulse = 0.0;
		this.springMass = 0.0;
		this.springImpulse = 0.0;
		
		// requires a spring damper by definition of the constraint.
		// if a spring/damper isn't needed, then use the RevoluteJoint instead.
		this.frequency = 8.0;
		this.dampingRatio = 0.0;
		this.gamma = 0.0;
		this.bias = 0.0;
		
		// no motor
		this.motorEnabled = false;
		this.maximumMotorTorque = 0.0;
		this.motorSpeed = 0.0;
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
		  .append("|MaximumMotorTorque=").append(this.maximumMotorTorque)
		  .append("|Frequency=").append(this.frequency)
		  .append("|DampingRatio=").append(this.dampingRatio)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#initializeConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void initializeConstraints(Step step, Settings settings) {
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
		Vector2 d = this.body1.getWorldCenter().sum(r1).subtract(this.body2.getWorldCenter().sum(r2));
		
		// get the world vectors of the axes
		this.axis = this.body2.getWorldVector(this.xAxis);
		this.perp = this.body2.getWorldVector(this.yAxis);
		
		// compute invK for the point-on-line constraint
		{
			// s1 = r1.cross(perp)
			// s2 = (r2 + d).cross(perp)
			this.s1 = r1.cross(this.perp);
			this.s2 = r2.sum(d).cross(this.perp);
			this.invK = invM1 + invM2 + this.s1 * this.s1 * invI1 + this.s2 * this.s2 * invI2;
			// make sure we don't divide by zero
			if (this.invK > Epsilon.E) {
				this.invK = 1.0 / this.invK;
			}
		}
		
		// compute the spring mass for the spring constraint
		if (this.frequency > 0.0) {
			// then we include the spring constraint
			// a1 = r1.cross(axis)
			// a2 = (r2 + d).cross(axis)
			this.a1 = r1.cross(this.axis);
			this.a2 = r2.sum(d).cross(this.axis);
			double invMass = invM1 + invM2 + this.a1 * this.a1 * invI1 + this.a2 * this.a2 * invI2;
			// make sure we don't divide by zero
			if (invMass > Epsilon.E) {
				// invert the spring mass
				this.springMass = 1.0 / invMass;
				// compute the current spring extension (we are solving for zero here)
				double c = d.dot(axis);
				// get the delta time
				double dt = step.getDeltaTime();
				// compute the natural frequency; f = w / (2 * pi) -> w = 2 * pi * f
				double w = Geometry.TWO_PI * this.frequency;
				// compute the damping coefficient; dRatio = d / (2 * m * w) -> d = 2 * m * w * dRatio
				double dc = 2.0 * this.springMass * this.dampingRatio * w;
				// compute the spring constant; w = sqrt(k / m) -> k = m * w * w
				double k = this.springMass * w * w;
				
				// compute gamma = CMF = 1 / (hk + d)
				this.gamma = dt * (dc + dt * k);
				// check for zero before inverting
				this.gamma = Math.abs(this.gamma) <= Epsilon.E ? 0.0 : 1.0 / this.gamma;			
				// compute the bias = x * ERP where ERP = hk / (hk + d)
				this.bias = c * dt * k * this.gamma;
				
				// compute the effective mass			
				this.springMass = invMass + this.gamma;
				// check for zero before inverting
				this.springMass = Math.abs(this.springMass) <= Epsilon.E ? 0.0 : 1.0 / this.springMass;
			}
		} else {
			// don't include the spring constraint
			this.springMass = 0.0;
			this.springImpulse = 0.0;
		}
		
		// check if the motor is enabled
		if (this.motorEnabled) {
			// compute the motor mass
			this.motorMass = invI1 + invI2;
			if (Math.abs(this.motorMass) > Epsilon.E) {
				this.motorMass = 1.0 / this.motorMass;
			}
		} else {
			// clear the previous motor impulse
			this.motorMass = 0.0;
			this.motorImpulse = 0.0;
		}
		
		// warm start
		// account for variable time step
		this.impulse *= step.getDeltaTimeRatio();
		this.springImpulse *= step.getDeltaTimeRatio();
		this.motorImpulse *= step.getDeltaTimeRatio();
		
		// we only compute the impulse for body1 since body2's impulse is
		// just the negative of body1's impulse
		Vector2 P = new Vector2();
		// perp.product(impulse) + axis.product(springImpulse)
		P.x = this.perp.x * this.impulse + this.springImpulse * this.axis.x;
		P.y = this.perp.y * this.impulse + this.springImpulse * this.axis.y;
		
		double l1 = this.impulse * this.s1 + this.springImpulse * this.a1 + this.motorImpulse;
		double l2 = this.impulse * this.s2 + this.springImpulse * this.a2 + this.motorImpulse;
		
		// apply the impulses
		this.body1.getLinearVelocity().add(P.product(invM1));
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * l1);
		this.body2.getLinearVelocity().subtract(P.product(invM2));
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * l2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void solveVelocityConstraints(Step step, Settings settings) {
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
		{
			double Cdt = this.axis.dot(v1.difference(v2)) + this.a1 * w1 - this.a2 * w2;
			// compute the impulse
			double impulse = -this.springMass * (Cdt + this.bias + this.gamma * this.springImpulse);
			// accumulate the spring impulse
			this.springImpulse += impulse;
			
			// compute the applied impulses
			// Pc = Jtrans * lambda
			Vector2 P = this.axis.product(impulse);
			double l1 = impulse * this.a1;
			double l2 = impulse * this.a2;
			
			v1.add(P.product(invM1));
			w1 += l1 * invI1;
			v2.subtract(P.product(invM2));
			w2 -= l2 * invI2;
		}
		
		// solve the motor constraint
		if (this.motorEnabled) {
			// compute Jv + b
			double Cdt = w1 - w2 - this.motorSpeed;
			// compute lambda = Kinv * (Jv + b)
			double impulse = this.motorMass * (-Cdt);
			// clamp the impulse between the max torque
			double oldImpulse = this.motorImpulse;
			double maxImpulse = this.maximumMotorTorque * step.getDeltaTime();
			this.motorImpulse = Interval.clamp(this.motorImpulse + impulse, -maxImpulse, maxImpulse);
			impulse = this.motorImpulse - oldImpulse;
			
			// apply the impulse
			w1 += impulse * invI1;
			w2 -= impulse * invI2;
		}
		
		// finally, solve the point-on-line constraint
		{
			double Cdt = this.perp.dot(v1.difference(v2)) + this.s1 * w1 - this.s2 * w2;
			double impulse = this.invK * -Cdt;
			// accumulate the impulse
			this.impulse += impulse;

			// compute the applied impulses
			// Pc = Jtrans * lambda
			Vector2 P = this.perp.product(impulse);
			double l1 = impulse * this.s1;
			double l2 = impulse * this.s2;
			
			v1.add(P.product(invM1));
			w1 += l1 * invI1;
			v2.subtract(P.product(invM2));
			w2 -= l2 * invI2;
		}
		
		// finally set the velocities
		// NOTE we dont have to update v1 or v2 because they are references
		this.body1.setAngularVelocity(w1);
		this.body2.setAngularVelocity(w2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(Step step, Settings settings) {
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
		
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		Vector2 d = c1.sum(r1).subtract(c2.sum(r2));
		this.axis = this.body2.getWorldVector(this.xAxis);
		this.perp = this.body2.getWorldVector(this.yAxis);
		
		double Cx = this.perp.dot(d);
		
		double k = invM1 + invM2 + this.s1 * this.s1 * invI1 + this.s2 * this.s2 * invI2;
		double impulse = 0.0;
		
		// make sure k is not zero
		if (k > Epsilon.E) {
			impulse = -Cx / k;
		} else {
			impulse = 0.0;
		}
		
		// apply the impulse
		Vector2 P = new Vector2();
		P.x = this.perp.x * impulse;
		P.y = this.perp.y * impulse;
		double l1 = this.s1 * impulse;
		double l2 = this.s2 * impulse;
		
		this.body1.translate(P.product(invM1));
		this.body1.rotateAboutCenter(l1 * invI1);
		
		this.body2.translate(P.product(-invM2));
		this.body2.rotateAboutCenter(-l2 * invI2);
		
		// return if we corrected the error enough
		return Math.abs(Cx) <= linearTolerance;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getAnchor1()
	 */
	@Override
	public Vector2 getAnchor1() {
		return this.body1.getWorldPoint(this.localAnchor1);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getAnchor2()
	 */
	@Override
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
		force.x = this.impulse * this.perp.x + this.springImpulse * this.axis.x;
		force.y = this.impulse * this.perp.y + this.springImpulse * this.axis.y;
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
	 * Returns the current joint speed.
	 * @return double
	 * @deprecated Replaced by {@link #getAngularSpeed()} in 3.2.1
	 */
	@Deprecated
	public double getJointSpeed() {
		return this.getAngularSpeed();
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
		Vector2 axis = this.body2.getWorldVector(this.xAxis);
		
		// compute the velocities along the vectors pointing to the world space anchor points
		Vector2 v1 = r1.cross(this.body1.getAngularVelocity()).add(this.body1.getLinearVelocity());
		Vector2 v2 = r2.cross(this.body2.getAngularVelocity()).add(this.body2.getLinearVelocity());
		
		// project them onto the joint axis
		return v2.dot(axis) - v1.dot(axis);
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
	 * Returns the current joint translation.
	 * @return double
	 * @deprecated Replaced by {@link #getLinearTranslation()} in 3.2.1
	 */
	@Deprecated
	public double getJointTranslation() {
		return this.getLinearTranslation();
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
		Vector2 axis = this.body2.getWorldVector(this.xAxis);
		return d.dot(axis);
	}
	
	/**
	 * Returns the current angular translation between the joined bodies.
	 * @return double
	 * @since 3.2.1
	 */
	public double getAngularTranslation() {
		double a1 = this.body1.getTransform().getRotation();
		double a2 = this.body2.getTransform().getRotation();
		return a2 - a1;
	}

	/**
	 * Returns true if this wheel joint is a spring wheel joint.
	 * @return boolean
	 */
	public boolean isSpring() {
		return this.frequency > 0.0;
	}
	
	/**
	 * Returns true if this wheel joint is a spring wheel joint
	 * with damping.
	 * @return boolean
	 */
	public boolean isSpringDamper() {
		return this.frequency > 0.0 && this.dampingRatio > 0.0;
	}
	
	/**
	 * Returns the damping ratio.
	 * @return double
	 */
	public double getDampingRatio() {
		return this.dampingRatio;
	}
	
	/**
	 * Sets the damping ratio.
	 * <p>
	 * Larger values reduce the oscillation of the spring.
	 * @param dampingRatio the damping ratio; in the range [0, 1]
	 * @throws IllegalArgumentException if damping ration is less than zero or greater than 1
	 */
	public void setDampingRatio(double dampingRatio) {
		// make sure its within range
		if (dampingRatio < 0 || dampingRatio > 1) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidDampingRatio"));
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new value
		this.dampingRatio = dampingRatio;
	}
	
	/**
	 * Returns the spring frequency.
	 * @return double
	 */
	public double getFrequency() {
		return this.frequency;
	}
	
	/**
	 * Sets the spring frequency.
	 * <p>
	 * Larger values increase the stiffness of the spring.
	 * @param frequency the spring frequency in hz; must be greater than zero
	 * @throws IllegalArgumentException if frequency is less than or equal to zero
	 */
	public void setFrequency(double frequency) {
		// check for valid value
		if (frequency <= 0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidFrequencyZero"));
		// wake up both bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new value
		this.frequency = frequency;
	}
	
	/**
	 * Returns true if the motor is enabled.
	 * @return boolean
	 */
	public boolean isMotorEnabled() {
		return motorEnabled;
	}
	
	/**
	 * Enables or disables the motor.
	 * @param motorEnabled true if the motor should be enabled
	 */
	public void setMotorEnabled(boolean motorEnabled) {
		// wake up the joined bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new value
		this.motorEnabled = motorEnabled;
	}
	
	/**
	 * Returns the target motor speed in radians / second.
	 * @return double
	 */
	public double getMotorSpeed() {
		return motorSpeed;
	}
	
	/**
	 * Sets the target motor speed.
	 * @param motorSpeed the target motor speed in radians / second
	 * @see #setMaximumMotorTorque(double)
	 */
	public void setMotorSpeed(double motorSpeed) {
		// wake up the joined bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new value
		this.motorSpeed = motorSpeed;
	}
	
	/**
	 * Returns the maximum torque the motor can apply to the joint
	 * to achieve the target speed.
	 * @return double
	 */
	public double getMaximumMotorTorque() {
		return this.maximumMotorTorque;
	}
	
	/**
	 * Sets the maximum torque the motor can apply to the joint
	 * to achieve the target speed.
	 * @param maximumMotorTorque the maximum torque in newtons-meters; in the range [0, &infin;]
	 * @throws IllegalArgumentException if maxMotorTorque is less than zero
	 * @see #setMotorSpeed(double)
	 */
	public void setMaximumMotorTorque(double maximumMotorTorque) {
		// make sure its greater than or equal to zero
		if (maximumMotorTorque < 0.0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidMaximumMotorTorque"));
		// wake up the joined bodies
		this.body1.setAsleep(false);
		this.body2.setAsleep(false);
		// set the new value
		this.maximumMotorTorque = maximumMotorTorque;
	}
	
	/**
	 * Returns the applied motor torque.
	 * @param invdt the inverse delta time from the time step
	 * @return double
	 */
	public double getMotorTorque(double invdt) {
		return this.motorImpulse * invdt;
	}
	
	/**
	 * Returns the axis in which the joint is allowed move along in world coordinates.
	 * @return {@link Vector2}
	 */
	public Vector2 getAxis() {
		return this.body2.getWorldVector(this.xAxis);
	}
}
