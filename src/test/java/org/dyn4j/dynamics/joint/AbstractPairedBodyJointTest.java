/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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

import java.util.Iterator;
import java.util.List;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests the abstract paired-body joint's methods.
 * @author William Bittle
 * @version 5.0.0
 * @since 5.0.0
 */
public class AbstractPairedBodyJointTest {
	/** The first body used for testing */
	protected Body b1;

	/** The second body used for testing */
	protected Body b2;
	
	/** The abstract joint */
	protected AbstractPairedBodyJoint<Body> aj;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.b1 = new Body();
		this.b2 = new Body();
		this.aj = new TestAbstractPairedBodyJoint(this.b1, this.b2);
	}
	
	/**
	 * Tests the state of the joint at create time.
	 */
	@Test
	public void create() {
		TestCase.assertEquals(2, aj.getBodyCount());
		TestCase.assertNull(aj.owner);
		TestCase.assertNull(aj.getOwner());
		TestCase.assertNull(aj.userData);
		TestCase.assertNull(aj.getUserData());
		TestCase.assertFalse(aj.collisionAllowed);
		TestCase.assertFalse(aj.isCollisionAllowed());
		TestCase.assertEquals(b1, aj.getBody(0));
		TestCase.assertEquals(b2, aj.getBody(1));
		TestCase.assertEquals(b1, aj.getBody1());
		TestCase.assertEquals(b2, aj.getBody2());
		TestCase.assertNotNull(aj.getBodies());
		TestCase.assertNotNull(aj.getBodyIterator());
		TestCase.assertNotNull(aj.toString());
	}
	
	/**
	 * Tests receiving an NPE when passing a null first argument.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullBody1() {
		new TestAbstractPairedBodyJoint(null, this.b2);
	}
	
	/**
	 * Tests receiving an NPE when passing a null second argument.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullBody2() {
		new TestAbstractPairedBodyJoint(this.b1, null);
	}
	
	/**
	 * Tests receiving an NPE when passing a null second argument.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullBoth() {
		new TestAbstractPairedBodyJoint(null, null);
	}
	
	/**
	 * Tests receiving an exception when body1 and body2 are the same instance.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createSame() {
		new TestAbstractPairedBodyJoint(this.b1, this.b1);
	}
	
	/**
	 * Tests the getBodies method.
	 */
	@Test
	public void getBodies() {
		List<Body> bodies = aj.getBodies();
		
		TestCase.assertNotNull(bodies);
		TestCase.assertEquals(2, bodies.size());
		TestCase.assertEquals(b1, aj.getBody(0));
		TestCase.assertEquals(b2, aj.getBody(1));
		TestCase.assertEquals(b1, aj.getBody1());
		TestCase.assertEquals(b2, aj.getBody2());
	}
	
	/**
	 * Makes sure the returned list is unmodifiable.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getBodiesAndAdd() {
		AbstractPairedBodyJoint<Body> aj = new TestAbstractPairedBodyJoint(this.b1, this.b2);
		aj.getBodies().add(new Body());
	}
	
	/**
	 * Makes sure the returned list is unmodifiable.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getBodiesAndRemove() {
		AbstractPairedBodyJoint<Body> aj = new TestAbstractPairedBodyJoint(this.b1, this.b2);
		aj.getBodies().remove(0);
	}
	
	/**
	 * Tests successfully getting the bodies by index.
	 */
	@Test
	public void getBodyAtValidIndex() {
		TestCase.assertEquals(b1, aj.getBody(0));
	}
	
	/**
	 * Tests getting bodies at a negative index.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void getBodyAtNegativeIndex() {
		aj.getBody(-1);
	}
	
	/**
	 * Tests getting bodies at an index over the size.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void getBodyAtTooHighIndex() {
		aj.getBody(2);
	}
	
	/**
	 * Tests getting the body count.
	 */
	@Test
	public void getBodyCount() {
		TestCase.assertEquals(2, aj.getBodyCount());
	}
	
	/**
	 * Tests getting the body iterator and iterating through the bodies.
	 */
	@Test
	public void getBodyIterator() {
		Iterator<Body> it = aj.getBodyIterator();
		TestCase.assertNotNull(it);
		
		Body b1 = null;
		Body b2 = null;
		
		
		int n = 0;
		while (it.hasNext()) {
			if (n == 0) b1 = it.next();
			if (n == 1) b2 = it.next();
			n++;
		}
		
		TestCase.assertEquals(2, n);
		TestCase.assertEquals(this.b1, b1);
		TestCase.assertEquals(this.b2, b2);
	}
	
	/**
	 * Tests prevention of removal by the iterator.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getBodyIteratorRemove() {
		Iterator<Body> it = aj.getBodyIterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
	}
	
	/**
	 * Tests the isMember method.
	 */
	@Test
	public void isMember() {
		TestCase.assertTrue(aj.isMember(b1));
		TestCase.assertTrue(aj.isMember(b2));
		TestCase.assertFalse(aj.isMember(null));
		TestCase.assertFalse(aj.isMember(new Body()));
	}
	
	/**
	 * Tests the isEnabled method.
	 */
	@Test
	public void isEnabled() {
		TestCase.assertTrue(aj.isEnabled());
		
		b1.setEnabled(false);
		
		TestCase.assertFalse(aj.isEnabled());
		
		b2.setEnabled(false);
		
		TestCase.assertFalse(aj.isEnabled());
		
		b1.setEnabled(true);
		b2.setEnabled(true);
		
		TestCase.assertTrue(aj.isEnabled());
	}
	
	/**
	 * Tests the get other body method.
	 */
	@Test
	public void getOtherBody() {
		TestCase.assertEquals(b2, aj.getOtherBody(b1));
		TestCase.assertEquals(b1, aj.getOtherBody(b2));
		TestCase.assertEquals(null, aj.getOtherBody(null));
		TestCase.assertEquals(null, aj.getOtherBody(new Body()));
	}

	/**
	 * Tests the getReducedInertia method.
	 */
	@Test
	public void getReducedInertia() {
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
