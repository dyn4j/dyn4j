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
package org.dyn4j.dynamics.contact;

import java.util.List;

import org.dyn4j.Epsilon;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Matrix22;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * Represents an impulse based rigid {@link PhysicsBody} physics collision resolver.
 * @author William Bittle
 * @version 4.0.0
 * @since 3.2.0
 * @param <T> the {@link PhysicsBody} type
 */
public class SequentialImpulses<T extends PhysicsBody> implements ContactConstraintSolver<T> {
	
	/**
	 * Compute the mass coefficient for a {@link SolvableContact}.
	 * 
	 * @param contactConstraint The {@link ContactConstraint} of the contact
	 * @param contact The contact
	 * @param n The normal
	 * @return The mass coefficient
	 * @since 3.4.0
	 */
	private double getMassCoefficient(ContactConstraint<T> contactConstraint, SolvableContact contact, Vector2 n) {
		return this.getMassCoefficient(contactConstraint, contact.r1, contact.r2, n);
	}
	
	/**
	 * Compute the mass coefficient for a {@link SolvableContact}.
	 * 
	 * @param contactConstraint The {@link ContactConstraint} of the contact
	 * @param r1 The contact.r1 field
	 * @param r2 The contact.r2 field
	 * @param n The normal
	 * @return The mass coefficient
	 * @since 3.4.0
	 */
	private double getMassCoefficient(ContactConstraint<T> contactConstraint, Vector2 r1, Vector2 r2, Vector2 n) {
		Mass m1 = contactConstraint.getBody1().getMass();
		Mass m2 = contactConstraint.getBody2().getMass();
		
		double r1CrossN = r1.cross(n);
		double r2CrossN = r2.cross(n);
		
		return m1.getInverseMass() + m2.getInverseMass() + m1.getInverseInertia() * r1CrossN * r1CrossN + m2.getInverseInertia() * r2CrossN * r2CrossN;
	}
	
	/**
	 * Helper method to update the bodies of a {@link ContactConstraint}
	 * 
	 * @param contactConstraint The {@link ContactConstraint} of the bodies
	 * @param contact The corresponding {@link contact}
	 * @param J
	 * @since 3.4.0
	 */
	private void updateBodies(ContactConstraint<T> contactConstraint, SolvableContact contact, Vector2 J) {
		PhysicsBody b1 = contactConstraint.getBody1();
		PhysicsBody b2 = contactConstraint.getBody2();
		Mass m1 = b1.getMass();
		Mass m2 = b2.getMass();
		
		// b1.getVelocity().add(J.product(invM1));
		b1.getLinearVelocity().add(J.x * m1.getInverseMass(), J.y * m1.getInverseMass());
		b1.setAngularVelocity(b1.getAngularVelocity() + m1.getInverseInertia() * contact.r1.cross(J));
		
		// b2.getVelocity().subtract(J.product(invM2));
		b2.getLinearVelocity().subtract(J.x * m2.getInverseMass(), J.y * m2.getInverseMass());
		b2.setAngularVelocity(b2.getAngularVelocity() - m2.getInverseInertia() * contact.r2.cross(J));
	}
	
	/**
	 * Compute the relative velocity to the {@link ContactConstraint}'s normal.
	 * 
	 * @param contactConstraint The {@link ContactConstraint}
	 * @param contact The {@link SolvableContact}
	 * @return double
	 * @since 3.4.0
	 */
	private double getRelativeVelocityAlongNormal(ContactConstraint<T> contactConstraint, SolvableContact contact) {
		Vector2 rv = this.getRelativeVelocity(contactConstraint, contact);
		return contactConstraint.normal.dot(rv);
	}
	
