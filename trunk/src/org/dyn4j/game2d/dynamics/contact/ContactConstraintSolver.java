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
import org.dyn4j.game2d.geometry.Vector;

/**
 * Represents an impulse based rigid {@link Body} physics collision resolver.
 * <p>
 * This class uses the method from Box2d called Sequential Impulses.  SI, for short, is an iterative
 * method of obtaining a global solution to a number of contacts.  A global solution must be found to 
 * facilitate stable stacking of rigid {@link Body}s.
 * <p>
 * The change in velocity of two bodies due to impulse is given by:
 * <pre>
 * e * (V<sub>ai</sub> - V<sub>bi</sub>) = - (V<sub>af</sub> - V<sub>bf</sub>)
 * </pre>
 * To obtain the velocities relative to the collision normal we dot both sides by N<br />
 * Equation 1:
 * <pre>
 * e * (V<sub>ai</sub> - V<sub>bi</sub>) &middot; N  = - (V<sub>af</sub> - V<sub>bf</sub>) &middot; N
 * </pre>
 * This will allow solving the equations more straight forward.
 * <p>
 * The linear velocity of a point on a rotating body is given by:
 * <pre>
 * V<sub>p</sub> = V<sub>cm</sub> + w x R
 * </pre>
 * The change in linear velocity of a point on a rotating body<br />
 * Equation 2:
 * <pre>
 * V<sub>pf</sub> - V<sub>pi</sub> = V<sub>cmf</sub> - V<sub>cmi</sub> + (w<sub>f</sub> - w<sub>i</sub>) x R
 * V<sub>pf</sub> = V<sub>pi</sub> + V<sub>cmf</sub> - V<sub>cmi</sub> + (w<sub>f</sub> - w<sub>i</sub>) x R
 * </pre>
 * The linear impulse of a colliding body is given by:
 * <pre>
 * &Delta;V<sub>cm</sub> = J * m<sup>-1</sup> or
 * V<sub>cmf</sub> = V<sub>cmi</sub> + J * m<sup>-1</sup>
 * </pre>
 * The angular impulse of a colliding body is given by:
 * <pre>
 * &Delta;w = I<sup>-1</sup>(R x J) or 
 * w<sub>f</sub> = w<sub>i</sub> + I<sup>-1</sup>(R x J)
 * </pre>
 * Substituting for the final angular and linear velocities in Equation 2 with the linear 
 * and angular impulse equations we get:<br />
 * <pre>
 * V<sub>pf</sub> = V<sub>pi</sub> + V<sub>cmi</sub> + J * m<sup>-1</sup> - V<sub>cmi</sub> + (w<sub>i</sub> + I<sup>-1</sup>(R x J) - w<sub>i</sub>) x R
 * by reduction
 * V<sub>pf</sub> = V<sub>pi</sub> + J * m<sup>-1</sup> + (I<sup>-1</sup>(R x J)) x R
 * we can pull out I since for 2D it is a scalar
 * V<sub>pf</sub> = V<sub>pi</sub> + J * m<sup>-1</sup> + I<sup>-1</sup>((R x J) x R)
 * </pre>
 * From the above equation we have two equations, one for each object.  However J will be 
 * negative for one.
 * Equation 3 and 4:
 * <pre>
 * V<sub>acf</sub> = V<sub>aci</sub> + J * m<sub>a</sub><sup>-1</sup> + I<sub>a</sub><sup>-1</sup>((R<sub>a</sub> x J) x R<sub>a</sub>)
 * V<sub>bcf</sub> = V<sub>bci</sub> - J * m<sub>b</sub><sup>-1</sup> - I<sub>b</sub><sup>-1</sup>((R<sub>b</sub> x J) x R<sub>b</sub>)
 * </pre>
 * Where V<sub>cf</sub> and V<sub>ci</sub> are the final and initial velocities of the contact point respectively<br /> 
 * Substituting equations 3 and 4 into equation 1 we get:
 * <pre>
 * e * (V<sub>aci</sub> - V<sub>bci</sub>) &middot; N  = - ((V<sub>aci</sub> + J * m<sub>a</sub><sup>-1</sup> + I<sub>a</sub><sup>-1</sup>((R<sub>a</sub> x J) x R<sub>a</sub>)) - (V<sub>bci</sub> - J * m<sub>b</sub><sup>-1</sup> - I<sub>b</sub><sup>-1</sup>((R<sub>b</sub> x J) x R<sub>b</sub>))) &middot; N
 * </pre>
 * distributing the "&middot; N" on the right side gives
 * <pre>
 * e * (V<sub>aci</sub> - V<sub>bci</sub>) &middot; N  = - V<sub>aci</sub> &middot; N - J * m<sub>a</sub><sup>-1</sup> &middot; N - I<sub>a</sub><sup>-1</sup>((R<sub>a</sub> x J) x R<sub>a</sub>) &middot; N + V<sub>bci</sub> &middot; N - J * m<sub>b</sub><sup>-1</sup> &middot; N - I<sub>b</sub><sup>-1</sup>((R<sub>b</sub> x J) x R<sub>b</sub>)) &middot; N
 * </pre>
 * grouping terms on the right side gives
 * <pre>
 * e * (V<sub>aci</sub> - V<sub>bci</sub>) &middot; N  = - (V<sub>aci</sub> - V<sub>bci</sub>) &middot; N - J * m<sub>a</sub><sup>-1</sup> &middot; N - I<sub>a</sub><sup>-1</sup>((R<sub>a</sub> x J) x R<sub>a</sub>) &middot; N - J * m<sub>b</sub><sup>-1</sup> &middot; N - I<sub>b</sub><sup>-1</sup>((R<sub>b</sub> x J) x R<sub>b</sub>)) &middot; N
 * e * (V<sub>aci</sub> - V<sub>bci</sub>) &middot; N + (V<sub>aci</sub> - V<sub>bci</sub>) &middot; N = - J * m<sub>a</sub><sup>-1</sup> &middot; N - I<sub>a</sub><sup>-1</sup>((R<sub>a</sub> x J) x R<sub>a</sub>) &middot; N - J * m<sub>b</sub><sup>-1</sup> &middot; N - I<sub>b</sub><sup>-1</sup>((R<sub>b</sub> x J) x R<sub>b</sub>)) &middot; N
 * (e + 1) * (V<sub>aci</sub> - V<sub>bci</sub>) &middot; N = - J * m<sub>a</sub><sup>-1</sup> &middot; N - I<sub>a</sub><sup>-1</sup>((R<sub>a</sub> x J) x R<sub>a</sub>) &middot; N - J * m<sub>b</sub><sup>-1</sup> &middot; N - I<sub>b</sub><sup>-1</sup>((R<sub>b</sub> x J) x R<sub>b</sub>)) &middot; N
 * </pre>
 * Spliting J into j * N, where j is the magnitude of the impulse and N is the impulse normal, we get
 * <pre>
 * (e + 1) * (V<sub>aci</sub> - V<sub>bci</sub>) &middot; N = - j * m<sub>a</sub><sup>-1</sup> * N &middot; N - j * I<sub>a</sub><sup>-1</sup>((R<sub>a</sub> x N) x R<sub>a</sub>) &middot; N - j * m<sub>b</sub><sup>-1</sup> * N &middot; N - j * I<sub>b</sub><sup>-1</sup>((R<sub>b</sub> x N) x R<sub>b</sub>)) &middot; N
 * </pre>
 * And we know that N &middot; N = |N|<sup>2</sup>, and since N is a unit vector we have 1<sup>2</sup> = 1, giving us:
 * <pre>
 * (e + 1) * (V<sub>aci</sub> - V<sub>bci</sub>) &middot; N = - j * m<sub>a</sub><sup>-1</sup> - j * I<sub>a</sub><sup>-1</sup>((R<sub>a</sub> x N) x R<sub>a</sub>) &middot; N - j * m<sub>b</sub><sup>-1</sup> - j * I<sub>b</sub><sup>-1</sup>((R<sub>b</sub> x N) x R<sub>b</sub>)) &middot; N
 * </pre>
 * To get rid of those tricky cross products we will refer to the triple product identity:
 * <pre>
 * (A x B) &middot; C = (B x C) &middot; A
 * </pre>
 * We have:
 * <pre>
 * (A x B) x A &middot; B
 * </pre>
 * Let (A x B) = D
 * <pre>
 * D x A &middot; B
 * </pre>
 * Using the identity
 * <pre>
 * D x A &middot; B = (A x B) &middot; D = (A x B) &middot; (A x B) = (A x B)<sup>2</sup>
 * </pre>
 * Simplifying our equation to:
 * <pre>
 * (e + 1) * (V<sub>aci</sub> - V<sub>bci</sub>) &middot; N = - j * m<sub>a</sub><sup>-1</sup> - j * I<sub>a</sub><sup>-1</sup>(R<sub>a</sub> x N)<sup>2</sup> - j * m<sub>b</sub><sup>-1</sup> - j * I<sub>b</sub><sup>-1</sup>(R<sub>b</sub> x N)<sup>2</sup>
 * </pre>
 * Now solving for j we obtain:
 * <pre>
 *              (e + 1) * (V<sub>aci</sub> - V<sub>bci</sub>) &middot; N
 * j = - ----------------------------------------
 *        m<sub>a</sub><sup>-1</sup> + m<sub>b</sub><sup>-1</sup> + I<sub>a</sub><sup>-1</sup>(R<sub>a</sub> x N)<sup>2</sup> + I<sub>b</sub><sup>-1</sup>(R<sub>b</sub> x N)<sup>2</sup>
 * </pre>
 * Once j is found we can get the J by j * N
 * <p>
 * If one object has infinite mass we can further reduce the equation to:
 * <pre>
 *        (e + 1) * (V<sub>aci</sub> - V<sub>bci</sub>) &middot; N
 * j = - --------------------------
 *           m<sub>a</sub><sup>-1</sup> + I<sub>a</sub><sup>-1</sup>(R<sub>a</sub> x N)<sup>2</sup>
 * </pre>
 * To find the final velocities plug in J into the other equations:
 * <pre>
 * V<sub>acmf</sub> = V<sub>acmi</sub> + J * m<sub>a</sub><sup>-1</sup>
 * V<sub>bcmf</sub> = V<sub>bcmi</sub> - J * m<sub>b</sub><sup>-1</sup>
 * 
 * w<sub>af</sub> = w<sub>ai</sub> + I<sub>a</sub><sup>-1</sup>(R<sub>a</sub> x J)
 * w<sub>bf</sub> = w<sub>bi</sub> - I<sub>b</sub><sup>-1</sup>(R<sub>b</sub> x J)
 * </pre>
 * Friction on the system is given by F = f * T.  Where T = &lt;N.y, -N.x&gt; = N.perp() and
 * N is normalized, therefore T is normalized and where f is given by:
 * <pre>
 *                 &micro; * (V<sub>aci</sub> - V<sub>bci</sub>) &middot; T
 * f = - ----------------------------------------
 *        m<sub>a</sub><sup>-1</sup> + m<sub>b</sub><sup>-1</sup> + I<sub>a</sub><sup>-1</sup>(R<sub>a</sub> x T)<sup>2</sup> + I<sub>b</sub><sup>-1</sup>(R<sub>b</sub> x T)<sup>2</sup>
 * </pre>
 * So the equations become:
 * <pre>
 * V<sub>acmf</sub> = V<sub>acmi</sub> + (J + F) * m<sub>a</sub><sup>-1</sup>
 * V<sub>bcmf</sub> = V<sub>bcmi</sub> + (-J - F) * m<sub>b</sub><sup>-1</sup>
 * 
 * w<sub>af</sub> = w<sub>ai</sub> + I<sub>a</sub><sup>-1</sup>(R<sub>a</sub> x (J + F))
 * w<sub>bf</sub> = w<sub>bi</sub> + I<sub>b</sub><sup>-1</sup>(R<sub>b</sub> x (-J - F))
 * </pre>
 * This class performs better by finding the change in velocities instead of the final ones:
 * <pre>
 * &Delta;V<sub>acm</sub> = (J + F) * m<sub>a</sub><sup>-1</sup>
 * &Delta;V<sub>bcm</sub> = (-J - F) * m<sub>b</sub><sup>-1</sup>
 * 
 * &Delta;w<sub>a</sub> = I<sub>a</sub><sup>-1</sup>(R<sub>a</sub> x (J + F))
 * &Delta;w<sub>b</sub> = I<sub>b</sub><sup>-1</sup>(R<sub>b</sub> x (-J - F))
 * </pre>
 * Once the bodies have been updated the solver goes onto another contact.  After performing this
 * many times over all contacts the solver approaches the global solution.  The accuracy is determined
 * by the number of iterations performed.
 * @author William Bittle
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
			Vector c1 = t1.getTransformed(m1.getCenter());
			Vector c2 = t2.getTransformed(m2.getCenter());
			
			// get the contacts
			Contact[] contacts = contactConstraint.contacts;
			int cSize = contacts.length;
			
			// get the penetration axis
			Vector N = contactConstraint.normal;
			// get the tangent vector
			Vector T = N.cross(1.0);
			
			// loop through the contact points
			for (int j = 0; j < cSize; j++) {
				Contact contact = contacts[j];
				// get ra and rb
				Vector r1 = c1.to(contact.p);
				Vector r2 = c2.to(contact.p);
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
				Vector lv1 = r1.cross(b1.getAv()).add(b1.getV());
				Vector lv2 = r2.cross(b2.getAv()).add(b2.getV());
				Vector rv = lv1.subtract(lv2);
				
				// project the relative velocity onto the penetration normal
				double rvn = N.dot(rv);
				// if its negative then the bodies are moving away from one another
				if (rvn < -restitutionVelocity) {
					// use the coefficient of elasticity
					contact.vb += -contactConstraint.e * rvn; 
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
			Vector N = cc.normal;
			// get the tangent vector
			Vector T = N.cross(1.0);
			
			for (int j = 0; j < cc.getContacts().length; j++) {
				Contact contact = cc.getContacts()[j];
				// scale the accumulated impulses by the delta time ratio
				contact.jn *= ratio;
				contact.jt *= ratio;
				
				// apply accumulated impulses to warm start the solver
				Vector J = N.product(contact.jn);
				J.add(T.product(contact.jt));
				b1.getV().add(J.product(invM1));
				b1.setAv(b1.getAv() + invI1 * contact.r1.cross(J));
				b2.getV().subtract(J.product(invM2));
				b2.setAv(b2.getAv() - invI2 * contact.r2.cross(J));
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
			Vector N = contactConstraint.normal;
			Vector T = N.cross(1.0);

			// loop through the contact points
			for (int k = 0; k < cSize; k++) {
				Contact contact = contacts[k];
				// get ra and rb
				Vector r1 = contact.r1;
				Vector r2 = contact.r2;
				
				// get the relative velocity
				Vector lv1 = r1.cross(b1.getAv()).add(b1.getV());
				Vector lv2 = r2.cross(b2.getAv()).add(b2.getV());
				Vector rv = lv1.subtract(lv2);
				
				// project the relative velocity onto the penetration normal
				double rvn = N.dot(rv);
				
				// calculate the impulse using the velocity bias
				double j = -contact.massN * (rvn - contact.vb);
				
				// clamp the accumulated impulse
				double j0 = contact.jn;
				contact.jn = Math.max(j0 + j, 0.0);
				j = contact.jn - j0;
				
				// only update the bodies after processing all the contacts
				Vector J = N.product(j);
				b1.getV().add(J.product(invM1));
				b1.setAv(b1.getAv() + invI1 * r1.cross(J));
				b2.getV().subtract(J.product(invM2));
				b2.setAv(b2.getAv() - invI2 * r2.cross(J));
			}
			
			// evaluate friction impulse
			// since this requires the jn to be computed we must loop through
			// the contacts twice
			for (int k = 0; k < cSize; k++) {
				Contact contact = contacts[k];
				// get ra and rb
				Vector r1 = contact.r1;
				Vector r2 = contact.r2;
				
				// get the relative velocity
				Vector lv1 = r1.cross(b1.getAv()).add(b1.getV());
				Vector lv2 = r2.cross(b2.getAv()).add(b2.getV());
				Vector rv = lv1.subtract(lv2);
				
				// project the relative velocity onto the tangent normal
				double rvt = T.dot(rv);
				// calculate the tangential impulse
				double jt = contact.massT * (-rvt);
				
				// apply the coefficient of friction
				double maxJt = contactConstraint.mu * contact.jn;
				// clamp the accumulated tangential impulse
				double Jt0 = contact.jt;
				contact.jt = Math.max(-maxJt, Math.min(Jt0 + jt, maxJt));
				jt = contact.jt - Jt0;
				
				// apply to the bodies immediately
				Vector J = T.product(jt);
				b1.getV().add(J.product(invM1));
				b1.setAv(b1.getAv() + invI1 * r1.cross(J));
				b2.getV().subtract(J.product(invM2));
				b2.setAv(b2.getAv() - invI2 * r2.cross(J));
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
			Vector N = contactConstraint.normal;
			
			// could be 1 or 0 if one object has infinite mass
			double invMass1 = mass1 * m1.getInverseMass();
			double invI1 = mass1 * m1.getInverseInertia();
			// could be 1 or 0 if one object has infinite mass
			double invMass2 = mass2 * m2.getInverseMass();
			double invI2 = mass2 * m2.getInverseInertia();
			
			// solve normal constraints
			for (int k = 0; k < cSize; k++) {
				Contact contact = contacts[k];
				// get the world centers of mass
				Vector c1 = t1.getTransformed(m1.getCenter());
				Vector c2 = t2.getTransformed(m2.getCenter());

				// get r1 and r2
				Vector r1 = contact.p1.difference(m1.getCenter());
				t1.transformR(r1);
				Vector r2 = contact.p2.difference(m2.getCenter());
				t2.transformR(r2);
				
				// get the world contact points
				Vector p1 = c1.sum(r1);
				Vector p2 = c2.sum(r2);
				Vector dp = p1.subtract(p2);

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

				Vector J = N.product(jp);

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
