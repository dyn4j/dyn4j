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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.resources.Messages;

/**
 * Panel used to create a unit circle polygon shape.
 * @author William Bittle
 * @version 1.0.1
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
		
		JLabel lblRadius = new JLabel(Messages.getString("panel.unitCircle.radius"), Icons.INFO, JLabel.LEFT);
		lblRadius.setToolTipText(MessageFormat.format(Messages.getString("panel.unitCircle.radius.tooltip"), Messages.getString("unit.length")));
		JLabel lblCount = new JLabel(Messages.getString("panel.unitCircle.pointCount"), Icons.INFO, JLabel.LEFT);
		lblCount.setToolTipText(Messages.getString("panel.unitCircle.pointCount.tooltip"));
		
		JFormattedTextField txtRadius = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.unitCircle.radius.format")));
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
		
		JLabel lblPreview = new JLabel(Messages.getString("panel.preview"), Icons.INFO, JLabel.LEFT);
		lblPreview.setToolTipText(Messages.getString("panel.preview.tooltip"));
		this.pnlPreview = new PreviewPanel(new Dimension(250, 225), Geometry.createUnitCirclePolygon(this.count, this.radius));
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblCount)
						.addComponent(lblRadius)
						.addComponent(lblPreview))
				.addGroup(layout.createParallelGroup()
						.addComponent(txtCount)
						.addComponent(txtRadius)
						.addComponent(this.pnlPreview)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblCount)
						.addComponent(txtCount, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblRadius)
						.addComponent(txtRadius, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(lblPreview)
						.addComponent(this.pnlPreview)));
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
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.unitCircle.invalid"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
}
