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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.ConfigurationException;

import org.codezealot.game.core.G2dCore;
import org.codezealot.game.input.Input;
import org.codezealot.game.input.Input.Hold;
import org.codezealot.game.render.Container;
import org.codezealot.game.render.G2dSurface;
import org.dyn4j.game2d.collision.broadphase.Sap;
import org.dyn4j.game2d.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.game2d.collision.narrowphase.Gjk;
import org.dyn4j.game2d.collision.narrowphase.Sat;
import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.Mass;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Container for the tests.
 * @author William Bittle
 * @param <E> the container type
 */
public class TestBed<E extends Container<G2dSurface>> extends G2dCore<E> {
	/** The class logger */
	private static final Logger LOGGER = Logger.getLogger(TestBed.class.getName());

	/** One second in nanoseconds */
	private static final long ONE_SECOND_IN_NANOSECONDS = 1000000000;
		
	/** The last update time in nanoseconds */
	private long lastUpdateTime = 0;
	
	/** The input polling elapsed time in nanoseconds */
	private long inputElapsedTime = 0;
	
	/** The updating elapsed time in nanoseconds */
	private long updateElapsedTime = 0;
	
	/** The rendering elapsed time in nanoseconds */
	private long renderElapsedTime = 0;
	
	/** The number of iterations between metric time calculations */
	private long iterations = 0;
	
	/** The average input polling time in milliseconds */
	private double inputAvgTime = 0;
	
	/** The average updating time in milliseconds */
	private double updateAvgTime = 0;
	
	/** The average rendering time in milliseconds */
	private double renderAvgTime = 0;
	
	/** The decimal formater */
	private DecimalFormat format = new DecimalFormat("000.00000");

	/** The current test */
	private Test test;
	
	/** The frame to set the simulation settings */
	private ControlPanel settingsFrame;

	/** Flag indicating step mode */
	private boolean stepMode = false;
	
	// text labels
	/** The label for the control panel key */
	private Text controlsLabel;
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
	/** The label for the frame rate */
	private Text fpsLabel;
	/** The label for the time to render */
	private Text renderLabel;
	/** The label for the time to accept input */
	private Text inputLabel;
	/** The label for the time to update the simulation */
	private Text updateLabel;
	/** The label indicating paused state */
	private Text pausedLabel;
	
	// picking
	/** The selected {@link Body} for picking capability */
	private Body selected = null;
	/** The old position for picking capability */
	private Vector vOld = null;
	/** The old mass for picking capability */
	private Mass mOld = null;
	
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
		// button to hold to pick a shape
		this.mouse.add(new Input(MouseEvent.BUTTON1));
		// key to hold when rotating a picked shape
		this.keyboard.add(new Input(KeyEvent.VK_Z));
		// key to launch a bomb
		this.keyboard.add(new Input(KeyEvent.VK_B, Hold.NO_HOLD));
		
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
		AttributedString testString = new AttributedString("Test:");
		this.testLabel = new Text(testString);
		this.testLabel.generate();
		
		// text for the current zoom
		AttributedString zoomString = new AttributedString("Zoom:");
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
		sModeString.addAttribute(TextAttribute.FOREGROUND, Color.BLUE);
		this.stepModeLabel = new Text(sModeString);
		this.stepModeLabel.generate();
		
		// text for contacts
		AttributedString contactsString = new AttributedString("Contacts");
		contactsString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
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
		
		// text for frames per second
		AttributedString fpsString = new AttributedString("FPS:");
		this.fpsLabel = new Text(fpsString);
		this.fpsLabel.generate();
		
		// text for rendering duration
		AttributedString renderingString = new AttributedString("Rendering:");
		this.renderLabel = new Text(renderingString);
		this.renderLabel.generate();
		
		AttributedString inputString = new AttributedString("Input:");
		this.inputLabel = new Text(inputString);
		this.inputLabel.generate();
		
		AttributedString updateString = new AttributedString("Updating:");
		this.updateLabel = new Text(updateString);
		this.updateLabel.generate();
		
		AttributedString pausedString = new AttributedString("Paused");
		pausedString.addAttribute(TextAttribute.FOREGROUND, Color.RED);
		this.pausedLabel = new Text(pausedString);
		this.pausedLabel.generate();
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
		
