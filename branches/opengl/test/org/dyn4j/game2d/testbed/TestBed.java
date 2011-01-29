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
package org.dyn4j.game2d.testbed;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.naming.ConfigurationException;
import javax.swing.JFrame;

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
import org.dyn4j.game2d.testbed.input.Input;
import org.dyn4j.game2d.testbed.input.Input.Hold;
import org.dyn4j.game2d.testbed.input.Keyboard;
import org.dyn4j.game2d.testbed.input.Mouse;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.glsl.ShaderUtil;

/**
 * Container for the tests.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class TestBed extends GLCanvas implements GLEventListener {
	/** The UID */
	private static final long serialVersionUID = -3159660619562196286L;
	
	/**
	 * The container modes.
	 * @author William Bittle
	 * @version 2.2.3
	 * @since 2.2.3
	 */
	public static enum Mode {
		/** The TestBed is running in an applet */
		APPLET,
		/** The TestBed is running in a desktop application */
		APPLICATION
	}
	
	/**
	 * The stepping modes.
	 * @author William Bittle
	 * @version 2.0.0
	 * @since 2.0.0
	 */
	public static enum StepMode {
		/** Continuous mode */
		CONTINUOUS,
		/** Manual stepping mode */
		MANUAL,
		/** Timed stepping mode */
		TIMED
	}

	// HUD parameters
	
	/** The HUD text color (grey) */
	private static final float[] HUD_TEXT_COLOR = new float[] {0.5f, 0.5f, 0.5f, 1.0f};
	
	/** The HUD height */
	private static final double HUD_HEIGHT = 110.0;
	
	/** The HUD padding */
	private static final double HUD_PADDING = 7.0;
	
	/** The HUD line spacing */
	private static final double HUD_SPACING = (HUD_HEIGHT - HUD_PADDING * 4) / 6;
	
	// State data
	
	/** The Swing/AWT container for this GLCanvas */
	private Container container;
	
	/** The mode application or applet */
	private TestBed.Mode mode;
	
	/** The desired rendering surface size */
	private Dimension size;
	
	/** The keyboard to accept and store key events */
	private Keyboard keyboard;
	
	/** The mouse to accept and store mouse events */
	private Mouse mouse;
	
	/** The FPS monitor */
	private Fps fps;
	
	/** The timer for measuring execution duration */
	private Timer timer;
	
	/** The JOGL automatic animator */
	private Animator animator;
	
	/** Whether the TestBed is paused or not */
	private boolean paused = false;
	
	/** The paused flag lock object */
	private Object pauseLock = new Object();
	
	/** The time usage object */
	private Usage usage = new Usage();
	
	/** The current test */
	private Test test;
	
	/** The frame to set the simulation settings */
	private ControlPanel settingsFrame;

	/** Flag indicating step mode */
	private StepMode stepMode = StepMode.CONTINUOUS;
	
	/** The wait time in seconds between steps in timed stepping mode */
	private double tModeInterval = 0.5;
	
	/** The total elapsed time in nanoseconds for timed stepping mode */
	private double tModeElapsed = 0.0;
	
	// OpenGL data
	
	/** The GLUT object for drawing strings */
	private GLUT glut = new GLUT();
	
	/** The id of the texture that the FBO will render to */
	private int texId;
	
	/** The FBO id for rendering to a different target */
	private int fboId;
	
	/** The id of the blur shader program */
	private int pBlurId;
	
	/** The id of the blur vertex shader */
	private int vsBlurId;
	
	/** The id of the blur fragment shader */
	private int fsBlurId;
	
	/** Whether to use the shader or not */
	private boolean shaderProgramValid = false;
	
	// System information
	
	/** The label for the jre version */
	private static final String JRE_VERSION = TestBed.getJreVersion();
	
	/** The label for the jre mode */
	private static final String JRE_MODE = TestBed.getJreMode();
	
	/** The label for the operating system name */
	private static final String OS_NAME = TestBed.getOsName();
	
	/** The label for the architecture name */
	private static final String OS_ARCHITECTURE = TestBed.getOsArchitecture();
	
	/** The label for the data model name */
	private static final String OS_DATA_MODEL = TestBed.getOsDataModel();
	
	/** The label for the number of processors */
	private static final String PROCESSOR_COUNT = String.valueOf(Runtime.getRuntime().availableProcessors());
	
	// Picking using left click
	
	/** The mouse joint created when picking shapes */
	private MouseJoint mouseJoint = null;
	
	// Picking using right click
	
	/** The selected {@link Body} for picking capability */
	private Body selected = null;
	
	/** The old position for picking capability */
	private Vector2 vOld = null;
	
	/** The saved state of the body under control */
	private DirectControl.State controlState = null;
	
	/**
	 * Full constructor.
	 * @param capabilities the desired OpenGL capabilities; null to use a default set
	 * @param container the container that this TestBed is being added to
	 * @param size the desired size of the rendering area
	 * @param mode the mode; either application or applet
	 */
	public TestBed(GLCapabilities capabilities, Container container, Dimension size, TestBed.Mode mode) {
		// pass the capabilities down
		super(capabilities);
		
		if (container == null) throw new NullPointerException("The container cannot be null.");
		if (size == null) throw new NullPointerException("The desired size cannot be null.");
		if (mode == null) throw new NullPointerException("The mode cannot be null.");
		
		// set the size and mode
		this.size = size;
		this.mode = mode;
		this.container = container;
		
		// set the size
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
		
		// create the keyboard and mouse mappings
		this.keyboard = new Keyboard();
		this.mouse = new Mouse();
		
		// add the listeners
		container.addKeyListener(this.keyboard);
		container.addMouseListener(this.mouse);
		container.addMouseMotionListener(this.mouse);
		container.addMouseWheelListener(this.mouse);
		this.addKeyListener(this.keyboard);
		this.addMouseListener(this.mouse);
		this.addMouseMotionListener(this.mouse);
		this.addMouseWheelListener(this.mouse);
		
		// dont allow the canvas to repaint itself
		this.setIgnoreRepaint(true);
		
		// initialize the fps monitor
		this.fps = new Fps();
		
		// initialize the timer
		this.timer = new Timer();
		
		// add this class as the event listener
		this.addGLEventListener(this);
		
		try {
			// create the simulation settings frame
			this.settingsFrame = new ControlPanel();
			// set the current test
			this.test = this.settingsFrame.getTest();
		} catch (ConfigurationException e) {
			System.err.println("An error occurred when attempting to configure the TestBed.");
			e.printStackTrace();
		}

		// add all the keys
		this.initializeInputs();
	}
	
	/**
	 * Sets up listening for various inputs.
	 */
	protected void initializeInputs() {
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
	
//	/**
//	 * Sets up text images.
//	 */
//	private void initText() {
//		AttributedString timeString = new AttributedString("Time ( Render | Update | System )");
//		timeString.addAttribute(TextAttribute.FOREGROUND, new Color(222, 48, 12), 7, 13);
//		timeString.addAttribute(TextAttribute.FOREGROUND, new Color(222, 117, 0), 16, 22);
//		timeString.addAttribute(TextAttribute.FOREGROUND, new Color(20, 134, 222), 25, 31);
//		this.timeUsageLabel = new SimpleText(timeString);
//		
//		try {
//			jreVersionValue = new SimpleText(System.getProperty("java.runtime.version"));
//			jreModeValue = new SimpleText(System.getProperty("java.vm.info"));
//			osNameValue = new SimpleText(System.getProperty("os.name"));
//			osArchitectureValue = new SimpleText(System.getProperty("os.arch"));
//			osDataModelValue = new SimpleText(System.getProperty("sun.arch.data.model"));
//		} catch (SecurityException e) {
//			jreVersionValue = new SimpleText("Unknown");
//			jreModeValue = new SimpleText("Unknown");
//			osNameValue = new SimpleText("Unknown");
//			osArchitectureValue = new SimpleText("Unknown");
//			osDataModelValue = new SimpleText("Unknown");
//		}
//	}
	
	/**
	 * Starts automatic rendering of this TestBed.
	 */
	public void start() {
		// begin the render loop
		this.animator = new Animator(this);
		// start the animator
		this.animator.start();
		// reset the timer; this is to make sure that the system doesn't explode
		// out of control at first (since the first elapsed time will be huge)
		this.timer.reset();
	}
	
	/**
	 * Returns true if this TestBed is paused.
	 * @return boolean
	 */
	public boolean isPaused() {
		return this.paused;
	}
	
	/**
	 * Pauses or unpauses this TestBed.
	 * @param flag true if the TestBed should be paused
	 */
	public void setPaused(boolean flag) {
		// obtain the lock on the paused variable
		synchronized (this.pauseLock) {
			this.paused = flag;
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void init(GLAutoDrawable glDrawable) {
		int width = this.size.width;
		int height = this.size.height;
		
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
		
		// set the clear color to white
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		// disable depth testing since we are working in 2D
		gl.glDisable(GL.GL_DEPTH_TEST);
		
		// enable blending for translucency
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
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
		boolean useFrameBuffer = false;
		if (status == GL.GL_FRAMEBUFFER_COMPLETE) {
			useFrameBuffer = true;
		}
		// switch back to the default frame buffer
		gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		
		// ignore the primary color source
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);
		
		// set the swap interval to as fast as possible
		gl.setSwapInterval(0);
		
		// make sure a shader compiler is available and make sure that
		// we were able to setup a framebuffer to write to
		if (useFrameBuffer && ShaderUtil.isShaderCompilerAvailable(gl)) {
			// load the shader programs from the file system
			// we do this first so that we know whether to create them or not
			Shader vsBlur, fsBlur;
			try {
				vsBlur = Shader.load("/shaders/blur.vs");
				fsBlur = Shader.load("/shaders/blur.fs");
				
				// create the shaders
				vsBlurId = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
				fsBlurId = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
				
				// set the source for the shaders
				gl.glShaderSource(vsBlurId, vsBlur.source.length, vsBlur.source, vsBlur.lengths, 0);
				gl.glShaderSource(fsBlurId, fsBlur.source.length, fsBlur.source, fsBlur.lengths, 0);
				
				// compile the shaders
				gl.glCompileShader(vsBlurId);
				gl.glCompileShader(fsBlurId);
				
				// create the program
				pBlurId = gl.glCreateProgram();
				
				// attach the shaders to the programs
				gl.glAttachShader(pBlurId, vsBlurId);
				gl.glAttachShader(pBlurId, fsBlurId);
				
				// link the program
				gl.glLinkProgram(pBlurId);
				
				// validate the programs
				gl.glValidateProgram(pBlurId);
				
				// verify the shader program can be used
				if (ShaderUtil.isProgramValid(gl, pBlurId, System.out)
				 && ShaderUtil.isShaderStatusValid(gl, vsBlurId, GL2.GL_COMPILE_STATUS, System.out)
				 && ShaderUtil.isShaderStatusValid(gl, fsBlurId, GL2.GL_COMPILE_STATUS, System.out)) {
					// we are good to use the shader
					shaderProgramValid = true;
				}
			} catch (FileNotFoundException e) {
				System.err.println("File not found in classpath:");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("An error occurred when reading a file:");
				e.printStackTrace();
			}
		}
	}
	
    /* (non-Javadoc)
     * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
     */
    @Override
    public void dispose(GLAutoDrawable glDrawable) {
    	// dispose of any resources
    	GL2 gl = glDrawable.getGL().getGL2();
    	
    	// create an array of ids to pass to the delete functions
    	int[] ids = new int[] { fboId, texId };
    	
    	// delete the FBO (if this is the current draw target glBindFramebuffer is called
    	// using the default target implicitly)
    	gl.glDeleteFramebuffers(1, ids, 0);
    	
    	// delete the texture that the FBO was rendering into
    	gl.glDeleteTextures(1, ids, 1);
    	
    	// detach the shaders from the shader program
    	gl.glDetachShader(pBlurId, vsBlurId);
    	gl.glDetachShader(pBlurId, fsBlurId);
    	
    	// delete the shaders
    	gl.glDeleteShader(vsBlurId);
    	gl.glDeleteShader(fsBlurId);
    	
    	// delete the shader program
    	gl.glDeleteProgram(pBlurId);
    }
    
    /* (non-Javadoc)
     * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
     */
	@Override
	public void display(GLAutoDrawable glDrawable) {
		// perform other operations at the start of the loop
		{
			// poll for input
			this.poll();
			
			// get the elapsed time
			long currentTime = this.timer.getCurrentTime();
			long elapsedTime = this.timer.getElapsedTime(currentTime);
			
			// update the TestBed
			this.update(elapsedTime);
			
			// update the timer
			this.timer.update(currentTime);
			
			// update the fps
			this.fps.update(elapsedTime);
		}
		
		// draw the scene
		
		// get the current time
		long startTime = this.timer.getCurrentTime();

		// get the draw singleton
		Draw draw = Draw.getInstance();
		
		// get the rendering width and height
		int width = this.size.width;
		int height = this.size.height;
		
		GL2 gl = glDrawable.getGL().getGL2();
		
		// perform initial setup given the control panel draw settings
		
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
		
		// check for vertical sync
		if (draw.isVerticalSyncEnabled()) {
			if (gl.getSwapInterval() == 0) {
				gl.setSwapInterval(1);
			}
		} else {
			if (gl.getSwapInterval() == 1) {
				gl.setSwapInterval(0);
			}
		}
		
		// see if we need to blur the panel
		if (draw.drawPanel() && draw.isPanelBlurred() && this.shaderProgramValid) {
			// if so, then we need to render to an off screen buffer
			// first then render that off screen buffer to the screen
			// applying a blur using a shader program
			
			// switch to draw to the FBO
			gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fboId);
			// set the clear color
			gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		}
		
		// begin drawing
		
		// clear the screen
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		// switch to the model view matrix
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		// initialize the matrix (0,0) is in the center of the window
		gl.glLoadIdentity();
		
		// check for a null test
		if (this.test != null) {
			// render the test
			this.test.render(gl, width, height);
		}
		
		// see if we need to blur the panel
		if (draw.drawPanel() && draw.isPanelBlurred() && this.shaderProgramValid) {
			// unbind the off screen buffer by telling OpenGL to use
			// the default buffer
			gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
			
			// set the clear color
			gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
			// clear the buffer
			gl.glClear(GL.GL_COLOR_BUFFER_BIT);
			
			// set the texture to the off screen buffer we have
			// been rendering to
			gl.glBindTexture(GL.GL_TEXTURE_2D, texId);
			
			// tell OpenGL to use our shader program instead of the default
			gl.glUseProgram(pBlurId);
			// pass the parameters to the shader program
			// NOTE: for sampler objects you pass the texture ordinal, not the id
			gl.glUniform1i(0, 0);
			// pass in the width and height of the texture
			gl.glUniform1f(1, width);
			gl.glUniform1f(2, height);
			// pass in the yOffset, any y value above this number is blurred
			gl.glUniform1f(3, 110.0f / height);
			
			// draw the texture to a 2d quad
			gl.glBegin(GL2.GL_QUADS);
				gl.glTexCoord2d(1, 0);
				gl.glVertex2d(width * 0.5, -height * 0.5);
				gl.glTexCoord2d(0, 0);
				gl.glVertex2d(-width * 0.5, -height * 0.5);
				gl.glTexCoord2d(0, 1);
				gl.glVertex2d(-width * 0.5, height * 0.5);
				gl.glTexCoord2d(1, 1);
				gl.glVertex2d(width * 0.5, height * 0.5);
			gl.glEnd();
			
			// switch back to the default texture
			gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
			// tell OpenGL to use the default shader program
			gl.glUseProgram(0);
		}
		
		// render the paused overlay if the simulation is paused
		if (this.isPaused()) {
			// show a black translucent screen over everything
			gl.glColor4f(0.0f, 0.0f, 0.0f, 0.3f);
			GLHelper.fillRectangle(gl, 0, 0, width, height);
			// show the paused label in the top left corner
			this.renderPaused(gl, glut, width, height);
		}
		
		// render the open control panel text
		
		this.renderControls(gl, glut, -width * 0.5 + 300, height * 0.5 - 15.0);
		
		// render the HUD panel
		
		if (draw.drawPanel()) {
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glTranslated(-width * 0.5, -height * 0.5 + HUD_HEIGHT, 0.0);
			
			// draw the translucent backgrounds
			gl.glBegin(GL2.GL_QUADS);
				// the overall background
				gl.glColor4f(0.0f, 0.0f, 0.0f, 0.8f);
				gl.glVertex2d(0.0, 0.0);
				gl.glVertex2d(0.0, -HUD_HEIGHT);
				gl.glVertex2d(width, -HUD_HEIGHT);
				gl.glVertex2d(width, 0.0);
				
				gl.glColor4f(0.0f, 0.0f, 0.0f, 0.3f);
				// the information background (w = 150)
				gl.glVertex2d(HUD_PADDING, -HUD_PADDING);
				gl.glVertex2d(HUD_PADDING + 150.0, -HUD_PADDING);
				gl.glVertex2d(HUD_PADDING + 150.0, -HUD_HEIGHT + HUD_PADDING);
				gl.glVertex2d(HUD_PADDING, -HUD_HEIGHT + HUD_PADDING);
				// the contact background (w = 120)
				gl.glVertex2d(2 * HUD_PADDING + 150.0, -HUD_PADDING);
				gl.glVertex2d(2 * HUD_PADDING + 270.0, -HUD_PADDING);
				gl.glVertex2d(2 * HUD_PADDING + 270.0, -HUD_HEIGHT + HUD_PADDING);
				gl.glVertex2d(2 * HUD_PADDING + 150.0, -HUD_HEIGHT + HUD_PADDING);
				// the performance background (w = 180)
				gl.glVertex2d(3 * HUD_PADDING + 270.0, -HUD_PADDING);
				gl.glVertex2d(3 * HUD_PADDING + 450.0, -HUD_PADDING);
				gl.glVertex2d(3 * HUD_PADDING + 450.0, -HUD_HEIGHT + HUD_PADDING);
				gl.glVertex2d(3 * HUD_PADDING + 270.0, -HUD_HEIGHT + HUD_PADDING);
				// the system info background (w = 200)
				gl.glVertex2d(4 * HUD_PADDING + 450.0, -HUD_PADDING);
				gl.glVertex2d(4 * HUD_PADDING + 650.0, -HUD_PADDING);
				gl.glVertex2d(4 * HUD_PADDING + 650.0, -HUD_HEIGHT + HUD_PADDING);
				gl.glVertex2d(4 * HUD_PADDING + 450.0, -HUD_HEIGHT + HUD_PADDING);
			gl.glEnd();
			
			// draw the borders
			// save the line width
			float[] lw = new float[1];
			gl.glGetFloatv(GL.GL_LINE_WIDTH, lw, 0);
			// make it wider
			gl.glLineWidth(2.0f);
			gl.glColor4f(0.5f, 0.5f, 0.5f, 0.3f);
			// the information border (w = 150)
			gl.glBegin(GL2.GL_LINE_LOOP);
				gl.glVertex2d(HUD_PADDING, -HUD_PADDING);
				gl.glVertex2d(HUD_PADDING + 150.0, -HUD_PADDING);
				gl.glVertex2d(HUD_PADDING + 150.0, -HUD_HEIGHT + HUD_PADDING);
				gl.glVertex2d(HUD_PADDING, -HUD_HEIGHT + HUD_PADDING);
			gl.glEnd();
			// the contact background (w = 120)
			gl.glBegin(GL2.GL_LINE_LOOP);
				gl.glVertex2d(2 * HUD_PADDING + 150.0, -HUD_PADDING);
				gl.glVertex2d(2 * HUD_PADDING + 270.0, -HUD_PADDING);
				gl.glVertex2d(2 * HUD_PADDING + 270.0, -HUD_HEIGHT + HUD_PADDING);
				gl.glVertex2d(2 * HUD_PADDING + 150.0, -HUD_HEIGHT + HUD_PADDING);
			gl.glEnd();
			// the performance background (w = 180)
			gl.glBegin(GL2.GL_LINE_LOOP);
				gl.glVertex2d(3 * HUD_PADDING + 270.0, -HUD_PADDING);
				gl.glVertex2d(3 * HUD_PADDING + 450.0, -HUD_PADDING);
				gl.glVertex2d(3 * HUD_PADDING + 450.0, -HUD_HEIGHT + HUD_PADDING);
				gl.glVertex2d(3 * HUD_PADDING + 270.0, -HUD_HEIGHT + HUD_PADDING);
			gl.glEnd();
			// the system info background (w = 200)
			gl.glBegin(GL2.GL_LINE_LOOP);
				gl.glVertex2d(4 * HUD_PADDING + 450.0, -HUD_PADDING);
				gl.glVertex2d(4 * HUD_PADDING + 650.0, -HUD_PADDING);
				gl.glVertex2d(4 * HUD_PADDING + 650.0, -HUD_HEIGHT + HUD_PADDING);
				gl.glVertex2d(4 * HUD_PADDING + 450.0, -HUD_HEIGHT + HUD_PADDING);
			gl.glEnd();
			// reset the line width
			gl.glLineWidth(lw[0]);
			
			// draw the test information
			this.renderTestInformation(gl, glut, 2 * HUD_PADDING, -2 * HUD_PADDING);
			
			// draw the contact information
			this.renderContactInformation(gl, glut, 3 * HUD_PADDING + 150.0, -2 * HUD_PADDING);
			
			// draw the contact information
			this.renderPerformanceInformation(gl, glut, 4 * HUD_PADDING + 270.0, -2 * HUD_PADDING);
			
			// draw the system information
			this.renderSystemInformation(gl, glut, 5 * HUD_PADDING + 450.0, -2 * HUD_PADDING);
			
			// restore the previous matrix
			gl.glPopMatrix();
		}
		
		this.usage.setRender(this.timer.getCurrentTime() - startTime);
	}
	
	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	@Override
	public void reshape(GLAutoDrawable glDrawable, int x, int y, int width, int height) {
		// do nothing since the window is not resizeable
	}
	
	/**
	 * Renders the paused label in the top left corner along with a transparent black
	 * overlay over the entire window.
	 * @param gl the OpenGL graphics context
	 * @param glut the GLUT for drawing strings
	 * @param w the width of the bounding rectangle
	 * @param h the height of the bounding rectangle
	 */
	private void renderPaused(GL2 gl, GLUT glut, double w, double h) {
		// save the current matrix
		gl.glPushMatrix();
		// reset the current matrix
		gl.glLoadIdentity();
		// set the color to red
		gl.glColor4f(1.0f, 0.0f, 0.0f, 0.7f);
		// fill a red rectangle to show the paused label
		GLHelper.fillRectangle(gl, w / 2.0 - 50.0, h / 2.0 - 12, 100, 24);
		// set the color to white
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		// set the raster for the text
		gl.glRasterPos2d(w / 2.0 - 70.0, h / 2.0 - 15.0);
		// draw the text
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "Paused");
		// reset to the old matrix
		gl.glPopMatrix();
	}
	
	/**
	 * Renders the controls label at the given coordinates.
	 * @param gl the OpenGL graphics context
	 * @param glut the GLUT for drawing strings
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	private void renderControls(GL2 gl, GLUT glut, double x, double y) {
		// set the text color
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		
		// save the current matrix
		gl.glPushMatrix();
		// reset the current matrix
		gl.glLoadIdentity();
		// set the render position
		gl.glRasterPos2d(x, y);
		
		// render the text
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Press 'c' to open the Test Bed Control Panel.");
		
		// restore the original matrix
		gl.glPopMatrix();
	}
	
	/**
	 * Renders the test information at the given coordinates.
	 * @param gl the OpenGL graphics context
	 * @param glut the GLUT for drawing strings
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	private void renderTestInformation(GL2 gl, GLUT glut, double x, double y) {
		// set the padding/spacing of the text lines and values
		final int padding = 55;
		
		// set the text color
		gl.glColor4fv(HUD_TEXT_COLOR, 0);
		
		// save the current matrix
		gl.glPushMatrix();
		
		y -= HUD_SPACING * 0.7;
		// render the version
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Version:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, Version.getVersion());
		
		y -= HUD_SPACING;
		// render the test name
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Test:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, this.test.getName());
		
		y -= HUD_SPACING;
		// render the zoom
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Scale:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, this.test.getZoom() + " px/m");
		
		y -= HUD_SPACING;
		// render the number of bodies
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Bodies:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, String.valueOf(this.test.getWorld().getBodyCount()));
		
		y -= HUD_SPACING;
		// render the mode
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Mode:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		if (this.stepMode == StepMode.MANUAL) {
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Manual");
		} else if (this.stepMode == StepMode.TIMED) {
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Timed");
		} else {
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Continuous");
		}
		
		Point loc = this.mouse.getRelativeLocation();
		Vector2 pos = this.test.screenToWorld(loc.x, loc.y);
		DecimalFormat df = new DecimalFormat("0.000");
		
		y -= HUD_SPACING;
		// render the mouse world position
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "( " + df.format(pos.x) + ", " + df.format(pos.y) + " )");
		
		// restore the original matrix
		gl.glPopMatrix();
	}
	
	/**
	 * Renders the contact information at the given coordinates.
	 * @param gl the OpenGL graphics context
	 * @param glut the GLUT for drawing strings
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	private void renderContactInformation(GL2 gl, GLUT glut, double x, double y) {
		// set the padding/spacing of the text lines and values
		final int padding = 70;
		
		// set the text color
		gl.glColor4fv(HUD_TEXT_COLOR, 0);
		
		// save the current matrix
		gl.glPushMatrix();
		
		y -= HUD_SPACING * 0.7;
		// render the version
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Contacts");
		
		// get the contact counter
		ContactCounter cc = (ContactCounter) this.test.getWorld().getContactListener();
		// get the numbers
		int total = cc.getSolved();
		int added = cc.getAdded();
		int persisted = cc.getPersisted();
		int removed = cc.getRemoved();
		int sensed = cc.getSensed();
		
		y -= HUD_SPACING;
		// render the version
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Total:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, String.valueOf(total));
		
		y -= HUD_SPACING;
		// render the version
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Added:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, String.valueOf(added));
		
		y -= HUD_SPACING;
		// render the version
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Persisted:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, String.valueOf(persisted));
		
		y -= HUD_SPACING;
		// render the version
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Removed:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, String.valueOf(removed));
		
		y -= HUD_SPACING;
		// render the version
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Sensed:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, String.valueOf(sensed));
		
		// restore the original matrix
		gl.glPopMatrix();
	}
	
	/**
	 * Renders the performance information at the given coordinates.
	 * @param gl the OpenGL graphics context
	 * @param glut the GLUT for drawing strings
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	private void renderPerformanceInformation(GL2 gl, GLUT glut, double x, double y) {
		// set the padding/spacing of the text lines and values
		final int padding = 80;
		final double ufBarWidth = 130.0;
		final double ptBarWidth = 160.0;
		
		// set the text color
		gl.glColor4fv(HUD_TEXT_COLOR, 0);
		
		// save the current matrix
		gl.glPushMatrix();
		
		y -= HUD_SPACING * 0.7;
		// render the frames per second
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Frames/Second:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, String.valueOf((int) Math.floor(this.fps.getFps())));
		
		y -= HUD_SPACING;
		// show the total memory usage
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Memory:");
		// render the value
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, nf.format(this.usage.getTotalMemory() / 1024.0 / 1024.0) + " MB");
		gl.glRasterPos2d(x + padding + 55, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Total");
		
		y -= HUD_SPACING * 0.5;
		// render the used/free bars
		gl.glColor4f(1.0f, 0.6f, 0.2f, 0.8f);
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex2d(x, y);
			gl.glVertex2d(x + this.usage.getUsedMemoryPercentage() * ufBarWidth, y);
			gl.glVertex2d(x + this.usage.getUsedMemoryPercentage() * ufBarWidth, y - 10);
			gl.glVertex2d(x, y - 10);
		gl.glEnd();
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.9f);
		gl.glBegin(GL2.GL_LINE_LOOP);
			gl.glVertex2d(x, y);
			gl.glVertex2d(x + ufBarWidth, y);
			gl.glVertex2d(x + ufBarWidth, y - 10);
			gl.glVertex2d(x, y - 10);
		gl.glEnd();
		y -= HUD_SPACING * 0.5;
		gl.glColor4fv(HUD_TEXT_COLOR, 0);
		gl.glRasterPos2d(x + padding + 55, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Used");
		
		y -= HUD_SPACING * 0.5;
		gl.glColor4f(0.5f, 0.7f, 0.0f, 0.8f);
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex2d(x, y);
			gl.glVertex2d(x + this.usage.getFreeMemoryPercentage() * ufBarWidth, y);
			gl.glVertex2d(x + this.usage.getFreeMemoryPercentage() * ufBarWidth, y - 10);
			gl.glVertex2d(x, y - 10);
		gl.glEnd();
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.9f);
		gl.glBegin(GL2.GL_LINE_LOOP);
			gl.glVertex2d(x, y);
			gl.glVertex2d(x + ufBarWidth, y);
			gl.glVertex2d(x + ufBarWidth, y - 10);
			gl.glVertex2d(x, y - 10);
		gl.glEnd();
		y -= HUD_SPACING * 0.5;
		gl.glColor4fv(HUD_TEXT_COLOR, 0);
		gl.glRasterPos2d(x + padding + 55, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Free");
		
		// render the % time
		y -= HUD_SPACING;
		
		// render the colored labels
		gl.glRasterPos2d(x, y); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Time (");
		gl.glColor4f(0.9f, 0.2f, 0.0f, 1.0f); gl.glRasterPos2d(x + 28, y);  glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Render");
		gl.glColor4fv(HUD_TEXT_COLOR, 0);     gl.glRasterPos2d(x + 62, y);  glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "|");
		gl.glColor4f(0.9f, 0.5f, 0.0f, 1.0f); gl.glRasterPos2d(x + 66, y);  glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Update");
		gl.glColor4fv(HUD_TEXT_COLOR, 0);     gl.glRasterPos2d(x + 100, y); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "|");
		gl.glColor4f(0.0f, 0.5f, 0.9f, 1.0f); gl.glRasterPos2d(x + 104, y); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "System");
		gl.glColor4fv(HUD_TEXT_COLOR, 0);     gl.glRasterPos2d(x + 140, y); glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, ")");
		
		// show the time usage bar
		y -= HUD_SPACING * 0.5;
		// get the widths depending on the percentage of time
		double renderW = this.usage.getRenderTimePercentage() * ptBarWidth;
		double updateW = this.usage.getUpdateTimePercentage() * ptBarWidth;
		// since input polling time is so low, just consider it part of the system time
		double systemW = (this.usage.getSystemTimePercentage() + this.usage.getInputTimePercentage()) * ptBarWidth;
		// draw the bars
		gl.glBegin(GL2.GL_QUADS);
			gl.glColor4f(0.9f, 0.2f, 0.0f, 0.8f);
			gl.glVertex2d(x, y);
			gl.glVertex2d(x + renderW, y);
			gl.glVertex2d(x + renderW, y - 10);
			gl.glVertex2d(x, y - 10);
			gl.glColor4f(0.9f, 0.5f, 0.0f, 0.8f);
			gl.glVertex2d(x + renderW, y);
			gl.glVertex2d(x + renderW + updateW, y);
			gl.glVertex2d(x + renderW + updateW, y - 10);
			gl.glVertex2d(x + renderW, y - 10);
			gl.glColor4f(0.0f, 0.5f, 0.9f, 0.8f);
			gl.glVertex2d(x + renderW + updateW, y);
			gl.glVertex2d(x + renderW + updateW + systemW, y);
			gl.glVertex2d(x + renderW + updateW + systemW, y - 10);
			gl.glVertex2d(x + renderW + updateW, y - 10);
		gl.glEnd();
		// draw the outline
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.9f);
		gl.glBegin(GL2.GL_LINE_LOOP);
			gl.glVertex2d(x, y);
			gl.glVertex2d(x + ptBarWidth, y);
			gl.glVertex2d(x + ptBarWidth, y - 10);
			gl.glVertex2d(x, y - 10);
		gl.glEnd();
		
		// draw the percentages
		y -= HUD_SPACING * 0.5;
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glRasterPos2d(x + 2, y - 2);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, Math.round(this.usage.getRenderTimePercentage() * 100) + "%");
		gl.glRasterPos2d(x + renderW + 2, y - 2);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, Math.round(this.usage.getUpdateTimePercentage() * 100) + "%");
		gl.glRasterPos2d(x + ptBarWidth - 25, y - 2);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, Math.round(this.usage.getSystemTimePercentage() * 100) + "%");
		
		// restore the original matrix
		gl.glPopMatrix();
	}
	
	/**
	 * Renders some system information at the given coordinates.
	 * @param gl the OpenGL graphics context
	 * @param glut the GLUT for drawing strings
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	private void renderSystemInformation(GL2 gl, GLUT glut, double x, double y) {
		// set the padding/spacing of the text lines and values
		final int padding = 80;

		// set the text color
		gl.glColor4fv(HUD_TEXT_COLOR, 0);
		
		// save the current matrix
		gl.glPushMatrix();
		
		y -= HUD_SPACING * 0.7;
		// render the frames per second
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "JRE Version:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, TestBed.JRE_VERSION);
		
		y -= HUD_SPACING;
		// render the frames per second
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "JRE Mode:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, TestBed.JRE_MODE);
		
		y -= HUD_SPACING;
		// render the frames per second
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "OS Name:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, TestBed.OS_NAME);
		
		y -= HUD_SPACING;
		// render the frames per second
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Architecture:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, TestBed.OS_ARCHITECTURE);
		
		y -= HUD_SPACING;
		// render the frames per second
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Data Model:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, TestBed.OS_DATA_MODEL);
		
		y -= HUD_SPACING;
		// render the frames per second
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Processors:");
		// render the value
		gl.glRasterPos2d(x + padding, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, TestBed.PROCESSOR_COUNT);
		
		// restore the original matrix
		gl.glPopMatrix();
	}
	
	/**
	 * Polls the keyboard and mouse for input.
	 */
	protected void poll() {
		long startTime = this.timer.getCurrentTime();
		
		// allow the current test to override the default functionality
		this.test.poll(this.keyboard, this.mouse);
		
		// check the escape key
		if (this.keyboard.isPressed(KeyEvent.VK_ESCAPE) || this.keyboard.isPressed(KeyEvent.VK_E)) {
			// only exit if its not applet mode
			if (this.mode == Mode.APPLICATION) {
				// we have to stop the animator in another thread so that
				// it blocks until its stopped
				Thread t = new Thread() {
					public void run() {
						// stop the animator
						animator.stop();
						JFrame frame = (JFrame)container;
						// hide the frame
						frame.setVisible(false);
						// dispose of resources (so the dispose method
						// on the GLEventListener is called)
						frame.dispose();
						// finally exit the JVM
						System.exit(0);
					}
				};
				t.start();
			}
		}
		
		// check the space key
		if (this.keyboard.isPressed(KeyEvent.VK_PAUSE) || this.keyboard.isPressed(KeyEvent.VK_P)) {
			// pause or unpause
			this.setPaused(!this.isPaused());
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
			if (this.stepMode == StepMode.CONTINUOUS) {
				this.stepMode = StepMode.MANUAL;
			} else if (this.stepMode == StepMode.MANUAL) {
				this.tModeElapsed = 0.0;
				this.stepMode = StepMode.TIMED;
			} else {
				this.stepMode = StepMode.CONTINUOUS;
			}
		}
		
		// check for the m key
		if (this.stepMode == StepMode.MANUAL && this.keyboard.isPressed(KeyEvent.VK_M)) {
			this.test.world.step(1);
			this.test.update(1);
		}
		
		// check for the t key
		if (this.stepMode == StepMode.TIMED && this.keyboard.isPressed(KeyEvent.VK_T)) {
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
	
	/**
	 * Updates the TestBed given an elapsed time in nanoseconds.
	 * @param elapsedTime the elapsed time in nanoseconds
	 */
	protected void update(long elapsedTime) {
		this.usage.update(elapsedTime);
		long startTime = this.timer.getCurrentTime();

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
			if (this.stepMode == StepMode.CONTINUOUS) {
				// convert the nanosecond elapsed time to elapsed time in seconds
				double dt = (double)elapsedTime / 1.0e9;
				// update the test
				this.test.world.update(dt);
				// update the test
				this.test.update(dt);
			} else if (this.stepMode == StepMode.MANUAL) {
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
	
	/**
	 * Returns the system property for the java runtime version.
	 * <p>
	 * Returns the string "Unknown" if a security exception is thrown.
	 * @return String
	 */
	private static final String getJreVersion() {
		try {
			String value = System.getProperty("java.runtime.version");
			if (value == null) {
				value = "Unknown";
			}
			return value;
		} catch (SecurityException e) {
			return "Unknown";
		}
	}
	
	/**
	 * Returns the system property for the JRE mode.
	 * <p>
	 * Returns the string "Unknown" if a security exception is thrown.
	 * @return String
	 */
	private static final String getJreMode() {
		try {
			String value = System.getProperty("java.vm.info");
			if (value == null) {
				value = "Unknown";
			}
			return value;
		} catch (SecurityException e) {
			return "Unknown";
		}
	}
	
	/**
	 * Returns the system property for the operating system name.
	 * <p>
	 * Returns the string "Unknown" if a security exception is thrown.
	 * @return String
	 */
	private static final String getOsName() {
		try {
			String value = System.getProperty("os.name");
			if (value == null) {
				value = "Unknown";
			}
			return value;
		} catch (SecurityException e) {
			return "Unknown";
		}
	}
	
	/**
	 * Returns the system property for the operating system architecture.
	 * <p>
	 * Returns the string "Unknown" if a security exception is thrown.
	 * @return String
	 */
	private static final String getOsArchitecture() {
		try {
			String value = System.getProperty("os.arch");
			if (value == null) {
				value = "Unknown";
			}
			return value;
		} catch (SecurityException e) {
			return "Unknown";
		}
	}
	
	/**
	 * Returns the system property for the operating system data model.
	 * <p>
	 * Returns the string "Unknown" if a security exception is thrown.
	 * @return String
	 */
	private static final String getOsDataModel() {
		try {
			String value = System.getProperty("sun.arch.data.model");
			// check if the property was found
			if (value == null) {
				value = "Unknown";
			}
			return value;
		} catch (SecurityException e) {
			return "Unknown";
		}
	}
}
