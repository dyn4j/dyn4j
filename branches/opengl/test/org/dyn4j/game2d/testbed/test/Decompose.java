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
package org.dyn4j.game2d.testbed.test;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.codezealot.game.input.Input;
import org.codezealot.game.input.Keyboard;
import org.codezealot.game.input.Mouse;
import org.codezealot.game.input.Input.Hold;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Vector2;
import org.dyn4j.game2d.geometry.decompose.Bayazit;
import org.dyn4j.game2d.geometry.decompose.Decomposer;
import org.dyn4j.game2d.geometry.decompose.EarClipping;
import org.dyn4j.game2d.geometry.decompose.SweepLine;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.GLHelper;
import org.dyn4j.game2d.testbed.Test;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Tests the decomposition of a simple polygon without holes.
 * @author William Bittle
 * @version 2.2.2
 * @since 2.2.0
 */
public class Decompose extends Test {
	/** The elapsed time since the last update */
	private double elapsedTime;
	
	/** The list of decomposition algorithms */
	private Decomposer[] algorithms = new Decomposer[] {
		new EarClipping(),
		new SweepLine(),
		new Bayazit()
	};
	
	/** The current algorithm's index */
	private int currentAlgorithm = 0;
	
	/** The list of premade polygons */
	private Vector2[][] polygons = new Vector2[4][];
	
	/** The current premade polygon in use */
	private int currentPolgyon = 0;
	
	/** The first simple polygon to decompose */
	private Vector2[] vertices;
	
	/** The current list of points added by the user */
	private List<Vector2> points;
	
	/** The first polygon decomposition result */
	private List<Convex> triangles;
	
	/** The time interval between showing the decomposition */
	private double interval = 0.10;
	
	/** The index of the shape to render to */
	private int toIndex = 0;
	
	/** Flag indicating the animation is complete */
	private boolean done = false;
	
	/** The mouse to show the current position */
	private Mouse mouse;
	
	/** The radius of points drawn */
	private static final double r = 0.025;
	
	/** True if an error occurred in tesselation */
	private boolean error = false;
	
