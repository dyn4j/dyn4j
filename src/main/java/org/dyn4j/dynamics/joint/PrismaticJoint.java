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
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Matrix22;
import org.dyn4j.geometry.Matrix33;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Vector3;

/**
 * Implementation of a prismatic joint.
 * <p>
 * A prismatic joint constrains the linear motion of two bodies along an axis
 * and prevents relative rotation.  The whole system can rotate and translate 
 * freely.
 * <p>
 * The initial relative rotation of the bodies will remain unchanged unless 
 * updated by calling {@link #setReferenceAngle(double)} method.  The bodies
 * are not required to be aligned in any particular way.
 * <p>
 * The world space axis is fixed to the first body.  This means that when the
 * first body rotates, the axis will rotate with it.  The axis represents the
 * allowed linear motion between the bodies.
 * <p>
 * The world space anchor point can be any point but is typically a point on
 * the axis of allowed motion, usually the world center of either of the joined
 * bodies.  The anchor point is used to track the separation of the bodies from
 * the allowed axis of motion when drift occurs in the velocity solver.
 * <p>
 * This joint also supports a linear spring, but is disabled by default. By 
 * default the frequency and damping ratio are set to 8.0 and 0.0 respectively.  
 * You can enable/disable the various spring-damper features using 
 * {@link #setSpringEnabled(boolean)}, 
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
 * The joint also supports upper and lower limits. The limits represent the
 * maximum displacement from the anchor point along the given axis.  This means
 * that the limits are typically negative for the lower limit and positive for
 * the upper limit.  The limits are solved relative to the given axis's
 * direction.  The limits can be enabled separately using 
 * {@link #setLowerLimitEnabled(boolean)} and
 * {@link #setUpperLimitEnabled(boolean)}.  This also means that the limits
 * are relative to the initial starting position of the bodies.
 * <p>
 * This joint also supports a motor.  The motor is a linear motor along the
 * axis.  The motor speed can be positive or negative to indicate motion along
 * or opposite the axis direction.  The maximum motor force must be greater 
 * than zero for the motor to apply any motion.  The motor must be enabled 
 * using {@link #setMotorEnabled(boolean)}.
 * <p>
 * NOTE: The spring-damper and motor can be enabled at the same time, but
 * since they operate on the same degree of freedom, the feature that has the
 * highest maximum force will win.  It's recommended to only use one or the
 * other.
 * <p>
 * NOTE: In versions of dyn4j before 5.0.0, the body arguments in the 
 * constructor were reversed. It was changed to accept the frame first, then 
 * the wheel to make things more natural.
 * @author William Bittle
 * @version 5.0.0
 * @since 1.0.0
 * @see <a href="https://www.dyn4j.org/pages/joints#Prismatic_Joint" target="_blank">Documentation</a>
 * @see <a href="https://www.dyn4j.org/2011/03/prismatic-constraint/" target="_blank">Prismatic Constraint</a>
 * @param <T> the {@link PhysicsBody} type
 */
