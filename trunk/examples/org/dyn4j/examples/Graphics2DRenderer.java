/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.HalfEllipse;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Slice;
import org.dyn4j.geometry.Vector2;

/**
 * Graphics2D renderer for dyn4j shape types.
 * @author William Bittle
 * @version 3.1.5
 * @since 3.1.5
 */
public final class Graphics2DRenderer {
	/**
	 * Renders the given shape to the given graphics context using the given scale and color.
	 * @param g the graphics context
	 * @param shape the shape to render
	 * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
	 * @param color the color
	 */
	public static final void render(Graphics2D g, Shape shape, double scale, Color color) {
		// no-op
		if (shape == null) return;
		
		if (shape instanceof Circle) {
			Graphics2DRenderer.render(g, (Circle)shape, scale, color);
		} else if (shape instanceof Polygon) {
			Graphics2DRenderer.render(g, (Polygon)shape, scale, color);
		} else if (shape instanceof Segment) {
			Graphics2DRenderer.render(g, (Segment)shape, scale, color);
		} else if (shape instanceof Capsule) {
			Graphics2DRenderer.render(g, (Capsule)shape, scale, color);
		} else if (shape instanceof Ellipse) {
			Graphics2DRenderer.render(g, (Ellipse)shape, scale, color);
		} else if (shape instanceof Slice) {
			Graphics2DRenderer.render(g, (Slice)shape, scale, color);
		} else if (shape instanceof HalfEllipse) {
			Graphics2DRenderer.render(g, (HalfEllipse)shape, scale, color);
		} else {
			// unknown shape
		}
	}
	
	/**
	 * Renders the given {@link Circle} to the given graphics context using the given scale and color.
	 * @param g the graphics context
	 * @param circle the circle to render
	 * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
	 * @param color the color
	 */
	public static final void render(Graphics2D g, Circle circle, double scale, Color color) {
		double radius = circle.getRadius();
		Vector2 center = circle.getCenter();
		
		double radius2 = 2.0 * radius;
		Ellipse2D.Double c = new Ellipse2D.Double(
			(center.x - radius) * scale,
			(center.y - radius) * scale,
			radius2 * scale,
			radius2 * scale);
		
		// fill the shape
		g.setColor(color);
		g.fill(c);
		// draw the outline
		g.setColor(getOutlineColor(color));
		g.draw(c);
		
		// draw a line so that rotation is visible
		Line2D.Double l = new Line2D.Double(
				center.x * scale,
				center.y * scale,
				(center.x + radius) * scale,
				center.y * scale);
		g.draw(l);
	}
	
	/**
	 * Renders the given {@link Polygon} to the given graphics context using the given scale and color.
	 * @param g the graphics context
	 * @param polygon the polygon to render
	 * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
	 * @param color the color
	 */
	public static final void render(Graphics2D g, Polygon polygon, double scale, Color color) {
		Vector2[] vertices = polygon.getVertices();
		int l = vertices.length;
		
		// create the awt polygon
		Path2D.Double p = new Path2D.Double();
		p.moveTo(vertices[0].x * scale, vertices[0].y * scale);
		for (int i = 1; i < l; i++) {
			p.lineTo(vertices[i].x * scale, vertices[i].y * scale);
		}
		p.closePath();
		
		// fill the shape
		g.setColor(color);
		g.fill(p);
		// draw the outline
		g.setColor(getOutlineColor(color));
		g.draw(p);
	}
	
	/**
	 * Renders the given {@link Segment} to the given graphics context using the given scale and color.
	 * @param g the graphics context
	 * @param segment the segment to render
	 * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
	 * @param color the color
	 */
	public static final void render(Graphics2D g, Segment segment, double scale, Color color) {
		Vector2[] vertices = segment.getVertices();
		
		Line2D.Double l = new Line2D.Double(
			vertices[0].x * scale,
			vertices[0].y * scale,
			vertices[1].x * scale,
			vertices[1].y * scale);
		
		// draw the outline
		g.setColor(getOutlineColor(color));
		g.draw(l);
	}
	
