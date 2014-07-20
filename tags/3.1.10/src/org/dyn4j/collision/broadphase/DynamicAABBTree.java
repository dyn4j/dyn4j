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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Collisions;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

/**
 * Implementation of an axis-aligned bounding box tree.
 * <p>
 * This class uses a self-balancing binary tree to store the AABBs.  The AABBs are sorted using the perimeter.
 * The perimeter hueristic is better than area for 2D because axis aligned segments have zero area.
 * @author William Bittle
 * @version 3.1.5
 * @since 3.0.0
 * @param <E> the {@link Collidable} type
 */
public class DynamicAABBTree<E extends Collidable> extends AbstractAABBDetector<E> implements BroadphaseDetector<E> {
	/**
	 * Represents a node in the tree.
	 * @author William Bittle
	 * @version 3.0.0
	 * @since 3.0.0
	 */
	protected class Node {
		/** The left child */
		public Node left;
		
		/** The right child */
		public Node right;
		
		/** The parent node */
		public Node parent;
		
		/** The height of this subtree */
		public int height;
		
		/** The collidable; null if this node is not a leaf node */
		public E collidable;
		
		/** The aabb containing all children */
		public AABB aabb;
		
		/** Flag used to determine if a node has been tested before */
		public boolean tested = false;
		
		/**
		 * Returns true if this node is a leaf node.
		 * @return boolean true if this node is a leaf node
		 */
		public boolean isLeaf() {
			return left == null;
		}
	}
	
	/** The root node of the tree */
	protected Node root;
	
	/** The unsorted list of proxies */
	protected List<Node> proxyList;
	
	/** Id to node map for fast lookup */
	protected Map<UUID, Node> proxyMap;
	
