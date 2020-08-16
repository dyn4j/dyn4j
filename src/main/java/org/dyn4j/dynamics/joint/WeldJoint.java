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
import org.dyn4j.geometry.Geometry;
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
 * A weld joint joins two bodies together as if they were a single body with
 * two fixtures.  Both their relative linear and angular motion are constrained
 * to keep them attached to each other.  The system as a whole can rotate and 
 * translate freely.
 * <p>
 * Using a frequency greater than zero allows the joint to function as a
 * torsion spring about the anchor point.  A good starting point is a frequency
 * of 8.0 and damping ratio of 0.3 then adjust as necessary.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 * @see <a href="http://www.dyn4j.org/documentation/joints/#Weld_Joint" target="_blank">Documentation</a>
 * @see <a href="http://www.dyn4j.org/2010/12/weld-constraint/" target="_blank">Weld Constraint</a>
 * @param <T> the {@link PhysicsBody} type
 */
public class WeldJoint<T extends PhysicsBody> extends Joint<T> implements Shiftable, DataContainer {
	/** The local anchor point on the first {@link PhysicsBody} */
	protected final Vector2 localAnchor1;
	
	/** The local anchor point on the second {@link PhysicsBody} */
	protected final Vector2 localAnchor2;

	/** The initial angle between the two {@link PhysicsBody}s */
	protected double referenceAngle;
	
	/** The oscillation frequency in hz */
	protected double frequency;
	
	/** The damping ratio */
	protected double dampingRatio;

	// current state

	/** The stiffness (k) of the spring */
	private double stiffness;
	
	/** The damping coefficient of the spring-damper */
	private double damping;

	/** The constraint mass; K = J * Minv * Jtrans */
	private final Matrix33 K;
	
	/** The bias for adding work to the constraint (simulating a spring) */
	private double bias;
	
	/** The damping portion of the constraint */
	private double gamma;

	// output
	
	/** The accumulated impulse for warm starting */
	private Vector3 impulse;

