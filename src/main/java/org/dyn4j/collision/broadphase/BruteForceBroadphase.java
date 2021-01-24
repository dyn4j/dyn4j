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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.dyn4j.collision.CollisionPair;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

/**
 * This class implements the simplest possible broad-phase detector,
 * a brute-force algorithm for finding all pairs of collisions (and similar queries).
 * <p>
 * This implementation is not tuned for performance in any way and should <b>not</b> be used
 * except for testing purposes. One main reason this was developed is for automated testing of the other broad-phase detectors.
 * <p>
 * The logic of this class is simple: It holds a hash table of all the nodes and each time a query is made it scans linearly
 * all the nodes to find the answer.
 * <p>
 * Important note: This class must not use AABB expansion in order to always return the minimum set of pairs/items.
 * This property is used to test the other broad-phase detectors correctly.
 * 
 * @author Manolis Tsamis
 * @version 4.1.0
 * @since 3.4.0
 * @param <T> the object type
 */
public final class BruteForceBroadphase<T> extends AbstractBroadphaseDetector<T> {
	/** Id to node map for fast lookup */
	private final Map<T, AABBBroadphaseProxy<T>> map;
	
	/**
	 * Default constructor.
	 * @param broadphaseFilter the broadphase filter
	 * @param aabbProducer the AABB producer
	 * @throws NullPointerException if broadphaseFilter or aabbProducer are null
	 */
	public BruteForceBroadphase(BroadphaseFilter<T> broadphaseFilter, AABBProducer<T> aabbProducer) {
		super(broadphaseFilter, aabbProducer, new NullAABBExpansionMethod<T>());
		this.map = new LinkedHashMap<T, AABBBroadphaseProxy<T>>();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#add(java.lang.Object)
	 */
	@Override
	public void add(T object) {
		AABBBroadphaseProxy<T> node = this.map.get(object);
		AABB aabb = this.aabbProducer.compute(object);
		
		if (node != null) {
			// if the body-fixture has already been added just update it
			node.aabb.set(aabb);
		} else {
			// else add the new node
			node = new AABBBroadphaseProxy<T>(object);
			node.aabb.set(aabb);
			this.map.put(object, node);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(T object) {
		AABBBroadphaseProxy<T> proxy = this.map.remove(object);
		return proxy != null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update()
	 */
	@Override
	public void update() {
		for (AABBBroadphaseProxy<T> proxy : this.map.values()) {
			this.update(proxy.item);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update(java.lang.Object)
	 */
	@Override
	public void update(T object) {
		this.add(object);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdated(java.lang.Object)
	 */
	@Override
	public boolean isUpdated(T object) {
		if (!this.map.containsKey(object)) {
			return false;
		}
		
		// every run is a new run of everything
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#setUpdated(java.lang.Object)
	 */
	@Override
	public void setUpdated(T object) {
		// no-op
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clearUpdates()
	 */
	@Override
	public void clearUpdates() {
		// no-op
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(java.lang.Object)
	 */
	@Override
	public AABB getAABB(T object) {
		AABBBroadphaseProxy<T> node = this.map.get(object);
		
		if (node != null) {
			return node.aabb;
		}
		return this.aabbProducer.compute(object);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(T object) {
		return this.map.containsKey(object);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clear()
	 */
	@Override
	public void clear() {
		this.map.clear();
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
	public Iterator<CollisionPair<T>> detectIterator(boolean forceFullDetection) {
		return new DetectPairsIterator();
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
		for (AABBBroadphaseProxy<T> node : this.map.values()) {
			node.aabb.translate(shift);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.AbstractBroadphaseDetector#setUpdateTrackingEnabled(boolean)
	 */
	@Override
	public void setUpdateTrackingEnabled(boolean flag) {
		// no-op
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.AbstractBroadphaseDetector#isUpdateTrackingEnabled()
	 */
	@Override
	public boolean isUpdateTrackingEnabled() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdateTrackingSupported()
	 */
	@Override
	public boolean isUpdateTrackingSupported() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#optimize()
	 */
	@Override
	public void optimize() {
		// no-op
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
		 * Default constructor.
		 */
		public DetectPairsIterator() {
			this.tested = new HashMap<T, Boolean>();
			this.outerIterator = BruteForceBroadphase.this.map.values().iterator();
			
			// get the first item to test
			this.currentProxy = null;
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
		 * @see java.util.Iterator#remove()
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
			// test each body in the collection against every other body in the collection O(n^2)
			while (this.currentProxy != null) {
				// if the inner-iterator is null, this means that we advanced to another outer-iterator item
				// and we need to start from the beginning
				if (this.innerIterator == null) {
					this.innerIterator = BruteForceBroadphase.this.map.values().iterator();
				}
				
				// iterate all other objects
				while (this.innerIterator.hasNext()) {
					AABBBroadphaseProxy<T> b = this.innerIterator.next();
					
					// if they are the same body, then skip it
					if (!BruteForceBroadphase.this.broadphaseFilter.isAllowed(this.currentProxy.item, b.item)) continue;
					
					// if this pair has already been tested, then skip it
					boolean tested = this.tested.containsKey(b.item);
					if (tested) continue;
					
					if (this.currentProxy.aabb.overlaps(b.aabb)) {
						this.nextPair.first = this.currentProxy.item;
						this.nextPair.second = b.item;
						
						// in this iterator we can immediately exit when we find a collision
						// because the outer/inner iterator track our position so we can
						// pick up again later
						return true;
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
	 * A specialized iterator for detecting collisions of a given {@link AABB} and this broadphase.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	private final class DetectAABBIterator implements Iterator<T> {
		/** The {@link AABB} to test with */
		private final AABB aabb;
		
		/** The iterator for testing all objects in this broadphase */
		private final Iterator<AABBBroadphaseProxy<T>> iterator;
		
		/** The next item */
		private T nextItem;
		
		/**
		 * Minimal constructor.
		 * @param aabb the {@link AABB} to test
		 */
		public DetectAABBIterator(AABB aabb) {
			this.aabb = aabb;
			this.iterator = BruteForceBroadphase.this.map.values().iterator();
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
				if (this.aabb.overlaps(b.aabb)) {
					this.nextItem = b.item;
					return true;
				}
			}
			
			return false;
		}
	}

	/**
	 * A specialized iterator for detecting collisions of a given {@link AABB} and this broadphase.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	private final class DetectRayIterator implements Iterator<T> {
		/** The ray to test with */
		private final Ray ray;
		
		/** The length of the ray */
		private final double length;
		
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
			this.iterator = BruteForceBroadphase.this.map.values().iterator();
			
			// precompute
			Vector2 d = ray.getDirectionVector();
			this.invDx = 1.0 / d.x;
			this.invDy = 1.0 / d.y;
			
			// get the length
			double l = length;
			if (length <= 0.0) l = Double.MAX_VALUE;
			this.length = l;
			
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
			
			while (this.iterator.hasNext()) {
				AABBBroadphaseProxy<T> b = this.iterator.next();
				if (AbstractBroadphaseDetector.raycast(this.ray.getStart(), this.length, this.invDx, this.invDy, b.aabb)) {
					this.nextItem = b.item;
					return true;
				}
			}
			
			return false;
		}
	}
}
