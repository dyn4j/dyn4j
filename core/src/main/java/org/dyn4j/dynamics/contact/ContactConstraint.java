/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.dynamics.contact;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldPoint;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Constraint;
import org.dyn4j.geometry.Matrix22;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Vector2;

/**
 * Represents a {@link Contact} constraint for each {@link Body} pair.  
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public class ContactConstraint extends Constraint implements Shiftable {
	/** The unique contact id */
	protected final ContactConstraintId id;
	
	/** The first {@link Body}'s {@link BodyFixture} */
	protected final BodyFixture fixture1;
	
	/** The second {@link Body}'s {@link BodyFixture} */
	protected final BodyFixture fixture2;
	
	/** The {@link Contact}s */
	protected final List<Contact> contacts;
	
	/** The penetration normal */
	protected Vector2 normal;
	
	/** The tangent of the normal */
	protected Vector2 tangent;
	
	/** The coefficient of friction */
	protected double friction;
	
	/** The coefficient of restitution */
	protected double restitution;

	/** Whether the contact is a sensor contact or not */
	protected boolean sensor;
	
	/** The surface speed of the contact patch */
	protected double tangentSpeed;
	
	/** The K matrix for block solving a contact pair */
	Matrix22 K;
	
	/** The inverse of the {@link #K} matrix */
	Matrix22 invK;
	
	/**
	 * Full constructor.
	 * @param body1 the first {@link Body}
	 * @param fixture1 the first {@link Body}'s {@link BodyFixture}
	 * @param body2 the second {@link Body}
	 * @param fixture2 the second {@link Body}'s {@link BodyFixture}
	 * @param manifold the contact {@link Manifold}
	 * @param friction the friction for the contact constraint
	 * @param restitution the restitution for the contact constraint
	 */
	public ContactConstraint(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Manifold manifold, double friction, double restitution) {
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
		this.contacts = new ArrayList<Contact>(mSize);
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
			this.contacts.add(contact);
		}
		// set the normal
		this.normal = manifold.getNormal();
		// set the tangent
		this.tangent = this.normal.cross(1.0);
		// set coefficients
		this.friction = friction;
		this.restitution = restitution;
		// set the sensor flag (if either fixture is a sensor then the
		// contact constraint between the fixtures is a sensor)
		this.sensor = fixture1.isSensor() || fixture2.isSensor();
		// by default the tangent speed is zero
		this.tangentSpeed = 0;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ContactConstraint[").append(super.toString())
		  .append("|Body1=").append(this.body1.getId())
		  .append("|Fixture1=").append(this.fixture1.getId())
		  .append("|Body2=").append(this.body2.getId())
		  .append("|Fixture2=").append(this.fixture2.getId())
		  .append("|Normal=").append(this.normal)
		  .append("|Tangent=").append(this.tangent)
		  .append("|Friction=").append(this.friction)
		  .append("|Restitution=").append(this.restitution)
		  .append("|IsSensor=").append(this.sensor)
		  .append("|TangentSpeed=").append(this.tangentSpeed)
		  .append("|Contacts={");
		int size = contacts.size();
		for (int i = 0; i < size; i++) {
			if (i != 0) sb.append(",");
			sb.append(this.contacts.get(i));
		}
		sb.append("}]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		int size = this.contacts.size();
		// loop over the contacts
		for (int i = 0; i < size; i++) {
			Contact c = this.contacts.get(i);
			// translate the world space contact point
			c.p.add(shift);
			// c.p1 and c.p2 are in local coordinates
			// and don't need to be shifted
		}
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
	 * Returns the collision tangent.
	 * @return {@link Vector2} the collision tangent
	 */
	public Vector2 getTangent() {
		return this.tangent;
	}
	
	/**
	 * Returns the list of {@link Contact}s.
	 * <p>
	 * Modification of the list is permitted.
	 * @return List&lt;{@link Contact}&gt; the list of {@link Contact}s
	 */
	public List<Contact> getContacts() {
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
	 * Returns the first {@link Body}'s {@link BodyFixture}.
	 * @return {@link BodyFixture} the first {@link Body}'s {@link BodyFixture}
	 */
	public BodyFixture getFixture1() {
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
	 * Returns the second {@link Body}'s {@link BodyFixture}.
	 * @return {@link BodyFixture} the second {@link Body}'s {@link BodyFixture}
	 */
	public BodyFixture getFixture2() {
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
	 * Sets the coefficient of friction for this contact constraint.
	 * @param friction the friction
	 * @since 3.0.2
	 */
	public void setFriction(double friction) {
		this.friction = friction;
	}
	
	/**
	 * Returns the coefficient of restitution for this contact constraint.
	 * @return double
	 */
	public double getRestitution() {
		return this.restitution;
	}

	/**
	 * Sets the coefficient of restitution for this contact constraint.
	 * @param restitution the restitution
	 * @since 3.0.2
	 */
	public void setRestitution(double restitution) {
		this.restitution = restitution;
	}
	
	/**
	 * Returns true if this {@link ContactConstraint} is a sensor.
	 * <p>
	 * By default a contact constraint is a sensor if either of the
	 * two {@link BodyFixture}s are sensor fixtures.  This can be
	 * overridden using the {@link #setSensor(boolean)} method.
	 * @return boolean
	 * @since 1.0.1
	 */
	public boolean isSensor() {
		return this.sensor;
	}
	
	/**
	 * Sets this contact constraint to a sensor if flag is true.
	 * <p>
	 * A sensor constraint is not solved.
	 * @param flag true if this contact constraint should be a sensor
	 * @since 3.0.2
	 */
	public void setSensor(boolean flag) {
		this.sensor = flag;
	}
	
	/**
	 * Returns the surface speed of the contact manifold.
	 * <p>
	 * This will always be zero unless specified manually. This can
	 * be used to set the target velocity at the contact to simulate
	 * a conveyor belt type effect.
	 * @return double
	 * @since 3.0.2
	 */
	public double getTangentSpeed() {
		return this.tangentSpeed;
	}
	
	/**
	 * Sets the target surface speed of the contact manifold.
	 * <p>
	 * The surface speed, in meters / second, is used to simulate a
	 * conveyor belt.
	 * <p>
	 * A value of zero deactivates this feature.
	 * @param speed the speed in Meters / Second
	 * @since 3.0.2
	 */
	public void setTangentSpeed(double speed) {
		this.tangentSpeed = speed;
	}
}
