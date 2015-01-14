/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision.broadphase;

import java.util.List;

import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * Represents a broad-phase collision detection algorithm.
 * <p>
 * A {@link BroadphaseDetector} should quickly determine the pairs of {@link Collidable}s that 
 * possibly intersect.  These algorithms are used to filter out collision pairs in the interest 
 * of sending less pairs to the {@link NarrowphaseDetector}.
 * <p>
 * {@link BroadphaseDetector}s require that the collidables are updated via the {@link #update(Collidable)}
 * method when the collidables move, rotate, or are changed in anyway that changes their AABB.
 * <p>
 * {@link BroadphaseDetector}s use a expansion value to expand the AABB of a collidable.  The {@link #getAABB(Collidable)}
 * returns the expanded {@link AABB}.  This expansion is used to reduce the number of updates to the
 * broadphase.  See the {@link #setAABBExpansion(double)} for more details on this value.
 * <p>
 * The {@link #detect()}, {@link #detect(AABB)}, {@link #raycast(Ray, double)} methods use the current state of
 * all the collidables that have been added.  Make sure that all changes have been reflected to the broadphase
 * using the {@link #update(Collidable)} method before calling these methods.
 * <p>
 * The {@link #detect(Collidable, Collidable)} and {@link #detect(Convex, Transform, Convex, Transform)} methods do not
 * use the current state of the broadphase.
 * @author William Bittle
 * @version 3.1.0
 * @since 1.0.0
 * @param <E> the {@link Collidable} type
 */
// TODO add abstract keyword to methods
// TODO drop or condense Sap broadphases
// TODO rework proxies to store collidable/fixture/aabb for better multi-fixure performance
public interface BroadphaseDetector<E extends Collidable> {
	/** The default {@link AABB} expansion value */
	public static final double DEFAULT_AABB_EXPANSION = 0.2;
	
	/**
	 * Adds a new {@link Collidable} to the broadphase.
	 * @param collidable the {@link Collidable}
	 * @since 3.0.0
	 */
	public void add(E collidable);
	
	/**
	 * Removes the given {@link Collidable} from the broadphase.
	 * @param collidable the {@link Collidable}
	 * @since 3.0.0
	 */
	public void remove(E collidable);
	
	/**
	 * Updates the given {@link Collidable}.
	 * <p>
	 * Used when the collidable has moved or rotated.
	 * @param collidable the {@link Collidable}
	 * @since 3.0.0
	 */
	public void update(E collidable);
	
	/**
	 * Clears all {@link Collidable}s from the broadphase.
	 * @since 3.0.0
	 */
	public void clear();
	
	/**
	 * Returns the expanded {@link AABB} for a given {@link Collidable}.
	 * <p>
	 * Returns null if the collidable has not been added to this
	 * broadphase detector.
	 * @param collidable the {@link Collidable}
	 * @return {@link AABB} the {@link AABB} for the given {@link Collidable}
	 */
	// TODO have this return a new AABB if the collidable hasn't been added to the broadphase
	public AABB getAABB(E collidable);
	
	/**
	 * Performs collision detection on all {@link Collidable}s that have been added to
	 * this {@link BroadphaseDetector}.
	 * @return List&lt;{@link BroadphasePair}&gt;
	 * @since 3.0.0
	 */
	public List<BroadphasePair<E>> detect();
	
	/**
	 * Performs a broadphase collision test using the given {@link AABB}.
	 * @param aabb the {@link AABB} to test
	 * @return List list of all {@link AABB}s that overlap with the given {@link AABB}
	 * @since 3.0.0
	 */
	public List<E> detect(AABB aabb);

	/**
	 * Performs a preliminary raycast over all the collidables in the broadphase to improve performance of the
	 * narrowphase raycasts.
	 * @param ray the {@link Ray}
	 * @param length the length of the ray; 0.0 for infinite length
	 * @return List the filtered list of possible collidables
	 * @since 3.0.0
	 */
	public abstract List<E> raycast(Ray ray, double length);
	
	/**
	 * Performs a broadphase collision test on the given {@link Collidable}s and
	 * returns true if they could possibly intersect.
	 * @param a the first {@link Collidable}
	 * @param b the second {@link Collidable}
	 * @return boolean
	 */
	// TODO have this use the AABBs in the broadphase via the getAABB method
	public abstract boolean detect(E a, E b);
	
	/**
	 * Performs a broadphase collision test on the given {@link Convex} {@link Shape}s and
	 * returns true if they could possibly intersect.
	 * <p>
	 * This method does not use the expansion value.
	 * @param convex1 the first {@link Convex} {@link Shape}
	 * @param transform1 the first {@link Convex} {@link Shape}'s {@link Transform}
	 * @param convex2 the second {@link Convex} {@link Shape}
	 * @param transform2 the second {@link Convex} {@link Shape}'s {@link Transform}
	 * @return boolean
	 */
	public abstract boolean detect(Convex convex1, Transform transform1, Convex convex2, Transform transform2);
	
	/**
	 * Returns the {@link AABB} expansion value used to improve performance of broadphase updates.
	 * @return double
	 */
	public abstract double getAABBExpansion();
	
	/**
	 * Sets the {@link AABB} expansion value used to improve performance of broadphase updates.
	 * <p>
	 * Increasing this value will cause less updates to the broadphase but will cause more pairs
	 * to be sent to the narrowphase.
	 * @param expansion the expansion
	 */
	public abstract void setAABBExpansion(double expansion);
	
	/**
	 * Translates all {@link Collidable} proxies to match the given coordinate shift.
	 * @param shift the amount to shift along the x and y axes
	 * @since 3.1.0
	 */
	public abstract void shiftCoordinates(Vector2 shift);
}
