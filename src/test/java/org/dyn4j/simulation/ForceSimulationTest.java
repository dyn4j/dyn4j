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
package org.dyn4j.simulation;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Force;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.world.World;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Class used to test the {@link Force} class with simulation.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 */
public class ForceSimulationTest {
	/**
	 * Tests the apply method where the force is retained for two steps.
	 */
	@Test
	public void applyTimed() {
		World w = new World();
		Body b = new Body();
		b.addFixture(Geometry.createCircle(1.0));
		b.setMass(MassType.NORMAL);
		
		Force f = new Force(1, 0) {
			private double time = 0;
			public boolean isComplete(double elapsedTime) {
				time += elapsedTime;
				if (time >= 2.0 / 60.0) {
					return true;
				}
				return false;
			}
		};
		
		b.applyForce(f);
		w.addBody(b);
		
		w.step(1);
		
		// make sure the force is still there
		TestCase.assertEquals(1.0, b.getAccumulatedForce().getMagnitude());
		
		w.step(1);
		
		TestCase.assertEquals(0.0, b.getAccumulatedForce().getMagnitude());
	}
}
