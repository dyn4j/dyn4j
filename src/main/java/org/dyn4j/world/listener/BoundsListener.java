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
package org.dyn4j.world.listener;

import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;

/**
 * Represents an object that is notified when a {@link CollisionBody} goes out of {@link Bounds}.
 * <p>
 * NOTE: Modification of the simulation in these methods can cause unexpected behavior.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 * @see Bounds
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public interface BoundsListener<T extends CollisionBody<E>, E extends Fixture> extends WorldEventListener {
	/**
	 * Method called when a {@link CollisionBody} is outside the {@link Bounds} of a simulation.
	 * <p>
	 * The {@link CollisionBody} must be fully outside the bounds defined in the world. This means that
	 * all the fixtures are outside the bounds.
	 * <p>
	 * When this is detected the {@link CollisionBody} is set to disabled and is effectively ignored until
	 * moved back within the bounds and enabled again.
	 * <p>
	 * Typically this event is used to clean up bodies that have strayed from the simulation, but be aware
	 * that the removal of the body cannot be done here. Instead it must be done after the simulation step
	 * completes.
	 * @param body the {@link CollisionBody} outside the {@link Bounds}
	 */
	public void outside(T body);
}
