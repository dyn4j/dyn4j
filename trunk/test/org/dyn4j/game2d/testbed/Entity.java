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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Represents a game entity.
 * @author William Bittle
 */
public class Entity extends Body {
	/** class level logger */
	private static final Logger LOGGER = Logger.getLogger(Entity.class.getName());
	
	/** Used to determine the brightness of the colors used; in the range [0, 255] */
	private static final int COLOR_BRIGHTNESS = 191;
	
	/** Fill color for frozen bodies */
	private static final Color FROZEN_COLOR = new Color(255, 255, 200, 255);
	
	/** Fill color for asleep bodies */
	private static final Color ASLEEP_COLOR = new Color(200, 200, 255, 255);
	
	/** image of a wooden box used to render on rectangles */
	private static BufferedImage r_img = null;
	
	/** image of a basketball used to render on circles */
	private static BufferedImage c_img = null;
	
	/**
	 * Static initializer to read in image files.
	 */
	static {
		try {
			// load the wood box image once
			r_img = ImageIO.read(Entity.class.getResource("/wood.jpg"));
			// load the basketball image once
			c_img = ImageIO.read(Entity.class.getResource("/bb.png"));
		} catch (IOException e) {
			// just log the exception
			LOGGER.throwing(Entity.class.getName(), "static", e);
		}
	}
	
	/** The random fill color */
	private Color color = null;
	
	/** The alpha value */
	private int alpha = 0;
	
	/**
	 * Optional constructor.
	 */
	public Entity() {
		this(255);
	}
	
	/**
	 * Full constructor.
	 * @param alpha the alpha value used for the colors
	 */
	public Entity(int alpha) {
		this.alpha = alpha;
		this.initialize();
	}
	
	/**
	 * Initializes the entity.
	 */
	private void initialize() {
		int offset = Entity.COLOR_BRIGHTNESS - (255 - this.alpha) / 2;
		int range = 255 - offset;
		// create a random (pastel) fill color
		this.color = new Color((int)(Math.random() * (range) + offset),
							   (int)(Math.random() * (range) + offset),
							   (int)(Math.random() * (range) + offset),
							   this.alpha);
	}
	
	/**
	 * Renders the body to the given graphics object using the given
	 * world to device/screen space transform.
	 * @param graphics the graphics object to render to
	 * @param scale the scale amount
	 */
	public void render(Graphics2D graphics, double scale) {
		Draw draw = Draw.getInstance();
		// get the center of mass
		Vector center = this.mass.getCenter();
		// get the transform
		Transform tx = this.transform;
		// get the world center
		Vector wCenter = tx.getTransformed(center);
		
		// draw the shapes
		for (Convex c : this.shapes) {
			// check if we should render fill color
			if (draw.drawFill()) {
				// set the color
				graphics.setColor(this.color);
				// if the body is asleep set the render color to blue
				if (this.isAsleep()) graphics.setColor(Entity.ASLEEP_COLOR);
				// if the body is frozen set the render color to yellow
				if (this.isFrozen()) graphics.setColor(Entity.FROZEN_COLOR);
				// render the fill color
				this.renderFill(graphics, c, tx, scale);
			}
			
			// check if we should render outlines
			if (draw.drawOutline()) {
				// set the color to red
				graphics.setColor(this.color.darker());
				// if the body is asleep set the render color to blue
				if (this.isAsleep()) graphics.setColor(Entity.ASLEEP_COLOR.darker());
				// if the body is frozen set the render color to yellow
				if (this.isFrozen()) graphics.setColor(Entity.FROZEN_COLOR.darker());
				// render the convex shape
				this.renderConvex(graphics, c, tx, scale);
			}
		}
		
		if (draw.drawCenter()) {
			// draw the center of mass
			graphics.setColor(Color.GREEN);
			graphics.drawOval(
					(int) Math.ceil((wCenter.x - 0.0625) * scale),
					(int) Math.ceil((wCenter.y - 0.0625) * scale),
					(int) Math.ceil(0.125 * scale),
					(int) Math.ceil(0.125 * scale));
		}
		
		if (draw.drawVelocityVectors()) {
			// draw the velocity vector
			graphics.setColor(Color.MAGENTA);
			this.renderVector(graphics, this.v, wCenter, scale);
		}
	}
	
