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
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * Implementation of a self-balancing axis-aligned bounding box tree broad-phase collision detection algorithm.
 * <p>
 * This class uses a self-balancing binary tree to store the AABBs.  The AABBs are sorted using the perimeter.
 * The perimeter hueristic is better than area for 2D because axis aligned segments would have zero area.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.0.0
 * @param <E> the {@link Collidable} type
 * @param <T> the {@link Fixture} type
 */
public class DynamicAABBTree<E extends Collidable<T>, T extends Fixture> extends AbstractBroadphaseDetector<E, T> implements BroadphaseDetector<E, T> {
	/** The root node of the tree */
	DynamicAABBTreeNode root;
	
	/** Id to node map for fast lookup */
	final Map<BroadphaseKey, DynamicAABBTreeLeaf<E, T>> map;
	
	/**
	 * Default constructor.
	 */
	public DynamicAABBTree() {
		this(BroadphaseDetector.DEFAULT_INITIAL_CAPACITY);
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * Allows fine tuning of the initial capacity of local storage for faster running times.
	 * @param initialCapacity the initial capacity of local storage
	 * @throws IllegalArgumentException if initialCapacity is less than zero
	 * @since 3.1.1
	 */
	public DynamicAABBTree(int initialCapacity) {
		// 0.75 = 3/4, we can garuantee that the hashmap will not need to be rehashed
		// if we take capacity / load factor
		// the default load factor is 0.75 according to the javadocs, but lets assign it to be sure
		this.map = new LinkedHashMap<BroadphaseKey, DynamicAABBTreeLeaf<E, T>>(initialCapacity * 4 / 3 + 1, 0.75f);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#add(org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void add(E collidable, T fixture) {
		BroadphaseKey key = BroadphaseKey.get(collidable, fixture);
		// see if the collidable-fixture has already been added
		DynamicAABBTreeLeaf<E, T> node = this.map.get(key);
		if (node != null) {
			this.update(key, node, collidable, fixture);
		} else {
			this.add(key, collidable, fixture);
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
		DynamicAABBTreeLeaf<E, T> node = new DynamicAABBTreeLeaf<E, T>(collidable, fixture);
		node.aabb = aabb;
		// add the proxy to the map
		this.map.put(key, node);
		// insert the node into the tree
		this.insert(node);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean remove(E collidable, T fixture) {
		BroadphaseKey key = BroadphaseKey.get(collidable, fixture);
		// find the node in the map
		DynamicAABBTreeLeaf<E, T> node = this.map.remove(key);
		// make sure it was found
		if (node != null) {
			// remove the node from the tree
			this.remove(node);
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
		// get the node from the map
		DynamicAABBTreeLeaf<E, T> node = this.map.get(key);
		// make sure we found it
		if (node != null) {
			// update the node
			this.update(key, node, collidable, fixture);
		} else {
			// add the node
			this.add(key, collidable, fixture);
		}
	}
	
	/**
	 * Internal update method.
	 * <p>
	 * This method assumes the given arguments are all non-null.
	 * @param key the key for the collidable-fixture pair
	 * @param node the current node in the tree
	 * @param collidable the collidable
	 * @param fixture the fixture
	 */
	void update(BroadphaseKey key, DynamicAABBTreeLeaf<E, T> node, E collidable, T fixture) {
		Transform tx = collidable.getTransform();
		// create the new aabb
		AABB aabb = fixture.getShape().createAABB(tx);
		// see if the old aabb contains the new one
		if (node.aabb.contains(aabb)) {
			// if so, don't do anything
			return;
		}
		// otherwise expand the new aabb
		aabb.expand(this.expansion);
		// remove the current node from the tree
		this.remove(node);
		// set the new aabb
		node.aabb = aabb;
		// reinsert the node
		this.insert(node);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture)
	 */
	@Override
	public AABB getAABB(E collidable, T fixture) {
		BroadphaseKey key = BroadphaseKey.get(collidable, fixture);
		DynamicAABBTreeLeaf<E, T> node = this.map.get(key);
		if (node != null) {
			return node.aabb;
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
		this.root = null;
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
		Collection<DynamicAABBTreeLeaf<E, T>> nodes = this.map.values();
		for (DynamicAABBTreeLeaf<E, T> node : nodes) {
			// reset the flag
			node.tested = false;
		}
		
		// the estimated size of the pair list
		int eSize = Collisions.getEstimatedCollisionPairs(size);
		List<BroadphasePair<E, T>> pairs = new ArrayList<BroadphasePair<E, T>>(eSize);
		
		// test each collidable in the list
		for (DynamicAABBTreeLeaf<E, T> node : nodes) {
			// perform a stackless detection routine
			detectNonRecursive(node, this.root, filter, pairs);
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
		return this.detectNonRecursive(aabb, this.root, filter);
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
		
		// create the min and max points
		Vector2 min = new Vector2(
				Math.min(x1, x2),
				Math.min(y1, y2));
		Vector2 max = new Vector2(
				Math.max(x1, x2),
				Math.max(y1, y2));
		
		// create the aabb
		AABB aabb = new AABB(min, max);
		
		// precompute
		double invDx = 1.0 / d.x;
		double invDy = 1.0 / d.y;
		DynamicAABBTreeNode node = this.root;
		
		// get the estimated collision count
		int eSize = Collisions.getEstimatedRaycastCollisions(this.map.size());
		List<BroadphaseItem<E, T>> list = new ArrayList<BroadphaseItem<E, T>>(eSize);
		// perform a iterative, stack-less, traversal of the tree
		while (node != null) {
			// check if the current node overlaps the desired node
			if (aabb.overlaps(node.aabb)) {
				// if they do overlap, then check the left child node
				if (node.left != null) {
					// if the left is not null, then check that subtree
					node = node.left;
					continue;
				} else if (this.raycast(s, l, invDx, invDy, node.aabb)) {
					// if both are null, then this is a leaf node
					@SuppressWarnings("unchecked")
					DynamicAABBTreeLeaf<E, T> leaf = (DynamicAABBTreeLeaf<E, T>)node;
					if (filter.isAllowed(ray, length, leaf.collidable, leaf.fixture)) {
						list.add(new BroadphaseItem<E, T>(leaf.collidable, leaf.fixture));
					}
					// if its a leaf node then we need to go back up the
					// tree and test nodes we haven't yet
				}
			}
			// if the current node is a leaf node or doesnt overlap the
			// desired aabb, then we need to go back up the tree until we
			// find the first left node who's right node is not null
			boolean nextNodeFound = false;
			while (node.parent != null) {
				// check if the current node the left child of its parent
				if (node == node.parent.left) {
					// it is, so check if the right node is non-null
					// NOTE: not need since the tree is a complete tree (every node has two children)
					//if (node.parent.right != null) {
						// it isn't so the sibling node is the next node
						node = node.parent.right;
						nextNodeFound = true;
						break;
					//}
				}
				// if the current node isn't a left node or it is but its
				// sibling is null, go to the parent node
				node = node.parent;
			}
			// if we didn't find it then we are done
			if (!nextNodeFound) break;
		}
		
		return list;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		// we need to update all nodes in the tree (not just the
		// nodes that contain the bodies)
		DynamicAABBTreeNode node = this.root;
		// perform a iterative, stack-less, in order traversal of the tree
		while (node != null) {
			// traverse down the left most tree first
			if (node.left != null) {
				node = node.left;
			} else if (node.right != null) {
				// if the left sub tree is null then go
				// down the right sub tree
				node.aabb.translate(shift);
				node = node.right;
			} else {
				// if both sub trees are null then go back
				// up the tree until we find the first left
				// node who's right node is not null
				node.aabb.translate(shift);
				boolean nextNodeFound = false;
				while (node.parent != null) {
					if (node == node.parent.left) {
						if (node.parent.right != null) {
							node.parent.aabb.translate(shift);
							node = node.parent.right;
							nextNodeFound = true;
							break;
						}
					}
					node = node.parent;
				}
				if (!nextNodeFound) break;
			}
		}
	}
	
	/**
	 * Internal recursive detection method.
	 * @param node the node to test
	 * @param root the root node of the subtree
	 * @param filter the broadphase filter
	 * @param pairs the list of pairs to add to
	 */
	void detect(DynamicAABBTreeLeaf<E, T> node, DynamicAABBTreeNode root, BroadphaseFilter<E, T> filter, List<BroadphasePair<E, T>> pairs) {
		// test the node itself
		if (node.aabb.overlaps(root.aabb)) {
			// check for leaf node
			// non-leaf nodes always have a left child
			if (root.left == null) {
				@SuppressWarnings("unchecked")
				DynamicAABBTreeLeaf<E, T> leaf = (DynamicAABBTreeLeaf<E, T>)root;
				if (!leaf.tested && leaf.collidable != node.collidable) {
					// its a leaf so add the pair
					if (filter.isAllowed(node.collidable, node.fixture, leaf.collidable, leaf.fixture)) {
						BroadphasePair<E, T> pair = new BroadphasePair<E, T>(
								node.collidable,	// A
								node.fixture,
								leaf.collidable,	// B
								leaf.fixture);	
						// add the pair to the list of pairs
						pairs.add(pair);
					}
				}
				// return and check other limbs
				return;
			}
			// they overlap so descend into both children
			if (root.left != null) detect(node, root.left, filter, pairs);
			if (root.right != null) detect(node, root.right, filter, pairs);
		}
	}
	
	/**
	 * Internal non-recursive detection method.
	 * @param node the node to test
	 * @param root the root node of the subtree
	 * @param filter the broadphase filter
	 * @param pairs the list of pairs to add to
	 */
	void detectNonRecursive(DynamicAABBTreeLeaf<E, T> node, DynamicAABBTreeNode root, BroadphaseFilter<E, T> filter, List<BroadphasePair<E, T>> pairs) {
		// start at the root node
		DynamicAABBTreeNode test = root;
		// perform a iterative, stack-less, traversal of the tree
		while (test != null) {
			// check if the current node overlaps the desired node
			if (test.aabb.overlaps(node.aabb)) {
				// if they do overlap, then check the left child node
				if (test.left != null) {
					// if the left is not null, then check that subtree
					test = test.left;
					continue;
				} else {
					@SuppressWarnings("unchecked")
					DynamicAABBTreeLeaf<E, T> leaf = (DynamicAABBTreeLeaf<E, T>)test;
					// if both are null, then this is a leaf node
					// check the tested flag to avoid duplicates and
					// verify we aren't testing the same collidable against
					// itself
					if (!leaf.tested && leaf.collidable != node.collidable) {
						// its a leaf so add the pair
						if (filter.isAllowed(node.collidable, node.fixture, leaf.collidable, leaf.fixture)) {
							BroadphasePair<E, T> pair = new BroadphasePair<E, T>(
									node.collidable,	// A
									node.fixture,
									leaf.collidable,	// B
									leaf.fixture);	
							// add the pair to the list of pairs
							pairs.add(pair);
						}
					}
					// if its a leaf node then we need to go back up the
					// tree and test nodes we haven't yet
				}
			}
			// if the current node is a leaf node or doesnt overlap the
			// desired aabb, then we need to go back up the tree until we
			// find the first left node who's right node is not null
			boolean nextNodeFound = false;
			while (test.parent != null) {
				// check if the current node the left child of its parent
				if (test == test.parent.left) {
					// it is, so check if the right node is non-null
					// NOTE: not need since the tree is a complete tree (every node has two children)
					//if (n.parent.right != null) {
						// it isn't so the sibling node is the next node
						test = test.parent.right;
						nextNodeFound = true;
						break;
					//}
				}
				// if the current node isn't a left node or it is but its
				// sibling is null, go to the parent node
				test = test.parent;
			}
			// if we didn't find it then we are done
			if (!nextNodeFound) break;
		}
	}
	
	/**
	 * Internal recursive {@link AABB} detection method.
	 * @param aabb the {@link AABB} to test
	 * @param node the root node of the subtree
	 * @param filter the broadphase filter
	 * @param list the list to contain the results
	 */
	void detect(AABB aabb, DynamicAABBTreeNode node, BroadphaseFilter<E, T> filter, List<BroadphaseItem<E, T>> list) {
		// test the node itself
		if (aabb.overlaps(node.aabb)) {
			// check for leaf node
			// non-leaf nodes always have a left child
			if (node.left == null) {
				@SuppressWarnings("unchecked")
				DynamicAABBTreeLeaf<E, T> leaf = (DynamicAABBTreeLeaf<E, T>)node;
				// its a leaf so add the collidable
				if (filter.isAllowed(aabb, leaf.collidable, leaf.fixture)) {
					list.add(new BroadphaseItem<E, T>(leaf.collidable, leaf.fixture));
				}
				// return and check other limbs
				return;
			}
			// they overlap so descend into both children
			if (node.left != null) detect(aabb, node.left, filter, list);
			if (node.right != null) detect(aabb, node.right, filter, list);
		}
	}
	
	/**
	 * Internal non-recursive {@link AABB} detection method.
	 * @param aabb the {@link AABB} to test
	 * @param node the root node of the subtree
	 * @param filter the broadphase filter
	 * @return List a list containing the results
	 */
	List<BroadphaseItem<E, T>> detectNonRecursive(AABB aabb, DynamicAABBTreeNode node, BroadphaseFilter<E, T> filter) {
		// get the estimated collision count
		int eSize = Collisions.getEstimatedCollisionsPerObject();
		List<BroadphaseItem<E, T>> list = new ArrayList<BroadphaseItem<E, T>>(eSize);
		// perform a iterative, stack-less, traversal of the tree
		while (node != null) {
			// check if the current node overlaps the desired node
			if (aabb.overlaps(node.aabb)) {
				// if they do overlap, then check the left child node
				if (node.left != null) {
					// if the left is not null, then check that subtree
					node = node.left;
					continue;
				} else {
					// if both are null, then this is a leaf node
					@SuppressWarnings("unchecked")
					DynamicAABBTreeLeaf<E, T> leaf = (DynamicAABBTreeLeaf<E, T>)node;
					if (filter.isAllowed(aabb, leaf.collidable, leaf.fixture)) {
						list.add(new BroadphaseItem<E, T>(leaf.collidable, leaf.fixture));
					}
					// if its a leaf node then we need to go back up the
					// tree and test nodes we haven't yet
				}
			}
			// if the current node is a leaf node or doesnt overlap the
			// desired aabb, then we need to go back up the tree until we
			// find the first left node who's right node is not null
			boolean nextNodeFound = false;
			while (node.parent != null) {
				// check if the current node the left child of its parent
				if (node == node.parent.left) {
					// it is, so check if the right node is non-null
					// NOTE: not need since the tree is a complete tree (every node has two children)
					//if (node.parent.right != null) {
						// it isn't so the sibling node is the next node
						node = node.parent.right;
						nextNodeFound = true;
						break;
					//}
				}
				// if the current node isn't a left node or it is but its
				// sibling is null, go to the parent node
				node = node.parent;
			}
			// if we didn't find it then we are done
			if (!nextNodeFound) break;
		}
		
		return list;
	}
	
	/**
	 * Internal method to insert a node into the tree.
	 * @param item the node to insert
	 */
	void insert(DynamicAABBTreeNode item) {
		// make sure the root is not null
		if (this.root == null) {
			// if it is then set this node as the root
			this.root = item;
			// return from the insert method
			return;
		}
		
		// get the new node's aabb
		AABB itemAABB = item.aabb;
		
		// start looking for the insertion point at the root
		DynamicAABBTreeNode node = this.root;
		// loop until node is a leaf or we find a better location
		while (!node.isLeaf()) {
			// get the current node's aabb
			AABB aabb = node.aabb;
			
			// the perimeter heuristic is better than area for 2D because
			// a line segment aligned with the x or y axis will generate
			// zero area
			
			// get its perimeter
			double perimeter = aabb.getPerimeter();
			
			// union the new node's aabb and the current aabb
			AABB union = aabb.getUnion(itemAABB);
			
			// get the union's perimeter
			double unionPerimeter = union.getPerimeter();
			
			// compute the cost of creating a new parent for the new
			// node and the current node
			double cost = 2 * unionPerimeter;
			
			// compute the minimum cost of descending further down the tree
			double descendCost = 2 * (unionPerimeter - perimeter);
			
			// get the left and right nodes
			DynamicAABBTreeNode left = node.left;
			DynamicAABBTreeNode right = node.right;
			
			// compute the cost of descending to the left
			double costl = 0.0;
			if (left.isLeaf()) {
				AABB u = left.aabb.getUnion(itemAABB);
				costl = u.getPerimeter() + descendCost;
			} else {
				AABB u = left.aabb.getUnion(itemAABB);
				double oldPerimeter = left.aabb.getPerimeter();
				double newPerimeter = u.getPerimeter();
				costl = newPerimeter - oldPerimeter + descendCost;
			}
			// compute the cost of descending to the right
			double costr = 0.0;
			if (right.isLeaf()) {
				AABB u = right.aabb.getUnion(itemAABB);
				costr = u.getPerimeter() + descendCost;
			} else {
				AABB u = right.aabb.getUnion(itemAABB);
				double oldPerimeter = right.aabb.getPerimeter();
				double newPerimeter = u.getPerimeter();
				costr = newPerimeter - oldPerimeter + descendCost;
			}
			
			// see if the cost to create a new parent node for the new
			// node and the current node is better than the children of
			// this node
			if (cost < costl && cost < costr) {
				break;
			}
			
			// if not then choose the next best node to try
			if (costl < costr) {
				node = left;
			} else {
				node = right;
			}
		}
		
		// now that we have found a suitable place, insert a new root
		// node for node and item
		DynamicAABBTreeNode parent = node.parent;
		DynamicAABBTreeNode newParent = new DynamicAABBTreeNode();
		newParent.parent = node.parent;
		newParent.aabb = node.aabb.getUnion(itemAABB);
		newParent.height = node.height + 1;
		
		if (parent != null) {
			// node is not the root node
			if (parent.left == node) {
				parent.left = newParent;
			} else {
				parent.right = newParent;
			}
			
			newParent.left = node;
			newParent.right = item;
			node.parent = newParent;
			item.parent = newParent;
		} else {
			// node is the root item
			newParent.left = node;
			newParent.right = item;
			node.parent = newParent;
			item.parent = newParent;
			this.root = newParent;
		}
		
		// fix the heights and aabbs
		node = item.parent;
		while (node != null) {
			// balance the current tree
			node = balance(node);
			
			DynamicAABBTreeNode left = node.left;
			DynamicAABBTreeNode right = node.right;
			
			// neither node should be null
			node.height = 1 + Math.max(left.height, right.height);
			node.aabb = left.aabb.getUnion(right.aabb);
			
			node = node.parent;
		}
	}
	
	/**
	 * Internal method to remove a node from the tree.
	 * @param node the node to remove
	 */
	void remove(DynamicAABBTreeNode node) {
		// check for an empty tree
		if (this.root == null) return;
		// check the root node
		if (node == this.root) {
			// set the root to null
			this.root = null;
			// return from the remove method
			return;
		}
		
		// get the node's parent, grandparent, and sibling
		DynamicAABBTreeNode parent = node.parent;
		DynamicAABBTreeNode grandparent = parent.parent;
		DynamicAABBTreeNode other;
		if (parent.left == node) {
			other = parent.right;
		} else {
			other = parent.left;
		}
		
		// check if the grandparent is null
		// indicating that the parent is the root
		if (grandparent != null) {
			// remove the node by overwriting the parent node
			// reference in the grandparent with the sibling
			if (grandparent.left == parent) {
				grandparent.left = other;
			} else {
				grandparent.right = other;
			}
			// set the siblings parent to the grandparent
			other.parent = grandparent;
			
			// finally rebalance the tree
			DynamicAABBTreeNode n = grandparent;
			while (n != null) {
				// balance the current subtree
				n = balance(n);
				
				DynamicAABBTreeNode left = n.left;
				DynamicAABBTreeNode right = n.right;
				
				// neither node should be null
				n.height = 1 + Math.max(left.height, right.height);
				n.aabb = left.aabb.getUnion(right.aabb);
				
				n = n.parent;
			}
		} else {
			// the parent is the root so set the root to the sibling
			this.root = other;
			// set the siblings parent to null
			other.parent = null;
		}
	}
	
	/**
	 * Balances the subtree using node as the root.
	 * @param node the root node of the subtree to balance
	 * @return {@link DynamicAABBTreeNode} the new root of the subtree
	 */
	DynamicAABBTreeNode balance(DynamicAABBTreeNode node) {
		DynamicAABBTreeNode a = node;
		
		// see if the node is a leaf node or if
		// it doesn't have enough children to be unbalanced
		if (a.isLeaf() || a.height < 2) {
			// return since there isn't any work to perform
			return a;
		}
		
		// get the nodes left and right children
		DynamicAABBTreeNode b = a.left;
		DynamicAABBTreeNode c = a.right;
		
		// compute the balance factor for node a
		int balance = c.height - b.height;
		
		// if the balance is off on the right side
		if (balance > 1) {
			// get the c's left and right nodes
			DynamicAABBTreeNode f = c.left;
			DynamicAABBTreeNode g = c.right;
			
			// switch a and c
			c.left = a;
			c.parent = a.parent;
			a.parent = c;
			
			// update c's parent to point to c instead of a
			if (c.parent != null) {
				if (c.parent.left == a) {
					c.parent.left = c;
				} else {
					c.parent.right = c;
				}
			} else {
				this.root = c;
			}
			
			// compare the balance of the children of c
			if (f.height > g.height) {
				// rotate left
				c.right = f;
				a.right = g;
				g.parent = a;
				// update the aabb
				a.aabb = b.aabb.getUnion(g.aabb);
				c.aabb = a.aabb.getUnion(f.aabb);
				// update the heights
				a.height = 1 + Math.max(b.height, g.height);
				c.height = 1 + Math.max(a.height, f.height);
			} else {
				// rotate right
				c.right = g;
				a.right = f;
				f.parent = a;
				// update the aabb
				a.aabb = b.aabb.getUnion(f.aabb);
				c.aabb = a.aabb.getUnion(g.aabb);
				// update the heights
				a.height = 1 + Math.max(b.height, f.height);
				c.height = 1 + Math.max(a.height, g.height);
			}
			// c is the new root node of the subtree
			return c;
		}
		// if the balance is off on the left side
		if (balance < -1) {
			// get b's children
			DynamicAABBTreeNode d = b.left;
			DynamicAABBTreeNode e = b.right;
			
			// switch a and b
			b.left = a;
			b.parent = a.parent;
			a.parent = b;
			
			// update b's parent to point to b instead of a
			if (b.parent != null) {
				if (b.parent.left == a) {
					b.parent.left = b;
				} else {
					b.parent.right = b;
				}
			} else {
				this.root = b;
			}
			
			// compare the balance of the children of b
			if (d.height > e.height) {
				// rotate left
				b.right = d;
				a.left = e;
				e.parent = a;
				// update the aabb
				a.aabb = c.aabb.getUnion(e.aabb);
				b.aabb = a.aabb.getUnion(d.aabb);
				// update the heights
				a.height = 1 + Math.max(c.height, e.height);
				b.height = 1 + Math.max(a.height, d.height);
			} else {
				// rotate right
				b.right = e;
				a.left = d;
				d.parent = a;
				// update the aabb
				a.aabb = c.aabb.getUnion(d.aabb);
				b.aabb = a.aabb.getUnion(e.aabb);
				// update the heights
				a.height = 1 + Math.max(c.height, d.height);
				b.height = 1 + Math.max(a.height, e.height);
			}
			// b is the new root node of the subtree
			return b;
		}
		// no balancing required so return the original subtree root node
		return a;
	}
	
	/**
	 * Internal recursive method used to validate the state of the
	 * subtree with the given node as the root.
	 * <p>
	 * Used for testing only.  Test using the -ea flag on the command line.
	 * @param node the root of the subtree to validate
	 */
	void validate(DynamicAABBTreeNode node) {
		// just return if the given node is null
		if (node == null) {
			return;
		}
		// check if the node is the root node
		if (node == this.root) {
			// if so, then make sure its parent is null
			assert(node.parent == null);
		}
		
		// get the left and right children
		DynamicAABBTreeNode left = node.left;
		DynamicAABBTreeNode right = node.right;
		
		// check if the node is a leaf
		if (node.isLeaf()) {
			@SuppressWarnings("unchecked")
			DynamicAABBTreeLeaf<E, T> leaf = (DynamicAABBTreeLeaf<E, T>)node;
			// if so, then both children should be null
			// the height should be zero and the collidable
			// should not be null
			assert(node.left == null);
			assert(node.right == null);
			assert(node.height == 0);
			assert(leaf.collidable != null);
			return;
		}
		
		// if its not a leaf node then check that both the right
		// and the left aabbs are contained within this aabb
		assert(node.aabb.contains(left.aabb));
		if (right != null) assert(node.aabb.contains(right.aabb));
		
		// make sure the parent nodes of the children point to this node
		assert(left.parent == node);
		assert(right.parent == node);
		
		// validate the child subtrees
		validate(left);
		validate(right);
	}
}
