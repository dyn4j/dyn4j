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
 * This package contains algorithms to decompose polygons into 
 * {@link org.dyn4j.game2d.geometry.Convex} pieces.
 * <p>
 * Three implementations of the {@link org.dyn4j.game2d.geometry.decompose.Decomposer} 
 * interface are provided: {@link org.dyn4j.game2d.geometry.decompose.Bayazit}, 
 * {@link org.dyn4j.game2d.geometry.decompose.EarClipping}, and 
 * {@link org.dyn4j.game2d.geometry.decompose.SweepLine}.
 * <p>
 * The {@link org.dyn4j.game2d.geometry.decompose.Bayazit} algorithm is O(nr) and finds a non-optimal 
 * convex decomposition.
 * <p>
 * The {@link org.dyn4j.game2d.geometry.decompose.EarClipping} algorithm is O(n<sup>2</sup>) and finds 
 * a valid triangulation, then uses the Hertel-Mehlhorn algorithm to combine triangles into convex pieces.  
 * This is also an non-optimal decomposition.
 * <p>
 * The {@link org.dyn4j.game2d.geometry.decompose.SweepLine} algorithm is O(n log n) and, like 
 * {@link org.dyn4j.game2d.geometry.decompose.EarClipping}, finds a valid triangulation,
 * then uses the Hertel-Mehlhorn algorithm to combine triangles into convex pieces.  This is also a
 * non-optimal decomposition.
 * <p>
 * In general the algorithms will generate different decompositions.  If used for pre-processing just
 * choose the best result, for runtime generation, the {@link org.dyn4j.game2d.geometry.decompose.Bayazit} 
 * may be slower but will generally produce a better decomposition.
 * <p>
 * A "better" decomposition is one that contains fewer convex pieces and the convex pieces that are created
 * are of better quality for simulation.
 * @author William Bittle 
 * @version 2.2.2
 * @since 2.2.0
 */
package org.dyn4j.game2d.geometry.decompose;