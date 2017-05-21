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
 * Sub package of the Collision package handling contact manifold generation.
 * <p>
 * Once a {@link org.dyn4j.collision.narrowphase.Penetration} object has been found between two 
 * {@link org.dyn4j.geometry.Convex} {@link org.dyn4j.geometry.Shape}s using a 
 * {@link org.dyn4j.collision.narrowphase.NarrowphaseDetector}, the next step is to find where the 
 * collision occurred by using a {@link org.dyn4j.collision.manifold.ManifoldSolver}.
 * <p>
 * A {@link org.dyn4j.collision.manifold.ManifoldSolver} is an algorithm that generates a contact
 * {@link org.dyn4j.collision.manifold.Manifold} for a collision.
 * <p>
 * A contact {@link org.dyn4j.collision.manifold.Manifold} is an object representing the surface 
 * or point where the two {@link org.dyn4j.geometry.Convex} {@link org.dyn4j.geometry.Shape}s are 
 * in contact in a collision.
 * <p>
 * {@link org.dyn4j.collision.manifold.Manifold}s are currently one or two points and represent either
 * a single point or a straight edge respectively. Curved edges are not represented and instead use the single
 * point representation. In two dimensions the {@link org.dyn4j.collision.manifold.Manifold} will always
 * have two or less contact points.
 * <p>
 * A {@link org.dyn4j.collision.manifold.Manifold} is made up of one or more {@link org.dyn4j.collision.manifold.ManifoldPoint}s
 * which describe the information for each contact point in the collision. The {@link org.dyn4j.collision.manifold.ManifoldPoint}s
 * have a {@link org.dyn4j.collision.manifold.ManifoldPointId} associated with them to facilitate caching.
 * <p>
 * Only one implementation of the {@link org.dyn4j.collision.manifold.ManifoldSolver} is provided: 
 * {@link org.dyn4j.collision.manifold.ClippingManifoldSolver}.
 * @author William Bittle
 * @version 2.2.2
 * @since 1.0.0
 */
package org.dyn4j.collision.manifold;