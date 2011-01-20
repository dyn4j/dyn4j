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

import java.util.Iterator;

import junit.framework.TestCase;

import org.dyn4j.game2d.BinarySearchTree.Node;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for the {@link BinarySearchTree} class.
 * @author William Bittle
 * @version 2.2.0
 * @since 2.2.0
 */
public class BinarySearchTreeTest {
	/** The binary tree */
	private BinarySearchTree<Integer> tree;
	
	/**
	 * Sets up the binary tree and populates with data.
	 */
	@Before
	public void setup() {
		tree = new BinarySearchTree<Integer>();
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
	 * Tests the insert methods.
	 */
	@Test
	public void insert() {
		TestCase.assertFalse(tree.contains(5));
		BinarySearchTree.Node<Integer> node = tree.insert(5);
		
		TestCase.assertTrue(tree.contains(node));
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
		
		tree.insertSubtree(t2.getRoot());
		TestCase.assertTrue(tree.contains(14));
		TestCase.assertTrue(tree.contains(8));
		TestCase.assertTrue(tree.contains(16));
		TestCase.assertTrue(tree.contains(15));
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
		TestCase.assertTrue(tree.size() + 1 == size);
		
		BinarySearchTree.Node<Integer> node = tree.get(0);
		tree.removeMinimum(node);
		TestCase.assertFalse(tree.contains(0));
		TestCase.assertTrue(tree.size() + 2 == size);
		
		tree.removeMaximum();
		TestCase.assertFalse(tree.contains(19));
		TestCase.assertTrue(tree.size() + 3 == size);
		
		tree.removeMaximum(node);
		TestCase.assertFalse(tree.contains(2));
		TestCase.assertTrue(tree.size() + 4 == size);
		
		tree.removeSubtree(3);
		TestCase.assertFalse(tree.contains(3));
		TestCase.assertFalse(tree.contains(4));
		TestCase.assertFalse(tree.contains(6));
		TestCase.assertTrue(tree.size() == 2);
	}
	
	/**
	 * Tests the remove methods with non-existing values.
	 */
	@Test
	public void removeNotFound() {
		TestCase.assertNull(tree.remove(7));
		
		Node<Integer> node = new Node<Integer>(-3);
		TestCase.assertFalse(tree.remove(node));
	}
	
	/**
	 * Tests the getDepth methods.
	 */
	@Test
	public void getDepth() {
		TestCase.assertEquals(6, tree.getDepth());
		
		Node<Integer> node = tree.get(-3);
		TestCase.assertEquals(4, tree.getDepth(node));
	}
	
	/**
	 * Tests the getMinimum methods.
	 */
	@Test
	public void getMinimum() {
		TestCase.assertEquals(-4, (int) tree.getMinimum().getComparable());
		
		Node<Integer> node = tree.get(4);
		TestCase.assertEquals(4, (int) tree.getMinimum(node).getComparable());
		
		node = tree.get(0);
		TestCase.assertEquals(-1, (int) tree.getMinimum(node).getComparable());
	}
	
	/**
	 * Tests the getMaximum methods.
	 */
	@Test
	public void getMaximum() {
		TestCase.assertEquals(19, (int) tree.getMaximum().getComparable());
		
		Node<Integer> node = tree.get(-3);
		TestCase.assertEquals(2, (int) tree.getMaximum(node).getComparable());
		
		node = tree.get(11);
		TestCase.assertEquals(19, (int) tree.getMaximum(node).getComparable());
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
		
		tree.clear();
		TestCase.assertTrue(tree.isEmpty());
	}
	
	/**
	 * Tests the contains method.
	 */
	@Test
	public void contains() {
		TestCase.assertTrue(tree.contains(9));
		TestCase.assertFalse(tree.contains(14));
		
		Node<Integer> node = tree.get(-3);
		TestCase.assertTrue(tree.contains(node));
		
		node = new Node<Integer>(-3);
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
		
		Node<Integer> node = tree.get(-3);
		TestCase.assertEquals(6, tree.size(node));
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
}
