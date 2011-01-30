/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import org.dyn4j.game2d.BinarySearchTree.TreeIterator.Direction;

/**
 * Represents an unbalanced Binary Search Tree.
 * <p>
 * This class cannot store null values and duplicates can have unexpected behavior.
 * @author William Bittle
 * @version 2.2.3
 * @since 2.2.0
 * @param <E> Comparable
 */
public class BinarySearchTree<E extends Comparable<E>> implements Iterable<E> {
	/**
	 * Node class for a {@link BinarySearchTree}.
	 * @author William Bittle
	 * @version 2.2.0
	 * @since 2.2.0
	 * @param <T> Comparable
	 */
	public static class Node<T extends Comparable<T>> implements Comparable<Node<T>> {
		/** The comparable data */
		protected T comparable;
		
		/** The parent node of this node */
		protected Node<T> parent;
		
		/** The node to the left; the left node is greater than this node */
		protected Node<T> left;
		
		/** The node to the right; the right node is greater than this node */
		protected Node<T> right;
		
		/**
		 * Minimal constructor.
		 * @param comparable the comparable object
		 */
		public Node(T comparable) {
			this(comparable, null, null, null);
		}
		
		/**
		 * Full constructor.
		 * @param comparable the comparable object
		 * @param parent the parent node
		 * @param left the left node
		 * @param right the right node
		 * @throws NullPointerException if comparable is null
		 */
		protected Node(T comparable, Node<T> parent, Node<T> left, Node<T> right) {
			if (comparable == null) throw new NullPointerException("Cannot create a node with a null comparable.");
			this.comparable = comparable;
			this.parent = parent;
			this.left = left;
			this.right = right;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Node<T> other) {
			return this.comparable.compareTo(other.comparable);
		}
		
		/**
		 * Returns true if this node is the left child of
		 * its parent node.
		 * <p>
		 * Returns false if this node does not have a parent.
		 * @return boolean
		 */
		public boolean isLeftChild() {
			if (this.parent == null) return false;
			return (this.parent.left == this);
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
		protected void setComparable(T comparable) {
			this.comparable = comparable;
		}
		
		/**
		 * Returns the node left of this node.
		 * <p>
		 * The left node is considered less than this node given by
		 * the comparable objects of the respective nodes.
		 * @return {@link BinarySearchTree.Node}
		 */
		public Node<T> getLeft() {
			return left;
		}
		
		/**
		 * Sets the left node of this node.
		 * <p>
		 * This method does not check the validity of the given node.
		 * @param left the new left node
		 */
		protected void setLeft(Node<T> left) {
			this.left = left;
		}
		
		/**
		 * Returns the node right of this node.
		 * <p>
		 * The right node is considered greater than this node given by
		 * the comparable objects of the respective nodes.
		 * @return {@link BinarySearchTree.Node}
		 */
		public Node<T> getRight() {
			return right;
		}
		
		/**
		 * Sets the right node of this node.
		 * <p>
		 * This method does not check the validity of the given node.
		 * @param right the new right node
		 */
		protected void setRight(Node<T> right) {
			this.right = right;
		}
		
		/**
		 * Returns the parent node.
		 * @return {@link BinarySearchTree.Node}
		 */
		public Node<T> getParent() {
			return parent;
		}
		
		/**
		 * Sets the parent node of this node.
		 * @param parent the parent node
		 */
		protected void setParent(Node<T> parent) {
			this.parent = parent;
		}
	}
	
	/**
	 * Iterator class for looping through the elements in order or in reverse order.
	 * @author William Bittle
	 * @version 2.2.0
	 * @since 2.2.0
	 * @param <T> the Comparable type
	 */
	public static class TreeIterator<T extends Comparable<T>> implements Iterator<T> {
		/**
		 * Enumeration of the traversal orders.
		 * @author William Bittle
		 * @version 2.2.0
		 * @since 2.2.0
		 */
		public enum Direction {
			/** Traverses the nodes in order */
			ASCENDING,
			/** Traverses the node in reverse order */
			DESCENDING
		}
		
