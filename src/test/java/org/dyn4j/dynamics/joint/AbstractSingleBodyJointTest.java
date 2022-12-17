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
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests the abstract single-body joint's methods.
 * @author William Bittle
 * @version 5.0.0
 * @since 5.0.0
 */
public class AbstractSingleBodyJointTest {
	/** The first body used for testing */
	protected Body b1;
	
	/** The abstract joint */
	protected AbstractSingleBodyJoint<Body> aj;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		this.b1 = new Body();
		this.aj = new TestAbstractSingleBodyJoint(this.b1);
	}
	
	/**
	 * Tests the state of the joint at create time.
	 */
	@Test
	public void create() {
		TestCase.assertEquals(1, aj.getBodyCount());
		TestCase.assertNull(aj.owner);
		TestCase.assertNull(aj.getOwner());
		TestCase.assertNull(aj.userData);
		TestCase.assertNull(aj.getUserData());
		TestCase.assertFalse(aj.collisionAllowed);
		TestCase.assertFalse(aj.isCollisionAllowed());
		TestCase.assertEquals(b1, aj.getBody(0));
		TestCase.assertEquals(b1, aj.getBody());
		TestCase.assertNotNull(aj.getBodies());
		TestCase.assertNotNull(aj.getBodyIterator());
		TestCase.assertNotNull(aj.toString());
	}
	
	/**
	 * Tests receiving an NPE when passing a null list.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullBody() {
		new TestAbstractSingleBodyJoint(null);
	}
	
	/**
	 * Tests the getBodies method.
	 */
	@Test
	public void getBodies() {
		List<Body> bodies = aj.getBodies();
		
		TestCase.assertNotNull(bodies);
		TestCase.assertEquals(1, bodies.size());
		TestCase.assertEquals(b1, bodies.get(0));
	}
	
	/**
	 * Makes sure the returned list is unmodifiable.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getBodiesAndAdd() {
		AbstractSingleBodyJoint<Body> aj = new TestAbstractSingleBodyJoint(b1);
		aj.getBodies().add(new Body());
	}
	
	/**
	 * Makes sure the returned list is unmodifiable.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void getBodiesAndRemove() {
		AbstractSingleBodyJoint<Body> aj = new TestAbstractSingleBodyJoint(b1);
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
		aj.getBody(1);
	}
	
	/**
	 * Tests getting the body count.
	 */
	@Test
	public void getBodyCount() {
		TestCase.assertEquals(1, aj.getBodyCount());
	}
	
	/**
	 * Tests getting the body iterator and iterating through the bodies.
	 */
	@Test
	public void getBodyIterator() {
		Iterator<Body> it = aj.getBodyIterator();
		TestCase.assertNotNull(it);
		
		Body b1 = null;
		
		int n = 0;
		while (it.hasNext()) {
			if (n == 0) b1 = it.next();
			n++;
		}
		
		TestCase.assertEquals(1, n);
		TestCase.assertEquals(this.b1, b1);
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
		
		b1.setEnabled(true);
		
		TestCase.assertTrue(aj.isEnabled());
	}
}
