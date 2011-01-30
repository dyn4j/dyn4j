/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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
 * Sub package of the Collision package handling narrow-phase collision detection.
 * <p>
 * Narrow-phase collision detection is used to determine if two {@link org.dyn4j.game2d.geometry.Convex} 
 * {@link org.dyn4j.game2d.geometry.Shape}s penetrate, and if so, compute the vector of minimum magnitude 
 * able to push the {@link org.dyn4j.game2d.geometry.Convex} {@link org.dyn4j.game2d.geometry.Shape}s out 
 * of penetration.
 * <p>
 * The {@link org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector}s can only perform on 
 * {@link org.dyn4j.game2d.geometry.Convex} {@link org.dyn4j.game2d.geometry.Shape}s.  This allows for fast 
 * and simple algorithms.
 * <p>
 * Even though the {@link org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector}s are fast, performance 
 * can be improved substantially if a {@link org.dyn4j.game2d.collision.broadphase.BroadphaseDetector} is 
 * used to eliminate obvious non-penetrating pairs.
 * <p>
 * {@link org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector}s return 
 * {@link org.dyn4j.game2d.collision.narrowphase.Penetration} objects representing the vector of minimum 
 * magnitude able to push the {@link org.dyn4j.game2d.geometry.Convex} 
 * {@link org.dyn4j.game2d.geometry.Shape}s out of penetration.
 * <p>
 * There are two {@link org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector} implementations provided: 
 * {@link org.dyn4j.game2d.collision.narrowphase.Sat} and {@link org.dyn4j.game2d.collision.narrowphase.Gjk}.
 * <p>
 * The {@link org.dyn4j.game2d.collision.narrowphase.Gjk} 
 * {@link org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector} also has another method to return 
 * the {@link org.dyn4j.game2d.collision.narrowphase.Separation} between to 
 * {@link org.dyn4j.game2d.geometry.Convex} {@link org.dyn4j.game2d.geometry.Shape}s.  The 
 * {@link org.dyn4j.game2d.collision.narrowphase.Separation} represents the vector of minimum distance from 
 * one {@link org.dyn4j.game2d.geometry.Convex} {@link org.dyn4j.game2d.geometry.Shape} to the other.
 * @author William Bittle
 * @version 2.2.2
 * @since 1.0.0
 */
package org.dyn4j.game2d.collision.narrowphase;