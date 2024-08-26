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
 * Represents a Binary Search Tree for fast insertion, deletion, and searching.
 * @author William Bittle
 * @version 6.0.0
 * @since 6.0.0
 * @param <E> Comparable
 */
public interface BinarySearchTree<E extends Comparable<E>> extends Iterable<E> {
	/**
	 * Inserts the given comparable into this binary tree.
	 * <p>
	 * Returns false if the given comparable is null.
	 * @param comparable the comparable object to insert
	 * @return boolean true if the insert was successful
	 */
	public boolean insert(E comparable);
	
	/**
	 * Removes the comparable object from the tree returning true
	 * if the comparable was found and removed
	 * <p>
	 * If the given comparable is null, false is returned.
	 * @param comparable the comparable object
	 * @return boolean true if the element was found and removed
	 */
	public boolean remove(E comparable);

	/**
	 * Removes the minimum value node from this tree.
	 * <p>
	 * Returns null if the tree is empty.
	 * @return E the minimum value
	 */
	public E removeMinimum();
	
	/**
	 * Removes the maximum value node from this tree.
	 * <p>
	 * Returns null if the tree is empty.
	 * @return E the maximum value
	 */
	public E removeMaximum();
	
	/**
	 * Returns the minimum value of the tree.
	 * <p>
	 * Returns null if the tree is empty.
	 * @return E the minimum value
	 */
	public E getMinimum();
	
	/**
	 * Returns the maximum value of the tree.
	 * <p>
	 * Returns null if the tree is empty.
	 * @return E the maximum value
	 */
	public E getMaximum();

	/**
	 * Attempts to find the given comparable object within the tree.
	 * @param comparable the comparable object to find
	 * @return boolean true if the given comparable object was found
	 */
	public boolean contains(E comparable);
	
	/**
	 * Performs a binary search on this tree given the criteria.
	 * @param criteria the criteria
	 * @param <T> the {@link BinarySearchTreeSearchCriteria} type
	 * @return the criteria for chaining
	 */
	public <T extends BinarySearchTreeSearchCriteria<E>> T search(T criteria);
	
	/**
	 * Returns the root of the tree.
	 * @return E the root value; null if the tree is empty
	 */
	public E getRoot();

	/**
	 * Empties this tree.
	 */
	public void clear();
	
	/**
	 * Returns true if this tree is empty.
	 * @return boolean true if empty
	 */
	public boolean isEmpty();
	
	/**
	 * Returns the maximum depth of the tree.
	 * @return int the maximum depth
	 */
	public int getHeight();
	
	/**
	 * Returns the number of elements in the tree.
	 * @return int
	 */
	public int size();
	
	/**
	 * Returns true if this tree is self balancing.
	 * @return boolean
	 */
	public boolean isSelfBalancing();
	
	/**
	 * Returns the in-order (ascending) iterator.
	 * @return Iterator&lt;E&gt;
	 */
	@Override
	public Iterator<E> iterator();

	/**
	 * Returns the in-order (ascending) iterator starting from the given node.
	 * @param from the starting value
	 * @return Iterator&lt;E&gt;
	 */
	public Iterator<E> tailIterator(E from);
	
	/**
	 * Returns the in-order (ascending) iterator.
	 * @param to the ending value
	 * @return Iterator&lt;E&gt;
	 */
	public Iterator<E> headIterator(E to);

	/**
	 * Returns the in-order (ascending) iterator.
	 * @param from the starting value
	 * @param to the ending value
	 * @return Iterator&lt;E&gt;
	 */
	public Iterator<E> subsetIterator(E from, E to);
	
	/**
	 * Returns a new iterator for traversing the tree in order.
	 * @return Iterator&lt;E&gt;
	 */
	public Iterator<E> inOrderIterator();
	
	/**
	 * Returns a new iterator for traversing the tree in reverse order.
	 * @return Iterator&lt;E&gt;
	 */
	public Iterator<E> reverseOrderIterator();

	/**
	 * Returns true if this binary search tree is balanced.
	 * @return boolean
	 */
	public boolean isBalanced();
	
	/**
	 * Returns true if this binary search tree is valid.
	 * <p>
	 * This method should always return true unless there's an
	 * implementation defect.
	 * @return boolean
	 */
	public boolean isValid();
	
	/**
	 * Prints this binary search tree to System.out for troubleshooting.
	 */
	public void print();
}
