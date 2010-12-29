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
package org.dyn4j.game2d.collision.narrowphase;

import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Ray;
import org.dyn4j.game2d.geometry.Shape;
import org.dyn4j.game2d.geometry.Transform;

/**
 * Represents an algorithm for determining whether a {@link Ray} and a {@link Convex}
 * {@link Shape} intersect, given the ray's maximum length and the convex shape's
 * transform.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public interface RaycastDetector {
	/**
	 * Performs a ray cast given a {@link Ray} and a {@link Convex} {@link Shape} returning
	 * true if the ray passes through the convex shape.
	 * <p>
	 * The raycast parameter is used to stored the results of the raycast upon a successful
	 * or true return.
	 * @param ray the {@link Ray}
	 * @param maxLength the maximum length of the ray; 0 for infinite length
	 * @param convex the {@link Convex} {@link Shape}
	 * @param t the {@link Convex} {@link Shape}'s {@link Transform}
	 * @param raycast the ray cast result
	 * @return boolean true if the {@link Ray} passes through the {@link Convex} {@link Shape}
	 */
	public abstract boolean raycast(Ray ray, double maxLength, Convex convex, Transform t, Raycast raycast);
}
