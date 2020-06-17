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
package org.dyn4j.world.listener;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.PhysicsWorld;

/**
 * Represents an object that is notified of contact events.
 * <p>
 * Implement this interface and register it with a {@link PhysicsWorld}
 * to be notified when contact events occur.  Contact events occur after all 
 * {@link CollisionListener} events have been raised.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public interface ContactListener<T extends PhysicsBody> extends WorldEventListener {
	/**
	 * Called before contact constraints are solved.
	 * @param collision the collision data
	 * @param contact the contact
	 */
	public abstract void preSolve(ContactCollisionData<T> collision, Contact contact);
	
	/**
	 * Called when two {@link BodyFixture}s begin to overlap, generating a contact point.
	 * @param collision the collision data
	 * @param contact the contact
	 */
	public abstract void begin(ContactCollisionData<T> collision, Contact contact);
	
	/**
	 * Called when two {@link BodyFixture}s begin to separate and the contact point is no longer valid.
	 * @param collision the collision data
	 * @param contact the contact
	 */
	public abstract void end(ContactCollisionData<T> collision, Contact contact);
	
	/**
	 * Called when two {@link BodyFixture}s remain in contact.
	 * @param collision the collision data
	 * @param oldContact the old contact
	 * @param newContact the new contact
	 */
	public abstract void persist(ContactCollisionData<T> collision, Contact oldContact, Contact newContact);
	
	/**
	 * Called after contacts have been solved.
	 * @param collision the collision data
	 * @param contact the contact
	 */
	public abstract void postSolve(ContactCollisionData<T> collision, SolvedContact contact);
}
