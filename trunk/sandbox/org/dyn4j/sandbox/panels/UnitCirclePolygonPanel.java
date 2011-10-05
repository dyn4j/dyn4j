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
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to create a unit circle polygon shape.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class UnitCirclePolygonPanel extends ConvexShapePanel implements InputPanel {
	/** the version id */
	private static final long serialVersionUID = -3651067811878657243L;

	/** The default circle radius */
	private static final double DEFAULT_RADIUS = 0.5;
	
	/** The default number of points */
	private static final int DEFAULT_COUNT = 5;
	
	/** The default circle shape */
	private static final Polygon DEFAULT_SHAPE = Geometry.createUnitCirclePolygon(DEFAULT_COUNT, DEFAULT_RADIUS);
	
	/** The circle radius */
	private double radius = DEFAULT_RADIUS;
	
	/** The point count */
	private int count = DEFAULT_COUNT;

	/** Panel used to preview the current shape */
	private PreviewPanel pnlPreview;
	
	/**
	 * Default constructor.
	 */
	public UnitCirclePolygonPanel() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JLabel lblRadius = new JLabel("Radius", Icons.INFO, JLabel.LEFT);
		lblRadius.setToolTipText("The radius of the circle that the polygon will fit inside of in Meters.");
		JLabel lblCount = new JLabel("Point Count", Icons.INFO, JLabel.LEFT);
		lblCount.setToolTipText("The number of points to create.");
		
		JFormattedTextField txtRadius = new JFormattedTextField(new DecimalFormat("0.000"));
		JFormattedTextField txtCount = new JFormattedTextField(NumberFormat.getIntegerInstance());
		
		txtRadius.setValue(DEFAULT_RADIUS);
		txtCount.setValue(DEFAULT_COUNT);
		
		txtRadius.addFocusListener(new SelectTextFocusListener(txtRadius));
		txtRadius.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				radius = number.doubleValue();
				try {
					pnlPreview.setShape(Geometry.createUnitCirclePolygon(count, radius));
				} catch (IllegalArgumentException e) {
					// clear the shape since its not valid anymore
					pnlPreview.setShape(null);
				}
			}
		});
		
		txtCount.addFocusListener(new SelectTextFocusListener(txtCount));
		txtCount.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				count = number.intValue();
				try {
					pnlPreview.setShape(Geometry.createUnitCirclePolygon(count, radius));
				} catch (IllegalArgumentException e) {
					// clear the shape since its not valid anymore
					pnlPreview.setShape(null);
				}
			}
		});
		
		JLabel lblPreview = new JLabel("Preview", Icons.INFO, JLabel.LEFT);
		lblPreview.setToolTipText("Shows a preview of the current shape.");
		this.pnlPreview = new PreviewPanel(new Dimension(150, 150), Geometry.createUnitCirclePolygon(this.count, this.radius));
		this.pnlPreview.setBackground(Color.WHITE);
		this.pnlPreview.setBorder(BorderFactory.createEtchedBorder());
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblCount)
						.addComponent(lblRadius)
						.addComponent(lblPreview))
				.addGroup(layout.createParallelGroup()
						.addComponent(txtCount)
						.addComponent(txtRadius)
						.addComponent(this.pnlPreview, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblCount)
						.addComponent(txtCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblRadius)
						.addComponent(txtRadius, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblPreview)
						.addComponent(this.pnlPreview, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getDefaultShape()
	 */
	@Override
	public Convex getDefaultShape() {
		return DEFAULT_SHAPE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getShape()
	 */
	@Override
	public Convex getShape() {
		return Geometry.createUnitCirclePolygon(this.count, this.radius);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		return this.radius > 0.0 && this.count >= 3;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {
		if (!this.isValidInput()) {
			JOptionPane.showMessageDialog(owner, "A circle requires a point count greater than 2 and a radius greater than zero.", "Notice", JOptionPane.ERROR_MESSAGE);
		}
	}
}
