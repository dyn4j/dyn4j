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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Collisions;
import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

/**
 * Implementation of a self-balancing axis-aligned bounding box tree broad-phase collision detection algorithm.
 * <p>
 * This class implements a aabb tree broad-phase detector that is based on ideas from {@link DynamicAABBTree} but with some very critical improvements.
 * This data structure is lazy in the sense that it will build the actual tree as late as possible (hence the name).
 * Performance is optimized for fast detection of collisions, as required by the {@link World} class. Raycasting and other functionalities should see no big improvements.
 * Insertion is O(1), update is O(logn) but batch update (update of all bodies) is O(n), remove is O(logn) average but O(n) worse.
 * <p>
 * The class will rebuild the whole tree at each detection and will detect the collisions at the same time in an efficient manner.
 * <p>
 * This structure keeps the bodies sorted by the radius of their fixtures and rebuilds the tree each time in order to construct better trees.
 * 
 * @author Manolis Tsamis
 * @version 4.0.0
 * @since 3.4.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 * @deprecated Deprecated in 4.0.0. Use a different {@link BroadphaseDetector} instead.
 */
@Deprecated
public final class LazyAABBTree<T extends CollisionBody<E>, E extends Fixture> extends AbstractBroadphaseDetector<T, E> implements BatchBroadphaseDetector<T, E> {
	/** The root node of the tree */
	LazyAABBTreeNode root;
	
	/** Id to leaf map for fast lookup in tree of list */
	final Map<CollisionItem<T, E>, LazyAABBTreeLeaf<T, E>> elementMap;
	
	/** List of all leafs, either on tree or not */
	final List<LazyAABBTreeLeaf<T, E>> elements;
	
	/** Whether there's new data to sort */
	boolean sorted = true;
	
	/** Whether there are leafs waiting to be batch-removed */
	boolean pendingRemoves = false;
	
	/** Whether there are leafs waiting to be batch-inserted */
	boolean pendingInserts = false;
	
