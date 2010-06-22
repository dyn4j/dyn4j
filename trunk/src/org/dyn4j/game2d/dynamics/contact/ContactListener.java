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
import org.dyn4j.game2d.dynamics.Fixture;
import org.dyn4j.game2d.dynamics.World;

/**
 * Represents an object that is notified on contact events.
 * <p>
 * Implement this interface and register it with the {@link World}
 * to be notified when contact events occur.
 * <p>
 * {@link Body} objects can be removed from the {@link World} from any of these methods.
 * @author William Bittle
 */
public interface ContactListener {
	/**
	 * Called when a two fixtures begin to overlap generating a contact point.
	 * @param point the contact point that was added
	 */
	public abstract void begin(ContactPoint point);
	
	/**
	 * Called when a two {@link Fixture}s begin to separate.
	 * @param point the contact point that was removed
	 */
	public abstract void end(ContactPoint point);
	
	/**
	 * Called when two {@link Fixture}s remain in contact.
	 * @param point the persisted contact point
	 */
	public abstract void persist(PersistedContactPoint point);
	
	/**
	 * Called when a contact has been sensed between two {@link Body}s,
	 * where one or both {@link Body}s are sensors.
	 * @param point the contact point that was sensed
	 */
	public abstract void sensed(SensedContactPoint point);
	
	/**
	 * Called before the contact constraints are solved.
	 * @param point the contact point
	 */
	public abstract void preSolve(ContactPoint point);
	
	/**
	 * Called after a contact has been solved.
	 * @param point the contact point that was solved
	 */
	public abstract void postSolve(SolvedContactPoint point);
}
