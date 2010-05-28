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

import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Shape;

/**
 * Represents and id for a contact constraint between two {@link Convex}
 * {@link Shape}s on two {@link Body}s.
 * @author William Bittle
 */
public class ContactConstraintId {
	/** The first {@link Body}'s id */
	protected String b1;
	
	/** The second {@link Body}'s id */
	protected String b2;
	
	/** The first {@link Body}'s {@link Convex} {@link Shape} id */
	protected String c1;
	
	/** The second {@link Body}'s {@link Convex} {@link Shape} id */
	protected String c2;
	
	/**
	 * Full constructor.
	 * @param b1 the first {@link Body}
	 * @param b2 the second {@link Body}
	 * @param c1 the first {@link Body}'s {@link Convex} {@link Shape}
	 * @param c2 the second {@link Body}'s {@link Convex} {@link Shape}
	 */
	public ContactConstraintId(Body b1, Body b2, Convex c1, Convex c2) {
		this.b1 = b1.getId();
		this.b2 = b2.getId();
		this.c1 = c1.getId();
		this.c2 = c2.getId();
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
			if ((this.b1.equals(o.b1) && this.b2.equals(o.b2)
			  && this.c1.equals(o.c1) && this.c2.equals(o.c2))
			  // the order of the objects doesn't matter
			 || (this.b1.equals(o.b2) && this.b2.equals(o.b1)
			  && this.c1.equals(o.c2) && this.c2.equals(o.c1))) {
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
		hash = hash * 31 + b1.hashCode() + b2.hashCode();
		hash = hash * 31 + c1.hashCode() + c2.hashCode();
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CONTACT_CONSTRAINT_ID[")
		.append(this.b1).append("|")
		.append(this.b2).append("|")
		.append(this.c1).append("|")
		.append(this.c2).append("]");
		return sb.toString();
	}
}
