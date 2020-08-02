/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.dynamics;

import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Class to test the {@link AbstractPhysicsBody} class.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 */
public class AbstractPhysicsBodyTest {
	private class TestBody extends AbstractPhysicsBody {
		public TestBody() {
			super();
		}
		public TestBody(int fixtureCount) {
			super(fixtureCount);
		}
	}
	
	/**
	 * Tests the constructor.
	 */
	@Test
	public void create() {
		TestBody b = new TestBody();
		
		// these field should be defaulted
		TestCase.assertEquals(PhysicsBody.DEFAULT_ANGULAR_DAMPING, b.angularDamping);
		TestCase.assertEquals(0.0, b.angularVelocity);
		TestCase.assertFalse(b.atRest);
		TestCase.assertTrue(b.atRestDetectionEnabled);
		TestCase.assertEquals(0.0, b.atRestTime);
		TestCase.assertFalse(b.bullet);
		TestCase.assertNotNull(b.force);
		TestCase.assertNotNull(b.forces);
		TestCase.assertEquals(1.0, b.gravityScale);
		TestCase.assertEquals(PhysicsBody.DEFAULT_LINEAR_DAMPING, b.linearDamping);
		TestCase.assertNotNull(b.mass);
		TestCase.assertEquals(0.0, b.torque);
		TestCase.assertNotNull(b.torques);
		TestCase.assertNotNull(b.transform0);
		TestCase.assertNotNull(b.linearVelocity);
		
		b = new TestBody(3);
		b = new TestBody(-3);
		b = new TestBody(0);
	}
	
