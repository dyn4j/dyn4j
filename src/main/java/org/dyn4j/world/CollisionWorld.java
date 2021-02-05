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
package org.dyn4j.world;

import java.util.Iterator;
import java.util.List;

import org.dyn4j.DataContainer;
import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.broadphase.CollisionItemBroadphaseDetector;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.NarrowphasePostProcessor;
import org.dyn4j.collision.narrowphase.RaycastDetector;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.listener.BoundsListener;
import org.dyn4j.world.listener.CollisionListener;
import org.dyn4j.world.result.ConvexCastResult;
import org.dyn4j.world.result.ConvexDetectResult;
import org.dyn4j.world.result.DetectResult;
import org.dyn4j.world.result.RaycastResult;

/**
 * Represents a world where {@link CollisionBody}s are added to participate in collision detection.
 * <p>
 * Along with defining the necessary methods to maintain the {@link CollisionBody}s in an instance, this 
 * interface exposes the broad-narrow-manifold phases of collision detection allowing a user to swap 
 * implementations.
 * <p>
 * This interface also defines the basic static queries a user would want to perform against the world
 * including raycating, convex casting, AABB, and convex queries.
 * <p>
 * NOTE: This interface does not define a collision detection pipeline or process. Instead it defines the
 * necessary components to build one.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.0.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 * @param <V> the {@link CollisionData} type
 */
public interface CollisionWorld<T extends CollisionBody<E>, E extends Fixture, V extends CollisionData<T, E>> extends Shiftable, DataContainer {
	/** The default {@link CollisionBody} count */
	public static final int DEFAULT_INITIAL_BODY_CAPACITY = 64;
	
	/**
	 * Adds the given {@link CollisionBody} to this {@link CollisionWorld}.
	 * @param body the {@link CollisionBody} to add
	 * @throws NullPointerException if body is null
	 * @throws IllegalArgumentException if body has already been added to this world or if its a member of another world instance
	 * @since 3.1.1
	 */
	public void addBody(T body);
	
	/**
	 * Returns true if this world contains the given body.
	 * @param body the {@link CollisionBody} to test for
	 * @return boolean true if the body is contained in this world
	 * @since 3.1.1
	 */
	public boolean containsBody(T body);
	
	/**
	 * Removes the {@link CollisionBody} at the given index from this {@link CollisionWorld}.
	 * @param index the index of the body to remove.
	 * @return boolean true if the body was removed
	 * @since 3.2.0
	 */
	public boolean removeBody(int index);

	/**
	 * Removes the given {@link CollisionBody} from this {@link CollisionWorld}.
	 * @param body the {@link CollisionBody} to remove.
	 * @return boolean true if the body was removed
	 */
	public boolean removeBody(T body);
	
	/**
	 * Removes all bodies from this world.
	 * @since 3.0.1
	 */
	public void removeAllBodies();
	
	/**
	 * Returns the number of {@link CollisionBody}s in this {@link CollisionWorld}.
	 * @return int the number of bodies
	 */
	public int getBodyCount();
	
	/**
	 * Returns the {@link CollisionBody} at the given index.
	 * @param index the index
	 * @return {@link CollisionBody}
	 */
	public T getBody(int index);
	
	/**
	 * Returns an unmodifiable list containing all the bodies in this world.
	 * <p>
	 * The returned list is backed by the internal list, therefore adding or removing bodies while 
	 * iterating through the returned list is not permitted.  Use the {@link #getBodyIterator()}
	 * method instead.
	 * @return List&lt;{@link CollisionBody}&gt;
	 * @since 3.1.5
	 * @see #getBodyIterator()
	 */
	public List<T> getBodies();

	/**
	 * Returns an iterator for iterating over the bodies in this world.
	 * <p>
	 * The returned iterator supports the <code>remove</code> method.
	 * @return Iterator&lt;{@link CollisionBody}&gt;
	 * @since 3.2.0
	 */
	public Iterator<T> getBodyIterator();
	
	/**
	 * Returns true if this world doesn't contain any
	 * bodies or joints.
	 * @return boolean
	 * @since 3.0.1
	 */
	public boolean isEmpty();
	
	// bounds
	
