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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.dyn4j.BinarySearchTree;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * Implementation of the Sweep and Prune broad-phase collision detection algorithm.
 * <p>
 * This implementation maintains a red-black tree of {@link CollisionBody} {@link Fixture}s where each update
 * will reposition the respective {@link CollisionBody} {@link Fixture} in the tree.
 * <p>
 * Projects all {@link CollisionBody} {@link Fixture}s on both the x and y axes and performs overlap checks
 * on all the projections to test for possible collisions (AABB tests).
 * <p>
 * This algorithm is O(n) for all {@link #detect(AABB)} and {@link #detect(Ray, double)} methods.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public final class Sap<T extends CollisionBody<E>, E extends Fixture> extends AbstractBroadphaseDetector<T, E> implements BroadphaseDetector<T, E> {
	/** Sorted tree set of proxies */
	private BinarySearchTree<AABBBroadphaseProxy<T, E>> tree;
	
	/** Id to proxy map for fast lookup */
	private final Map<CollisionItem<T, E>, AABBBroadphaseProxy<T, E>> map;

	/** Id to proxy map for fast lookup */
	private final Map<CollisionItem<T, E>, AABBBroadphaseProxy<T, E>> updated;
	
	/** Default constructor. */
	public Sap() {
		this(BroadphaseDetector.DEFAULT_INITIAL_CAPACITY);
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Allows fine tuning of the initial capacity of local storage for faster running times.
	 * @param initialCapacity the initial capacity of local storage
	 * @throws IllegalArgumentException if initialCapacity is less than zero
	 * @since 3.1.1
	 */
	public Sap(int initialCapacity) {
		this.tree = new BinarySearchTree<AABBBroadphaseProxy<T, E>>(true);
		// 0.75 = 3/4, we can garuantee that the hashmap will not need to be rehashed
		// if we take capacity / load factor
		// the default load factor is 0.75 according to the javadocs, but lets assign it to be sure
		this.map = new HashMap<CollisionItem<T, E>, AABBBroadphaseProxy<T, E>>(initialCapacity * 4 / 3 + 1, 0.75f);
		this.updated = new LinkedHashMap<CollisionItem<T, E>, AABBBroadphaseProxy<T, E>>(initialCapacity * 4 / 3 + 1, 0.75f);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#add(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void add(T body, E fixture) {
		BroadphaseItem<T, E> key = new BroadphaseItem<T, E>(body, fixture);
		AABBBroadphaseProxy<T, E> proxy = this.map.get(key);
		if (proxy == null) {
			this.add(key, body, fixture);
		} else {
			this.update(key, proxy, body, fixture);
		}
	}
	
	/**
	 * Internal add method.
	 * <p>
	 * This method assumes the given arguments are all non-null and that the
	 * {@link CollisionBody} {@link Fixture} is not currently in this broad-phase.
	 * @param key the key for the body-fixture pair
	 * @param body the body
	 * @param fixture the fixture
	 */
	void add(BroadphaseItem<T, E> key, T body, E fixture) {
		Transform tx = body.getTransform();
		AABB aabb = fixture.getShape().createAABB(tx);
		// expand the aabb
		aabb.expand(this.expansion);
		// create a new node for the body
		AABBBroadphaseProxy<T, E> proxy = new AABBBroadphaseProxy<T, E>(key, aabb);
		// add the proxy to the map
		this.map.put(key, proxy);
		this.updated.put(key, proxy);
		// insert the node into the tree
		this.tree.insert(proxy);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean remove(T body, E fixture) {
		CollisionItem<T, E> key = new BroadphaseItem<T, E>(body, fixture);
		
		// find the proxy in the map
		AABBBroadphaseProxy<T, E> proxy = this.map.remove(key);
		// make sure it was found
		if (proxy != null) {
			// remove the proxy from the tree
			this.tree.remove(proxy);
			this.updated.remove(key);
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(org.dyn4j.collision.CollisionItem)
	 */
	@Override
	public boolean remove(CollisionItem<T, E> item) {
		// find the proxy in the map
		AABBBroadphaseProxy<T, E> proxy = this.map.remove(item);
		// make sure it was found
		if (proxy != null) {
			// remove the proxy from the tree
			this.tree.remove(proxy);
			this.updated.remove(item);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void update(T body, E fixture) {
		BroadphaseItem<T, E> key = new BroadphaseItem<T, E>(body, fixture);
		
		AABBBroadphaseProxy<T, E> proxy = this.map.get(key);
		if (proxy != null) {
			this.update(key, proxy, body, fixture);
		} else {
			this.add(key, body, fixture);
		}
	}
	
	/**
	 * Internal update method.
	 * <p>
	 * This method assumes the given arguments are all non-null.
	 * @param key the key for the body-fixture pair
	 * @param proxy the current node in the tree
	 * @param body the body
	 * @param fixture the fixture
	 */
	void update(CollisionItem<T, E> key, AABBBroadphaseProxy<T, E> proxy, T body, E fixture) {
		Transform tx = body.getTransform();
		// create the new aabb
		AABB aabb = fixture.getShape().createAABB(tx);
		// see if the old aabb contains the new one
		if (proxy.aabb.contains(aabb)) {
			// if so, don't do anything
			return;
		}
		// otherwise expand the new aabb
		aabb.expand(this.expansion);
		// remove the current proxy from the tree
		this.tree.remove(proxy);
		// set the new aabb
		proxy.aabb = aabb;
		this.updated.put(key, proxy);
		// reinsert the proxy
		this.tree.insert(proxy);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdated(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean isUpdated(T body, E fixture) {
		CollisionItem<T, E> key = new BroadphaseItem<T, E>(body, fixture);
		return this.updated.containsKey(key);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdated(org.dyn4j.collision.CollisionItem)
	 */
	@Override
	public boolean isUpdated(CollisionItem<T, E> item) {
		return this.updated.containsKey(item);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#setUpdated(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void setUpdated(T body, E fixture) {
		CollisionItem<T, E> key = new BroadphaseItem<T, E>(body, fixture);
		AABBBroadphaseProxy<T, E> proxy = this.map.get(key);
		this.updated.put(key, proxy);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clearUpdates()
	 */
	@Override
	public void clearUpdates() {
		this.updated.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public AABB getAABB(T body, E fixture) {
		CollisionItem<T, E> key = new BroadphaseItem<T, E>(body, fixture);
		return this.getAABB(key);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(org.dyn4j.collision.CollisionItem)
	 */
	@Override
	public AABB getAABB(CollisionItem<T, E> item) {
		AABBBroadphaseProxy<T, E> proxy = this.map.get(item);
		if (proxy != null) {
			return proxy.aabb;
		}
		
		return item.getFixture().getShape().createAABB(item.getBody().getTransform());
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#contains(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean contains(T body, E fixture) {
		CollisionItem<T, E> key = new BroadphaseItem<T, E>(body, fixture);
		return this.map.containsKey(key);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#contains(org.dyn4j.collision.CollisionItem)
	 */
	@Override
	public boolean contains(CollisionItem<T, E> item) {
		return this.map.containsKey(item);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clear()
	 */
	@Override
	public void clear() {
		this.map.clear();
		this.tree.clear();
		this.updated.clear();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#size()
	 */
	@Override
	public int size() {
		return this.map.size();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detectIterator(boolean)
	 */
	@Override
	public Iterator<CollisionPair<T, E>> detectIterator(boolean forceFullDetection) {
		if (forceFullDetection || !this.updateTrackingEnabled) {
			return new DetectIterator(this.tree.iterator());
		}
		return new DetectIterator(this.updated.values().iterator());
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detectIterator(org.dyn4j.geometry.AABB)
	 */
	@Override
	public Iterator<CollisionItem<T, E>> detectIterator(AABB aabb) {
		return new DetectAABBIterator(aabb);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detectIterator(org.dyn4j.geometry.Ray, double)
	 */
	@Override
	public Iterator<CollisionItem<T, E>> detectIterator(Ray ray, double length) {
		return new DetectRayIterator(ray, length);
	}
	
//	/* (non-Javadoc)
//	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.collision.broadphase.BroadphaseFilter)
//	 */
//	@Override
//	public List<BroadphasePair<E, T>> detect(BroadphaseFilter<E, T> filter) {
//		// get the number of proxies
//		int size = this.tree.size();
//		
//		// check the size
//		if (size == 0) {
//			// return the empty list
//			return Collections.emptyList();
//		}
//		
//		// the estimated size of the pair list
//		int eSize = Collisions.getEstimatedCollisionPairs(size);
//		List<BroadphasePair<E, T>> pairs = new ArrayList<BroadphasePair<E, T>>(eSize);
//		
//		// clear the tested flags
//		Iterator<AABBBroadphaseProxy<E, T>> itp = this.tree.iterator();
//		while (itp.hasNext()) {
//			AABBBroadphaseProxy<E, T> p = itp.next();
//			p.tested = false;
//		}
//		
//		// find all the possible pairs O(n*log(n))
//		Iterator<AABBBroadphaseProxy<E, T>> ito = this.tree.iterator();
//		while (ito.hasNext()) {
//			// get the current proxy
//			AABBBroadphaseProxy<E, T> current = ito.next();
//			Iterator<AABBBroadphaseProxy<E, T>> iti = this.tree.tailIterator(current);
//			while (iti.hasNext()) {
//				AABBBroadphaseProxy<E, T> test = iti.next();
//				// dont compare objects against themselves
//				if (test.body == current.body) continue;
//				// dont compare object that have already been compared
//				if (test.tested) continue;
//				// test overlap
//				// the >= is to support degenerate intervals created by vertical segments
//				if (current.aabb.getMaxX() >= test.aabb.getMinX()) {
//					if (current.aabb.overlaps(test.aabb)) {
//						if (filter.isAllowed(current.body, current.fixture, test.body, test.fixture)) {
//							pairs.add(new BroadphasePair<E, T>(
//									current.body,
//									current.fixture,
//									test.body,
//									test.fixture));
//						}
//					}
//				} else {
//					// otherwise we can break from the loop
//					break;
//				}
//			}
//			current.tested = true;
//		}
//		
//		return pairs;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.geometry.AABB, org.dyn4j.collision.broadphase.BroadphaseFilter)
//	 */
//	@Override
//	public List<BroadphaseItem<E, T>> detect(AABB aabb, BroadphaseFilter<E, T> filter) {
//		// get the size of the proxy list
//		int size = this.tree.size();
//		
//		// check the size of the proxy list
//		if (size == 0) {
//			// return the empty list
//			return Collections.emptyList();
//		}
//		
//		List<BroadphaseItem<E, T>> list = new ArrayList<BroadphaseItem<E, T>>(Collisions.getEstimatedCollisionsPerObject());
//		
//		// we must check all aabbs starting at the root
//		// from which point the first aabb to not intersect
//		// flags us to stop O(n)
//		Iterator<AABBBroadphaseProxy<E, T>> it = this.tree.inOrderIterator();
//		while (it.hasNext()) {
//			AABBBroadphaseProxy<E, T> proxy = it.next();
//			// check for overlap
//			if (proxy.aabb.getMaxX() > aabb.getMinX()) {
//				if (proxy.aabb.overlaps(aabb)) {
//					if (filter.isAllowed(aabb, proxy.body, proxy.fixture)) {
//						list.add(new BroadphaseItem<E, T>(
//								proxy.body,
//								proxy.fixture));
//					}
//				}
//			} else if (aabb.getMaxX() < proxy.aabb.getMinX()) {
//				// if not overlapping, then nothing after this
//				// node will overlap either so we can exit the loop
//				break;
//			}
//		}
//		
//		return list;
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#raycast(org.dyn4j.geometry.Ray, double)
//	 */
//	@Override
//	public List<BroadphaseItem<E, T>> raycast(Ray ray, double length, BroadphaseFilter<E, T> filter) {
//		// check the size of the proxy list
//		if (this.tree.size() == 0) {
//			// return an empty list
//			return Collections.emptyList();
//		}
//		
//		// create an aabb from the ray
//		Vector2 s = ray.getStart();
//		Vector2 d = ray.getDirectionVector();
//		
//		// get the length
//		double l = length;
//		if (length <= 0.0) l = Double.MAX_VALUE;
//		
//		// compute the coordinates
//		double x1 = s.x;
//		double x2 = s.x + d.x * l;
//		double y1 = s.y;
//		double y2 = s.y + d.y * l;
//		
//		// create the aabb
//		AABB aabb = AABB.createAABBFromPoints(x1, y1, x2, y2);
//		
//		double invDx = 1.0 / d.x;
//		double invDy = 1.0 / d.y;
//		
//		// get the size of the proxy list
//		int size = this.tree.size();
//		
//		// check the size of the proxy list
//		if (size == 0) {
//			// return the empty list
//			return Collections.emptyList();
//		}
//		
//		int eSize = Collisions.getEstimatedRaycastCollisions(this.map.size());
//		List<BroadphaseItem<E, T>> list = new ArrayList<BroadphaseItem<E, T>>(eSize);
//		
//		// we must check all aabbs starting with the root
//		// from which point the first aabb to not intersect
//		// flags us to stop O(n)
//		Iterator<AABBBroadphaseProxy<E, T>> it = this.tree.inOrderIterator();
//		while (it.hasNext()) {
//			AABBBroadphaseProxy<E, T> proxy = it.next();
//			// check for overlap
//			if (proxy.aabb.getMaxX() > aabb.getMinX()) {
//				if (proxy.aabb.overlaps(aabb)) {
//					if (this.raycast(s, l, invDx, invDy, proxy.aabb)) {
//						if (filter.isAllowed(ray, length, proxy.body, proxy.fixture)) {
//							list.add(new BroadphaseItem<E, T>(
//									proxy.body,
//									proxy.fixture));
//						}
//					}
//				}
//			} else if (aabb.getMaxX() < proxy.aabb.getMinX()) {
//				// if not overlapping, then nothing after this
//				// node will overlap either so we can exit the loop
//				break;
//			}
//		}
//		
//		return list;
//	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		// loop over all the proxies and translate their aabb
		Iterator<AABBBroadphaseProxy<T, E>> it = this.tree.iterator();
		while (it.hasNext()) {
			AABBBroadphaseProxy<T, E> proxy = it.next();
			proxy.aabb.translate(shift);
		}
	}
	
	@Override
	public void optimize() {
		// no-op - the implementation is always optimized due to insertion sorting and balancing
	}
	
	/**
	 * A specialized iterator for detecting pairs of colliding {@link AABB}s in this broaphase.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	private final class DetectIterator implements Iterator<CollisionPair<T, E>> {
		/** An iterator for all the objects to test */
		private final Iterator<AABBBroadphaseProxy<T, E>> outerIterator;

		/** Internal state to track pairs already tested */
		private final Map<CollisionItem<T, E>, Boolean> tested;
		
		/** The current outer-iterator object to test with */
		private AABBBroadphaseProxy<T, E> currentProxy;
		
		/** The inner-iterator for all objects to test with the current object */
		private Iterator<AABBBroadphaseProxy<T, E>> innerIterator;
		
		/** A reusable pair for collision output */
		private final BroadphasePair<T, E> reusablePair;
		
		/**
		 * Minimal constructor.
		 * @param iterator the iterator of items to test
		 */
		public DetectIterator(Iterator<AABBBroadphaseProxy<T, E>> iterator) {
			this.outerIterator = iterator;
			this.tested = new HashMap<CollisionItem<T, E>, Boolean>();
			this.currentProxy = null;
			this.innerIterator = null;
			this.reusablePair = new BroadphasePair<T, E>();
			
			if (this.outerIterator.hasNext()) {
				this.currentProxy = this.outerIterator.next();
			}
		}
		
		@Override
		public boolean hasNext() {
			// find all the possible pairs O(n*log(n))
			while (this.currentProxy != null) {
				// if the inner-iterator is null, this means that we advanced to another outer-iterator item
				// and we need to start from where the current proxy is
				if (this.innerIterator == null) {
					this.innerIterator = Sap.this.tree.tailIterator(this.currentProxy);
				}
				
				// iterate over everything past the current
				while (this.innerIterator.hasNext()) {
					AABBBroadphaseProxy<T, E> test = this.innerIterator.next();
					
					// dont compare objects against themselves
					if (test.item.body == this.currentProxy.item.body) continue;
					
					// dont compare object that have already been compared
//					int bKey = CollisionItem.getHashCode(test.item.body, test.item.fixture);
					boolean tested = this.tested.containsKey(test.item);
					
					if (tested) {
						continue;
					}
					
					// test overlap
					// the >= is to support degenerate intervals created by vertical segments
					if (this.currentProxy.aabb.getMaxX() >= test.aabb.getMinX()) {
						if (this.currentProxy.aabb.overlaps(test.aabb)) {
							this.reusablePair.body1 = this.currentProxy.item.body;
							this.reusablePair.fixture1 = this.currentProxy.item.fixture;
							this.reusablePair.body2 = test.item.body;
							this.reusablePair.fixture2 = test.item.fixture;
							
							// in this iterator we can immediately exit when we find a collision
							// because the outer/inner iterator track our position so we can
							// pick up again later
							return true;
						}
					} else {
						// otherwise we can break from the loop
						break;
					}
				}
				
				// we've made it through all other objects, so we're done with the outer
				// iterator object (A) so indicate that it's been tested
//				int aKey = CollisionItem.getHashCode(this.currentProxy.item.body, this.currentProxy.item.fixture);
				this.tested.put(this.currentProxy.item, true);
				
				// clear the inner iterator since we're moving onto another item
				this.innerIterator = null;
				
				// see if there's a next item to test
				if (this.outerIterator.hasNext()) {
					this.currentProxy = this.outerIterator.next();
				} else {
					this.currentProxy = null;
				}
			}
			
			return false;
		}
		
		@Override
		public CollisionPair<T, E> next() {
			return this.reusablePair;
		}
	}
	
	/**
	 * A specialized iterator for detecting pairs of colliding {@link AABB}s in this broaphase.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	private final class DetectAABBIterator implements Iterator<CollisionItem<T, E>> {
		/** The {@link AABB} to test */
		private final AABB aabb;
		
		/** An iterator for all the objects to test */
		private final Iterator<AABBBroadphaseProxy<T, E>> iterator;
		
		/** A reusable pair for collision output */
		private final BroadphaseItem<T, E> reusableItem;
		
		/**
		 * Minimal constructor.
		 * @param aabb the {@link AABB} to test
		 */
		public DetectAABBIterator(AABB aabb) {
			this.aabb = aabb;
			this.iterator = Sap.this.tree.inOrderIterator();
			this.reusableItem = new BroadphaseItem<T, E>();
		}
		
		@Override
		public boolean hasNext() {
			// iterate over everything past the current
			while (this.iterator.hasNext()) {
				AABBBroadphaseProxy<T, E> test = this.iterator.next();
				
				// test overlap
				// the >= is to support degenerate intervals created by vertical segments
				if (this.aabb.getMaxX() >= test.aabb.getMinX()) {
					if (this.aabb.overlaps(test.aabb)) {
						this.reusableItem.body = test.item.body;
						this.reusableItem.fixture = test.item.fixture;
						
						// in this iterator we can immediately exit when we find a collision
						// because the outer/inner iterator track our position so we can
						// pick up again later
						return true;
					}
				} else if (test.aabb.getMinX() > this.aabb.getMaxX()) {
					// otherwise we can break from the loop
					break;
				}
			}
			
			return false;
		}
		
		@Override
		public CollisionItem<T, E> next() {
			return this.reusableItem;
		}
	}
	
	/**
	 * A specialized iterator for detecting collisions of a given {@link Ray}, it's length, and this broadphase.
	 * <p>
	 * Unfortunately, the SAP algorithm doesn't support accelerating raycasts so this is just a brute force
	 * raycast method.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	private final class DetectRayIterator implements Iterator<CollisionItem<T, E>> {
		/** The ray to test with */
		private final Ray ray;
		
		/** The length of the ray */
		private final double length;
		
		/** The AABB of the ray */
		private final AABB aabb;
		
		/** Precomputed 1/x */
		private final double invDx;
		
		/** Precomputed 1/y */
		private final double invDy;
		
		/** The iterator for testing all objects in this broadphase */
		private final Iterator<AABBBroadphaseProxy<T, E>> iterator;
		
		/** A reusable item to save on allocation */
		private final BroadphaseItem<T, E> reusableItem;
		
		/**
		 * Minimal constructor.
		 * @param ray the {@link Ray}
		 * @param length the length of the ray
		 */
		public DetectRayIterator(Ray ray, double length) {
			this.ray = ray;
			this.iterator = Sap.this.tree.inOrderIterator();
			this.reusableItem = new BroadphaseItem<T, E>();
			
			// create an aabb from the ray
			Vector2 s = ray.getStart();
			Vector2 d = ray.getDirectionVector();
			
			// get the length
			double l = length;
			if (length <= 0.0) l = Double.MAX_VALUE;
			this.length = l;
			
			// compute the coordinates
			double x1 = s.x;
			double x2 = s.x + d.x * l;
			double y1 = s.y;
			double y2 = s.y + d.y * l;
			
			// create the aabb
			this.aabb = AABB.createAABBFromPoints(x1, y1, x2, y2);
			
			// precompute
			this.invDx = 1.0 / d.x;
			this.invDy = 1.0 / d.y;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			// just iterate all pairs - the iterator maintains the position
			while (this.iterator.hasNext()) {
				AABBBroadphaseProxy<T, E> b = this.iterator.next();
				
				if (b.aabb.getMaxX() >= this.aabb.getMinX()) {
					if (this.aabb.overlaps(b.aabb)) {
						if (AbstractBroadphaseDetector.raycast(this.ray.getStart(), this.length, this.invDx, this.invDy, b.aabb)) {
							this.reusableItem.body = b.item.body;
							this.reusableItem.fixture = b.item.fixture;
							
							return true;
						}
					}
				} else if (b.aabb.getMinX() > this.aabb.getMaxX()) {
					// if not overlapping, then nothing after this
					// node will overlap either so we can exit the loop
					break;
				}
			}
			
			return false;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public BroadphaseItem<T, E> next() {
			return this.reusableItem;
		}
	}
}
