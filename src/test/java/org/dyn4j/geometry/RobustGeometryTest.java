package org.dyn4j.geometry;

import java.math.BigDecimal;
import java.util.Random;

import org.junit.Test;

import junit.framework.TestCase;

public class RobustGeometryTest {
	
	/** Seed for the randomized test. Can be any value */
	private static final int SEED = 0;
	
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
	
	private double getLocationExact(Vector2 pa, Vector2 pb, Vector2 pc) {
		BigDecimal pax = new BigDecimal(pa.x);
		BigDecimal pay = new BigDecimal(pa.y);
		BigDecimal pbx = new BigDecimal(pb.x);
		BigDecimal pby = new BigDecimal(pb.y);
		BigDecimal pcx = new BigDecimal(pc.x);
		BigDecimal pcy = new BigDecimal(pc.y);
		
		BigDecimal d1 = pcx.subtract(pbx).multiply(pay.subtract(pby));
		BigDecimal d2 = pax.subtract(pbx).multiply(pcy.subtract(pby));
		BigDecimal result = d1.subtract(d2);
		
		return result.doubleValue();
	}
	
}