	/**
	 * Sets the bounds of this {@link CollisionWorld}.
	 * @param bounds the bounds; can be null
	 */
	public void setBounds(Bounds bounds);
	
	/**
	 * Returns the bounds of this world.
	 * <p>
	 * This will return null if no bounds were initially set
	 * or if it was set to null via the {@link #setBounds(Bounds)}
	 * method.
	 * @return {@link Bounds} the bounds or null
	 */
	public Bounds getBounds();
	
	// listeners

	/**
	 * Returns an unmodifiable list of all the collision listeners registered to this world.
	 * @return List&lt;{@link CollisionListener}&gt;
	 */
	public List<CollisionListener<T, E>> getCollisionListeners();
	
	/**
	 * Returns an unmodifiable list of all the bounds listeners registered to this world.
	 * @return List&lt;{@link BoundsListener}&gt;
	 */
	public List<BoundsListener<T, E>> getBoundsListeners();
	
	/**
	 * Removes all listeners from this world.
	 */
	public void removeAllListeners();

	/**
	 * Removes all collision listeners from this world.
	 */
	public void removeAllCollisionListeners();
	
	/**
	 * Removes all bounds listeners from this world.
	 */
	public void removeAllBoundsListeners();

	/**
	 * Removes the given collision listener from this world and returns true if it was removed.
	 * <p>
	 * This method will return false if the listener was not found in this world.
	 * @param listener the listener
	 * @return boolean
	 */
	public boolean removeCollisionListener(CollisionListener<T, E> listener);
	
	/**
	 * Removes the given bounds listener from this world and returns true if it was removed.
	 * <p>
	 * This method will return false if the listener was not found in this world.
	 * @param listener the listener
	 * @return boolean
	 */
	public boolean removeBoundsListener(BoundsListener<T, E> listener);
	
	/**
	 * Adds the given {@link CollisionListener} to this world.
	 * <p>
	 * NOTE: No effort is made to prevent duplicate listeners from being added.
	 * @param listener the listener to add
	 * @return boolean
	 */
	public boolean addCollisionListener(CollisionListener<T, E> listener);
	
	/**
	 * Adds the given {@link BoundsListener} to this world.
	 * <p>
	 * NOTE: No effort is made to prevent duplicate listeners from being added.
	 * @param listener the listener to add
	 * @return boolean
	 */
	public boolean addBoundsListener(BoundsListener<T, E> listener);
	
	// broadphase
	
	/**
	 * Sets the broad-phase collision detection algorithm.
	 * @param broadphaseDetector the broad-phase collision detection algorithm
	 * @throws NullPointerException if broadphaseDetector is null
	 */
	public void setBroadphaseDetector(CollisionItemBroadphaseDetector<T, E> broadphaseDetector);
	
	/**
	 * Returns the broad-phase collision detection algorithm.
	 * @return {@link BroadphaseDetector} the broad-phase collision detection algorithm
	 */
	public CollisionItemBroadphaseDetector<T, E> getBroadphaseDetector();
	
	/**
	 * Sets the {@link BroadphaseCollisionDataFilter} used when detecting collisions for each time step.
	 * <p>
	 * This should always be an instance of a class that extends the {@link PhysicsBodyBroadphaseCollisionDataFilter}
	 * so that the standard filters are retained.
	 * @param filter the filter
	 * @since 3.2.2
	 */
	public void setBroadphaseCollisionDataFilter(BroadphaseCollisionDataFilter<T, E> filter);
	
	/**
	 * Returns the {@link BroadphaseCollisionDataFilter} used when detecting collisions for each time step.
	 * @return {@link BroadphaseCollisionDataFilter}
	 * @since 3.2.2
	 */
	public BroadphaseCollisionDataFilter<T, E> getBroadphaseCollisionDataFilter();
	
	// narrowphase
	
	/**
	 * Sets the narrow-phase collision detection algorithm.
	 * @param narrowphaseDetector the narrow-phase collision detection algorithm
	 * @throws NullPointerException if narrowphaseDetector is null
	 */
	public void setNarrowphaseDetector(NarrowphaseDetector narrowphaseDetector);
	
	/**
	 * Returns the narrow-phase collision detection algorithm.
	 * @return {@link NarrowphaseDetector} the narrow-phase collision detection algorithm
	 */
	public NarrowphaseDetector getNarrowphaseDetector();

