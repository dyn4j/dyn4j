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
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;
import org.dyn4j.world.BroadphaseCollisionData;
import org.dyn4j.world.ManifoldCollisionData;
import org.dyn4j.world.NarrowphaseCollisionData;

/**
 * Interface to listen for collision events.
 * <p>
 * Events for a pair of bodies (as long as they pass the criteria for the event to be called)
 * will be called in the following order:
 * <ol>
 * <li>Collision detected by the broadphase: {@link #collision(BroadphaseCollisionData)}</li>
 * <li>Collision detected by the narrowphase: {@link #collision(NarrowphaseCollisionData)}</li>
 * <li>Contact manifold created by the manifold solver:{@link #collision(ManifoldCollisionData)}</li>
 * </ol>
 * Returning false from any of the listener methods will halt processing of that event.  Other
 * {@link CollisionListener}s will still be notified of that event, but subsequent events will
 * not occur (this indicates that you didn't want the collision to be resolved later).
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public interface CollisionListener<T extends CollisionBody<E>, E extends Fixture> extends WorldEventListener {
	/**
	 * Called when two {@link Fixture}s are colliding as determined by the {@link BroadphaseDetector}.
	 * <p>
	 * {@link CollisionBody} objects can have many {@link Convex} {@link Shape}s that make up their geometry.  Because
	 * of this, this method may be called multiple times if two multi-fixtured {@link CollisionBody}s are colliding.
	 * <p>
	 * This method is called when the two {@link Fixture}'s expanded {@link AABB}s are overlapping.
	 * Visually the bodies may not appear to be colliding (which is a valid case).  If you need to 
	 * make sure the {@link CollisionBody}s are colliding, and not just their AABBs, use the 
	 * {@link #collision(NarrowphaseCollisionData)} method.
	 * <p>
	 * Return false from this method to stop processing of this collision.  Other 
	 * {@link CollisionListener}s will still be notified of this event, however, no further
	 * collision or contact events will occur for this pair.
	 * <p>
	 * The {@link #collision(NarrowphaseCollisionData)} method is next in the sequence of collision events.
	 * @param collision the broadphase collision data
	 * @return boolean true if processing should continue for this collision
	 * @since 3.2.0
	 */
	public abstract boolean collision(BroadphaseCollisionData<T, E> collision);
	
	/**
	 * Called when two {@link Fixture}s are colliding as determined by the {@link NarrowphaseDetector}.
	 * <p>
	 * {@link CollisionBody} objects can have many {@link Convex} {@link Shape}s that make up their geometry.  Because
	 * of this, this method may be called multiple times if two multi-fixtured {@link CollisionBody}s are colliding.
	 * <p>
	 * Return false from this method to stop processing of this collision.  Other 
	 * {@link CollisionListener}s will still be notified of this event, however, no further
	 * collision or contact events will occur for this pair.
	 * <p>
	 * The {@link #collision(ManifoldCollisionData)} method is next
	 * in the sequence of collision events.
	 * @param collision the narrowphase collision data
	 * @return boolean true if processing should continue for this collision
	 */
	public abstract boolean collision(NarrowphaseCollisionData<T, E> collision);
	
	/**
	 * Called when two {@link Fixture}s are colliding and a contact {@link Manifold} has been found.
	 * <p>
	 * {@link CollisionBody} objects can have many {@link Convex} {@link Shape}s that make up their geometry.  Because
	 * of this, this method may be called multiple times if two multi-fixtured {@link CollisionBody}s are colliding.
	 * <p>
	 * Modification of the {@link Manifold} object is allowed.  The {@link Manifold} is used to create contact 
	 * constraints.
	 * <p>
	 * Return false from this method to stop processing of this collision.  Other 
	 * {@link CollisionListener}s will still be notified of this event, however, no further
	 * collision or contact events will occur for this pair.
	 * @param collision the manifold collision data
	 * @return boolean true if processing should continue for this collision
	 */
	public abstract boolean collision(ManifoldCollisionData<T, E> collision);
}
