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
package org.dyn4j.sandbox.utilities;

import java.text.MessageFormat;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.Bounds;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.MotorJoint;
import org.dyn4j.dynamics.joint.MouseJoint;
import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.dynamics.joint.RopeJoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Wound;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.resources.Messages;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

/**
 * Utility class used to perform common rendering operations.
 * @author William Bittle
 * @version 1.0.4
 * @since 1.0.0
 */
public final class RenderUtilities {
	
	// for rendering circles
	
	/** The number of sides a circle should have */
	private static final int N = 20;
	
	/** The angle of each side */
	private static final double THETA = 2.0 * Math.PI / (double)N;
	
	/** The pre-computed value of cosine */
	private static final double COS = Math.cos(THETA);
	
	/** The pre-computed value of sine */
	private static final double SIN = Math.sin(THETA);
	
	// for storing the value of line width
	
	/** The array to store one float value */
	private static final float[] FLOAT_ARRAY_1 = new float[1];
	
	// Basic methods
	
	/**
	 * Saves the current OpenGL transformation matrix.
	 * @param gl the OpenGL context
	 */
	public static final void pushTransform(GL2 gl) {
		gl.glPushMatrix();
	}
	
	/**
	 * Removes the current OpenGL transformation matrix.
	 * @param gl the OpenGL context
	 */
	public static final void popTransform(GL2 gl) {
		gl.glPopMatrix();
	}
	
	/**
	 * Applies the given transform to the given OpenGL context.
	 * @param gl the OpenGL context
	 * @param t the transform
	 */
	public static final void applyTransform(GL2 gl, Transform t) {
		Vector2 tr = t.getTranslation();
		// apply the translation
		gl.glTranslated(tr.x, tr.y, 0.0);
		// apply the rotation (remember that OpenGL expects degrees not radians)
		gl.glRotated(Math.toDegrees(t.getRotation()), 0.0, 0.0, 1.0);
	}
	
	/**
	 * Returns the current line width.
	 * @param gl the OpenGL context
	 * @return float
	 */
	public static final float getLineWidth(GL2 gl) {
		gl.glGetFloatv(GL.GL_LINE_WIDTH, FLOAT_ARRAY_1, 0);
		return FLOAT_ARRAY_1[0];
	}
	
	/**
	 * Sets the current line width and returns the old value.
	 * @param gl the OpenGL context
	 * @param w the new line width
	 * @return float the old line width
	 */
	public static final float setLineWidth(GL2 gl, float w) {
		float oldLineWidth = RenderUtilities.getLineWidth(gl);
		gl.glLineWidth(w);
		return oldLineWidth;
	}
	
	/**
	 * Returns the current point size.
	 * @param gl the OpenGL context
	 * @return float
	 */
	public static final float getPointSize(GL2 gl) {
		gl.glGetFloatv(GL.GL_POINT_SIZE, FLOAT_ARRAY_1, 0);
		return FLOAT_ARRAY_1[0];
	}
	
	/**
	 * Sets the point size and returns the old value.
	 * @param gl the OpenGL context
	 * @param s the new point size
	 * @return float the old point size
	 */
	public static final float setPointSize(GL2 gl, float s) {
		float oldPointSize = RenderUtilities.getPointSize(gl);
		gl.glPointSize(s);
		return oldPointSize;
	}
	
	// Circle methods
	
	/**
	 * Draws an outline of the given circle.
	 * @param gl the OpenGL context
	 * @param c the circle
	 * @param points true if points should be drawn
	 * @param line true if a line should be drawn to show rotation
	 */
	public static final void drawCircle(GL2 gl, Circle c, boolean points, boolean line) {
		double r = c.getRadius();
		Vector2 ce = c.getCenter();
		double cx = ce.x;
		double cy = ce.y;
		RenderUtilities.drawCircleFromCenter(gl, r, cx, cy, points, line);
	}
	
	/**
	 * Draws an outline of the given circle starting from the center coordinates and using
	 * the given radius.
	 * @param gl the OpenGL context
	 * @param r the circle radius
	 * @param cx the circle center x coordinate
	 * @param cy the circle center y coordinate
	 * @param points true if points should be drawn
	 * @param line true if a line should be drawn to show rotation
	 */
	public static final void drawCircleFromCenter(GL2 gl, double r, double cx, double cy, boolean points, boolean line) {
		// pre compute sin and cos
		double c = COS;
		double s = SIN;
		double t;

		// start at 0 
		double x = r;
		double y = 0; 
	    
		gl.glBegin(GL.GL_LINE_LOOP); 
		for(int i = 0; i < N; i++) 
		{ 
			gl.glVertex2d(x + cx, y + cy);//output vertex 
	        
			//apply the rotation matrix
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;
		} 
		gl.glEnd();
		
		// check if we should draw points
		if (points) {
			x = r;
			y = 0;
			gl.glBegin(GL.GL_POINTS); 
			for(int i = 0; i < N; i++) 
			{ 
				gl.glVertex2d(x + cx, y + cy);//output vertex 
		        
				//apply the rotation matrix
				t = x;
				x = c * x - s * y;
				y = s * t + c * y;
			} 
			gl.glEnd();
		}
		
		if (line) {
			gl.glBegin(GL.GL_LINES);
				gl.glVertex2d(cx, cy);
				gl.glVertex2d(cx + r, cy);
			gl.glEnd();
		}
	}
	
