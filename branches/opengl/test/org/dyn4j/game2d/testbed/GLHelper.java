/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * Helper class containing static methods to perform common rendering operations in OpenGL.
 * @author William Bittle
 * @version 2.2.3
 * @since 2.2.3
 */
public final class GLHelper {
	/**
	 * Renders a circle at (cx, cy) with the given radius r using n number of lines.
	 * @param gl the OpenGL graphics context
	 * @param cx the center x coordinate
	 * @param cy the center y coordinate
	 * @param r the radius
	 * @param n the number of lines
	 */
	public static final void renderCircle(GL2 gl, double cx, double cy, double r, int n) {
		double theta = 2.0 * Math.PI / n;
		// pre compute sin and cos
		double c = Math.cos(theta);
		double s = Math.sin(theta);
		double t;

		// start at 0 
		double x = r;
		double y = 0; 
	    
		gl.glBegin(GL.GL_LINE_LOOP); 
		for(int i = 0; i < n; i++) 
		{ 
			gl.glVertex2d(x + cx, y + cy);//output vertex 
	        
			//apply the rotation matrix
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;
		} 
		gl.glEnd(); 
	}
	
	/**
	 * Fills a circle at (cx, cy) with the given radius r using n number of lines.
	 * @param gl the OpenGL graphics context
	 * @param cx the center x coordinate
	 * @param cy the center y coordinate
	 * @param r the radius
	 * @param n the number of lines
	 */
	public static final void fillCircle(GL2 gl, double cx, double cy, double r, int n) {
		double theta = 2.0 * Math.PI / n;
		// pre compute sin and cos
		double c = Math.cos(theta);
		double s = Math.sin(theta);
		double t;

		// start at 0 
		double x = r;
		double y = 0; 
	    
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex2d(cx, cy);
		for(int i = 0; i <= n; i++) 
		{ 
			gl.glVertex2d(x + cx, y + cy);//output vertex 
	        
			//apply the rotation matrix
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;
		} 
		gl.glEnd(); 
	}
	
	/**
	 * Renders an arc at (cx, cy) with the given radius r using n number of lines.
	 * @param gl the OpenGL graphics context
	 * @param cx the center x coordinate
	 * @param cy the center y coordinate
	 * @param r the radius
	 * @param sa the start angle
	 * @param aa the arc angle
	 * @param n the number of lines
	 */
	public static final void renderArc(GL2 gl, double cx, double cy, double r, double sa, double aa, int n) {
		double theta = 2.0 * Math.PI / n * Math.signum(aa);
		// pre compute sin and cos
		double c = Math.cos(theta);
		double s = Math.sin(theta);
		double t;

		// start at sa 
		double x = r;
		double y = 0; 
		
		// start at the start angle
		t = x;
		x = Math.cos(sa) * x - Math.sin(sa) * y;
		y = Math.sin(sa) * t + Math.cos(sa) * y;
		
		gl.glBegin(GL.GL_LINE_STRIP); 
		for(int i = 0; i < n; i++) 
		{ 
			gl.glVertex2d(x + cx, y + cy);//output vertex 
	        
			//apply the rotation matrix
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;
			
			if (c * i >= Math.abs(aa)) break;
		} 
		gl.glEnd();  
	}
	
	/**
	 * Fills a rectangle at (cx, cy) with the given width and height.
	 * @param gl the OpenGL graphics context
	 * @param cx the center x coordinate
	 * @param cy the center y coordinate
	 * @param w the width of the rectangle
	 * @param h the height of the rectangle
	 */
	public static final void fillRectangle(GL2 gl, double cx, double cy, double w, double h) {
		double hw = w / 2.0;
		double hh = h / 2.0;
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex2d(cx - hw, cy - hh);
			gl.glVertex2d(cx - hw, cy + hh);
			gl.glVertex2d(cx + hw, cy + hh);
			gl.glVertex2d(cx + hw, cy - hh);
		gl.glEnd();
	}
}