		/** The node stack for iterative traversal */
		protected Stack<BinarySearchTree.Node<T>> stack;
		
		/** The traversal direction */
		protected final Direction direction;
		
		/**
		 * Default constructor using {@link Direction#ASCENDING}.
		 * @param node the root node of the subtree to traverse
		 */
		public TreeIterator(BinarySearchTree.Node<T> node) {
			this(node, Direction.ASCENDING);
		}
		
		/**
		 * Full constructor.
		 * @param node the root node of the subtree to traverse
		 * @param direction the direction of the traversal
		 * @throws NullPointerException if node or direction is null
		 */
		public TreeIterator(BinarySearchTree.Node<T> node, Direction direction) {
			// check for null
			if (node == null) throw new NullPointerException("Cannot create an iterator for a null (sub)tree.");
			if (direction == null) throw new NullPointerException("A traversal direction must be specified.");
			// set the direction
			this.direction = direction;
			// create the node stack and initialize it
			this.stack = new Stack<Node<T>>();
			// check the direction to determine how to initialize it
			if (direction == Direction.ASCENDING) {
				this.pushLeft(node);
			} else {
				this.pushRight(node);
			}
			
		}
		
		/**
		 * Pushes the left most nodes of the given subtree onto the stack.
		 * @param node the root node of the subtree
		 */
		protected void pushLeft(BinarySearchTree.Node<T> node) {
			// loop until we don't have any more left nodes
			while (node != null) {
				this.stack.push(node);
				node = node.left;
			}
		}
		
		/**
		 * Pushes the right most nodes of the given subtree onto the stack.
		 * @param node the root node of the subtree
		 */
		protected void pushRight(BinarySearchTree.Node<T> node) {
			// loop until we don't have any more right nodes
			while (node != null) {
				this.stack.push(node);
				node = node.right;
			}
		}
		
		/**
		 * Returns true if there exists a next value.
		 * @return boolean
		 */
		@Override
		public boolean hasNext() {
			return !this.stack.isEmpty();
		}
		
		/**
		 * Returns the next item in the iteration and advances.
		 * @return T the Comparable object
		 */
		@Override
		public T next() {
			// if the stack is empty throw an exception
			if (this.stack.isEmpty()) throw new NoSuchElementException();
			// get an element off the stack
			BinarySearchTree.Node<T> node = this.stack.pop();
			if (this.direction == Direction.ASCENDING) {
				// add all the left most nodes of the right subtree of this element 
				this.pushLeft(node.right);
			} else {
				// add all the right most nodes of the left subtree of this element 
				this.pushRight(node.left);
			}
			// return the comparable object
			return node.comparable;
		}
		
		/**
		 * Currently unsupported.
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	/** The root node of the tree */
	protected Node<E> root;
	
	/**
	 * Inserts the given comparable object into this binary tree.
	 * @param comparable the comparable object to insert
	 * @return {@link Node} the node inserted; null if the given comparable is null
	 */
	public Node<E> insert(E comparable) {
		// check for null
		if (comparable == null) return null;
		// create a node for this object
		Node<E> node = new Node<E>(comparable);
		// check for a null root
		if (this.root == null) {
			// if the root is null then this node becomes the root
			this.root = node;
		} else {
			// otherwise we need to find where to insert this node
			this.insert(node, this.root);
		}
		return node;
	}
	
	/**
	 * Inserts the given subtree into this binary tree.
	 * <p>
	 * This method copies the elements from the given subtree.
	 * @param node the subtree root node
	 */
	public void insertSubtree(Node<E> node) {
		// check for null
		if (node == null) return;
		// check for empty tree
		if (this.root == null) {
			// set the subtree as the root
			this.root = node;
		} else {
			// iterate over the nodes
			Iterator<E> iterator = new TreeIterator<E>(node);
			while (iterator.hasNext()) {
				// create a copy of the node
				Node<E> newNode = new Node<E>(iterator.next());
				// insert the node
				this.insert(newNode, this.root);
			}
		}
	}
	
