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

/**
 * Interface to listen for time of impact events.
 * <p>
 * Time of impact events are events fired when a collision was missed by
 * the discrete collision detection routines, and then caught by the continuous
 * collision detection routines.
 * <p>
 * Modification of the {@link World} is not permitted during these methods.
 * @author William Bittle
 * @version 3.1.5
 * @since 1.2.0
 */
public interface TimeOfImpactListener extends Listener {
	/**
	 * Called when a time of impact has been detected between two bodies.
	 * <p>
	 * Returning true from this method indicates that the collision of these
	 * two {@link Body}s should be processed (solved).
	 * <p>
	 * The values of the <code>toi</code> parameter can be changed in this method.
	 * @param body1 the first {@link Body}
	 * @param fixture1 the first {@link Body}'s fixture
	 * @param body2 the second {@link Body}
	 * @param fixture2 the second {@link Body}'s fixture
	 * @param toi the {@link TimeOfImpact}
	 * @return boolean true if the collision should be handled
	 * @since 2.0.0
	 */
	public abstract boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, TimeOfImpact toi);
}