	/**
	 * Renders any convex object.
	 * @param g the graphics object to render to
	 * @param c the convex object to render
	 * @param t the convex object's transform
	 * @param s the scale
	 */
	private void renderConvex(Graphics2D g, Convex c, Transform t, double s) {
		if (c instanceof Polygon) {
			Polygon p = (Polygon) c;
			for (int i = 0; i < p.getVertices().length; i++) {
				Vector p1 = p.getVertices()[i];
				Vector p2 = i + 1 == p.getVertices().length ? p.getVertices()[0] : p.getVertices()[i + 1];
				p1 = t.getTransformed(p1);
				p2 = t.getTransformed(p2);
				g.drawLine(
						(int) Math.ceil(p1.x * s),
						(int) Math.ceil(p1.y * s),
						(int) Math.ceil(p2.x * s),
						(int) Math.ceil(p2.y * s));
			}
		} else if (c instanceof Circle) {
			Circle cir = (Circle) c;
			Vector center = cir.getCenter();
			// transform the center into world coordinates
			center = t.getTransformed(center);
			double r = cir.getRadius() * s;
			Vector e = cir.getCenter().sum(cir.getRadius(), 0.0);
			t.transform(e);
			// draw the oval
			g.drawOval(
					(int) Math.ceil(center.x * s - r),
					(int) Math.ceil(center.y * s - r),
					(int) Math.ceil(r + r),
					(int) Math.ceil(r + r));
			// draw a line through the circle to show rotation effects
			g.drawLine(
					(int) Math.ceil(center.x * s),
					(int) Math.ceil(center.y * s),
					(int) Math.ceil(e.x * s),
					(int) Math.ceil(e.y * s));
		} else if (c instanceof Segment) {
			Segment seg = (Segment) c;
			Vector p1 = t.getTransformed(seg.getPoint1());
			Vector p2 = t.getTransformed(seg.getPoint2());
			g.drawLine(
					(int) Math.ceil(p1.x * s),
					(int) Math.ceil(p1.y * s),
					(int) Math.ceil(p2.x * s),
					(int) Math.ceil(p2.y * s));
		}
	}
	
	/**
	 * Renders a vector from a start position.
	 * @param g the graphics object to render to
	 * @param v the vector to render
	 * @param s the start position to render the vector from
	 * @param scale the scaling factor from world space to screen space
	 */
	private void renderVector(Graphics2D g, Vector v, Vector s, double scale) {
		g.drawLine(
				(int) Math.ceil(s.x * scale),
				(int) Math.ceil(s.y * scale),
				(int) Math.ceil((s.x + v.x) * scale),
				(int) Math.ceil((s.y + v.y) * scale));
	}
	
	/**
	 * Renders a fill color for the given convex object
	 * @param g the graphics object to render to
	 * @param c the convex object to render on top of
	 * @param t the transform of the convex object
	 * @param scale the world to screen space scale factor
	 */
	private void renderFill(Graphics2D g, Convex c, Transform t, double scale) {
		// get the transformed center
		Vector ce = t.getTransformed(c.getCenter());
		
		// check for arbitrary polygon
		if (c instanceof Polygon) {
			// do a color fill of the object
			java.awt.Polygon poly = new java.awt.Polygon();
			// cast to polygon
			Polygon p = (Polygon) c;
			for (int i = 0; i < p.getVertices().length; i++) {
				Vector p1 = p.getVertices()[i];
				p1 = t.getTransformed(p1);
				poly.addPoint((int) Math.ceil(p1.x * scale), (int) Math.ceil(p1.y * scale));
			}

			// fill the shape			
			g.fill(poly);
		}

		// check for circle
		if (c instanceof Circle) {
			Circle cir = (Circle) c;
			double r = cir.getRadius() * scale;
			
			g.fillOval((int) Math.ceil(ce.x * scale - r),
					   (int) Math.ceil(ce.y * scale - r),
					   (int) Math.ceil(r * 2.0),
					   (int) Math.ceil(r * 2.0));
		}
		
		// check for segment
		if (c instanceof Segment) {
			Segment seg = (Segment) c;
			Vector p1 = t.getTransformed(seg.getPoint1());
			Vector p2 = t.getTransformed(seg.getPoint2());
			g.drawLine(
					(int) Math.ceil(p1.x * scale),
					(int) Math.ceil(p1.y * scale),
					(int) Math.ceil(p2.x * scale),
					(int) Math.ceil(p2.y * scale));
		}
	}
	
	/**
	 * Renders an image for given convex object.
	 * <p>
	 * This method is never used, however, its here to help illustrate how to map an image
	 * to shapes of different types.
	 * @param g the graphics object to render to
	 * @param c the convex object to render on top of
	 * @param t the transform of the convex object
	 * @param scale the world to screen space scale factor
	 */
	@SuppressWarnings("unused")
	private void renderImage(Graphics2D g, Convex c, Transform t, double scale) {
		Vector ce = t.getTransformed(c.getCenter());
		
		// get the rotation and use an affine transform to rotate the subsequent graphics
		double rotation = t.getRotation();
		AffineTransform at = AffineTransform.getRotateInstance(rotation, ce.x * scale, ce.y * scale);
		// save the old transform
		AffineTransform old = g.getTransform();
		// apply the rotation transform
		g.transform(at);
		
		// check for rectangle
		if (c instanceof Rectangle && r_img != null) {
			Rectangle r = (Rectangle) c;
			double w = r.getWidth() * scale;
			double h = r.getHeight() * scale;
			
			// draw the image
			g.drawImage(
					r_img,
					(int) Math.ceil(ce.x * scale - w / 2.0),
					(int) Math.ceil(ce.y * scale - h / 2.0),
					(int) Math.ceil(w),
					(int) Math.ceil(h),
					null);
		}
		
		// check for circle
		if (c instanceof Circle && c_img != null) {
			Circle cir = (Circle) c;
			double r = cir.getRadius() * scale;

			// draw the image
			g.drawImage(
					c_img,
					(int) Math.ceil(ce.x * scale - r),
					(int) Math.ceil(ce.y * scale - r),
					(int) Math.ceil(r * 2.0),
					(int) Math.ceil(r * 2.0),
					null);
		}
		
		// restore the old transform
		g.setTransform(old);
	}
}
