/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test case for the {@link Geometry} class.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class GeometryTest {
	/**
	 * Tests the getAverageCenter method.
	 * <p>
	 * This test also shows that the average method can produce an incorrect
	 * center of mass when vertices are more dense at any place along the perimeter.
	 */
	@Test
	public void getAverageCenter() {
		Vector2[] vertices = new Vector2[6];
		vertices[0] = new Vector2(-2.0, 1.0);
		vertices[1] = new Vector2(-1.0, 2.0);
		vertices[2] = new Vector2(1.2, 0.5);
		vertices[3] = new Vector2(1.3, 0.3);
		vertices[4] = new Vector2(1.4, 0.2);
		vertices[5] = new Vector2(0.0, -1.0);
		
		Vector2 c = Geometry.getAverageCenter(vertices);
		
		TestCase.assertEquals(0.150, c.x, 1.0e-3);
		TestCase.assertEquals(0.500, c.y, 1.0e-3);
	}
	
	/**
	 * Tests the getAverageCenter method passing a null array.
	 * @since 2.0.0
	 */
	@Test(expected = NullPointerException.class)
	public void getAverageCenterNullArray() {
		Geometry.getAverageCenter((Vector2[]) null);
	}
	
	/**
	 * Tests the getAverageCenter method passing a null list.
	 * @since 2.0.0
	 */
	@Test(expected = NullPointerException.class)
	public void getAverageCenterNullList() {
		Geometry.getAverageCenter((List<Vector2>) null);
	}
	
	/**
	 * Tests the getAverageCenter method passing an empty list.
	 * @since 2.0.0
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getAverageCenterEmptyList() {
		Geometry.getAverageCenter(new ArrayList<Vector2>());
	}
	
	/**
	 * Tests the getAreaWeightedCenter method.
	 */
	@Test
	public void getAreaWeightedCenter() {
		Vector2[] vertices = new Vector2[6];
		vertices[0] = new Vector2(-2.0, 1.0);
		vertices[1] = new Vector2(-1.0, 2.0);
		// test dense area of points
		vertices[2] = new Vector2(1.2, 0.5);
		vertices[3] = new Vector2(1.3, 0.3);
		vertices[4] = new Vector2(1.4, 0.2);
		vertices[5] = new Vector2(0.0, -1.0);
		
		Vector2 c = Geometry.getAreaWeightedCenter(vertices);
		
		// note the x is closer to the "real" center of the object
		TestCase.assertEquals(-0.318, c.x, 1.0e-3);
		TestCase.assertEquals( 0.527, c.y, 1.0e-3);
	}

	/**
	 * Tests the getAreaWeightedCenter method passing a null array.
	 * @since 2.0.0
	 */
	@Test(expected = NullPointerException.class)
	public void getAreaWeightedCenterNullArray() {
		Geometry.getAreaWeightedCenter((Vector2[]) null);
	}
	
	/**
	 * Tests the getAreaWeightedCenter method passing a null list.
	 * @since 2.0.0
	 */
	@Test(expected = NullPointerException.class)
	public void getAreaWeightedCenterNullList() {
		Geometry.getAreaWeightedCenter((List<Vector2>) null);
	}
	
	/**
	 * Tests the getAreaWeightedCenter method passing an empty list.
	 * @since 2.0.0
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getAreaWeightedCenterEmptyList() {
		Geometry.getAreaWeightedCenter(new ArrayList<Vector2>());
	}
	
	/**
	 * Tests the getAreaWeightedCenter method passing a list of
	 * points who are all the same yielding zero area.
	 * @since 2.0.0
	 */
	@Test
	public void getAreaWeightedCenterZeroAreaList() {
		List<Vector2> points = new ArrayList<Vector2>();
		points.add(new Vector2(2.0, 1.0));
		points.add(new Vector2(2.0, 1.0));
		points.add(new Vector2(2.0, 1.0));
		points.add(new Vector2(2.0, 1.0));
		
		Vector2 c = Geometry.getAreaWeightedCenter(points);
		
		TestCase.assertEquals(2.000, c.x, 1.0e-3);
		TestCase.assertEquals(1.000, c.y, 1.0e-3);
	}
	
	/**
	 * Tests the getAreaWeightedCenter method passing a list of
	 * points who are all the same yielding zero area.
	 * @since 2.0.0
	 */
	@Test
	public void getAreaWeightedCenterZeroAreaArray() {
		Vector2[] points = new Vector2[4];
		points[0] = new Vector2(2.0, 1.0);
		points[1] = new Vector2(2.0, 1.0);
		points[2] = new Vector2(2.0, 1.0);
		points[3] = new Vector2(2.0, 1.0);
		
		Vector2 c = Geometry.getAreaWeightedCenter(points);
		
		TestCase.assertEquals(2.000, c.x, 1.0e-3);
		TestCase.assertEquals(1.000, c.y, 1.0e-3);
	}
	
	/**
	 * Test case for the unitCirclePolygon methods.
	 */
	@Test
	public void unitCirclePolygon() {
		Polygon p = Geometry.createUnitCirclePolygon(5, 0.5);
		// no exception indicates the generated polygon is valid
		// test that the correct vertices are created
		TestCase.assertEquals( 0.154, p.vertices[4].x, 1.0e-3);
		TestCase.assertEquals(-0.475, p.vertices[4].y, 1.0e-3);
		TestCase.assertEquals(-0.404, p.vertices[3].x, 1.0e-3);
		TestCase.assertEquals(-0.293, p.vertices[3].y, 1.0e-3);
		TestCase.assertEquals(-0.404, p.vertices[2].x, 1.0e-3);
		TestCase.assertEquals( 0.293, p.vertices[2].y, 1.0e-3);
		TestCase.assertEquals( 0.154, p.vertices[1].x, 1.0e-3);
		TestCase.assertEquals( 0.475, p.vertices[1].y, 1.0e-3);
		TestCase.assertEquals( 0.500, p.vertices[0].x, 1.0e-3);
		TestCase.assertEquals( 0.000, p.vertices[0].y, 1.0e-3);
		
		Vector2 v11 = p.vertices[0];
		
		p = Geometry.createUnitCirclePolygon(5, 0.5, Math.PI / 2.0);
		// no exception indicates the generated polygon is valid
		// test that the correct vertices are created
		TestCase.assertEquals( 0.475, p.vertices[4].x, 1.0e-3);
		TestCase.assertEquals( 0.154, p.vertices[4].y, 1.0e-3);
		TestCase.assertEquals( 0.293, p.vertices[3].x, 1.0e-3);
		TestCase.assertEquals(-0.404, p.vertices[3].y, 1.0e-3);
		TestCase.assertEquals(-0.293, p.vertices[2].x, 1.0e-3);
		TestCase.assertEquals(-0.404, p.vertices[2].y, 1.0e-3);
		TestCase.assertEquals(-0.475, p.vertices[1].x, 1.0e-3);
		TestCase.assertEquals( 0.154, p.vertices[1].y, 1.0e-3);
		TestCase.assertEquals( 0.000, p.vertices[0].x, 1.0e-3);
		TestCase.assertEquals( 0.500, p.vertices[0].y, 1.0e-3);
		
		Vector2 v21 = p.vertices[0];
		
		// the angle between any two vertices of the two polygons should be PI / 2
		double angle = v11.getAngleBetween(v21);
		TestCase.assertEquals(Math.PI / 2.0, angle, 1.0e-3);
	}
	
	/**
	 * Tests the successful creation of a circle.
	 */
	@Test
	public void createCircle() {
		Geometry.createCircle(1.0);
	}
	
	/**
	 * Tests the creation of a polygon with a null array.
	 */
	@Test(expected = NullPointerException.class)
	public void createPolygonNullArray() {
		Vector2[] vertices = null;
		// should fail since the vertices list contains null items
		Geometry.createPolygon(vertices);
	}
	
	/**
	 * Tests the creation of a polygon with a null point.
	 */
	@Test(expected = NullPointerException.class)
	public void createPolygonNullPoint() {
		Vector2[] vertices = new Vector2[5];
		// should fail since the vertices list contains null items
		Geometry.createPolygon(vertices);
	}
	
	/**
	 * Tests the successful creation of a polygon using vertices.
	 */
	@Test
	public void createPolygon() {
		Vector2[] vertices = new Vector2[5];
		vertices[0] = new Vector2(1.0, 0.0);
		vertices[1] = new Vector2(0.5, 1.0);
		vertices[2] = new Vector2(-0.5, 1.0);
		vertices[3] = new Vector2(-1.0, 0.0);
		vertices[4] = new Vector2(0.0, -1.0);
		// should fail since the vertices list contains null items
		Polygon p = Geometry.createPolygon(vertices);
		
		// the array should not be the same object
		TestCase.assertFalse(p.vertices == vertices);
		// the points should also be copies
		for (int i = 0; i < 5; i++) {
			TestCase.assertFalse(p.vertices[0] == vertices[0]);
		}
	}
	
	/**
	 * Tests the successful creation of a polygon using vertices.
	 */
	@Test
	public void createPolygonAtOrigin() {
		Vector2[] vertices = new Vector2[5];
		vertices[0] = new Vector2(1.0, 0.0);
		vertices[1] = new Vector2(0.5, 1.0);
		vertices[2] = new Vector2(-0.5, 1.0);
		vertices[3] = new Vector2(-1.0, 0.0);
		vertices[4] = new Vector2(0.0, -1.0);
		// should fail since the vertices list contains null items
		Polygon p = Geometry.createPolygonAtOrigin(vertices);
		
		// the array should not be the same object
		TestCase.assertFalse(p.vertices == vertices);
		// the points should also be copies
		for (int i = 0; i < 5; i++) {
			TestCase.assertFalse(p.vertices[0] == vertices[0]);
		}
		
		// make sure the center is at the origin
		Vector2 c = p.getCenter();
		TestCase.assertEquals(0.000, c.x, 1.0e-3);
		TestCase.assertEquals(0.000, c.y, 1.0e-3);
	}
	
	/**
	 * Tests the creation of a square with a zero size.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createSquareZero() {
		Geometry.createSquare(0.0);
	}
	
	/**
	 * Tests the creation of a square with a negative size.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createSquareNegative() {
		Geometry.createSquare(-1.0);
	}
	
	/**
	 * Tests the successful creation of a square.
	 */
	@Test
	public void createSquare() {
		Rectangle r = Geometry.createSquare(1.0);
		TestCase.assertEquals(1.000, r.getWidth(), 1.0e-3);
		TestCase.assertEquals(1.000, r.getHeight(), 1.0e-3);
	}
	
	/**
	 * Tests the successful creation of a rectangle.
	 */
	@Test
	public void createRectangle() {
		Geometry.createRectangle(1.0, 2.0);
	}
	
	/**
	 * Tests the creation of a triangle using a null point.
	 */
	@Test(expected = NullPointerException.class)
	public void createTriangleNullPoint() {
		Vector2 p1 = new Vector2(1.0, 0.0);
		Vector2 p2 = new Vector2(0.5, 1.0);
		// should fail since the vertices list contains null items
		Geometry.createTriangle(p1, p2, null);
	}
	
	/**
	 * Tests the successful creation of a triangle using points.
	 */
	@Test
	public void createTriangle() {
		Vector2 p1 = new Vector2(1.0, 0.0);
		Vector2 p2 = new Vector2(0.5, 1.0);
		Vector2 p3 = new Vector2(-0.5, 1.0);
		Triangle t = Geometry.createTriangle(p1, p2, p3);
		
		// the points should not be the same instances		
		TestCase.assertFalse(t.vertices[0] == p1);
		TestCase.assertFalse(t.vertices[1] == p2);
		TestCase.assertFalse(t.vertices[2] == p3);
	}
	
	/**
	 * Tests the successful creation of a triangle using points.
	 */
	@Test
	public void createTriangleAtOrigin() {
		Vector2 p1 = new Vector2(1.0, 0.0);
		Vector2 p2 = new Vector2(0.5, 1.0);
		Vector2 p3 = new Vector2(-0.5, 1.0);
		Triangle t = Geometry.createTriangleAtOrigin(p1, p2, p3);
		
		// the points should not be the same instances
		TestCase.assertFalse(t.vertices[0] == p1);
		TestCase.assertFalse(t.vertices[1] == p2);
		TestCase.assertFalse(t.vertices[2] == p3);
		
		// make sure the center is at the origin
		Vector2 c = t.getCenter();
		TestCase.assertEquals(0.000, c.x, 1.0e-3);
		TestCase.assertEquals(0.000, c.y, 1.0e-3);
	}
	
	/**
	 * Tests the create right triangle method with a zero width.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroWidthRightTriangle() {
		Geometry.createRightTriangle(0.0, 2.0);
	}
	
	/**
	 * Tests the create right triangle method with a zero height.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroHeightRightTriangle() {
		Geometry.createRightTriangle(1.0, 0.0);
	}
	
	/**
	 * Tests the create right triangle method with a negative width.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeWidthRightTriangle() {
		Geometry.createRightTriangle(-1.0, 2.0);
	}
	
	/**
	 * Tests the create right triangle method with a negative height.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeHeightRightTriangle() {
		Geometry.createRightTriangle(2.0, -2.0);
	}
	
	/**
	 * Tests the successful creation of a right angle triangle.
	 */
	@Test
	public void createRightTriangle() {
		Triangle t = Geometry.createRightTriangle(1.0, 2.0);
		
		// test that the center is the origin
		Vector2 center = t.getCenter();
		TestCase.assertEquals(0.000, center.x, 1.0e-3);
		TestCase.assertEquals(0.000, center.y, 1.0e-3);
		
		// get the vertices
		Vector2 v1 = t.vertices[0];
		Vector2 v2 = t.vertices[1];
		Vector2 v3 = t.vertices[2];
		
		// create the edges
		Vector2 e1 = v1.to(v2);
		Vector2 e2 = v2.to(v3);
		Vector2 e3 = v3.to(v1);
		
		// one of the follow dot products must be zero
		// indicating a 90 degree angle
		if (e1.dot(e2) < 0.00001 && e1.dot(e2) > -0.00001) {
			TestCase.assertTrue(true);
			return;
		}
		
		if (e2.dot(e3) < 0.00001 && e2.dot(e3) > -0.00001) {
			TestCase.assertTrue(true);
			return;
		}
		
		if (e3.dot(e1) < 0.00001 && e3.dot(e1) > -0.00001) {
			TestCase.assertTrue(true);
			return;
		}
		
		// if we get here we didn't find a 90 degree angle
		TestCase.assertFalse(true);
	}
	
	/**
	 * Tests the create equilateral triangle method with a zero height.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroHeightEquilateralTriangle() {
		Geometry.createEquilateralTriangle(0.0);
	}
	
	/**
	 * Tests the create equilateral triangle method with a negative height.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeHeightEquilateralTriangle() {
		Geometry.createEquilateralTriangle(-1.0);
	}
	
	/**
	 * Tests the successful creation of an equilateral angle triangle.
	 */
	@Test
	public void createEquilateralTriangle() {
		Triangle t = Geometry.createEquilateralTriangle(2.0);
		
		// test that the center is the origin
		Vector2 center = t.getCenter();
		TestCase.assertEquals(0.000, center.x, 1.0e-3);
		TestCase.assertEquals(0.000, center.y, 1.0e-3);
		
		// compute the first angle
		double previousA = t.vertices[0].getAngleBetween(t.vertices[1]);
		// put the angle between 0 and 180
		previousA = Math.abs(Math.PI - Math.abs(previousA));
		// compute the first distance
		double previousD = t.vertices[0].distance(t.vertices[1]);
		// make sure all the angles are the same
		for (int i = 1; i < 3; i++) {
			Vector2 v1 = t.vertices[i];
			Vector2 v2 = t.vertices[i + 1 == 3 ? 0 : i + 1];
			// test the angle between the vectors
			double angle = v1.getAngleBetween(v2);
			// put the angle between 0 and 180
			angle = Math.abs(Math.PI - Math.abs(angle)); 
			if (angle < previousA * 0.9999 || angle > previousA * 1.0001) {
				// its not the same as the last so we fail
				TestCase.assertFalse(true);
			}
			// test the distance between the points
			double distance = v1.distance(v2);
			if (distance < previousD * 0.9999 || distance > previousD * 1.0001) {
				// its not the same as the last so we fail
				TestCase.assertFalse(true);
			}
		}
		// if we get here we didn't find a 90 degree angle
		TestCase.assertTrue(true);
	}

	/**
	 * Tests the create right triangle method with a zero width.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroWidthIsoscelesTriangle() {
		Geometry.createIsoscelesTriangle(0.0, 1.0);
	}
	
	/**
	 * Tests the create right triangle method with a zero height.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createZeroHeightIsoscelesTriangle() {
		Geometry.createIsoscelesTriangle(1.0, 0.0);
	}
	
	/**
	 * Tests the create right triangle method with a negative width.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeWidthIsoscelesTriangle() {
		Geometry.createIsoscelesTriangle(-1.0, 2.0);
	}
	
	/**
	 * Tests the create right triangle method with a negative height.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeHeightIsoscelesTriangle() {
		Geometry.createIsoscelesTriangle(2.0, -2.0);
	}
	
	/**
	 * Tests the successful creation of an isosceles triangle.
	 */
	@Test
	public void createIsoscelesTriangle() {
		Triangle t = Geometry.createIsoscelesTriangle(2.0, 1.0);
		
		// test that the center is the origin
		Vector2 center = t.getCenter();
		TestCase.assertEquals(0.000, center.x, 1.0e-3);
		TestCase.assertEquals(0.000, center.y, 1.0e-3);
		
		// get the vertices
		Vector2 v1 = t.vertices[0];
		Vector2 v2 = t.vertices[1];
		Vector2 v3 = t.vertices[2];
		
		// create the edges
		Vector2 e1 = v1.to(v2);
		Vector2 e2 = v2.to(v3);
		Vector2 e3 = v3.to(v1);
		
		// the length of e1 and e3 should be identical
		TestCase.assertEquals(e1.getMagnitude(), e3.getMagnitude(), 1.0e-3);
		
		// then angles between e1 and e2 and e2 and e3 should be identical
		TestCase.assertEquals(e1.getAngleBetween(e2), e2.getAngleBetween(e3), 1.0e-3);
	}
	
	/**
	 * Tests the creation of a segment passing a null point.
	 */
	@Test(expected = NullPointerException.class)
	public void createSegmentNullPoint() {
		Geometry.createSegment(null, new Vector2());
	}
	
	/**
	 * Tests the successful creation of a segment given two points.
	 */
	@Test
	public void createSegment() {
		Geometry.createSegment(new Vector2(1.0, 1.0), new Vector2(2.0, -1.0));
	}
	
	/**
	 * Tests the successful creation of a segment given two points at the origin.
	 */
	@Test
	public void createSegmentAtOrigin() {
		Segment s = Geometry.createSegmentAtOrigin(new Vector2(1.0, 1.0), new Vector2(2.0, -1.0));
		
		// test that the center is the origin
		Vector2 center = s.getCenter();
		TestCase.assertEquals(0.000, center.x, 1.0e-3);
		TestCase.assertEquals(0.000, center.y, 1.0e-3);
	}

	/**
	 * Tests the successful creation of a segment given an end point.
	 */
	@Test
	public void createSegmentEnd() {
		Geometry.createSegment(new Vector2(1.0, 1.0));
	}
	
	/**
	 * Tests the creation of a segment passing a zero length.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createHorizontalSegmentZeroLength() {
		Geometry.createHorizontalSegment(0.0);
	}

	/**
	 * Tests the creation of a segment passing a negative length.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createHorizontalSegmentNegativeLength() {
		Geometry.createHorizontalSegment(-1.0);
	}
	
	/**
	 * Tests the successful creation of a segment given a length.
	 */
	@Test
	public void createHorizontalSegmentLength() {
		Segment s = Geometry.createHorizontalSegment(5.0);
		
		// test that the center is the origin
		Vector2 center = s.getCenter();
		TestCase.assertEquals(0.000, center.x, 1.0e-3);
		TestCase.assertEquals(0.000, center.y, 1.0e-3);
	}
	
	/**
	 * Tests the creation of a segment passing a zero length.
	 * @since 2.2.3
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createVerticalSegmentZeroLength() {
		Geometry.createVerticalSegment(0.0);
	}

	/**
	 * Tests the creation of a segment passing a negative length.
	 * @since 2.2.3
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createVerticalSegmentNegativeLength() {
		Geometry.createVerticalSegment(-1.0);
	}
	
	/**
	 * Tests the successful creation of a segment given a length.
	 * @since 2.2.3
	 */
	@Test
	public void createVerticalSegmentLength() {
		Segment s = Geometry.createVerticalSegment(5.0);
		
		// test that the center is the origin
		Vector2 center = s.getCenter();
		TestCase.assertEquals(0.000, center.x, 1.0e-3);
		TestCase.assertEquals(0.000, center.y, 1.0e-3);
	}
	
	/**
	 * Tests the getWinding method passing a list.
	 */
	@Test
	public void getWindingList() {
		List<Vector2> points = new ArrayList<Vector2>();
		points.add(new Vector2(-1.0, -1.0));
		points.add(new Vector2(1.0, -1.0));
		points.add(new Vector2(1.0, 1.0));
		points.add(new Vector2(-1.0, 1.0));
		TestCase.assertTrue(Geometry.getWinding(points) > 0);
		
		Collections.reverse(points);
		TestCase.assertTrue(Geometry.getWinding(points) < 0);
	}
	
	/**
	 * Tests the getWinding method passing a null list.
	 */
	@Test(expected = NullPointerException.class)
	public void getWindingNullList() {
		Geometry.getWinding((List<Vector2>)null);
	}
	
	/**
	 * Tests the getWinding method passing a list with 1 point.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getWindingListLessThan2Points() {
		List<Vector2> points = new ArrayList<Vector2>();
		points.add(new Vector2());
		Geometry.getWinding(points);
	}
	
	/**
	 * Tests the getWinding method passing a list that contains a null point.
	 */
	@Test(expected = NullPointerException.class)
	public void getWindingListNullPoint() {
		List<Vector2> points = new ArrayList<Vector2>();
		points.add(new Vector2());
		points.add(null);
		points.add(null);
		Geometry.getWinding(points);
	}
	
	/**
	 * Tests the getWinding method passing a valid array.
	 */
	@Test
	public void getWindingArray() {
		Vector2[] points = new Vector2[4];
		points[0] = new Vector2(-1.0, -1.0);
		points[1] = new Vector2(1.0, -1.0);
		points[2] = new Vector2(1.0, 1.0);
		points[3] = new Vector2(-1.0, 1.0);
		TestCase.assertTrue(Geometry.getWinding(points) > 0);
		
		// reverse the array
		Vector2 p = points[0];
		points[0] = points[3];
		points[3] = p;
		p = points[1];
		points[1] = points[2];
		points[2] = p;
		
		TestCase.assertTrue(Geometry.getWinding(points) < 0);
	}
	
	/**
	 * Tests the getWinding method passing a null array.
	 */
	@Test(expected = NullPointerException.class)
	public void getWindingNullArray() {
		Geometry.getWinding((Vector2[])null);
	}
	
	/**
	 * Tests the getWinding method passing an array with less than two points.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getWindingArrayLessThan2Points() {
		Vector2[] points = new Vector2[1];
		points[0] = new Vector2(-1.0, -1.0);
		Geometry.getWinding(points);
	}
	
	/**
	 * Tests the getWinding method passing an array containing null points.
	 */
	@Test(expected = NullPointerException.class)
	public void getWindingArrayNullPoint() {
		Vector2[] points = new Vector2[4];
		points[0] = new Vector2(-1.0, -1.0);
		points[1] = null;
		points[2] = null;
		points[3] = null;
		Geometry.getWinding(points);
	}
	
	/**
	 * Tests the reverse winding method passing a null list.
	 */
	@Test(expected = NullPointerException.class)
	public void reverseWindingNullList() {
		Geometry.reverseWinding((List<Vector2>) null);
	}
	
	/**
	 * Tests the reverse winding method passing a null array.
	 */
	@Test(expected = NullPointerException.class)
	public void reverseWindingNullArray() {
		Geometry.reverseWinding((Vector2[]) null);
	}
	
	/**
	 * Tests the cleanse method passing a null list.
	 * @since 2.2.3
	 */
	@Test(expected = NullPointerException.class)
	public void cleanseNullList() {
		Geometry.cleanse((List<Vector2>)null);
	}
	
	/**
	 * Tests the cleanse method passing a null array.
	 * @since 2.2.3
	 */
	@Test(expected = NullPointerException.class)
	public void cleanseNullArray() {
		Geometry.cleanse((List<Vector2>)null);
	}
	
	/**
	 * Tests the cleanse method passing a null list.
	 * @since 2.2.3
	 */
	@Test(expected = NullPointerException.class)
	public void cleanseListWithNullElements() {
		List<Vector2> list = new ArrayList<Vector2>();
		list.add(new Vector2());
		list.add(null);
		list.add(new Vector2());
		list.add(new Vector2());
		Geometry.cleanse(list);
	}
	
	/**
	 * Tests the cleanse method passing a null array.
	 * @since 2.2.3
	 */
	@Test(expected = NullPointerException.class)
	public void cleanseArrayWithNullElements() {
		Vector2[] array = new Vector2[5];
		array[0] = new Vector2();
		array[3] = new Vector2();
		array[4] = new Vector2();
		Geometry.cleanse(array);
	}
	
	/**
	 * Tests the cleanse list method.
	 */
	@Test
	public void cleanseList() {
		List<Vector2> points = new ArrayList<Vector2>();
		points.add(new Vector2(1.0, 0.0));
		points.add(new Vector2(1.0, 0.0));
		points.add(new Vector2(0.5, -0.5));
		points.add(new Vector2(0.0, -0.5));
		points.add(new Vector2(-0.5, -0.5));
		points.add(new Vector2(-2.0, -0.5));
		points.add(new Vector2(2.1, 0.5));
		points.add(new Vector2(1.0, 0.0));
		
		List<Vector2> result = Geometry.cleanse(points);
		
		TestCase.assertTrue(Geometry.getWinding(result) > 0.0);
		TestCase.assertEquals(4, result.size());
	}
	
	/**
	 * Tests the cleanse array method.
	 */
	@Test
	public void cleanseArray() {
		Vector2[] points = new Vector2[8];
		points[0] = new Vector2(1.0, 0.0);
		points[1] = new Vector2(1.0, 0.0);
		points[2] = new Vector2(0.5, -0.5);
		points[3] = new Vector2(0.0, -0.5);
		points[4] = new Vector2(-0.5, -0.5);
		points[5] = new Vector2(-2.0, -0.5);
		points[6] = new Vector2(2.1, 0.5);
		points[7] = new Vector2(1.0, 0.0);
		
		Vector2[] result = Geometry.cleanse(points);
		
		TestCase.assertTrue(Geometry.getWinding(result) > 0.0);
		TestCase.assertEquals(4, result.length);
	}
}
