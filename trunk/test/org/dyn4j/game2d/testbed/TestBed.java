/*
 * Copyright (c) 2010, William Bittle
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
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Logger;

import javax.naming.ConfigurationException;

import org.codezealot.game.core.G2dCore;
import org.codezealot.game.input.Input;
import org.codezealot.game.input.Input.Hold;
import org.codezealot.game.render.Container;
import org.codezealot.game.render.G2dSurface;
import org.dyn4j.game2d.Version;
import org.dyn4j.game2d.collision.broadphase.Sap;
import org.dyn4j.game2d.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.game2d.collision.narrowphase.Gjk;
import org.dyn4j.game2d.collision.narrowphase.Sat;
import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.Fixture;
import org.dyn4j.game2d.dynamics.joint.MouseJoint;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Container for the tests.
 * @author William Bittle
 * @param <E> the container type
 * @version 1.1.0
 * @since 1.0.0
 */
public class TestBed<E extends Container<G2dSurface>> extends G2dCore<E> {
	/** The class logger */
	private static final Logger LOGGER = Logger.getLogger(TestBed.class.getName());
	
	/** The text color */
	private static final Color TEXT_COLOR = Color.GRAY;
	
	/** The time usage object */
	private Usage usage = new Usage();
	
	/** The current test */
	private Test test;
	
	/** The frame to set the simulation settings */
	private ControlPanel settingsFrame;

	/** Flag indicating step mode */
	private boolean stepMode = false;
		
	// text labels
	/** The label for the control panel key */
	private Text controlsLabel;
	/** The label for the version */
	private Text versionLabel;
	/** The label for the current test */
	private Text testLabel;
	/** The label for the current zoom */
	private Text zoomLabel;
	/** The label for the current number of bodies */
	private Text bodyCountLabel;
	/** The label for the current simulation mode */
	private Text modeLabel;
	/** The label for continuous mode */
	private Text continuousModeLabel;
	/** The label for step mode */
	private Text stepModeLabel;
	/** The label for the contacts table */
	private Text contactLabel;
	/** The label for the total number of contacts */
	private Text cTotalLabel;
	/** The label for the number of added contacts */
	private Text cAddedLabel;
	/** The label for the number of persisted contacts */
	private Text cPersistedLabel;
	/** The label for the number of removed contacts */
	private Text cRemovedLabel;
	/** The label for the number of sensed contacts */
	private Text cSensedLabel;
	/** The label for the frame rate */
	private Text fpsLabel;
	/** The label indicating paused state */
	private Text pausedLabel;
	/** The label for memory usage */
	private Text memoryLabel;
	/** The label for used memory */
	private Text usedMemoryLabel;
	/** The label for free memory */
	private Text freeMemoryLabel;
	/** The label for total memory */
	private Text totalMemoryLabel;
	/** The label for time usage */
	private Text timeUsageLabel;
	/** The label for the jre version */
	private Text jreVersionLabel;
	/** The label for the jre mode */
	private Text jreModeLabel;
	/** The label for the operating system name */
	private Text osNameLabel;
	/** The label for the architecture name */
	private Text osArchitectureLabel;
	/** The label for the data model name */
	private Text osDataModelLabel;
	/** The label for the number of processors */
	private Text processorCountLabel;
	/** The label for the jre version */
	private Text jreVersionValue;
	/** The label for the jre mode */
	private Text jreModeValue;
	/** The label for the operating system name */
	private Text osNameValue;
	/** The label for the architecture name */
	private Text osArchitectureValue;
	/** The label for the data model name */
	private Text osDataModelValue;
	/** The label for the number of processors */
	private Text processorCountValue;
	
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
	
	/**
	 * Full constructor.
	 * @param container the rendering container
	 */
	public TestBed(E container) {
		super(container);
	}

	/* (non-Javadoc)
	 * @see org.codezealot.game.core.G2dCore#initialize()
	 */
	@Override
	protected void initialize() {
		super.initialize();
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
	}

