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
package org.dyn4j.geometry;

import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link Segment} class.
 * @author William Bittle
 * @version 4.2.2
 * @since 4.2.2
 */
public class LinkTest {
	/**
	 * Tests a failed create using one null point.
	 * @since 3.1.0
	 */
	@Test(expected = NullPointerException.class)
	public void createNullPoint1() {
		new Link(null, new Vector2());
	}
	
	/**
	 * Tests a failed create using one null point.
	 * @since 3.1.0
	 */
	@Test(expected = NullPointerException.class)
	public void createNullPoint2() {
		new Link(new Vector2(), null);
	}
	
	/**
	 * Tests coincident points.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCoincident() {
		new Link(new Vector2(), new Vector2());
	}
	
	/**
	 * Tests a successful creation.
	 */
	@Test
	public void creatSuccess() {
		Link l = new Link(
			new Vector2(0.0, 1.0),
			new Vector2(1.0, 2.0)
		);
		
		TestCase.assertEquals(0.500, l.center.x, 1.0e-3);
		TestCase.assertEquals(1.500, l.center.y, 1.0e-3);
		TestCase.assertNotNull(l.toString());
		TestCase.assertNotNull(l.getVertexIterator());
		TestCase.assertEquals(WoundIterator.class, l.getVertexIterator().getClass());
		TestCase.assertNotNull(l.getNormalIterator());
		TestCase.assertEquals(WoundIterator.class, l.getNormalIterator().getClass());
		TestCase.assertNull(l.next);
		TestCase.assertNull(l.previous);
		TestCase.assertNull(l.getNext());
		TestCase.assertNull(l.getPrevious());
		TestCase.assertNull(l.getPoint0());
		TestCase.assertNotNull(l.getPoint1());
		TestCase.assertNotNull(l.getPoint2());
		TestCase.assertEquals(l.vertices[0], l.getPoint1());
		TestCase.assertEquals(l.vertices[1], l.getPoint2());
		TestCase.assertNull(l.getPoint3());
	}
	
	/**
	 * Tests the rotate method.
	 */
	@Test
	public void rotate() {
		List<Link> links = Geometry.createLinks(new Vector2[] {
			new Vector2(0.0, 0.0), 
			new Vector2(1.0, 0.0), 
			new Vector2(2.0, 1.0), 
			new Vector2(3.0, 1.0), 
		}, false);
		
		TestCase.assertEquals(3, links.size());
		
		Link l = links.get(1);
		TestCase.assertEquals(1.500, l.center.x, 1.0e-3);
		TestCase.assertEquals(0.500, l.center.y, 1.0e-3);
		
		double l1 = links.get(0).getLength();
		double l2 = links.get(1).getLength();
		double l3 = links.get(2).getLength();
		TestCase.assertEquals(1.0, l1, 1.0e-3);
		TestCase.assertEquals(Math.sqrt(2.0), l2, 1.0e-3);
		TestCase.assertEquals(1.0, l3, 1.0e-3);
		
		TestCase.assertEquals( 1.000, links.get(0).normals[0].x, 1.0e-3);
		TestCase.assertEquals( 0.000, links.get(0).normals[0].y, 1.0e-3);
		TestCase.assertEquals( 0.707, links.get(1).normals[0].x, 1.0e-3);
		TestCase.assertEquals( 0.707, links.get(1).normals[0].y, 1.0e-3);
		TestCase.assertEquals( 1.000, links.get(2).normals[0].x, 1.0e-3);
		TestCase.assertEquals( 0.000, links.get(2).normals[0].y, 1.0e-3);
		TestCase.assertEquals( 0.000, links.get(0).normals[1].x, 1.0e-3);
		TestCase.assertEquals( 1.000, links.get(0).normals[1].y, 1.0e-3);
		TestCase.assertEquals(-0.707, links.get(1).normals[1].x, 1.0e-3);
		TestCase.assertEquals( 0.707, links.get(1).normals[1].y, 1.0e-3);
		TestCase.assertEquals( 0.000, links.get(2).normals[1].x, 1.0e-3);
		TestCase.assertEquals( 1.000, links.get(2).normals[1].y, 1.0e-3);
		
		l.rotate(Math.toRadians(45), l.center.x, l.center.y);
		TestCase.assertEquals(1.500, l.center.x, 1.0e-3);
		TestCase.assertEquals(0.500, l.center.y, 1.0e-3);
		TestCase.assertEquals(1.500, l.vertices[0].x, 1.0e-3);
		TestCase.assertEquals(-0.207, l.vertices[0].y, 1.0e-3);
		TestCase.assertEquals(1.500, l.vertices[1].x, 1.0e-3);
		TestCase.assertEquals(1.207, l.vertices[1].y, 1.0e-3);
		TestCase.assertEquals(links.get(0).getPoint2(), links.get(1).getPoint1());
		TestCase.assertEquals(links.get(2).getPoint1(), links.get(1).getPoint2());
		
		l1 = links.get(0).getLength();
		l2 = links.get(1).getLength();
		l3 = links.get(2).getLength();
		TestCase.assertEquals(l.getPoint0().distance(l.getPoint1()), l1, 1.0e-3);
		TestCase.assertEquals(Math.sqrt(2), l2, 1.0e-3);
		TestCase.assertEquals(l.getPoint2().distance(l.getPoint3()), l3, 1.0e-3);
		
		TestCase.assertEquals( 0.990, links.get(0).normals[0].x, 1.0e-3);
		TestCase.assertEquals(-0.136, links.get(0).normals[0].y, 1.0e-3);
		TestCase.assertEquals( 0.000, links.get(1).normals[0].x, 1.0e-3);
		TestCase.assertEquals( 1.000, links.get(1).normals[0].y, 1.0e-3);
		TestCase.assertEquals( 0.990, links.get(2).normals[0].x, 1.0e-3);
		TestCase.assertEquals(-0.136, links.get(2).normals[0].y, 1.0e-3);
		TestCase.assertEquals( 0.136, links.get(0).normals[1].x, 1.0e-3);
		TestCase.assertEquals( 0.990, links.get(0).normals[1].y, 1.0e-3);
		TestCase.assertEquals(-1.000, links.get(1).normals[1].x, 1.0e-3);
		TestCase.assertEquals( 0.000, links.get(1).normals[1].y, 1.0e-3);
		TestCase.assertEquals( 0.136, links.get(2).normals[1].x, 1.0e-3);
		TestCase.assertEquals( 0.990, links.get(2).normals[1].y, 1.0e-3);
	}
	
