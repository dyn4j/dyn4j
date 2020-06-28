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
package org.dyn4j.world.listener;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.world.PhysicsWorld;

/**
 * Interface to listen for implicit destruction events.
 * <p>
 * These events can happen when, for example, a {@link PhysicsBody} is removed from a {@link PhysicsWorld}
 * where it was attached to a {@link Joint}.  The joint must be removed as well.  These methods
 * will be called when any such implicit destruction events happen.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public interface DestructionListener<T extends PhysicsBody> extends WorldEventListener {
	/**
	 * Called when implicit destruction of a {@link Joint} has occurred.
	 * <p>
	 * Modification of the {@link PhysicsWorld} is permitted during this method.
	 * @see PhysicsWorld#removeBody(CollisionBody)
	 * @see PhysicsWorld#removeAllBodiesAndJoints(boolean)
	 * @param joint the {@link Joint} that was destroyed
	 */
	public void destroyed(Joint<T> joint);
	
	/**
	 * Called when implicit destruction of a {@link ContactConstraint} has occurred.
	 * <p>
	 * Modification of the {@link PhysicsWorld} is not permitted during this method.
	 * @see PhysicsWorld#removeBody(CollisionBody)
	 * @see PhysicsWorld#removeAllBodiesAndJoints(boolean)
	 * @param contactConstraint the {@link ContactConstraint} that was destroyed
	 */
	public void destroyed(ContactConstraint<T> contactConstraint);
	
	/**
	 * Called when implicit destruction of a {@link PhysicsBody} has occurred.
	 * <p>
	 * Modification of the {@link PhysicsWorld} is not permitted during this method.
	 * @see PhysicsWorld#removeAllBodiesAndJoints(boolean)
	 * @param body the {@link PhysicsBody} that was destroyed
	 * @since 1.0.2
	 */
	public void destroyed(T body);
}
