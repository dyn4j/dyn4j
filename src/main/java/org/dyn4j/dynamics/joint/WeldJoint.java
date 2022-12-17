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
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Matrix33;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Vector3;
import org.dyn4j.resources.Messages;

/**
 * Implementation of a weld joint.
 * <p>
 * A weld joint joins two bodies together as if they were a single body.  Both
 * their relative linear and angular motion are constrained to keep them 
 * attached to each other.  The system as a whole can rotate and translate 
 * freely.
 * <p>
 * The bodies are locked in place in their current positions. The anchor point
 * is used to detect joint separation and to serve as the pivot point for the
 * angular spring.
 * <p>
 * This joint supports an angular spring using the 
 * {@link #setSpringEnabled(boolean)} method.  You can control the behavior of
 * the spring using the frequency, damping ratio, etc. settings.  A good 
 * starting point is a frequency of 8.0 and damping ratio of 0.3 then adjust 
 * as necessary.
 * <p>
 * This joint also supports angular limits, but only when the spring is
 * enabled.  Its recommended that you use limits when using the angular spring
 * to prevent wrap-around (when the relative angle between the bodies changes
 * from 180 to -180 for example).
 * <p>
 * By default the lower and upper limit angles are set to the current angle 
 * between the bodies.  When the lower and upper limits are equal, the bodies 
 * rotate together and are not allowed rotate relative to one another.  By
 * default the limits are disabled.  If you want the lower and upper limit
 * to be the same, disable the spring/limits for a more stable simulation.
 * <p>
 * If the lower and upper limits are set explicitly, the values must follow 
 * these restrictions:
 * <ul>
 * <li>lower limit &le; upper limit</li>
 * <li>lower limit &gt; -180</li>
 * <li>upper limit &lt; 180</li>
 * </ul> 
 * To create a joint with limits outside of this range use the 
 * {@link #setLimitsReferenceAngle(double)} method.  This method sets the 
 * baseline angle for the joint, which represents 0 radians in the context of
 * the limits.  For example:
 * <pre>
 * // we would like the joint limits to be [30, 260]
 * // this is the same as the limits [-60, 170] if the reference angle is 90
 * joint.setLimits(Math.toRadians(-60), Math.toRadians(170));
 * joint.setReferenceAngle(Math.toRadians(90));
 * </pre>
 * @author William Bittle
 * @version 5.0.0
 * @since 1.0.0
 * @see <a href="https://www.dyn4j.org/pages/joints#Weld_Joint" target="_blank">Documentation</a>
 * @see <a href="https://www.dyn4j.org/2010/12/weld-constraint/" target="_blank">Weld Constraint</a>
 * @param <T> the {@link PhysicsBody} type
 */
