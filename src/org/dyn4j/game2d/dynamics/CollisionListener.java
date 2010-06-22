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
package org.dyn4j.game2d.dynamics;

import org.dyn4j.game2d.collision.broadphase.BroadphaseDetector;
import org.dyn4j.game2d.collision.manifold.Manifold;
import org.dyn4j.game2d.collision.manifold.ManifoldSolver;
import org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.game2d.collision.narrowphase.Penetration;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Shape;

/**
 * Interface to listen for collision events.
 * @author William Bittle
 */
public interface CollisionListener {
	/**
	 * Called when two {@link Body}s are colliding as determined by the {@link BroadphaseDetector}.
	 * @param body1 the first {@link Body}
	 * @param body2 the second {@link Body}
	 * @return boolean true if processing should continue for this collision
	 */
	public abstract boolean collision(Body body1, Body body2);
	
	/**
	 * Called when two {@link Body}s are colliding as determined by the {@link NarrowphaseDetector}.
	 * <p>
	 * {@link Body} objects can have many {@link Convex} {@link Shape}s that make up their geometry.  Because
	 * of this this method may be called multiple times.
	 * <p>
	 * Modification of the {@link Penetration} object is allowed and will be used to generate the contact
	 * manifold in the {@link ManifoldSolver}.
	 * @param body1 the first {@link Body}
	 * @param fixture1 the first {@link Body}'s {@link Fixture}
	 * @param body2 the second {@link Body}
	 * @param fixture2 the second {@link Body}'s {@link Fixture}
	 * @param penetration the {@link Penetration} between the {@link Shape}s
	 * @return boolean true if processing should continue for this collision
	 */
	public abstract boolean collision(Body body1, Fixture fixture1, Body body2, Fixture fixture2, Penetration penetration);
	
	/**
	 * Called when two {@link Body}s are colliding and a contact {@link Manifold} has been found.
	 * <p>
	 * {@link Body} objects can have many {@link Convex} {@link Shape}s that make up their geometry.  Because
	 * of this this method may be called multiple times.
	 * <p>
	 * Modification of the {@link Manifold} object is allowed.  The {@link Manifold} is used to create contact constraints.
	 * @param body1 the first {@link Body}
	 * @param fixture1 the first {@link Body}'s {@link Fixture}
	 * @param body2 the second {@link Body}
	 * @param fixture2 the second {@link Body}'s {@link Fixture}
	 * @param manifold the contact {@link Manifold} for the collision
	 * @return boolean true if processing should continue for this collision
	 */
	public abstract boolean collision(Body body1, Fixture fixture1, Body body2, Fixture fixture2, Manifold manifold);
}
