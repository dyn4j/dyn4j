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
 * This package contains algorithms used to create convex hulls of point clouds.
 * <p>
 * {@link org.dyn4j.geometry.hull.HullGenerator} four different implementations:
 * <ul>
 * <li>{@link org.dyn4j.geometry.hull.GiftWrap}</li>
 * <li>{@link org.dyn4j.geometry.hull.GrahamScan}</li>
 * <li>{@link org.dyn4j.geometry.hull.MonotoneChain}</li>
 * <li>{@link org.dyn4j.geometry.hull.DivideAndConquer}</li>
 * </ul>
 * The hulls are returned as {@link org.dyn4j.geometry.Vector2}[]s that can then be used to create a 
 * {@link org.dyn4j.geometry.Convex} {@link org.dyn4j.geometry.Shape}.
 * <p>
 * Algorithms implementing this interface are intended for general use but does not imply that
 * the results produced would be appropriate for the core dyn4j engine.
 * <p>
 * All algorithms should produce a valid convex hull that encapsulates all the given points, but there's
 * no guarantee that the algorithms produce identical results.
 * @author William Bittle 
 * @version 3.3.1
 * @since 2.2.0
 */
package org.dyn4j.geometry.hull;
