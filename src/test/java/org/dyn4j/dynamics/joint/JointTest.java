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
package org.dyn4j.dynamics.joint;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link Joint} class.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.2.0
 */
public class JointTest extends AbstractJointTest {
	/**
	 * A class just to test the base {@link Joint} class methods.
	 * @author William Bittle
	 * @version 4.2.0
	 * @since 4.2.0
	 * @param <T> The {@link PhysicsBody} type
	 */
	private class TestJoint<T extends PhysicsBody> extends Joint<T> {
		public TestJoint(T body1, T body2, boolean collisionAllowed) {
			super(body1, body2, collisionAllowed);
		}
		public TestJoint(T body1, T body2) {
			super(body1, body2);
		}
		@Override
		public void shift(Vector2 shift) {}
		@Override
		public void initializeConstraints(TimeStep step, Settings settings) {}
		@Override
		public void solveVelocityConstraints(TimeStep step, Settings settings) {}
		@Override
		public boolean solvePositionConstraints(TimeStep step, Settings settings) { return false; }
		@Override
		public Vector2 getAnchor1() { return null; }
		@Override
		public Vector2 getAnchor2() { return null; }
		@Override
		public Vector2 getReactionForce(double invdt) { return null; }
		@Override
		public double getReactionTorque(double invdt) { return 0; }
	}
	
	/**
	 * Tests the successful creation of the joint.
	 */
	@Test
	public void create() {
		TestJoint<Body> aj = new TestJoint<Body>(b1, b2);
		TestCase.assertEquals(false, aj.isCollisionAllowed());
		TestCase.assertEquals(b1, aj.getBody1());
		TestCase.assertEquals(b2, aj.getBody2());
		TestCase.assertEquals(null, aj.getOwner());
		TestCase.assertEquals(null, aj.getUserData());
		TestCase.assertEquals(b2, aj.getOtherBody(b1));
		TestCase.assertNotNull(aj.toString());
		
		aj = new TestJoint<Body>(b1, b2, false);
		TestCase.assertEquals(false, aj.isCollisionAllowed());
		TestCase.assertEquals(b1, aj.getBody1());
		TestCase.assertEquals(b2, aj.getBody2());
		TestCase.assertEquals(null, aj.getOwner());
		TestCase.assertEquals(null, aj.getUserData());
		TestCase.assertEquals(b2, aj.getOtherBody(b1));
		TestCase.assertNotNull(aj.toString());
		
		aj = new TestJoint<Body>(b1, b2, true);
		TestCase.assertEquals(true, aj.isCollisionAllowed());
		TestCase.assertEquals(b1, aj.getBody1());
		TestCase.assertEquals(b2, aj.getBody2());
		TestCase.assertEquals(null, aj.getOwner());
		TestCase.assertEquals(null, aj.getUserData());
		TestCase.assertEquals(b2, aj.getOtherBody(b1));
		TestCase.assertNotNull(aj.toString());
	}
	
	/**
	 * Tests get/set of the user data.
	 */
	@Test
	public void getSetUserData() {
		TestJoint<Body> aj = new TestJoint<Body>(b1, b2);
		TestCase.assertEquals(null, aj.getUserData());
		
		Object o = new Object();
		aj.setUserData(o);
		TestCase.assertEquals(o, aj.getUserData());
		
		aj.setUserData(null);
		TestCase.assertEquals(null, aj.getUserData());
	}
	
	/**
	 * Tests get/set of the owner.
	 */
	@Test
	public void getSetOwner() {
		TestJoint<Body> aj = new TestJoint<Body>(b1, b2);
		TestCase.assertEquals(null, aj.getOwner());
		
		Object o = new Object();
		aj.setOwner(o);
		TestCase.assertEquals(o, aj.getOwner());
		
		aj.setOwner(null);
		TestCase.assertEquals(null, aj.getOwner());
	}
	
	/**
	 * Tests get/set of collision allowed flag.
	 */
	@Test
	public void getSetCollisionAllowed() {
		TestJoint<Body> aj = new TestJoint<Body>(b1, b2);
		TestCase.assertFalse(aj.isCollisionAllowed());
		
		aj.setCollisionAllowed(true);
		TestCase.assertTrue(aj.isCollisionAllowed());
		
		aj.setCollisionAllowed(false);
		TestCase.assertFalse(aj.isCollisionAllowed());
	}
	
	/**
	 * Tests the isEnabled method.
	 */
	@Test
	public void isEnabled() {
		TestJoint<Body> aj = new TestJoint<Body>(b1, b2);
		TestCase.assertTrue(aj.isEnabled());
		
		b1.setEnabled(false);
		TestCase.assertFalse(aj.isEnabled());
		
		b2.setEnabled(false);
		TestCase.assertFalse(aj.isEnabled());
		
		b1.setEnabled(true);
		TestCase.assertFalse(aj.isEnabled());
		
		b2.setEnabled(true);
		TestCase.assertTrue(aj.isEnabled());
	}

	/**
	 * Tests the getOtherBody method.
	 */
	@Test
	public void getOtherBody() {
		TestJoint<Body> aj = new TestJoint<Body>(b1, b2);
		TestCase.assertEquals(b1, aj.getOtherBody(b2));
		TestCase.assertEquals(b2, aj.getOtherBody(b1));
		TestCase.assertEquals(null, aj.getOtherBody(null));
		TestCase.assertEquals(null, aj.getOtherBody(new Body()));
	}
	
	/**
	 * Tests the getReducedInertia method.
	 */
	@Test
	public void getReducedInertia() {
		TestJoint<Body> aj = new TestJoint<Body>(b1, b2);
		b1.setMass(new Mass(new Vector2(), 1.0, 2.0));
		b2.setMass(new Mass(new Vector2(), 1.0, 2.0));
		
		double ri = aj.getReducedInertia();
		TestCase.assertEquals(1.0, ri);
		
		b1.setMassType(MassType.INFINITE);
		ri = aj.getReducedInertia();
		TestCase.assertEquals(2.0, ri);
		
		b1.setMassType(MassType.NORMAL);
		b2.setMassType(MassType.INFINITE);
		ri = aj.getReducedInertia();
		TestCase.assertEquals(2.0, ri);
		
		b1.setMassType(MassType.INFINITE);
		b2.setMassType(MassType.INFINITE);
		ri = aj.getReducedInertia();
		TestCase.assertEquals(0.0, ri);
	}
	
	/**
	 * Tests the getReducedMass method.
	 */
	@Test
	public void getReducedMass() {
		TestJoint<Body> aj = new TestJoint<Body>(b1, b2);
		b1.setMass(new Mass(new Vector2(), 1.0, 2.0));
		b2.setMass(new Mass(new Vector2(), 1.0, 2.0));
		
		double ri = aj.getReducedMass();
		TestCase.assertEquals(0.5, ri);
		
		b1.setMassType(MassType.INFINITE);
		ri = aj.getReducedMass();
		TestCase.assertEquals(1.0, ri);
		
		b1.setMassType(MassType.NORMAL);
		b2.setMassType(MassType.INFINITE);
		ri = aj.getReducedMass();
		TestCase.assertEquals(1.0, ri);
		
		b1.setMassType(MassType.INFINITE);
		b2.setMassType(MassType.INFINITE);
		ri = aj.getReducedMass();
		TestCase.assertEquals(0.0, ri);
	}
}
