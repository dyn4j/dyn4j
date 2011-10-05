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
package org.dyn4j.sandbox.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JPanel;

import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.Wound;

/**
 * Panel used by the shape panels to preview the current shape being created.
 * <p>
 * Use the {@link #setPoints(Vector2[])} method to draw a preview of a polygon
 * that is not valid.
 * <p>
 * Use the {@link #setDecomposition(List)} method to draw a preview of a decomposed
 * simple polygon.
 * <p>
 * Use the {@link #setHull(Convex, Vector2[])} method to draw a convex shape along with
 * its point cloud.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class PreviewPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = 8800065059450605097L;

	/** The padding between the edges of the panel and the shape in pixels */
	private static final int PADDING = 5;
	
	/** The padding at the top to show the scale */
	private static final int TOP = 10;
	
	/** The format for the scale */
	private static final DecimalFormat FORMAT = new DecimalFormat("0");
	
	/** The size of the panel */
	private Dimension size;
	
	/** The convex shape to draw */
	private Convex convex;
	
	/** The points of the polygon to draw */
	private Vector2[] points;
	
	/** The points of the convex hull */
	private Vector2[] pointCloud;
	
	/** The list of convex shapes of a decomposition */
	private List<Convex> decomposition;
	
	/**
	 * Minimal constructor.
	 * @param size the size
	 */
	public PreviewPanel(Dimension size) {
		this.size = size;
		this.convex = null;
		this.points = null;
		this.decomposition = null;
		this.pointCloud = null;
		
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
	}
	
	/**
	 * Full constructor.
	 * @param size the size of the preview panel
	 * @param initialPoints the initial points; can be null
	 */
	public PreviewPanel(Dimension size, Vector2[] initialPoints) {
		this.size = size;
		this.points = initialPoints;
		this.convex = null;
		this.decomposition = null;
		this.pointCloud = null;
		
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
	}
	
	/**
	 * Full constructor.
	 * @param size the size of the preview panel
	 * @param initialShape the initial shape; can be null
	 */
	public PreviewPanel(Dimension size, Convex initialShape) {
		this.size = size;
		this.convex = initialShape;
		this.points = null;
		this.decomposition = null;
		this.pointCloud = null;
		
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
	}
	
	/**
	 * Full constructor.
	 * @param size the size of the preview panel
	 * @param initialShape the initial shape; can be null
	 * @param initialPoints the initial points; can be null
	 */
	public PreviewPanel(Dimension size, Convex initialShape, Vector2[] initialPoints) {
		this.size = size;
		this.points = null;
		this.convex = initialShape;
		this.decomposition = null;
		this.pointCloud = initialPoints;
		
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
	}
	
	/**
	 * Sets the current shape to render.
	 * @param shape the shape
	 */
	public void setShape(Convex shape) {
		this.convex = shape;
		this.points = null;
		this.decomposition = null;
		this.repaint();
	}
	
	/**
	 * Sets the current points to render.
	 * @param points the points
	 */
	public void setPoints(Vector2[] points) {
		this.points = points;
		this.convex = null;
		this.decomposition = null;
		this.repaint();
	}
	
	/**
	 * Sets the decomposition to render.
	 * @param decomposition the decomposition
	 */
	public void setDecomposition(List<Convex> decomposition) {
		this.points = null;
		this.convex = null;
		this.decomposition = decomposition;
		this.repaint();
	}
	
	/**
	 * Sets the current shape and points to render.
	 * @param shape the shape
	 * @param points the points
	 */
	public void setHull(Convex shape, Vector2[] points) {
		this.pointCloud = points;
		this.convex = shape;
		this.decomposition = null;
		this.repaint();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int w = size.width;
		int h = size.height;
		
		// the scale
		double s = 0.0;
		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setColor(Color.BLACK);
		g2d.translate(w/2, h/2 + TOP);
		g2d.scale(1.0, -1.0);
		
		double maxSize = 0.0;
		if (w < h - TOP) {
			maxSize = w * 0.5 - PADDING;
		} else {
			maxSize = h * 0.5 - TOP - PADDING;
		}
		
		// check for convex polygon
		if (convex != null) {
			if (convex instanceof Circle) {
				// get the radius
				double r = ((Circle)convex).getRadius();
				// compute the scale to fit the shape within the size
				s = maxSize / r;
				// draw the circle
				g2d.drawOval(
						(int)Math.floor(-r * s),
						(int)Math.floor(-r * s),
						(int)Math.floor(r * 2.0 * s),
						(int)Math.floor(r * 2.0 * s));
			} else if (convex instanceof Wound) {
				Vector2[] vs = ((Wound)convex).getVertices();
				double m = Math.max(Math.abs(vs[0].x), Math.abs(vs[0].y));
				// find the largest coordinate to properly scale
				for (int i = 1; i < vs.length; i++) {
					Vector2 v = vs[i];
					double t = Math.max(Math.abs(v.x), Math.abs(v.y));
					if (m < t) {
						m = t;
					}
				}
				// compute the scale to fit the shape within the size
				s = maxSize / m;
				// draw the polygon to scale at the center
				for (int i = 0; i < vs.length; i++) {
					Vector2 p1 = vs[i];
					Vector2 p2 = vs[i + 1 == vs.length ? 0 : i + 1];
					g2d.drawLine(
							(int)Math.floor(p1.x * s),
							(int)Math.floor(p1.y * s),
							(int)Math.floor(p2.x * s),
							(int)Math.floor(p2.y * s));
				}
			}
		}
		
		// check for non-convex polygon
		if (this.points != null) {
			Vector2[] vs = this.points;
			double m = Math.max(Math.abs(vs[0].x), Math.abs(vs[0].y));
			// find the largest coordinate to properly scale
			for (int i = 1; i < vs.length; i++) {
				Vector2 v = vs[i];
				double t = Math.max(Math.abs(v.x), Math.abs(v.y));
				if (m < t) {
					m = t;
				}
			}
			// compute the scale to fit the shape within the size
			s = maxSize / m;
			// draw the polygon to scale at the center
			for (int i = 0; i < vs.length; i++) {
				Vector2 p1 = vs[i];
				Vector2 p2 = vs[i + 1 == vs.length ? 0 : i + 1];
				g2d.drawLine(
						(int)Math.floor(p1.x * s),
						(int)Math.floor(p1.y * s),
						(int)Math.floor(p2.x * s),
						(int)Math.floor(p2.y * s));
			}
		}
		
		if (this.decomposition != null) {
			double m = -Double.MAX_VALUE;
			
			// loop over all the convexes
			for (Convex convex : this.decomposition) {
				// render the convex
				if (convex instanceof Wound) {
					// all of them should be wound
					Vector2[] vs = ((Wound)convex).getVertices();
					// find the largest coordinate to properly scale
					for (int i = 1; i < vs.length; i++) {
						Vector2 v = vs[i];
						double t = Math.max(Math.abs(v.x), Math.abs(v.y));
						if (m < t) {
							m = t;
						}
					}
				}
			}
			// compute the scale to fit the shape within the size
			s = maxSize / m;
			
			// loop over all the convexes
			for (Convex convex : this.decomposition) {
				// render the convex
				if (convex instanceof Wound) {
					// all of them should be wound
					Vector2[] vs = ((Wound)convex).getVertices();
					// find the largest coordinate to properly scale
					for (int i = 0; i < vs.length; i++) {
						Vector2 p1 = vs[i];
						Vector2 p2 = vs[i + 1 == vs.length ? 0 : i + 1];
						g2d.drawLine(
								(int)Math.floor(p1.x * s),
								(int)Math.floor(p1.y * s),
								(int)Math.floor(p2.x * s),
								(int)Math.floor(p2.y * s));
					}
				}
			}
		}
		
		// draw the point cloud
		if (this.pointCloud != null) {
			Vector2[] vs = this.pointCloud;
			double m = Math.max(Math.abs(vs[0].x), Math.abs(vs[0].y));
			// find the largest coordinate to properly scale
			for (int i = 1; i < vs.length; i++) {
				Vector2 v = vs[i];
				double t = Math.max(Math.abs(v.x), Math.abs(v.y));
				if (m < t) {
					m = t;
				}
			}
			// compute the scale to fit the shape within the size
			s = maxSize / m;
			// find the largest coordinate to properly scale
			for (int i = 0; i < vs.length; i++) {
				Vector2 p = vs[i];
				g2d.fillOval(
						(int)Math.floor(p.x * s) - 1,
						(int)Math.floor(p.y * s) - 1,
						3,
						3);
			}
		}
		
		// draw the origin point
		g2d.drawOval(-2, -2, 5, 5);
		g2d.scale(1.0, -1.0);
		
//		g2d.drawString("(0, 0)", 5, 5);
		
		g2d.translate(-w/2, -h/2 - TOP);
		
		g2d.drawString(FORMAT.format(s) + " Pixels/Meter", 5, 15);
	}
}
