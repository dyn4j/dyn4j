/*
 * Copyright (c) 2010-2015 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.samples;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.examples.Graphics2DRenderer;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

/**
 * A somewhat complex scene with a ragdoll.
 * @author William Bittle
 * @since 3.2.0
 * @version 3.2.0
 */
public class Ragdoll extends JFrame {
	/** The serial version id */
	private static final long serialVersionUID = 5663760293144882635L;
	
	/** The scale 45 pixels per meter */
	public static final double SCALE = 64.0;
	
	/** The conversion factor from nano to base */
	public static final double NANO_TO_BASE = 1.0e9;

	/**
	 * Custom Body class to add drawing functionality.
	 * @author William Bittle
	 * @version 3.0.2
	 * @since 3.0.0
	 */
	public static class GameObject extends Body {
		/** The color of the object */
		protected Color color;
		
		/**
		 * Default constructor.
		 */
		public GameObject() {
			// randomly generate the color
			this.color = new Color(
					(float)Math.random() * 0.5f + 0.5f,
					(float)Math.random() * 0.5f + 0.5f,
					(float)Math.random() * 0.5f + 0.5f);
		}
		
		/**
		 * Draws the body.
		 * <p>
		 * Only coded for polygons and circles.
		 * @param g the graphics object to render to
		 */
		public void render(Graphics2D g) {
			// save the original transform
			AffineTransform ot = g.getTransform();
			
			// transform the coordinate system from world coordinates to local coordinates
			AffineTransform lt = new AffineTransform();
			lt.translate(this.transform.getTranslationX() * SCALE, this.transform.getTranslationY() * SCALE);
			lt.rotate(this.transform.getRotation());
			
			// apply the transform
			g.transform(lt);
			
			// loop over all the body fixtures for this body
			for (BodyFixture fixture : this.fixtures) {
				// get the shape on the fixture
				Convex convex = fixture.getShape();
				Graphics2DRenderer.render(g, convex, SCALE, this.color);
			}

			// set the original transform
			g.setTransform(ot);
		}
	}
	
	/** The canvas to draw to */
	protected Canvas canvas;
	
	/** The dynamics engine */
	protected World world;
	
	/** Wether the example is stopped or not */
	protected boolean stopped;
	
	/** The time stamp for the last iteration */
	protected long last;
	
