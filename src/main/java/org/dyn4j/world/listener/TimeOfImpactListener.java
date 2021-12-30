/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.world.listener;

import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.world.PhysicsWorld;

/**
 * Interface to listen for time of impact events.
 * <p>
 * Time of impact events are events fired when a collision was missed by
 * the discrete collision detection routines, and then caught by the continuous
 * collision detection routines.
 * <p>
 * Modification of the {@link PhysicsWorld} is not permitted during these methods.
 * @author William Bittle
 * @version 4.2.1
 * @since 1.2.0
 * @param <T> the {@link PhysicsBody} type
 */
public interface TimeOfImpactListener<T extends PhysicsBody> extends WorldEventListener {
	/**
	 * Called when a time of impact has been detected between two bodies during the broad-phase.
	 * <p>
	 * Returning true from this method indicates that the collision of these
	 * two {@link PhysicsBody}s should be processed (solved).
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @return boolean true if the collision should be handled
	 * @since 4.1.0
	 */
	public abstract boolean collision(T body1, T body2);

	/**
	 * Called <b>before</b> the {@link PhysicsBody}s and {@link BodyFixture}s are tested for
	 * a time of impact collision.
	 * <p>
	 * Returning true from this method indicates that the collision of these
	 * two {@link PhysicsBody}s should be tested.
	 * @param body1 the first {@link PhysicsBody}
	 * @param fixture1 the first {@link PhysicsBody}'s {@link Fixture}
	 * @param body2 the second {@link PhysicsBody}
	 * @param fixture2 the first {@link PhysicsBody}'s {@link Fixture}
	 * @return boolean true if the collision should be handled
	 * @since 4.1.0
	 */
	public abstract boolean collision(T body1, BodyFixture fixture1, T body2, BodyFixture fixture2);
	
	/**
	 * Called when a time of impact has been detected between two bodies.
	 * <p>
	 * Returning true from this method indicates that the collision of these
	 * two {@link PhysicsBody}s should be processed (solved).
	 * <p>
	 * The values of the <code>toi</code> parameter can be changed in this method.
	 * @param body1 the first {@link PhysicsBody}
	 * @param fixture1 the first {@link PhysicsBody}'s {@link Fixture}
	 * @param body2 the second {@link PhysicsBody}
	 * @param fixture2 the second {@link PhysicsBody}'s {@link Fixture}
	 * @param toi the {@link TimeOfImpact}
	 * @return boolean true if the collision should be handled
	 * @since 2.0.0
	 */
	public abstract boolean collision(T body1, BodyFixture fixture1, T body2, BodyFixture fixture2, TimeOfImpact toi);
	
	/**
	 * Called when the minimum time of impact has been found for the first body.
	 * <p>
	 * Returning true from this method indicates that the collision of these
	 * two {@link PhysicsBody}s should be processed (solved).
	 * <p>
	 * The values of the <code>toi</code> parameter can be changed in this method.
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @param toi the {@link TimeOfImpact}
	 * @return boolean true if the collision should be handled
	 * @since 4.2.1
	 */
	public abstract boolean collision(T body1, T body2, TimeOfImpact toi);
}
