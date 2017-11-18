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
 * Sub package of the Collision package handling narrow-phase collision detection, distance checking,
 * and raycasting.
 * <p>
 * Narrow-phase collision detection is used to determine if two {@link org.dyn4j.geometry.Convex} 
 * {@link org.dyn4j.geometry.Shape}s penetrate, and if so, compute the vector of minimum magnitude 
 * able to push the {@link org.dyn4j.geometry.Convex} {@link org.dyn4j.geometry.Shape}s out 
 * of penetration.
 * <p>
 * The {@link org.dyn4j.collision.narrowphase.NarrowphaseDetector}s can only perform on 
 * {@link org.dyn4j.geometry.Convex} {@link org.dyn4j.geometry.Shape}s.  This allows for fast 
 * and simple algorithms.  Non-convex shapes can be decomposed using a {@link org.dyn4j.geometry.decompose.Decomposer}
 * into {@link org.dyn4j.geometry.Convex} pieces which can then be tested individually.
 * <p>
 * Even though the {@link org.dyn4j.collision.narrowphase.NarrowphaseDetector}s are fast, performance 
 * can be improved substantially if a {@link org.dyn4j.collision.broadphase.BroadphaseDetector} is 
 * used to eliminate obvious non-penetrating pairs.
 * <p>
 * {@link org.dyn4j.collision.narrowphase.NarrowphaseDetector}s return 
 * {@link org.dyn4j.collision.narrowphase.Penetration} objects representing the vector of minimum 
 * magnitude able to push the {@link org.dyn4j.geometry.Convex} {@link org.dyn4j.geometry.Shape}s out 
 * of penetration.  This information is typically passed onto a {@link org.dyn4j.collision.manifold.ManifoldSolver}
 * to find the collision points.
 * <p>
 * There are two {@link org.dyn4j.collision.narrowphase.NarrowphaseDetector} implementations provided: 
 * {@link org.dyn4j.collision.narrowphase.Sat} and {@link org.dyn4j.collision.narrowphase.Gjk}.
 * <p>
 * <strong>NOTE: The {@link org.dyn4j.collision.narrowphase.Sat} algorithm doesn't
 * support the {@link org.dyn4j.geometry.Ellipse} and {@link org.dyn4j.geometry.HalfEllipse} shapes.</strong>
 * <p>
 * This package also contains a {@link org.dyn4j.collision.narrowphase.FallbackNarrowphaseDetector} class.  This class is
 * used to build a hierarchy of {@link org.dyn4j.collision.narrowphase.NarrowphaseDetector}s that can select detection
 * algorithms based on arbitrary conditions.  For example, in the case of {@link org.dyn4j.geometry.Ellipse} and
 * {@link org.dyn4j.geometry.HalfEllipse}, {@link org.dyn4j.collision.narrowphase.Gjk} could be the fallback detector while 
 * {@link org.dyn4j.collision.narrowphase.Sat} is the primary. Fallback is determined by the assigned 
 * {@link org.dyn4j.collision.narrowphase.FallbackCondition}s.
 * <p>
 * <strong>NOTE: The {@link org.dyn4j.collision.narrowphase.CircleDetector} and {@link org.dyn4j.collision.narrowphase.SegmentDetector}
 * classes already being used in the {@link org.dyn4j.collision.narrowphase.Sat} and {@link org.dyn4j.collision.narrowphase.Gjk}
 * classes so no {@link org.dyn4j.collision.narrowphase.FallbackNarrowphaseDetector} is necessary for these.</strong>
 * <p>
 * The {@link org.dyn4j.collision.narrowphase.Gjk} algorithm also implements the {@link org.dyn4j.collision.narrowphase.DistanceDetector} 
 * and {@link org.dyn4j.collision.narrowphase.RaycastDetector} interfaces.  These interfaces allow performing distance checks and raycasts 
 * against {@link org.dyn4j.geometry.Convex} {@link org.dyn4j.geometry.Shape}s.
 * <p>
 * The {@link org.dyn4j.collision.narrowphase.DistanceDetector}s can return a {@link org.dyn4j.collision.narrowphase.Separation}
 * object which contains the separation normal, distance and closest points.
 * <p>
 * The {@link org.dyn4j.collision.narrowphase.RaycastDetector}s can return a {@link org.dyn4j.collision.narrowphase.Raycast}
 * object which contains the point the {@link org.dyn4j.geometry.Ray} intersects the shape, the normal and the
 * distance.  For raycasting, it's also advisable to perform a raycast against a {@link org.dyn4j.collision.broadphase.BroadphaseDetector}
 * first to rule out the obvious failures.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
package org.dyn4j.collision.narrowphase;