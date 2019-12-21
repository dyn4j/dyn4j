/*
 * Copyright (c) 2010-2017 William Bittle  http://www.dyn4j.org/
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

import java.math.BigDecimal;
import java.util.Random;

import org.dyn4j.Epsilon;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test cases and randomized versions for the methods in {@link RobustGeometry} class.
 * @author Manolis Tsamis
 * @version 3.4.0
 * @since 3.4.0
 */
public class RobustGeometryTest {
	
	/** Seed for the randomized test. Can be any value */
	private static final int SEED = 0;
	
	/**
	 * Randomized test to check almost colinear vectors.
	 * Chose three random, almost colinear, points and then exhaustively check for all
	 * floating point values near the tested point. This will trigger the most
	 * complex paths in {@link RobustGeometry#getLocation(Vector2, Vector2, Vector2)}
	 */
	@Test
	public void randomizedTest() {
		// Constant seed so we always get the same sequence of randoms
		Random random = new Random(SEED);
		
		final int iterations = 100;
		final int blockSize = 10;
		
		for (int i = 0; i < iterations; i++) {
			// generate three colinear points pa, pb, pc
			Vector2 pa = new Vector2(random.nextDouble(), random.nextDouble());
			Vector2 pb = pa.product(random.nextDouble());
			Vector2 pc = pa.product(random.nextDouble());
			
			//loop to all directions
			for (int up = 0; up <= 4; up++) {
				boolean xUp = (up % 2) == 0;
				boolean yUp = (up / 2) == 0;
				
				Vector2 pcOffset = pc.copy();
				
				// test all adjacent floating point values in this quadrant
				for (int y = 0; y < blockSize; y++) {
					for (int x = 0; x < blockSize; x++) {
						double exact = getLocationExact(pcOffset, pa, pb);
						double robust = RobustGeometry.getLocation(pcOffset, pa, pb);
						
						TestCase.assertEquals(Math.signum(exact), Math.signum(robust));
						
						pcOffset.x = xUp? Math.nextUp(pcOffset.x) : Math.nextDown(pcOffset.x);
					}
					
					pcOffset.y = yUp? Math.nextUp(pcOffset.y) : Math.nextDown(pcOffset.y);
				}
			}
		}
	}
	
	/**
	 * Another randomized test but with uniform random points.
	 * This will mostly trigger the short path in {@link RobustGeometry#getLocation(Vector2, Vector2, Vector2)}
	 */
	@Test
	public void randomizedTest2() {
		// Constant seed so we always get the same sequence of randoms
		Random random = new Random(SEED);
		
		final int iterations = 1000;
		
		for (int i = 0; i < iterations; i++) {
			// generate three uniform random points pa, pb, pc
			Vector2 pa = new Vector2(random.nextDouble(), random.nextDouble());
			Vector2 pb = new Vector2(random.nextDouble(), random.nextDouble());
			Vector2 pc = new Vector2(random.nextDouble(), random.nextDouble());
			
			double exact = getLocationExact(pc, pa, pb);
			double robust = RobustGeometry.getLocation(pc, pa, pb);
			
			TestCase.assertEquals(Math.signum(exact), Math.signum(robust));
		}
	}
	
	/**
	 * Helper method to compute the equivalent of {@link Segment#getLocation(Vector2, Vector2, Vector2)}
	 * but with exact arithmetic, via the use of {@link BigDecimal}.
	 * The result is returned as a double which is an approximation of the computed value
	 * but this is the best we can request given the requirements.
	 * 
	 * @param point the point
	 * @param linePoint1 the first point of the line
	 * @param linePoint2 the second point of the line
	 * @return the approximation as double of the result
	 * @see Segment#getLocation(Vector2, Vector2, Vector2)
	 */
	private double getLocationExact(Vector2 point, Vector2 linePoint1, Vector2 linePoint2) {
		BigDecimal pax = new BigDecimal(point.x);
		BigDecimal pay = new BigDecimal(point.y);
		BigDecimal pbx = new BigDecimal(linePoint1.x);
		BigDecimal pby = new BigDecimal(linePoint1.y);
		BigDecimal pcx = new BigDecimal(linePoint2.x);
		BigDecimal pcy = new BigDecimal(linePoint2.y);
		
		BigDecimal d1 = pcx.subtract(pbx).multiply(pay.subtract(pby));
		BigDecimal d2 = pax.subtract(pbx).multiply(pcy.subtract(pby));
		BigDecimal result = d1.subtract(d2);
		
		return result.doubleValue();
	}
	
	/**
	 * This is a helper method to test the convex hull generators.
	 * 
	 * Tests if {@code point} is contained in the convex polygon defined by {@code vertices}.
	 * This points must be from a {@link Polygon} instance, validated through it's constructor.
	 * Uses robust geometric predicates.
	 * 
	 * @param vertices A list of vertices in CW/CCW order that describe a convex polygon
	 * @param point The point to test
	 * @return boolean
	 * @see Polygon#contains(Vector2)
	 */
	public static boolean robustPolygonContains(Vector2[] vertices, Vector2 point) {
		// Copied from Polygon#contains but uses RobustGeometry#getLocation instead
		
		// start from the pair (p1 = last, p2 = first) so there's no need to check in the loop for wrap-around of the i + 1 vertice
		int size = vertices.length;
		Vector2 p1 = vertices[size - 1];
		Vector2 p2 = vertices[0];
		
		// get the location of the point relative to the first two vertices
		double last = RobustGeometry.getLocation(point, p1, p2);
		
		// loop through the rest of the vertices
		for (int i = 0; i < size - 1; i++) {
			// p1 is now p2
			p1 = p2;
			// p2 is the next point
			p2 = vertices[i + 1];
			// check if they are equal (one of the vertices)
			if (point.equals(p1) || point.equals(p2)) {
				return true;
			}
			
			// do side of line test
			double location = RobustGeometry.getLocation(point, p1, p2);
			
			// multiply the last location with this location
			// if they are the same sign then the opertation will yield a positive result
			// -x * -y = +xy, x * y = +xy, -x * y = -xy, x * -y = -xy
			if (last * location < 0) {
				// reminder: (-0.0 < 0.0) evaluates to false and not true
				return false;
			}
			
			// update the last location, but only if it's not zero
			// a location of zero indicates that the point lies ON the line
			// through p1 and p2. We can ignore these values because the
			// convexity requirement of the shape will ensure that if it's
			// outside, a sign will change.
			if (Math.abs(location) > Epsilon.E) {
				last = location;
			}
		}
		
		return true;
	}
	
}