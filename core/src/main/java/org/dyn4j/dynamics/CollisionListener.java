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
package org.dyn4j.dynamics;

import org.dyn4j.Listener;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;

/**
 * Interface to listen for collision events.
 * <p>
 * Events for a pair of bodies (as long as they pass the criteria for the event to be called)
 * will be called in the following order:
 * <ol>
 * <li>Collision detected by the broadphase: {@link #collision(Body, BodyFixture, Body, BodyFixture)}</li>
 * <li>Collision detected by the narrowphase: {@link #collision(Body, BodyFixture, Body, BodyFixture, Penetration)}</li>
 * <li>Contact manifold created by the manifold solver:{@link #collision(Body, BodyFixture, Body, BodyFixture, Manifold)}</li>
 * <li>Contact constraint created: {@link #collision(ContactConstraint)}</li>
 * </ol>
 * Returning false from any of the listener methods will halt processing of that event.  Other
 * {@link CollisionListener}s will still be notified of that event, but subsequent events will
 * not occur (this indicates that you didn't want the collision to be resolved later).
 * <p>
 * Modification of the {@link World} is permitted in these methods.  Modification of the {@link Body}'s
 * fixtures is not permitted (adding/removing will cause a runtime exception).
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public interface CollisionListener extends Listener {
	/**
	 * Called when two {@link BodyFixture}s are colliding as determined by the {@link BroadphaseDetector}.
	 * 	 * <p>
	 * {@link Body} objects can have many {@link Convex} {@link Shape}s that make up their geometry.  Because
	 * of this, this method may be called multiple times if two multi-fixtured {@link Body}s are colliding.
	 * <p>
	 * This method is called when the two {@link BodyFixture}'s expanded {@link AABB}s are overlapping.
	 * Visually the bodies may not appear to be colliding (which is a valid case).  If you need to 
	 * make sure the {@link Body}s are colliding, and not just their AABBs, use the 
	 * {@link #collision(Body, BodyFixture, Body, BodyFixture, Penetration)} method.
	 * <p>
	 * Return false from this method to stop processing of this collision.  Other 
	 * {@link CollisionListener}s will still be notified of this event, however, no further
	 * collision or contact events will occur for this pair.
	 * <p>
	 * The {@link #collision(Body, BodyFixture, Body, BodyFixture, Penetration)} method is next
	 * in the sequence of collision events.
	 * @param body1 the first {@link Body}
	 * @param fixture1 the first {@link Body}'s {@link BodyFixture}
	 * @param body2 the second {@link Body}
	 * @param fixture2 the second {@link Body}'s {@link BodyFixture}
	 * @return boolean true if processing should continue for this collision
	 * @since 3.2.0
	 */
	public abstract boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2);
	
	/**
	 * Called when two {@link BodyFixture}s are colliding as determined by the {@link NarrowphaseDetector}.
	 * <p>
	 * {@link Body} objects can have many {@link Convex} {@link Shape}s that make up their geometry.  Because
	 * of this, this method may be called multiple times if two multi-fixtured {@link Body}s are colliding.
	 * <p>
	 * Modification of the {@link Penetration} object is allowed.  The {@link Penetration} object passed 
	 * will be used to generate the contact manifold in the {@link ManifoldSolver}.
	 * <p>
	 * Return false from this method to stop processing of this collision.  Other 
	 * {@link CollisionListener}s will still be notified of this event, however, no further
	 * collision or contact events will occur for this pair.
	 * <p>
	 * The {@link #collision(Body, BodyFixture, Body, BodyFixture, Manifold)} method is next
	 * in the sequence of collision events.
	 * @param body1 the first {@link Body}
	 * @param fixture1 the first {@link Body}'s {@link BodyFixture}
	 * @param body2 the second {@link Body}
	 * @param fixture2 the second {@link Body}'s {@link BodyFixture}
	 * @param penetration the {@link Penetration} between the {@link Shape}s
	 * @return boolean true if processing should continue for this collision
	 */
	public abstract boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Penetration penetration);
	
	/**
	 * Called when two {@link BodyFixture}s are colliding and a contact {@link Manifold} has been found.
	 * <p>
	 * {@link Body} objects can have many {@link Convex} {@link Shape}s that make up their geometry.  Because
	 * of this, this method may be called multiple times if two multi-fixtured {@link Body}s are colliding.
	 * <p>
	 * Modification of the {@link Manifold} object is allowed.  The {@link Manifold} is used to create contact 
	 * constraints.
	 * <p>
	 * Return false from this method to stop processing of this collision.  Other 
	 * {@link CollisionListener}s will still be notified of this event, however, no further
	 * collision or contact events will occur for this pair.
	 * <p>
	 * The {@link #collision(ContactConstraint)} method is next in the sequence of collision events.
	 * @param body1 the first {@link Body}
	 * @param fixture1 the first {@link Body}'s {@link BodyFixture}
	 * @param body2 the second {@link Body}
	 * @param fixture2 the second {@link Body}'s {@link BodyFixture}
	 * @param manifold the contact {@link Manifold} for the collision
	 * @return boolean true if processing should continue for this collision
	 */
	public abstract boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Manifold manifold);
	
	/**
	 * Called after a {@link ContactConstraint} has been created for a collision.
	 * <p>
	 * {@link Body} objects can have many {@link Convex} {@link Shape}s that make up their geometry.  Because
	 * of this, this method may be called multiple times if two multi-fixtured {@link Body}s are colliding.
	 * <p>
	 * Modification of the friction and restitution (both computed using the {@link CoefficientMixer}
	 * and sensor fields is allowed.
	 * <p>
	 * Setting the tangent velocity of the {@link ContactConstraint} can create a conveyor effect.
	 * <p>
	 * Return false from this method to stop processing of this collision.  Other 
	 * {@link CollisionListener}s will still be notified of this event, however, no further
	 * collision or contact events will occur for this pair.
	 * <p>
	 * This is the last collision event before contact processing (via {@link ContactListener}s) occur.
	 * @param contactConstraint the contact constraint
	 * @return boolean true if processing should continue for this collision
	 * @since 3.0.2
	 */
	public abstract boolean collision(ContactConstraint contactConstraint);
}
