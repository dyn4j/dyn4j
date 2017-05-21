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
package org.dyn4j.collision.continuous;

import org.dyn4j.collision.narrowphase.Separation;

/**
 * Represents the time of impact information between two objects.
 * <p>
 * The {@link #getTime()} is in the range of [0, 1] and represents the time within the current
 * timestep that the collision occurred.
 * @author William Bittle
 * @version 3.1.5
 * @since 1.2.0
 */
public class TimeOfImpact {
	/** The time of impact in the range [0, 1] */
	protected double time;
	
	/** The separation at the time of impact */
	protected Separation separation;
	
	/**
	 * Default constructor.
	 */
	public TimeOfImpact() {}
	
	/**
	 * Full constructor.
	 * @param time the time of impact; in the range [0, 1]
	 * @param separation the separation at the time of impact
	 */
	public TimeOfImpact(double time, Separation separation) {
		this.time = time;
		this.separation = separation;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TimeOfImpact[Time=").append(this.time)
		.append("|Separation=").append(this.separation)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the time of impact in the range [0, 1].
	 * @return double
	 * @since 3.1.5
	 */
	public double getTime() {
		return this.time;
	}
	
	/**
	 * Sets the time of impact.
	 * @param time the time of impact in the range [0, 1]
	 * @since 3.1.5
	 */
	public void setTime(double time) {
		this.time = time;
	}
	
	/**
	 * Returns the separation at the time of impact.
	 * @return {@link Separation}
	 */
	public Separation getSeparation() {
		return this.separation;
	}
	
	/**
	 * Sets the separation at the time of impact.
	 * @param separation the separation
	 */
	public void setSeparation(Separation separation) {
		this.separation = separation;
	}
}
