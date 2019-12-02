package org.dyn4j.geometry;

import java.math.BigDecimal;
import java.util.Random;

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
	
}