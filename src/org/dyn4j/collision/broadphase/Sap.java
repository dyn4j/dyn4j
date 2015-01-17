/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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
import java.util.SortedSet;
import java.util.TreeSet;
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
 * This implementation maintains a red-black tree of {@link Collidable}s where each update
 * will reposition the respective {@link Collidable} in the tree.
 * <p>
 * Projects all {@link Collidable}s on both the x and y axes and performs overlap checks
 * on all the projections to test for possible collisions (AABB tests).
 * <p>
 * The overlap checks are performed faster when the {@link Interval}s created by the projections
 * are sorted by their minimum value.  Doing so will allow the detector to ignore any projections
 * after the first {@link Interval} that does not overlap.
 * @author William Bittle
 * @version 3.1.5
 * @since 1.0.0
 * @param <E> the {@link Collidable} type
 */
public class Sap<E extends Collidable> extends AbstractAABBDetector<E> implements BroadphaseDetector<E> {
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
		
		/** Whether the proxy has been tested or not */
		public boolean tested;
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Proxy o) {
			// check if the objects are the same instance
			if (this == o) return 0;
			// compute the difference in the minimum x values of the aabbs
			double diff = this.aabb.getMinX() - o.aabb.getMinX();
			if (diff != 0) {
				return (int)Math.signum(diff);
			} else {
				// if the x values are the same then compare on the y values
				diff = this.aabb.getMinY() - o.aabb.getMinY();
				if (diff != 0) {
					return (int)Math.signum(diff);
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
			return this.aabb.toString();
		}
	}

	/** Sorted tree set of proxies */
	protected TreeSet<Proxy> proxyTree;
	
	/** Id to proxy map for fast lookup */
	protected Map<UUID, Proxy> proxyMap;

	/** Default constructor. */
	public Sap() {
		this(64);
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
		this.proxyTree = new TreeSet<Proxy>();
		// 0.75 = 3/4, we can garuantee that the hashmap will not need to be rehashed
		// if we take capacity / load factor
		// the default load factor is 0.75 according to the javadocs, but lets assign it to be sure
		this.proxyMap = new HashMap<UUID, Proxy>(initialCapacity * 4 / 3 + 1, 0.75f);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#add(org.dyn4j.collision.Collidable)
	 */
	@Override
	public void add(E collidable) {
		// get the id of the collidable
		UUID id = collidable.getId();
		// create an aabb for this collidable
		AABB aabb = collidable.createAABB();
		// expand it
		aabb.expand(this.expansion);
		// otherwise add it to the list
		Proxy p = new Proxy();
		p.collidable = collidable;
		p.aabb = aabb;
		// add it to the tree [log(n)]
		this.proxyTree.add(p);
		// add it to the map [constant]
		this.proxyMap.put(id, p);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(org.dyn4j.collision.Collidable)
	 */
	@Override
	public void remove(E collidable) {
		// remove it from the map [constant]
		Proxy p = this.proxyMap.remove(collidable.getId());
		// make sure its found
		if (p != null) {
			// remove it from the tree [log(n)]
			this.proxyTree.remove(p);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update(org.dyn4j.collision.Collidable)
	 */
	@Override
	public void update(E collidable) {
		// get the proxy for this collidable [constant]
		Proxy p = this.proxyMap.get(collidable.getId());
		// check for not found
		if (p == null) return;
		
		// create a new aabb
		AABB aabb = collidable.createAABB();
		// check the aabb
		if (p.aabb.contains(aabb)) {
			// if the aabb is still inside the expanded
			// aabb then just return
			return;
		} else {
			// otherwise expand the new aabb
			aabb.expand(this.expansion);
		}
		
		// remove the proxy from the tree [log(n)]
		this.proxyTree.remove(p);
		// update the aabb
		p.aabb = aabb;
		// add back the proxy [log(n)]
		this.proxyTree.add(p);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#contains(org.dyn4j.collision.Collidable)
	 */
	public boolean contains(E collidable) {
		return this.proxyMap.containsKey(collidable.getId());
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clear()
	 */
	@Override
	public void clear() {
		this.proxyTree.clear();
		this.proxyMap.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(org.dyn4j.collision.Collidable)
	 */
	public AABB getAABB(E collidable) {
		// [constant]
		Proxy p = this.proxyMap.get(collidable.getId());
		if (p != null) {
			return p.aabb;
		}
		return collidable.createAABB();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect()
	 */
	@Override
	public List<BroadphasePair<E>> detect() {
		// get the number of proxies
		int size = this.proxyTree.size();
		
		// check the size
		if (size == 0) {
			// return the empty list
			return Collections.emptyList();
		}
		
		// the estimated size of the pair list
		int eSize = Collisions.getEstimatedCollisionPairs(size);
		List<BroadphasePair<E>> pairs = new ArrayList<BroadphasePair<E>>(eSize);
		
		// clear the tested flags
		Iterator<Proxy> itp = this.proxyTree.iterator();
		while (itp.hasNext()) {
			Proxy p = itp.next();
			p.tested = false;
		}
		
		// find all the possible pairs
		Iterator<Proxy> ito = this.proxyTree.iterator();
		while (ito.hasNext()) {
			// get the current proxy
			Proxy current = ito.next();
			// only check the ones greater than (or equal to) the current item
			SortedSet<Proxy> set = this.proxyTree.tailSet(current, false);
			Iterator<Proxy> iti = set.iterator();
			while (iti.hasNext()) {
				Proxy test = iti.next();
				// dont compare objects against themselves
				if (test.collidable == current.collidable) continue;
				// dont compare object that have already been compared
				if (test.tested) continue;
				// test overlap
				// the >= is to support degenerate intervals created by vertical segments
				if (current.aabb.getMaxX() >= test.aabb.getMinX()) {
					if (current.aabb.overlaps(test.aabb)) {
						pairs.add(new BroadphasePair<E>(current.collidable, test.collidable));
					}
				} else {
					// otherwise we can break from the loop
					break;
				}
			}
			current.tested = true;
		}
		
		return pairs;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.geometry.AABB)
	 */
	@Override
	public List<E> detect(AABB aabb) {
		// get the size of the proxy list
		int size = this.proxyTree.size();
		
		// check the size of the proxy list
		if (size == 0) {
			// return the empty list
			return Collections.emptyList();
		}
		
		List<E> list = new ArrayList<E>(Collisions.getEstimatedCollisions());
		
		// create a proxy for the aabb
		Proxy p = new Proxy();
		p.aabb = aabb;
		p.collidable = null;
		p.tested = false;
		// find the proxy in the tree that is least of all the
		// proxies greater than this one
		Proxy l = this.proxyTree.ceiling(p);
		
		// we must check all aabbs up to the found proxy
		// from which point the first aabb to not intersect
		// flags us to stop
		Iterator<Proxy> it = this.proxyTree.iterator();
		boolean found = false;
		while (it.hasNext()) {
			Proxy proxy = it.next();
			// see if we found the proxy
			if (proxy == l) {
				found = true;
			}
			if (proxy.aabb.getMaxX() > aabb.getMinX()) {
				if (proxy.aabb.overlaps(aabb)) {
					list.add(proxy.collidable);
				}
			} else {
				// check if we have passed the proxy
				if (found) break;
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
		if (this.proxyTree.size() == 0) {
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
		
		double invDx = 1.0 / d.x;
		double invDy = 1.0 / d.y;
		
		// get the size of the proxy list
		int size = this.proxyTree.size();
		
		// check the size of the proxy list
		if (size == 0) {
			// return the empty list
			return Collections.emptyList();
		}
		
		List<E> list = new ArrayList<E>(Collisions.getEstimatedCollisions());
		
		// create a proxy for the aabb
		Proxy p = new Proxy();
		p.aabb = aabb;
		p.collidable = null;
		p.tested = false;
		// find the proxy in the tree that is least of all the
		// proxies greater than this one
		Proxy ceil = this.proxyTree.ceiling(p);
		
		// we must check all aabbs up to the found proxy
		// from which point the first aabb to not intersect
		// flags us to stop
		Iterator<Proxy> it = this.proxyTree.iterator();
		boolean found = false;
		while (it.hasNext()) {
			Proxy proxy = it.next();
			// see if we found the proxy
			if (proxy == ceil) {
				found = true;
			}
			if (proxy.aabb.getMaxX() > aabb.getMinX()) {
				if (proxy.aabb.overlaps(aabb)) {
					if (this.raycast(s, l, invDx, invDy, aabb)) {
						list.add(proxy.collidable);
					}
				}
			} else {
				// check if we have passed the proxy
				if (found) break;
			}
		}
		
		return list;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#shiftCoordinates(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shiftCoordinates(Vector2 shift) {
		// loop over all the proxies and translate their aabb
		Iterator<Proxy> it = this.proxyTree.iterator();
		while (it.hasNext()) {
			Proxy proxy = it.next();
			proxy.aabb.translate(shift);
		}
	}
}
