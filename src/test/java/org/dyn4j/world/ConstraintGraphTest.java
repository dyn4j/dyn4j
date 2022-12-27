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
package org.dyn4j.world;

import java.util.Arrays;
import java.util.List;

import org.dyn4j.collision.BasicCollisionItem;
import org.dyn4j.collision.BasicCollisionPair;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactConstraintSolver;
import org.dyn4j.dynamics.contact.SequentialImpulses;
import org.dyn4j.dynamics.joint.AbstractJoint;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.PinJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests the {@link ConstraintGraph} class.
 * @author William Bittle
 * @version 4.1.3
 * @since 4.0.0
 */
public class ConstraintGraphTest {
	/**
	 * Tests the constructors with various parameters.
	 */
	@Test
	public void create() {
		new ConstraintGraph<Body>();
		new ConstraintGraph<Body>(10, 5);
		new ConstraintGraph<Body>(-1, 5);
		new ConstraintGraph<Body>(0, 5);
		new ConstraintGraph<Body>(5, -5);
		new ConstraintGraph<Body>(10, 0);
	}
	
	/**
	 * Tests the addBody method.
	 */
	@Test
	public void addBody() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		Body b1 = new Body();
		
		g.addBody(b1);
		
		TestCase.assertTrue(g.containsBody(b1));
		
		ConstraintGraphNode<Body> node = g.getNode(b1);
		TestCase.assertNotNull(node);
		TestCase.assertEquals(node.body, b1);
		TestCase.assertNotNull(node.joints);
		TestCase.assertNotNull(node.jointsUnmodifiable);
		TestCase.assertNotNull(node.contactConstraints);
		TestCase.assertNotNull(node.contactConstraintsUnmodifiable);
		
		TestCase.assertEquals(node.getBody(), b1);
		TestCase.assertNotNull(node.getJoints());
		TestCase.assertNotNull(node.getContactConstraints());
		
		// test adding again (no error, but no side effect either)
		g.addBody(b1);
		
		TestCase.assertTrue(g.containsBody(b1));
		
		node = g.getNode(b1);
		TestCase.assertNotNull(node);
		TestCase.assertEquals(node.body, b1);
		TestCase.assertNotNull(node.joints);
		TestCase.assertNotNull(node.jointsUnmodifiable);
		TestCase.assertNotNull(node.contactConstraints);
		TestCase.assertNotNull(node.contactConstraintsUnmodifiable);
		