	/**
	 * Inserts the given subtree into this binary tree.
	 * <p>
	 * This method copies the elements from the given tree.
	 * @param tree the subtree
	 */
	public void insertSubtree(BinarySearchTree<E> tree) {
		// check for null
		if (tree == null) return;
		// check for empty tree
		if (this.root == null) {
			// set the subtree as the root
			this.root = tree.root;
		} else {
			// iterate over the nodes
			Iterator<E> iterator = tree.inOrderIterator();
			while (iterator.hasNext()) {
				// create a copy of the node
				Node<E> newNode = new Node<E>(iterator.next());
				// insert the node
				this.insert(newNode, this.root);
			}
		}
	}
	
	/**
	 * Removes the comparable object from the tree returning the node or
	 * null if the comparable object was not found.
	 * @param comparable the comparable object
	 * @return {@link Node} the node removed
	 */
	public Node<E> remove(E comparable) {
		// check for null
		if (comparable == null) return null;
		// check for an empty tree
		if (this.root == null) return null;
		// otherwise we need to find and remove the node
		// retaining any children of the removed node
		return this.remove(comparable, this.root);
	}
	
	/**
	 * Removes the given node from this tree and returns
	 * true if the node existed and was removed.
	 * @param node the node to remove
	 * @return boolean
	 */
	public boolean remove(Node<E> node) {
		// check for null
		if (node == null) return false;
		// check for empty tree
		if (this.root == null) return false;
		// make sure this node is contained in the tree
		if (this.contains(node)) {
			// remove the node
			this.removeNode(node);
			// return true that the node was removed
			return true;
		}
		// otherwise return false
		return false;
	}
	
	/**
	 * Returns the minimum value of the tree.
	 * @return E the minimum value; null if the tree is empty
	 */
	public Node<E> getMinimum() {
		// attempt to find the minimum
		return this.getMinimum(this.root);
	}
	
	/**
	 * Returns the maximum value of the tree.
	 * @return E the maximum value; null if the tree is empty
	 */
	public Node<E> getMaximum() {
		// attempt to find the maximum
		return this.getMaximum(this.root);
	}
	
