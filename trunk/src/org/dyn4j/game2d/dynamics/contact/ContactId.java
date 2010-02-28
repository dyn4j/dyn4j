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

/**
 * Represents a contact ID.
 * <p>
 * This is used to match contacts when attempting to warm
 * start the {@link ContactConstraintSolver}.
 * @author William Bittle
 */
public class ContactId {
	/** The index of the reference edge */
	protected int referenceIndex;
	
	/** The index of the incident edge */
	protected int incidentIndex;
	
	/** The index of the manifold point */
	protected int index;
	
	/** Whether the shape/body order was flipped */
	protected boolean flip;
	
	/**
	 * Default constructor.
	 */
	public ContactId() {}
	
	/**
	 * Full constructor.
	 * @param referenceIndex the referenced edge index
	 * @param incidentIndex the incident edge index
	 * @param index the manifold point index
	 * @param flip if the shape/body order was flipped
	 */
	public ContactId(int referenceIndex, int incidentIndex, int index, boolean flip) {
		this.referenceIndex = referenceIndex;
		this.incidentIndex = incidentIndex;
		this.index = index;
		this.flip = flip;
	}
	
	/**
	 * Returns true if the contacts are the same.
	 * @param id the id
	 * @return booolean
	 */
	public boolean equals(ContactId id) {
		if (id.referenceIndex == this.referenceIndex
		 && id.incidentIndex == this.incidentIndex
		 && id.index == this.index
		 && id.flip == this.flip) {
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CONTACT_ID[")
		.append(this.referenceIndex).append("|")
		.append(this.incidentIndex).append("|")
		.append(this.index).append("|")
		.append(this.flip).append("]");
		return sb.toString();
	}
}