	/**
	 * Sets up listening for various inputs.
	 */
	private void initializeInputs() {
		// make sure the input mappings are clear
		this.keyboard.clear();
		this.mouse.clear();
		
		// listen for some keys
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
		// perform step in step mode
		this.keyboard.add(new Input(KeyEvent.VK_S, Hold.NO_HOLD));
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
		// key to increas/decrease the metrics update rate
		this.keyboard.add(new Input(KeyEvent.VK_I, Hold.NO_HOLD));
		this.keyboard.add(new Input(KeyEvent.VK_D, Hold.NO_HOLD));
		
		// initialize the keys for the test
		this.test.initializeInput(this.keyboard, this.mouse);
	}
	
	/**
	 * Sets up text images.
	 */
	private void initText() {
		// text for the control panel
		AttributedString controlsString = new AttributedString("Press 'c' to open the Test Bed Control Panel.");
		this.controlsLabel = new Text(controlsString);
		this.controlsLabel.generate();
		
		// text for the current test
		AttributedString versionString = new AttributedString("Version:");
		this.versionLabel = new Text(versionString);
		this.versionLabel.generate();
		
		// text for the current test
		AttributedString testString = new AttributedString("Test:");
		this.testLabel = new Text(testString);
		this.testLabel.generate();
		
		// text for the current zoom
		AttributedString zoomString = new AttributedString("Scale:");
		this.zoomLabel = new Text(zoomString);
		this.zoomLabel.generate();
		
		// text for the number of bodies
		AttributedString bodiesString = new AttributedString("Bodies:");
		this.bodyCountLabel = new Text(bodiesString);
		this.bodyCountLabel.generate();
		
		// text for the simulation mode
		AttributedString modeLabelString = new AttributedString("Mode:");
		this.modeLabel = new Text(modeLabelString);
		this.modeLabel.generate();
		
		AttributedString cModeString = new AttributedString("Continuous");
		this.continuousModeLabel = new Text(cModeString);
		this.continuousModeLabel.generate();
		
		AttributedString sModeString = new AttributedString("Step");
		this.stepModeLabel = new Text(sModeString);
		this.stepModeLabel.generate();
		
		// text for contacts
		AttributedString contactsString = new AttributedString("Contact Information");
		this.contactLabel = new Text(contactsString);
		this.contactLabel.generate();
		
		AttributedString cTotalString = new AttributedString("Total:");
		this.cTotalLabel = new Text(cTotalString);
		this.cTotalLabel.generate();
		
		AttributedString cAddedString = new AttributedString("Added:");
		this.cAddedLabel = new Text(cAddedString);
		this.cAddedLabel.generate();
		
		AttributedString cPersistedString = new AttributedString("Persisted:");
		this.cPersistedLabel = new Text(cPersistedString);
		this.cPersistedLabel.generate();
		
		AttributedString cRemovedString = new AttributedString("Removed:");
		this.cRemovedLabel = new Text(cRemovedString);
		this.cRemovedLabel.generate();
		
		AttributedString cSensedString = new AttributedString("Sensed:");
		this.cSensedLabel = new Text(cSensedString);
		this.cSensedLabel.generate();
		
		// text for frames per second
		AttributedString fpsString = new AttributedString("FPS:");
		this.fpsLabel = new Text(fpsString);
		this.fpsLabel.generate();
		
		AttributedString pausedString = new AttributedString("Paused");
		this.pausedLabel = new Text(pausedString);
		this.pausedLabel.generate();
		
		AttributedString memString = new AttributedString("Memory:");
		this.memoryLabel = new Text(memString);
		this.memoryLabel.generate();
		
		AttributedString umemString = new AttributedString("Used");
		this.usedMemoryLabel = new Text(umemString);
		this.usedMemoryLabel.generate();
		
		AttributedString fmemString = new AttributedString("Free");
		this.freeMemoryLabel = new Text(fmemString);
		this.freeMemoryLabel.generate();
		
		AttributedString tmemString = new AttributedString("Total");
		this.totalMemoryLabel = new Text(tmemString);
		this.totalMemoryLabel.generate();
		
		AttributedString timeString = new AttributedString("Time ( Render | Update | System )");
		timeString.addAttribute(TextAttribute.FOREGROUND, new Color(222, 48, 12), 7, 13);
		timeString.addAttribute(TextAttribute.FOREGROUND, new Color(222, 117, 0), 16, 22);
		timeString.addAttribute(TextAttribute.FOREGROUND, new Color(20, 134, 222), 25, 31);
		this.timeUsageLabel = new Text(timeString);
		this.timeUsageLabel.generate();
		
		AttributedString jreVersionString = new AttributedString("JRE Version:");
		this.jreVersionLabel = new Text(jreVersionString);
		this.jreVersionLabel.generate();
		
		AttributedString jreModeString = new AttributedString("JRE Mode:");
		this.jreModeLabel = new Text(jreModeString);
		this.jreModeLabel.generate();
		
		AttributedString osNameString = new AttributedString("OS Name:");
		this.osNameLabel = new Text(osNameString);
		this.osNameLabel.generate();
		
		AttributedString osArchitectureString = new AttributedString("Architecture:");
		this.osArchitectureLabel = new Text(osArchitectureString);
		this.osArchitectureLabel.generate();
		
		AttributedString osDataModelString = new AttributedString("Data Model:");
		this.osDataModelLabel = new Text(osDataModelString);
		this.osDataModelLabel.generate();
		
		AttributedString processorCountString = new AttributedString("Processors:");
		this.processorCountLabel = new Text(processorCountString);
		this.processorCountLabel.generate();
		
		String jreVersion = System.getProperty("java.runtime.version"); // the jre version
		String jreMode = System.getProperty("java.vm.info"); // mixed mode or interpreted
		String osArchitecture = System.getProperty("os.arch"); // x86, amd64, etc.
		String osDataModel = System.getProperty("sun.arch.data.model"); // 32 or 64 bit
		String osName = System.getProperty("os.name"); // os name
		// get the number of processors
		int processors = Runtime.getRuntime().availableProcessors();
		
		this.jreVersionValue = new Text(new AttributedString(jreVersion));
		this.jreVersionValue.generate();
		
		this.jreModeValue = new Text(new AttributedString(jreMode));
		this.jreModeValue.generate();
		
		this.osNameValue = new Text(new AttributedString(osName));
		this.osNameValue.generate();
		
		this.osArchitectureValue = new Text(new AttributedString(osArchitecture));
		this.osArchitectureValue.generate();
		
		this.osDataModelValue = new Text(new AttributedString(osDataModel));
		this.osDataModelValue.generate();
		
		this.processorCountValue = new Text(new AttributedString(String.valueOf(processors)));
		this.processorCountValue.generate();
	}
	
