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
package org.dyn4j.geometry.hull;

import java.util.List;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Vector2;

/**
 * Represents an algorithm used to create a convex hull of a given point set.
 * <p>
 * The hulls are returned as {@link Vector2}[]s that can then be used to create a {@link Convex} {@link Shape}.
 * <p>
 * Algorithms implementing this interface are intended for general use but does not imply that
 * the results produced would be appropriate for the core dyn4j engine.
 * @author William Bittle
 * @version 4.2.0
 * @since 2.2.0
 */
public interface HullGenerator {
	/**
	 * Returns a convex hull generated from the given point set in counter-clockwise point order.
	 * <p>
	 * Returns null if the given points array is null.
	 * <p>
	 * Returns the array unchanged if the length is less than or equal to 2.
	 * @param points the point set or cloud
	 * @return {@link Vector2}[] the convex hull vertices
	 * @throws NullPointerException if points is null or contains null points
	 */
	public abstract Vector2[] generate(Vector2... points);
	
	/**
	 * Returns a convex hull generated from the given point set in counter-clockwise point order.
	 * <p>
	 * Returns null if the given points array is null.
	 * <p>
	 * Returns the array unchanged if the length is less than or equal to 2.
	 * @param points the point set or cloud
	 * @return List&lt;{@link Vector2}&gt; the convex hull vertices
	 * @throws NullPointerException if points is null or contains null points
	 * @since 4.2.0
	 */
	public abstract List<Vector2> generate(List<Vector2> points);
}
