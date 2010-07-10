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
package org.dyn4j.game2d;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case for the {@link UnitConversion} class.
 * @author William Bittle
 */
public class UnitConversionTest {
	/**
	 * Tests the inch to meter and meter to inch conversions.
	 */
	@Test
	public void inchMeter() {
		// test the static values
		TestCase.assertEquals(1.000, UnitConversion.INCH_TO_METER * UnitConversion.METER_TO_INCH);
		
		// test a normal value
		double m = 2.5;
		double i = UnitConversion.metersToInches(m);
		double r = UnitConversion.inchesToMeters(i);
		
		TestCase.assertEquals(m, r, 1.0e-9);
	}
	
	/**
	 * Tests the foot to meter and meter to foot conversions.
	 */
	@Test
	public void footMeter() {
		// test the static values
		TestCase.assertEquals(1.000, UnitConversion.FOOT_TO_METER * UnitConversion.METER_TO_FOOT);
		
		// test a normal value
		double m = 2.5;
		double f = UnitConversion.metersToFeet(m);
		double r = UnitConversion.feetToMeters(f);
		
		TestCase.assertEquals(m, r, 1.0e-9);
	}
	
	/**
	 * Tests the yard to meter and meter to yard conversions.
	 */
	@Test
	public void yardMeter() {
		// test the static values
		TestCase.assertEquals(1.000, UnitConversion.YARD_TO_METER * UnitConversion.METER_TO_YARD);
		
		// test a normal value
		double m = 2.5;
		double y = UnitConversion.metersToYards(m);
		double r = UnitConversion.yardsToMeters(y);
		
		TestCase.assertEquals(m, r, 1.0e-9);
	}
	
	/**
	 * Tests the slug to kilogram and kilogram to slug conversions.
	 */
	@Test
	public void slugKilogram() {
		// test the static values
		TestCase.assertEquals(1.000, UnitConversion.SLUG_TO_KILOGRAM * UnitConversion.KILOGRAM_TO_SLUG);
		
		// test a normal value
		double s = 2.5;
		double k = UnitConversion.slugsToKilograms(s);
		double r = UnitConversion.kilogramsToSlugs(k);
		
		TestCase.assertEquals(s, r, 1.0e-9);
	}
	
	/**
	 * Tests the pound to kilogram and kilogram to pound conversions.
	 */
	@Test
	public void poundKilogram() {
		// test the static values
		TestCase.assertEquals(1.000, UnitConversion.POUND_TO_KILOGRAM * UnitConversion.KILOGRAM_TO_POUND);
		
		// test a normal value
		double p = 2.5;
		double k = UnitConversion.poundsToKilograms(p);
		double r = UnitConversion.kilogramsToPounds(k);
		
		TestCase.assertEquals(p, r, 1.0e-9);
	}
	
	/**
	 * Tests the pound to newton and newton to pound conversions.
	 */
	@Test
	public void poundNewton() {
		// test the static values
		TestCase.assertEquals(1.000, UnitConversion.POUND_TO_NEWTON * UnitConversion.NEWTON_TO_POUND);
		
		// test a normal value
		double p = 2.5;
		double n = UnitConversion.poundsToNewtons(p);
		double r = UnitConversion.newtonsToPounds(n);
		
		TestCase.assertEquals(p, r, 1.0e-9);
	}
	
	/**
	 * Tests the foot-pound to newton-meter and newton-meter to foot-pound conversions.
	 */
	@Test
	public void footPoundNewtonMeter() {
		// test the static values
		TestCase.assertEquals(1.000, UnitConversion.FOOT_POUND_TO_NEWTON_METER * UnitConversion.NEWTON_METER_TO_FOOT_POUND);
		
		// test a normal value
		double fp = 2.5;
		double nm = UnitConversion.footPoundsToNewtonMeters(fp);
		double r  = UnitConversion.newtonMetersToFootPounds(nm);
		
		TestCase.assertEquals(fp, r, 1.0e-9);
	}
}
