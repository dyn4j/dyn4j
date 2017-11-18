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

import org.dyn4j.collision.continuous.TimeOfImpact;

/**
 * Represents the result of a convex cast.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.1.5
 */
public class ConvexCastResult implements Comparable<ConvexCastResult> {
	/** The body */
	protected Body body;
	
	/** The body fixture with the smallest time of impact */
	protected BodyFixture fixture;
	
	/** The time of impact information */
	protected TimeOfImpact timeOfImpact;
	
	/**
	 * Default constructor.
	 */
	public ConvexCastResult() {}
	
	/** 
	 * Full constructor.
	 * @param body the body
	 * @param fixture the fixture
	 * @param timeOfImpact the time of impact
	 */
	public ConvexCastResult(Body body, BodyFixture fixture, TimeOfImpact timeOfImpact) {
		this.body = body;
		this.fixture = fixture;
		this.timeOfImpact = timeOfImpact;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ConvexCastResult o) {
		return (int)Math.signum(this.timeOfImpact.getTime() - o.timeOfImpact.getTime());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ConvexCastResult[Body=").append(this.body)
		.append("|Fixture=").append(this.fixture)
		.append("|TimeOfImpact=").append(this.timeOfImpact)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the body.
	 * @return {@link Body}
	 */
	public Body getBody() {
		return this.body;
	}

	/**
	 * Sets the body.
	 * @param body the {@link Body}
	 */
	public void setBody(Body body) {
		this.body = body;
	}

	/**
	 * Returns the fixture on the body with the smallest
	 * time of impact.
	 * @return {@link BodyFixture}
	 */
	public BodyFixture getFixture() {
		return this.fixture;
	}
	
	/**
	 * Returns the time of impact information.
	 * @return {@link TimeOfImpact}
	 */
	public TimeOfImpact getTimeOfImpact() {
		return this.timeOfImpact;
	}

	/**
	 * Sets the fixture with the smallest time of impact.
	 * @param fixture the fixture
	 */
	public void setFixture(BodyFixture fixture) {
		this.fixture = fixture;
	}

	/**
	 * Sets the time of impact information.
	 * @param timeOfImpact the time of impact
	 */
	public void setTimeOfImpact(TimeOfImpact timeOfImpact) {
		this.timeOfImpact = timeOfImpact;
	}
}
