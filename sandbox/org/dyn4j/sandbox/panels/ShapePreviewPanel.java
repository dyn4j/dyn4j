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
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ShapePreviewPanel extends JPanel {
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
	
	/** The list of convex shapes of a decomposition */
	private List<Convex> decomposition;
	
	/**
	 * Minimal constructor.
	 * @param size the size
	 */
	public ShapePreviewPanel(Dimension size) {
		this.size = size;
		this.convex = null;
		this.points = null;
		this.decomposition = null;
		
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
	}
	
	/**
	 * Full constructor.
	 * @param size the size of the preview panel
	 * @param initialPoints the initial points; can be null
	 */
	public ShapePreviewPanel(Dimension size, Vector2[] initialPoints) {
		this.size = size;
		this.points = initialPoints;
		this.convex = null;
		this.decomposition = null;
		
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
	}
	
	/**
	 * Full constructor.
	 * @param size the size of the preview panel
	 * @param initialShape the initial shape; can be null
	 */
	public ShapePreviewPanel(Dimension size, Convex initialShape) {
		this.size = size;
		this.convex = initialShape;
		this.points = null;
		this.decomposition = null;
		
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
	 * Sets the current poitns to render.
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
		
		// draw the origin point
		g2d.fillOval(-1, 1, 3, 3);
		g2d.scale(1.0, -1.0);
		
//		g2d.drawString("(0, 0)", 5, 5);
		
		g2d.translate(-w/2, -h/2 - TOP);
		
		g2d.drawString(FORMAT.format(s) + " Pixels/Meter", 5, 15);
	}
}
