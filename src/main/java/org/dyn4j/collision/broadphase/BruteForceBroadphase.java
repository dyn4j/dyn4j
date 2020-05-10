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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Transform;
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
 * @version 4.0.0
 * @since 3.4.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public final class BruteForceBroadphase<T extends CollisionBody<E>, E extends Fixture> extends AbstractBroadphaseDetector<T, E> implements BroadphaseDetector<T, E> {
	/** Id to node map for fast lookup */
	final Map<CollisionItem<T, E>, AABBBroadphaseProxy<T, E>> map;
	
	/**
	 * Default constructor.
	 */
	public BruteForceBroadphase() {
		this.map = new LinkedHashMap<CollisionItem<T, E>, AABBBroadphaseProxy<T,E>>();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#add(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void add(T body, E fixture) {
		BroadphaseItem<T, E> key = new BroadphaseItem<T, E>(body, fixture);
		AABBBroadphaseProxy<T, E> node = this.map.get(key);
		
		Transform tx = body.getTransform();
		AABB aabb = fixture.getShape().createAABB(tx);
		
		if (node != null) {
			// if the body-fixture has already been added just update it
			node.aabb = aabb;
		} else {
			// else add the new node
			this.map.put(key, new AABBBroadphaseProxy<T, E>(key, aabb));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean remove(T body, E fixture) {
		CollisionItem<T, E> key = new BroadphaseItem<T, E>(body, fixture);
		return this.remove(key);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(org.dyn4j.collision.CollisionItem)
	 */
	@Override
	public boolean remove(CollisionItem<T, E> item) {
		AABBBroadphaseProxy<T, E> node = this.map.remove(item);
		return node != null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void update(T body, E fixture) {
		this.add(body, fixture);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdated(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean isUpdated(T body, E fixture) {
		// every run is a new run of everything
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdated(org.dyn4j.collision.CollisionItem)
	 */
	@Override
	public boolean isUpdated(CollisionItem<T, E> item) {
		// every run is a new run of everything
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#setUpdated(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void setUpdated(T body, E fixture) {
		// no-op
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdateTrackingEnabled()
	 */
	@Override
	public boolean isUpdateTrackingEnabled() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#setUpdateTrackingEnabled(boolean)
	 */
	@Override
	public void setUpdateTrackingEnabled(boolean flag) {
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
		AABBBroadphaseProxy<T, E> node = this.map.get(item);
		
		if (node != null) {
			return node.aabb;
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
		return new DetectPairsIterator();
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
		Collection<AABBBroadphaseProxy<T, E>> nodes = this.map.values();
		
		for (AABBBroadphaseProxy<T, E> node : nodes) {
			node.aabb.translate(shift);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABBExpansion()
	 */
	@Override
	public double getAABBExpansion() {
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#supportsAABBExpansion()
	 */
	@Override
	public boolean supportsAABBExpansion() {
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
	private final class DetectPairsIterator implements Iterator<CollisionPair<T, E>> {
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
		 * Default constructor.
		 */
		public DetectPairsIterator() {
			this.tested = new HashMap<CollisionItem<T, E>, Boolean>();
			this.outerIterator = BruteForceBroadphase.this.map.values().iterator();
			this.reusablePair = new BroadphasePair<T, E>();
			
			// get the first item to test
			this.currentProxy = null;
			if (this.outerIterator.hasNext()) {
				this.currentProxy = this.outerIterator.next();
			}
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			// test each body in the collection against every other body in the collection O(n^2)
			while (this.currentProxy != null) {
				// if the inner-iterator is null, this means that we advanced to another outer-iterator item
				// and we need to start from the beginning
				if (this.innerIterator == null) {
					this.innerIterator = BruteForceBroadphase.this.map.values().iterator();
				}
				
				// iterate all other objects
				while (this.innerIterator.hasNext()) {
					AABBBroadphaseProxy<T, E> b = this.innerIterator.next();
					
					// if they are the same body, then skip it
					if (b.item.body == this.currentProxy.item.body) continue;
					
//					int bKey = CollisionItem.getHashCode(b.item.body, b.item.fixture);
					boolean tested = this.tested.containsKey(b.item);
					
					// if this pair has already been tested, then skip it
					if (tested) continue;
					
					if (this.currentProxy.aabb.overlaps(b.aabb)) {
						this.reusablePair.body1 = this.currentProxy.item.body;
						this.reusablePair.fixture1 = this.currentProxy.item.fixture;
						this.reusablePair.body2 = b.item.body;
						this.reusablePair.fixture2 = b.item.fixture;
						
						// in this iterator we can immediately exit when we find a collision
						// because the outer/inner iterator track our position so we can
						// pick up again later
						return true;
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

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public CollisionPair<T, E> next() {
			return this.reusablePair;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * A specialized iterator for detecting collisions of a given {@link AABB} and this broadphase.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	private final class DetectAABBIterator implements Iterator<CollisionItem<T, E>> {
		/** The {@link AABB} to test with */
		private final AABB aabb;
		
		/** The iterator for testing all objects in this broadphase */
		private final Iterator<AABBBroadphaseProxy<T, E>> iterator;
		
		/** A reusable item to save on allocation */
		private final BroadphaseItem<T, E> reusableItem;
		
		/**
		 * Minimal constructor.
		 * @param aabb the {@link AABB} to test
		 */
		public DetectAABBIterator(AABB aabb) {
			this.aabb = aabb;
			this.iterator = BruteForceBroadphase.this.map.values().iterator();
			this.reusableItem = new BroadphaseItem<T, E>();
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			// just iterate all pairs - the iterator maintains the position
			while (this.iterator.hasNext()) {
				AABBBroadphaseProxy<T, E> b = this.iterator.next();
				
				if (this.aabb.overlaps(b.aabb)) {
					this.reusableItem.body = b.item.body;
					this.reusableItem.fixture = b.item.fixture;
					
					return true;
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
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * A specialized iterator for detecting collisions of a given {@link AABB} and this broadphase.
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
			this.iterator = BruteForceBroadphase.this.map.values().iterator();
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
				
				if (this.aabb.overlaps(b.aabb)) {
					if (AbstractBroadphaseDetector.raycast(this.ray.getStart(), this.length, this.invDx, this.invDy, b.aabb)) {
						this.reusableItem.body = b.item.body;
						this.reusableItem.fixture = b.item.fixture;
						
						return true;
					}
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
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