	/**
	 * Draws an outline of the given circle starting from the top left to the width and height.
	 * <p>
	 * This method will use the minimum of the width and height for the diameter of the circle.
	 * <p>
	 * This method is useful for drawing a circle within a bounding box.
	 * @param gl the OpenGL context
	 * @param sx the x coordinate of the top left position
	 * @param sy the y coordinate of the top left position
	 * @param w the width
	 * @param h the height
	 * @param points true if points should be drawn
	 * @param line true if a line should be drawn to show rotation
	 */
	public static final void drawCircleFromTopLeft(GL2 gl, double sx, double sy, double w, double h, boolean points, boolean line) {
		double r = Math.min(w, h) * 0.5;
		RenderUtilities.drawCircleFromCenter(gl, r, sx + r, sy - r, points, line);
	}
	
	/**
	 * Draws an outline of the given circle starting from the top left and ending at the bottom right.
	 * <p>
	 * This method will use the minimum of the width and height for the diameter of the circle.  The width
	 * and height are computed from the start and end positions.
	 * <p>
	 * This method is useful for drawing a circle within a bounding box.
	 * @param gl the OpenGL context
	 * @param sx the x coordinate of the top left position
	 * @param sy the y coordinate of the top left position
	 * @param ex the x coordinate of the bottom right position
	 * @param ey the y coordinate of the bottom right position
	 * @param points true if points should be drawn
	 * @param line true if a line should be drawn to show rotation
	 */
	public static final void drawCircleFromStartToEnd(GL2 gl, double sx, double sy, double ex, double ey, boolean points, boolean line) {
		double w = Math.abs(sx - ex);
		double h = Math.abs(sy - ey);
		RenderUtilities.drawCircleFromTopLeft(gl, sx, sy, w, h, points, line);
	}
	
	/**
	 * Fills the given circle.
	 * @param gl the OpenGL context
	 * @param c the circle
	 */
	public static final void fillCircle(GL2 gl, Circle c) {
		double r = c.getRadius();
		Vector2 ce = c.getCenter();
		double cx = ce.x;
		double cy = ce.y;
		RenderUtilities.fillCircleFromCenter(gl, r, cx, cy);
	}
	