	/**
	 * Returns the minimum value of the subtree of the given node.
	 * @param node the subtree root node
	 * @return {@link Node} the node found; null if subtree is empty
	 */
	public Node<E> getMinimum(Node<E> node) {
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
	 * Returns the maximum value of the subtree of the given node.
	 * @param node the subtree root node
	 * @return {@link Node} the node found; null if subtree is empty
	 */
	public Node<E> getMaximum(Node<E> node) {
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
	 * Returns the root node of this tree.
	 * @return {@link Node}
	 */
	public Node<E> getRoot() {
		return this.root;
	}
	
	/**
	 * Removes the minimum value node from the subtree of the given node.
	 * @param node the subtree root node
	 * @return {@link Node} the node removed
	 */
	public Node<E> removeMinimum(Node<E> node) {
		// find the minimum
		node = this.getMinimum(node);
		// check if the given subtree root node is null
		if (node == null) return null;
		// is the minimum the root node?
		if (node == this.root) {
			// preserve the right subtree by setting the new root
			// of the tree to the root of the right subtree
			this.root = node.right;
		} else if (node.parent.right == node) {
			// otherwise the minimum node is the right node of its parent
			// overwrite the right pointer of the parent to the minimum
			// node's right subtree
			node.parent.right = node.right;
		} else {
			// otherwise the minimum node is the left node of its parent
			// overwrite the left pointer of the parent to the minimum
			// node's right subtree
			node.parent.left = node.right;
		}
		return node;
	}
	
	/**
	 * Removes the maximum value node from the subtree of the given node.
	 * @param node the subtree root node
	 * @return {@link Node} the node removed
	 */
	public Node<E> removeMaximum(Node<E> node) {
		// find the maximum
		node = this.getMaximum(node);
		// check if the given subtree root node is null
		if (node == null) return null;
		// is the maximum the root node?
		if (node == this.root) {
			// preserve the left subtree by setting the new root
			// of the tree to the root of the left subtree
			this.root = node.left;
		} else if (node.parent.right == node) {
			// otherwise the maximum node is the right node of its parent
			// overwrite the right pointer of the parent to the maximum
			// node's left subtree
			node.parent.right = node.left;
		} else {
			// otherwise the maximum node is the left node of its parent
			// overwrite the left pointer of the parent to the maximum
			// node's left subtree
			node.parent.left = node.left;
		}
		return node;
	}
	
	/**
	 * Removes the minimum value node from this tree.
	 * @return {@link Node} the node removed
	 */
	public Node<E> removeMinimum() {
		return this.removeMinimum(this.root);
	}
	
	/**
	 * Removes the maximum value node from this tree.
	 * @return {@link Node} the node removed
	 */
	public Node<E> removeMaximum() {
		return this.removeMaximum(this.root);
	}
	
	/**
	 * Empties this tree.
	 */
	public void clear() {
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
	 * @return int the maximum depth
	 */
	public int getDepth() {
		return this.getDepth(this.root);
	}
	
	/**
	 * Returns the maximum depth of the subtree of the given node.
	 * @param node the root node of the subtree
	 * @return int the maximum depth
	 */
	public int getDepth(Node<E> node) {
		if (node == null) return 0;
		return 1 + Math.max(this.getDepth(node.left), this.getDepth(node.right));
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
		Node<E> node = this.contains(this.root, comparable);
		// check for null
		if (node == null) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns true if the given node is contained in this tree.
	 * @param node the node to find
	 * @return boolean
	 */
	public boolean contains(Node<E> node) {
		// check for null
		if (node == null) return false;
		// check for empty tree
		if (this.root == null) return false;
		// check for root node
		if (node == this.root) return true;
		// start at the root node
		Node<E> curr = this.root;
		// make sure the node is not null
		while (curr != null) {
			// check for reference equality
			if (curr == node) return true;
			// otherwise pick the direction to search
			// by comparing the data in the nodes
			int diff = node.compareTo(curr);
			// check the difference
			if (diff == 0) {
				// we have found an item exactly like this
				// node but not the same reference so return
				// false
				return false;
			} else if (diff < 0) {
				// the comparable must be to the left of this node
				// since its less than this node
				curr = curr.left;
			} else {
				// the comparable must be to the right of this node
				// since its greater than this node
				curr = curr.right;
			}
		}
		// the node was not found
		return false;
	}
	
	/**
	 * Returns the node that contains the given value or null if the
	 * value is not found.
	 * @param comparable the comparable value
	 * @return {@link Node} the node containing the given value; null if its not found
	 */
	public Node<E> get(E comparable) {
		// check for null comparable
		if (comparable == null) return null;
		// check for empty tree
		if (this.root == null) return null;
		// attempt to find the comparable
		return this.contains(this.root, comparable);
	}
	
	/**
	 * Returns the number of elements in the tree.
	 * @return int
	 */
	public int size() {
		return this.size(this.root);
	}
	
	/**
	 * Returns the number of elements in the subtree.
	 * @param node the root node of the subtree
	 * @return int
	 */
	public int size(Node<E> node) {
		if (node == null) return 0;
		return 1 + this.size(node.left) + this.size(node.right);
	}
	
	/**
	 * Removes the node containing the given value and the corresponding 
	 * subtree from this tree.
	 * @param comparable the comparable to search for
	 * @return {@link Node} the subtree; null if not found
	 */
	public Node<E> removeSubtree(E comparable) {
		// check for null input
		if (comparable == null) return null;
		// check for empty tree
		if (this.root == null) return null;
		// attempt to find the node
		Node<E> node = this.root;
		while (node != null) {
			// compare the data to the current node
			int diff = comparable.compareTo(node.comparable);
			// check the difference
			if (diff < 0) {
				// if the given comparable is less than the current
				// node then go to the left on the tree
				node = node.left;
			} else if (diff > 0) {
				// if the given comparable is greater than the current
				// node then go to the right on the tree
				node = node.right;
			} else {
				// we found the node, now remove it
				if (node.isLeftChild()) {
					node.parent.left = null;
				} else {
					node.parent.right = null;
				}
				return node;
			}
		}
		// if we get here the node was not found
		return null;
	}
	
	/**
	 * Removes the given node and the corresponding subtree from this tree.
	 * @param node the node and subtree to remove
	 * @return boolean true if the node was found and removed successfully
	 */
	public boolean removeSubtree(Node<E> node) {
		// check for null input
		if (node == null) return false;
		// check for empty tree
		if (this.root == null) return false;
		// check for root node
		if (this.root == node) {
			// set the root node to null
			this.root = null;
		} else {
			// see if the tree contains the given node
			if (this.contains(node)) {
				// which child is the node?
				if (node.isLeftChild()) {
					node.parent.left = null;
				} else {
					node.parent.right = null;
				}
				return true;
			}
		}
		// if we get here the node was not found
		return false;
	}
	
	/**
	 * Returns the in-order (ascending) iterator.
	 * @return Iterator&lt;E&gt;
	 */
	@Override
	public Iterator<E> iterator() {
		return this.inOrderIterator();
	}
	
	/**
	 * Returns a new iterator for traversing the tree in order.
	 * @return Iterator&lt;E&gt;
	 */
	public Iterator<E> inOrderIterator() {
		return new TreeIterator<E>(this.root, Direction.ASCENDING);
	}
	
	/**
	 * Returns a new iterator for traversing the tree in reverse order.
	 * @return Iterator&lt;E&gt;
	 */
	public Iterator<E> reverseOrderIterator() {
		return new TreeIterator<E>(this.root, Direction.DESCENDING);
	}
	
	/**
	 * Internal insertion method.
	 * @param item the node to insert
	 * @param node the subtree root node to start the search
	 */
	protected void insert(Node<E> item, Node<E> node) {
		// loop until we find where the node should be placed
		while (node != null) {
			// compare the item to the current item
			if (item.compareTo(node) < 0) {
				// if the new item is less than the current item,
				// then check the left node of the current item
				if (node.left == null) {
					// if its null then we can go ahead and add
					// the item to the tree at this location
					node.left = item;
					// don't forget to set the parent node
					item.parent = node;
					// we are done, so break from the loop
					break;
				} else {
					// if the left node is not null then we need
					// to continue searching for a place to 
					// insert the new item
					node = node.left;
				}
			} else {
				// if the new item is greater than (or equal) to 
				// the current item, then check the right node 
				// of the current item
				if (node.right == null) {
					// if its null then we can go ahead and add
					// the item to the tree at this location
					node.right = item;
					// don't forget to set the parent node
					item.parent = node;
					// we are done, so break from the loop
					break;
				} else {
					// if the right node is not null then we need
					// to continue searching for a place to 
					// insert the new item
					node = node.right;
				}
			}
		}
	}
	
	/**
	 * Internal removal method.
	 * <p>
	 * Returns the node removed if the comparable is found, null otherwise.
	 * @param comparable the comparable object to remove
	 * @param node the subtree node to start the search
	 * @return {@link Node} null if the given comparable was not found
	 */
	protected Node<E> remove(E comparable, Node<E> node) {
		// perform an iterative version of the remove method so that
		// we can return a boolean result about removal
		while (node != null) {
			// check if the given comparable object is less than the current 
			// subtree root node
			int diff = comparable.compareTo(node.comparable);
			if (diff < 0) {
				// if its less than, we need to continue to search for the item
				// in the left subtree
				node = node.left;
			} else if (diff > 0) {
				// if its greater than, we need to continue to search for the item
				// in the right subtree
				node = node.right;
			} else {
				// if we got here we know that we found the
				// node that contains the given comparable
				this.removeNode(node);
				// return the node removed
				return node;
			}
		}
		
		// if we get here we didn't find the node in the tree
		return null;
	}
	
	/**
	 * Internal method to remove the given node from the tree retaining
	 * all the subtree nodes.
	 * <p>
	 * This method assumes that the node is contained in this tree.
	 * @param node the node to remove
	 */
	protected void removeNode(Node<E> node) {
		boolean isLeftChild = node.isLeftChild();
		// check how many children it has
		if (node.left != null && node.right != null) {
			// find the minimum node in the right subtree and
			// use it as a replacement for the node we are removing
			Node<E> min = this.getMinimum(node.right);
			
			// remove the minimum node from the tree
			if (min != node.right) {
				// set the minimum node's parent's left pointer to
				// the minimum node's right pointer (this removes the minimum
				// node from the tree and preserves the elements to the right
				// of the minimum node; no elements should exist to the left
				// of the minimum node since this is the minimum for this
				// subtree)
				min.parent.left = min.right;
				// we need to change the parent of the right subtree also
				if (min.right != null) {
					min.right.parent = min.parent;
				}
				// preserve the subtree to the right of the node we plan to 
				// remove by setting the minimum node's right pointer
				min.right = node.right;
			}
			
			// change the node's right subtree's parent
			if (node.right != null) node.right.parent = min;
			if (node.left != null) node.left.parent = min;
			
			// check if the node we are removing is the root
			if (node == this.root) {
				// just set the root pointer to the replacement node
				this.root = min;
			} else if (isLeftChild) {
				// set the parent's left pointer of the node we plan to delete
				// to the replacement node (the minimum node in the right subtree)
				node.parent.left = min;
			} else {
				// set the parent's right pointer of the node we plan to delete
				// to the replacement node (the minimum node in the right subtree)
				node.parent.right = min;
			}
			
			// set the left subtree of the replacement node to the left
			// subtree of the node we are removing
			min.left = node.left;
			
			// set the parent of the replacement node to the parent of the
			// node we are removing
			min.parent = node.parent;
		} else if (node.left != null) {
			// otherwise the right node of the node we want to remove is null
			
			// check if the node we are removing is the root
			if (node == this.root) {
				// just set the root pointer to the left subtree node
				this.root = node.left;
			} else if (isLeftChild) {
				// if the node we are trying to remove is the left node
				// of its parent, then set the left node of the parent to the
				// left subtree of this node
				node.parent.left = node.left;
			} else {
				// if the node we are trying to remove is the right node
				// of its parent, then set the right node of the parent to the
				// left subtree of this node
				node.parent.right = node.left;
			}
			// we need to change the parent of the left subtree also
			if (node.left != null) {
				node.left.parent = node.parent;
			}
		} else if (node.right != null) {
			// otherwise the left node of the node we want to remove is null
			
			// check if the node we are removing is the root
			if (node == this.root) {
				// just set the root pointer to the right subtree node
				this.root = node.right;
			} else if (isLeftChild) {
				// if the node we are trying to remove is the left node
				// of its parent, then set the left node of the parent to the
				// right subtree of this node
				node.parent.left = node.right;
			} else {
				// if the node we are trying to remove is the right node
				// of its parent, then set the right node of the parent to the
				// right subtree of this node
				node.parent.right = node.right;
			}
			// we need to change the parent of the right subtree also
			if (node.right != null) {
				node.right.parent = node.parent;
			}
		} else {
			// if both are null then we can just remove the node
			// check if this node is the root node
			if (node == this.root) {
				this.root = null;
			} else if (isLeftChild) {
				node.parent.left = null;
			} else {
				node.parent.right = null;
			}
		}
	}
	
	/**
	 * Internal recursive method to find an item in the tree.
	 * @param node the subtree root node
	 * @param comparable the comparable to find
	 * @return {@link Node} the node found; null if not found
	 */
	protected Node<E> contains(Node<E> node, E comparable) {
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
}
