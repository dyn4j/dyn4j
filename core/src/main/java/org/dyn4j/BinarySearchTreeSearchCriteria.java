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

/**
 * Represents criteria for performing a binary search on a {@link BinarySearchTree}.
 * <p>
 * The {@link BinarySearchTree#search(BinarySearchTreeSearchCriteria)} method performs a 
 * binary search and requires some criteria to determine whether to traverse to the left 
 * (smaller) or right (larger) child.
 * <p>
 * The {@link #evaluate(Comparable)} method is called for each node visited starting
 * at the root of the tree.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.2.0
 * @param <E> the comparable type
 */
public interface BinarySearchTreeSearchCriteria<E extends Comparable<E>> {
	/**
	 * Evaluates the current comparable determining which child to navigate
	 * to next.
	 * <ul>
	 * <li>Return zero to stop the search.</li>
	 * <li>Return less than zero to continue searching to the left.</li>
	 * <li>Return greater than zero to continue searching to the right</li>
	 * </ul>
	 * @param comparable the current comparable
	 * @return int
	 */
	public int evaluate(E comparable);
}
