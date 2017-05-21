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
package org.dyn4j.collision.manifold;

import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;

/**
 * Finds a contact {@link Manifold} for two given {@link Convex} {@link Shape}s that are in collision.
 * <p>
 * A contact {@link Manifold} is a collection of contact points for a collision. For two dimensions, this will never
 * be more than two contacts.
 * <p>
 * A {@link ManifoldSolver} relies on the {@link Penetration} object returned from a {@link NarrowphaseDetector} to
 * determine the contact {@link Manifold}. The {@link Manifold}s have ids to facilitate caching of contact information.
 * <p>
 * It's possible that no contact points are returned, in which case the {@link #getManifold(Penetration, Convex, Transform, Convex, Transform, Manifold)}
 * method will return false.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 * @see Manifold
 */
public interface ManifoldSolver {
	/**
	 * Returns true if there exists a valid contact manifold between the two {@link Convex} {@link Shape}s. 
	 * <p>
	 * When returning true, this method fills in the {@link Manifold} object with the points, depth, and normal.
	 * <p>
	 * The given {@link Manifold} object will be cleared using the {@link Manifold#clear()} method. This allows reuse of the
	 * {@link Manifold} if desired.
	 * <p>
	 * The {@link Penetration} object will be left unchanged by this method.
	 * @param penetration the {@link Penetration}
	 * @param convex1 the first {@link Convex} {@link Shape}
	 * @param transform1 the first {@link Shape}'s {@link Transform}
	 * @param convex2 the second {@link Convex} {@link Shape}
	 * @param transform2 the second {@link Shape}'s {@link Transform}
	 * @param manifold the {@link Manifold} object to fill
	 * @return boolean
	 */
	public abstract boolean getManifold(Penetration penetration, Convex convex1, Transform transform1, Convex convex2, Transform transform2, Manifold manifold);
}
