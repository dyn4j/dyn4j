package org.dyn4j.collision;

import org.dyn4j.collision.narrowphase.Raycast;
import org.dyn4j.collision.narrowphase.SegmentDetector;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

public class SegmentDetectorTest {
	@Test
	public void raycastHorizontalSegmentNoIntersection() {
		Ray ray = new Ray(new Vector2(-0.85, 0.48), Math.PI * 0.25);
		Segment c = new Segment(new Vector2(-0.59, 0.68), new Vector2(-0.40, 0.68));
		Transform t = new Transform();
		Raycast raycast = new Raycast();
		
		// successful test
		boolean collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
		
		// should not intersect
		TestCase.assertFalse(collision);
		TestCase.assertNull(raycast.getNormal());
		TestCase.assertNull(raycast.getPoint());
		TestCase.assertEquals(0.0, raycast.getDistance());
	}
	
	@Test
	public void raycastHorizontalSegmentWithIntersection() {
		Ray ray = new Ray(new Vector2(-0.85, 0.48), Math.PI * 0.25);
		Segment c = new Segment(new Vector2(-0.68, 0.68), new Vector2(-0.53, 0.68));
		Transform t = new Transform();
		Raycast raycast = new Raycast();
		
		// successful test
		boolean collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
		
		// should intersect
		TestCase.assertTrue(collision);
		
		Vector2 point = raycast.getPoint();
		Vector2 normal = raycast.getNormal();
		
		TestCase.assertEquals(-0.649, point.x, 1.0e-3);
		TestCase.assertEquals(0.680, point.y, 1.0e-3);
		TestCase.assertEquals(0.000, normal.x, 1.0e-3);
		TestCase.assertEquals(-1.000, normal.y, 1.0e-3);
		TestCase.assertEquals(0.282, raycast.getDistance(), 1.0e-3);
	}
	
	@Test
	public void raycastVerticalSegmentNoIntersection() {
		Ray ray = new Ray(new Vector2(-0.85, 0.48), Math.PI * 0.25);
		Segment c = new Segment(new Vector2(-0.58, 0.68), new Vector2(-0.58, 0.41));
		Transform t = new Transform();
		Raycast raycast = new Raycast();
		
		// successful test
		boolean collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
		
		// should not intersect
		TestCase.assertFalse(collision);
		TestCase.assertNull(raycast.getNormal());
		TestCase.assertNull(raycast.getPoint());
		TestCase.assertEquals(0.0, raycast.getDistance());
	}
	
	@Test
	public void raycastVerticalSegmentWithIntersection() {
		Ray ray = new Ray(new Vector2(-0.85, 0.48), Math.PI * 0.25);
		Segment c = new Segment(new Vector2(-0.58, 1.2), new Vector2(-0.58, 0.41));
		Transform t = new Transform();
		Raycast raycast = new Raycast();
		
		// successful test
		boolean collision = SegmentDetector.raycast(ray, 0.0, c, t, raycast);
		
		// should intersect
		TestCase.assertTrue(collision);
		
		Vector2 point = raycast.getPoint();
		Vector2 normal = raycast.getNormal();
		
		TestCase.assertEquals(-0.58, point.x, 1.0e-3);
		TestCase.assertEquals(0.75, point.y, 1.0e-3);
		TestCase.assertEquals(-1.000, normal.x, 1.0e-3);
		TestCase.assertEquals(0.000, normal.y, 1.0e-3);
		TestCase.assertEquals(0.381, raycast.getDistance(), 1.0e-3);
	}
}
