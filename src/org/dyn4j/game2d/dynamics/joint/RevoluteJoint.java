package org.dyn4j.game2d.dynamics.joint;

import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.Mass;
import org.dyn4j.game2d.dynamics.Settings;
import org.dyn4j.game2d.dynamics.Step;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector;

public class RevoluteJoint extends Joint {
	protected Vector localAnchor1;
	protected Vector localAnchor2;
	
	protected Matrix22 pivotMass;
	protected Vector pivotForce = new Vector();
	
	// TODO this needs to be moved to geometry package
	public static class Matrix22 {
		public Vector c1 = new Vector();
		public Vector c2 = new Vector();
		
		public Matrix22 invert() {
			double a = c1.x;
			double b = c2.x;
			double c = c1.y;
			double d = c2.y;
			Matrix22 r = new Matrix22();
			double det = a * d - b * c;
			// check for zero determinant
			if (det == 0.0) throw new ArithmeticException();
			det = 1.0 / det;
			r.c1.x =  det * d;
			r.c2.x = -det * b;
			r.c1.y = -det * c;
			r.c2.y =  det * a;
			return r;
		}
		
		public Vector solve(Vector b) {
			double m11 = c1.x;
			double m12 = c2.x;
			double m21 = c1.y;
			double m22 = c2.y;
			double det = m11 * m22 - m12 * m21;
			// check for zero determinant
			if (det == 0.0) throw new ArithmeticException();
			det = 1.0 / det;
			Vector r = new Vector();
			r.x = det * (m22 * b.x - m12 * b.y);
			r.y = det * (m11 * b.y - m21 * b.x);
			return r;
		}
		
		public Matrix22 add(Matrix22 m) {
			this.c1.add(m.c1);
			this.c2.add(m.c2);
			return this;
		}
		
		public Matrix22 sum(Matrix22 m) {
			Matrix22 r = new Matrix22();
			r.c1.add(this.c1).add(m.c1);
			r.c2.add(this.c2).add(m.c2);
			return r;
		}
		
		public Vector multiply(Vector v) {
			Vector r = new Vector();
			r.x = c1.x * v.x + c2.x * v.y;
			r.y = c1.y * v.x + c2.y * v.y;
			return r;
		}
	}
	
	public RevoluteJoint(Body b1, Body b2, Vector anchor) {
		super(b1, b2);
		this.localAnchor1 = b1.getLocalPoint(anchor);
		this.localAnchor2 = b2.getLocalPoint(anchor);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		return sb.toString();
	}
	