	/**
	 * Default constructor.
	 */
	public Decompose() {
		for (int i = 0; i < 4; i++) {
			// load the polygon dat files
			InputStream stream = this.getClass().getResourceAsStream("/polygon" + (i + 1) + ".dat");
			this.polygons[i] = this.load(stream);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Decompose";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests the decomposition methods.  Files loaded via the 'f' key should" +
			   "be normal text files with the first line containing the number of points" +
			   "in the polygon.  The remaining lines should be a listing of the x and y" +
		       "values separated by a space.  For example:\n\n5\n1.0 1.0\n0.5 1.2\n-0.5 2.0" +
		       "\n-0.7 -0.1\n0.0 -0.3\n\n  The # character can be used for comments to describe the file" +
		       "and are ignored when the file is read.  The time taken to show the decomposition is not respective" +
		       "of the alorithms complexity.  The animation is used to verify that decomposition is" +
		       "correct.";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#initialize()
	 */
	@Override
	public void initialize() {
		// call the super method
		super.initialize();
		
		// set the camera position and zoom
		this.home();
		
		// create the world
		this.world = new World();
		
		// setup the contact counter
		ContactCounter cc = new ContactCounter();
		this.world.setContactListener(cc);
		this.world.setStepListener(cc);
		
		// turn off gravity
		this.world.setGravity(new Vector2());
		
		// setup the bodies in the world
		this.setup();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#setup()
	 */
	@Override
	protected void setup() {
		this.points = new ArrayList<Vector2>();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#renderBefore(javax.media.opengl.GL2)
	 */
	@Override
	protected void renderBefore(GL2 gl) {
		// render the axes
		this.renderAxes(gl, new float[] { 0.3f, 0.3f, 0.3f, 1.0f }, 
				1.0, 0.25, new float[] { 0.3f, 0.3f, 0.3f, 1.0f }, 
				0.1, 0.125, new float[] { 0.5f, 0.5f, 0.5f, 1.0f });
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#renderAfter(javax.media.opengl.GL2)
	 */
	@Override
	protected void renderAfter(GL2 gl) {
		if (this.vertices != null) {
			int size = this.vertices.length;
			
			// render the total polygon
			gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
			gl.glBegin(GL.GL_LINES);
			for (int i = 0; i < size; i++) {
				Vector2 p1 = this.vertices[i];
				Vector2 p2 = this.vertices[i + 1 == size ? 0 : i + 1];
				gl.glVertex2d(p1.x, p1.y);
				gl.glVertex2d(p2.x, p2.y);
			}
			
			// render the convex decomposition depending on the time
			gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
			for (int i = 0; i < this.toIndex; i++) {
				Convex convex = this.triangles.get(i);
				if (convex instanceof Polygon) {
					Polygon poly = (Polygon) convex;
					Vector2[] vertices = poly.getVertices();
					int vSize = vertices.length;
					for (int j = 0; j < vSize; j++) {
						Vector2 p1 = vertices[j];
						Vector2 p2 = vertices[(j + 1) == vSize ? 0 : j + 1];
						gl.glVertex2d(p1.x, p1.y);
						gl.glVertex2d(p2.x, p2.y);
					}
				}
			}
			gl.glEnd();
			
			// always render the points
			gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
			for (int i = 0; i < size; i++) {
				Vector2 p = this.vertices[i];
				GLHelper.fillRectangle(gl, p.x, p.y, r, r);
			}
		} else {
			int size = this.points.size();
			if (this.error) {
				gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
			} else {
				gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
			}
			
			// get the mouse location
			Point point = this.mouse.getRelativeLocation();
			// convert to world coordinates
			Vector2 v = this.screenToWorld(point.x, point.y);
			
			// only render lines if there is more than one point
			gl.glBegin(GL.GL_LINES);
			if (size > 1) {
				// draw the current list of points
				for (int i = 0; i < size - 1; i++) {
					Vector2 p1 = this.points.get(i);
					Vector2 p2 = this.points.get(i + 1);
					gl.glVertex2d(p1.x, p1.y);
					gl.glVertex2d(p2.x, p2.y);
				}
			}
			gl.glEnd();
			
			// always render the points
			for (int i = 0; i < size; i++) {
				Vector2 p = this.points.get(i);
				GLHelper.fillRectangle(gl, p.x, p.y, r, r);
			}
			
			if (size > 0 && !this.error) {
				// draw a line from the last point to the current mouse point
				Vector2 l = this.points.get(size - 1);
				gl.glBegin(GL.GL_LINES);
					gl.glVertex2d(l.x, l.y);
					gl.glVertex2d(v.x, v.y);
				gl.glEnd();
			}
			
			if (!this.error) {
				// draw the mouse point
				GLHelper.fillRectangle(gl, v.x, v.y, r, r);
			}
		}
		
		// get the number of convexes created
		int n = 0;
		if (this.triangles != null) {
			n = this.triangles.size();
		}
		
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		
		gl.glPushMatrix();
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glLoadIdentity();
		gl.glRasterPos2d(-this.size.width / 2.0 + 115.0, this.size.height / 2.0 - 15.0);
		GLUT glut = new GLUT();
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "(" + (this.currentAlgorithm + 1) + " of " + this.algorithms.length + ") " + this.algorithms[this.currentAlgorithm].getClass().getSimpleName() + " : " + n);
		gl.glPopMatrix();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#update(double)
	 */
	@Override
	public void update(double dt) {
		// call the super method
		super.update(dt);
		// only update when the user wants the shape tesselated
		if (this.vertices != null && !this.done) {
			// compute the number of convex shapes to draw
			this.elapsedTime += dt;
			if (this.elapsedTime >= interval) {
				if ((this.toIndex + 1) > this.triangles.size()) {
					this.done = true;
				} else {
					this.toIndex++;
				}
				this.elapsedTime = this.elapsedTime - interval;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#update(int)
	 */
	@Override
	public void update(int steps) {
		// update by the interval
		this.update(this.interval);
	}
	
	/**
	 * Renders the x and y axis with minor and major ticks.
	 * @param gl the OpenGL graphics context
	 * @param lineColor the color of the axes; RGBA
	 * @param majorTickScale the major tick scale in meters
	 * @param majorTickWidth the major tick width in pixels
	 * @param majorTickColor the major tick color; RGBA
	 * @param minorTickScale the minor tick scale in meters
	 * @param minorTickWidth the minor tick width in pixels
	 * @param minorTickColor the minor tick color; RGBA
	 */
	protected void renderAxes(GL2 gl, float[] lineColor,
			double majorTickScale, double majorTickWidth, float[] majorTickColor,
			double minorTickScale, double minorTickWidth, float[] minorTickColor) {
		// set the line color
		gl.glColor4fv(lineColor, 0);
		
		// get the current width and height
		double width = this.size.width;
		double height = this.size.height;
		
		// render the y axis
		gl.glBegin(GL.GL_LINES);
			gl.glVertex2d(0.0,  height / 2.0 - this.offset.y);
			gl.glVertex2d(0.0, -height / 2.0 + this.offset.y);
			
			gl.glVertex2d( width / 2.0 - this.offset.x, 0.0);
			gl.glVertex2d(-width / 2.0 + this.offset.x, 0.0);
		gl.glEnd();
		
		// compute the major tick offset
		double mao = majorTickWidth / 2.0;
		// compute the minor tick offset
		double mio = minorTickWidth / 2.0;
		
		// render the y tick marks
		// compute the number of major ticks on the y axis
		int yMajorTicks= (int) Math.ceil(height / 2.0 / majorTickScale) + 1;
		// compute the y axis offset
		int yoffset = -(int) Math.floor(this.offset.y / majorTickScale);
		
		gl.glBegin(GL.GL_LINES);
		for (int i = (-yMajorTicks + yoffset); i < (yMajorTicks + yoffset); i++) {
			// set the color
			gl.glColor4fv(majorTickColor, 0);
			// compute the major tick y
			double yma = majorTickScale * i;
			// skip drawing the major tick at zero
			
			if (i != 0) {
				// draw the +y ticks
				gl.glVertex2d(-mao, yma);
				gl.glVertex2d( mao, yma);
			}
			
			// render the minor y tick marks
			// set the color
			gl.glColor4fv(minorTickColor, 0);
			// compute the number of minor ticks
			int minorTicks = (int) Math.ceil(majorTickScale / minorTickScale);
			for (int j = 1; j < minorTicks; j++) {
				// compute the major tick y
				double ymi = majorTickScale * i - minorTickScale * j;
				// draw the +y ticks
				gl.glVertex2d(-mio, ymi);
				gl.glVertex2d( mio, ymi);
			}
		}
		
		// render the x tick marks
		// compute the number of major ticks on the x axis
		int xMajorTicks= (int) Math.ceil(width / 2.0 / majorTickScale) + 1;
		// compute the x axis offset
		int xoffset = -(int) Math.floor(this.offset.x / majorTickScale);
		for (int i = (-xMajorTicks + xoffset); i < (xMajorTicks + xoffset); i++) {
			// set the color
			gl.glColor4fv(majorTickColor, 0);
			// compute the major tick x
			double xma = majorTickScale * i;
			// skip drawing the major tick at zero
			if (i != 0) {
				// draw the major ticks
				gl.glVertex2d(xma,  mao);
				gl.glVertex2d(xma, -mao);
			}
			
			// render the minor x tick marks
			// set the color
			gl.glColor4fv(minorTickColor, 0);
			// compute the number of minor ticks
			int minorTicks = (int) Math.ceil(majorTickScale / minorTickScale);
			for (int j = 1; j < minorTicks; j++) {
				// compute the major tick x
				double xmi = majorTickScale * i - minorTickScale * j;
				// draw the minor ticks
				gl.glVertex2d(xmi,  mio);
				gl.glVertex2d(xmi, -mio);
			}
		}
		gl.glEnd();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#getControls()
	 */
	@Override
	public String[][] getControls() {
		return new String[][] {
				{"Add Point", "<html>Adds a point to the point list.  The list should form a<br />non-intersecting simple polygon.</html>", "<html><span style='color: blue;'>Left Mouse Button</span></html>"},
				{"Clear Points", "Clears the current list of points.", "<html><span style='color: blue;'>Right Mouse Button</span></html>"},
				{"Decompose Polygon", "<html>Closes the polygon by attaching the first and last<br />points, then decomposes the polygon</html>.", "<html><span style='color: blue;'>d</span></html>"},
				{"Change Algorithm", "<html>Cycles through the available decomposition algorithms.</html>.", "<html><span style='color: blue;'>1</span></html>"},
				{"Change Polygon", "<html>Cycles through the sample simple polygons.</html>.", "<html><span style='color: blue;'>2</span></html>"},
				{"Load Polygon", "<html>Loads a text file containing a formatted listing of<br />points.  See test description.</html>.", "<html><span style='color: blue;'>f</span></html>"},
				{"Print Points", "<html>Prints the current list of points to std out.</html>.", "<html><span style='color: blue;'>Enter</span></html>"},
				{"Generate Code", "<html>Generates code for the decomposed polygon.</html>.", "<html><span style='color: blue;'>g</span></html>"}
		};
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#initializeInput(org.codezealot.game.input.Keyboard, org.codezealot.game.input.Mouse)
	 */
	@Override
	public void initializeInput(Keyboard keyboard, Mouse mouse) {
		super.initializeInput(keyboard, mouse);
		
		keyboard.add(new Input(KeyEvent.VK_1, Input.Hold.NO_HOLD));
		keyboard.add(new Input(KeyEvent.VK_2, Input.Hold.NO_HOLD));
		keyboard.add(new Input(KeyEvent.VK_D, Input.Hold.NO_HOLD));
		keyboard.add(new Input(KeyEvent.VK_F, Input.Hold.NO_HOLD));
		keyboard.add(new Input(KeyEvent.VK_G, Input.Hold.NO_HOLD));
		// key to print the points
		keyboard.add(new Input(KeyEvent.VK_ENTER,Input.Hold.NO_HOLD));
		
		// we need to store the mouse so we know its location
		this.mouse = mouse;
		// we also need to override the TestBed's registration of the left mouse button
		// so that we don't allow holding of the button
		mouse.remove(MouseEvent.BUTTON1);
		mouse.add(new Input(MouseEvent.BUTTON1, Hold.NO_HOLD));
		mouse.remove(MouseEvent.BUTTON3);
		mouse.add(new Input(MouseEvent.BUTTON3, Hold.NO_HOLD));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#poll(org.codezealot.game.input.Keyboard, org.codezealot.game.input.Mouse)
	 */
	@Override
	public void poll(Keyboard keyboard, Mouse mouse) {
		super.poll(keyboard, mouse);
		
		// look for the 1 key
		if (keyboard.isPressed(KeyEvent.VK_1)) {
			// cycle the current algorithm
			if (this.currentAlgorithm + 1 == this.algorithms.length) {
				this.currentAlgorithm = 0;
			} else {
				this.currentAlgorithm++;
			}
			this.elapsedTime = 0;
			this.toIndex = 0;
			this.vertices = null;
			this.triangles = null;
			this.error = false;
			this.done = false;
		}
		
		// look for the t key
		if (keyboard.isPressed(KeyEvent.VK_D)) {
			// only tesselate polys with 4 or more vertices
			if (this.points.size() > 3) {
				// place the points into the array
				this.vertices = new Vector2[this.points.size()];
				this.points.toArray(this.vertices);
				// perform the tesselation
				Decomposer d = this.algorithms[this.currentAlgorithm];
				try {
					this.triangles = d.decompose(this.vertices);
					this.elapsedTime = 0;
					this.toIndex = 0;
					this.error = false;
					this.done = false;
				} catch (Exception e) {
					// if we get an exception color the thing red
					// and flag that its in error so that the next
					// click of the mouse clears the polygon points
					JOptionPane.showMessageDialog(null, "An error occurred durring the decomposition of the given polygon.\nExamine the Console for details.");
					this.error = true;
					this.vertices = null;
					this.triangles = null;
					// print the error to the console
					e.printStackTrace();
				}
			}
		}
		
		// look for the left mouse button
		if (mouse.isPressed(MouseEvent.BUTTON1)) {
			if (this.vertices != null || this.error) {
				// stop the tesselation animation and start a new poly
				this.vertices = null;
				this.points.clear();
				this.elapsedTime = 0;
				this.toIndex = 0;
				this.triangles = null;
				this.error = false;
				this.done = false;
			} else {
				// add the point to the list of points
				Point p = mouse.getRelativeLocation();
				// convert to world coordinates
				Vector2 v = this.screenToWorld(p.x, p.y);
				this.points.add(v);
			}
		}
		
		// look for the right mouse button
		if (mouse.isPressed(MouseEvent.BUTTON3)) {
			this.vertices = null;
			this.points.clear();
			this.elapsedTime = 0;
			this.toIndex = 0;
			this.triangles = null;
			this.error = false;
			this.done = false;
		}
		
		if (keyboard.isPressed(KeyEvent.VK_F)) {
			try{
				// show the open file dialog
				JFileChooser fileChooser = new JFileChooser();
				// the default is one item, files only
				int returnValue = fileChooser.showOpenDialog(null);
				
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					// load up the file
					Vector2[] points = this.load(fileChooser.getSelectedFile());
					// check if the points array is null
					if (points != null) {
						// the file was loaded successfully
						// clear the current point list
						this.points.clear();
						// add all the points to the list
						for (int i = 0; i < points.length; i++) {
							Vector2 p = points[i];
							this.points.add(p);
						}
						this.vertices = null;
						this.elapsedTime = 0;
						this.toIndex = 0;
						this.triangles = null;
						this.error = false;
						this.done = false;
					} else {
						JOptionPane.showMessageDialog(null, "An error occurred loading the selected file.  Please check the format.\nExamine the Console for details.");
					}
				} else if (returnValue == JFileChooser.ERROR_OPTION) {
					JOptionPane.showMessageDialog(null, "An unexpected error occurred when reading the file.  Please try again.\nExamine the Console for details.");
				}
			} catch (Exception e) {
				// an error occurred
				JOptionPane.showMessageDialog(null, "An error occurred when attempting to access the local file system.  Please check the security permissions.");
			}
		}
		
		// check for the enter key
		if (keyboard.isPressed(KeyEvent.VK_ENTER)) {
			// print the points
			int pSize = this.points.size();
			System.out.print("Points[");
			for (int i = 0; i < pSize; i++) {
				System.out.print(this.points.get(i));
			}
			System.out.println("]");
		}
		
		// check for the 2 key
		if (keyboard.isPressed(KeyEvent.VK_2)) {
			// get the polygon requested
			Vector2[] points = this.polygons[this.currentPolgyon++];
			// increment the current polygon
			if (this.currentPolgyon == 4) {
				this.currentPolgyon = 0;
			}
			// make sure its not null
			if (points != null) {
				// the file was loaded successfully
				// clear the current point list
				this.points.clear();
				// add all the points to the list
				for (int i = 0; i < points.length; i++) {
					Vector2 p = points[i];
					this.points.add(p);
				}
				this.vertices = null;
				this.elapsedTime = 0;
				this.toIndex = 0;
				this.triangles = null;
				this.error = false;
				this.done = false;
			}
		}
		
		// check for the g key
		if (keyboard.isPressed(KeyEvent.VK_G)) {
			// output the current tesselation in code
			if (this.triangles != null && this.triangles.size() > 0) {
				System.out.println("List<Convex> polygons = new ArrayList<Convex>();");
				int i = 0;
				for (Convex c : this.triangles) {
					Polygon p = (Polygon) c;
					System.out.print("Polygon p" + i + " = new Polygon(new Vector2[] {");
					int j = 0;
					for (Vector2 v : p.getVertices()) {
						if (j == 0) {
							System.out.print("new Vector2(" + v.x + ", " + v.y + ")");
						} else {
							System.out.print(", new Vector2(" + v.x + ", " + v.y + ")");
						}
						j++;
					}
					System.out.println("});");
					System.out.println("polygons.add(p" + i + ");");
					i++;
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 64.0;
		// set the offset
		this.offset.zero();
	}
	
	/**
	 * Loads the given resource from the file system and attempts to
	 * interpret the contents.
	 * <p>
	 * If any exception occurs, null is returned.
	 * @param file the file to load
	 * @return {@link Vector2}[] the points in the file
	 */
	private Vector2[] load(File file) {
		if (file == null) return null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			return parse(br);
		} catch (FileNotFoundException e) {
			// just show the error on the console
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Loads the given resource from the file system and attempts to
	 * interpret the contents.
	 * <p>
	 * If any exception occurs, null is returned.
	 * @param stream the stream to load
	 * @return {@link Vector2}[] the points in the file
	 */
	private Vector2[] load(InputStream stream) {
		if (stream == null) return null;
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		return parse(br);
	}
	
	/**
	 * Parses the contents of the buffered reader.
	 * <p>
	 * If any exception occurs, null is returned.
	 * @param reader the buffered reader to read from
	 * @return {@link Vector2}[] the points
	 */
	private Vector2[] parse(BufferedReader reader) {
		String line;
		int i = 0;
		Vector2[] points = null;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				if (line.startsWith("#")) continue;
				if (i == 0) {
					// the first line contains the number of vertices
					int size = Integer.parseInt(line.trim());
					points = new Vector2[size];
				} else {
					// otherwise its a line containing a point
					String[] xy = line.split("\\s");
					double x = Double.parseDouble(xy[0].trim());
					double y = Double.parseDouble(xy[1].trim());
					Vector2 p = new Vector2(x, y);
					points[i - 1] = p;
				}
				i++;
			}
			
			return points;
		} catch (IOException e) {
			// just show the error on the console
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// just show the error on the console
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			// just show the error on the console
			e.printStackTrace();
		}
		return null;
	}
}