	/**
	 * Fills the circle from the center location using the given radius.
	 * @param gl the OpenGL context
	 * @param r the radius
	 * @param cx the x coordinate of the center
	 * @param cy the y coordinate of the center
	 */
	public static final void fillCircleFromCenter(GL2 gl, double r, double cx, double cy) {
		// pre compute sin and cos
		double c = COS;
		double s = SIN;
		double t;

		// start at 0 
		double x = r;
		double y = 0; 
	    
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex2d(cx, cy);
		for(int i = 0; i <= N; i++) 
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
	 * Fills the circle starting from the top left to the width and height.
	 * <p>
	 * This method will use the minimum of the width and height for the diameter of the circle.
	 * <p>
	 * This method is useful for filling a circle within a bounding box.
	 * @param gl the OpenGL context
	 * @param sx the x coordinate of the top left position
	 * @param sy the y coordinate of the top left position
	 * @param w the width
	 * @param h the height
	 */
	public static final void fillCircleFromTopLeft(GL2 gl, double sx, double sy, double w, double h) {
		double r = Math.min(w, h) * 0.5;
		RenderUtilities.fillCircleFromCenter(gl, r, sx + r, sy - r);
	}
	
	/**
	 * Fills the given circle starting from the top left and ending at the bottom right.
	 * <p>
	 * This method will use the minimum of the width and height for the diameter of the circle.  The width
	 * and height are computed from the start and end positions.
	 * <p>
	 * This method is useful for filling a circle within a bounding box.
	 * @param gl the OpenGL context
	 * @param sx the x coordinate of the top left position
	 * @param sy the y coordinate of the top left position
	 * @param ex the x coordinate of the bottom right position
	 * @param ey the y coordinate of the bottom right position
	 */
	public static final void fillCircleFromStartToEnd(GL2 gl, double sx, double sy, double ex, double ey) {
		double w = Math.abs(sx - ex);
		double h = Math.abs(sy - ey);
		RenderUtilities.fillCircleFromTopLeft(gl, sx, sy, w, h);
	}
	
	// Rectangle methods
	
	/**
	 * Draws the given rectangle.
	 * @param gl the OpenGL context
	 * @param r the rectangle
	 * @param points true if points should be drawn
	 */
	public static final void drawRectangle(GL2 gl, Rectangle r, boolean points) {
		RenderUtilities.drawPolygon(gl, r.getVertices(), points);
	}
	
	/**
	 * Draws an outline of a rectangle from the center using the given height and width.
	 * @param gl the OpenGL context
	 * @param cx the x coordinate of the center
	 * @param cy the y coordinate of the center
	 * @param w the width
	 * @param h the height
	 * @param points true if points should be drawn
	 */
	public static final void drawRectangleFromCenter(GL2 gl, double cx, double cy, double w, double h, boolean points) {
		double w2 = w * 0.5;
		double h2 = h * 0.5;
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex2d(cx - w2, cy + h2);
			gl.glVertex2d(cx + w2, cy + h2);
			gl.glVertex2d(cx + w2, cy - h2);
			gl.glVertex2d(cx - w2, cy - h2);
		gl.glEnd();
		
		if (points) {
			gl.glBegin(GL.GL_POINTS);
				gl.glVertex2d(cx - w2, cy + h2);
				gl.glVertex2d(cx + w2, cy + h2);
				gl.glVertex2d(cx + w2, cy - h2);
				gl.glVertex2d(cx - w2, cy - h2);
			gl.glEnd();
		}
	}
	
	/**
	 * Draws an outline of a rectangle from the top left position using the given height and width.
	 * @param gl the OpenGL context
	 * @param sx the x coordinate of the top left position
	 * @param sy the y coordinate of the top left position
	 * @param w the width
	 * @param h the height
	 * @param points true if points should be drawn
	 */
	public static final void drawRectangleFromTopLeft(GL2 gl, double sx, double sy, double w, double h, boolean points) {
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex2d(sx, sy);
			gl.glVertex2d(sx + w, sy);
			gl.glVertex2d(sx + w, sy - h);
			gl.glVertex2d(sx, sy - h);
		gl.glEnd();
		
		if (points) {
			gl.glBegin(GL.GL_POINTS);
				gl.glVertex2d(sx, sy);
				gl.glVertex2d(sx + w, sy);
				gl.glVertex2d(sx + w, sy - h);
				gl.glVertex2d(sx, sy - h);
			gl.glEnd();
		}
	}
	
	/**
	 * Draws an outline of a rectangle from the top left position to the bottom right position.
	 * @param gl the OpenGL context
	 * @param sx the x coordinate of the top left position
	 * @param sy the y coordinate of the top left position
	 * @param ex the x coordinate of the bottom right position
	 * @param ey the y coordinate of the bottom right position
	 * @param points true if points should be drawn
	 */
	public static final void drawRectangleFromStartToEnd(GL2 gl, double sx, double sy, double ex, double ey, boolean points) {
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex2d(sx, sy);
			gl.glVertex2d(ex, sy);
			gl.glVertex2d(ex, ey);
			gl.glVertex2d(sx, ey);
		gl.glEnd();
		
		if (points) {
			gl.glBegin(GL.GL_POINTS);
				gl.glVertex2d(sx, sy);
				gl.glVertex2d(ex, sy);
				gl.glVertex2d(ex, ey);
				gl.glVertex2d(sx, ey);
			gl.glEnd();
		}
	}
	
	/**
	 * Fills the given rectangle.
	 * @param gl the OpenGL context
	 * @param r the rectangle
	 */
	public static final void fillRectangle(GL2 gl, Rectangle r) {
		Vector2[] vs = r.getVertices();
		gl.glBegin(GL2.GL_QUADS);
			for (int i = 0; i < vs.length; i++) {
				Vector2 v = vs[i];
				gl.glVertex2d(v.x, v.y);
			}
		gl.glEnd();
	}
	
	/**
	 * Fills the rectangle from the center using the given height and width.
	 * @param gl the OpenGL context
	 * @param cx the x coordinate of the center
	 * @param cy the y coordinate of the center
	 * @param w the width
	 * @param h the height
	 */
	public static final void fillRectangleFromCenter(GL2 gl, double cx, double cy, double w, double h) {
		double w2 = w * 0.5;
		double h2 = h * 0.5;
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex2d(cx - w2, cy + h2);
			gl.glVertex2d(cx + w2, cy + h2);
			gl.glVertex2d(cx + w2, cy - h2);
			gl.glVertex2d(cx - w2, cy - h2);
		gl.glEnd();
	}
	
	/**
	 * Fills the rectangle from the top left position using the given height and width.
	 * @param gl the OpenGL context
	 * @param sx the x coordinate of the top left position
	 * @param sy the y coordinate of the top left position
	 * @param w the width
	 * @param h the height
	 */
	public static final void fillRectangleFromTopLeft(GL2 gl, double sx, double sy, double w, double h) {
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex2d(sx, sy);
			gl.glVertex2d(sx + w, sy);
			gl.glVertex2d(sx + w, sy - h);
			gl.glVertex2d(sx, sy - h);
		gl.glEnd();
	}
	
	/**
	 * Fills the rectangle from the top left position to the bottom right position.
	 * @param gl the OpenGL context
	 * @param sx the x coordinate of the top left position
	 * @param sy the y coordinate of the top left position
	 * @param ex the x coordinate of the bottom right position
	 * @param ey the y coordinate of the bottom right position
	 */
	public static final void fillRectangleFromStartToEnd(GL2 gl, double sx, double sy, double ex, double ey) {
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex2d(sx, sy);
			gl.glVertex2d(ex, sy);
			gl.glVertex2d(ex, ey);
			gl.glVertex2d(sx, ey);
		gl.glEnd();
	}
	
	// Polygon methods
	
	/**
	 * Draws an outline of the polygon.
	 * @param gl the OpenGL context
	 * @param p the polygon
	 * @param points true if points should be drawn
	 */
	public static final void drawPolygon(GL2 gl, Polygon p, boolean points) {
		RenderUtilities.drawPolygon(gl, p.getVertices(), points);
	}
	
	/**
	 * Draws an outline of the polygon specified by vertices.
	 * @param gl the OpenGL context
	 * @param vs the array of vertices
	 * @param points true if points should be drawn
	 */
	public static final void drawPolygon(GL2 gl, Vector2[] vs, boolean points) {
		gl.glBegin(GL.GL_LINE_LOOP);
			for (int i = 0; i < vs.length; i++) {
				Vector2 v = vs[i];
				gl.glVertex2d(v.x, v.y);
			}
		gl.glEnd();
		
		if (points) {
			gl.glBegin(GL.GL_POINTS);
				for (int i = 0; i < vs.length; i++) {
					Vector2 v = vs[i];
					gl.glVertex2d(v.x, v.y);
				}
			gl.glEnd();			
		}
	}
	
	/**
	 * Fills the polygon.
	 * @param gl the OpenGL context
	 * @param p the polygon
	 */
	public static final void fillPolygon(GL2 gl, Polygon p) {
		RenderUtilities.fillPolygon(gl, p.getVertices());
	}
	
	/**
	 * Fills the polygon specified by vertices.
	 * @param gl the OpenGL context
	 * @param vs the array of vertices
	 */
	public static final void fillPolygon(GL2 gl, Vector2... vs) {
		gl.glBegin(GL2.GL_POLYGON);
			for (int i = 0; i < vs.length; i++) {
				Vector2 v = vs[i];
				gl.glVertex2d(v.x, v.y);
			}
		gl.glEnd();
	}
	
	// Segment methods (fill methods not applicable)
	
	/**
	 * Draws the line segment.
	 * @param gl the OpenGL context
	 * @param segment the line segment
	 * @param points true if points should be drawn
	 */
	public static final void drawLineSegment(GL2 gl, Segment segment, boolean points) {
		Vector2 p1 = segment.getPoint1();
		Vector2 p2 = segment.getPoint2();
		RenderUtilities.drawLineSegment(gl, p1.x, p1.y, p2.x, p2.y, points);
	}
	
	/**
	 * Draws the line segment specified by two points.
	 * @param gl the OpenGL context
	 * @param p1 the first point
	 * @param p2 the second point
	 * @param points true if points should be drawn
	 */
	public static final void drawLineSegment(GL2 gl, Vector2 p1, Vector2 p2, boolean points) {
		RenderUtilities.drawLineSegment(gl, p1.x, p1.y, p2.x, p2.y, points);
	}
	
	/**
	 * Draws the line segment specified by two points.
	 * @param gl the OpenGL context
	 * @param p1x the x coordinate of the first point
	 * @param p1y the y coordinate of the first point
	 * @param p2x the x coordinate of the second point
	 * @param p2y the y coordinate of the second point
	 * @param points true if points should be drawn
	 */
	public static final void drawLineSegment(GL2 gl, double p1x, double p1y, double p2x, double p2y, boolean points) {
		gl.glBegin(GL.GL_LINES);
			gl.glVertex2d(p1x, p1y);
			gl.glVertex2d(p2x, p2y);
		gl.glEnd();
		
		if (points) {
			gl.glBegin(GL.GL_POINTS);
				gl.glVertex2d(p1x, p1y);
				gl.glVertex2d(p2x, p2y);
			gl.glEnd();
		}
	}
	
	// Arc methods
	
	/**
	 * Renders an arc at (cx, cy) with the given radius r using n number of lines.
	 * @param gl the OpenGL graphics context
	 * @param cx the center x coordinate
	 * @param cy the center y coordinate
	 * @param r the radius
	 * @param sa the start angle
	 * @param aa the arc angle
	 */
	public static final void drawArc(GL2 gl, double cx, double cy, double r, double sa, double aa) {
		double t;

		// start at sa 
		double x = r;
		double y = 0; 
		
		// start at the start angle
		t = x;
		x = Math.cos(sa) * x - Math.sin(sa) * y;
		y = Math.sin(sa) * t + Math.cos(sa) * y;
		
		// determine the direction given the +/- of the angular velocity
		double sign = Math.signum(aa);
		double cos = COS;
		double sin = sign >= 0 ? SIN : -SIN;
		
		gl.glBegin(GL.GL_LINE_STRIP); 
		for(int i = 0; i < N; i++) 
		{ 
			gl.glVertex2d(x + cx, y + cy);//output vertex 
	        
			//apply the rotation matrix
			t = x;
			x = cos * x - sin * y;
			y = sin * t + cos * y;
			
			if (Math.abs(cos * i) >= Math.abs(aa)) break;
		} 
		gl.glEnd();  
	}
	
	// Point methods
	
	/**
	 * Draws the given point.
	 * @param gl the OpenGL context
	 * @param p the point
	 */
	public static final void drawPoint(GL2 gl, Vector2 p) {
		RenderUtilities.drawPoint(gl, p.x, p.y);
	}
	
	/**
	 * Draws the given point.
	 * @param gl the OpenGL context
	 * @param px the x coordinate of the point
	 * @param py the y coordinate of the point
	 */
	public static final void drawPoint(GL2 gl, double px, double py) {
		gl.glBegin(GL.GL_POINTS);
			gl.glVertex2d(px, py);
		gl.glEnd();
	}
	
	// Vector methods
	
	/**
	 * Draws a vector from the given start in the direction of d with length l.
	 * @param gl the OpenGL context
	 * @param s the start point
	 * @param d the direction
	 * @param l the length
	 */
	public static final void drawVector(GL2 gl, Vector2 s, Vector2 d, double l) {
		RenderUtilities.drawVector(gl, s.x, s.y, d.x, d.y, l);
	}
	
	/**
	 * Draws a vector from the given start in the direction of (dx, dy) with length l.
	 * @param gl the OpenGL context
	 * @param sx the x coordinate of the start point
	 * @param sy the y coordinate of the start point
	 * @param dx the x value of the direction
	 * @param dy the y value of the direction
	 * @param l the length
	 */
	public static final void drawVector(GL2 gl, double sx, double sy, double dx, double dy, double l) {
		gl.glBegin(GL.GL_LINES);
			gl.glVertex2d(sx, sy);
			gl.glVertex2d(sx + dx * l, sy + dy * l);
		gl.glEnd();
	}
	
	// High-level methods
	
	/**
	 * Draws the given bounds object.
	 * @param gl the OpenGL context
	 * @param b the bounds
	 */
	public static final void drawBounds(GL2 gl, Bounds b) {
		if (b instanceof AxisAlignedBounds) {
			AxisAlignedBounds aab = (AxisAlignedBounds)b;
			double w = aab.getWidth();
			double h = aab.getHeight();
			Vector2 c = aab.getTranslation();
			
			RenderUtilities.drawRectangleFromCenter(gl, c.x, c.y, w, h, false);
		} else {
			// no rendering available
		}
	}
	
	/**
	 * Draws the given shape.
	 * @param gl the OpenGL context
	 * @param s the shape
	 * @param points true if points should be drawn
	 */
	public static final void drawShape(GL2 gl, Shape s, boolean points) {
		if (s instanceof Circle) {
			RenderUtilities.drawCircle(gl, (Circle)s, points, true);
		} else if (s instanceof Rectangle) {
			RenderUtilities.drawRectangle(gl, (Rectangle)s, points);
		} else if (s instanceof Polygon) {
			RenderUtilities.drawPolygon(gl, (Polygon)s, points);
		} else if (s instanceof Segment) {
			RenderUtilities.drawLineSegment(gl, (Segment)s, points);
		} else {
			// no rendering available
		}
	}
	
	/**
	 * Fills the given shape.
	 * @param gl the OpenGL context
	 * @param s the shape
	 */
	public static final void fillShape(GL2 gl, Shape s) {
		if (s instanceof Circle) {
			RenderUtilities.fillCircle(gl, (Circle)s);
		} else if (s instanceof Rectangle) {
			RenderUtilities.fillRectangle(gl, (Rectangle)s);
		} else if (s instanceof Polygon) {
			RenderUtilities.fillPolygon(gl, (Polygon)s);
		} else if (s instanceof Segment) {
			// segments don't need to be filled
		} else {
			// no rendering available
		}
	}
	
	/**
	 * Outlines the given shape using the given line width.
	 * @param gl the OpenGL context
	 * @param s the shape to outline
	 * @param w the line width
	 * @param color the line color
	 */
	public static final void outlineShape(GL2 gl, Shape s, float w, float[] color) {
		float lw = RenderUtilities.getLineWidth(gl);
		RenderUtilities.setLineWidth(gl, w);
		if (color != null) gl.glColor4fv(color, 0);
		RenderUtilities.drawShape(gl, s, true);
		RenderUtilities.setLineWidth(gl, lw);
	}
	
	/**
	 * Outlines each fixture of the body.
	 * <p>
	 * This method does <b>not</b> apply the body transform before rendering.
	 * @param gl the OpenGL context
	 * @param body the body to outline
	 * @param w the line width
	 * @param color the line color
	 * @param scale the pixel to meter scale factor
	 */
	public static final void outlineShapes(GL2 gl, SandboxBody body, float w, float[] color, double scale) {
		// set the line width
		float lw = RenderUtilities.setLineWidth(gl, w);
		// set the color
		if (color != null) gl.glColor4fv(color, 0);
		// compute the width expansion
		double we = w * 0.5 / scale;
		// draw all the fixtures
		int fSize = body.getFixtureCount();
		for (int i = 0; i < fSize; i++) {
			BodyFixture bf = body.getFixture(i);
			Shape s = bf.getShape();
			// check for circle
			if (s instanceof Circle) {
				Circle c = (Circle)s;
				Vector2 ct = c.getCenter();
				// fill the expanded circle
				RenderUtilities.fillCircleFromCenter(gl, c.getRadius() + we, ct.x, ct.y);
			} else {
				Wound wo = (Wound)s;
				Vector2[] vs = wo.getVertices();
				// render the lines with the line width
				gl.glBegin(GL.GL_LINE_LOOP);
				for (int j = 0; j < vs.length; j++) {
					Vector2 v = vs[j];
					gl.glVertex2d(v.x, v.y);
				}
				gl.glEnd();
				// render circles at the vertices
				for (int j = 0; j < vs.length; j++) {
					Vector2 v = vs[j];
					RenderUtilities.fillCircleFromCenter(gl, we, v.x, v.y);
				}
			}
		}
		// reset the line width
		RenderUtilities.setLineWidth(gl, lw);
	}
	
	/**
	 * Draws the given joint.
	 * @param gl the OpenGL context
	 * @param joint the joint
	 * @param invdt the inverse delta time from the last world simulation step
	 */
	public static final void drawJoint(GL2 gl, Joint joint, double invdt) {
		if (joint instanceof AngleJoint) {
			// no rendering available
		} else if (joint instanceof DistanceJoint) {
			RenderUtilities.drawDistanceJoint(gl, (DistanceJoint)joint);
		} else if (joint instanceof FrictionJoint) {
			// no rendering available
		} else if (joint instanceof MotorJoint) {
			RenderUtilities.drawMotorJoint(gl, (MotorJoint)joint);
		} else if (joint instanceof MouseJoint) {
			RenderUtilities.drawMouseJoint(gl, (MouseJoint)joint, invdt);
		} else if (joint instanceof PrismaticJoint) {
			RenderUtilities.drawPrismaticJoint(gl, (PrismaticJoint)joint);
		} else if (joint instanceof PulleyJoint) {
			RenderUtilities.drawPulleyJoint(gl, (PulleyJoint)joint);
		} else if (joint instanceof RevoluteJoint) {
			RenderUtilities.drawRevoluteJoint(gl, (RevoluteJoint)joint);
		} else if (joint instanceof RopeJoint) {
			RenderUtilities.drawRopeJoint(gl, (RopeJoint)joint);
		} else if (joint instanceof WeldJoint) {
			RenderUtilities.drawWeldJoint(gl, (WeldJoint)joint);
		} else if (joint instanceof WheelJoint) {
			RenderUtilities.drawWheelJoint(gl, (WheelJoint)joint);
		} else {
			// no rendering available
		}
	}
	
	/**
	 * Draws the given distance joint.
	 * @param gl the OpenGL context
	 * @param joint the joint
	 */
	public static final void drawDistanceJoint(GL2 gl, DistanceJoint joint) {
		// draw line from anchor point to anchor point
		// get the anchor points
		Vector2 v1 = joint.getAnchor1();
		Vector2 v2 = joint.getAnchor2();
		// set the color to be mostly transparent
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.3f);
		// check for spring distance joint
		if (joint.isSpring()) {
			// draw a spring
			final double h = 0.03;
			final double w = 0.25;
			// compute the offset from the first joint point to the start
			// of the spring loops
			double offset = h * 0.5;
			// compute the number of spring loops
			// we have to use the joint's desired distance here so that the
			// number of loops in the spring doesnt change as the simulation
			// progresses
			int loops = (int) Math.ceil((joint.getDistance() - offset * 2.0) / h);
			// get the vector between the two points
			Vector2 n = v1.to(v2);
			// normalize it to get the current distance
			double x = n.normalize();
			// get the tangent to the normal
			Vector2 t = n.getRightHandOrthogonalVector();
			// compute the distance between each loop along the normal
			double d = (x - offset * 2.0) / (loops - 1);
			// draw a line straight down using the offset
			Vector2 d1 = n.product(offset).add(v1);
			gl.glBegin(GL.GL_LINES);
				gl.glVertex2d(v1.x, v1.y);
				gl.glVertex2d(d1.x, d1.y);
				// draw the first loop (half loop)
				Vector2 ct = t.product(w * 0.5);
				Vector2 cn = n.product(d * 0.5);
				Vector2 first = ct.sum(cn).add(d1);
				gl.glVertex2d(d1.x, d1.y);
				gl.glVertex2d(first.x, first.y);
				// draw the middle loops
				Vector2 prev = first;
				for (int i = 1; i < loops - 1; i++) {
					ct = t.product(w * 0.5 * ((i + 1) % 2 == 1 ? 1.0 : -1.0));
					cn = n.product(d * (i + 0.5) + offset);
					Vector2 p2 = ct.sum(cn).add(v1);
					// draw the line
					gl.glVertex2d(prev.x, prev.y);
					gl.glVertex2d(p2.x, p2.y);
					prev = p2;
				}
				// draw the final loop (half loop)
				Vector2 d2 = n.product(-offset).add(v2);
				gl.glVertex2d(prev.x, prev.y);
				gl.glVertex2d(d2.x, d2.y);
				// draw a line straight down using the offset
				gl.glVertex2d(d2.x, d2.y);
				gl.glVertex2d(v2.x, v2.y);
			gl.glEnd();
		} else {
			// emulate a line stroke of arbitrary width without cap/join
			
			// get the tangent vector
			Vector2 t = v1.to(v2);
			t.normalize();
			t.left();
			t.multiply(0.025);
			
			gl.glBegin(GL2.GL_QUADS);
				gl.glVertex2d(v1.x - t.x, v1.y - t.y);
				gl.glVertex2d(v1.x + t.x, v1.y + t.y);
				gl.glVertex2d(v2.x + t.x, v2.y + t.y);
				gl.glVertex2d(v2.x - t.x, v2.y - t.y);
			gl.glEnd();
		}
	}
	