	@Override
	public void initializeConstraints(Step step) {
		Transform t1 = this.b1.getTransform();
		Transform t2 = this.b2.getTransform();
		
		Mass m1 = b1.getMass();
		Mass m2 = b2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		Vector r1 = t1.getTransformedR(this.b1.getLocalCenter().to(this.localAnchor1));
		Vector r2 = t2.getTransformedR(this.b2.getLocalCenter().to(this.localAnchor2));
//		b2Body* b1 = m_body1;
//		b2Body* b2 = m_body2;
//
//		// Compute the effective mass matrix.
//		b2Vec2 r1 = b2Mul(b1->GetXForm().R, m_localAnchor1 - b1->GetLocalCenter());
//		b2Vec2 r2 = b2Mul(b2->GetXForm().R, m_localAnchor2 - b2->GetLocalCenter());

		// K    = [(1/m1 + 1/m2) * eye(2) - skew(r1) * invI1 * skew(r1) - skew(r2) * invI2 * skew(r2)]
		//      = [1/m1+1/m2     0    ] + invI1 * [r1.y*r1.y -r1.x*r1.y] + invI2 * [r2.y*r2.y -r2.x*r2.y]
		//        [    0     1/m1+1/m2]           [-r1.x*r1.y r1.x*r1.x]           [-r2.x*r2.y r2.x*r2.x]
//		float32 invMass1 = b1->m_invMass, invMass2 = b2->m_invMass;
//		float32 invI1 = b1->m_invI, invI2 = b2->m_invI;

		Matrix22 K1 = new Matrix22();
		K1.c1.x = invM1 + invM2;	K1.c2.x = 0.0;
		K1.c1.y = 0.0;				K1.c2.y = invM1 + invM2;

		Matrix22 K2 = new Matrix22();
		K2.c1.x =  invI1 * r1.y * r1.y;	K2.c2.x = -invI1 * r1.x * r1.y;
		K2.c1.y = -invI1 * r1.x * r1.y;	K2.c2.y =  invI1 * r1.x * r1.x;

		Matrix22 K3 = new Matrix22();
		K3.c1.x =  invI2 * r2.y * r2.y;	K3.c2.x = -invI2 * r2.x * r2.y;
		K3.c1.y = -invI2 * r2.x * r2.y;	K3.c2.y =  invI2 * r2.x * r2.x;

		Matrix22 K = new Matrix22();
		K.add(K1).add(K2).add(K3);
		//Matrix22 K = K1 + K2 + K3;
		this.pivotMass = K.invert();

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
			b1.getV().add(this.pivotForce.product(invM1 * dt));
			b1.setAv(b1.getAv() + dt * invI1 * r1.cross(this.pivotForce));
			b2.getV().subtract(this.pivotForce.product(invM2 * dt));
			b2.setAv(b2.getAv() - dt * invI2 * r2.cross(this.pivotForce));
			
//			b1->m_linearVelocity -= B2FORCE_SCALE(step.dt) * invMass1 * m_pivotForce;
//			b1->m_angularVelocity -= B2FORCE_SCALE(step.dt) * invI1 * (b2Cross(r1, m_pivotForce) + B2FORCE_INV_SCALE(m_motorForce + m_limitForce));
//
//			b2->m_linearVelocity += B2FORCE_SCALE(step.dt) * invMass2 * m_pivotForce;
//			b2->m_angularVelocity += B2FORCE_SCALE(step.dt) * invI2 * (b2Cross(r2, m_pivotForce) + B2FORCE_INV_SCALE(m_motorForce + m_limitForce));
//		}
//		else
//		{
//			m_pivotForce.SetZero();
//			m_motorForce = 0.0f;
//			m_limitForce = 0.0f;
//		}

//		m_limitPositionImpulse = 0.0f;
	}

