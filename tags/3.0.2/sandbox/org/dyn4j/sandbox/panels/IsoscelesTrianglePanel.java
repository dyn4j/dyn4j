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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.resources.Messages;

/**
 * Panel used to create an isosceles triangle shape.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class IsoscelesTrianglePanel extends ConvexShapePanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = -2219164534123186833L;

	/** The default width */
	private static final double DEFAULT_WIDTH = 0.5;
	
	/** The default height */
	private static final double DEFAULT_HEIGHT = 1.0;
	
	/** The default shape */
	private static final Triangle DEFAULT_TRIANGLE = Geometry.createIsoscelesTriangle(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	
	/** The width of the rectangle */
	private double width = DEFAULT_WIDTH;
	
	/** The height of the rectangle */
	private double height = DEFAULT_HEIGHT;

	/** Panel used to preview the current shape */
	private PreviewPanel pnlPreview;
	
	/**
	 * Default constructor.
	 */
	public IsoscelesTrianglePanel() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JLabel lblWidth = new JLabel(Messages.getString("panel.isosceles.width"), Icons.INFO, JLabel.LEFT);
		lblWidth.setToolTipText(Messages.getString("panel.isosceles.width.tooltip"));
		JLabel lblHeight = new JLabel(Messages.getString("panel.isosceles.height"), Icons.INFO, JLabel.LEFT);
		lblHeight.setToolTipText(Messages.getString("panel.isosceles.height.tooltip"));
		JFormattedTextField txtWidth = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.isosceles.width.format")));
		JFormattedTextField txtHeight = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.isosceles.height.format")));
		txtWidth.setValue(DEFAULT_WIDTH);
		txtHeight.setValue(DEFAULT_HEIGHT);
		
		txtWidth.addFocusListener(new SelectTextFocusListener(txtWidth));
		txtWidth.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				width = number.doubleValue();
				try {
					pnlPreview.setShape(Geometry.createIsoscelesTriangle(width, height));
				} catch (IllegalArgumentException e) {
					// clear the shape since its not valid anymore
					pnlPreview.setShape(null);
				}
			}
		});
		txtHeight.addFocusListener(new SelectTextFocusListener(txtHeight));
		txtHeight.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				height = number.doubleValue();
				try {
					pnlPreview.setShape(Geometry.createIsoscelesTriangle(width, height));
				} catch (IllegalArgumentException e) {
					// clear the shape since its not valid anymore
					pnlPreview.setShape(null);
				}
			}
		});
		
		JLabel lblPreview = new JLabel(Messages.getString("panel.preview"), Icons.INFO, JLabel.LEFT);
		lblPreview.setToolTipText(Messages.getString("panel.preview.tooltip"));
		this.pnlPreview = new PreviewPanel(new Dimension(250, 225), Geometry.createIsoscelesTriangle(this.width, this.height));
		
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblWidth)
						.addComponent(lblHeight)
						.addComponent(lblPreview))
				.addGroup(layout.createParallelGroup()
						.addComponent(txtWidth)
						.addComponent(txtHeight)
						.addComponent(this.pnlPreview)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblWidth)
						.addComponent(txtWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblHeight)
						.addComponent(txtHeight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(lblPreview)
						.addComponent(this.pnlPreview)));
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getDefaultShape()
	 */
	@Override
	public Convex getDefaultShape() {
		return DEFAULT_TRIANGLE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.ShapePanel#getShape()
	 */
	@Override
	public Convex getShape() {
		return Geometry.createIsoscelesTriangle(this.width, this.height);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		if (width <= 0.0 || height <= 0.0) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {
		if (!this.isValidInput()) {
			JOptionPane.showMessageDialog(owner, Messages.getString("panel.isosceles.invalid"), Messages.getString("panel.invalid.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
}
