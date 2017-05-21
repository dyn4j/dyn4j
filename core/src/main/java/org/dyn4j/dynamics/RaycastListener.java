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
import org.dyn4j.collision.narrowphase.Raycast;
import org.dyn4j.geometry.Ray;

/**
 * Interface for listening for raycast events.
 * <p>
 * Modification of the {@link World} is not permitted inside these methods.
 * <p>
 * By default all methods should return true.
 * @author William Bittle
 * @version 3.2.0
 * @since 2.0.0
 */
public interface RaycastListener extends Listener {
	/**
	 * Called before a {@link BodyFixture} is tested against the {@link Ray}.
	 * <p>
	 * Use this method to filter the ray casting based on the {@link BodyFixture}.
	 * @param ray the {@link Ray}
	 * @param body the {@link Body}
	 * @param fixture the {@link BodyFixture} to be tested
	 * @return boolean true if the {@link BodyFixture} should be included in the raycast
	 * @since 3.0.0
	 */
	public abstract boolean allow(Ray ray, Body body, BodyFixture fixture);
	
	/**
	 * Called after a successful raycast of the given {@link Body} and {@link BodyFixture}.
	 * <p>
	 * Use this method to filter the raycasting based on the {@link Raycast} result.
	 * @param ray the {@link Ray}
	 * @param body the {@link Body}
	 * @param fixture the {@link BodyFixture} to be tested
	 * @param raycast the {@link Raycast} result
	 * @return boolean true if the {@link Raycast} result should be allowed
	 * @since 3.1.5
	 */
	public abstract boolean allow(Ray ray, Body body, BodyFixture fixture, Raycast raycast);
}