	/**
	 * Default constructor.
	 */
	public DynamicAABBTree() {
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
	public DynamicAABBTree(int initialCapacity) {
		this.proxyList = new ArrayList<Node>(initialCapacity);
		// 0.75 = 3/4, we can garuantee that the hashmap will not need to be rehashed
		// if we take capacity / load factor
		// the default load factor is 0.75 according to the javadocs, but lets assign it to be sure
		this.proxyMap = new HashMap<UUID, Node>(initialCapacity * 4 / 3 + 1, 0.75f);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#add(org.dyn4j.collision.Collidable)
	 */
	@Override
	public void add(E collidable) {
		// create an aabb for the collidable
		AABB aabb = collidable.createAABB();
		// expand the aabb
		aabb.expand(this.expansion);
		// create a new node for the collidable
		Node node = new Node();
		node.collidable = collidable;
		node.aabb = aabb;
		// add the proxy to the list
		this.proxyList.add(node);
		// add the proxy to the map
		this.proxyMap.put(collidable.getId(), node);
		// insert the node into the tree
		this.insert(node);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#remove(org.dyn4j.collision.Collidable)
	 */
	@Override
	public void remove(E collidable) {
		// find the node in the map
		Node node = this.proxyMap.get(collidable.getId());
		// make sure it was found
		if (node != null) {
			// remove the node from the tree
			this.remove(node);
			// remove the node from the list
			this.proxyList.remove(node);
			// remove the node from the map
			this.proxyMap.remove(collidable.getId());
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#update(org.dyn4j.collision.Collidable)
	 */
	@Override
	public void update(E collidable) {
		// get the node from the map
		Node node = this.proxyMap.get(collidable.getId());
		// make sure we found it
		if (node != null) {
			// create the new aabb
			AABB aabb = collidable.createAABB();
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
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#clear()
	 */
	@Override
	public void clear() {
		this.proxyList.clear();
		this.proxyMap.clear();
		this.root = null;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseDetector#getAABB(org.dyn4j.collision.Collidable)
	 */
	@Override
	public AABB getAABB(E collidable) {
		Node node = this.proxyMap.get(collidable.getId());
		if (node != null) {
			return node.aabb;
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
		
		// clear all the tested flags on the nodes
		for (int i = 0; i < size; i++) {
			Node node = this.proxyList.get(i);
			// reset the flag
			node.tested = false;
		}

		// the estimated size of the pair list
		int eSize = Collisions.getEstimatedCollisionPairs(size);
		List<BroadphasePair<E>> pairs = new ArrayList<BroadphasePair<E>>(eSize);
		
		// test each collidable in the list
		for (int i = 0; i < size; i++) {
			// get the current collidable to test
			Node node = this.proxyList.get(i);
			// perform a stackless detection routine
			detectNonRecursive(node, this.root, pairs);
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
	public List<E> detect(AABB aabb) {
		return this.detectNonRecursive(aabb, this.root);
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
		// we need to update all nodes in the tree (not just the
		// nodes that contain the bodies)
		Node node = root;
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
	 * @param pairs the list of pairs to add to
	 */
	protected void detect(Node node, Node root, List<BroadphasePair<E>> pairs) {
		// check for null node (shouldnt happen)
		if (root == null) return;
		// check for the tested flag (to remove duplicates)
		if (root.tested) return;
		// don't bother returning a pair of the same object
		if (node.collidable == root.collidable) return;
		// test the node itself
		if (node.aabb.overlaps(root.aabb)) {
			// check for leaf node
			// non-leaf nodes always have a left child
			if (root.left == null) {
				// its a leaf so add the pair
				BroadphasePair<E> pair = new BroadphasePair<E>(
						node.collidable,	// A
						root.collidable);	// B
				// add the pair to the list of pairs
				pairs.add(pair);
				// return and check other limbs
				return;
			}
			// they overlap so descend into both children
			if (root.left != null) detect(node, root.left, pairs);
			if (root.right != null) detect(node, root.right, pairs);
		}
	}
	
	/**
	 * Internal non-recursive detection method.
	 * @param node the node to test
	 * @param root the root node of the subtree
	 * @param pairs the list of pairs to add to
	 */
	protected void detectNonRecursive(Node node, Node root, List<BroadphasePair<E>> pairs) {
		// start at the root node
		Node n = root;
		// perform a iterative, stack-less, traversal of the tree
		while (n != null) {
			// check if the current node overlaps the desired node
			if (n.aabb.overlaps(node.aabb)) {
				// if they do overlap, then check the left child node
				if (n.left != null) {
					// if the left is not null, then check that subtree
					n = n.left;
					continue;
				} else {
					// if both are null, then this is a leaf node
					// check the tested flag to avoid duplicates and
					// verify we aren't testing the same collidable against
					// itself
					if (!n.tested && n.collidable != node.collidable) {
						// its a leaf so add the pair
						BroadphasePair<E> pair = new BroadphasePair<E>(
								node.collidable,	// A
								n.collidable);		// B
						// add the pair to the list of pairs
						pairs.add(pair);
					}
					// if its a leaf node then we need to go back up the
					// tree and test nodes we haven't yet
				}
			}
			// if the current node is a leaf node or doesnt overlap the
			// desired aabb, then we need to go back up the tree until we
			// find the first left node who's right node is not null
			boolean nextNodeFound = false;
			while (n.parent != null) {
				// check if the current node the left child of its parent
				if (n == n.parent.left) {
					// it is, so check if the right node is non-null
					if (n.parent.right != null) {
						// it isn't so the sibling node is the next node
						n = n.parent.right;
						nextNodeFound = true;
						break;
					}
				}
				// if the current node isn't a left node or it is but its
				// sibling is null, go to the parent node
				n = n.parent;
			}
			// if we didn't find it then we are done
			if (!nextNodeFound) break;
		}
	}
	
	/**
	 * Internal recursive {@link AABB} detection method.
	 * @param aabb the {@link AABB} to test
	 * @param node the root node of the subtree
	 * @param list the list to contain the results
	 */
	protected void detect(AABB aabb, Node node, List<E> list) {
		// test the node itself
		if (aabb.overlaps(node.aabb)) {
			// check for leaf node
			// non-leaf nodes always have a left child
			if (node.left == null) {
				// its a leaf so add the collidable
				list.add(node.collidable);
				// return and check other limbs
				return;
			}
			// they overlap so descend into both children
			if (node.left != null) detect(aabb, node.left, list);
			if (node.right != null) detect(aabb, node.right, list);
		}
	}
	
	/**
	 * Internal non-recursive {@link AABB} detection method.
	 * @param aabb the {@link AABB} to test
	 * @param node the root node of the subtree
	 * @return List a list containing the results
	 */
	protected List<E> detectNonRecursive(AABB aabb, Node node) {
		// get the estimated collision count
		int eSize = Collisions.getEstimatedCollisions();
		List<E> list = new ArrayList<E>(eSize);
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
					list.add(node.collidable);
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
					if (node.parent.right != null) {
						// it isn't so the sibling node is the next node
						node = node.parent.right;
						nextNodeFound = true;
						break;
					}
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
	protected void insert(Node item) {
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
		Node node = this.root;
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
			Node left = node.left;
			Node right = node.right;
			
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
		Node parent = node.parent;
		Node newParent = new Node();
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
			
			Node left = node.left;
			Node right = node.right;
			
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
	protected void remove(Node node) {
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
		Node parent = node.parent;
		Node grandparent = parent.parent;
		Node other;
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
			Node n = grandparent;
			while (n != null) {
				// balance the current subtree
				n = balance(n);
				
				Node left = n.left;
				Node right = n.right;
				
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
	 * @return {@link Node} the new root of the subtree
	 */
	protected Node balance(Node node) {
		Node a = node;
		
		// see if the node is a leaf node or if
		// it doesn't have enough children to be unbalanced
		if (a.isLeaf() || a.height < 2) {
			// return since there isn't any work to perform
			return a;
		}
		
		// get the nodes left and right children
		Node b = a.left;
		Node c = a.right;
		
		// compute the balance factor for node a
		int balance = c.height - b.height;
		
		// if the balance is off on the right side
		if (balance > 1) {
			// get the c's left and right nodes
			Node f = c.left;
			Node g = c.right;
			
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
			Node d = b.left;
			Node e = b.right;
			
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
	protected void validate(Node node) {
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
		Node left = node.left;
		Node right = node.right;
		
		// check if the node is a leaf
		if (node.isLeaf()) {
			// if so, then both children should be null
			// the height should be zero and the collidable
			// should not be null
			assert(node.left == null);
			assert(node.right == null);
			assert(node.height == 0);
			assert(node.collidable != null);
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
