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
package org.dyn4j.game2d.dynamics;

/**
 * Represents a torque about the z-axis.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class Torque {
	/** The point where the {@link Force} is applied in world coordinates */
	protected double torque;
	
	/**
	 * Default constructor.
	 * @since 1.0.2
	 */
	public Torque() {
		this.torque = 0.0;
	}
	
	/**
	 * Creates a {@link Torque} using specified torque value.
	 * @param torque the torque
	 */
	public Torque(double torque) {
		this.torque = torque;
	}
	
	/**
	 * Copy constructor.
	 * @param torque the {@link Torque} to copy
	 * @throws NullPointerException if torque is null
	 */
	public Torque(Torque torque) {
		if (torque == null) throw new NullPointerException("Cannot copy a null torque.");
		this.torque = torque.torque;
	}
	
	/**
	 * Sets this {@link Torque} to the given torque value.
	 * @param torque the torque
	 */
	public void set(double torque) {
		this.torque = torque;
	}
	
	/**
	 * Sets this {@link Torque} to the given {@link Torque}.
	 * @param torque the {@link Torque} to copy
	 * @throws NullPointerException if torque is null
	 */
	public void set(Torque torque) {
		if (torque == null) throw new NullPointerException("Cannot set this torque to a null torque.");
		this.torque = torque.torque;
	}
	
	/**
	 * Applies this {@link Torque} to the given {@link Body}.
	 * @param body the {@link Body} to apply the {@link Torque} to
	 */
	public void apply(Body body) {
		body.torque += this.torque;
	}

	/**
	 * Returns true if this torque should be removed.
	 * <p>
	 * Implement this method to create {@link Torque} objects
	 * that are not cleared each iteration by the {@link World}.
	 * <p>
	 * The default implementation always returns true.
	 * @return boolean true if this torque should be removed
	 * @since 2.0.0
	 */
	public boolean isComplete() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.Force#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TORQUE[").append(this.torque).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the torque value.
	 * @return double
	 */
	public double getTorque() {
		return this.torque;
	}
}
