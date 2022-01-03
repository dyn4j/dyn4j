/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.geometry.simplify;

import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link SegmentTree} and related classes class.
 * @author William Bittle
 * @version 4.2.1
 * @since 4.2.1
 */
public class SegmentTreeTest {
	/**
	 * Tests create of a segment tree leaf node.
	 */
	@Test
	public void createLeaf() {
		Vector2 p1 = new Vector2(1.0, 2.0);
		Vector2 p2 = new Vector2(3.0, 4.0);
		SegmentTreeLeaf leaf = new SegmentTreeLeaf(p1, p2, 6, 8);
		
		TestCase.assertSame(p1, leaf.point1);
		TestCase.assertSame(p2, leaf.point2);
		TestCase.assertNotNull(leaf.aabb);
		TestCase.assertEquals(0, leaf.height);
		TestCase.assertEquals(6, leaf.index1);
		TestCase.assertEquals(8, leaf.index2);
		TestCase.assertNull(leaf.left);
		TestCase.assertNull(leaf.parent);
		TestCase.assertNull(leaf.right);
		
		TestCase.assertNotNull(leaf.toString());
	}
	
	/**
	 * Tests create of a segment tree leaf node.
	 */
	@Test
	public void createNode() {
		SegmentTreeNode node = new SegmentTreeNode();
		
		TestCase.assertNotNull(node.aabb);
		TestCase.assertEquals(0, node.height);
		TestCase.assertNull(node.left);
		TestCase.assertNull(node.parent);
		TestCase.assertNull(node.right);
		
		TestCase.assertNotNull(node.toString());
	}
}
