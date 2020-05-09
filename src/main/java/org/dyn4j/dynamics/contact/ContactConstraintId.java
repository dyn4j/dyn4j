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
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;

/**
 * Represents and id for a contact constraint between two {@link Convex}
 * {@link Shape}s on two {@link Body}s.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 * @deprecated Deprecated in 4.0.0. No longer needed.
 */
@Deprecated
public final class ContactConstraintId {
	/** The first {@link Body} */
	private final PhysicsBody body1;
	
	/** The second {@link Body} */
	private final PhysicsBody body2;
	
	/** The first {@link Body}'s {@link Convex} {@link Shape} */
	private final BodyFixture fixture1;
	
	/** The second {@link Body}'s {@link Convex} {@link Shape} */
	private final BodyFixture fixture2;
	
	/**
	 * Full constructor.
	 * @param body1 the first {@link Body}
	 * @param fixture1 the first {@link Body}'s {@link BodyFixture}
	 * @param body2 the second {@link Body}
	 * @param fixture2 the second {@link Body}'s {@link BodyFixture}
	 */
	public ContactConstraintId(PhysicsBody body1, BodyFixture fixture1, PhysicsBody body2, BodyFixture fixture2) {
		this.body1 = body1;
		this.body2 = body2;
		this.fixture1 = fixture1;
		this.fixture2 = fixture2;
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
			if ((this.body1 == o.body1 && this.body2 == o.body2
			  && this.fixture1 == o.fixture1 && this.fixture2 == o.fixture2)
			  // the order of the objects doesn't matter
			 || (this.body1 == o.body2 && this.body2 == o.body1
			  && this.fixture1 == o.fixture2 && this.fixture2 == o.fixture1)) {
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
		hash = hash * 31 + body1.hashCode() + body2.hashCode();
		hash = hash * 31 + fixture1.hashCode() + fixture2.hashCode();
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ContactConstraintId[Body1=").append(this.body1.hashCode())
		.append("|Body2=").append(this.body2.hashCode())
		.append("|Fixture1=").append(this.fixture1.hashCode())
		.append("|Fixture2=").append(this.fixture2.hashCode())
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the first body.
	 * @return Body
	 * @since 3.4.0
	 */
	public PhysicsBody getBody1() {
		return this.body1;
	}

	/**
	 * Returns the second body.
	 * @return Body
	 * @since 3.4.0
	 */
	public PhysicsBody getBody2() {
		return this.body2;
	}

	/**
	 * Returns the fixture on the first body.
	 * @return BodyFixture
	 * @since 3.4.0
	 */
	public BodyFixture getFixture1() {
		return this.fixture1;
	}

	/**
	 * Returns the fixture on the second body.
	 * @return BodyFixture
	 * @since 3.4.0
	 */
	public BodyFixture getFixture2() {
		return this.fixture2;
	}
}
