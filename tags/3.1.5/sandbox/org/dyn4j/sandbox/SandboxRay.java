/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.sandbox;

import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;

/**
 * Extended ray class for the Sandbox.
 * <p>
 * Adds storage for the name, length, and flags.
 * @author William Bittle
 * @version 3.0.2
 * @since 3.0.2
 */
public class SandboxRay extends Ray {
	/** An infinite length ray */
	public static final double INFINITE = 0.0;
	
	/** The ray name */
	protected String name;
	
	/** The length of the ray */
	protected double length;
	
	/** True if sensor fixtures should be ignored by this ray */
	protected boolean sensors;
	
	/** True if all fixtures intersecting the ray should be returned or just the first */
	protected boolean all;
	
	/**
	 * Creates a ray from the origin in the given direction.
	 * @param name the name
	 * @param direction the direction in radians
	 */
	public SandboxRay(String name, double direction) {
		super(direction);
		this.name = name;
	}

	/**
	 * Creates a ray with the given start point in the given direction.
	 * @param name the name
	 * @param start the start point
	 * @param direction the direction in radians
	 */
	public SandboxRay(String name, Vector2 start, double direction) {
		super(start, direction);
		this.name = name;
	}
	
	/**
	 * Creates a ray from the origin in the given direction.
	 * @param name the name
	 * @param direction the direction vector; must be normalized
	 */
	public SandboxRay(String name, Vector2 direction) {
		super(direction);
		this.name = name;
	}
	
	/**
	 * Creates a ray with the given start point in the given direction.
	 * @param name the name
	 * @param start the start point
	 * @param direction the direction vector; must be normalized
	 */
	public SandboxRay(String name, Vector2 start, Vector2 direction) {
		super(start, direction);
		this.name = name;
	}
	
	/**
	 * Returns the name of this ray.
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of this ray.
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the length of this ray.
	 * <p>
	 * A zero length indicates an infinite length ray.
	 * @return double
	 */
	public double getLength() {
		return length;
	}
	
	/**
	 * Sets the length of this ray.
	 * <p>
	 * Use {@link SandboxRay#INFINITE} to set the length to infinite. 
	 * @param length the length in meters; must be greater than zero
	 */
	public void setLength(double length) {
		this.length = length;
	}
	
	/**
	 * Returns true if sensor fixtures should be ignored.
	 * @return boolean
	 */
	public boolean isIgnoreSensors() {
		return sensors;
	}
	
	/**
	 * Sets whether sensor fixtures should be ignored.
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 */
	public void setIgnoreSensors(boolean ignoreSensors) {
		this.sensors = ignoreSensors;
	}
	
	/**
	 * Returns true if all intersecting bodies should be returned, false if
	 * only the closest.
	 * @return boolean
	 */
	public boolean isAll() {
		return all;
	}
	
	/**
	 * Sets whether all intersecting bodies should be return or just the closest.
	 * @param all true if all intersecting bodies should be returned; false if just the closest should be returned
	 */
	public void setAll(boolean all) {
		this.all = all;
	}
}
