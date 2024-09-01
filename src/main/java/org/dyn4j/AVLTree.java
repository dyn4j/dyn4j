/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j;

import java.util.Iterator;

/**
 * Represents an AVL Tree - a balanced {@link BinarySearchTree}.
 * <p>
 * Null elements and duplicates are not allowed. Changing the value of the elements after being 
 * inserted into the tree is undefined. It's also undefined behavior if the elements are not 
 * consistent with equals.
 * <p>
 * This class can be used in conjunction with the {@link BinarySearchTreeSearchCriteria} interface 
 * to perform arbitrary searches on the tree.
 * @author William Bittle
 * @version 6.0.0
 * @since 6.0.0
 * @param <E> Comparable
 */
public class AVLTree<E extends Comparable<E>> implements BinarySearchTree<E>, Iterable<E> {
	/** The golden ratio for testing the height of a balanced binary tree */
	private static final double GOLDEN_RATIO = (1.0 + Math.sqrt(5.0)) / 2.0;
	
	/** log base 2 to assist with calculations */
	private static final double LOG_BASE2 = Math.log(2);
	
	/** log2(golden ratio) */
	private static final double LOG_BASE2_GOLDEN_RATIO = Math.log(GOLDEN_RATIO) / LOG_BASE2;
	
	/** inverse log2(golden ratio) */
	private static final double INV_LOG_BASE2_GOLDEN_RATIO = 1.0 / LOG_BASE2_GOLDEN_RATIO;
	
	/** The root node of the tree; null when empty */
	BinarySearchTreeNode<E> root;
	
	/** The current size of the tree */
	int size;

