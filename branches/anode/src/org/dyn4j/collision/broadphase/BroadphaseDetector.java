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
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;

/**
 * Represents a broad-phase collision detection algorithm.
 * <p>
 * A {@link BroadphaseDetector} should quickly determine the pairs of {@link Collidable}s and 
 * {@link Fixture}s that possibly intersect.  These algorithms are used to filter out collision 
 * pairs in the interest of sending less pairs to the {@link NarrowphaseDetector}.
 * <p>
 * {@link BroadphaseDetector}s require that the collidables are updated via the {@link #update(Collidable)}
 * or {@link #update(Collidable, Fixture)} methods when the collidables move, rotate, or are changed in 
 * anyway that changes their AABB or their fixture's AABBs.
 * <p>
 * {@link BroadphaseDetector}s use a expansion value to expand the AABB of a collidable.  The 
 * {@link #getAABB(Collidable)} returns the expanded {@link AABB}.  This expansion is used to reduce the 
 * number of updates to the broadphase.  See the {@link #setAABBExpansion(double)} for more details on 
 * this value.
 * <p>
 * The {@link #detect(BroadphaseFilter)}, {@link #detect(AABB)}, {@link #raycast(Ray, double)} methods 
 * use the current state of all the collidables and fixtures that have been added.  Make sure that all 
 * changes have been reflected to the broadphase using the {@link #update(Collidable)} and 
 * {@link #update(Collidable, Fixture)} methods before calling these.
 * <p>
 * The {@link #detect(Collidable, Collidable)} and {@link #detect(Convex, Transform, Convex, Transform)} methods do not
 * use the current state of the broadphase.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 * @param <E> the {@link Collidable} type
 * @param <T> the {@link Fixture} type
 */
public interface BroadphaseDetector<E extends Collidable<T>, T extends Fixture> extends Shiftable {
	/** The default {@link AABB} expansion value */
	public static final double DEFAULT_AABB_EXPANSION = 0.2;
	
	/** The default initial capacity of fixtures */
	public static final int DEFAULT_INITIAL_CAPACITY = 64;
	
	/**
	 * Adds a new {@link Collidable} to the broadphase.
	 * <p>
	 * This will add all the collidable's {@link Fixture}s to the broadphase.
	 * <p>
	 * This method does not check if the collidable has already been added.
	 * <p>
	 * If the colliable has no fixtures, this method does nothing.
	 * @param collidable the {@link Collidable}
	 * @since 3.0.0
	 */
	public abstract void add(E collidable);
	
	/**
	 * Adds a new {@link Fixture} for the given {@link Collidable} to
	 * the broadphase.
	 * <p>
	 * This method does not check if the given fixture has already been
	 * added for the given collidable.
	 * @param collidable the collidable
	 * @param fixture the fixture to add
	 * @since 3.2.0
	 */
	public abstract void add(E collidable, T fixture);
	
	/**
	 * Removes the given {@link Collidable} from the broadphase.
	 * <p>
	 * This method removes all the {@link Fixture}s attached to the
	 * given {@link Collidable} from the broadphase, if they exist.
	 * <p>
	 * If a fixture is removed from a {@link Collidable}, the calling code must
	 * call the {@link #remove(Collidable, Fixture)} method for that fixture to 
	 * be removed from the broadphase.  This method makes no effort to remove other
	 * fixtures no longer attached to the given collidable.
	 * @param collidable the {@link Collidable}
	 * @since 3.0.0
	 */
	public abstract void remove(E collidable);
	
	/**
	 * Removes the given {@link Fixture} for the given {@link Collidable} from
	 * the broadphase and returns true if it was found.
	 * @param collidable the collidable
	 * @param fixture the fixture to remove
	 * @return boolean true if the fixture was found and removed
	 * @since 3.2.0
	 */
	public abstract boolean remove(E collidable, T fixture);
	
	/**
	 * Updates all the {@link Fixture}s on the given {@link Collidable}.
	 * <p>
	 * Used when the collidable or its fixtures have moved or rotated.
	 * <p>
	 * This method updates all the {@link Fixture}s attached to the
	 * given {@link Collidable} from the broadphase, if they exist.  If they
	 * do not exist in the broadphase they will not be added or updated.
	 * @param collidable the {@link Collidable}
	 * @since 3.2.0
	 */
	public abstract void update(E collidable);
	
	/**
	 * Updates the given {@link Collidable}'s {@link Fixture}.
	 * <p>
	 * Used when a fixture on a {@link Collidable} has moved or rotated.
	 * @param collidable the {@link Collidable}
	 * @param fixture the {@link Fixture} that has moved
	 * @since 3.2.0
	 */
	public abstract void update(E collidable, T fixture);
	
