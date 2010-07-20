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

import org.dyn4j.game2d.collision.manifold.Manifold;
import org.dyn4j.game2d.collision.manifold.ManifoldPoint;
import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.Constraint;
import org.dyn4j.game2d.dynamics.Fixture;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Represents a {@link Contact} constraint for each {@link Body} pair.  
 * @author William Bittle
 */
public class ContactConstraint extends Constraint {
	/** The unique contact id */
	protected ContactConstraintId id;
	
	/** The first {@link Body}'s {@link Fixture} */
	protected Fixture fixture1;
	
	/** The second {@link Body}'s {@link Fixture} */
	protected Fixture fixture2;
	
	/** The {@link Contact}s */
	protected Contact[] contacts;
	
	/** The penetration normal */
	protected Vector2 normal;
	
	/** The coefficient of friction */
	protected double friction;
	
	/** The coefficient of restitution */
	protected double restitution;

	/** Whether the contact is a sensor contact or not */
	protected boolean sensor;
	
	/**
	 * Full constructor.
	 * @param body1 the first {@link Body}
	 * @param fixture1 the first {@link Body}'s {@link Fixture}
	 * @param body2 the second {@link Body}
	 * @param fixture2 the second {@link Body}'s {@link Fixture}
	 * @param manifold the contact {@link Manifold}
	 * @param friction the contact's coefficient of friction
	 * @param restitution the contact's coefficient of restitution
	 */
	public ContactConstraint(Body body1, Fixture fixture1, Body body2, Fixture fixture2,
			Manifold manifold, double friction, double restitution) {
		super(body1, body2);
		// set the involved convex shapes
		this.fixture1 = fixture1;
		this.fixture2 = fixture2;
		// create the constraint id
		this.id = new ContactConstraintId(body1, fixture1, body2, fixture2);
		// get the manifold points
		List<ManifoldPoint> points = manifold.getPoints();
		// get the manifold point size
		int mSize = points.size();
		// create contact array
		this.contacts = new Contact[mSize];
		// create contacts for each point
		for (int l = 0; l < mSize; l++) {
			// get the manifold point
			ManifoldPoint point = points.get(l);
			// create a contact from the manifold point
			Contact contact = new Contact(point.getId(),
					                      point.getPoint(), 
					                      point.getDepth(), 
					                      this.body1.getLocalPoint(point.getPoint()), 
					                      this.body2.getLocalPoint(point.getPoint()));
			// add the contact to the array
			this.contacts[l] = contact;
		}
		// set the normal
		this.normal = manifold.getNormal();
		// set the coefficients
		this.friction = friction;
		this.restitution = restitution;
		// set the sensor flag (if either fixture is a sensor then the
		// contact constraint between the fixtures is a sensor)
		this.sensor = fixture1.isSensor() || fixture2.isSensor();
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
		.append(this.fixture1).append("|")
		.append(this.fixture2).append("|")
		.append(this.normal).append("|")
		.append(this.friction).append("|")
		.append(this.restitution).append("|")
		.append(this.sensor).append("|{");
		int size = contacts.length;
		for (int i = 0; i < size; i++) {
			sb.append(contacts[i]);
		}
		sb.append("}]");
		return sb.toString();
	}
	
	/**
	 * Returns the contact constraint id.
	 * @return {@link ContactConstraintId}
	 */
	public ContactConstraintId getId() {
		return this.id;
	}
	
	/**
	 * Returns the collision normal.
	 * @return {@link Vector2} the collision normal
	 */
	public Vector2 getNormal() {
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
		return this.body1;
	}
	
	/**
	 * Returns the first {@link Body}'s {@link Fixture}.
	 * @return {@link Fixture} the first {@link Body}'s {@link Fixture}
	 */
	public Fixture getFixture1() {
		return this.fixture1;
	}
	
	/**
	 * Returns the second {@link Body}.
	 * @return {@link Body} the second {@link Body}
	 */
	public Body getBody2() {
		return this.body2;
	}
	
	/**
	 * Returns the second {@link Body}'s {@link Fixture}.
	 * @return {@link Fixture} the second {@link Body}'s {@link Fixture}
	 */
	public Fixture getFixture2() {
		return this.fixture2;
	}
	
	/**
	 * Returns the coefficient of friction for this contact constraint.
	 * @return double
	 */
	public double getFriction() {
		return this.friction;
	}
	
	/**
	 * Returns the coefficient of restitution for this contact constraint.
	 * @return double
	 */
	public double getRestitution() {
		return this.restitution;
	}
	
	/**
	 * Returns true if this {@link ContactConstraint} is a sensor.
	 * @return boolean
	 */
	public boolean isSensor() {
		return this.sensor;
	}
}
