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
package org.dyn4j.collision.narrowphase;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;

/**
 * Represents an algorithm to detect whether two {@link Convex} {@link Shape}s are 
 * penetrating/overlapping each other.
 * <p>
 * If the {@link Convex} {@link Shape}s are penetrating/overlapping one another, the 
 * algorithm must determine the penetration axis of minimum depth and the depth of 
 * the penetration. 
 * <p>
 * All {@link NarrowphaseDetector}s are only required to perform tests on {@link Convex}
 * {@link Shape}s.  Doing so allows for faster and simpler code.  Concave objects can also
 * be supported by performing a convex decomposition and testing the convex pieces individually.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public interface NarrowphaseDetector {
	/**
	 * Returns true if the two {@link Convex} {@link Shape}s intersect and fills
	 * the {@link Penetration} object with the penetration vector and depth.
	 * @param convex1 the first {@link Convex} {@link Shape}
	 * @param transform1 the first {@link Shape}'s {@link Transform}
	 * @param convex2 the second {@link Convex} {@link Shape}
	 * @param transform2 the second {@link Shape}'s {@link Transform}
	 * @param penetration the {@link Penetration} object to fill
	 * @return boolean
	 */
	public abstract boolean detect(Convex convex1, Transform transform1, Convex convex2, Transform transform2, Penetration penetration);
	
	/**
	 * Returns true if the two {@link Convex} {@link Shape}s intersect.
	 * <p>
	 * Used to quickly test if two {@link Convex} {@link Shape}s intersect without
	 * incurring the cost of determining {@link Penetration}.
	 * @param convex1 the first {@link Convex} {@link Shape}
	 * @param transform1 the first {@link Shape}'s {@link Transform}
	 * @param convex2 the second {@link Convex} {@link Shape}
	 * @param transform2 the second {@link Shape}'s {@link Transform}
	 * @return boolean
	 */
	public abstract boolean detect(Convex convex1, Transform transform1, Convex convex2, Transform transform2);
}