	/* (non-Javadoc)
	 * @see org.codezealot.game.core.G2dCore#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		// get the current time
		long startTime = this.timer.getCurrentTime();
		super.render(g);
		
		// get the rendering width and height
		int width = this.renderer.getDisplaySize().width;
		int height = this.renderer.getDisplaySize().height;
		
		// set the background color to white
		g.setBackground(Color.WHITE);
		g.setClip(0, 0, width, height);
		
		// paint the background
		g.clearRect(0, 0, width, height);
		
		// paint the test
		this.test.render(g, width, height);
		
		// get the draw singleton
		Draw draw = Draw.getInstance();
		
		// render the controls label top center
		this.renderControls(g, (int) Math.ceil((width - this.controlsLabel.getWidth()) / 2.0), 5);
		
		// make sure we should draw the metrics panel
		if (draw.drawPanel()) {
			// draw the translucent background
			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.8f));
			g.fillRect(0, height - 110, width, 110);
			
			// draw the gradient top
			g.setPaint(new GradientPaint(0, height - 110, new Color(0.5f, 0.5f, 0.5f, 0.5f), 0, height - 101, new Color(0.0f, 0.0f, 0.0f, 0.5f)));
			g.fillRect(0, height - 110, width, 10);
			
			// draw the small box around the test info
			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.2f));
			g.fillRect(2, height - 98, 150, 95);
			g.setColor(Color.BLACK);
			g.drawRect(2, height - 98, 150, 95);
			// render the general test information
			this.renderTestInformation(g, 7, height - 95);
			
			// draw the small box around the contact info
			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.2f));
			g.fillRect(155, height - 98, 120, 95);
			g.setColor(Color.BLACK);
			g.drawRect(155, height - 98, 120, 95);
			// render the contact information
			this.renderContactInformation(g, 159, height - 95);
			
			// draw the small box around the performance info
			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.2f));
			g.fillRect(278, height - 98, 200, 95);
			g.setColor(Color.BLACK);
			g.drawRect(278, height - 98, 200, 95);
			// render the performance information
			this.renderPerformanceInformation(g, 282, height - 95);
			
			// draw the small box around the system info
			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.2f));
			g.fillRect(481, height - 98, 200, 95);
			g.setColor(Color.BLACK);
			g.drawRect(481, height - 98, 200, 95);
			// render the system information
			this.renderSystemInformation(g, 485, height - 95);
		}
		
		// always show the paused box on top of everything
		if (this.isPaused()) {
			// show the paused label in the top left corner
			this.renderPaused(g, 0, 0, 100, 20);
		}
		
		this.usage.setRender(this.timer.getCurrentTime() - startTime);
	}
	
	/**
	 * Renders the paused label to the given graphics object at the
	 * given screen coordinates.
	 * @param g the graphics object to render to
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param w the width of the bounding rectangle
	 * @param h the height of the bounding rectangle
	 */
	private void renderPaused(Graphics2D g, int x, int y, int w, int h) {
		g.setColor(new Color(1.0f, 0.0f, 0.0f, 0.7f));
		// render a red background behind the text
		g.fillRect(x, y, w, h);
		
		// set the paused text color to white
		g.setColor(Color.WHITE);
		// get the text metrics
		double tw = this.pausedLabel.getWidth();
		double th = this.pausedLabel.getHeight();
		
		// render the text in the center of the given rect
		this.pausedLabel.render(g, x + (int) Math.ceil((w - tw) / 2.0), y + (int) Math.ceil((h - th) / 2.0));
	}
	
