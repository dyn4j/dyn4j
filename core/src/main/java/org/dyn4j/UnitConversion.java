/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j;

/**
 * Class used to convert units.
 * <p>
 * dyn4j uses meters-kilograms-seconds (MKS) units by default.  This class can be used
 * to convert to and from MKS.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public final class UnitConversion {
	/** 1 foot = {@value #FOOT_TO_METER} meters */
	public static final double FOOT_TO_METER = 0.0254 * 12.0;
	
	/** 1 meter = {@value #METER_TO_FOOT} feet */
	public static final double METER_TO_FOOT = 1.0 / FOOT_TO_METER;
	
	/** 1 slug = {@value #SLUG_TO_KILOGRAM} kilograms */
	public static final double SLUG_TO_KILOGRAM = 14.5939029;
	
	/** 1 kilogram = {@value #KILOGRAM_TO_SLUG} slugs */
	public static final double KILOGRAM_TO_SLUG = 1.0 / SLUG_TO_KILOGRAM;
	
	/** 1 pound-mass = {@value #POUND_TO_KILOGRAM} kilograms */
	public static final double POUND_TO_KILOGRAM = 0.45359237;
	
	/** 1 kilogram = {@value #KILOGRAM_TO_POUND} pounds */
	public static final double KILOGRAM_TO_POUND = 1.0 / POUND_TO_KILOGRAM;
	
	/** 1 pound-force = {@value #POUND_TO_NEWTON} newtons */
	public static final double POUND_TO_NEWTON = 4.448222;
	
	/** 1 newton = {@value #NEWTON_TO_POUND} pound-force */
	public static final double NEWTON_TO_POUND = 1.0 / POUND_TO_NEWTON;
	
	/** 1 foot-pound = {@value #FOOT_POUND_TO_NEWTON_METER} newton-meters */
	public static final double FOOT_POUND_TO_NEWTON_METER = 0.7375621;
	
	/** 1 newton-meter = {@value #NEWTON_METER_TO_FOOT_POUND} foot-pounds */
	public static final double NEWTON_METER_TO_FOOT_POUND = 1.0 / FOOT_POUND_TO_NEWTON_METER;
	
	/**
	 * Hidden constructor.
	 */
	private UnitConversion() {}
	
	// FPS (mixture of Gravitational and Engineering approaches) to MKS
	
	// Length Conversions
	
	/**
	 * Converts feet to meters.
	 * @param feet the length value in feet
	 * @return double the length value in meters
	 */
	public static final double feetToMeters(double feet) {
		return feet * FOOT_TO_METER;
	}
	
	// Mass Conversions
	
	/**
	 * Converts slugs to kilograms.
	 * @param slugs the mass value in slugs
	 * @return double the mass value in kilograms
	 */
	public static final double slugsToKilograms(double slugs) {
		return slugs * SLUG_TO_KILOGRAM;
	}
	
	/**
	 * Converts pound-mass to kilograms.
	 * @param pound the mass value in pound-masses
	 * @return double the mass value in kilograms
	 */
	public static final double poundsToKilograms(double pound) {
		return pound * POUND_TO_KILOGRAM;
	}
	
	// Velocity Conversions
	
	/**
	 * Converts feet per second to meters per second.
	 * @param feetPerSecond the velocity in feet per second
	 * @return double the velocity in meters per second
	 */
	public static final double feetPerSecondToMetersPerSecond(double feetPerSecond) {
		return feetPerSecond * METER_TO_FOOT;
	}
	
	// Force Conversions
	
	/**
	 * Converts pound-force to newtons.
	 * @param pound the force value in pound-force
	 * @return double the force value in newtons
	 */
	public static final double poundsToNewtons(double pound) {
		return pound * POUND_TO_NEWTON;
	}
	
	// Torque Conversions
	
	/**
	 * Converts foot-pounds to newton-meters.
	 * @param footPound the torque value in foot-pounds
	 * @return double the torque value in newton-meters
	 */
	public static final double footPoundsToNewtonMeters(double footPound) {
		return footPound * FOOT_POUND_TO_NEWTON_METER;
	}
	
	// MKS to FPS (mixture of Gravitational and Engineering approaches)
	
	// Length Conversions
	
	/**
	 * Converts meters to feet.
	 * @param meters the length value in meters
	 * @return double the length value in feet
	 */
	public static final double metersToFeet(double meters) {
		return meters * METER_TO_FOOT;
	}
	
	// Mass Conversions
	
	/**
	 * Converts kilograms to slugs.
	 * @param kilograms the mass value in kilograms
	 * @return double the mass value in slugs
	 */
	public static final double kilogramsToSlugs(double kilograms) {
		return kilograms * KILOGRAM_TO_SLUG;
	}
	
	/**
	 * Converts kilograms to pound-mass.
	 * @param kilograms the mass value in kilograms
	 * @return double the mass value in pound-masses
	 */
	public static final double kilogramsToPounds(double kilograms) {
		return kilograms * KILOGRAM_TO_POUND;
	}

	// Velocity Conversions
	
	/**
	 * Converts meters per second to feet per second.
	 * @param metersPerSecond the velocity in meters per second
	 * @return double the velocity in feet per second
	 */
	public static final double metersPerSecondToFeetPerSecond(double metersPerSecond) {
		return metersPerSecond * FOOT_TO_METER;
	}
	
	// Force Conversions
	
	/**
	 * Converts newtons to pound-force.
	 * @param newtons the force value in newtons
	 * @return double the force value in pound-force
	 */
	public static final double newtonsToPounds(double newtons) {
		return newtons * NEWTON_TO_POUND;
	}
	
	// Torque Conversions
	
	/**
	 * Converts newton-meters to foot-pounds.
	 * @param newtonMeters the torque value in newton-meters
	 * @return double the torque value in foot-pounds
	 */
	public static final double newtonMetersToFootPounds(double newtonMeters) {
		return newtonMeters * NEWTON_METER_TO_FOOT_POUND;
	}
}
