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
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.codezealot.game.input.Input;
import org.codezealot.game.input.Keyboard;
import org.codezealot.game.input.Mouse;
import org.codezealot.game.input.Input.Hold;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.geometry.Vector2;
import org.dyn4j.game2d.geometry.hull.DivideAndConquer;
import org.dyn4j.game2d.geometry.hull.GiftWrap;
import org.dyn4j.game2d.geometry.hull.GrahamScan;
import org.dyn4j.game2d.geometry.hull.HullGenerator;
import org.dyn4j.game2d.geometry.hull.MonotoneChain;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.GLHelper;
import org.dyn4j.game2d.testbed.Test;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * Tests the generation of a convex hull given a point set.
 * @author William Bittle
 * @version 2.2.2
 * @since 2.2.0
 */
public class Hull extends Test {
	/** The convex hull of the points */
	private Vector2[] vertices;
	
	/** The current list of points added by the user */
	private List<Vector2> points;
	
	/** The list of hull generation algorithms */
	private HullGenerator[] generators = new HullGenerator[] {
		new GiftWrap(),
		new DivideAndConquer(),
		new GrahamScan(),
		new MonotoneChain()
	};
	
	/** The current hull generator being used */
	private int currentGenerator = 0;
	
	/** The radius of points drawn */
	private static final double r = 0.025;
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Hull";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests the convex hull generation algorithms.";
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
			gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
			int vSize = this.vertices.length;
			// draw the convex hull
			gl.glBegin(GL.GL_LINES);
			for (int i = 0; i < vSize; i++) {
				Vector2 p1 = this.vertices[i];
				Vector2 p2 = this.vertices[i + 1 == vSize ? 0 : i + 1];
				gl.glVertex2d(p1.x, p1.y);
				gl.glVertex2d(p2.x, p2.y);
			}
			gl.glEnd();
		}
		
		// draw the points
		gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		int pSize = this.points.size();
		for (int i = 0; i < pSize; i++) {
			Vector2 p = this.points.get(i);
			GLHelper.fillRectangle(gl, p.x, p.y, r, r);
		}
		
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		
		gl.glPushMatrix();
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glLoadIdentity();
		gl.glRasterPos2d(-this.size.width / 2.0 + 115.0, this.size.height / 2.0 - 15.0);
		GLUT glut = new GLUT();
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "(" + (this.currentGenerator + 1) + " of " + this.generators.length + ") " + this.generators[this.currentGenerator].getClass().getSimpleName());
		gl.glPopMatrix();
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
				{"Add Point", "<html>Adds a point to the point list.</html>", "<html><span style='color: blue;'>Left Mouse Button</span></html>"},
				{"Clear Points", "Clears the current list of points.", "<html><span style='color: blue;'>Right Mouse Button</span></html>"},
				{"Create Hull", "Creates a convex hull from the current point cloud.", "<html><span style='color: blue;'>h</span></html>"},
				{"Change Algorithm", "Cycles through the available algorithms.", "<html><span style='color: blue;'>1</span></html>"},
				{"Print Points", "Prints the current list of points to std out.", "<html><span style='color: blue;'>Enter</span></html>"}
		};
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#initializeInput(org.codezealot.game.input.Keyboard, org.codezealot.game.input.Mouse)
	 */
	@Override
	public void initializeInput(Keyboard keyboard, Mouse mouse) {
		super.initializeInput(keyboard, mouse);
		// the keys above are already registered by the TestBed
		
		// key to build the hull
		keyboard.add(new Input(KeyEvent.VK_H, Input.Hold.NO_HOLD));
		// key to cycle the algorithms
		keyboard.add(new Input(KeyEvent.VK_1, Input.Hold.NO_HOLD));
		// key to print the points and hull
		keyboard.add(new Input(KeyEvent.VK_ENTER,Input.Hold.NO_HOLD));
		
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
		
		// look for the h key
		if (keyboard.isPressed(KeyEvent.VK_H)) {
			// create a convex hull
			if (this.points.size() > 0) {
				// place the points into the array
				Vector2[] cloud = new Vector2[this.points.size()];
				this.points.toArray(cloud);
				// get the current hull generation algorithm
				HullGenerator hg = this.generators[this.currentGenerator];
				// generate the hull
				this.vertices = hg.generate(cloud);
			}
		}
		
		// look for the 1 key
		if (keyboard.isPressed(KeyEvent.VK_1)) {
			if (this.currentGenerator == this.generators.length - 1) {
				this.currentGenerator = 0;
			} else {
				this.currentGenerator++;
			}
		}
		
		// check for the enter key
		if (keyboard.isPressed(KeyEvent.VK_ENTER)) {
			// print the point cloud
			int pSize = this.points.size();
			System.out.print("Points[");
			for (int i = 0; i < pSize; i++) {
				System.out.print(this.points.get(i));
			}
			System.out.println("]");
			// print the hull if its been created
			if (this.vertices != null) {
				System.out.print("Hull[");
				int vSize = this.vertices.length;
				for (int i = 0; i < vSize; i++) {
					System.out.print(this.vertices[i]);
				}
				System.out.println("]");
			}
		}
		
		// look for the left mouse button
		if (mouse.isPressed(MouseEvent.BUTTON1)) {
			if (this.vertices != null) {
				// clear and start over
				this.vertices = null;
				this.points.clear();
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
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 128.0;
		// set the offset
		this.offset.zero();
	}
}
