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

import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link ManifoldPoint} class.
 * @author William Bittle
 * @version 6.0.0
 * @since 4.0.0
 */
public class ManifoldPointTest {
	/**
	 * Tests the constructors.
	 */
	@Test
	public void create() {
		ManifoldPoint mp = new ManifoldPoint(ManifoldPointId.DISTANCE);
		
		TestCase.assertNotNull(mp.point);
		TestCase.assertEquals(0.0, mp.depth);
		TestCase.assertEquals(ManifoldPointId.DISTANCE, mp.id);
		
		Vector2 pt = new Vector2(1.0, 2.0);
		ManifoldPoint mp2 = new ManifoldPoint(ManifoldPointId.DISTANCE, pt, 1.0);
		
		TestCase.assertEquals(ManifoldPointId.DISTANCE, mp2.id);
		TestCase.assertNotNull(mp2.point);
		TestCase.assertNotSame(pt, mp2.point);
		TestCase.assertEquals(pt.x, mp2.point.x);
		TestCase.assertEquals(pt.y, mp2.point.y);
		TestCase.assertEquals(1.0, mp2.depth);
		
		// create with null id
		ManifoldPoint mp3 = new ManifoldPoint(null);
		
		TestCase.assertNotNull(mp3.point);
		TestCase.assertEquals(0.0, mp3.depth);
		TestCase.assertEquals(ManifoldPointId.DISTANCE, mp3.id);
	}
	
	/**
	 * Tests the copy method.
	 */
	@Test
	public void copy() {
		Vector2 pt = new Vector2(1.0, 2.0);
		ManifoldPoint mp = new ManifoldPoint(ManifoldPointId.DISTANCE, pt, 1.0);
		
		ManifoldPoint mp2 = mp.copy();
		
		TestCase.assertEquals(ManifoldPointId.DISTANCE, mp2.id);
		TestCase.assertNotNull(mp2.point);
		TestCase.assertNotSame(mp.point, mp2.point);
		TestCase.assertSame(mp.id, mp2.id);
		TestCase.assertEquals(pt.x, mp2.point.x);
		TestCase.assertEquals(pt.y, mp2.point.y);
		TestCase.assertEquals(1.0, mp2.depth);
		
		ManifoldPoint mp3 = new ManifoldPoint(ManifoldPointId.DISTANCE);
		mp3.copy(mp);
		
		TestCase.assertEquals(ManifoldPointId.DISTANCE, mp3.id);
		TestCase.assertNotNull(mp3.point);
		TestCase.assertSame(mp.id, mp3.id);
		TestCase.assertNotSame(mp.point, mp3.point);
		TestCase.assertEquals(pt.x, mp3.point.x);
		TestCase.assertEquals(pt.y, mp3.point.y);
		TestCase.assertEquals(1.0, mp3.depth);
		
		IndexedManifoldPointId mpid = new IndexedManifoldPointId(1, 2, 3, true);
		ManifoldPoint mp4 = new ManifoldPoint(mpid, new Vector2(3, 4), 2);
		
		ManifoldPoint mp5 = mp4.copy();
		
		TestCase.assertNotSame(mp4, mp5);
		TestCase.assertNotSame(mp4.id, mp5.id);
		TestCase.assertEquals(mp4.depth, mp5.depth);
		TestCase.assertNotNull(mp5.point);
		TestCase.assertNotSame(mp4.point, mp5.point);
		TestCase.assertEquals(mp4.point.x, mp5.point.x);
		TestCase.assertEquals(mp4.point.y, mp5.point.y);
	}
	
	/**
	 * Tests the get/set point methods.
	 */
	@Test
	public void getSetPoint() {
		ManifoldPoint mp = new ManifoldPoint(ManifoldPointId.DISTANCE);
		
		TestCase.assertNotNull(mp.getPoint());
		TestCase.assertEquals(0.0, mp.getDepth());
		TestCase.assertEquals(0.0, mp.getPoint().x);
		TestCase.assertEquals(0.0, mp.getPoint().y);
		TestCase.assertEquals(ManifoldPointId.DISTANCE, mp.getId());
		
		Vector2 pt = new Vector2(-2.0, 1.0);
		mp.setPoint(pt);
		
		TestCase.assertNotNull(mp.getPoint());
		TestCase.assertNotSame(pt, mp.getPoint());
		TestCase.assertEquals(0.0, mp.getDepth());
		TestCase.assertEquals(-2.0, mp.getPoint().x);
		TestCase.assertEquals(1.0, mp.getPoint().y);
		TestCase.assertEquals(ManifoldPointId.DISTANCE, mp.getId());
	}
	
	/**
	 * Tests the get/set depth methods.
	 */
	@Test
	public void getSetDepth() {
		ManifoldPoint mp = new ManifoldPoint(ManifoldPointId.DISTANCE);
		
		TestCase.assertNotNull(mp.getPoint());
		TestCase.assertEquals(0.0, mp.getDepth());
		TestCase.assertEquals(0.0, mp.getPoint().x);
		TestCase.assertEquals(0.0, mp.getPoint().y);
		TestCase.assertEquals(ManifoldPointId.DISTANCE, mp.getId());
		
		mp.setDepth(5.0);
		
		TestCase.assertNotNull(mp.getPoint());
		TestCase.assertEquals(5.0, mp.getDepth());
		TestCase.assertEquals(0.0, mp.getPoint().x);
		TestCase.assertEquals(0.0, mp.getPoint().y);
		TestCase.assertEquals(ManifoldPointId.DISTANCE, mp.getId());
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void tostring() {
		ManifoldPoint mp = new ManifoldPoint(ManifoldPointId.DISTANCE);
		
		TestCase.assertNotNull(mp.toString());
	}
	
	/**
	 * Tests the shift method.
	 */
	@Test
	public void shift() {
		ManifoldPoint mp = new ManifoldPoint(ManifoldPointId.DISTANCE);
		mp.setDepth(4.0);
		mp.getPoint().x = 1.0;
		mp.getPoint().y = -1.0;
		
		TestCase.assertNotNull(mp.getPoint());
		TestCase.assertEquals(4.0, mp.getDepth());
		TestCase.assertEquals(1.0, mp.getPoint().x);
		TestCase.assertEquals(-1.0, mp.getPoint().y);
		TestCase.assertEquals(ManifoldPointId.DISTANCE, mp.getId());
		
		// only the point should change
		
		mp.shift(new Vector2(2.0, -1.0));
		
		TestCase.assertNotNull(mp.getPoint());
		TestCase.assertEquals(4.0, mp.getDepth());
		TestCase.assertEquals(3.0, mp.getPoint().x);
		TestCase.assertEquals(-2.0, mp.getPoint().y);
		TestCase.assertEquals(ManifoldPointId.DISTANCE, mp.getId());
	}
}
