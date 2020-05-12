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

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;
import org.dyn4j.world.ContactCollisionData;

/**
 * Extended version of the {@link CollisionListener} class to support reporting {@link ContactCollisionData} events.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 * @param <T> the {@link CollisionBody} type
 * @see CollisionListener
 */
public interface ContactCollisionListener<T extends PhysicsBody> extends CollisionListener<T, BodyFixture> {
	/**
	 * Called when two {@link BodyFixture}s are colliding and a {@link ContactConstraint} has been setup.
	 * <p>
	 * {@link PhysicsBody} objects can have many {@link Convex} {@link Shape}s that make up their geometry.  Because
	 * of this, this method may be called multiple times if two multi-fixtured {@link PhysicsBody}s are colliding.
	 * <p>
	 * Modification of the {@link ContactConstraint} object is allowed.  The {@link ContactConstraint} is used to 
	 * resolve the collision.
	 * <p>
	 * Return false from this method to stop processing of this collision.  Other 
	 * {@link CollisionListener}s will still be notified of this event, however, no further
	 * collision or contact events will occur for this pair.
	 * @param collision the contact collision data
	 * @return boolean true if processing should continue for this collision
	 */
	public abstract boolean collision(ContactCollisionData<T> collision);
}
