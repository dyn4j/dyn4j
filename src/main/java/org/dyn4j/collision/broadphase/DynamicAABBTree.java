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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

/**
 * Implementation of a self-balancing axis-aligned bounding box tree broad-phase collision detection algorithm.
 * <p>
 * This class uses a self-balancing binary tree to store the AABBs.  The AABBs are sorted using the perimeter.
 * The perimeter hueristic is better than area for 2D because axis aligned segments would have zero area.
 * @author William Bittle
 * @version 4.1.0
 * @since 3.0.0
 * @param <T> the object type
 */
public final class DynamicAABBTree<T> extends AbstractBroadphaseDetector<T> {
	/** The root node of the tree */
	private DynamicAABBTreeNode root;
	
	/** Id to node map for fast lookup */
	private final Map<T, DynamicAABBTreeLeaf<T>> leaves;
	
	/** Map to store what objects were updated since the last detection phase */
	private final Map<T, DynamicAABBTreeLeaf<T>> updated;
	
	/** A reusable {@link AABB} for updates to reduce allocation */
	private final AABB updatedAABB;

	/**
	 * Default constructor.
	 * @param broadphaseFilter the broadphase filter
	 * @param aabbProducer the AABB producer
	 * @param aabbExpansionMethod the AABB expansion method
	 * @throws NullPointerException if broadphaseFilter, aabbProducer or aabbExpansionMethod are null
	 */
	public DynamicAABBTree(BroadphaseFilter<T> broadphaseFilter, AABBProducer<T> aabbProducer, AABBExpansionMethod<T> aabbExpansionMethod) {
		this(broadphaseFilter, aabbProducer, aabbExpansionMethod, BroadphaseDetector.DEFAULT_INITIAL_CAPACITY);
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * Allows fine tuning of the initial capacity of local storage for faster running times.
	 * @param broadphaseFilter the broadphase filter
	 * @param aabbProducer the AABB producer; cannot be null
	 * @param aabbExpansionMethod the AABB expansion method; cannot be null
	 * @param initialCapacity the initial capacity of local storage
	 * @throws NullPointerException if broadphaseFilter, aabbProducer or aabbExpansionMethod are null
	 * @throws IllegalArgumentException if initialCapacity is less than zero
	 * @since 3.1.1
	 */
	public DynamicAABBTree(BroadphaseFilter<T> broadphaseFilter, AABBProducer<T> aabbProducer, AABBExpansionMethod<T> aabbExpansionMethod, int initialCapacity) {
		super(broadphaseFilter, aabbProducer, aabbExpansionMethod);
		// 0.75 = 3/4, we can garuantee that the hashmap will not need to be rehashed
		// if we take capacity / load factor
		// the default load factor is 0.75 according to the javadocs, but lets assign it to be sure
		this.leaves = new LinkedHashMap<T, DynamicAABBTreeLeaf<T>>(initialCapacity * 4 / 3 + 1, 0.75f);
		this.updated = new LinkedHashMap<T, DynamicAABBTreeLeaf<T>>(initialCapacity * 4 / 3 + 1, 0.75f);
		this.updatedAABB = new AABB(0,0,0,0);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(java.lang.Object)
	 */
	@Override
	public AABB getAABB(T object) {
		DynamicAABBTreeLeaf<T> node = this.leaves.get(object);
		if (node != null) {
			return node.aabb;
		} else {
			AABB aabb = this.aabbProducer.compute(object);
			if (aabb.isDegenerate()) {
				return aabb;
			}
			
			this.aabbExpansionMethod.expand(object, aabb);
			return aabb;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#add(java.lang.Object)
	 */
	@Override
	public void add(T obj) {
		// see if the body-fixture has already been added
		DynamicAABBTreeLeaf<T> node = this.leaves.get(obj);
		if (node != null) {
			this.updateNode(obj, node);
		} else {
			this.addNode(obj);
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
	private void addNode(T obj) {
		// compute the aabb
		this.aabbProducer.compute(obj, this.updatedAABB);
		// expand the aabb
		this.aabbExpansionMethod.expand(obj, this.updatedAABB);
		// create a new node for the body
		DynamicAABBTreeLeaf<T> node = new DynamicAABBTreeLeaf<T>(obj);
		node.aabb.set(this.updatedAABB);
		// add the proxy to the map
		this.leaves.put(obj, node);
		// insert the node into the tree
		this.insert(node);
		// are we tracking updates?
		if (this.updateTrackingEnabled) {
			this.updated.put(obj, node);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(T obj) {
		// find the node in the map
		DynamicAABBTreeLeaf<T> node = this.leaves.remove(obj);
		// make sure it was found
		if (node != null) {
			// remove the node from the tree
			this.updated.remove(obj);
			this.remove(node);
			return true;
		}
		return false;
	}
	
	public void update() {
		for (DynamicAABBTreeLeaf<T> node : this.leaves.values()) {
			this.update(node.item);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update(java.lang.Object)
	 */
	@Override
	public void update(T obj) {
		// get the node from the map
		DynamicAABBTreeLeaf<T> node = this.leaves.get(obj);
		// make sure we found it
		if (node != null) {
			// update the node
			this.updateNode(obj, node);
		} else {
			// add the node
			this.addNode(obj);
		}
	}
	
	/**
	 * Internal update method.
	 * <p>
	 * This method assumes the given arguments are all non-null.
	 * @param key the key for the body-fixture pair
	 * @param node the current node in the tree
	 * @param body the body
	 * @param fixture the fixture
	 */
	private void updateNode(T obj, DynamicAABBTreeLeaf<T> node) {
		// compute the AABB
		this.aabbProducer.compute(obj, this.updatedAABB);
		
		// see if the old AABB contains the new one
		// NOTE: the old AABB is the expanded AABB and this is how
		// we handle what's been updated vs. not
		boolean isNonExpanedNewContainedInExpandedOld = node.aabb.contains(this.updatedAABB);

		// now expand the AABB so that we can do some additional
		// checking on it and so we can use it later if we find
		// we need to update it
		this.aabbExpansionMethod.expand(obj, this.updatedAABB);
		
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
			double p0 = node.aabb.getPerimeter();
			double p1 = this.updatedAABB.getPerimeter();
			double ratio = p0 / p1;
			if (ratio <= AABB_REDUCTION_RATIO) {
				// if the old AABB is 2x (or less) the size (in perimeter) to the new
				// then we'll accept it and not update
				return;
			}
		}

		// remove the current node from the tree
		this.remove(node);
		// set the new aabb
		node.aabb.set(this.updatedAABB);
		// reinsert the node
		this.insert(node);
		// are we tracking updates?
		if (this.updateTrackingEnabled) {
			this.updated.put(obj, node);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdated(java.lang.Object)
	 */
	@Override
	public boolean isUpdated(T obj) {
		if (!this.leaves.containsKey(obj)) {
			return false;
		}
		
		if (!this.updateTrackingEnabled) {
			return true; 
		}
		
		return this.updated.containsKey(obj);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#setUpdated(java.lang.Object)
	 */
	@Override
	public void setUpdated(T obj) {
		if (!this.updateTrackingEnabled) {
			return;
		}
		
		DynamicAABBTreeLeaf<T> node = this.leaves.get(obj);
		this.updated.put(obj, node);
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
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clearUpdates()
	 */
	@Override
	public void clearUpdates() {
		this.updated.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(T obj) {
		return this.leaves.containsKey(obj);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clear()
	 */
	@Override
	public void clear() {
		this.leaves.clear();
		this.updated.clear();
		this.root = null;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#size()
	 */
	@Override
	public int size() {
		return this.leaves.size();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detectIterator(boolean)
	 */
	@Override
	public Iterator<CollisionPair<T>> detectIterator(boolean forceFullDetection) {
		if (forceFullDetection || !this.updateTrackingEnabled) {
			return new DetectPairsIterator(this.leaves.values().iterator());
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
		// we need to update all nodes in the tree (not just the
		// nodes that contain the bodies)
		DynamicAABBTreeNode node = this.root;
		// perform a iterative, stack-less, in order traversal of the tree
		while (node != null) {
			// traverse down the left most tree first
			if (node.left != null) {
				node = node.left;
			} else if (node.right != null) {
				// NOTE: should never happen since this tree is a complete tree
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

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdateTrackingSupported()
	 */
	@Override
	public boolean isUpdateTrackingSupported() {
		return true;
	}
	
	/**
	 * Internal method to insert a node into the tree.
	 * @param item the node to insert
	 */
	private void insert(DynamicAABBTreeNode item) {
		// make sure the root is not null
		if (this.root == null) {
			// if it is then set this node as the root
			this.root = item;
			// return from the insert method
			return;
		}
		
		AABB temp = new AABB(0,0,0,0);
		
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
			// get the union's perimeter
			double unionPerimeter = temp.set(aabb).union(itemAABB).getPerimeter();
			
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
				costl = temp.union(left.aabb, itemAABB).getPerimeter() + descendCost;
			} else {
				double oldPerimeter = left.aabb.getPerimeter();
				double newPerimeter = temp.union(left.aabb, itemAABB).getPerimeter();
				costl = newPerimeter - oldPerimeter + descendCost;
			}
			
			// compute the cost of descending to the right
			double costr = 0.0;
			if (right.isLeaf()) {
				costr = temp.union(right.aabb, itemAABB).getPerimeter() + descendCost;
			} else {
				double oldPerimeter = right.aabb.getPerimeter();
				double newPerimeter = temp.union(right.aabb, itemAABB).getPerimeter();
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
//		newParent.aabb = node.aabb.getUnion(itemAABB);
//		newParent.aabb.set(node.aabb.getUnion(itemAABB));
		newParent.aabb.union(node.aabb, itemAABB);
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
			// the node's AABB should be the union of it's children
			node.aabb.union(left.aabb, right.aabb);
			
			node = node.parent;
		}
	}
	
	/**
	 * Internal method to remove a node from the tree.
	 * @param node the node to remove
	 */
	private void remove(DynamicAABBTreeNode node) {
		// check for an empty tree
		// should never happen based on current usage
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
//				n.aabb.set(left.aabb).union(right.aabb);
				n.aabb.union(left.aabb, right.aabb);
				
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
	private DynamicAABBTreeNode balance(DynamicAABBTreeNode node) {
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
//				a.aabb.set(b.aabb).union(g.aabb);
				a.aabb.union(b.aabb, g.aabb);
//				c.aabb.set(a.aabb).union(f.aabb);
				c.aabb.union(a.aabb, f.aabb);
				// update the heights
				a.height = 1 + Math.max(b.height, g.height);
				c.height = 1 + Math.max(a.height, f.height);
			} else {
				// rotate right
				c.right = g;
				a.right = f;
				f.parent = a;
				// update the aabb
//				a.aabb.set(b.aabb).union(f.aabb);
				a.aabb.union(b.aabb, f.aabb);
//				c.aabb.set(a.aabb).union(g.aabb);
				c.aabb.union(a.aabb, g.aabb);
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
//				a.aabb.set(c.aabb).union(e.aabb);
				a.aabb.union(c.aabb, e.aabb);
//				b.aabb.set(a.aabb).union(d.aabb);
				b.aabb.union(a.aabb, d.aabb);
				// update the heights
				a.height = 1 + Math.max(c.height, e.height);
				b.height = 1 + Math.max(a.height, d.height);
			} else {
				// rotate right
				b.right = e;
				a.left = d;
				d.parent = a;
				// update the aabb
//				a.aabb.set(c.aabb).union(d.aabb);
				a.aabb.union(c.aabb, d.aabb);
//				b.aabb.set(a.aabb).union(e.aabb);
				b.aabb.union(a.aabb, e.aabb);
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
	 * Returns the height of the tree.
	 * @return int
	 */
	public int getHeight() {
		if (this.root == null) return 0;
		return this.root.height;
	}
	
	/**
	 * Returns a quality metric for the tree.
	 * @return double
	 * @since 4.0.0
	 */
	public double getPerimeterRatio()
	{
		if (this.root == null) return 0.0;

		double root = this.root.aabb.getPerimeter();
		double total = this.getPerimeterRatio(this.root);

		return total / root;
	}
	
	/**
	 * Returns the quality metric for the given subtree.
	 * @param node the subtree root node
	 * @return double
	 * @since 4.0.0
	 */
	private double getPerimeterRatio(DynamicAABBTreeNode node) {
		if (node == null) return 0;
		double total = node.aabb.getPerimeter();
		total += getPerimeterRatio(node.left);
		total += getPerimeterRatio(node.right);
		return total;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#optimize()
	 */
	public void optimize() {
		if (this.root == null) return;

		this.root = null;

		// get all the leaves
		List<DynamicAABBTreeLeaf<T>> leaves = new ArrayList<DynamicAABBTreeLeaf<T>>(this.leaves.values());
		
		// sort them by their perimeter
		Collections.sort(leaves, new Comparator<DynamicAABBTreeLeaf<T>>() {
	    	@Override
	    	public int compare(DynamicAABBTreeLeaf<T> o1, DynamicAABBTreeLeaf<T> o2) {
	    		double p1 = o1.aabb.getPerimeter();
	    		double p2 = o2.aabb.getPerimeter();
	    		double diff = p2 - p1;
	    		int result = diff < 0 ? -1 : diff > 0 ? 1 : 0;
	    		return result;
	    	}
		});
		
		// re-insert them in order
		for (DynamicAABBTreeLeaf<T> leaf : leaves) {
			leaf.height = 0;
			leaf.left = null;
			leaf.right = null;
			leaf.parent = null;
			this.insert(leaf);
		}
	}

	/**
	 * Validates the tree structure.
	 * @return true if the structure is valid
	 * @since 4.1.0
	 */
	boolean isValid() {
		return this.isValid(this.root);
	}
	
	/**
	 * Internal recursive method used to validate the state of the
	 * subtree with the given node as the root.
	 * @param node the root node of the tree
	 * @return boolean true if the subtree is valid
	 */
	boolean isValid(DynamicAABBTreeNode node) {
		// just return if the given node is null
		if (node == null) {
			return true;
		}
		
		// check if the node is the root node
		if (node == this.root) {
			// if so, then make sure its parent is null
			if (node.parent != null) return false;
		}
		
		// get the left and right children
		DynamicAABBTreeNode left = node.left;
		DynamicAABBTreeNode right = node.right;
		
		// check if the node is a leaf
		if (node.isLeaf()) {
			@SuppressWarnings("unchecked")
			DynamicAABBTreeLeaf<T> leaf = (DynamicAABBTreeLeaf<T>)node;
			// if so, then both children should be null
			// the height should be zero and the body
			// should not be null
			if (node.left != null ||
				node.right != null ||
				node.height != 0 ||
				leaf.item == null) {
				return false;
			}
			
			return true;
		}
		
		// if its not a leaf node then check that both the right
		// and the left aabbs are contained within this aabb
		if (!node.aabb.contains(left.aabb)) {
			return false;
		}
		
		if (right != null && !node.aabb.contains(right.aabb)) {
			return false;
		}
		
		// make sure the parent nodes of the children point to this node
		if (left.parent != node) return false;
		if (right.parent != node) return false;
		
		// validate the child subtrees
		boolean leftValid = this.isValid(left);
		boolean rightValid = this.isValid(right);
		
		return leftValid && rightValid;
	}
	
	/**
	 * A specialized iterator for detecting pairs of colliding {@link AABB}s in this broaphase.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	private final class DetectPairsIterator implements Iterator<CollisionPair<T>> {
		/** The iterator for all {@link AABB}s to test against the broadphase */
		private final Iterator<DynamicAABBTreeLeaf<T>> iterator;
		
		/** A map to track the pairs already tested */
		private final Map<T, Boolean> tested;
		
		/** Internal state to track the current {@link AABB} we're testing the broadphase with */
		private DynamicAABBTreeLeaf<T> currentLeaf;
		
		/** Internal state to track the node in the tree we're testing against */
		private DynamicAABBTreeNode currentNode;
		
		/** A reusable pair to output collisions */
		private final BroadphasePair<T> currentPair;
		
		/** A reusable pair to output collisions */
		private final BroadphasePair<T> nextPair;
		
		/** True if there's another pair */
		private boolean hasNext;
		
		/**
		 * Minimal constructor.
		 * @param bodyIterator an Iterator for the {@link AABB}s to test agains the broadphase
		 */
		public DetectPairsIterator(Iterator<DynamicAABBTreeLeaf<T>> bodyIterator) {
			this.iterator = bodyIterator;
			this.tested = new HashMap<T, Boolean>();
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
		 * Returns true if there's another pair to process and sets
		 * the nextPair field to that pair.
		 * @return boolean
		 */
		private boolean findNext() {
			// iterate through the list of AABBs to test the entire
			// broadphase against
			while (this.iterator.hasNext() || this.currentLeaf != null) {
				// if the current AABB is null, then grab a new one
				if (this.currentLeaf == null) {
					this.currentLeaf = this.iterator.next();
				}
				
				// if the current node in the broadphase is null
				// then we need to start at the root
				if (this.currentNode == null) {
					// start at the root node
					this.currentNode = DynamicAABBTree.this.root;
				}
			
				// is there another collision with the current leaf?
				if (this.findNextForCurrentLeaf()) {
					return true;
				}
				
				// if not we need to move to the next leaf
			}
			
			return false;
		}
		
		/**
		 * Conversion of the non-recursive detection method into a finite state machine.
		 * <p>
		 * This method returns true if there's a "next" collision and places the next collision
		 * result in storage to be reported in the call to the {@link #next()} method.
		 * @return boolean
		 */
		private boolean findNextForCurrentLeaf() {
			boolean foundCollision = false;
			
			// find the next collision pair (if there is one)
			DynamicAABBTreeLeaf<T> node = this.currentLeaf;
			DynamicAABBTreeNode test = this.currentNode;
			
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
						DynamicAABBTreeLeaf<T> leaf = (DynamicAABBTreeLeaf<T>)test;
						// if both are null, then this is a leaf node
						
						// don't compare nodes among themselves
						if (DynamicAABBTree.this.broadphaseFilter.isAllowed(leaf.item, node.item)) {
							// have we already tested this pair?
							boolean tested = this.tested.containsKey(leaf.item);
							
							// check the tested flag to avoid duplicates and
							// verify we aren't testing the same body against
							// itself
							if (!tested) {
								// its a leaf so we have a collision
								this.nextPair.first = node.item;
								this.nextPair.second = leaf.item;
								
								// we can't return here because we need to advance the detection
								// to the next node to test before we exit from this method
								foundCollision = true;
							}
							// if its a leaf node then we need to go back up the
							// tree and test nodes we haven't yet
						}
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
				
				// update the current node so we can pick up where we left off
				this.currentNode = test;
				
				// if we didn't find it then we are done
				if (!nextNodeFound) {
					// this indicates that we're done testing the currentLeaf against
					// the entire broadphase
					
					// make sure the leaf is marked as already tested
					this.tested.put(this.currentLeaf.item, true);
					
					// make sure the next call to hasNext gets the next AABB to test
					this.currentLeaf = null;
					
					// make sure the testing begins at the root node
					this.currentNode = null;
					break;
				}
				
				// if we found a collision then we need to stop
				if (foundCollision) {
					break;
				}
			}
			
			return foundCollision;
		}
		
	}

	/**
	 * A specialized iterator for testing an {@link AABB} against this broadphase.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	private final class DetectAABBIterator implements Iterator<T> {
		/** The {@link AABB} to test with */
		private final AABB aabb;
		
		/** Internal state to track the node in the tree we're testing against */
		private DynamicAABBTreeNode currentNode;
		
		/** The next item to return */
		private T nextItem;
		
		/**
		 * Minimal constructor.
		 * @param aabb the {@link AABB} to test
		 */
		public DetectAABBIterator(AABB aabb) {
			this.aabb = aabb;
			this.currentNode = DynamicAABBTree.this.root;
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
		 * Conversion of the non-recursive detection method into a finite state machine.
		 * <p>
		 * This method returns true if there's a "next" collision and places the next collision
		 * result in storage to be reported in the call to the {@link #next()} method.
		 * @return boolean
		 */
		private boolean findNext() {
			this.nextItem = null;
			boolean foundCollision = false;
			
			// start where we left off
			DynamicAABBTreeNode node = this.currentNode;
			
			// perform a iterative, stack-less, traversal of the tree
			while (node != null) {
				// check if the current node overlaps the desired node
				if (this.aabb.overlaps(node.aabb)) {
					// if they do overlap, then check the left child node
					if (node.left != null) {
						// if the left is not null, then check that subtree
						node = node.left;
						continue;
					} else {
						// if both are null, then this is a leaf node
						@SuppressWarnings("unchecked")
						DynamicAABBTreeLeaf<T> leaf = (DynamicAABBTreeLeaf<T>)node;
						
						// record the collision details
						this.nextItem = leaf.item;
						
						// we can't return here because we need to advance the detection
						// to the next node to test before we exit from this method
						foundCollision = true;
						
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
				
				// update the current node so we can pick up where we left off
				this.currentNode = node;
				
				// if we didn't find another node to test then we are done
				if (!nextNodeFound) {
					this.currentNode = null;
					break;
				}
				
				// if we found a collision then we need to stop
				if (foundCollision) {
					break;
				}
			}
			
			return foundCollision;
		}
	}

	/**
	 * A specialized iterator for detecting ray collisions in this broaphase.
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
		
		/** Internal state to track the node in the tree we're testing against */
		private DynamicAABBTreeNode currentNode;
		
		/** The next item to return */
		private T nextItem;
		
		/**
		 * Minimal constructor.
		 * @param ray the {@link Ray}
		 * @param length the length of the ray
		 */
		public DetectRayIterator(Ray ray, double length) {
			this.ray = ray;
			this.currentNode = DynamicAABBTree.this.root;
			
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
		 * Conversion of the non-recursive detection method into a finite state machine.
		 * <p>
		 * This method returns true if there's a "next" collision and places the next collision
		 * result in storage to be reported in the call to the {@link #next()} method.
		 * @return boolean
		 */
		private boolean findNext() {
			this.nextItem = null;
			boolean foundCollision = false;
			
			// start where we left off
			DynamicAABBTreeNode node = this.currentNode;
			
			// perform a iterative, stack-less, traversal of the tree
			while (node != null) {
				// check if the current node overlaps the desired node
				if (aabb.overlaps(node.aabb)) {
					// if they do overlap, then check the left child node
					if (node.left != null) {
						// if the left is not null, then check that subtree
						node = node.left;
						continue;
					} else if (AbstractBroadphaseDetector.raycast(this.ray.getStart(), this.length, this.invDx, this.invDy, node.aabb)) {
						// if both are null, then this is a leaf node
						@SuppressWarnings("unchecked")
						DynamicAABBTreeLeaf<T> leaf = (DynamicAABBTreeLeaf<T>)node;

						// set the collision details
						this.nextItem = leaf.item;
						
						// we can't return here because we need to advance the detection
						// to the next node to test before we exit from this method
						foundCollision = true;
						
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
				
				// update the current node so we can pick up where we left off
				this.currentNode = node;
				
				// if we didn't find it then we are done
				if (!nextNodeFound) {
					this.currentNode = null;
					break;
				}
				
				// if we found a collision then exit so we can report it
				if (foundCollision) {
					break;
				}
			}
			
			return foundCollision;
		}
	}
}
