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
package org.dyn4j.dynamics;

import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests the methods of the {@link BodyFixture} and {@link Fixture} classes.
 * <p>
 * Was FixtureTest.
 * @author William Bittle
 * @version 3.1.1
 * @since 3.1.1
 */
public class BodyFixtureTest {
	/** The {@link BodyFixture} object to test */
	private BodyFixture fixture;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		fixture = new BodyFixture(Geometry.createUnitCirclePolygon(5, 0.5));
	}
	
	/**
	 * Tests the creation of a fixture with a null shape.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullShape() {
		new BodyFixture(null);
	}
	
	/**
	 * Tests a successful creation.
	 */
	@Test
	public void createSuccess() {
		Convex convex = Geometry.createUnitCirclePolygon(5, 0.5);
		new BodyFixture(convex);
	}
	
	/**
	 * Tests the get/set density methods.
	 */
	@Test
	public void getSetDensity() {
		TestCase.assertEquals(BodyFixture.DEFAULT_DENSITY, fixture.getDensity());
		
		fixture.setDensity(1.0);
		TestCase.assertEquals(1.0, fixture.getDensity());
		
		fixture.setDensity(0.001);
		TestCase.assertEquals(0.001, fixture.getDensity());
		
		fixture.setDensity(1000);
		TestCase.assertEquals(1000.0, fixture.getDensity());
	}

	/**
	 * Tests setting the density to a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeDensity() {
		fixture.setDensity(-1.0);
	}
	
	/**
	 * Tests setting the density to a zero value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setZeroDensity() {
		fixture.setDensity(0.0);
	}
	
	/**
	 * Tests the get/set friction methods.
	 */
	@Test
	public void getSetFriction() {
		TestCase.assertEquals(BodyFixture.DEFAULT_FRICTION, fixture.getFriction());
		
		fixture.setFriction(1.0);
		TestCase.assertEquals(1.0, fixture.getFriction());
		
		fixture.setFriction(0.001);
		TestCase.assertEquals(0.001, fixture.getFriction());
		
		fixture.setFriction(0.0);
		TestCase.assertEquals(0.0, fixture.getFriction());
		
		fixture.setFriction(5);
		TestCase.assertEquals(5.0, fixture.getFriction());
	}

	/**
	 * Tests setting friction to a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeFriction() {
		fixture.setFriction(-2.0);
	}
	
	/**
	 * Tests the get/set restitution methods.
	 */
	@Test
	public void getSetRestitution() {
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION, fixture.getRestitution());
		
		fixture.setRestitution(1.0);
		TestCase.assertEquals(1.0, fixture.getRestitution());
		
		fixture.setRestitution(0.001);
		TestCase.assertEquals(0.001, fixture.getRestitution());
		
		fixture.setRestitution(0.0);
		TestCase.assertEquals(0.0, fixture.getRestitution());
		
		fixture.setRestitution(5);
		TestCase.assertEquals(5.0, fixture.getRestitution());
	}

	/**
	 * Tests setting the restitution to a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeRestitution() {
		fixture.setRestitution(-1.0);
	}
	
	/**
	 * Tests the toString method.
	 */
	@Test
	public void tostring() {
		TestCase.assertNotNull(fixture.toString());
	}
	
	/**
	 * Tests the createMass method.
	 */
	@Test
	public void createMass() {
		Mass mass = fixture.createMass();
		TestCase.assertNotNull(mass);
		TestCase.assertEquals(MassType.NORMAL, mass.getType());
		TestCase.assertTrue(mass.getInertia() > 0);
		TestCase.assertTrue(mass.getMass() > 0);
	}
	
	/**
	 * Tests the get/set restitution velocity methods.
	 */
	@Test
	public void getSetRestitutionVelocity() {
		TestCase.assertEquals(BodyFixture.DEFAULT_RESTITUTION_VELOCITY, fixture.getRestitutionVelocity());
		
		fixture.setRestitutionVelocity(0.0001);
		TestCase.assertEquals(0.0001, fixture.getRestitutionVelocity());
		
		fixture.setRestitutionVelocity(5.0);
		TestCase.assertEquals(5.0, fixture.getRestitutionVelocity());
		
		fixture.setRestitutionVelocity(0.0);
		TestCase.assertEquals(0.0, fixture.getRestitutionVelocity());
		
		fixture.setRestitutionVelocity(-1.0);
		TestCase.assertEquals(-1.0, fixture.getRestitutionVelocity());
	}
}
