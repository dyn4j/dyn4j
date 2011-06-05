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
package org.dyn4j.collision;

import junit.framework.TestCase;

import org.dyn4j.collision.continuous.ConservativeAdvancement;
import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.collision.narrowphase.DistanceDetector;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Transform;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the {@link ConservativeAdvancement} class.
 * <p>
 * All bodies in this test must be infinite mass so that the
 * World class doesn't solve their TOI when a step is performed.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.2.0
 */
public class ConservativeAdvancementTest {
	/** The time of impact detector */
	private ConservativeAdvancement detector;
	
	/** The world object */
	private World world;
	
	/** The first body */
	private Body b1;
	
	/** The second body */
	private Body b2;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.detector = new ConservativeAdvancement();
		
		// setup the initial configuration
		this.b1 = new Body();
		this.b2 = new Body();
		
		this.b1.addFixture(Geometry.createUnitCirclePolygon(5, 0.1));
		this.b2.addFixture(Geometry.createRectangle(20.0, 0.5));
		
		this.world = new World();
		
		this.world.add(this.b1);
		this.world.add(this.b2);
	}
	
	/**
	 * Tests the time of impact computation when the bodies
	 * intersect after the midpoint of the dynamic body's trajectory.
	 */
	@Test
	public void afterMidPoint() {
		// translate the one body
		this.b1.translate(0.0, 1.5);
		this.b1.getVelocity().set(0.0, -120.0);
		
		//               0
		//               |
		//               |
		//  -------------------------------- stationary
		//               V
		//               E
		
		// perform one iteration
		this.world.step(1);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		this.detector.getTimeOfImpact(this.b1, this.b2, 0.0, 1.0, toi);
		
		// test the TOI
		TestCase.assertEquals(0.577, toi.getToi(), 1.0e-3);
		
		// test the final transform the body should be at before the collision
		Transform tx = b1.getInitialTransform().lerped(b1.getFinalTransform(), toi.getToi());
		TestCase.assertEquals(0.000, tx.getTranslationX(), 1.0e-3);
		TestCase.assertEquals(0.345, tx.getTranslationY(), 1.0e-3);
	}
	
	/**
	 * Tests the time of impact computation when the bodies
	 * intersect before the midpoint of the dynamic body's trajectory.
	 */
	@Test
	public void beforeMidPoint() {
		// translate the one body
		this.b1.translate(0.0, 1.0);
		this.b1.getVelocity().set(0.0, -120.0);
		
		//               0
		//               |
		//  -------------------------------- stationary
		//               |
		//               V
		//               E
		
		// perform one iteration
		this.world.step(1);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		this.detector.getTimeOfImpact(this.b1, this.b2, 0.0, 1.0, toi);

		// test the TOI
		TestCase.assertEquals(0.327, toi.getToi(), 1.0e-3);
		
		// test the final transform the body should be at before the collision
		Transform tx = b1.getInitialTransform().lerped(b1.getFinalTransform(), toi.getToi());
		TestCase.assertEquals(0.000, tx.getTranslationX(), 1.0e-3);
		TestCase.assertEquals(0.345, tx.getTranslationY(), 1.0e-3);
	}
	
	/**
	 * Tests the time of impact computation when the bodies are 
	 * moving in the same direction where the faster should collide
	 * with the slower.
	 */
	@Test
	public void sameDirection() {
		// translate the one body
		this.b1.translate(0.0, 1.5);
		this.b1.getVelocity().set(120.0, 0.0);
		
		this.b2.removeFixture(0);
		this.b2.addFixture(Geometry.createSquare(0.2));
		this.b2.translate(0.5, 1.5);
		this.b2.getVelocity().set(30.0, 0.0);
		
		// S--------------------->E
		//     S------>E
		
		// perform one iteration
		this.world.step(1);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		this.detector.getTimeOfImpact(this.b1, this.b2, 0.0, 1.0, toi);
		
		// test the TOI
		TestCase.assertEquals(0.199, toi.getToi(), 1.0e-3);
		
		// test the final transform the body should be at before the collision
		Transform tx = b1.getInitialTransform().lerped(b1.getFinalTransform(), toi.getToi());
		TestCase.assertEquals(0.399, tx.getTranslationX(), 1.0e-3);
		TestCase.assertEquals(1.499, tx.getTranslationY(), 1.0e-3);
	}
	
	/**
	 * Tests the time of impact computation when two bodies are
	 * moving in opposing directions.
	 */
	@Test
	public void oppositeDirection() {
		// translate the one body
		this.b1.translate(0.0, 1.5);
		this.b1.getVelocity().set(120.0, 0.0);
		
		this.b2.removeFixture(0);
		this.b2.addFixture(Geometry.createSquare(0.5));
		this.b2.translate(2.0, 1.5);
		this.b2.getVelocity().set(-30.0, 0.0);
		
		// S--------------------->E
		//                     E<----S
		
		// perform one iteration
		this.world.step(1);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		this.detector.getTimeOfImpact(this.b1, this.b2, 0.0, 1.0, toi);
		
		// test the TOI
		TestCase.assertEquals(0.659, toi.getToi(), 1.0e-3);
		
		// test the final transform the body should be at before the collision
		Transform tx1 = b1.getInitialTransform().lerped(b1.getFinalTransform(), toi.getToi());
		TestCase.assertEquals(1.319, tx1.getTranslationX(), 1.0e-3);
		TestCase.assertEquals(1.500, tx1.getTranslationY(), 1.0e-3);
		Transform tx2 = b2.getInitialTransform().lerped(b2.getFinalTransform(), toi.getToi());
		TestCase.assertEquals(1.670, tx2.getTranslationX(), 1.0e-3);
		TestCase.assertEquals(1.500, tx2.getTranslationY(), 1.0e-3);
	}
	
	/**
	 * Tests the time of impact computation where the two bodies
	 * are moving orthogonal to one another.
	 */
	@Test
	public void orthogonalDirection() {
		// translate the one body
		this.b1.translate(0.0, 1.5);
		this.b1.getVelocity().set(120.0, 0.0);
		
		this.b2.removeFixture(0);
		this.b2.addFixture(Geometry.createSquare(0.2));
		this.b2.translate(1.0, 1.25);
		this.b2.getVelocity().set(0.0, 30.0);
		
		//          E
		//          ^
		//          |
		// S--------------------->E
		//          |
		//          S
		
		// perform one iteration
		this.world.step(1);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		this.detector.getTimeOfImpact(this.b1, this.b2, 0.0, 1.0, toi);

		// test the TOI
		TestCase.assertEquals(0.400, toi.getToi(), 1.0e-3);
		
		// test the final transform the body should be at before the collision
		Transform tx1 = b1.getInitialTransform().lerped(b1.getFinalTransform(), toi.getToi());
		TestCase.assertEquals(0.800, tx1.getTranslationX(), 1.0e-3);
		TestCase.assertEquals(1.500, tx1.getTranslationY(), 1.0e-3);
		Transform tx2 = b2.getInitialTransform().lerped(b2.getFinalTransform(), toi.getToi());
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
		// translate the one body
		this.b1.translate(1.0, 1.5);
		this.b1.getVelocity().set(120.0, 0.0);
		
		this.b2.removeFixture(0);
		this.b2.addFixture(Geometry.createSquare(0.5));
		this.b2.translate(0.0, 1.5);
		this.b2.getVelocity().set(30.0, 0.0);
		
		//                  S--------------------->E
		// S----------->E
		
		// perform one iteration
		this.world.step(1);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean collide = this.detector.getTimeOfImpact(this.b1, this.b2, 0.0, 1.0, toi);
		
		TestCase.assertFalse(collide);
	}
	
	/**
	 * Tests the time of impact computation in a failure case
	 * where the two bodies are moving in the same direction
	 * but the bodies do not collide.
	 */
	@Test
	public void sameDirectionNoCollision2() {
		// translate the one body
		this.b1.translate(0.0, 1.5);
		this.b1.getVelocity().set(120.0, 0.0);
		
		this.b2.removeFixture(0);
		this.b2.addFixture(Geometry.createSquare(0.5));
		this.b2.translate(1.6, 1.5);
		this.b2.getVelocity().set(60.0, 0.0);
		
		// S--------------------->E
		//               S----------->E
		
		// perform one iteration
		this.world.step(1);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean collide = this.detector.getTimeOfImpact(this.b1, this.b2, 0.0, 1.0, toi);
		TestCase.assertFalse(collide);
	}
	
	/**
	 * Tests the time of impact computation in a failure case
	 * where the two bodies are moving orthogonal to one another
	 * but do not collide.
	 */
	@Test
	public void orthogonalDirectionNoCollision() {
		// translate the one body
		this.b1.translate(0.0, 1.5);
		this.b1.getVelocity().set(120.0, 0.0);
		
		this.b2.removeFixture(0);
		this.b2.addFixture(Geometry.createSquare(0.1));
		this.b2.translate(1.0, 1.3);
		this.b2.getVelocity().set(0.0, 60.0);
		
		//          E
		//          ^
		//          |
		// S--------------------->E
		//          |
		//          S
		
		// perform one iteration
		this.world.step(1);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean collide = this.detector.getTimeOfImpact(this.b1, this.b2, 0.0, 1.0, toi);
		
		TestCase.assertFalse(collide);
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
		// translate the one body
		this.b1.removeFixture(0);
		this.b1.addFixture(Geometry.createRectangle(2.0, 0.2));
		// need to use the set mass method to compute the rotation disc radius
		this.b1.setMass(Mass.Type.INFINITE);
		this.b1.translate(0.5, 0.0);
		this.b1.rotateAboutCenter(Math.toRadians(-40));
		// set the rotation to very fast
		this.b1.setAngularVelocity(60.0 * Math.toRadians(80.0));
		
		this.b2.removeFixture(0);
		this.b2.addFixture(Geometry.createRectangle(10.0, 0.5));
		this.b2.translate(-5.0, 0.0);
		
		// perform one iteration
		this.world.step(1);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean missed = this.detector.getTimeOfImpact(this.b1, this.b2, 0.0, 1.0, toi);
		
		// get the final transform given the time of impact
		Transform tx10 = b1.getInitialTransform();
		Transform tx1 = b1.getTransform();
		Transform tx1f = tx10.lerped(tx1, toi.getToi());
		
		// make sure a collision was missed and detected by the toi detector
		TestCase.assertTrue(missed);
		// make sure the time of impact is small in this case
		TestCase.assertEquals(0.039, toi.getToi(), 1.0e-3);
		// the rotation shouldn't be much more than -40
		TestCase.assertEquals(-0.643, tx1f.getRotation(), 1.0e-3);
	}
	
	/**
	 * Tests a body rotating very fast against a
	 * another body rotating very fast.
	 */
	@Test
	public void fastRotationAgainstFastRotation() {
		// translate the one body
		this.b1.removeFixture(0);
		this.b1.addFixture(Geometry.createRectangle(2.0, 0.2));
		// need to use the set mass method to compute the rotation disc radius
		this.b1.setMass(Mass.Type.INFINITE);
		this.b1.translate(0.5, 0.0);
		this.b1.rotateAboutCenter(Math.toRadians(-40));
		// set the rotation to very fast
		this.b1.setAngularVelocity(60.0 * Math.toRadians(80.0));
		
		this.b2.removeFixture(0);
		this.b2.addFixture(Geometry.createRectangle(10.0, 0.5));
		// need to use the set mass method to compute the rotation disc radius
		this.b2.setMass(Mass.Type.INFINITE);
		this.b2.translate(-5.0, 0.0);
		this.b2.rotateAboutCenter(Math.toRadians(-20));
		this.b2.setAngularVelocity(60.0 * Math.toRadians(60.0));
		
		// perform one iteration
		this.world.step(1);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean missed = this.detector.getTimeOfImpact(this.b1, this.b2, 0.0, 1.0, toi);
		
		// interpolate the final transforms
		Transform tx1 = b1.getTransform();
		Transform tx2 = b2.getTransform();
		Transform tx10 = b1.getInitialTransform();
		Transform tx20 = b2.getInitialTransform();
		Transform tx1f = tx10.lerped(tx1, toi.getToi());
		Transform tx2f = tx20.lerped(tx2, toi.getToi());
		
		// a collision should have been missed but detected by the toi detector
		TestCase.assertTrue(missed);
		// make sure the time of impact is small in this case
		TestCase.assertEquals(0.293, toi.getToi(), 1.0e-3);
		// the rotation shouldn't be much more than -40
		TestCase.assertEquals(-0.288, tx1f.getRotation(), 1.0e-3);
		TestCase.assertEquals(-0.041, tx2f.getRotation(), 1.0e-3);
	}
	
	/**
	 * Tests a body rotating very fast against a
	 * another body rotating very fast where no collision
	 * occurs.
	 */
	@Test
	public void fastRotationAgainstFastRotationNoCollision() {
		// translate the one body
		this.b1.removeFixture(0);
		this.b1.addFixture(Geometry.createRectangle(2.0, 0.2));
		this.b1.translate(0.5, 0.0);
		this.b1.rotateAboutCenter(Math.toRadians(-40));
		// need to use the set mass method to compute the rotation disc radius
		this.b2.setMass(Mass.Type.INFINITE);
		// set the rotation to very fast
		this.b1.setAngularVelocity(Math.toRadians(60.0 * 80.0));
		
		this.b2.removeFixture(0);
		this.b2.addFixture(Geometry.createRectangle(10.0, 0.5));
		// need to use the set mass method to compute the rotation disc radius
		this.b2.setMass(Mass.Type.INFINITE);
		this.b2.translate(-5.0, 0.0);
		this.b2.rotateAboutCenter(Math.toRadians(-50));
		this.b2.setAngularVelocity(Math.toRadians(60.0 * 60.0));
		
		// perform one iteration
		this.world.step(1);
		
		// detect the time of impact
		TimeOfImpact toi = new TimeOfImpact();
		boolean missed = this.detector.getTimeOfImpact(this.b1, this.b2, 0.0, 1.0, toi);
		
		// no collision should have been detected
		TestCase.assertFalse(missed);
	}
}
