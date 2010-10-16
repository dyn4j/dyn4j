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

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for the {@link BinaryTree} class.
 * @author William Bittle
 * @version 2.2.0
 * @since 2.2.0
 */
public class BinaryTreeTest {
	/** The binary tree */
	private BinaryTree<Integer> tree;
	
	/**
	 * Sets up the binary tree and populates with data.
	 */
	@Before
	public void setup() {
		tree = new BinaryTree<Integer>();
		tree.insert(10);
		tree.insert(3);
		tree.insert(-3);
		tree.insert(4);
		tree.insert(0);
		tree.insert(1);
		tree.insert(11);
		tree.insert(19);
		tree.insert(6);
		tree.insert(-1);
		tree.insert(2);
		tree.insert(9);
	}
	
	/**
	 * Tests the remove method with a valid value.
	 */
	@Test
	public void remove() {
		TestCase.assertTrue(tree.contains(0));
		tree.remove(0);
		TestCase.assertTrue(!tree.contains(0));
	}
	
	/**
	 * Tests the remove method with a non-existing value.
	 */
	@Test
	public void removeNotFound() {
		TestCase.assertTrue(!tree.contains(7));
		tree.remove(7);
	}
	
	/**
	 * Tests the getDepth method.
	 */
	@Test
	public void getDepth() {
		TestCase.assertEquals(6, tree.getDepth());
	}
	
	/**
	 * Tests the getMinimum method.
	 */
	@Test
	public void getMinimum() {
		TestCase.assertEquals(-3, (int) tree.getMinimum());
	}
	
	/**
	 * Tests the getMaximum method.
	 */
	@Test
	public void getMaximum() {
		TestCase.assertEquals(19, (int) tree.getMaximum());
	}
	
	/**
	 * Tests the isEmpty method.
	 */
	@Test
	public void isEmpty() {
		TestCase.assertFalse(tree.isEmpty());
		
		BinaryTree<Integer> test = new BinaryTree<Integer>();
		TestCase.assertTrue(test.isEmpty());
	}
	
	/**
	 * Tests the empty method.
	 */
	@Test
	public void empty() {
		TestCase.assertFalse(tree.isEmpty());
		
		tree.empty();
		TestCase.assertTrue(tree.isEmpty());
	}
	
	/**
	 * Tests the contains method.
	 */
	@Test
	public void contains() {
		TestCase.assertTrue(tree.contains(9));
		TestCase.assertFalse(tree.contains(14));
	}
}