public class PrismaticJoint<T extends PhysicsBody> extends AbstractPairedBodyJoint<T> implements LinearLimitsJoint, LinearMotorJoint, LinearSpringJoint, PairedBodyJoint<T>, Joint<T>, Shiftable, DataContainer, Ownable {
	/** The local anchor point on the first {@link PhysicsBody} */
	protected final Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link PhysicsBody} */
	protected final Vector2 localAnchor2;
	
	/** The axis representing the allowed line of motion */
	protected final Vector2 xAxis;
	
	/** The perpendicular axis of the line of motion */
	protected final Vector2 yAxis;

	/** The initial angle between the two {@link PhysicsBody}s */
	protected double referenceAngle;

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
	
	/** The target velocity in meters / second */
	protected double motorSpeed;
	
	/** True if the motor force should be limited */
	protected boolean maximumMotorForceEnabled;
	
	/** The maximum force the motor can apply in newtons */
	protected double maximumMotorForce;

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
	
	/** The constraint mass; K = J * Minv * Jtrans */
	private final Matrix22 K;
	
	/** The mass of the motor */
	private double axialMass;

	/** The spring/damper constraint mass */
	private double springMass;
	
	/** The world space yAxis from body1's transform */
	private Vector2 perp;
	
	/** The world space xAxis from body1's transform */
	private Vector2 axis;
	
	/** s1y = (r1 + d).cross(yaxis) */
	private double s1;

	/** s2y = r2.cross(yaxis) */
	private double s2;
	
	/** s1x = (r1 + d).cross(xaxis) */
	private double a1;

	/** s2x = r2.cross(xaxis) */
	private double a2;

	/** The current translation */
	private double translation;
	
	// output
	
	/** The accumulated impulse for warm starting */
	private Vector2 impulse;

	/** The impulse applied by the spring/damper */
	private double springImpulse;
	
	/** The impulse applied by the motor */
	private double motorImpulse;
	
	/** The impulse applied by the lower limit */
	private double lowerLimitImpulse;
	
	/** The impulse applied by the upper limit */
	private double upperLimitImpulse;
	
	/**
	 * Minimal constructor.
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @param anchor the anchor point in world coordinates
	 * @param axis the axis of allowed motion
	 * @throws NullPointerException if body1, body2, anchor or axis is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public PrismaticJoint(T body1, T body2, Vector2 anchor, Vector2 axis) {
		super(body1, body2);
		
		// check for a null anchor
		if (anchor == null) 
			throw new ArgumentNullException("anchor");
		
		// check for a null axis
		if (axis == null) 
			throw new ArgumentNullException("axis");
		
		// set the anchor point
		this.localAnchor1 = body1.getLocalPoint(anchor);
		this.localAnchor2 = body2.getLocalPoint(anchor);
		
		// make sure the axis is normalized
		Vector2 n = axis.getNormalized();
		// get the axis in local coordinates
		this.xAxis = body1.getLocalVector(n);
		// get the perpendicular axis
		this.yAxis = this.xAxis.getRightHandOrthogonalVector();
		// get the initial rotation
		this.referenceAngle = body2.getTransform().getRotationAngle() - body1.getTransform().getRotationAngle();
		
		// initialize
		this.K = new Matrix22();

		// limits
		this.upperLimitEnabled = false;
		this.lowerLimitEnabled = false;
		this.lowerLimit = 0.0;
		this.upperLimit = 0.0;
		
		// motor
		this.motorEnabled = false;
		this.motorSpeed = 0.0;
		this.maximumMotorForceEnabled = false;
		this.maximumMotorForce = 1000.0;

		// spring
		this.springMode = SPRING_MODE_FREQUENCY;
		this.springEnabled = false;
		this.springDamperEnabled = false;
		this.springMaximumForce = 1000.0;
		this.springMaximumForceEnabled = false;
		this.springStiffness = 0.0;
		this.springFrequency = 8.0;
		this.springDampingRatio = 0.3;
		
		this.axialMass = 0.0;
		
		this.perp = null;
		this.axis = null;
		this.a1 = 0.0;
		this.a2 = 0.0;
		this.s2 = 0.0;
		this.s1 = 0.0;
		
		this.translation = 0.0;
		
		this.impulse = new Vector2();
		this.motorImpulse = 0.0;
		this.lowerLimitImpulse = 0.0;
		this.upperLimitImpulse = 0.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PrismaticJoint[").append(super.toString())
		  .append("|Anchor=").append(this.getAnchor1())
		  .append("|Axis=").append(this.getAxis())
		  .append("|IsMotorEnabled=").append(this.motorEnabled)
		  .append("|MotorSpeed=").append(this.motorSpeed)
		  .append("|MaximumMotorForce=").append(this.maximumMotorForce)
		  .append("|ReferenceAngle=").append(this.referenceAngle)
		  .append("|IsLowerLimitEnabled=").append(this.lowerLimitEnabled)
		  .append("|IsUpperLimitEnabled=").append(this.upperLimitEnabled)
		  .append("|LowerLimit=").append(this.lowerLimit)
		  .append("|UpperLimit=").append(this.upperLimit)
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
		this.axis = this.body1.getWorldVector(this.xAxis);
		this.perp = this.body1.getWorldVector(this.yAxis);
		
		// s1y = (r1 + d).cross(yaxis)
		this.s1 = r1.sum(d).cross(this.perp);
		// s2y = r2.cross(yaxis)
		this.s2 = r2.cross(this.perp);
		
		// s1x = (r1 + d).cross(xaxis)
		this.a1 = r1.sum(d).cross(this.axis);
		// s2x = r2.cross(xaxis)
		this.a2 = r2.cross(this.axis);
		
		double invMass = invM1 + invM2 + this.a1 * this.a1 * invI1 + this.a2 * this.a2 * invI2;
		if (invMass > Epsilon.E) {
			this.axialMass = 1.0 / invMass;
		} else {
			this.axialMass = 0.0;
		}
		
		this.K.m00 = invM1 + invM2 + this.s1 * this.s1 * invI1 + this.s2 * this.s2 * invI2;
		this.K.m01 = this.s1 * invI1 + this.s2 * invI2;
		this.K.m10 = this.K.m01;
		this.K.m11 = invI1 + invI2;
		
		// handle prismatic constraint between two fixed rotation bodies
		if (this.K.m11 <= Epsilon.E) {
			this.K.m11 = 1.0;
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
			double c = d.dot(this.axis) - this.springRestOffset;
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
			this.damping = 0.0;
		}
		
		// check for the limit being enabled
		if (this.lowerLimitEnabled || this.upperLimitEnabled) {
			this.translation = this.axis.dot(d);
		} else {
			this.lowerLimitImpulse = 0.0;
			this.upperLimitImpulse = 0.0;
		}
		
		// check if the motor is still enabled
		if (!this.motorEnabled) {
			// if not then make the current motor impulse zero
			this.motorImpulse = 0.0;
		}
		
		// warm start
		if (settings.isWarmStartingEnabled()) {
			// account for variable time step
			double dtr = step.getDeltaTimeRatio();
			this.impulse.multiply(dtr);
			this.springImpulse *= dtr;
			this.motorImpulse *= dtr;
			this.lowerLimitImpulse *= dtr;
			this.upperLimitImpulse *= dtr;
			
			// the impulse along the axis
			double axialImpulse = this.springImpulse + this.motorImpulse + this.lowerLimitImpulse - this.upperLimitImpulse;
			
			// compute the applied impulses
			// Pc = Jtrans * lambda
			
			// where Jtrans = |  perp   axis | excluding rotational elements
			//                | -perp  -axis |
			// we only compute the impulse for body1 since body2's impulse is
			// just the negative of body1's impulse
			Vector2 P = new Vector2();
			// perp.product(impulse.x) + axis.product(motorImpulse + impulse.z)
			P.x = this.perp.x * this.impulse.x + axialImpulse * this.axis.x;
			P.y = this.perp.y * this.impulse.x + axialImpulse * this.axis.y;
			
			// where Jtrans = |  s1   a1 | excluding linear elements
			//                |   1    1 |
			//                | -s2  -a2 |
			double l1 = this.impulse.x * this.s1 + this.impulse.y + axialImpulse * this.a1;
			double l2 = this.impulse.x * this.s2 + this.impulse.y + axialImpulse * this.a2;
			
			// apply the impulses
			this.body1.getLinearVelocity().subtract(P.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() - invI1 * l1);
			this.body2.getLinearVelocity().add(P.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * l2);
		} else {
			this.impulse.zero();
			this.motorImpulse = 0.0;
			this.springImpulse = 0.0;
			this.lowerLimitImpulse = 0.0;
			this.upperLimitImpulse = 0.0;
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
			double Cdt = this.axis.dot(v2.difference(v1)) + this.a2 * w2 - this.a1 * w1;
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
			Vector2 P = this.axis.product(stepImpulse);
			double l1 = stepImpulse * this.a1;
			double l2 = stepImpulse * this.a2;
			
			v1.subtract(P.product(invM1));
			w1 -= l1 * invI1;
			v2.add(P.product(invM2));
			w2 += l2 * invI2;
		}
		
		// solve the motor constraint
		if (this.motorEnabled) {
			// compute Jv + b
			double Cdt = this.axis.dot(v2.difference(v1)) + this.a2 * w2 - this.a1 * w1;
			// compute lambda = Kinv * (Jv + b)
			double stepImpulse = this.axialMass * (this.motorSpeed - Cdt);
			
			if (this.maximumMotorForceEnabled) {
				// clamp the impulse between the max force
				double currentAccumulatedImpulse = this.motorImpulse;
				double maxImpulse = this.maximumMotorForce * step.getDeltaTime();
				this.motorImpulse = Interval.clamp(this.motorImpulse + stepImpulse, -maxImpulse, maxImpulse);
				stepImpulse = this.motorImpulse - currentAccumulatedImpulse;
			} else {
				this.motorImpulse += stepImpulse;
			}
			
			// apply the impulse
			Vector2 P = this.axis.product(stepImpulse);
			double l1 = stepImpulse * this.a1;
			double l2 = stepImpulse * this.a2;
			
			v1.subtract(P.product(invM1));
			w1 -= l1 * invI1;
			v2.add(P.product(invM2));
			w2 += l2 * invI2;
		}
		
		double invdt = step.getInverseDeltaTime();
		
		// solve lower limit
		if (this.lowerLimitEnabled) {
			double C = this.translation - this.lowerLimit;
			double Cdot = this.axis.dot(v2.difference(v1)) + this.a2 * w2 - this.a1 * w1;
			double stepImpulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * invdt);
			
			// clamp
			double currentAccumulatedImpulse = this.lowerLimitImpulse;
			this.lowerLimitImpulse = Math.max(this.lowerLimitImpulse + stepImpulse, 0.0);
			stepImpulse = this.lowerLimitImpulse - currentAccumulatedImpulse;
			
			// apply the impulse
			Vector2 P = this.axis.product(stepImpulse);
			double l1 = stepImpulse * this.a1;
			double l2 = stepImpulse * this.a2;
			
			v1.subtract(P.product(invM1));
			w1 -= l1 * invI1;
			v2.add(P.product(invM2));
			w2 += l2 * invI2;
		}
			
		// solve upper limit
		if (this.upperLimitEnabled) {
			double C = this.upperLimit - this.translation;
			double Cdot = this.axis.dot(v1.difference(v2)) + this.a1 * w1 - this.a2 * w2;
			double stepImpulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * invdt);
			
			double currentAccumulatedImpulse = this.upperLimitImpulse;
			this.upperLimitImpulse = Math.max(this.upperLimitImpulse + stepImpulse, 0.0);
			stepImpulse = this.upperLimitImpulse - currentAccumulatedImpulse;
			
			// apply the impulse
			Vector2 P = this.axis.product(stepImpulse);
			double l1 = stepImpulse * this.a1;
			double l2 = stepImpulse * this.a2;
			
			v1.add(P.product(invM1));
			w1 += l1 * invI1;
			v2.subtract(P.product(invM2));
			w2 -= l2 * invI2;
		}
		
		// solve the prismatic constraint
		Vector2 Cdt = new Vector2();
		Cdt.x = this.perp.dot(v2.difference(v1)) + this.s2 * w2 - this.s1 * w1;
		Cdt.y = w2 - w1;
		
		// otherwise just solve the linear and angular constraints
		Vector2 f2r = this.K.solve(Cdt.negate());
		this.impulse.x += f2r.x;
		this.impulse.y += f2r.y;
		
		// compute the applied impulses
		// Pc = Jtrans * lambda
		Vector2 P = this.perp.product(f2r.x);
		double l1 = f2r.x * this.s1 + f2r.y;
		double l2 = f2r.x * this.s2 + f2r.y;
		
		v1.subtract(P.product(invM1));
		w1 -= l1 * invI1;
		v2.add(P.product(invM2));
		w2 += l2 * invI2;
		
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
		double angularTolerance = settings.getAngularTolerance();
		
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
		
		Vector2 d = c2.sum(r2).subtract(c1.sum(r1));
		
		Vector2 axis = this.body1.getWorldVector(this.xAxis);
		double a1 = r1.sum(d).cross(axis);
		double a2 = r2.cross(axis);
		
		Vector2 perp = this.body1.getWorldVector(this.yAxis);
		double s1 = r1.sum(d).cross(perp);
		double s2 = r2.cross(perp);
		
		Vector2 C = new Vector2();
		C.x = perp.dot(d);
		C.y = this.getRelativeRotation();
		
		double C2 = 0.0;
		double linearError = Math.abs(C.x);
		double angularError = Math.abs(C.y);
		boolean limitActive = false;
		
		// check if the limit is enabled
		if (this.lowerLimitEnabled || this.upperLimitEnabled) {
			// what's the current distance
			double translation = axis.dot(d);
			// check for equal limits
			if (this.upperLimitEnabled && this.lowerLimitEnabled && Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * linearTolerance) {
				// then apply the limit and clamp it
				C2 = translation;
				linearError = Math.max(linearError, Math.abs(translation));
				limitActive = true;
			} else if (this.lowerLimitEnabled && translation <= this.lowerLimit) {
				// if its less than the lower limit then attempt to correct it
				C2 = Math.min(translation - this.lowerLimit, 0.0);
				linearError = Math.max(linearError, this.lowerLimit - translation);
				limitActive = true;
			} else if (this.upperLimitEnabled && translation >= this.upperLimit) {
				// if its less than the lower limit then attempt to correct it
				C2 = Math.max(translation - this.upperLimit, 0.0);
				linearError = Math.max(linearError, translation - this.upperLimit);
				limitActive = true;
			}
		}
		
		Vector3 impulse;
		// check if the limit is active
		if (limitActive) {
			Matrix33 K = new Matrix33();
			
			// then solve the linear and angular constraints along with the limit constraint
			K.m00 = invM1 + invM2 + s1 * s1 * invI1 + s2 * s2 * invI2;
			K.m01 = s1 * invI1 + s2 * invI2;
			K.m02 = s1 * a1 * invI1 + s2 * a2 * invI2;
			
			K.m10 = K.m01;
			K.m11 = invI1 + invI2;
			
			// handle prismatic constraint between two fixed rotation bodies
			if (K.m11 <= Epsilon.E) {
				K.m11 = 1.0;
			}
			
			K.m12 = a1 * invI1 + a2 * invI2;
			K.m20 = K.m02;
			K.m21 = K.m12;
			K.m22 = invM1 + invM2 + a1 * a1 * invI1 + a2 * a2 * invI2;
			
			Vector3 Clim = new Vector3(C.x, C.y, C2);
			impulse = K.solve33(Clim.negate());
		} else {
			Matrix22 K = new Matrix22();
			
			// then solve just the linear and angular constraints
			K.m00 = invM1 + invM2 + s1 * s1 * invI1 + s2 * s2 * invI2;
			K.m01 = s1 * invI1 + s2 * invI2;
			K.m10 = K.m01;
			K.m11 = invI1 + invI2;
			
			// handle prismatic constraint between two fixed rotation bodies
			if (K.m11 <= Epsilon.E) {
				K.m11 = 1.0;
			}
			
			Vector2 impulsec = K.solve(C.negate());
			impulse = new Vector3(impulsec.x, impulsec.y, 0.0);
		}
		
		// compute the applied impulses
		// Pc = Jtrans * lambda
		
		// where Jtrans = |  perp   axis | excluding rotational elements
		//                | -perp  -axis |
		// we only compute the impulse for body1 since body2's impulse is
		// just the negative of body1's impulse
		Vector2 P = new Vector2();
		// perp.product(impulse.x) + axis.product(impulse.y)
		P.x = perp.x * impulse.x + impulse.z * axis.x;
		P.y = perp.y * impulse.x + impulse.z * axis.y;
		
		// where Jtrans = |  s1   a1 | excluding linear elements
		//                |   1    1 |
		//                | -s2  -a2 |
		double l1 = impulse.x * s1 + impulse.y + impulse.z * a1;
		double l2 = impulse.x * s2 + impulse.y + impulse.z * a2;
		
		// apply the impulse
		this.body1.translate(P.product(-invM1));
		this.body1.rotateAboutCenter(-l1 * invI1);
		
		this.body2.translate(P.product(invM2));
		this.body2.rotateAboutCenter(l2 * invI2);
		
		// return if we corrected the error enough
		return linearError <= linearTolerance && angularError <= angularTolerance;
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
	 * Returns the relative angle between the two bodies given the reference angle.
	 * @return double
	 */
	private double getRelativeRotation() {
		double rr = this.body2.getTransform().getRotationAngle() - this.body1.getTransform().getRotationAngle() - this.referenceAngle;
		if (rr < -Math.PI) rr += Geometry.TWO_PI;
		if (rr > Math.PI) rr -= Geometry.TWO_PI;
		return rr;
	}
	
	/**
	 * The original anchor point on body1 in world space.
	 * @return {@link Vector2}
	 */
	public Vector2 getAnchor1() {
		return this.body1.getWorldPoint(this.localAnchor1);
	}
	
	/**
	 * The original anchor point on body2 in world space.
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
		force.x = this.impulse.x * this.perp.x + (this.springImpulse + this.motorImpulse + this.lowerLimitImpulse - this.upperLimitImpulse) * this.axis.x;
		force.y = this.impulse.x * this.perp.y + (this.springImpulse + this.motorImpulse + this.lowerLimitImpulse - this.upperLimitImpulse) * this.axis.y;
		// multiply by invdt to obtain the force
		force.multiply(invdt);
		return force;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionTorque(double)
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return invdt * this.impulse.y;
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
	 * <p>
	 * Renamed from getJointSpeed in 5.0.0
	 * @return double
	 * @since 5.0.0
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
	 * Returns the current joint translation.
	 * <p>
	 * Renamed from getJointTranslation in 5.0.0
	 * @return double
	 * @since 5.0.0
	 */
	public double getLinearTranslation() {
		Vector2 p1 = this.body1.getWorldPoint(this.localAnchor1);
		Vector2 p2 = this.body2.getWorldPoint(this.localAnchor2);
		Vector2 d = p2.difference(p1);
		Vector2 axis = this.body1.getWorldVector(this.xAxis);
		return d.dot(axis);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearMotorJoint#isMotorEnabled()
	 */
	public boolean isMotorEnabled() {
		return this.motorEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearMotorJoint#setMotorEnabled(boolean)
	 */
	public void setMotorEnabled(boolean motorEnabled) {
		// only wake the bodies if the enable flag changed
		if (this.motorEnabled != motorEnabled) {
			// wake up the joined bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the new value
			this.motorEnabled = motorEnabled;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearMotorJoint#getMotorSpeed()
	 */
	public double getMotorSpeed() {
		return this.motorSpeed;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearMotorJoint#setMotorSpeed(double)
	 */
	public void setMotorSpeed(double motorSpeed) {
		// don't do anything if the motor speed isn't changing
		if (this.motorSpeed != motorSpeed) {
			// only wake up the bodies if the motor is currently enabled
			if (this.motorEnabled) {
				// wake up the joined bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			
			// set the new value
			this.motorSpeed = motorSpeed;
			this.motorImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearMotorJoint#getMotorMaximumForce()
	 */
	@Override
	public double getMaximumMotorForce() {
		return this.maximumMotorForce;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearMotorJoint#setMotorMaximumForce(double)
	 */
	@Override
	public void setMaximumMotorForce(double maximumMotorForce) {
		// make sure its greater than or equal to zero
		if (maximumMotorForce <= 0.0) 
			throw new ValueOutOfRangeException("maximumMotorForce", maximumMotorForce, ValueOutOfRangeException.MUST_BE_GREATER_THAN, 0.0);
		
		if (this.maximumMotorForce != maximumMotorForce) {
			this.maximumMotorForce = maximumMotorForce;
			
			if (this.motorEnabled && this.maximumMotorForceEnabled) {
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearMotorJoint#setMotorMaximumForceEnabled(boolean)
	 */
	@Override
	public void setMaximumMotorForceEnabled(boolean enabled) {
		if (this.maximumMotorForceEnabled != enabled) {
			this.maximumMotorForceEnabled = enabled;
			
			// only wake if necessary
			if (this.motorEnabled) {
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearMotorJoint#isMotorMaximumForceEnabled()
	 */
	@Override
	public boolean isMaximumMotorForceEnabled() {
		return this.maximumMotorForceEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.LinearMotorJoint#getMotorForce(double)
	 */
	public double getMotorForce(double invdt) {
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
		return this.springImpulse * invdt;
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
	 * @see org.dyn4j.dynamics.joint.LinearLimitsJoint#setLimits(double, double)
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
	 * @see org.dyn4j.dynamics.joint.LinearLimitsJoint#setLimitsEnabled(double, double)
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
	
	/**
	 * Returns the axis in which the joint is allowed move along in world coordinates.
	 * @return {@link Vector2}
	 * @since 3.0.0
	 */
	public Vector2 getAxis() {
		return this.body1.getWorldVector(this.xAxis);
	}
	
	/**
	 * Returns the reference angle.
	 * <p>
	 * The reference angle is the angle calculated when the joint was created from the
	 * two joined bodies.  The reference angle is the angular difference between the
	 * bodies.
	 * @return double
	 * @since 3.0.1
	 */
	public double getReferenceAngle() {
		return this.referenceAngle;
	}
	
	/**
	 * Sets the reference angle.
	 * <p>
	 * This method can be used to set the reference angle to override the computed
	 * reference angle from the constructor.  This is useful in recreating the joint
	 * from a current state.
	 * <p>
	 * This can also be used to override the initial angle between the bodies.
	 * @param angle the reference angle
	 * @see #getReferenceAngle()
	 * @since 3.0.1
	 */
	public void setReferenceAngle(double angle) {
		if (this.referenceAngle != angle) {
			this.referenceAngle = angle;
			
			// the reference angle is intrinsic to the prismatic joint
			// so if it changes we need to make sure both bodies are
			// awake to resolve the change
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
		}
	}
}
