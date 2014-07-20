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
package org.dyn4j.sandbox.panels;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.hull.HullGenerator;
import org.dyn4j.sandbox.resources.Messages;

/**
 * Panel used to create a polygon using arbitrary points.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class ArbitraryConvexHullPolygonPanel extends ConvexHullShapePanel implements InputPanel, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 7259833235547997274L;

	/** The default point count of the unit circle polygon */
	private static final int DEFAULT_COUNT = 8;
	
	/** The default polygon is just a unit circle polygon */
	private static final Vector2[] DEFAULT_POINT_CLOUD = new Vector2[] {
		new Vector2(0.0, 1.0),
		new Vector2(1.0, 1.0),
		new Vector2(1.0, 0.0),
		new Vector2(0.0, 0.0),
		new Vector2(0.5, 2.0),
		new Vector2(1.5, 2.0),
		new Vector2(-1.0, 0.5),
		new Vector2(-3.0, -0.5)
	};
	
	/** The list of point panels */
	private List<PointPanel> pointPanels = new ArrayList<PointPanel>();
	
	/** The panel containing the point panels */
	private JPanel pnlPanel;
	
	/** The scroll panel for the point panels */
	private JScrollPane scrPane;
	
	/** Panel used to preview the current shape */
	private PreviewPanel pnlPreview;
	
	/** The convex hull algorithm */
	private HullGenerator hullGenerator = null;
	
	/**
	 * Full constructor.
	 * @param hullGenerator the convex hull generation algorithm
	 */
	public ArbitraryConvexHullPolygonPanel(HullGenerator hullGenerator) {
		this.pnlPanel = new JPanel();
		
		this.hullGenerator = hullGenerator;
		this.scrPane = new JScrollPane(this.pnlPanel);
		
		Vector2[] points = DEFAULT_POINT_CLOUD;
		for (int i = 0; i < DEFAULT_COUNT; i++) {
			Vector2 p = points[i];
			PointPanel panel = new PointPanel(p.x, p.y);
			panel.addActionListener(this);
			this.pointPanels.add(panel);
		}
		
		this.pnlPreview = new PreviewPanel(new Dimension(250, 225), new Polygon(this.hullGenerator.generate(DEFAULT_POINT_CLOUD)), DEFAULT_POINT_CLOUD);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.scrPane)
				.addComponent(this.pnlPreview));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.scrPane)
				.addComponent(this.pnlPreview));
		
		this.createLayout();
	}
	
	/**
	 * Creates the layout for the panel.
	 */
	private void createLayout() {
		// remove all the components
		this.pnlPanel.removeAll();
		
		// recreate the layout
		GroupLayout layout = new GroupLayout(this.pnlPanel);
		this.pnlPanel.setLayout(layout);
		
		// set all the flags
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(false);
		
		int size = this.pointPanels.size();
		
		// create the horizontal layout
		ParallelGroup hGroup = layout.createParallelGroup();
		for (int i = 0; i < size; i++) {
			PointPanel panel = this.pointPanels.get(i);
			hGroup.addComponent(panel);
			if (i < 3) {
				panel.btnRemove.setEnabled(false);
			} else {
				panel.btnRemove.setEnabled(true);
			}
		}
		// create the vertical layout
		SequentialGroup vGroup = layout.createSequentialGroup();
		for (int i = 0; i < size; i++) {
			PointPanel panel = this.pointPanels.get(i);
			vGroup.addComponent(panel);
		}
		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		// find the point panel issuing the event
		int index = this.pointPanels.indexOf(event.getSource());
		// check if its found
		if (index >= 0) {
			// check the type of event
			if ("add".equals(event.getActionCommand())) {
				// insert a new point panel after this one
				PointPanel panel = new PointPanel();
				panel.addActionListener(this);
				this.pointPanels.add(index + 1, panel);
				// redo the layout
				this.createLayout();
				pnlPreview.setHull(this.getShape(), this.getPoints());
			} else if ("remove".equals(event.getActionCommand())) {
				// remove the point panel from the list
				this.pointPanels.remove(index);
				// redo the layout
				this.createLayout();
				pnlPreview.setHull(this.getShape(), this.getPoints());
			} else if ("changed".equals(event.getActionCommand())) {
				// a value has changed
				pnlPreview.setHull(this.getShape(), this.getPoints());
			}
		}
	}
	
	/**
	 * Returns the hull generator currently being used.
	 * @return HullGenerator
	 */
	public HullGenerator getHullGenerator() {
		return this.hullGenerator;
	}
	
	/**
	 * Sets the hull generator currently being used.
	 * @param hullGenerator the hull generator
	 */
	public void setHullGenerator(HullGenerator hullGenerator) {
		this.hullGenerator = hullGenerator;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getShape()
	 */
	@Override
	public Convex getShape() {
		// get the vertices of the convex hull
		Vector2[] vertices = this.hullGenerator.generate(this.getPoints());
		// check the winding direction
		if (Geometry.getWinding(vertices) < 0) {
			Geometry.reverseWinding(vertices);
		}
		return new Polygon(vertices);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		try {
			this.getShape();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {
		try {
			this.getShape();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(owner, e.getMessage(), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Returns a list of points from the point panels.
	 * @return Vector2[]
	 */
	private Vector2[] getPoints() {
		int size = this.pointPanels.size();
		Vector2[] points = new Vector2[size];
		for (int i = 0; i < size; i++) {
			PointPanel panel = this.pointPanels.get(i);
			points[i] = new Vector2(panel.getValueX(), panel.getValueY());
		}
		return points;
	}
}
