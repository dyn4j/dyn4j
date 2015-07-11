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
package org.dyn4j.sandbox;

import java.awt.Color;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Wound;
import org.dyn4j.sandbox.utilities.ColorUtilities;
import org.dyn4j.sandbox.utilities.RenderUtilities;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

/**
 * Custom body class used to store colors and a name.
 * @author William Bittle
 * @version 1.0.5
 * @since 1.0.0
 */
public class SandboxBody extends Body {
	/** The body outline color */
	protected float[] outlineColor = ColorUtilities.convertColor(new Color(113, 149, 70));
	
	/** The body fill color */
	protected float[] fillColor = ColorUtilities.convertColor(new Color(196, 213, 150));
	
	/** True if the user has overridden the calculated mass */
	protected boolean massExplicit = false;
	
	/**
	 * Renders the body normally.
	 * <p>
	 * Uses the fill and outline colors to fill and outline each fixture in sequence.
	 * @param gl the OpenGL context
	 */
	public void render(GL2 gl) {
		// loop over all the fixtures
		int fSize = this.fixtures.size();
		for (int i = 0; i < fSize; i++) {
			BodyFixture bodyFixture = this.getFixture(i);
			Convex convex = bodyFixture.getShape();
			
			// render the fill
			this.setFillColor(gl);
			RenderUtilities.fillShape(gl, convex);
			// render the outline
			this.setOutlineColor(gl);
			RenderUtilities.drawShape(gl, convex, false);
		}
	}
	
	/**
	 * Renders the body using the stencil buffer to show multi-fixutre bodies
	 * as one body.
	 * @param gl the OpenGL context
	 */
	public void stencil(GL2 gl) {
		// clear the stencil
	    gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
	    
	    // disable color
	    gl.glColorMask(false, false, false, false);
	    // enable stencil testing
	    gl.glEnable(GL.GL_STENCIL_TEST);
	    
	    // fill the body into the stencil buffer
	    gl.glStencilFunc(GL.GL_ALWAYS, 1, -1);
	    gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
	    this.fill(gl);
	    
	    // draw the body into the stencil buffer only keeping the
	    // overlapping portions
	    gl.glStencilFunc(GL.GL_NOTEQUAL, 1, -1);
	    gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
	    // set the line width so we dont have to render the body 4 times
	    float lw = RenderUtilities.setLineWidth(gl, 3.0f);
	    // enable color to draw the outline
	    gl.glColorMask(true, true, true, true);
	    this.setOutlineColor(gl);
	    this.draw(gl);
	    
	    gl.glLineWidth(lw);
	    
	    // disable the stencil test
	    gl.glDisable(GL.GL_STENCIL_TEST);
	    
	    // fill the body
	    this.setFillColor(gl);
		this.fill(gl);
	}
	
	/**
	 * Outlines the body only.
	 * <p>
	 * Uses the outline color to outline each fixture in sequence.
	 * @param gl the OpenGL context
	 */
	public void draw(GL2 gl) {
		int fSize = this.fixtures.size();
		for (int i = 0; i < fSize; i++) {
			BodyFixture bodyFixture = this.getFixture(i);
			Convex convex = bodyFixture.getShape();
			
			// render
			RenderUtilities.drawShape(gl, convex, false);
		}
	}
	
	/**
	 * Fills the body only.
	 * <p>
	 * Uses the fill color to fill each fixture in sequence.
	 * @param gl the OpenGL context
	 */
	public void fill(GL2 gl) {
		int fSize = this.fixtures.size();
		for (int i = 0; i < fSize; i++) {
			BodyFixture bodyFixture = this.getFixture(i);
			Convex convex = bodyFixture.getShape();
			
			// render
			RenderUtilities.fillShape(gl, convex);
		}
	}
	
	/**
	 * Sets the OpenGL color to the outline color of this body.
	 * @param gl the OpenGL context
	 */
	public void setOutlineColor(GL2 gl) {
		// check for inactive
		if (Preferences.isBodyInActiveColorEnabled() && !this.isActive()) {
			float[] color = Preferences.getBodyInActiveColor();
			gl.glColor4f(color[0] * 0.8f, color[1] * 0.8f, color[2] * 0.8f, color[3]);
		// check for asleep
		} else if (Preferences.isBodyAsleepColorEnabled() && this.isAsleep()) {
			float[] color = Preferences.getBodyAsleepColor();
	    	gl.glColor4f(color[0] * 0.8f, color[1] * 0.8f, color[2] * 0.8f, color[3]);
	    // otherwise just set it normally
	    } else {
	    	gl.glColor4fv(this.outlineColor, 0);
	    }
	}
	
