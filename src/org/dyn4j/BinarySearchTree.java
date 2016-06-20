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
package org.dyn4j;

import java.util.Iterator;

/**
 * Represents an (optionally balanced) Binary Search Tree.
 * <p>
 * Null elements are not allowed and duplicates can have unexpected behavior. Changing the value
 * of the elements after being inserted into the tree is undefined. It's also undefined behavior
 * if the elements are not consistent with equals.
 * <p>
 * Use the {@link #isSelfBalancing()} and {@link #setSelfBalancing(boolean)} methods to enable
 * or disable self balancing. A balanced tree minimizes the tree depth and improves search 
 * performance. When self balancing is enabled, each insert or removal rebalances the tree
 * as necessary.
 * <p>
 * This class can be used in conjunction with the {@link BinarySearchTreeSearchCriteria} interface 
 * to perform arbitrary searches on the tree.
 * @author William Bittle
 * @version 3.2.0
 * @since 2.2.0
 * @param <E> Comparable
 */
public class BinarySearchTree<E extends Comparable<E>> implements Iterable<E> {
	/** The root node of the tree; null when empty */
	BinarySearchTreeNode<E> root;
	
	/** The current size of the tree */
	int size;
	
	/** Whether to keep the tree balanced or not */
	boolean selfBalancing;
	
	/**
	 * Creates a new binary search tree with automatic balancing off.
	 */
	public BinarySearchTree() {
		this.root = null;
		this.size = 0;
		this.selfBalancing = false;
	}
	
