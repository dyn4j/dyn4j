/*
 * Copyright (c) 2010-2015 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.geometry;

import junit.framework.TestCase;

import org.dyn4j.geometry.hull.DivideAndConquer;
import org.dyn4j.geometry.hull.GiftWrap;
import org.dyn4j.geometry.hull.GrahamScan;
import org.dyn4j.geometry.hull.HullGenerator;
import org.dyn4j.geometry.hull.MonotoneChain;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for the {@link HullGenerator} algorithms.
 * @author William Bittle
 * @version 2.2.0
 * @since 2.2.0
 */
public class HullGeneratorTest {
	/** The point cloud */
	private Vector2[] cloud;
	
	/**
	 * Sets up the testing point cloud.
	 */
	@Before
	public void setup() {
		// randomize the size from 4 to 100
		int size = (int) Math.floor(Math.random() * 96.0 + 4.0);
		// create the cloud container
		this.cloud = new Vector2[size];
		// fill the cloud with a random distribution of points
		for (int i = 0; i < size; i++) {
			this.cloud[i] = new Vector2(Math.random() * 2.0 - 1.0, Math.random() * 2.0 - 1.0);
		}
	}
	
	/**
	 * Tests the Gift Wrap class against the random
	 * point cloud.
	 */
	@Test
	public void giftWrap() {
		GiftWrap gw = new GiftWrap();
		Vector2[] hull = gw.generate(this.cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < this.cloud.length; i++) {
			Vector2 p = this.cloud[i];
			if (!poly.contains(p, Transform.IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
	
	/**
	 * Tests the Divide And Conquer class against the random
	 * point cloud.
	 */
	@Test
	public void divideAndConquer() {
		DivideAndConquer dac = new DivideAndConquer();
		Vector2[] hull = dac.generate(this.cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < this.cloud.length; i++) {
			Vector2 p = this.cloud[i];
			if (!poly.contains(p, Transform.IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
	
	/**
	 * Tests the Graham Scan class against the random
	 * point cloud.
	 */
	@Test
	public void grahamScan() {
		GrahamScan gs = new GrahamScan();
		Vector2[] hull = gs.generate(this.cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < this.cloud.length; i++) {
			Vector2 p = this.cloud[i];
			if (!poly.contains(p, Transform.IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
	
	/**
	 * Tests the Monotone Chain class against the random
	 * point cloud.
	 */
	@Test
	public void monotoneChain() {
		MonotoneChain mc = new MonotoneChain();
		Vector2[] hull = mc.generate(this.cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < this.cloud.length; i++) {
			Vector2 p = this.cloud[i];
			if (!poly.contains(p, Transform.IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
	
}
