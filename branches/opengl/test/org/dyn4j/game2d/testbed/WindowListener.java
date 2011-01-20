/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d.testbed;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.codezealot.game.core.Core;

/**
 * Listens for the window to lose focus or close.
 * <p>
 * Upon the window losing focus the core will be paused.<br />
 * Upon the window regaining focus the core will stay paused.<br />
 * Upon the window being closed the core will be shutdown.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public class WindowListener extends WindowAdapter {
	/** The reference to the core */
	private Core<?, ?> core = null;

	/**
	 * Full constructor.
	 * @param core the core object
	 */
	public WindowListener(Core<?, ?> core) {
		super();
		this.core = core;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowAdapter#windowDeactivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeactivated(WindowEvent event) {}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowAdapter#windowIconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowIconified(WindowEvent event) {
		if (this.core != null && this.core.isRunning()) {
			this.core.pause();
		}
		super.windowIconified(event);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowAdapter#windowLostFocus(java.awt.event.WindowEvent)
	 */
	public void windowLostFocus(WindowEvent event) {}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		if (this.core != null && this.core.isRunning()) {
			// flag the core to exit
			this.core.shutdown();
		}
		// release the core
		this.core = null;
		// exit the JVM
		System.exit(0);
	}
}
