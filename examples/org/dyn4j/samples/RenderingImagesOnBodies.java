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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

/**
 * A simple scene illustrating the mapping of images to bodies.
 * @author William Bittle
 * @version 3.2.1
 * @since 3.2.0
 */
public class RenderingImagesOnBodies extends SimulationFrame {
	/** Generated serial version id */
	private static final long serialVersionUID = -4165832122583574360L;
	
	// images

	/** The basketball image */
	private static final BufferedImage BASKETBALL = getImageSuppressExceptions("/org/dyn4j/samples/resources/Basketball.png");
	
	/** The create image */
	private static final BufferedImage CRATE = getImageSuppressExceptions("/org/dyn4j/samples/resources/Crate.png");
	
	/** Helper function to read the images from the class path */
	private static final BufferedImage getImageSuppressExceptions(String pathOnClasspath) {
		try {
			return ImageIO.read(RenderingImagesOnBodies.class.getResource(pathOnClasspath));
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * A custom body that uses an image instead.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.0
	 */
	private final class ImageBody extends SimulationBody {
		/** The image to use, if required */
		public BufferedImage image;
		
		/* (non-Javadoc)
		 * @see org.dyn4j.samples.SimulationBody#renderFixture(java.awt.Graphics2D, double, org.dyn4j.dynamics.BodyFixture, java.awt.Color)
		 */
		@Override
		protected void renderFixture(Graphics2D g, double scale, BodyFixture fixture, Color color) {
			// do we need to render an image?
			if (this.image != null) {
				// get the shape on the fixture
				Convex convex = fixture.getShape();
				// check the shape type
				if (convex instanceof Rectangle) {
					Rectangle r = (Rectangle)convex;
					Vector2 c = r.getCenter();
					double w = r.getWidth();
					double h = r.getHeight();
					g.drawImage(CRATE, 
							(int)Math.ceil((c.x - w / 2.0) * scale),
							(int)Math.ceil((c.y - h / 2.0) * scale),
							(int)Math.ceil(w * scale),
							(int)Math.ceil(h * scale),
							null);
				} else if (convex instanceof Circle) {
					// cast the shape to get the radius
					Circle c = (Circle) convex;
					double r = c.getRadius();
					Vector2 cc = c.getCenter();
					int x = (int)Math.ceil((cc.x - r) * scale);
					int y = (int)Math.ceil((cc.y - r) * scale);
					int w = (int)Math.ceil(r * 2 * scale);
						// lets us an image instead
						g.drawImage(BASKETBALL, x, y, w, w, null);
				}
			} else {
				// default rendering
				super.renderFixture(g, scale, fixture, color);
			}
		}
	}
	
	/**
	 * Default constructor.
	 */
	public RenderingImagesOnBodies() {
		super("Rendering Images on Bodies", 45.0);
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
		// create all your bodies/joints
		
		// create the floor
		SimulationBody floor = new SimulationBody();
		floor.addFixture(Geometry.createRectangle(15, 1));
		floor.setMass(MassType.INFINITE);
		// move the floor down a bit
		floor.translate(0.0, -4.0);
		this.world.addBody(floor);
		
		// create a circle
		ImageBody circle = new ImageBody();
		circle.image = BASKETBALL;
		circle.addFixture(Geometry.createCircle(0.5), 1, 0.2, 0.5);
		circle.setMass(MassType.NORMAL);
		circle.translate(2.0, 2.0);
		// test adding some force
		circle.applyForce(new Vector2(-100.0, 0.0));
		// set some linear damping to simulate rolling friction
		circle.setLinearDamping(0.05);
		this.world.addBody(circle);
		
		// try a rectangle
		ImageBody rectangle = new ImageBody();
		rectangle.image = CRATE;
		rectangle.addFixture(Geometry.createRectangle(1, 1));
		rectangle.setMass(MassType.NORMAL);
		rectangle.translate(0.0, 1.0);
		rectangle.getLinearVelocity().set(-2, 0);
		this.world.addBody(rectangle);
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		RenderingImagesOnBodies simulation = new RenderingImagesOnBodies();
		simulation.run();
	}
}
