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
import java.awt.DisplayMode;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.codezealot.game.core.JoglCore;
import org.codezealot.game.render.Application;
import org.codezealot.game.render.JoglSurface;
import org.dyn4j.game2d.Version;

/**
 * Entry point class where setup of the game core is done when
 * running in application mode.
 * @author William Bittle
 * @version 2.2.2
 * @since 1.0.0
 */
public class Driver {
	/** the class logger */
	private static final Logger LOGGER = Logger.getLogger("org.dyn4j.game2d.Driver");
	
	/**
	 * The main method; uses zero arguments in the args array.
	 * @param args the command line arguments
	 */
	public static final void main(String[] args) {
		try {
			// setup logging
			LogManager manager = LogManager.getLogManager();
		    manager.readConfiguration(Driver.class.getResourceAsStream("/logging.properties"));
		    
		    // set the look and feel to the system look and feel
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e) {
				LOGGER.throwing(Application.class.getName(), "setLookAndFeel", e);
			} catch (InstantiationException e) {
				LOGGER.throwing(Application.class.getName(), "setLookAndFeel", e);
			} catch (IllegalAccessException e) {
				LOGGER.throwing(Application.class.getName(), "setLookAndFeel", e);
			} catch (UnsupportedLookAndFeelException e) {
				LOGGER.throwing(Application.class.getName(), "setLookAndFeel", e);
			}
		    
			// create the size of the window
			Dimension size = new Dimension(800, 600);
			// get a display mode from it
			DisplayMode mode = Application.getDisplayMode(size);
			// create the rendering surface
			GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
			caps.setDoubleBuffered(true);
			caps.setHardwareAccelerated(true);
			caps.setNumSamples(2);
			caps.setSampleBuffers(true);
			JoglSurface surface = new JoglSurface(caps);
			// create the container for the surface
			Application<JoglSurface> app = new Application<JoglSurface>(surface, "dyn4j v" + Version.getVersion() + " TestBed", mode, null, false);
			// set the container in the core
			JoglCore<Application<JoglSurface>> core = new TestBed<Application<JoglSurface>>(app);
			app.setLocationByPlatform(true);
			
			try {
				// attempt to load the image icon
				app.setIconImage(ImageIO.read(Driver.class.getResource("/icon.png")));
			} catch (IOException e1) {
				LOGGER.finest("Icon image 'icon.png' not found.");
			}
			
			// setup the window listener
			WindowListener listener = new WindowListener(core);
			// add the window listener to the container
			app.addWindowListener(listener);
			// start the core
			core.start();
		} catch (Exception e) {
			// log any exceptions we get
			LOGGER.log(Level.SEVERE, "", e);
		}
	}
}
