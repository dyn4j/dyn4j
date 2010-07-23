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
package org.dyn4j.game2d.dynamics.contact;

import java.util.List;

import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.Settings;
import org.dyn4j.game2d.dynamics.Step;
import org.dyn4j.game2d.geometry.Interval;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Represents an impulse based rigid {@link Body} physics collision resolver.
 * <p>
 * This class uses the method from <a href="http://www.box2d.org">Box2d</a> called Sequential Impulses.  SI, for short, is an iterative
 * method of obtaining a global solution to a number of contacts.  A global solution must be found to 
 * facilitate stable stacking of rigid {@link Body}s.
 * @see <a href="http://www.box2d.org">Box2d</a>
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public class ContactConstraintSolver {
	/** List for iterating through the {@link ContactConstraint}s */
	protected List<ContactConstraint> contactConstraints = null;
	
	/**
	 * Sets the {@link ContactConstraint}s to solve.
	 * @param ccs the {@link ContactConstraint}s to solve
	 */
	public void setup(List<ContactConstraint> ccs) {
		this.contactConstraints = ccs;
		
		// get the restitution velocity from the settings object
		double restitutionVelocity = Settings.getInstance().getRestitutionVelocity();
		
		// loop through the contact constraints
		int size = this.contactConstraints.size();
		for (int i = 0; i < size; i++) {
			ContactConstraint contactConstraint = this.contactConstraints.get(i);
			// get the bodies
			Body b1 = contactConstraint.getBody1();
			Body b2 = contactConstraint.getBody2();
			// get the body transforms
			Transform t1 = b1.getTransform();
			Transform t2 = b2.getTransform();
			// get the body masses
			Mass m1 = b1.getMass();
			Mass m2 = b2.getMass();
			
			double mass1 = m1.getMass();
			double mass2 = m2.getMass();
			double invM1 = m1.getInverseMass();
			double invM2 = m2.getInverseMass();
			double invI1 = m1.getInverseInertia();
			double invI2 = m2.getInverseInertia();
			
			// get the transformed centers of mass
			Vector2 c1 = t1.getTransformed(m1.getCenter());
			Vector2 c2 = t2.getTransformed(m2.getCenter());
			
			// get the contacts
			Contact[] contacts = contactConstraint.contacts;
			int cSize = contacts.length;
			
			// get the penetration axis
			Vector2 N = contactConstraint.normal;
			// get the tangent vector
			Vector2 T = N.cross(1.0);
			
			// loop through the contact points
			for (int j = 0; j < cSize; j++) {
				Contact contact = contacts[j];
				
				// is the contact enabled?
				if (!contact.isEnabled()) continue;
				
				// get ra and rb
				Vector2 r1 = c1.to(contact.p);
				Vector2 r2 = c2.to(contact.p);
				contact.r1 = r1;
				contact.r2 = r2;
				
				// pre calculate the mass normal
				double r1CrossN = r1.cross(N);
				double r2CrossN = r2.cross(N);
				contact.massN = 1.0 / (invM1 + invM2 + invI1 * r1CrossN * r1CrossN + invI2 * r2CrossN * r2CrossN);

				// pre calculate the mass tangent
				double r1CrossT = r1.cross(T);
				double r2CrossT = r2.cross(T);
				contact.massT = 1.0 / (invM1 + invM2 + invI1 * r1CrossT * r1CrossT + invI2 * r2CrossT * r2CrossT);

				// pre calculate the equalized mass
				// the equalized mass is the average of the mass of the two objects
				// this is done to force heavy objects from sinking into lighter objects
				double massE = mass1 * invM1 + mass2 * invM2;
				massE += mass1 * invI1 * r1CrossN * r1CrossN + mass2 * invI2 * r2CrossN * r2CrossN;
				contact.massE = 1.0 / massE;
				
				// set the velocity bias
				contact.vb = 0.0;
				
				// find the relative velocity
				Vector2 lv1 = r1.cross(b1.getAngularVelocity()).add(b1.getVelocity());
				Vector2 lv2 = r2.cross(b2.getAngularVelocity()).add(b2.getVelocity());
				Vector2 rv = lv1.subtract(lv2);
				
				// project the relative velocity onto the penetration normal
				double rvn = N.dot(rv);
				// if its negative then the bodies are moving away from one another
				if (rvn < -restitutionVelocity) {
					// use the coefficient of elasticity
					contact.vb += -contactConstraint.restitution * rvn; 
				}
			}
		}
	}
	
	/**
	 * Performs intialization of velocity constraint solving.
	 * @param step the current step object
	 */
	public void initializeConstraints(Step step) {
		// pre divide for performance
		double ratio = 1.0 / step.getDeltaTimeRatio();
		
		// we have to perform a separate loop to warm start
		for (int i = 0; i < this.contactConstraints.size(); i++) {
			ContactConstraint cc = this.contactConstraints.get(i);
			
			// get the bodies
			Body b1 = cc.getBody1();
			Body b2 = cc.getBody2();
			// get the body masses
			Mass m1 = b1.getMass();
			Mass m2 = b2.getMass();
			
			double invM1 = m1.getInverseMass();
			double invM2 = m2.getInverseMass();
			double invI1 = m1.getInverseInertia();
			double invI2 = m2.getInverseInertia();
			
			// get the penetration axis
			Vector2 N = cc.normal;
			// get the tangent vector
			Vector2 T = N.cross(1.0);
			
			for (int j = 0; j < cc.getContacts().length; j++) {
				Contact contact = cc.getContacts()[j];
				
				// is the contact enabled?
				if (!contact.isEnabled()) continue;
				
				// scale the accumulated impulses by the delta time ratio
				contact.jn *= ratio;
				contact.jt *= ratio;
				
				// apply accumulated impulses to warm start the solver
				Vector2 J = N.product(contact.jn);
				J.add(T.product(contact.jt));
				b1.getVelocity().add(J.product(invM1));
				b1.setAngularVelocity(b1.getAngularVelocity() + invI1 * contact.r1.cross(J));
				b2.getVelocity().subtract(J.product(invM2));
				b2.setAngularVelocity(b2.getAngularVelocity() - invI2 * contact.r2.cross(J));
			}
		}
	}
	
	/**
	 * Solves the velocity constraints.
	 */
	public void solveVelocityContraints() {
		// loop through the contact constraints
		int size = this.contactConstraints.size();
		for (int i = 0; i < size; i++) {
			ContactConstraint contactConstraint = this.contactConstraints.get(i);
			// get the bodies
			Body b1 = contactConstraint.getBody1();
			Body b2 = contactConstraint.getBody2();
			// get the masses
			Mass m1 = b1.getMass();
			Mass m2 = b2.getMass();
			
			double invM1 = m1.getInverseMass();
			double invM2 = m2.getInverseMass();
			double invI1 = m1.getInverseInertia();
			double invI2 = m2.getInverseInertia();

			// get the contact list
			Contact[] contacts = contactConstraint.contacts;
			int cSize = contacts.length;
			
			// get the penetration axis and tangent
			Vector2 N = contactConstraint.normal;
			Vector2 T = N.cross(1.0);

			// loop through the contact points
			for (int k = 0; k < cSize; k++) {
				Contact contact = contacts[k];
				
				// is the contact enabled?
				if (!contact.isEnabled()) continue;
				
				// get ra and rb
				Vector2 r1 = contact.r1;
				Vector2 r2 = contact.r2;
				
				// get the relative velocity
				Vector2 lv1 = r1.cross(b1.getAngularVelocity()).add(b1.getVelocity());
				Vector2 lv2 = r2.cross(b2.getAngularVelocity()).add(b2.getVelocity());
				Vector2 rv = lv1.subtract(lv2);
				
				// project the relative velocity onto the penetration normal
				double rvn = N.dot(rv);
				
				// calculate the impulse using the velocity bias
				double j = -contact.massN * (rvn - contact.vb);
				
				// clamp the accumulated impulse
				double j0 = contact.jn;
				contact.jn = Math.max(j0 + j, 0.0);
				j = contact.jn - j0;
				
				// only update the bodies after processing all the contacts
				Vector2 J = N.product(j);
				b1.getVelocity().add(J.product(invM1));
				b1.setAngularVelocity(b1.getAngularVelocity() + invI1 * r1.cross(J));
				b2.getVelocity().subtract(J.product(invM2));
				b2.setAngularVelocity(b2.getAngularVelocity() - invI2 * r2.cross(J));
			}
			
			// evaluate friction impulse
			// since this requires the jn to be computed we must loop through
			// the contacts twice
			for (int k = 0; k < cSize; k++) {
				Contact contact = contacts[k];
				
				// is the contact enabled?
				if (!contact.isEnabled()) continue;
				
				// get ra and rb
				Vector2 r1 = contact.r1;
				Vector2 r2 = contact.r2;
				
				// get the relative velocity
				Vector2 lv1 = r1.cross(b1.getAngularVelocity()).add(b1.getVelocity());
				Vector2 lv2 = r2.cross(b2.getAngularVelocity()).add(b2.getVelocity());
				Vector2 rv = lv1.subtract(lv2);
				
				// project the relative velocity onto the tangent normal
				double rvt = T.dot(rv);
				// calculate the tangential impulse
				double jt = contact.massT * (-rvt);
				
				// apply the coefficient of friction
				double maxJt = contactConstraint.friction * contact.jn;
				// clamp the accumulated tangential impulse
				double Jt0 = contact.jt;
				contact.jt = Math.max(-maxJt, Math.min(Jt0 + jt, maxJt));
				jt = contact.jt - Jt0;
				
				// apply to the bodies immediately
				Vector2 J = T.product(jt);
				b1.getVelocity().add(J.product(invM1));
				b1.setAngularVelocity(b1.getAngularVelocity() + invI1 * r1.cross(J));
				b2.getVelocity().subtract(J.product(invM2));
				b2.setAngularVelocity(b2.getAngularVelocity() - invI2 * r2.cross(J));
			}
		}
	}
	
	/**
	 * Solves the position constraints
	 * @return boolean if the constraints were held
	 */
	public boolean solvePositionContraints() {
		// get the max linear correction, baumgarte, and allowed penetration from
		// the settings object.
		Settings settings = Settings.getInstance();
		double maxLinearCorrection = settings.getMaxLinearCorrection();
		double allowedPenetration = settings.getLinearTolerance();
		double baumgarte = settings.getBaumgarte();
		
		// track the minimum separation
		double minSeparation = 0.0;
		
		// loop through the contact constraints
		int size = this.contactConstraints.size();
		for (int i = 0; i < size; i++) {
			ContactConstraint contactConstraint = this.contactConstraints.get(i);
			// get the bodies
			Body b1 = contactConstraint.getBody1();
			Body b2 = contactConstraint.getBody2();
			// get their transforms
			Transform t1 = b1.getTransform();
			Transform t2 = b2.getTransform();
			// get the masses
			Mass m1 = b1.getMass();
			Mass m2 = b2.getMass();
			
			double mass1 = m1.getMass();
			double mass2 = m2.getMass();
			
			// get the contact list
			Contact[] contacts = contactConstraint.contacts;
			int cSize = contacts.length;
			
			// get the penetration axis
			Vector2 N = contactConstraint.normal;
			
			// could be 1 or 0 if one object has infinite mass
			double invMass1 = mass1 * m1.getInverseMass();
			double invI1 = mass1 * m1.getInverseInertia();
			// could be 1 or 0 if one object has infinite mass
			double invMass2 = mass2 * m2.getInverseMass();
			double invI2 = mass2 * m2.getInverseInertia();
			
			// solve normal constraints
			for (int k = 0; k < cSize; k++) {
				Contact contact = contacts[k];
				
				// is the contact enabled?
				if (!contact.isEnabled()) continue;
				
				// get the world centers of mass
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
				double jp = -contact.massE * cp;

				// clamp the accumulated position impulse
				double jp0 = contact.jp;
				contact.jp = Math.max(jp0 + jp, 0.0);
				jp = contact.jp - jp0;

				Vector2 J = N.product(jp);

				// translate and rotate the objects
				b1.translate(J.product(invMass1));
				b1.rotate(invI1 * r1.cross(J), c1);
				
				b2.translate(J.product(-invMass2));
				b2.rotate(-invI2 * r2.cross(J), c2);
			}
		}
		// check if the minimum separation between all objects is still
		// greater than or equal to allowed penetration plus half of allowed penetration
		// since we cannot expect it to be above allowed penetration alone
		return minSeparation >= -1.5 * allowedPenetration;
	}
}
