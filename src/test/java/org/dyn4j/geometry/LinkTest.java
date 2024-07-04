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
package org.dyn4j.geometry;

import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link Segment} class.
 * @author William Bittle
 * @version 6.0.0
 * @since 4.2.2
 */
public class LinkTest {
	/**
	 * Tests a failed create using one null point.
	 * @since 3.1.0
	 */
	@Test(expected = NullPointerException.class)
	public void createNullPoint1() {
		new Link(null, null, new Vector2(), null);
	}
	
	/**
	 * Tests a failed create using one null point.
	 * @since 3.1.0
	 */
	@Test(expected = NullPointerException.class)
	public void createNullPoint2() {
		new Link(null, new Vector2(), null, null);
	}
	
	/**
	 * Tests coincident points.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCoincident() {
		new Link(null, new Vector2(), new Vector2(), null);
	}
	
	/**
	 * Tests a successful creation.
	 */
	@Test
	public void creatSuccess() {
		Link l = new Link(
			null,
			new Vector2(0.0, 1.0),
			new Vector2(1.0, 2.0),
			null
		);
		
		TestCase.assertEquals(0.500, l.center.x, 1.0e-3);
		TestCase.assertEquals(1.500, l.center.y, 1.0e-3);
		TestCase.assertNotNull(l.toString());
		TestCase.assertNotNull(l.getVertexIterator());
		TestCase.assertEquals(WoundIterator.class, l.getVertexIterator().getClass());
		TestCase.assertNotNull(l.getNormalIterator());
		TestCase.assertEquals(WoundIterator.class, l.getNormalIterator().getClass());
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
		
		for (Link link : links) {
			try { link.rotate(Math.toRadians(30)); TestCase.fail(); } catch (UnsupportedOperationException ex) { }
			try { link.rotate(Rotation.of(Math.toRadians(40))); TestCase.fail(); } catch (UnsupportedOperationException ex) { }
			try { link.rotate(Math.toRadians(40), new Vector2(1, 2)); TestCase.fail(); } catch (UnsupportedOperationException ex) { }
			try { link.rotate(Rotation.of(Math.toRadians(20)), new Vector2(0, 3)); TestCase.fail(); } catch (UnsupportedOperationException ex) { }
			try { link.rotate(Math.toRadians(10), 2, -1); TestCase.fail(); } catch (UnsupportedOperationException ex) { }
			try { link.rotate(Rotation.of(Math.toRadians(45)), -1, -1); TestCase.fail(); } catch (UnsupportedOperationException ex) { }
			try { link.rotateAboutCenter(Math.toRadians(26)); TestCase.fail(); } catch (UnsupportedOperationException ex) { }
		}
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
		
		for (Link link : links) {
			try { link.translate(new Vector2(1, 1)); TestCase.fail(); } catch (UnsupportedOperationException ex) { }
			try { link.translate(6, 7); TestCase.fail(); } catch (UnsupportedOperationException ex) { }
		}
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		Link link = new Link(
				new Vector2(0, 0), 
				new Vector2(1, 1), 
				new Vector2(2, 1), 
				new Vector2(3, 0));
		link.setUserData(new Object());
		
		Link copy = link.copy();
		
		TestCase.assertNotSame(link, copy);
		TestCase.assertNotSame(link.point0, copy.point0);
		TestCase.assertNotSame(link.point3, copy.point3);
		TestCase.assertNotSame(link.center, copy.center);
		TestCase.assertNotSame(link.normals, copy.normals);
		TestCase.assertNotSame(link.vertices, copy.vertices);
		TestCase.assertEquals(link.length, copy.length);
		TestCase.assertEquals(link.radius, copy.radius);
		TestCase.assertNull(copy.userData);
		
		TestCase.assertEquals(link.center.x, copy.center.x);
		TestCase.assertEquals(link.center.y, copy.center.y);
		TestCase.assertEquals(link.point0.x, copy.point0.x);
		TestCase.assertEquals(link.point0.y, copy.point0.y);
		TestCase.assertEquals(link.point3.x, copy.point3.x);
		TestCase.assertEquals(link.point3.y, copy.point3.y);
		
		TestCase.assertEquals(link.normals.length, copy.normals.length);
		for (int i = 0; i < link.normals.length; i++) {
			TestCase.assertEquals(link.normals[i].x, copy.normals[i].x);
			TestCase.assertEquals(link.normals[i].y, copy.normals[i].y);
		}
		
		TestCase.assertEquals(link.vertices.length, copy.vertices.length);
		for (int i = 0; i < link.vertices.length; i++) {
			TestCase.assertEquals(link.vertices[i].x, copy.vertices[i].x);
			TestCase.assertEquals(link.vertices[i].y, copy.vertices[i].y);
		}
	}
}