	/**
	 * Returns the AABB for the given {@link Collidable}.
	 * <p>
	 * The AABB returned is an AABB encompasing all fixtures on the
	 * given {@link Collidable}.  When possible, AABBs from the
	 * broadphase will be used to create this.
	 * <p>
	 * If the collidable doesn't have any fixtures a degenerate
	 * AABB is returned.
	 * @param collidable the {@link Collidable}
	 * @return {@link AABB}
	 * @since 3.2.0
	 */
	public abstract AABB getAABB(E collidable);
	
	/**
	 * Returns the AABB for the given {@link Collidable} {@link Fixture}.
	 * <p>
	 * If the collidable and its fixture have not been added to this
	 * broadphase, a new AABB is created and returned.
	 * @param collidable the {@link Collidable}
	 * @param fixture the {@link Fixture}
	 * @return {@link AABB}
	 * @since 3.2.0
	 */
	public abstract AABB getAABB(E collidable, T fixture);
	
	/**
	 * Returns true if all the {@link Fixture}s on the given {@link Collidable}
	 * have been added to this broadphase.
	 * <p>
	 * If a collidable is added without any fixtures, this method will return
	 * false, since the fixtures, and not the collidable are added to the
	 * broadphase.
	 * @param collidable the {@link Collidable}
	 * @return boolean
	 * @since 3.2.0
	 */
	public abstract boolean contains(E collidable);
	
	/**
	 * Returns true if the given {@link Fixture} on the given {@link Collidable}
	 * has been added to this broadphase.
	 * @param collidable the {@link Collidable}
	 * @param fixture the {@link Fixture}
	 * @return boolean
	 * @since 3.2.0
	 */
	public abstract boolean contains(E collidable, T fixture);
	
	/**
	 * Clears all {@link Collidable}s and their {@link Fixture}s from the broadphase.
	 * @since 3.0.0
	 */
	public abstract void clear();
	
	/**
	 * Returns the number of {@link Fixture}s that are being managed in this broadphase.
	 * @return int
	 */
	public abstract int size();
	
	/**
	 * Performs collision detection on all {@link Collidable} {@link Fixture}s that have 
	 * been added to this {@link BroadphaseDetector}.
	 * @return List&lt;{@link BroadphasePair}&gt;
	 * @since 3.0.0
	 */
	public abstract List<BroadphasePair<E, T>> detect();
	
	/**
	 * Performs collision detection on all {@link Collidable} {@link Fixture}s that have 
	 * been added to this {@link BroadphaseDetector}.
	 * <p>
	 * Use the <code>filter</code> parameter to further reduce the number of potential pairs
	 * send to the narrowphase.
	 * @param filter the broadphase filter
	 * @return List&lt;{@link BroadphasePair}&gt;
	 * @since 3.2.0
	 * @see #detect()
	 */
	public abstract List<BroadphasePair<E, T>> detect(BroadphaseFilter<E, T> filter);
	
	/**
	 * Performs a broadphase collision test using the given {@link AABB}.
	 * @param aabb the {@link AABB} to test
	 * @return List&lt;{@link BroadphaseItem}&gt;
	 * @since 3.0.0
	 */
	public abstract List<BroadphaseItem<E, T>> detect(AABB aabb);
	
	/**
	 * Performs a broadphase collision test using the given {@link AABB}.
	 * <p>
	 * Use the <code>filter</code> parameter to further reduce the number of {@link Collidable}
	 * {@link Fixture}s returned.
	 * @param aabb the {@link AABB} to test
	 * @param filter the broadphase filter
	 * @return List&lt;{@link BroadphaseItem}&gt;
	 * @since 3.2.0
	 * @see #detect(AABB)
	 */
	public abstract List<BroadphaseItem<E, T>> detect(AABB aabb, BroadphaseFilter<E, T> filter);

	/**
	 * Performs a preliminary raycast over all the collidables in the broadphase to improve performance of the
	 * narrowphase raycasts.
	 * @param ray the {@link Ray}
	 * @param length the length of the ray; 0.0 for infinite length
	 * @return List&lt;{@link BroadphaseItem}&gt;
	 * @since 3.0.0
	 */
	public abstract List<BroadphaseItem<E, T>> raycast(Ray ray, double length);
	
	/**
	 * Performs a preliminary raycast over all the collidables in the broadphase to improve performance of the
	 * narrowphase raycasts.
	 * <p>
	 * Use the <code>filter</code> parameter to further reduce the number of {@link Collidable}
	 * {@link Fixture}s returned.
	 * @param ray the {@link Ray}
	 * @param length the length of the ray; 0.0 for infinite length
	 * @param filter the broadphase filter
	 * @return List&lt;{@link BroadphaseItem}&gt;
	 * @since 3.2.0
	 * @see #raycast(Ray, double)
	 */
	public abstract List<BroadphaseItem<E, T>> raycast(Ray ray, double length, BroadphaseFilter<E, T> filter);
	
	/**
	 * Performs a broadphase collision test on the given {@link Collidable}s and
	 * returns true if they could possibly intersect.
	 * @param a the first {@link Collidable}
	 * @param b the second {@link Collidable}
	 * @return boolean
	 */
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
}
