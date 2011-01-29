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
package org.dyn4j.game2d.collision;

import junit.framework.TestCase;

import org.dyn4j.game2d.collision.narrowphase.Gjk;
import org.dyn4j.game2d.collision.narrowphase.Raycast;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Geometry;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Ray;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Triangle;
import org.dyn4j.game2d.geometry.Vector2;
import org.junit.Test;

/**
 * Test cases for the {@link Gjk#raycast(Ray, double, Convex, Transform, Raycast)}
 * method.
 * @author William Bittle
 * @version 2.2.3
 * @since 2.0.0
 */
public class GjkRaycastTest {
	/**
	 * Tests a successful raycast against a {@link Rectangle}.
	 */
	@Test
	public void raycastRectangle() {
		Ray ray = new Ray(new Vector2(), new Vector2(1.0, 0.0));
		Gjk gjk = new Gjk();
		Rectangle r = Geometry.createRectangle(1.0, 1.0);
		Transform t = new Transform();
		t.translate(2.0, 0.0);
		Raycast raycast = new Raycast();
		
		// successful test
		boolean collision = gjk.raycast(ray, 0.0, r, t, raycast);
		
		TestCase.assertTrue(collision);
		
		Vector2 point = raycast.getPoint();
		Vector2 normal = raycast.getNormal();
		
		TestCase.assertEquals(1.500, point.x, 1.0e-3);
		TestCase.assertEquals(0.000, point.y, 1.0e-3);
		TestCase.assertEquals(-1.000, normal.x, 1.0e-3);
		TestCase.assertEquals(0.000, normal.y, 1.0e-3);
		TestCase.assertEquals(1.500, raycast.getDistance(), 1.0e-3);
		raycast.clear();
		
		// length test
		TestCase.assertFalse(gjk.raycast(ray, 1.4, r, t, raycast));
		TestCase.assertTrue(gjk.raycast(ray, 1.6, r, t, raycast));
		
		// opposite direction test
		ray.getDirection().negate();
		TestCase.assertFalse(gjk.raycast(ray, 0.0, r, t, raycast));
		
		// non-intersection case
		ray.setDirection(new Vector2(1.0, 1.0));
		TestCase.assertFalse(gjk.raycast(ray, 0.0, r, t, raycast));
		
		// start at center case (or any point within the convex shape)
		ray.setStart(t.getTransformed(r.getCenter()));
		TestCase.assertFalse(gjk.raycast(ray, 0.0, r, t, raycast));
	}
	
	/**
	 * Tests a successful raycast against a {@link Polygon}.
	 */
	@Test
	public void raycastPolygon() {
		Vector2 d = new Vector2(1.0, 1.0);
		d.normalize();
		Ray ray = new Ray(d);
		Gjk gjk = new Gjk();
		Convex c = Geometry.createUnitCirclePolygon(5, 1.0);
		Transform t = new Transform();
		t.translate(2.0, 1.0);
		Raycast raycast = new Raycast();
		
		// successful test
		boolean collision = gjk.raycast(ray, 0.0, c, t, raycast);
		
		TestCase.assertTrue(collision);
		
		Vector2 point = raycast.getPoint();
		Vector2 normal = raycast.getNormal();
		
		TestCase.assertEquals(1.190, point.x, 1.0e-3);
		TestCase.assertEquals(1.190, point.y, 1.0e-3);
		TestCase.assertEquals(-1.000, normal.x, 1.0e-3);
		TestCase.assertEquals(0.000, normal.y, 1.0e-3);
		TestCase.assertEquals(1.684, raycast.getDistance(), 1.0e-3);
		raycast.clear();
		
		// length test
		TestCase.assertFalse(gjk.raycast(ray, 1.4, c, t, raycast));
		TestCase.assertTrue(gjk.raycast(ray, 1.7, c, t, raycast));
		
		// opposite direction test
		ray.getDirection().negate();
		TestCase.assertFalse(gjk.raycast(ray, 0.0, c, t, raycast));
		
		// non-intersection case
		ray.setDirection(new Vector2(1.0, 2.0));
		TestCase.assertFalse(gjk.raycast(ray, 0.0, c, t, raycast));
		
		// start at center case (or any point within the convex shape)
		ray.setStart(t.getTransformed(c.getCenter()));
		TestCase.assertFalse(gjk.raycast(ray, 0.0, c, t, raycast));
	}
	