	/**
	 * Minimal constructor.
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @param anchor the anchor point in world coordinates
	 * @throws NullPointerException if body1, body2, or anchor is null
	 * @throws IllegalArgumentException if body1 == body2
	 */
	public WeldJoint(T body1, T body2, Vector2 anchor) {
		super(body1, body2, false);
		// verify the bodies are not the same instance
		if (body1 == body2) throw new IllegalArgumentException(Messages.getString("dynamics.joint.sameBody"));
		// check for a null anchor
		if (anchor == null) throw new NullPointerException(Messages.getString("dynamics.joint.nullAnchor"));
		// set the anchor point
		this.localAnchor1 = body1.getLocalPoint(anchor);
		this.localAnchor2 = body2.getLocalPoint(anchor);
		// set the reference angle
		this.referenceAngle = body1.getTransform().getRotationAngle() - body2.getTransform().getRotationAngle();
		// initialize
		this.stiffness = 0.0;
		this.damping = 0.0;
		this.K = new Matrix33();
		this.impulse = new Vector3();
		this.frequency = 0.0;
		this.dampingRatio = 0.0;
		this.gamma = 0.0;
		this.bias = 0.0;
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
		
		// compute the K inverse matrix
		this.K.m00 = invM1 + invM2 + r1.y * r1.y * invI1 + r2.y * r2.y * invI2;
		this.K.m01 = -r1.y * r1.x * invI1 - r2.y * r2.x * invI2;
		this.K.m02 = -r1.y * invI1 - r2.y * invI2;
		this.K.m10 = this.K.m01;
		this.K.m11 = invM1 + invM2 + r1.x * r1.x * invI1 + r2.x * r2.x * invI2;
		this.K.m12 = r1.x * invI1 + r2.x * invI2;
		this.K.m20 = this.K.m02;
		this.K.m21 = this.K.m12;
		this.K.m22 = invI1 + invI2;

		// recompute spring reduced mass (m), stiffness (k), and damping (d)
		// since frequency, dampingRatio, or the masses of the joined bodies
		// could change
		if (this.frequency > 0.0) {
			double lm = this.getReducedInertia();
			double nf = this.getNaturalFrequency(this.frequency);
			
			this.stiffness = this.getSpringStiffness(lm, nf);
			this.damping = this.getSpringDampingCoefficient(lm, nf, this.dampingRatio);
		} else {
			this.stiffness = 0.0;
			this.damping = 0.0;
		}
		
		// see if we need to compute spring damping
		if (this.stiffness > 0.0) {
			double dt = step.getDeltaTime();
			double invI = invI1 + invI2;

			// compute the current angle between relative to the reference angle
			double r = this.getRelativeRotation();

			// compute the CIM
			this.gamma = this.getConstraintImpulseMixing(dt, this.stiffness, this.damping);
			
			// compute the ERP
			double erp = this.getErrorReductionParameter(dt, this.stiffness, this.damping);
			
			// compute the bais 
			// bias = x * ERP
			this.bias = r * erp;
			
			// compute the effective mass
			invI += this.gamma;
			// check for zero before inverting
			this.K.m22 = invI <= Epsilon.E ? 0.0 : 1.0 / invI;
		} else {
			this.gamma = 0.0;
			this.bias = 0.0;
		}
		
		// account for variable time step
		this.impulse.multiply(step.getDeltaTimeRatio());
		
		// warm start
		Vector2 impulse = new Vector2(this.impulse.x, this.impulse.y);
		this.body1.getLinearVelocity().add(impulse.product(invM1));
		this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * (r1.cross(impulse) + this.impulse.z));
		this.body2.getLinearVelocity().subtract(impulse.product(invM2));
		this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * (r2.cross(impulse) + this.impulse.z));
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
		
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		// check if the spring is enabled
		// we do this because stiffness is a function of the frequency
		// and the mass of the bodies (both of which could make stiffness zero)
		if (this.stiffness > 0.0) {
			// get the relative angular velocity
			double rav = this.body1.getAngularVelocity() - this.body2.getAngularVelocity();
			// solve for the spring/damper impulse
			double j2 = -this.K.m22 * (rav + this.bias + this.gamma * this.impulse.z);
			this.impulse.z += j2;
			
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * j2);
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * j2);
			
			// solve the point-to-point and angle constraint
			Vector2 v1 = this.body1.getLinearVelocity().sum(r1.cross(this.body1.getAngularVelocity()));
			Vector2 v2 = this.body2.getLinearVelocity().sum(r2.cross(this.body2.getAngularVelocity()));
			Vector2 anchorV = v1.subtract(v2);
			
			Vector2 j1 = this.K.solve22(anchorV).negate();
			this.impulse.x += j1.x;
			this.impulse.y += j1.y;
			
			this.body1.getLinearVelocity().add(j1.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * r1.cross(j1));
			this.body2.getLinearVelocity().subtract(j1.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * r2.cross(j1));
		} else {
			Vector2 v1 = this.body1.getLinearVelocity().sum(r1.cross(this.body1.getAngularVelocity()));
			Vector2 v2 = this.body2.getLinearVelocity().sum(r2.cross(this.body2.getAngularVelocity()));
			Vector2 anchorV = v1.subtract(v2);
			Vector3 C = new Vector3(anchorV.x, anchorV.y, this.body1.getAngularVelocity() - this.body2.getAngularVelocity());
			
			Vector3 impulse = null;
			if (this.K.m22 > 0.0) {
				impulse = this.K.solve33(C.negate());
			} else {
				Vector2 impulse2 = this.K.solve22(anchorV).negate();
				impulse = new Vector3(impulse2.x, impulse2.y, 0.0);
			}
			this.impulse.add(impulse);
			
			// apply the impulse
			Vector2 imp = new Vector2(impulse.x, impulse.y);
			this.body1.getLinearVelocity().add(imp.product(invM1));
			this.body1.setAngularVelocity(this.body1.getAngularVelocity() + invI1 * (r1.cross(imp) + impulse.z));
			this.body2.getLinearVelocity().subtract(imp.product(invM2));
			this.body2.setAngularVelocity(this.body2.getAngularVelocity() - invI2 * (r2.cross(imp) + impulse.z));
		}
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
		
		Vector2 r1 = t1.getTransformedR(this.body1.getLocalCenter().to(this.localAnchor1));
		Vector2 r2 = t2.getTransformedR(this.body2.getLocalCenter().to(this.localAnchor2));
		
		Vector2 p1 = this.body1.getWorldCenter().add(r1);
		Vector2 p2 = this.body2.getWorldCenter().add(r2);
		Vector2 C1 = p1.difference(p2);
		double  C2 = this.getRelativeRotation();
		Vector3 C = new Vector3(C1.x, C1.y, C2);
		
		double linearError = C1.getMagnitude();
		double angularError = Math.abs(C2);
		
		// compute the K inverse matrix
		this.K.m00 = invM1 + invM2 + r1.y * r1.y * invI1 + r2.y * r2.y * invI2;
		this.K.m01 = -r1.y * r1.x * invI1 - r2.y * r2.x * invI2;
		this.K.m02 = -r1.y * invI1 - r2.y * invI2;
		this.K.m10 = this.K.m01;
		this.K.m11 = invM1 + invM2 + r1.x * r1.x * invI1 + r2.x * r2.x * invI2;
		this.K.m12 = r1.x * invI1 + r2.x * invI2;
		this.K.m20 = this.K.m02;
		this.K.m21 = this.K.m12;
		this.K.m22 = invI1 + invI2;
		
		// check if spring is enabled
		// we do this because stiffness is a function of the frequency
		// and the mass of the bodies (both of which could make stiffness zero)
		if (this.stiffness > 0.0) {
			// only solve the linear constraint
			angularError = 0.0;
			Vector2 j = this.K.solve22(C1).negate();
			
			this.body1.translate(j.product(invM1));
			this.body1.rotateAboutCenter(invI1 * r1.cross(j));
			this.body2.translate(j.product(-invM2));
			this.body2.rotateAboutCenter(-invI2 * r2.cross(j));
		} else {
			Vector3 impulse = null;
			
			if (this.K.m22 > 0.0) {
				impulse = this.K.solve33(C.negate());
			} else {
				Vector2 impulse2 = this.K.solve22(C1).negate();
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
	 * Returns the relative angle between the two bodies given the reference angle.
	 * @return double
	 */
	private double getRelativeRotation() {
		double rr = this.body1.getTransform().getRotationAngle() - this.body2.getTransform().getRotationAngle() - this.referenceAngle;
		if (rr < -Math.PI) rr += Geometry.TWO_PI;
		if (rr > Math.PI) rr -= Geometry.TWO_PI;
		return rr;
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
		Vector2 impulse = new Vector2(this.impulse.x, this.impulse.y);
		return impulse.multiply(invdt);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getReactionTorque(double)
	 */
	@Override
	public double getReactionTorque(double invdt) {
		return this.impulse.z * invdt;
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
	 * Returns true if this distance joint is a spring distance joint.
	 * @return boolean
	 * @since 3.0.1
	 * @deprecated Deprecated in 4.0.0. Use the {@link #isSpringEnabled()} method instead.
	 */
	@Deprecated
	public boolean isSpring() {
		return this.frequency > 0.0;
	}
	
	/**
	 * Returns true if this distance joint is a spring distance joint
	 * with damping.
	 * @return boolean
	 * @since 3.0.1
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
	 * @since 3.0.1
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
	 * @since 3.0.1
	 */
	public void setDampingRatio(double dampingRatio) {
		// make sure its within range
		if (dampingRatio < 0 || dampingRatio > 1) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidDampingRatio"));
		// set the new value
		this.dampingRatio = dampingRatio;
	}
	
	/**
	 * Returns the spring frequency.
	 * @return double
	 * @since 3.0.1
	 */
	public double getFrequency() {
		return this.frequency;
	}
	
	/**
	 * Sets the spring frequency.
	 * <p>
	 * Larger values increase the stiffness of the spring.
	 * @param frequency the spring frequency in hz; must be greater than or equal to zero
	 * @throws IllegalArgumentException if frequency is less than zero
	 * @since 3.0.1
	 */
	public void setFrequency(double frequency) {
		// check for valid value
		if (frequency < 0) throw new IllegalArgumentException(Messages.getString("dynamics.joint.invalidFrequency"));
		// set the new value
		this.frequency = frequency;
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
	 * @param angle the reference angle in radians
	 * @see #getReferenceAngle()
	 * @since 3.0.1
	 */
	public void setReferenceAngle(double angle) {
		this.referenceAngle = angle;
	}
}
