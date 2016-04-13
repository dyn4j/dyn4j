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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

/**
 * A simple scene of a terrain made using the {@link Link}s to avoid
 * the internal edge collision problem.
 * @author William Bittle
 * @since 3.2.1
 * @version 3.2.0
 */
public class LinkTerrain extends SimulationFrame {
	/** The serial version id */
	private static final long serialVersionUID = -3675099977835892473L;

	private final AtomicBoolean stepPressed = new AtomicBoolean(false);
	
	/**
	 * Default constructor for the window
	 */
	public LinkTerrain() {
		super("Internal Edge - Without Links", 64.0);
		this.pause();
		
		KeyListener listener = new CustomKeyListener();
		this.addKeyListener(listener);
		this.canvas.addKeyListener(listener);
	}
	
	/**
	 * Custom key adapter to listen for key events.
	 * @author William Bittle
	 * @version 3.2.1
	 * @since 3.2.0
	 */
	private class CustomKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_SPACE:
					if (isPaused()) {
						resume();
					} else {
						pause();
					}
					break;
			}
			
		}
	}
	
	/**
	 * Creates game objects and adds them to the world.
	 */
	protected void initializeWorld() {
//		this.world.setGravity(this.world.getGravity().negate());
		
		// the terrain
		List<Link> links = getLinks(
				// normal
//				new Vector2(-6.0,  0.5),
//				new Vector2( 0.0,  0.0),
//				new Vector2( 2.0,  0.0),
//				new Vector2( 4.0,  0.2),
//				new Vector2( 4.5,  0.3),
//				new Vector2( 6.0, -0.5));
				// upside down
//				new Vector2(-6.0, -0.5),
//				new Vector2( 0.0, -0.0),
//				new Vector2( 2.0, -0.0),
//				new Vector2( 4.0, -0.2),
//				new Vector2( 4.5, -0.3),
//				new Vector2( 6.0,  0.5));
				// reverse winding
//				new Vector2( 6.0, -0.5),
//				new Vector2( 4.5,  0.3),
//				new Vector2( 4.0,  0.2),
//				new Vector2( 2.0,  0.0),
//				new Vector2( 0.0,  0.0),
//				new Vector2(-6.0,  0.5));
				// another terrain
				new Vector2(-5.0,  0.5),
	    		new Vector2(-0.0,  0.0),
	    		new Vector2( 1.0,  0.0),
	    		new Vector2( 1.5,  0.2),
	    		new Vector2( 2.5,  0.0),
	    		new Vector2( 3.5, -0.5),
	    		new Vector2( 6.0, -0.4),
	    		new Vector2( 7.0, -0.3));
		SimulationBody floor = new SimulationBody();
		for (Link link : links) {
			floor.addFixture(link);
		}
		floor.setMass(MassType.INFINITE);
		this.world.addBody(floor);
		
//		List<Link> links = getLinks(
//	    		new Vector2(-5.0,  0.5),
//	    		new Vector2(-0.0,  0.0),
//	    		new Vector2( 1.0,  0.0),
//	    		new Vector2( 1.5,  0.2),
//	    		new Vector2( 2.5,  0.0),
//	    		new Vector2( 3.5, -0.5),
//	    		new Vector2( 6.0, -0.4),
//	    		new Vector2( 7.0, -0.3));
//	    Body terrain = new SimulationBody();
//	    for (Link link : links) {
//	    	terrain.addFixture(link);
//	    }
//	    terrain.setMass(MassType.INFINITE);
//	    this.world.addBody(terrain);
		
		
		// the body
		SimulationBody slider = new SimulationBody();
		slider.addFixture(Geometry.createSquare(0.25));
		slider.setMass(MassType.NORMAL);
		slider.setLinearVelocity(6.2, 0);
		slider.translate(-5.5, 1.0);
		this.world.addBody(slider);
	}

	private static final List<Link> getLinks(Vector2... vertices) {
		List<Link> links = new ArrayList<Link>();
		if (vertices != null) {
			for (int i = 0; i < vertices.length - 1; i++) {
				Vector2 p0 = (i != 0 ? vertices[i - 1] : null);
				Vector2 p1 = vertices[i];
				Vector2 p2 = (i + 1 < vertices.length ? vertices[i + 1] : null);
				Vector2 p3 = (i + 2 < vertices.length ? vertices[i + 2] : null);
				links.add(new Link(p0, p1, p2, p3));
			}
		}
		return links;
	}
	
	@Override
	protected void render(Graphics2D g, double elapsedTime) {
		g.translate(-3.0 * this.scale, 0.0);
		
		super.render(g, elapsedTime);
	}
	
	@Override
	protected void render(Graphics2D g, double elapsedTime, SimulationBody body) {
		super.render(g, elapsedTime, body);
		List<ContactPoint> contacts = body.getContacts(false);
		for (ContactPoint c : contacts) {
			// draw the contact point
			final double r = 0.05;
			final double d = r * 2;
			Ellipse2D.Double cp = new Ellipse2D.Double((c.getPoint().x - r) * this.scale, (c.getPoint().y - r) * this.scale, d * this.scale, d * this.scale);
			g.setColor(Color.GREEN);
			g.fill(cp);
			
			// draw the contact normal
			Line2D.Double vn = new Line2D.Double(
					c.getPoint().x * this.scale, c.getPoint().y * this.scale, 
					(c.getPoint().x + c.getNormal().x * c.getDepth() * 100) * this.scale, (c.getPoint().y + c.getNormal().y * c.getDepth() * 100) * this.scale);
			g.setColor(Color.BLUE);
			g.draw(vn);
		}
	}
	
	/**
	 * Entry point for the example application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		LinkTerrain simulation = new LinkTerrain();
		simulation.run();
	}
}
