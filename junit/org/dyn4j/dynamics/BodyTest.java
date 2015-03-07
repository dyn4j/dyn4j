/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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

import java.util.List;

import junit.framework.TestCase;

import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.collision.narrowphase.Sat;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.Force;
import org.dyn4j.dynamics.Torque;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactEdge;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.JointEdge;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

/**
 * Class to test the {@link Body} class.
 * @author William Bittle
 * @version 3.1.8
 * @since 1.0.2
 */
public class BodyTest {
	/**
	 * Tests the constructor.
	 */
	@Test
	public void create() {
		Body b = new Body();
		
		// these field should be defaulted
		TestCase.assertNotNull(b.getId());
		TestCase.assertNotNull(b.contacts);
		TestCase.assertNotNull(b.getFixtures());
		TestCase.assertNotNull(b.force);
		TestCase.assertNotNull(b.forces);
		TestCase.assertNotNull(b.joints);
		TestCase.assertNotNull(b.mass);
		TestCase.assertNotNull(b.torques);
		TestCase.assertNotNull(b.getTransform());
		TestCase.assertNotNull(b.transform0);
		TestCase.assertNotNull(b.velocity);
		TestCase.assertNull(b.world);
	}
	
	/**
	 * Tests adding a fixture using a null convex shape.
	 */
	@Test(expected = NullPointerException.class)
	public void addNullConvex() {
		Body b = new Body();
		b.addFixture((Convex) null);
	}
	
	/**
	 * Tests adding a null fixture.
	 */
	@Test(expected = NullPointerException.class)
	public void addNullFixture() {
		Body b = new Body();
		b.addFixture((BodyFixture)null);
	}
	
	/**
	 * Tests the remove fixture methods.
	 */
	@Test
	public void removeFixtureFound() {
		Body b = new Body();
		BodyFixture f = b.addFixture(Geometry.createCircle(1.0));
		
		// test removing the fixture
		boolean success = b.removeFixture(f);
		TestCase.assertEquals(0, b.getFixtures().size());
		TestCase.assertTrue(success);
		
		b.addFixture(f);
		BodyFixture f2 = b.addFixture(Geometry.createSquare(0.5));
		success = b.removeFixture(f);
		TestCase.assertEquals(1, b.getFixtures().size());
		TestCase.assertTrue(f2 == b.getFixtures().get(0));
		TestCase.assertTrue(success);
		
		// test removing by index
		f = b.addFixture(Geometry.createEquilateralTriangle(0.5));
		b.addFixture(Geometry.createRectangle(1.0, 2.0));
		f2 = b.removeFixture(1);
		
		TestCase.assertEquals(2, b.getFixtures().size());
		TestCase.assertTrue(f2 == f);
	}
	
	/**
	 * Tests the remove fixture method failure cases.
	 */
	@Test
	public void removeFixtureNotFound() {
		Body b = new Body();
		
		// test null fixture
		boolean success = b.removeFixture((BodyFixture)null);
		TestCase.assertFalse(success);
		
		// test not found fixture
		b.addFixture(Geometry.createCircle(1.0));
		success = b.removeFixture(new BodyFixture(Geometry.createRightTriangle(0.5, 0.3)));
		TestCase.assertFalse(success);
	}
	
