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
package org.dyn4j.dynamics.joint;

import org.dyn4j.DataContainer;
import org.dyn4j.Ownable;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Vector2;

/**
 * Represents constrained motion between two {@link PhysicsBody}s.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public abstract class Joint<T extends PhysicsBody> implements Shiftable, DataContainer, Ownable {
	/** The first linked body */
	protected final T body1;
	
	/** The second linked body */
	protected final T body2;
	
	/** Whether the pair of bodies joined together can collide with each other */
	protected boolean collisionAllowed;

	/** The user data */
	protected Object userData;
	
	/** [INTERNAL] The joint owner */
	protected Object owner;
	
	/**
	 * True if this joint is on an island.
	 * @deprecated Deprecated in 4.0.0. No replacement needed.
	 */
	@Deprecated
	boolean onIsland;
	
	/**
	 * Optional constructor.
	 * <p>
	 * Assumes that the joined bodies do not participate 
	 * in collision detection and resolution.
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @throws NullPointerException if body1 or body2 is null
	 */
	public Joint(T body1, T body2) {
		this(body1, body2, false);
	}
	
	/**
	 * Full constructor.
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @param collisionAllowed true if the joined {@link PhysicsBody}s can take part in collision detection
	 * @throws NullPointerException if body1 or body2 is null
	 */
	public Joint(T body1, T body2, boolean collisionAllowed) {
		this.body1 = body1;
		this.body2 = body2;
		this.collisionAllowed = collisionAllowed;
		this.onIsland = false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("HashCode=").append(this.hashCode());
		// body1, body2, island
		sb.append("|").append(super.toString())
		.append("|IsCollisionAllowed=").append(this.collisionAllowed);
		return sb.toString();
	}
	
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
	 * Returns the first body.
	 * @return T
	 */
	public T getBody1() {
		return this.body1;
	}
	
	/**
	 * Returns the second body.
	 * @return T
	 */
	public T getBody2() {
		return this.body2;
	}

	/**
	 * Returns the body that does not match the given body.
	 * <p>
	 * If the given body is neither body1 or body2, null is returned.
	 * @param body the body
	 * @return T
	 */
	public T getOtherBody(CollisionBody<?> body) {
		if (this.body1 == body) {
			return this.body2;
		} else if (this.body2 == body) {
			return this.body1;
		}
		return null;
	}
	
	/**
	 * Returns the anchor point on the first {@link PhysicsBody} in
	 * world coordinates.
	 * @return {@link Vector2}
	 */
	public abstract Vector2 getAnchor1();
	
	/**
	 * Returns the anchor point on the second {@link PhysicsBody} in
	 * world coordinates.
	 * @return {@link Vector2}
	 */
	public abstract Vector2 getAnchor2();
	
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
	 * Returns true if this {@link Joint} is active.
	 * <p>
	 * A joint is only active if both joined {@link PhysicsBody}s are active.
	 * @return boolean
	 * @deprecated Deprecated in 4.0.0. Use the isEnabled method instead
	 */
	@Deprecated
	public boolean isActive() {
		return this.body1.isActive() && this.body2.isActive();
	}
	
	/**
	 * Returns true if this {@link Joint} is enabled.
	 * <p>
	 * A joint is only enabled if both joined {@link PhysicsBody}s are enabled.
	 * @return boolean
	 */
	public boolean isEnabled() {
		return this.body1.isEnabled() && this.body2.isEnabled();
	}
	
	/**
	 * Returns true if collision between the joined {@link PhysicsBody}s is allowed.
	 * @return boolean
	 */
	public boolean isCollisionAllowed() {
		return this.collisionAllowed;
	}
	
	/**
	 * Sets whether collision is allowed between the joined {@link PhysicsBody}s.
	 * @param flag true if collisions are allowed
	 */
	public void setCollisionAllowed(boolean flag) {
		// is it different than the current value
		if (this.collisionAllowed != flag) {
			// wake up both bodies
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
			// set the new value
			this.collisionAllowed = flag;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.DataContainer#getUserData()
	 */
	public Object getUserData() {
		return this.userData;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.DataContainer#setUserData(java.lang.Object)
	 */
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
	/**
	 * Returns true if this joint is on an island.
	 * @return boolean
	 * @deprecated Deprecated in 4.0.0. No replacement needed.
	 */
	@Deprecated
	public boolean isOnIsland() {
		return this.onIsland;
	}
	
	/**
	 * Flags this joint as being on an island.
	 * @param flag true if this joint is on an island
	 * @deprecated Deprecated in 4.0.0. No replacement needed.
	 */
	@Deprecated
	public void setOnIsland(boolean flag) {
		this.onIsland = flag;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.Ownable#getOwner()
	 */
	public Object getOwner() {
		return this.owner;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Ownable#setOwner(java.lang.Object)
	 */
	public void setOwner(Object owner) {
		this.owner = owner;
	}
}
