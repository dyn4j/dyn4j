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
package org.dyn4j.collision.broadphase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.dyn4j.AVLTree;
import org.dyn4j.BinarySearchTree;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Ray;
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
 * This algorithm is O(n) for all {@link #detect(AABB)} and {@link #raycast(Ray, double)} methods.
 * @author William Bittle
 * @version 6.0.0
 * @since 1.0.0
 * @param <T> the object type
 */
public final class Sap<T> extends AbstractBroadphaseDetector<T> implements BroadphaseDetector<T> {
	/** Sorted tree set of proxies */
	private BinarySearchTree<AABBBroadphaseProxy<T>> tree;
	
	/** Id to proxy map for fast lookup */
	private final Map<T, AABBBroadphaseProxy<T>> nodes;

	/** Id to proxy map for fast lookup */
	private final Map<T, AABBBroadphaseProxy<T>> updated;
	
	/** A reusable {@link AABB} for updates to reduce allocation */
	private final AABB updatedAABB;
	
	/** 
	 * Default constructor.
	 * @param broadphaseFilter the broadphase filter
	 * @param aabbProducer the AABB producer
	 * @param aabbExpansionMethod the AABB expansion method 
	 * @throws NullPointerException if broadphaseFilter, aabbProducer or aabbExpansionMethod are null
	 */
	public Sap(BroadphaseFilter<T> broadphaseFilter, AABBProducer<T> aabbProducer, AABBExpansionMethod<T> aabbExpansionMethod) {
		this(broadphaseFilter, aabbProducer, aabbExpansionMethod, BroadphaseDetector.DEFAULT_INITIAL_CAPACITY);
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Allows fine tuning of the initial capacity of local storage for faster running times.
	 * @param broadphaseFilter the broadphase filter
	 * @param aabbProducer the AABB producer
	 * @param aabbExpansionMethod the AABB expansion method
	 * @param initialCapacity the initial capacity of local storage
	 * @throws NullPointerException if broadphaseFilter, aabbProducer or aabbExpansionMethod are null
	 * @throws IllegalArgumentException if initialCapacity is less than zero
	 * @since 3.1.1
	 */
	public Sap(BroadphaseFilter<T> broadphaseFilter, AABBProducer<T> aabbProducer, AABBExpansionMethod<T> aabbExpansionMethod, int initialCapacity) {
		super(broadphaseFilter, aabbProducer, aabbExpansionMethod);
		
		this.tree = new AVLTree<AABBBroadphaseProxy<T>>();
		// 0.75 = 3/4, we can garuantee that the hashmap will not need to be rehashed
		// if we take capacity / load factor
		// the default load factor is 0.75 according to the javadocs, but lets assign it to be sure
		this.nodes = new HashMap<T, AABBBroadphaseProxy<T>>(initialCapacity * 4 / 3 + 1, 0.75f);
		this.updated = new LinkedHashMap<T, AABBBroadphaseProxy<T>>(initialCapacity * 4 / 3 + 1, 0.75f);
		this.updatedAABB = new AABB(0,0,0,0);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#add(java.lang.Object)
	 */
	@Override
	public void add(T object) {
		AABBBroadphaseProxy<T> proxy = this.nodes.get(object);
		if (proxy == null) {
			this.insert(object);
		} else {
			this.update(proxy, object);
		}
	}
	
	/**
	 * Internal add method.
	 * <p>
	 * This method assumes the given arguments are all non-null and that the
	 * {@link CollisionBody} {@link Fixture} is not currently in this broad-phase.
	 * @param object the object
	 */
	void insert(T object) {
		this.aabbProducer.compute(object, this.updatedAABB);
		// expand the aabb
		this.aabbExpansionMethod.expand(object, this.updatedAABB);
		// create a new node for the body
		AABBBroadphaseProxy<T> proxy = new AABBBroadphaseProxy<T>(object);
		proxy.aabb.set(this.updatedAABB);
		// add the proxy to the map
		this.nodes.put(object, proxy);
		// are we tracking updates?
		if (this.updateTrackingEnabled) {
			this.updated.put(object, proxy);
		}
		// insert the node into the tree
		this.tree.insert(proxy);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(T object) {
		// find the proxy in the map
		AABBBroadphaseProxy<T> proxy = this.nodes.remove(object);
		// make sure it was found
		if (proxy != null) {
			// remove the proxy from the tree
			this.tree.remove(proxy);
			this.updated.remove(object);
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update()
	 */
	@Override
	public void update() {
		for (AABBBroadphaseProxy<T> proxy : this.nodes.values()) {
			this.update(proxy.item);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update(java.lang.Object)
	 */
	@Override
	public void update(T object) {
		AABBBroadphaseProxy<T> proxy = this.nodes.get(object);
		if (proxy != null) {
			this.update(proxy, object);
		} else {
			this.insert(object);
		}
	}
	
	/**
	 * Internal update method.
	 * <p>
	 * This method assumes the given arguments are all non-null.
	 * @param proxy the current node in the tree
	 * @param object the object
	 */
	void update(AABBBroadphaseProxy<T> proxy, T object) {
		this.aabbProducer.compute(object, this.updatedAABB);
		
		// see if the old AABB contains the new one
		// NOTE: the old AABB is the expanded AABB and this is how
		// we handle what's been updated vs. not
		boolean isNonExpanedNewContainedInExpandedOld = proxy.aabb.contains(this.updatedAABB);

		// now expand the AABB so that we can do some additional
		// checking on it and so we can use it later if we find
		// we need to update it
		this.aabbExpansionMethod.expand(object, this.updatedAABB);
		
		// now we check if the new non-expanded AABB fits within the
		// current expanded AABB
		if (isNonExpanedNewContainedInExpandedOld) {
			// we could stop here and conclude that there's nothing to do, but
			// there's an edge case where the current AABB is MUCH larger than
			// the new AABB and it never gets sized down.  This has the effect
			// of sending a lot more pairs to the narrow phase until the object
			// moves out of the current AABB.  If this doesn't happen, for example,
			// object stops, then the broadphase retains the large AABB forever.
			
			// so the goal here is to understand and adapt the larger AABBs to 
			// smaller ones based on their perimeter ratio
			double p0 = proxy.aabb.getPerimeter();
			double p1 = this.updatedAABB.getPerimeter();
			double ratio = p0 / p1;
			if (ratio <= AABB_REDUCTION_RATIO) {
				// if the old AABB is 2x (or less) the size (in perimeter) to the new
				// then we'll accept it and not update
				return;
			}
		}

		// remove the current proxy from the tree
		this.tree.remove(proxy);
		// set the new aabb
		proxy.aabb.set(this.updatedAABB);
		// are we tracking updates?
		if (this.updateTrackingEnabled) {
			this.updated.put(object, proxy);
		}
		// reinsert the proxy
		this.tree.insert(proxy);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdated(java.lang.Object)
	 */
	@Override
	public boolean isUpdated(T object) {
		if (!this.nodes.containsKey(object)) {
			return false;
		}
		
		if (!this.updateTrackingEnabled) {
			return true;
		}

		return this.updated.containsKey(object);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#setUpdated(java.lang.Object)
	 */
	@Override
	public void setUpdated(T object) {
		if (!this.updateTrackingEnabled) {
			return;
		}
		AABBBroadphaseProxy<T> proxy = this.nodes.get(object);
		this.updated.put(object, proxy);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clearUpdates()
	 */
	@Override
	public void clearUpdates() {
		this.updated.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(java.lang.Object)
	 */
	@Override
	public AABB getAABB(T object) {
		AABBBroadphaseProxy<T> proxy = this.nodes.get(object);
		if (proxy != null) {
			return proxy.aabb;
		}
		
		AABB aabb = this.aabbProducer.compute(object);
		if (aabb.isDegenerate()) {
			return aabb;
		}
		
		this.aabbExpansionMethod.expand(object, aabb);
		return aabb;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(T object) {
		return this.nodes.containsKey(object);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clear()
	 */
	@Override
	public void clear() {
		this.nodes.clear();
		this.tree.clear();
		this.updated.clear();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#size()
	 */
	@Override
	public int size() {
		return this.nodes.size();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdateTrackingSupported()
	 */
	@Override
	public boolean isUpdateTrackingSupported() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.AbstractBroadphaseDetector#setUpdateTrackingEnabled(boolean)
	 */
	@Override
	public void setUpdateTrackingEnabled(boolean flag) {
		if (this.updateTrackingEnabled != flag) {
			if (flag) {
				// nothing to do here, we'll just have to wait for the next
				// round of updates to come in
			} else {
				// clear everything to save space and so that it doesn't produce
				// odd results if it's turned back on
				this.updated.clear();
			}
		}
		super.setUpdateTrackingEnabled(flag);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detectIterator(boolean)
	 */
	@Override
	public Iterator<CollisionPair<T>> detectIterator(boolean forceFullDetection) {
		if (forceFullDetection || !this.updateTrackingEnabled) {
			return new DetectPairsIterator(this.tree.iterator());
		}
		return new DetectPairsIterator(this.updated.values().iterator());
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detectIterator(org.dyn4j.geometry.AABB)
	 */
	@Override
	public Iterator<T> detectIterator(AABB aabb) {
		return new DetectAABBIterator(aabb);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#raycastIterator(org.dyn4j.geometry.Ray, double)
	 */
	@Override
	public Iterator<T> raycastIterator(Ray ray, double length) {
		return new DetectRayIterator(ray, length);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		// loop over all the proxies and translate their aabb
		Iterator<AABBBroadphaseProxy<T>> it = this.tree.iterator();
		while (it.hasNext()) {
			AABBBroadphaseProxy<T> proxy = it.next();
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
	private final class DetectPairsIterator implements Iterator<CollisionPair<T>> {
		/** An iterator for all the objects to test */
		private final Iterator<AABBBroadphaseProxy<T>> outerIterator;

		/** Internal state to track pairs already tested */
		private final Map<T, Boolean> tested;
		
		/** The current outer-iterator object to test with */
		private AABBBroadphaseProxy<T> currentProxy;
		
		/** The inner-iterator for all objects to test with the current object */
		private Iterator<AABBBroadphaseProxy<T>> innerIterator;
		
		/** A reusable pair to output collisions */
		private final BroadphasePair<T> currentPair;
		
		/** A reusable pair to output collisions */
		private final BroadphasePair<T> nextPair;
		
		/** True if there's another pair */
		private boolean hasNext;
		
		/**
		 * Minimal constructor.
		 * @param iterator the iterator of items to test
		 */
		public DetectPairsIterator(Iterator<AABBBroadphaseProxy<T>> iterator) {
			this.outerIterator = iterator;
			this.tested = new HashMap<T, Boolean>();
			this.currentProxy = null;
			this.innerIterator = null;
			
			if (this.outerIterator.hasNext()) {
				this.currentProxy = this.outerIterator.next();
			}
			
			this.currentPair = new BroadphasePair<T>();
			this.nextPair = new BroadphasePair<T>();
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
		public CollisionPair<T> next() {
			if (this.hasNext) {
				// copy over to the one we return
				this.currentPair.first = this.nextPair.first;
				this.currentPair.second = this.nextPair.second;
				
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
					AABBBroadphaseProxy<T> test = this.innerIterator.next();
					
					// dont compare objects against themselves
					if (!Sap.this.broadphaseFilter.isAllowed(this.currentProxy.item, test.item)) continue;
					
					// dont compare object that have already been compared
					boolean tested = this.tested.containsKey(test.item);
					if (tested) {
						continue;
					}
					
					// test overlap
					// the >= is to support degenerate intervals created by vertical segments
					if (this.currentProxy.aabb.getMaxX() >= test.aabb.getMinX()) {
						if (this.currentProxy.aabb.overlaps(test.aabb)) {
							this.nextPair.first = this.currentProxy.item;
							this.nextPair.second = test.item;
							
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
	private final class DetectAABBIterator implements Iterator<T> {
		/** The {@link AABB} to test */
		private final AABB aabb;
		
		/** An iterator for all the objects to test */
		private final Iterator<AABBBroadphaseProxy<T>> iterator;
		
		/** The next item */
		private T nextItem;
		
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
		public T next() {
			if (this.nextItem != null) {
				T item = this.nextItem;
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
				AABBBroadphaseProxy<T> test = this.iterator.next();
				
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
	private final class DetectRayIterator implements Iterator<T> {
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
		private final Iterator<AABBBroadphaseProxy<T>> iterator;
		
		/** The next item */
		private T nextItem;
		
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
			this.aabb = AABB.createFromPoints(x1, y1, x2, y2);
			
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
		public T next() {
			if (this.nextItem != null) {
				T item = this.nextItem;
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
				AABBBroadphaseProxy<T> b = this.iterator.next();
				
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
