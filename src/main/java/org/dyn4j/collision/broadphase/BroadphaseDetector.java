/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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

import java.util.Iterator;
import java.util.List;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.CollisionPair;
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
 * A {@link BroadphaseDetector} should quickly determine the pairs of {@link CollisionBody}s and 
 * {@link Fixture}s that possibly intersect.  These algorithms are used to filter out collision 
 * pairs in the interest of sending less pairs to the {@link NarrowphaseDetector} which is generally
 * much more expensive.
 * <p>
 * {@link BroadphaseDetector}s require that the bodies are updated via the {@link #update(CollisionBody)}
 * or {@link #update(CollisionBody, Fixture)} methods when the bodies move, rotate, or have their shape
 * changed in anyway.
 * <p>
 * <b>
 * NOTE: Special care must be taken when removing fixtures from a body.  Be sure to call the 
 * {@link #remove(CollisionBody, Fixture)} method to make sure its removed from the broad-phase.
 * </b>
 * <p>
 * Some {@link BroadphaseDetector}s use a expansion value to expand a body's AABB width and height.  The 
 * {@link #getAABB(CollisionBody)} returns the expanded {@link AABB}.  This expansion is used to reduce the 
 * number of updates to the broad-phase.  See the {@link #setAABBExpansion(double)} for more details on 
 * this value.
 * <p>
 * The {@link #detect()}, {@link #detect(AABB)}, {@link #raycast(Ray, double)} methods and their Iterator 
 * counterpart methods use the current state of all the bodies and fixtures that have been added.  Make 
 * sure that all changes have been reflected to the broad-phase using the {@link #update(CollisionBody)} and 
 * {@link #update(CollisionBody, Fixture)} methods before calling these.
 * <p>
 * The {@link #detect(Convex, Transform, Convex, Transform)} method does not use the current state of the broad-phase,
 * but the {@link #detect(CollisionBody, CollisionBody)} does.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public interface BroadphaseDetector<T extends CollisionBody<E>, E extends Fixture> extends Shiftable {
	/** The default {@link AABB} expansion value */
	public static final double DEFAULT_AABB_EXPANSION = 0.2;
	
	/** The default initial capacity of fixtures */
	public static final int DEFAULT_INITIAL_CAPACITY = 64;
	
	/**
	 * Adds a new {@link CollisionBody} to the broad-phase.
	 * <p>
	 * This will add all the given {@link CollisionBody}'s {@link Fixture}s to the broad-phase.
	 * <p>
	 * If the body has no fixtures, nothing will be added to this broad-phase.
	 * <p>
	 * If the {@link CollisionBody}'s {@link Fixture}s have already been added to this broad-phase
	 * they will instead be updated.
	 * <p>
	 * If a fixture is removed from a {@link CollisionBody}, the calling code must
	 * call the {@link #remove(CollisionBody, Fixture)} method for that fixture to 
	 * be removed from the broad-phase.  This method makes no effort to remove
	 * fixtures no longer attached to the given body.
	 * @param body the {@link CollisionBody}
	 * @since 3.0.0
	 */
	public abstract void add(T body);
	
	/**
	 * Adds a new {@link Fixture} for the given {@link CollisionBody} to
	 * the broad-phase.
	 * @param body the body
	 * @param fixture the fixture to add
	 * @since 3.2.0
	 */
	public abstract void add(T body, E fixture);
	
	/**
	 * Removes the given {@link CollisionBody} from the broad-phase.
	 * <p>
	 * This method removes all the {@link Fixture}s attached to the
	 * given {@link CollisionBody} from the broad-phase.
	 * <p>
	 * If a fixture is removed from a {@link CollisionBody}, the calling code must
	 * call the {@link #remove(CollisionBody, Fixture)} method for that fixture to 
	 * be removed from the broad-phase.  This method makes no effort to remove
	 * fixtures no longer attached to the given body.
	 * @param body the {@link CollisionBody}
	 * @since 3.0.0
	 */
	public abstract void remove(T body);
	
	/**
	 * Removes the given {@link Fixture} for the given {@link CollisionBody} from
	 * the broad-phase and returns true if it was found.
	 * @param body the body
	 * @param fixture the fixture to remove
	 * @return boolean true if the fixture was found and removed
	 * @since 3.2.0
	 */
	public abstract boolean remove(T body, E fixture);
	
	/**
	 * Removes the given {@link Fixture} for the given {@link CollisionBody} from
	 * the broad-phase and returns true if it was found.
	 * @param item the collision item
	 * @return boolean true if the fixture was found and removed
	 * @since 4.0.0
	 */
	public abstract boolean remove(CollisionItem<T, E> item);
	
	/**
	 * Updates all the {@link Fixture}s on the given {@link CollisionBody}.
	 * <p>
	 * Used when the body or its fixtures have moved or rotated.
	 * <p>
	 * This method updates all the {@link Fixture}s attached to the
	 * given {@link CollisionBody} from the broad-phase, if they exist. If the 
	 * fixtures on the given body do not exist in the broad-phase, they are
	 * added.
	 * <p>
	 * If a fixture is removed from a {@link CollisionBody}, the calling code must
	 * call the {@link #remove(CollisionBody, Fixture)} method for that fixture to 
	 * be removed from the broad-phase.  This method makes no effort to remove
	 * fixtures no longer attached to the given body.
	 * @param body the {@link CollisionBody}
	 * @since 3.2.0
	 */
	public abstract void update(T body);
	
	/**
	 * Updates the given {@link CollisionBody}'s {@link Fixture}.
	 * <p>
	 * Used when a fixture on a {@link CollisionBody} has moved or rotated.
	 * <p>
	 * This method will add the {@link Fixture} if it doesn't currently exist in
	 * this broad-phase.
	 * @param body the {@link CollisionBody}
	 * @param fixture the {@link Fixture} that has moved
	 * @since 3.2.0
	 */
	public abstract void update(T body, E fixture);
	
	/**
	 * The {@link #update(CollisionBody)} method will only mark a {@link CollisionBody}
	 * as updated if it's fixtures have moved enough to change the internally
	 * stored AABB.
	 * <p>
	 * This method is intended to force the broadphase to include
	 * this {@link CollisionBody}'s {@link Fixture}s in the updated list to ensure
	 * they are checked in the updated-only detection routine.
	 * @param body the {@link CollisionBody}
	 * @since 4.0.0
	 */
	public abstract void setUpdated(T body);
	
	/**
	 * The {@link #update(CollisionBody, Fixture)} method will only mark the 
	 * {@link Fixture} as updated if the {@link Fixture} has 
	 * moved enough to change the internally stored AABB.
	 * <p>
	 * This method is intended to force the broadphase to include
	 * the {@link Fixture} in the updated list to ensure
	 * they are checked in the updated-only detection routine.
	 * @param body the {@link CollisionBody}
	 * @param fixture the {@link Fixture}
	 * @since 4.0.0
	 */
	public abstract void setUpdated(T body, E fixture);
	
	/**
	 * Returns true if any of the {@link Fixture}s on the given {@link CollisionBody}
	 * are included in the updated list.
	 * @param body the {@link CollisionBody}
	 * @return boolean
	 * @since 4.0.0
	 */
	public abstract boolean isUpdated(T body);
	
	/**
	 * Returns true if the given {@link Fixture} is included in the updated list.
	 * @param body the {@link CollisionBody}
	 * @param fixture the {@link Fixture}
	 * @return boolean
	 * @since 4.0.0
	 */
	public abstract boolean isUpdated(T body, E fixture);
	
	/**
	 * Returns true if the given {@link Fixture} is included in the updated list.
	 * @param item the collision item
	 * @return boolean
	 * @since 4.0.0
	 */
	public abstract boolean isUpdated(CollisionItem<T, E> item);
	
	/**
	 * Clears any internal state that tracks what {@link CollisionBody} {@link Fixture}s have
	 * been updated.
	 * <p>
	 * Typically this method would be called from a pipeline after a broadphase collision
	 * detection method has been called to clear the state before starting to track new
	 * updates.
	 * @since 4.0.0
	 */
	public abstract void clearUpdates();
	
	/**
	 * Returns the AABB for the given {@link CollisionBody}.
	 * <p>
	 * The AABB returned is an AABB encompasing all fixtures on the
	 * given {@link CollisionBody}.  When possible, AABBs from the
	 * broad-phase will be used to create this.
	 * <p>
	 * If the body doesn't have any fixtures a degenerate
	 * AABB is returned.
	 * @param body the {@link CollisionBody}
	 * @return {@link AABB}
	 * @since 3.2.0
	 */
	public abstract AABB getAABB(T body);
	
	/**
	 * Returns the AABB for the given {@link CollisionBody} {@link Fixture}.
	 * <p>
	 * If the body and its fixture have not been added to this
	 * broad-phase, a new AABB is created and returned (but not added to
	 * broad-phase).
	 * <p>
	 * NOTE: The {@link AABB} returned from this method should not be modified.
	 * Instead use the {@link AABB#copy()} method to create a new instance to 
	 * modify.
	 * @param body the {@link CollisionBody}
	 * @param fixture the {@link Fixture}
	 * @return {@link AABB}
	 * @since 3.2.0
	 */
	public abstract AABB getAABB(T body, E fixture);
	
	/**
	 * Returns the AABB for the given {@link CollisionBody} {@link Fixture}.
	 * <p>
	 * If the body and its fixture have not been added to this
	 * broad-phase, a new AABB is created and returned (but not added to
	 * broad-phase).
	 * <p>
	 * NOTE: The {@link AABB} returned from this method should not be modified.
	 * Instead use the {@link AABB#copy()} method to create a new instance to 
	 * modify.
	 * @param item the collision item
	 * @return {@link AABB}
	 * @since 4.0.0
	 */
	public abstract AABB getAABB(CollisionItem<T, E> item);
	
	/**
	 * Returns true if all the {@link Fixture}s on the given {@link CollisionBody}
	 * have been added to this broad-phase.
	 * <p>
	 * If a body is added without any fixtures, this method will return
	 * false, since the fixtures, not the body, are added to the
	 * broad-phase.
	 * @param body the {@link CollisionBody}
	 * @return boolean
	 * @since 3.2.0
	 */
	public abstract boolean contains(T body);
	
	/**
	 * Returns true if the given {@link Fixture} on the given {@link CollisionBody}
	 * has been added to this broadphase.
	 * @param body the {@link CollisionBody}
	 * @param fixture the {@link Fixture}
	 * @return boolean
	 * @since 3.2.0
	 */
	public abstract boolean contains(T body, E fixture);
	
	/**
	 * Returns true if the given {@link Fixture} on the given {@link CollisionBody}
	 * has been added to this broadphase.
	 * @param item the collision item
	 * @return boolean
	 * @since 4.0.0
	 */
	public abstract boolean contains(CollisionItem<T, E> item);
	
	/**
	 * Clears all the {@link CollisionBody} {@link Fixture}s from this broad-phase.
	 * @since 3.0.0
	 */
	public abstract void clear();
	
	/**
	 * Returns the number of {@link CollisionBody} {@link Fixture}s that are being managed in this broad-phase.
	 * @return int
	 */
	public abstract int size();
	
	/**
	 * Performs collision detection on all {@link CollisionBody} {@link Fixture}s that have 
	 * been added to this {@link BroadphaseDetector} and returns the list of potential
	 * pairs.
	 * @return List&lt;{@link BroadphasePair}&gt;
	 * @since 3.0.0 
	 */
	public abstract List<CollisionPair<T, E>> detect();

	/**
	 * Performs collision detection on {@link CollisionBody} {@link Fixture}s that have 
	 * been added to this {@link BroadphaseDetector} and returns the list of potential
	 * pairs.
	 * <p>
	 * The returned pairs from this method will depend on the {@link #isUpdateTrackingEnabled()}
	 * flag. If the flag is true, then only updated pairs will be emitted, otherwise all pairs
	 * are emitted.
	 * <p>
	 * Use the forceFullDetection parameter to override this behavior for this call.
	 * @param forceFullDetection true if a full detection should be performed
	 * @return List&lt;{@link BroadphasePair}&gt;
	 * @since 3.0.0 
	 */
	public abstract List<CollisionPair<T, E>> detect(boolean forceFullDetection);
	
	/**
	 * Performs collision detection on all {@link CollisionBody} {@link Fixture}s that have 
	 * been added to this {@link BroadphaseDetector} and returns the list of potential
	 * pairs.
	 * <p>
	 * Use the <code>filter</code> parameter to further reduce the number of potential pairs.
	 * @param filter the broad-phase filter
	 * @return List&lt;{@link BroadphasePair}&gt;
	 * @since 3.2.0
	 * @see #detect()
	 * @deprecated Deprecated in 4.0.0. Use the {@link #detect()} method instead.
	 */
	@Deprecated
	public abstract List<CollisionPair<T, E>> detect(BroadphaseFilter<T, E> filter);
	
	/**
	 * Performs collision detection on all {@link CollisionBody} {@link Fixture}s that have 
	 * been added to this {@link BroadphaseDetector} and returns the list of potential
	 * pairs.
	 * <p>
	 * The returned pairs from this method will depend on the {@link #isUpdateTrackingEnabled()}
	 * flag. If the flag is true, then only updated pairs will be emitted, otherwise all pairs
	 * are emitted.
	 * <p>
	 * Use the forceFullDetection parameter to override this behavior for this call.
	 * <p>
	 * NOTE: This method returns {@link CollisionPair}s that are mutable internally. If you need
	 * to store the pairs outside of the iteration, be sure to call the {@link CollisionPair#copy()}
	 * method to create a copy of the pair data.
	 * @param forceFullDetection true if a full detection should be performed
	 * @return Iterator&lt;{@link CollisionPair}&gt;
	 * @since 4.0.0
	 */
	public abstract Iterator<CollisionPair<T, E>> detectIterator(boolean forceFullDetection);
	
	/**
	 * Performs a broad-phase collision test using the given {@link AABB} and returns
	 * the items that overlap.
	 * @param aabb the {@link AABB} to test
	 * @return List&lt;{@link CollisionItem}&gt;
	 * @since 3.0.0
	 */
	public abstract List<CollisionItem<T, E>> detect(AABB aabb);

	/**
	 * Performs a broad-phase collision test using the given {@link AABB} and returns
	 * the items that overlap.
	 * @param aabb the {@link AABB} to test
	 * @return Iterator&lt;{@link CollisionItem}&gt;
	 * @since 4.0.0
	 */
	public abstract Iterator<CollisionItem<T, E>> detectIterator(AABB aabb);
	
	/**
	 * Performs a broad-phase collision test using the given {@link AABB} and returns
	 * the items that overlap.
	 * <p>
	 * Use the <code>filter</code> parameter to further reduce the number of items returned.
	 * @param aabb the {@link AABB} to test
	 * @param filter the broad-phase filter
	 * @return List&lt;{@link CollisionItem}&gt;
	 * @since 3.2.0
	 * @see #detect(AABB)
	 * @deprecated Deprecated in 4.0.0. Use the {@link #detect(AABB)} method instead
	 */
	@Deprecated
	public abstract List<CollisionItem<T, E>> detect(AABB aabb, BroadphaseFilter<T, E> filter);

	/**
	 * Performs a preliminary raycast over all the bodies in the broad-phase and returns the
	 * items that intersect.
	 * @param ray the {@link Ray}
	 * @param length the length of the ray; 0.0 for infinite length
	 * @return List&lt;{@link CollisionItem}&gt;
	 * @since 3.0.0
	 * @deprecated Deprecated in 4.0.0. Use the {@link #detect(Ray, double)} method instead.
	 */
	@Deprecated
	public abstract List<CollisionItem<T, E>> raycast(Ray ray, double length);
	
	/**
	 * Performs a raycast over all the bodies in the broad-phase and returns the
	 * items that intersect.
	 * @param ray the {@link Ray}
	 * @param length the length of the ray; 0.0 for infinite length
	 * @return List&lt;{@link CollisionItem}&gt;
	 * @since 4.0.0
	 */
	public abstract List<CollisionItem<T, E>> detect(Ray ray, double length);
	
	/**
	 * Performs a raycast over all the bodies in the broad-phase and returns the
	 * items that intersect.
	 * @param ray the {@link Ray}
	 * @param length the length of the ray; 0.0 for infinite length
	 * @return Iterator&lt;{@link CollisionItem}&gt;
	 * @since 4.0.0
	 */
	public abstract Iterator<CollisionItem<T, E>> detectIterator(Ray ray, double length);
	
	/**
	 * Performs a preliminary raycast over all the bodies in the broad-phase and returns the
	 * items that intersect.
	 * <p>
	 * Use the <code>filter</code> parameter to further reduce the number of items returned.
	 * @param ray the {@link Ray}
	 * @param length the length of the ray; 0.0 for infinite length
	 * @param filter the broad-phase filter
	 * @return List&lt;{@link BroadphaseItem}&gt;
	 * @since 3.2.0
	 * @see #raycast(Ray, double)
	 * @deprecated Deprecated in 4.0.0. Use the {@link #detect(Ray, double)} method instead
	 */
	@Deprecated
	public abstract List<CollisionItem<T, E>> raycast(Ray ray, double length, BroadphaseFilter<T, E> filter);
	
	/**
	 * Returns true if this broad-phase detector considers the given bodies to be in collision.
	 * @param a the first {@link CollisionBody}
	 * @param b the second {@link CollisionBody}
	 * @return boolean
	 */
	public abstract boolean detect(T a, T b);
	
	/**
	 * Returns true if this broad-phase detector considers the given {@link Convex} {@link Shape}s to be in collision.
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
	 * Returns whether this particular {@link BroadphaseDetector} supports expanding AABBs.
	 * @return boolean
	 */
	public abstract boolean supportsAABBExpansion();
	
	/**
	 * Returns the {@link AABB} expansion value used to improve performance of broad-phase updates.
	 * <p>
	 * If supportsAABBExpansion() returns false the value returned is unspecified and should not be taken into account.
	 * @return double
	 * @see #setAABBExpansion(double)
	 */
	public abstract double getAABBExpansion();
	
	/**
	 * Sets the {@link AABB} expansion value used to improve performance of broad-phase updates.
	 * <p>
	 * Increasing this value will cause less updates to the broad-phase but will cause more pairs
	 * to be sent to the narrow-phase.
	 * <p>
	 * Note that a broadphase implementation may ignore this value, if supportsAABBExpansion() returns false.
	 * @param expansion the expansion
	 */
	public abstract void setAABBExpansion(double expansion);
	
	/**
	 * Returns true if this broadphase is tracking updated items.
	 * <p>
	 * Tracking updates to the broadphase can have huge performance gains if the majority of objects
	 * are stationary or moving slowly enough.
	 * @return boolean
	 * @since 4.0.0
	 */
	public abstract boolean isUpdateTrackingEnabled();
	
	/**
	 * Sets the update tracking to the given flag.
	 * <p>
	 * Tracking updates to the broadphase can have huge performance gains if the majority of objects
	 * are stationary or moving slowly enough.
	 * <p>
	 * Disabling this feature will clear the set of tracked updates (the updates themselves are not cleared).
	 * In addition, when enabling this feature (after disabling it), the user is expected to re-update all
	 * items in the broadphase manually to ensure the updates set is non-empty.  Typically this will self
	 * heal in the next iteration though.
	 * @param flag true to turn on update tracking
	 * @since 4.0.0
	 */
	public abstract void setUpdateTrackingEnabled(boolean flag);
	
	/**
	 * Attempts to optimize the broadphase based on the current state.
	 * <p>
	 * This method could be very intensive so should only be called if there's a clear benefit.
	 * @since 4.0.0
	 */
	public abstract void optimize();
}