	/**
	 * Tests the translate method.
	 */
	@Test
	public void translate() {
		List<Link> links = Geometry.createLinks(new Vector2[] {
			new Vector2(0.0, 0.0), 
			new Vector2(1.0, 0.0), 
			new Vector2(2.0, 1.0), 
			new Vector2(3.0, 1.0), 
		}, false);
		
		TestCase.assertEquals(3, links.size());
		
		Link l = links.get(1);
		TestCase.assertEquals(1.500, l.center.x, 1.0e-3);
		TestCase.assertEquals(0.500, l.center.y, 1.0e-3);
		
		double l1 = links.get(0).getLength();
		double l2 = links.get(1).getLength();
		double l3 = links.get(2).getLength();
		TestCase.assertEquals(1.0, l1, 1.0e-3);
		TestCase.assertEquals(Math.sqrt(2.0), l2, 1.0e-3);
		TestCase.assertEquals(1.0, l3, 1.0e-3);
		
		TestCase.assertEquals( 1.000, links.get(0).normals[0].x, 1.0e-3);
		TestCase.assertEquals( 0.000, links.get(0).normals[0].y, 1.0e-3);
		TestCase.assertEquals( 0.707, links.get(1).normals[0].x, 1.0e-3);
		TestCase.assertEquals( 0.707, links.get(1).normals[0].y, 1.0e-3);
		TestCase.assertEquals( 1.000, links.get(2).normals[0].x, 1.0e-3);
		TestCase.assertEquals( 0.000, links.get(2).normals[0].y, 1.0e-3);
		TestCase.assertEquals( 0.000, links.get(0).normals[1].x, 1.0e-3);
		TestCase.assertEquals( 1.000, links.get(0).normals[1].y, 1.0e-3);
		TestCase.assertEquals(-0.707, links.get(1).normals[1].x, 1.0e-3);
		TestCase.assertEquals( 0.707, links.get(1).normals[1].y, 1.0e-3);
		TestCase.assertEquals( 0.000, links.get(2).normals[1].x, 1.0e-3);
		TestCase.assertEquals( 1.000, links.get(2).normals[1].y, 1.0e-3);
		
		l.translate(0.5, 0.5);
		
		TestCase.assertEquals(2.000, l.center.x, 1.0e-3);
		TestCase.assertEquals(1.000, l.center.y, 1.0e-3);
		TestCase.assertEquals(1.500, l.vertices[0].x, 1.0e-3);
		TestCase.assertEquals(0.500, l.vertices[0].y, 1.0e-3);
		TestCase.assertEquals(2.500, l.vertices[1].x, 1.0e-3);
		TestCase.assertEquals(1.500, l.vertices[1].y, 1.0e-3);
		TestCase.assertEquals(links.get(0).getPoint2(), links.get(1).getPoint1());
		TestCase.assertEquals(links.get(2).getPoint1(), links.get(1).getPoint2());
		
		l1 = links.get(0).getLength();
		l2 = links.get(1).getLength();
		l3 = links.get(2).getLength();
		TestCase.assertEquals(l.getPoint0().distance(l.getPoint1()), l1, 1.0e-3);
		TestCase.assertEquals(Math.sqrt(2), l2, 1.0e-3);
		TestCase.assertEquals(l.getPoint2().distance(l.getPoint3()), l3, 1.0e-3);
		
		TestCase.assertEquals( 0.948, links.get(0).normals[0].x, 1.0e-3);
		TestCase.assertEquals( 0.316, links.get(0).normals[0].y, 1.0e-3);
		TestCase.assertEquals( 0.707, links.get(1).normals[0].x, 1.0e-3);
		TestCase.assertEquals( 0.707, links.get(1).normals[0].y, 1.0e-3);
		TestCase.assertEquals( 0.707, links.get(2).normals[0].x, 1.0e-3);
		TestCase.assertEquals(-0.707, links.get(2).normals[0].y, 1.0e-3);
		TestCase.assertEquals(-0.316, links.get(0).normals[1].x, 1.0e-3);
		TestCase.assertEquals( 0.948, links.get(0).normals[1].y, 1.0e-3);
		TestCase.assertEquals(-0.707, links.get(1).normals[1].x, 1.0e-3);
		TestCase.assertEquals( 0.707, links.get(1).normals[1].y, 1.0e-3);
		TestCase.assertEquals( 0.707, links.get(2).normals[1].x, 1.0e-3);
		TestCase.assertEquals( 0.707, links.get(2).normals[1].y, 1.0e-3);
	}
	
}
