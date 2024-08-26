package org.dyn4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests the {@link AVLTree} class.
 * @author William Bittle
 * @version 6.0.0
 * @since 6.0.0
 */
public class AVLTreeTest {
	private static final int[] VALUES = new int[] { 10, 3, -3, 4, 0, 1, 11, 19, 6, -1, 2, 9, -4, -8, 14, -5, 7, 31, 20, 8, -15, -11, -6, 16, -40 };
	
	private AVLTree<Integer> tree;
	private List<Integer> list;
	
	/**
	 * Setup with the same tree for all tests.
	 */
	@Before
	public void setup() {
		this.tree = new AVLTree<Integer>();
		this.list = new ArrayList<Integer>();
		
		for (int i = 0; i < VALUES.length; i++) {
			tree.insert(VALUES[i]);
			list.add(VALUES[i]);
		}
	}
	
	/**
	 * Tests that the tree fails to insert a duplicate node.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void insertDuplicate() {
		tree.insert(3);
	}
	
	/**
	 * Tests the insert method at each stage by inserting each element in the
	 * array above in the order it's in the array.
	 */
	@Test
	public void insertSequential() {
		AVLTree<Integer> tree = new AVLTree<Integer>();

		for (int i = 0; i < VALUES.length; i++) {
			tree.insert(VALUES[i]);
			
			TestCase.assertEquals(i + 1, tree.size());
			TestCase.assertTrue(tree.isBalanced());
			TestCase.assertTrue(tree.isValid());
			TestCase.assertTrue(tree.contains(VALUES[i]));
		}
	}
	
	/**
	 * Tests the insert method at each stage.
	 */
	@Test
	public void insertInOrder() {
		AVLTree<Integer> tree = new AVLTree<Integer>();
		
		Collections.sort(this.list);

		for (int i = 0; i < this.list.size(); i++) {
			tree.insert(this.list.get(i));
			
			TestCase.assertEquals(i + 1, tree.size());
			TestCase.assertTrue(tree.isBalanced());
			TestCase.assertTrue(tree.isValid());
			TestCase.assertTrue(tree.contains(this.list.get(i)));
		}
	}
	
	/**
	 * Tests the insert method at each stage.
	 */
	@Test
	public void insertReverseOrder() {
		AVLTree<Integer> tree = new AVLTree<Integer>();
		
		Collections.reverse(this.list);

		for (int i = 0; i < this.list.size(); i++) {
			tree.insert(this.list.get(i));
			
			TestCase.assertEquals(i + 1, tree.size());
			TestCase.assertTrue(tree.isBalanced());
			TestCase.assertTrue(tree.isValid());
			TestCase.assertTrue(tree.contains(this.list.get(i)));
		}
	}
	
	/**
	 * Tests the insert method at each stage.
	 */
	@Test
	public void insertReverseOrderSorted() {
		AVLTree<Integer> tree = new AVLTree<Integer>();
		
		Collections.sort(this.list);
		Collections.reverse(this.list);

		for (int i = 0; i < this.list.size(); i++) {
			tree.insert(this.list.get(i));
			
			TestCase.assertEquals(i + 1, tree.size());
			TestCase.assertTrue(tree.isBalanced());
			TestCase.assertTrue(tree.isValid());
			TestCase.assertTrue(tree.contains(this.list.get(i)));
		}
	}
	
	/**
	 * Tests the insert method at each stage.
	 */
	@Test
	public void insertShuffled() {
		AVLTree<Integer> tree = new AVLTree<Integer>();
		
		Collections.shuffle(this.list, new Random(17));

		for (int i = 0; i < this.list.size(); i++) {
			tree.insert(this.list.get(i));
			
			TestCase.assertEquals(i + 1, tree.size());
			TestCase.assertTrue(tree.isBalanced());
			TestCase.assertTrue(tree.isValid());
			TestCase.assertTrue(tree.contains(this.list.get(i)));
		}
	}
	
	/**
	 * Tests removing elements one after another.
	 */
	@Test
	public void sequentialRemoval() {
		tree.remove(4);
		
		TestCase.assertEquals(VALUES.length - 1, tree.size());
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(4));
		
		tree.insert(4);
		
		TestCase.assertEquals(VALUES.length, tree.size());
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertTrue(tree.contains(4));
		
		tree.remove(-11);
		
