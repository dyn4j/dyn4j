/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.dynamics.contact;

/**
 * Represents a contact that has been solved.
 * @author William Bittle
 * @version 5.0.1
 * @since 4.0.0
 */
public interface SolvedContact extends Contact {
	/**
	 * Returns the accumulated normal impulse applied at this point.
	 * @return double the accumulated normal impulse
	 */
	public double getNormalImpulse();
	
	/**
	 * Returns the accumulated tangential impulse applied at this point.
	 * @return double the accumulated tangential impulse
	 */
	public double getTangentialImpulse();
	
	/**
	 * Returns true if this contact was solved.
	 * <p>
	 * A contact is only solved if it's been through the contact solver.
	 * This means that any new contact will return false.  It also means
	 * that contacts that are part of a sensor {@link ContactConstraint}
	 * will return false as will manually disabled {@link ContactConstraint}s.
	 * One last situation is when the contact is part of a {@link ContactConstraint}
	 * that has linearly dependent contacts - one of them will be solved
	 * and the other will be ignored.
	 * disabled.
	 * @return boolean
	 * @since 4.0.0
	 */
	public boolean isSolved();
}
