/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.Epsilon;
import org.dyn4j.collision.narrowphase.Raycast;

/**
 * Represents the result of a raycast.
 * <p>
 * Implements the Comparable interface to allow for sorting by the distance.
 * @author William Bittle
 * @version 3.0.0
 * @since 2.0.0
 */
public class RaycastResult implements Comparable<RaycastResult> {
	/** The {@link Body} detected */
	protected Body body;
	
	/** The {@link BodyFixture} of the {@link Body} detected */
	protected BodyFixture fixture;
	
	/** The {@link Raycast} result information */
	protected Raycast raycast;
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(RaycastResult o) {
		double value = this.raycast.getDistance() - o.raycast.getDistance();
		if (value > Epsilon.E) {
			return 1;
		} else if (value < Epsilon.E) {
			return -1;
		} else {
			return 0;
		}
	}
	
	/**
	 * Returns the {@link Body} detected.
	 * @return {@link Body}
	 */
	public Body getBody() {
		return body;
	}
	
	/**
	 * Sets the detected {@link Body}.
	 * @param body the body detected
	 */
	public void setBody(Body body) {
		this.body = body;
	}
	
	/**
	 * Returns the {@link BodyFixture} of the {@link Body} detected.
	 * @return {@link BodyFixture}
	 */
	public BodyFixture getFixture() {
		return fixture;
	}
	
	/**
	 * Sets the {@link BodyFixture} of the {@link Body} detected.
	 * @param fixture the fixture of the body detected
	 */
	public void setFixture(BodyFixture fixture) {
		this.fixture = fixture;
	}
	
	/**
	 * Returns the {@link Raycast} result information.
	 * @return {@link Raycast}
	 */
	public Raycast getRaycast() {
		return raycast;
	}
	
	/**
	 * Sets the {@link Raycast} result information.
	 * @param raycast the raycast result information
	 */
	public void setRaycast(Raycast raycast) {
		this.raycast = raycast;
	}
}
