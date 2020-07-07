/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link BinarySearchTree} class.
 * <p>
 * For visualizing the tests.
 * https://www.cs.usfca.edu/~galles/visualization/BST.html
 * @author William Bittle
 * @version 4.0.0
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
	 * Tests creation with a subtree.
	 */
	@Test
	public void create() {
		BinarySearchTree<Integer> t2 = new BinarySearchTree<Integer>(this.tree);
		TestCase.assertEquals(tree.size(), t2.size());
		TestCase.assertTrue(t2.contains(10));
		TestCase.assertTrue(t2.contains(3));
		TestCase.assertTrue(t2.contains(-3));
		TestCase.assertTrue(t2.contains(4));
		TestCase.assertTrue(t2.contains(0));
		TestCase.assertTrue(t2.contains(1));
		TestCase.assertTrue(t2.contains(11));
		TestCase.assertTrue(t2.contains(19));
		TestCase.assertTrue(t2.contains(6));
		TestCase.assertTrue(t2.contains(-1));
		TestCase.assertTrue(t2.contains(2));
		TestCase.assertTrue(t2.contains(9));
		TestCase.assertTrue(t2.contains(-4));
		
		BinarySearchTree<Integer> t3 = new BinarySearchTree<Integer>(this.tree, true);
		TestCase.assertEquals(tree.size(), t3.size());
		TestCase.assertTrue(t3.contains(10));
		TestCase.assertTrue(t3.contains(3));
		TestCase.assertTrue(t3.contains(-3));
		TestCase.assertTrue(t3.contains(4));
		TestCase.assertTrue(t3.contains(0));
		TestCase.assertTrue(t3.contains(1));
		TestCase.assertTrue(t3.contains(11));
		TestCase.assertTrue(t3.contains(19));
		TestCase.assertTrue(t3.contains(6));
		TestCase.assertTrue(t3.contains(-1));
		TestCase.assertTrue(t3.contains(2));
		TestCase.assertTrue(t3.contains(9));
		TestCase.assertTrue(t3.contains(-4));
		TestCase.assertTrue(t3.getHeight() <= tree.getHeight());
	}
	
	/**
	 * Tests the insert methods.
	 */
	@Test
	public void insert() {
		TestCase.assertFalse(tree.contains(5));
		TestCase.assertTrue(tree.insert(5));
		TestCase.assertTrue(tree.contains(5));
		TestCase.assertEquals(14, tree.size());
		
		BinarySearchTree<Integer> t2 = new BinarySearchTree<Integer>();
		t2.insert(14);
		t2.insert(8);
		t2.insert(16);
		t2.insert(15);
		
		TestCase.assertFalse(tree.contains(14));
		TestCase.assertFalse(tree.contains(8));
		TestCase.assertFalse(tree.contains(16));
		TestCase.assertFalse(tree.contains(15));
		
		// insert into a populated tree
		TestCase.assertTrue(tree.insertSubtree(t2));
		TestCase.assertTrue(tree.contains(14));
		TestCase.assertTrue(tree.contains(8));
		TestCase.assertTrue(tree.contains(16));
		TestCase.assertTrue(tree.contains(15));
		TestCase.assertTrue(tree.contains(5));
		TestCase.assertTrue(tree.contains(10));
		TestCase.assertEquals(18, tree.size());
		
		// insert into an empty tree
		tree.clear();
		TestCase.assertTrue(tree.insertSubtree(t2));
		TestCase.assertTrue(tree.contains(14));
		TestCase.assertTrue(tree.contains(8));
		TestCase.assertTrue(tree.contains(16));
		TestCase.assertTrue(tree.contains(15));
		TestCase.assertEquals(4, tree.size());
		
		// insert a null tree
		TestCase.assertFalse(tree.insertSubtree(null));
		TestCase.assertTrue(tree.contains(14));
		TestCase.assertTrue(tree.contains(8));
		TestCase.assertTrue(tree.contains(16));
		TestCase.assertTrue(tree.contains(15));
		TestCase.assertEquals(4, tree.size());
		
		// insert a tree who's root is null
		t2.clear();
		TestCase.assertTrue(tree.insertSubtree(t2));
		TestCase.assertTrue(tree.contains(14));
		TestCase.assertTrue(tree.contains(8));
		TestCase.assertTrue(tree.contains(16));
		TestCase.assertTrue(tree.contains(15));
		TestCase.assertEquals(4, tree.size());
	}
	
	/**
	 * Tests the remove method with a valid value.
	 */
	@Test
	public void remove() {
		TestCase.assertTrue(tree.remove(-3));
		// make sure it was removed
		TestCase.assertFalse(tree.contains(-3));
		// make sure the other values around that node are still there
		TestCase.assertTrue(tree.contains(-4));
		TestCase.assertTrue(tree.contains(0));
		TestCase.assertTrue(tree.contains(1));
		TestCase.assertTrue(tree.contains(2));
		TestCase.assertTrue(tree.contains(-1));
		TestCase.assertTrue(tree.contains(3));
		
		// test removing null
		TestCase.assertFalse(tree.remove((Integer)null));
		
		// test removing from empty tree
		tree.clear();
		TestCase.assertFalse(tree.remove(4));
		
		BinarySearchTree<InconsistentElementType> t2 = new BinarySearchTree<InconsistentElementType>();
		t2.insert(new InconsistentElementType(0));
		t2.insert(new InconsistentElementType(2));
		t2.insert(new InconsistentElementType(10));
		t2.insert(new InconsistentElementType(7));
		t2.insert(new InconsistentElementType(1));
		
		TestCase.assertFalse(t2.remove(new InconsistentElementType(7)));
	}

	/**
	 * Tests the remove method with a valid value.
	 */
	@Test
	public void removeMaximum() {
		tree.removeMaximum();
		TestCase.assertFalse(tree.contains(19));
		TestCase.assertEquals(12, tree.size());
		
		BinarySearchTreeNode<Integer> node = tree.get(0);
		tree.removeMaximum(node);
		TestCase.assertFalse(tree.contains(2));
		TestCase.assertEquals(11, tree.size());

		// test removing null
		TestCase.assertNull(tree.removeMaximum(null));
		
		tree.clear();
		tree.insert(0);
		tree.insert(-1);
		TestCase.assertEquals(0, tree.removeMaximum(tree.root).comparable.intValue());
		
		tree.clear();
		tree.insert(0);
		tree.insert(-1);
		tree.insert(1);
		TestCase.assertEquals(-1, tree.removeMaximum(tree.get(-1)).comparable.intValue());
		
		// test removing when empty
		tree.clear();
		TestCase.assertNull(tree.removeMaximum());
	}
	
	/**
	 * Tests the remove method with a valid value.
	 */
	@Test
	public void removeMinimum() {
		tree.removeMinimum();
		TestCase.assertFalse(tree.contains(-4));
		TestCase.assertEquals(12, tree.size());
		
		BinarySearchTreeNode<Integer> node = tree.get(11);
		tree.removeMinimum(node);
		TestCase.assertFalse(tree.contains(11));
		TestCase.assertEquals(11, tree.size());

		// test removing null
		TestCase.assertNull(tree.removeMinimum(null));
		
		tree.clear();
		tree.insert(0);
		tree.insert(1);
		TestCase.assertEquals(0, tree.removeMinimum(tree.root).comparable.intValue());
		
		tree.clear();
		tree.insert(0);
		tree.insert(-1);
		tree.insert(1);
		TestCase.assertEquals(1, tree.removeMinimum(tree.get(1)).comparable.intValue());
		
		// test removing when empty
		tree.clear();
		TestCase.assertNull(tree.removeMinimum());
	}
	
	/**
	 * Tests the removeSubTree method.
	 */
	@Test
	public void removeSubTree() {
		tree.removeSubtree(-3);
		TestCase.assertFalse(tree.contains(0));
		TestCase.assertFalse(tree.contains(-1));
		TestCase.assertFalse(tree.contains(1));
		TestCase.assertFalse(tree.contains(-4));
		TestCase.assertFalse(tree.contains(2));
		TestCase.assertEquals(7, tree.size());
		
		// test not found
		TestCase.assertFalse(tree.removeSubtree(8));
		TestCase.assertFalse(tree.removeSubtree(5));
		
		// test found
		TestCase.assertTrue(tree.removeSubtree(4));
		TestCase.assertFalse(tree.contains(4));
		TestCase.assertFalse(tree.contains(6));
		TestCase.assertFalse(tree.contains(9));
		TestCase.assertEquals(4, tree.size());
		
		// remove null
		TestCase.assertFalse(tree.removeSubtree((Integer)null));
		
		// remove from empty
		tree.clear();
		TestCase.assertEquals(0, tree.size());
		TestCase.assertFalse(tree.removeSubtree(4));
		
		BinarySearchTree<InconsistentElementType> t2 = new BinarySearchTree<InconsistentElementType>();
		t2.insert(new InconsistentElementType(0));
		t2.insert(new InconsistentElementType(2));
		t2.insert(new InconsistentElementType(10));
		t2.insert(new InconsistentElementType(7));
		t2.insert(new InconsistentElementType(1));
		
		TestCase.assertFalse(t2.removeSubtree(new InconsistentElementType(7)));
	}
	
	/**
	 * Tests the storage of the comparable in the node.
	 */
	@Test
	public void nodeComparable() {
		BinarySearchTreeNode<Integer> node = new BinarySearchTreeNode<Integer>(-3);
		TestCase.assertEquals(-3, node.getComparable().intValue());
	}
	
	/**
	 * Tests insertion of null into the BST (it just returns false).
	 */
	@Test
	public void insertNull() {
		TestCase.assertFalse(tree.insert((Integer)null));
	}
	
	/**
	 * Test creating a BST node with a null comparable.
	 */
	@Test(expected = NullPointerException.class)
	public void nodeNullComparable() {
		new BinarySearchTreeNode<Integer>(null);
	}
	
	/**
	 * Tests the toString method. Should print whatever the comparable is.
	 */
	@Test
	public void testToString() {
		BinarySearchTreeNode<Integer> node = new BinarySearchTreeNode<Integer>(-3);
		String str = node.toString();
		TestCase.assertEquals(Integer.valueOf(-3).toString(), str);
		
		str = tree.toString();
		TestCase.assertNotNull(str);
		TestCase.assertTrue(str.length() > 0);
		
		tree.clear();
		str = tree.toString();
		TestCase.assertNotNull(str);
		TestCase.assertTrue(str.length() > 0);
	}
	
	/**
	 * Tests the getDepth methods.
	 */
	@Test
	public void getDepth() {
		TestCase.assertEquals(6, tree.getHeight());
		
		BinarySearchTreeNode<Integer> node = tree.get(-3);
		TestCase.assertEquals(4, tree.getHeight(node));
	}
	
	/**
	 * Tests the getMinimum methods.
	 */
	@Test
	public void getMinimum() {
		TestCase.assertEquals(-4, (int) tree.getMinimum());
		
		BinarySearchTreeNode<Integer> node = tree.get(4);
		TestCase.assertEquals(4, (int) tree.getMinimum(node).comparable);
		
		node = tree.get(0);
		TestCase.assertEquals(-1, (int) tree.getMinimum(node).comparable);
		
		tree.clear();
		TestCase.assertNull(tree.getMinimum());
	}
	
	/**
	 * Tests the getMaximum methods.
	 */
	@Test
	public void getMaximum() {
		TestCase.assertEquals(19, (int) tree.getMaximum());
		
		BinarySearchTreeNode<Integer> node = tree.get(-3);
		TestCase.assertEquals(2, (int) tree.getMaximum(node).comparable);
		
		node = tree.get(11);
		TestCase.assertEquals(19, (int) tree.getMaximum(node).comparable);
		
		tree.clear();
		TestCase.assertNull(tree.getMaximum());
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
		
		TestCase.assertFalse(tree.contains((Integer)null));
		
		tree.clear();
		TestCase.assertFalse(tree.contains(9));
		
		BinarySearchTree<InconsistentElementType> t2 = new BinarySearchTree<InconsistentElementType>();
		t2.insert(new InconsistentElementType(0));
		t2.insert(new InconsistentElementType(2));
		t2.insert(new InconsistentElementType(10));
		t2.insert(new InconsistentElementType(7));
		t2.insert(new InconsistentElementType(1));
		
		TestCase.assertFalse(t2.contains(new InconsistentElementType(7)));
	}
	
	/**
	 * Tests the get method.
	 */
	@Test
	public void get() {
		TestCase.assertNotNull(tree.get(-3));
		TestCase.assertNull(tree.get(45));
		TestCase.assertEquals(10, tree.getRoot().intValue());
		
		tree.clear();
		TestCase.assertEquals(null, tree.getRoot());
		TestCase.assertEquals(null, tree.get(0));
		TestCase.assertEquals(null, tree.get(null));
	}
	
	/**
	 * Tests the size methods.
	 */
	@Test
	public void size() {
		TestCase.assertEquals(13, tree.size());
		
		BinarySearchTreeNode<Integer> node = tree.get(-3);
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
				TestCase.fail("The next item was not greater than the last. In order traversal failed.");
			}
			last = i;
		}
		// test reverse order traversal
		it = tree.reverseOrderIterator();
		last = Integer.MAX_VALUE;
		while (it.hasNext()) {
			int i = it.next();
			if (i > last) {
				TestCase.fail("The next item was not less than the last. Reverse order traversal failed.");
			}
			last = i;
		}
	}
	
	/**
	 * Tests the iterator remove method (should throw).
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void iteratorRemove() {
		// test in order traversal
		Iterator<Integer> it = tree.inOrderIterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
			break;
		}
	}
	
	/**
	 * Tests the iterator throw if hasNext isn't used.
	 */
	@Test(expected = NoSuchElementException.class)
	public void iteratorWithoutCheckingHasNext() {
		BinarySearchTree<Integer> tree = new BinarySearchTree<Integer>();
		tree.insert(10);
		tree.insert(3);
		
		// test in order traversal
		Iterator<Integer> it = tree.inOrderIterator();
		it.next();
		it.next();
		it.next();
	}
	
	/**
	 * Tests the iterator for an empty tree.
	 */
	@Test
	public void iteratorWithNoElements() {
		BinarySearchTree<Integer> tree = new BinarySearchTree<Integer>();
		Iterator<Integer> it = tree.inOrderIterator();
		while (it.hasNext()) {
			it.next();
		}
	}
	
	/**
	 * Tests the conversion from not self balancing to self balancing.
	 */
	@Test
	public void balance() {
		TestCase.assertFalse(tree.isSelfBalancing());
		
		TestCase.assertEquals(6, tree.getHeight());
		
		tree.setSelfBalancing(true);
		
		TestCase.assertEquals(4, tree.getHeight());
		
		TestCase.assertTrue(tree.isSelfBalancing());
		
		TestCase.assertNull(tree.balance(null));
		
		tree.clear();
		tree.insert(1);
		TestCase.assertEquals(tree.root, tree.balance(tree.root));
	}
}
