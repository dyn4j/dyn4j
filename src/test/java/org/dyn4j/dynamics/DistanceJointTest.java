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
package org.dyn4j.dynamics;

import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Used to test the {@link DistanceJoint} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.2
 */
public class DistanceJointTest extends AbstractJointTest {
	/**
	 * Tests the successful creation case.
	 */
	@Test
	public void createWithTwoDifferentBodies() {
		new DistanceJoint(b1, b2, new Vector2(), new Vector2());
	}

	/**
	 * Tests the failed creation passing null body1.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody1() {
		new DistanceJoint(null, b2, new Vector2(), new Vector2());
	}

	/**
	 * Tests the failed creation passing null body2.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullBody2() {
		new DistanceJoint(b1, null, new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor1.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor1Point() {
		new DistanceJoint(b1, b2, null, new Vector2());
	}
	
	/**
	 * Tests the create method passing a null anchor2.
	 */
	@Test(expected = NullPointerException.class)
	public void createWithNullAnchor2Point() {
		new DistanceJoint(b1, b2, new Vector2(), null);
	}
	
	/**
	 * Tests the create method passing the same body.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createWithSameBody() {
		new DistanceJoint(b1, b1, new Vector2(), new Vector2());
	}
	
	/**
	 * Tests the isSpring method.
	 */
	@Test
	public void isSpring() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		TestCase.assertFalse(dj.isSpring());
		
		dj.setFrequency(0.0);
		TestCase.assertFalse(dj.isSpring());
		
		dj.setFrequency(1.0);
		TestCase.assertTrue(dj.isSpring());
		
		dj.setFrequency(15.24);
		TestCase.assertTrue(dj.isSpring());
		
		dj.setFrequency(0.0);
		TestCase.assertFalse(dj.isSpring());
	}

	/**
	 * Tests the isSpringDamper method.
	 */
	@Test
	public void isSpringDamper() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		TestCase.assertFalse(dj.isSpringDamper());
		
		dj.setFrequency(0.0);
		TestCase.assertFalse(dj.isSpringDamper());
		
		dj.setFrequency(1.0);
		TestCase.assertFalse(dj.isSpringDamper());
		
		dj.setFrequency(15.24);
		TestCase.assertFalse(dj.isSpringDamper());
		
		dj.setDampingRatio(0.4);
		TestCase.assertTrue(dj.isSpringDamper());
		
		dj.setDampingRatio(0.0);
		TestCase.assertFalse(dj.isSpringDamper());
		
		dj.setDampingRatio(0.61);
		dj.setFrequency(0.0);
		TestCase.assertFalse(dj.isSpringDamper());
	}
	
	/**
	 * Tests valid distance values.
	 */
	@Test
	public void setPositiveDistance() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		dj.setDistance(0.0);
		TestCase.assertEquals(0.0, dj.getDistance());
		
		dj.setDistance(1.0);
		TestCase.assertEquals(1.0, dj.getDistance());
	}
	
	/**
	 * Tests a negative distance value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDistance() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setDistance(-2.0);
	}
	
	/**
	 * Tests valid damping ratio values.
	 */
	@Test
	public void setDampingRatio() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setDampingRatio(0.0);
		TestCase.assertEquals(0.0, dj.getDampingRatio());
		
		dj.setDampingRatio(1.0);
		TestCase.assertEquals(1.0, dj.getDampingRatio());
		
		dj.setDampingRatio(0.2);
		TestCase.assertEquals(0.2, dj.getDampingRatio());
	}
	
	/**
	 * Tests a negative damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDampingRatio() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setDampingRatio(-1.0);
	}
	
	/**
	 * Tests a greater than one damping ratio value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setDampingRatioGreaterThan1() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setDampingRatio(2.0);
	}
	
	/**
	 * Tests valid frequency values.
	 */
	@Test
	public void setFrequency() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		dj.setFrequency(0.0);
		TestCase.assertEquals(0.0, dj.getFrequency());
		
		dj.setFrequency(1.0);
		TestCase.assertEquals(1.0, dj.getFrequency());
		
		dj.setFrequency(29.0);
		TestCase.assertEquals(29.0, dj.getFrequency());
	}
	
	/**
	 * Tests a negative frequency value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeFrequency() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		dj.setFrequency(-0.3);
	}

	/**
	 * Tests the body's sleep state when changing the distance.
	 */
	@Test
	public void setDistanceSleep() {
		DistanceJoint dj = new DistanceJoint(b1, b2, new Vector2(1.0, 2.0), new Vector2(-3.0, 0.5));
		
		double distance = dj.getDistance();
		
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(distance, dj.getDistance());
		
		b1.setAsleep(true);
		b2.setAsleep(true);
		
		// set the distance to the same value
		dj.setDistance(distance);
		TestCase.assertTrue(b1.isAsleep());
		TestCase.assertTrue(b2.isAsleep());
		TestCase.assertEquals(distance, dj.getDistance());
		
		// set the distance to a different value and make
		// sure the bodies are awakened
		dj.setDistance(10);
		TestCase.assertFalse(b1.isAsleep());
		TestCase.assertFalse(b2.isAsleep());
		TestCase.assertEquals(10.0, dj.getDistance());
	}
	

	/**
	 * Tests the body separation as enforced by the distance joint.
	 */
	@Test
	public void simulateDistanceChange() {
		World w = new World();
		// take gravity out the picture
		w.setGravity(World.ZERO_GRAVITY);
		
		// take friction and damping out of the picture
		
		Body g = new Body();
		BodyFixture gf = g.addFixture(Geometry.createRectangle(10.0, 0.5));
		gf.setFriction(0.0);
		g.setMass(MassType.INFINITE);
		g.setLinearDamping(0.0);
		g.setAngularDamping(0.0);
		w.addBody(g);
		
		Body b = new Body();
		BodyFixture bf = b.addFixture(Geometry.createCircle(0.5));
		bf.setFriction(0.0);
		b.setMass(MassType.NORMAL);
		b.translate(0.0, 2.0);
		b.setLinearDamping(0.0);
		b.setAngularDamping(0.0);
		w.addBody(b);
		
		DistanceJoint dj = new DistanceJoint(g, b, g.getWorldCenter(), b.getWorldCenter());
		
		dj.setDistance(10.0);
		w.addJoint(dj);
		
		Vector2 v1 = g.getWorldCenter();
		Vector2 v2 = b.getWorldCenter();
		TestCase.assertTrue(v1.distance(v2) < dj.getDistance());
		
		// the way the distance joint is currently working is that it will immediately try to solve
		// it to be the correct distance apart, but the position solver is bound by the default
		// correction factor, which is 0.2m. The world is also specified to run 10 position solving
		// iterations as well so this accounts for +2m difference each iteration, so after 4 iterations
		// we should be at 10m (we'll set these defaults below just in case they change in the future)
		w.getSettings().setMaximumLinearCorrection(0.2);
		w.getSettings().setPositionConstraintSolverIterations(10);
		w.step(4);
		
		v1 = g.getWorldCenter();
		v2 = b.getWorldCenter();
		TestCase.assertEquals(v1.distance(v2), dj.getDistance(), 1e-5);	
	}
}