	/**
	 * Compute the relative velocity of this {@link ContactConstraint}'s bodies.
	 * 
	 * @param contactConstraint The {@link ContactConstraint}
	 * @param contact The {@link SolvableContact}
	 * @return The relative velocity vector
	 * @since 3.4.0
	 */
	private Vector2 getRelativeVelocity(ContactConstraint<T> contactConstraint, SolvableContact contact) {
		PhysicsBody b1 = contactConstraint.getBody1();
		PhysicsBody b2 = contactConstraint.getBody2();
		
		Vector2 lv1 = contact.r1.cross(b1.getAngularVelocity()).add(b1.getLinearVelocity());
		Vector2 lv2 = contact.r2.cross(b2.getAngularVelocity()).add(b2.getLinearVelocity());
		Vector2 rv = lv1.subtract(lv2);
		
		return rv;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactConstraintSolver#initialize(java.util.List, org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	public void initialize(List<ContactConstraint<T>> contactConstraints, TimeStep step, Settings settings) {
		// get the restitution velocity from the settings object
		double restitutionVelocity = settings.getRestitutionVelocity();
		
		// loop through the contact constraints
		int size = contactConstraints.size();
		for (int i = 0; i < size; i++) {
			ContactConstraint<T> contactConstraint = contactConstraints.get(i);
			
			// get the contacts
			List<SolvableContact> contacts = contactConstraint.contacts;
			// get the size
			int cSize = contacts.size();
			if (cSize == 0) return; 
			
			// get the bodies
			PhysicsBody b1 = contactConstraint.getBody1();
			PhysicsBody b2 = contactConstraint.getBody2();
			// get the body transforms
			Transform t1 = b1.getTransform();
			Transform t2 = b2.getTransform();
			// get the body masses
			Mass m1 = b1.getMass();
			Mass m2 = b2.getMass();
			
			double invM1 = m1.getInverseMass();
			double invM2 = m2.getInverseMass();
			double invI1 = m1.getInverseInertia();
			double invI2 = m2.getInverseInertia();
			
			// get the transformed centers of mass
			Vector2 c1 = t1.getTransformed(m1.getCenter());
			Vector2 c2 = t2.getTransformed(m2.getCenter());
			
			// get the penetration axis
			Vector2 N = contactConstraint.normal;
			// get the tangent vector
			Vector2 T = contactConstraint.tangent;
			
			// loop through the contact points
			for (int j = 0; j < cSize; j++) {
				SolvableContact contact = contacts.get(j);
				
				// calculate ra and rb
				contact.r1 = c1.to(contact.p);
				contact.r2 = c2.to(contact.p);
				
				// pre calculate the mass normal
				contact.massN = 1.0 / this.getMassCoefficient(contactConstraint, contact, N);
				// pre calculate the mass tangent
				contact.massT = 1.0 / this.getMassCoefficient(contactConstraint, contact, T);
				// set the velocity bias
				contact.vb = 0.0;
				
				// find the relative velocity and project it onto the penetration normal
				double rvn = this.getRelativeVelocityAlongNormal(contactConstraint, contact);
				
				// if its negative then the bodies are moving away from one another
				if (rvn < -restitutionVelocity) {
					// use the coefficient of elasticity
					contact.vb += -contactConstraint.restitution * rvn; 
				}
			}
			
			// does this contact have 2 points?
			if (cSize == 2) {
				// setup the block solver
				SolvableContact contact1 = contacts.get(0);
				SolvableContact contact2 = contacts.get(1);
				
				double rn1A = contact1.r1.cross(N);
				double rn1B = contact1.r2.cross(N);
				double rn2A = contact2.r1.cross(N);
				double rn2B = contact2.r2.cross(N);
				
				// compute the K matrix for the constraints
				Matrix22 K = new Matrix22();
				K.m00 = invM1 + invM2 + invI1 * rn1A * rn1A + invI2 * rn1B * rn1B;
				K.m01 = invM1 + invM2 + invI1 * rn1A * rn2A + invI2 * rn1B * rn2B;
				K.m10 = K.m01;
				K.m11 = invM1 + invM2 + invI1 * rn2A * rn2A + invI2 * rn2B * rn2B;
				
				// check the condition number of the matrix
				
				// typically you would take the matrix and compute
				// one of the norms (1, max, infinity, etc), do the same
				// for the inverse of the matrix, then multiple the two
				// to get the condition number
				
				// instead, box2d takes a bit of a short cut by only
				// evaluating the norm for a single element:
				// ||A||      = m00
				// ||inv(A)|| = m00 / determinant
				// cond(A) = m00 * m00 / determinant
				
				// then, instead of dividing by the determinant we use
				// it as part of the predicate for ill-conditioned
				// m00 * m00 = cond(A) * determinant
				// m00 * m00 < maxCondition * determinant
				
				// what's interesting about this is that the values i've seen here are very
				// close to the values produced by the max-norm
				final double maxCondition = 1000.0;
				final double det = K.determinant();
				if (K.m00 * K.m00 < maxCondition * det) {
					// if the condition number is below the max then we can
					// assume that we can invert K
					contactConstraint.K = K;
					contactConstraint.invK = K.getInverse();
				} else {
					// otherwise the matrix is ill conditioned
					
					// it looks like this will only be the case if the points are
					// close to being the same point.  If they were the same point
					// then the constraints would be redundant
					// just choose one of the points as the point to solve
					
					// let's choose the deepest point
					// FIXME this would break the begin/persist/end sequencing....
					
					// don't remove, but don't solve the other one either. so we'll need to record that it was ill conditioned
					// or that it wasn't solved and make sure thats part of the notification system or something
					// this way we avoid having to report it "end"ing and "begin"ing over and over again.
					
					if (contact1.depth > contact2.depth) {
						// then remove the second contact
						contactConstraint.contacts.remove(1);
					} else {
						// then remove the first contact
						contactConstraint.contacts.remove(0);
					}
				}
			}
		}
		
		// perform warm starting
		this.warmStart(contactConstraints, step, settings);
	}
	
