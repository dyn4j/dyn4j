/*
 * Copyright (c) 2010-2012 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.sandbox.tests;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldPoint;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.DistanceDetector;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.collision.narrowphase.Separation;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.utilities.RenderUtilities;

/**
 * Compiled test for the collision detection pipeline.
 * @author William Bittle
 * @version 1.0.4
 * @since 1.0.2
 */
public class Plotter extends CompiledSimulation {
	/** The distance detector */
	private static final DistanceDetector DISTANCE_DETECTOR = new Gjk();
	
	/** The point rendering radius */
	private static final double RADIUS = 0.05;
	
	/**
	 * Default constructor.
	 */
	public Plotter() {
		super();
		// setup the camera
		this.camera.setScale(64.0);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#initialize()
	 */
	@Override
	public void initialize() {
		this.world.setUserData("Plotter");
		this.world.setGravity(World.ZERO_GRAVITY);
		
		for (int i = 0; i < 5; i++) {
			SandboxBody body = create(i);
			this.world.addBody(body);
		}
	}
	
	/**
	 * Helper method to create the various bodies for the collision test.
	 * @param index the shape index
	 * @return SandboxBody
	 */
	private SandboxBody create(int index) {
		SandboxBody body = new SandboxBody();
		// which shape to make?
		if (index == 0) {
			body.addFixture(new Circle(0.5));
			body.translate(3.5, 0.0);
		} else if (index == 1) {
			body.addFixture(Geometry.createUnitCirclePolygon(5, 1.0));
			body.translate(0.0, 2.5);
		} else if (index == 2) {
			body.addFixture(new Rectangle(0.5, 0.5));
			body.translate(-4.0, -0.5);
		} else if (index == 3) {
			body.addFixture(new Triangle(new Vector2(0.45, -0.12), new Vector2(-0.45, 0.38), new Vector2(-0.15, -0.22)));
			body.translate(0.5, 0.5);
		} else {
			body.addFixture(new Segment(new Vector2(-0.3, 0.2), new Vector2(0.0, -0.1)));
			body.translate(-1.0, -1.5);
		}
		// set the mass to infinite
		body.setMass(Mass.Type.INFINITE);
		body.setUserData("Body" + (index + 1));
		// return the entity
		return body;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#update(double, boolean)
	 */
	@Override
	public void update(double elapsedTime, boolean stepped) {}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#render(javax.media.opengl.GL2)
	 */
	@Override
	public void render(GL2 gl) {
		// use the current world's detectors	
		NarrowphaseDetector npd = this.world.getNarrowphaseDetector();
		ManifoldSolver ms = this.world.getManifoldSolver();
		
		Separation s = new Separation();
		Penetration p = new Penetration();
		
		int bSize = this.world.getBodyCount();
		
		// loop over all the bodies and fixtures and get their
		// penetrations, manifolds, and separations
		for (int i = 0; i < bSize; i++) {
			for (int j = i + 1; j < bSize; j++) {
				Body b1 = this.world.getBody(i);
				Body b2 = this.world.getBody(j);
				
				Transform t1 = b1.getTransform();
				Transform t2 = b2.getTransform();
				
				int fSize1 = b1.getFixtureCount();
				int fSize2 = b2.getFixtureCount();
				for (int k = 0; k < fSize1; k++) {
					for (int l = 0; l < fSize2; l++) {
						Convex c1 = b1.getFixture(k).getShape();
						Convex c2 = b2.getFixture(l).getShape();

						// set the color
						gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
						
						if (npd.detect(c1, t1, c2, t2, p)) {
							Manifold m = new Manifold();
							if (ms.getManifold(p, c1, t1, c2, t2, m)) {
								// get the points
								List<ManifoldPoint> points = m.getPoints();
								Vector2 n = m.getNormal();
								// if we got a manifold lets show it
								// there are only two cases for 2D (2 points or 1 point)
								if (points.size() == 2) {
									ManifoldPoint mp1 = points.get(0);
									ManifoldPoint mp2 = points.get(1);
									Vector2 p1 = mp1.getPoint();
									Vector2 p2 = mp2.getPoint();
									
									gl.glColor4f(1.0f, 0.6f, 0.0f, 1.0f);
									RenderUtilities.fillRectangleFromCenter(gl, p1.x, p1.y, RADIUS, RADIUS);
									RenderUtilities.fillRectangleFromCenter(gl, p2.x, p2.y, RADIUS, RADIUS);
									
									Vector2 mid = p1.copy().add(p2).multiply(0.5);
									gl.glBegin(GL.GL_LINES);
										gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
										gl.glVertex2d(mid.x, mid.y);
										gl.glVertex2d(mid.x + n.x * p.getDepth(), mid.y + n.y * p.getDepth());
										
										gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
										gl.glVertex2d(p1.x, p1.y);
										gl.glVertex2d(p1.x + n.x * mp1.getDepth(), p1.y + n.y * mp1.getDepth());
										
										gl.glVertex2d(p2.x, p2.y);
										gl.glVertex2d(p2.x + n.x * mp2.getDepth(), p2.y + n.y * mp2.getDepth());
									gl.glEnd();
								} else if (points.size() == 1) {
									ManifoldPoint mp1 = points.get(0);
									Vector2 p1 = mp1.getPoint();
									
									gl.glColor4f(1.0f, 0.6f, 0.0f, 1.0f);
									RenderUtilities.fillRectangleFromCenter(gl, p1.x, p1.y, RADIUS, RADIUS);
									
									gl.glBegin(GL.GL_LINES);
										gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
										gl.glVertex2d(p1.x, p1.y);
										gl.glVertex2d(p1.x + n.x * p.getDepth(), p1.y + n.y * p.getDepth());
										
										gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
										gl.glVertex2d(p1.x, p1.y);
										gl.glVertex2d(p1.x + n.x * mp1.getDepth(), p1.y + n.y * mp1.getDepth());
									gl.glEnd();
								}
							}
						} else {
							if (DISTANCE_DETECTOR.distance(c1, t1, c2, t2, s)) {
								Vector2 p1 = s.getPoint1();
								Vector2 p2 = s.getPoint2();
								Vector2 n = s.getNormal();
								
								RenderUtilities.fillRectangleFromCenter(gl, p1.x, p1.y, RADIUS, RADIUS);
								RenderUtilities.fillRectangleFromCenter(gl, p2.x, p2.y, RADIUS, RADIUS);
								
								gl.glBegin(GL.GL_LINES);
									gl.glVertex2d(p1.x, p1.y);
									gl.glVertex2d(p1.x + n.x * s.getDistance(), p1.y + n.y * s.getDistance());
								gl.glEnd();
							}
						}
					}
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.tests.CompiledSimulation#reset()
	 */
	@Override
	public void reset() {
		// remove everything from the world
		this.world.removeAllBodiesAndJoints();
		// add it all back
		this.initialize();
	}
}
