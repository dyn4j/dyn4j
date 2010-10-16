/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d;

/**
 * Represents a Binary Search Tree.
 * @author William Bittle
 * @version 2.2.0
 * @since 2.2.0
 * @param <E> Comparable
 */
public class BinaryTree<E extends Comparable<E>> {
	/**
	 * Node class for a {@link BinaryTree}.
	 * @author William Bittle
	 * @version 2.2.0
	 * @since 2.2.0
	 * @param <T> Comparable
	 */
	protected class BinaryTreeNode<T extends Comparable<T>> implements Comparable<BinaryTreeNode<T>> {
		/** The comparable data */
		protected T comparable;
		
		/** The node to the left; the left node is greater than this node */
		protected BinaryTreeNode<T> left;
		
		/** The node to the right; the right node is greater than this node */
		protected BinaryTreeNode<T> right;
		
		/**
		 * Minimal constructor.
		 * @param comparable the comparable object
		 */
		public BinaryTreeNode(T comparable) {
			this(comparable, null, null);
		}
		
		/**
		 * Full constructor.
		 * @param comparable the comparable object
		 * @param left the left node
		 * @param right the right node
		 */
		public BinaryTreeNode(T comparable, BinaryTreeNode<T> left, BinaryTreeNode<T> right) {
			if (comparable == null) throw new NullPointerException("Cannot create a node with a null comparable.");
			this.comparable = comparable;
			this.left = left;
			this.right = right;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(BinaryTreeNode<T> other) {
			return this.comparable.compareTo(other.comparable);
		}
		
		/**
		 * Returns the comparable object for this node.
		 * @return T
		 */
		public T getComparable() {
			return comparable;
		}
		
		/**
		 * Sets the comparable object for this node.
		 * @param comparable the comparable object
		 */
		public void setComparable(T comparable) {
			this.comparable = comparable;
		}
		
		/**
		 * Returns the node left of this node.
		 * <p>
		 * The left node is considered less than this node given by
		 * the comparable objects of the respective nodes.
		 * @return {@link BinaryTree.BinaryTreeNode}
		 */
		public BinaryTreeNode<T> getLeft() {
			return left;
		}
		
		/**
		 * Sets the left node of this node.
		 * <p>
		 * This method does not check the validity of the given node.
		 * @param left the new left node
		 */
		public void setLeft(BinaryTreeNode<T> left) {
			this.left = left;
		}
		
		/**
		 * Returns the node right of this node.
		 * <p>
		 * The right node is considered greater than this node given by
		 * the comparable objects of the respective nodes.
		 * @return {@link BinaryTree.BinaryTreeNode}
		 */
		public BinaryTreeNode<T> getRight() {
			return right;
		}
		
		/**
		 * Sets the right node of this node.
		 * <p>
		 * This method does not check the validity of the given node.
		 * @param right the new right node
		 */
		public void setRight(BinaryTreeNode<T> right) {
			this.right = right;
		}
	}
	
	/** The root node of the tree */
	protected BinaryTreeNode<E> root;
	
	/**
	 * Inserts the given comparable object into this binary tree.
	 * @param comparable the comparable object to insert
	 */
	public void insert(E comparable) {
		// check for null
		if (comparable == null) return;
		// create a node for this object
		BinaryTreeNode<E> node = new BinaryTreeNode<E>(comparable);
		// check for a null root
		if (this.root == null) {
			// if the root is null then this node becomes the root
			this.root = node;
		} else {
			// otherwise we need to find where to insert this node
			this.insert(node, this.root);
		}
	}
	
	/**
	 * Removes the comparable object from the tree.
	 * @param comparable the comparable object
	 */
	public void remove(E comparable) {
		// check for null
		if (comparable == null) return;
		// check for an empty tree
		if (this.root == null) return;
		// otherwise we need to find and remove the node
		// retaining any children of the removed node
		this.root = this.remove(comparable, this.root);
	}
	
	/**
	 * Returns the minimum value of the tree.
	 * @return E the minimum value; null if the tree is empty
	 */
	public E getMinimum() {
		// check for an empty tree
		if (this.root == null) return null;
		// attempt to find the minimum
		BinaryTreeNode<E> min = this.findMinimum(this.root);
		// check for null
		if (min == null) {
			return null;
		} else {
			// if its not null then return the comparable object
			return min.comparable;
		}
	}
	
	/**
	 * Returns the maximum value of the tree.
	 * @return E the maximum value; null if the tree is empty
	 */
	public E getMaximum() {
		// check for an empty tree
		if (this.root == null) return null;
		// attempt to find the maximum
		BinaryTreeNode<E> min = this.findMaximum(this.root);
		// check for null
		if (min == null) {
			return null;
		} else {
			// if its not null then return the comparable object
			return min.comparable;
		}
	}
	
	/**
	 * Empties this tree.
	 */
	public void empty() {
		// just set the root to null
		this.root = null;
	}
	
	/**
	 * Returns true if this tree is empty.
	 * @return boolean true if empty
	 */
	public boolean isEmpty() {
		return this.root == null;
	}
	
	/**
	 * Returns the maximum depth of the tree.
	 * @return int the depth
	 */
	public int getDepth() {
		if (this.root == null) return 0;
		return 1 + this.depth(this.root);
	}
	
