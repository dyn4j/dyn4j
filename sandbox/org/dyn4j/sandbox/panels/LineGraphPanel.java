package org.dyn4j.sandbox.panels;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JPanel;

/**
 * Panel used to display a line graph of multiple data series with only vertical labels.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class LineGraphPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = -8502340693217171952L;

	/**
	 * Represents one data point in a series.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class DataPoint {
		/** The data point value */
		public double value;
		
		/**
		 * Full constructor
		 * @param value the value of the data point
		 */
		public DataPoint(double value) {
			this.value = value;
		}
	}
	
	/**
	 * Represents a listing of data points.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	private class Series {
		/** The list of data in the series */
		public Queue<DataPoint> data;
		
		/** The color of the series line */
		public Color color;
		
		/** The start color for the series fill gradient */
		private Color color1;
		
		/** The end color for the series fill gradient */
		private Color color2;
		
		/** The maximum y value in the series */
		public double max;
		
		/**
		 * Full constructor.
		 * @param color the color for the series line
		 */
		public Series(Color color) {
			this.data = new LinkedList<DataPoint>();
			this.color = color;
			this.color1 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 50);
			this.color2 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
		}

		/**
		 * Adds a new data point to the series.
		 * @param value the value of the data point
		 */
		public void addDataPoint(double value) {
			if (this.data.size() == dataPointLimit) {
				this.data.poll();
			}
			this.data.add(new DataPoint(value));
			// track the largest y value
			if (value > this.max) this.max = value;
		}
		
	}
	
	/** The list of data series */
	private List<Series> series;
	
	/** The maximum y value */
	private double maxY;
	
	/** The maximum number of data points */
	private int dataPointLimit;
	
	/** The number of horizontal labels */
	private int verticalTicks = 3;
	
	/** The top padding */
	private int paddingTop = 10;
	
	/** The right padding */
	private int paddingRight = 10;
	
	/** The bottom padding */
	private int paddingBottom = 10;
	
	/** The left padding */
	private int paddingLeft = 40;

	/** The horizontal guide color */
	private Color guideColor = new Color(170, 170, 170, 100);
	
	/** The axis color */
	private Color axisColor = new Color(90, 90, 90);
	
	/** The axis labels font */
	private Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
	
	/**
	 * Minimal constructor.
	 * @param maxY the maximum value of the y axis
	 */
	public LineGraphPanel(double maxY) {
		this(maxY, 50);
	}
	
	/**
	 * Full constructor.
	 * @param maxY the maximum value of the y axis
	 * @param dataPointLimit the maximum number of datapoints
	 */
	public LineGraphPanel(double maxY, int dataPointLimit) {
		this.series = new ArrayList<Series>();
		this.maxY = maxY;
		this.dataPointLimit = dataPointLimit;
	}
	
	/**
	 * Sets the maximum value of the y axis.
	 * @param maxY the maximum value of the y axis 
	 */
	public void setMaximumValue(double maxY) {
		this.maxY = maxY;
	}
	
	/**
	 * Sets the maximum number of data points to show.
	 * @param limit the maximum number of data points
	 */
	public void setDataPointLimit(int limit) {
		this.dataPointLimit = limit;
	}
	
	/**
	 * Sets the number of vertical ticks.
	 * @param count the number of vertical ticks
	 */
	public void setVerticalTicks(int count) {
		this.verticalTicks = count;
	}
	
	/**
	 * Sets the padding of the whitespace for the graph.
	 * <p>
	 * The padding provides space for the vertical labels.
	 * @param top the top padding
	 * @param right the right padding
	 * @param bottom the bottom padding
	 * @param left the left padding
	 */
	public void setPadding(int top, int right, int bottom, int left) {
		this.paddingTop = top;
		this.paddingRight = right;
		this.paddingBottom = bottom;
		this.paddingLeft = left;
	}
	
	/**
	 * Adds a new series to the list of series.
	 * @param color the color for the series
	 * @return int the series index
	 */
	public int addSeries(Color color) {
		int i = this.series.size();
		this.series.add(new Series(color));
		return i;
	}
	
	/**
	 * Adds a new data point to the default series (index 0).
	 * @param value the value of the data point
	 */
	public void addDataPoint(double value) {
		this.series.get(0).addDataPoint(value);
		this.repaint();
	}
	
	/**
	 * Adds a new data point to the given series.
	 * @param value the value of the data point
	 * @param series the series index
	 */
	public void addDataPoint(double value, int series) {
		this.series.get(series).addDataPoint(value);
		this.repaint();
	}
	
	/**
	 * Sets the size of this panel to a static size.
	 * @param size the size
	 */
	public void setStaticSize(Dimension size) {
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		this.setMinimumSize(size);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		// call the super method
		super.paintComponent(g);
		
		// paint a gradient background
		Graphics2D g2d = (Graphics2D)g;
		
		// get the size of the panel
		Dimension size = this.getSize();
		double w = size.getWidth();
		double h = size.getHeight();
		
		double mw = w - this.paddingLeft - this.paddingRight;
		double mh = h - this.paddingTop - this.paddingBottom;
		
		// fill background
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, (int)w, (int)h);
		
		// fill background for chart
		g2d.setPaint(new GradientPaint(new Point2D.Float(0, 0), new Color(240, 240, 240), new Point2D.Float(0, (float)h), Color.WHITE));
		g2d.fillRect(this.paddingLeft, this.paddingTop, (int)mw, (int)mh);
		
		// invert the y axis to draw from the bottom left corner
		AffineTransform af = g2d.getTransform();
		g2d.translate(0.0, h);
		g2d.transform(AffineTransform.getScaleInstance(1.0, -1.0));
		
		if (this.series.isEmpty()) return;
		
		int slSize = this.series.size();
		int sSize = this.series.get(0).data.size();
		
		if (sSize < 2) return;
		
		// save current info
		Stroke stroke = g2d.getStroke();
		Composite composite = g2d.getComposite();
		Shape clip = g2d.getClip();
		g2d.setClip(this.paddingLeft, this.paddingBottom, (int)mw, (int)mh);
		g2d.setComposite(AlphaComposite.SrcOver);
		
		int[] xs = new int[sSize * 2];
		int[] ys = new int[sSize * 2];
		int tSize;
		for (int i = slSize - 1; i >= 0; i--) {
			Series seriesI = this.series.get(i);
			// compute the x increment
			double xi = (mw) / (double)(sSize - 1);
			
			double x = this.paddingLeft;
			int j = 0;
			for(DataPoint dp : seriesI.data) {
				// compute the y value given the height of the container
				double y = dp.value / this.maxY * mh;
				
				// if its the last point in the list then set the
				// x value to the edge of the allowed area (otherwise
				// it can be less than the width because of rounding)
				if (j == (sSize - 1)) {
					xs[j] = (int)(w - this.paddingRight);
				} else {
					xs[j] = (int)Math.floor(x);
				}
				ys[j] = (int)Math.floor(y) + this.paddingBottom;
				x += xi;
				j++;
			}
			
			if (i == 0) {
				// if its the only series or its the last one to draw then just
				// output two more points
				ys[sSize] = this.paddingBottom;
				xs[sSize + 1] = this.paddingLeft;
				ys[sSize + 1] = this.paddingBottom;
				tSize = sSize + 2;
			} else {
				Series seriesJ = this.series.get(i - 1);
				x = this.paddingLeft;
				j = 0;
				for (DataPoint dp : seriesJ.data) {
					// compute the y value given the height of the container
					double y = dp.value / this.maxY * mh;
					int pos = sSize * 2 - j - 1;
					
					// if its the last point in the list then set the
					// x value to the edge of the allowed area (otherwise
					// it can be less than the width because of rounding)
					if (j == (sSize - 1)) {
						xs[pos] = (int)(w - this.paddingRight);
					} else {
						xs[pos] = (int)Math.floor(x);
					}
					ys[pos] = (int)Math.floor(y) + this.paddingBottom;
					x += xi;
					j++;
				}
				tSize = sSize * 2;
			}
			
			// fill in between the poly-lines
			g2d.setPaint(new GradientPaint(
					new Point2D.Float(0, 0), 
					seriesI.color1, 
					new Point2D.Float(0, (float)(seriesI.max / this.maxY * h)), 
					seriesI.color2));
			g.fillPolygon(xs, ys, tSize);
			
			// draw the line
			g.setColor(seriesI.color);
			g2d.setStroke(new BasicStroke(3));
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.drawPolyline(xs, ys, sSize);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
		g2d.setStroke(stroke);
		g2d.setComposite(composite);
		
		// axes
		g2d.setColor(new Color(90, 90, 90));
		g2d.drawLine(this.paddingLeft, this.paddingBottom, this.paddingLeft, this.paddingBottom + (int)mh);
		g2d.drawLine(this.paddingLeft, this.paddingBottom + 1, this.paddingLeft + (int)mw, this.paddingBottom + 1);
		g2d.setClip(clip);
		
		g2d.setTransform(af);
		
		this.drawHorizontalAxis(g2d);
	}
	
	/**
	 * Renders the horizontal axis labels, ticks and guides.
	 * @param g2d the graphics surface to render to
	 */
	private void drawHorizontalAxis(Graphics2D g2d) {
		// get the height and width of the panel
		int h = this.getSize().height;
		int w = this.getSize().width - this.paddingRight;
		// save the current stroke
		Font font = g2d.getFont();
		FontMetrics fm = getFontMetrics(this.font);
		g2d.setFont(this.font);
		Stroke stroke = g2d.getStroke();
		Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[] {1.0f}, 0.0f);
		DecimalFormat df = new DecimalFormat("0");
		int yi = (h - this.paddingBottom - this.paddingTop) / this.verticalTicks;
		int y = h - this.paddingBottom - 1;
		for (int i = 0; i <= this.verticalTicks; i++) {
			double value = this.maxY / this.verticalTicks * i;
			g2d.setColor(this.axisColor);
			g2d.setStroke(stroke);
			g2d.drawLine(this.paddingLeft - 3, y, this.paddingLeft, y);
			String text = df.format(value) + " MB";
			int fw = fm.stringWidth(text);
			int fh = fm.getAscent() / 2 - 1;
			g2d.drawString(text, this.paddingLeft - fw - 5, y + fh);
			if (i != 0) {
				g2d.setColor(this.guideColor);
				g2d.setStroke(dashed);
				g2d.drawLine(this.paddingLeft, y, w, y);
			}
			y -= yi;
		}
		// reset the stroke
		g2d.setFont(font);
		g2d.setStroke(stroke);
	}
}
