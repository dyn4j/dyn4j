/*
 * Copyright (c) 2010-2017 William Bittle  http://www.dyn4j.org/
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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Collisions;
import org.dyn4j.collision.Fixture;
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
 * @version 3.3.1
 * @since 3.3.1
 * @param <E> the {@link Collidable} type
 * @param <T> the {@link Fixture} type
 */
public class BruteForceBroadphase<E extends Collidable<T>, T extends Fixture> extends AbstractBroadphaseDetector<E, T> implements BroadphaseDetector<E, T> {
	
	/** Id to node map for fast lookup */
	final Map<BroadphaseKey, BruteForceBroadphaseNode<E, T>> map;
	
	/**
	 * Default constructor.
	 */
	public BruteForceBroadphase() {
		this.map = new LinkedHashMap<BroadphaseKey, BruteForceBroadphaseNode<E,T>>();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#add(org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void add(E collidable, T fixture) {
		BroadphaseKey key = BroadphaseKey.get(collidable, fixture);
		BruteForceBroadphaseNode<E, T> node = this.map.get(key);
		
		if (node != null) {
			// if the collidable-fixture has already been added just update it
			node.updateAABB();
		} else {
			// else add the new node
			this.map.put(key, new BruteForceBroadphaseNode<E, T>(collidable, fixture));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean remove(E collidable, T fixture) {
		BroadphaseKey key = BroadphaseKey.get(collidable, fixture);
		// find the node in the map
		BruteForceBroadphaseNode<E, T> node = this.map.remove(key);
		
		return node != null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update(org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void update(E collidable, T fixture) {
		add(collidable, fixture);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture)
	 */
	@Override
	public AABB getAABB(E collidable, T fixture) {
		BroadphaseKey key = BroadphaseKey.get(collidable, fixture);
		BruteForceBroadphaseNode<E, T> node = this.map.get(key);
		
		if (node != null) {
			return node.aabb;
		}
		
		return fixture.getShape().createAABB(collidable.getTransform());
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
		// clear all the tested flags on the nodes
		int size = this.map.size();
		Collection<BruteForceBroadphaseNode<E, T>> nodes = this.map.values();
		for (BruteForceBroadphaseNode<E, T> node : nodes) {
			// reset the flag
			node.tested = false;
		}
		
		// the estimated size of the pair list
		int eSize = Collisions.getEstimatedCollisionPairs(size);
		List<BroadphasePair<E, T>> pairs = new ArrayList<BroadphasePair<E, T>>(eSize);
		
		// test each collidable in the collection
		for (BruteForceBroadphaseNode<E, T> node : nodes) {
			for (BruteForceBroadphaseNode<E, T> other : nodes) {
				if (node.aabb.overlaps(other.aabb) && !other.tested && other.collidable != node.collidable) {
					// if they overlap and not already tested
					if (filter.isAllowed(node.collidable, node.fixture, other.collidable, other.fixture)) {
						BroadphasePair<E, T> pair = new BroadphasePair<E, T>(
								node.collidable,	// A
								node.fixture,
								other.collidable,	// B
								other.fixture);	
						
						// add the pair to the list of pairs
						pairs.add(pair);
					}
				}
			}
			
			// update the tested flag
			node.tested = true;
		}
		
		// return the list of pairs
		return pairs;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.geometry.AABB)
	 */
	@Override
	public List<BroadphaseItem<E, T>> detect(AABB aabb, BroadphaseFilter<E, T> filter) {
		// the estimated size of the item list
		int eSize = Collisions.getEstimatedCollisionsPerObject();
		List<BroadphaseItem<E, T>> list = new ArrayList<BroadphaseItem<E, T>>(eSize);
		Collection<BruteForceBroadphaseNode<E, T>> nodes = this.map.values();
		
		// test each collidable in the collection
		for (BruteForceBroadphaseNode<E, T> node : nodes) {
			if (aabb.overlaps(node.aabb)) {
				if (filter.isAllowed(aabb, node.collidable, node.fixture)) {
					list.add(new BroadphaseItem<E, T>(node.collidable, node.fixture));
				}
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
		if (this.map.size() == 0) {
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
		
		// create the aabb
		AABB aabb = AABB.createAABBFromPoints(x1, y1, x2, y2);
		
		// precompute
		double invDx = 1.0 / d.x;
		double invDy = 1.0 / d.y;
		
		// get the estimated collision count
		int eSize = Collisions.getEstimatedRaycastCollisions(this.map.size());
		List<BroadphaseItem<E, T>> list = new ArrayList<BroadphaseItem<E, T>>(eSize);
		Collection<BruteForceBroadphaseNode<E, T>> nodes = this.map.values();
		
		for (BruteForceBroadphaseNode<E, T> node : nodes) {
			if (aabb.overlaps(node.aabb) && this.raycast(s, l, invDx, invDy, node.aabb)) {
				if (filter.isAllowed(ray, length, node.collidable, node.fixture)) {
					list.add(new BroadphaseItem<E, T>(node.collidable, node.fixture));
				}
			}
		}
		
		return list;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		Collection<BruteForceBroadphaseNode<E, T>> nodes = this.map.values();
		
		for (BruteForceBroadphaseNode<E, T> node : nodes) {
			node.aabb.translate(shift);
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABBExpansion()
	 */
	@Override
	public double getAABBExpansion() {
		return 0;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#supportsAABBExpansion()
	 */
	@Override
	public boolean supportsAABBExpansion() {
		return false;
	}
}