	/**
	 * Attempts to find the given comparable object within the tree.
	 * @param comparable the comparable object to find
	 * @return boolean true if the given comparable object was found
	 */
	public boolean contains(E comparable) {
		// check for null comparable
		if (comparable == null) return false;
		// check for empty tree
		if (this.root == null) return false;
		// attempt to find the comparable
		BinaryTreeNode<E> node = this.find(this.root, comparable);
		// check for null
		if (node == null) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Internal recursive insertion method.
	 * @param item the node to insert
	 * @param node the subtree root node to start the search
	 */
	private void insert(BinaryTreeNode<E> item, BinaryTreeNode<E> node) {
		// compare the current node to the node to be inserted
		if (item.compareTo(node) < 0) {
			// the new item is less than the current node
			// so check if the left child is present
			if (node.left == null) {
				// if not then add the item as the node's left child
				node.left = item;
			} else {
				// otherwise continue to search for a location to insert
				this.insert(item, node.left);
			}
		} else {
			// the new item is greater than (or equal to) the current node
			// so check if the right child is present
			if (node.right == null) {
				// if not then add the item as the node's right child
				node.right = item;
			} else {
				// otherwise continue to search for a location to insert
				this.insert(item, node.right);
			}
		}
	}
	
	/**
	 * Internal recursive removal method.
	 * @param comparable the comparable object to remove
	 * @param node the subtree node to start the search
	 * @return {@link BinaryTreeNode}
	 */
	private BinaryTreeNode<E> remove(E comparable, BinaryTreeNode<E> node) {
		if (node == null) return node;
		// check if the given comparable object is less than the current 
		// subtree root node
		int diff = comparable.compareTo(node.comparable);
		if (diff < 0) {
			// if its less than, we need to continue to search for the item
			// in the left subtree
			node.left = this.remove(comparable, node.left);
		} else if (diff > 0) {
			// if its greater than, we need to continue to search for the item
			// in the right subtree
			node.right = this.remove(comparable, node.right);
		} else {
			// if we got here we know that we found the
			// node that contains the given comparable
			
			// check how many children it has
			if (node.left != null && node.right != null) {
				// find the minimum node in the right subtree and
				// use it as a replacement for the node we are removing
				BinaryTreeNode<E> min = this.findMinimum(node.right);
				// copy the comparable object over
				node.comparable = min.comparable;
				// remove the minimum from the subtree
				node.right = this.removeMinimum(node.right);
			} else if (node.left != null) {
				// return the node thats not null
				node = node.left;
			} else if (node.right != null) {
				// return the node thats not null
				node = node.right;
			} else {
				// if both are null then we can just remove the node
				node = null;
			}
		}
		// return the node
		return node;
	}
	
	/**
	 * Internal method to recursively remove the minimum node from the
	 * given subtree.
	 * @param node the subtree root node
	 * @return {@link BinaryTreeNode} the node 
	 */
	private BinaryTreeNode<E> removeMinimum(BinaryTreeNode<E> node) {
		if (node.left != null) {
			node.left = removeMinimum(node.left);
			return node;
		} else {
			return node.right;
		}
	}
	
	/**
	 * Internal method to find the minimum value in the tree.
	 * @param node the subtree root node
	 * @return {@link BinaryTreeNode} the node found; null if subtree is empty
	 */
	private BinaryTreeNode<E> findMinimum(BinaryTreeNode<E> node) {
		// check for a null node
		if (node == null) return null;
		// loop until we find the minimum
		while (node.left != null) {
			// continue to the left since the minimum value
			// will always be the left most node
			node = node.left;
		}
		// the minimum will be last node traversed
		return node;
	}
	
	/**
	 * Internal method to find the maximum value in the tree.
	 * @param node the subtree root node
	 * @return {@link BinaryTreeNode} the node found; null if subtree is empty
	 */
	private BinaryTreeNode<E> findMaximum(BinaryTreeNode<E> node) {
		// check for a null node
		if (node == null) return null;
		// loop until we find the maximum
		while (node.right != null) {
			// continue to the right since the maximum value
			// will always be the right most node
			node = node.right;
		}
		// the maximum will be last node traversed
		return node;
	}
	
	/**
	 * Internal recursive method to find an item in the tree.
	 * @param node the subtree root node
	 * @param comparable the comparable to find
	 * @return {@link BinaryTreeNode} the node found; null if not found
	 */
	private BinaryTreeNode<E> find(BinaryTreeNode<E> node, E comparable) {
		// make sure the node is not null
		while (node != null) {
			// compare the comparable
			E nodeData = node.comparable;
			int diff = comparable.compareTo(nodeData);
			if (diff == 0) {
				// we found the item and we can stop
				return node;
			} else if (diff < 0) {
				// the comparable must be to the left of this node
				// since its less than this node
				node = node.left;
			} else {
				// the comparable must be to the right of this node
				// since its greater than this node
				node = node.right;
			}
		}
		// the node was not found
		return null;
	}
	
	/**
	 * Internal recursive method to find the maximum depth of
	 * a given subtree.
	 * @param node the root node of the subtree
	 * @return int the depth
	 */
	private int depth(BinaryTreeNode<E> node) {
		int l = 0;
		int r = 0;
		if (node.left != null) l = 1 + this.depth(node.left);
		if (node.right != null) r = 1 + this.depth(node.right);
		return Math.max(l, r);
	}
}
