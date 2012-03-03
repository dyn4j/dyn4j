/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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
 * Sub package of the Collision package handling broad-phase collision detection.
 * <p>
 * Currently there are three broadphase implementations:
 * {@link org.dyn4j.collision.broadphase.SapIncremental},
 * {@link org.dyn4j.collision.broadphase.SapBruteForce}, and
 * {@link org.dyn4j.collision.broadphase.DynamicAABBTree}.
 * <p>
 * A {@link org.dyn4j.collision.broadphase.BroadphaseDetector} should accept a list of 
 * {@link org.dyn4j.collision.Collidable}s and return those pairs who may be penetrating in a
 * list of {@link org.dyn4j.collision.broadphase.BroadphasePair}s.
 * <p>
 * {@link org.dyn4j.collision.broadphase.BroadphaseDetector}s are not expected to be accurate, 
 * but are expected to be conservative.  Meaning, its acceptable for a 
 * {@link org.dyn4j.collision.broadphase.BroadphaseDetector} to return false positives.
 * <p>
 * Typical applications will want to use the {@link org.dyn4j.collision.broadphase.DynamicAABBTree}
 * class since it has double the performance than the other two in raycasting at minimal performance
 * decrease in general collision detection.  If raycasting is not a large portion of the application, then
 * use of {@link org.dyn4j.collision.broadphase.SapIncremental} is advised because of its performance
 * benefits when objects are moving either rarely or not at all.  Finally, if constant performance is required
 * use {@link org.dyn4j.collision.broadphase.SapBruteForce}.
 * @author William Bittle
 * @version 3.0.0
 * @since 1.0.0
 */
package org.dyn4j.collision.broadphase;