		// set the color to black
		g.setColor(Color.WHITE);
		// paint the background
		g.fillRect(0, 0, width, height);
		
		// paint the test
		this.test.render(g, width, height);
		
		// get the draw singleton
		Draw draw = Draw.getInstance();
		
		// set the color to white
		g.setColor(Color.DARK_GRAY);
		
		// make sure we should draw text
		if (draw.drawText()) {
			// show the controls message in the bottom right corner
			
			// get the width of the text
			double w = controlsLabel.getWidth();
			double h = controlsLabel.getHeight();
			controlsLabel.render(g, width - w - 5, height - h - 5);
			
			// show general information in the top left corner
			
			// show the curren test
			// render the label
			this.testLabel.render(g, 5, 5);
			// render the value
			AttributedString testString = new AttributedString(this.test.name);
			Text test = new Text(testString);
			test.generate();
			test.render(g, 50, 5);

			// show the zoom
			// render the label
			this.zoomLabel.render(g, 5, 20);
			// render the value
			AttributedString zoomString = new AttributedString(this.test.getZoom() + "x");
			Text zoom = new Text(zoomString);
			zoom.generate();
			zoom.render(g, 50, 20);
			
			// show the number of bodies
			// render the label
			this.bodyCountLabel.render(g, 5, 35);
			// render the value
			AttributedString bodiesString = new AttributedString(String.valueOf(this.test.getWorld().getBodies().size()));
			Text bodies = new Text(bodiesString);
			bodies.generate();
			bodies.render(g, 50, 35);
			
			// show the mode
			// render the label
			this.modeLabel.render(g, 5, 50);
			
			// render the value
			if (this.stepMode) {
				this.stepModeLabel.render(g, 50, 50);
			} else {
				this.continuousModeLabel.render(g, 50, 50);
			}
			
			// show contact information in the top right corner
			
			// show the contact label
			this.contactLabel.render(g, 700, 5);
			// show the contact values
			// render the labels
			this.cTotalLabel.render(g, 700, 20);
			this.cAddedLabel.render(g, 700, 35);
			this.cPersistedLabel.render(g, 700, 50);
			this.cRemovedLabel.render(g, 700, 65);
			
			// display the number of persisted
			ContactCounter cc = (ContactCounter) this.test.getWorld().getContactListener();
			// get the numbers
			int total = cc.getSolved();
			int added = cc.getAdded();
			int persisted = cc.getPersisted();
			int removed = cc.getRemoved();
			
			AttributedString ctString = new AttributedString(String.valueOf(total));
			Text ct = new Text(ctString);
			ct.generate();
			ct.render(g, width - ct.getWidth() - 5, 20);
			
			AttributedString caString = new AttributedString(String.valueOf(added));
			Text ca = new Text(caString);
			ca.generate();
			ca.render(g, width - ca.getWidth() - 5, 35);
			
			AttributedString cpString = new AttributedString(String.valueOf(persisted));
			Text cp = new Text(cpString);
			cp.generate();
			cp.render(g, width - cp.getWidth() - 5, 50);
			
			AttributedString crString = new AttributedString(String.valueOf(removed));
			Text cr = new Text(crString);
			cr.generate();
			cr.render(g, width - cr.getWidth() - 5, 65);

			// show frame rate information in the bottom left corner
			
			// render the frames per second
			// render the label
			this.fpsLabel.render(g, 5, 535);
			this.renderLabel.render(g, 5, 550);
			this.inputLabel.render(g, 5, 565);
			this.updateLabel.render(g, 5, 580);
			// render the value
			AttributedString fpsString = new AttributedString(String.valueOf(this.fps.getFps()));
			Text fps = new Text(fpsString);
			fps.generate();
			fps.render(g, 150 - fps.getWidth(), 535);
			
			AttributedString renderingString = new AttributedString(this.format.format(this.renderAvgTime) + " ms");
			Text render = new Text(renderingString);
			render.generate();
			render.render(g, 150 - render.getWidth(), 550);
			
			AttributedString inputString = new AttributedString(this.format.format(this.inputAvgTime) + " ms");
			Text input = new Text(inputString);
			input.generate();
			input.render(g, 150 - input.getWidth(), 565);
			
			AttributedString updateString = new AttributedString(this.format.format(this.updateAvgTime) + " ms");
			Text update = new Text(updateString);
			update.generate();
			update.render(g, 150 - update.getWidth(), 580);
		}
		
