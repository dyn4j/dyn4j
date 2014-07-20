/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.examples;

import java.awt.Dimension;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;

import com.jogamp.opengl.util.Animator;

/**
 * Class used to show a simple example of using the dyn4j project using
 * JOGL for rendering.
 * <p>
 * This class can be used as a starting point for projects.
 * @author William Bittle
 * @version 3.1.1
 * @since 3.0.0
 */
public class ExampleJOGL extends JFrame implements GLEventListener {
	/** The serial version id */
	private static final long serialVersionUID = 5663760293144882635L;
	
	/** The scale 45 pixels per meter */
	public static final double SCALE = 45.0;
	
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
		protected float[] color = new float[4];
		
		/**
		 * Default constructor.
		 */
		public GameObject() {
			// randomly generate the color
			this.color[0] = (float)Math.random() * 0.5f + 0.5f;
			this.color[1] = (float)Math.random() * 0.5f + 0.5f;
			this.color[2] = (float)Math.random() * 0.5f + 0.5f;
			this.color[3] = 1.0f;
		}
		
		/**
		 * Draws the body.
		 * <p>
		 * Only coded for polygons.
		 * @param gl the OpenGL graphics context
		 */
		public void render(GL2 gl) {
			// save the original transform
			gl.glPushMatrix();
			
			// transform the coordinate system from world coordinates to local coordinates	
			gl.glTranslated(this.transform.getTranslationX(), this.transform.getTranslationY(), 0.0);
			// rotate about the z-axis
			gl.glRotated(Math.toDegrees(this.transform.getRotation()), 0.0, 0.0, 1.0);
			
			// loop over all the body fixtures for this body
			for (BodyFixture fixture : this.fixtures) {
				// get the shape on the fixture
				Convex convex = fixture.getShape();
				// check the shape type
				if (convex instanceof Polygon) {
					// since Triangle, Rectangle, and Polygon are all of
					// type Polygon in addition to their main type
					Polygon p = (Polygon) convex;
					
					// set the color
					gl.glColor4fv(this.color, 0);
					
					// fill the shape
					gl.glBegin(GL2.GL_POLYGON);
					for (Vector2 v : p.getVertices()) {
						gl.glVertex3d(v.x, v.y, 0.0);
					}
					gl.glEnd();
					
					// set the color
					gl.glColor4f(this.color[0] * 0.8f, this.color[1] * 0.8f, this.color[2] * 0.8f, 1.0f);
					
					// draw the shape
					gl.glBegin(GL.GL_LINE_LOOP);
					for (Vector2 v : p.getVertices()) {
						gl.glVertex3d(v.x, v.y, 0.0);
					}
					gl.glEnd();
				}
				// circles and other curved shapes require a little more work, so to keep
				// this example short we only include polygon shapes; see the RenderUtilities
				// in the Sandbox application
			}
			
			// set the original transform
			gl.glPopMatrix();
		}
	}
	
	/** The canvas to draw to */
	protected GLCanvas canvas;
	
	/** The OpenGL animator */
	protected Animator animator;
	
	/** The dynamics engine */
	protected World world;
	
	/** The time stamp for the last iteration */
	protected long last;
	
	/**
	 * Default constructor for the window
	 */
	public ExampleJOGL() {
		super("JOGL Example");
		
		// setup the JFrame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// create the size of the window
		Dimension size = new Dimension(800, 600);
		
		// setup OpenGL capabilities
		GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		caps.setDoubleBuffered(true);
		caps.setHardwareAccelerated(true);
		
		// create a canvas to paint to 
		this.canvas = new GLCanvas(caps);
		this.canvas.setPreferredSize(size);
		this.canvas.setMinimumSize(size);
		this.canvas.setMaximumSize(size);
		this.canvas.setIgnoreRepaint(true);
		this.canvas.addGLEventListener(this);
		
		// add the canvas to the JFrame
		this.add(this.canvas);
		
		// make the JFrame not resizable
		// (this way I dont have to worry about resize events)
		this.setResizable(false);
		
		// size everything
		this.pack();
		
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
		
		// create all your bodies/joints
		
		// create the floor
		Rectangle floorRect = new Rectangle(15.0, 1.0);
		GameObject floor = new GameObject();
		floor.addFixture(new BodyFixture(floorRect));
		floor.setMass(Mass.Type.INFINITE);
		// move the floor down a bit
		floor.translate(0.0, -4.0);
		this.world.addBody(floor);
		
		// create a triangle object
		Triangle triShape = new Triangle(
				new Vector2(0.0, 0.5), 
				new Vector2(-0.5, -0.5), 
				new Vector2(0.5, -0.5));
		GameObject triangle = new GameObject();
		triangle.addFixture(triShape);
		triangle.setMass();
		triangle.translate(-1.0, 2.0);
		// test having a velocity
		triangle.getLinearVelocity().set(5.0, 0.0);
		this.world.addBody(triangle);
		
		// try a rectangle
		Rectangle rectShape = new Rectangle(1.0, 1.0);
		GameObject rectangle = new GameObject();
		rectangle.addFixture(rectShape);
		rectangle.setMass();
		rectangle.translate(0.0, 2.0);
		rectangle.getLinearVelocity().set(-5.0, 0.0);
		this.world.addBody(rectangle);
		
		// try a polygon with lots of vertices
		Polygon polyShape = Geometry.createUnitCirclePolygon(10, 1.0);
		GameObject polygon = new GameObject();
		polygon.addFixture(polyShape);
		polygon.setMass();
		polygon.translate(-2.5, 2.0);
		// set the angular velocity
		polygon.setAngularVelocity(Math.toRadians(-20.0));
		this.world.addBody(polygon);
		
		GameObject issTri = new GameObject();
		issTri.addFixture(Geometry.createIsoscelesTriangle(1.0, 3.0));
		issTri.setMass();
		issTri.translate(2.0, 3.0);
		this.world.addBody(issTri);
		
		GameObject equTri = new GameObject();
		equTri.addFixture(Geometry.createEquilateralTriangle(2.0));
		equTri.setMass();
		equTri.translate(3.0, 3.0);
		this.world.addBody(equTri);
		
		GameObject rightTri = new GameObject();
		rightTri.addFixture(Geometry.createRightTriangle(2.0, 1.0));
		rightTri.setMass();
		rightTri.translate(4.0, 3.0);
		this.world.addBody(rightTri);
	}
	
	/**
	 * Start active rendering the example.
	 * <p>
	 * This should be called after the JFrame has been shown.
	 */
	public void start() {
		// initialize the last update time
		this.last = System.nanoTime();
		// create an animator to animated the canvas
		Animator animator = new Animator(this.canvas);
		// run as fast as possible
		animator.setRunAsFastAsPossible(true);
		// start the animator
		animator.start();
	}
	
	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void init(GLAutoDrawable glDrawable) {
		// get the OpenGL context
		GL2 gl = glDrawable.getGL().getGL2();
		
		// set the matrix mode to projection
		gl.glMatrixMode(GL2.GL_PROJECTION);
		// initialize the matrix
		gl.glLoadIdentity();
		// set the view to a 2D view
		gl.glOrtho(-400, 400, -300, 300, 0, 1);
		
		// switch to the model view matrix
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		// initialize the matrix
		gl.glLoadIdentity();
		
		// set the clear color to white
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		// set the swap interval to as fast as possible
		gl.setSwapInterval(0);
	}
	
	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void display(GLAutoDrawable glDrawable) {
		// get the OpenGL context
		GL2 gl = glDrawable.getGL().getGL2();
		
		// clear the screen
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		// switch to the model view matrix
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		// initialize the matrix (0,0) is in the center of the window
		gl.glLoadIdentity();
		
		// render the scene
		this.render(gl);
		
		// perform other operations at the end (it really
		// doesn't matter if its done at the start or end)
		this.update();
	}
	
	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#dispose(javax.media.opengl.GLAutoDrawable)
	 */
	@Override
	public void dispose(GLAutoDrawable glDrawable) {
		// nothing to dispose from OpenGL for this example
	}
	
	/* (non-Javadoc)
	 * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable, int, int, int, int)
	 */
	@Override
	public void reshape(GLAutoDrawable glDrawable, int x, int y, int width, int height) {
		// do nothing since the window is not resizable
	}
	
	/**
	 * Updates the Example and World.
	 */
	protected void update() {
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
	 * @param gl the OpenGL context
	 */
	protected void render(GL2 gl) {
		// apply a scaling transformation
		gl.glScaled(SCALE, SCALE, SCALE);
		
		// lets move the view up some
		gl.glTranslated(0.0, -1.0, 0.0);
		
		// draw all the objects in the world
		for (int i = 0; i < this.world.getBodyCount(); i++) {
			// get the object
			GameObject go = (GameObject) this.world.getBody(i);
			// draw the object
			go.render(gl);
		}
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
		ExampleJOGL window = new ExampleJOGL();
		
		// show it
		window.setVisible(true);
		
		// start it
		window.start();
	}
}
