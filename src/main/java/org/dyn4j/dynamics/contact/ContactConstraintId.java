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

import java.util.UUID;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;

/**
 * Represents and id for a contact constraint between two {@link Convex}
 * {@link Shape}s on two {@link Body}s.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public final class ContactConstraintId {
	/** The first {@link Body}'s id */
	private final UUID body1Id;
	
	/** The second {@link Body}'s id */
	private final UUID body2Id;
	
	/** The first {@link Body}'s {@link Convex} {@link Shape} id */
	private final UUID fixture1Id;
	
	/** The second {@link Body}'s {@link Convex} {@link Shape} id */
	private final UUID fixture2Id;
	
	/**
	 * Full constructor.
	 * @param body1 the first {@link Body}
	 * @param fixture1 the first {@link Body}'s {@link BodyFixture}
	 * @param body2 the second {@link Body}
	 * @param fixture2 the second {@link Body}'s {@link BodyFixture}
	 */
	public ContactConstraintId(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2) {
		this.body1Id = body1.getId();
		this.body2Id = body2.getId();
		this.fixture1Id = fixture1.getId();
		this.fixture2Id = fixture2.getId();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other instanceof ContactConstraintId) {
			ContactConstraintId o = (ContactConstraintId) other;
			if ((this.body1Id.equals(o.body1Id) && this.body2Id.equals(o.body2Id)
			  && this.fixture1Id.equals(o.fixture1Id) && this.fixture2Id.equals(o.fixture2Id))
			  // the order of the objects doesn't matter
			 || (this.body1Id.equals(o.body2Id) && this.body2Id.equals(o.body1Id)
			  && this.fixture1Id.equals(o.fixture2Id) && this.fixture2Id.equals(o.fixture1Id))) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + body1Id.hashCode() + body2Id.hashCode();
		hash = hash * 31 + fixture1Id.hashCode() + fixture2Id.hashCode();
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ContactConstraintId[Body1Id=").append(this.body1Id)
		.append("|Body2Id=").append(this.body2Id)
		.append("|Fixture1Id=").append(this.fixture1Id)
		.append("|Fixture2Id=").append(this.fixture2Id)
		.append("]");
		return sb.toString();
	}

	/**
	 * Returns the id of the first body.
	 * @return String
	 * @since 3.1.2
	 */
	public UUID getBody1Id() {
		return this.body1Id;
	}

	/**
	 * Returns the id of the second body.
	 * @return String
	 * @since 3.1.2
	 */
	public UUID getBody2Id() {
		return this.body2Id;
	}

	/**
	 * Returns the id of the fixture on the first body.
	 * @return String
	 * @since 3.1.2
	 */
	public UUID getFixture1Id() {
		return this.fixture1Id;
	}

	/**
	 * Returns the id of the fixture on the second body.
	 * @return String
	 * @since 3.1.2
	 */
	public UUID getFixture2Id() {
		return this.fixture2Id;
	}
}
