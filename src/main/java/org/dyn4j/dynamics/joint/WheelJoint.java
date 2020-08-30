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
package org.dyn4j.dynamics.joint;

import org.dyn4j.DataContainer;
import org.dyn4j.Epsilon;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
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
 * <p>
 * The joint also supports upper and lower limits. The limits represent the
 * maximum displacement from the anchor point along the given axis.
 * @author William Bittle
 * @version 4.0.0
 * @since 3.0.0
 * @see <a href="http://www.dyn4j.org/documentation/joints/#Wheel_Joint" target="_blank">Documentation</a>
 * @param <T> the {@link PhysicsBody} type
 */
public class WheelJoint<T extends PhysicsBody> extends Joint<T> implements Shiftable, DataContainer {
	/** The local anchor point on the first {@link PhysicsBody} */
	protected final Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link PhysicsBody} */
	protected final Vector2 localAnchor2;

	/** The axis representing the allowed line of motion */
	private final Vector2 xAxis;
	
	/** The perpendicular axis of the line of motion */
	private final Vector2 yAxis;
	
	// limits
	
	/** True if the limits are enabled */
	protected boolean limitEnabled;
	
	/** the upper limit in meters */
	protected double upperLimit;
	
	/** the lower limit in meters */
	protected double lowerLimit;
	
	// motor
	
	/** Whether the motor is enabled or not */
	protected boolean motorEnabled;
	
	/** The target velocity in radians / second */
	protected double motorSpeed;
	
	/** The maximum torque the motor can apply in newton-meters */
	protected double maximumMotorTorque;
	
	// spring damper
	
	/** The oscillation frequency in hz */
	protected double frequency;
	
	/** The damping ratio */
	protected double dampingRatio;

	// current state

	/** The stiffness of the spring */
	private double stiffness;
	
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
	
	/** The lower limit impulse */
	private double lowerImpulse;
	
	/** The upper limit impulse */
	private double upperImpulse;
	
	/** The impulse applied by the spring/damper */
	private double springImpulse;
	
	/** The impulse applied by the motor */
	private double motorImpulse;
	
	/**
	 * Minimal constructor.
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @param anchor the anchor point in world coordinates
	 * @param axis the axis of allowed motion
	 * @throws NullPointerException if body1, body2, anchor, or axis is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public WheelJoint(T body1, T body2, Vector2 anchor, Vector2 axis) {
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
		this.yAxis = this.xAxis.getRightHandOrthogonalVector();
		
		// limits
		this.limitEnabled = false;
		this.lowerLimit = 0.0;
		this.upperLimit = 0.0;
		
		// motor
		this.motorEnabled = false;
		this.motorSpeed = 0.0;
		this.maximumMotorTorque = 1000.0;
		
		// spring
		this.frequency = 8.0;
		this.dampingRatio = 0.0;
		
		// initialize
		this.stiffness = 0.0;
		this.damping = 0.0;
		this.gamma = 0.0;
		this.bias = 0.0;
		
		this.translation = 0.0;
		
		this.invK = 0.0;
		this.axialMass = 0.0;
		this.motorMass = 0.0;
		this.springMass = 0.0;

		this.perp = null;
		this.axis = null;
		this.a1 = 0.0;
		this.a2 = 0.0;
		this.s1 = 0.0;
		this.s2 = 0.0;
		
		this.impulse = 0.0;
		this.lowerImpulse = 0.0;
		this.upperImpulse = 0.0;
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
		  .append("|MaximumMotorTorque=").append(this.maximumMotorTorque)
		  .append("|Frequency=").append(this.frequency)
		  .append("|DampingRatio=").append(this.dampingRatio)
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
		
		// a1 = r1.cross(axis)
		// a2 = (r2 + d).cross(axis)
		this.a1 = r1.cross(this.axis);
		this.a2 = r2.sum(d).cross(this.axis);
		double invMass = invM1 + invM2 + this.a1 * this.a1 * invI1 + this.a2 * this.a2 * invI2;
		if (invMass > Epsilon.E) {
			this.axialMass = 1.0 / invMass;
		} else {
			this.axialMass = 0.0;
		}
		
		this.springMass = 0.0;
		this.gamma = 0.0;
		this.bias = 0.0;
		
		// recompute spring reduced mass (m), stiffness (k), and damping (d)
		// since frequency, dampingRatio, or the masses of the joined bodies
		// could change
		if (this.frequency > 0.0) {
			double lm = this.getReducedMass();
			double nf = this.getNaturalFrequency(this.frequency);
			
			this.stiffness = this.getSpringStiffness(lm, nf);
			this.damping = this.getSpringDampingCoefficient(lm, nf, this.dampingRatio);
		} else {
			this.stiffness = 0.0;
			this.damping = 0.0;
		}
		
		// compute the spring mass for the spring constraint
		if (this.stiffness > 0.0 && invMass > 0.0) {
			this.springMass = this.axialMass;
			
			// compute the current spring extension (we are solving for zero here)
			double c = d.dot(this.axis);
			// get the delta time
			double dt = step.getDeltaTime();

			this.gamma = this.getConstraintImpulseMixing(dt, this.stiffness, this.damping);
			double erp = this.getErrorReductionParameter(dt, this.stiffness, this.damping);
			
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
		}
		
		if (this.limitEnabled) {
			this.translation = this.axis.dot(d);
		} else {
			this.lowerImpulse = 0.0;
			this.upperImpulse = 0.0;
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
			
			double axialImpulse = this.springImpulse + this.lowerImpulse - this.upperImpulse;
			
			// we only compute the impulse for body1 since body2's impulse is
			// just the negative of body1's impulse
			Vector2 P = new Vector2();
			// perp.product(impulse) + axis.product(springImpulse)
			P.x = this.perp.x * this.impulse + axialImpulse * this.axis.x;
			P.y = this.perp.y * this.impulse + axialImpulse * this.axis.y;
			
			double l1 = this.impulse * this.s1 + axialImpulse * this.a1 + this.motorImpulse;
			double l2 = this.impulse * this.s2 + axialImpulse * this.a2 + this.motorImpulse;
			
			// apply the impulses
			this.body1.getLinearVelocity().add(P.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * l1);
			this.body2.getLinearVelocity().subtract(P.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * l2);
		} else {
			this.impulse = 0.0;
			this.lowerImpulse = 0.0;
			this.upperImpulse = 0.0;
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
		
		if (this.limitEnabled) {
			// lower limit
			{
				double C = this.translation - this.lowerLimit;
				double Cdot = this.axis.dot(v1.difference(v2)) + this.a1 * w1 - this.a2 * w2;
				double impulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * step.getInverseDeltaTime());
				double oldImpulse = this.lowerImpulse;
				this.lowerImpulse = Math.max(this.lowerImpulse + impulse, 0.0);
				impulse = this.lowerImpulse - oldImpulse;
				
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
			
			// upper limit
			{
				double C = this.upperLimit - this.translation;
				double Cdot = this.axis.dot(v2.difference(v1)) + this.a2 * w2 - this.a1 * w1;
				double impulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * step.getInverseDeltaTime());
				double oldImpulse = this.upperImpulse;
				this.upperImpulse = Math.max(this.upperImpulse + impulse, 0.0);
				impulse = this.upperImpulse - oldImpulse;
				
				// compute the applied impulses
				// Pc = Jtrans * lambda
				Vector2 P = this.axis.product(impulse);
				double l1 = impulse * this.a1;
				double l2 = impulse * this.a2;
				
				v1.subtract(P.product(invM1));
				w1 -= l1 * invI1;
				v2.add(P.product(invM2));
				w2 += l2 * invI2;
			}
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
		
		if (this.limitEnabled) {
			Vector2 c1 = this.body1.getWorldCenter();
			Vector2 c2 = this.body2.getWorldCenter();
			
			Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
			Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
			
			Vector2 d = c1.sum(r1).subtract(c2.sum(r2));
			
			Vector2 axis = this.body2.getWorldVector(this.xAxis);
			double a1 = r1.cross(axis);
			double a2 = r2.sum(d).cross(axis);
			
			double C = 0.0;
			double translation = axis.dot(d);
			
			if (Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * linearTolerance) {
				C = translation;
			} else if (translation <= this.lowerLimit) {
				C = Math.min(translation - this.lowerLimit, 0.0);
			} else if (translation >= this.upperLimit) {
				C = Math.max(translation - this.upperLimit, 0.0);
			}
			
			if (C != 0.0) {
				double invMass = invM1 + invM2 + a1 * a1 * invI1 + a2 * a2 * invI2;
				double impulse = 0.0;
				if (invMass > Epsilon.E) {
					impulse = -C / invMass;
				}
				
				// compute the applied impulses
				// Pc = Jtrans * lambda
				Vector2 P = axis.product(impulse);
				double l1 = impulse * a1;
				double l2 = impulse * a2;
				
				this.body1.translate(P.product(invM1));
				this.body1.rotateAboutCenter(l1 * invI1);
				
				this.body2.translate(P.product(-invM2));
				this.body2.rotateAboutCenter(-l2 * invI2);
				
				linearError = Math.abs(C);
			}
			
		}
		
		// solve the point on line constraint
		Vector2 c1 = this.body1.getWorldCenter();
		Vector2 c2 = this.body2.getWorldCenter();
		
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		Vector2 d = c1.sum(r1).subtract(c2.sum(r2));
		
		Vector2 perp = this.body2.getWorldVector(this.yAxis);
		double s1 = r1.cross(perp);
		double s2 = r2.sum(d).cross(perp);
		
		double Cx = perp.dot(d);
		
		double invMass = invM1 + invM2 + s1 * s1 * invI1 + s2 * s2 * invI2;
		double impulse = 0.0;
		
		// make sure k is not zero
		if (invMass > Epsilon.E) {
			impulse = -Cx / invMass;
		}
		
		// apply the impulse
		Vector2 P = new Vector2();
		P.x = perp.x * impulse;
		P.y = perp.y * impulse;
		double l1 = s1 * impulse;
		double l2 = s2 * impulse;
		
		this.body1.translate(P.product(invM1));
		this.body1.rotateAboutCenter(l1 * invI1);
		
		this.body2.translate(P.product(-invM2));
		this.body2.rotateAboutCenter(-l2 * invI2);
		
		linearError = Math.max(linearError, Math.abs(Cx));
		
		// return if we corrected the error enough
		return linearError <= linearTolerance;
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
		force.x = this.impulse * this.perp.x + (this.springImpulse + this.lowerImpulse + this.upperImpulse) * this.axis.x;
		force.y = this.impulse * this.perp.y + (this.springImpulse + this.lowerImpulse + this.upperImpulse) * this.axis.y;
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
		double a1 = this.body1.getTransform().getRotationAngle();
		double a2 = this.body2.getTransform().getRotationAngle();
		return a2 - a1;
	}

	/**
	 * Returns true if this wheel joint is a spring wheel joint.
	 * <p>
	 * Since the frequency cannot be less than or equal to zero, this should
	 * always returne true.
	 * @return boolean
	 * @deprecated Deprecated in 4.0.0. Use the {@link #isSpringEnabled()} method instead.
	 */
	@Deprecated
	public boolean isSpring() {
		return this.frequency > 0.0;
	}
	
	/**
	 * Returns true if this wheel joint is a spring wheel joint
	 * with damping.
	 * @return boolean
	 * @deprecated Deprecated in 4.0.0. Use the {@link #isSpringDamperEnabled()} method instead.
	 */
	@Deprecated
	public boolean isSpringDamper() {
		return this.frequency > 0.0 && this.dampingRatio > 0.0;
	}

	/**
	 * Returns true if this distance joint is a spring distance joint.
	 * @return boolean
	 * @since 4.0.0
	 */
	public boolean isSpringEnabled() {
		return this.frequency > 0.0;
	}
	
	/**
	 * Returns true if this distance joint is a spring distance joint
	 * with damping.
	 * @return boolean
	 * @since 4.0.0
	 */
	public boolean isSpringDamperEnabled() {
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
		this.body1.setAtRest(false);
		this.body2.setAtRest(false);
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
		this.body1.setAtRest(false);
		this.body2.setAtRest(false);
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
		if (this.motorEnabled != motorEnabled) {
			// wake up the joined bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the new value
			this.motorEnabled = motorEnabled;
		}
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
		
		if (this.maximumMotorTorque != maximumMotorTorque) {
			if (this.motorEnabled) {
				// wake up the joined bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new value
			this.maximumMotorTorque = maximumMotorTorque;
		}
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
	 * Returns true if the limit is enabled.
	 * @return boolean
	 */
	public boolean isLimitEnabled() {
		return this.limitEnabled;
	}
	
	/**
	 * Enables or disables the limit.
	 * @param flag true if the limit should be enabled
	 */
	public void setLimitEnabled(boolean flag) {
		// check if its changing
		if (this.limitEnabled != flag) {
			// wake up both bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the new value
			this.limitEnabled = flag;
			// clear the accumulated limit impulse
			this.lowerImpulse = 0.0;
			this.upperImpulse = 0.0;
		}
	}
	
	/**
	 * Returns the upper limit in meters.
	 * @return double
	 */
	public double getUpperLimit() {
		return this.upperLimit;
	}
	
	/**
	 * Sets the upper limit.
	 * <p>
	 * Must be greater than or equal to the current lower limit.
	 * @param upperLimit the upper limit in meters
	 * @throws IllegalArgumentException if upperLimit is less than the current lower limit
	 */
	public void setUpperLimit(double upperLimit) {
		if (upperLimit < this.lowerLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidUpperLimit"));
		if (this.upperLimit != upperLimit) {
			// only wake the bodies if the motor is enabled and the limit has changed
			if (this.limitEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new value
			this.upperLimit = upperLimit;
			// clear accumulated impulse
			this.upperImpulse = 0.0;
		}
	}
	
	/**
	 * Returns the lower limit in meters.
	 * @return double
	 */
	public double getLowerLimit() {
		return this.lowerLimit;
	}
	
	/**
	 * Sets the lower limit.
	 * <p>
	 * Must be less than or equal to the current upper limit.
	 * @param lowerLimit the lower limit in meters
	 * @throws IllegalArgumentException if lowerLimit is greater than the current upper limit
	 */
	public void setLowerLimit(double lowerLimit) {
		if (lowerLimit > this.upperLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLowerLimit"));
		if (this.lowerLimit != lowerLimit) {
			// only wake the bodies if the motor is enabled and the limit has changed
			if (this.limitEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new value
			this.lowerLimit = lowerLimit;
			// clear accumulated impulse
			this.lowerImpulse = 0.0;
		}
	}
	
	/**
	 * Sets the upper and lower limits.
	 * <p>
	 * The lower limit must be less than or equal to the upper limit.
	 * @param lowerLimit the lower limit in meters
	 * @param upperLimit the upper limit in meters
	 * @throws IllegalArgumentException if the lowerLimit is greater than upperLimit
	 */
	public void setLimits(double lowerLimit, double upperLimit) {
		if (lowerLimit > upperLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLimits"));
		if (this.lowerLimit != lowerLimit || this.upperLimit != upperLimit) {
			// only wake the bodies if the motor is enabled and one of the limits has changed
			if (this.limitEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the values
			this.lowerLimit = lowerLimit;
			this.upperLimit = upperLimit;
			// clear accumulated impulse
			this.lowerImpulse = 0.0;
			this.upperImpulse = 0.0;
		}
	}
	
	/**
	 * Returns the axis in which the joint is allowed move along in world coordinates.
	 * @return {@link Vector2}
	 */
	public Vector2 getAxis() {
		return this.body2.getWorldVector(this.xAxis);
	}
}
