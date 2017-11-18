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

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Represents a contact point and used to report events via the {@link ContactListener}.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public class ContactPoint {
	/** The contact point id */
	protected final ContactPointId id;
	
	/** The first {@link Body} in contact */
	protected final Body body1;
	
	/** The second {@link Body} in contact */
	protected final Body body2;
	
	/** The first {@link Body}'s {@link BodyFixture} */
	protected final BodyFixture fixture1;
	
	/** The second {@link Body}'s {@link BodyFixture} */
	protected final BodyFixture fixture2;
	
	/** The world space contact point */
	protected Vector2 point;
	
	/** The world space contact normal */
	protected Vector2 normal;
	
	/** The penetration depth */
	protected double depth;
	
	/**
	 * Full constructor.
	 * @param id the contact point id
	 * @param body1 the first {@link Body} in contact
	 * @param fixture1 the first {@link Body}'s {@link BodyFixture}
	 * @param body2 the second {@link Body} in contact
	 * @param fixture2 the second {@link Body}'s {@link BodyFixture}
	 * @param point the world space contact point
	 * @param normal the world space contact normal
	 * @param depth the penetration depth
	 */
	public ContactPoint(ContactPointId id, Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2,
			Vector2 point, Vector2 normal, double depth) {
		this.id = id;
		this.body1 = body1;
		this.fixture1 = fixture1;
		this.body2 = body2;
		this.fixture2 = fixture2;
		this.point = point;
		this.normal = normal;
		this.depth = depth;
	}
	
	/**
	 * Copy constructor (shallow).
	 * @param contactPoint the {@link ContactPoint} to copy
	 */
	public ContactPoint(ContactPoint contactPoint) {
		if (contactPoint == null) throw new NullPointerException(Messages.getString("dynamics.contact.contactPoint.nullContactPoint"));
		// shallow copy all the fields
		this.id = contactPoint.id;
		this.body1 = contactPoint.body1;
		this.fixture1 = contactPoint.fixture1;
		this.body2 = contactPoint.body2;
		this.fixture2 = contactPoint.fixture2;
		this.point = contactPoint.point;
		this.normal = contactPoint.normal;
		this.depth = contactPoint.depth;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ContactPoint[Id=").append(this.id)
		.append("|Body1=").append(this.body1.getId())
		.append("|Fixture1=").append(this.fixture1.getId())
		.append("|Body2=").append(this.body2.getId())
		.append("|Fixture2=").append(this.fixture2.getId())
		.append("|Point=").append(this.point)
		.append("|Normal=").append(this.normal)
		.append("|Depth=").append(this.depth)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the contact point id.
	 * @return {@link ContactPointId}
	 * @since 3.1.2
	 */
	public ContactPointId getId() {
		return this.id;
	}
	
	/**
	 * Returns the contact point.
	 * @return {@link Vector2}
	 */
	public Vector2 getPoint() {
		return this.point;
	}
	
	/**
	 * Returns the normal.
	 * @return {@link Vector2}
	 */
	public Vector2 getNormal() {
		return this.normal;
	}
	
	/**
	 * Returns the depth.
	 * @return double
	 */
	public double getDepth() {
		return this.depth;
	}
	
	/**
	 * Returns the first {@link Body}.
	 * @return {@link Body}
	 */
	public Body getBody1() {
		return this.body1;
	}
	
	/**
	 * Returns the second {@link Body}.
	 * @return {@link Body}
	 */
	public Body getBody2() {
		return this.body2;
	}
	
	/**
	 * Returns the first {@link Body}'s {@link BodyFixture}.
	 * @return {@link BodyFixture}
	 */
	public BodyFixture getFixture1() {
		return this.fixture1;
	}
	
	/**
	 * Returns the second {@link Body}'s {@link BodyFixture}.
	 * @return {@link BodyFixture}
	 */
	public BodyFixture getFixture2() {
		return this.fixture2;
	}
}
