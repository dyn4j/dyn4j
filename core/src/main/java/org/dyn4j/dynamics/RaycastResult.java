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

import org.dyn4j.collision.narrowphase.Raycast;

/**
 * Represents the result of a raycast.
 * <p>
 * Implements the Comparable interface to allow for sorting by the distance.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 * @author William Bittle
 * @version 3.2.0
 * @since 2.0.0
 */
public class RaycastResult implements Comparable<RaycastResult> {
	/** The {@link Body} detected */
	protected Body body;
	
	/** The {@link BodyFixture} of the {@link Body} detected */
	protected BodyFixture fixture;
	
	/** The {@link Raycast} result information */
	protected Raycast raycast;
	
	/**
	 * Default constructor.
	 */
	public RaycastResult() {}
	
	/**
	 * Full constructor.
	 * @param body the body
	 * @param fixture the fixture
	 * @param raycast the raycast
	 */
	public RaycastResult(Body body, BodyFixture fixture, Raycast raycast) {
		this.body = body;
		this.fixture = fixture;
		this.raycast = raycast;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RaycastResult o) {
		return (int)Math.signum(this.raycast.getDistance() - o.raycast.getDistance());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RaycastResult[Body=").append(this.body)
		.append("|Fixture=").append(this.fixture)
		.append("|Raycast=").append(this.raycast)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the {@link Body} detected.
	 * @return {@link Body}
	 */
	public Body getBody() {
		return this.body;
	}

	/**
	 * Sets the {@link Body} detected.
	 * @param body the {@link Body}
	 */
	public void setBody(Body body) {
		this.body = body;
	}

	/**
	 * Returns the {@link BodyFixture} of the {@link Body} detected.
	 * @return {@link BodyFixture}
	 */
	public BodyFixture getFixture() {
		return this.fixture;
	}

	/**
	 * Sets the {@link BodyFixture} of the {@link Body} detected.
	 * @param fixture the {@link BodyFixture}
	 */
	public void setFixture(BodyFixture fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the {@link Raycast} result information.
	 * @return {@link Raycast}
	 */
	public Raycast getRaycast() {
		return this.raycast;
	}

	/**
	 * Sets the {@link Raycast} result information.
	 * @param raycast the {@link Raycast}
	 */
	public void setRaycast(Raycast raycast) {
		this.raycast = raycast;
	}
}