		// always show the paused box
		if (this.isPaused()) {
			// show the paused text top center and bottom center
			this.pausedLabel.render(g, (width - this.pausedLabel.getWidth()) / 2.0, 5);
			this.pausedLabel.render(g, (width - this.pausedLabel.getWidth()) / 2.0, height - this.pausedLabel.getHeight() - 5);
		}
		
		this.renderElapsedTime += this.timer.getCurrentTime() - startTime;
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
			for (Body b : this.test.getWorld().getBodies()) {
				System.out.println(b);
			}
		}
		
		// see if we should check the mouse movement
		if (this.selected != null) {
			// get the new location
			Point newLoc = mouse.getRelativeLocation();
			// convert it to world coordinates
			Vector vNew = this.test.screenToWorld(newLoc.x, newLoc.y);
			// make sure there is a previous location to compare to
			if (this.vOld != null) {
				// see if the z key is held down
				if (keyboard.isPressed(KeyEvent.VK_Z)) {
					// then we should rotate the shape
					// get the angle between the new point and the old point
					Vector c = this.selected.getWorldCenter();
					Vector p1 = c.to(this.vOld);
					Vector p2 = c.to(vNew);
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
		}
		
		// look for left click press
		if (mouse.isPressed(MouseEvent.BUTTON1)) {
			// don't do anything if we have already determined that the
			// click is in one of the shapes
			if (this.selected == null) {
				// get the move location
				Point p = mouse.getRelativeLocation();
				// convert to world coordinates
				Vector v = this.test.screenToWorld(p.x, p.y);
				// try to find the object that we are clicking on
				for (Body b : this.test.world.getBodies()) {
					// loop over the shapes in the body
					for (Convex c : b.getShapes()) {
						// see if the point is contained in it
						if (c.contains(v, b.getTransform())) {
							// if it is then set the body as the current
							// selected item
							this.selected = b;
							// control the body
							this.mOld = this.test.world.control(this.selected);
							// break from the loop
							break;
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
			this.test.world.relinquish(this.selected, this.mOld);
			// then set the selected shape and old point to null
			this.selected = null;
			this.vOld = null;
			// set the old mass to null
			this.mOld = null;
		}
		
		// check for the B key
		if (this.keyboard.isPressed(KeyEvent.VK_B)) {
			// launch a bomb
			Circle bombShape = new Circle(0.25); List<Convex> shapes = new ArrayList<Convex>(1); shapes.add(bombShape);
			Entity bomb = new Entity(shapes, Mass.create(bombShape, 1.0));
			// set the elasticity
			bomb.setE(0.3);
			// launch from the left
			bomb.getV().set(20.0, 0.0);
			// move the bomb 'off' screen
			bomb.translate(-6.0, 3.0);
			// add the bomb to the world
			this.test.world.add(bomb);
		}
		
		// call the test poll method
		this.test.poll(this.keyboard, this.mouse);
		
		this.inputElapsedTime += this.timer.getCurrentTime() - startTime;
	}
	
	/* (non-Javadoc)
	 * @see org.codezealot.game.core.Core#update(long)
	 */
	@Override
	public void update(long elapsedTime) {
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
		double dt = elapsedTime / 1000000000.0;
		// update the test
		this.test.update(this.isPaused(), this.stepMode, dt);

		this.lastUpdateTime += elapsedTime;
		this.updateElapsedTime += this.timer.getCurrentTime() - startTime;

		this.iterations++;
		
		if (this.lastUpdateTime >= ONE_SECOND_IN_NANOSECONDS) {
			this.renderAvgTime = (double)this.renderElapsedTime / (double)this.iterations / 1000000.0;
			this.updateAvgTime = (double)this.updateElapsedTime / (double)this.iterations / 1000000.0;
			this.inputAvgTime = (double)this.inputElapsedTime / (double)this.iterations / 1000000.0;
			this.renderElapsedTime = 0;
			this.updateElapsedTime = 0;
			this.inputElapsedTime = 0;
			this.lastUpdateTime = 0;
			this.iterations = 0;
		}
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
