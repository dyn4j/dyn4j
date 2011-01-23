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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.dyn4j.game2d.Version;

/**
 * Class used as the entry point for running the TestBed as a desktop application.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class Driver {
	/**
	 * The main method; uses zero arguments in the args array.
	 * @param args the command line arguments
	 */
	public static final void main(String[] args) {
	    // set the look and feel to the system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	    
		// create the size of the window
		Dimension size = new Dimension(800, 600);
		
		// create a JFrame to put the TestBed into
		JFrame window = new JFrame("dyn4j v" + Version.getVersion() + " TestBed");
		
		// attempt to set the icon
		try {
			// attempt to load the image icon
			window.setIconImage(ImageIO.read(Driver.class.getResource("/icon.png")));
		} catch (IOException e1) {
			System.err.println("Icon image 'icon.png' not found.");
		}
		
		// setup OpenGL capabilities
		GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		caps.setDoubleBuffered(true);
		caps.setHardwareAccelerated(true);
		caps.setNumSamples(2);
		caps.setSampleBuffers(true);
		
		// create the testbed
		TestBed testbed = new TestBed(caps, window, size, TestBed.Mode.APPLICATION);
		
		// set the layout of the frame
		window.getContentPane().setLayout(new BorderLayout());
		// add the testbed to the frame
		window.getContentPane().add(testbed);
		
		window.pack();
		window.setResizable(false);
		// move from (0, 0) since this hides some of the window frame
		window.setLocation(10, 10);
		
		// show the window
		window.setVisible(true);
		
		// setting this property will call the dispose methods on the GLCanvas
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// finally start the testbed
		testbed.start();
	}
}