	/**
	 * Sets the narrow-phase post processing algorithm.
	 * @param narrowphasePostProcessor the narrow-phase post processing algorithm
	 * @throws NullPointerException if narrowphasePostProcessor is null
	 */
	public void setNarrowphasePostProcessor(NarrowphasePostProcessor narrowphasePostProcessor);
	
	/**
	 * Returns the narrow-phase post processing algorithm.
	 * @return {@link NarrowphasePostProcessor} the narrow-phase post processing algorithm
	 */
	public NarrowphasePostProcessor getNarrowphasePostProcessor();
	
	// manifold
	
	/**
	 * Sets the manifold solver.
	 * @param manifoldSolver the manifold solver
	 * @throws NullPointerException if manifoldSolver is null
	 */
	public void setManifoldSolver(ManifoldSolver manifoldSolver);
	
	/**
	 * Returns the manifold solver.
	 * @return {@link ManifoldSolver} the manifold solver
	 */
	public ManifoldSolver getManifoldSolver();

	// raycast
	
	/**
	 * Sets the raycast detector.
	 * @param raycastDetector the raycast detector
	 * @throws NullPointerException if raycastDetector is null
	 * @since 2.0.0
	 */
	public void setRaycastDetector(RaycastDetector raycastDetector);
	
	/**
	 * Returns the raycast detector.
	 * @return {@link RaycastDetector} the raycast detector
	 * @since 2.0.0
	 */
	public RaycastDetector getRaycastDetector();

	/**
	 * Sets the time of impact detector.
	 * @param timeOfImpactDetector the time of impact detector
	 * @throws NullPointerException if timeOfImpactDetector is null
	 * @since 1.2.0
	 */
	public void setTimeOfImpactDetector(TimeOfImpactDetector timeOfImpactDetector);
	
	/**
	 * Returns the time of impact detector.
	 * @return {@link TimeOfImpactDetector} the time of impact detector
	 * @since 1.2.0
	 */
	public TimeOfImpactDetector getTimeOfImpactDetector();
	
	// collision data
	
	/**
	 * Returns the collision data for the given body-fixture pairs.
	 * <p>
	 * This returns the collision data for any pair that was detected in the broadphase. Use the 
	 * {@link CollisionData#isBroadphaseCollision()}, {@link CollisionData#isManifoldCollision()}, etc.
	 * methods to inspect the type of collision or the details about the collision.
	 * <p>
	 * Returns null if the body-fixtures are not colliding or if either no longer exist in the world.
	 * @param body1 the first body
	 * @param fixture1 the first body's fixture
	 * @param body2 the second body
	 * @param fixture2 the second body's fixture
	 * @return V
	 */
	public V getCollisionData(T body1, E fixture1, T body2, E fixture2);
	
	/**
	 * Returns an iterator that can be used to enumerate all the collisions in this world.
	 * <p>
	 * This returns the collision data for any pair that was detected in the broadphase. Use the 
	 * {@link CollisionData#isBroadphaseCollision()}, {@link CollisionData#isManifoldCollision()}, etc.
	 * methods to inspect the type of collision or the details about the collision.
	 * <p>
	 * NOTE: This iterator does NOT support removal.
	 * @return Iterator&lt;V&gt;
	 */
	public Iterator<V> getCollisionDataIterator();
	
	// AABB Detection
	
	/**
	 * Returns a list of {@link DetectResult}s containing all the body-fixtures that 
	 * overlap with the given {@link AABB} using the current state of the {@link BroadphaseDetector}.
	 * @param aabb the aabb
	 * @param filter the filter; can be null
	 * @return List&lt;{@link DetectResult}&gt;
	 */
	public List<DetectResult<T, E>> detect(AABB aabb, DetectFilter<T, E> filter);
	
	/**
	 * Returns an iterator of {@link DetectResult}s containing all the body-fixtures that 
	 * overlap with the given {@link AABB} using the current state of the {@link BroadphaseDetector}.
	 * <p>
	 * NOTE: The returned {@link DetectResult}s are reused internally. You should call the {@link DetectResult#copy()}
	 * method to create a copy of the result if you need to keep it outside of the iteration.
	 * @param aabb the aabb
	 * @param filter the filter; can be null
	 * @return Iterator&lt;{@link DetectResult}&gt;
	 */
	public Iterator<DetectResult<T, E>> detectIterator(AABB aabb, DetectFilter<T, E> filter);
	
