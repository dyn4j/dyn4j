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
package org.dyn4j.sandbox.actions;

import org.dyn4j.geometry.Vector2;

/**
 * Action to store information about rotating an object.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class RotateAction extends Action {
	/** The begin position of the rotation */
	private Vector2 beginPosition;
	
	/**
	 * Begins the rotate action with the specified begin position in world coordinates.
	 * @param mousePosition the begin position in world coordinates
	 */
	public synchronized void begin(Vector2 mousePosition) {
		this.active = true;
		this.beginPosition = mousePosition;
	}
	
	/**
	 * Updates the action with the new begin position in world coordinates.
	 * <p>
	 * This is used if the action is carried out over a time period in which the
	 * user would like visual feedback.
	 * @param mousePosition the new begin position in world coordinates
	 */
	public synchronized void update(Vector2 mousePosition) {
		this.beginPosition = mousePosition;
	}
	
	/**
	 * Ends the rotate action.
	 */
	public synchronized void end() {
		this.active = false;
		this.beginPosition = null;
	}
	
	/**
	 * Returns the begin position of the rotate action in world coordinates.
	 * <p>
	 * Returns null if the action is inactive.
	 * @return Vector2
	 * @see #isActive()
	 */
	public synchronized Vector2 getBeginPosition() {
		return this.beginPosition;
	}
}
