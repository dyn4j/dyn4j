/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * This package contains algorithms to simplify <a href="https://en.wikipedia.org/wiki/Simple_polygon">simple polygons</a> 
 * without holes with the intent for better performance and stability within dyn4j.
 * <p>
 * Three implementations of the {@link org.dyn4j.geometry.simplify.Simplifier} interface are provided: 
 * {@link org.dyn4j.geometry.simplify.VertexClusterReduction}, 
 * {@link org.dyn4j.geometry.simplify.Visvalingam}, and 
 * {@link org.dyn4j.geometry.simplify.DouglasPeucker}.
 * <p>
 * All three {@link org.dyn4j.geometry.simplify.Simplifier} require tuning parameters to set the level
 * of allowed simplification.
 * <p>
 * <strong>NOTE: All {@link org.dyn4j.geometry.simplify.Simplifier} algorithms operate on simple
 * polygons <u>without holes</u></strong> and assume the input already confirms to this constraint.
 * <p>
 * All the algorithms in this package have O(n log n) complexity and are intended for use real-time, but
 * selection of the tuning parameters will be the challenge.  Generally, they should be chosen based on the
 * input polygon to simplify.  For example, imagine we have two polygons we want to simplify, A and B. 
 * Polygon A's vertices hover around the 1.0-2.0 units while Polygon B's vertices hover around the 100-200
 * units.  These might be the same polygon, but scaled, but would require different tuning parameters.  The
 * recommendation is to first scale the polygon down to a known range, then apply the simplification.
 * <p>
 * The {@link org.dyn4j.geometry.simplify.VertexClusterReduction} algorithm is primarily for pre-processing
 * but can be used by itself.  It's used, by default, in both the {@link org.dyn4j.geometry.simplify.Visvalingam}
 * and {@link org.dyn4j.geometry.simplify.DouglasPeucker} classes.
 * <p>
 * Each algorithm has a different method for simplifying the input polygon and will therefore produce
 * different results.  It's also not guaranteed that the algorithms will produce a valid output in the case
 * of poorly chosen tuning parameters - always check the output before sending to the next step (which is
 * typically a convex decomposition process).
 * <p>
 * If the input is null, null is returned.  If the input contains null elements, they are removed before
 * simplification.  If the input is less than 4 vertices, no simplification is done and the input is returned
 * with exception of {@link org.dyn4j.geometry.simplify.VertexClusterReduction}. If the tuning parameters, as
 * mentioned above, are not appropriate, the output can contain 0, 1, or 2 vertices - i.e. not a valid
 * simple polygon.
 * @author William Bittle 
 * @version 4.2.0
 * @since 4.2.0
 */
package org.dyn4j.geometry.simplify;