	/**
	 * Returns a list of {@link DetectResult}s containing all the body-fixtures that 
	 * overlap with the given {@link AABB} using the current state of the {@link BroadphaseDetector},
	 * only testing against the given {@link CollisionBody}.
	 * @param aabb the aabb
	 * @param body the body
	 * @param filter the filter; can be null
	 * @return List&lt;{@link DetectResult}&gt;
	 */
	public List<DetectResult<T, E>> detect(AABB aabb, T body, DetectFilter<T, E> filter);
	
	/**
	 * Returns an iterator of {@link DetectResult}s containing all the body-fixtures that 
	 * overlap with the given {@link AABB} using the current state of the {@link BroadphaseDetector},
	 * only testing against the given {@link CollisionBody}.
	 * <p>
	 * NOTE: The returned {@link DetectResult}s are reused internally. You should call the {@link DetectResult#copy()}
	 * method to create a copy of the result if you need to keep it outside of the iteration.
	 * @param aabb the aabb
	 * @param body the body
	 * @param filter the filter; can be null
	 * @return Iterator&lt;{@link DetectResult}&gt;
	 */
	public Iterator<DetectResult<T, E>> detectIterator(AABB aabb, T body, DetectFilter<T, E> filter);
	
	// Convex Detection
	
	/**
	 * Returns a list of {@link ConvexDetectResult}s containing all the body-fixtures that 
	 * overlap with the given {@link Convex} using the current state of the {@link BroadphaseDetector}.
	 * @param convex the convex
	 * @param transform the transform
	 * @param filter the filter; can be null
	 * @return List&lt;{@link ConvexDetectResult}&gt;
	 */
	public List<ConvexDetectResult<T, E>> detect(Convex convex, Transform transform, DetectFilter<T, E> filter);
	
	/**
	 * Returns an iterator of {@link ConvexDetectResult}s containing all the body-fixtures that 
	 * overlapped with the given {@link Convex} using the current state of the {@link BroadphaseDetector}.
	 * @param convex the convex
	 * @param transform the transform
	 * @param filter the filter; can be null
	 * @return Iterator&lt;{@link ConvexDetectResult}&gt;
	 */
	public Iterator<ConvexDetectResult<T, E>> detectIterator(Convex convex, Transform transform, DetectFilter<T, E> filter);
	
	/**
	 * Returns a list of {@link ConvexDetectResult}s containing all the body-fixtures that 
	 * overlapped with the given {@link Convex} using the current state of the {@link BroadphaseDetector},
	 * only testing against the given {@link CollisionBody}.
	 * @param convex the convex
	 * @param transform the transform
	 * @param body the body
	 * @param filter the filter; can be null
	 * @return List&lt;{@link ConvexDetectResult}&gt;
	 */
	public List<ConvexDetectResult<T, E>> detect(Convex convex, Transform transform, T body, DetectFilter<T, E> filter);
	
	/**
	 * Returns an iterator of {@link ConvexDetectResult}s containing all the body-fixtures that 
	 * overlapped with the given {@link Convex} using the current state of the {@link BroadphaseDetector},
	 * only testing against the given {@link CollisionBody}.
	 * @param convex the convex
	 * @param transform the transform
	 * @param body the body
	 * @param filter the filter; can be null
	 * @return Iterator&lt;{@link ConvexDetectResult}&gt;
	 */
	public Iterator<ConvexDetectResult<T, E>> detectIterator(Convex convex, Transform transform, T body, DetectFilter<T, E> filter);
	
	// raycast

	/**
	 * Returns a list of {@link RaycastResult}s containing all the body-fixtures that 
	 * overlap with the given {@link Ray} using the current state of the {@link BroadphaseDetector}.
	 * <p>
	 * If the ray begins inside a fixture, that fixture will not be included in the results.
	 * @param ray the ray
	 * @param maxLength the max length of the ray; any value less than or equal to zero represents infinite length
	 * @param filter the filter; can be null
	 * @return List&lt;{@link RaycastResult}&gt;
	 */
	public List<RaycastResult<T, E>> raycast(Ray ray, double maxLength, DetectFilter<T, E> filter);
	