	/**
	 * Sets the OpenGL color to the fill color of this body.
	 * @param gl the OpenGL context
	 */
	public void setFillColor(GL2 gl) {
		// check for inactive
		if (Preferences.isBodyInActiveColorEnabled() && !this.isActive()) {
			float[] color = Preferences.getBodyInActiveColor();
	    	gl.glColor4fv(color, 0);
		// check for asleep
		} else if (Preferences.isBodyAsleepColorEnabled() && this.isAsleep()) {
			float[] color = Preferences.getBodyAsleepColor();
	    	gl.glColor4fv(color, 0);
    	// otherwise just set it normally
	    } else {
	    	gl.glColor4fv(this.fillColor, 0);
	    }
	}
	
	/**
	 * Renders the center of mass of this body.
	 * @param gl the OpenGL context
	 */
	public void renderCenter(GL2 gl) {
		// get the center of mass
		Vector2 c = this.mass.getCenter();
		// set the color
		gl.glColor4fv(Preferences.getBodyCenterColor(), 0);
		RenderUtilities.drawPoint(gl, c);
	}
	
	/**
	 * Renders the edge normals of any Wound Shapes on this Body.
	 * @param gl the OpenGL graphics context
	 */
	public void renderNormals(GL2 gl) {
		gl.glColor4fv(Preferences.getBodyNormalColor(), 0);
		int fSize = this.getFixtureCount();
		for (int i = 0; i < fSize; i++) {
			// get the fixture
			BodyFixture bf = this.getFixture(i);
			// get the shape
			Convex convex = bf.getShape();
			// check the type
			if (convex instanceof Wound) {
				Wound w = (Wound)convex;
				// get the data
				Vector2[] vertices = w.getVertices();
				Vector2[] normals = w.getNormals();
				int size = normals.length;
				
				// declare some place holders
				Vector2 p1, p2, n;
				Vector2 mid = new Vector2();
							
				// render all the normals
				for (int j = 0; j < size; j++) {
					// get the points and the normal
					p1 = vertices[j];
					p2 = vertices[(j + 1 == size) ? 0 : j + 1];
					n = normals[j];
					
					// find the mid point between p1 and p2
					mid.set(p2).subtract(p1).multiply(0.5).add(p1);
					
					gl.glBegin(GL.GL_LINES);
						gl.glVertex2d(mid.x, mid.y);
						gl.glVertex2d(mid.x + n.x * 0.1, mid.y + n.y * 0.1);
					gl.glEnd();
				}
			}
		}
	}
	
	/**
	 * Renders the rotation disc.
	 * @param gl the OpenGL context
	 */
	public void renderRotationDisc(GL2 gl) {
		// check if we should draw the rotation disc
		Vector2 c = this.mass.getCenter();
		// set the color
		gl.glColor4fv(Preferences.getBodyRotationDiscColor(), 0);
		// get the radius
		double r = this.getRotationDiscRadius();
		// draw the circle
		RenderUtilities.drawCircleFromCenter(gl, r, c.x, c.y, false, false);
	}
	
	/**
	 * Renders the linear and angular velocity.
	 * @param gl the OpenGL context
	 */
	public void renderVelocity(GL2 gl) {
		// set the color
		gl.glColor4fv(Preferences.getBodyVelocityColor(), 0);
		// draw the velocities
		Vector2 c = this.getWorldCenter();
		Vector2 v = this.getLinearVelocity();
		double av = this.getAngularVelocity();
		
		// draw the linear velocity for each body
		gl.glBegin(GL.GL_LINES);
			gl.glVertex2d(c.x, c.y);
			gl.glVertex2d(c.x + v.x, c.y + v.y);
		gl.glEnd();
		
		// draw an arc
		RenderUtilities.drawArc(gl, c.x, c.y, 0.125, 0, av);
	}
	
	/**
	 * Returns the outline color.
	 * @return float[]
	 */
	public float[] getOutlineColor() {
		return this.outlineColor;
	}
	
	/**
	 * Sets the outline color.
	 * <p>
	 * An array of 4 floats is expected.
	 * @param color the outline color
	 */
	public void setOutlineColor(float[] color) {
		this.outlineColor = color;
	}
	
	/**
	 * Returns the fill color.
	 * @return float[]
	 */
	public float[] getFillColor() {
		return this.fillColor;
	}
	
	/**
	 * Sets the fill color.
	 * <p>
	 * An array of 4 floats is expected.
	 * @param color the fill color
	 */
	public void setFillColor(float[] color) {
		this.fillColor = color;
	}

	/**
	 * Returns the name of the body.
	 * @return String
	 */
	public String getName() {
		return (String)this.getUserData();
	}
	
	/**
	 * Sets the name of the body.
	 * @param name the name
	 */
	public void setName(String name) {
		this.setUserData(name);
	}
	
	/**
	 * Returns true if the mass of this body has been explicitly set.
	 * @return boolean
	 */
	public boolean isMassExplicit() {
		return this.massExplicit;
	}
	
	/**
	 * Sets the mass explicit flag.
	 * @param flag true if the mass has been set explicitly
	 */
	public void setMassExplicit(boolean flag) {
		this.massExplicit = flag;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.Body#toString()
	 */
	@Override
	public String toString() {
		return (String)this.getUserData();
	}
}
