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

/**
 * Package containing classes to detect collisions between {@link Convex} {@link Shape}s.
 * <p>
 * Collision detection can be an expensive process.  To avoid unnecessary processing
 * a two phase approach to collision detection is used.  First a inaccurate, yet
 * conservative algorithm is used to detect possible collision pairs.  This process is
 * called the broad-phase.  Next, after obtaining all the possible collision pairs, each
 * pair is tested using an accurate algorithm.  This is called the narrow-phase.
 * <p>
 * <b>Broad-phase</b>
 * <p>
 * The {@link BroadphaseDetector}s will determine whether two {@link Convex} {@link Shape}s
 * can possibly penetrate, returning only valid {@link BroadphasePair}s, which should then 
 * be sent to a {@link NarrowphaseDetector}.
 * <p>
 * <b>Narrow-phase</b>
 * <p>
 * The {@link NarrowphaseDetector}s will determine whether two {@link Convex} {@link Shape}s 
 * penetrate and returning the {@link Penetration}.
 * <p>
 * A {@link Penetration} object represents the minimum distance along some vector required 
 * to push the {@link Shape}s out of the penetration.
 * <p>
 * <b>Manifold Generation</b>
 * <p>
 * Once a {@link Penetration} object is obtained, it should be sent off to a 
 * {@link ManifoldSolver}.  A {@link ManifoldSolver} will use the {@link Penetration} object
 * and the given {@link Convex} {@link Shape}s to create a collision {@link Manifold}.
 * <p>
 * A collision {@link Manifold} represents the collision points between the two {@link Convex}
 * {@link Shape}s.
 * <p>
 * The following is a list of implementations of the above interfaces:
 * {@link BroadphaseDetector}: {@link Sap}<br />
 * {@link NarrowphaseDetector}: {@link Gjk} and {@link Sat}<br />
 * {@link ManifoldSolver}: {@link ClippingManifoldSolver}
 */
package org.dyn4j.game2d.collision;

import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Shape;
import org.dyn4j.game2d.collision.broadphase.BroadphaseDetector;
import org.dyn4j.game2d.collision.broadphase.BroadphasePair;
import org.dyn4j.game2d.collision.broadphase.Sap;
import org.dyn4j.game2d.collision.narrowphase.Gjk;
import org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.game2d.collision.narrowphase.Sat;
import org.dyn4j.game2d.collision.narrowphase.Penetration;
import org.dyn4j.game2d.collision.manifold.ManifoldSolver;
import org.dyn4j.game2d.collision.manifold.Manifold;
import org.dyn4j.game2d.collision.manifold.ClippingManifoldSolver;;