	/**
	 * Performs warm-starting of the contact constraints.
	 * @param contactConstraints the contact constraints to solve
	 * @param step the time step information
	 * @param settings the current settings
	 */
	protected void warmStart(List<ContactConstraint<T>> contactConstraints, TimeStep step, Settings settings) {
		// pre divide for performance
		double ratio = 1.0 / step.getDeltaTimeRatio();
		
		// get the size
		int size = contactConstraints.size();
		
		// we have to perform a separate loop to warm start
		for (int i = 0; i < size; i++) {
			ContactConstraint<T> contactConstraint = contactConstraints.get(i);
			
			// get the penetration axis
			Vector2 N = contactConstraint.normal;
			// get the tangent vector
			Vector2 T = contactConstraint.tangent;
			
			// get the contacts and contact size
			List<SolvableContact> contacts = contactConstraint.contacts;
			int cSize = contacts.size();
			
			for (int j = 0; j < cSize; j++) {
				SolvableContact contact = contacts.get(j);
				
				// scale the accumulated impulses by the delta time ratio
				contact.jn *= ratio;
				contact.jt *= ratio;
				
				// apply accumulated impulses to warm start the solver
				Vector2 J = new Vector2(N.x * contact.jn + T.x * contact.jt, N.y * contact.jn + T.y * contact.jt);
				this.updateBodies(contactConstraint, contact, J);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactConstraintSolver#solveVelocityContraints(java.util.List, org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	public void solveVelocityContraints(List<ContactConstraint<T>> contactConstraints, TimeStep step, Settings settings) {
		// loop through the contact constraints
		int size = contactConstraints.size();
		for (int i = 0; i < size; i++) {
			ContactConstraint<T> contactConstraint = contactConstraints.get(i);
			
			// get the contact list
			List<SolvableContact> contacts = contactConstraint.contacts;
			int cSize = contacts.size();
			if (cSize == 0) continue;
			
			// get the penetration axis and tangent
			Vector2 N = contactConstraint.normal;
			Vector2 T = contactConstraint.tangent;
			
			double tangentSpeed = contactConstraint.tangentSpeed;
			
			// evaluate friction impulse
			for (int k = 0; k < cSize; k++) {
				SolvableContact contact = contacts.get(k);
				
				// get the relative velocity
				Vector2 rv = this.getRelativeVelocity(contactConstraint, contact);
				
				// project the relative velocity onto the tangent normal
				double rvt = T.dot(rv) - tangentSpeed;
				// calculate the tangential impulse
				double jt = contact.massT * (-rvt);
				
				// apply the coefficient of friction
				double maxJt = contactConstraint.friction * contact.jn;
				
				// clamp the accumulated tangential impulse
				double Jt0 = contact.jt;
				contact.jt = Math.max(-maxJt, Math.min(Jt0 + jt, maxJt));
				jt = contact.jt - Jt0;
				
				// apply to the bodies immediately
				Vector2 J = new Vector2(T.x * jt, T.y * jt);
				this.updateBodies(contactConstraint, contact, J);
			}
			
			// evalutate the normal impulse
			
			// check the number of contacts to solve
			if (cSize == 1) {
				// if its one then solve the one contact
				SolvableContact contact = contacts.get(0);
				
				// get the relative velocity and project it onto the penetration normal
				double rvn = this.getRelativeVelocityAlongNormal(contactConstraint, contact);
				
				// calculate the impulse using the velocity bias
				double j = -contact.massN * (rvn - contact.vb);
				
				// clamp the accumulated impulse
				double j0 = contact.jn;
				contact.jn = Math.max(j0 + j, 0.0);
				j = contact.jn - j0;
				
				// only update the bodies after processing all the contacts
				Vector2 J = new Vector2(N.x * j, N.y * j);
				this.updateBodies(contactConstraint, contact, J);
			} else {
				// if its 2 then solve the contacts simultaneously using a mini-LCP
				
				// Block solver developed by Erin Cato and Dirk Gregorius (see Box2d).
				// Build the mini LCP for this contact patch
				//
				// vn = A * x + b, vn >= 0, x >= 0 and vn_i * x_i = 0 with i = 1..2
				//
				// A = J * W * JT and J = ( -n, -r1 x n, n, r2 x n )
				// b = vn_0 - velocityBias
				//
				// The system is solved using the "Total enumeration method" (s. Murty). The complementary constraint vn_i * x_i
				// implies that we must have in any solution either vn_i = 0 or x_i = 0. So for the 2D contact problem the cases
				// vn1 = 0 and vn2 = 0, x1 = 0 and x2 = 0, x1 = 0 and vn2 = 0, x2 = 0 and vn1 = 0 need to be tested. The first valid
				// solution that satisfies the problem is chosen.
				// 
				// In order to account for the accumulated impulse 'a' (because of the iterative nature of the solver which only requires
				// that the accumulated impulse is clamped and not the incremental impulse) we change the impulse variable (x_i).
				//
				// Substitute:
				// 
				// x = a + d
				// 
				// a := old total impulse
				// x := new total impulse
				// d := incremental impulse
				//
				// For the current iteration we extend the formula for the incremental impulse
				// to compute the new total impulse:
				//
				// vn = A * d + b
				//    = A * (x - a) + b
				//    = A * x + b - A * a
				//    = A * x + b'
				// b' = b - A * a;
				
				SolvableContact contact1 = contacts.get(0);
				SolvableContact contact2 = contacts.get(1);
				
				// create a vector containing the current accumulated impulses
				Vector2 a = new Vector2(contact1.jn, contact2.jn);
				
				// get the relative velocity at both contacts and
				// compute the relative velocities along the collision normal
				double rvn1 = this.getRelativeVelocityAlongNormal(contactConstraint, contact1);
				double rvn2 = this.getRelativeVelocityAlongNormal(contactConstraint, contact2);
				
				// create the b vector
				Vector2 b = new Vector2();
				b.x = rvn1 - contact1.vb;
				b.y = rvn2 - contact2.vb;
				b.subtract(contactConstraint.K.product(a));
				
				for (;;) {
					//
					// Case 1: vn = 0
					//
					// 0 = A * x + b'
					//
					// Solve for x:
					//
					// x = - inv(A) * b'
					//
					Vector2 x = contactConstraint.invK.product(b).negate();

					if (x.x >= 0.0 && x.y >= 0.0) {
						this.updateBodies(contactConstraint, contact1, contact2, x, a);
						break;
					}

					//
					// Case 2: vn1 = 0 and x2 = 0
					//
					//   0 = a11 * x1 + a12 * 0 + b1' 
					// vn2 = a21 * x1 + a22 * 0 + b2'
					//
					
					x.x = -contact1.massN * b.x;
					x.y = 0.0;
					rvn1 = 0.0;
					rvn2 = contactConstraint.K.m10 * x.x + b.y;

					if (x.x >= 0.0 && rvn2 >= 0.0) {
						this.updateBodies(contactConstraint, contact1, contact2, x, a);
						break;
					}


					//
					// Case 3: vn2 = 0 and x1 = 0
					//
					// vn1 = a11 * 0 + a12 * x2 + b1' 
					//   0 = a21 * 0 + a22 * x2 + b2'
					//
					
					x.x = 0.0;
					x.y = -contact2.massN * b.y;
					rvn1 = contactConstraint.K.m01 * x.y + b.x;
					rvn2 = 0.0;

					if (x.y >= 0.0 && rvn1 >= 0.0) {
						this.updateBodies(contactConstraint, contact1, contact2, x, a);
						break;
					}

					//
					// Case 4: x1 = 0 and x2 = 0
					// 
					// vn1 = b1
					// vn2 = b2;
					x.x = 0.0f;
					x.y = 0.0f;
					rvn1 = b.x;
					rvn2 = b.y;
					
					if (rvn1 >= 0.0 && rvn2 >= 0.0) {
						this.updateBodies(contactConstraint, contact1, contact2, x, a);
						break;
					}
					
					// No solution, give up. This is hit sometimes, but it doesn't seem to matter.
					break;
				}
			}
		}
	}
	
	/**
	 * Helper method to update bodies while performing the solveVelocityContraints step.
	 * 
	 * @param contactConstraint The {@link ContactConstraint} of the contacts
	 * @param contact1 The first contact
	 * @param contact2 The second contact
	 * @param x
	 * @param a
	 * @since 3.4.0
	 */
	private void updateBodies(ContactConstraint<T> contactConstraint, SolvableContact contact1, SolvableContact contact2, Vector2 x, Vector2 a) {
		PhysicsBody b1 = contactConstraint.getBody1();
		PhysicsBody b2 = contactConstraint.getBody2();
		Mass m1 = b1.getMass();
		Mass m2 = b2.getMass();
		
		Vector2 N = contactConstraint.normal;
		
		// find the incremental impulse
		// Vector2 d = x.difference(a);
		// apply the incremental impulse
		Vector2 J1 = N.product(x.x - a.x);
		Vector2 J2 = N.product(x.y - a.y);
		
		double Jx = J1.x + J2.x;
		double Jy = J1.y + J2.y;
		
		// v1.add(J1.sum(J2).multiply(invM1));
		b1.getLinearVelocity().add(Jx * m1.getInverseMass(), Jy * m1.getInverseMass());
		b1.setAngularVelocity(b1.getAngularVelocity() + m1.getInverseInertia() * (contact1.r1.cross(J1) + contact2.r1.cross(J2)));
		
		// v2.subtract(J1.sum(J2).multiply(invM2));
		b2.getLinearVelocity().subtract(Jx * m2.getInverseMass(), Jy * m2.getInverseMass());
		b2.setAngularVelocity(b2.getAngularVelocity() - m2.getInverseInertia() * (contact1.r2.cross(J1) + contact2.r2.cross(J2)));
		
		// set the new incremental impulse
		contact1.jn = x.x;
		contact2.jn = x.y;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactConstraintSolver#solvePositionContraints(java.util.List, org.dyn4j.dynamics.TimeStep, org.dyn4j.dynamics.Settings)
	 */
	public boolean solvePositionContraints(List<ContactConstraint<T>> contactConstraints, TimeStep step, Settings settings) {
		// immediately return true if there are no contact constraints to solve
		if (contactConstraints.isEmpty()) return true;
		
		// track the minimum separation
		double minSeparation = 0.0;
		
		// get the max linear correction, baumgarte, and allowed penetration from
		// the settings object.
		double maxLinearCorrection = settings.getMaximumLinearCorrection();
		double allowedPenetration = settings.getLinearTolerance();
		double baumgarte = settings.getBaumgarte();
		
		// loop through the contact constraints
		int size = contactConstraints.size();
		for (int i = 0; i < size; i++) {
			ContactConstraint<T> contactConstraint = contactConstraints.get(i);
			
			// get the contact list
			List<SolvableContact> contacts = contactConstraint.contacts;
			int cSize = contacts.size();
			if (cSize == 0) continue;
			
			// get the bodies
			PhysicsBody b1 = contactConstraint.getBody1();
			PhysicsBody b2 = contactConstraint.getBody2();
			// get their transforms
			Transform t1 = b1.getTransform();
			Transform t2 = b2.getTransform();
			// get the masses
			Mass m1 = b1.getMass();
			Mass m2 = b2.getMass();
		  
			// get the penetration axis
			Vector2 N = contactConstraint.normal;

			// solve normal constraints
			for (int k = 0; k < cSize; k++) {
				SolvableContact contact = contacts.get(k);
				
				// get the world centers of mass
				// NOTE: the world center needs to be recomputed each iteration because
				//       we are modifying the transform in each iteration
				Vector2 c1 = t1.getTransformed(m1.getCenter());
				Vector2 c2 = t2.getTransformed(m2.getCenter());
				
				// get r1 and r2
				Vector2 r1 = contact.p1.difference(m1.getCenter());
				t1.transformR(r1);
				Vector2 r2 = contact.p2.difference(m2.getCenter());
				t2.transformR(r2);
				
				// get the world contact points
				Vector2 p1 = c1.sum(r1);
				Vector2 p2 = c2.sum(r2);
				Vector2 dp = p1.subtract(p2);
				
				// estimate the current penetration
				double penetration = dp.dot(N) - contact.depth;

				// track the maximum error
				minSeparation = Math.min(minSeparation, penetration);
				
				// allow for penetration to avoid jitter
				double cp = baumgarte * Interval.clamp(penetration + allowedPenetration, -maxLinearCorrection, 0.0);
				
				// compute the position impulse
				double K = this.getMassCoefficient(contactConstraint, r1, r2, N);
				double jp = (K > Epsilon.E)? (-cp / K) : 0.0;
				
				// clamp the accumulated position impulse
				double jp0 = contact.jp;
				contact.jp = Math.max(jp0 + jp, 0.0);
				jp = contact.jp - jp0;
				
				Vector2 J = N.product(jp);
				
				// translate and rotate the objects
				b1.translate(J.product(m1.getInverseMass()));
				b1.rotate(m1.getInverseInertia() * r1.cross(J), c1.x, c1.y);
				
				b2.translate(J.product(-m2.getInverseMass()));
				b2.rotate(-m2.getInverseInertia() * r2.cross(J), c2.x, c2.y);
			}
		}
		// check if the minimum separation between all objects is still
		// greater than or equal to allowed penetration plus half of allowed penetration
		// since we cannot expect it to be above allowed penetration alone
		return minSeparation >= -3.0 * allowedPenetration;
	}
}
