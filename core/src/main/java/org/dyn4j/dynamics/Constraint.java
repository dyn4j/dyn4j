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

import org.dyn4j.geometry.Shiftable;
import org.dyn4j.resources.Messages;

/**
 * Represents some physical constraint between a pair of {@link Body}s.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public abstract class Constraint implements Shiftable {
	/** The first {@link Body} */
	protected final Body body1;
	
	/** The second {@link Body} */
	protected final Body body2;
	
	// internal
	
	/** Whether the {@link Constraint} has been added to an {@link Island} or not */
	boolean onIsland;

	/** 
	 * The world the constraint has been added to
	 * <p>
	 * This is really only used for Joints to determine if they have been added
	 * to a world already. 
	 */
	World world;
	
	/**
	 * Full constructor.
	 * @param body1 the first participating {@link Body}
	 * @param body2 the second participating {@link Body}
	 * @throws NullPointerException if body1 or body2 is null
	 */
	public Constraint(Body body1, Body body2) {
		// the bodies cannot be null
		if (body1 == null) throw new NullPointerException(Messages.getString("dynamics.constraint.nullBody1"));
		if (body2 == null) throw new NullPointerException(Messages.getString("dynamics.constraint.nullBody2"));
		this.body1 = body1;
		this.body2 = body2;
		this.onIsland = false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Body1=").append(this.body1)
		.append("|Body2=").append(this.body2);
		return sb.toString();
	}
	
	/**
	 * Returns the first {@link Body}.
	 * @return {@link Body}
	 */
	public Body getBody1() {
		return this.body1;
	}
	
	/**
	 * Returns the second {@link Body}.
	 * @return {@link Body}
	 */
	public Body getBody2() {
		return this.body2;
	}
	
	/**
	 * Sets the on {@link Island} flag to the given value.
	 * @param onIsland true if the {@link Constraint} has been added to an {@link Island}
	 */
	void setOnIsland(boolean onIsland) {
		this.onIsland = onIsland;
	}
	
	/**
	 * Returns true if this {@link Constraint} has been added
	 * to an {@link Island}
	 * @return boolean
	 */
	boolean isOnIsland() {
		return this.onIsland;
	}
}
