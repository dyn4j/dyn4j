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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Logger;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.naming.ConfigurationException;

import org.codezealot.game.core.JoglCore;
import org.codezealot.game.input.Input;
import org.codezealot.game.input.Input.Hold;
import org.codezealot.game.render.Container;
import org.codezealot.game.render.JoglSurface;
import org.dyn4j.game2d.Version;
import org.dyn4j.game2d.collision.Fixture;
import org.dyn4j.game2d.collision.broadphase.Sap;
import org.dyn4j.game2d.collision.continuous.ConservativeAdvancement;
import org.dyn4j.game2d.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.game2d.collision.narrowphase.Gjk;
import org.dyn4j.game2d.collision.narrowphase.Sat;
import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.BodyFixture;
import org.dyn4j.game2d.dynamics.joint.MouseJoint;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Vector2;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Container for the tests.
 * @author William Bittle
 * @param <E> the container type
 * @version 2.2.1
 * @since 1.0.0
 */
public class TestBed<E extends Container<JoglSurface>> extends JoglCore<E> {
	/** The class logger */
	private static final Logger LOGGER = Logger.getLogger(TestBed.class.getName());
	
	/**
	 * The stepping modes.
	 * @author William Bittle
	 * @version 2.0.0
	 * @since 2.0.0
	 */
	private static enum StepMode {
		/** Continuous mode */
		CONTINUOUS,
		/** Manual stepping mode */
		MANUAL,
		/** Timed stepping mode */
		TIMED
	}
	
	/** The text color */
	private static final Color TEXT_COLOR = Color.GRAY;
	
	/** The time usage object */
	private Usage usage = new Usage();
	
	/** The current test */
	private Test test;
	
	/** The frame to set the simulation settings */
	private ControlPanel settingsFrame;

	/** Flag indicating step mode */
	private StepMode mode = StepMode.CONTINUOUS;
	
	/** The wait time in seconds between steps in timed stepping mode */
	private double tModeInterval = 0.5;
	
	/** The total elapsed time in nanoseconds for timed stepping mode */
	private double tModeElapsed = 0.0;
	
	// text labels
	/** The label for the control panel key */
	private SimpleText controlsLabel = new SimpleText("Press 'c' to open the Test Bed Control Panel.");
	/** The label for the version */
	private SimpleText versionLabel = new SimpleText("Version:");
	/** The value of the dyn4j version */
	private SimpleText versionValue = new SimpleText("v" + Version.getVersion());
	/** The label for the current test */
	private SimpleText testLabel = new SimpleText("Test:");
	/** The label for the current zoom */
	private SimpleText zoomLabel = new SimpleText("Scale:");
	/** The label for the current number of bodies */
	private SimpleText bodyCountLabel = new SimpleText("Bodies:");
	/** The label for the current simulation mode */
	private SimpleText modeLabel = new SimpleText("Mode:");
	/** The label for continuous mode */
	private SimpleText continuousModeLabel = new SimpleText("Continuous");
	/** The label for manual mode */
	private SimpleText manualModeLabel = new SimpleText("Manual");
	/** The label for timed mode */
	private SimpleText timedModeLabel = new SimpleText("Timed");
	/** The label for the contacts table */
	private SimpleText contactLabel = new SimpleText("Contact Information");
	/** The label for the total number of contacts */
	private SimpleText cTotalLabel = new SimpleText("Total:");
	/** The label for the number of added contacts */
	private SimpleText cAddedLabel = new SimpleText("Added:");
	/** The label for the number of persisted contacts */
	private SimpleText cPersistedLabel = new SimpleText("Persisted:");
	/** The label for the number of removed contacts */
	private SimpleText cRemovedLabel = new SimpleText("Removed:");
	/** The label for the number of sensed contacts */
	private SimpleText cSensedLabel = new SimpleText("Sensed:");
	/** The label for the frame rate */
	private SimpleText fpsLabel = new SimpleText("FPS:");
	/** The label indicating paused state */
	private SimpleText pausedLabel = new SimpleText("Paused");
	/** The label for memory usage */
	private SimpleText memoryLabel = new SimpleText("Memory");
	/** The label for used memory */
	private SimpleText usedMemoryLabel = new SimpleText("Used");
	/** The label for free memory */
	private SimpleText freeMemoryLabel = new SimpleText("Free");
	/** The label for total memory */
	private SimpleText totalMemoryLabel = new SimpleText("Total");
	/** The label for time usage */
	private SimpleText timeUsageLabel;
	/** The label for the jre version */
	private SimpleText jreVersionLabel = new SimpleText("JRE Version:");
	/** The label for the jre mode */
	private SimpleText jreModeLabel = new SimpleText("JRE Mode:");
	/** The label for the operating system name */
	private SimpleText osNameLabel = new SimpleText("OS Name:");
	/** The label for the architecture name */
	private SimpleText osArchitectureLabel = new SimpleText("Architecture:");
	/** The label for the data model name */
	private SimpleText osDataModelLabel = new SimpleText("Data Model:");
	/** The label for the number of processors */
	private SimpleText processorCountLabel = new SimpleText("Processors:");
	/** The label for the jre version */
	private SimpleText jreVersionValue;
	/** The label for the jre mode */
	private SimpleText jreModeValue;
	/** The label for the operating system name */
	private SimpleText osNameValue;
	/** The label for the architecture name */
	private SimpleText osArchitectureValue;
	/** The label for the data model name */
	private SimpleText osDataModelValue;
	/** The label for the number of processors */
	private SimpleText processorCountValue = new SimpleText(String.valueOf(Runtime.getRuntime().availableProcessors()));
	
	// picking using left click
	/** The mouse joint created when picking shapes */
	private MouseJoint mouseJoint = null;
	
