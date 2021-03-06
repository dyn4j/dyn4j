package org.dyn4j.geometry.simplify;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.dyn4j.Epsilon;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Vector2;

class RTree {

	RTreeNode root;
	
	public void add(RTreeLeaf leaf) {
		this.insert(leaf);
	}
	
	public void remove(RTreeLeaf leaf) {
		this.remove((RTreeNode)leaf);
	}
	
	public Iterator<RTreeLeaf> getAABBDetectIterator(AABB aabb) {
		return new DetectAABBIterator(aabb);
	}
	
	public void clear() {
		this.root = null;
	}
	
	/**
	 * Internal method to insert a node into the tree.
	 * @param item the node to insert
	 */
	private void insert(RTreeNode item) {
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
		RTreeNode node = this.root;
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
			RTreeNode left = node.left;
			RTreeNode right = node.right;
			
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
		RTreeNode parent = node.parent;
		RTreeNode newParent = new RTreeNode();
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
			
			RTreeNode left = node.left;
			RTreeNode right = node.right;
			
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
	private void remove(RTreeNode node) {
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
		RTreeNode parent = node.parent;
		RTreeNode grandparent = parent.parent;
		RTreeNode other;
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
			RTreeNode n = grandparent;
			while (n != null) {
				// balance the current subtree
				n = balance(n);
				
				RTreeNode left = n.left;
				RTreeNode right = n.right;
				
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
	private RTreeNode balance(RTreeNode node) {
		RTreeNode a = node;
		
		// see if the node is a leaf node or if
		// it doesn't have enough children to be unbalanced
		if (a.isLeaf() || a.height < 2) {
			// return since there isn't any work to perform
			return a;
		}
		
		// get the nodes left and right children
		RTreeNode b = a.left;
		RTreeNode c = a.right;
		
		// compute the balance factor for node a
		int balance = c.height - b.height;
		
		// if the balance is off on the right side
		if (balance > 1) {
			// get the c's left and right nodes
			RTreeNode f = c.left;
			RTreeNode g = c.right;
			
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
			RTreeNode d = b.left;
			RTreeNode e = b.right;
			
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
	 * A specialized iterator for testing an {@link AABB} against this broadphase.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	private final class DetectAABBIterator implements Iterator<RTreeLeaf> {
		/** The {@link AABB} to test with */
		private final AABB aabb;
		
		/** Internal state to track the node in the tree we're testing against */
		private RTreeNode currentNode;
		
		/** The next item to return */
		private RTreeLeaf nextItem;
		
		/**
		 * Minimal constructor.
		 * @param aabb the {@link AABB} to test
		 */
		public DetectAABBIterator(AABB aabb) {
			this.aabb = aabb;
			this.currentNode = RTree.this.root;
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
		public RTreeLeaf next() {
			if (this.nextItem != null) {
				RTreeLeaf item = this.nextItem;
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
			RTreeNode node = this.currentNode;
			
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
						RTreeLeaf leaf = (RTreeLeaf)node;
						
						// record the collision details
						this.nextItem = leaf;
						
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

}
