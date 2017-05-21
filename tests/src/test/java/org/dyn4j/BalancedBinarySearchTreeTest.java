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

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for the {@link BinarySearchTree} class.
 * @author William Bittle
 * @version 3.2.3
 * @since 2.2.0
 */
public class BalancedBinarySearchTreeTest {
	/** The golden ratio for testing the height of a balanced binary tree */
	private static final double GOLDEN_RATIO = (1.0 + Math.sqrt(5.0)) / 2.0;
	
	/** The binary tree */
	private BinarySearchTree<Integer> tree;
	
	/**
	 * Sets up the binary tree and populates with data.
	 */
	@Before
	public void setup() {
		tree = new BinarySearchTree<Integer>(true);
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
		tree.insert(-4);
	}
	
	/**
	 * Returns the height limit of a balanced binary tree of the given size.
	 * @param size the size of the tree
	 * @return the height limit
	 */
	private static final double getHeightLimit(int size) {
		return Math.log((size + 2.0) - 1.0) / Math.log(GOLDEN_RATIO);
	}
	
	/**
	 * Tests the insert methods.
	 */
	@Test
	public void insert() {
		// after setup the tree should be balanced
		double hl = getHeightLimit(tree.size());
		TestCase.assertTrue(tree.getHeight() < hl);
		
		TestCase.assertFalse(tree.contains(5));
		TestCase.assertTrue(tree.insert(5));
		TestCase.assertTrue(tree.contains(5));
		
		BinarySearchTree<Integer> t2 = new BinarySearchTree<Integer>();
		t2.insert(14);
		t2.insert(8);
		t2.insert(16);
		t2.insert(15);
		
		TestCase.assertFalse(tree.contains(14));
		TestCase.assertFalse(tree.contains(8));
		TestCase.assertFalse(tree.contains(16));
		TestCase.assertFalse(tree.contains(15));
		
		tree.insertSubtree(t2);
		TestCase.assertTrue(tree.contains(14));
		TestCase.assertTrue(tree.contains(8));
		TestCase.assertTrue(tree.contains(16));
		TestCase.assertTrue(tree.contains(15));
		
		hl = getHeightLimit(tree.size());
		TestCase.assertTrue(tree.getHeight() < hl);
	}
	
	/**
	 * Tests the remove method with a valid value.
	 */
	@Test
	public void remove() {
		TestCase.assertNotNull(tree.remove(-3));
		// make sure it was removed
		TestCase.assertFalse(tree.contains(-3));
		// make sure the other values around that node are still there
		TestCase.assertTrue(tree.contains(-4));
		TestCase.assertTrue(tree.contains(0));
		TestCase.assertTrue(tree.contains(1));
		TestCase.assertTrue(tree.contains(2));
		TestCase.assertTrue(tree.contains(3));
		
		// remove the minimum of the entire tree
		int size = tree.size();
		tree.removeMinimum();
		TestCase.assertFalse(tree.contains(-4));
		TestCase.assertEquals(size - 1, tree.size());
		
		BinarySearchTreeNode<Integer> node = tree.get(10);
		tree.removeMinimum(node);
		TestCase.assertFalse(tree.contains(4));
		TestCase.assertEquals(size - 2, tree.size());
		
		tree.removeMaximum();
		TestCase.assertFalse(tree.contains(19));
		TestCase.assertEquals(size - 3, tree.size());
		
		node = tree.get(0);
		tree.removeMaximum(node);
		TestCase.assertFalse(tree.contains(2));
		TestCase.assertEquals(size - 4, tree.size());
		
		double hl = getHeightLimit(tree.size());
		TestCase.assertTrue(tree.getHeight() < hl);
		
		tree.removeSubtree(0);
		TestCase.assertFalse(tree.contains(0));
		TestCase.assertFalse(tree.contains(-1));
		TestCase.assertFalse(tree.contains(1));
		TestCase.assertFalse(tree.contains(2));
		TestCase.assertEquals(tree.size(), 5);
		
		hl = getHeightLimit(tree.size());
		TestCase.assertTrue(tree.getHeight() < hl);
	}
	
	/**
	 * Tests the remove methods with non-existing values.
	 */
	@Test
	public void removeNotFound() {
		TestCase.assertFalse(tree.remove(7));
		
		BinarySearchTreeNode<Integer> node = new BinarySearchTreeNode<Integer>(-3);
		TestCase.assertFalse(tree.remove(node));
	}
	
	/**
	 * Tests the getDepth methods.
	 */
	@Test
	public void getDepth() {
		TestCase.assertEquals(4, tree.getHeight());
		
		BinarySearchTreeNode<Integer> node = tree.get(-3);
		TestCase.assertEquals(2, tree.getHeight(node));
	}
	
	/**
	 * Tests the getMinimum methods.
	 */
	@Test
	public void getMinimum() {
		TestCase.assertEquals(-4, (int) tree.getMinimum());
		
		BinarySearchTreeNode<Integer> node = tree.get(10);
		TestCase.assertEquals(4, (int) tree.getMinimum(node).comparable);
		
		node = tree.get(1);
		TestCase.assertEquals(1, (int) tree.getMinimum(node).comparable);
	}
	