	// picking using right click
	/** The selected {@link Body} for picking capability */
	private Body selected = null;
	/** The old position for picking capability */
	private Vector2 vOld = null;
	/** The saved state of the body under control */
	private DirectControl.State controlState = null;
	
	/** The blur image for the metrics panel */
	private BufferedImage blur = null;
	
	/**
	 * Full constructor.
	 * @param container the rendering container
	 */
	public TestBed(E container) {
		super(container);
	}
	
	/**
	 * Sets up listening for various inputs.
	 */
	private void initializeInputs() {
		// make sure the input mappings are clear
		this.keyboard.clear();
		this.mouse.clear();
		
		// listen for some keys
		// shift may be used by a number of keys
		this.keyboard.add(new Input(KeyEvent.VK_SHIFT, Hold.HOLD));
		
		// exit
		this.keyboard.add(new Input(KeyEvent.VK_ESCAPE, Hold.NO_HOLD));
		this.keyboard.add(new Input(KeyEvent.VK_E, Hold.NO_HOLD));
		// pause
		this.keyboard.add(new Input(KeyEvent.VK_PAUSE, Hold.NO_HOLD));
		this.keyboard.add(new Input(KeyEvent.VK_P, Hold.NO_HOLD));
		// zoom in
		this.keyboard.add(new Input(KeyEvent.VK_ADD, Hold.NO_HOLD));
		// zoom out
		this.keyboard.add(new Input(KeyEvent.VK_SUBTRACT, Hold.NO_HOLD));
		// pan
		this.keyboard.add(new Input(KeyEvent.VK_RIGHT, Hold.HOLD));
		this.keyboard.add(new Input(KeyEvent.VK_LEFT, Hold.HOLD));
		this.keyboard.add(new Input(KeyEvent.VK_UP, Hold.HOLD));
		this.keyboard.add(new Input(KeyEvent.VK_DOWN, Hold.HOLD));
		// home keys
		this.keyboard.add(new Input(KeyEvent.VK_HOME, Hold.NO_HOLD));
		this.keyboard.add(new Input(KeyEvent.VK_H, Hold.NO_HOLD));
		// reset
		this.keyboard.add(new Input(KeyEvent.VK_R, Hold.NO_HOLD));
		// control panel
		this.keyboard.add(new Input(KeyEvent.VK_C, Hold.NO_HOLD));
		// mode toggle
		this.keyboard.add(new Input(KeyEvent.VK_SPACE, Hold.NO_HOLD));
		// perform manual step in step mode
		this.keyboard.add(new Input(KeyEvent.VK_M, Hold.NO_HOLD));
		// increase/decrease (T/t) time interval in timed mode
		this.keyboard.add(new Input(KeyEvent.VK_T, Hold.NO_HOLD));
		// output the state of all the objects on the scene
		this.keyboard.add(new Input(KeyEvent.VK_O, Hold.NO_HOLD));
		// button to hold to pick a shape using the mouse joint
		this.mouse.add(new Input(MouseEvent.BUTTON1));
		// button to hold to pick a shape and control directly
		this.mouse.add(new Input(MouseEvent.BUTTON3));
		// key to hold when rotating a picked shape
		this.keyboard.add(new Input(KeyEvent.VK_Z));
		// key to launch a bomb
		this.keyboard.add(new Input(KeyEvent.VK_B, Hold.NO_HOLD));
		// key to increase/decrease (U/u) the metrics update rate
		this.keyboard.add(new Input(KeyEvent.VK_U, Hold.NO_HOLD));
		
		// initialize the keys for the test
		this.test.initializeInput(this.keyboard, this.mouse);
	}
	
	/**
	 * Sets up text images.
	 */
	private void initText() {
		AttributedString timeString = new AttributedString("Time ( Render | Update | System )");
		timeString.addAttribute(TextAttribute.FOREGROUND, new Color(222, 48, 12), 7, 13);
		timeString.addAttribute(TextAttribute.FOREGROUND, new Color(222, 117, 0), 16, 22);
		timeString.addAttribute(TextAttribute.FOREGROUND, new Color(20, 134, 222), 25, 31);
		this.timeUsageLabel = new SimpleText(timeString);
		
		try {
			jreVersionValue = new SimpleText(System.getProperty("java.runtime.version"));
			jreModeValue = new SimpleText(System.getProperty("java.vm.info"));
			osNameValue = new SimpleText(System.getProperty("os.name"));
			osArchitectureValue = new SimpleText(System.getProperty("os.arch"));
			osDataModelValue = new SimpleText(System.getProperty("sun.arch.data.model"));
		} catch (SecurityException e) {
			jreVersionValue = new SimpleText("Unknown");
			jreModeValue = new SimpleText("Unknown");
			osNameValue = new SimpleText("Unknown");
			osArchitectureValue = new SimpleText("Unknown");
			osDataModelValue = new SimpleText("Unknown");
		}
	}
	
	int texId, fboId;
	
