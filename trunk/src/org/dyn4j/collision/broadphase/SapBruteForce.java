/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Collisions;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Vector2;

/**
 * Implementation of the Sweep and Prune broad-phase collision detection algorithm.
 * <p>
 * This implementation maintains an unsorted list of {@link Collidable}s and each time
 * the {@link #detect()} method is called the list is resorted.
 * <p>
 * Projects all {@link Collidable}s on both the x and y axes and performs overlap checks
 * on all the projections to test for possible collisions (AABB tests).
 * <p>
 * The overlap checks are performed faster when the {@link Interval}s created by the projections
 * are sorted by their minimum value.  Doing so will allow the detector to ignore any projections
 * after the first {@link Interval} that does not overlap.
 * <p>
 * If a {@link Collidable} is made up of more than one {@link Shape} and the {@link Shape}s 
 * are not connected, this detection algorithm may cause false hits.  For example,
 * if your {@link Collidable} consists of the following geometry: (the line below the {@link Shape}s
 * is the projection that will be used for the broad-phase)
 * <pre>
 * +--------+     +--------+  |
 * | body1  |     | body1  |  |
 * | shape1 |     | shape2 |  | y-axis projection
 * |        |     |        |  |
 * +--------+     +--------+  |
 * 
 * -------------------------
 *     x-axis projection
 * </pre>
 * So if following configuration is encountered it will generate a hit:
 * <pre>
 *             +--------+               |
 * +--------+  | body2  |  +--------+   | |
 * | body1  |  | shape1 |  | body1  |   | |
 * | shape1 |  |        |  | shape2 |   | | y-axis projection
 * |        |  |        |  |        |   | |
 * +--------+  |        |  +--------+   | |
 *             +--------+               |
 * 
 *             ----------
 * ----------------------------------
 *         x-axis projection
 * </pre>
 * These cases are OK since the {@link NarrowphaseDetector}s will handle these cases.
 * However, allowing this causes more work for the {@link NarrowphaseDetector}s whose
 * algorithms are more complex.  These situations should be avoided for maximum performance.
 * @author William Bittle
 * @version 3.1.4
 * @since 1.0.0
 * @param <E> the {@link Collidable} type
 */
public class SapBruteForce<E extends Collidable> extends AbstractAABBDetector<E> implements BroadphaseDetector<E> {
	/**
	 * Internal class to hold the {@link Collidable} to {@link AABB} relationship.
	 * @author William Bittle
	 * @version 3.0.0
	 * @since 3.0.0
	 */
	protected class Proxy implements Comparable<Proxy> {
		/** The collidable */
		public E collidable;
		