	/**
	 * Creates a new binary search tree.
	 * @param selfBalancing true if the tree should automatically balance
	 */
	public BinarySearchTree(boolean selfBalancing) {
		this.root = null;
		this.size = 0;
		this.selfBalancing = selfBalancing;
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This performs a deep copy of the elements in the tree.  The values contained
	 * in the tree are shallow copied.
	 * @param tree the tree to copy
	 * @since 3.0.0
	 */
	public BinarySearchTree(BinarySearchTree<E> tree) {
		this.selfBalancing = tree.selfBalancing;
		this.insertSubtree(tree);
	}

	/**
	 * Copy constructor.
	 * <p>
	 * This performs a deep copy of the elements in the tree.  The values contained
	 * in the tree are shallow copied.
	 * @param tree the tree to copy
	 * @param selfBalancing true if the tree should self balance
	 * @since 3.0.0
	 */
	public BinarySearchTree(BinarySearchTree<E> tree, boolean selfBalancing) {
		this.selfBalancing = selfBalancing;
		this.insertSubtree(tree);
	}
	
	/**
	 * Returns true if this tree is self balancing.
	 * @return boolean
	 * @since 3.0.0
	 */
	public boolean isSelfBalancing() {
		return this.selfBalancing;
	}
	
	/**
	 * Sets whether this tree should self balance.
	 * <p>
	 * When self balancing is enabled, adding and removing elements will perform a post
	 * step to make sure the tree stays balanced. Balancing minimizes the tree's depth, 
	 * thereby increasing search performance.
	 * <p>
	 * If enabled and the tree contains more than 2 elements, the tree will be balanced
	 * before this method returns.
	 * @param flag true if the tree should self balance
	 * @since 3.0.0
	 */
	public void setSelfBalancing(boolean flag) {
		// check if the flag is true (indicating the tree should self balance) and
		// check if the tree is not already self balancing
		if (flag && !this.selfBalancing) {
			// check for elements (0, 1, or 2 element trees don't have to be re-balanced)
			if (this.size > 2) {
				// if the tree was not already self balancing and the new flag is to have
				// the tree self balance itself we need to recreate the tree
				this.balanceTree();
			}
		}
		// set the local flag
		this.selfBalancing = flag;
	}
	
	/**
	 * Inserts the given comparable into this binary tree.
	 * <p>
	 * Returns false if the given comparable is null.
	 * @param comparable the comparable object to insert
	 * @return boolean true if the insert was successful
	 */
	public boolean insert(E comparable) {
		// check for null
		if (comparable == null) return false;
		// create a node for this object
		BinarySearchTreeNode<E> node = new BinarySearchTreeNode<E>(comparable);
		// otherwise we need to find where to insert this node
		return this.insert(node);
	}
	
	/**
	 * Removes the comparable object from the tree returning true
	 * if the comparable was found and removed
	 * <p>
	 * If the given comparable is null, false is returned.
	 * @param comparable the comparable object
	 * @return boolean true if the element was found and removed
	 */
	public boolean remove(E comparable) {
		// check for null
		if (comparable == null) return false;
		// check for an empty tree
		if (this.root == null) return false;
		// otherwise we need to find and remove the node
		// retaining any children of the removed node
		return this.remove(this.root, comparable) != null;
	}

	/**
	 * Removes the minimum value node from this tree.
	 * <p>
	 * Returns null if the tree is empty.
	 * @return E the minimum value
	 */
	public E removeMinimum() {
		// check for an empty tree
		if (this.root == null) return null;
		// attempt to find the minimum
		return this.removeMinimum(this.root).comparable;
	}
	
	/**
	 * Removes the maximum value node from this tree.
	 * <p>
	 * Returns null if the tree is empty.
	 * @return E the maximum value
	 */
	public E removeMaximum() {
		// check for an empty tree
		if (this.root == null) return null;
		// attempt to find the maximum
		return this.removeMaximum(this.root).comparable;
	}
	
	/**
	 * Returns the minimum value of the tree.
	 * <p>
	 * Returns null if the tree is empty.
	 * @return E the minimum value
	 */
	public E getMinimum() {
		// check for an empty tree
		if (this.root == null) return null;
		// attempt to find the minimum
		return this.getMinimum(this.root).comparable;
	}
	
	/**
	 * Returns the maximum value of the tree.
	 * <p>
	 * Returns null if the tree is empty.
	 * @return E the maximum value
	 */
	public E getMaximum() {
		// check for an empty tree
		if (this.root == null) return null;
		// attempt to find the maximum
		return this.getMaximum(this.root).comparable;
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
		// if its not found then null will be returned
		return this.contains(this.root, comparable) != null;
	}
	
	/**
	 * Performs a binary search on this tree given the criteria.
	 * @param criteria the criteria
	 * @param <T> the {@link BinarySearchTreeSearchCriteria} type
	 * @return the criteria for chaining
	 * @since 3.2.0
	 */
	public <T extends BinarySearchTreeSearchCriteria<E>> T search(T criteria) {
		// check for a null root node
		if (this.root == null) return criteria;
		// set the current node to the root
		BinarySearchTreeNode<E> node = this.root;
		// loop until the current node is null
		while (node != null) {
			// perform the search criteria 
			int result = criteria.evaluate(node.comparable);
			if (result < 0) {
				node = node.left;
			} else if (result > 0) {
				node = node.right;
			} else {
				break;
			}
		}
		return criteria;
	}
	
	/**
	 * Returns the root of the tree.
	 * @return E the root value; null if the tree is empty
	 */
	public E getRoot() {
		// check for an empty tree
		if (this.root == null) return null;
		// otherwise return the value
		return this.root.comparable;
	}

	/**
	 * Empties this tree.
	 */
	public void clear() {
		// just set the root to null
		this.root = null;
		this.size = 0;
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
	 * @since 3.0.0
	 */
	public int getHeight() {
		return this.getHeight(this.root);
	}
	
	/**
	 * Returns the number of elements in the tree.
	 * @return int
	 */
	public int size() {
		return this.size;
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
		return new BinarySearchTreeIterator<E>(this.root, true);
	}
	
	/**
	 * Returns a new iterator for traversing the tree in reverse order.
	 * @return Iterator&lt;E&gt;
	 */
	public Iterator<E> reverseOrderIterator() {
		return new BinarySearchTreeIterator<E>(this.root, false);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<E> iterator = this.inOrderIterator();
		sb.append("BinarySearchTree[");
		while (iterator.hasNext()) {
			sb.append(iterator.next());
			if (iterator.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the minimum value of the subtree of the given node.
	 * @param node the subtree root node
	 * @return {@link BinarySearchTreeNode} the node found; null if subtree is empty
	 */
	BinarySearchTreeNode<E> getMinimum(BinarySearchTreeNode<E> node) {
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
	 * @return {@link BinarySearchTreeNode} the node found; null if subtree is empty
	 */
	BinarySearchTreeNode<E> getMaximum(BinarySearchTreeNode<E> node) {
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
	 * Removes the minimum value node from the subtree of the given node.
	 * @param node the subtree root node
	 * @return {@link BinarySearchTreeNode} the node removed
	 */
	BinarySearchTreeNode<E> removeMinimum(BinarySearchTreeNode<E> node) {
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
		// decrement the size of the tree
		this.size--;
		// return the minimum
		return node;
	}
	
	/**
	 * Removes the maximum value node from the subtree of the given node.
	 * @param node the subtree root node
	 * @return {@link BinarySearchTreeNode} the node removed
	 */
	BinarySearchTreeNode<E> removeMaximum(BinarySearchTreeNode<E> node) {
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
		// decrement the size of the tree
		this.size--;
		// return the maximum
		return node;
	}

	/**
	 * Returns the maximum depth of the subtree of the given node.
	 * @param node the root node of the subtree
	 * @return int the maximum depth
	 * @since 3.0.0
	 */
	int getHeight(BinarySearchTreeNode<E> node) {
		// check for null node
		if (node == null) return 0;
		// check for the leaf node
		if (node.left == null && node.right == null) return 1;
		// otherwise recurse
		return 1 + Math.max(this.getHeight(node.left), this.getHeight(node.right));
	}
	
	/**
	 * Returns the number of elements in the subtree.
	 * @param node the root node of the subtree
	 * @return int
	 */
	int size(BinarySearchTreeNode<E> node) {
		// check for null node
		if (node == null) return 0;
		// check for the leaf node
		if (node.left == null && node.right == null) return 1;
		// otherwise recurse
		return 1 + this.size(node.left) + this.size(node.right);
	}
	
	/**
	 * Returns true if the given node (not the node's comparable) is contained in this tree.
	 * <p>
	 * This method performs a reference equals comparison on the nodes
	 * rather than a comparison on the node's comparable.
	 * @param node the node to find
	 * @return boolean
	 */
	boolean contains(BinarySearchTreeNode<E> node) {
		// check for null
		if (node == null) return false;
		// check for empty tree
		if (this.root == null) return false;
		// check for root node
		if (node == this.root) return true;
		// start at the root node
		BinarySearchTreeNode<E> curr = this.root;
		// make sure the node is not null
		while (curr != null) {
			// check for reference equality
			if (curr == node) return true;
			// otherwise pick the direction to search
			// by comparing the data in the nodes
			int diff = node.compareTo(curr);
			// check the difference
			if (diff == 0) {
				// we have found where the item should be
				// now compare by reference
				return curr == node;
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
	 * @return {@link BinarySearchTreeNode} the node containing the given value; null if its not found
	 */
	BinarySearchTreeNode<E> get(E comparable) {
		// check for null comparable
		if (comparable == null) return null;
		// check for empty tree
		if (this.root == null) return null;
		// attempt to find the comparable
		return this.contains(this.root, comparable);
	}
	
	/**
	 * Inserts the given subtree into this binary tree.
	 * <p>
	 * This method copies the elements from the given subtree.
	 * @return boolean true if the insertion was successful
	 * @param node the subtree root node
	 */
	boolean insertSubtree(BinarySearchTreeNode<E> node) {
		// check for null
		if (node == null) return false;
		// get an iterator to go through all the nodes
		Iterator<E> iterator = new BinarySearchTreeIterator<E>(node);
		// iterate over the nodes
		while (iterator.hasNext()) {
			// create a copy of the node
			BinarySearchTreeNode<E> newNode = new BinarySearchTreeNode<E>(iterator.next());
			// insert the node
			this.insert(newNode);
		}
		// the inserts were successful
		return true;
	}

	/**
	 * Inserts the given subtree into this binary tree.
	 * <p>
	 * This method copies the elements from the given tree.
	 * @return boolean true if the insertion was successful
	 * @param tree the subtree
	 */
	protected boolean insertSubtree(BinarySearchTree<E> tree) {
		// check for null
		if (tree == null) return false;
		// check for empty source tree
		if (tree.root == null) return true;
		// get an iterator to go through all the nodes
		Iterator<E> iterator = tree.inOrderIterator();
		// iterate over the nodes
		while (iterator.hasNext()) {
			// create a copy of the node
			BinarySearchTreeNode<E> newNode = new BinarySearchTreeNode<E>(iterator.next());
			// insert the node
			this.insert(newNode);
		}
		// the inserts were successful
		return true;
	}
	
	/**
	 * Removes the node containing the given value and the corresponding 
	 * subtree from this tree.
	 * @param comparable the comparable to search for
	 * @return boolean true if the element was found and its subtree was removed
	 */
	protected boolean removeSubtree(E comparable) {
		// check for null input
		if (comparable == null) return false;
		// check for empty tree
		if (this.root == null) return false;
		// attempt to find the node
		BinarySearchTreeNode<E> node = this.root;
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
				// we found where the value should be, now check for equality
				if (node.comparable.equals(comparable)) {
					// we found the node, now remove it
					if (node.isLeftChild()) {
						node.parent.left = null;
					} else {
						node.parent.right = null;
					}
					// decrement the size by the size of the removed subtree
					this.size -= this.size(node);
					// re-balance the tree
					if (this.selfBalancing) this.balanceTree(node.parent);
					// return success
					return true;
				} else {
					// wasn't found
					return false;
				}
			}
		}
		// if we get here the node was not found
		return false;
	}
	
	/**
	 * Removes the given node (not the node's comparable) and the corresponding subtree from this tree.
	 * @param node the node and subtree to remove
	 * @return boolean true if the node was found and removed successfully
	 */
	boolean removeSubtree(BinarySearchTreeNode<E> node) {
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
				// decrement the size by the size of the removed subtree
				this.size -= this.size(node);
				// re-balance the tree
				if (this.selfBalancing) this.balanceTree(node.parent);
				// return success
				return true;
			}
		}
		// if we get here the node was not found
		return false;
	}
	
	/**
	 * Inserts the given node into the tree.
	 * @param item the new node to insert
	 * @return boolean true if the insertion was successful
	 * @since 3.0.0
	 */
	boolean insert(BinarySearchTreeNode<E> item) {
		// check for an empty tree
		if (this.root == null) {
			// set the root to the new item
			this.root = item;
			// increment the size ot the tree
			this.size++;
			// return a success
			return true;
		} else {
			// otherwise use the internal insert method
			return this.insert(item, this.root);
		}
	}
	
	/**
	 * Internal insertion method.
	 * <p>
	 * This method cannot insert into the tree if the given node parameter is null.  Use the
	 * {@link #insert(BinarySearchTreeNode)} method instead to ensure that the node is inserted.
	 * @param item the node to insert
	 * @param node the subtree root node to start the search
	 * @return true if the insertion was successful
	 * @see #insert(BinarySearchTreeNode)
	 */
	boolean insert(BinarySearchTreeNode<E> item, BinarySearchTreeNode<E> node) {
		// make sure the given node is not null
		if (node == null) return false;
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
		// increment the size
		this.size++;
		// make sure the tree remains balanced
		if (this.selfBalancing) this.balanceTree(node);
		// return success
		return true;
	}

	/**
	 * Removes the given node from this tree and returns
	 * true if the node (not the node's comparable) existed and was removed.
	 * @param node the node to remove
	 * @return boolean
	 */
	boolean remove(BinarySearchTreeNode<E> node) {
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
	 * Returns the node removed if the comparable is found, null otherwise.
	 * @param node the subtree node to start the search
	 * @param comparable the comparable object to remove
	 * @return {@link BinarySearchTreeNode} null if the given comparable was not found
	 */
	BinarySearchTreeNode<E> remove(BinarySearchTreeNode<E> node, E comparable) {
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
				// if we got here we know that we found where
				// the comparable would be, now check equallity
				if (node.comparable.equals(comparable)) {
					this.removeNode(node);
					// return the node removed
					return node;
				} else {
					return null;
				}
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
	void removeNode(BinarySearchTreeNode<E> node) {
		boolean isLeftChild = node.isLeftChild();
		// check how many children it has
		if (node.left != null && node.right != null) {
			// find the minimum node in the right subtree and
			// use it as a replacement for the node we are removing
			BinarySearchTreeNode<E> min = this.getMinimum(node.right);
			
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
			
			// finally make sure the tree remains balanced
			if (this.selfBalancing) this.balanceTree(min.parent);
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
		// decrement the size
		this.size--;
	}
	
	/**
	 * Internal iterative method to find an item in the tree.
	 * @param node the subtree root node
	 * @param comparable the comparable to find
	 * @return {@link BinarySearchTreeNode} the node found; null if not found
	 */
	BinarySearchTreeNode<E> contains(BinarySearchTreeNode<E> node, E comparable) {
		// make sure the node is not null
		while (node != null) {
			// compare the comparable
			E nodeData = node.comparable;
			int diff = comparable.compareTo(nodeData);
			if (diff == 0) {
				// we found where the item should be now we to test
				// for equality
				if (node.comparable.equals(comparable)) {
					return node;
				} else {
					return null;
				}
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
	 * Re-balances the entire tree.
	 * @since 3.0.0
	 */
	protected void balanceTree() {
		// save the current tree
		BinarySearchTreeNode<E> root = this.root;
		boolean balancing = this.selfBalancing;
		// empty the tree
		this.root = null;
		this.size = 0;
		this.selfBalancing = true;
		// create an iterator for the old tree
		Iterator<E> iterator = new BinarySearchTreeIterator<E>(root);
		// add all the elements from the old tree into the new tree
		while (iterator.hasNext()) {
			// create a new node for each old node
			BinarySearchTreeNode<E> node = new BinarySearchTreeNode<E>(iterator.next());
			// add the new node to this tree
			this.insert(node);
		}
		this.selfBalancing = balancing;
	}
	
	/**
	 * Balances the tree iteratively to the root starting at the given node.
	 * @param node the node to begin balancing
	 * @since 3.0.0
	 */
	void balanceTree(BinarySearchTreeNode<E> node) {
		// loop until we reach the root node
		while (node != null) {
			// balance the tree; this can return a new root
			// node because of the rotations that happen therefore
			// we need to update the current node we are on
			node = balance(node);
			// next balance the parent of this node
			node = node.parent;
		}
	}
	
	/**
	 * Balances the given node's subtree.
	 * @param node the root node of the subtree
	 * @return {@link BinarySearchTreeNode} the new root
	 * @since 3.0.0
	 */
	BinarySearchTreeNode<E> balance(BinarySearchTreeNode<E> node) {
		// check if the node is null
		if (node == null) return null;
		// check if the node has a height of 2 or more
		if (this.getHeight(node) < 2) return node;
		
		// get the child nodes
		BinarySearchTreeNode<E> p = node.parent;
		BinarySearchTreeNode<E> a = node.left;
		BinarySearchTreeNode<E> b = node.right;
		// get the heights of the children
		int ah = this.getHeight(a);
		int bh = this.getHeight(b);
		// compute the balance
		int balance = ah - bh;
		// check the balance
		if (balance > 1) {
			
			//	    node  or    node
			//     /           /
			//    a           a
			//   /             \
			//  c               c
			
			int ach = this.getHeight(a.right);
			
			// get the subtree into left-left case
			if (ach > 1) {
				// the subtree of node is left-right
				// change it to be left-left
				BinarySearchTreeNode<E> c = a.right;
				a.right = c.left; if (c.left != null) c.left.parent = a;
				c.left = a;
				a.parent = c;
				node.left = c;
				c.parent = node;
			}
			
			//		node
			//     /
			//    c
			//   /
			//  a
			
			BinarySearchTreeNode<E> c = node.left;
			node.left = c.right; if (c.right != null) c.right.parent = node;
			c.right = node;
			c.parent = node.parent;
			node.parent = c;
			
			if (p != null) {
				if (p.left == node) {
					p.left = c;
				} else {
					p.right = c;
				}
			} else {
				this.root = c;
			}
			
			//   c
			//  / \
			// a   node
			return c;
		}
		
		if (balance < -1) {
			// node   or    node
			//     \            \
			//      b            b
			//       \          /
			//        d        d
			
			// then the right subtree need to rotate
			int bch = this.getHeight(b.left);
			
			if (bch > 1) {
				BinarySearchTreeNode<E> d = b.left;
				b.left = d.right; if (d.right != null) d.right.parent = b;
				d.right = b;
				b.parent = d;
				node.right = d;
				d.parent = node;
			}
			
			// node
			//     \
			//      d
			//       \
			//        b
			
			BinarySearchTreeNode<E> d = node.right;
			node.right = d.left; if (d.left != null) d.left.parent = node;
			d.left = node;
			d.parent = node.parent;
			node.parent = d;
			
			if (p != null) {
				if (p.left == node) {
					p.left = d;
				} else {
					p.right = d;
				}
			} else {
				this.root = d;
			}
			
			//      d
			//     / \
			// node   b
			return d;
		}
		
		return node;
	}
}
