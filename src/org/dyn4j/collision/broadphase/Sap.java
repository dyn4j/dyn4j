/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Collisions;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * Implementation of the Sweep and Prune broad-phase collision detection algorithm.
 * <p>
 * This implementation maintains a red-black tree of {@link Collidable} {@link Fixture}s where each update
 * will reposition the respective {@link Collidable} {@link Fixture} in the tree.
 * <p>
 * Projects all {@link Collidable} {@link Fixture}s on both the x and y axes and performs overlap checks
 * on all the projections to test for possible collisions (AABB tests).
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 * @param <E> the {@link Collidable} type
 * @param <T> the {@link Fixture} type
 */
public class Sap<E extends Collidable<T>, T extends Fixture> extends AbstractBroadphaseDetector<E, T> implements BroadphaseDetector<E, T> {
	/** Sorted tree set of proxies */
	TreeSet<SapProxy<E, T>> tree;
	
	/** Id to proxy map for fast lookup */
	Map<BroadphaseKey, SapProxy<E, T>> map;

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
		this.tree = new TreeSet<SapProxy<E, T>>();
		// 0.75 = 3/4, we can garuantee that the hashmap will not need to be rehashed
		// if we take capacity / load factor
		// the default load factor is 0.75 according to the javadocs, but lets assign it to be sure
		this.map = new HashMap<BroadphaseKey, SapProxy<E, T>>(initialCapacity * 4 / 3 + 1, 0.75f);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#add(org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void add(E collidable, T fixture) {
		BroadphaseKey key = BroadphaseKey.get(collidable, fixture);
		SapProxy<E, T> proxy = this.map.get(key);
		if (proxy == null) {
			this.add(key, collidable, fixture);
		} else {
			this.update(key, proxy, collidable, fixture);
		}
	}
	
	/**
	 * Internal add method.
	 * <p>
	 * This method assumes the given arguments are all non-null and that the
	 * {@link Collidable} {@link Fixture} is not currently in this broad-phase.
	 * @param key the key for the collidable-fixture pair
	 * @param collidable the collidable
	 * @param fixture the fixture
	 */
	void add(BroadphaseKey key, E collidable, T fixture) {
		Transform tx = collidable.getTransform();
		AABB aabb = fixture.getShape().createAABB(tx);
		// expand the aabb
		aabb.expand(this.expansion);
		// create a new node for the collidable
		SapProxy<E, T> proxy = new SapProxy<E, T>(collidable, fixture, aabb);
		// add the proxy to the map
		this.map.put(key, proxy);
		// insert the node into the tree
		this.tree.add(proxy);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean remove(E collidable, T fixture) {
		BroadphaseKey key = BroadphaseKey.get(collidable, fixture);
		// find the proxy in the map
		SapProxy<E, T> proxy = this.map.remove(key);
		// make sure it was found
		if (proxy != null) {
			// remove the proxy from the tree
			this.tree.remove(proxy);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update(org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void update(E collidable, T fixture) {
		BroadphaseKey key = BroadphaseKey.get(collidable, fixture);
		SapProxy<E, T> proxy = this.map.get(key);
		if (proxy != null) {
			this.update(key, proxy, collidable, fixture);
		} else {
			this.add(key, collidable, fixture);
		}
	}
	
	/**
	 * Internal update method.
	 * <p>
	 * This method assumes the given arguments are all non-null.
	 * @param key the key for the collidable-fixture pair
	 * @param proxy the current node in the tree
	 * @param collidable the collidable
	 * @param fixture the fixture
	 */
	void update(BroadphaseKey key, SapProxy<E, T> proxy, E collidable, T fixture) {
		Transform tx = collidable.getTransform();
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
		// reinsert the proxy
		this.tree.add(proxy);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture)
	 */
	@Override
	public AABB getAABB(E collidable, T fixture) {
		BroadphaseKey key = BroadphaseKey.get(collidable, fixture);
		SapProxy<E, T> proxy = this.map.get(key);
		if (proxy != null) {
			return proxy.aabb;
		}
		return fixture.getShape().createAABB(collidable.getTransform());
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#contains(org.dyn4j.collision.Collidable)
	 */
	@Override
	public boolean contains(E collidable) {
		int size = collidable.getFixtureCount();
		boolean result = true;
		for (int i = 0; i < size; i++) {
			T fixture = collidable.getFixture(i);
			BroadphaseKey key = BroadphaseKey.get(collidable, fixture);
			result &= this.map.containsKey(key);
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#contains(org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean contains(E collidable, T fixture) {
		BroadphaseKey key = BroadphaseKey.get(collidable, fixture);
		return this.map.containsKey(key);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clear()
	 */
	@Override
	public void clear() {
		this.map.clear();
		this.tree.clear();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#size()
	 */
	@Override
	public int size() {
		return this.map.size();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.collision.broadphase.BroadphaseFilter)
	 */
	@Override
	public List<BroadphasePair<E, T>> detect(BroadphaseFilter<E, T> filter) {
		// get the number of proxies
		int size = this.tree.size();
		
		// check the size
		if (size == 0) {
			// return the empty list
			return Collections.emptyList();
		}
		
		// the estimated size of the pair list
		int eSize = Collisions.getEstimatedCollisionPairs(size);
		List<BroadphasePair<E, T>> pairs = new ArrayList<BroadphasePair<E, T>>(eSize);
		
		// clear the tested flags
		Iterator<SapProxy<E, T>> itp = this.tree.iterator();
		while (itp.hasNext()) {
			SapProxy<E, T> p = itp.next();
			p.tested = false;
		}
		
		// find all the possible pairs
		Iterator<SapProxy<E, T>> ito = this.tree.iterator();
		while (ito.hasNext()) {
			// get the current proxy
			SapProxy<E, T> current = ito.next();
			// only check the ones greater than (or equal to) the current item
			SortedSet<SapProxy<E, T>> set = this.tree.tailSet(current, false);
			Iterator<SapProxy<E, T>> iti = set.iterator();
			while (iti.hasNext()) {
				SapProxy<E, T> test = iti.next();
				// dont compare objects against themselves
				if (test.collidable == current.collidable) continue;
				// dont compare object that have already been compared
				if (test.tested) continue;
				// test overlap
				// the >= is to support degenerate intervals created by vertical segments
				if (current.aabb.getMaxX() >= test.aabb.getMinX()) {
					if (current.aabb.overlaps(test.aabb)) {
						if (filter.isAllowed(current.collidable, current.fixture, test.collidable, test.fixture)) {
							pairs.add(new BroadphasePair<E, T>(
									current.collidable,
									current.fixture,
									test.collidable,
									test.fixture));
						}
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
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.geometry.AABB, org.dyn4j.collision.broadphase.BroadphaseFilter)
	 */
	@Override
	public List<BroadphaseItem<E, T>> detect(AABB aabb, BroadphaseFilter<E, T> filter) {
		// get the size of the proxy list
		int size = this.tree.size();
		
		// check the size of the proxy list
		if (size == 0) {
			// return the empty list
			return Collections.emptyList();
		}
		
		List<BroadphaseItem<E, T>> list = new ArrayList<BroadphaseItem<E, T>>(Collisions.getEstimatedCollisionsPerObject());
		
		// create a search proxy for the aabb
		SapProxy<E, T> search = new SapProxy<E, T>(null, null, aabb);
		
		// find the proxy in the tree that is least of all the
		// proxies greater than this one
		SapProxy<E, T> least = this.tree.ceiling(search);
		
		if (least == null) {
			return Collections.emptyList();
		}
		
		// we must check all aabbs up to the found proxy
		// from which point the first aabb to not intersect
		// flags us to stop
		Iterator<SapProxy<E, T>> it = this.tree.iterator();
		boolean found = false;
		while (it.hasNext()) {
			SapProxy<E, T> proxy = it.next();
			// see if we found the proxy
			if (proxy == least) {
				found = true;
			}
			if (proxy.aabb.getMaxX() > aabb.getMinX()) {
				if (proxy.aabb.overlaps(aabb)) {
					if (filter.isAllowed(aabb, proxy.collidable, proxy.fixture)) {
						list.add(new BroadphaseItem<E, T>(
								proxy.collidable,
								proxy.fixture));
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
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#raycast(org.dyn4j.geometry.Ray, double)
	 */
	@Override
	public List<BroadphaseItem<E, T>> raycast(Ray ray, double length, BroadphaseFilter<E, T> filter) {
		// check the size of the proxy list
		if (this.tree.size() == 0) {
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
		int size = this.tree.size();
		
		// check the size of the proxy list
		if (size == 0) {
			// return the empty list
			return Collections.emptyList();
		}
		
		int eSize = Collisions.getEstimatedRaycastCollisions(this.map.size());
		List<BroadphaseItem<E, T>> list = new ArrayList<BroadphaseItem<E, T>>(eSize);
		
		// create a search proxy for the aabb
		SapProxy<E, T> search = new SapProxy<E, T>(null, null, aabb);
		
		// find the proxy in the tree that is least of all the
		// proxies greater than this one
		SapProxy<E, T> ceil = this.tree.ceiling(search);
		
		// we must check all aabbs up to the found proxy
		// from which point the first aabb to not intersect
		// flags us to stop
		Iterator<SapProxy<E, T>> it = this.tree.iterator();
		boolean found = false;
		while (it.hasNext()) {
			SapProxy<E, T> proxy = it.next();
			// see if we found the proxy
			if (proxy == ceil) {
				found = true;
			}
			if (proxy.aabb.getMaxX() > aabb.getMinX()) {
				if (proxy.aabb.overlaps(aabb)) {
					if (this.raycast(s, l, invDx, invDy, proxy.aabb)) {
						if (filter.isAllowed(ray, length, proxy.collidable, proxy.fixture)) {
							list.add(new BroadphaseItem<E, T>(
									proxy.collidable,
									proxy.fixture));
						}
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
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		// loop over all the proxies and translate their aabb
		Iterator<SapProxy<E, T>> it = this.tree.iterator();
		while (it.hasNext()) {
			SapProxy<E, T> proxy = it.next();
			proxy.aabb.translate(shift);
		}
	}
}
