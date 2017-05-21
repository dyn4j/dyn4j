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
 * Sub package of the Collision package handling broad-phase collision detection.
 * <p>
 * Broad-phase collision detection is the process of detecting collision between all the {@link org.dyn4j.collision.Collidable}
 * {@link org.dyn4j.collision.Fixture}s. The broad-phase attempts to reduce the O(n<sup>2</sup>) complexity of
 * this process by using specialized data structures.  The broad-phase is not exact, but instead finds pairs of
 * {@link org.dyn4j.collision.Fixture}s that could be colliding.  While not exact, the broad-phase is conservative.
 * In other words, the broad-phase will never miss collisions, but will detect false positives.
 * <p>
 * There are two broad-phase implementations, {@link org.dyn4j.collision.broadphase.Sap} and 
 * {@link org.dyn4j.collision.broadphase.DynamicAABBTree}, both with their own merits. Generally, they have similar
 * performance.
 * <p>
 * This package also contains an interface for filtering the results of the {@link org.dyn4j.collision.broadphase.BroadphaseDetector}s
 * methods: {@link org.dyn4j.collision.broadphase.BroadphaseFilter}s.  These can be useful in both logic and performance to pre-filter
 * results before performing more expensive logic.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
package org.dyn4j.collision.broadphase;