	/**
	 * Tests a successful raycast against a {@link Triangle}.
	 */
	@Test
	public void raycastTriangle() {
		Vector2 d = new Vector2(2.0, 1.0);
		d.normalize();
		Ray ray = new Ray(d);
		Gjk gjk = new Gjk();
		Convex c = Geometry.createEquilateralTriangle(1.0);
		Transform t = new Transform();
		t.translate(2.0, 1.0);
		Raycast raycast = new Raycast();
		
		// successful test
		boolean collision = gjk.raycast(ray, 0.0, c, t, raycast);
		
		TestCase.assertTrue(collision);
		
		Vector2 point = raycast.getPoint();
		Vector2 normal = raycast.getNormal();
		
		TestCase.assertEquals(1.458, point.x, 1.0e-3);
		TestCase.assertEquals(0.729, point.y, 1.0e-3);
		TestCase.assertEquals(-0.866, normal.x, 1.0e-3);
		TestCase.assertEquals(0.5, normal.y, 1.0e-3);
		TestCase.assertEquals(1.631, raycast.getDistance(), 1.0e-3);
		raycast.clear();
		
		// length test
		TestCase.assertFalse(gjk.raycast(ray, 1.4, c, t, raycast));
		TestCase.assertTrue(gjk.raycast(ray, 1.7, c, t, raycast));
		
		// opposite direction test
		ray.getDirection().negate();
		TestCase.assertFalse(gjk.raycast(ray, 0.0, c, t, raycast));
		
		// non-intersection case
		ray.setDirection(new Vector2(1.0, 2.0));
		TestCase.assertFalse(gjk.raycast(ray, 0.0, c, t, raycast));
		
		// start at center case (or any point within the convex shape)
		ray.setStart(t.getTransformed(c.getCenter()));
		TestCase.assertFalse(gjk.raycast(ray, 0.0, c, t, raycast));
	}
	
	/**
	 * Tests a successful raycast against a {@link Circle}.
	 */
	@Test
	public void raycastCircle() {
		Vector2 d = new Vector2(2.0, 1.0);
		d.normalize();
		Ray ray = new Ray(d);
		Gjk gjk = new Gjk();
		Convex c = Geometry.createCircle(0.5);
		Transform t = new Transform();
		t.translate(2.0, 0.5);
		Raycast raycast = new Raycast();
		
		// successful test
		boolean collision = gjk.raycast(ray, 0.0, c, t, raycast);
		
		TestCase.assertTrue(collision);
		
		Vector2 point = raycast.getPoint();
		Vector2 normal = raycast.getNormal();
		
		TestCase.assertEquals(1.599, point.x, 1.0e-3);
		TestCase.assertEquals(0.799, point.y, 1.0e-3);
		TestCase.assertEquals(-0.800, normal.x, 1.0e-3);
		TestCase.assertEquals(0.599, normal.y, 1.0e-3);
		TestCase.assertEquals(1.788, raycast.getDistance(), 1.0e-3);
		raycast.clear();
		
		// length test
		TestCase.assertFalse(gjk.raycast(ray, 1.7, c, t, raycast));
		TestCase.assertTrue(gjk.raycast(ray, 1.8, c, t, raycast));
		
		// opposite direction test
		ray.getDirection().negate();
		TestCase.assertFalse(gjk.raycast(ray, 0.0, c, t, raycast));
		
		// non-intersection case
		ray.setDirection(new Vector2(1.0, 2.0));
		TestCase.assertFalse(gjk.raycast(ray, 0.0, c, t, raycast));
		
		// start at center case (or any point within the convex shape)
		ray.setStart(t.getTransformed(c.getCenter()));
		TestCase.assertFalse(gjk.raycast(ray, 0.0, c, t, raycast));
	}
	