	/**
	 * Renders a RevoluteJoint to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param joint the joint
	 */
	public static final void drawRevoluteJoint(GL2 gl, RevoluteJoint joint) {
		Vector2 anchor = joint.getAnchor1();
		gl.glColor4f(0.8f, 0.8f, 0.8f, 1.0f);
		RenderUtilities.fillCircleFromCenter(gl, 0.025, anchor.x, anchor.y);
		gl.glColor4f(0.3f, 0.3f, 0.3f, 1.0f);
		RenderUtilities.drawCircleFromCenter(gl, 0.025, anchor.x, anchor.y, false, false);
	}
	
	/**
	 * Draws the given MotorJoint.
	 * @param gl the OpenGL graphics context
	 * @param joint the joint
	 * @since 3.1.0
	 */
	public static final void drawMotorJoint(GL2 gl, MotorJoint joint) {
		// set the color
		gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		Vector2 target = joint.getBody1().getWorldVector(joint.getLinearTarget());
		RenderUtilities.fillRectangleFromCenter(gl, target.x, target.y, 0.1, 0.1);
	}
	
	/**
	 * Renders a MouseJoint to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param joint the joint
	 * @param invdt the inverse of the delta time of the last world step
	 */
	public static final void drawMouseJoint(GL2 gl, MouseJoint joint, double invdt) {
		// set the color
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.8f);
		// draw the anchor point
		Vector2 anchor = joint.getAnchor2();
		RenderUtilities.fillRectangleFromCenter(gl, anchor.x, anchor.y, 0.05, 0.05);
		// draw the target point
		Vector2 target = joint.getTarget();
		RenderUtilities.fillRectangleFromCenter(gl, target.x, target.y, 0.05, 0.05);
		// draw a line connecting them
		// make the line color a function of stress (black to red)
		// get the inverse delta time
		double maxForce = joint.getMaximumForce();
		double force = joint.getReactionForce(invdt).getMagnitude();
		double red = force / maxForce;
		red *= 1.10;
		red = Interval.clamp(red, 0.0, 1.0);
		// set the color
		gl.glColor4f((float)red, 0.0f, 0.0f, 0.8f);
		gl.glBegin(GL.GL_LINES);
			gl.glVertex2d(anchor.x, anchor.y);
			gl.glVertex2d(target.x, target.y);
		gl.glEnd();
	}

	/**
	 * Renders a WeldJoint to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param joint the joint
	 */
	public static final void drawWeldJoint(GL2 gl, WeldJoint joint) {
		// set the color
		gl.glColor4f(0.3f, 0.3f, 0.3f, 1.0f);
		// draw an x at the anchor point
		Vector2 anchor = joint.getAnchor1();
		final double d = 0.025;
		gl.glBegin(GL.GL_LINES);
			gl.glVertex2d(anchor.x - d, anchor.y - d);
			gl.glVertex2d(anchor.x + d, anchor.y + d);
			gl.glVertex2d(anchor.x - d, anchor.y + d);
			gl.glVertex2d(anchor.x + d, anchor.y - d);
		gl.glEnd();
	}

	/**
	 * Renders a WheelJoint to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param joint the joint
	 */
	public static final void drawWheelJoint(GL2 gl, WheelJoint joint) {
		// draw an x at the anchor point
		Vector2 anchor = joint.getAnchor1();
		// draw a circle at the rotation anchor point
		gl.glColor4f(0.8f, 0.8f, 0.8f, 1.0f);
		RenderUtilities.fillCircleFromCenter(gl, 0.025, anchor.x, anchor.y);
		gl.glColor4f(0.3f, 0.3f, 0.3f, 1.0f);
		RenderUtilities.drawCircleFromCenter(gl, 0.025, anchor.x, anchor.y, false, false);
	}

	/**
	 * Renders a PrismaticJoint to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param joint the joint
	 */
	public static final void drawPrismaticJoint(GL2 gl, PrismaticJoint joint) {
		// the length scale factor
		final double lf = 0.75;
		// the "piston" width
		final double w = 0.10;
		
		double hw = w * 0.5;
		Body b1 = joint.getBody1();
		Body b2 = joint.getBody2();
		Vector2 c1 = b1.getWorldCenter();
		Vector2 c2 = b2.getWorldCenter();
		Vector2 n = c1.to(c2);
		double l = n.normalize();
		
		// emulate a line stroke of arbitrary width without cap/join
		// get the tangent vector
		Vector2 t = n.product(w * 0.25).left();
		
		// set the color to be mostly transparent
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.3f);
		// draw the inner piston
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex2d(c1.x - t.x, c1.y - t.y);
			gl.glVertex2d(c1.x + t.x, c1.y + t.y);
			gl.glVertex2d(c2.x + t.x, c2.y + t.y);
			gl.glVertex2d(c2.x - t.x, c2.y - t.y);
		gl.glEnd();
		
		// draw a line from body1's center to the anchor
		gl.glBegin(GL.GL_LINES);
			// draw two lines slightly offset from the center line
			t = n.cross(1.0);
			gl.glVertex2d(c2.x + t.x * hw, c2.y + t.y * hw);
			gl.glVertex2d(c2.x - n.x * l * lf + t.x * hw, c2.y - n.y * l * lf + t.y * hw);
			gl.glVertex2d(c2.x - t.x * hw, c2.y - t.y * hw);
			gl.glVertex2d(c2.x - n.x * l * lf - t.x * hw, c2.y - n.y * l * lf - t.y * hw);
		gl.glEnd();
	}

	/**
	 * Renders a PulleyJoint to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param joint the joint
	 * @since 2.2.0
	 */
	public static final void drawPulleyJoint(GL2 gl, PulleyJoint joint) {
		// set the color to be mostly transparent
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.3f);
		
		Vector2 p1 = joint.getAnchor1();
		Vector2 p2 = joint.getPulleyAnchor1();
		Vector2 p3 = joint.getPulleyAnchor2();
		Vector2 p4 = joint.getAnchor2();
		
		gl.glBegin(GL.GL_LINE_STRIP);
			gl.glVertex2d(p1.x, p1.y);
			gl.glVertex2d(p2.x, p2.y);
			gl.glVertex2d(p3.x, p3.y);
			gl.glVertex2d(p4.x, p4.y);
		gl.glEnd();
	}
	
	/**
	 * Renders a RopeJoint to the given graphics object.
	 * @param gl the OpenGL graphics context
	 * @param joint the joint
	 */
	public static final void drawRopeJoint(GL2 gl, RopeJoint joint) {
		Vector2 v1 = joint.getAnchor1();
		Vector2 v2 = joint.getAnchor2();
		// set the color to be mostly transparent
		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.3f);
		
		// emulate a line stroke of arbitrary width without cap/join
		// get the tangent vector
		Vector2 t = v1.to(v2);
		t.normalize();
		t.left();
		t.multiply(0.05);
		
		// save the original stroke
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex2d(v1.x - t.x, v1.y - t.y);
			gl.glVertex2d(v1.x + t.x, v1.y + t.y);
			gl.glVertex2d(v2.x + t.x, v2.y + t.y);
			gl.glVertex2d(v2.x - t.x, v2.y - t.y);
		gl.glEnd();
	}
	
	/**
	 * Formats the given vector for output.
	 * @param v the vector
	 * @return String
	 */
	public static final String formatVector2(Vector2 v) {
		return MessageFormat.format(Messages.getString("canvas.vector.format"), v.x, v.y);
	}
}