	/* (non-Javadoc)
	 * @see org.codezealot.game.core.JoglCore#init(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void init(GLAutoDrawable glDrawable) {
		super.init(glDrawable);
		
		// create the simulation settings frame
		try {
			this.settingsFrame = new ControlPanel();
			// set the current test
			this.test = this.settingsFrame.getTest();
		} catch (ConfigurationException e) {
			LOGGER.severe("An error occurred when attempting to configure the TestBed.");
			LOGGER.throwing("TestBed", "initialize", e);
		}
		
		// initialize the inputs to listen for
		this.initializeInputs();
		// initialize the text images
		this.initText();
		
		int width = this.renderer.getDisplaySize().width;
		int height = this.renderer.getDisplaySize().height;
		
		// get the OpenGL context
		GL2 gl = glDrawable.getGL().getGL2();
		
		// set the matrix mode to projection
		gl.glMatrixMode(GL2.GL_PROJECTION);
		// initialize the matrix
		gl.glLoadIdentity();
		// set the view to a 2D view
		gl.glOrtho(-width / 2.0, width / 2.0, -height / 2.0, height / 2.0, 0, 1);
		
		// switch to the model view matrix
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		// initialize the matrix
		gl.glLoadIdentity();
		
		// set the shading model to smooth
//		gl.glShadeModel(GL2.GL_SMOOTH);
		
		// set the clear color to white
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		// disable depth testing since we are working in 2D
		gl.glDisable(GL.GL_DEPTH_TEST);
		
		// enable blending for translucency
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glEnable(GL.GL_TEXTURE_2D);
		
		// get a texture object
		int[] ids = new int[1];
		gl.glGenTextures(1, ids, 0);
		texId = ids[0];
		
		// bind the texture to set it up
		gl.glBindTexture(GL.GL_TEXTURE_2D, texId);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA8, width, height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
		// switch back to the default texture
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		
		// create a frame buffer object (FBO)
		gl.glGenFramebuffers(1, ids, 0);
		fboId = ids[0];
		
		// bind the FBO to set it up
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fboId);
		// attach the texture to the FBO
		gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D, texId, 0);
		// check the FBO status
		int status = gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
		if (status != GL.GL_FRAMEBUFFER_COMPLETE) System.out.println("no go");
		// switch back to the default frame buffer
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		
		// ignore the primary color source
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);
	}
	
	/* (non-Javadoc)
	 * @see org.codezealot.game.core.Core#render(java.lang.Object)
	 */
	@Override
	public void render(GL2 gl) {
		// get the current time
		long startTime = this.timer.getCurrentTime();

		// get the draw singleton
		Draw draw = Draw.getInstance();
		
		// get the rendering width and height
		int width = this.renderer.getDisplaySize().width;
		int height = this.renderer.getDisplaySize().height;
		
		// check for anti-aliasing
		if (draw.isAntiAliased()) {
			gl.glEnable(GL.GL_LINE_SMOOTH);
			gl.glEnable(GL2.GL_POLYGON_SMOOTH);
			gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
			gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
		} else {
			gl.glDisable(GL.GL_LINE_SMOOTH);
			gl.glDisable(GL2.GL_POLYGON_SMOOTH);
			gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_FASTEST);
			gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_FASTEST);
		}
		
		// make sure glsl is supported
//		if (gl.hasGLSL()) {
//
//		}
		
		// switch to draw to the FBO
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fboId);
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
//		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		// draw everything
		
		// see if we should anti-alias the text
//		if (draw.isTextAntiAliased()) {
//			// turn it on
//			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		}
		
		// determine if we need to do the convolve-op
//		if (draw.isPanelBlurred() && draw.drawPanel()) {
//			// check if the cached blur surface is null
//			if (blur == null) {
//				// if so create it
//				blur = g.getDeviceConfiguration().createCompatibleImage(width, height);
//			}
//			// get the graphics object to paint to
//			Graphics2D bg = blur.createGraphics();
//			
//			// to perform the convolve op we need to draw to the buffered image instead
//			// of the graphics object
//			
//			// call the super method
//			super.render(bg);
//			
//			// see if we should anti-alias the text
//			if (draw.isTextAntiAliased()) {
//				// turn it on
//				bg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//			}
//			
//			// see if we should anti-alias
//			if (draw.isAntiAliased()) {
//				// turn it on
//				bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			}
//			
//			// set the background color to white
//			bg.setBackground(Color.WHITE);
//			bg.setClip(0, 0, width, height);
//			
//			// paint the background
//			bg.clearRect(0, 0, width, height);
//			
//			// paint the test
//			this.test.render(bg, width, height);
//			
//			// render the controls label top center
//			this.renderControls(bg, (int) Math.ceil((width - this.controlsLabel.getWidth(bg)) / 2.0), 5);
//			
//			// we don't need that anymore
//			bg.dispose();
//			
//			// save the current clip
//			Shape clip = g.getClip();
//			// set the clip to above the metrics panel
//			g.setClip(new Rectangle(0, 0, width, height - 110));
//			// copy that clip
//			g.drawImage(blur, 0, 0, null);
//			// set the clip to only the metrics panel
//			g.setClip(new Rectangle(0, height - 110, width, 110));
//			
//			// setup the blur 5x5 kernel matrix
//			float[] blurMatrix = new float[] {0.04f, 0.04f, 0.04f, 0.04f, 0.04f,
//											  0.04f, 0.04f, 0.04f, 0.04f, 0.04f,
//											  0.04f, 0.04f, 0.04f, 0.04f, 0.04f,
//											  0.04f, 0.04f, 0.04f, 0.04f, 0.04f,
//											  0.04f, 0.04f, 0.04f, 0.04f, 0.04f};
//			
//			// create the blur convolve op
//			ConvolveOp op = new ConvolveOp(new Kernel(5, 5, blurMatrix));
//			
//			// draw the metrics panel with the convolve op
//			g.drawImage(blur, op, 0, 0);
//			// set the color to a partially transparent black
//			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.8f));
//			// shade the metrics panel (for the background)
//			g.fillRect(0, height - 110, width, 110);
//			// set the original clip back
//			g.setClip(clip);
//		} else {
			super.render(gl);
			
			// see if we should anti-alias
//			if (draw.isAntiAliased()) {
//				// turn anti-aliasing on
//				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			}
			
			// clear the screen
			gl.glClear(GL.GL_COLOR_BUFFER_BIT);
			gl.glLoadIdentity();
			
			// paint the test
			this.test.render(gl, width, height);
			
			gl.glPushMatrix();
			gl.glLoadIdentity();
			
			gl.glColor3f(0.0f, 0.0f, 0.0f);
			GLUT glut = new GLUT();
			gl.glRasterPos2d(-9, 8);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, String.valueOf(this.fps.getFps()));
			
			gl.glPopMatrix();
			
			// render the controls label top center
