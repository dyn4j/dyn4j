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

import junit.framework.TestCase;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Torque;
import org.dyn4j.geometry.Geometry;
import org.junit.Test;

/**
 * Class used to test the {@link Torque} class.
 * @author William Bittle
 * @version 3.1.1
 * @since 1.0.2
 */
public class TorqueTest {
	/**
	 * Tests successful creation.
	 */
	@Test
	public void createSuccess() {
		Torque t = new Torque();
		t = new Torque(0.2);
		TestCase.assertEquals(0.2, t.torque);
		
		t = new Torque(-3.5);
		TestCase.assertEquals(-3.5, t.torque);
		
		Torque t2 = new Torque(t);
		TestCase.assertEquals(-3.5, t2.torque);
	}
	
	/**
	 * Tests the set methods successfully.
	 */
	@Test
	public void setSuccess() {
		Torque t = new Torque();
		t.set(0.32);
		TestCase.assertEquals(0.32, t.torque);
		
		Torque t2 = new Torque(3.42);
		t.set(t2);
		TestCase.assertEquals(3.42, t.torque);
	}
	
	/**
	 * Tests creation using a null torque.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullTorque() {
		new Torque(null);
	}
	
	/**
	 * Tests setting the torque using a null torque.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullTorque() {
		Torque t = new Torque();
		t.set(null);
	}
	
	/**
	 * Tests the apply method.
	 */
	@Test
	public void apply() {
		Body b = new Body();
		Torque t = new Torque(-0.2);
		
		t.apply(b);
		
		TestCase.assertEquals(-0.2, b.torque);
	}

	/**
	 * Tests the apply method where the torque is retained for two steps.
	 */
	@Test
	public void applyTimed() {
		World w = new World();
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0));
		b.update();
		
		Torque t = new Torque() {
			private double time = 0;
			public boolean isComplete(double elapsedTime) {
				time += elapsedTime;
				if (time >= 2.0 / 60.0) {
					return true;
				}
				return false;
			}
		};
		
		b.applyTorque(t);
		w.addBody(b);
		
		w.step(1);
		
		// make sure the torque is still there
		TestCase.assertEquals(1, b.torques.size());
		
		w.step(1);
		
		TestCase.assertEquals(0, b.torques.size());
	}
}