		TestCase.assertEquals(node.getBody(), b1);
		TestCase.assertNotNull(node.getJoints());
		TestCase.assertNotNull(node.getContactConstraints());		
	}
	
	/**
	 * Tests the addContactConstraint method.
	 */
	@Test
	public void addContactConstraint() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		
		Body b1 = new Body();
		BodyFixture f1 = b1.addFixture(c1);
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		
		g.addBody(b1);
		g.addBody(b2);
		
		ContactConstraint<Body> cc = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f2)));
		g.addContactConstraint(cc);
		
		TestCase.assertTrue(g.isInContact(b1, b2));
		TestCase.assertTrue(g.containsContactConstraint(cc));
		
		ConstraintGraphNode<Body> n1 = g.getNode(b1);
		TestCase.assertEquals(1, n1.contactConstraints.size());
		TestCase.assertEquals(cc, n1.contactConstraints.get(0));
		
		ConstraintGraphNode<Body> n2 = g.getNode(b2);
		TestCase.assertEquals(1, n2.contactConstraints.size());
		TestCase.assertEquals(cc, n2.contactConstraints.get(0));
		
		// it's reference equals, so check that
		cc = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f2)));
		TestCase.assertFalse(g.containsContactConstraint(cc));
	}
	
	/**
	 * Tests the addContactConstraint method when the bodies haven't been added.
	 */
	@Test
	public void addContactConstraintBodiesNotAdded() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		
		Body b1 = new Body();
		BodyFixture f1 = b1.addFixture(c1);
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		
		ContactConstraint<Body> cc = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f2)));
		g.addContactConstraint(cc);
		
		TestCase.assertTrue(g.isInContact(b1, b2));
		TestCase.assertTrue(g.containsContactConstraint(cc));
		
		ConstraintGraphNode<Body> n1 = g.getNode(b1);
		TestCase.assertEquals(1, n1.contactConstraints.size());
		TestCase.assertEquals(cc, n1.contactConstraints.get(0));
		
		ConstraintGraphNode<Body> n2 = g.getNode(b2);
		TestCase.assertEquals(1, n2.contactConstraints.size());
		TestCase.assertEquals(cc, n2.contactConstraints.get(0));
	}
	
	/**
	 * Tests the addJoint method.
	 */
	@Test
	public void addJoint() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		g.addBody(b1);
		g.addBody(b2);
		
		Joint<Body> j = new AngleJoint<Body>(b1, b2);
		g.addJoint(j);

		ConstraintGraphNode<Body> n1 = g.getNode(b1);
		TestCase.assertNotNull(n1);
		TestCase.assertEquals(1, n1.joints.size());
		TestCase.assertEquals(j, n1.joints.get(0));
		
		ConstraintGraphNode<Body> n2 = g.getNode(b2);
		TestCase.assertNotNull(n2);
		TestCase.assertEquals(1, n2.joints.size());
		TestCase.assertEquals(j, n2.joints.get(0));

		TestCase.assertTrue(g.isJoined(b1, b2));
		TestCase.assertTrue(g.containsJoint(j));
		
		// it's reference equals, so check that
		j = new AngleJoint<Body>(b1, b2);
		TestCase.assertFalse(g.containsJoint(j));
	}
	
	/**
	 * Tests the addJoint method when the bodies haven't been added.
	 */
	@Test
	public void addJointBodiesNotAdded() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		Joint<Body> j = new AngleJoint<Body>(b1, b2);
		g.addJoint(j);

		ConstraintGraphNode<Body> n1 = g.getNode(b1);
		TestCase.assertNotNull(n1);
		TestCase.assertEquals(1, n1.joints.size());
		TestCase.assertEquals(j, n1.joints.get(0));
		
		ConstraintGraphNode<Body> n2 = g.getNode(b2);
		TestCase.assertNotNull(n2);
		TestCase.assertEquals(1, n2.joints.size());
		TestCase.assertEquals(j, n2.joints.get(0));

		TestCase.assertTrue(g.isJoined(b1, b2));
		TestCase.assertTrue(g.containsJoint(j));
		
		// it's reference equals, so check that
		j = new AngleJoint<Body>(b1, b2);
		TestCase.assertFalse(g.containsJoint(j));
	}
	
	/**
	 * Tests the removeAllContactConstraints method.
	 */
	@Test
	public void removeAllContactConstraints() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		
		Body b1 = new Body();
		BodyFixture f1 = b1.addFixture(c1);
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		
		g.addBody(b1);
		g.addBody(b2);
		
		ContactConstraint<Body> cc1 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f2)));
		g.addContactConstraint(cc1);
		
		ContactConstraint<Body> cc2 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f2)));
		g.addContactConstraint(cc2);

		ConstraintGraphNode<Body> n1 = g.getNode(b1);
		TestCase.assertEquals(2, n1.contactConstraints.size());
		TestCase.assertEquals(cc1, n1.contactConstraints.get(0));
		TestCase.assertEquals(cc2, n1.contactConstraints.get(1));
		
		ConstraintGraphNode<Body> n2 = g.getNode(b2);
		TestCase.assertEquals(2, n2.contactConstraints.size());
		TestCase.assertEquals(cc1, n2.contactConstraints.get(0));
		TestCase.assertEquals(cc2, n2.contactConstraints.get(1));
		
		TestCase.assertTrue(g.isInContact(b1, b2));
		TestCase.assertTrue(g.containsContactConstraint(cc1));
		TestCase.assertTrue(g.containsContactConstraint(cc2));
		
		g.removeAllContactConstraints();

		n1 = g.getNode(b1);
		TestCase.assertEquals(0, n1.contactConstraints.size());
		
		n2 = g.getNode(b2);
		TestCase.assertEquals(0, n2.contactConstraints.size());
		
		TestCase.assertFalse(g.isInContact(b1, b2));
		TestCase.assertFalse(g.containsContactConstraint(cc1));
		TestCase.assertFalse(g.containsContactConstraint(cc2));
		TestCase.assertTrue(g.containsBody(b1));
		TestCase.assertTrue(g.containsBody(b2));
	}
	
	/**
	 * Tests the removeAllJoints method.
	 */
	@Test
	public void removeAllJoints() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		g.addBody(b1);
		g.addBody(b2);
		
		Joint<Body> j1 = new AngleJoint<Body>(b1, b2);
		Joint<Body> j2 = new AngleJoint<Body>(b1, b2);
		
		g.addJoint(j1);
		g.addJoint(j2);
		
		ConstraintGraphNode<Body> n1 = g.getNode(b1);
		TestCase.assertEquals(2, n1.joints.size());
		TestCase.assertEquals(j1, n1.joints.get(0));
		TestCase.assertEquals(j2, n1.joints.get(1));
		
		ConstraintGraphNode<Body> n2 = g.getNode(b2);
		TestCase.assertEquals(2, n2.joints.size());
		TestCase.assertEquals(j1, n2.joints.get(0));
		TestCase.assertEquals(j2, n2.joints.get(1));
		
		TestCase.assertTrue(g.isJoined(b1, b2));
		TestCase.assertTrue(g.containsJoint(j1));
		TestCase.assertTrue(g.containsJoint(j2));
		
		g.removeAllJoints();
		
		n1 = g.getNode(b1);
		TestCase.assertEquals(0, n1.joints.size());
		
		n2 = g.getNode(b2);
		TestCase.assertEquals(0, n2.joints.size());
		
		TestCase.assertFalse(g.isJoined(b1, b2));
		TestCase.assertFalse(g.containsJoint(j1));
		TestCase.assertFalse(g.containsJoint(j2));
		TestCase.assertTrue(g.containsBody(b1));
		TestCase.assertTrue(g.containsBody(b2));
	}
	
	/**
	 * Tests the removeBody method.
	 */
	@Test
	public void removeBody() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Convex c3 = Geometry.createCircle(0.5);
		
		Body b1 = new Body();
		BodyFixture f1 = b1.addFixture(c1);
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		Body b3 = new Body();
		BodyFixture f3 = b3.addFixture(c3);
		
		g.addBody(b1);
		g.addBody(b2);
		g.addBody(b3);
		
		Joint<Body> j1 = new AngleJoint<Body>(b1, b2);
		Joint<Body> j2 = new AngleJoint<Body>(b1, b3);
		Joint<Body> j3 = new AngleJoint<Body>(b2, b3);

		g.addJoint(j1);
		g.addJoint(j2);
		g.addJoint(j3);
		
		ContactConstraint<Body> cc1 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f2)));
		ContactConstraint<Body> cc2 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b3, f3)));
		ContactConstraint<Body> cc3 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b2, f2),
				new BasicCollisionItem<Body, BodyFixture>(b3, f3)));
		
		g.addContactConstraint(cc1);
		g.addContactConstraint(cc2);
		g.addContactConstraint(cc3);

		ConstraintGraphNode<Body> n1 = g.getNode(b1);
		TestCase.assertEquals(2, n1.contactConstraints.size());
		TestCase.assertEquals(cc1, n1.contactConstraints.get(0));
		TestCase.assertEquals(cc2, n1.contactConstraints.get(1));
		TestCase.assertEquals(2, n1.joints.size());
		TestCase.assertEquals(j1, n1.joints.get(0));
		TestCase.assertEquals(j2, n1.joints.get(1));
		
		ConstraintGraphNode<Body> n2 = g.getNode(b2);
		TestCase.assertEquals(2, n2.contactConstraints.size());
		TestCase.assertEquals(cc1, n2.contactConstraints.get(0));
		TestCase.assertEquals(cc3, n2.contactConstraints.get(1));
		TestCase.assertEquals(2, n2.joints.size());
		TestCase.assertEquals(j1, n2.joints.get(0));
		TestCase.assertEquals(j3, n2.joints.get(1));
		
		ConstraintGraphNode<Body> n3 = g.getNode(b3);
		TestCase.assertEquals(2, n3.contactConstraints.size());
		TestCase.assertEquals(cc2, n3.contactConstraints.get(0));
		TestCase.assertEquals(cc3, n3.contactConstraints.get(1));
		TestCase.assertEquals(2, n3.joints.size());
		TestCase.assertEquals(j2, n3.joints.get(0));
		TestCase.assertEquals(j3, n3.joints.get(1));
		
		TestCase.assertTrue(g.isInContact(b1, b2));
		TestCase.assertTrue(g.isInContact(b1, b3));
		TestCase.assertTrue(g.isInContact(b2, b3));
		TestCase.assertTrue(g.containsContactConstraint(cc1));
		TestCase.assertTrue(g.containsContactConstraint(cc2));
		TestCase.assertTrue(g.containsContactConstraint(cc3));
		TestCase.assertTrue(g.isJoined(b1, b2));
		TestCase.assertTrue(g.isJoined(b1, b3));
		TestCase.assertTrue(g.isJoined(b2, b3));
		TestCase.assertTrue(g.containsJoint(j1));
		TestCase.assertTrue(g.containsJoint(j2));
		TestCase.assertTrue(g.containsJoint(j3));
		
		g.removeBody(b1);

		TestCase.assertNull(g.getNode(b1));
		TestCase.assertFalse(g.containsBody(b1));
		
		n2 = g.getNode(b2);
		TestCase.assertEquals(1, n2.contactConstraints.size());
		TestCase.assertEquals(cc3, n2.contactConstraints.get(0));
		TestCase.assertEquals(1, n2.joints.size());
		TestCase.assertEquals(j3, n2.joints.get(0));
		
		n3 = g.getNode(b3);
		TestCase.assertEquals(1, n3.contactConstraints.size());
		TestCase.assertEquals(cc3, n3.contactConstraints.get(0));
		TestCase.assertEquals(1, n3.joints.size());
		TestCase.assertEquals(j3, n3.joints.get(0));
		
		TestCase.assertFalse(g.isInContact(b1, b2));
		TestCase.assertFalse(g.isInContact(b1, b3));
		TestCase.assertTrue(g.isInContact(b2, b3));
		TestCase.assertFalse(g.containsContactConstraint(cc1));
		TestCase.assertFalse(g.containsContactConstraint(cc2));
		TestCase.assertTrue(g.containsContactConstraint(cc3));
		TestCase.assertFalse(g.isJoined(b1, b2));
		TestCase.assertFalse(g.isJoined(b1, b3));
		TestCase.assertTrue(g.isJoined(b2, b3));
		TestCase.assertFalse(g.containsJoint(j1));
		TestCase.assertFalse(g.containsJoint(j2));
		TestCase.assertTrue(g.containsJoint(j3));
	}
	
	/**
	 * Tests the clear method.
	 */
	@Test
	public void clear() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Convex c3 = Geometry.createCircle(0.5);
		
		Body b1 = new Body();
		BodyFixture f1 = b1.addFixture(c1);
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		Body b3 = new Body();
		BodyFixture f3 = b3.addFixture(c3);
		
		g.addBody(b1);
		g.addBody(b2);
		g.addBody(b3);
		
		Joint<Body> j1 = new AngleJoint<Body>(b1, b2);
		Joint<Body> j2 = new AngleJoint<Body>(b1, b3);
		Joint<Body> j3 = new AngleJoint<Body>(b2, b3);

		g.addJoint(j1);
		g.addJoint(j2);
		g.addJoint(j3);
		
		ContactConstraint<Body> cc1 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f2)));
		ContactConstraint<Body> cc2 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b3, f3)));
		ContactConstraint<Body> cc3 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b2, f2),
				new BasicCollisionItem<Body, BodyFixture>(b3, f3)));
		
		g.addContactConstraint(cc1);
		g.addContactConstraint(cc2);
		g.addContactConstraint(cc3);
		
		TestCase.assertTrue(g.isInContact(b1, b2));
		TestCase.assertTrue(g.isInContact(b1, b3));
		TestCase.assertTrue(g.isInContact(b2, b3));
		TestCase.assertTrue(g.containsContactConstraint(cc1));
		TestCase.assertTrue(g.containsContactConstraint(cc2));
		TestCase.assertTrue(g.containsContactConstraint(cc3));
		TestCase.assertTrue(g.isJoined(b1, b2));
		TestCase.assertTrue(g.isJoined(b1, b3));
		TestCase.assertTrue(g.isJoined(b2, b3));
		TestCase.assertTrue(g.containsJoint(j1));
		TestCase.assertTrue(g.containsJoint(j2));
		TestCase.assertTrue(g.containsJoint(j3));
		
		g.clear();
		
		TestCase.assertNull(g.getNode(b1));
		TestCase.assertNull(g.getNode(b2));
		TestCase.assertNull(g.getNode(b3));
		
		TestCase.assertFalse(g.isInContact(b1, b2));
		TestCase.assertFalse(g.isInContact(b1, b3));
		TestCase.assertFalse(g.isInContact(b2, b3));
		TestCase.assertFalse(g.containsContactConstraint(cc1));
		TestCase.assertFalse(g.containsContactConstraint(cc2));
		TestCase.assertFalse(g.containsContactConstraint(cc3));
		TestCase.assertFalse(g.isJoined(b1, b2));
		TestCase.assertFalse(g.isJoined(b1, b3));
		TestCase.assertFalse(g.isJoined(b2, b3));
		TestCase.assertFalse(g.containsJoint(j1));
		TestCase.assertFalse(g.containsJoint(j2));
		TestCase.assertFalse(g.containsJoint(j3));
	}
	
	/**
	 * Test the removeJoint method.
	 */
	@Test
	public void removeJoint() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		g.addBody(b1);
		g.addBody(b2);
		
		Joint<Body> j1 = new AngleJoint<Body>(b1, b2);
		Joint<Body> j2 = new AngleJoint<Body>(b1, b2);
		
		g.addJoint(j1);
		g.addJoint(j2);
		
		TestCase.assertTrue(g.isJoined(b1, b2));
		TestCase.assertTrue(g.containsJoint(j1));
		TestCase.assertTrue(g.containsJoint(j2));
		
		g.removeJoint(j1);
		
		TestCase.assertTrue(g.isJoined(b1, b2));
		TestCase.assertFalse(g.containsJoint(j1));
		TestCase.assertTrue(g.containsJoint(j2));
		TestCase.assertTrue(g.containsBody(b1));
		TestCase.assertTrue(g.containsBody(b2));
		
		ConstraintGraphNode<Body> n1 = g.getNode(b1);
		TestCase.assertEquals(1, n1.joints.size());
		TestCase.assertEquals(j2, n1.joints.get(0));
		
		ConstraintGraphNode<Body> n2 = g.getNode(b2);
		TestCase.assertEquals(1, n2.joints.size());
		TestCase.assertEquals(j2, n2.joints.get(0));
	}
	
	/**
	 * Test the removeContactConstraint method.
	 */
	@Test
	public void removeContactConstraint() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		
		Body b1 = new Body();
		BodyFixture f1 = b1.addFixture(c1);
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		
		g.addBody(b1);
		g.addBody(b2);
		
		ContactConstraint<Body> cc1 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f2)));
		ContactConstraint<Body> cc2 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f2)));
		
		g.addContactConstraint(cc1);
		g.addContactConstraint(cc2);
		
		ConstraintGraphNode<Body> n1 = g.getNode(b1);
		TestCase.assertEquals(2, n1.contactConstraints.size());
		TestCase.assertEquals(cc1, n1.contactConstraints.get(0));
		TestCase.assertEquals(cc2, n1.contactConstraints.get(1));
		
		ConstraintGraphNode<Body> n2 = g.getNode(b2);
		TestCase.assertEquals(2, n2.contactConstraints.size());
		TestCase.assertEquals(cc1, n2.contactConstraints.get(0));
		TestCase.assertEquals(cc2, n2.contactConstraints.get(1));
		
		TestCase.assertTrue(g.isInContact(b1, b2));
		TestCase.assertTrue(g.containsContactConstraint(cc1));
		TestCase.assertTrue(g.containsContactConstraint(cc2));
		
		g.removeContactConstraint(cc1);

		TestCase.assertFalse(g.containsContactConstraint(cc1));
		TestCase.assertTrue(g.containsContactConstraint(cc2));

		n1 = g.getNode(b1);
		TestCase.assertEquals(1, n1.contactConstraints.size());
		TestCase.assertEquals(cc2, n1.contactConstraints.get(0));
		
		n2 = g.getNode(b2);
		TestCase.assertEquals(1, n2.contactConstraints.size());
		TestCase.assertEquals(cc2, n2.contactConstraints.get(0));
		
		TestCase.assertTrue(g.isInContact(b1, b2));
		TestCase.assertFalse(g.containsContactConstraint(cc1));
		TestCase.assertTrue(g.containsContactConstraint(cc2));
	}
	
	/**
	 * Tests the size method.
	 */
	@Test
	public void size() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		Convex c3 = Geometry.createCircle(0.5);
		
		Body b1 = new Body();
		BodyFixture f1 = b1.addFixture(c1);
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		Body b3 = new Body();
		BodyFixture f3 = b3.addFixture(c3);
		
		g.addBody(b1);
		g.addBody(b2);
		g.addBody(b3);
		
		Joint<Body> j1 = new AngleJoint<Body>(b1, b2);
		Joint<Body> j2 = new AngleJoint<Body>(b1, b3);
		Joint<Body> j3 = new AngleJoint<Body>(b2, b3);

		g.addJoint(j1);
		g.addJoint(j2);
		g.addJoint(j3);
		
		ContactConstraint<Body> cc1 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f2)));
		ContactConstraint<Body> cc2 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b3, f3)));
		ContactConstraint<Body> cc3 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b2, f2),
				new BasicCollisionItem<Body, BodyFixture>(b3, f3)));
		
		g.addContactConstraint(cc1);
		g.addContactConstraint(cc2);
		g.addContactConstraint(cc3);
		
		TestCase.assertEquals(3, g.size());

		g.removeBody(b1);

		TestCase.assertEquals(2, g.size());
		
		g.removeContactConstraint(cc3);
		
		TestCase.assertEquals(2, g.size());
		
		g.removeJoint(j3);
		
		TestCase.assertEquals(2, g.size());
		
		g.clear();
		
		TestCase.assertEquals(0, g.size());
	}
	
	/**
	 * Tests the isJoined method.
	 */
	@Test
	public void isJoined() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Body b1 = new Body();
		Body b2 = new Body();
		Body b3 = new Body();
		
		g.addBody(b1);
		g.addBody(b2);
		g.addBody(b3);
		
		// test null test
		TestCase.assertFalse(g.isJoined(b1, null));
		
		// test no connections
		TestCase.assertFalse(g.isJoined(b1, b2));
				
		Joint<Body> j = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2());
		
		g.addJoint(j);
		
		// test no connection to b3
		TestCase.assertFalse(g.isJoined(b1, b3));
		
		// test that they are connected
		TestCase.assertTrue(g.isJoined(b1, b2));
	}
	
	/**
	 * Tests a case where two or more joints are connecting
	 * two bodies one of which allows collision and another
	 * does not.
	 * @since 2.2.2
	 */
	@Test
	public void isJointCollisionAllowed() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		g.addBody(b1);
		g.addBody(b2);
		
		RevoluteJoint<Body> rj = new RevoluteJoint<Body>(b1, b2, new Vector2());
		AngleJoint<Body> aj = new AngleJoint<Body>(b1, b2);
		
		g.addJoint(rj);
		g.addJoint(aj);
		
		// test both with no collision
		TestCase.assertFalse(g.isJointCollisionAllowed(b1, b2));
		
		// set one joint to allow collision
		aj.setCollisionAllowed(true);
		
		TestCase.assertTrue(g.isJointCollisionAllowed(b1, b2));
		
		TestCase.assertFalse(g.isJointCollisionAllowed(b1, null));
		TestCase.assertFalse(g.isJointCollisionAllowed(null, b2));
		TestCase.assertFalse(g.isJointCollisionAllowed(null, null));
		TestCase.assertFalse(g.isJointCollisionAllowed(b1, new Body()));
		TestCase.assertFalse(g.isJointCollisionAllowed(new Body(), b2));
		
		Body b3 = new Body();
		
		g.addBody(b3);
		TestCase.assertTrue(g.isJointCollisionAllowed(b3, b1));
	}

	/**
	 * Tests the getJoinedBodies method.
	 */
	@Test
	public void getJoinedBodies() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		g.addBody(b1);
		g.addBody(b2);
		
		List<Body> bodies = g.getJoinedBodies(b1);
		TestCase.assertNotNull(bodies);
		TestCase.assertTrue(bodies.isEmpty());
		
		Joint<Body> j = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2());
		
		g.addJoint(j);
		
		bodies = g.getJoinedBodies(b1);
		TestCase.assertNotNull(bodies);
		TestCase.assertFalse(bodies.isEmpty());
		TestCase.assertSame(b2, bodies.get(0));
	}
	
	/**
	 * Tests the getJoints method.
	 */
	@Test
	public void getJointsForBody() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		g.addBody(b1);
		g.addBody(b2);
		
		List<Joint<Body>> joints = g.getJoints(b1);
		TestCase.assertNotNull(joints);
		TestCase.assertTrue(joints.isEmpty());
		
		Joint<Body> j = new DistanceJoint<Body>(b1, b2, new Vector2(), new Vector2());
		
		g.addJoint(j);
		
		joints = g.getJoints(b1);
		TestCase.assertNotNull(joints);
		TestCase.assertFalse(joints.isEmpty());
		TestCase.assertSame(j, joints.get(0));
		
		joints = g.getJoints(null);
		TestCase.assertNotNull(joints);
		TestCase.assertTrue(joints.isEmpty());
	}
	
	/**
	 * Test the getInContactBodies method.
	 */
	@Test
	public void getInContactBodies() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		
		Body b1 = new Body();
		BodyFixture f1 = b1.addFixture(c1);
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		
		b1.setMass(MassType.NORMAL);
		b2.setMass(MassType.NORMAL);
		
		g.addBody(b1);
		g.addBody(b2);
		
		List<Body> bodies = g.getInContactBodies(b1, false);
		TestCase.assertNotNull(bodies);
		TestCase.assertTrue(bodies.isEmpty());
		bodies = g.getInContactBodies(b1, true);
		TestCase.assertNotNull(bodies);
		TestCase.assertTrue(bodies.isEmpty());
		
		ContactConstraint<Body> cc = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f2)));
		g.addContactConstraint(cc);
		
		bodies = g.getInContactBodies(b1, false);
		TestCase.assertNotNull(bodies);
		TestCase.assertFalse(bodies.isEmpty());
		TestCase.assertSame(b2, bodies.get(0));
		
		cc.setSensor(true);
		
		bodies = g.getInContactBodies(b1, false);
		TestCase.assertNotNull(bodies);
		TestCase.assertTrue(bodies.isEmpty());
		
		bodies = g.getInContactBodies(b1, true);
		TestCase.assertNotNull(bodies);
		TestCase.assertFalse(bodies.isEmpty());
		TestCase.assertSame(b2, bodies.get(0));
	}
	
	/**
	 * Test the getContacts method.
	 */
	@Test
	public void getContacts() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		
		Body b1 = new Body();
		BodyFixture f1 = b1.addFixture(c1);
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		
		b1.setMass(MassType.NORMAL);
		b2.setMass(MassType.NORMAL);
		
		g.addBody(b1);
		g.addBody(b2);
		
		List<ContactConstraint<Body>> contacts = g.getContacts(b1);
		TestCase.assertNotNull(contacts);
		TestCase.assertTrue(contacts.isEmpty());
		
		ContactConstraint<Body> cc = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f2)));
		g.addContactConstraint(cc);
		
		contacts = g.getContacts(b1);
		TestCase.assertNotNull(contacts);
		TestCase.assertFalse(contacts.isEmpty());
		
		contacts = g.getContacts(null);
		TestCase.assertNotNull(contacts);
		TestCase.assertTrue(contacts.isEmpty());
	}

	/**
	 * Test the isInContact method.
	 */
	@Test
	public void isInContact() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Convex c1 = Geometry.createCircle(1.0);
		Convex c2 = Geometry.createEquilateralTriangle(0.5);
		
		Body b1 = new Body();
		BodyFixture f1 = b1.addFixture(c1);
		Body b2 = new Body();
		BodyFixture f2 = b2.addFixture(c2);
		
		b1.setMass(MassType.NORMAL);
		b2.setMass(MassType.NORMAL);
		
		g.addBody(b1);
		g.addBody(b2);
		
		TestCase.assertFalse(g.isInContact(b1, b2));
		
		ContactConstraint<Body> cc = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f2)));
		g.addContactConstraint(cc);
		
		TestCase.assertTrue(g.isInContact(b1, b2));
	}
	
	/**
	 * Tests bodies joined by multiple joints ensuring that the
	 * getJoinedBodies method only returns one instance of the joined
	 * body.
	 */
	@Test
	public void getJoinedBodiesMulti() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		g.addBody(b1);
		g.addBody(b2);
		
		Joint<Body> j1 = new AngleJoint<Body>(b1, b2);
		Joint<Body> j2 = new AngleJoint<Body>(b1, b2);
		
		g.addJoint(j1);
		g.addJoint(j2);
		
		List<Body> jbs = g.getJoinedBodies(b1);
		TestCase.assertEquals(1, jbs.size());
	}
	
	/**
	 * Tests bodies in contact with multiple fixtures ensuring that the
	 * getInContactBodies method only returns one instance of the in contact
	 * body.
	 */
	@Test
	public void getInContactBodiesMulti() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Body b1 = new Body();
		Body b2 = new Body();
		
		BodyFixture f1 = b1.addFixture(Geometry.createRectangle(15.0, 1.0));
		b1.setMass(MassType.NORMAL);
		
		BodyFixture f2 = b2.addFixture(Geometry.createSquare(1.0));
		Convex c = Geometry.createSquare(1.0);
		c.translate(-0.5, 0.0);
		BodyFixture f3 = b2.addFixture(c);
		b2.setMass(MassType.NORMAL);
		b2.translate(0.0, 0.75);
		
		g.addBody(b1);
		g.addBody(b2);
		
		ContactConstraint<Body> cc1 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f2)));
		ContactConstraint<Body> cc2 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f3)));
		g.addContactConstraint(cc1);
		g.addContactConstraint(cc2);
		
		List<Body> cbs = g.getInContactBodies(b1, false);
		TestCase.assertEquals(1, cbs.size());
	}
	
	private final class TestBody extends Body {
		boolean integrated = false;
		@Override
		public void integrateVelocity(Vector2 gravity, TimeStep timestep, Settings settings) {
			if (this.integrated) throw new IllegalStateException();
			super.integrateVelocity(gravity, timestep, settings);
			this.integrated = true;
		}
	}
	
	/**
	 * Test for issue 179 where bodies were being added to the Island
	 * more than one time (and therefore being integrated more than once
	 * per frame)
	 */
	@Test
	public void multiCollision() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Body b1 = new TestBody();
		Body b2 = new TestBody();
		Body b3 = new TestBody();
		Body b4 = new TestBody();
		
		BodyFixture f1 = b1.addFixture(Geometry.createRectangle(15.0, 1.0));
		b1.setMass(MassType.NORMAL);
		
		BodyFixture f2 = b2.addFixture(Geometry.createSquare(1.0));
		Convex c = Geometry.createSquare(1.0);
		c.translate(-0.5, 0.0);
		BodyFixture f3 = b2.addFixture(c);
		b2.setMass(MassType.NORMAL);
		b2.translate(0.0, 0.75);
		
		BodyFixture f4 = b3.addFixture(Geometry.createCircle(1.0));
		b3.setMass(MassType.NORMAL);
		
		BodyFixture f5 = b4.addFixture(Geometry.createCircle(0.5));
		b4.setMass(MassType.NORMAL);
		
		g.addBody(b1);
		g.addBody(b2);
		g.addBody(b3);
		g.addBody(b4);
		
		ContactConstraint<Body> cc1 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f2)));
		ContactConstraint<Body> cc2 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b2, f3)));
		ContactConstraint<Body> cc3 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b1, f1),
				new BasicCollisionItem<Body, BodyFixture>(b3, f4)));
		ContactConstraint<Body> cc4 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b3, f4),
				new BasicCollisionItem<Body, BodyFixture>(b4, f5)));
		ContactConstraint<Body> cc5 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b2, f2),
				new BasicCollisionItem<Body, BodyFixture>(b3, f4)));
		ContactConstraint<Body> cc6 = new ContactConstraint<Body>(new BasicCollisionPair<CollisionItem<Body, BodyFixture>>(
				new BasicCollisionItem<Body, BodyFixture>(b2, f2),
				new BasicCollisionItem<Body, BodyFixture>(b4, f5)));
		
		g.addContactConstraint(cc1);
		g.addContactConstraint(cc2);
		g.addContactConstraint(cc3);
		g.addContactConstraint(cc4);
		g.addContactConstraint(cc5);
		g.addContactConstraint(cc6);
		
		Settings settings = new Settings();
		TimeStep step = new TimeStep(settings.getStepFrequency());
		Vector2 gravity = new Vector2();
		ContactConstraintSolver<Body> solver = new SequentialImpulses<Body>();
		
		g.solve(solver, gravity, step, settings);
	}

	/**
	 * Tests removing a body that's linked to a unary joint. In versions
	 * 4.0.0 through 4.1.2 this would throw a NullPointerException.
	 * @since 4.1.3
	 */
	@Test
	public void testRemoveBodyWithUnaryJoint() {
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		Body b1 = new Body();
		g.addBody(b1);
		
		Joint<Body> j1 = new PinJoint<Body>(b1, b1.getWorldCenter());
		g.addJoint(j1);
		
		TestCase.assertTrue(g.containsBody(b1));
		TestCase.assertTrue(g.containsJoint(j1));
		
		g.removeBody(b1);
		
		TestCase.assertFalse(g.containsBody(b1));
		TestCase.assertFalse(g.containsJoint(j1));
	}
	
	// a temporary class for testing multi-body joints
	@SuppressWarnings("javadoc")
	final class QuadrupleBodyJoint<T extends PhysicsBody> extends AbstractJoint<T> {
		public QuadrupleBodyJoint(T b1, T b2, T b3, T b4) {
			super(Arrays.asList(b1, b2, b3, b4));
		}

		@Override
		public void initializeConstraints(TimeStep step, Settings settings) {}
		@Override
		public void solveVelocityConstraints(TimeStep step, Settings settings) {}
		@Override
		public boolean solvePositionConstraints(TimeStep step, Settings settings) { return false; }
		@Override
		public Vector2 getReactionForce(double invdt) { return null; }
		@Override
		public double getReactionTorque(double invdt) { return 0; }
		@Override
		public void shift(Vector2 shift) {}
	}
	
	/**
	 * Tests the adding and removal of bodies/joints with multi-body joints.
	 */
	@Test
	public void removeBodyWithQuadrupleJoint() {
		Body b1 = new Body();
		Body b2 = new Body();
		Body b3 = new Body();
		Body b4 = new Body();
		
		Joint<Body> j = new QuadrupleBodyJoint<Body>(b1, b2, b3, b4);
		
		ConstraintGraph<Body> g = new ConstraintGraph<Body>();
		
		g.addBody(b1);
		g.addBody(b2);
		g.addBody(b3);
		g.addBody(b4);
		g.addJoint(j);
		
		// verify everything is added correctly
		TestCase.assertTrue(g.containsBody(b1));
		TestCase.assertTrue(g.containsBody(b2));
		TestCase.assertTrue(g.containsBody(b3));
		TestCase.assertTrue(g.containsBody(b4));
		TestCase.assertTrue(g.containsJoint(j));
		
		// remove one of the bodies, the joint
		// should get implicitly removed along
		// with the body that was removed
		g.removeBody(b3);
		
		TestCase.assertTrue(g.containsBody(b1));
		TestCase.assertTrue(g.containsBody(b2));
		TestCase.assertFalse(g.containsBody(b3));
		TestCase.assertTrue(g.containsBody(b4));
		TestCase.assertFalse(g.containsJoint(j));
		
		// add them back, and check the joint
		// removal
		g.addBody(b3);
		g.addJoint(j);
		
		TestCase.assertTrue(g.containsBody(b1));
		TestCase.assertTrue(g.containsBody(b2));
		TestCase.assertTrue(g.containsBody(b3));
		TestCase.assertTrue(g.containsBody(b4));
		TestCase.assertTrue(g.containsJoint(j));
		
		g.removeJoint(j);
		
		TestCase.assertTrue(g.containsBody(b1));
		TestCase.assertTrue(g.containsBody(b2));
		TestCase.assertTrue(g.containsBody(b3));
		TestCase.assertTrue(g.containsBody(b4));
		TestCase.assertFalse(g.containsJoint(j));
	}
}
