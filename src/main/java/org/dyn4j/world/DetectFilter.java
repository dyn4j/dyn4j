/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.world;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Filter;
import org.dyn4j.collision.Fixture;

/**
 * Represents the filters for queries against a {@link CollisionWorld}.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public class DetectFilter<T extends CollisionBody<E>, E extends Fixture> {
	/** True if fixtures flagged as sensors should be ignored */
	private final boolean ignoreSensorsEnabled; 
	
	/** True if disabled bodies/fixtures should be ignored */
	private final boolean ignoreDisabledEnabled;
	
	/** A {@link Filter} to filter fixtures */
	private final Filter filter;

	/**
	 * Minimal constructor.
	 * @param ignoreSensors true if fixtures flagged as sensors should be ignored
	 * @param ignoreDisabled true if disabled bodies/fixtures should be ignored
	 * @param filter the {@link Filter} to filter fixtures; can be null
	 */
	public DetectFilter(boolean ignoreSensors, boolean ignoreDisabled, Filter filter) {
		super();
		this.ignoreSensorsEnabled = ignoreSensors;
		this.ignoreDisabledEnabled = ignoreDisabled;
		this.filter = filter;
	}

	/**
	 * Returns true if the given {@link CollisionBody} and {@link Fixture} is
	 * allowed to be in the results of the query.
	 * @param body the body
	 * @param fixture the fixture
	 * @return boolean
	 */
	public boolean isAllowed(T body, E fixture) {
		// check for inactive
		if (this.ignoreDisabledEnabled && !body.isEnabled()) {
			return false;
		}
		
		// check for sensor
		if (this.ignoreSensorsEnabled && fixture.isSensor()) {
			return false;
		}
		
		// check against the filter
		if (this.filter != null && !this.filter.isAllowed(fixture.getFilter())) {
			return false;
		}
		
		return true;
	}

	/**
	 * Returns true if sensor fixtures should be ignored.
	 * @return boolean
	 */
	public boolean isIgnoreSensorsEnabled() {
		return this.ignoreSensorsEnabled;
	}

	/**
	 * Returns true if disabled bodies should be ignored.
	 * @return boolean
	 */
	public boolean IsIgnoreDisabledEnabled() {
		return this.ignoreDisabledEnabled;
	}

	/**
	 * Returns the fixture-level filter.
	 * @return {@link Filter}
	 */
	public Filter getFilter() {
		return this.filter;
	}
}
