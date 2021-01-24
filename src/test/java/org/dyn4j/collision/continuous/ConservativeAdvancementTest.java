/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision.continuous;

import org.dyn4j.collision.narrowphase.DistanceDetector;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test class for the {@link ConservativeAdvancement} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 1.2.0
 */
public class ConservativeAdvancementTest {
	/** Identity Transform instance */
	private static final Transform IDENTITY = new Transform();
	
	/** A test time step */
	private static final double TIME_STEP = 1.0 / 60.0;
	
	/** The time of impact detector */
	private ConservativeAdvancement detector;
	
	/** The first convex */
	private Convex c1;
	
	/** The second convex */
	private Convex c2;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.detector = new ConservativeAdvancement();
		
		this.c1 = Geometry.createUnitCirclePolygon(5, 0.1);
		this.c2 = Geometry.createRectangle(20.0, 0.5);
	}
	
	/**
	 * Tests the time of impact computation when the bodies
	 * intersect after the midpoint of the dynamic body's trajectory.
	 */
	@Test
	public void afterMidPoint() {
		//               0
		//               |
		//               |
		//  -------------------------------- stationary
		//               V
		//               E
		
		Transform t1 = new Transform();
		t1.translate(0.0, 1.5);
		
		Vector2 dp1 = new Vector2(0.0, -120.0 * TIME_STEP);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean collision = this.detector.getTimeOfImpact(this.c1, t1, dp1, 0.0, this.c2, IDENTITY, new Vector2(), 0.0, toi);
		TestCase.assertTrue(collision);
		
		// test the TOI
		TestCase.assertEquals(0.577, toi.getTime(), 1.0e-3);
		
		// test the final transform the body should be at before the collision
		Transform tx = t1.lerped(dp1, 0.0, toi.getTime());
		TestCase.assertEquals(0.000, tx.getTranslationX(), 1.0e-3);
		TestCase.assertEquals(0.346, tx.getTranslationY(), 1.0e-3);
	}
	
	/**
	 * Tests the time of impact computation when the bodies
	 * intersect before the midpoint of the dynamic body's trajectory.
	 */
	@Test
	public void beforeMidPoint() {
		//               0
		//               |
		//  -------------------------------- stationary
		//               |
		//               V
		//               E

		Transform t1 = new Transform();
		t1.translate(0.0, 1.0);
		
		Vector2 dp1 = new Vector2(0.0, -120.0 * TIME_STEP);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean collision = this.detector.getTimeOfImpact(this.c1, t1, dp1, 0.0, this.c2, IDENTITY, new Vector2(), 0.0, toi);
		TestCase.assertTrue(collision);
		
		// test the TOI
		TestCase.assertEquals(0.327, toi.getTime(), 1.0e-3);
		
		// test the final transform the body should be at before the collision
		Transform tx = t1.lerped(dp1, 0.0, toi.getTime());
		TestCase.assertEquals(0.000, tx.getTranslationX(), 1.0e-3);
		TestCase.assertEquals(0.346, tx.getTranslationY(), 1.0e-3);
	}
	
	/**
	 * Tests the time of impact computation when the bodies are 
	 * moving in the same direction where the faster should collide
	 * with the slower.
	 */
	@Test
	public void sameDirection() {
		// S--------------------->E
		//     S------>E
		
		Transform t1 = new Transform();
		t1.translate(0.0, 1.5);
		Vector2 dp1 = new Vector2(120.0 * TIME_STEP, 0.0);
		
		Convex c2 = Geometry.createSquare(0.2);
		Transform t2 = new Transform();
		t2.translate(0.5, 1.5);
		Vector2 dp2 = new Vector2(30.0 * TIME_STEP, 0.0);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean collision = this.detector.getTimeOfImpact(this.c1, t1, dp1, 0.0, c2, t2, dp2, 0.0, toi);
		TestCase.assertTrue(collision);
		
		// test the TOI
		TestCase.assertEquals(0.200, toi.getTime(), 1.0e-3);
		
		// test the final transform the body should be at before the collision
		Transform tx = t1.lerped(dp1, 0.0, toi.getTime());
		TestCase.assertEquals(0.400, tx.getTranslationX(), 1.0e-3);
		TestCase.assertEquals(1.500, tx.getTranslationY(), 1.0e-3);
	}
	
	/**
	 * Tests the time of impact computation when two bodies are
	 * moving in opposing directions.
	 */
	@Test
	public void oppositeDirection() {
		// S--------------------->E
		//                     E<----S
		
		Transform t1 = new Transform();
		t1.translate(0.0, 1.5);
		Vector2 dp1 = new Vector2(120.0 * TIME_STEP, 0.0);
		
		Convex c2 = Geometry.createSquare(0.5);
		Transform t2 = new Transform();
		t2.translate(2.0, 1.5);
		Vector2 dp2 = new Vector2(-30.0 * TIME_STEP, 0.0);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean collision = this.detector.getTimeOfImpact(this.c1, t1, dp1, 0.0, c2, t2, dp2, 0.0, toi);
		TestCase.assertTrue(collision);
		
		// test the TOI
		TestCase.assertEquals(0.659, toi.getTime(), 1.0e-3);
		
		// test the final transform the body should be at before the collision
		Transform tx1 = t1.lerped(dp1, 0.0, toi.getTime());
		TestCase.assertEquals(1.319, tx1.getTranslationX(), 1.0e-3);
		TestCase.assertEquals(1.500, tx1.getTranslationY(), 1.0e-3);
		Transform tx2 = t2.lerped(dp2, 0.0, toi.getTime());
		TestCase.assertEquals(1.670, tx2.getTranslationX(), 1.0e-3);
		TestCase.assertEquals(1.500, tx2.getTranslationY(), 1.0e-3);
	}
	
	/**
	 * Tests the time of impact computation where the two bodies
	 * are moving orthogonal to one another.
	 */
	@Test
	public void orthogonalDirection() {
		//          E
		//          ^
		//          |
		// S--------------------->E
		//          |
		//          S
		
		Transform t1 = new Transform();
		t1.translate(0.0, 1.5);
		Vector2 dp1 = new Vector2(120.0 * TIME_STEP, 0.0);
		
		Convex c2 = Geometry.createSquare(0.2);
		Transform t2 = new Transform();
		t2.translate(1.0, 1.25);
		Vector2 dp2 = new Vector2(0.0, 30.0 * TIME_STEP);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean collision = this.detector.getTimeOfImpact(this.c1, t1, dp1, 0.0, c2, t2, dp2, 0.0, toi);
		TestCase.assertTrue(collision);
		
		// test the TOI
		TestCase.assertEquals(0.400, toi.getTime(), 1.0e-3);
		
		// test the final transform the body should be at before the collision
		Transform tx1 = t1.lerped(dp1, 0.0, toi.getTime());
		TestCase.assertEquals(0.800, tx1.getTranslationX(), 1.0e-3);
		TestCase.assertEquals(1.500, tx1.getTranslationY(), 1.0e-3);
		Transform tx2 = t2.lerped(dp2, 0.0, toi.getTime());
		TestCase.assertEquals(1.000, tx2.getTranslationX(), 1.0e-3);
		TestCase.assertEquals(1.450, tx2.getTranslationY(), 1.0e-3);
	}
	
	/**
	 * Tests the time of impact computation in a failure case
	 * where the two bodies are moving in the same direction
	 * but the bodies do not collide.
	 */
	@Test
	public void sameDirectionNoCollision() {
		//                  S--------------------->E
		// S----------->E
		
		Transform t1 = new Transform();
		t1.translate(0.0, 1.0);
		Vector2 dp1 = new Vector2(120.0 * TIME_STEP, 0.0);
		
		Convex c2 = Geometry.createSquare(0.5);
		Transform t2 = new Transform();
		t2.translate(0.0, 1.5);
		Vector2 dp2 = new Vector2(30.0 * TIME_STEP, 0.0);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean collision = this.detector.getTimeOfImpact(this.c1, t1, dp1, 0.0, c2, t2, dp2, 0.0, toi);
		TestCase.assertFalse(collision);
	}
	
	/**
	 * Tests the time of impact computation in a failure case
	 * where the two bodies are moving in the same direction
	 * but the bodies do not collide.
	 */
	@Test
	public void sameDirectionNoCollision2() {
		// S--------------------->E
		//               S----------->E
		
		Transform t1 = new Transform();
		t1.translate(0.0, 1.0);
		Vector2 dp1 = new Vector2(120.0 * TIME_STEP, 0.0);
		
		Convex c2 = Geometry.createSquare(0.5);
		Transform t2 = new Transform();
		t2.translate(1.6, 1.5);
		Vector2 dp2 = new Vector2(60.0 * TIME_STEP, 0.0);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean collision = this.detector.getTimeOfImpact(this.c1, t1, dp1, 0.0, c2, t2, dp2, 0.0, toi);
		TestCase.assertFalse(collision);
	}
	
	/**
	 * Tests the time of impact computation in a failure case
	 * where the two bodies are moving orthogonal to one another
	 * but do not collide.
	 */
	@Test
	public void orthogonalDirectionNoCollision() {
		//          E
		//          ^
		//          |
		// S--------------------->E
		//          |
		//          S
		
		Transform t1 = new Transform();
		t1.translate(0.0, 1.0);
		Vector2 dp1 = new Vector2(120.0 * TIME_STEP, 0.0);
		
		Convex c2 = Geometry.createSquare(0.1);
		Transform t2 = new Transform();
		t2.translate(1.0, 1.3);
		Vector2 dp2 = new Vector2(0.0, 60.0 * TIME_STEP);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean collision = this.detector.getTimeOfImpact(this.c1, t1, dp1, 0.0, c2, t2, dp2, 0.0, toi);
		TestCase.assertFalse(collision);
	}
	
	/**
	 * Tests the set distance detector method.
	 */
	@Test
	public void setDistanceDetector() {
		DistanceDetector dd = new Gjk();
		this.detector.setDistanceDetector(dd);
		
		TestCase.assertSame(dd, this.detector.getDistanceDetector());
	}
	
	/**
	 * Tests the set distance detector method passing a
	 * null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullDistanceDetector() {
		this.detector.setDistanceDetector(null);
	}
	
	/**
	 * Tests the creation of the class with a null detector.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullDistanceDetector() {
		new ConservativeAdvancement(null);
	}
	
	/**
	 * Tests the successful creation of the class.
	 */
	@Test
	public void createSuccess() {
		DistanceDetector dd = new Gjk();
		ConservativeAdvancement ca = new ConservativeAdvancement(dd);
		
		TestCase.assertEquals(dd, ca.getDistanceDetector());
		TestCase.assertEquals(ConservativeAdvancement.DEFAULT_DISTANCE_EPSILON, ca.getDistanceEpsilon());
		TestCase.assertEquals(ConservativeAdvancement.DEFAULT_MAX_ITERATIONS, ca.getMaxIterations());
	}
	
	/**
	 * Tests the set tolerance method.
	 */
	@Test
	public void setTolerance() {
		this.detector.setDistanceEpsilon(0.3);
		TestCase.assertEquals(0.3, this.detector.getDistanceEpsilon());
		
		this.detector.setDistanceEpsilon(0.000002);
		TestCase.assertEquals(0.000002, this.detector.getDistanceEpsilon());
	}
	
	/**
	 * Tests the set tolerance method passing a zero value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setZeroTolerance() {
		this.detector.setDistanceEpsilon(0);
	}
	
	/**
	 * Tests the set tolerance method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeTolerance() {
		this.detector.setDistanceEpsilon(-0.00003);
	}
	
	/**
	 * Tests the set max iterations method.
	 */
	@Test
	public void setMaxIterations() {
		this.detector.setMaxIterations(23);
		TestCase.assertEquals(23, this.detector.getMaxIterations());
		
		this.detector.setMaxIterations(10);
		TestCase.assertEquals(10, this.detector.getMaxIterations());
	}
	
	/**
	 * Tests the set max iterations method passing a zero value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setZeroMaxIterations() {
		this.detector.setMaxIterations(0);
	}
	
	/**
	 * Tests the set max iterations method passing a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeMaxIterations() {
		this.detector.setMaxIterations(-2);
	}
	
	/**
	 * Tests the set max iterations method passing a value less than 5.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setLessThanTenMaxIterations() {
		this.detector.setMaxIterations(4);
	}
	
	/**
	 * Tests a body rotating very fast against a static body.
	 */
	@Test
	public void fastRotationAgainstStatic() {
		Convex c1 = Geometry.createRectangle(2.0, 0.2);
		Transform t1 = new Transform();
		t1.translate(0.5, 0.0);
		t1.rotate(Math.toRadians(-40.0), t1.getTranslationX(), t1.getTranslationY());
		Vector2 dp1 = new Vector2();
		double da1 = Math.toRadians(80.0) * 60.0 * TIME_STEP;
		
		Convex c2 = Geometry.createRectangle(10.0, 0.5);
		Transform t2 = new Transform();
		t2.translate(-5.0, 0.0);
		Vector2 dp2 = new Vector2();
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean collision = this.detector.getTimeOfImpact(c1, t1, dp1, da1, c2, t2, dp2, 0.0, toi);
		TestCase.assertTrue(collision);
		
		// get the final transform given the time of impact
		Transform tx1f = t1.lerped(dp1, da1, toi.getTime());
		
		// make sure the time of impact is small in this case
		TestCase.assertEquals(0.039, toi.getTime(), 1.0e-3);
		// the rotation shouldn't be much more than -40
		TestCase.assertEquals(-0.643, tx1f.getRotationAngle(), 1.0e-3);
	}
	
	/**
	 * Tests a body rotating very fast against a
	 * another body rotating very fast.
	 */
	@Test
	public void fastRotationAgainstFastRotation() {
		Convex c1 = Geometry.createRectangle(2.0, 0.2);
		Transform t1 = new Transform();
		t1.translate(0.5, 0.0);
		t1.rotate(Math.toRadians(-40.0), t1.getTranslationX(), t1.getTranslationY());
		Vector2 dp1 = new Vector2();
		double da1 = Math.toRadians(80.0) * 60.0 * TIME_STEP;
		
		Convex c2 = Geometry.createRectangle(10.0, 0.5);
		Transform t2 = new Transform();
		t2.translate(-5.0, 0.0);
		t2.rotate(Math.toRadians(-20.0), t2.getTranslationX(), t2.getTranslationY());
		Vector2 dp2 = new Vector2();
		double da2 = Math.toRadians(60.0) * 60.0 * TIME_STEP;
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean collision = this.detector.getTimeOfImpact(c1, t1, dp1, da1, c2, t2, dp2, da2, toi);
		TestCase.assertTrue(collision);
		
		// get the final transform given the time of impact
		Transform tx1f = t1.lerped(dp1, da1, toi.getTime());
		Transform tx2f = t2.lerped(dp2, da2, toi.getTime());
		
		// make sure the time of impact is small in this case
		TestCase.assertEquals(0.293, toi.getTime(), 1.0e-3);
		// the rotation shouldn't be much more than -40
		TestCase.assertEquals(-0.288, tx1f.getRotationAngle(), 1.0e-3);
		TestCase.assertEquals(-0.041, tx2f.getRotationAngle(), 1.0e-3);
	}
	
	/**
	 * Tests a body rotating very fast against a
	 * another body rotating very fast where no collision
	 * occurs.
	 */
	@Test
	public void fastRotationAgainstFastRotationNoCollision() {
		Convex c1 = Geometry.createRectangle(2.0, 0.2);
		Transform t1 = new Transform();
		t1.translate(0.5, 0.0);
		t1.rotate(Math.toRadians(-40.0), t1.getTranslationX(), t1.getTranslationY());
		Vector2 dp1 = new Vector2();
		double da1 = Math.toRadians(80.0 * 60.0) * TIME_STEP;
		
		Convex c2 = Geometry.createRectangle(10.0, 0.5);
		Transform t2 = new Transform();
		t2.translate(-5.0, 0.0);
		t2.rotate(Math.toRadians(-80.0), t2.getTranslationX(), t2.getTranslationY());
		Vector2 dp2 = new Vector2();
		double da2 = Math.toRadians(60.0 * 60.0) * TIME_STEP;
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean collision = this.detector.getTimeOfImpact(c1, t1, dp1, da1, c2, t2, dp2, da2, toi);
		TestCase.assertFalse(collision);
	}
}
