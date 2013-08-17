/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.sandbox.input;

/**
 * Represents a mouse button used to hold its current state.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MouseButton {
	/** The MouseEvent code */
	private int code;
	
	/** The current value (number of clicks) */
	private int value;
	
	/** True if the button is pressed and waiting for release */
	private boolean pressed;
	
	/** True if the button was waiting to be released and it was released */
	private boolean released;
	
	/**
	 * Full constructor.
	 * @param code the MouseEvent code
	 */
	public MouseButton(int code) {
		this.code = code;
		this.value = 0;
		this.pressed = false;
		this.released = false;
	}
	
	/**
	 * Sets the value of this mouse button.
	 * <p>
	 * The value indicates the number of clicks issued.
	 * @param value the value or number of clicks
	 */
	public synchronized void setValue(int value) {
		this.value = value;
	}
	
	/**
	 * Returns true if this mouse button was clicked once.
	 * <p>
	 * Returns false if the button was double/triple/etc clicked or is currently
	 * waiting to be released.
	 * @return boolean
	 */
	public synchronized boolean wasClicked() {
		return this.value == 1;
	}
	
	/**
	 * Returns true if this mouse button was double clicked.
	 * <p>
	 * Returns false if the button was single/triple/etc clicked or is currently
	 * waiting to be released.
	 * @return boolean
	 */
	public synchronized boolean wasDoubleClicked() {
		return this.value == 2;
	}
	
	/**
	 * Returns the current value of this mouse button.
	 * <p>
	 * This represents the number of clicks.
	 * @return int
	 */
	public synchronized int getValue() {
		return this.value;
	}
	
	/**
	 * Sets the pressed flag.
	 * <p>
	 * The pressed flag indicates the button is currently
	 * pressed and waiting for release.
	 * @param flag true if the button is pressed and waiting for release
	 */
	public synchronized void setPressed(boolean flag) {
		this.pressed = flag;
	}
	
	/**
	 * Returns true if this button is currently pressed and is waiting for release.
	 * @return boolean
	 */
	public synchronized boolean isPressed() {
		return this.pressed;
	}
	
	/**
	 * Sets the was released flag.
	 * <p>
	 * The was released flag indicates the button was pressed and
	 * waiting for release and is now released.
	 * <p>
	 * This state should be cleared after use.
	 * @param flag true if the button was pressed and waiting for release and is now released
	 */
	public synchronized void setWasReleased(boolean flag) {
		this.released = flag;
	}
	
	/**
	 * Returns true if this button was pressed and waiting for release but is now released.
	 * @return boolean
	 */
	public synchronized boolean wasReleased() {
		return this.released;
	}
	
	/**
	 * Clears the state of the button.
	 */
	public synchronized void clear() {
		this.value = 0;
		this.released = false;
	}
	
	/**
	 * Returns the MouseEvent code for this button.
	 * @return int
	 */
	public int getCode() {
		return this.code;
	}
}