	/**
	 * Tests a successful raycast against a {@link Segment}.
	 */
	@Test
	public void raycastSegment() {
		Vector2 d = new Vector2(2.0, 1.0);
		d.normalize();
		Ray ray = new Ray(d);
		Gjk gjk = new Gjk();
		Convex c = Geometry.createHorizontalSegment(1.0);
		Transform t = new Transform();
		t.translate(2.0, 1.0);
		Raycast raycast = new Raycast();
		
		// successful test
		boolean collision = gjk.raycast(ray, 0.0, c, t, raycast);
		
		TestCase.assertTrue(collision);
		
		Vector2 point = raycast.getPoint();
		Vector2 normal = raycast.getNormal();
		
		TestCase.assertEquals(2.000, point.x, 1.0e-3);
		TestCase.assertEquals(1.000, point.y, 1.0e-3);
		TestCase.assertEquals(0.000, normal.x, 1.0e-3);
		TestCase.assertEquals(-1.000, normal.y, 1.0e-3);
		TestCase.assertEquals(2.236, raycast.getDistance(), 1.0e-3);
		raycast.clear();
		
		// length test
		TestCase.assertFalse(gjk.raycast(ray, 2.2, c, t, raycast));
		TestCase.assertTrue(gjk.raycast(ray, 2.4, c, t, raycast));
		
		// opposite direction test
		ray.getDirection().negate();
		TestCase.assertFalse(gjk.raycast(ray, 0.0, c, t, raycast));
		
		// non-intersection case
		ray.setDirection(new Vector2(1.0, 2.0));
		TestCase.assertFalse(gjk.raycast(ray, 0.0, c, t, raycast));
		
		// start at center case (or any point within the convex shape)
		ray.setStart(t.getTransformed(c.getCenter()));
		TestCase.assertFalse(gjk.raycast(ray, 0.0, c, t, raycast));
	}
	
	/**
	 * Tests the raycast method using a parallel segment.
	 */
	@Test
	public void raycastParallelSegment() {
		Vector2 d = new Vector2(1.0, 0.0); d.normalize();
		Ray ray = new Ray(d);
		Gjk gjk = new Gjk();
		Convex c = Geometry.createHorizontalSegment(1.0);
		Transform t = new Transform();
		t.translate(2.0, 0.0);
		Raycast raycast = new Raycast();
		
		// successful test
		boolean collision = gjk.raycast(ray, 0.0, c, t, raycast);
		
		TestCase.assertTrue(collision);
		
		Vector2 point = raycast.getPoint();
		Vector2 normal = raycast.getNormal();
		
		TestCase.assertEquals(1.500, point.x, 1.0e-3);
		TestCase.assertEquals(0.000, point.y, 1.0e-3);
		TestCase.assertEquals(-1.000, normal.x, 1.0e-3);
		TestCase.assertEquals(0.000, normal.y, 1.0e-3);
		TestCase.assertEquals(1.500, raycast.getDistance(), 1.0e-3);
		raycast.clear();
		
		// length test
		TestCase.assertFalse(gjk.raycast(ray, 1.4, c, t, raycast));
		TestCase.assertTrue(gjk.raycast(ray, 2.0, c, t, raycast));
		
		// opposite direction test
		ray.getDirection().negate();
		TestCase.assertFalse(gjk.raycast(ray, 0.0, c, t, raycast));
		
		// non-intersection case
		ray.setStart(new Vector2(0.0, 1.0));
		TestCase.assertFalse(gjk.raycast(ray, 0.0, c, t, raycast));
		
		// start at center case (or any point within the convex shape)
		ray.setStart(t.getTransformed(c.getCenter()));
		TestCase.assertFalse(gjk.raycast(ray, 0.0, c, t, raycast));
	}
}