	/**
	 * Returns an iterator of {@link RaycastResult}s containing all the body-fixtures that 
	 * overlap with the given {@link Ray} using the current state of the {@link BroadphaseDetector}.
	 * <p>
	 * If the ray begins inside a fixture, that fixture will not be included in the results.
	 * @param ray the ray
	 * @param maxLength the max length of the ray; any value less than or equal to zero represents infinite length
	 * @param filter the filter; can be null
	 * @return Iterator&lt;{@link RaycastResult}&gt;
	 */
	public Iterator<RaycastResult<T, E>> raycastIterator(Ray ray, double maxLength, DetectFilter<T, E> filter);

	/**
	 * Returns a list of {@link RaycastResult}s containing all the body-fixtures that 
	 * overlap with the given {@link Ray} only testing against the given {@link CollisionBody}.
	 * <p>
	 * This method does not use the current state of the {@link BroadphaseDetector} and instead tests the
	 * given body's fixtures directly.
	 * <p>
	 * If the ray begins inside a fixture, that fixture will not be included in the results.
	 * @param ray the ray
	 * @param maxLength the max length of the ray; any value less than or equal to zero represents infinite length
	 * @param body the body
	 * @param filter the filter; can be null
	 * @return List&lt;{@link RaycastResult}&gt;
	 */
	public List<RaycastResult<T, E>> raycast(Ray ray, double maxLength, T body, DetectFilter<T, E> filter);
	
	/**
	 * Returns an iterator of {@link RaycastResult}s containing all the body-fixtures that 
	 * overlap with the given {@link Ray} only testing against the given {@link CollisionBody}.
	 * <p>
	 * This method does not use the current state of the {@link BroadphaseDetector} and instead tests the
	 * given body's fixtures directly.
	 * <p>
	 * If the ray begins inside a fixture, that fixture will not be included in the results.
	 * @param ray the ray
	 * @param maxLength the max length of the ray; any value less than or equal to zero represents infinite length
	 * @param body the body
	 * @param filter the filter; can be null
	 * @return Iterator&lt;{@link RaycastResult}&gt;
	 */
	public Iterator<RaycastResult<T, E>> raycastIterator(Ray ray, double maxLength, T body, DetectFilter<T, E> filter);
	
	/**
	 * Returns the closest {@link RaycastResult} that overlaps with the given {@link Ray} 
	 * using the current state of the {@link BroadphaseDetector}.
	 * <p>
	 * The closest is defined as the closest intersection of the ray and a fixture to the 
	 * ray's start point.
	 * <p>
	 * If the ray begins inside a fixture, that fixture will not be included in the results.
	 * @param ray the ray
	 * @param maxLength the max length of the ray; any value less than or equal to zero represents infinite length
	 * @param filter the filter; can be null
	 * @return {@link RaycastResult}
	 */
	public RaycastResult<T, E> raycastClosest(Ray ray, double maxLength, DetectFilter<T, E> filter);
	
	/**
	 * Returns the closest {@link RaycastResult} that overlaps with the given {@link Ray} 
	 * only testing against the given {@link CollisionBody}.
	 * <p>
	 * The closest is defined as the closest intersection of the ray and a fixture to the 
	 * ray's start point.
	 * <p>
	 * This method does not use the current state of the {@link BroadphaseDetector} and instead tests the
	 * given body's fixtures directly.
	 * <p>
	 * If the ray begins inside a fixture, that fixture will not be included in the results.
	 * @param ray the ray
	 * @param maxLength the max length of the ray; any value less than or equal to zero represents infinite length
	 * @param body the body
	 * @param filter the filter; can be null
	 * @return {@link RaycastResult}
	 */
	public RaycastResult<T, E> raycastClosest(Ray ray, double maxLength, T body, DetectFilter<T, E> filter);
	
	// convex cast
	
