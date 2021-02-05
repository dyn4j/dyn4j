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
package org.dyn4j.dynamics.contact;

import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.collision.narrowphase.Separation;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests the methods of the {@link ForceCollisionTimeOfImpactSolver} class.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 */
public class ForceCollisionTimeOfImpactSolverTest {
	/**
	 * Tests resolution for one normal body and one infinite and the maximum translation clamping.
	 */
	@Test
	public void simpleScenario1() {
		ForceCollisionTimeOfImpactSolver<Body> solver = new ForceCollisionTimeOfImpactSolver<Body>();
		
		Settings settings = new Settings();
		
		Body body1 = new Body();
		body1.addFixture(Geometry.createCircle(1.0));
		body1.setMass(MassType.NORMAL);
		
		Body body2 = new Body();
		body2.addFixture(Geometry.createCircle(1.0));
		body2.translate(2.5, 0.0);
		body2.setMass(MassType.INFINITE);
		
		Separation separation = new Separation();
		separation.setDistance(0.5);
		separation.setNormal(new Vector2(1.0, 0.0));
		separation.setPoint1(new Vector2(1.0, 0.0));
		separation.setPoint2(new Vector2(1.5, 0.0));
		
		TimeOfImpact toi = new TimeOfImpact();
		toi.setSeparation(separation);
		toi.setTime(0.5);
		
		solver.solve(body1, body2, toi, settings);
		
		TestCase.assertEquals(0.2, body1.getTransform().getTranslationX());
		TestCase.assertEquals(0.0, body1.getTransform().getTranslationY());
		TestCase.assertEquals(2.5, body2.getTransform().getTranslationX());
		TestCase.assertEquals(0.0, body2.getTransform().getTranslationY());
	}
	
	/**
	 * Test moving of two non-infinite bodies.
	 */
	@Test
	public void simpleScenario2() {
		ForceCollisionTimeOfImpactSolver<Body> solver = new ForceCollisionTimeOfImpactSolver<Body>();
		
		Settings settings = new Settings();
		
		Body body1 = new Body();
		body1.addFixture(Geometry.createCircle(1.0));
		body1.setMass(MassType.NORMAL);
		
		Body body2 = new Body();
		body2.addFixture(Geometry.createCircle(1.0));
		body2.translate(2.0001, 0.0);
		body2.setMass(MassType.NORMAL);
		
		Separation separation = new Separation();
		separation.setDistance(0.0001);
		separation.setNormal(new Vector2(1.0, 0.0));
		separation.setPoint1(new Vector2(1.0, 0.0));
		separation.setPoint2(new Vector2(1.0001, 0.0));
		
		TimeOfImpact toi = new TimeOfImpact();
		toi.setSeparation(separation);
		toi.setTime(1.0);
		
		solver.solve(body1, body2, toi, settings);
		
		double d = (0.0001 + settings.getLinearTolerance()) * 0.5;
		TestCase.assertEquals(d, body1.getTransform().getTranslationX());
		TestCase.assertEquals(0.0, body1.getTransform().getTranslationY());
		TestCase.assertEquals(2.0001 - d, body2.getTransform().getTranslationX());
		TestCase.assertEquals(0.0, body2.getTransform().getTranslationY());
	}
}