public class WeldJoint<T extends PhysicsBody> extends AbstractPairedBodyJoint<T> implements AngularLimitsJoint, AngularSpringJoint, PairedBodyJoint<T>, Joint<T>, Shiftable, DataContainer, Ownable {
	/** The local anchor point on the first {@link PhysicsBody} */
	protected final Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link PhysicsBody} */
	protected final Vector2 localAnchor2;

	// limits
	
	/** Whether the {@link Joint} limits are enabled or not */
	protected boolean limitsEnabled;
	
	/** The upper limit of the {@link Joint} */
	protected double upperLimit;
	
	/** The lower limit of the {@link Joint} */
	protected double lowerLimit;

	/** The initial angle between the two {@link PhysicsBody}s */
	protected double referenceAngle;
	
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
	protected boolean springMaximumTorqueEnabled;
	
	/** The maximum force the spring will apply */
	protected double springMaximumTorque;

	// current state

	/** The current angle between the bodies */
	private double angle;

	/** The world space vector from b1's COM to the pivot point */
	private Vector2 r1;
	
	/** The world space vector from b2's COM to the pivot point */
	private Vector2 r2;

	/** The constraint mass; K = J * Minv * Jtrans */
	private final Matrix33 K;
	
	/** The axial mass for limits */
	private double axialMass;
	
	/** The soft mass for angular spring */
	private double springMass;

	/** The damping coefficient of the spring-damper */
	private double damping;

	/** The bias for adding work to the constraint (simulating a spring) */
	private double bias;
	
	/** The damping portion of the constraint */
	private double gamma;

	// output
	
	/** The accumulated impulse for warm starting */
	private Vector3 impulse;
	
	/** The spring impulse */
	private double springImpulse;
	
	/** The impulse applied by the lower limit */
	private double lowerLimitImpulse;
	
	/** The impulse applied by the upper limit */
	private double upperLimitImpulse;

	/**
	 * Minimal constructor.
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @param anchor the anchor point in world coordinates
	 * @throws NullPointerException if body1, body2, or anchor is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public WeldJoint(T body1, T body2, Vector2 anchor) {
		super(body1, body2);
		// check for a null anchor
		if (anchor == null) throw new NullPointerException(Messages.getString("dynamics.joint.nullAnchor"));
		
		// set the anchor point
		this.localAnchor1 = body1.getLocalPoint(anchor);
		this.localAnchor2 = body2.getLocalPoint(anchor);
		// set the reference angle
		this.referenceAngle = body1.getTransform().getRotationAngle() - body2.getTransform().getRotationAngle();
		
		// initialize
		this.K = new Matrix33();
		this.r1 = null;
		this.r2 = null;

		// spring
		this.springMode = SPRING_MODE_FREQUENCY;
		this.springEnabled = false;
		this.springDamperEnabled = false;
		this.springMaximumTorque = 1000.0;
		this.springMaximumTorqueEnabled = false;
		this.springStiffness = 0.0;
		this.springFrequency = 8.0;
		this.springDampingRatio = 0.3;
		
		// limits
		this.limitsEnabled = false;
		this.lowerLimit = this.referenceAngle;
		this.upperLimit = this.referenceAngle;
		
		this.angle = 0.0;
		this.axialMass = 0.0;
		this.springMass = 0.0;
		this.damping = 0.0;
		this.gamma = 0.0;
		this.bias = 0.0;
		
		this.impulse = new Vector3();
		this.springImpulse = 0.0;
		this.lowerLimitImpulse = 0.0;
		this.upperLimitImpulse = 0.0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WeldJoint[").append(super.toString())
		  .append("|Anchor=").append(this.getAnchor1())
		  .append("|ReferenceAngle=").append(this.referenceAngle)
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
		
		this.angle = this.getRelativeRotation();
		
		this.r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		this.r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		// compute the K inverse matrix
		this.K.m00 = invM1 + invM2 + this.r1.y * this.r1.y * invI1 + this.r2.y * this.r2.y * invI2;
		this.K.m01 = -this.r1.y * this.r1.x * invI1 - this.r2.y * this.r2.x * invI2;
		this.K.m02 = -this.r1.y * invI1 - this.r2.y * invI2;
		this.K.m10 = this.K.m01;
		this.K.m11 = invM1 + invM2 + this.r1.x * this.r1.x * invI1 + this.r2.x * this.r2.x * invI2;
		this.K.m12 = this.r1.x * invI1 + this.r2.x * invI2;
		this.K.m20 = this.K.m02;
		this.K.m21 = this.K.m12;
		this.K.m22 = invI1 + invI2;
		
		// compute the axial mass
		this.axialMass = this.K.m22;
		if (this.axialMass > Epsilon.E) {
			this.axialMass = 1.0 / this.axialMass;
		} else {
			this.axialMass = 0.0;
		}

		if (this.springEnabled) {
			// recompute spring reduced mass (m), stiffness (k), and damping (d)
			// since frequency, dampingRatio, or the masses of the joined bodies
			// could change
			double dt = step.getDeltaTime();
			double invI = this.K.m22;
			
			// recompute spring coefficients
			this.updateSpringCoefficients();
			
			// compute the CIM
			this.gamma = getConstraintImpulseMixing(dt, this.springStiffness, this.damping);
			
			// compute the ERP
			double erp = getErrorReductionParameter(dt, this.springStiffness, this.damping);
			
			// compute the bais 
			// bias = x * ERP
			this.bias = this.angle * erp;
			
			// compute the effective mass
			invI += this.gamma;
			// check for zero before inverting
			this.springMass = invI <= Epsilon.E ? 0.0 : 1.0 / invI;
		} else {
			this.springMass = 0.0;
			this.gamma = 0.0;
			this.bias = 0.0;
			this.springImpulse = 0.0;
		}
		
		// account for variable time step
		if (settings.isWarmStartingEnabled()) {
			double dtr = step.getDeltaTimeRatio();
			this.impulse.multiply(dtr);
			this.springImpulse *= dtr;
			this.lowerLimitImpulse *= dtr;
			this.upperLimitImpulse *= dtr;
			
			double axialImpulse = this.impulse.z + this.springImpulse + this.lowerLimitImpulse - this.upperLimitImpulse;
			
			// warm start
			Vector2 impulse = new Vector2(this.impulse.x, this.impulse.y);
			this.body1.getLinearVelocity().add(impulse.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * (this.r1.cross(impulse) + axialImpulse));
			this.body2.getLinearVelocity().subtract(impulse.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * (this.r2.cross(impulse) + axialImpulse));
		} else {
			this.impulse.zero();
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
		
		// check if the spring is enabled
		// we do this because stiffness is a function of the frequency
		// and the mass of the bodies (both of which could make stiffness zero)
		if (this.springEnabled) {
			// get the relative angular velocity
			double rav = this.body1.getAngularVelocity() - this.body2.getAngularVelocity();
			// solve for the spring/damper impulse
			double springStepImpulse = -this.springMass * (rav + this.bias + this.gamma * this.springImpulse);
			
			// clamp to max spring torque
			if (this.springMaximumTorqueEnabled) {
				double currentAccumulatedImpulse = this.springImpulse;
				double maxImpulse = this.springMaximumTorque * step.getDeltaTime();
				this.springImpulse = Interval.clamp(this.springImpulse + springStepImpulse, -maxImpulse, maxImpulse);
				springStepImpulse = this.springImpulse - currentAccumulatedImpulse;
			} else {
				this.springImpulse += springStepImpulse;
			}
			
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * springStepImpulse);
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * springStepImpulse);
			
			// solve the limit constraints
			// (limits only apply when the angular spring is enabled)
			if (this.limitsEnabled && this.axialMass > 0.0) {
				// lower limit
				{
					double C = this.angle - this.lowerLimit;
					double Cdot = this.body1.getAngularVelocity() - this.body2.getAngularVelocity();
					double stepImpulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * step.getInverseDeltaTime());
					
					// clamp
					double currentAccumulatedImpulse = this.lowerLimitImpulse;
					this.lowerLimitImpulse = Math.max(this.lowerLimitImpulse + stepImpulse, 0.0);
					stepImpulse = this.lowerLimitImpulse - currentAccumulatedImpulse;
					
					this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * stepImpulse);
					this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * stepImpulse);
				}
				
				// upper limit
				{
					double C = this.upperLimit - this.angle;
					double Cdot = this.body2.getAngularVelocity() - this.body1.getAngularVelocity();
					double stepImpulse = -this.axialMass * (Cdot + Math.max(C, 0.0) * step.getInverseDeltaTime());
					
					// clamp
					double currentAccumulatedImpulse = this.upperLimitImpulse;
					this.upperLimitImpulse = Math.max(this.upperLimitImpulse + stepImpulse, 0.0);
					stepImpulse = this.upperLimitImpulse - currentAccumulatedImpulse;
					
					this.body1.setAngularVelocity(this.body1.getAngularVelocity() - invI1 * stepImpulse);
					this.body2.setAngularVelocity(this.body2.getAngularVelocity() + invI2 * stepImpulse);
				}
			}
			
			// solve the point-to-point constraint
			Vector2 v1 = this.body1.getLinearVelocity().sum(this.r1.cross(this.body1.getAngularVelocity()));
			Vector2 v2 = this.body2.getLinearVelocity().sum(this.r2.cross(this.body2.getAngularVelocity()));
			Vector2 relv = v1.subtract(v2);
			
			Vector2 stepImpulse = this.K.solve22(relv).negate();
			this.impulse.x += stepImpulse.x;
			this.impulse.y += stepImpulse.y;
			
			this.body1.getLinearVelocity().add(stepImpulse.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * this.r1.cross(stepImpulse));
			this.body2.getLinearVelocity().subtract(stepImpulse.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * this.r2.cross(stepImpulse));
		} else {
			Vector2 v1 = this.body1.getLinearVelocity().sum(this.r1.cross(this.body1.getAngularVelocity()));
			Vector2 v2 = this.body2.getLinearVelocity().sum(this.r2.cross(this.body2.getAngularVelocity()));
			Vector2 relv = v1.subtract(v2);
			Vector3 C = new Vector3(relv.x, relv.y, this.body1.getAngularVelocity() - this.body2.getAngularVelocity());
			
			Vector3 stepImpulse = null;
			if (this.K.m22 > 0.0) {
				stepImpulse = this.K.solve33(C.negate());
			} else {
				Vector2 impulse2 = this.K.solve22(relv).negate();
				stepImpulse = new Vector3(impulse2.x, impulse2.y, 0.0);
			}
			this.impulse.add(stepImpulse);
			
			// apply the impulse
			Vector2 imp = new Vector2(stepImpulse.x, stepImpulse.y);
			this.body1.getLinearVelocity().add(imp.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * (this.r1.cross(imp) + stepImpulse.z));
			this.body2.getLinearVelocity().subtract(imp.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * (this.r2.cross(imp) + stepImpulse.z));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#solvePositionConstraints(org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	@Override
	public boolean solvePositionConstraints(TimeStep step, Settings settings) {
		double linearTolerance = settings.getLinearTolerance();
		double angularTolerance = settings.getAngularTolerance();
		double maxAngularCorrection = settings.getMaximumAngularCorrection();
		
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
		
		Vector2 p1 = this.body1.getWorldCenter().add(r1);
		Vector2 p2 = this.body2.getWorldCenter().add(r2);
		Vector2 relativePosition = p1.difference(p2);
		double  relativeRotation = this.getRelativeRotation();
		Vector3 C = new Vector3(relativePosition.x, relativePosition.y, relativeRotation);
		
		double linearError = relativePosition.getMagnitude();
		double angularError = Math.abs(relativeRotation);
		
		Matrix33 K = new Matrix33();
		
		// compute the K inverse matrix
		K.m00 = invM1 + invM2 + r1.y * r1.y * invI1 + r2.y * r2.y * invI2;
		K.m01 = -r1.y * r1.x * invI1 - r2.y * r2.x * invI2;
		K.m02 = -r1.y * invI1 - r2.y * invI2;
		K.m10 = K.m01;
		K.m11 = invM1 + invM2 + r1.x * r1.x * invI1 + r2.x * r2.x * invI2;
		K.m12 = r1.x * invI1 + r2.x * invI2;
		K.m20 = K.m02;
		K.m21 = K.m12;
		K.m22 = invI1 + invI2;
		
		// check if spring is enabled
		// we do this because stiffness is a function of the frequency
		// and the mass of the bodies (both of which could make stiffness zero)
		if (this.springEnabled) {
			angularError = 0.0;
			
			// solve limits
			if (this.limitsEnabled && this.axialMass > 0.0) {
				double C3 = 0.0;
				if (Math.abs(this.upperLimit - this.lowerLimit) < 2.0 * angularTolerance) {
					C3 = Interval.clamp(relativeRotation - this.lowerLimit, -maxAngularCorrection, maxAngularCorrection);
				} else if (relativeRotation <= this.lowerLimit) {
					C3 = Interval.clamp(relativeRotation - this.lowerLimit + angularTolerance, -maxAngularCorrection, 0.0);
				} else if (relativeRotation >= this.upperLimit) {
					C3 = Interval.clamp(relativeRotation - this.upperLimit - angularTolerance, 0.0, maxAngularCorrection);
				}
				
				double impulse = -this.axialMass * C3;
				this.body1.rotateAboutCenter(invI1 * impulse);
				this.body2.rotateAboutCenter(-invI2 * impulse);
				angularError = Math.abs(C3);
			}
			
			// then solve the linear constraint
			Vector2 j = K.solve22(relativePosition).negate();
			
			this.body1.translate(j.product(invM1));
			this.body1.rotateAboutCenter(invI1 * r1.cross(j));
			this.body2.translate(j.product(-invM2));
			this.body2.rotateAboutCenter(-invI2 * r2.cross(j));
		} else {
			Vector3 impulse = null;
			
			if (K.m22 > 0.0) {
				impulse = K.solve33(C.negate());
			} else {
				Vector2 impulse2 = K.solve22(relativePosition).negate();
				impulse = new Vector3(impulse2.x, impulse2.y, 0.0);
			}
	
			// translate and rotate the objects
			Vector2 imp = new Vector2(impulse.x, impulse.y);
			this.body1.translate(imp.product(invM1));
			this.body1.rotateAboutCenter(invI1 * (r1.cross(imp) + impulse.z));
			this.body2.translate(imp.product(-invM2));
			this.body2.rotateAboutCenter(-invI2 * (r2.cross(imp) + impulse.z));
		}
		
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
		double rr = this.body1.getTransform().getRotationAngle() - this.body2.getTransform().getRotationAngle() - this.referenceAngle;
		if (rr < -Math.PI) rr += Geometry.TWO_PI;
		if (rr > Math.PI) rr -= Geometry.TWO_PI;
		return rr;
	}
	
	/**
	 * Returns the world space anchor point for the first body.
	 * @return {@link Vector2}
	 */
	public Vector2 getAnchor1() {
		return this.body1.getWorldPoint(this.localAnchor1);
	}
	
	/**
	 * Returns the world space anchor point for the second body.
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
		Vector2 impulse = new Vector2(this.impulse.x, this.impulse.y);
		return impulse.multiply(invdt);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionTorque(double)
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return (this.impulse.z + this.springImpulse + this.lowerLimitImpulse - this.upperLimitImpulse) * invdt;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		// nothing to translate here since the anchor points are in local coordinates
		// they will move with the bodies
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularSpringJoint#getSpringDampingRatio()
	 */
	@Override
	public double getSpringDampingRatio() {
		return this.springDampingRatio;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularSpringJoint#setSpringDampingRatio(double)
	 */
	@Override
	public void setSpringDampingRatio(double dampingRatio) {
		// make sure its within range
		if (dampingRatio <= 0 || dampingRatio > 1) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidDampingRatio"));
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
	 * @see org.dyn4j.dynamics.joint.AngularSpringJoint#getSpringFrequency()
	 */
	@Override
	public double getSpringFrequency() {
		return this.springFrequency;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularSpringJoint#getSpringStiffness()
	 */
	@Override
	public double getSpringStiffness() {
		return this.springStiffness;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularSpringJoint#setSpringFrequency(double)
	 */
	@Override
	public void setSpringFrequency(double frequency) {
		// check for valid value
		if (frequency <= 0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidFrequency"));
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
	 * @see org.dyn4j.dynamics.joint.AngularSpringJoint#setSpringStiffness(double)
	 */
	@Override
	public void setSpringStiffness(double stiffness) {
		// check for valid value
		if (stiffness <= 0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidStiffness"));
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
	 * @see org.dyn4j.dynamics.joint.AngularSpringJoint#getSpringMaximumTorque()
	 */
	@Override
	public double getMaximumSpringTorque() {
		return this.springMaximumTorque;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularSpringJoint#setSpringMaximumTorque(double)
	 */
	@Override
	public void setMaximumSpringTorque(double maximum) {
		// check for valid value
		if (maximum <= 0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidSpringMaximumTorque"));
		// check if changed
		if (this.springMaximumTorque != maximum) {
			this.springMaximumTorque = maximum;
			// wake up the bodies
			if (this.springEnabled && this.springMaximumTorqueEnabled) {
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularSpringJoint#isSpringMaximumTorqueEnabled()
	 */
	@Override
	public boolean isMaximumSpringTorqueEnabled() {
		return this.springMaximumTorqueEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularSpringJoint#setSpringMaximumTorqueEnabled(boolean)
	 */
	@Override
	public void setMaximumSpringTorqueEnabled(boolean enabled) {
		if (this.springMaximumTorqueEnabled != enabled) {
			this.springMaximumTorqueEnabled = enabled;
			
			if (this.springEnabled) {
				// wake the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularSpringJoint#isSpringEnabled()
	 */
	public boolean isSpringEnabled() {
		return this.springEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularSpringJoint#setSpringEnabled(boolean)
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
	 * @see org.dyn4j.dynamics.joint.AngularSpringJoint#isSpringDamperEnabled()
	 */
	public boolean isSpringDamperEnabled() {
		return this.springDamperEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularSpringJoint#setSpringDamperEnabled(boolean)
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
	 * @see org.dyn4j.dynamics.joint.AngularSpringJoint#getSpringTorque(double)
	 */
	@Override
	public double getSpringTorque(double invdt) {
		return this.springImpulse * invdt;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#isLimitsEnabled()
	 */
	public boolean isLimitsEnabled() {
		return this.limitsEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimitsEnabled(boolean)
	 */
	public void setLimitsEnabled(boolean flag) {
		// check if its changing
		if (this.limitsEnabled != flag) {
			// wake up both bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the new value
			this.limitsEnabled = flag;
			// clear the accumulated limit impulse
			this.lowerLimitImpulse = 0.0;
			this.upperLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#getUpperLimit()
	 */
	public double getUpperLimit() {
		return this.upperLimit;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setUpperLimit(double)
	 */
	public void setUpperLimit(double upperLimit) {
		if (upperLimit < this.lowerLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidUpperLimit"));
		if (this.upperLimit != upperLimit) {
			// only wake the bodies if the motor is enabled and the limit has changed
			if (this.limitsEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new value
			this.upperLimit = upperLimit;
			// clear accumulated impulse
			this.upperLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#getLowerLimit()
	 */
	public double getLowerLimit() {
		return this.lowerLimit;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLowerLimit(double)
	 */
	public void setLowerLimit(double lowerLimit) {
		if (lowerLimit > this.upperLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLowerLimit"));
		if (this.lowerLimit != lowerLimit) {
			// only wake the bodies if the motor is enabled and the limit has changed
			if (this.limitsEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the new value
			this.lowerLimit = lowerLimit;
			// clear accumulated impulse
			this.lowerLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimits(double, double)
	 */
	public void setLimits(double lowerLimit, double upperLimit) {
		if (lowerLimit > upperLimit) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidLimits"));
		if (this.lowerLimit != lowerLimit || this.upperLimit != upperLimit) {
			// only wake the bodies if the motor is enabled and one of the limits has changed
			if (this.limitsEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the values
			this.lowerLimit = lowerLimit;
			this.upperLimit = upperLimit;
			// clear accumulated impulse
			this.lowerLimitImpulse = 0.0;
			this.upperLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimitsEnabled(double, double)
	 */
	public void setLimitsEnabled(double lowerLimit, double upperLimit) {
		// enable the limits
		this.setLimitsEnabled(true);
		// set the values
		this.setLimits(lowerLimit, upperLimit);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimitsEnabled(double)
	 */
	public void setLimitsEnabled(double limit) {
		this.setLimitsEnabled(limit, limit);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimits(double)
	 */
	public void setLimits(double limit) {
		if (this.lowerLimit != limit || this.upperLimit != limit) {
			if (this.limitsEnabled) {
				// wake up the bodies
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
			// set the limits
			this.upperLimit = limit;
			this.lowerLimit = limit;
			// clear accumulated impulse
			this.lowerLimitImpulse = 0.0;
			this.upperLimitImpulse = 0.0;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#getLimitsReferenceAngle()
	 */
	@Override
	public double getLimitsReferenceAngle() {
		return this.referenceAngle;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AngularLimitsJoint#setLimitsReferenceAngle(double)
	 */
	@Override
	public void setLimitsReferenceAngle(double angle) {
		if (this.referenceAngle != angle) {
			this.referenceAngle = angle;
			
			if (this.limitsEnabled) {
				this.body1.setAtRest(false);
				this.body2.setAtRest(false);
			}
		}
	}
}
