/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.geometry.hull.GiftWrap;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link GiftWrap} algorithm.
 * @author William Bittle
 * @version 3.3.1
 * @since 3.3.1
 */
public class GiftWrapTest {
	/** Identity Transform instance */
	private static final Transform IDENTITY = new Transform();
	
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
	public void giftWrapRandom1() {
		GiftWrap gw = new GiftWrap();
		Vector2[] hull = gw.generate(this.cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < this.cloud.length; i++) {
			Vector2 p = this.cloud[i];
			if (!poly.contains(p, IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
	
	/**
	 * Tests the Gift Wrap class against the random
	 * point cloud.
	 */
	@Test
	public void giftWrapRandom2() {
		GiftWrap gw = new GiftWrap();
		Vector2[] hull = gw.generate(this.cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < this.cloud.length; i++) {
			Vector2 p = this.cloud[i];
			if (!poly.contains(p, IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
	
	/**
	 * Tests the Gift Wrap class against a failure case.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void giftWrap2() {
		Vector2[] cloud = new Vector2[] {
			new Vector2(23.854242711277, 1.0),
			new Vector2(57.707453390935676, 1.0), 
			new Vector2(13.0, 1.0), 
			new Vector2(27.918475169266998, 1.0)
		};
		
		GiftWrap gw = new GiftWrap();
		Vector2[] hull = gw.generate(cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < cloud.length; i++) {
			Vector2 p = cloud[i];
			if (!poly.contains(p, IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
	
	/**
	 * Tests the Gift Wrap class against a prior failure case.
	 */
	@Test
	public void giftWrap3() {
		Vector2[] cloud = new Vector2[] {
				new Vector2(1.5, 1.0),
				new Vector2(2.0, 1.0),
				new Vector2(2.5, 1.0),
				new Vector2(3.5, 1.0),
				new Vector2(1.0, 1.0), 
				new Vector2(5.0, 0.0)
		};
		
		GiftWrap gw = new GiftWrap();
		Vector2[] hull = gw.generate(cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < cloud.length; i++) {
			Vector2 p = cloud[i];
			if (!poly.contains(p, IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}

	/**
	 * Tests the Gift Wrap class against a prior failure case.
	 */
	@Test
	public void giftWrap4() {
		Vector2[] cloud = new Vector2[] {
			new Vector2(1.0, 1.0),
			new Vector2(1.0, 1.0),
			new Vector2(9135172.538699752, 1594.2921033236523), 
			new Vector2(1.0, 3436.444789677664),
			new Vector2(53371.47726301303, 63.201463180191396), 
			new Vector2(1.0, 1.0),
			new Vector2(0.09713620217398017, 286668.0866273699), 
			new Vector2(104.83526669412421, 579.583503857007)
		};
		
		GiftWrap gw = new GiftWrap();
		Vector2[] hull = gw.generate(cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < cloud.length; i++) {
			Vector2 p = cloud[i];
			if (!poly.contains(p, IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
	
	/**
	 * Tests the Gift Wrap class against a prior failure case.
	 */
	@Test
	public void giftWrap6() {
		Vector2[] cloud = new Vector2[] {
			new Vector2(9.67305424383519, 114.09907896473986),
			new Vector2(1.0, 1161.9752606517477), 
			new Vector2(1.0, 1.0),
			new Vector2(1.0, 10.546088997659012), 
			new Vector2(22.088494561091807, 230.94365885699824), 
			new Vector2(2.8366426821689994, 1.0),
			new Vector2(8.944224404040732, 6.315177488492587), 
			new Vector2(1.0, 59.98064323348245), 
			new Vector2(9.24861145190379, 8404.268678968832),
			new Vector2(1.0, 0.03504029713737921),
			new Vector2(1.0, 82.55330004652801)
		};
		
		GiftWrap gw = new GiftWrap();
		Vector2[] hull = gw.generate(cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < cloud.length; i++) {
			Vector2 p = cloud[i];
			if (!poly.contains(p, IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
	
	/**
	 * Tests the Gift Wrap class against a prior failure case.
	 */
	@Test
	public void giftWrap7() {
		Vector2[] cloud = new Vector2[] {
			new Vector2(1.0, 1.5767304065549268E12), 
			new Vector2(1.870841152451107, 6.534140012505794E23), 
			new Vector2(1.9603053816739006E12, 9.074076290143341E10), 
			new Vector2(3260266.640396411, 7.796498271329308E7), 
			new Vector2(28.709287118505284, 82447.5698720256), 
			new Vector2(1.8463774403168068E15, 0.013098511687408589), 
			new Vector2(3740193.601814064, 4682.6340006396895), 
			new Vector2(3170758.3618271016, 2.131083797649407E7), 
			new Vector2(143.83527343008367, 3.2294659543003845E15), 
			new Vector2(1.0, 5.956908518731977E17), 
			new Vector2(2.0531014115467064E-7, 2306510.1010659263), 
			new Vector2(1.2474786776966758E20, 1.4802417824918536E11), 
			new Vector2(1.0, 15.084034859698757)
		};
		
		GiftWrap gw = new GiftWrap();
		Vector2[] hull = gw.generate(cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < cloud.length; i++) {
			Vector2 p = cloud[i];
			if (!poly.contains(p, IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
	
	/**
	 * Tests the Gift Wrap class against a prior failure case.
	 */
	@Test
	public void giftWrap5() {
		Vector2[] cloud = new Vector2[] {
			new Vector2(69464.96179292782, 0.05006981639456297), 
			new Vector2(0.03735960666625501, 0.3783853688716485), 
			new Vector2(334.68865889609134, 3.955720227287777E-23), 
			new Vector2(5.758935896542613E22, 8.12199057379559E21)
		};
		
		GiftWrap gw = new GiftWrap();
		Vector2[] hull = gw.generate(cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < cloud.length; i++) {
			Vector2 p = cloud[i];
			if (!poly.contains(p, IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
	
	/**
	 * Tests the Gift Wrap class against a prior failure case.
	 */
	@Test
	public void giftWrap5Part2() {
		Vector2[] cloud = new Vector2[] {
			new Vector2(334.68865889609134, 3.955720227287777E-23),
			new Vector2(5.758935896542613E22, 8.12199057379559E21),
			new Vector2(69464.96179292782, 0.05006981639456297), 
			new Vector2(0.03735960666625501, 0.3783853688716485)
		};
		
		GiftWrap gw = new GiftWrap();
		Vector2[] hull = gw.generate(cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < cloud.length; i++) {
			Vector2 p = cloud[i];
			if (!poly.contains(p, IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
	
	/**
	 * Tests the Gift Wrap class against a prior failure case.
	 */
	@Test
	public void giftWrap5Part3() {
		Vector2[] cloud = new Vector2[] {
			new Vector2(334.68865889609134, 3.955720227287777E-23),
			new Vector2(5.758935896542613E22, 8.12199057379559E21),
			new Vector2(400.758935896542613E21, 8.12199057379559E20),
			new Vector2(69464.96179292782, 0.05006981639456297), 
			new Vector2(0.03735960666625501, 0.3783853688716485)
		};
		
		GiftWrap gw = new GiftWrap();
		Vector2[] hull = gw.generate(cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < cloud.length; i++) {
			Vector2 p = cloud[i];
			if (!poly.contains(p, IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
	
	/**
	 * Tests the Gift Wrap class against a prior failure case.
	 */
	@Test
	public void giftWrap8() {
		Vector2[] cloud = new Vector2[] {
			new Vector2(24.00000000000005, 24.000000000000053),
			new Vector2(54.85, 6),
			new Vector2(24.000000000000068, 24.000000000000071),
			new Vector2(54.850000000000357, 61.000000000000121), 
			new Vector2(24, 6),
			new Vector2(6, 6)
		};
		
		GiftWrap gw = new GiftWrap();
		Vector2[] hull = gw.generate(cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < cloud.length; i++) {
			Vector2 p = cloud[i];
			if (!poly.contains(p, IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}

	/**
	 * Tests the Gift Wrap class against a prior failure case.
	 */
	@Test
	public void giftWrap9() {
		Vector2[] cloud = new Vector2[] {
				new Vector2(23, 1.0),
				new Vector2(57, 1.0),
				new Vector2(13, 1.0),
				new Vector2(27, 10.0)};
		
		GiftWrap gw = new GiftWrap();
		Vector2[] hull = gw.generate(cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < cloud.length; i++) {
			Vector2 p = cloud[i];
			if (!poly.contains(p, IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
	
	/**
	 * Tests the GiftWrap class against a prior failure case.
	 */
	@Test
	public void dac10() {
		Vector2[] cloud = new Vector2[] {
			new Vector2(-7.725662343635252, 3.239314248048395), 
			new Vector2(-7.725662343635252, 9.244107520658332), 
			new Vector2(-7.725662343635252, 5.6066430781506575), 
			new Vector2(-5.985432177897989, 1.0634285355681339), 
			new Vector2(2.7404621676247265, -4.946792659796997), 
		};
		
		GiftWrap gw = new GiftWrap();
		Vector2[] hull = gw.generate(cloud);
		
		// make sure we can create a polygon from it
		// (this will check for convexity, winding, etc)
		Polygon poly = new Polygon(hull);
		
		// make sure all the points are either on or contained in the hull
		for (int i = 0; i < cloud.length; i++) {
			Vector2 p = cloud[i];
			if (!poly.contains(p, IDENTITY)) {
				TestCase.fail("Hull does not contain all points.");
			}
		}
	}
}
