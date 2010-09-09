/*
 * Copyright (c) 2010, William Bittle
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

import junit.framework.TestCase;

import org.dyn4j.game2d.dynamics.contact.ContactConstraintId;
import org.dyn4j.game2d.geometry.Rectangle;
import org.junit.Test;

/**
 * Tests the methods of the {@link ContactConstraintId} class.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public class ContactConstraintIdTest {
	/**
	 * Test case for the hashCode method.
	 */
	@Test
	public void hash() {
		Body b1 = new Body();
		Body b2 = new Body();
		Body b3 = new Body();
		
		BodyFixture f1 = new BodyFixture(new Rectangle(1.0, 1.0));
		BodyFixture f2 = new BodyFixture(new Rectangle(2.0, 2.0));
		BodyFixture f3 = new BodyFixture(new Rectangle(3.0, 3.0));
		
		// since im just testing the id's im not going to fully setup the bodies
		
		ContactConstraintId cci1 = new ContactConstraintId(b1, f1, b2, f2);
		ContactConstraintId cci2 = new ContactConstraintId(b2, f2, b1, f1);
		
		// the two above should have the same hashcode
		TestCase.assertEquals(cci2.hashCode(), cci1.hashCode());
		
		ContactConstraintId cci3 = new ContactConstraintId(b3, f3, b1, f1);
		
		// should not be equal
		TestCase.assertFalse(cci3.hashCode() == cci2.hashCode());
	}
}
