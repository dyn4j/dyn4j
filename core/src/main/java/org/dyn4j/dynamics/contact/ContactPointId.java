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

import org.dyn4j.collision.manifold.ManifoldPointId;

/**
 * Represents a contact point id to identify contacts from frame to frame.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.1.2
 */
public final class ContactPointId {
	/** The contact constraint id */
	private final ContactConstraintId contactConstraintId;
	
	/** The manifold point id */
	private final ManifoldPointId manifoldPointId;
	
	/**
	 * Full constructor.
	 * @param contactConstraintId the contact constraint id
	 * @param manifoldPointId the manifold point id
	 */
	public ContactPointId(ContactConstraintId contactConstraintId, ManifoldPointId manifoldPointId) {
		this.contactConstraintId = contactConstraintId;
		this.manifoldPointId = manifoldPointId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other instanceof ContactPointId) {
			ContactPointId id = (ContactPointId)other;
			if (id.contactConstraintId.equals(this.contactConstraintId) && id.manifoldPointId.equals(this.manifoldPointId)) {
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
		hash = hash * 31 + this.contactConstraintId.hashCode();
		hash = hash * 31 + this.manifoldPointId.hashCode();
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ContactPointId[ContactConstraintId=").append(this.contactConstraintId)
		.append("|ManifoldPointId=").append(this.manifoldPointId)
		.append("]");
		return sb.toString();
	}

	/**
	 * Returns the {@link ContactConstraintId} for this contact.
	 * @return {@link ContactConstraintId}
	 */
	public ContactConstraintId getContactConstraintId() {
		return this.contactConstraintId;
	}

	/**
	 * Returns the {@link ManifoldPointId} for this contact.
	 * @return {@link ManifoldPointId}
	 */
	public ManifoldPointId getManifoldPointId() {
		return this.manifoldPointId;
	}
}