	/**
	 * Default constructor for the window
	 */
	public Ragdoll() {
		super("Graphics2D Example");
		// setup the JFrame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// add a window listener
		this.addWindowListener(new WindowAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				// before we stop the JVM stop the example
				stop();
				super.windowClosing(e);
			}
		});
		
		// create the size of the window
		Dimension size = new Dimension(800, 600);
		
		// create a canvas to paint to 
		this.canvas = new Canvas();
		this.canvas.setPreferredSize(size);
		this.canvas.setMinimumSize(size);
		this.canvas.setMaximumSize(size);
		
		// add the canvas to the JFrame
		this.add(this.canvas);
		
		// make the JFrame not resizable
		// (this way I dont have to worry about resize events)
		this.setResizable(false);
		
		// size everything
		this.pack();
		
		// make sure we are not stopped
		this.stopped = false;
		
		// setup the world
		this.initializeWorld();
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 * <p>
	 * Basically the same shapes from the Shapes test in
	 * the TestBed.
	 */
	protected void initializeWorld() {
		// create the world
		this.world = new World();
		
		// exported and slightly modified from Sandbox
		
		// Ground
	    Body ground = new GameObject();
	    {// Fixture1
	      Convex c = Geometry.createRectangle(100.0, 1.0);
	      BodyFixture bf = new BodyFixture(c);
	      ground.addFixture(bf);
	    }
	    ground.translate(new Vector2(0.6875, -8.75));
	    ground.setMass(MassType.INFINITE);
	    world.addBody(ground);

	    // Head
	    Body head = new GameObject();
	    {// Fixture2
	      Convex c = Geometry.createCircle(0.25);
	      BodyFixture bf = new BodyFixture(c);
	      head.addFixture(bf);
	    }
	    head.setMass(MassType.NORMAL);
	    world.addBody(head);

	    // Torso
	    Body torso = new GameObject();
	    {// Fixture4
	      Convex c = Geometry.createRectangle(0.5, 1.0);
	      BodyFixture bf = new BodyFixture(c);
	      torso.addFixture(bf);
	    }
	    {// Fixture16
	      Convex c = Geometry.createRectangle(1.0, 0.25);
	      c.translate(new Vector2(0.00390625, 0.375));
	      BodyFixture bf = new BodyFixture(c);
	      torso.addFixture(bf);
	    }
	    torso.translate(new Vector2(0.0234375, -0.8125));
	    torso.setMass(MassType.NORMAL);
	    world.addBody(torso);

	    // Right Humerus
	    Body rightHumerus = new GameObject();
	    {// Fixture5
	      Convex c = Geometry.createRectangle(0.25, 0.5);
	      BodyFixture bf = new BodyFixture(c);
	      rightHumerus.addFixture(bf);
	    }
	    rightHumerus.translate(new Vector2(0.4375, -0.609375));
	    rightHumerus.setMass(MassType.NORMAL);
	    world.addBody(rightHumerus);

	    // Right Ulna
	    Body rightUlna = new GameObject();
	    {// Fixture6
	      Convex c = Geometry.createRectangle(0.25, 0.4);
	      BodyFixture bf = new BodyFixture(c);
	      rightUlna.addFixture(bf);
	    }
	    rightUlna.translate(new Vector2(0.44140625, -0.98828125));
	    rightUlna.setMass(MassType.NORMAL);
	    world.addBody(rightUlna);

	    // Neck
	    Body neck = new GameObject();
	    {// Fixture7
	      Convex c = Geometry.createRectangle(0.15, 0.2);
	      BodyFixture bf = new BodyFixture(c);
	      neck.addFixture(bf);
	    }
	    neck.translate(new Vector2(0.015625, -0.2734375));
	    neck.setMass(MassType.NORMAL);
	    world.addBody(neck);

	    // Left Humerus
	    Body leftHumerus = new GameObject();
	    {// Fixture9
	      Convex c = Geometry.createRectangle(0.25, 0.5);
	      BodyFixture bf = new BodyFixture(c);
	      leftHumerus.addFixture(bf);
	    }
	    leftHumerus.translate(new Vector2(-0.3828125, -0.609375));
	    leftHumerus.setMass(MassType.NORMAL);
	    world.addBody(leftHumerus);

	    // Left Ulna
	    Body leftUlna = new GameObject();
	    {// Fixture11
	      Convex c = Geometry.createRectangle(0.25, 0.4);
	      BodyFixture bf = new BodyFixture(c);
	      leftUlna.addFixture(bf);
	    }
	    leftUlna.translate(new Vector2(-0.3828125, -0.9765625));
	    leftUlna.setMass(MassType.NORMAL);
	    world.addBody(leftUlna);

	    // Right Femur
	    Body rightFemur = new GameObject();
	    {// Fixture12
	      Convex c = Geometry.createRectangle(0.25, 0.75);
	      BodyFixture bf = new BodyFixture(c);
	      rightFemur.addFixture(bf);
	    }
	    rightFemur.translate(new Vector2(0.1796875, -1.5703125));
	    rightFemur.setMass(MassType.NORMAL);
	    world.addBody(rightFemur);

	    // Left Femur
	    Body leftFemur = new GameObject();
	    {// Fixture13
	      Convex c = Geometry.createRectangle(0.25, 0.75);
	      BodyFixture bf = new BodyFixture(c);
	      leftFemur.addFixture(bf);
	    }
	    leftFemur.translate(new Vector2(-0.1328125, -1.5703125));
	    leftFemur.setMass(MassType.NORMAL);
	    world.addBody(leftFemur);

	    // Right Tibia
	    Body rightTibia = new GameObject();
	    {// Fixture14
	      Convex c = Geometry.createRectangle(0.25, 0.5);
	      BodyFixture bf = new BodyFixture(c);
	      rightTibia.addFixture(bf);
	    }
	    rightTibia.translate(new Vector2(0.18359375, -2.11328125));
	    rightTibia.setMass(MassType.NORMAL);
	    world.addBody(rightTibia);

	    // Left Tibia
	    Body leftTibia = new GameObject();
	    {// Fixture15
	      Convex c = Geometry.createRectangle(0.25, 0.5);
	      BodyFixture bf = new BodyFixture(c);
	      leftTibia.addFixture(bf);
	    }
	    leftTibia.translate(new Vector2(-0.1328125, -2.1171875));
	    leftTibia.setMass(MassType.NORMAL);
	    world.addBody(leftTibia);

	    // Head to Neck
	    RevoluteJoint headToNeck = new RevoluteJoint(head, neck, new Vector2(0.01, -0.2));
	    headToNeck.setLimitEnabled(false);
	    headToNeck.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    headToNeck.setReferenceAngle(Math.toRadians(0.0));
	    headToNeck.setMotorEnabled(false);
	    headToNeck.setMotorSpeed(Math.toRadians(0.0));
	    headToNeck.setMaximumMotorTorque(0.0);
	    headToNeck.setCollisionAllowed(false);
	    world.addJoint(headToNeck);
	    
	    // Neck to Torso
	    RevoluteJoint neckToTorso = new RevoluteJoint(neck, torso, new Vector2(0.01, -0.35));
	    neckToTorso.setLimitEnabled(false);
	    neckToTorso.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    neckToTorso.setReferenceAngle(Math.toRadians(0.0));
	    neckToTorso.setMotorEnabled(false);
	    neckToTorso.setMotorSpeed(Math.toRadians(0.0));
	    neckToTorso.setMaximumMotorTorque(0.0);
	    neckToTorso.setCollisionAllowed(false);
	    world.addJoint(neckToTorso);
	    
	    // Torso to Left Humerus
	    RevoluteJoint torsoToLeftHumerus = new RevoluteJoint(torso, leftHumerus, new Vector2(-0.4, -0.4));
	    torsoToLeftHumerus.setLimitEnabled(false);
	    torsoToLeftHumerus.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    torsoToLeftHumerus.setReferenceAngle(Math.toRadians(0.0));
	    torsoToLeftHumerus.setMotorEnabled(false);
	    torsoToLeftHumerus.setMotorSpeed(Math.toRadians(0.0));
	    torsoToLeftHumerus.setMaximumMotorTorque(0.0);
	    torsoToLeftHumerus.setCollisionAllowed(false);
	    world.addJoint(torsoToLeftHumerus);
	    
	    // Torso to Right Humerus
	    RevoluteJoint torsoToRightHumerus = new RevoluteJoint(torso, rightHumerus, new Vector2(0.4, -0.4));
	    torsoToRightHumerus.setLimitEnabled(false);
	    torsoToRightHumerus.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    torsoToRightHumerus.setReferenceAngle(Math.toRadians(0.0));
	    torsoToRightHumerus.setMotorEnabled(false);
	    torsoToRightHumerus.setMotorSpeed(Math.toRadians(0.0));
	    torsoToRightHumerus.setMaximumMotorTorque(0.0);
	    torsoToRightHumerus.setCollisionAllowed(false);
	    world.addJoint(torsoToRightHumerus);
	    
	    // Right Humerus to Right Ulna
	    RevoluteJoint rightHumerusToRightUlna = new RevoluteJoint(rightHumerus, rightUlna, new Vector2(0.43, -0.82));
	    rightHumerusToRightUlna.setLimitEnabled(false);
	    rightHumerusToRightUlna.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    rightHumerusToRightUlna.setReferenceAngle(Math.toRadians(0.0));
	    rightHumerusToRightUlna.setMotorEnabled(false);
	    rightHumerusToRightUlna.setMotorSpeed(Math.toRadians(0.0));
	    rightHumerusToRightUlna.setMaximumMotorTorque(0.0);
	    rightHumerusToRightUlna.setCollisionAllowed(false);
	    world.addJoint(rightHumerusToRightUlna);
	    
	    // Left Humerus to Left Ulna
	    RevoluteJoint leftHumerusToLeftUlna = new RevoluteJoint(leftHumerus, leftUlna, new Vector2(-0.4, -0.81));
	    leftHumerusToLeftUlna.setLimitEnabled(false);
	    leftHumerusToLeftUlna.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    leftHumerusToLeftUlna.setReferenceAngle(Math.toRadians(0.0));
	    leftHumerusToLeftUlna.setMotorEnabled(false);
	    leftHumerusToLeftUlna.setMotorSpeed(Math.toRadians(0.0));
	    leftHumerusToLeftUlna.setMaximumMotorTorque(0.0);
	    leftHumerusToLeftUlna.setCollisionAllowed(false);
	    world.addJoint(leftHumerusToLeftUlna);
	    
	    // Torso to Right Femur
	    RevoluteJoint torsoToRightFemur = new RevoluteJoint(torso, rightFemur, new Vector2(0.16, -1.25));
	    torsoToRightFemur.setLimitEnabled(false);
	    torsoToRightFemur.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    torsoToRightFemur.setReferenceAngle(Math.toRadians(0.0));
	    torsoToRightFemur.setMotorEnabled(false);
	    torsoToRightFemur.setMotorSpeed(Math.toRadians(0.0));
	    torsoToRightFemur.setMaximumMotorTorque(0.0);
	    torsoToRightFemur.setCollisionAllowed(false);
	    world.addJoint(torsoToRightFemur);
	    
	    // Torso to Left Femur
	    RevoluteJoint torsoToLeftFemur = new RevoluteJoint(torso, leftFemur, new Vector2(-0.13, -1.25));
	    torsoToLeftFemur.setLimitEnabled(false);
	    torsoToLeftFemur.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    torsoToLeftFemur.setReferenceAngle(Math.toRadians(0.0));
	    torsoToLeftFemur.setMotorEnabled(false);
	    torsoToLeftFemur.setMotorSpeed(Math.toRadians(0.0));
	    torsoToLeftFemur.setMaximumMotorTorque(0.0);
	    torsoToLeftFemur.setCollisionAllowed(false);
	    world.addJoint(torsoToLeftFemur);
	    
	    // Right Femur to Right Tibia
	    RevoluteJoint rightFemurToRightTibia = new RevoluteJoint(rightFemur, rightTibia, new Vector2(0.17, -1.9));
	    rightFemurToRightTibia.setLimitEnabled(false);
	    rightFemurToRightTibia.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    rightFemurToRightTibia.setReferenceAngle(Math.toRadians(0.0));
	    rightFemurToRightTibia.setMotorEnabled(false);
	    rightFemurToRightTibia.setMotorSpeed(Math.toRadians(0.0));
	    rightFemurToRightTibia.setMaximumMotorTorque(0.0);
	    rightFemurToRightTibia.setCollisionAllowed(false);
	    world.addJoint(rightFemurToRightTibia);
	    
	    // Left Femur to Left Tibia
	    RevoluteJoint leftFemurToLeftTibia = new RevoluteJoint(leftFemur, leftTibia, new Vector2(-0.14, -1.9));
	    leftFemurToLeftTibia.setLimitEnabled(false);
	    leftFemurToLeftTibia.setLimits(Math.toRadians(0.0), Math.toRadians(0.0));
	    leftFemurToLeftTibia.setReferenceAngle(Math.toRadians(0.0));
	    leftFemurToLeftTibia.setMotorEnabled(false);
	    leftFemurToLeftTibia.setMotorSpeed(Math.toRadians(0.0));
	    leftFemurToLeftTibia.setMaximumMotorTorque(0.0);
	    leftFemurToLeftTibia.setCollisionAllowed(false);
	    world.addJoint(leftFemurToLeftTibia);
	}
	
	/**
	 * Start active rendering the example.
	 * <p>
	 * This should be called after the JFrame has been shown.
	 */
	public void start() {
		// initialize the last update time
		this.last = System.nanoTime();
		// don't allow AWT to paint the canvas since we are
		this.canvas.setIgnoreRepaint(true);
		// enable double buffering (the JFrame has to be
		// visible before this can be done)
		this.canvas.createBufferStrategy(2);
		// run a separate thread to do active rendering
		// because we don't want to do it on the EDT
		Thread thread = new Thread() {
			public void run() {
				// perform an infinite loop stopped
				// render as fast as possible
				while (!isStopped()) {
					gameLoop();
					// you could add a Thread.yield(); or
					// Thread.sleep(long) here to give the
					// CPU some breathing room
				}
			}
		};
		// set the game loop thread to a daemon thread so that
		// it cannot stop the JVM from exiting
		thread.setDaemon(true);
		// start the game loop
		thread.start();
	}
	
	/**
	 * The method calling the necessary methods to update
	 * the game, graphics, and poll for input.
	 */
	protected void gameLoop() {
		// get the graphics object to render to
		Graphics2D g = (Graphics2D)this.canvas.getBufferStrategy().getDrawGraphics();
		
		// before we render everything im going to flip the y axis and move the
		// origin to the center (instead of it being in the top left corner)
		AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
		AffineTransform move = AffineTransform.getTranslateInstance(400, -300);
		g.transform(yFlip);
		g.transform(move);
		
		// now (0, 0) is in the center of the screen with the positive x axis
		// pointing right and the positive y axis pointing up
		
		// render anything about the Example (will render the World objects)
		this.render(g);
		
		// dispose of the graphics object
		g.dispose();
		
		// blit/flip the buffer
		BufferStrategy strategy = this.canvas.getBufferStrategy();
		if (!strategy.contentsLost()) {
			strategy.show();
		}
		
		// Sync the display on some systems.
        // (on Linux, this fixes event queue problems)
        Toolkit.getDefaultToolkit().sync();
        
        // update the World
        
        // get the current time
        long time = System.nanoTime();
        // get the elapsed time from the last iteration
        long diff = time - this.last;
        // set the last time
        this.last = time;
    	// convert from nanoseconds to seconds
    	double elapsedTime = (double)diff / NANO_TO_BASE;
        // update the world with the elapsed time
        this.world.update(elapsedTime);
	}

	/**
	 * Renders the example.
	 * @param g the graphics object to render to
	 */
	protected void render(Graphics2D g) {
		// lets draw over everything with a white background
		g.setColor(Color.WHITE);
		g.fillRect(-400, -300, 800, 600);
		
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// lets move the view up some
		g.translate(0.0, 7.0 * SCALE);
		
		// draw all the objects in the world
		for (int i = 0; i < this.world.getBodyCount(); i++) {
			// get the object
			GameObject go = (GameObject) this.world.getBody(i);
			// draw the object
			go.render(g);
		}
	}
	
	/**
	 * Stops the example.
	 */
	public synchronized void stop() {
		this.stopped = true;
	}
	
	/**
	 * Returns true if the example is stopped.
	 * @return boolean true if stopped
	 */
	public synchronized boolean isStopped() {
		return this.stopped;
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
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
		
		// create the example JFrame
		Ragdoll window = new Ragdoll();
		
		// show it
		window.setVisible(true);
		
		// start it
		window.start();
	}
}