	@Override
	public void solveVelocityConstraints(Step step) {
		Transform t1 = this.b1.getTransform();
		Transform t2 = this.b2.getTransform();
		
		Mass m1 = b1.getMass();
		Mass m2 = b2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		Vector r1 = t1.getTransformedR(this.b1.getLocalCenter().to(this.localAnchor1));
		Vector r2 = t2.getTransformedR(this.b2.getLocalCenter().to(this.localAnchor2));

		// Solve point-to-point constraint
		Vector v1 = b1.getV().sum(r1.cross(b1.getAv()));
		Vector v2 = b2.getV().sum(r2.cross(b2.getAv()));
		Vector pivotV = v1.subtract(v2);
		
		double dt = step.getDeltaTime();
		Vector pivotF = this.pivotMass.multiply(pivotV).multiply(-1.0 / dt);
		this.pivotForce.add(pivotF);
		//b2Vec2 pivotCdot = b2->m_linearVelocity + b2Cross(b2->m_angularVelocity, r2) - b1->m_linearVelocity - b2Cross(b1->m_angularVelocity, r1);
		//b2Vec2 pivotForce = -B2FORCE_INV_SCALE(step.inv_dt) * b2Mul(m_pivotMass, pivotCdot);
		//m_pivotForce += pivotForce;
		
		Vector P = pivotF.product(dt);
		//b2Vec2 P = B2FORCE_SCALE(step.dt) * pivotForce;
		b1.getV().add(P.product(invM1));
		b1.setAv(b1.getAv() + invI1 * r1.cross(P));
		b2.getV().subtract(P.product(invM2));
		b2.setAv(b2.getAv() - invI2 * r2.cross(P));
//		b1->m_linearVelocity -= b1->m_invMass * P;
//		b1->m_angularVelocity -= b1->m_invI * b2Cross(r1, P);
//
//		b2->m_linearVelocity += b2->m_invMass * P;
//		b2->m_angularVelocity += b2->m_invI * b2Cross(r2, P);

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
	
	@Override
	public boolean solvePositionConstraints() {
//		b2Body* b1 = m_body1;
//		b2Body* b2 = m_body2;
		
		Settings settings = Settings.getInstance();
		double linearTolerance = settings.getLinearTolerance();
		
		Transform t1 = this.b1.getTransform();
		Transform t2 = this.b2.getTransform();
		
		Mass m1 = b1.getMass();
		Mass m2 = b2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		double error = 0.0f;

		Vector r1 = t1.getTransformedR(this.b1.getLocalCenter().to(this.localAnchor1));
		Vector r2 = t2.getTransformedR(this.b2.getLocalCenter().to(this.localAnchor2));
		// Solve point-to-point position error.
//		b2Vec2 r1 = b2Mul(b1->GetXForm().R, m_localAnchor1 - b1->GetLocalCenter());
//		b2Vec2 r2 = b2Mul(b2->GetXForm().R, m_localAnchor2 - b2->GetLocalCenter());
		
		Vector p1 = b1.getWorldCenter().sum(r1);
		Vector p2 = b2.getWorldCenter().sum(r2);
		Vector p = p1.difference(p2);
//		b2Vec2 p1 = b1->m_sweep.c + r1;
//		b2Vec2 p2 = b2->m_sweep.c + r2;
//		b2Vec2 ptpC = p2 - p1;

		error = p.getMagnitude();
//		positionError = ptpC.Length();

		// Prevent overly large corrections.
		//b2Vec2 dpMax(b2_maxLinearCorrection, b2_maxLinearCorrection);
		//ptpC = b2Clamp(ptpC, -dpMax, dpMax);

//		float32 invMass1 = b1->m_invMass, invMass2 = b2->m_invMass;
//		float32 invI1 = b1->m_invI, invI2 = b2->m_invI;

		Matrix22 K1 = new Matrix22();
		K1.c1.x = invM1 + invM2;	K1.c2.x = 0.0f;
		K1.c1.y = 0.0f;				K1.c2.y = invM1 + invM2;

		Matrix22 K2 = new Matrix22();
		K2.c1.x =  invI1 * r1.y * r1.y;	K2.c2.x = -invI1 * r1.x * r1.y;
		K2.c1.y = -invI1 * r1.x * r1.y;	K2.c2.y =  invI1 * r1.x * r1.x;

		Matrix22 K3 = new Matrix22();
		K3.c1.x =  invI2 * r2.y * r2.y;	K3.c2.x = -invI2 * r2.x * r2.y;
		K3.c1.y = -invI2 * r2.x * r2.y;	K3.c2.y =  invI2 * r2.x * r2.x;

		Matrix22 K = new Matrix22();
		K.add(K1).add(K2).add(K3);
		
		Vector J = K.solve(p.getNegative());
		
//		b2Vec2 impulse = K.Solve(-ptpC);

		// translate and rotate the objects
		b1.translate(J.product(invM1));
		b1.rotateAboutCenter(invI1 * r1.cross(J));
		
		b2.translate(J.product(-invM2));
		b2.rotateAboutCenter(-invI2 * r2.cross(J));
		
//		b1->m_sweep.c -= b1->m_invMass * impulse;
//		b1->m_sweep.a -= b1->m_invI * b2Cross(r1, impulse);
//
//		b2->m_sweep.c += b2->m_invMass * impulse;
//		b2->m_sweep.a += b2->m_invI * b2Cross(r2, impulse);
//
//		b1->SynchronizeTransform();
//		b2->SynchronizeTransform();

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

		return error <= linearTolerance;// && angularError <= b2_angularSlop;
	}
}
