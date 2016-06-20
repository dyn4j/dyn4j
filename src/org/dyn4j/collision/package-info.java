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

/**
 * Package containing supporting classes to detect collisions between 
 * {@link org.dyn4j.geometry.Convex} {@link org.dyn4j.geometry.Shape}s.
 * <p>
 * Collision detection can be an expensive process.  To avoid unnecessary processing
 * a two phase approach to collision detection is used.  First a inaccurate, yet
 * conservative, algorithm is used to detect possible collision pairs.  This process is
 * called the broad-phase.  Next, after obtaining all the possible collision pairs, each
 * pair is tested using an accurate algorithm.  This is called the narrow-phase.  After
 * the narrow-phase is complete, another phase, Manifold generation, generates the contact points.
 * <p>
 * <b>Broad-phase</b>
 * <p>
 * The {@link org.dyn4j.collision.broadphase.BroadphaseDetector}s will determine whether 
 * two {@link org.dyn4j.geometry.Convex} {@link org.dyn4j.geometry.Shape}s can possibly 
 * penetrate, returning only valid {@link org.dyn4j.collision.broadphase.BroadphasePair}s, which 
 * should then be sent to a {@link org.dyn4j.collision.narrowphase.NarrowphaseDetector}.
 * <p>
 * <b>Narrow-phase</b>
 * <p>
 * The {@link org.dyn4j.collision.narrowphase.NarrowphaseDetector}s will determine whether two 
 * {@link org.dyn4j.geometry.Convex} {@link org.dyn4j.geometry.Shape}s penetrate and 
 * return a {@link org.dyn4j.collision.narrowphase.Penetration}.
 * <p>
 * A {@link org.dyn4j.collision.narrowphase.Penetration} object represents the minimum distance 
 * along some vector required to push the {@link org.dyn4j.geometry.Shape}s out of the penetration.
 * <p>
 * The narrow-phase also contains classes for supporting multiple detectors at once via the
 * {@link org.dyn4j.collision.narrowphase.FallbackNarrowphaseDetector} class. Using the 
 * {@link org.dyn4j.collision.narrowphase.FallbackCondition}s the detector will determine whether to
 * use the primary {@link org.dyn4j.collision.narrowphase.NarrowphaseDetector} or the fallback.
 * <p>
 * <b>Manifold Generation</b>
 * <p>
 * Once a {@link org.dyn4j.collision.narrowphase.Penetration} object is obtained, it should be passed
 * to a {@link org.dyn4j.collision.manifold.ManifoldSolver}.  A 
 * {@link org.dyn4j.collision.manifold.ManifoldSolver} will use the 
 * {@link org.dyn4j.collision.narrowphase.Penetration} object and the given 
 * {@link org.dyn4j.geometry.Convex} {@link org.dyn4j.geometry.Shape}s to create a collision 
 * {@link org.dyn4j.collision.manifold.Manifold}.
 * <p>
 * A collision {@link org.dyn4j.collision.manifold.Manifold} represents the collision point(s) between 
 * the two {@link org.dyn4j.geometry.Convex} {@link org.dyn4j.geometry.Shape}s.
 * <p>
 * <b>CCD and Raycasting</b>
 * <p>
 * Outside of what's called discrete collision detection, there's continuous collision detection.
 * This is the process of detecting (would be) missed collisions in the discrete process since it's only
 * checking for collisions at instants in time.  These detectors implement the {@link org.dyn4j.collision.continuous.TimeOfImpactDetector}
 * interface and return a {@link org.dyn4j.collision.continuous.TimeOfImpact} object when a collision is
 * detected.
 * <p>
 * This package also supports raycasting for both the broad and narrow phases.  These detectors implement
 * the {@link org.dyn4j.collision.narrowphase.RaycastDetector} interface and return {@link org.dyn4j.collision.narrowphase.Raycast}
 * objects when a ray hits a {@link org.dyn4j.collision.Fixture}.
 * <p>
 * The following is a list of implementations of the above interfaces:
 * <ul>
 * <li>{@link org.dyn4j.collision.broadphase.BroadphaseDetector}s
 * 	<ul>
 *	<li>{@link org.dyn4j.collision.broadphase.Sap}</li>
 * 	<li>{@link org.dyn4j.collision.broadphase.DynamicAABBTree}</li>
 * 	</ul>
 * </li>
 * <li>{@link org.dyn4j.collision.narrowphase.NarrowphaseDetector}
 * 	<ul>
 * 	<li>{@link org.dyn4j.collision.narrowphase.Gjk}</li>
 * 	<li>{@link org.dyn4j.collision.narrowphase.Sat}</li>
 * 	</ul>
 * </li>
 * <li>{@link org.dyn4j.collision.manifold.ManifoldSolver}
 * 	<ul>
 * 	<li>{@link org.dyn4j.collision.manifold.ClippingManifoldSolver}</li>
 * 	</ul>
 * </li>
 * <li>{@link org.dyn4j.collision.continuous.TimeOfImpactDetector}
 * 	<ul>
 * 	<li>{@link org.dyn4j.collision.continuous.ConservativeAdvancement}</li>
 * 	</ul>
 * </li>
 * <li>{@link org.dyn4j.collision.narrowphase.RaycastDetector}
 * 	<ul>
 * 	<li>{@link org.dyn4j.collision.narrowphase.Gjk}</li>
 * 	</ul>
 * </li>
 * </ul>
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
package org.dyn4j.collision;