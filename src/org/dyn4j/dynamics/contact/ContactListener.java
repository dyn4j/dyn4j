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

import org.dyn4j.Listener;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.CollisionListener;
import org.dyn4j.dynamics.World;

/**
 * Represents an object that is notified of contact events.
 * <p>
 * Implement this interface and register it with the {@link World}
 * to be notified when contact events occur.  Contact events occur after all 
 * {@link CollisionListener} events have been raised.
 * <p>
 * Modification of the {@link World} is permitted from any of these methods.
 * <p>
 * If a body is to be removed, make sure to return false to disable the contact.  Otherwise
 * the contact between the bodies will still be resolved even if the body has been removed.
 * If a body is removed you should check the remaining contacts for that body and return
 * false from the those methods as well.
 * @author William Bittle
 * @version 3.1.0
 * @since 1.0.0
 */
public interface ContactListener extends Listener {
	/**
	 * Called when a contact has been sensed between two {@link Body}s,
	 * where one or both {@link Body}'s {@link BodyFixture}s are sensors.
	 * @param point the contact point that was sensed
	 */
	public abstract void sensed(ContactPoint point);
	
	/**
	 * Called when two {@link BodyFixture}s begin to overlap, generating a contact point.
	 * @param point the contact point that was added
	 * @return boolean true if the contact should remain enabled
	 */
	public abstract boolean begin(ContactPoint point);
	
	/**
	 * Called when two {@link BodyFixture}s begin to separate.
	 * @param point the contact point that was removed
	 */
	public abstract void end(ContactPoint point);
	
	/**
	 * Called when two {@link BodyFixture}s remain in contact.
	 * @param point the persisted contact point
	 * @return boolean true if the contact should remain enabled
	 */
	public abstract boolean persist(PersistedContactPoint point);
	
	/**
	 * Called before contact constraints are solved.
	 * @param point the contact point
	 * @return boolean true if the contact should remain enabled
	 */
	public abstract boolean preSolve(ContactPoint point);
	
	/**
	 * Called after contacts have been solved.
	 * @param point the contact point that was solved
	 */
	public abstract void postSolve(SolvedContactPoint point);
}
