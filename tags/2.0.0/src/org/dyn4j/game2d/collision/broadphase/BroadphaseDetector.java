/*
 * Copyright (c) 2010, William Bittle
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
package org.dyn4j.game2d.collision.broadphase;

import java.util.List;

import org.dyn4j.game2d.collision.Collidable;
import org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Shape;
import org.dyn4j.game2d.geometry.Transform;

/**
 * Represents a broad-phase collision detection algorithm.
 * <p>
 * A {@link BroadphaseDetector} should quickly determine the pairs of {@link Collidable}s that 
 * possibly intersect.  These algorithms are used to filter out collision pairs in the interest 
 * of sending less pairs to the {@link NarrowphaseDetector}.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public interface BroadphaseDetector {
	/**
	 * Tests the given list of {@link Collidable}s for collisions and returns a list
	 * containing all the possible collision pairs.
	 * @param <E> the {@link Collidable} type
	 * @param collidables list of {@link Collidable}s
	 * @return List&lt;{@link BroadphasePair}&gt;
	 */
	public abstract <E extends Collidable> List<BroadphasePair<E>> detect(List<E> collidables);
	
	/**
	 * Performs a broadphase collision test on the given {@link Collidable}s and
	 * returns true if they could possibly intersect.
	 * @param collidable1 the first {@link Collidable}
	 * @param collidable2 the second {@link Collidable}
	 * @return boolean
	 */
	public abstract boolean detect(Collidable collidable1, Collidable collidable2);

	/**
	 * Performs a broadphase collision test on the given {@link Convex} {@link Shape}s and
	 * returns true if they could possibly intersect.
	 * @param convex1 the first {@link Convex} {@link Shape}
	 * @param transform1 the first {@link Convex} {@link Shape}'s {@link Transform}
	 * @param convex2 the second {@link Convex} {@link Shape}
	 * @param transform2 the second {@link Convex} {@link Shape}'s {@link Transform}
	 * @return boolean
	 */
	public abstract boolean detect(Convex convex1, Transform transform1, Convex convex2, Transform transform2);
}