	/**
	 * Tests getting fixtures using a world space point.
	 * @since 3.1.8
	 */
	@Test
	public void getFixtureByPoint() {
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0));
		BodyFixture bf = b.addFixture(Geometry.createUnitCirclePolygon(5, 1.0));
		bf.getShape().translate(0.5, 0);
		
		// test not in body
		bf = b.getFixture(new Vector2(-1.0, -1.0));
		TestCase.assertNull(bf);
		
		// confirm there are two fixtures at this location
		TestCase.assertEquals(2, b.getFixtures(new Vector2(0.5, 0.25)).size());
		
		// test getting the first one
		bf = b.getFixture(new Vector2(0.5, 0.25));
		TestCase.assertNotNull(bf);
		TestCase.assertTrue(bf.getShape() instanceof Circle);
		
		// test not in body
		List<BodyFixture> bfs = b.getFixtures(new Vector2(-1.0, -1.0));
		TestCase.assertNotNull(bfs);
		TestCase.assertEquals(0, bfs.size());
		
		// test in body remove one
		bfs = b.getFixtures(new Vector2(1.25, 0.10));
		TestCase.assertNotNull(bfs);
		TestCase.assertEquals(1, bfs.size());
		TestCase.assertTrue(bfs.get(0).getShape() instanceof Polygon);
		
		// test in body remove both
		bfs = b.getFixtures(new Vector2(0.75, 0.10));
		TestCase.assertNotNull(bfs);
		TestCase.assertEquals(2, bfs.size());
	}
	
	/**
	 * Tests removing fixtures by a world space point.
	 * @since 3.1.8
	 */
	@Test
	public void removeFixtureByPoint() {
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0));
		BodyFixture bf = b.addFixture(Geometry.createUnitCirclePolygon(5, 1.0));
		bf.getShape().translate(0.5, 0);
		
		// test not in body
		bf = b.removeFixture(new Vector2(-1.0, -1.0));
		TestCase.assertNull(bf);
		TestCase.assertEquals(2, b.getFixtures().size());
		
		// confirm there are two fixtures at this location
		TestCase.assertEquals(2, b.getFixtures(new Vector2(0.5, 0.25)).size());
		// test remove the first one
		bf = b.removeFixture(new Vector2(0.5, 0.25));
		TestCase.assertNotNull(bf);
		TestCase.assertTrue(bf.getShape() instanceof Circle);
		TestCase.assertEquals(1, b.getFixtures().size());
		
		// add the fixture back
		bf = b.addFixture(Geometry.createCircle(1.0));
		
		// test not in body
		List<BodyFixture> bfs = b.removeFixtures(new Vector2(-1.0, -1.0));
		TestCase.assertNotNull(bfs);
		TestCase.assertEquals(0, bfs.size());
		TestCase.assertEquals(2, b.getFixtures().size());
		
		// test in body remove one
		bfs = b.removeFixtures(new Vector2(1.25, 0.10));
		TestCase.assertNotNull(bfs);
		TestCase.assertEquals(1, bfs.size());
		TestCase.assertTrue(bfs.get(0).getShape() instanceof Polygon);
		TestCase.assertEquals(1, b.getFixtures().size());
		
		// add the fixture back
		bf = b.addFixture(Geometry.createUnitCirclePolygon(5, 1.0));
		bf.getShape().translate(0.5, 0);
		
		// test in body remove both
		bfs = b.removeFixtures(new Vector2(0.75, 0.10));
		TestCase.assertNotNull(bfs);
		TestCase.assertEquals(2, bfs.size());
		TestCase.assertEquals(0, b.getFixtures().size());
	}
	
	/**
	 * Tests receiving index out of bounds exceptions.
	 * @since 2.2.3
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void removeFixtureIndexOutOfBounds1() {
		Body b = new Body();
		// test index with empty fixture list
		b.removeFixture(0);
	}
	
	/**
	 * Tests receiving index out of bounds exceptions.
	 * @since 2.2.3
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void removeFixtureIndexOutOfBounds2() {
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0));
		
		// test negative index
		b.removeFixture(-2);
	}
	
	/**
	 * Tests receiving index out of bounds exceptions.
	 * @since 2.2.3
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void removeFixtureIndexOutOfBounds3() {
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0));
		
		// test positive index with null fixture list
		b.removeFixture(3);
	}
	
	/**
	 * Tests the removal of all fixtures.
	 * @since 3.0.2
	 */
	@Test
	public void removeAllFixtures() {
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0));
		b.addFixture(Geometry.createRectangle(1.0, 0.5));
		b.addFixture(Geometry.createSegment(new Vector2(1.0, -2.0)));
		
		TestCase.assertEquals(3, b.getFixtureCount());
		b.removeAllFixtures();
		TestCase.assertEquals(0, b.getFixtureCount());
	}
	
	/**
	 * Tests adding a valid fixture.
	 */
	@Test
	public void addFixture() {
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0));
		TestCase.assertEquals(1, b.getFixtureCount());
		
		b.addFixture(new BodyFixture(Geometry.createEquilateralTriangle(2.0)));
		TestCase.assertEquals(2, b.getFixtureCount());
	}
	
	/**
	 * Tests adding a fixture with density, friction and restitution values.
	 */
	@Test
	public void addFixtureDFR() {
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0), 2.0, 0.5, 0.4);
		TestCase.assertEquals(1, b.getFixtureCount());
		BodyFixture bf = b.getFixture(0);
		TestCase.assertEquals(2.0, bf.getDensity());
		TestCase.assertEquals(0.5, bf.getFriction());
		TestCase.assertEquals(0.4, bf.getRestitution());
	}
	
	/**
	 * Tests the set mass methods.
	 */
	@Test
	public void setMass() {
		Body b = new Body();
		
		// test setting the mass with no fixtures
		b.update();
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
		b.update(Mass.Type.NORMAL);
		TestCase.assertNotNull(b.mass);
		TestCase.assertFalse(b.mass.isInfinite());
		
		// test setting with multiple fixtures
		// it should not be infinite and should not be
		// equal to either shapes mass
		BodyFixture f2 = b.addFixture(Geometry.createIsoscelesTriangle(1.0, 1.0));
		b.update();
		TestCase.assertNotNull(b.mass);
		TestCase.assertFalse(b.mass.isInfinite());
		Mass m1 = f1.createMass();
		Mass m2 = f2.createMass();
		TestCase.assertFalse(m1.equals(b.mass));
		TestCase.assertFalse(m2.equals(b.mass));
		
		// test setting the mass with a flag
		// make sure the type of mass is correct but the
		// values of the mass and 
		b.update(Mass.Type.INFINITE);
		TestCase.assertNotNull(b.mass);
		TestCase.assertTrue(b.mass.isInfinite());
		TestCase.assertEquals(0.0, b.mass.getMass());
		TestCase.assertEquals(0.0, b.mass.getInverseMass());
		TestCase.assertEquals(0.0, b.mass.getInertia());
		TestCase.assertEquals(0.0, b.mass.getInverseInertia());
		b.update(Mass.Type.FIXED_ANGULAR_VELOCITY);
		TestCase.assertNotNull(b.mass);
		TestCase.assertEquals(0.0, b.mass.getInertia());
		TestCase.assertEquals(0.0, b.mass.getInverseInertia());
		b.update(Mass.Type.FIXED_LINEAR_VELOCITY);
		TestCase.assertNotNull(b.mass);
		TestCase.assertEquals(0.0, b.mass.getMass());
		TestCase.assertEquals(0.0, b.mass.getInverseMass());
		
		// test setting the mass directly
		Mass mass = new Mass(f2.getShape().getCenter(), 2.3, 20.3);
		b.update(mass);
		TestCase.assertSame(mass, b.mass);
		
		// test only setting the type
		b.setMassType(Mass.Type.INFINITE);
		// make sure the mass was not recomputed
		TestCase.assertSame(mass, b.mass);
		// make sure the type was successfully set
		TestCase.assertEquals(Mass.Type.INFINITE, b.mass.getType());
		// set it back to normal
		b.setMassType(Mass.Type.NORMAL);
		// make sure the mass values are still present
		TestCase.assertEquals(2.3, b.mass.getMass());
		TestCase.assertEquals(20.3, b.mass.getInertia());
	}
	
	/**
	 * Tests setting the mass type.
	 * @since 2.2.3
	 */
	@Test
	public void setMassType() {
		Body b = new Body();
		// should auto generate it
		b.setMassType(Mass.Type.NORMAL);
	}

	/**
	 * Tests setting the mass type to null.
	 * @since 3.1.0
	 */
	@Test(expected = NullPointerException.class)
	public void setNullMassType() {
		Body b = new Body();
		b.setMassType(null);
	}

	/**
	 * Tests setting the mass to null.
	 * @since 3.1.0
	 */
	@Test(expected = NullPointerException.class)
	public void setNullMass() {
		Body b = new Body();
		b.update((Mass)null);
	}

	/**
	 * Tests setting the mass type to null.
	 * @since 3.1.0
	 */
	@Test(expected = NullPointerException.class)
	public void setNullMassType2() {
		Body b = new Body();
		b.update((Mass.Type)null);
	}
	
	/**
	 * Tests the apply methods for forces and torques.
	 */
	@Test
	public void apply() {
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0));
		b.update(Mass.Type.NORMAL);
		
		// all the methods should add forces/torques to
		// an accumulator, wake up the body, and not
		// modify the force/torque values
		
		// test the apply force method
		b.setAsleep(true);
		b.applyForce(new Force(new Vector2(0.0, 2.0)));
		TestCase.assertEquals(1, b.forces.size());
		TestCase.assertTrue(b.force.isZero());
		TestCase.assertFalse(b.isAsleep());
		
		// test the apply force vector method
		b.setAsleep(true);
		b.applyForce(new Vector2(0.0, 2.0));
		TestCase.assertEquals(1, b.forces.size());
		TestCase.assertTrue(b.force.isZero());
		TestCase.assertFalse(b.isAsleep());
		
		// test the apply force at point method
		b.setAsleep(true);
		b.applyForce(new Vector2(0.0, 2.0), new Vector2(0.0, 0.5));
		TestCase.assertEquals(1, b.forces.size());
		TestCase.assertEquals(1, b.torques.size());
		TestCase.assertTrue(b.force.isZero());
		TestCase.assertEquals(0.0, b.torque);
		TestCase.assertFalse(b.isAsleep());
		
		// test the apply torque method
		b.setAsleep(true);
		b.applyTorque(new Torque(0.4));
		TestCase.assertEquals(1, b.torques.size());
		TestCase.assertEquals(0.0, b.torque);
		TestCase.assertFalse(b.isAsleep());
		
		// test the apply torque value method
		b.setAsleep(true);
		b.applyTorque(0.4);
		TestCase.assertEquals(1, b.torques.size());
		TestCase.assertEquals(0.0, b.torque);
		TestCase.assertFalse(b.isAsleep());
	}
	
	/**
	 * Tests the apply method for a null force.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullForce() {
		Body b = new Body();
		b.applyForce((Force) null);
	}
	
	/**
	 * Tests the apply method for a null force.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullForceVector() {
		Body b = new Body();
		b.applyForce((Vector2) null);
	}
	
	/**
	 * Tests the apply method for a null torque.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullTorque() {
		Body b = new Body();
		b.applyTorque((Torque) null);
	}
	
	/**
	 * Tests the apply method for a null force
	 * at a point.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullForceAtPoint() {
		Body b = new Body();
		b.applyForce(null, new Vector2(0.0, 0.0));
	}
	
	/**
	 * Tests the apply method for a force at a
	 * null point.
	 */
	@Test(expected = NullPointerException.class)
	public void applyForceAtNullPoint() {
		Body b = new Body();
		b.applyForce(new Vector2(0.0, 1.0), null);
	}
	
	/**
	 * Tests the clearXXX methods.
	 */
	@Test
	public void clear() {
		Body b = new Body();
		
		b.applyForce(new Vector2(0.0, 1.0), new Vector2(2.0, 1.0));
		b.force = new Vector2(-1.0, 0.5);
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
	 * Tests the accumulate method.
	 */
	@Test
	public void accumulate() {
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0));
		b.update(Mass.Type.NORMAL);
		
		b.applyForce(new Vector2(0.0, -2.0), new Vector2(1.0, -0.3));
		// just use the default elapsed time
		b.accumulate(Settings.DEFAULT_STEP_FREQUENCY);
		
		TestCase.assertFalse(b.force.isZero());
		TestCase.assertFalse(0.0 == b.torque);
		TestCase.assertTrue(b.forces.isEmpty());
		TestCase.assertTrue(b.torques.isEmpty());
		TestCase.assertFalse(b.isAsleep());
	}
	
	/**
	 * Tests the is dynamic method.
	 */
	@Test
	public void isDynamic() {
		Body b = new Body();
		b.addFixture(Geometry.createIsoscelesTriangle(2.0, 1.0));
		b.update(Mass.Type.NORMAL);
		
		TestCase.assertTrue(b.isDynamic());
		TestCase.assertFalse(b.isKinematic());
		TestCase.assertFalse(b.isStatic());
	}
	
	/**
	 * Tests the is dynamic method.
	 */
	@Test
	public void isKinematic() {
		Body b = new Body();
		
		b.setLinearVelocity(new Vector2(0.0, 2.0));
		TestCase.assertFalse(b.isDynamic());
		TestCase.assertTrue(b.isKinematic());
		TestCase.assertFalse(b.isStatic());
		
		b.angularVelocity = 0.4;
		TestCase.assertFalse(b.isDynamic());
		TestCase.assertTrue(b.isKinematic());
		TestCase.assertFalse(b.isStatic());
		
		b.velocity.zero();
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
		Body b = new Body();
		
		TestCase.assertFalse(b.isDynamic());
		TestCase.assertFalse(b.isKinematic());
		TestCase.assertTrue(b.isStatic());
	}
	
	/**
	 * Tests the sleep flag methods.
	 */
	@Test
	public void sleeping() {
		Body b = new Body();
		b.setAutoSleepingEnabled(true);
		TestCase.assertTrue(b.isAutoSleepingEnabled());
		b.setAutoSleepingEnabled(false);
		TestCase.assertFalse(b.isAutoSleepingEnabled());
		
		b.setAsleep(false);
		TestCase.assertFalse(b.isAsleep());
		
		// test that we can sleep the body even if the
		// auto sleep is not enabled.
		b.velocity.x = -0.5;
		b.velocity.y = 1.0;
		b.angularVelocity = 3.2;
		b.force.x = 1.0;
		b.torque = 1.2;
		b.applyTorque(0.3);
		b.applyForce(new Vector2(1.0, 1.0));
		b.setAsleep(true);
		TestCase.assertTrue(b.isAsleep());
		TestCase.assertTrue(b.forces.isEmpty());
		TestCase.assertTrue(b.torques.isEmpty());
		TestCase.assertEquals(1.2, b.torque);
		TestCase.assertEquals(1.0, b.force.x);
		TestCase.assertTrue(b.velocity.isZero());
		TestCase.assertEquals(0.0, b.angularVelocity);
	}
	
	/**
	 * Tests the active methods.
	 */
	@Test
	public void active() {
		Body b = new Body();
		TestCase.assertTrue(b.isActive());
		
		b.setActive(false);
		TestCase.assertFalse(b.isActive());
		
		b.setActive(true);
		TestCase.assertTrue(b.isActive());
	}
	
	/**
	 * Tests the on island methods.
	 */
	@Test
	public void onIsland() {
		Body b = new Body();
		TestCase.assertFalse(b.isOnIsland());
		
		b.setOnIsland(true);
		TestCase.assertTrue(b.isOnIsland());
		
		b.setOnIsland(false);
		TestCase.assertFalse(b.isOnIsland());
	}
	
	/**
	 * Tests the is connected method.
	 */
	@Test
	public void isConnected() {
		Body b1 = new Body();
		Body b2 = new Body();
		Body b3 = new Body();
		
		// test null test
		TestCase.assertFalse(b1.isConnected(null));
		
		// test no connections
		TestCase.assertFalse(b1.isConnected(b2));
				
		Joint j = new DistanceJoint(b1, b2, new Vector2(), new Vector2());
		JointEdge je1 = new JointEdge(b2, j);
		JointEdge je2 = new JointEdge(b1, j);
		
		b1.joints.add(je1);
		b2.joints.add(je2);
		
		// test no connection to b3
		TestCase.assertFalse(b1.isConnected(b3));
		
		// test that they are connected
		TestCase.assertTrue(b1.isConnected(b2));
		
		// test with or without collision allowed
		j.setCollisionAllowed(true);
		
		// test that they are connected given the flag
		TestCase.assertTrue(b1.isConnected(b2, true));
		TestCase.assertFalse(b1.isConnected(b2, false));
	}
	
	/**
	 * Tests a case where two or more joints are connecting
	 * two bodies one of which allows collision and another
	 * does not.
	 * @since 2.2.2
	 */
	@Test
	public void isConnectedTwoOrMoreJoints() {
		Body b1 = new Body();
		Body b2 = new Body();
		
		RevoluteJoint rj = new RevoluteJoint(b1, b2, new Vector2());
		JointEdge je11 = new JointEdge(b2, rj);
		JointEdge je12 = new JointEdge(b1, rj);
		
		AngleJoint aj = new AngleJoint(b1, b2);
		JointEdge je21 = new JointEdge(b2, aj);
		JointEdge je22 = new JointEdge(b1, aj);
		
		b1.joints.add(je11);
		b1.joints.add(je21);
		
		b2.joints.add(je12);
		b2.joints.add(je22);
		
		// test both with no collision
		TestCase.assertTrue(b1.isConnected(b2, false));
		
		// set one joint to allow collision
		aj.setCollisionAllowed(true);
		
		TestCase.assertTrue(b1.isConnected(b2, true));
	}
	
	/**
	 * Tests the set velocity method passing a null value.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullVelocity() {
		Body b = new Body();
		b.setLinearVelocity(null);
	}
	
	/**
	 * Tests the set linear damping method passing a 
	 * negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeLinearDamping() {
		Body b = new Body();
		b.setLinearDamping(-1.0);
	}
	
	/**
	 * Tests the set linear damping method.
	 */
	@Test
	public void setLinearDamping() {
		Body b = new Body();
		b.setLinearDamping(1.0);
		b.setLinearDamping(0.0);
	}
	
	/**
	 * Tests the set angular damping method passing a 
	 * negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeAngularDamping() {
		Body b = new Body();
		b.setAngularDamping(-2.0 * Math.PI);
	}

	/**
	 * Tests the set linear damping method.
	 */
	@Test
	public void setAngularDamping() {
		Body b = new Body();
		b.setAngularDamping(1.0);
		b.setAngularDamping(0.0);
	}
	
	/**
	 * Tests the getJoinedBodies method.
	 */
	@Test
	public void getJoinedBodies() {
		Body b1 = new Body();
		Body b2 = new Body();
		
		List<Body> bodies = b1.getJoinedBodies();
		TestCase.assertNotNull(bodies);
		TestCase.assertTrue(bodies.isEmpty());
		
		Joint j = new DistanceJoint(b1, b2, new Vector2(), new Vector2());
		JointEdge je1 = new JointEdge(b2, j);
		JointEdge je2 = new JointEdge(b1, j);
		
		b1.joints.add(je1);
		b2.joints.add(je2);
		
		bodies = b1.getJoinedBodies();
		TestCase.assertNotNull(bodies);
		TestCase.assertFalse(bodies.isEmpty());
		TestCase.assertSame(b2, bodies.get(0));
	}
	
	/**
	 * Tests the getJoints method.
	 */
	@Test
	public void getJoints() {
		Body b1 = new Body();
		Body b2 = new Body();
		
		List<Joint> joints = b1.getJoints();
		TestCase.assertNotNull(joints);
		TestCase.assertTrue(joints.isEmpty());
		
		Joint j = new DistanceJoint(b1, b2, new Vector2(), new Vector2());
		JointEdge je1 = new JointEdge(b2, j);
		JointEdge je2 = new JointEdge(b1, j);
		
		b1.joints.add(je1);
		b2.joints.add(je2);
		
		joints = b1.getJoints();
		TestCase.assertNotNull(joints);
		TestCase.assertFalse(joints.isEmpty());
		TestCase.assertSame(j, joints.get(0));
	}
	
	/**
	 * Test the getInContactBodies method.
	 */
	@Test
	public void getInContactBodies() {
		World world = new World();
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		
		Body b1 = new Body();
		BodyFixture f1 = b1.addFixture(c1);
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		
		List<Body> bodies = b1.getInContactBodies(false);
		TestCase.assertNotNull(bodies);
		TestCase.assertTrue(bodies.isEmpty());
		bodies = b1.getInContactBodies(true);
		TestCase.assertNotNull(bodies);
		TestCase.assertTrue(bodies.isEmpty());
		
		Sat sat = new Sat();
		Penetration p = new Penetration();
		// detect the collision
		sat.detect(c1, b1.getTransform(), c2, b2.getTransform(), p);
		// create a manifold
		ClippingManifoldSolver cms = new ClippingManifoldSolver();
		Manifold m = new Manifold();
		cms.getManifold(p, c1, b1.getTransform(), c2, b2.getTransform(), m);
		ContactConstraint cc = new ContactConstraint(b1, f1, b2, f2, m, world);
		ContactEdge ce1 = new ContactEdge(b2, cc);
		ContactEdge ce2 = new ContactEdge(b1, cc);
		
		b1.contacts.add(ce1);
		b2.contacts.add(ce2);
		
		bodies = b1.getInContactBodies(false);
		TestCase.assertNotNull(bodies);
		TestCase.assertFalse(bodies.isEmpty());
		TestCase.assertSame(b2, bodies.get(0));
		
		b1.contacts.clear();
		b2.contacts.clear();
		
		f1.setSensor(true);
		cc = new ContactConstraint(b1, f1, b2, f2, m, world);
		ce1 = new ContactEdge(b2, cc);
		ce2 = new ContactEdge(b1, cc);
		b1.contacts.add(ce1);
		b2.contacts.add(ce2);
		
		bodies = b1.getInContactBodies(false);
		TestCase.assertNotNull(bodies);
		TestCase.assertTrue(bodies.isEmpty());
		
		bodies = b1.getInContactBodies(true);
		TestCase.assertNotNull(bodies);
		TestCase.assertFalse(bodies.isEmpty());
		TestCase.assertSame(b2, bodies.get(0));
	}
	
	/**
	 * Test the getContacts method.
	 */
	@Test
	public void getContacts() {
		World world = new World();
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		
		Body b1 = new Body();
		BodyFixture f1 = b1.addFixture(c1);
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		
		List<ContactPoint> contacts = b1.getContacts(false);
		TestCase.assertNotNull(contacts);
		TestCase.assertTrue(contacts.isEmpty());
		contacts = b1.getContacts(true);
		TestCase.assertNotNull(contacts);
		TestCase.assertTrue(contacts.isEmpty());
		
		Sat sat = new Sat();
		Penetration p = new Penetration();
		// detect the collision
		sat.detect(c1, b1.getTransform(), c2, b2.getTransform(), p);
		// create a manifold
		ClippingManifoldSolver cms = new ClippingManifoldSolver();
		Manifold m = new Manifold();
		cms.getManifold(p, c1, b1.getTransform(), c2, b2.getTransform(), m);
		ContactConstraint cc = new ContactConstraint(b1, f1, b2, f2, m, world);
		ContactEdge ce1 = new ContactEdge(b2, cc);
		ContactEdge ce2 = new ContactEdge(b1, cc);
		
		b1.contacts.add(ce1);
		b2.contacts.add(ce2);
		
		contacts = b1.getContacts(false);
		TestCase.assertNotNull(contacts);
		TestCase.assertFalse(contacts.isEmpty());
		
		b1.contacts.clear();
		b2.contacts.clear();
		
		f1.setSensor(true);
		cc = new ContactConstraint(b1, f1, b2, f2, m, world);
		ce1 = new ContactEdge(b2, cc);
		ce2 = new ContactEdge(b1, cc);
		b1.contacts.add(ce1);
		b2.contacts.add(ce2);
		
		contacts = b1.getContacts(false);
		TestCase.assertNotNull(contacts);
		TestCase.assertTrue(contacts.isEmpty());
		
		contacts = b1.getContacts(true);
		TestCase.assertNotNull(contacts);
		TestCase.assertFalse(contacts.isEmpty());
	}
	
	/**
	 * Tests the setRotationDiscRadius method.
	 * @since 2.0.0
	 */
	@Test
	public void setRotationDiscRadius() {
		Body b = new Body();
		BodyFixture f = b.addFixture(Geometry.createCircle(2.0));
		f.getShape().translate(-2.0, 1.0);
		b.addFixture(Geometry.createSquare(1.0));
		f = b.addFixture(Geometry.createUnitCirclePolygon(5, 1.0));
		f.getShape().translate(1.0, -3.0);
		b.update();
		
		double rdr = b.getRotationDiscRadius();
		
		TestCase.assertEquals(5.089, rdr, 1.0e-3);
	}
	

	/**
	 * Tests the setRotationDiscRadius method.
	 * @since 2.2.2
	 */
	@Test
	public void translateToOrigin() {
		Body b = new Body();
		
		// the 4 from the dyn4j logo testbed test
		Polygon four1 = new Polygon(new Vector2[] {
				            new Vector2(0.859375, 1.828125),
			                new Vector2(-0.21875, 0.8125),
			                new Vector2(0.8125, 0.8125)});
		Polygon four2 = new Polygon(new Vector2[] {
					        new Vector2(0.8125, 0.8125),
			                new Vector2(0.828125, 0.046875),
			                new Vector2(1.125, 0.046875),
			                new Vector2(1.125, 1.828125),
			                new Vector2(0.859375, 1.828125)});
		
		// add the shapes to the body
		b.addFixture(four1);
		b.addFixture(four2);
		
		// setup the body
		b.update();
		
		// make sure the center of mass is not at the origin
		Vector2 p = b.getWorldCenter();
		TestCase.assertTrue(p.x > 1e-6 || p.x < -1e-6);
		TestCase.assertTrue(p.y > 1e-6 || p.y < -1e-6);
		
		// perform the method we are try to test
		b.translateToOrigin();
		
		// make sure it worked
		p = b.getWorldCenter();
		TestCase.assertEquals(p.x, 0.0, 1e-6);
		TestCase.assertEquals(p.y, 0.0, 1e-6);
	}
	
	/**
	 * Tests the getVelocity at a given point on the body method.
	 * @since 3.0.1
	 */
	@Test
	public void velocityAtPoint() {
		Body b = new Body();
		b.velocity.set(5.0, 4.0);
		b.angularVelocity = Math.PI;
		b.addFixture(Geometry.createEquilateralTriangle(2.0));
		BodyFixture bf = b.addFixture(Geometry.createUnitCirclePolygon(5, 1.0));
		bf.getShape().translate(-1.0, 1.0);
		b.update();
		b.translateToOrigin();
		
		Vector2 p = new Vector2(-2.0, 1.0);
		
		Vector2 vp = b.getLinearVelocity(p);
		
		TestCase.assertEquals( 1.858, vp.x, 1.0E-3);
		TestCase.assertEquals(-2.283, vp.y, 1.0E-3);
	}
	
	/**
	 * Tests the create AABB method.
	 * @since 3.0.2
	 */
	@Test
	public void createAABB() {
		Body b = new Body();
		
		// create an aabb from an empty body (no fixtures)
		AABB aabb = b.createAABB();
		TestCase.assertEquals(0.0, aabb.getMaxX());
		TestCase.assertEquals(0.0, aabb.getMaxY());
		TestCase.assertEquals(0.0, aabb.getMinX());
		TestCase.assertEquals(0.0, aabb.getMinY());
		
		// create an aabb from just one fixture
		b.addFixture(Geometry.createCircle(0.5));
		aabb = b.createAABB();
		TestCase.assertEquals(0.5, aabb.getMaxX());
		TestCase.assertEquals(0.5, aabb.getMaxY());
		TestCase.assertEquals(-0.5, aabb.getMinX());
		TestCase.assertEquals(-0.5, aabb.getMinY());
		
		// create an aabb from more than one fixture
		BodyFixture bf = b.addFixture(Geometry.createRectangle(1.0, 1.0));
		bf.getShape().translate(-0.5, 0.0);
		aabb = b.createAABB();
		TestCase.assertEquals(0.5, aabb.getMaxX());
		TestCase.assertEquals(0.5, aabb.getMaxY());
		TestCase.assertEquals(-1.0, aabb.getMinX());
		TestCase.assertEquals(-0.5, aabb.getMinY());
	}
	
	/**
	 * Tests the create get accumulated force method.
	 * @since 3.0.2
	 */
	@Test
	public void getAccumulatedForce() {
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0));
		b.update(Mass.Type.NORMAL);
		
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
		Body b = new Body();
		
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
	 * Tests the shiftCoordinates method.
	 */
	@Test
	public void shiftCoordinates() {
		Body b = new Body();
		b.shift(new Vector2(-2.0, 1.0));
		// it just translates the transform
		Vector2 tx = b.getTransform().getTranslation();
		TestCase.assertEquals(-2.0, tx.x, 1.0e-3);
		TestCase.assertEquals(1.0, tx.y, 1.0e-3);
	}
	
	/**
	 * Tests bodies joined by multiple joints ensuring that the
	 * getJoinedBodies method only returns one instance of the joined
	 * body.
	 */
	@Test
	public void getJoinedBodiesMulti() {
		World w = new World();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		w.addBody(b1);
		w.addBody(b2);
		
		Joint j1 = new AngleJoint(b1, b2);
		Joint j2 = new AngleJoint(b1, b2);
		
		w.addJoint(j1);
		w.addJoint(j2);
		
		List<Body> jbs = b1.getJoinedBodies();
		TestCase.assertEquals(1, jbs.size());
	}
	
	/**
	 * Tests bodies in contact with multiple fixtures ensuring that the
	 * getInContactBodies method only returns one instance of the in contact
	 * body.
	 */
	@Test
	public void getInContactBodiesMulti() {
		World w = new World();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		b1.addFixture(Geometry.createRectangle(15.0, 1.0));
		b1.update(Mass.Type.NORMAL);
		
		b2.addFixture(Geometry.createSquare(1.0));
		Convex c = Geometry.createSquare(1.0);
		c.translate(-0.5, 0.0);
		b2.addFixture(c);
		b2.update(Mass.Type.NORMAL);
		b2.translate(0.0, 0.75);
		
		w.addBody(b1);
		w.addBody(b2);
		
		w.step(1);
		
		List<Body> cbs = b1.getInContactBodies(false);
		TestCase.assertEquals(1, cbs.size());
	}
	
	/**
	 * Tests passing a null impulse.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullImpulse() {
		Body b = new Body();
		b.applyImpulse(null);
	}
	
	/**
	 * Tests passing a null impulse.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullImpulse2() {
		Body b = new Body();
		b.applyImpulse(null, new Vector2());
	}
	
	/**
	 * Tests passing a null point.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullImpulsePoint() {
		Body b = new Body();
		b.applyImpulse(new Vector2(), null);
	}
	
	/**
	 * Test the successful use of the applyImpulse methods.
	 */
	@Test
	public void applyImpulse() {
		Body b = new Body();
		b.addFixture(Geometry.createRectangle(1.0, 1.0));
		b.update(Mass.Type.NORMAL);
		
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
	}
	
	/**
	 * Make sure the user data is stored.
	 */
	@Test
	public void getUserData() {
		String obj = "hello";
		Body b = new Body();
		
		TestCase.assertNull(b.getUserData());
		
		b.setUserData(obj);
		TestCase.assertNotNull(b.getUserData());
		TestCase.assertSame(obj, b.getUserData());
	}
	
	/**
	 * Tests the contains(Vector2) method.
	 * @since 3.1.5
	 */
	@Test
	public void containsPoint() {
		Body b = new Body();
		Convex c1 = Geometry.createCircle(0.5);
		Convex c2 = Geometry.createRectangle(1.0, 1.0);
		c1.translate(0.75, 0.0);
		b.addFixture(c1);
		b.addFixture(c2);
		
		TestCase.assertTrue(b.contains(new Vector2(0.0, 0.0)));
		TestCase.assertTrue(b.contains(new Vector2(0.5, 0.0)));
		TestCase.assertTrue(b.contains(new Vector2(0.55, 0.25)));
		TestCase.assertFalse(b.contains(new Vector2(0.52, 0.45)));
		TestCase.assertTrue(b.contains(new Vector2(0.70, 0.3)));
	}
	
	/**
	 * Tests the getChangeInPosition method.
	 * @since 3.1.5
	 */
	@Test
	public void getChangeInPosition() {
		Body b = new Body();
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
		Body b = new Body();
		b.addFixture(Geometry.createCircle(0.5));
		b.rotate(Math.toRadians(30));
		b.setAngularVelocity(Math.toRadians(1));
		b.update();
		
		b.getInitialTransform().set(b.getTransform());
		
		// test moving forward 20 degrees
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
		
		// reverse direction
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
	}
}
