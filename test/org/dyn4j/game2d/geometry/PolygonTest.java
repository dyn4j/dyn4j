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
package org.dyn4j.game2d.geometry;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test case for the {@link Polygon} class.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public class PolygonTest {
	/**
	 * Tests not enough points.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNotEnoughPoints() {
		new Polygon(new Vector2[] {
			new Vector2(), 
			new Vector2()
		});
	}
	
	/**
	 * Tests not CCW.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNotCCW() {
		new Polygon(new Vector2[] {
			new Vector2(), 
			new Vector2(2.0, 2.0), 
			new Vector2(1.0, 0.0)
		});
	}
	
	/**
	 * Tests that the triangle is CCW.
	 */
	@Test
	public void createCCW() {
		new Polygon(new Vector2[] {
			new Vector2(0.5, 0.5),
			new Vector2(-0.3, -0.5),
			new Vector2(1.0, -0.3)
		});
	}
	
	/**
	 * Tests coincident points
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCoincident() {
		new Polygon(new Vector2[] {
			new Vector2(),
			new Vector2(2.0, 2.0),
			new Vector2(2.0, 2.0),
			new Vector2(1.0, 0.0)
		});
	}
	
	/**
	 * Tests non convex.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNonConvex() {
		new Polygon(new Vector2[] {
			new Vector2(1.0, 1.0),
			new Vector2(-1.0, 1.0),
			new Vector2(-0.5, 0.0),
			new Vector2(-1.0, -1.0),
			new Vector2(1.0, -1.0)
		});
	}
	
	/**
	 * Tests null point array.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullPoints() {
		new Polygon((Vector2[])null);
	}
	
	/**
	 * Tests an array with null points
	 */
	@Test(expected = NullPointerException.class)
	public void createNullPoint() {
		new Polygon(new Vector2[] {
			new Vector2(),
			null,
			new Vector2(0, 2)
		});
	}
	
	/**
	 * Tests the constructor.
	 */
	@Test
	public void createSuccess() {
		new Polygon(new Vector2[] {
			new Vector2(0.0, 1.0),
			new Vector2(-2.0, -2.0),
			new Vector2(1.0, -2.0)
		});
	}
	
	/**
	 * Tests the contains method.
	 */
	@Test
	public void contains() {
		Vector2[] vertices = new Vector2[] {
			new Vector2(0.0, 1.0),
			new Vector2(-1.0, 0.0),
			new Vector2(1.0, 0.0)
		};
		Polygon p = new Polygon(vertices);
		
		Transform t = new Transform();
		Vector2 pt = new Vector2(2.0, 4.0);
		
		// shouldn't be in the polygon
		TestCase.assertTrue(!p.contains(pt, t));
		
		// move the polygon a bit
		t.translate(2.0, 3.5);
		
		// should be in the polygon
		TestCase.assertTrue(p.contains(pt, t));
		
		t.translate(0.0, -0.5);
		
		// should be on a vertex
		TestCase.assertTrue(p.contains(pt, t));
	}
	
	/**
	 * Tests the project method.
	 */
	@Test
	public void project() {
		Vector2[] vertices = new Vector2[] {
				new Vector2(0.0, 1.0),
				new Vector2(-1.0, 0.0),
				new Vector2(1.0, 0.0)
			};
		Polygon p = new Polygon(vertices);
		Transform t = new Transform();
		Vector2 x = new Vector2(1.0, 0.0);
		Vector2 y = new Vector2(0.0, 1.0);
		
		t.translate(1.0, 0.5);
		
		Interval i = p.project(x, t);
		
		TestCase.assertEquals(0.000, i.min, 1.0e-3);
		TestCase.assertEquals(2.000, i.max, 1.0e-3);
		
		// rotating about the center
		t.rotate(Math.toRadians(90), 1.0, 0.5);
		
		i = p.project(y, t);
		
		TestCase.assertEquals(-0.500, i.min, 1.0e-3);
		TestCase.assertEquals(1.500, i.max, 1.0e-3);
	}
	
	/**
	 * Tests the farthest methods.
	 */
	@Test
	public void getFarthest() {
		Vector2[] vertices = new Vector2[] {
				new Vector2(0.0, 1.0),
				new Vector2(-1.0, -1.0),
				new Vector2(1.0, -1.0)
			};
		Polygon p = new Polygon(vertices);
		Transform t = new Transform();
		Vector2 y = new Vector2(0.0, -1.0);
		
		Edge f = p.getFarthestFeature(y, t);
		// should always get an edge
		TestCase.assertTrue(f.isEdge());
		TestCase.assertEquals(-1.000, f.max.point.x, 1.0e-3);
		TestCase.assertEquals(-1.000, f.max.point.y, 1.0e-3);
		TestCase.assertEquals(-1.000, f.vertex1.point.x, 1.0e-3);
		TestCase.assertEquals(-1.000, f.vertex1.point.y, 1.0e-3);
		TestCase.assertEquals( 1.000, f.vertex2.point.x, 1.0e-3);
		TestCase.assertEquals(-1.000, f.vertex2.point.y, 1.0e-3);
		
		Vector2 pt = p.getFarthestPoint(y, t);
		
		TestCase.assertEquals(-1.000, pt.x, 1.0e-3);
		TestCase.assertEquals(-1.000, pt.y, 1.0e-3);
		
		// rotating about the origin
		t.rotate(Math.toRadians(90), 0, 0);
		
		pt = p.getFarthestPoint(y, t);
		
		TestCase.assertEquals( 1.000, pt.x, 1.0e-3);
		TestCase.assertEquals(-1.000, pt.y, 1.0e-3);
	}
	
	/**
	 * Tests the getAxes method.
	 */
	@Test
	public void getAxes() {
		Vector2[] vertices = new Vector2[] {
				new Vector2(0.0, 1.0),
				new Vector2(-1.0, -1.0),
				new Vector2(1.0, -1.0)
			};
		Polygon p = new Polygon(vertices);
		Transform t = new Transform();
		
		Vector2[] axes = p.getAxes(null, t);
		TestCase.assertNotNull(axes);
		TestCase.assertEquals(3, axes.length);
		
		// test passing some focal points
		Vector2 pt = new Vector2(-3.0, 2.0);
		axes = p.getAxes(new Vector2[] {pt}, t);
		TestCase.assertEquals(4, axes.length);
		
		// make sure the axes are perpendicular to the edges
		Vector2 ab = p.vertices[0].to(p.vertices[1]);
		Vector2 bc = p.vertices[1].to(p.vertices[2]);
		Vector2 ca = p.vertices[2].to(p.vertices[0]);
		
		TestCase.assertEquals(0.000, ab.dot(axes[0]), 1.0e-3);
		TestCase.assertEquals(0.000, bc.dot(axes[1]), 1.0e-3);
		TestCase.assertEquals(0.000, ca.dot(axes[2]), 1.0e-3);
		
		// make sure that the focal axes are correct
		TestCase.assertEquals(0.000, p.vertices[0].to(pt).cross(axes[3]), 1.0e-3);
	}
	
	/**
	 * Tests the getFoci method.
	 */
	@Test
	public void getFoci() {
		Vector2[] vertices = new Vector2[] {
				new Vector2(0.0, 1.0),
				new Vector2(-1.0, -1.0),
				new Vector2(1.0, -1.0)
			};
		Polygon p = new Polygon(vertices);
		Transform t = new Transform();
		// should return none
		Vector2[] foci = p.getFoci(t);
		TestCase.assertNull(foci);
	}
	
	/**
	 * Tests the rotate methods.
	 */
	@Test
	public void rotate() {
		Vector2[] vertices = new Vector2[] {
				new Vector2(0.0, 1.0),
				new Vector2(-1.0, -1.0),
				new Vector2(1.0, -1.0)
			};
		Polygon p = new Polygon(vertices);
		
		// should move the points
		p.rotate(Math.toRadians(90), 0, 0);
		
		TestCase.assertEquals(-1.000, p.vertices[0].x, 1.0e-3);
		TestCase.assertEquals( 0.000, p.vertices[0].y, 1.0e-3);
		
		TestCase.assertEquals( 1.000, p.vertices[1].x, 1.0e-3);
		TestCase.assertEquals(-1.000, p.vertices[1].y, 1.0e-3);
		
		TestCase.assertEquals( 1.000, p.vertices[2].x, 1.0e-3);
		TestCase.assertEquals( 1.000, p.vertices[2].y, 1.0e-3);
	}
	
	/**
	 * Tests the translate methods.
	 */
	@Test
	public void translate() {
		Vector2[] vertices = new Vector2[] {
				new Vector2(0.0, 1.0),
				new Vector2(-1.0, -1.0),
				new Vector2(1.0, -1.0)
			};
		Polygon p = new Polygon(vertices);
		
		p.translate(1.0, -0.5);
		
		TestCase.assertEquals( 1.000, p.vertices[0].x, 1.0e-3);
		TestCase.assertEquals( 0.500, p.vertices[0].y, 1.0e-3);
		
		TestCase.assertEquals( 0.000, p.vertices[1].x, 1.0e-3);
		TestCase.assertEquals(-1.500, p.vertices[1].y, 1.0e-3);
		
		TestCase.assertEquals( 2.000, p.vertices[2].x, 1.0e-3);
		TestCase.assertEquals(-1.500, p.vertices[2].y, 1.0e-3);
	}
}
