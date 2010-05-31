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
package org.dyn4j.game2d.dynamics.joint;

import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.Settings;
import org.dyn4j.game2d.dynamics.Step;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Matrix;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Represents a pivot joint.
 * @author William Bittle
 */
// TODO implement motor constraint
public class RevoluteJoint extends Joint {
	/** The local anchor point on the first {@link Body} */
	protected Vector localAnchor1;
	
	/** The local anchor point on the second {@link Body} */
	protected Vector localAnchor2;
	
	/** The pivot mass */
	protected Matrix K;
	
	/** The pivot force */
	protected Vector pivotForce = new Vector();
	
	/**
	 * Full constructor.
	 * @param b1 the first {@link Body}
	 * @param b2 the second {@link Body}
	 * @param anchor the anchor point in world coordinates
	 */
	public RevoluteJoint(Body b1, Body b2, Vector anchor) {
		super(b1, b2);
		if (anchor == null) throw new NullPointerException("The anchor point cannot be null.");
		this.localAnchor1 = b1.getLocalPoint(anchor);
		this.localAnchor2 = b2.getLocalPoint(anchor);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("REVOLUTE_JOINT[")
		.append(super.toString()).append("|")
		.append(this.localAnchor1).append("|")
		.append(this.localAnchor2).append("|")
		.append(this.pivotForce).append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#initializeConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	@Override
	public void initializeConstraints(Step step) {
		Transform t1 = this.b1.getTransform();
		Transform t2 = this.b2.getTransform();
		
		Mass m1 = this.b1.getMass();
		Mass m2 = this.b2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		Vector r1 = t1.getTransformedR(this.b1.getLocalCenter().to(this.localAnchor1));
		Vector r2 = t2.getTransformedR(this.b2.getLocalCenter().to(this.localAnchor2));
		
		// compute the K inverse matrix
		Matrix K1 = new Matrix();
		K1.m00 = invM1 + invM2;	K1.m01 = 0.0;
		K1.m10 = 0.0;			K1.m11 = invM1 + invM2;

		Matrix K2 = new Matrix();
		K2.m00 =  invI1 * r1.y * r1.y;	K2.m01 = -invI1 * r1.x * r1.y;
		K2.m10 = -invI1 * r1.x * r1.y;	K2.m11 =  invI1 * r1.x * r1.x;

		Matrix K3 = new Matrix();
		K3.m00 =  invI2 * r2.y * r2.y;	K3.m01 = -invI2 * r2.x * r2.y;
		K3.m10 = -invI2 * r2.x * r2.y;	K3.m11 =  invI2 * r2.x * r2.x;

		Matrix K = new Matrix();
		K.add(K1).add(K2).add(K3);
		
		this.K = K.invert();

//		m_motorMass = 1.0f / (invI1 + invI2);

//		if (m_enableMotor == false)
//		{
//			m_motorForce = 0.0f;
//		}

//		if (m_enableLimit)
//		{
//			float32 jointAngle = b2->m_sweep.a - b1->m_sweep.a - m_referenceAngle;
//			if (b2Abs(m_upperAngle - m_lowerAngle) < 2.0f * b2_angularSlop)
//			{
//				m_limitState = e_equalLimits;
//			}
//			else if (jointAngle <= m_lowerAngle)
//			{
//				if (m_limitState != e_atLowerLimit)
//				{
//					m_limitForce = 0.0f;
//				}
//				m_limitState = e_atLowerLimit;
//			}
//			else if (jointAngle >= m_upperAngle)
//			{
//				if (m_limitState != e_atUpperLimit)
//				{
//					m_limitForce = 0.0f;
//				}
//				m_limitState = e_atUpperLimit;
//			}
//			else
//			{
//				m_limitState = e_inactiveLimit;
//				m_limitForce = 0.0f;
//			}
//		}
//		else
//		{
//			m_limitForce = 0.0f;
//		}

//		if (step.warmStarting)
//		{
			double dt = step.getDeltaTime();
			this.b1.getV().add(this.pivotForce.product(invM1 * dt));
			this.b1.setAv(this.b1.getAv() + dt * invI1 * r1.cross(this.pivotForce));
			this.b2.getV().subtract(this.pivotForce.product(invM2 * dt));
			this.b2.setAv(this.b2.getAv() - dt * invI2 * r2.cross(this.pivotForce));
//		}
//		else
//		{
//			m_pivotForce.SetZero();
//			m_motorForce = 0.0f;
//			m_limitForce = 0.0f;
//		}

//		m_limitPositionImpulse = 0.0f;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	@Override
	public void solveVelocityConstraints(Step step) {
		Transform t1 = this.b1.getTransform();
		Transform t2 = this.b2.getTransform();
		
		Mass m1 = this.b1.getMass();
		Mass m2 = this.b2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		Vector r1 = t1.getTransformedR(this.b1.getLocalCenter().to(this.localAnchor1));
		Vector r2 = t2.getTransformedR(this.b2.getLocalCenter().to(this.localAnchor2));

		// solve the point-to-point constraint
		Vector v1 = this.b1.getV().sum(r1.cross(this.b1.getAv()));
		Vector v2 = this.b2.getV().sum(r2.cross(this.b2.getAv()));
		Vector pivotV = v1.subtract(v2);
		
		double dt = step.getDeltaTime();
		Vector pivotF = this.K.multiply(pivotV).multiply(-1.0 / dt);
		this.pivotForce.add(pivotF);
		
		Vector P = pivotF.product(dt);
		this.b1.getV().add(P.product(invM1));
		this.b1.setAv(this.b1.getAv() + invI1 * r1.cross(P));
		this.b2.getV().subtract(P.product(invM2));
		this.b2.setAv(this.b2.getAv() - invI2 * r2.cross(P));

//		if (m_enableMotor && m_limitState != e_equalLimits)
//		{
//			float32 motorCdot = b2->m_angularVelocity - b1->m_angularVelocity - m_motorSpeed;
//			float32 motorForce = -step.inv_dt * m_motorMass * motorCdot;
//			float32 oldMotorForce = m_motorForce;
//			m_motorForce = b2Clamp(m_motorForce + motorForce, -m_maxMotorTorque, m_maxMotorTorque);
//			motorForce = m_motorForce - oldMotorForce;
//
//			float32 P = step.dt * motorForce;
//			b1->m_angularVelocity -= b1->m_invI * P;
//			b2->m_angularVelocity += b2->m_invI * P;
//		}
//
//		if (m_enableLimit && m_limitState != e_inactiveLimit)
//		{
//			float32 limitCdot = b2->m_angularVelocity - b1->m_angularVelocity;
//			float32 limitForce = -step.inv_dt * m_motorMass * limitCdot;
//
//			if (m_limitState == e_equalLimits)
//			{
//				m_limitForce += limitForce;
//			}
//			else if (m_limitState == e_atLowerLimit)
//			{
//				float32 oldLimitForce = m_limitForce;
//				m_limitForce = b2Max(m_limitForce + limitForce, 0.0f);
//				limitForce = m_limitForce - oldLimitForce;
//			}
//			else if (m_limitState == e_atUpperLimit)
//			{
//				float32 oldLimitForce = m_limitForce;
//				m_limitForce = b2Min(m_limitForce + limitForce, 0.0f);
//				limitForce = m_limitForce - oldLimitForce;
//			}
//
//			float32 P = step.dt * limitForce;
//			b1->m_angularVelocity -= b1->m_invI * P;
//			b2->m_angularVelocity += b2->m_invI * P;
//		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solvePositionConstraints()
	 */
	@Override
	public boolean solvePositionConstraints() {
		Settings settings = Settings.getInstance();
		double linearTolerance = settings.getLinearTolerance();
		
		Transform t1 = this.b1.getTransform();
		Transform t2 = this.b2.getTransform();
		
		Mass m1 = this.b1.getMass();
		Mass m2 = this.b2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		double error = 0.0f;

		Vector r1 = t1.getTransformedR(this.b1.getLocalCenter().to(this.localAnchor1));
		Vector r2 = t2.getTransformedR(this.b2.getLocalCenter().to(this.localAnchor2));
		
		Vector p1 = this.b1.getWorldCenter().sum(r1);
		Vector p2 = this.b2.getWorldCenter().sum(r2);
		Vector p = p1.difference(p2);

		error = p.getMagnitude();

		// compute the K inverse matrix
		Matrix K1 = new Matrix();
		K1.m00 = invM1 + invM2;	K1.m01 = 0.0;
		K1.m10 = 0.0;			K1.m11 = invM1 + invM2;

		Matrix K2 = new Matrix();
		K2.m00 =  invI1 * r1.y * r1.y;	K2.m01 = -invI1 * r1.x * r1.y;
		K2.m10 = -invI1 * r1.x * r1.y;	K2.m11 =  invI1 * r1.x * r1.x;

		Matrix K3 = new Matrix();
		K3.m00 =  invI2 * r2.y * r2.y;	K3.m01 = -invI2 * r2.x * r2.y;
		K3.m10 = -invI2 * r2.x * r2.y;	K3.m11 =  invI2 * r2.x * r2.x;

		Matrix K = new Matrix();
		K.add(K1).add(K2).add(K3);
		
		Vector J = K.solve(p.getNegative());

		// translate and rotate the objects
		this.b1.translate(J.product(invM1));
		this.b1.rotateAboutCenter(invI1 * r1.cross(J));
		
		this.b2.translate(J.product(-invM2));
		this.b2.rotateAboutCenter(-invI2 * r2.cross(J));

		// Handle limits.
//		float32 angularError = 0.0f;
//
//		if (m_enableLimit && m_limitState != e_inactiveLimit)
//		{
//			float32 angle = b2->m_sweep.a - b1->m_sweep.a - m_referenceAngle;
//			float32 limitImpulse = 0.0f;
//
//			if (m_limitState == e_equalLimits)
//			{
//				// Prevent large angular corrections
//				float32 limitC = b2Clamp(angle, -b2_maxAngularCorrection, b2_maxAngularCorrection);
//				limitImpulse = -m_motorMass * limitC;
//				angularError = b2Abs(limitC);
//			}
//			else if (m_limitState == e_atLowerLimit)
//			{
//				float32 limitC = angle - m_lowerAngle;
//				angularError = b2Max(0.0f, -limitC);
//
//				// Prevent large angular corrections and allow some slop.
//				limitC = b2Clamp(limitC + b2_angularSlop, -b2_maxAngularCorrection, 0.0f);
//				limitImpulse = -m_motorMass * limitC;
//				float32 oldLimitImpulse = m_limitPositionImpulse;
//				m_limitPositionImpulse = b2Max(m_limitPositionImpulse + limitImpulse, 0.0f);
//				limitImpulse = m_limitPositionImpulse - oldLimitImpulse;
//			}
//			else if (m_limitState == e_atUpperLimit)
//			{
//				float32 limitC = angle - m_upperAngle;
//				angularError = b2Max(0.0f, limitC);
//
//				// Prevent large angular corrections and allow some slop.
//				limitC = b2Clamp(limitC - b2_angularSlop, 0.0f, b2_maxAngularCorrection);
//				limitImpulse = -m_motorMass * limitC;
//				float32 oldLimitImpulse = m_limitPositionImpulse;
//				m_limitPositionImpulse = b2Min(m_limitPositionImpulse + limitImpulse, 0.0f);
//				limitImpulse = m_limitPositionImpulse - oldLimitImpulse;
//			}
//
//			b1->m_sweep.a -= b1->m_invI * limitImpulse;
//			b2->m_sweep.a += b2->m_invI * limitImpulse;
//
//			b1->SynchronizeTransform();
//			b2->SynchronizeTransform();
//		}

		return error <= linearTolerance;
	}
}