	/**
	 * Tests the getMaximum methods.
	 */
	@Test
	public void getMaximum() {
		TestCase.assertEquals(19, (int) tree.getMaximum());
		
		BinarySearchTreeNode<Integer> node = tree.get(-3);
		TestCase.assertEquals(-1, (int) tree.getMaximum(node).comparable);
		
		node = tree.get(6);
		TestCase.assertEquals(9, (int) tree.getMaximum(node).comparable);
	}
	
	/**
	 * Tests the isEmpty method.
	 */
	@Test
	public void isEmpty() {
		TestCase.assertFalse(tree.isEmpty());
		
		BinarySearchTree<Integer> test = new BinarySearchTree<Integer>();
		TestCase.assertTrue(test.isEmpty());
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		TestCase.assertFalse(tree.isEmpty());
		TestCase.assertEquals(13, tree.size());
		tree.clear();
		TestCase.assertTrue(tree.isEmpty());
		TestCase.assertEquals(0, tree.size());
		TestCase.assertEquals(0, tree.getHeight());
		TestCase.assertEquals(null, tree.getRoot());
	}
	
	/**
	 * Tests the contains method.
	 */
	@Test
	public void contains() {
		TestCase.assertTrue(tree.contains(9));
		TestCase.assertFalse(tree.contains(14));
		
		BinarySearchTreeNode<Integer> node = tree.get(-3);
		TestCase.assertTrue(tree.contains(node));
		
		node = new BinarySearchTreeNode<Integer>(-3);
		TestCase.assertFalse(tree.contains(node));
	}
	
	/**
	 * Tests the get method.
	 */
	@Test
	public void get() {
		TestCase.assertNotNull(tree.get(-3));
		TestCase.assertNull(tree.get(45));
	}
	
	/**
	 * Tests the size methods.
	 */
	@Test
	public void size() {
		TestCase.assertEquals(13, tree.size());
		
		BinarySearchTreeNode<Integer> node = tree.get(-3);
		TestCase.assertEquals(3, tree.size(node));
	}
	
	/**
	 * Tests the iterators.
	 */
	@Test
	public void iterator() {
		// test in order traversal
		Iterator<Integer> it = tree.inOrderIterator();
		int last = Integer.MIN_VALUE;
		while (it.hasNext()) {
			int i = it.next();
			if (i < last) {
				TestCase.fail();
			}
			last = i;
		}
		// test reverse order traversal
		it = tree.reverseOrderIterator();
		last = Integer.MAX_VALUE;
		while (it.hasNext()) {
			int i = it.next();
			if (i > last) {
				TestCase.fail();
			}
			last = i;
		}
	}
	
	/**
	 * Tests the tail iterator.
	 * @since 3.2.3
	 */
	@Test
	public void tailIterator() {
		Iterator<Integer> it = tree.tailIterator(1);
		int last = Integer.MIN_VALUE;
		int n = 0;
		while (it.hasNext()) {
			int i = it.next();
			if (i < last) {
				TestCase.fail();
			}
			last = i;
			n++;
		}
		TestCase.assertEquals(9, n);
	}
	
	/**
	 * Tests the head iterator.
	 * @since 3.2.3
	 */
	@Test
	public void headIterator() {
		Iterator<Integer> it = tree.headIterator(6);
		int last = Integer.MIN_VALUE;
		int n = 0;
		while (it.hasNext()) {
			int i = it.next();
			if (i < last) {
				TestCase.fail();
			}
			last = i;
			n++;
		}
		TestCase.assertEquals(9, n);
	}
	
	/**
	 * Tests the subset iterator.
	 * @since 3.2.3
	 */
	@Test
	public void subsetIterator() {
		Iterator<Integer> it = tree.subsetIterator(1, 6);
		int last = Integer.MIN_VALUE;
		int n = 0;
		while (it.hasNext()) {
			int i = it.next();
			if (i < last) {
				TestCase.fail();
			}
			last = i;
			n++;
		}
		TestCase.assertEquals(5, n);
	}

	/**
	 * Tests the tail iterator with a value that's not in the tree.
	 * @since 3.2.3
	 */
	@Test
	public void tailIteratorMissing() {
		Iterator<Integer> it = tree.tailIterator(5);
		int last = Integer.MIN_VALUE;
		int n = 0;
		while (it.hasNext()) {
			int i = it.next();
			if (i < last) {
				TestCase.fail();
			}
			last = i;
			n++;
		}
		TestCase.assertEquals(5, n);
	}
	
	/**
	 * Tests the head iterator with a value that's not in the tree.
	 * @since 3.2.3
	 */
	@Test
	public void headIteratorMissing() {
		Iterator<Integer> it = tree.headIterator(5);
		int last = Integer.MIN_VALUE;
		int n = 0;
		while (it.hasNext()) {
			int i = it.next();
			if (i < last) {
				TestCase.fail();
			}
			last = i;
			n++;
		}
		TestCase.assertEquals(8, n);
	}
	
	/**
	 * Tests the subset iterator with values that are not in the tree.
	 * @since 3.2.3
	 */
	@Test
	public void subsetIteratorMissing() {
		Iterator<Integer> it = tree.subsetIterator(-2, 5);
		int last = Integer.MIN_VALUE;
		int n = 0;
		while (it.hasNext()) {
			int i = it.next();
			if (i < last) {
				TestCase.fail();
			}
			last = i;
			n++;
		}
		TestCase.assertEquals(6, n);
	}
}
