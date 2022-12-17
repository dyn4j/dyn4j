/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.dynamics.joint;

import java.util.Iterator;
import java.util.List;

import org.dyn4j.DataContainer;
import org.dyn4j.Ownable;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Vector2;

/**
 * Represents constrained motion between {@link PhysicsBody}s.
 * @author William Bittle
 * @version 5.0.0
 * @since 5.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public interface Joint<T extends PhysicsBody> extends Shiftable, DataContainer, Ownable {
	/**
	 * Returns an unmodifiable list of bodies involved in this joint.
	 * @return List&lt;T&gt;
	 */
	public abstract List<T> getBodies();
	
	/**
	 * Returns the number of bodies involved in this joint.
	 * @return int
	 */
	public abstract int getBodyCount();
	
	/**
	 * Returns the body at the given index.
	 * @param index the index
	 * @return T
	 * @throws IndexOutOfBoundsException when index is greater than or equal to {@link #getBodyCount()}
	 */
	public abstract T getBody(int index);
	
	/**
	 * Returns an iterator for the bodies involved in this joint.
	 * <p>
	 * NOTE: The iterator is read-only and will throw if methods like <code>remove</code> are used.
	 * @return Iterator&lt;T&gt;
	 */
	public abstract Iterator<T> getBodyIterator();
	
	/**
	 * Returns true if the given body is a member of this joint.
	 * @param body the body
	 * @return boolean
	 */
	public abstract boolean isMember(CollisionBody<?> body);
	
	/**
	 * Performs any initialization of the velocity and position constraints.
	 * @param step the time step information
	 * @param settings the current world settings
	 */
	public abstract void initializeConstraints(TimeStep step, Settings settings);
	
	/**
	 * Solves the velocity constraints.
	 * @param step the time step information
	 * @param settings the current world settings
	 */
	public abstract void solveVelocityConstraints(TimeStep step, Settings settings);
	
	/**
	 * Solves the position constraints.
	 * @param step the time step information
	 * @param settings the current world settings
	 * @return boolean true if the position constraints were solved
	 */
	public abstract boolean solvePositionConstraints(TimeStep step, Settings settings);
	
	/**
	 * Returns the force applied to the {@link PhysicsBody}s in order
	 * to satisfy the constraint in newtons.
	 * @param invdt the inverse delta time
	 * @return {@link Vector2}
	 */
	public abstract Vector2 getReactionForce(double invdt);
	
	/**
	 * Returns the torque applied to the {@link PhysicsBody}s in order
	 * to satisfy the constraint in newton-meters.
	 * @param invdt the inverse delta time
	 * @return double
	 */
	public abstract double getReactionTorque(double invdt);
	
	/**
	 * Returns true if this {@link Joint} is enabled.
	 * <p>
	 * A joint is only enabled if all joined {@link PhysicsBody}s are enabled.
	 * @return boolean
	 */
	public boolean isEnabled();
	
	/**
	 * Returns true if collision between the joined {@link PhysicsBody}s is allowed.
	 * @return boolean
	 */
	public boolean isCollisionAllowed();
	
	/**
	 * Sets whether collision is allowed between the joined {@link PhysicsBody}s.
	 * @param flag true if collisions are allowed
	 */
	public void setCollisionAllowed(boolean flag);
}
