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
package org.dyn4j.collision.manifold;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link Manifold} class.
 * @author William Bittle
 * @version 6.0.0
 * @since 4.0.0
 */
public class ManifoldTest {
	/**
	 * Tests the constructors.
	 */
	@Test
	public void create() {
		Manifold m = new Manifold();
		
		TestCase.assertNotNull(m.normal);
		TestCase.assertNotNull(m.points);
		TestCase.assertNotNull(m.getNormal());
		TestCase.assertNotNull(m.getPoints());
		TestCase.assertSame(m.normal, m.getNormal());
		TestCase.assertSame(m.points, m.getPoints());
		
		TestCase.assertEquals(0.0, m.normal.x);
		TestCase.assertEquals(0.0, m.normal.y);
		TestCase.assertEquals(0.0, m.getNormal().x);
		TestCase.assertEquals(0.0, m.getNormal().y);
		
		TestCase.assertEquals(0, m.points.size());
		
		// test internal constructor (copy by reference)
		Manifold m2 = new Manifold(m.normal, m.points);
		TestCase.assertEquals(m.normal, m2.normal);
		TestCase.assertEquals(m.points, m2.points);
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		Manifold m = new Manifold();
		m.getNormal().x = 1;
		m.getNormal().y = 1;
		m.getPoints().add(new ManifoldPoint(ManifoldPointId.DISTANCE, new Vector2(1, 1),  1));
		
		m.clear();
		
		TestCase.assertNotNull(m.normal);
		TestCase.assertNotNull(m.points);
		
		TestCase.assertEquals(0.0, m.normal.x);
		TestCase.assertEquals(0.0, m.normal.y);
		
		TestCase.assertEquals(0, m.points.size());
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		Manifold m = new Manifold();
		m.getNormal().x = 1.0;
		m.getNormal().y = 1.0;
		m.getPoints().add(new ManifoldPoint(ManifoldPointId.DISTANCE, new Vector2(1.0, 1.0),  1.0));
		
		Manifold m2 = m.copy();
		
		TestCase.assertNotNull(m2.normal);
		TestCase.assertNotNull(m2.points);
		TestCase.assertEquals(1.0, m2.normal.x);
		TestCase.assertEquals(1.0, m2.normal.y);
		TestCase.assertEquals(1, m2.points.size());
		TestCase.assertSame(m2.points.get(0).id, m.points.get(0).id);
		TestCase.assertEquals(1.0, m2.points.get(0).point.x);
		TestCase.assertEquals(1.0, m2.points.get(0).point.y);
		TestCase.assertEquals(1.0, m2.points.get(0).depth);
		
		// test copy by value
		TestCase.assertNotSame(m.normal, m2.normal);
		TestCase.assertNotSame(m.points, m2.points);
		TestCase.assertNotSame(m.points.get(0), m2.points.get(0));
		TestCase.assertNotSame(m.points.get(0).point, m2.points.get(0).point);
		
		Manifold m3 = new Manifold();
		m3.set(m);
		
		TestCase.assertNotNull(m3.normal);
		TestCase.assertNotNull(m3.points);
		TestCase.assertEquals(1.0, m3.normal.x);
		TestCase.assertEquals(1.0, m3.normal.y);
		TestCase.assertEquals(1, m3.points.size());
		TestCase.assertSame(m3.points.get(0).id, m.points.get(0).id);
		TestCase.assertEquals(1.0, m3.points.get(0).point.x);
		TestCase.assertEquals(1.0, m3.points.get(0).point.y);
		TestCase.assertEquals(1.0, m3.points.get(0).depth);
		
		// test copy by value
		TestCase.assertNotSame(m.normal, m3.normal);
		TestCase.assertNotSame(m.points, m3.points);
		TestCase.assertNotSame(m.points.get(0), m3.points.get(0));
		TestCase.assertNotSame(m.points.get(0).point, m3.points.get(0).point);
	}
	
	/**
	 * Tests the get/set normal methods.
	 */
	@Test
	public void getSetNormal() {
		Manifold m = new Manifold();
		
		TestCase.assertNotNull(m.getNormal());
		TestCase.assertEquals(0.0, m.getNormal().x);
		TestCase.assertEquals(0.0, m.getNormal().y);
		
		Vector2 v = new Vector2(1.0, 2.0);
		m.setNormal(v);
		
		TestCase.assertNotNull(m.getNormal());
		TestCase.assertNotSame(v, m.getNormal());
		TestCase.assertEquals(1.0, m.getNormal().x);
		TestCase.assertEquals(2.0, m.getNormal().y);
	}
	
	/**
	 * Tests the get/set points methods.
	 */
	@Test
	public void getSetPoints() {
		Manifold m = new Manifold();
		
		TestCase.assertNotNull(m.getPoints());
		TestCase.assertEquals(0, m.points.size());
		
		List<ManifoldPoint> pts = new ArrayList<ManifoldPoint>();
		pts.add(new ManifoldPoint(ManifoldPointId.DISTANCE, new Vector2(2.0, 3.0), 5.0));
		m.setPoints(pts);
		
		TestCase.assertNotNull(m.getPoints());
		TestCase.assertNotSame(pts, m.getPoints());
		TestCase.assertEquals(1, m.getPoints().size());
		TestCase.assertEquals(2.0, m.getPoints().get(0).point.x);
		TestCase.assertEquals(3.0, m.getPoints().get(0).point.y);
		TestCase.assertEquals(5.0, m.getPoints().get(0).depth);
		
		// test copy by value
		TestCase.assertNotSame(pts.get(0), m.getPoints().get(0));
		TestCase.assertNotSame(pts.get(0).point, m.getPoints().get(0).point);
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void tostring() {
		Manifold m = new Manifold();
		
		TestCase.assertNotNull(m.toString());
		
		// test with some points
		m.getNormal().x = 1.0;
		m.getNormal().y = 1.0;
		m.getPoints().add(new ManifoldPoint(ManifoldPointId.DISTANCE, new Vector2(1.0, 1.0),  1.0));
		m.getPoints().add(new ManifoldPoint(ManifoldPointId.DISTANCE, new Vector2(2.0, 3.0),  0.5));
		
		TestCase.assertNotNull(m.toString());
	}
	
	/**
	 * Tests the shift method.
	 */
	@Test
	public void shift() {
		Manifold m = new Manifold();
		m.getNormal().x = 1.0;
		m.getNormal().y = 1.0;
		m.getPoints().add(new ManifoldPoint(ManifoldPointId.DISTANCE, new Vector2(1.0, 1.0),  1.0));
		
		TestCase.assertNotNull(m.normal);
		TestCase.assertNotNull(m.points);
		TestCase.assertEquals(1.0, m.normal.x);
		TestCase.assertEquals(1.0, m.normal.y);
		TestCase.assertEquals(1, m.points.size());
		TestCase.assertEquals(1.0, m.points.get(0).point.x);
		TestCase.assertEquals(1.0, m.points.get(0).point.y);
		TestCase.assertEquals(1.0, m.points.get(0).depth);
		
		// only the points should change
		
		m.shift(new Vector2(2.0, -1.0));
		
		TestCase.assertNotNull(m.normal);
		TestCase.assertNotNull(m.points);
		TestCase.assertEquals(1.0, m.normal.x);
		TestCase.assertEquals(1.0, m.normal.y);
		TestCase.assertEquals(1, m.points.size());
		TestCase.assertEquals(3.0, m.points.get(0).point.x);
		TestCase.assertEquals(0.0, m.points.get(0).point.y);
		TestCase.assertEquals(1.0, m.points.get(0).depth);
	}
}
