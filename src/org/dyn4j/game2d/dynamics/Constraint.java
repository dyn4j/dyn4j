/*
 * Copyright (c) 2010, William Bittle
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

/**
 * Represents some physical constraint.
 * @author William Bittle
 */
public abstract class Constraint {
	/** The first {@link Body} */
	protected Body b1;
	
	/** The second {@link Body} */
	protected Body b2;
	
	/** Whether the {@link Constraint} has been added to an {@link Island} or not */
	protected boolean onIsland;
	
	/**
	 * Full constructor.
	 * @param b1 the first participating {@link Body}
	 * @param b2 the second participating {@link Body}
	 */
	public Constraint(Body b1, Body b2) {
		this.b1 = b1;
		this.b2 = b2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.b1).append("|")
		.append(this.b2).append("|")
		.append(this.onIsland);
		return sb.toString();
	}
	
	/**
	 * Returns the first {@link Body}.
	 * @return {@link Body}
	 */
	public Body getBody1() {
		return this.b1;
	}
	
	/**
	 * Returns the second {@link Body}.
	 * @return {@link Body}
	 */
	public Body getBody2() {
		return this.b2;
	}
	
	/**
	 * Sets the on {@link Island} flag to the given value.
	 * @param onIsland true if the {@link Constraint} has been added to an {@link Island}
	 */
	protected void setOnIsland(boolean onIsland) {
		this.onIsland = onIsland;
	}
	
	/**
	 * Returns true if this {@link Constraint} has been added
	 * to an {@link Island}
	 * @return boolean
	 */
	protected boolean isOnIsland() {
		return this.onIsland;
	}
}
