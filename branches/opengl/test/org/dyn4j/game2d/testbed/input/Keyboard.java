/*
 * Copyright (c) 2009, William Bittle
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
 *   * Neither the name of William Bittle nor the names of its contributors may be used to endorse or 
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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import org.dyn4j.game2d.testbed.input.Input.Hold;

/**
 * Represents a keyboard input device.
 * @author William Bittle
 * @version $Revision: 424 $
 */
public class Keyboard implements KeyListener {
	/** The keys */
	protected Map<Integer, Input> keys = new HashMap<Integer, Input>();
	
	/**
	 * Adds the given key.
	 * @param key the key to listen for
	 */
	public void add(Input key) {
		synchronized (this.keys) {
			this.keys.put(key.getEvent(), key);
		}
	}
	
	/**
	 * Removes the input with the given event code.
	 * @param code the event code
	 * @return {@link Input}
	 */
	public Input remove(int code) {
		synchronized (this.keys) {
			return this.keys.remove(code);
		}
	}
	
	/**
	 * Returns true if the given input has been signaled.
	 * <p>
	 * Calling this method will clear the value of the input if the input
	 * has already been released or if the input is {@link Hold#NO_HOLD} and
	 * the input has not been released.
	 * @param code the event code
	 * @return boolean
	 */
	public boolean isPressed(int code) {
		synchronized (this.keys) {
			if (this.keys.containsKey(code)) {
				return this.keys.get(code).isPressed();
			}
		}
		return false;
	}
	
	/**
	 * Returns the value of the given input.
	 * <p>
	 * Returns zero if the input is not being listened for.
	 * <p>
	 * Calling this method will clear the value of the input if the input
	 * has already been released or if the input is {@link Hold#NO_HOLD} and
	 * the input has not been released.
	 * @param code the event code
	 * @return int
	 */
	public int getValue(int code) {
		synchronized (this.keys) {
			if (this.keys.containsKey(code)) {
				return this.keys.get(code).getValue();
			}
		}
		return 0;
	}
	
	/**
	 * Resets the given input.
	 * @param code the event code
	 */
	public void reset(int code) {
		synchronized (this.keys) {
			if (this.keys.containsKey(code)) {
				this.keys.get(code).reset();
			}
		}
	}
	
	/**
	 * Clears all input mappings.
	 */
	public void clear() {
		this.keys.clear();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		synchronized (this.keys) {
			if (this.keys.containsKey(keyCode)) {
				this.keys.get(keyCode).press();
			}
		}
		e.consume();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		synchronized (this.keys) {
			if (this.keys.containsKey(keyCode)) {
				this.keys.get(keyCode).release();
			}
		}
		e.consume();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		e.consume();
	}
}
