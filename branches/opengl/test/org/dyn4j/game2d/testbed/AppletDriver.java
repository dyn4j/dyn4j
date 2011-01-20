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

import java.awt.Dimension;
import java.io.IOException;
import java.util.logging.LogManager;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.codezealot.game.core.AppletLoader;
import org.codezealot.game.render.Applet;
import org.codezealot.game.render.JoglSurface;

/**
 * The applet driver where the core creation is done.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public class AppletDriver extends AppletLoader<JoglSurface> {
	/** the version id */
	private static final long serialVersionUID = 7803602971018002468L;

	/* (non-Javadoc)
	 * @see java.applet.Applet#start()
	 */
	@Override
	public void start() {
		super.start();
		// this method is called every time they hit the page
		
		// setup logging
		LogManager manager = LogManager.getLogManager();
	    try {
			manager.readConfiguration(AppletDriver.class.getResourceAsStream("/logging.properties"));
		} catch (SecurityException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
	    
	    // set the look and feel to the system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		} catch (InstantiationException e) {
			System.err.println(e);
		} catch (IllegalAccessException e) {
			System.err.println(e);
		} catch (UnsupportedLookAndFeelException e) {
			System.err.println(e);
		}
		
		this.setFocusable(true);
		// create the size of the applet
		Dimension size = new Dimension(800, 600);
		// create the rendering surface
		GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		caps.setDoubleBuffered(true);
		caps.setHardwareAccelerated(true);
		caps.setNumSamples(2);
		caps.setSampleBuffers(true);
		JoglSurface surface = new JoglSurface(caps);
		surface.setFocusable(true);
		surface.setFocusTraversalKeysEnabled(true);
		// create the container for the surface
		Applet<JoglSurface> applet = new Applet<JoglSurface>(this, surface, size);
		// create the core and set its rendering container
		this.core = new TestBed<Applet<JoglSurface>>(applet);
		// start the core
		this.core.start();
		
		if (this.isVisible()) {
			this.requestFocus();
		}
	}
}