//			this.renderControls(gl, (int) Math.ceil((width - this.controlsLabel.getWidth(gl)) / 2.0), 5);
			
			if (draw.drawPanel()) {
				// draw the translucent background
				gl.glColor4f(0.0f, 0.0f, 0.0f, 0.8f);
				gl.glBegin(GL2.GL_QUADS);
					gl.glVertex2d(-width / 2.0, -height / 2.0 + 110);
					gl.glVertex2d(-width / 2.0, -height / 2.0);
					gl.glVertex2d( width / 2.0, -height / 2.0);
					gl.glVertex2d( width / 2.0, -height / 2.0 + 110);
				gl.glEnd();
			}
			
//		}
		
		// make sure we should draw the metrics panel
//		if (draw.drawPanel()) {
//			// draw the gradient top
//			g.setPaint(new GradientPaint(0, height - 110, new Color(0.5f, 0.5f, 0.5f, 0.5f), 0, height - 101, new Color(0.0f, 0.0f, 0.0f, 0.5f)));
//			g.fillRect(0, height - 110, width, 10);
//			
//			// draw the small box around the test info
//			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.2f));
//			g.fillRect(2, height - 98, 150, 95);
//			g.setColor(Color.BLACK);
//			g.drawRect(2, height - 98, 150, 95);
//			// render the general test information
//			this.renderTestInformation(g, 7, height - 95);
//			
//			// draw the small box around the contact info
//			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.2f));
//			g.fillRect(155, height - 98, 120, 95);
//			g.setColor(Color.BLACK);
//			g.drawRect(155, height - 98, 120, 95);
//			// render the contact information
//			this.renderContactInformation(g, 159, height - 95);
//			
//			// draw the small box around the performance info
//			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.2f));
//			g.fillRect(278, height - 98, 200, 95);
//			g.setColor(Color.BLACK);
//			g.drawRect(278, height - 98, 200, 95);
//			// render the performance information
//			this.renderPerformanceInformation(g, 282, height - 95);
//			
//			// draw the small box around the system info
//			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.2f));
//			g.fillRect(481, height - 98, 200, 95);
//			g.setColor(Color.BLACK);
//			g.drawRect(481, height - 98, 200, 95);
//			// render the system information
//			this.renderSystemInformation(g, 485, height - 95);
//		}
		
		// always show the paused box on top of everything
		if (this.isPaused()) {
			// show a black translucent screen over everything
			gl.glColor4f(0.0f, 0.0f, 0.0f, 0.3f);
			GLHelper.fillRectangle(gl, 0, 0, width, height);
			// show the paused label in the top left corner
			this.renderPaused(gl, width, height);
		}
		
		// un bind the fbo
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		// set the texture to render
		gl.glBindTexture(GL.GL_TEXTURE_2D, texId);
		
		// draw the texture to a 2d quad
		gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2d(1, 0);
			gl.glVertex2d(width/2.0, -height/2.0);
			gl.glTexCoord2d(0, 0);
			gl.glVertex2d(-width/2.0, -height/2.0);
			gl.glTexCoord2d(0, 1);
			gl.glVertex2d(-width/2.0, height/2.0);
			gl.glTexCoord2d(1, 1);
			gl.glVertex2d(width/2.0, height/2.0);
		gl.glEnd();
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		// switch back to the default texture
		
		this.usage.setRender(this.timer.getCurrentTime() - startTime);
	}
	
	/**
	 * Renders the paused label.
	 * @param gl the OpenGL graphics context
	 * @param w the width of the bounding rectangle
	 * @param h the height of the bounding rectangle
	 */
	private void renderPaused(GL2 gl, double w, double h) {
		// save the current matrix
		gl.glPushMatrix();
		// reset the current matrix
		gl.glLoadIdentity();
		// set the color to red
		gl.glColor4f(1.0f, 0.0f, 0.0f, 0.7f);
		// fill a red rectangle to show the paused label
		GLHelper.fillRectangle(gl, -w / 2.0 + 50.0, h / 2.0 - 12, 100, 24);
		// set the color to white
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.7f);
		// set the raster for the text
		gl.glRasterPos2d(-w / 2.0 + 25.0, h / 2.0 - 15.0);
		// draw the text
		GLUT glut = new GLUT();
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Paused");
		// reset to the old matrix
		gl.glPopMatrix();
	}
	
	/**
	 * Renders the controls label to the given graphics object at the
	 * given screen coordinates.
	 * @param gl the OpenGL graphics context
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	private void renderControls(GL2 gl, int x, int y) {
		// set the text color
//		g.setColor(Color.BLACK);
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		
		//width - w - 5, height - h - 5
//		controlsLabel.render(g, x, y);
		//"Press 'c' to open the Test Bed Control Panel."
	}
	
	/**
	 * Renders the test information to the given graphics object at the
	 * given screen coordinates.
	 * @param g the graphics object to render to
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	private void renderTestInformation(Graphics2D g, int x, int y) {
		// set the padding/spacing of the text lines and values
		final int padding = 55;
		final int spacing = 15;
		
		// set the text color
		g.setColor(TEXT_COLOR);
		
		// render the label
		this.versionLabel.render(g, x, y);
		// render the value
		this.versionValue.render(g, x + padding, y);
		
		// render the label
		this.testLabel.render(g, x, y + spacing);
		// render the value
		SimpleText testName = new SimpleText(this.test.getName());
		testName.render(g, x + padding, y + spacing);

		// show the zoom
		// render the label
		this.zoomLabel.render(g, x, y + spacing * 2);
		// render the value
		SimpleText zoom = new SimpleText(this.test.getZoom() + " px/m");
		zoom.render(g, x + padding, y + spacing * 2);
		
		// show the number of bodies
		// render the label
		this.bodyCountLabel.render(g, x, y + spacing * 3);
		// render the value
		SimpleText bodies = new SimpleText(String.valueOf(this.test.getWorld().getBodyCount()));
		bodies.render(g, x + padding, y + spacing * 3);
		
		// show the mode
		// render the label
		this.modeLabel.render(g, x, y + spacing * 4);
		// render the value
		if (this.mode == StepMode.MANUAL) {
			this.manualModeLabel.render(g, x + padding, y + spacing * 4);
		} else if (this.mode == StepMode.TIMED) {
			this.timedModeLabel.render(g, x + padding, y + spacing * 4);
		} else {
			this.continuousModeLabel.render(g, x + padding, y + spacing * 4);
		}
		
		Point loc = this.mouse.getRelativeLocation();
		Vector2 pos = this.test.screenToWorld(loc.x, loc.y);
		DecimalFormat df = new DecimalFormat("0.000");
		// show the current x,y of the mouse
		SimpleText mousePosition = new SimpleText("( " + df.format(pos.x) + ", " + df.format(pos.y) + " )");
		mousePosition.render(g, x, y + spacing * 5);
	}
	
	/**
	 * Renders the contact information to the given graphics object at the
	 * given screen coordinates.
	 * @param g the graphics object to render to
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	private void renderContactInformation(Graphics2D g, int x, int y) {
		// set the padding/spacing of the text lines and values
		final int padding = 105;
		final int spacing = 15;
		
		// set the text color
		g.setColor(TEXT_COLOR);
		// render the contact header
		this.contactLabel.render(g, x, y);
		
		// render the various labels
		this.cTotalLabel.render(g, x, y + spacing);
		this.cAddedLabel.render(g, x, y + spacing * 2);
		this.cPersistedLabel.render(g, x, y + spacing * 3);
		this.cRemovedLabel.render(g, x, y + spacing * 4);
		this.cSensedLabel.render(g, x, y + spacing * 5);
		
		// get the contact counter
		ContactCounter cc = (ContactCounter) this.test.getWorld().getContactListener();
		// get the numbers
		int total = cc.getSolved();
		int added = cc.getAdded();
		int persisted = cc.getPersisted();
		int removed = cc.getRemoved();
		int sensed = cc.getSensed();
		
		// create the texts
		SimpleText ct = new SimpleText(String.valueOf(total));
		SimpleText ca = new SimpleText(String.valueOf(added));
		SimpleText cp = new SimpleText(String.valueOf(persisted));
		SimpleText cr = new SimpleText(String.valueOf(removed));
		SimpleText cs = new SimpleText(String.valueOf(sensed));
		
		// render the values
		ct.render(g, x + padding - ct.getWidth(g), y + spacing);
		ca.render(g, x + padding - ca.getWidth(g), y + spacing * 2);
		cp.render(g, x + padding - cp.getWidth(g), y + spacing * 3);
		cr.render(g, x + padding - cr.getWidth(g), y + spacing * 4);
		cs.render(g, x + padding - cs.getWidth(g), y + spacing * 5);
	}
	
	/**
	 * Renders the performance information to the given graphics object at the
	 * given screen coordinates.
	 * @param g the graphics object to render to
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	private void renderPerformanceInformation(Graphics2D g, int x, int y) {
		// set the padding/spacing of the text lines and values
		final int padding = 55;
		final int spacing = 15;
		
		// set the text color
		g.setColor(TEXT_COLOR);
		
		// render the frames per second
		this.fpsLabel.render(g, x, y);
		// get the fps in integer form
		int iFps = (int) Math.floor(this.fps.getFps());
		// render the value
		SimpleText fps = new SimpleText(String.valueOf(iFps));
		fps.render(g, x + padding, y);
		
		// show the total memory usage
		double barWidth = 100;
		this.memoryLabel.render(g, x, y + spacing);
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		SimpleText total = new SimpleText(nf.format(this.usage.getTotalMemory() / 1024.0 / 1024.0) + "M");
		total.render(g, x + padding, y + spacing);
		this.totalMemoryLabel.render(g, x + barWidth + 10, y + spacing);
		
		// show the used/free memory bars
		g.setColor(new Color(255, 152, 63));
		g.fillRect(x, y + spacing * 2, (int) Math.ceil(this.usage.getUsedMemoryPercentage() * barWidth), 12);
		g.setColor(new Color(129, 186, 4));
		g.fillRect(x, y + spacing * 3, (int) Math.ceil(this.usage.getFreeMemoryPercentage() * barWidth), 12);
		g.setColor(Color.BLACK);
		g.drawRect(x, y + spacing * 2, (int) Math.ceil(barWidth) + 1, 12);
		g.drawRect(x, y + spacing * 3, (int) Math.ceil(barWidth) + 1, 12);
		
		// render the used/free labels
		g.setColor(TEXT_COLOR);
		this.usedMemoryLabel.render(g, x + barWidth + 10, y + spacing * 2);
		this.freeMemoryLabel.render(g, x + barWidth + 10, y + spacing * 3);
		
		barWidth = 180;
		// show the time usage bar
		this.timeUsageLabel.render(g, x, y + spacing * 4);
		double renderW = this.usage.getRenderTimePercentage() * barWidth;
		double updateW = this.usage.getUpdateTimePercentage() * barWidth;
		// since input polling time is so low, just consider it part of the system time
		double systemW = (this.usage.getSystemTimePercentage() + this.usage.getInputTimePercentage()) * barWidth;
		// save the original font
		Font font = g.getFont();
		// create a smaller font for the percentages
		Font f = new Font("arial", Font.PLAIN, 9);
		g.setFont(f);
		// render the boxes
		g.setColor(new Color(222, 48, 12));
		g.fillRect(x, y + spacing * 5, (int) Math.ceil(renderW), 12);
		g.setColor(Color.WHITE);
		g.drawString(Math.round(this.usage.getRenderTimePercentage() * 100) + "%", x + 3, (int) Math.ceil(y + spacing * 5.5) + 2);
		g.setColor(new Color(222, 117, 0));
		g.fillRect(x + (int) Math.ceil(renderW), y + spacing * 5, (int) Math.ceil(updateW), 12);
		g.setColor(Color.WHITE);
		g.drawString(Math.round(this.usage.getUpdateTimePercentage() * 100) + "%", x + (int) Math.ceil(renderW) + 3, (int) Math.ceil(y + spacing * 5.5) + 2);
		g.setColor(new Color(20, 134, 222));
		g.fillRect(x + (int) Math.ceil(renderW) + (int) Math.ceil(updateW), y + spacing * 5, (int) Math.ceil(systemW), 12);
		g.setColor(Color.WHITE);
		g.drawString(Math.round(this.usage.getSystemTimePercentage() * 100) + "%", x + (int) Math.ceil(renderW) + (int) Math.ceil(updateW) + 3, (int) Math.ceil(y + spacing * 5.5) + 2);
		g.setColor(Color.BLACK);
		g.drawRect(x, y + spacing * 5, (int) Math.ceil(barWidth) + 1, 12);
		g.setFont(font);
	}
	
	/**
	 * Renders some system information.
	 * @param g the graphics object to render to
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	private void renderSystemInformation(Graphics2D g, int x, int y) {
		// set the padding/spacing of the text lines and values
		final int padding = 80;
		final int spacing = 15;
		
		// set the text color
		g.setColor(TEXT_COLOR);
		
		this.jreVersionLabel.render(g, x, y);
		this.jreVersionValue.render(g, x + padding, y);
		
		this.jreModeLabel.render(g, x, y + spacing);
		this.jreModeValue.render(g, x + padding, y + spacing);
		
		this.osNameLabel.render(g, x, y + spacing * 2);
		this.osNameValue.render(g, x + padding, y + spacing * 2);
		
		this.osArchitectureLabel.render(g, x, y + spacing * 3);
		this.osArchitectureValue.render(g, x + padding, y + spacing * 3);
		
		this.osDataModelLabel.render(g, x, y + spacing * 4);
		this.osDataModelValue.render(g, x + padding, y + spacing * 4);
		
		this.processorCountLabel.render(g, x, y + spacing * 5);
		this.processorCountValue.render(g, x + padding, y + spacing * 5);
	}
	
	/* (non-Javadoc)
	 * @see org.codezealot.game.core.Core#poll()
	 */
	@Override
	public void poll() {
		long startTime = this.timer.getCurrentTime();
		super.poll();
		
		// allow the current test to override the default functionality
		this.test.poll(this.keyboard, this.mouse);
		
		// check the escape key
		if (this.keyboard.isPressed(KeyEvent.VK_ESCAPE) || this.keyboard.isPressed(KeyEvent.VK_E)) {
			// only exit if its not applet mode
			if (this.renderer.getMode() == Container.Mode.APPLICATION) {
				// exit the game
				this.shutdown();
				// exit the JVM
				System.exit(0);
			}
		}
		
		// check the space key
		if (this.keyboard.isPressed(KeyEvent.VK_PAUSE) || this.keyboard.isPressed(KeyEvent.VK_P)) {
			// pause or unpause
			if (this.isPaused()) {
				this.resume();
			} else {
				this.pause();
			}
		}
		
		// check the + key
		if (this.keyboard.isPressed(KeyEvent.VK_ADD)) {
			this.test.zoom(2.0);
		}
		
		// check the - key
		if (this.keyboard.isPressed(KeyEvent.VK_SUBTRACT)) {
			this.test.zoom(0.5);
		}
		
		// check for the mouse wheel
		int scrollAmount = this.mouse.getScroll();
		if (scrollAmount != 0) {
			if (scrollAmount < 0) {
				this.test.zoom(0.5);
			} else {
				this.test.zoom(2.0);
			}
		}
		
		// check the left key
		if (this.keyboard.isPressed(KeyEvent.VK_LEFT)) {
			this.test.translate(8.0 / this.test.scale, 0.0);
		}
		
		// check the right key
		if (this.keyboard.isPressed(KeyEvent.VK_RIGHT)) {
			this.test.translate(-8.0 / this.test.scale, 0.0);
		}
		
		// check the up key
		if (this.keyboard.isPressed(KeyEvent.VK_UP)) {
			this.test.translate(0.0, -8.0 / this.test.scale);
		}
		
		// check the down key
		if (this.keyboard.isPressed(KeyEvent.VK_DOWN)) {
			this.test.translate(0.0, 8.0 / this.test.scale);
		}

		// check for the home key
		if (this.keyboard.isPressed(KeyEvent.VK_HOME) || this.keyboard.isPressed(KeyEvent.VK_H)) {
			this.test.home();
		}
		
		// check for the r key
		if (this.keyboard.isPressed(KeyEvent.VK_R)) {
			// reset the test
			this.test.reset();
			// dont need to reset inputs
		}
		
		// check for the r key
		if (this.keyboard.isPressed(KeyEvent.VK_C)) {
			// when I show the settings frame this frame loses
			// focus, because of this the key release event does not
			// fire, therefore we must manually call the release for
			// this key; we can do this by reseting the input
			this.keyboard.reset(KeyEvent.VK_C);
			// check if the window is already showing
			if (this.settingsFrame.isShowing()) {
				this.settingsFrame.toFront();
			} else {
				this.settingsFrame.setVisible(true);
			}
		}
		
		// check for the space bar
		if (this.keyboard.isPressed(KeyEvent.VK_SPACE)) {
			if (this.mode == StepMode.CONTINUOUS) {
				this.mode = StepMode.MANUAL;
			} else if (this.mode == StepMode.MANUAL) {
				this.tModeElapsed = 0.0;
				this.mode = StepMode.TIMED;
			} else {
				this.mode = StepMode.CONTINUOUS;
			}
		}
		
		// check for the m key
		if (this.mode == StepMode.MANUAL && this.keyboard.isPressed(KeyEvent.VK_M)) {
			this.test.world.step(1);
			this.test.update(1);
		}
		
		// check for the t key
		if (this.mode == StepMode.TIMED && this.keyboard.isPressed(KeyEvent.VK_T)) {
			if (this.keyboard.isPressed(KeyEvent.VK_SHIFT)) {
				this.tModeInterval += 0.1;
			} else {
				// make sure we dont make the interval zero
				if (this.tModeInterval > 0.1) {
					this.tModeInterval -= 0.1;
				}
			}
		}
		
		// look for the o key
		if (this.keyboard.isPressed(KeyEvent.VK_O)) {
			// output the current state of the objects
			int size = this.test.world.getBodyCount();
			for (int i = 0; i < size; i++) {
				System.out.println(this.test.world.getBody(i));
			}
		}
		
		// look for left click press
		if (this.mouse.isPressed(MouseEvent.BUTTON1)) {
			// don't do anything if we have already determined that the
			// click is in one of the shapes
			if (this.mouseJoint == null) {
				// get the mouse location
				Point p = this.mouse.getRelativeLocation();
				// convert to world coordinates
				Vector2 v = this.test.screenToWorld(p.x, p.y);
				// try to find the object that we are clicking on
				int bSize = this.test.world.getBodyCount();
				for (int i = 0; i < bSize; i++) {
					Body b = this.test.world.getBody(i);
					// dont bother trying to attach to static bodies
					if (b.isStatic()) continue;
					int cSize = b.getFixtureCount();
					// loop over the shapes in the body
					for (int j = 0; j < cSize; j++) {
						Fixture f = b.getFixture(j);
						Convex c = f.getShape();
						// see if the point is contained in it
						if (c.contains(v, b.getTransform())) {
							// once we find the body, create a mouse joint
							this.mouseJoint = new MouseJoint(b, v, 4.0, 0.7, 1000.0 * b.getMass().getMass());
							// add the joint to the world
							this.test.world.add(this.mouseJoint);
							// make sure the body is awake
							b.setAsleep(false);
							// break from the loop
							break;
						} else {
							// check for line segment
							if (c.isType(Segment.TYPE)) {
								Segment s = (Segment) c;
								// if you are a tenth of a meter from it then consider that
								// selecting the segment
								if (s.contains(v, b.getTransform(), 0.05)) {
									// once we find the body, create a mouse joint
									this.mouseJoint = new MouseJoint(b, v, 4.0, 0.7, 1000.0 * b.getMass().getMass());
									// add the joint to the world
									this.test.world.add(this.mouseJoint);
									// make sure the body is awake
									b.setAsleep(false);
									// break from the loop
									break;
								}
							}
						}
					}
					// check if we found an object
					if (this.mouseJoint != null) {
						// if we found one then break from the loop
						break;
					}
				}
			}
		} else if (this.mouseJoint != null) {
			// remove the mouse joint from the world
			this.test.world.remove(this.mouseJoint);
			// make the local reference null
			this.mouseJoint = null;
		}
		
		// look for right click press
		if (this.mouse.isPressed(MouseEvent.BUTTON3)) {
			// don't do anything if we have already determined that the
			// click is in one of the shapes
			if (this.selected == null) {
				// get the move location
				Point p = this.mouse.getRelativeLocation();
				// convert to world coordinates
				Vector2 v = this.test.screenToWorld(p.x, p.y);
				// try to find the object that we are clicking on
				int bSize = this.test.world.getBodyCount();
				for (int i = 0; i < bSize; i++) {
					Body b = this.test.world.getBody(i);
					int cSize = b.getFixtureCount();
					// loop over the shapes in the body
					for (int j = 0; j < cSize; j++) {
						Fixture f = b.getFixture(j);
						Convex c = f.getShape();
						// see if the point is contained in it
						if (c.contains(v, b.getTransform())) {
							// if it is then set the body as the current
							// selected item
							this.selected = b;
							// control the body
							this.controlState = DirectControl.control(b);
							// break from the loop
							break;
						} else {
							// check for line segment
							if (c.isType(Segment.TYPE)) {
								Segment s = (Segment) c;
								// if you are a tenth of a meter from it then consider that
								// selecting the segment
								if (s.contains(v, b.getTransform(), 0.05)) {
									// selected item
									this.selected = b;
									// control the body
									this.controlState = DirectControl.control(b);
									// break from the loop
									break;
								}
							}
						}
					}
					// check if we found an object
					if (selected != null) {
						// if we found one then break from the loop
						break;
					}
				}
			}
		} else if (this.selected != null) {
			// once the mouse button is no longer held down
			// release the body
			DirectControl.release(this.selected, this.controlState);
			// then set the selected shape and old point to null
			this.selected = null;
			this.vOld = null;
			this.controlState = null;
		}
		
		// see if we should check the mouse movement
		if (this.selected != null) {
			// get the new location
			Point newLoc = mouse.getRelativeLocation();
			// convert it to world coordinates
			Vector2 vNew = this.test.screenToWorld(newLoc.x, newLoc.y);
			// make sure there is a previous location to compare to
			if (this.vOld != null) {
				// see if the z key is held down
				if (keyboard.isPressed(KeyEvent.VK_Z)) {
					// then we should rotate the shape
					// get the angle between the new point and the old point
					Vector2 c = this.selected.getWorldCenter();
					Vector2 p1 = c.to(this.vOld);
					Vector2 p2 = c.to(vNew);
					double theta = p1.getAngleBetween(p2);
					// check if theta is more than zero
					if (theta != 0) {
						// rotate the shape by theta
						this.selected.rotate(theta, c);
					}
				} else {
					// then we should translate the shape
					// check if the mouse moved any
					if (this.vOld.distanceSquared(vNew) > 0) {
						// move the shape from the previous position
						// to the new position by translating along
						// the vector from one point to the other
						this.selected.translate(vNew.difference(this.vOld));
					}
				}
			}
			// set the new position
			this.vOld = vNew;
		} else if (this.mouseJoint != null) {
			// get the new location
			Point newLoc = mouse.getRelativeLocation();
			// convert it to world coordinates
			Vector2 vNew = this.test.screenToWorld(newLoc.x, newLoc.y);
			// set the target point for the mouse joint
			this.mouseJoint.setTarget(vNew);
		}
		
		// check for the B key
		if (this.keyboard.isPressed(KeyEvent.VK_B)) {
			// launch a bomb
			Circle bombShape = new Circle(0.25);
			BodyFixture bombFixture = new BodyFixture(bombShape);
			Entity bomb = new Entity();
			bomb.addFixture(bombFixture);
			bomb.setMass();
			// set the elasticity
			bombFixture.setRestitution(0.3);
			// launch from the left
			bomb.getVelocity().set(20.0, 0.0);
			// move the bomb 'off' screen
			bomb.translate(-6.0, 3.0);
			// add the bomb to the world
			this.test.world.add(bomb);
		}
		
		// check the u key
		if (this.keyboard.isPressed(KeyEvent.VK_U)) {
			if (this.keyboard.isPressed(KeyEvent.VK_SHIFT)) {
				this.usage.setRefreshRate(this.usage.getRefreshRate() / 2);
			} else {
				this.usage.setRefreshRate(this.usage.getRefreshRate() * 2);
			}
		}
		
		this.usage.setInput(this.timer.getCurrentTime() - startTime);
	}
	
	/* (non-Javadoc)
	 * @see org.codezealot.game.core.Core#update(long)
	 */
	@Override
	public void update(long elapsedTime) {
		this.usage.update(elapsedTime);
		long startTime = this.timer.getCurrentTime();
		
		super.update(elapsedTime);

		// set the selected test if its changed
		if (!this.test.equals(this.settingsFrame.getTest())) {
			// set the new test
			this.test = this.settingsFrame.getTest();
			// setup the inputs again
			this.initializeInputs();
		}
		
		// set the selected CD algorithm if its changed
		String currentCDAlgo = this.test.world.getNarrowphaseDetector().getClass().getSimpleName();
		// make sure the algorithm setting has changed
		if (!currentCDAlgo.equals(this.settingsFrame.getNPCDAlgorithm())) {
			// if it has then set it
			if ("Gjk".equals(this.settingsFrame.getNPCDAlgorithm())) {
				this.test.world.setNarrowphaseDetector(new Gjk());
			} else if ("Sat".equals(this.settingsFrame.getNPCDAlgorithm())) {
				this.test.world.setNarrowphaseDetector(new Sat());
			}
		}
		
		// set the selected broad-phase CD algorithm if its changed
		String bpAlgo = this.test.world.getBroadphaseDetector().getClass().getSimpleName();
		// make sure the algorithm setting has changed
		if (!bpAlgo.equals(this.settingsFrame.getBPCDAlgorithm())) {
			// if it has then set it
			if ("Sap".equals(this.settingsFrame.getBPCDAlgorithm())) {
				this.test.world.setBroadphaseDetector(new Sap());
			}
		}

		// set the selected manifold solver algorithm
		String msAlgo = this.test.world.getManifoldSolver().getClass().getSimpleName();
		// make sure the algorithm setting has changed
		if (!this.settingsFrame.getMSAlgorithm().startsWith(msAlgo)) {
			// if it has then set it
			if ("Clip".equals(this.settingsFrame.getMSAlgorithm())) {
				this.test.world.setManifoldSolver(new ClippingManifoldSolver());
			}
		}
		
		// set the selected time of impact detector
		String toiAlgo = this.test.world.getTimeOfImpactDetector().getClass() == ConservativeAdvancement.class ? "CA" : "";
		// make sure the algorithm setting has changed
		if (!this.settingsFrame.getTOIAlgorithm().equals(toiAlgo)) {
			// if it has then set it
			if ("CA".equals(this.settingsFrame.getTOIAlgorithm())) {
				this.test.world.setTimeOfImpactDetector(new ConservativeAdvancement());
			}
		}

		// make sure we are not paused
		if (!this.isPaused()) {
			if (this.mode == StepMode.CONTINUOUS) {
				// convert the nanosecond elapsed time to elapsed time in seconds
				double dt = (double)elapsedTime / 1.0e9;
				// update the test
				this.test.world.update(dt);
				// update the test
				this.test.update(dt);
			} else if (this.mode == StepMode.MANUAL) {
				// do nothing since its controlled by the user
			} else {
				// increment the elapsed time
				this.tModeElapsed += elapsedTime;
				// wait until the time to advance the standard amount
				// get the interval in nano seconds
				double nano = this.tModeInterval * 1.0e9;
				if (this.tModeElapsed >= nano) {
					this.test.world.step(1);
					this.test.update(1);
					this.tModeElapsed = 0.0;
				}
			}
		}
		
		this.usage.setUpdate(this.timer.getCurrentTime() - startTime);
	}
	
	/* (non-Javadoc)
	 * @see org.codezealot.game.core.Core#shutdownSubsystems()
	 */
	@Override
	protected synchronized void shutdownSubsystems() {
		// dispose of the settings frame
		this.settingsFrame.dispose();
		// always call the super method here
		super.shutdownSubsystems();
	}
}
