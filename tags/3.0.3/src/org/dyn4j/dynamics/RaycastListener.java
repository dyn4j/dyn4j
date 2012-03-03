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

import org.dyn4j.geometry.Ray;

/**
 * Interface for listening for raycast events.
 * @author William Bittle
 * @version 3.0.0
 * @since 2.0.0
 */
public interface RaycastListener {
	/**
	 * Called before a {@link Body} is tested against the {@link Ray}.  This method will be
	 * called for every body in the {@link World}.
	 * <p>
	 * Use this method to filter the raycasting based on the {@link Body}.
	 * @param ray the {@link Ray}
	 * @param body the {@link Body} to be tested
	 * @return boolean true if the {@link Body} should be included in the raycast
	 * @since 3.0.0
	 */
	public abstract boolean allow(Ray ray, Body body);
	
	/**
	 * Called when a {@link BodyFixture} of a {@link Body} is intersected by a {@link Ray}.
	 * This method will be called for every fixture on each {@link Body}.
	 * <p>
	 * Use this method to filter the raycasting based on the {@link BodyFixture}.
	 * @param ray the {@link Ray}
	 * @param body the {@link Body}
	 * @param fixture the {@link BodyFixture} to be tested
	 * @return boolean true if the {@link BodyFixture} should be included in the raycast
	 * @since 3.0.0
	 */
	public abstract boolean allow(Ray ray, Body body, BodyFixture fixture);
}