	/**
	 * Renders the controls label to the given graphics object at the
	 * given screen coordinates.
	 * @param g the graphics object to render to
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	private void renderControls(Graphics2D g, int x, int y) {
		// set the text color
		g.setColor(Color.BLACK);
		
		//width - w - 5, height - h - 5
		controlsLabel.render(g, x, y);
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
		AttributedString versionString = new AttributedString("v" + Version.getVersion());
		Text version = new Text(versionString);
		version.generate();
		version.render(g, x + padding, y);
		
		// render the label
		this.testLabel.render(g, x, y + spacing);
		// render the value
		AttributedString testString = new AttributedString(this.test.getName());
		Text test = new Text(testString);
		test.generate();
		test.render(g, x + padding, y + spacing);

		// show the zoom
		// render the label
		this.zoomLabel.render(g, x, y + spacing * 2);
		// render the value
		AttributedString zoomString = new AttributedString(this.test.getZoom() + " px/m");
		Text zoom = new Text(zoomString);
		zoom.generate();
		zoom.render(g, x + padding, y + spacing * 2);
		
		// show the number of bodies
		// render the label
		this.bodyCountLabel.render(g, x, y + spacing * 3);
		// render the value
		AttributedString bodiesString = new AttributedString(String.valueOf(this.test.getWorld().getBodyCount()));
		Text bodies = new Text(bodiesString);
		bodies.generate();
		bodies.render(g, x + padding, y + spacing * 3);
		
		// show the mode
		// render the label
		this.modeLabel.render(g, x, y + spacing * 4);
		// render the value
		if (this.stepMode) {
			this.stepModeLabel.render(g, x + padding, y + spacing * 4);
		} else {
			this.continuousModeLabel.render(g, x + padding, y + spacing * 4);
		}
		
		Point loc = this.mouse.getRelativeLocation();
		Vector2 pos = this.test.screenToWorld(loc.x, loc.y);
		DecimalFormat df = new DecimalFormat("0.000");
		// show the current x,y of the mouse
		AttributedString mouseString = new AttributedString("( " + df.format(pos.x) + ", " + df.format(pos.y) + " )");
		Text mousePos = new Text(mouseString);
		mousePos.generate();
		mousePos.render(g, x, y + spacing * 5);
	}
	
	/**
	 * Renders the contace information to the given graphics object at the
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
		AttributedString ctString = new AttributedString(String.valueOf(total));
		Text ct = new Text(ctString);
		ct.generate();
		
		AttributedString caString = new AttributedString(String.valueOf(added));
		Text ca = new Text(caString);
		ca.generate();
		
		AttributedString cpString = new AttributedString(String.valueOf(persisted));
		Text cp = new Text(cpString);
		cp.generate();
		
		AttributedString crString = new AttributedString(String.valueOf(removed));
		Text cr = new Text(crString);
		cr.generate();
		
		AttributedString csString = new AttributedString(String.valueOf(sensed));
		Text cs = new Text(csString);
		cs.generate();
		
		// render the values
		ct.render(g, x + padding - ct.getWidth(), y + spacing);
		ca.render(g, x + padding - ca.getWidth(), y + spacing * 2);
		cp.render(g, x + padding - cp.getWidth(), y + spacing * 3);
		cr.render(g, x + padding - cr.getWidth(), y + spacing * 4);
		cs.render(g, x + padding - cs.getWidth(), y + spacing * 5);
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
		AttributedString fpsString = new AttributedString(String.valueOf(iFps));
		Text fps = new Text(fpsString);
		fps.generate();
		fps.render(g, x + padding, y);
		
		// show the total memory usage
		double barWidth = 100;
		this.memoryLabel.render(g, x, y + spacing);
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		AttributedString tot = new AttributedString(nf.format(this.usage.getTotalMemory() / 1024.0 / 1024.0) + "M");
		Text tota = new Text(tot);
		tota.generate();
		tota.render(g, x + padding, y + spacing);
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
		
		// show the time usage bar
		this.timeUsageLabel.render(g, x, y + spacing * 4);
		double renderW = this.usage.getRenderTimePercentage() * barWidth;
		double updateW = this.usage.getUpdateTimePercentage() * barWidth;
		// since input polling time is so low, just consider it part of the system time
		double systemW = (this.usage.getSystemTimePercentage() + this.usage.getInputTimePercentage()) * barWidth;
		g.setColor(new Color(222, 48, 12));
		g.fillRect(x, y + spacing * 5, (int) Math.ceil(renderW), 12);
		g.setColor(new Color(222, 117, 0));
		g.fillRect(x + (int) Math.ceil(renderW), y + spacing * 5, (int) Math.ceil(updateW), 12);
		g.setColor(new Color(20, 134, 222));
		g.fillRect(x + (int) Math.ceil(renderW) + (int) Math.ceil(updateW), y + spacing * 5, (int) Math.ceil(systemW), 12);
		g.setColor(Color.BLACK);
		g.drawRect(x, y + spacing * 5, (int) Math.ceil(barWidth) + 1, 12);
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
		// check the escape key
		if (this.keyboard.isPressed(KeyEvent.VK_ESCAPE) || this.keyboard.isPressed(KeyEvent.VK_E)) {
			// only exit if its not applet mode
			if (this.renderer.getMode() == Container.Mode.APPLICATION) {
				// exit the game
				this.shutdown();	
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
			this.stepMode = !this.stepMode;
		}
		
		// check for the s key
		if (this.keyboard.isPressed(KeyEvent.VK_S) && this.stepMode) {
			this.test.world.step(1);
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
					int cSize = b.getShapeCount();
					// loop over the shapes in the body
					for (int j = 0; j < cSize; j++) {
						Convex c = b.getShape(j);
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
					int cSize = b.getShapeCount();
					// loop over the shapes in the body
					for (int j = 0; j < cSize; j++) {
						Convex c = b.getShape(j);
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
			Fixture bombFixture = new Fixture(bombShape);
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
		
		// check the i key
		if (this.keyboard.isPressed(KeyEvent.VK_I)) {
			this.usage.setRefreshRate(this.usage.getRefreshRate() / 2);
		}
		
		// check the d key
		if (this.keyboard.isPressed(KeyEvent.VK_D)) {
			this.usage.setRefreshRate(this.usage.getRefreshRate() * 2);
		}
		
		// call the test poll method
		this.test.poll(this.keyboard, this.mouse);
		
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

		// convert the nanosecond elapsed time to elapsed time in seconds
		double dt = (double)elapsedTime / 1.0e9;
		// update the test
		this.test.update(this.isPaused(), this.stepMode, dt);
		
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
