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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

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
	
	/** A reusable {@link AABB} for updates to reduce allocation */
	private final AABB updatedAABB;
	
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
		this.updatedAABB = new AABB(0,0,0,0);
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
		fixture.getShape().computeAABB(tx, this.updatedAABB);
		// expand the aabb
		this.updatedAABB.expand(this.expansion);
		// create a new node for the body
		AABBBroadphaseProxy<T, E> proxy = new AABBBroadphaseProxy<T, E>(key, this.updatedAABB.copy());
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
		fixture.getShape().computeAABB(tx, this.updatedAABB);
		// see if the old aabb contains the new one
		if (proxy.aabb.contains(this.updatedAABB)) {
			// if so, don't do anything
			return;
		}
		// otherwise expand the new aabb
		this.updatedAABB.expand(this.expansion);
		// remove the current proxy from the tree
		this.tree.remove(proxy);
		// set the new aabb
		proxy.aabb.set(this.updatedAABB);
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#optimize()
	 */
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
		
		/** A reusable pair to output collisions */
		private final BroadphasePair<T, E> currentPair;
		
		/** A reusable pair to output collisions */
		private final BroadphasePair<T, E> nextPair;
		
		/** True if there's another pair */
		private boolean hasNext;
		
		/**
		 * Minimal constructor.
		 * @param iterator the iterator of items to test
		 */
		public DetectIterator(Iterator<AABBBroadphaseProxy<T, E>> iterator) {
			this.outerIterator = iterator;
			this.tested = new HashMap<CollisionItem<T, E>, Boolean>();
			this.currentProxy = null;
			this.innerIterator = null;
			
			if (this.outerIterator.hasNext()) {
				this.currentProxy = this.outerIterator.next();
			}
			
			this.currentPair = new BroadphasePair<T, E>();
			this.nextPair = new BroadphasePair<T, E>();
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
		public CollisionPair<T, E> next() {
			if (this.hasNext) {
				// copy over to the one we return
				this.currentPair.body1 = this.nextPair.body1;
				this.currentPair.fixture1 = this.nextPair.fixture1;
				this.currentPair.body2 = this.nextPair.body2;
				this.currentPair.fixture2 = this.nextPair.fixture2;
				
				// find the next pair
				this.hasNext = this.findNext();
				
				// return the current pair
				return this.currentPair;
			}
			throw new NoSuchElementException();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Returns true if there's another pair to process and sets the pair to the nextPair.
		 * @return boolean
		 */
		private boolean findNext() {
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
					boolean tested = this.tested.containsKey(test.item);
					if (tested) {
						continue;
					}
					
					// test overlap
					// the >= is to support degenerate intervals created by vertical segments
					if (this.currentProxy.aabb.getMaxX() >= test.aabb.getMinX()) {
						if (this.currentProxy.aabb.overlaps(test.aabb)) {
							this.nextPair.body1 = this.currentProxy.item.body;
							this.nextPair.fixture1 = this.currentProxy.item.fixture;
							this.nextPair.body2 = test.item.body;
							this.nextPair.fixture2 = test.item.fixture;
							
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
		
		/** The next item */
		private CollisionItem<T, E> nextItem;
		
		/**
		 * Minimal constructor.
		 * @param aabb the {@link AABB} to test
		 */
		public DetectAABBIterator(AABB aabb) {
			this.aabb = aabb;
			this.iterator = Sap.this.tree.inOrderIterator();
			
			this.findNext();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.nextItem != null;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public CollisionItem<T, E> next() {
			if (this.nextItem != null) {
				CollisionItem<T, E> item = this.nextItem;
				this.findNext();
				return item;
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
		
		private boolean findNext() {
			this.nextItem = null;
			
			// iterate over everything past the current
			while (this.iterator.hasNext()) {
				AABBBroadphaseProxy<T, E> test = this.iterator.next();
				
				// test overlap
				// the >= is to support degenerate intervals created by vertical segments
				if (this.aabb.getMaxX() >= test.aabb.getMinX()) {
					if (this.aabb.overlaps(test.aabb)) {
						this.nextItem = test.item;
						
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
		
		/** The next item */
		private CollisionItem<T, E> nextItem;
		
		/**
		 * Minimal constructor.
		 * @param ray the {@link Ray}
		 * @param length the length of the ray
		 */
		public DetectRayIterator(Ray ray, double length) {
			this.ray = ray;
			this.iterator = Sap.this.tree.inOrderIterator();
			
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
			
			this.findNext();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.nextItem != null;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public CollisionItem<T, E> next() {
			if (this.nextItem != null) {
				CollisionItem<T, E> item = this.nextItem;
				this.findNext();
				return item;
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
		 * Returns true if there's a next item and sets the nextItem to it.
		 * @return boolean
		 */
		private boolean findNext() {
			this.nextItem = null;
			
			// just iterate all pairs - the iterator maintains the position
			while (this.iterator.hasNext()) {
				AABBBroadphaseProxy<T, E> b = this.iterator.next();
				
				if (b.aabb.getMaxX() >= this.aabb.getMinX()) {
					if (this.aabb.overlaps(b.aabb)) {
						if (AbstractBroadphaseDetector.raycast(this.ray.getStart(), this.length, this.invDx, this.invDy, b.aabb)) {
							this.nextItem = b.item;
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
	}
}
