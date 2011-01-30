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
package org.dyn4j.game2d.testbed.input;

/**
 * Represents an input of an input device.
 * @author William Bittle
 * @version 2.2.3
 * @since 2.2.3
 */
public class Input {
	/**
	 * This enumeration represents the action taken when the input is held hown.
	 * @author William Bittle
	 * @version 2.2.3
	 * @since 2.2.3
	 */
	public static enum Hold {
		/** The isPressed method will return true as long as the input is held down */
		HOLD,
		/** The isPressed method will return true only when the input has been released and pressed again. */
		NO_HOLD
	}
	
	/**
	 * This enumeration represents the state of an input.
	 * @author William Bittle
	 * @version 2.2.3
	 * @since 2.2.3
	 */
	public static enum State {
		/** the key has been released */
		RELEASED,
		/** the key has been pressed */
		PRESSED,
		/** the key is currenlty pressed */
		WAITING_FOR_RELEASE
	}

	/** What happens when the input is held */
	private Input.Hold holdType = Input.Hold.HOLD;
	
	/** The input state */
	private Input.State state = Input.State.RELEASED;
	
	/** The event id */
	private int event = 0;
	
	/** The input count */
	private int value = 0;

	/**
	 * Minimal constructor.
	 * @param event the event id
	 */
	public Input(int event) {
		this(event, Input.Hold.HOLD);
	}

	/**
	 * Full constructor.
	 * @param event the event id
	 * @param holdType the hold type
	 */
	public Input(int event, Input.Hold holdType) {
		super();
		this.event = event;
		this.holdType = holdType;
	}

	/** 
	 * Resets the input.
	 */
	public synchronized void reset() {
		this.state = Input.State.RELEASED;
		this.value = 0;
	}
	
	/** 
	 * Notify that the input has been released.
	 */
	public synchronized void release() {
		this.state = Input.State.RELEASED;
	}
	
	/** 
	 * Notify that the input was pressed.
	 * @param value the number of times pressed
	 */
	public synchronized void press(int value) {
		if (this.state != Input.State.WAITING_FOR_RELEASE) {
			this.value = this.value + value;
			this.state = Input.State.PRESSED;
		}
	}
	
	/** 
	 * Represents an input being signaled once.
	 */
	public synchronized void press() {
		this.press(1);
	}
	
	/**
	 * Returns true if the input has been pressed since the last check.
	 * <p>
	 * Calling this method will clear the value of this input if the input
	 * has already been released or if the input is {@link Hold#NO_HOLD} and
	 * the input has not been released.
	 * @return boolean
	 */
	public synchronized boolean isPressed() {
		return this.getValue() > 0;
	}

	/**
	 * Returns the value of the input.
	 * <p>
	 * Calling this method will clear the value of this input if the input
	 * has already been released or if the input is {@link Hold#NO_HOLD} and
	 * the input has not been released.
	 * @return int
	 */
	public synchronized int getValue() {
		int value = this.value;
		// if the value is greater than 0 then this input has been used
		// since the last check
		if (value > 0) {
			if (this.state == Input.State.RELEASED) {
				// if the input state is released then set the value to zero
				// but still return that the input was pressed since the last check
				this.value = 0;
			} else if (this.holdType == Input.Hold.NO_HOLD) {
				// if the input hold type is no hold then set the state to waiting for release
				// and set the value to zero
				this.state = Input.State.WAITING_FOR_RELEASE;
				this.value = 0;
			}
		}
		return value;
	}
	
	/**
	 * Returns the event id.
	 * @return int
	 */
	public int getEvent() {
		return this.event;
	}

	/**
	 * Returns the hold type.
	 * @return {@link Input.Hold}
	 */
	public Input.Hold getHoldType() {
		return holdType;
	}

	/**
	 * Returns the state.
	 * @return {@link Input.State}
	 */
	public Input.State getState() {
		return state;
	}
}
