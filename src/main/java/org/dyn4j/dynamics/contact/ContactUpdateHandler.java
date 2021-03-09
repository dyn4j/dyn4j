/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.dynamics.BodyFixture;

/**
 * This interface defines the mechanism to report begin, persist, and end events
 * and the method of mixing the friction and restitution coefficients.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.0.0
 */
public interface ContactUpdateHandler {
	/**
	 * Returns the coefficient of friction given the two {@link BodyFixture}s in contact.
	 * @param fixture1 the first fixture
	 * @param fixture2 the second fixture
	 * @return double
	 */
	public double getFriction(BodyFixture fixture1, BodyFixture fixture2);
	
	/**
	 * Returns the coefficient of restitution given the two {@link BodyFixture}s in contact.
	 * @param fixture1 the first fixture
	 * @param fixture2 the second fixture
	 * @return double
	 */
	public double getRestitution(BodyFixture fixture1, BodyFixture fixture2);
	
	/**
	 * Returns the minimum velocity to apply restitution given the two {@link BodyFixture}s in contact.
	 * @param fixture1 the first fixture
	 * @param fixture2 the second fixture
	 * @return double
	 * @since 4.2.0
	 */
	public double getRestitutionVelocity(BodyFixture fixture1, BodyFixture fixture2);
	
	/**
	 * Called when the given contact is a new contact.
	 * <p>
	 * A new contact is a contact that either didn't exist before or one that could
	 * not be warm started.
	 * @param contact the new contact
	 */
	public void begin(Contact contact);
	
	/**
	 * Called when the given contact was persisted.
	 * <p>
	 * A persisted contact is a contact that existed before and the new contact
	 * just updates the old.
	 * @param oldContact the old contact
	 * @param newContact the new contact
	 */
	public void persist(Contact oldContact, Contact newContact);
	
	/**
	 * Called when the given contact was ended.
	 * <p>
	 * An ended contact is a contact that no longer exists.
	 * @param contact the ended contact
	 */
	public void end(Contact contact);
}