	/**
	 * Default constructor.
	 */
	public AVLTree() {
		this.root = null;
		this.size = 0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#insert(java.lang.Comparable)
	 */
	public boolean insert(E comparable) {
		// check for null
		if (comparable == null) return false;
		// create a node for this object
		BinarySearchTreeNode<E> node = new BinarySearchTreeNode<E>(comparable);
		// otherwise we need to find where to insert this node
		return this.insert(node);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#remove(java.lang.Comparable)
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

	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#removeMinimum()
	 */
	public E removeMinimum() {
		// check for an empty tree
		if (this.root == null) return null;
		// attempt to find the minimum
		return this.removeMinimum(this.root).comparable;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#removeMaximum()
	 */
	public E removeMaximum() {
		// check for an empty tree
		if (this.root == null) return null;
		// attempt to find the maximum
		return this.removeMaximum(this.root).comparable;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#getMinimum()
	 */
	public E getMinimum() {
		// check for an empty tree
		if (this.root == null) return null;
		// attempt to find the minimum
		return this.getMinimum(this.root).comparable;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#getMaximum()
	 */
	public E getMaximum() {
		// check for an empty tree
		if (this.root == null) return null;
		// attempt to find the maximum
		return this.getMaximum(this.root).comparable;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#contains(java.lang.Comparable)
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#search(org.dyn4j.BinarySearchTreeSearchCriteria)
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#getRoot()
	 */
	public E getRoot() {
		// check for an empty tree
		if (this.root == null) return null;
		// otherwise return the value
		return this.root.comparable;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#clear()
	 */
	public void clear() {
		// just set the root to null
		this.root = null;
		this.size = 0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#isEmpty()
	 */
	public boolean isEmpty() {
		return this.root == null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#getHeight()
	 */
	public int getHeight() {
		return this.getHeight(this.root);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#size()
	 */
	public int size() {
		return this.size;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#isSelfBalancing()
	 */
	public boolean isSelfBalancing() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		return this.inOrderIterator();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#tailIterator(java.lang.Comparable)
	 */
	public Iterator<E> tailIterator(E from) {
		return new BinarySearchTreeIterator<E>(this.root, from, null);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#headIterator(java.lang.Comparable)
	 */
	public Iterator<E> headIterator(E to) {
		return new BinarySearchTreeIterator<E>(this.root, null, to);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#subsetIterator(java.lang.Comparable, java.lang.Comparable)
	 */
	public Iterator<E> subsetIterator(E from, E to) {
		return new BinarySearchTreeIterator<E>(this.root, from, to);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#inOrderIterator()
	 */
	public Iterator<E> inOrderIterator() {
		return new BinarySearchTreeIterator<E>(this.root, true);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#reverseOrderIterator()
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#isBalanced()
	 */
	public boolean isBalanced() {
		return this.isBalanced(this.root);
	}
	
	/**
	 * Returns true if this AVL tree is valid.
	 * <p>
	 * A valid AVL tree is a balanced binary tree where every node's
	 * left child is less than the node and every node's right child is
	 * greater than the node.
	 * <p>
	 * This method will also check that each node's parent is correctly
	 * set.
	 * <p>
	 * This method should always return true, unless the implementation
	 * has a defect.
	 * @return boolean
	 */
	public boolean isValid() {
		return this.isValid(this.root) && this.isUnderHeightLimit();
	}
	
	/**
	 * Returns true if this tree is under the height limit for a balanced binary
	 * tree with the current number of nodes.
	 * @return boolean
	 */
	public boolean isUnderHeightLimit() {
		// if there a no nodes, then we should be height zero, and that's fine
		if (this.size <= 1)
			return true;
		
		// otherwise the height should always be less than or equal to:
		int height = this.getHeight();
		
		if (this.size <= 3 && height == 2)
			return true;
		
		if (this.size == 4 && height == 3)
			return true;
		
		// https://www.cs.umd.edu/class/fall2019/cmsc420-0201/Lects/lect05-avl.pdf
		double log2Size = Math.log(this.size) / LOG_BASE2;
		double log2GoldenRatio = INV_LOG_BASE2_GOLDEN_RATIO;
		double maxHeight = log2Size * log2GoldenRatio;

		return height <= maxHeight;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTree2#print()
	 */
	public void print() {
		this.print(this.root);
	}
	
	// INTERNALS
	
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
		// remove the node
		this.remove(node);
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
		// remove the node
		this.remove(node);
		// return the maximum
		return node;
	}

	/**
	 * Returns the maximum depth of the subtree of the given node.
	 * @param node the root node of the subtree
	 * @return int the maximum depth
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
	 * Inserts the given node into the tree.
	 * @param item the new node to insert
	 * @return boolean true if the insertion was successful
	 * @throws IllegalArgumentException if the value being inserted already exists in the tree
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
	 * Performs a left rotation for the given node and returns the=
	 * new root node for the subtree rooted at the given node.
	 * @param node the node
	 * @return {@link BinarySearchTreeNode}
	 */
	BinarySearchTreeNode<E> rotateLeft(BinarySearchTreeNode<E> node) {
		BinarySearchTreeNode<E> parent = node.parent;
		BinarySearchTreeNode<E> right = node.right;
		BinarySearchTreeNode<E> rightLeft = right.left;
		
		node.parent = right;
		node.right = rightLeft;
		right.left = node;
		right.parent = parent;
		
		if (rightLeft != null) {
			rightLeft.parent = node;
		}
		
		if (parent == null) {
			right.parent = null;
			this.root = right;
		} else if (parent.left == node) {
			parent.left = right;
		} else if (parent.right == node) {
			parent.right = right;
		}
		
		return right;
	}
	
	/**
	 * Performs a right rotation for the given node and returns the=
	 * new root node for the subtree rooted at the given node.
	 * @param node the node
	 * @return {@link BinarySearchTreeNode}
	 */
	BinarySearchTreeNode<E> rotateRight(BinarySearchTreeNode<E> node) {
		BinarySearchTreeNode<E> parent = node.parent;
		BinarySearchTreeNode<E> left = node.left;
		BinarySearchTreeNode<E> leftRight = left.right;
		
		node.parent = left;
		node.left = leftRight;
		left.right = node;
		left.parent = parent;
		
		if (leftRight != null) {
			leftRight.parent = node;
		}
		
		if (parent == null) {
			left.parent = null;
			this.root = left;
		} else if (parent.left == node) {
			parent.left = left;
		} else if (parent.right == node) {
			parent.right = left;
		}
		
		return left;
	}
	
	/**
	 * Returns the balance of the given node.
	 * <p>
	 * The balance of a node is the difference between
	 * the left subtree height and the right subtree height.
	 * @param node the node
	 * @return int
	 */
	int getBalance(BinarySearchTreeNode<E> node) {
		if (node == null)
			return 0;
		
		int ah = this.getHeight(node.left);
		int bh = this.getHeight(node.right);
		// compute the balance
		return ah - bh;	
	}
	
	/**
	 * Internal insertion method.
	 * <p>
	 * This method cannot insert into the tree if the given node parameter is null.  Use the
	 * {@link #insert(BinarySearchTreeNode)} method instead to ensure that the node is inserted.
	 * @param item the node to insert
	 * @param root the subtree root node to start the search
	 * @return true if the insertion was successful
	 * @throws IllegalArgumentException if the value being inserted already exists in the tree
	 * @see #insert(BinarySearchTreeNode)
	 */
	boolean insert(BinarySearchTreeNode<E> item, BinarySearchTreeNode<E> root) {
		BinarySearchTreeNode<E> node = root;
		
		// loop until we find where the node should be placed
		while (node != null) {
			int comp = item.compareTo(node);
			// compare the item to the current item
			if (comp < 0) {
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
			} else if (comp > 0) {
				// if the new item is greater than to 
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
			} else {
				throw new IllegalArgumentException("Cannot insert multiple of the same value: " + item.comparable);
			}
		}
		
		// increment the size
		this.size++;
		
		// make sure the tree remains balanced
		while (node != null) {
			int height = this.getHeight(node);
			if (height > 2) {
				// determine the balance
				int balance = this.getBalance(node);
				if (balance > 1) {
					int itemToLeft = item.comparable.compareTo(node.left.comparable);
					if (itemToLeft < 0) {
						rotateRight(node);
					} else if (itemToLeft > 0) {
						rotateLeft(node.left);
						rotateRight(node);
					}
				} else if (balance < -1) {
					int itemToRight = item.comparable.compareTo(node.right.comparable);
					if (itemToRight > 0) {
						rotateLeft(node);
					} else if (itemToRight < 0) {
						rotateRight(node.right);
						rotateLeft(node);
					}
				}
			}
			node = node.parent;
		}
		
		// return success
		return true;
	}

	/**
	 * Returns the node removed if the comparable is found, null otherwise.
	 * @param root the subtree root node to start the search
	 * @param comparable the comparable object to remove
	 * @return {@link BinarySearchTreeNode} null if the given comparable was not found
	 */
	BinarySearchTreeNode<E> remove(BinarySearchTreeNode<E> root, E comparable) {
		BinarySearchTreeNode<E> node = this.contains(root, comparable);
		
		if (node == null) {
			return null;
		}
		
		this.remove(node);
		
		return node;
	}
	
	/**
	 * Internal method to remove the given node from the tree retaining
	 * all the subtree nodes.
	 * <p>
	 * This method assumes that the node is contained in this tree.
	 * @param node the node to remove
	 */
	void remove(BinarySearchTreeNode<E> node) {
		boolean isLeftChild = node.isLeftChild();
		
		// check how many children it has
		if (node.left != null && node.right != null) {
			// find the minimum node in the right subtree and
			// use it as a replacement for the node we are removing
			BinarySearchTreeNode<E> min = this.getMinimum(node.right);
			
			// there are two outcomes for the minimum search:
			// 1) The minimum is the node's right node
			// 2) The minimum is elsewhere
			
			BinarySearchTreeNode<E> minRight = min.right;
			BinarySearchTreeNode<E> minParent = min.parent;
			
			BinarySearchTreeNode<E> nodeParent = node.parent;
			BinarySearchTreeNode<E> nodeLeft = node.left;
			BinarySearchTreeNode<E> nodeRight = node.right;
			
			if (min == node.right) {
				// in the case the minimum is the node's right node
				// we only need to promote the right node to the node's position
				min.parent = nodeParent;
				min.left = nodeLeft;
				
				if (nodeLeft != null) {
					nodeLeft.parent = min;
				}
				
				if (nodeParent != null) {
					if (isLeftChild) {
						nodeParent.left = min;
					} else {
						nodeParent.right = min;
					}
				} else {
					// if node's parent is null, then that indicates
					// node was the root node and we need to update
					// the root node reference for the tree
					this.root = min;
				}
				
				// just for good measure, let's make sure
				// we break any links from node
				node.left = null;
				node.right = null;
				node.parent = null;
				
				// re-balance at the min node
				node = min;
			} else {
				// for this case, we swap the position of the node
				// we're removing and the minimum node
				// then we delete the node
				
				min.left = nodeLeft;
				min.parent = nodeParent;
				min.right = nodeRight;

				nodeRight.parent = min;
				nodeLeft.parent = min;
				
				if (nodeParent != null) {
					if (isLeftChild) {
						nodeParent.left = min;
					} else {
						nodeParent.right = min;
					}
				} else {
					// if node's parent is null, then that indicates
					// node was the root node and we need to update
					// the root node reference for the tree
					this.root = min;
				}
				
				minParent.left = minRight;
				if (minRight != null) {
					minRight.parent = minParent;
				}
				
				// just for good measure, let's make sure
				// we break any links from node
				node.left = null;
				node.right = null;
				node.parent = null;
				
				// we need to re-balance starting at the minimum node's parent
				node = minParent;
			}
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
			node.left.parent = node.parent;
			
			// start re-balancing at the node that replaced the deleted one
			node = node.left;
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
			node.right.parent = node.parent;
			
			// start re-balancing at the node that replaced the deleted one
			node = node.right;
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
			
			// start re-balancing at the parent of the deleted node
			node = node.parent;
		}
		
		// decrement the size
		this.size--;
		
		// now balance up the tree
		while (node != null) {
			int height = this.getHeight(node);
			if (height > 2) {
				// determine the balance
				int balance = this.getBalance(node);
				if (balance > 1) {
					int leftBalance = this.getBalance(node.left);
					if (leftBalance >= 0) {
						rotateRight(node);
					} else {
						rotateLeft(node.left);
						rotateRight(node);
					}
				} else if (balance < -1) {
					int rightBalance = this.getBalance(node.right);
					if (rightBalance <= 0) {
						rotateLeft(node);
					} else {
						rotateRight(node.right);
						rotateLeft(node);
					}
				}
			}
			node = node.parent;
		}
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
	 * Checks if the given subtree with node as the root is balanced
	 * @param node the root node of the subtree to check
	 * @return boolean
	 */
	boolean isBalanced(BinarySearchTreeNode<E> node) {
		if (node == null) return true;
		
		int lh = this.getHeight(node.left);
		int rh = this.getHeight(node.right);
		
		if (Math.abs(lh - rh) <= 1
			&& this.isBalanced(node.left)
			&& this.isBalanced(node.right)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns true if the given subtree rooted by the given node is valid
	 * @param node the root node of the subtree to check
	 * @return boolean
	 */
	boolean isValid(BinarySearchTreeNode<E> node) {
		if (node == null)
			return true;
		
		boolean leftValid = false;
		boolean rightValid = false;
		
		// check the left
		if (node.left == null) {
			leftValid = true;
		} else {
			leftValid = 
				// make sure the values are in the correct place
				node.left.comparable.compareTo(node.comparable) < 0
				// make sure the parent is correct
				&& node.left.parent == node
				// now recursively check the subtree
				&& isValid(node.left);
		}
		
		// check the right
		if (node.right == null) {
			rightValid = true;
		} else {
			rightValid =
				// make sure the values are in the correct place
				node.right.comparable.compareTo(node.comparable) > 0
				// make sure the parent is correct
				&& node.right.parent == node
				// now recursively check the subtree
				&& isValid(node.right);
		}
		
		// this node is only valid if both left and right are valid
		return leftValid && rightValid;
	}
	
	/**
	 * Prints the tree to System.out in a pretty format.
	 * @param node the root node of the subtree to print
	 */
	void print(BinarySearchTreeNode<E> node) {
		int height = this.getHeight(node);
		
		//                                                                                           --00--
		//                                           --00--                                                                                           --01--
		//                   --00--                                          --01--                                          --02--                                          --03--
		//       --00--                  --01--                  --02--                  --03--                  --04--                  --05--                  --06--                  --07--
		// --00--      --01--      --02--      --03--      --04--      --05--      --06--      --07--      --08--      --09--      --10--      --11--      --12--      --13--      --14--      --15--
		
		// create a complete tree in row/column form
		Object[][] completeTree = new Object[height][];
		for (int r = 0; r < height; r++) {
			int n = (int)Math.pow(2, r);
			completeTree[r] = new Object[n];
		}
		
		// fill in the complete tree with the values we have
		fillCompleteTree(node, 0, 0, completeTree);

		// create the spacer
		final int size = 6;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) 
			sb.append(' ');
		String spacer = sb.toString();
		
		for (int r = 0; r < completeTree.length; r++) {
			Object[] tr = completeTree[r];
			int offset = (int)Math.pow(2, height - r - 1) - 1;
			
			// print the offset
			for (int j = 0; j < offset; j++)
				System.out.print(spacer);
			
			// print the values, each with x spacers in between, determined by current row
			int spaceBetween = offset * 2 + 1;
			for (int c = 0; c < tr.length; c++) {
				Object value = tr[c];
				String text = value != null ? value.toString() : "null";
				
				System.out.print("[" + String.format("%1$" + (size - 2) + "s", text) + "]");
				for (int j = 0; j < spaceBetween; j++)
					System.out.print(spacer);
			}
			System.out.println();
		}
		
		System.out.println();
	}
	
	/**
	 * Fills a complete tree with the values from the given tree recursively
	 * @param node the root node of the subtree
	 * @param row the current row
	 * @param column the current column
	 * @param completeTree the complete tree to fill in
	 */
	private void fillCompleteTree(BinarySearchTreeNode<E> node, int row, int column, Object[][] completeTree) {
		completeTree[row][column] = node;
		
		if (node.left != null) {
			fillCompleteTree(node.left, row + 1, column * 2, completeTree);
		}
		if (node.right != null) {
			fillCompleteTree(node.right, row + 1, column * 2 + 1, completeTree);
		}
	}
}
