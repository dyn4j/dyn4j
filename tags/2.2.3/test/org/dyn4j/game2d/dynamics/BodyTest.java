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
package org.dyn4j.game2d.dynamics;

import java.util.List;

import junit.framework.TestCase;

import org.dyn4j.game2d.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.game2d.collision.manifold.Manifold;
import org.dyn4j.game2d.collision.narrowphase.Penetration;
import org.dyn4j.game2d.collision.narrowphase.Sat;
import org.dyn4j.game2d.dynamics.contact.ContactConstraint;
import org.dyn4j.game2d.dynamics.contact.ContactEdge;
import org.dyn4j.game2d.dynamics.contact.ContactPoint;
import org.dyn4j.game2d.dynamics.joint.AngleJoint;
import org.dyn4j.game2d.dynamics.joint.DistanceJoint;
import org.dyn4j.game2d.dynamics.joint.Joint;
import org.dyn4j.game2d.dynamics.joint.JointEdge;
import org.dyn4j.game2d.dynamics.joint.RevoluteJoint;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Geometry;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Vector2;
import org.junit.Test;

/**
 * Class to test the {@link Body} class.
 * @author William Bittle
 * @version 2.2.3
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
		TestCase.assertNotNull(b.id);
		TestCase.assertNotNull(b.contacts);
		TestCase.assertNotNull(b.fixtures);
		TestCase.assertNotNull(b.force);
		TestCase.assertNotNull(b.forces);
		TestCase.assertNotNull(b.joints);
		TestCase.assertNotNull(b.mass);
		TestCase.assertNotNull(b.torques);
		TestCase.assertNotNull(b.transform);
		TestCase.assertNotNull(b.velocity);
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
		TestCase.assertEquals(0, b.fixtures.size());
		TestCase.assertTrue(success);
		
		b.addFixture(f);
		BodyFixture f2 = b.addFixture(Geometry.createSquare(0.5));
		success = b.removeFixture(f);
		TestCase.assertEquals(1, b.fixtures.size());
		TestCase.assertTrue(f2 == b.fixtures.get(0));
		TestCase.assertTrue(success);
		
		// test removing by index
		f = b.addFixture(Geometry.createEquilateralTriangle(0.5));
		b.addFixture(Geometry.createRectangle(1.0, 2.0));
		f2 = b.removeFixture(1);
		
		TestCase.assertEquals(2, b.fixtures.size());
		TestCase.assertTrue(f2 == f);
	}
	
	/**
	 * Tests the remove fixture method failure cases.
	 */
	@Test
	public void removeFixtureNotFound() {
		Body b = new Body();
		
		// test null fixture
		boolean success = b.removeFixture(null);
		TestCase.assertFalse(success);
		
		// test not found fixture
		b.addFixture(Geometry.createCircle(1.0));
		success = b.removeFixture(new BodyFixture(Geometry.createRightTriangle(0.5, 0.3)));
		TestCase.assertFalse(success);
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
	 * Tests the set mass methods.
	 */
	@Test
	public void setMass() {
		Body b = new Body();
		
		// test setting the mass with no fixtures
		// this should create an infinite mass at the origin
		b.setMass();
		TestCase.assertNotNull(b.mass);
		TestCase.assertTrue(b.mass.isInfinite());
		TestCase.assertTrue(b.mass.getCenter().isZero());
		TestCase.assertEquals(0.0, b.mass.getMass());
		TestCase.assertEquals(0.0, b.mass.getInverseMass());
		TestCase.assertEquals(0.0, b.mass.getInertia());
		TestCase.assertEquals(0.0, b.mass.getInverseInertia());
		
		// test setting with one fixture
		// it should not be infinite
		BodyFixture f1 = b.addFixture(Geometry.createUnitCirclePolygon(5, 0.5));
		b.setMass();
		TestCase.assertNotNull(b.mass);
		TestCase.assertFalse(b.mass.isInfinite());
		
		// test setting with multiple fixtures
		// it should not be infinite and should not be
		// equal to either shapes mass
		BodyFixture f2 = b.addFixture(Geometry.createIsoscelesTriangle(1.0, 1.0));
		b.setMass();
		TestCase.assertNotNull(b.mass);
		TestCase.assertFalse(b.mass.isInfinite());
		Mass m1 = f1.createMass();
		Mass m2 = f2.createMass();
		TestCase.assertFalse(m1.equals(b.mass));
		TestCase.assertFalse(m2.equals(b.mass));
		
		// test setting the mass with a flag
		// make sure the type of mass is correct but the
		// values of the mass and 
		b.setMass(Mass.Type.INFINITE);
		TestCase.assertNotNull(b.mass);
		TestCase.assertTrue(b.mass.isInfinite());
		TestCase.assertEquals(0.0, b.mass.getMass());
		TestCase.assertEquals(0.0, b.mass.getInverseMass());
		TestCase.assertEquals(0.0, b.mass.getInertia());
		TestCase.assertEquals(0.0, b.mass.getInverseInertia());
		b.setMass(Mass.Type.FIXED_ANGULAR_VELOCITY);
		TestCase.assertNotNull(b.mass);
		TestCase.assertEquals(0.0, b.mass.getInertia());
		TestCase.assertEquals(0.0, b.mass.getInverseInertia());
		b.setMass(Mass.Type.FIXED_LINEAR_VELOCITY);
		TestCase.assertNotNull(b.mass);
		TestCase.assertEquals(0.0, b.mass.getMass());
		TestCase.assertEquals(0.0, b.mass.getInverseMass());
		
		// test setting the mass directly
		Mass mass = new Mass(f2.getShape().getCenter(), 2.3, 20.3);
		b.setMass(mass);
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
	public void setMassType() {
		Body b = new Body();
		// should auto generate it
		b.setMassType(Mass.Type.NORMAL);
		// should generate another and should default to normal
		b.setMassType(null);
	}
	
	/**
	 * Tests the apply methods for forces and torques.
	 */
	@Test
	public void apply() {
		Body b = new Body();
		
		// all the methods should add forces/torques to
		// an accumulator, wake up the body, and not
		// modify the force/torque values
		
		// test the apply force method
		b.setAsleep(true);
		b.apply(new Force(new Vector2(0.0, 2.0)));
		TestCase.assertEquals(1, b.forces.size());
		TestCase.assertTrue(b.force.isZero());
		TestCase.assertFalse(b.isAsleep());
		
		// test the apply force vector method
		b.setAsleep(true);
		b.apply(new Vector2(0.0, 2.0));
		TestCase.assertEquals(1, b.forces.size());
		TestCase.assertTrue(b.force.isZero());
		TestCase.assertFalse(b.isAsleep());
		
		// test the apply force at point method
		b.setAsleep(true);
		b.apply(new Vector2(0.0, 2.0), new Vector2(0.0, 0.5));
		TestCase.assertEquals(1, b.forces.size());
		TestCase.assertEquals(1, b.torques.size());
		TestCase.assertTrue(b.force.isZero());
		TestCase.assertEquals(0.0, b.torque);
		TestCase.assertFalse(b.isAsleep());
		
		// test the apply torque method
		b.setAsleep(true);
		b.apply(new Torque(0.4));
		TestCase.assertEquals(1, b.torques.size());
		TestCase.assertEquals(0.0, b.torque);
		TestCase.assertFalse(b.isAsleep());
		
		// test the apply torque value method
		b.setAsleep(true);
		b.apply(0.4);
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
		b.apply((Force) null);
	}
	
	/**
	 * Tests the apply method for a null force.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullForceVector() {
		Body b = new Body();
		b.apply((Vector2) null);
	}
	
	/**
	 * Tests the apply method for a null torque.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullTorque() {
		Body b = new Body();
		b.apply((Torque) null);
	}
	
	/**
	 * Tests the apply method for a null force
	 * at a point.
	 */
	@Test(expected = NullPointerException.class)
	public void applyNullForceAtPoint() {
		Body b = new Body();
		b.apply(null, new Vector2(0.0, 0.0));
	}
	
	/**
	 * Tests the apply method for a force at a
	 * null point.
	 */
	@Test(expected = NullPointerException.class)
	public void applyForceAtNullPoint() {
		Body b = new Body();
		b.apply(new Vector2(0.0, 1.0), null);
	}
	
	/**
	 * Tests the clearXXX methods.
	 */
	@Test
	public void clear() {
		Body b = new Body();
		
		b.apply(new Vector2(0.0, 1.0), new Vector2(2.0, 1.0));
		b.force = new Vector2(-1.0, 0.5);
		b.torque = 0.4;
		
		b.clearForce();
		TestCase.assertTrue(b.force.isZero());
		
		b.clearTorque();
		TestCase.assertEquals(0.0, b.torque);
		
		b.clearForces();
		TestCase.assertTrue(b.forces.isEmpty());
		
		b.clearTorques();
		TestCase.assertTrue(b.torques.isEmpty());
	}
	
	/**
	 * Tests the accumulate method.
	 */
	@Test
	public void accumulate() {
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0));
		
		b.apply(new Vector2(0.0, -2.0), new Vector2(1.0, -0.3));
		b.accumulate();
		
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
		b.setMass();
		
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
		
		b.setVelocity(new Vector2(0.0, 2.0));
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
		b.apply(0.3);
		b.apply(new Vector2(1.0, 1.0));
		b.setAsleep(true);
		TestCase.assertTrue(b.isAsleep());
		TestCase.assertTrue(b.forces.isEmpty());
		TestCase.assertTrue(b.torques.isEmpty());
		TestCase.assertEquals(0.0, b.torque);
		TestCase.assertTrue(b.force.isZero());
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
		b.setVelocity(null);
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
		sat.detect(c1, b1.transform, c2, b2.transform, p);
		// create a manifold
		ClippingManifoldSolver cms = new ClippingManifoldSolver();
		Manifold m = new Manifold();
		cms.getManifold(p, c1, b1.transform, c2, b2.transform, m);
		ContactConstraint cc = new ContactConstraint(b1, f1, b2, f2, m, 0.4, 0.2);
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
		cc = new ContactConstraint(b1, f1, b2, f2, m, 0.4, 0.2);
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
		sat.detect(c1, b1.transform, c2, b2.transform, p);
		// create a manifold
		ClippingManifoldSolver cms = new ClippingManifoldSolver();
		Manifold m = new Manifold();
		cms.getManifold(p, c1, b1.transform, c2, b2.transform, m);
		ContactConstraint cc = new ContactConstraint(b1, f1, b2, f2, m, 0.4, 0.2);
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
		cc = new ContactConstraint(b1, f1, b2, f2, m, 0.4, 0.2);
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
		b.setMass();
		
		double rdr = b.getRotationDiscRadius();
		
		TestCase.assertEquals(5.129, rdr, 1.0e-3);
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
		b.setMass();
		
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
}
