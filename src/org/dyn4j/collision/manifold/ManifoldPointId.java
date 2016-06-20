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
package org.dyn4j.collision.manifold;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Feature;
import org.dyn4j.geometry.PointFeature;
import org.dyn4j.geometry.Shape;

/**
 * Represents the identification of a specific contact point of a {@link Manifold}.
 * <p>
 * The id is relative to the {@link Convex} {@link Shape}s in a particular collision.
 * <p>
 * For {@link Convex} {@link Shape}s that return {@link PointFeature} {@link Feature}s the id
 * will always be {@link #DISTANCE}. The {@link #DISTANCE} id relays to any caching mechanism
 * that a distance check should be done rather than an id comparison to determine whether
 * to use a cached value or not. 
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 * @see IndexedManifoldPointId
 */
public interface ManifoldPointId {
	/**
	 * The default {@link ManifoldPointId}.  Flags that the points should be
	 * tested by performing a distance check.
	 */
	public static final ManifoldPointId DISTANCE = new ManifoldPointId() {
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "DistanceManifoldPointId[]";
		}
	};
}
