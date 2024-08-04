/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.dyn4j.DataContainer;
import org.dyn4j.collision.BasicCollisionItem;
import org.dyn4j.collision.BasicCollisionPair;
import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Collisions;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.FixtureModificationHandler;
import org.dyn4j.collision.broadphase.AABBExpansionMethod;
import org.dyn4j.collision.broadphase.AABBProducer;
import org.dyn4j.collision.broadphase.CollisionItemBroadphaseDetector;
import org.dyn4j.collision.broadphase.CollisionItemBroadphaseDetectorAdapter;
import org.dyn4j.collision.broadphase.CollisionItemBroadphaseFilter;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.BroadphaseFilter;
import org.dyn4j.collision.broadphase.CollisionItemAABBProducer;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.broadphase.StaticValueAABBExpansionMethod;
import org.dyn4j.collision.continuous.ConservativeAdvancement;
import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.LinkPostProcessor;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.NarrowphasePostProcessor;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.collision.narrowphase.Raycast;
import org.dyn4j.collision.narrowphase.RaycastDetector;
import org.dyn4j.dynamics.Body;
import org.dyn4j.exception.ArgumentNullException;
import org.dyn4j.exception.ObjectAlreadyExistsException;
import org.dyn4j.exception.ObjectAlreadyOwnedException;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Link;
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
 * Abstract implementation of the {@link CollisionWorld} interface.
 * <p>
 * Implements the basic wiring for a collision <i>detection</i> pipeline. Extenders are expected
 * to implement the {@link #processCollisions(Iterator)} and {@link #createCollisionData(CollisionPair)}
 * methods. It's expected that extenders would fully enumerate the Iterator given in the
 * {@link #processCollisions(Iterator)} method to ensure a detection cycle is fully completed.
 * <p>
 * Extenders should call the {@link #detect()} method to initiate the collision detection pipeline.
 * Calling the {@link #detect()} will call the {@link #processCollisions(Iterator)} method to perform
 * additional processing on the collisions found.
 * <p>
 * At a high-level this class handles the following pipeline activities:
 * <ol>
 * <li>(out of) Bounds detection (optional)
 * <li>Broad-phase collision detection
 * <li>Narrow-phase collision detection
 * <li>Narrow-phase post-processing (ex. {@link Link} shapes)
 * <li>Manifold (contact point) generation
 * </ol>
 * <p>
 * <b>NOTE</b>: This class uses the {@link Body#setOwner(Object)} and 
 * {@link Body#setFixtureModificationHandler(org.dyn4j.collision.FixtureModificationHandler)}
 * methods to handle certain scenarios like fixture removal on a body or bodies added to
 * more than one world. Callers should <b>NOT</b> use the methods.
 * @author William Bittle
 * @version 6.0.0
 * @since 4.0.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 * @param <V> the {@link CollisionData} type
 * @see CollisionWorld
 */
public abstract class AbstractCollisionWorld<T extends CollisionBody<E>, E extends Fixture, V extends CollisionData<T, E>> implements CollisionWorld<T, E, V>, Shiftable, DataContainer {
	
	/** The user data */
	protected Object userData;
	
	// algorithms
	
	/** The world {@link Bounds} */
	protected Bounds bounds;
	
	/** The {@link BroadphaseDetector} */
	protected CollisionItemBroadphaseDetector<T, E> broadphaseDetector;
	
	/** The {@link BroadphaseCollisionDataFilter} for detection */
	protected BroadphaseCollisionDataFilter<T, E> broadphaseFilter;
	
	/** The {@link NarrowphaseDetector} */
	protected NarrowphaseDetector narrowphaseDetector;
	
	/** The {@link NarrowphasePostProcessor} */
	protected NarrowphasePostProcessor narrowphasePostProcessor;
	
	/** The {@link ManifoldSolver} */
	protected ManifoldSolver manifoldSolver;
	
	/** The {@link RaycastDetector} */
	protected RaycastDetector raycastDetector;

	/** The {@link TimeOfImpactDetector} */
	protected TimeOfImpactDetector timeOfImpactDetector;
	
	// members
	
	/** The list of all bodies in the world */
	protected final List<T> bodies;
	
	/** An unmodifiable view of the bodies */
	protected final List<T> bodiesUnmodifiable;
	
	// collision tracking
	
	/** 
	 * The full set of tracked collision data
	 * <p>
	 * NOTE: This collection could contain collisions for bodies or fixtures that no longer
	 * exist in the world. Using the {@link #getCollisionDataIterator()} filters those out
	 * automatically if reading the collision is needed. 
	 */
	protected final Map<CollisionPair<CollisionItem<T, E>>, V> collisionData;
	
	// listeners
	
	/** The collision listeners */
	protected final List<CollisionListener<T, E>> collisionListeners;

	/** The collision listeners (unmodifiable view) */
	protected final List<CollisionListener<T, E>> collisionListenersUnmodifiable;
	
	/** The bounds listeners */
	protected final List<BoundsListener<T, E>> boundsListeners;
	
	/** The bounds listeners (unmodifiable view) */
	protected final List<BoundsListener<T, E>> boundsListenersUnmodifiable;
	
	/**
	 * Default constructor.
	 * <p>
	 * Uses the {@link CollisionWorld#DEFAULT_INITIAL_BODY_CAPACITY} as the initial capacity.
	 */
	public AbstractCollisionWorld() {
		this(DEFAULT_INITIAL_BODY_CAPACITY);
	}
	
	/**
	 * Optional constructor.
	 * @param initialBodyCapacity the default initial body capacity
	 */
	public AbstractCollisionWorld(int initialBodyCapacity) {
		if (initialBodyCapacity <= 0) {
			initialBodyCapacity = DEFAULT_INITIAL_BODY_CAPACITY;
		}
		
		this.bounds = null;
		
		// build the broadphase detector
		final BroadphaseFilter<CollisionItem<T, E>> broadphaseFilter = new CollisionItemBroadphaseFilter<T, E>();
		final AABBProducer<CollisionItem<T, E>> aabbProducer = new CollisionItemAABBProducer<T, E>();
		final AABBExpansionMethod<CollisionItem<T, E>> expansionMethod = new StaticValueAABBExpansionMethod<CollisionItem<T, E>>(0.2);
		final BroadphaseDetector<CollisionItem<T, E>> broadphase = new DynamicAABBTree<CollisionItem<T,E>>(
				broadphaseFilter,
				aabbProducer, 
				expansionMethod, 
				initialBodyCapacity);
		this.broadphaseDetector = new CollisionItemBroadphaseDetectorAdapter<T, E>(broadphase);
		this.broadphaseDetector.setUpdateTrackingEnabled(true);
		
		this.broadphaseFilter = new CollisionBodyBroadphaseCollisionDataFilter<T, E>();
		this.narrowphaseDetector = new Gjk();
		this.narrowphasePostProcessor = new LinkPostProcessor();
		this.manifoldSolver = new ClippingManifoldSolver();
		this.raycastDetector = new Gjk();
		this.timeOfImpactDetector = new ConservativeAdvancement();
		
		this.bodies = new ArrayList<T>(initialBodyCapacity);
		this.bodiesUnmodifiable = Collections.unmodifiableList(this.bodies);
		
		this.collisionData = new LinkedHashMap<CollisionPair<CollisionItem<T, E>>, V>(Collisions.getEstimatedCollisionPairs(initialBodyCapacity));
		
		this.collisionListeners = new ArrayList<CollisionListener<T,E>>(10);
		this.boundsListeners = new ArrayList<BoundsListener<T,E>>(10);
		this.boundsListenersUnmodifiable = Collections.unmodifiableList(this.boundsListeners);
		this.collisionListenersUnmodifiable = Collections.unmodifiableList(this.collisionListeners);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#addBody(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public void addBody(T body) {
		// check for null body
		if (body == null) 
			throw new ArgumentNullException("body");
		
		// dont allow adding it twice
		if (body.getOwner() == this) 
			throw new ObjectAlreadyExistsException("body", body, body.getOwner());
		
		// dont allow a body that already is assigned to another world
		if (body.getOwner() != null) 
			throw new ObjectAlreadyOwnedException("body", body, body.getOwner());
		
		// add it to the world
		this.bodies.add(body);
		// set the world property on the body
		body.setFixtureModificationHandler(new BodyModificationHandler(body));
		body.setOwner(this);
		// set the previous transform to the current transform
		body.getPreviousTransform().set(body.getTransform());
		// add it to the broadphase
		this.broadphaseDetector.add(body);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#removeAllBodies()
	 */
	@Override
	public void removeAllBodies() {
		int bsize = this.bodies.size();
		for (int i = 0; i < bsize; i++) {
			// get the body
			T body = this.bodies.get(i);
			// set the world property to null
			body.setFixtureModificationHandler(null);
			body.setOwner(null);
		}
		
		this.bodies.clear();
		this.broadphaseDetector.clear();
		this.collisionData.clear();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#containsBody(org.dyn4j.collision.CollisionBody)
	 */
	public boolean containsBody(T body) {
		return this.bodies.contains(body);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#removeBody(int)
	 */
	public boolean removeBody(int index) {
		T body = this.bodies.get(index);
		return this.removeBody(body);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#removeBody(org.dyn4j.collision.CollisionBody)
	 */
	public boolean removeBody(T body) {
		// remove the body from the list
		boolean removed = this.bodies.remove(body);
		
		// only remove joints and contacts if the body was removed
		if (removed) {
			// set the world property to null
			body.setFixtureModificationHandler(null);
			body.setOwner(null);
			
			// remove the body from the broadphase
			this.broadphaseDetector.remove(body);
			
			// NOTE: I've opted to remove any collision data in the next collision 
			// detection phase. The effect is that users of the stored collisionData need 
			// to understand that the data stored there could include collision information 
			// for bodies that no longer exist in the world. The alternative is to iterate 
			// the entire set of pairs checking for this body - which isn't particularly
			// efficient.
		}
		
		return removed;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getBodies()
	 */
	@Override
	public List<T> getBodies() {
		return this.bodiesUnmodifiable;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getBody(int)
	 */
	@Override
	public T getBody(int index) {
		return this.bodies.get(index);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getBodyCount()
	 */
	@Override
	public int getBodyCount() {
		return this.bodies.size();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.bodies.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getBodyIterator()
	 */
	@Override
	public Iterator<T> getBodyIterator() {
		return new BodyIterator();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#setBounds(org.dyn4j.collision.Bounds)
	 */
	@Override
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getBounds()
	 */
	@Override
	public Bounds getBounds() {
		return this.bounds;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#setBroadphaseDetector(org.dyn4j.collision.broadphase.BroadphaseDetector)
	 */
	@Override
	public void setBroadphaseDetector(CollisionItemBroadphaseDetector<T, E> broadphaseDetector) {
		if (broadphaseDetector == null) 
			throw new ArgumentNullException("broadphaseDetector");
		
		// set the new broadphase
		this.broadphaseDetector = broadphaseDetector;
		
		// re-add all bodies to the broadphase
		int size = this.bodies.size();
		for (int i = 0; i < size; i++) {
			T body = this.bodies.get(i);
			this.broadphaseDetector.add(body);
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getBroadphaseDetector()
	 */
	@Override
	public CollisionItemBroadphaseDetector<T, E> getBroadphaseDetector() {
		return this.broadphaseDetector;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getBroadphaseCollisionDataFilter()
	 */
	@Override
	public BroadphaseCollisionDataFilter<T, E> getBroadphaseCollisionDataFilter() {
		return this.broadphaseFilter;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#setBroadphaseCollisionDataFilter(org.dyn4j.world.BroadphaseCollisionDataFilter)
	 */
	@Override
	public void setBroadphaseCollisionDataFilter(BroadphaseCollisionDataFilter<T, E> filter) {
		if (filter == null) 
			throw new ArgumentNullException("filter");
		
		this.broadphaseFilter = filter;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#setNarrowphaseDetector(org.dyn4j.collision.narrowphase.NarrowphaseDetector)
	 */
	@Override
	public void setNarrowphaseDetector(NarrowphaseDetector narrowphaseDetector) {
		if (narrowphaseDetector == null) 
			throw new ArgumentNullException("narrowphaseDetector");
		
		this.narrowphaseDetector = narrowphaseDetector;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getNarrowphaseDetector()
	 */
	@Override
	public NarrowphaseDetector getNarrowphaseDetector() {
		return this.narrowphaseDetector;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#setNarrowphasePostProcessor(org.dyn4j.collision.narrowphase.NarrowphasePostProcessor)
	 */
	@Override
	public void setNarrowphasePostProcessor(NarrowphasePostProcessor narrowphasePostProcessor) {
		if (narrowphasePostProcessor == null) 
			throw new ArgumentNullException("narrowphasePostProcessor");
		
		this.narrowphasePostProcessor = narrowphasePostProcessor;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getNarrowphasePostProcessor()
	 */
	@Override
	public NarrowphasePostProcessor getNarrowphasePostProcessor() {
		return this.narrowphasePostProcessor;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#setManifoldSolver(org.dyn4j.collision.manifold.ManifoldSolver)
	 */
	@Override
	public void setManifoldSolver(ManifoldSolver manifoldSolver) {
		if (manifoldSolver == null) 
			throw new ArgumentNullException("manifoldSolver");
		
		this.manifoldSolver = manifoldSolver;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getManifoldSolver()
	 */
	@Override
	public ManifoldSolver getManifoldSolver() {
		return this.manifoldSolver;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#setRaycastDetector(org.dyn4j.collision.narrowphase.RaycastDetector)
	 */
	@Override
	public void setRaycastDetector(RaycastDetector raycastDetector) {
		if (raycastDetector == null) 
			throw new ArgumentNullException("raycastDetector");
		
		this.raycastDetector = raycastDetector;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getRaycastDetector()
	 */
	@Override
	public RaycastDetector getRaycastDetector() {
		return this.raycastDetector;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#setTimeOfImpactDetector(org.dyn4j.collision.continuous.TimeOfImpactDetector)
	 */
	@Override
	public void setTimeOfImpactDetector(TimeOfImpactDetector timeOfImpactDetector) {
		if (timeOfImpactDetector == null) 
			throw new ArgumentNullException("timeOfImpactDetector");
		
		this.timeOfImpactDetector = timeOfImpactDetector;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getTimeOfImpactDetector()
	 */
	@Override
	public TimeOfImpactDetector getTimeOfImpactDetector() {
		return this.timeOfImpactDetector;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		// update the broadphase
		this.broadphaseDetector.shift(shift);
		
		// update the bounds
		if (this.bounds != null) {
			this.bounds.shift(shift);
		}
		
		// update the bodies
		int bSize = this.bodies.size();
		for (int i = 0; i < bSize; i++) {
			T body = this.bodies.get(i);
			body.shift(shift);
		}
		
		// update the cached data
		for (V item : this.collisionData.values()) {
			if (item.isManifoldCollision()) {
				item.shift(shift);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getCollisionDataIterator()
	 */
	@Override
	public Iterator<V> getCollisionDataIterator() {
		return new CollisionDataIterator();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getCollisionData(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture, org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public V getCollisionData(T body1, E fixture1, T body2, E fixture2) {
		if (body1 == null || body2 == null || fixture1 == null || fixture2 == null) return null;
		
		CollisionItemAdapter<T, E> item = new CollisionItemAdapter<T, E>();
		
		// makes sure the body and fixture are still part of this world
		item.set(body1, fixture1);
		if (!this.broadphaseDetector.contains(body1, fixture1)) {
			return null;
		}

		// makes sure the body and fixture are still part of this world
		item.set(body2, fixture2);
		if (!this.broadphaseDetector.contains(body2, fixture2)) {
			return null;
		}
		
		CollisionPair<CollisionItem<T, E>> pair = new BasicCollisionPair<CollisionItem<T, E>>(
				new BasicCollisionItem<T, E>(body1, fixture1),
				new BasicCollisionItem<T, E>(body2, fixture2));
		
		return this.collisionData.get(pair);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getBoundsListeners()
	 */
	@Override
	public List<BoundsListener<T, E>> getBoundsListeners() {
		return this.boundsListenersUnmodifiable;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#getCollisionListeners()
	 */
	@Override
	public List<CollisionListener<T, E>> getCollisionListeners() {
		return this.collisionListenersUnmodifiable;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#removeAllListeners()
	 */
	@Override
	public void removeAllListeners() {
		this.boundsListeners.clear();
		this.collisionListeners.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#removeAllBoundsListeners()
	 */
	@Override
	public void removeAllBoundsListeners() {
		this.boundsListeners.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#removeAllCollisionListeners()
	 */
	@Override
	public void removeAllCollisionListeners() {
		this.collisionListeners.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#removeBoundsListener(org.dyn4j.world.listener.BoundsListener)
	 */
	@Override
	public boolean removeBoundsListener(BoundsListener<T, E> listener) {
		return this.boundsListeners.remove(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#removeCollisionListener(org.dyn4j.world.listener.CollisionListener)
	 */
	@Override
	public boolean removeCollisionListener(CollisionListener<T, E> listener) {
		return this.collisionListeners.remove(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#addListener(org.dyn4j.world.listener.BoundsListener)
	 */
	@Override
	public boolean addBoundsListener(BoundsListener<T, E> listener) {
		return this.boundsListeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#addListener(org.dyn4j.world.listener.CollisionListener)
	 */
	@Override
	public boolean addCollisionListener(CollisionListener<T, E> listener) {
		return this.collisionListeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.DataContainer#getUserData()
	 */
	@Override
	public Object getUserData() {
		return this.userData;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.DataContainer#setUserData(java.lang.Object)
	 */
	@Override
	public void setUserData(Object data) {
		this.userData = data;
	}
	
	/**
	 * Performs collision detection on the world.
	 * <p>
	 * Implement the {@link #processCollisions(Iterator)} method to get access
	 * to the generated collision information.
	 */
	protected void detect() {
		// get the bounds listeners
		List<BoundsListener<T, E>> boundsListeners = this.boundsListeners;
		
		int blSize = boundsListeners.size();
		int bSize = this.bodies.size();
		
		// update all fixtures in the broadphase
		this.broadphaseDetector.update();
		
		// update all AABBs in the broadphase
		CollisionItemAdapter<T, E> bAdapter = new CollisionItemAdapter<T, E>();
		for (int i = 0; i < bSize; i++) {
			T body = this.bodies.get(i);
			Transform tx = body.getTransform();
			
			// skip if already not active
			if (!body.isEnabled()) continue;
			
			// instead of building an AABB for the whole body, let's check
			// each fixture AABB so that we can exit early (in most cases
			// one fixture will be within bounds). This also saves an allocation
			if (this.bounds != null) {
				boolean withinBounds = false;
				
				int fSize = body.getFixtureCount();
				for (int k = 0; k < fSize; k++) {
					E fixture = body.getFixture(k);
					bAdapter.set(body, fixture);
					AABB aabb = this.broadphaseDetector.getAABB(bAdapter);
					if (!this.bounds.isOutside(aabb, tx, fixture)) {
						withinBounds = true;
						break;
					}
				}
				
				if (!withinBounds) {
					// set the body to inactive
					body.setEnabled(false);
					// if so, notify via the listeners
					for (int j = 0; j < blSize; j++) {
						BoundsListener<T, E> bl = boundsListeners.get(j);
						bl.outside(body);
					}
				}
			}
		}
		
		// detect broadphase pairs
		Iterator<CollisionPair<CollisionItem<T, E>>> broadphasePairIterator = this.broadphaseDetector.detectIterator();
		while(broadphasePairIterator.hasNext()) {
			// NOTE: since the broadphase reuses the pair object, make sure to make a copy of it
			CollisionPair<CollisionItem<T, E>> pair = broadphasePairIterator.next().copy();
			if (!this.collisionData.containsKey(pair)) {
				this.collisionData.put(pair, this.createCollisionData(pair));
			}
		}
		
		this.processCollisions(new DetectIterator(this.collisionData.values().iterator()));
		
		this.broadphaseDetector.clearUpdates();
	}
	
	/**
	 * Creates a new {@link CollisionData} instance for the given pair.
	 * @param pair the pair
	 * @return V
	 */
	protected abstract V createCollisionData(CollisionPair<CollisionItem<T, E>> pair);
	
	/**
	 * This method should process the collisions returned by the given iterator.
	 * <p>
	 * The given iterator will emit collision data for all collision pairs and it's
	 * the responsibility of the sub class to do something with these.
	 * <p>
	 * At a minimum, sub classes should drain the given iterator:
	 * <pre>
	 * while (iterator.hasNext()) { 
	 * 	iterator.next(); 
	 * }
	 * </pre>
	 * @param iterator the collision iterator
	 */
	protected abstract void processCollisions(Iterator<V> iterator);
	
	/**
	 * Handler for fixture addition.
	 * @param body the body the fixture was added to
	 * @param fixture the fixture that was added
	 */
	protected void handleFixtureAdded(T body, E fixture) {
		this.broadphaseDetector.add(body, fixture);
	}
	
	/**
	 * Handler for fixture removal.
	 * @param body the body the fixture was removed from
	 * @param fixture the fixture that was removed
	 */
	protected void handleFixtureRemoved(T body, E fixture) {
		this.broadphaseDetector.remove(body, fixture);
	}
	
	/**
	 * Handler for all fixture removal.
	 * @param body the body the fixtures were removed from
	 */
	protected void handleAllFixturesRemoved(T body) {
		this.broadphaseDetector.remove(body);
	}
	
	// AABB
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#detect(org.dyn4j.geometry.AABB, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public List<DetectResult<T, E>> detect(AABB aabb, DetectFilter<T, E> filter) {
		List<DetectResult<T, E>> results = new ArrayList<DetectResult<T, E>>();
		
		Iterator<DetectResult<T, E>> iterator = this.detectIterator(aabb, filter);
		while (iterator.hasNext()) {
			results.add(iterator.next().copy());
		}
		
		return results;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#detectIterator(org.dyn4j.geometry.AABB, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public Iterator<DetectResult<T, E>> detectIterator(AABB aabb, DetectFilter<T, E> filter) {
		return new AABBDetectIterator(aabb, filter);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#detect(org.dyn4j.geometry.AABB, org.dyn4j.collision.CollisionBody, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public List<DetectResult<T, E>> detect(AABB aabb, T body, DetectFilter<T, E> filter) {
		List<DetectResult<T, E>> results = new ArrayList<DetectResult<T, E>>();
		
		Iterator<DetectResult<T, E>> iterator = this.detectIterator(aabb, body, filter);
		while (iterator.hasNext()) {
			results.add(iterator.next().copy());
		}
		
		return results;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#detectIterator(org.dyn4j.geometry.AABB, org.dyn4j.collision.CollisionBody, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public Iterator<DetectResult<T, E>> detectIterator(AABB aabb, T body, DetectFilter<T, E> filter) {
		return new AABBBodyDetectIterator(aabb, body, filter);
	}
	
	// convex
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#detect(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public List<ConvexDetectResult<T, E>> detect(Convex convex, Transform transform, DetectFilter<T, E> filter) {
		List<ConvexDetectResult<T, E>> results = new ArrayList<ConvexDetectResult<T, E>>();
		
		Iterator<ConvexDetectResult<T, E>> iterator = this.detectIterator(convex, transform, filter);
		while (iterator.hasNext()) {
			results.add(iterator.next().copy());
		}
		
		return results;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#detectIterator(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public Iterator<ConvexDetectResult<T, E>> detectIterator(Convex convex, Transform transform, DetectFilter<T, E> filter) {
		return new ConvexDetectIterator(convex, transform, filter);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#detect(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.collision.CollisionBody, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public List<ConvexDetectResult<T, E>> detect(Convex convex, Transform transform, T body, DetectFilter<T, E> filter) {
		List<ConvexDetectResult<T, E>> results = new ArrayList<ConvexDetectResult<T, E>>();
		
		Iterator<ConvexDetectResult<T, E>> iterator = this.detectIterator(convex, transform, body, filter);
		while (iterator.hasNext()) {
			results.add(iterator.next().copy());
		}
		
		return results;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#detectIterator(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.collision.CollisionBody, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public Iterator<ConvexDetectResult<T, E>> detectIterator(Convex convex, Transform transform, T body, DetectFilter<T, E> filter) {
		return new ConvexBodyDetectIterator(convex, transform, body, filter);
	}
	
	// raycast
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#raycast(org.dyn4j.geometry.Ray, double, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public List<RaycastResult<T, E>> raycast(Ray ray, double maxLength, DetectFilter<T, E> filter) {
		List<RaycastResult<T, E>> results = new ArrayList<RaycastResult<T, E>>();
		
		Iterator<RaycastResult<T, E>> iterator = this.raycastIterator(ray, maxLength, filter);
		while (iterator.hasNext()) {
			results.add(iterator.next().copy());
		}
		
		return results;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#raycastIterator(org.dyn4j.geometry.Ray, double, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public Iterator<RaycastResult<T, E>> raycastIterator(Ray ray, double maxLength, DetectFilter<T, E> filter) {
		return new RaycastDetectIterator(ray, maxLength, filter);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#raycast(org.dyn4j.geometry.Ray, double, org.dyn4j.collision.CollisionBody, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public List<RaycastResult<T, E>> raycast(Ray ray, double maxLength, T body, DetectFilter<T, E> filter) {
		List<RaycastResult<T, E>> results = new ArrayList<RaycastResult<T, E>>();
		
		Iterator<RaycastResult<T, E>> iterator = this.raycastIterator(ray, maxLength, body, filter);
		while (iterator.hasNext()) {
			results.add(iterator.next().copy());
		}
		
		return results;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#raycastIterator(org.dyn4j.geometry.Ray, double, org.dyn4j.collision.CollisionBody, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public Iterator<RaycastResult<T, E>> raycastIterator(Ray ray, double maxLength, T body, DetectFilter<T, E> filter) {
		return new RaycastBodyDetectIterator(ray, maxLength, body, filter);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#raycastClosest(org.dyn4j.geometry.Ray, double, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public RaycastResult<T, E> raycastClosest(Ray ray, double maxLength, DetectFilter<T, E> filter) {
		// check for the desired length
		double max = 0.0;
		if (maxLength > 0.0) {
			max = maxLength;
		}
		
		// create a raycast result
		RaycastResult<T, E> result = null;
		Raycast raycast = new Raycast();

		// filter using the broadphase first
		Iterator<CollisionItem<T, E>> iterator = this.broadphaseDetector.raycastIterator(ray, maxLength);
		
		while (iterator.hasNext()) {
			CollisionItem<T, E> item = iterator.next();
			T body = item.getBody();
			E fixture = item.getFixture();
			
			if (!filter.isAllowed(body, fixture)) {
				continue;
			}
			
			// get the convex shape
			Transform transform = body.getTransform();
			Convex convex = fixture.getShape();
			
			// perform the raycast
			if (this.raycastDetector.raycast(ray, max, convex, transform, raycast)) {
				if (result == null) {
					result = new RaycastResult<T, E>();
				}
				
				result.setBody(body);
				result.setFixture(fixture);
				result.setRaycast(raycast);
				
				// we are only looking for the closest so
				// set the new maximum
				max = raycast.getDistance();
			}
		}
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#raycastClosest(org.dyn4j.geometry.Ray, double, org.dyn4j.collision.CollisionBody, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public RaycastResult<T, E> raycastClosest(Ray ray, double maxLength, T body, DetectFilter<T, E> filter) {
		// set the maximum length
		double max = 0.0;
		if (maxLength > 0.0) {
			max = maxLength;
		}
		
		// get the number of fixtures
		int size = body.getFixtureCount();
		// get the body transform
		Transform transform = body.getTransform();
		
		// create a raycast object to store the result
		Raycast raycast = new Raycast();
		RaycastResult<T, E> result = null;
		
		// loop over the fixtures finding the closest one
		for (int i = 0; i < size; i++) {
			// get the fixture
			E fixture = body.getFixture(i);
			
			if (!filter.isAllowed(body, fixture)) {
				continue;
			}
			
			// get the convex shape
			Convex convex = fixture.getShape();
			// perform the raycast
			if (this.raycastDetector.raycast(ray, max, convex, transform, raycast)) {
				if (result == null) {
					result = new RaycastResult<T, E>();
				}
				// if the raycast detected a collision then set the new
				// maximum distance
				max = raycast.getDistance();
				// assign the fixture
				result.setBody(body);
				result.setFixture(fixture);
				result.setRaycast(raycast);
				// the last raycast will always be the minimum raycast
				// flag that we did get a successful raycast
			}
		}
		
		return result;
	}
	
	// convex cast

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#convexCastClosest(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Vector2, double, org.dyn4j.collision.CollisionBody, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public ConvexCastResult<T, E> convexCastClosest(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, T body, DetectFilter<T, E> filter) {
		ConvexCastResult<T, E> result = null;
		
		final Vector2 dp2 = new Vector2();
		double t2 = 1.0;

		// find the minimum time of impact for the given convex
		// and the current body
		int bSize = body.getFixtureCount();
		Transform bodyTransform = body.getTransform();
		
		// loop through all the body fixtures until we find
		// a the fixture that has the smallest time of impact
		for (int i = 0; i < bSize; i++) {
			E fixture = body.getFixture(i);
			
			if (!filter.isAllowed(body, fixture)) {
				continue;
			}
			
			// get the time of impact
			Convex c = fixture.getShape();
			TimeOfImpact toi = new TimeOfImpact();
			// we pass the zero vector and 0 for the change in position and angle for the body
			// since we assume that it is not moving since this is a static test
			if (this.timeOfImpactDetector.getTimeOfImpact(convex, transform, deltaPosition, deltaAngle, c, bodyTransform, dp2, 0.0, 0.0, t2, toi)) {
				// set the new maximum time
				t2 = toi.getTime();
				
				if (result == null) {
					result = new ConvexCastResult<T, E>();
				}
				
				// save the min time of impact
				result.setBody(body);
				result.setFixture(fixture);
				result.setTimeOfImpact(toi);
			}
		}
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#convexCast(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Vector2, double, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public List<ConvexCastResult<T, E>> convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, DetectFilter<T, E> filter) {
		List<ConvexCastResult<T, E>> results = new ArrayList<ConvexCastResult<T, E>>();
		
		Iterator<ConvexCastResult<T, E>> iterator = this.convexCastIterator(convex, transform, deltaPosition, deltaAngle, filter);
		while (iterator.hasNext()) {
			results.add(iterator.next().copy());
		}
		
		return results;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#convexCastClosest(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Vector2, double, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public ConvexCastResult<T, E> convexCastClosest(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, DetectFilter<T, E> filter) {
		// compute a conservative AABB for the motion of the convex
		double radius = convex.getRadius();
		Vector2 startWorldCenter = transform.getTransformed(convex.getCenter());
		AABB startAABB = new AABB(startWorldCenter, radius);
		
		// linearlly interpolate to get the final transform given the
		// change in position and angle
		Transform finalTransform = transform.lerped(deltaPosition, deltaAngle, 1.0);
		// get the end AABB
		Vector2 endWorldCenter = finalTransform.getTransformed(convex.getCenter());
		AABB endAABB = new AABB(endWorldCenter, radius);
		// union the AABBs to get the swept AABB
		AABB aabb = startAABB.getUnion(endAABB);
		
		ConvexCastResult<T, E> min = null;
		final Vector2 dp2 = new Vector2();
		double t2 = 1.0;
		
		// use the broadphase to filter first
		Iterator<CollisionItem<T, E>> iterator = this.broadphaseDetector.detectIterator(aabb);
		// loop over the potential collisions
		while (iterator.hasNext()) {
			CollisionItem<T, E> item = iterator.next();
			T body = item.getBody();
			E fixture = item.getFixture();
			
			if (!filter.isAllowed(body, fixture)) {
				continue;
			}
			
			// only get the minimum fixture
			double ft2 = t2;
			Transform bodyTransform = body.getTransform();
			
			// get the time of impact
			Convex fixtureShape = fixture.getShape();
			TimeOfImpact timeOfImpact = new TimeOfImpact();
			// we pass the zero vector and 0 for the change in position and angle for the body
			// since we assume that it is not moving since this is a static test
			if (this.timeOfImpactDetector.getTimeOfImpact(
					convex, transform, deltaPosition, deltaAngle, 
					fixtureShape, bodyTransform, dp2, 0.0, 
					0.0, ft2, timeOfImpact)) {
				// only save the minimum
				if (min == null || timeOfImpact.getTime() < min.getTimeOfImpact().getTime()) {
					if (min == null) {
						min = new ConvexCastResult<T, E>();
					}
					ft2 = timeOfImpact.getTime();
					min.setBody(body);
					min.setFixture(fixture);
					min.setTimeOfImpact(timeOfImpact);
				}
			}
		}
		
		return min;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionWorld#convexCastIterator(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Vector2, double, org.dyn4j.world.DetectFilter)
	 */
	@Override
	public Iterator<ConvexCastResult<T, E>> convexCastIterator(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, DetectFilter<T, E> filter) {
		return new ConvexCastDetectIterator(convex, transform, deltaPosition, deltaAngle, filter);
	}
	
	// iterators
	
	private final class DetectIterator implements Iterator<V> {
		private final Iterator<V> iterator;
		private final List<CollisionListener<T, E>> listeners;
		private final int clSize;
		
		private final CollisionItemAdapter<T, E> adapter1 = new CollisionItemAdapter<T, E>();
		private final CollisionItemAdapter<T, E> adapter2 = new CollisionItemAdapter<T, E>();
		
		public DetectIterator(Iterator<V> iterator) {
			this.iterator = iterator;
			this.listeners = AbstractCollisionWorld.this.collisionListeners;
			this.clSize = this.listeners.size();
		}
		
		private final boolean isAllowedBroadphase(BroadphaseCollisionData<T, E> data) {
			// if any collision listener returned false then skip this collision
			// we must allow all the listeners to get notified first, then skip
			// the collision
			boolean allow = true;
			for (int j = 0; j < this.clSize; j++) {
				CollisionListener<T, E> cl = this.listeners.get(j);
				if (!cl.collision(data)) {
					allow = false;
				}
			}
			return allow;
		}
		
		private final boolean isAllowedNarrowphase(NarrowphaseCollisionData<T, E> data) {
			// if any collision listener returned false then skip this collision
			// we must allow all the listeners to get notified first, then skip
			// the collision
			boolean allow = true;
			for (int j = 0; j < this.clSize; j++) {
				CollisionListener<T, E> cl = this.listeners.get(j);
				if (!cl.collision(data)) {
					allow = false;
				}
			}
			return allow;
		}
		
		private final boolean isAllowedManifold(ManifoldCollisionData<T, E> data) {
			// if any collision listener returned false then skip this collision
			// we must allow all the listeners to get notified first, then skip
			// the collision
			boolean allow = true;
			for (int j = 0; j < this.clSize; j++) {
				CollisionListener<T, E> cl = this.listeners.get(j);
				if (!cl.collision(data)) {
					allow = false;
				}
			}
			return allow;
		}
		
		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		@Override
		public V next() {
			V collision = this.iterator.next();
			
			// get the bodies/fixtures
			T body1 = collision.getBody1();
			T body2 = collision.getBody2();
			E fixture1 = collision.getFixture1();
			E fixture2 = collision.getFixture2();
			
			collision.reset();
			
			// since the broadphase is a new-overlap-only detection
			// we need to check every item in the stored set of collisions:
			//		1. check if they were updated
			// 		2. if so, then check if their AABBs still overlap
			this.adapter1.set(body1, fixture1);
			this.adapter2.set(body2, fixture2);
			
			// we need to remove the pair if either body/fixture doesn't exist anymore too
			if (!AbstractCollisionWorld.this.broadphaseDetector.contains(this.adapter1) ||
				!AbstractCollisionWorld.this.broadphaseDetector.contains(this.adapter2)) {
				this.iterator.remove();
				return collision;
			}
			
			if (AbstractCollisionWorld.this.broadphaseDetector.isUpdated(this.adapter1) || AbstractCollisionWorld.this.broadphaseDetector.isUpdated(this.adapter2)) {
				// then we need to verify the pair is still valid
				boolean overlaps = AbstractCollisionWorld.this.broadphaseDetector.detect(this.adapter1, this.adapter2);
				if (!overlaps) {
					// remove the collision from the set of collisions
					this.iterator.remove();
					// always report back the collision because we may need to send
					// notifications of "end" contacts
					return collision;
				}
			}
			
			// check broadphase filter conditions
			if (!AbstractCollisionWorld.this.broadphaseFilter.isAllowed(body1, fixture1, body2, fixture2)) {
				return collision;
			}
			
			// check listeners
			if (!this.isAllowedBroadphase(collision)) {
				return collision;
			}
			
			// it's a legit broadphase collision now
			collision.setBroadphaseCollision(true);
			
			// get the body/fixture data needed for the narrowphase
			Transform transform1 = body1.getTransform();
			Transform transform2 = body2.getTransform();
			Convex convex2 = fixture2.getShape();
			Convex convex1 = fixture1.getShape();

			// narrowphase detection
			Penetration penetration = collision.getPenetration();
			if (AbstractCollisionWorld.this.narrowphaseDetector.detect(convex1, transform1, convex2, transform2, penetration)) {
				// check for zero penetration
				if (penetration.getDepth() == 0.0) {
					// this should only happen if numerical error occurs
					return collision;
				}
				
				// perform post processing
				if (AbstractCollisionWorld.this.narrowphasePostProcessor != null) {
					AbstractCollisionWorld.this.narrowphasePostProcessor.process(convex1, transform1, convex2, transform2, penetration);
					// should we continue processing this collision?
					if (penetration.getDepth() == 0.0) {
						// this happens when the process finds a collision that should not
						// be handled. In most cases this will be when an object is sliding
						// across a chain of segments (Links)
						return collision;
					}
				}
				
				// notify of the narrow-phase collision
				if (!isAllowedNarrowphase(collision)) {
					return collision;
				}

				// it's a legit narrowphase collision now
				collision.setNarrowphaseCollision(true);
				
				// if there is penetration then find a contact manifold
				// using the filled in penetration object
				Manifold manifold = collision.getManifold();
				if (AbstractCollisionWorld.this.manifoldSolver.getManifold(penetration, convex1, transform1, convex2, transform2, manifold)) {
					// check for zero points
					if (manifold.getPoints().size() == 0) {
						// this should only happen if numerical error occurs
						return collision;
					}
					
					// notify of the manifold solving result
					if (!isAllowedManifold(collision)) {
						return collision;
					}
					
					// it's a legit manifold collision now
					collision.setManifoldCollision(true);
				}
			}
			
			return collision;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private final class BodyIterator implements Iterator<T> {
		/** The current index */
		private int index;
		
		/** True if the current element has been removed */
		private boolean removed;
		
		/**
		 * Minimal constructor.
		 */
		public BodyIterator() {
			this.index = -1;
			this.removed = false;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.index + 1 < AbstractCollisionWorld.this.bodies.size();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {
			if (this.index + 1 >= AbstractCollisionWorld.this.bodies.size()) {
				throw new IndexOutOfBoundsException();
			}
			try {
				this.index++;
				this.removed = false;
				T body = AbstractCollisionWorld.this.bodies.get(this.index);
				return body;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			if (this.index < 0 || this.removed) {
				throw new IllegalStateException();
			}
			if (this.index >= AbstractCollisionWorld.this.bodies.size()) {
				throw new IndexOutOfBoundsException();
			}
			try {
				AbstractCollisionWorld.this.removeBody(this.index);
				this.index--;
				this.removed = true;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}
	
	private final class AABBDetectIterator implements Iterator<DetectResult<T, E>>  {
		private final DetectFilter<T, E> filter;
		private final Iterator<CollisionItem<T, E>> iterator;
		
		private final DetectResult<T, E> currentResult;
		private final DetectResult<T, E> nextResult;
		private boolean hasNext;
		
		public AABBDetectIterator(AABB aabb, DetectFilter<T, E> filter) {
			this.filter = filter;
			this.iterator = AbstractCollisionWorld.this.broadphaseDetector.detectIterator(aabb);
			
			this.currentResult = new DetectResult<T, E>();
			this.nextResult = new DetectResult<T, E>();
			
			this.hasNext = this.findNext();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.hasNext;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public DetectResult<T, E> next() {
			if (this.hasNext) {
				this.currentResult.set(this.nextResult);
				this.hasNext = this.findNext();
				return this.currentResult;
			}
			throw new NoSuchElementException();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Returns true if there's another item in the iteration and sets the nextItem.
		 * @return boolean
		 */
		private boolean findNext() {
			while (this.iterator.hasNext()) {
				CollisionItem<T, E> item = this.iterator.next();
				
				T body = item.getBody();
				E fixture = item.getFixture();
				
				if (this.filter != null && !this.filter.isAllowed(body, fixture)) {
					continue;
				}
				
				this.nextResult.setBody(body);
				this.nextResult.setFixture(fixture);
				
				return true;
			}
			
			return false;
		}
	}
	
	private final class AABBBodyDetectIterator implements Iterator<DetectResult<T, E>>  {
		private final AABB aabb;
		private final T body;
		private final DetectFilter<T, E> filter;
		private final Iterator<E> iterator;
		
		private final DetectResult<T, E> currentResult;
		private final DetectResult<T, E> nextResult;
		private boolean hasNext;
		
		public AABBBodyDetectIterator(AABB aabb, T body, DetectFilter<T, E> filter) {
			this.aabb = aabb;
			this.body = body;
			this.filter = filter;
			this.iterator = body.getFixtureIterator();
			
			this.currentResult = new DetectResult<T, E>();
			this.nextResult = new DetectResult<T, E>();
			
			this.hasNext = this.findNext();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.hasNext;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public DetectResult<T, E> next() {
			if (this.hasNext) {
				this.currentResult.set(this.nextResult);
				this.hasNext = this.findNext();
				return this.currentResult;
			}
			throw new NoSuchElementException();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Returns true if there's another item in the iteration and sets the nextItem.
		 * @return boolean
		 */
		private boolean findNext() {
			while (this.iterator.hasNext()) {
				E fixture = this.iterator.next();
				
				if (this.filter != null && !this.filter.isAllowed(this.body, fixture)) {
					continue;
				}
				
				AABB aabb = AbstractCollisionWorld.this.broadphaseDetector.getAABB(this.body, fixture);
				
				if (this.aabb.overlaps(aabb)) {
					this.nextResult.setBody(this.body);
					this.nextResult.setFixture(fixture);
					
					return true;
				}
			}
			
			return false;
		}
	}
	
	private final class ConvexDetectIterator implements Iterator<ConvexDetectResult<T, E>>  {
		private final Convex convex;
		private final Transform transform;
		private final AABB aabb;
		private final DetectFilter<T, E> filter;
		private final Iterator<CollisionItem<T, E>> iterator;
		
		private final ConvexDetectResult<T, E> currentResult;
		private final ConvexDetectResult<T, E> nextResult;
		private boolean hasNext;
		
		public ConvexDetectIterator(Convex convex, Transform transform, DetectFilter<T, E> filter) {
			this.convex = convex;
			this.transform = transform;
			this.filter = filter;
			this.aabb = convex.createAABB(transform);
			this.iterator = AbstractCollisionWorld.this.broadphaseDetector.detectIterator(this.aabb);
			
			this.currentResult = new ConvexDetectResult<T, E>();
			this.nextResult = new ConvexDetectResult<T, E>();
			this.hasNext = this.findNext();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.hasNext;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public ConvexDetectResult<T, E> next() {
			if (this.hasNext) {
				this.currentResult.set(this.nextResult);
				this.hasNext = this.findNext();
				return this.currentResult;
			}
			throw new NoSuchElementException();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Returns true if there's another item in the iteration and sets the nextItem.
		 * @return boolean
		 */
		private boolean findNext() {
			while (this.iterator.hasNext()) {
				CollisionItem<T, E> item = this.iterator.next();
				
				T body = item.getBody();
				E fixture = item.getFixture();
				
				if (this.filter != null && !this.filter.isAllowed(body, fixture)) {
					continue;
				}
				
				Convex convex1 = fixture.getShape();
				Transform transform1 = body.getTransform();
				
				if (AbstractCollisionWorld.this.narrowphaseDetector.detect(convex1, transform1, this.convex, this.transform, this.nextResult.getPenetration())) {
					this.nextResult.setBody(body);
					this.nextResult.setFixture(fixture);
				}
				
				return true;
			}
			
			return false;
		}
	}
	
	private final class ConvexBodyDetectIterator implements Iterator<ConvexDetectResult<T, E>>  {
		private final Convex convex;
		private final Transform transform;
		private final T body;
		private final AABB aabb;
		private final DetectFilter<T, E> filter;
		private final Iterator<E> iterator;
		
		private final ConvexDetectResult<T, E> currentResult;
		private final ConvexDetectResult<T, E> nextResult;
		private boolean hasNext;
		
		public ConvexBodyDetectIterator(Convex convex, Transform transform, T body, DetectFilter<T, E> filter) {
			this.convex = convex;
			this.transform = transform;
			this.body = body;
			this.filter = filter;
			this.aabb = convex.createAABB(transform);
			this.iterator = body.getFixtureIterator();
			
			this.currentResult = new ConvexDetectResult<T, E>();
			this.nextResult = new ConvexDetectResult<T, E>();
			this.hasNext = this.findNext();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.hasNext;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public ConvexDetectResult<T, E> next() {
			if (this.hasNext) {
				this.currentResult.set(this.nextResult);
				this.hasNext = this.findNext();
				return this.currentResult;
			}
			throw new NoSuchElementException();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Returns true if there's another item in the iteration and sets the nextItem.
		 * @return boolean
		 */
		private boolean findNext() {
			while (this.iterator.hasNext()) {
				E fixture = this.iterator.next();
				
				if (this.filter != null && !this.filter.isAllowed(this.body, fixture)) {
					continue;
				}
				
				AABB aabb = AbstractCollisionWorld.this.broadphaseDetector.getAABB(this.body, fixture);
				if (this.aabb.overlaps(aabb)) {
					Convex convex1 = fixture.getShape();
					Transform transform1 = this.body.getTransform();
					
					if (AbstractCollisionWorld.this.narrowphaseDetector.detect(convex1, transform1, this.convex, this.transform, this.nextResult.getPenetration())) {
						this.nextResult.setBody(this.body);
						this.nextResult.setFixture(fixture);
					}
					
					return true;
				}
			}
			
			return false;
		}
	}
	
	private final class RaycastDetectIterator implements Iterator<RaycastResult<T, E>> {
		private final Ray ray;
		private double max;
		private final DetectFilter<T, E> filter;
		private final Iterator<CollisionItem<T, E>> iterator;
		
		private final RaycastResult<T, E> currentResult;
		private final RaycastResult<T, E> nextResult;
		private boolean hasNext;
		
		public RaycastDetectIterator(Ray ray, double maxLength, DetectFilter<T, E> filter) {
			this.ray = ray;
			this.filter = filter;
			this.iterator = AbstractCollisionWorld.this.broadphaseDetector.raycastIterator(ray, maxLength);
			
			double max = 0.0;
			if (maxLength > 0.0) {
				max = maxLength;
			}
			this.max = max;
			
			this.currentResult = new RaycastResult<T, E>();
			this.nextResult = new RaycastResult<T, E>();
			this.hasNext = this.findNext();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.hasNext;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public RaycastResult<T, E> next() {
			if (this.hasNext) {
				this.currentResult.set(this.nextResult);
				this.hasNext = this.findNext();
				return this.currentResult;
			}
			throw new NoSuchElementException();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Returns true if there's another item in the iteration and sets the nextItem.
		 * @return boolean
		 */
		private boolean findNext() {
			while (this.iterator.hasNext()) {
				CollisionItem<T, E> item = this.iterator.next();
				T body = item.getBody();
				E fixture = item.getFixture();
				
				if (this.filter != null && !this.filter.isAllowed(body, fixture)) {
					continue;
				}
				
				// get the convex shape
				Transform transform = body.getTransform();
				Convex convex = fixture.getShape();
				
				// perform the raycast
				if (AbstractCollisionWorld.this.raycastDetector.raycast(this.ray, this.max, convex, transform, this.nextResult.getRaycast())) {
					// we found a collision to report
					this.nextResult.setBody(body);
					this.nextResult.setFixture(fixture);
					
					return true;
				}
			}
			
			return false;
		}
	}
	
	private final class RaycastBodyDetectIterator implements Iterator<RaycastResult<T, E>> {
		private final Ray ray;
		private double max;
		private final T body;
		private final DetectFilter<T, E> filter;
		private final Iterator<E> iterator;
		
		private final RaycastResult<T, E> currentResult;
		private final RaycastResult<T, E> nextResult;
		private boolean hasNext;
		
		public RaycastBodyDetectIterator(Ray ray, double maxLength, T body, DetectFilter<T, E> filter) {
			this.ray = ray;
			this.body = body;
			this.filter = filter;
			this.iterator = body.getFixtureIterator();
			
			double max = 0.0;
			if (maxLength > 0.0) {
				max = maxLength;
			}
			this.max = max;
			
			this.currentResult = new RaycastResult<T, E>();
			this.nextResult = new RaycastResult<T, E>();
			this.hasNext = this.findNext();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.hasNext;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public RaycastResult<T, E> next() {
			if (this.hasNext) {
				this.currentResult.set(this.nextResult);
				this.hasNext = this.findNext();
				return this.currentResult;
			}
			throw new NoSuchElementException();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Returns true if there's another item in the iteration and sets the nextItem.
		 * @return boolean
		 */
		private boolean findNext() {
			while (this.iterator.hasNext()) {
				E fixture = this.iterator.next();
				
				if (this.filter != null && !this.filter.isAllowed(this.body, fixture)) {
					continue;
				}
				
				// get the convex shape
				Transform transform = this.body.getTransform();
				Convex convex = fixture.getShape();
				
				// perform the raycast
				if (AbstractCollisionWorld.this.raycastDetector.raycast(this.ray, this.max, convex, transform, this.nextResult.getRaycast())) {
					// we found a collision to report
					this.nextResult.setBody(this.body);
					this.nextResult.setFixture(fixture);
					
					return true;
				}
			}
			
			return false;
		}
	}

	private final class ConvexCastDetectIterator implements Iterator<ConvexCastResult<T, E>> {
		private final Convex convex;
		private final Transform transform;
		private final Vector2 deltaPosition;
		private final double deltaAngle;
		private final DetectFilter<T, E> filter;
		
		private final AABB aabb;
		private final Iterator<CollisionItem<T, E>> iterator;
		
		private final ConvexCastResult<T, E> currentResult;
		private final ConvexCastResult<T, E> nextResult;
		private boolean hasNext;
		
		public ConvexCastDetectIterator(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, DetectFilter<T, E> filter) {
			this.convex = convex;
			this.transform = transform;
			this.deltaPosition = deltaPosition;
			this.deltaAngle = deltaAngle;
			this.filter = filter;
			
			// compute a conservative AABB for the motion of the convex
			double radius = convex.getRadius();
			Vector2 startWorldCenter = transform.getTransformed(convex.getCenter());
			AABB startAABB = new AABB(startWorldCenter, radius);
			
			// linearlly interpolate to get the final transform given the
			// change in position and angle
			Transform finalTransform = transform.lerped(deltaPosition, deltaAngle, 1.0);
			// get the end AABB
			Vector2 endWorldCenter = finalTransform.getTransformed(convex.getCenter());
			AABB endAABB = new AABB(endWorldCenter, radius);
			// union the AABBs to get the swept AABB
			this.aabb = startAABB.getUnion(endAABB);
			
			this.iterator = AbstractCollisionWorld.this.broadphaseDetector.detectIterator(this.aabb);
			
			this.currentResult = new ConvexCastResult<T, E>();
			this.nextResult = new ConvexCastResult<T, E>();
			this.hasNext = this.findNext();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.hasNext;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public ConvexCastResult<T, E> next() {
			if (this.hasNext) {
				this.currentResult.set(this.nextResult);
				this.hasNext = this.findNext();
				return this.currentResult;
			}
			throw new NoSuchElementException();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Returns true if there's another item in the iteration and sets the nextItem.
		 * @return boolean
		 */
		private boolean findNext() {
			final Vector2 dp2 = new Vector2();
			
			// loop over the potential collisions
			while (this.iterator.hasNext()) {
				CollisionItem<T, E> item = this.iterator.next();
				T body = item.getBody();
				E fixture = item.getFixture();
				
				if (this.filter != null && !this.filter.isAllowed(body, fixture)) {
					continue;
				}
				
				Transform bodyTransform = body.getTransform();
				
				// get the time of impact
				Convex c = fixture.getShape();
				// we pass the zero vector and 0 for the change in position and angle for the body
				// since we assume that it is not moving since this is a static test
				if (AbstractCollisionWorld.this.timeOfImpactDetector.getTimeOfImpact(
						this.convex, this.transform, this.deltaPosition, this.deltaAngle, 
						c, bodyTransform, dp2, 0.0, 
						0.0, 1.0, this.nextResult.getTimeOfImpact())) {
					this.nextResult.setBody(body);
					this.nextResult.setFixture(fixture);
					
					return true;
				}
			}
			
			return false;
		}
	}

	private final class CollisionDataIterator implements Iterator<V> {
		private final Iterator<V> iterator;
		private final CollisionItemAdapter<T, E> item = new CollisionItemAdapter<T, E>();
		private V current;
		
		private boolean hasNext;
		
		public CollisionDataIterator() {
			this.iterator = AbstractCollisionWorld.this.collisionData.values().iterator();
			this.hasNext = this.findNext();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.hasNext;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public V next() {
			if (this.hasNext) {
				V current = this.current;
				this.hasNext = this.findNext();
				return current;
			}
			throw new NoSuchElementException();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Returns true if there's another item in the iteration and sets the nextItem.
		 * @return boolean
		 */
		private boolean findNext() {
			while (this.iterator.hasNext()) {
				V collision = this.iterator.next();
				
				// makes sure the body and fixture are still part of this world
				this.item.set(collision.getBody1(), collision.getFixture1());
				if (!AbstractCollisionWorld.this.broadphaseDetector.contains(item)) {
					continue;
				}

				// make sure the body and fixture are still part of this world
				this.item.set(collision.getBody2(), collision.getFixture2());
				if (!AbstractCollisionWorld.this.broadphaseDetector.contains(item)) {
					continue;
				}
				
				this.current = collision;
				return true;
			}
			return false;
		}
	}
	
	/**
	 * {@link FixtureModificationHandler} used to update the broadphase when fixtures
	 * are added or removed from {@link CollisionBody}s.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	private final class BodyModificationHandler implements FixtureModificationHandler<E> {
		/** The body */
		private final T body;
		
		/**
		 * Minimal constructor.
		 * @param body the body
		 */
		public BodyModificationHandler(T body) {
			this.body = body;
		}

		/* (non-Javadoc)
		 * @see org.dyn4j.collision.FixtureModificationHandler#onFixtureAdded(org.dyn4j.collision.Fixture)
		 */
		@Override
		public void onFixtureAdded(E fixture) {
			AbstractCollisionWorld.this.handleFixtureAdded(this.body, fixture);
		}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.collision.FixtureModificationHandler#onFixtureRemoved(org.dyn4j.collision.Fixture)
		 */
		@Override
		public void onFixtureRemoved(E fixture) {
			AbstractCollisionWorld.this.handleFixtureRemoved(this.body, fixture);
		}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.collision.FixtureModificationHandler#onAllFixturesRemoved()
		 */
		@Override
		public void onAllFixturesRemoved() {
			AbstractCollisionWorld.this.handleAllFixturesRemoved(this.body);
		}
	}
}