	/**
	 * Renders the given {@link Capsule} to the given graphics context using the given scale and color.
	 * @param g the graphics context
	 * @param capsule the capsule to render
	 * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
	 * @param color the color
	 */
	public static final void render(Graphics2D g, Capsule capsule, double scale, Color color) {
		// get the local rotation and translation
		double rotation = capsule.getRotation();
		Vector2 center = capsule.getCenter();
		
		// save the old transform
		AffineTransform oTransform = g.getTransform();
		// translate and rotate
		g.translate(center.x * scale, center.y * scale);
		g.rotate(rotation);
		
		double width = capsule.getLength();
		double radius = capsule.getCapRadius();
		double radius2 = radius * 2.0;
		
		Arc2D.Double arcL = new Arc2D.Double(
				-(width * 0.5) * scale,
				-radius * scale,
				radius2 * scale,
				radius2 * scale,
				90.0,
				180.0,
				Arc2D.OPEN);
		Arc2D.Double arcR = new Arc2D.Double(
				(width * 0.5 - radius2) * scale,
				-radius * scale,
				radius2 * scale,
				radius2 * scale,
				-90.0,
				180.0,
				Arc2D.OPEN);
		
		// connect the shapes
		Path2D.Double path = new Path2D.Double();
		path.append(arcL, true);
		path.append(new Line2D.Double(arcL.getEndPoint(), arcR.getStartPoint()), true);
		path.append(arcR, true);
		path.append(new Line2D.Double(arcR.getEndPoint(), arcL.getStartPoint()), true);
		
		// set the color
		g.setColor(color);
		// fill the shape
		g.fill(path);
		// set the color
		g.setColor(getOutlineColor(color));
		// draw the shape
		g.draw(path);
		
		// re-instate the old transform
		g.setTransform(oTransform);
	}
	
	/**
	 * Renders the given {@link Ellipse} to the given graphics context using the given scale and color.
	 * @param g the graphics context
	 * @param ellipse the ellipse to render
	 * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
	 * @param color the color
	 */
	public static final void render(Graphics2D g, Ellipse ellipse, double scale, Color color) {
		// get the local rotation and translation
		double rotation = ellipse.getRotation();
		Vector2 center = ellipse.getCenter();
		
		// save the old transform
		AffineTransform oTransform = g.getTransform();
		g.rotate(rotation);
		
		double width = ellipse.getWidth();
		double height = ellipse.getHeight();
		Ellipse2D.Double c = new Ellipse2D.Double(
			(center.x - width * 0.5) * scale,
			(center.y - height * 0.5) * scale,
			width * scale,
			height * scale);
		
		// fill the shape
		g.setColor(color);
		g.fill(c);
		// draw the outline
		g.setColor(getOutlineColor(color));
		g.draw(c);
		
		// re-instate the old transform
		g.setTransform(oTransform);
	}
	
	/**
	 * Renders the given {@link Slice} to the given graphics context using the given scale and color.
	 * @param g the graphics context
	 * @param slice the slice to render
	 * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
	 * @param color the color
	 */
	public static final void render(Graphics2D g, Slice slice, double scale, Color color) {
		double radius = slice.getSliceRadius();
		double theta2 = slice.getTheta() * 0.5;
		
		// get the local rotation and translation
		double rotation = slice.getRotation();
		Vector2 circleCenter = slice.getCircleCenter();
		
		// save the old transform
		AffineTransform oTransform = g.getTransform();
		// translate and rotate
		g.translate(circleCenter.x * scale, circleCenter.y * scale);
		g.rotate(rotation);
		
		// to draw the arc, java2d wants the top left x,y
		// as if you were drawing a circle
		Arc2D a = new Arc2D.Double(-radius * scale, 
				-radius * scale,
				2.0 * radius * scale, 
				2.0 * radius * scale,
				-Math.toDegrees(theta2),
				Math.toDegrees(2.0 * theta2),
				Arc2D.PIE);
		
		// fill the shape
		g.setColor(color);
		g.fill(a);
		// draw the outline
		g.setColor(getOutlineColor(color));
		g.draw(a);
		
		// re-instate the old transform
		g.setTransform(oTransform);
	}
	
	/**
	 * Renders the given {@link HalfEllipse} to the given graphics context using the given scale and color.
	 * @param g the graphics context
	 * @param halfEllipse the halfEllipse to render
	 * @param scale the scale to render the shape (pixels per dyn4j unit (typically meter))
	 * @param color the color
	 */
	public static final void render(Graphics2D g, HalfEllipse halfEllipse, double scale, Color color) {
		double width = halfEllipse.getWidth();
		double height = halfEllipse.getHeight();
		
		// get the local rotation and translation
		double rotation = halfEllipse.getRotation();
		Vector2 center = halfEllipse.getEllipseCenter();
		
		// save the old transform
		AffineTransform oTransform = g.getTransform();
		// translate and rotate
		g.translate(center.x * scale, center.y * scale);
		g.rotate(rotation);
		
		// to draw the arc, java2d wants the top left x,y
		// as if you were drawing a circle
		Arc2D a = new Arc2D.Double(
				(-width * 0.5) * scale, 
				-height * scale,
				width * scale, 
				height * 2.0 * scale,
				0,
				-180.0,
				Arc2D.PIE);
		
		// fill the shape
		g.setColor(color);
		g.fill(a);
		// draw the outline
		g.setColor(getOutlineColor(color));
		g.draw(a);
		
		// re-instate the old transform
		g.setTransform(oTransform);
	}
	
	/**
	 * Returns the outline color for the given color.
	 * @param color the fill color
	 * @return Color
	 */
	private static final Color getOutlineColor(Color color) {
		Color oc = color.darker();
		return new Color(oc.getRed(), oc.getGreen(), oc.getBlue(), color.getAlpha());
	}
}
