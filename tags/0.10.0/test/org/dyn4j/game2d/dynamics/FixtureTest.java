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

import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Geometry;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the methods of the {@link Fixture} class.
 * @author William Bittle
 */
public class FixtureTest {
	/** The {@link Fixture} object to test */
	private Fixture fixture;
	
	/**
	 * Sets up the test.
	 */
	@Before
	public void setup() {
		fixture = new Fixture(Geometry.createUnitCirclePolygon(5, 0.5));
	}
	
	/**
	 * Tests the creation of a fixture with a null shape.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullShape() {
		new Fixture(null);
	}
	
	/**
	 * Tests a successful creation.
	 */
	@Test
	public void createSuccess() {
		Convex convex = Geometry.createUnitCirclePolygon(5, 0.5);
		new Fixture(convex);
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
	 * Tests setting the density to a valid value
	 */
	@Test
	public void setValidDensity() {
		fixture.setDensity(1.0);
	}
	
	/**
	 * Tests setting friction to a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeFriction() {
		fixture.setFriction(-2.0);
	}
	
	/**
	 * Tests setting friction to a valid value
	 */
	@Test
	public void setValidFriction() {
		fixture.setFriction(0.0);
		fixture.setFriction(1.0);
		fixture.setFriction(5.0);
	}
	
	/**
	 * Tests setting the restitution to a negative value.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setNegativeRestitution() {
		fixture.setRestitution(-1.0);
	}

	/**
	 * Tests setting restitution to a valid value
	 */
	@Test
	public void setValidRestitution() {
		fixture.setRestitution(0.0);
		fixture.setRestitution(1.0);
		fixture.setRestitution(5.0);
	}
}