	/**
	 * Default constructor.
	 */
	public LazyAABBTree() {
		this(BroadphaseDetector.DEFAULT_INITIAL_CAPACITY);
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * Allows fine tuning of the initial capacity of local storage for faster running times.
	 * 
	 * @param initialCapacity the initial capacity of local storage
	 * @throws IllegalArgumentException if initialCapacity is less than zero
	 */
	public LazyAABBTree(int initialCapacity) {
		this.elements = new ArrayList<LazyAABBTreeLeaf<T, E>>(initialCapacity);
		this.elementMap = new HashMap<CollisionItem<T, E>, LazyAABBTreeLeaf<T, E>>(initialCapacity);
	}
	
	/**
	 * Destroys the existing tree in O(n) time and prepares for batch-detection,
	 * but does not update the AABBs of elements.
	 */
	void batchRebuild() {
		for (LazyAABBTreeLeaf<T, E> node : this.elements) {
			node.setOnTree(false);
		}
		
		this.root = null;
		this.pendingInserts = true;
	}
	
	/**
	 * Destroys the existing tree in O(n) time and prepares for batch-detection while
	 * also updating all AABBs. Called by {@link World} in each step before detection.
	 */
	@Override
	public void batchUpdate() {
		for (LazyAABBTreeLeaf<T, E> node : this.elements) {
			node.setOnTree(false);
			node.updateAABB();
		}
		
		this.root = null;
		this.pendingInserts = true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#add(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void add(T body, E fixture) {
		// create a new node for the body
		CollisionItem<T, E> key = new BroadphaseItem<T, E>(body, fixture);
		LazyAABBTreeLeaf<T, E> existing = this.elementMap.get(key);
		
		if (existing != null) {
			// update existing node
			if (existing.isOnTree()) {
				this.remove(existing);
				existing.setOnTree(false);
			}
			
			existing.updateAABB();
		} else {
			// add new node
			LazyAABBTreeLeaf<T, E> node = new LazyAABBTreeLeaf<T, E>(body, fixture);
			
			this.elementMap.put(key, node);
			this.elements.add(node);
			
			this.sorted = false;
		}
		
		this.pendingInserts = true;
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
		// find the node in the map
		LazyAABBTreeLeaf<T, E> node = this.elementMap.remove(item);
		// make sure it was found
		
		if (node != null) {
			if (node.isOnTree()) {
				// remove the node from the tree
				// since the node is on the tree we know that the root is not null
				// so we can safely call removeImpl
				this.remove(node);
			}
			
			node.markForRemoval();
			this.pendingRemoves = true;
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Internal method to remove a leaf from the tree.
	 * Assumes the root is not null.
	 * 
	 * @param leaf the leaf to remove
	 */
	void remove(LazyAABBTreeLeaf<T, E> leaf) {
		// check the root node
		if (leaf == this.root) {
			// set the root to null
			this.root = null;
			// return from the remove method
			return;
		}
		
		// get the node's parent, grandparent, and sibling
		LazyAABBTreeNode parent = leaf.parent;
		LazyAABBTreeNode grandparent = parent.parent;
		LazyAABBTreeNode other = leaf.getSibling();
		
		// check if the grandparent is null
		// indicating that the parent is the root
		if (grandparent != null) {
			// remove the node by overwriting the parent node
			// reference in the grandparent with the sibling
			grandparent.replaceChild(parent, other);
			// set the siblings parent to the grandparent
			other.parent = grandparent;
			
			// finally rebalance the tree
			this.balanceAll(grandparent);
		} else {
			// the parent is the root so set the root to the sibling
			this.root = other;
			// set the siblings parent to null
			other.parent = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public void update(T body, E fixture) {
		// In the way the add and update are described in BroadphaseDetector, their functionallity is identical
		// so just redirect the work to add for less duplication.
		this.add(body, fixture);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clearUpdates()
	 */
	@Override
	public void clearUpdates() {
		// no-op
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdated(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean isUpdated(T body, E fixture) {
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
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdated(org.dyn4j.collision.CollisionItem)
	 */
	@Override
	public boolean isUpdated(CollisionItem<T, E> item) {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(org.dyn4j.collision.CollisionItem)
	 */
	@Override
	public AABB getAABB(CollisionItem<T, E> item) {
		LazyAABBTreeLeaf<T, E> node = this.elementMap.get(item);
		
		if (node != null && !node.mustRemove()) {
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
		return this.elementMap.containsKey(key);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#contains(org.dyn4j.collision.CollisionItem)
	 */
	@Override
	public boolean contains(CollisionItem<T, E> item) {
		return this.elementMap.containsKey(item);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clear()
	 */
	@Override
	public void clear() {
		this.elementMap.clear();
		this.elements.clear();
		this.root = null;
		
		// Important: since everything is removed there's no pending work to do
		this.sorted = true;
		this.pendingRemoves = false;
		this.pendingInserts = false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#size()
	 */
	@Override
	public int size() {
		return this.elements.size();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.collision.broadphase.BroadphaseFilter)
	 */
	@Deprecated
	@Override
	public List<CollisionPair<T, E>> detect(BroadphaseFilter<T, E> filter) {
		int eSize = Collisions.getEstimatedCollisionPairs(size());
		List<CollisionPair<T, E>> pairs = new ArrayList<CollisionPair<T, E>>(eSize);
		
		// this will not happen, unless the user makes more detect calls outside of the World class
		// so it can be considered rare
		if (this.root != null) {
			this.batchRebuild();
		}
		
		this.buildAndDetect(filter, pairs);
		
		return pairs;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detectIterator(boolean)
	 */
	@Override
	public Iterator<CollisionPair<T, E>> detectIterator(boolean forceFullDetection) {
		return this.detect(new BroadphaseFilterAdapter<T, E>()).iterator();
	}
	
	/**
	 * Internal method to actually remove all leafs marked for removal.
	 * If there are any (see pendingRemoves) performs all deletions in O(n) time, else no work is done.
	 * This mechanism is used to solve the O(n) time for removing an element from the elements ArrayList.
	 * Although worst case is the same, in various scenarios this will perform better.
	 * Assumes all leafs marked for removal are <b>not</b> on the tree.
	 */
	void doPendingRemoves() {
		// We use the check (this.pendingRemoves) to avoid scanning all elements (O(n)) when
		// detect(AABB) or raycast are called a lot of times consecutively
		if (this.pendingRemoves) {
			// Important: because we're removing elements this.elements.size() can change in the loop
			// so we must call it in each loop iteration
			for (int i = 0; i < this.elements.size(); i++) {
				LazyAABBTreeLeaf<T, E> node = this.elements.get(i);
				
				if (node.mustRemove()) {
					// removed nodes are not on the tree, no need to check.
					// Just remove the element from the list by swapping it with the last one and removing the last
					
					// Note that works ok for the case elements.size() == 1
					// so no need to have an extra branch for that case
					
					int lastIndex = this.elements.size() - 1;
					
					// Swap with the last
					this.elements.set(i, this.elements.get(lastIndex));
					
					// And remove the last
					// No copying involved here, just a size decrease
					this.elements.remove(lastIndex);
					
					// don't forget to check the new element that was moved into position i
					i--;
				}
			}
			
			this.pendingRemoves = false;
			
			// Due to the swapping we need to restore the sorting order
			this.sorted = false;
		}
	}
	
	/**
	 * Internal method that sorts the elements if needed.
	 * Note that the sorting routines in array list are optimized for partially sorted data
	 * and we can expect the sorting to happen very fast if just a few changes did happen from the last sorting.
	 */
	void ensureSorted() {
		if (!this.sorted) {
			Collections.sort(this.elements, new Comparator<LazyAABBTreeLeaf<T, E>>() {
				@Override
				public int compare(LazyAABBTreeLeaf<T, E> o1, LazyAABBTreeLeaf<T, E> o2) {
					// Important heuristic: sort by size of fixtures.
					// Radius is used here because the AABBs are not yet computed,
					// but this works just as fine.
					return Double.compare(o1.fixture.getShape().getRadius(), o2.fixture.getShape().getRadius());
				}
			});
			// NOTE: use this instead if dyn4j moves to Java 8+
//			elements.sort(new Comparator<LazyAABBTreeLeaf<E, T>>() {
//				@Override
//				public int compare(LazyAABBTreeLeaf<E, T> o1, LazyAABBTreeLeaf<E, T> o2) {
//					// Important heuristic: sort by size of fixtures.
//					// Radius is used here because the AABBs are not yet computed,
//					// but this works just as fine.
//					return Double.compare(o1.fixture.getShape().getRadius(), o2.fixture.getShape().getRadius());
//				}
//			});
			
			this.sorted = true;
		}
	}

	/**
	 * Internal method to ensure that all nodes are on the tree.
	 */
	void ensureOnTree() {
		// We use the check (this.pendingInserts) to avoid scanning all elements (O(n)) when
		// detect(AABB) or raycast are called a lot of times consecutively
		if (this.pendingInserts) {
			int size = this.elements.size();
			for (int i = 0; i < size; i++) {
				LazyAABBTreeLeaf<T, E> node = this.elements.get(i);
				
				if (!node.isOnTree()) {
					this.insert(node);
				}
			}
			
			this.pendingInserts = false;
		}
	}
	
	/**
	 * Internal method that ensures the whole tree is built. This just creates the tree and performs no detection.
	 * This is used to support raycasting and single AABB queries.
	 */
	void build() {
		this.doPendingRemoves();
		this.ensureSorted();
		this.ensureOnTree();
	}
	
	/**
	 * The heart of the LazyAABBTree batch detection.
	 * Assumes no tree exists and in performs all the broad-phase detection while building the tree from scratch.
	 * 
	 * @param filter the broadphase filter
	 * @param pairs List a list containing the results
	 */
	void buildAndDetect(BroadphaseFilter<T, E> filter, List<CollisionPair<T, E>> pairs) {
		this.doPendingRemoves();
		this.ensureSorted();
		
		int size = this.elements.size();
		for (int i = 0; i < size; i++) {
			LazyAABBTreeLeaf<T, E> node = this.elements.get(i);
			
			this.insertAndDetect(node, filter, pairs);
		}
	}

	/**
	 * Cost function for descending to a particular node.
	 * The cost equals the enlargement caused in the {@link AABB} of the node.
	 * More specifically, descendCost(node, aabb) = (perimeter(union(node.aabb, aabb)) - perimeter(node.aabb)) / 2
	 * 
	 * @param node the node to descend
	 * @param itemAABB the AABB of the item being inserted
	 * @return the cost of descending to node
	 */
	double descendCost(LazyAABBTreeNode node, AABB itemAABB) {
		AABB nodeAABB = node.aabb;
		
		// The positive values indicate enlargement
		double enlargement = 0;
		
		// Calculate enlargement in x axis
		double enlargementMinX = nodeAABB.getMinX() - itemAABB.getMinX();
		double enlargementMaxX = itemAABB.getMaxX() - nodeAABB.getMaxX();
		
		if (enlargementMinX > 0) enlargement += enlargementMinX;
		if (enlargementMaxX > 0) enlargement += enlargementMaxX; 
		
		// Calculate enlargement in y axis
		double enlargementMinY = nodeAABB.getMinY() - itemAABB.getMinY();
		double enlargementMaxY = itemAABB.getMaxY() - nodeAABB.getMaxY();
		
		if (enlargementMinY > 0) enlargement += enlargementMinY;
		if (enlargementMaxY > 0) enlargement += enlargementMaxY;
		
		return enlargement;
	}
	
	/**
	 * Internal method to insert a leaf in the tree
	 * 
	 * @param item the leaf to insert
	 */
	void insert(LazyAABBTreeLeaf<T, E> item) {
		this.insert(item, false, null, null);
	}
	
	/**
	 * Internal method to insert a leaf in the tree and also perform all the collision detection required for that tree
	 * 
	 * @param item the leaf to insert
	 * @param filter the broadphase filter
	 * @param pairs a list containing the results
	 */
	void insertAndDetect(LazyAABBTreeLeaf<T, E> item, BroadphaseFilter<T, E> filter, List<CollisionPair<T, E>> pairs) {
		this.insert(item, true, filter, pairs);
	}
	
	/**
	 * The implementation routine for the tree. In order to avoid code duplication this method performs either insertion with detection
	 * or just insertion, as requested by the 'detect' parameter. The actual insertion algorithm is the same with that in {@link DynamicAABBTree}
	 * but with a variety of optimizations and clean-ups.
	 * 
	 * @param item The leaf to insert
	 * @param detect Whether to also perform collision detection
	 * @param filter the broadphase filter
	 * @param pairs List a list containing the results
	 */
	void insert(LazyAABBTreeLeaf<T, E> item, final boolean detect, BroadphaseFilter<T, E> filter, List<CollisionPair<T, E>> pairs) {
		// Mark that this leaf is now on the tree
		item.setOnTree(true);
		
		// Make sure the root is not null
		if (this.root == null) {
			// If it is then set this node as the root
			this.root = item;
			return;
		}
		
		// Get the new node's AABB
		AABB itemAABB = item.aabb;
		
		// Start looking for the insertion point at the root
		LazyAABBTreeNode node = this.root;
		
		// loop until node is a leaf
		while (!node.isLeaf()) {
			LazyAABBTreeNode other;
			double costLeft = this.descendCost(node.left, itemAABB);
			
			if (costLeft == 0) {
				// Fast path: if (costLeft == 0) then this means that
				// itemAABB is contained inside node.left.aabb (zero enlargement).
				// This is optimal so we don't need to check the right child.
				// We also need not to enlarge node.aabb: since itemAABB is contained
				// in one of node's children then node.aabb already contains itemAABB as well.
				// This 'fast path' is beneficial because as the tree get's larger the first
				// levels of the tree have comparably large AABBs so this helps sink in the tree faster.
				
				other = node.right;
				node = node.left;
			} else {
				double costRight = this.descendCost(node.right, itemAABB);
				// Although we could check if (costRight == 0) and make a similar case as above
				// there are not many gains, one fast path is enough
				
				// Enlarge the AABB of this node as needed
				node.aabb.union(itemAABB);
				
				if (costLeft < costRight) {
					other = node.right;
					node = node.left;
				} else {
					other = node.left;
					node = node.right;
				}
			}
			
			// perform collision detection to the child that we did not descend if needed
			if (detect && other.aabb.overlaps(itemAABB)) {
				this.detectWhileBuilding(item, other, filter, pairs);	
			}
		}
		
		// We also need to perform collision detection for the leaf where we ended
		if (detect && node.aabb.overlaps(itemAABB)) {
			this.detectWhileBuilding(item, node, filter, pairs);	
		}
		
		// Now that we have found a suitable place, insert a new node here for the new item
		LazyAABBTreeNode parent = node.parent;
		LazyAABBTreeNode newParent = new LazyAABBTreeNode();
		newParent.parent = parent;
		newParent.aabb = node.aabb.getUnion(itemAABB);
		
		// Since node is always a leaf, newParent has height 1
		newParent.height = 1;
		
		if (parent != null) {
			// Node is not the root node
			parent.replaceChild(node, newParent);
		} else {
			// Node is the root item
			this.root = newParent;
		}
		
		newParent.left = node;
		newParent.right = item;
		node.parent = newParent;
		item.parent = newParent;
		
		// Fix the heights and balance the tree
		this.balanceAll(newParent.parent);
	}
	
	/**
	 * Internal recursive method to detect broad-phase collisions while building the tree. Only used from insertAndDetect.
	 * Caution: Assumes that node collides with root.aabb when called (In order to reduce recursion height).
	 * Note that in contrast to {@link DynamicAABBTree} we don't need to check if one node was tested for collision.
	 * Because the nodes are tested while being inserted each pair will only be tested once, so we skip those tests.
	 */
	private void detectWhileBuilding(LazyAABBTreeLeaf<T, E> node, LazyAABBTreeNode root, BroadphaseFilter<T, E> filter, List<CollisionPair<T, E>> pairs) {
		// test the node itself
		// check for leaf node
		// non-leaf nodes always have a left child
		if (root.isLeaf()) {
			@SuppressWarnings("unchecked")
			LazyAABBTreeLeaf<T, E> leaf = (LazyAABBTreeLeaf<T, E>) root;
			
			if (filter.isAllowed(node.body, node.fixture, leaf.body, leaf.fixture)) {
				BroadphasePair<T, E> pair = new BroadphasePair<T, E>(
						node.body,	// A
						node.fixture,
						leaf.body,	// B
						leaf.fixture);	
				// add the pair to the list of pairs
				pairs.add(pair);
			}
		} else {
			// they overlap so descend into both children
			if (node.aabb.overlaps(root.left.aabb)) this.detectWhileBuilding(node, root.left, filter, pairs);
			if (node.aabb.overlaps(root.right.aabb)) this.detectWhileBuilding(node, root.right, filter, pairs);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detect(org.dyn4j.geometry.AABB)
	 */
	@Deprecated
	@Override
	public List<CollisionItem<T, E>> detect(AABB aabb, BroadphaseFilter<T, E> filter) {
		this.build();
		
		if (this.root == null) {
			return Collections.emptyList();
		}
		
		int eSize = Collisions.getEstimatedCollisionsPerObject();
		List<CollisionItem<T, E>> list = new ArrayList<CollisionItem<T, E>>(eSize);
		
		if (aabb.overlaps(this.root.aabb)) {
			this.detect(aabb, this.root, filter, list);	
		}
		
		return list;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detectIterator(org.dyn4j.geometry.AABB)
	 */
	@Override
	public Iterator<CollisionItem<T, E>> detectIterator(AABB aabb) {
		return this.detect(aabb, new BroadphaseFilterAdapter<T, E>()).iterator();
	}
	
	/**
	 * Internal recursive method used to implement BroadphaseDetector#detect.
	 * @param aabb the aabb to test with
	 * @param node the node to begin at
	 * @param filter the filter
	 * @param list the results list
	 */
	private void detect(AABB aabb, LazyAABBTreeNode node, BroadphaseFilter<T, E> filter, List<CollisionItem<T, E>> list) {
		// test the node itself
		// check for leaf node
		// non-leaf nodes always have a left child
		
		if (node.isLeaf()) {
			@SuppressWarnings("unchecked")
			LazyAABBTreeLeaf<T, E> leaf = (LazyAABBTreeLeaf<T, E>)node;
			// its a leaf so add the body
			if (filter.isAllowed(aabb, leaf.body, leaf.fixture)) {
				list.add(new BroadphaseItem<T, E>(leaf.body, leaf.fixture));
			}
			// return and check other limbs
		} else {
			// they overlap so descend into both children
			if (aabb.overlaps(node.left.aabb)) this.detect(aabb, node.left, filter, list);
			if (aabb.overlaps(node.right.aabb)) this.detect(aabb, node.right, filter, list);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#raycast(org.dyn4j.geometry.Ray, double)
	 */
	@Deprecated
	@Override
	public List<CollisionItem<T, E>> raycast(Ray ray, double length, BroadphaseFilter<T, E> filter) {
		this.build();
		
		if (this.root == null) {
			return Collections.emptyList();
		}
		
		// create an aabb from the ray
		Vector2 s = ray.getStart();
		Vector2 d = ray.getDirectionVector();
		
		// get the length
		if (length <= 0.0) {
			length = Double.MAX_VALUE;
		}
		
		// create the aabb
		double w = d.x * length;
		double h = d.y * length;
		AABB aabb = AABB.createAABBFromPoints(s.x, s.y, s.x + w, s.y + h);
		
		if (!root.aabb.overlaps(aabb)) {
			return Collections.emptyList();
		}
		
		// precompute
		double invDx = 1.0 / d.x;
		double invDy = 1.0 / d.y;
		
		// get the estimated collision count
		int eSize = Collisions.getEstimatedRaycastCollisions(this.elementMap.size());
		List<CollisionItem<T, E>> items = new ArrayList<CollisionItem<T, E>>(eSize);
		LazyAABBTreeNode node = this.root;
		
		// perform a iterative, stack-less, traversal of the tree
		while (node != null) {
			// check if the current node overlaps the desired node
			if (aabb.overlaps(node.aabb)) {
				// if they do overlap, then check the left child node
				if (node.isLeaf()) {
					if (AbstractBroadphaseDetector.raycast(s, length, invDx, invDy, node.aabb)) {
						// if both are null, then this is a leaf node
						@SuppressWarnings("unchecked")
						LazyAABBTreeLeaf<T, E> leaf = (LazyAABBTreeLeaf<T, E>)node;
						if (filter.isAllowed(ray, length, leaf.body, leaf.fixture)) {
							items.add(new BroadphaseItem<T, E>(leaf.body, leaf.fixture));
						}
						// if its a leaf node then we need to go back up the
						// tree and test nodes we haven't yet
					}
				} else {
					// if the left is not null, then check that subtree
					node = node.left;
					continue;
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
		
		return items;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#detectIterator(org.dyn4j.geometry.Ray, double)
	 */
	@Override
	public Iterator<CollisionItem<T, E>> raycastIterator(Ray ray, double length) {
		return this.raycast(ray, length, new BroadphaseFilterAdapter<T, E>()).iterator();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		// make sure the tree is built
		this.build();
		
		// Left intact from DynamicAABBTree
		
		// we need to update all nodes in the tree (not just the
		// nodes that contain the bodies)
		LazyAABBTreeNode node = this.root;
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
	 * Balances the tree starting from node and going up to the root
	 * 
	 * @param node The starting node
	 */
	void balanceAll(LazyAABBTreeNode node) {
		while (node != null) {
			// balance the current tree
			this.balance(node);
			node = node.parent;
		}
	}
	
	/**
	 * Balances the subtree using node as the root.
	 * Note that this is the exact same balancing routine as in {@link DynamicAABBTree} but greatly reduced in size and optimized 
	 * 
	 * @param a the root node of the subtree to balance
	 */
	void balance(LazyAABBTreeNode a) {
		// see if the node is a leaf node or if
		// it doesn't have enough children to be unbalanced
		if (a.height < 2) {
			// return since there isn't any work to perform
			a.height = 1 + Math.max(a.left.height, a.right.height);
			return;
		}
		
		// get the nodes left and right children
		LazyAABBTreeNode b, c;
		
		// compute the balance factor for node a
		int balance = a.right.height - a.left.height;
		
		if (balance > 1) {
			b = a.left;
			c = a.right;
		} else if (balance < -1) {
			b = a.right;
			c = a.left;
		} else {
			a.height = 1 + Math.max(a.left.height, a.right.height);
			return;
		}
		
		// get the c's left and right nodes
		LazyAABBTreeNode d = c.left;
		LazyAABBTreeNode e = c.right;

		// switch a and c
		c.left = a;
		c.parent = a.parent;
		a.parent = c;
		
		// update c's parent to point to c instead of a
		if (c.parent != null) {
			c.parent.replaceChild(a, c);
		} else {
			this.root = c;
		}

		if (d.height <= e.height) {
			LazyAABBTreeNode temp = d;
			d = e;
			e = temp;
		}
		
		if (balance > 1) {
			a.right = e;
		} else {
			a.left = e;
		}
		
		c.right = d;
		e.parent = a;
		
		// update the aabb
		a.aabb.set(b.aabb).union(e.aabb);
		c.aabb.set(a.aabb).union(d.aabb);
		
		// update the heights
		a.height = 1 + Math.max(b.height, e.height);
		c.height = 1 + Math.max(a.height, d.height);
		
		// c is the new root node of the subtree
	}
	
	/*
	 * Ideally setAABBExpansion would throw an unsupported operation exception because we don't want to expand the AABBs in any case.
	 * But this could break existing applications that explicitly set the expansion in the case that this broadphase is set as the default.
	 * So we'll be transparent and just ignore the value.
	 * 
	 * @Override
	 * public void setAABBExpansion(double expansion) {
	 * 	throw new UnsupportedOperationException();
	 * }
	 */
	
	/*
	 * But at least if the user asks, let them know that expansion is logically 0.
	 * 
	 * (non-Javadoc)
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
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isUpdateTrackingEnabled()
	 */
	@Override
	public boolean isUpdateTrackingEnabled() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#isAABBExpansionSupported()
	 */
	@Override
	public boolean isAABBExpansionSupported() {
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
}