		/** The collidable's aabb */
		public AABB aabb;
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Proxy o) {
			// check if the objects are the same instance
			if (this == o) return 0;
			// compute the difference in the minimum x values of the aabbs
			double diff = this.aabb.getMinX() - o.aabb.getMinX();
			if (diff > 0) {
				return 1;
			} else if (diff < 0) {
				return -1;
			} else {
				// if the x values are the same then compare on the y values
				diff = this.aabb.getMinY() - o.aabb.getMinY();
				if (diff > 0) {
					return 1;
				} else if (diff < 0) {
					return -1;
				} else {
					// finally if their y values are the same then compare on the ids
					return this.collidable.getId().compareTo(o.collidable.getId());
				}
			}
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return aabb.toString();
		}
	}
	
	/**
	 * Represents a list of potential pairs.
	 * @author William Bittle
	 * @version 3.0.0
	 * @since 3.0.0
	 */
	protected class PairList {
		/** The proxy */
		public Proxy proxy;
		
		/** The proxy's potential pairs */
		public List<Proxy> potentials = new ArrayList<Proxy>(Collisions.getEstimatedCollisions());
	}
	
	/** Sorted list of proxies */
	protected List<Proxy> proxyList;
	
	/** Id to proxy map for fast lookup */
	protected Map<UUID, Proxy> proxyMap;
	
	/** Reusable list for storing potential detected pairs along the x-axis */
	protected ArrayList<PairList> potentialPairs;
	
	/** Flag used to indicate that the proxyList must be sorted before use */
	protected boolean sort = false;
	
	/** Default constructor. */
	public SapBruteForce() {
		this(64);
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * Allows fine tuning of the initial capacity of local storage for faster running times.
	 * @param initialCapacity the initial capacity of local storage
	 * @throws IllegalArgumentException if initialCapacity is less than zero
	 * @since 3.1.1
	 */
	public SapBruteForce(int initialCapacity) {
		this.proxyList = new ArrayList<Proxy>(initialCapacity);
		this.proxyMap = new HashMap<UUID, Proxy>(initialCapacity);
		this.potentialPairs = new ArrayList<PairList>(initialCapacity);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#add(org.dyn4j.collision.Collidable)
	 */
	@Override
	public void add(E collidable) {
		// get the id
		UUID id = collidable.getId();
		// create an aabb from the collidable
		AABB aabb = collidable.createAABB();
		// expand the aabb by some factor
		aabb.expand(this.expansion);
		
		// create a proxy for the collidable
		Proxy p = new Proxy();
		p.collidable = collidable;
		p.aabb = aabb;
		
		// insert the proxy into the sorted list
		this.proxyList.add(p);
		// insert the proxy into the map
		this.proxyMap.put(id, p);
		
		// set sort flag to true
		this.sort = true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(org.dyn4j.collision.Collidable)
	 */
	@Override
	public void remove(E collidable) {
		// loop through the list sequentially until we find
		// the element and remove it
		Iterator<Proxy> it = this.proxyList.iterator();
		while (it.hasNext()) {
			Proxy p = it.next();
			// test the collidable
			if (p.collidable == collidable) {
				// remove it
				it.remove();
				// break immediately
				break;
			}
		}
		// finally remove it from the map
		this.proxyMap.remove(collidable.getId());
		// no re-sort required
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update(org.dyn4j.collision.Collidable)
	 */
	@Override
	public void update(E collidable) {
		// get the proxy
		Proxy p0 = this.proxyMap.get(collidable.getId());
		// check for not found
		if (p0 == null) return;
		// test if we need to update
		AABB aabb = collidable.createAABB();
		if (p0.aabb.contains(aabb)) {
			// if the object is still inside the old aabb then don't
			// bother updating, just continue to use the current aabb
			return;
		} else {
			// otherwise use the new aabb and expand it
			aabb.expand(this.expansion);
		}
		
		// update the aabb
		p0.aabb = aabb;
		// set sort flag to true
		this.sort = true;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clear()
	 */
	@Override
	public void clear() {
		this.proxyList.clear();
		this.proxyMap.clear();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(org.dyn4j.collision.Collidable)
	 */
	@Override
	public AABB getAABB(E collidable) {
		Proxy proxy = this.proxyMap.get(collidable.getId());
		if (proxy != null) {
			return proxy.aabb;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect()
	 */
	@Override
	public List<BroadphasePair<E>> detect() {
		// get the number of proxies
		int size = this.proxyList.size();

		// check the size
		if (size == 0) {
			// return the empty list
			return Collections.emptyList();
		}
		
		// the estimated size of the pair list
		int eSize = Collisions.getEstimatedCollisionPairs(size);
		// create a new list for the resulting pairs
		List<BroadphasePair<E>> pairs = new ArrayList<BroadphasePair<E>>(eSize);
		// clear the local list and make sure it can store
		// all the potential pairs
		this.potentialPairs.clear();
		this.potentialPairs.ensureCapacity(size);
		
		// check the sort flag
		if (this.sort) {
			// sort by x; n log(n) stable sort
			Collections.sort(this.proxyList);
			// set the needs sort flag to false
			this.sort = false;
		}
		
		// create the potential pairs using the sorted x axis
		PairList pl = new PairList();
		for (int i = 0; i < size; i++) {
			Proxy current = this.proxyList.get(i);
			for (int j = i + 1; j < size; j++) {
				Proxy test = this.proxyList.get(j);
				// test overlap
				// the >= is to support degenerate intervals created by vertical segments
				if (current.aabb.getMaxX() >= test.aabb.getMinX()) {
					// add it to the current collidable's potential list
					pl.potentials.add(test);
				} else {
					// otherwise we can break from the loop
					break;
				}
			}
			// finally check if we found any potentials
			if (pl.potentials.size() > 0) {
				// sorting on the potentials proved to be slower than just
				// testing all potentials in the y phase
				
				// set the current collidable
				pl.proxy = current;
				// add the pair list to the potential pairs
				this.potentialPairs.add(pl);
				// create a new pair list for the next collidable
				pl = new PairList();
			}
		}
		
		// go through the potential pairs and filter using the
		// y axis projections
		size = this.potentialPairs.size();
		for (int i = 0; i < size; i++) {
			PairList current = this.potentialPairs.get(i);
			int pls = current.potentials.size();
			for (int j = 0; j < pls; j++) {
				Proxy test = current.potentials.get(j);
				// have to do full overlap test since the list is not sorted
				if (current.proxy.aabb.overlaps(test.aabb)) {
					// add to the colliding list
					BroadphasePair<E> pair = new BroadphasePair<E>();
					pair.a = current.proxy.collidable;
					pair.b = test.collidable;
					pairs.add(pair);
				}
			}
		}
		
		// return the list
		return pairs;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.geometry.AABB)
	 */
	@Override
	public List<E> detect(AABB aabb) {
		// get the size of the proxy list
		int size = this.proxyList.size();
		
		// check the size of the proxy list
		if (size == 0) {
			// return the empty list
			return Collections.emptyList();
		}
		List<E> list = new ArrayList<E>(Collisions.getEstimatedCollisions());
		
		// check the sort flag to see if we need to sort
		if (this.sort) {
			// sort the list
			Collections.sort(this.proxyList);
			// set the sort flag to false
			this.sort = false;
		}
		
		// peform a binary search to find where this
		// aabb should be inserted
		int index = size / 2;
		int max = size;
		int min = 0;
		while (true) {
			Proxy p = this.proxyList.get(index);
			if (p.aabb.getMinX() < aabb.getMinX()) {
				min = index;
			} else {
				max = index;
			}
			if (max - min == 1) {
				break;
			}
			index = (min + max) / 2;
		}
		
		// we must check all aabbs up to the found index
		// from which point the first aabb to not intersect
		// flags us to stop
		for (int i = 0; i < size; i++) {
			Proxy p = this.proxyList.get(i);
			if (p.aabb.getMaxX() > aabb.getMinX()) {
				if (p.aabb.overlaps(aabb)) {
					list.add(p.collidable);
				}
			} else {
				if (i >= index) break;
			}
		}
		
		return list;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#raycast(org.dyn4j.geometry.Ray, double)
	 */
	@Override
	public List<E> raycast(Ray ray, double length) {
		// check the size of the proxy list
		if (this.proxyList.size() == 0) {
			// return an empty list
			return Collections.emptyList();
		}
		
		// create an aabb from the ray
		Vector2 s = ray.getStart();
		Vector2 d = ray.getDirectionVector();
		
		// get the length
		double l = length;
		if (length <= 0.0) l = Double.MAX_VALUE;
		
		// compute the coordinates
		double x1 = s.x;
		double x2 = s.x + d.x * l;
		double y1 = s.y;
		double y2 = s.y + d.y * l;
		
		// create the min and max points
		Vector2 min = new Vector2(
				Math.min(x1, x2),
				Math.min(y1, y2));
		Vector2 max = new Vector2(
				Math.max(x1, x2),
				Math.max(y1, y2));
		
		// create the aabb
		AABB aabb = new AABB(min, max);
		
		// pass it to the aabb detection routine
		return this.detect(aabb);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#shiftCoordinates(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shiftCoordinates(Vector2 shift) {
		// loop over all the proxies and translate their aabb
		int pSize = this.proxyList.size();
		for (int i = 0; i < pSize; i++) {
			Proxy proxy = this.proxyList.get(i);
			proxy.aabb.translate(shift);
		}
	}
}
