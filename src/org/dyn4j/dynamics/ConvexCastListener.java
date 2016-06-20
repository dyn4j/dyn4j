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

import org.dyn4j.Listener;
import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.geometry.Convex;

/**
 * Interface to listen for convex cast events.
 * <p>
 * Modification of the {@link World} is not permitted during these methods.
 * <p>
 * By default all methods should return true.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.1.5
 */
public interface ConvexCastListener extends Listener {
	/**
	 * Called before the given fixture is cast against.
	 * <p>
	 * Returning true from this method indicates that the cast
	 * should be performed.
	 * <p>
	 * Use this method to filter fixtures from the tests to improve
	 * performance.
	 * @param convex the convex being cast
	 * @param body the body
	 * @param fixture the body fixture
	 * @return boolean true if the body should be tested
	 */
	public abstract boolean allow(Convex convex, Body body, BodyFixture fixture);
	
	/**
	 * Called after the given fixture has been cast against and a collision was
	 * detected.
	 * <p>
	 * Returning true from this method indicates that this time of impact result
	 * should be used by the final result.
	 * @param convex the convex being cast
	 * @param body the body
	 * @param fixture the body fixture
	 * @param toi the time of impact
	 * @return boolean true if the body should be tested
	 */
	public abstract boolean allow(Convex convex, Body body, BodyFixture fixture, TimeOfImpact toi);
}