	/**
	 * Returns a list of {@link ConvexCastResult}s containing all the body-fixtures that 
	 * overlap with the given {@link Convex} over the given deltaPosition and deltaAngle
	 * using the current state of the {@link BroadphaseDetector}.
	 * <p>
	 * The deltaPosition and deltaAngle parameters define the cast of the convex. Imagine moving
	 * the given convex from it's current transform position to the given position and rotating
	 * the given convex from it's current transform rotation to the given rotation. This method detects
	 * if there was any collisions between these start and end states.
	 * <p>
	 * This method assumes linear motion, meaning that the direction of motion is in the same direction
	 * for the start and end states as defined by the deltaPosition parameter.
	 * @param convex the convex
	 * @param transform the transform
	 * @param deltaPosition the change in position of the convex
	 * @param deltaAngle the change in rotation of the convex
	 * @param filter the filter; can be null
	 * @return List&lt;{@link ConvexCastResult}&gt;
	 */
	public List<ConvexCastResult<T, E>> convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, DetectFilter<T, E> filter);
	
	/**
	 * Returns an iterator of {@link ConvexCastResult}s containing all the body-fixtures that 
	 * overlap with the given {@link Convex} over the given deltaPosition and deltaAngle
	 * using the current state of the {@link BroadphaseDetector}.
	 * <p>
	 * The deltaPosition and deltaAngle parameters define the cast of the convex. Imagine moving
	 * the given convex from it's current transform position to the given position and rotating
	 * the given convex from it's current transform rotation to the given rotation. This method detects
	 * if there was any collisions between these start and end states.
	 * <p>
	 * This method assumes linear motion, meaning that the direction of motion is in the same direction
	 * for the start and end states as defined by the deltaPosition parameter.
	 * @param convex the convex
	 * @param transform the transform
	 * @param deltaPosition the change in position of the convex
	 * @param deltaAngle the change in rotation of the convex
	 * @param filter the filter; can be null
	 * @return Iterator&lt;{@link ConvexCastResult}&gt;
	 */
	public Iterator<ConvexCastResult<T, E>> convexCastIterator(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, DetectFilter<T, E> filter);
	
	/**
	 * Returns the closest {@link ConvexCastResult} that overlaps with the given {@link Convex} 
	 * over the given deltaPosition and deltaAngle using the current state of the {@link BroadphaseDetector}.
	 * <p>
	 * The closest is defined as the closest intersection from the initial position/rotation of
	 * the given convex.
	 * <p>
	 * The deltaPosition and deltaAngle parameters define the cast of the convex. Imagine moving
	 * the given convex from it's current transform position to the given position and rotating
	 * the given convex from it's current transform rotation to the given rotation. This method detects
	 * if there was any collisions between these start and end states.
	 * <p>
	 * This method assumes linear motion, meaning that the direction of motion is in the same direction
	 * for the start and end states as defined by the deltaPosition parameter.
	 * @param convex the convex
	 * @param transform the transform
	 * @param deltaPosition the change in position of the convex
	 * @param deltaAngle the change in rotation of the convex
	 * @param filter the filter; can be null
	 * @return {@link ConvexCastResult}
	 */
	public ConvexCastResult<T, E> convexCastClosest(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, DetectFilter<T, E> filter);
	
	/**
	 * Returns the closest {@link ConvexCastResult} that overlaps with the given {@link Convex} 
	 * over the given deltaPosition and deltaAngle only testing against the given {@link CollisionBody}.
	 * <p>
	 * The closest is defined as the closest intersection from the initial position/rotation of
	 * the given convex.
	 * <p>
	 * The deltaPosition and deltaAngle parameters define the cast of the convex. Imagine moving
	 * the given convex from it's current transform position to the given position and rotating
	 * the given convex from it's current transform rotation to the given rotation. This method detects
	 * if there was any collisions between these start and end states.
	 * <p>
	 * This method assumes linear motion, meaning that the direction of motion is in the same direction
	 * for the start and end states as defined by the deltaPosition parameter.
	 * @param convex the convex
	 * @param transform the transform
	 * @param deltaPosition the change in position of the convex
	 * @param deltaAngle the change in rotation of the convex
	 * @param body the body
	 * @param filter the filter; can be null
	 * @return {@link ConvexCastResult}
	 */
	public ConvexCastResult<T, E> convexCastClosest(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, T body, DetectFilter<T, E> filter);
}