		TestCase.assertEquals(VALUES.length - 1, tree.size());
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(-11));
		
		tree.remove(-6);
		
		TestCase.assertEquals(VALUES.length - 2, tree.size());
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(-6));
		
		tree.remove(9);
		
		TestCase.assertEquals(VALUES.length - 3, tree.size());
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(9));
		
		tree.remove(10);
		
		TestCase.assertEquals(VALUES.length - 4, tree.size());
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(10));
		
		tree.remove(16);
		
		TestCase.assertEquals(VALUES.length - 5, tree.size());
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(16));
		
		tree.remove(-5);
		
		TestCase.assertEquals(VALUES.length - 6, tree.size());
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(-5));
		
		tree.remove(6);
		
		TestCase.assertEquals(VALUES.length - 7, tree.size());
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(6));
		
		tree.remove(3);
		
		TestCase.assertEquals(VALUES.length - 8, tree.size());
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(3));
	}
	
	/**
	 * Tests a specific case of removing the root node.
	 */
	@Test
	public void removeRoot() {
		tree.remove(19);
		TestCase.assertEquals(VALUES.length - 1, tree.size());
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(19));

		tree.remove(3);
		TestCase.assertEquals(VALUES.length - 2, tree.size());
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(3));
	}
	
	/**
	 * Tests the specific case of removing the root node and replacing it with it's right node.
	 */
	@Test
	public void removeRootNodeAndReplaceWithRight() {
		tree.remove(3);
		tree.remove(-8);
		tree.remove(11);
		tree.remove(6);
		tree.remove(14);
		tree.remove(3);
		tree.remove(14);
		tree.remove(-40);
		tree.remove(31);
		tree.remove(8);
		tree.remove(20);
		tree.remove(31);
		tree.remove(-3);
		tree.remove(0);
		tree.remove(20);
		tree.remove(-4);
		tree.remove(-1);
		tree.remove(-6);
		tree.remove(31);
		tree.remove(-4);
		tree.remove(0);
		tree.remove(10);
		tree.remove(-15);
		tree.remove(20);
		tree.remove(20);
		tree.remove(-1);
		tree.remove(-11);
		tree.remove(3);
		tree.remove(14);
		tree.remove(-1);
		tree.remove(11);
		tree.remove(4);
		tree.remove(-1);
		tree.remove(8);
		tree.remove(9);
		tree.remove(9);
		
		// this is what caused the issue
		tree.remove(7);
		
		TestCase.assertEquals(5, tree.size);
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(7));
	}
	
	/**
	 * Tests removal in random order 1000 times.
	 */
	@Test
	public void random() {
		for (int n = 0; n < 1000; n++) {
			AVLTree<Integer> tree = new AVLTree<Integer>();
			
			for (int i = 0; i < VALUES.length; i++) {
				tree.insert(VALUES[i]);
				
				TestCase.assertEquals(i + 1, tree.size());
				TestCase.assertTrue(tree.isBalanced());
				TestCase.assertTrue(tree.isValid());
				TestCase.assertTrue(tree.contains(VALUES[i]));
			}

			Random r = new Random();
			
			while (tree.size > 0) {
				int index = r.nextInt(VALUES.length);
				int value = VALUES[index];
	
	//			System.out.println("Removing " + value);
				tree.remove(value);
	
				TestCase.assertTrue(tree.isBalanced());
				TestCase.assertTrue(tree.isValid());
				TestCase.assertFalse(tree.contains(value));
			}
		}
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
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(-40));
		TestCase.assertEquals(size - 1, tree.size());
		
		BinarySearchTreeNode<Integer> node = tree.get(10);
		tree.removeMinimum(node);
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(4));
		TestCase.assertEquals(size - 2, tree.size());
		
		tree.removeMaximum();
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(31));
		TestCase.assertEquals(size - 3, tree.size());
		
		node = tree.get(0);
		tree.removeMaximum(node);
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertFalse(tree.contains(2));
		TestCase.assertEquals(size - 4, tree.size());
	}
	
	/**
	 * Tests the remove methods with non-existing values.
	 */
	@Test
	public void removeNotFound() {
		TestCase.assertFalse(tree.remove(44));
	}
	
	/**
	 * Tests the getDepth methods.
	 */
	@Test
	public void getDepth() {
		TestCase.assertEquals(5, tree.getHeight());
		
		BinarySearchTreeNode<Integer> node = tree.get(-3);
		TestCase.assertEquals(2, tree.getHeight(node));
	}
	
	/**
	 * Tests the getMinimum methods.
	 */
	@Test
	public void getMinimum() {
		TestCase.assertEquals(-40, (int) tree.getMinimum());
		
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
		TestCase.assertEquals(31, (int) tree.getMaximum());
		
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
		
		AVLTree<Integer> test = new AVLTree<Integer>();
		TestCase.assertTrue(test.isEmpty());
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		TestCase.assertFalse(tree.isEmpty());
		TestCase.assertEquals(VALUES.length, tree.size());
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
		TestCase.assertFalse(tree.contains(-60));
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
		TestCase.assertEquals(VALUES.length, tree.size());
		
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
		Iterator<Integer> it = tree.tailIterator(0);
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
		TestCase.assertEquals(16, n);
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
		TestCase.assertEquals(15, n);
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
		TestCase.assertEquals(11, n);
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
		TestCase.assertEquals(14, n);
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
	
	/**
	 * A test of a failure case submitted by user Goncalo Marques.
	 */
	@Test
	public void goncaloMarquesIssue() {
		AVLTree<Integer> tree = new AVLTree<Integer>();
		tree.insert(3);
		tree.insert(1);
		tree.insert(2);
		
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertEquals(2, tree.getHeight());
		
	}
	
	/**
	 * A simple insert test.
	 */
	@Test
	public void insertSimple() {
		AVLTree<Integer> tree = new AVLTree<Integer>();
		tree.insert(5);
		tree.insert(1);
		tree.insert(2);
		tree.insert(3);
		
		TestCase.assertTrue(tree.isBalanced());
		TestCase.assertTrue(tree.isValid());
		TestCase.assertEquals(3, tree.getHeight());
		
	}
}