	/**
	 * Tests the accumulate method.
	 */
	@Test
	public void accumulate() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(1.0));
		b.setMass(MassType.NORMAL);
		
		b.applyForce(new Vector2(0.0, -2.0), new Vector2(1.0, -0.3));
		// just use the default elapsed time
		b.accumulate(Settings.DEFAULT_STEP_FREQUENCY);
		
		TestCase.assertFalse(b.force.isZero());
		TestCase.assertFalse(0.0 == b.torque);
		TestCase.assertTrue(b.forces.isEmpty());
		TestCase.assertTrue(b.torques.isEmpty());
		TestCase.assertFalse(b.isAtRest());
	}

	/**
	 * Tests adding a valid fixture.
	 */
	@Test
	public void addFixtureByConvexBodyFixture() {
		TestBody b = new TestBody();
		
		b.addFixture(Geometry.createCircle(1.0));
		
		TestCase.assertEquals(1, b.getFixtureCount());
		BodyFixture bf = b.getFixture(0);
		TestCase.assertEquals(BodyFixture.DEFAULT_DENSITY, bf.getDensity());
		TestCase.assertEquals(BodyFixture.DEFAULT_FRICTION, bf.getFriction());
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION, bf.getRestitution());
		
		b.addFixture(new BodyFixture(Geometry.createEquilateralTriangle(2.0)));
		
		TestCase.assertEquals(2, b.getFixtureCount());
		bf = b.getFixture(1);
		TestCase.assertEquals(BodyFixture.DEFAULT_DENSITY, bf.getDensity());
		TestCase.assertEquals(BodyFixture.DEFAULT_FRICTION, bf.getFriction());
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION, bf.getRestitution());
	}
	
	/**
	 * Tests adding a fixture with density, friction and restitution values.
	 */
	@Test
	public void addFixtureWithDensityFrictionRestitution() {
		TestBody b = new TestBody();
		
		b.addFixture(Geometry.createCircle(1.0), 2.0, 0.5, 0.4);
		
		TestCase.assertEquals(1, b.getFixtureCount());
		BodyFixture bf = b.getFixture(0);
		TestCase.assertEquals(2.0, bf.getDensity());
		TestCase.assertEquals(0.5, bf.getFriction());
		TestCase.assertEquals(0.4, bf.getRestitution());
		
		b.addFixture(Geometry.createCircle(1.0), 0.1);
		
		TestCase.assertEquals(2, b.getFixtureCount());
		bf = b.getFixture(1);
		TestCase.assertEquals(0.1, bf.getDensity());
		TestCase.assertEquals(BodyFixture.DEFAULT_FRICTION, bf.getFriction());
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION, bf.getRestitution());
	}

	/**
	 * Tests adding a fixture using a null convex shape.
	 */
	@Test(expected = NullPointerException.class)
	public void addNullConvex() {
		TestBody b = new TestBody();
		b.addFixture((Convex) null);
	}
	
	/**
	 * Tests adding a fixture using a null convex shape.
	 */
	@Test(expected = NullPointerException.class)
	public void addNullConvex2() {
		TestBody b = new TestBody();
		b.addFixture((Convex) null, 0.2);
	}

	/**
	 * Tests adding a fixture using a null convex shape.
	 */
	@Test(expected = NullPointerException.class)
	public void addNullConvex3() {
		TestBody b = new TestBody();
		b.addFixture((Convex) null, 0.2, 0.2, 0.3);
	}
	
	/**
	 * Tests adding a null fixture.
	 */
	@Test(expected = NullPointerException.class)
	public void addNullFixture() {
		TestBody b = new TestBody();
		b.addFixture((BodyFixture)null);
	}

	/**
	 * Tests the apply methods for forces and torques.
	 */
	@Test
	public void apply() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(1.0));
		b.setMass(MassType.NORMAL);
		
		// all the methods should add forces/torques to
		// an accumulator, wake up the body, and not
		// modify the force/torque values
		
		// test the apply force method
		b.setAtRest(true);
		b.applyForce(new Force(new Vector2(0.0, 2.0)));
		TestCase.assertEquals(1, b.forces.size());
		TestCase.assertTrue(b.force.isZero());
		TestCase.assertFalse(b.isAtRest());
		
		// test the apply force vector method
		b.setAtRest(true);
		b.applyForce(new Vector2(0.0, 2.0));
		TestCase.assertEquals(1, b.forces.size());
		TestCase.assertTrue(b.force.isZero());
		TestCase.assertFalse(b.isAtRest());
		
		// test the apply force at point method
		b.setAtRest(true);
		b.applyForce(new Vector2(0.0, 2.0), new Vector2(0.0, 0.5));
		TestCase.assertEquals(1, b.forces.size());
		TestCase.assertEquals(1, b.torques.size());
		TestCase.assertTrue(b.force.isZero());
		TestCase.assertEquals(0.0, b.torque);
		TestCase.assertFalse(b.isAtRest());
		
		// test the apply force at point method (at COM)
		b.setAtRest(true);
		b.applyForce(new Vector2(0.0, 2.0), b.getWorldCenter());
		TestCase.assertEquals(1, b.forces.size());
		TestCase.assertEquals(0, b.torques.size());
		TestCase.assertTrue(b.force.isZero());
		TestCase.assertEquals(0.0, b.torque);
		TestCase.assertFalse(b.isAtRest());
		
		// test the apply torque method
		b.setAtRest(true);
		b.applyTorque(new Torque(0.4));
		TestCase.assertEquals(1, b.torques.size());
		TestCase.assertEquals(0.0, b.torque);
		TestCase.assertFalse(b.isAtRest());
		
		// test the apply torque value method
		b.setAtRest(true);
		b.applyTorque(0.4);
		TestCase.assertEquals(1, b.torques.size());
		TestCase.assertEquals(0.0, b.torque);
		TestCase.assertFalse(b.isAtRest());
		
		// test applying a force to a zero mass body
		b = new TestBody();
		b.applyForce(new Force(1.0, 1.0));
		TestCase.assertEquals(0, b.forces.size());
		TestCase.assertTrue(b.force.isZero());
		
		// test applying a force to a zero mass body
		b = new TestBody();
		b.applyTorque(new Torque(4.0));
		TestCase.assertEquals(0, b.torques.size());
		TestCase.assertEquals(0.0, b.torque);
	}

	/**
	 * Test the successful use of the applyImpulse methods.
	 */
	@Test
	public void applyImpulse() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createRectangle(1.0, 1.0));
		b.setMass(MassType.NORMAL);
		
		double m = b.getMass().getMass();
		double i = b.getMass().getInertia();
		
		// should yield a velocity of 1.0, 1.0
		b.applyImpulse(new Vector2(m, m));
		TestCase.assertEquals(1.0, b.getLinearVelocity().x);
		TestCase.assertEquals(1.0, b.getLinearVelocity().y);
		
		// clear velocity
		b.getLinearVelocity().zero();
		
		// should yield an angular velocity of 1.0 rads
		b.applyImpulse(i);
		TestCase.assertEquals(1.0, b.getAngularVelocity());
		
		// clear angular velocity
		b.setAngularVelocity(0.0);
		
		// should yield a velocity of 1.0, 1.0 and an angular velocity of 1.0
		b.applyImpulse(new Vector2(0.0, i), new Vector2(1.0, 0.0));
		TestCase.assertEquals(1.0, b.getAngularVelocity());
		TestCase.assertEquals(i, b.getLinearVelocity().y);
		TestCase.assertEquals(0.0, b.getLinearVelocity().x);
		
		// test applying impulses to a zero mass/inertia body
		b = new TestBody();
		b.applyImpulse(0.2);
		TestCase.assertEquals(0.0, b.angularVelocity);
		
		b.applyImpulse(new Vector2(2.0, 1.0));
		TestCase.assertEquals(0.0, b.linearVelocity.x);
		TestCase.assertEquals(0.0, b.linearVelocity.y);
		
		b.applyImpulse(new Vector2(2.0, 1.0), new Vector2(2.0, 3.0));
		TestCase.assertEquals(0.0, b.angularVelocity);
		TestCase.assertEquals(0.0, b.linearVelocity.x);
		TestCase.assertEquals(0.0, b.linearVelocity.y);
	}
	
	/**
	 * Tests the apply method for a null force.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullForce() {
		TestBody b = new TestBody();
		b.applyForce((Force) null);
	}
	
	/**
	 * Tests the apply method for a null force.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullForceVector() {
		TestBody b = new TestBody();
		b.applyForce((Vector2) null);
	}
	
	/**
	 * Tests the apply method for a null torque.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullTorque() {
		TestBody b = new TestBody();
		b.applyTorque((Torque) null);
	}
	
	/**
	 * Tests the apply method for a null force
	 * at a point.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullForceAtPoint() {
		TestBody b = new TestBody();
		b.applyForce(null, new Vector2(0.0, 0.0));
	}
	
	/**
	 * Tests the apply method for a force at a
	 * null point.
	 */
	@Test(expected = NullPointerException.class)
	public void applyForceAtNullPoint() {
		TestBody b = new TestBody();
		b.applyForce(new Vector2(0.0, 1.0), null);
	}

	/**
	 * Tests passing a null impulse.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullImpulse() {
		TestBody b = new TestBody();
		b.applyImpulse(null);
	}
	
	/**
	 * Tests passing a null impulse.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullImpulse2() {
		TestBody b = new TestBody();
		b.applyImpulse(null, new Vector2());
	}
	
	/**
	 * Tests passing a null point.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullImpulsePoint() {
		TestBody b = new TestBody();
		b.applyImpulse(new Vector2(), null);
	}

	/**
	 * Tests the clearXXX methods.
	 */
	@Test
	public void clear() {
		TestBody b = new TestBody();
		
		// this should apply both a force and torque
		b.applyForce(new Vector2(0.0, 1.0), new Vector2(2.0, 1.0));
		b.force.add(new Vector2(-1.0, 0.5));
		b.torque = 0.4;
		
		b.clearForce();
		TestCase.assertTrue(b.force.isZero());
		
		b.clearTorque();
		TestCase.assertEquals(0.0, b.torque);
		
		b.clearAccumulatedForce();
		TestCase.assertTrue(b.forces.isEmpty());
		
		b.clearAccumulatedTorque();
		TestCase.assertTrue(b.torques.isEmpty());
	}
	
	/**
	 * Tests the createSweptAABB methods.
	 */
	@Test
	public void createSweptAABB() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(0.5));
		
		// must do this so that the radius is set
		// must have the radius set so that the swept
		// AABB is computed properly
		b.setMass(MassType.INFINITE);
		
		b.translate(2.0, -2.0);
		
		AABB aabb = b.createSweptAABB();
		TestCase.assertEquals(-0.5, aabb.getMinX());
		TestCase.assertEquals( 2.5, aabb.getMaxX());
		TestCase.assertEquals(-2.5, aabb.getMinY());
		TestCase.assertEquals( 0.5, aabb.getMaxY());
		
		Transform tx1 = new Transform();
		Transform tx2 = new Transform(); tx2.translate(-2.0, 0.0);
		aabb = b.createSweptAABB(tx1, tx2);
		TestCase.assertEquals(-2.5, aabb.getMinX());
		TestCase.assertEquals( 0.5, aabb.getMaxX());
		TestCase.assertEquals(-0.5, aabb.getMinY());
		TestCase.assertEquals( 0.5, aabb.getMaxY());
	}

	/**
	 * Tests the create get accumulated force method.
	 * @since 3.0.2
	 */
	@Test
	public void getAccumulatedForce() {
		TestBody b = new TestBody();
		
		// note: if the body has zero mass applyForce will do nothing, so we add a fixture with some mass/inertia
		b.addFixture(Geometry.createCircle(1.0));
		b.setMass(MassType.NORMAL);
		
		// no force applied yet
		Vector2 f = b.getAccumulatedForce();
		TestCase.assertEquals(0.0, f.x);
		TestCase.assertEquals(0.0, f.y);
		
		// one force
		b.applyForce(new Vector2(1.0, 0.0));
		f = b.getAccumulatedForce();
		TestCase.assertEquals(1.0, f.x);
		TestCase.assertEquals(0.0, f.y);
		
		// two forces
		b.applyForce(new Vector2(0.5, 2.0));
		f = b.getAccumulatedForce();
		TestCase.assertEquals(1.5, f.x);
		TestCase.assertEquals(2.0, f.y);
		
		// two forces and a force at point
		b.applyForce(new Vector2(0.5, 0.0), new Vector2(0.5, 0.0));
		f = b.getAccumulatedForce();
		TestCase.assertEquals(2.0, f.x);
		TestCase.assertEquals(2.0, f.y);
		
		// just a torque shouldn't affect the force
		b.applyTorque(0.5);
		f = b.getAccumulatedForce();
		TestCase.assertEquals(2.0, f.x);
		TestCase.assertEquals(2.0, f.y);
	}
	
	/**
	 * Tests the create get accumulated torque method.
	 * @since 3.0.2
	 */
	@Test
	public void getAccumulatedTorque() {
		TestBody b = new TestBody();
		
		// note: if the body has zero inertia applyTorque will do nothing, so we add a fixture with some mass/inertia
		b.addFixture(Geometry.createCircle(1.0));
		b.setMass(MassType.NORMAL);
		
		// no torque applied yet
		double t = b.getAccumulatedTorque();
		TestCase.assertEquals(0.0, t);
		
		// one torque
		b.applyTorque(0.5);
		t = b.getAccumulatedTorque();
		TestCase.assertEquals(0.5, t);
		
		// two torques
		b.applyTorque(0.5);
		t = b.getAccumulatedTorque();
		TestCase.assertEquals(1.0, t);
		
		// a force shouldn't affect the torque
		b.applyForce(new Vector2(0.5, 0.0));
		t = b.getAccumulatedTorque();
		TestCase.assertEquals(1.0, t);
	}

	/**
	 * Tests the get/set linear damping methods.
	 */
	@Test
	public void getSetLinearDamping() {
		TestBody b = new TestBody();
		
		b.setLinearDamping(1.0);
		TestCase.assertEquals(1.0, b.getLinearDamping());
		
		b.setLinearDamping(0.0);
		TestCase.assertEquals(0.0, b.getLinearDamping());
	}

	/**
	 * Tests the set linear damping method.
	 */
	@Test
	public void getSetAngularDamping() {
		TestBody b = new TestBody();
		
		b.setAngularDamping(1.0);
		TestCase.assertEquals(1.0, b.getAngularDamping());
		
		b.setAngularDamping(0.0);
		TestCase.assertEquals(0.0, b.getAngularDamping());
	}
	
	/**
	 * Tests the set linear damping method passing a 
	 * negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeLinearDamping() {
		TestBody b = new TestBody();
		b.setLinearDamping(-1.0);
	}
	
	/**
	 * Tests the set angular damping method passing a 
	 * negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeAngularDamping() {
		TestBody b = new TestBody();
		b.setAngularDamping(-2.0 * Math.PI);
	}

	/**
	 * Tests the get/set linear velocity methods.
	 */
	@Test
	public void getSetLinearVelocity() {
		TestBody b = new TestBody();
		
		b.setLinearVelocity(new Vector2(1.0, 2.0));
		TestCase.assertEquals(1.0, b.getLinearVelocity().x);
		TestCase.assertEquals(2.0, b.getLinearVelocity().y);
		
		b.setLinearVelocity(-4.0, 5.0);
		TestCase.assertEquals(-4.0, b.getLinearVelocity().x);
		TestCase.assertEquals(5.0, b.getLinearVelocity().y);
	}
	
	/**
	 * Tests the setLinearVelocity method with a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullLinearVelocity() {
		TestBody b = new TestBody();
		b.setLinearVelocity(null);
	}
	
	/**
	 * Tests the get/set angular velocity methods.
	 */
	@Test
	public void getSetAngularVelocity() {
		TestBody b = new TestBody();
		
		b.setAngularVelocity(2.0);
		TestCase.assertEquals(2.0, b.getAngularVelocity());
		
		b.setAngularVelocity(-3.0);
		TestCase.assertEquals(-3.0, b.getAngularVelocity());
		
		b.setAngularVelocity(0.0);
		TestCase.assertEquals(0.0, b.getAngularVelocity());
	}

	/**
	 * Tests the getChangeInPosition method.
	 * @since 3.1.5
	 */
	@Test
	public void getChangeInPosition() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(0.5));
		
		b.getInitialTransform().set(b.getTransform());
		
		// test moving just right
		b.translate(2.0, 0.0);
		Vector2 dp = b.getChangeInPosition();
		TestCase.assertEquals(2.000, dp.x, 1.0e-3);
		TestCase.assertEquals(0.000, dp.y, 1.0e-3);
		
		// test moving just left
		b.getTransform().identity();
		b.translate(-2.0, 0.0);
		dp = b.getChangeInPosition();
		TestCase.assertEquals(-2.000, dp.x, 1.0e-3);
		TestCase.assertEquals(0.000, dp.y, 1.0e-3);
		
		// test moving up and left
		b.getTransform().identity();
		b.translate(-2.0, 1.0);
		dp = b.getChangeInPosition();
		TestCase.assertEquals(-2.000, dp.x, 1.0e-3);
		TestCase.assertEquals(1.000, dp.y, 1.0e-3);
		
		// test with a rotation mixed in
		b.getTransform().identity();
		b.rotate(Math.toRadians(30));
		b.translate(-2.0, 1.0);
		b.rotate(Math.toRadians(30), -2.0, 1.0);
		dp = b.getChangeInPosition();
		TestCase.assertEquals(-2.000, dp.x, 1.0e-3);
		TestCase.assertEquals(1.000, dp.y, 1.0e-3);
	}
	
	/**
	 * Tests the getChangeInOrientation method.
	 * @since 3.1.5
	 */
	@Test
	public void getChangeInOrientation() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(0.5));
		b.rotate(Math.toRadians(30));
		b.setAngularVelocity(Math.toRadians(1));
		b.setMass(MassType.INFINITE);
		
		b.getInitialTransform().set(b.getTransform());
		
		// TEST moving forward 20 degrees
		
		b.getTransform().identity();
		b.rotate(Math.toRadians(50));
		double da = b.getChangeInOrientation();
		TestCase.assertEquals(20.000, Math.toDegrees(da), 1.0e-3);
		
		// test moving forward 200 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(200));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(170.000, Math.toDegrees(da), 1.0e-3);
		
		// test moving forward 350 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(20));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(350.000, Math.toDegrees(da), 1.0e-3);
		
		// TEST reverse direction
		
		b.setAngularVelocity(Math.toRadians(-1));
		
		// test moving forward 20 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(50));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(-340.000, Math.toDegrees(da), 1.0e-3);
		
		// test moving forward 200 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(200));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(-190.000, Math.toDegrees(da), 1.0e-3);
		
		// test moving forward 350 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(20));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(-10.000, Math.toDegrees(da), 1.0e-3);
		
		// TEST zero angular velocity
		
		b.setAngularVelocity(0.0);
		
		// test moving forward 20 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(50));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(20.000, Math.toDegrees(da), 1.0e-3);
		
		// test moving forward 200 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(200));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(170.000, Math.toDegrees(da), 1.0e-3);
		
		// test moving backward 10 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(20));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(-10.000, Math.toDegrees(da), 1.0e-3);
		
		// test moving forward 350 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(350));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(-40.000, Math.toDegrees(da), 1.0e-3);
		
		// TEST initial negative orientation
		
		b.getTransform().identity();
		b.rotate(Math.toRadians(-30.0));
		b.getInitialTransform().set(b.getTransform());
		b.setAngularVelocity(1.0);
		
		// test moving forward 20 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(50));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(80.000, Math.toDegrees(da), 1.0e-3);
		
		// test moving forward 200 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(200));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(230.000, Math.toDegrees(da), 1.0e-3);
		
		// test moving forward 350 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(20));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(50.000, Math.toDegrees(da), 1.0e-3);
		
		// reverse direction
		b.setAngularVelocity(Math.toRadians(-1));
		
		// test moving forward 20 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(50));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(-280.000, Math.toDegrees(da), 1.0e-3);
		
		// test moving forward 200 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(200));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(-130.000, Math.toDegrees(da), 1.0e-3);
		
		// test moving forward 350 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(20));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(-310.000, Math.toDegrees(da), 1.0e-3);
		
		// zero angular velocity
		b.setAngularVelocity(0.0);
		
		// test moving forward 20 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(50));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(80.000, Math.toDegrees(da), 1.0e-3);
		
		// test moving forward 200 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(200));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(-130.000, Math.toDegrees(da), 1.0e-3);
		
		// test moving backward 10 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(20));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(50.000, Math.toDegrees(da), 1.0e-3);
		
		// test moving forward 350 degrees
		b.getTransform().identity();
		b.rotate(Math.toRadians(350));
		da = b.getChangeInOrientation();
		TestCase.assertEquals(20.000, Math.toDegrees(da), 1.0e-3);
		
		// test no change
		b = new TestBody();
		da = b.getChangeInOrientation();
		TestCase.assertEquals(0.000, da, 1.0e-3);
	}
	
	/**
	 * Tests the getForce method
	 */
	@Test
	public void getForce() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(0.5));
		b.setMass(MassType.NORMAL);
		
		TestCase.assertEquals(0.0, b.getForce().x);
		TestCase.assertEquals(0.0, b.getForce().y);
		
		b.applyForce(new Vector2(2.0, -4.0));
		
		TestCase.assertEquals(0.0, b.getForce().x);
		TestCase.assertEquals(0.0, b.getForce().y);
		
		b.accumulate(Settings.DEFAULT_STEP_FREQUENCY);
		
		TestCase.assertEquals(2.0, b.getForce().x);
		TestCase.assertEquals(-4.0, b.getForce().y);
		
		// test that changing the returned vector doesn't do anything
		b.getForce().x = 0.0;
		b.getForce().y = 3.0;
		
		TestCase.assertEquals(2.0, b.getForce().x);
		TestCase.assertEquals(-4.0, b.getForce().y);
	}
	
	/**
	 * Tests the getTorque method
	 */
	@Test
	public void getTorque() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(0.5));
		b.setMass(MassType.NORMAL);
		
		TestCase.assertEquals(0.0, b.getTorque());
		
		b.applyTorque(0.5);
		
		TestCase.assertEquals(0.0, b.getTorque());
		
		b.accumulate(Settings.DEFAULT_STEP_FREQUENCY);
		
		TestCase.assertEquals(0.5, b.getTorque());
	}
	
	/**
	 * Tests the get/set gravity scale methods.
	 */
	@Test
	public void getSetGravityScale() {
		TestBody b = new TestBody();
		
		// test the default
		TestCase.assertEquals(1.0, b.getGravityScale());
		
		// should allow zero
		b.setGravityScale(0.0);
		TestCase.assertEquals(0.0, b.getGravityScale());
		
		// should allow negative
		b.setGravityScale(-1.0);
		TestCase.assertEquals(-1.0, b.getGravityScale());
		
		// should allow large/small values
		b.setGravityScale(100.0);
		TestCase.assertEquals(100.0, b.getGravityScale());
		b.setGravityScale(0.00001);
		TestCase.assertEquals(0.00001, b.getGravityScale());
	}
	
	/**
	 * Tests the getInitialTransform method.
	 */
	@Test
	public void getInitialTransform() {
		TestBody b = new TestBody();
		
		// test the default
		TestCase.assertNotNull(b.getInitialTransform());
		TestCase.assertTrue(b.getInitialTransform().isIdentity());
		
		b.translate(1.0, -1.0);
		
		TestCase.assertNotNull(b.getInitialTransform());
		TestCase.assertTrue(b.getInitialTransform().isIdentity());
		
		b.getInitialTransform().set(b.getTransform());
		
		TestCase.assertNotNull(b.getInitialTransform());
		TestCase.assertFalse(b.getInitialTransform().isIdentity());
		TestCase.assertEquals(1.0, b.getInitialTransform().getTranslationX());
		TestCase.assertEquals(-1.0, b.getInitialTransform().getTranslationY());
	}
	
	/**
	 * Tests the get local center method.
	 */
	@Test
	public void getLocalCenter() {
		TestBody b = new TestBody();
		
		// with no fixtures it's always the origin
		TestCase.assertEquals(0.0, b.getLocalCenter().x);
		TestCase.assertEquals(0.0, b.getLocalCenter().y);
		
		// adding a fixture doesn't change the local center
		b.addFixture(Geometry.createCircle(0.5));
		TestCase.assertEquals(0.0, b.getLocalCenter().x);
		TestCase.assertEquals(0.0, b.getLocalCenter().y);
		
		// setting the mass does, but in this case it won't
		// because the COM is the origin
		b.setMass(MassType.NORMAL);
		TestCase.assertEquals(0.0, b.getLocalCenter().x);
		TestCase.assertEquals(0.0, b.getLocalCenter().y);
		
		// now if we move the fixtures locally, then
		// the COM changes
		BodyFixture bf = b.addFixture(Geometry.createCircle(0.5));
		bf.getShape().translate(-0.5, 0.0);
		b.setMass(MassType.NORMAL);
		TestCase.assertEquals(-0.25, b.getLocalCenter().x);
		TestCase.assertEquals(0.0, b.getLocalCenter().y);
	}
	
	/**
	 * Tests the integrateVelocity method.
	 */
	@Test
	public void integrateVelocity() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(0.5));
		b.setMass(MassType.NORMAL);
		
		Vector2 g = new Vector2(0.0, -9.8);
		Settings s = new Settings();
		TimeStep ts = new TimeStep(s.getStepFrequency());
		
		b.integrateVelocity(g, ts, s);
		
		TestCase.assertEquals(0.0, b.linearVelocity.x);
		TestCase.assertEquals(g.y * s.getStepFrequency(), b.linearVelocity.y, 1e-9);
		TestCase.assertEquals(0.0, b.angularVelocity);
		
		// try with some linear damping
		b.setLinearDamping(0.1);
		b.integrateVelocity(g, ts, s);
		
		TestCase.assertEquals(0.0, b.linearVelocity.x);
		TestCase.assertTrue(2.0 * g.y * s.getStepFrequency() < b.linearVelocity.y);
		TestCase.assertEquals(0.0, b.angularVelocity);
		
		// try zero mass body
		b = new TestBody();
		b.setMass(MassType.NORMAL);
		
		b.integrateVelocity(g, ts, s);
		TestCase.assertEquals(0.0, b.linearVelocity.x);
		TestCase.assertEquals(0.0, b.linearVelocity.y);
		TestCase.assertEquals(0.0, b.angularVelocity);
		
		// try with infinite mass
		b = new TestBody();
		b.setMass(MassType.INFINITE);
		
		b.integrateVelocity(g, ts, s);
		TestCase.assertEquals(0.0, b.linearVelocity.x);
		TestCase.assertEquals(0.0, b.linearVelocity.y);
		TestCase.assertEquals(0.0, b.angularVelocity);
	}
	
	/**
	 * Tests the integrateVelocity method.
	 */
	@Test
	public void integratePosition() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(0.5));
		b.setMass(MassType.NORMAL);
		b.setLinearVelocity(new Vector2(0.0, -2.0));
		
		Settings s = new Settings();
		TimeStep ts = new TimeStep(s.getStepFrequency());
		
		b.integratePosition(ts, s);
		
		TestCase.assertEquals(0.0, b.getWorldCenter().x);
		TestCase.assertEquals(-2.0 * s.getStepFrequency(), b.getWorldCenter().y);
		TestCase.assertEquals(0.0, b.getTransform().getRotationAngle());
		
		// test large linear displacement
		b.setLinearVelocity(0.0, -400.0);
		b.integratePosition(ts, s);
		TestCase.assertEquals(0.0, b.getWorldCenter().x);
		TestCase.assertEquals(-2.0 * s.getStepFrequency() - s.getMaximumTranslation(), b.getWorldCenter().y);
		TestCase.assertTrue(-400.0 * s.getStepFrequency() < b.getWorldCenter().y);
		TestCase.assertEquals(0.0, b.getAngularVelocity());
		
		// test large angular displacement
		b.setLinearVelocity(0.0, 0.0);
		b.setAngularVelocity(s.getMaximumRotation() / s.getStepFrequency() * 10.0);
		b.integratePosition(ts, s);
		TestCase.assertEquals(s.getMaximumRotation(), b.getTransform().getRotationAngle());
		
		// test static
		b = new TestBody();
		b.setMass(MassType.INFINITE);
		
		b.integratePosition(ts, s);
		TestCase.assertEquals(0.0, b.getWorldCenter().x);
		TestCase.assertEquals(0.0, b.getWorldCenter().y);
		TestCase.assertEquals(0.0, b.getTransform().getRotationAngle());
	}
	
	/**
	 * Tests the updateAtRestTime method.
	 */
	@Test
	public void updateAtRestTime() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createCircle(0.5));
		b.setMass(MassType.NORMAL);
		
		Settings s = new Settings();
		TimeStep ts = new TimeStep(s.getStepFrequency());
		
		TestCase.assertEquals(0.0, b.atRestTime);
		
		// test normal operation
		TestCase.assertEquals(s.getStepFrequency(), b.updateAtRestTime(ts, s));
		TestCase.assertEquals(s.getStepFrequency(), b.atRestTime);
		
		TestCase.assertEquals(s.getStepFrequency() * 2.0, b.updateAtRestTime(ts, s));
		TestCase.assertEquals(s.getStepFrequency() * 2.0, b.atRestTime);
		
		// test linear velocity too high
		b.setLinearVelocity(s.getMaximumAtRestLinearVelocity() + 1.0, 0.0);
		TestCase.assertEquals(0.0, b.updateAtRestTime(ts, s));
		TestCase.assertEquals(0.0, b.atRestTime);
		b.setLinearVelocity(0, 0);
		
		// do another normal op
		TestCase.assertEquals(s.getStepFrequency(), b.updateAtRestTime(ts, s));
		TestCase.assertEquals(s.getStepFrequency(), b.atRestTime);
		
		// test angular velocity too high
		b.setAngularVelocity(s.getMaximumAtRestAngularVelocity() + 1.0);
		TestCase.assertEquals(0.0, b.updateAtRestTime(ts, s));
		TestCase.assertEquals(0.0, b.atRestTime);
		b.setAngularVelocity(0.0);
		
		// test with a static body
		b.setMassType(MassType.INFINITE);
		TestCase.assertEquals(-1.0, b.updateAtRestTime(ts, s));
		TestCase.assertEquals(0.0, b.atRestTime);
		
		// test at-rest detection turned off
		b.setAtRestDetectionEnabled(false);
		TestCase.assertEquals(0.0, b.updateAtRestTime(ts, s));
		TestCase.assertEquals(0.0, b.atRestTime);
	}
	
	/**
	 * Tests the is/set bullet methods.
	 */
	@Test
	public void getSetBullet() {
		TestBody b = new TestBody();
		
		TestCase.assertFalse(b.isBullet());
		
		b.setBullet(true);
		TestCase.assertTrue(b.isBullet());
		
		b.setBullet(false);
		TestCase.assertFalse(b.isBullet());
	}
	
	/**
	 * Tests the set mass methods.
	 */
	@Test
	public void setMass() {
		TestBody b = new TestBody();
		
		// test setting the mass with no fixtures
		b.setMass(MassType.INFINITE);
		TestCase.assertNotNull(b.mass);
		// make sure its not infinite (the mass and inertia will still be zero however)
		TestCase.assertTrue(b.mass.isInfinite());
		TestCase.assertTrue(b.mass.getCenter().isZero());
		TestCase.assertEquals(0.0, b.mass.getMass());
		TestCase.assertEquals(0.0, b.mass.getInverseMass());
		TestCase.assertEquals(0.0, b.mass.getInertia());
		TestCase.assertEquals(0.0, b.mass.getInverseInertia());
		
		// test setting with one fixture
		// it should not be infinite
		BodyFixture f1 = b.addFixture(Geometry.createUnitCirclePolygon(5, 0.5));
		b.setMass(MassType.NORMAL);
		TestCase.assertNotNull(b.mass);
		TestCase.assertFalse(b.mass.isInfinite());
		
		// test setting with multiple fixtures
		// it should not be infinite and should not be
		// equal to either shapes mass
		BodyFixture f2 = b.addFixture(Geometry.createIsoscelesTriangle(1.0, 1.0));
		b.setMass(MassType.NORMAL);
		TestCase.assertNotNull(b.mass);
		TestCase.assertFalse(b.mass.isInfinite());
		Mass m1 = f1.createMass();
		Mass m2 = f2.createMass();
		TestCase.assertFalse(m1.equals(b.mass));
		TestCase.assertFalse(m2.equals(b.mass));

		// test setting the mass with a flag
		// make sure the type of mass is correct but the
		// values of the mass and 
		b.setMass(MassType.INFINITE);
		TestCase.assertNotNull(b.mass);
		TestCase.assertTrue(b.mass.isInfinite());
		TestCase.assertEquals(0.0, b.mass.getMass());
		TestCase.assertEquals(0.0, b.mass.getInverseMass());
		TestCase.assertEquals(0.0, b.mass.getInertia());
		TestCase.assertEquals(0.0, b.mass.getInverseInertia());
		b.setMass(MassType.FIXED_ANGULAR_VELOCITY);
		TestCase.assertNotNull(b.mass);
		TestCase.assertEquals(0.0, b.mass.getInertia());
		TestCase.assertEquals(0.0, b.mass.getInverseInertia());
		b.setMass(MassType.FIXED_LINEAR_VELOCITY);
		TestCase.assertNotNull(b.mass);
		TestCase.assertEquals(0.0, b.mass.getMass());
		TestCase.assertEquals(0.0, b.mass.getInverseMass());
		
		// test setting the mass directly
		Mass mass = new Mass(f2.getShape().getCenter(), 2.3, 20.3);
		b.setMass(mass);
		TestCase.assertSame(mass, b.mass);
		
		// test only setting the type
		b.setMassType(MassType.INFINITE);
		// make sure the mass was not recomputed
		TestCase.assertSame(mass, b.mass);
		// make sure the type was successfully set
		TestCase.assertEquals(MassType.INFINITE, b.mass.getType());
		// set it back to normal
		b.setMassType(MassType.NORMAL);
		// make sure the mass values are still present
		TestCase.assertEquals(2.3, b.mass.getMass());
		TestCase.assertEquals(20.3, b.mass.getInertia());
		
		// test the updateMass method
		b.updateMass();
		TestCase.assertEquals(MassType.NORMAL, b.mass.getType());
		TestCase.assertEquals(1.094, b.mass.getMass(), 1e-3);
		TestCase.assertEquals(0.105, b.mass.getInertia(), 1e-3);
	}
	
	/**
	 * Tests setting the mass type.
	 * @since 2.2.3
	 */
	@Test
	public void setMassType() {
		TestBody b = new TestBody();
		// should auto generate it
		b.setMassType(MassType.NORMAL);
		
		// test setMass(null);
		b = new TestBody();
		b.addFixture(Geometry.createCircle(0.5));
		b.setMass(MassType.NORMAL);
		
		TestCase.assertEquals(MassType.NORMAL, b.getMass().getType());
		
		b.setMass((MassType)null);
		
		TestCase.assertEquals(MassType.NORMAL, b.getMass().getType());
	}

	/**
	 * Tests setting the mass type to null.
	 * @since 3.1.0
	 */
	@Test(expected = NullPointerException.class)
	public void setNullMassType() {
		TestBody b = new TestBody();
		b.setMassType(null);
	}

	/**
	 * Tests setting the mass to null.
	 * @since 3.1.0
	 */
	@Test(expected = NullPointerException.class)
	public void setNullMass() {
		TestBody b = new TestBody();
		b.setMass((Mass)null);
	}

	/**
	 * Tests the is dynamic method.
	 */
	@Test
	public void isDynamic() {
		TestBody b = new TestBody();
		b.addFixture(Geometry.createIsoscelesTriangle(2.0, 1.0));
		b.setMass(MassType.NORMAL);
		
		TestCase.assertTrue(b.isDynamic());
		TestCase.assertFalse(b.isKinematic());
		TestCase.assertFalse(b.isStatic());
	}
	
	/**
	 * Tests the is dynamic method.
	 */
	@Test
	public void isKinematic() {
		TestBody b = new TestBody();
		
		b.setLinearVelocity(new Vector2(0.0, 2.0));
		TestCase.assertFalse(b.isDynamic());
		TestCase.assertTrue(b.isKinematic());
		TestCase.assertFalse(b.isStatic());
		
		b.angularVelocity = 0.4;
		TestCase.assertFalse(b.isDynamic());
		TestCase.assertTrue(b.isKinematic());
		TestCase.assertFalse(b.isStatic());
		
		b.linearVelocity.zero();
		b.angularVelocity = 0.4;
		TestCase.assertFalse(b.isDynamic());
		TestCase.assertTrue(b.isKinematic());
		TestCase.assertFalse(b.isStatic());
	}
	
	/**
	 * Tests the is dynamic method.
	 */
	@Test
	public void isStatic() {
		TestBody b = new TestBody();
		
		TestCase.assertFalse(b.isDynamic());
		TestCase.assertFalse(b.isKinematic());
		TestCase.assertTrue(b.isStatic());
	}
	
	/**
	 * Tests the at-rest flag methods.
	 */
	@Test
	public void getSetAtRest() {
		TestBody b = new TestBody();
		b.setAtRestDetectionEnabled(true);
		TestCase.assertTrue(b.isAtRestDetectionEnabled());
		b.setAtRestDetectionEnabled(false);
		TestCase.assertFalse(b.isAtRestDetectionEnabled());
		
		b.setAtRest(false);
		TestCase.assertFalse(b.isAtRest());
		
		// test that we can sleep the body even if the
		// auto sleep is not enabled.
		b.linearVelocity.x = -0.5;
		b.linearVelocity.y = 1.0;
		b.angularVelocity = 3.2;
		b.force.x = 1.0;
		b.torque = 1.2;
		b.applyTorque(0.3);
		b.applyForce(new Vector2(1.0, 1.0));
		b.setAtRest(true);
		TestCase.assertTrue(b.isAtRest());
		TestCase.assertTrue(b.forces.isEmpty());
		TestCase.assertTrue(b.torques.isEmpty());
		TestCase.assertEquals(1.2, b.torque);
		TestCase.assertEquals(1.0, b.force.x);
		TestCase.assertTrue(b.linearVelocity.isZero());
		TestCase.assertEquals(0.0, b.angularVelocity);
	}
	
	/**
	 * Tests the getVelocity at a given point on the body method.
	 * @since 3.0.1
	 */
	@Test
	public void getLinearVelocityAtPoint() {
		TestBody b = new TestBody();
		b.linearVelocity.set(5.0, 4.0);
		b.angularVelocity = Math.PI;
		b.addFixture(Geometry.createEquilateralTriangle(2.0));
		BodyFixture bf = b.addFixture(Geometry.createUnitCirclePolygon(5, 1.0));
		bf.getShape().translate(-1.0, 1.0);
		b.setMass(MassType.INFINITE);
		b.translateToOrigin();
		
		Vector2 p = new Vector2(-2.0, 1.0);
		
		Vector2 vp = b.getLinearVelocity(p);
		
		TestCase.assertEquals( 1.858, vp.x, 1.0E-3);
		TestCase.assertEquals(-2.283, vp.y, 1.0E-3);
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void tostring() {
		TestBody b = new TestBody();
		
		TestCase.assertNotNull(b.toString());
		
		b.addFixture(Geometry.createCircle(0.5));
		b.addFixture(Geometry.createCircle(0.5));
		
		TestCase.assertNotNull(b.toString());
	}
}
