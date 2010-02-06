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

/**
 * Represents a contact edge connecting two bodies.
 * @author William Bittle
 */
public class ContactEdge {
	/** The connected body */
	protected Body other;
	
	/** The {@link ContactConstraint} between the bodies */
	protected ContactConstraint contactConstraint;
	
	/**
	 * Full constructor.
	 * @param other the other {@link Body} in contact
	 * @param contactConstraint the {@link ContactConstraint} between the {@link Body}s
	 */
	public ContactEdge(Body other, ContactConstraint contactConstraint) {
		this.other = other;
		this.contactConstraint = contactConstraint;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.contactConstraint).append("->").append(this.other);
		return sb.toString();
	}
	
	/**
	 * Returns the other body connected in the contact constraint.
	 * @return {@link Body}
	 */
	public Body getOther() {
		return this.other;
	}
	
	/**
	 * Returns the contact constraint.
	 * @return {@link ContactConstraint}
	 */
	public ContactConstraint getContactConstraint() {
		return this.contactConstraint;
	}
}
