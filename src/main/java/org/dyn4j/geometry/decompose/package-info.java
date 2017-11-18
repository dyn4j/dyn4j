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
 * This package contains algorithms to decompose polygons into {@link org.dyn4j.geometry.Convex} pieces.
 * <p>
 * Three implementations of the {@link org.dyn4j.geometry.decompose.Decomposer} 
 * interface are provided: {@link org.dyn4j.geometry.decompose.Bayazit}, 
 * {@link org.dyn4j.geometry.decompose.EarClipping}, and 
 * {@link org.dyn4j.geometry.decompose.SweepLine}.
 * <p>
 * All three {@link org.dyn4j.geometry.decompose.Decomposer} algorithms are non-optimal, meaning they do
 * not always produce a decomposition with the minimum number of convex pieces.  Instead, they get close
 * in exchange for performance.
 * <p>
 * <strong>NOTE: All three {@link org.dyn4j.geometry.decompose.Decomposer} algorithms operate on simple
 * polygons <u>without holes</u>.</strong>
 * <p>
 * The {@link org.dyn4j.geometry.decompose.EarClipping} and {@link org.dyn4j.geometry.decompose.SweepLine}
 * algorithms both triangulate the simple polygon first, then run Hertel-Mehlhorn to recombine triangles
 * into convex pieces.  Hertel-Mehlhorn is guarenteed to produce a convex decomposition with no more than
 * 4 times the minimum number of convex pieces.
 * <p>
 * The {@link org.dyn4j.geometry.decompose.EarClipping} and {@link org.dyn4j.geometry.decompose.SweepLine}
 * algorithms also implement the {@link org.dyn4j.geometry.decompose.Triangulator} interface to provide
 * the ability to get a triangulation as well.
 * <p>
 * The {@link org.dyn4j.geometry.decompose.Bayazit} algorithm has O(nr) time-complexity.
 * <p>
 * The {@link org.dyn4j.geometry.decompose.EarClipping} algorithm has O(n<sup>2</sup>) time-complexity.  
 * <p>
 * The {@link org.dyn4j.geometry.decompose.SweepLine} algorithm has O(n log n) time-complexity.
 * <p>
 * In general the algorithms will generate different decompositions.  If used for pre-processing just
 * choose the best result, for runtime generation, the {@link org.dyn4j.geometry.decompose.Bayazit} 
 * may be slower but will generally produce a better decomposition.
 * <p>
 * A "better" decomposition is one that contains fewer convex pieces and the convex pieces that are created
 * are of better quality for simulation.
 * @author William Bittle 
 * @version 3.2.0
 * @since 2.2.0
 */
package org.dyn4j.geometry.decompose;