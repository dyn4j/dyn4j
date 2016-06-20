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
package org.dyn4j.dynamics;

import org.dyn4j.collision.narrowphase.Penetration;

/**
 * Represents the result of a static detection of the world.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.1.9
 */
public class DetectResult {
	/** The overlapping {@link Body} */
	protected Body body;
	
	/** The overlapping {@link BodyFixture} */
	protected BodyFixture fixture;
	
	/** The overlap {@link Penetration}; may be null */
	protected Penetration penetration;
	
	/**
	 * Default constructor.
	 */
	public DetectResult() {}
	
	/**
	 * Optional constructor.
	 * @param body the body 
	 * @param fixture the fixture
	 */
	public DetectResult(Body body, BodyFixture fixture) {
		this(body, fixture, null);
	}
	
	/**
	 * Full constructor.
	 * @param body the body 
	 * @param fixture the fixture
	 * @param penetration the penetration; can be null
	 */
	public DetectResult(Body body, BodyFixture fixture, Penetration penetration) {
		this.body = body;
		this.fixture = fixture;
		this.penetration = penetration;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DetectResult[Body=").append(this.body.getId())
		  .append("|Fixture=").append(this.fixture.getId())
		  .append("|Penetration=").append(this.penetration)
		  .append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the overlapping body.
	 * @return {@link Body}
	 */
	public Body getBody() {
		return this.body;
	}

	/**
	 * Sets the overlapping body.
	 * @param body the {@link Body}
	 */
	public void setBody(Body body) {
		this.body = body;
	}

	/**
	 * Returns the overlapping fixture.
	 * @return {@link BodyFixture}
	 */
	public BodyFixture getFixture() {
		return this.fixture;
	}

	/**
	 * Sets the overlapping fixture.
	 * @param fixture the {@link BodyFixture}
	 */
	public void setFixture(BodyFixture fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the overlap penetration (collision data).
	 * <p>
	 * This will return null if the collision data was flagged to not be included. 
	 * @return {@link Penetration}
	 */
	public Penetration getPenetration() {
		return this.penetration;
	}

	/**
	 * Sets the overlap penetration (collision data).
	 * @param penetration the {@link Penetration}; can be null
	 */
	public void setPenetration(Penetration penetration) {
		this.penetration = penetration;
	}
}
