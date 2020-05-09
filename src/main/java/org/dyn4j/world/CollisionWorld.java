package org.dyn4j.world;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.RaycastDetector;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.listener.BoundsListener;
import org.dyn4j.world.listener.CollisionListener;
import org.dyn4j.world.listener.DestructionListener;
import org.dyn4j.world.result.ConvexCastResult;
import org.dyn4j.world.result.ConvexDetectResult;
import org.dyn4j.world.result.DetectResult;
import org.dyn4j.world.result.RaycastResult;

public interface CollisionWorld<T extends CollisionBody<E>, E extends Fixture, V extends CollisionData<T, E>> extends Shiftable {
	/** The default {@link CollisionBody} count */
	public static final int DEFAULT_BODY_COUNT = 32;
	
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
	 * <p>
	 * Use the {@link #removeBody(int, boolean)} method to enable implicit
	 * destruction notification.
	 * @param index the index of the body to remove.
	 * @return boolean true if the body was removed
	 * @since 3.2.0
	 */
	public boolean removeBody(int index);

	/**
	 * Removes the {@link CollisionBody} at the given index from this {@link CollisionWorld}.
	 * <p>
	 * When a body is removed, joints and contacts may be implicitly destroyed.
	 * Pass true to the notify parameter to be notified of the destruction of these objects
	 * via the {@link DestructionListener}s.
	 * <p>
	 * This method does not trigger {@link ContactListener#end(ContactPoint)} events
	 * for the contacts that are being removed.
	 * @param index the index of the body to remove.
	 * @param notify true if implicit destruction should be notified
	 * @return boolean true if the body was removed
	 * @since 3.2.0
	 */
	public boolean removeBody(int index, boolean notify);
	
	/**
	 * Removes the given {@link CollisionBody} from this {@link CollisionWorld}.
	 * <p>
	 * Use the {@link #removeBody(CollisionBody, boolean)} method to enable implicit
	 * destruction notification.
	 * @param body the {@link CollisionBody} to remove.
	 * @return boolean true if the body was removed
	 */
	public boolean removeBody(T body);
	
	/**
	 * Removes the given {@link CollisionBody} from this {@link CollisionWorld}.
	 * <p>
	 * When a body is removed, joints and contacts may be implicitly destroyed.
	 * Pass true to the notify parameter to be notified of the destruction of these objects
	 * via the {@link DestructionListener}s.
	 * <p>
	 * This method does not trigger {@link ContactListener#end(ContactPoint)} events
	 * for the contacts that are being removed.
	 * @param body the {@link CollisionBody} to remove
	 * @param notify true if implicit destruction should be notified
	 * @return boolean true if the body was removed
	 * @since 3.1.1
	 */
	public boolean removeBody(T body, boolean notify);

	/**
	 * This is a convenience method for the {@link #removeAllBodiesAndJoints()} method since all joints will be removed
	 * when all bodies are removed anyway.
	 * <p>
	 * This method does not notify of the destroyed contacts, joints, etc.
	 * @see #removeAllBodies(boolean)
	 * @since 3.0.1
	 */
	public void removeAllBodies();
	
	/**
	 * This is a convenience method for the {@link #removeAllBodiesAndJoints(boolean)} method since all joints will be removed
	 * when all bodies are removed anyway.
	 * @param notify true if destruction of joints and contacts should be notified of by the {@link DestructionListener}
	 * @since 3.0.1
	 */
	public void removeAllBodies(boolean notify);

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
	
	public List<CollisionListener<T, E>> getCollisionListeners();
	
	public List<BoundsListener<T, E>> getBoundsListeners();
	
	// broadphase
	
	/**
	 * Sets the broad-phase collision detection algorithm.
	 * @param broadphaseDetector the broad-phase collision detection algorithm
	 * @throws NullPointerException if broadphaseDetector is null
	 */
	public void setBroadphaseDetector(BroadphaseDetector<T, E> broadphaseDetector);
	
	/**
	 * Returns the broad-phase collision detection algorithm.
	 * @return {@link BroadphaseDetector} the broad-phase collision detection algorithm
	 */
	public BroadphaseDetector<T, E> getBroadphaseDetector();
	
	/**
	 * Sets the {@link BroadphaseFilter} used when detecting collisions for each time step.
	 * <p>
	 * This should always be an instance of a class that extends the {@link DetectBroadphaseFilter}
	 * so that the standard filters are retained.
	 * @param filter the filter
	 * @since 3.2.2
	 */
	public void setDetectBroadphaseFilter(BroadphaseFilter<T, E> filter);
	
	/**
	 * Returns the {@link BroadphaseFilter} used when detecting collisions for each time step.
	 * @return {@link BroadphaseFilter}
	 * @since 3.2.2
	 */
	public BroadphaseFilter<T, E> getDetectBroadphaseFilter();
	
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
	
	public Collection<V> getCollisionData();
	public Iterator<V> getCollisionDataIterator();
	
	// TODO the only thing i don't like about this is that there's so much generics and the caller will have to deal with them as well
	
	// AABB Detection
	
	public List<DetectResult<T, E>> detect(AABB aabb, DetectFilter<T, E> filter);
	public Iterator<DetectResult<T, E>> detectIterator(AABB aabb, DetectFilter<T, E> filter);
	public List<DetectResult<T, E>> detect(AABB aabb, T body, DetectFilter<T, E> filter);
	public Iterator<DetectResult<T, E>> detectIterator(AABB aabb, T body, DetectFilter<T, E> filter);
	
	// Convex Detection
	
	public List<ConvexDetectResult<T, E>> detect(Convex convex, Transform transform, DetectFilter<T, E> filter);
	public Iterator<ConvexDetectResult<T, E>> detectIterator(Convex convex, Transform transform, DetectFilter<T, E> filter);
	public List<ConvexDetectResult<T, E>> detect(Convex convex, Transform transform, T body, DetectFilter<T, E> filter);
	public Iterator<ConvexDetectResult<T, E>> detectIterator(Convex convex, Transform transform, T body, DetectFilter<T, E> filter);
	
	// raycast

	public List<RaycastResult<T, E>> raycast(Ray ray, double maxLength, DetectFilter<T, E> filter);
	public Iterator<RaycastResult<T, E>> raycastIterator(Ray ray, double maxLength, DetectFilter<T, E> filter);
	public RaycastResult<T, E> raycastClosest(Ray ray, double maxLength, DetectFilter<T, E> filter);
	public RaycastResult<T, E> raycast(Ray ray, double maxLength, T body, DetectFilter<T, E> filter);
	
	// convex cast
	
	public List<ConvexCastResult<T, E>> convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, DetectFilter<T, E> filter);
	public Iterator<ConvexCastResult<T, E>> convexCastIterator(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, DetectFilter<T, E> filter);
	public ConvexCastResult<T, E> convexCastClosest(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, DetectFilter<T, E> filter);
	public ConvexCastResult<T, E> convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, T body, DetectFilter<T, E> filter);
}
