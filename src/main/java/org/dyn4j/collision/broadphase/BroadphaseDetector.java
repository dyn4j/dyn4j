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
package org.dyn4j.collision.broadphase;

import java.util.Iterator;
import java.util.List;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.CollisionPair;
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
 * A {@link BroadphaseDetector} should quickly determine the pairs of objects that possibly intersect.  
 * These algorithms are used to filter out collision pairs in the interest of sending less pairs to 
 * the {@link NarrowphaseDetector} which is generally much more expensive.
 * <p>
 * {@link BroadphaseDetector}s require that the objects are updated via the {@link #update(Object)}
 * method when translated, rotated, or have their shape changed in anyway.
 * <p>
 * A {@link AABBProducer} is used to produce an {@link AABB} from a given object.  AABBs are used by the
 * {@link BroadphaseDetector}s to accelerate the initial detection of collision pairs.
 * <p>
 * Some {@link BroadphaseDetector}s may use a expansion method to expand an object's AABB size.  The 
 * {@link #getAABB(Object)} method returns the expanded {@link AABB} in this scenario.  This expansion 
 * is used to reduce the number of updates to the broad-phase acceleration structure.  See also the
 * {@link AABBExpansionMethod} interface for more detail.
 * <p>
 * The {@link #detect()}, {@link #detect(AABB)}, {@link #raycast(Ray, double)} methods, and their Iterator 
 * counterparts, use the current state of the acceleration structure housed by the {@link BroadphaseDetector}.  
 * Make sure that all changes have been reflected to the broad-phase (via {@link #update(Object)}, 
 * {@link #remove(Object)}, etc) before using these methods.
 * <p>
 * The {@link #detect(Convex, Transform, Convex, Transform)} method does not use the current state of the broad-phase,
 * but the {@link #detect(Object, Object)} does.
 * <p>
 * There are two main operation modes for {@link BroadphaseDetector}s: Full or Incremental. Full is the classic way
 * where every call to the {@link #detect()} or {@link #detectIterator()} methods return all overlapping pairs.  This
 * has the advantage of always getting a fresh list of pairs.  The down side, even with acceleration structures in place,
 * is that it's a lot of extra work, especially for scenes where most objects don't move between frames.  Incremental is
 * the new way where every call to the {@link #detect()} or {@link #detectIterator()} methods returns only the pairs where
 * one of the objects had their AABB updated (see above regarding the expansion method).  In this mode, callers would need
 * to manage the list of pairs overtime and check for those no longer overlapping.  While this is more work on the caller,
 * it's far more efficient.
 * <p>
 * <b>NOTE:</b> The objects added to a {@link BroadphaseDetector} may be used in data structures that rely on the
 * {@link Object#hashCode()} and {@link Object#equals(Object)} methods and the == operator.  In general, when using subclasses of
 * {@link CollisionBody}, avoid overriding these methods.  When using subclasses of {@link CollisionItem}, avoid
 * reusing those object as the hashcode and equals method depends upon the content.
 * @author William Bittle
 * @version 4.1.0
 * @since 1.0.0
 * @param <T> the object type
 */
public interface BroadphaseDetector<T> extends Shiftable {
	/** The default initial capacity of fixtures */
	public static final int DEFAULT_INITIAL_CAPACITY = 64;
	
	/**
	 * Adds a new object to this broad-phase.
	 * <p>
	 * If the given object has already been added, the object is updated ({@link #update(Object)}).
	 * @param object the object to add
	 * @since 3.0.0
	 */
	public abstract void add(T object);
	
	/**
	 * Removes the given object from this broad-phase.
	 * @param object the object to remove
	 * @since 3.0.0
	 * @return boolean true if the object was removed
	 */
	public abstract boolean remove(T object);
	
	/**
	 * Updates all the currently stored objects AABBs based on their
	 * current state, the {@link AABBProducer}, and the {@link AABBExpansionMethod}.
	 * @since 4.1.0
	 */
	public abstract void update();
	
	/**
	 * Updates the broad-phase representation of the given object.
	 * <p>
	 * This should be called when the object's location, rotation, or shape has changed.
	 * @param object the object
	 * @since 3.2.0
	 */
	public abstract void update(T object);
	
	/**
	 * This method forces this broad-phase to include the given object in the updated list to ensure
	 * they are checked in the updated-only detection routine.
	 * <p>
	 * The {@link #update(Object)} method will only mark an object as updated if it's fixtures 
	 * have moved enough to change the internally stored AABB.
	 * @param object the object
	 * @since 4.0.0
	 */
	public abstract void setUpdated(T object);
	
	/**
	 * Returns true if the given object is marked as updated in this broad-phase.
	 * <p>
	 * If {@link #isUpdateTrackingEnabled()} is false, this method will always return true.
	 * <p>
	 * Returns false if the given object is not part of this broad-phase.
	 * @param object the object
	 * @return boolean
	 * @since 4.0.0
	 */
	public abstract boolean isUpdated(T object);
	
	/**
	 * Clears internal state that tracks what objects have been updated.
	 * <p>
	 * Typically this method would be called from a pipeline after a broad-phase collision
	 * detection method has been called to clear the state before starting to track new
	 * updates.
	 * @since 4.0.0
	 */
	public abstract void clearUpdates();
	
	/**
	 * Returns the AABB for the given object.
	 * <p>
	 * <b>NOTE</b>: Some {@link BroadphaseDetector}s use modified (expanded for example)
	 * AABBs rather than tight fitting AABBs as a performance enhancement.  This
	 * method returns the AABB used by this detector, and therefore, the modified
	 * AABB.
	 * <p>
	 * <b>NOTE</b>: The {@link AABB} returned from this method should not be modified.
	 * Instead use the {@link AABB#copy()} method to create a new instance to 
	 * modify.
	 * <p>
	 * If the given object does not exist in this {@link BroadphaseDetector} a new AABB is 
	 * created based on the object's shape, the {@link #getAABBProducer()} and
	 * {@link #getAABBExpansionMethod()}.
	 * @param object the object
	 * @return {@link AABB}
	 * @since 3.2.0
	 */
	public abstract AABB getAABB(T object);
	
	/**
	 * Returns true if the given object exists in this broad-phase.
	 * @param object the object
	 * @return boolean
	 * @since 3.2.0
	 */
	public abstract boolean contains(T object);
	
	/**
	 * Clear all the internal state of this broad-phase.
	 * @since 3.0.0
	 */
	public abstract void clear();
	
	/**
	 * Returns the number of objects managed in this broad-phase.
	 * @return int
	 */
	public abstract int size();
	
	/**
	 * Performs collision detection on all objects that have been added to this 
	 * {@link BroadphaseDetector} and returns the list of potential collision pairs 
	 * (i.e. those pairs whose AABBs overlap).
	 * <p>
	 * The pairs returned from this method will depend on the value of the {@link #isUpdateTrackingEnabled()}
	 * flag.  When false, the returned list will report all pairs, every invocation.  When true, the 
	 * returned list will only contain pairs whose objects moved significantly enough to generate new 
	 * AABBs.  As a result, this mode would not report those pairs who ARE NOT overlapping, nor 
	 * would it report those pairs who ARE overlapping, but the objects didn't move enough.
	 * @return List&lt;{@link BroadphasePair}&gt;
	 * @since 3.0.0
	 */
	public abstract List<CollisionPair<T>> detect();

	/**
	 * Performs collision detection on all objects that have been added to this 
	 * {@link BroadphaseDetector} and returns the list of potential collision pairs 
	 * (i.e. those pairs whose AABBs overlap).
	 * <p>
	 * The pairs returned from this method will depend on the value of the {@link #isUpdateTrackingEnabled()}
	 * flag.  When false, the returned list will report all pairs, every invocation.  When true, the 
	 * returned list will only contain pairs whose objects moved significantly enough to generate new 
	 * AABBs.  As a result, this mode would not report those pairs who ARE NOT overlapping, nor 
	 * would it report those pairs who ARE overlapping, but the objects didn't move enough.
	 * <p>
	 * Use the forceFullDetection parameter to override the {@link #isUpdateTrackingEnabled()} flag for 
	 * this invocation.
	 * @param forceFullDetection true if a full detection should be performed
	 * @return List&lt;{@link BroadphasePair}&gt;
	 * @since 3.0.0 
	 */
	public abstract List<CollisionPair<T>> detect(boolean forceFullDetection);
	
	/**
	 * Performs collision detection on all objects that have been added to this 
	 * {@link BroadphaseDetector} and returns an iterator of potential collision pairs 
	 * (i.e. those pairs whose AABBs overlap).
	 * <p>
	 * The pairs returned from this method will depend on the value of the {@link #isUpdateTrackingEnabled()}
	 * flag.  When false, the returned iterator will report all pairs, every invocation.  When true, the 
	 * returned iterator will only contain pairs whose objects moved significantly enough to generate new 
	 * AABBs.  As a result, this mode would not report those pairs who ARE NOT overlapping, nor 
	 * would it report those pairs who ARE overlapping, but the objects didn't move enough.
	 * <p>
	 * NOTE: This method returns {@link CollisionPair}s that are mutable internally. If you need
	 * to store the pairs outside of the iteration, be sure to call the {@link CollisionPair#copy()}
	 * method to create a copy of the pair data.
	 * @return Iterator&lt;{@link CollisionPair}&gt;
	 * @since 4.1.0
	 */
	public abstract Iterator<CollisionPair<T>> detectIterator();
	
	/**
	 * Performs collision detection on all objects that have been added to this 
	 * {@link BroadphaseDetector} and returns an iterator of potential collision pairs 
	 * (i.e. those pairs whose AABBs overlap).
	 * <p>
	 * The pairs returned from this method will depend on the value of the {@link #isUpdateTrackingEnabled()}
	 * flag.  When false, the returned iterator will report all pairs, every invocation.  When true, the 
	 * returned iterator will only contain pairs whose objects moved significantly enough to generate new 
	 * AABBs.  As a result, this mode would not report those pairs who ARE NOT overlapping, nor 
	 * would it report those pairs who ARE overlapping, but the objects didn't move enough.
	 * <p>
	 * Use the forceFullDetection parameter to override this behavior for a single call.
	 * <p>
	 * NOTE: This method returns {@link CollisionPair}s that are mutable internally. If you need
	 * to store the pairs outside of the iteration, be sure to call the {@link CollisionPair#copy()}
	 * method to create a copy of the pair data.
	 * @param forceFullDetection true if a full detection should be performed
	 * @return Iterator&lt;{@link CollisionPair}&gt;
	 * @since 4.0.0
	 */
	public abstract Iterator<CollisionPair<T>> detectIterator(boolean forceFullDetection);
	
	/**
	 * Performs a broad-phase collision test using the given {@link AABB} and returns
	 * the items that overlap.
	 * @param aabb the {@link AABB} to test
	 * @return List&lt;T&gt;
	 * @since 3.0.0
	 */
	public abstract List<T> detect(AABB aabb);

	/**
	 * Performs a broad-phase collision test using the given {@link AABB} and returns
	 * the items that overlap.
	 * @param aabb the {@link AABB} to test
	 * @return Iterator&lt;T&gt;
	 * @since 4.0.0
	 */
	public abstract Iterator<T> detectIterator(AABB aabb);
	
	/**
	 * Performs a raycast over all the objects in this broad-phase and returns the
	 * items that intersect.
	 * @param ray the {@link Ray}
	 * @param length the length of the ray; 0.0 for infinite length
	 * @return List&lt;T&gt;
	 * @since 3.0.0
	 */
	public abstract List<T> raycast(Ray ray, double length);
	
	/**
	 * Performs a raycast over all the objects in the broad-phase and returns the
	 * items that intersect.
	 * @param ray the {@link Ray}
	 * @param length the length of the ray; 0.0 for infinite length
	 * @return Iterator&lt;T&gt;
	 * @since 4.0.0
	 */
	public abstract Iterator<T> raycastIterator(Ray ray, double length);
	
	/**
	 * Returns true if this broad-phase detector considers the given objects to be in collision.
	 * @param a the first object
	 * @param b the second object
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
	 * Returns the {@link AABBProducer} used by this broad-phase detector.
	 * @return {@link AABBProducer}
	 * @since 4.1.0
	 */
	public abstract AABBProducer<T> getAABBProducer();
	
	/**
	 * Returns the {@link AABBExpansionMethod} used by this broad-phase detector.
	 * @return {@link AABBExpansionMethod}
	 * @since 4.1.0
	 */
	public abstract AABBExpansionMethod<T> getAABBExpansionMethod();
	
	/**
	 * Returns the {@link BroadphaseFilter} used by this broad-phase detector.
	 * @return {@link BroadphaseFilter}
	 * @since 4.1.0
	 */
	public abstract BroadphaseFilter<T> getBroadphaseFilter();
	
	/**
	 * Returns whether this particular {@link BroadphaseDetector} supports update tracking.
	 * @return boolean
	 */
	public abstract boolean isUpdateTrackingSupported();

	/**
	 * Returns true if this broad-phase is tracking updated items.
	 * <p>
	 * Tracking updates to the broad-phase can have huge performance gains if the majority of objects
	 * are stationary or moving slowly enough.
	 * @return boolean
	 * @since 4.0.0
	 */
	public abstract boolean isUpdateTrackingEnabled();
	
	/**
	 * Sets the update tracking to the given flag.
	 * <p>
	 * Tracking updates to the broad-phase can have huge performance gains if the majority of objects
	 * are stationary or moving slowly enough.
	 * <p>
	 * Disabling this feature will clear the set of tracked updates (the updates themselves are not cleared).
	 * In addition, when enabling this feature (after disabling it), the user is expected to re-update all
	 * items in the broad-phase manually to ensure the updates set is non-empty.  Typically this will self
	 * heal in the next iteration though.
	 * @param flag true to turn on update tracking
	 * @since 4.0.0
	 * @see #isUpdateTrackingSupported()
	 */
	public abstract void setUpdateTrackingEnabled(boolean flag);
	
	/**
	 * Attempts to optimize the broad-phase based on the current state.
	 * <p>
	 * This method could be very intensive so should only be called if there's a clear benefit.
	 * @since 4.0.0
	 */
	public abstract void optimize();
}
