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
package org.dyn4j.geometry.simplify;

import java.util.List;

import org.dyn4j.geometry.Vector2;

/**
 * Represents a simple polygon (without holes) simplification algorithm.
 * <p>
 * Polygon simplification is the process of reducing the number of vertices in a
 * source polygon without too much loss in fidelity.  The goal is to keep significant
 * features and simplify insignificant features.  For example, a polygon with two
 * adjacent edges that are colinear provide no value visually and can have a 
 * negative effect when used as is.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.2.0
 */
public interface Simplifier {
	/**
	 * Simplifies the given simple polygon and returns a new
	 * simplified simple polygon.
	 * @param vertices the simple polygon's vertices
	 * @return List&lt;{@link Vector2}&gt;
	 */
	public List<Vector2> simplify(List<Vector2> vertices);
	
	/**
	 * Simplifies the given simple polygon and returns a new
	 * simplified simple polygon.
	 * @param vertices the simple polygon's vertices
	 * @return {@link Vector2}[]
	 */
	public Vector2[] simplify(Vector2... vertices);
}
