/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d.collision.continuous;

import org.dyn4j.game2d.collision.Collidable;
import org.dyn4j.game2d.collision.Fixture;
import org.dyn4j.game2d.collision.narrowphase.Separation;

/**
 * Represents the time of impact information between two {@link Swept}
 * {@link Collidable}s.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.2.0
 */
public class TimeOfImpact {
	/** The time of impact in the range [0, 1] */
	protected double toi;
	
	/** The separation at the time of impact */
	protected Separation separation;
	
	/** The closest {@link Fixture} on the first {@link Swept} {@link Collidable} */
	protected Fixture fixture1;
	
	/** The closest {@link Fixture} on the second {@link Swept} {@link Collidable} */
	protected Fixture fixture2;
	
	/**
	 * Default constructor.
	 */
	public TimeOfImpact() {}
	
	/**
	 * Full constructor.
	 * @param toi the time of impact; in the range [0, 1]
	 * @param separation the separation at the time of impact
	 * @param fixture1 the closest {@link Fixture} on the first {@link Swept} {@link Collidable}
	 * @param fixture2 the closest {@link Fixture} on the second {@link Swept} {@link Collidable}
	 */
	public TimeOfImpact(double toi, Separation separation, Fixture fixture1, Fixture fixture2) {
		this.toi = toi;
		this.separation = separation;
		this.fixture1 = fixture1;
		this.fixture2 = fixture2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TIME_OF_IMPACT[")
		.append(this.toi).append("|")
		.append(this.separation).append("|")
		.append(this.fixture1).append("|")
		.append(this.fixture2).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the time of impact in the range [0, 1].
	 * @return double
	 */
	public double getToi() {
		return this.toi;
	}
	
	/**
	 * Sets the time of impact.
	 * @param toi the time of impact in the range [0, 1]
	 */
	public void setToi(double toi) {
		this.toi = toi;
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
	
	/**
	 * Returns the closest {@link Fixture} on the first {@link Swept} {@link Collidable}.
	 * @return {@link Fixture}
	 * @since 2.0.0
	 */
	public Fixture getFixture1() {
		return fixture1;
	}
	
	/**
	 * Sets the closest {@link Fixture} on the first {@link Swept} {@link Collidable}.
	 * @param fixture1 the closest fixture
	 * @since 2.0.0
	 */
	public void setFixture1(Fixture fixture1) {
		this.fixture1 = fixture1;
	}
	
	/**
	 * Returns the closest {@link Fixture} on the second {@link Swept} {@link Collidable}.
	 * @return {@link Fixture}
	 * @since 2.0.0
	 */
	public Fixture getFixture2() {
		return fixture2;
	}
	
	/**
	 * Sets the closest {@link Fixture} on the second {@link Swept} {@link Collidable}.
	 * @param fixture2 the closest fixture
	 * @since 2.0.0
	 */
	public void setFixture2(Fixture fixture2) {
		this.fixture2 = fixture2;
	}
}
