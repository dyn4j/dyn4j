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
package org.dyn4j.game2d.dynamics;

import org.dyn4j.game2d.geometry.Ray;

/**
 * Interface for listening for raycast events.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public interface RaycastListener {
	/**
	 * Enumeration for controlling the raycast method in the {@link World} class.
	 * @author William Bittle
	 * @version 2.0.0
	 * @since 2.0.0
	 */
	public static enum Return {
		/** Used to stop the raycast completely */
		STOP,
		/** Used to stop the raycast completely and ignore the last raycast */
		STOP_IGNORE,
		/** Used to keep the current raycast and continue */
		CONTINUE,
		/** Used to ignore the current raycast and continue */
		CONTINUE_IGNORE
	}
	
	/**
	 * Called when a {@link BodyFixture} of a {@link Body} is intersected by a {@link Ray}.
	 * <p>
	 * This method is called from the {@link World#raycast(Ray, double, boolean, boolean, java.util.List)}
	 * method and the return will affect how this method continues.
	 * @param ray the {@link Ray}
	 * @param result the raycast result
	 * @return {@link RaycastListener.Return}
	 * @see RaycastListener.Return
	 */
	public abstract RaycastListener.Return detected(Ray ray, RaycastResult result);
}
