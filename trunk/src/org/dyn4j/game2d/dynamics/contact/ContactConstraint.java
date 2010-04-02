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

import org.dyn4j.game2d.collision.manifold.Manifold;
import org.dyn4j.game2d.collision.manifold.ManifoldPoint;
import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.Constraint;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Shape;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Represents a {@link Contact} constraint for each {@link Body} pair.  
 * @author William Bittle
 */
public class ContactConstraint extends Constraint {
	/** The unique contact id */
	protected String id;
	
	/** The first {@link Body}'s {@link Convex} {@link Shape} */
	protected Convex c1;
	
	/** The second {@link Body}'s {@link Convex} {@link Shape} */
	protected Convex c2;
	
	/** The {@link Contact}s */
	protected Contact[] contacts;
	
	/** The penetration normal */
	protected Vector normal;
	
	/** The coefficient of friction */
	protected double mu;
	
	/** The coefficient of restitution */
	protected double e;
	
	/**
	 * Full constructor.
	 * @param b1 the first {@link Body}
	 * @param c1 the first {@link Body}'s {@link Convex} {@link Shape}
	 * @param b2 the second {@link Body}
	 * @param c2 the second {@link Body}'s {@link Convex} {@link Shape}
	 * @param m the contact manifold
	 */
	public ContactConstraint(Body b1, Convex c1, Body b2, Convex c2, Manifold m) {
		super(b1, b2);
		// set the involved convexes
		this.c1 = c1;
		this.c2 = c2;
		// set the id
		StringBuilder sb = new StringBuilder();
		sb.append(b1.getId()).append(b2.getId()).append(c1.getId()).append(c2.getId());
		this.id = sb.toString();
		// get the manifold point size
		int mSize = m.getPoints().size();
		// create contact array
		this.contacts = new Contact[mSize];
		for (int l = 0; l < mSize; l++) {
			// get the manifold point
			ManifoldPoint point = m.getPoints().get(l);
			// create a contact id for the point
			ContactId id = new ContactId(m.getReferenceIndex(), 
					                     m.getIncidentIndex(), 
					                     point.getIndex());
			// create a contact from the manifold point
			Contact contact = new Contact(id, point.getPoint(), 
					                      point.getDepth(), 
					                      this.b1.getLocalPoint(point.getPoint()), 
					                      this.b2.getLocalPoint(point.getPoint()));
			// add the contact to the array
			this.contacts[l] = contact;
		}
		// set the normal
		this.normal = m.getNormal();
		// compute mu and e
		this.mu = Math.sqrt(b1.getMu() * b2.getMu());
		this.e = Math.max(b1.getE(), b2.getE());
		// default to false
		this.onIsland = false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CONTACT_CONSTRAINT[")
		.append(super.toString()).append("|")
		.append(this.c1).append("|")
		.append(this.c2).append("|")
		.append(this.normal).append("|")
		.append(this.mu).append("|")
		.append(this.e).append("|{");
		int size = contacts.length;
		for (int i = 0; i < size; i++) {
			sb.append(contacts[i]);
		}
		sb.append("}]");
		return sb.toString();
	}
	
	/**
	 * Returns the collision normal.
	 * @return {@link Vector} the collision normal
	 */
	public Vector getNormal() {
		return this.normal;
	}
	
	/**
	 * Returns the array of {@link Contact}s.
	 * @return {@link Contact}[] the array of {@link Contact}s
	 */
	public Contact[] getContacts() {
		return this.contacts;
	}
	
	/**
	 * Returns the first {@link Body}.
	 * @return {@link Body} the first {@link Body}
	 */
	public Body getBody1() {
		return this.b1;
	}
	
	/**
	 * Returns the first {@link Body}'s {@link Convex} {@link Shape}.
	 * @return {@link Convex} the first {@link Body}'s {@link Convex} {@link Shape}
	 */
	public Convex getConvex1() {
		return this.c1;
	}
	
	/**
	 * Returns the second {@link Body}.
	 * @return {@link Body} the second {@link Body}
	 */
	public Body getBody2() {
		return this.b2;
	}
	
	/**
	 * Returns the second {@link Body}'s {@link Convex} {@link Shape}.
	 * @return {@link Convex} the second {@link Body}'s {@link Convex} {@link Shape}
	 */
	public Convex getConvex2() {
		return this.c2;
	}
}
