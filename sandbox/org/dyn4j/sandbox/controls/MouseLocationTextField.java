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
package org.dyn4j.sandbox.controls;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.dyn4j.Epsilon;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.utilities.RenderUtilities;

/**
 * Custom JTextField that uses the EDT to set the text.
 * <p>
 * Using the custom method {@link #update(Vector2)}, a thread
 * will be queued that will update the text box.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MouseLocationTextField extends JTextField {
	/** The version id */
	private static final long serialVersionUID = -3862760227675450170L;

	/** The latest mouse position */
	private Vector2 mousePosition = new Vector2();
	
	/** True if an update by the EDT is required */
	private boolean updateRequired = false;
	
	/**
	 * Updates the mouse position.
	 * @param mousePosition the new mouse position
	 */
	public void update(Vector2 mousePosition) {
		// obtain the lock on the mouse position
		synchronized (this.mousePosition) {
			// check if the mouse has moved at all
			if (this.mousePosition.distanceSquared(mousePosition) <= Epsilon.E) return;
			// set the new mouse position
			this.mousePosition = mousePosition.copy();
			// see if a EDT update thread has already been queued
			if (!this.updateRequired) {
				// set the update required flag
				this.updateRequired = true;
				// if it hasn't then queue one
				this.updateEDT();
			}
		}
	}
	
	/**
	 * Updates the value of this text box on the EDT.
	 */
	private void updateEDT() {
		// make sure the value of this text box is updated on the EDT
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// obtain the lock on the mouse position
				synchronized (mousePosition) {
					// update the this text box with the new mouse position value
					setText(RenderUtilities.formatVector2(mousePosition));
					// set the update required to false
					updateRequired = false;
				}
			}
		});
	}
}
