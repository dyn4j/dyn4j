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
import org.dyn4j.game2d.dynamics.Mass;
import org.dyn4j.game2d.dynamics.Step;
import org.dyn4j.game2d.geometry.Interval;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Represents a fixed length distance joint.
 * <p>
 * Given the two world space anchor points a distance is computed and used
 * to constraint the attached {@link Body}s.
 * <p>
 * This joint doubles as a spring/damping distance joint where the length can
 * change but is constantly approaching the target distance.
 * @author William Bittle
 */
public class DistanceJoint extends Joint {
	/** The local anchor point on the first {@link Body} */
	protected Vector localAnchor1;
	
	/** The local anchor point on the second {@link Body} */
	protected Vector localAnchor2;
	
	/** The computed distance between the two world space anchor points */
	protected double distance;
	
	/** The effective mass of the two body system */
	protected double mass;
	
	/** The normal */
	protected Vector n;
	
	// TODO for spring distance joint
	/** */
	protected double bias;
	/** */
	protected double gamma;
	
	/** The accumulated impulse from the previous time step */
	protected double j;
	
	/**
	 * Optional constructor.
	 * <p>
	 * Creates a fixed distance {@link Joint} where the joined 
	 * {@link Body}s do not participate in collision detection and
	 * resolution.
	 * @param b1 the first {@link Body}
	 * @param b2 the second {@link Body}
	 * @param anchor1 in world coordinates
	 * @param anchor2 in world coordinates
	 */
	public DistanceJoint(Body b1, Body b2, Vector anchor1, Vector anchor2) {
		this(b1, b2, false, anchor1, anchor2);
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Creates a fixed distance joint.
	 * @param b1 the first {@link Body}
	 * @param b2 the second {@link Body}
	 * @param collisionAllowed true if collision between the two {@link Body}s is allowed
	 * @param anchor1 in world coordinates
	 * @param anchor2 in world coordinates
	 */
	public DistanceJoint(Body b1, Body b2, boolean collisionAllowed, Vector anchor1, Vector anchor2) {
		super(b1, b2, collisionAllowed);
		this.localAnchor1 = b1.getLocalPoint(anchor1);
		this.localAnchor2 = b2.getLocalPoint(anchor2);
		this.distance = anchor1.distance(anchor2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DISTANCE_JOINT[")
		.append(super.toString()).append("|")
		.append(this.localAnchor1).append("|")
		.append(this.localAnchor2).append("|")
		.append(this.distance).append("|")
		.append(this.mass).append("|")
		.append(this.n).append("|")
		.append(this.bias).append("|")
		.append(this.gamma).append("|")
		.append(this.j).append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#initializeConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	public void initializeConstraints(Step step) {
		Transform t1 = b1.getTransform();
		Transform t2 = b2.getTransform();
		Mass m1 = b1.getMass();
		Mass m2 = b2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// compute the normal
		Vector r1 = t1.getTransformedR(this.b1.getLocalCenter().to(this.localAnchor1));
		Vector r2 = t2.getTransformedR(this.b2.getLocalCenter().to(this.localAnchor2));
		n = r1.sum(b1.getWorldCenter()).subtract(r2.sum(b2.getWorldCenter()));
		
		// get the current length
		double l = n.getMagnitude();
		if (l < 0.0002) {
			n.zero();
		} else {
			// normalize it
			n.divide(l);
		}
		
		// compute the mass of the two body system
		// mc = 1 / (J * Minv * Jtrans)
		// J = Xtrans / mag(X)
		// mc = 1 / ((Xtrans / mag(X)) * Minv * (Xtrans / mag(X))trans)
		// mc = 1 / ((Xtrans * Minv * X) / mag(X)^2)
		// mc = mag(X)^2 / (Xtrans * Minv * X)
		// mc = mag(X)^2 / (X.dot(X) * Minv)
		// mc = mag(X)^2 / (mag(X)^2 * Minv)
		// mc = 1 / Minv
		// mc = 1 / (m1inv + m2inv + I1inv * (r1 x n)^2 + I2inv * (r2 x n)^2)
		double cr1n = r1.cross(n);
		double cr2n = r2.cross(n);
		double invMass = invM1 + invI1 * cr1n * cr1n;
		invMass += invM2 + invI2 * cr2n * cr2n;
		this.mass = 1.0 / invMass;
		
		// TODO add code to allow this to dual as a spring constraint
		this.bias = 0.0;
		this.gamma = 0.0;
		
		// warm start
		j *= step.getDeltaTimeRatio();
		Vector J = n.product(j);
		b1.getV().add(J.product(invM1));
		b1.setAv(b1.getAv() + invI1 * r1.cross(J));
		b2.getV().subtract(J.product(invM2));
		b2.setAv(b2.getAv() - invI2 * r2.cross(J));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solveVelocityConstraints(org.dyn4j.game2d.dynamics.Step)
	 */
	public void solveVelocityConstraints(Step step) {
		// solve the constraint:
		// C = mag(n) - L
		// C = sqrt(x^2 + y^2) - L
		// take the derivative with respect to time:
		// C = 1 / (2 * sqrt(x^2 + y^2)) * d/dt(x^2 + y^2) - 0
		// by the chain rule we take the derivative of the contents of the sqrt
		// C = 1 / (2 * sqrt(x^2 + y^2)) * 2 * (x * vx + y * vy)
		// since x and y are functions of time we use the chain rule to obtain vx and vy
		// C = (x * vx + y * vy) / sqrt(x^2 + y^2)
		// C = (x * vx + y * vy) / mag(n)
		// C = n.dot(v) / mag(n)
		
		Transform t1 = b1.getTransform();
		Transform t2 = b2.getTransform();
		Mass m1 = b1.getMass();
		Mass m2 = b2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		// compute r1 and r2
		Vector r1 = t1.getTransformedR(this.b1.getLocalCenter().to(this.localAnchor1));
		Vector r2 = t2.getTransformedR(this.b2.getLocalCenter().to(this.localAnchor2));
		
		// compute the relative velocity
		Vector v1 = b1.getV().sum(r1.cross(b1.getAv()));
		Vector v2 = b2.getV().sum(r2.cross(b2.getAv()));
		
		// compute C
		double C = n.dot(v1.difference(v2));
		
		// newton's law for impulses
		// M * Vdelta = P
		// re-written as:
		// v2 = v1 + MinvP
		// Virtual work:
		// P = Jtrans * lambda
		// constraint
		// Jv1 + b = 0
		// solve for lambda (substitute 2nd two equations into the first)
		// v2 = -b/J + Minv * Jtrans * lambda
		// lambda = (v2 + b/J) / Minv * Jtrans
		// lambda = (1/J)(Jv2 + b) (1/Minv) (1/Jtrans)
		// lambda = (1/(J * Minv * Jtrans) * (Jv2 + b)
		// where Mc =  (1/(J * Minv * Jtrans)
		// compute the impulse lambda = -m (Jv + b)
		double j = -this.mass * (C + this.bias + this.gamma * this.j);
		this.j += j;
		
		// apply the impulse
		Vector J = n.product(j);
		b1.getV().add(J.product(invM1));
		b1.setAv(b1.getAv() + invI1 * r1.cross(J));
		b2.getV().subtract(J.product(invM2));
		b2.setAv(b2.getAv() - invI2 * r2.cross(J));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.joint.Joint#solvePositionConstraints()
	 */
	@Override
	public boolean solvePositionConstraints() {
		Transform t1 = b1.getTransform();
		Transform t2 = b2.getTransform();
		Mass m1 = b1.getMass();
		Mass m2 = b2.getMass();
		
		double invM1 = m1.getInverseMass();
		double invM2 = m2.getInverseMass();
		double invI1 = m1.getInverseInertia();
		double invI2 = m2.getInverseInertia();
		
		Vector c1 = b1.getWorldCenter();
		Vector c2 = b2.getWorldCenter();
		
		Vector r1 = t1.getTransformedR(this.b1.getLocalCenter().to(this.localAnchor1));
		Vector r2 = t2.getTransformedR(this.b2.getLocalCenter().to(this.localAnchor2));
		n = r1.sum(b1.getWorldCenter()).subtract(r2.sum(b2.getWorldCenter()));
		
		double l = n.normalize();
		double C = l - this.distance;
		C = Interval.clamp(C, -0.2, 0.2);
		
		double impulse = -this.mass * C;
		
		Vector J = n.product(impulse);
		
		// translate and rotate the objects
		b1.translate(J.product(invM1));
		b1.rotate(invI1 * r1.cross(J), c1);
		
		b2.translate(J.product(-invM2));
		b2.rotate(-invI2 * r2.cross(J), c2);
		
		return Math.abs(C) < 0.005;
	}
	
	/**
	 * Returns the local anchor point for the first {@link Body}.
	 * @return {@link Vector}
	 */
	public Vector getLocalAnchor1() {
		return this.localAnchor1;
	}
	
	/**
	 * Returns the local anchor point for the second {@link Body}.
	 * @return {@link Vector}
	 */
	public Vector getLocalAnchor2() {
		return this.localAnchor2;
	}
	
	/**
	 * Returns the world space anchor point for the first {@link Body}.
	 * @return {@link Vector}
	 */
	public Vector getWorldAnchor1() {
		return b1.getWorldPoint(this.localAnchor1);
	}
	
	/**
	 * Returns the world space anchor point for the second {@link Body}.
	 * @return {@link Vector}
	 */
	public Vector getWorldAnchor2() {
		return b2.getWorldPoint(this.localAnchor2);
	}
}
