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

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.decompose.Decomposer;
import org.dyn4j.sandbox.utilities.UIUtilities;

/**
 * Panel used to create a non-convex polygon using arbitrary points.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class ArbitraryNonConvexPolygonPanel extends NonConvexShapePanel implements InputPanel, ActionListener {
	/** The version id */
	private static final long serialVersionUID = 7259833235547997274L;
	
	/** The default non-convex polygon */
	private static final Vector2[] DEFAULT_POLYGON = new Vector2[] {
		new Vector2(-0.5, -0.5),
		new Vector2(0.5, -0.5),
		new Vector2(0.5, 0.5),
		new Vector2(0.0, 0.0),
		new Vector2(-0.5, 0.5),
	};
	
	/** The list of point panels */
	private List<PointPanel> pointPanels = new ArrayList<PointPanel>();
	
	/** The panel containing the point panels */
	private JPanel pnlPanel;
	
	/** The scroll panel for the point panels */
	private JScrollPane scrPane;
	
	/** The text label for the polygon help */
	private JTextPane lblText;

	/** Panel used to preview the current shape */
	private PreviewPanel pnlPreview;

	/** The decomposotion algorithm */
	private Decomposer decomposer = null;
	
	/**
	 * Full constructor.
	 * @param decomposer the decomposition algorithm
	 */
	public ArbitraryNonConvexPolygonPanel(Decomposer decomposer) {
		this.decomposer = decomposer;
		this.pnlPanel = new JPanel();
		
		this.lblText = new JTextPane();
		this.lblText.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		this.lblText.setFont(UIUtilities.getDefaultLabelFont());
		this.lblText.setContentType("text/html");
		this.lblText.setText("<html>A simple polygon without holes or crossing edges.</html>");
		this.lblText.setEditable(false);
		
		this.scrPane = new JScrollPane(this.pnlPanel);
		this.scrPane.setPreferredSize(new Dimension(200, 200));
		
		for (int i = 0; i < DEFAULT_POLYGON.length; i++) {
			Vector2 p = DEFAULT_POLYGON[i];
			PointPanel panel = new PointPanel(p.x, p.y);
			panel.addActionListener(this);
			this.pointPanels.add(panel);
		}
		
		this.pnlPreview = new PreviewPanel(new Dimension(250, 225));
		this.pnlPreview.setDecomposition(this.decomposer.decompose(DEFAULT_POLYGON));
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.lblText)
				.addComponent(this.scrPane)
				.addComponent(this.pnlPreview));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.lblText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(this.scrPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
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
				try {
					// attempt to refresh the decomposition
					this.pnlPreview.setDecomposition(this.decomposer.decompose(this.getPoints()));
				} catch (Exception e) {
					this.pnlPreview.setPoints(this.getPoints());
				}
			} else if ("remove".equals(event.getActionCommand())) {
				// remove the point panel from the list
				this.pointPanels.remove(index);
				// redo the layout
				this.createLayout();
				try {
					// attempt to refresh the decomposition
					this.pnlPreview.setDecomposition(this.decomposer.decompose(this.getPoints()));
				} catch (Exception e) {
					this.pnlPreview.setPoints(this.getPoints());
				}
			} else if ("changed".equals(event.getActionCommand())) {
				// a value has changed
				try {
					// attempt to refresh the decomposition
					this.pnlPreview.setDecomposition(this.decomposer.decompose(this.getPoints()));
				} catch (Exception e) {
					this.pnlPreview.setPoints(this.getPoints());
				}
			}
		}
	}

	/**
	 * Sets the decomposition algorithm.
	 * @param decomposer the decomposition algorithm
	 */
	public void setDecomposer(Decomposer decomposer) {
		this.decomposer = decomposer;
		// rerun the decomposition on the current point set
		try {
			// attempt to refresh the decomposition
			this.pnlPreview.setDecomposition(this.decomposer.decompose(this.getPoints()));
		} catch (Exception e) {
			this.pnlPreview.setPoints(this.getPoints());
		}
	}
	
	/**
	 * Returns the current decomposition algorithm.
	 * @return Decomposer
	 */
	public Decomposer getDecomposer() {
		return this.decomposer;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.NonConvexShapePanel#getShapes()
	 */
	@Override
	public List<Convex> getShapes() {
		try {
			return this.decomposer.decompose(this.getPoints());
		} catch (Exception e) {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		Vector2[] points = this.getPoints();
		try {
			this.decomposer.decompose(points);
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
		Vector2[] points = this.getPoints();
		try {
			this.decomposer.decompose(points);
		} catch (Exception e) {
			String message = e.getMessage();
			if (message == null || message.isEmpty()) {
				message = "A simple polygon cannot have crossing edges.";
			}
			JOptionPane.showMessageDialog(owner, message, "Notice", JOptionPane.ERROR_MESSAGE);
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
