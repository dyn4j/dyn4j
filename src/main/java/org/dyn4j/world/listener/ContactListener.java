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
package org.dyn4j.world.listener;

import org.dyn4j.collision.manifold.ManifoldPointId;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.ContactConstraint;
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
 * @version 5.0.1
 * @since 1.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public interface ContactListener<T extends PhysicsBody> extends WorldEventListener {
	/**
	 * Called when two {@link BodyFixture}s begin to overlap, generating a contact point.
	 * <p>
	 * NOTE: The {@link ContactConstraint} stored in the <code>collision</code> parameter
	 * is being updated when this method is called. As a result, the data stored in the 
	 * contact constraint may not be accurate. If you need to access the final state of the 
	 * contact constraint, use the {@link #collision(ContactCollisionData)} 
	 * method.
	 * @param collision the collision data
	 * @param contact the contact
	 */
	public abstract void begin(ContactCollisionData<T> collision, Contact contact);

	/**
	 * Called when two {@link BodyFixture}s remain in contact.
	 * <p>
	 * For a {@link Contact} to persist, the {@link Settings#isWarmStartingEnabled()} must be true and the
	 * {@link ManifoldPointId}s must match.
	 * <p>
	 * For shapes with vertices only, the manifold ids will be identical when the features of the colliding
	 * fixtures are the same.  For rounded shapes, the manifold points must be within a specified tolerance
	 * defined in {@link Settings#getMaximumWarmStartDistance()}.
	 * <p>
	 * NOTE: The {@link ContactConstraint} stored in the <code>collision</code> parameter
	 * is being updated when this method is called. As a result, the data stored in the 
	 * contact constraint may not be accurate. If you need to access the final state of the 
	 * contact constraint, use the {@link #collision(ContactCollisionData)} 
	 * method.
	 * @param collision the collision data
	 * @param oldContact the old contact
	 * @param newContact the new contact
	 */
	public abstract void persist(ContactCollisionData<T> collision, Contact oldContact, Contact newContact);

	/**
	 * Called when two {@link BodyFixture}s begin to separate and the contact point is no longer valid.
	 * <p>
	 * This can happen in one of two ways. First, the fixtures in question have separated such that there's
	 * no longer any collision between them. Second, the fixtures could still be in collision, but the features
	 * that are in collision on those fixtures have changed.
	 * <p>
	 * NOTE: The {@link ContactConstraint} stored in the <code>collision</code> parameter
	 * is being updated when this method is called. As a result, the data stored in the 
	 * contact constraint may not be accurate. If you need to access the final state of the 
	 * contact constraint, use the {@link #collision(ContactCollisionData)} 
	 * method.
	 * @param collision the collision data
	 * @param contact the contact
	 */
	public abstract void end(ContactCollisionData<T> collision, Contact contact);

	/**
	 * Called when a body or fixture is removed from the world that had existing contacts.
	 * <p>
	 * This is different than the {@link #end(ContactCollisionData, Contact)} event. This will only be
	 * called when a user removes a fixture or body that's currently in collision.  The 
	 * {@link #end(ContactCollisionData, Contact)} applies when the fixtures separate and are no longer
	 * in collision.
	 * <p>
	 * This is called before the {@link DestructionListener#destroyed(org.dyn4j.dynamics.contact.ContactConstraint)}
	 * method in the event processing needed to occur by both listeners. There's no requirement that it must be
	 * processed in both (or at all) though.
	 * @param collision the collision data
	 * @param contact the contact
	 */
	public abstract void destroyed(ContactCollisionData<T> collision, Contact contact);
	
	/**
	 * Called after the {@link ContactConstraint} has been updated after collision detection, but before
	 * it's added to the solver to be solved.
	 * <p>
	 * This method is only called if {@link ContactCollisionData#isManifoldCollision()} returns true.
	 * <p>
	 * This listener is the place to use the {@link ContactConstraint#setEnabled(boolean)}, 
	 * and {@link ContactConstraint#setTangentSpeed(double)} methods. You can get access to the 
	 * {@link ContactConstraint} via the {@link ContactCollisionData#getContactConstraint()} method.
	 * @param collision the collision data
	 * @since 4.1.0
	 */
	public abstract void collision(ContactCollisionData<T> collision);
	
	/**
	 * Called before contact constraints are solved.
	 * @param collision the collision data
	 * @param contact the contact
	 */
	public abstract void preSolve(ContactCollisionData<T> collision, Contact contact);
	
	/**
	 * Called after contacts have been solved.
	 * <p>
	 * NOTE: This method will be called for {@link SolvedContact}s even when the {@link SolvedContact#isSolved()}
	 * is false. This should only occur in situations with multiple contact points that produce a linearly
	 * dependent system. These contacts are thus ignored during solving, but still reported here.
	 * @param collision the collision data
	 * @param contact the contact
	 */
	public abstract void postSolve(ContactCollisionData<T> collision, SolvedContact contact);
}
