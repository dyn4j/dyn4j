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

import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.resources.Messages;

/**
 * Panel used to capture translation and rotation.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class TransformPanel extends JPanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = -5389987241883097903L;

	/** The translation */
	private Vector2 translation;
	
	/** The rotation (in degrees) */
	private double rotation;
	
	/** The translation label */
	private JLabel lblT;
	
	/** The label for the translation along the x-axis */
	private JLabel lblX;
	
	/** The label for the translation along the y-axis */
	private JLabel lblY;
	
	/** The label for the rotation */
	private JLabel lblR;
	
	/** The text box for the translation along the x-axis */
	private JFormattedTextField txtX;
	
	/** The text box for the translation along the y-axis */
	private JFormattedTextField txtY;
	
	/** The text box for the rotation */
	private JFormattedTextField txtR;
	
	/**
	 * Default contructor.
	 */
	public TransformPanel() {
		this(new Vector2(), 0.0, null);
	}
	
	/**
	 * Optional contructor.
	 * @param header the header component of the panel
	 */
	public TransformPanel(JComponent header) {
		this(new Vector2(), 0.0, header);
	}
	
	/**
	 * Optional constructor.
	 * @param transform initial transform values
	 */
	public TransformPanel(Transform transform) {
		this(transform.getTranslation(), transform.getRotation(), null);
	}
	
	/**
	 * Full constructor.
	 * @param transform initial transform values
	 * @param header the header component of the panel
	 */
	public TransformPanel(Transform transform, JComponent header) {
		this(transform.getTranslation(), transform.getRotation(), header);
	}
	
	/**
	 * Optional constructor.
	 * @param tx the initial translation
	 * @param rot the initial rotation
	 */
	public TransformPanel(Vector2 tx, double rot) {
		this(tx, rot, null);
	}
	
	/**
	 * Full constructor.
	 * @param tx the initial translation
	 * @param rot the initial rotation
	 * @param header the header component of the panel
	 */
	public TransformPanel(Vector2 tx, double rot, JComponent header) {
		this.translation = tx.copy();
		this.rotation = Math.toDegrees(rot);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		boolean headerSupplied = (header != null);
		
		this.lblT = new JLabel(Messages.getString("panel.transform.translation"), Icons.INFO, JLabel.LEFT);
		this.lblT.setToolTipText(MessageFormat.format(Messages.getString("panel.transform.translation.tooltip"), Messages.getString("unit.length")));
		this.lblX = new JLabel(Messages.getString("x"));
		this.lblY = new JLabel(Messages.getString("y"));
		this.lblR = new JLabel(Messages.getString("panel.transform.rotation"), Icons.INFO, JLabel.LEFT);
		this.lblR.setToolTipText(MessageFormat.format(Messages.getString("panel.transform.rotation.tooltip"), Messages.getString("unit.rotation")));
		
		this.txtX = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.transform.translation.format")));
		this.txtY = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.transform.translation.format")));
		this.txtR = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.transform.rotation.format")));
		
		this.txtX.setColumns(7);
		this.txtY.setColumns(7);
		this.txtR.setColumns(7);
		
		this.txtX.setValue(this.translation.x);
		this.txtY.setValue(this.translation.y);
		this.txtR.setValue(this.rotation);
		
		this.txtX.addFocusListener(new SelectTextFocusListener(this.txtX));
		this.txtX.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				translation.x = number.doubleValue();
			}
		});
		
		this.txtY.addFocusListener(new SelectTextFocusListener(this.txtY));
		this.txtY.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				translation.y = number.doubleValue();
			}
		});
		
		this.txtR.addFocusListener(new SelectTextFocusListener(this.txtR));
		this.txtR.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Number number = (Number)event.getNewValue();
				rotation = number.doubleValue();
			}
		});
		
		JLabel lblFiller = new JLabel();
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		ParallelGroup pg = layout.createParallelGroup();
		if (headerSupplied) {
			pg.addComponent(header);
		}
		layout.setHorizontalGroup(pg
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup()
								.addComponent(this.lblT)
								.addComponent(lblFiller)
								.addComponent(this.lblR))
						.addGroup(layout.createParallelGroup()
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.txtX)
										.addComponent(this.lblX))
								.addGroup(layout.createSequentialGroup()
										.addComponent(this.txtY)
										.addComponent(this.lblY))
								.addComponent(this.txtR))));
		
		SequentialGroup sg = layout.createSequentialGroup();
		if (headerSupplied) {
			sg.addComponent(header);
		}
		layout.setVerticalGroup(sg
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblT)
						.addComponent(this.txtX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblX))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblFiller)
						.addComponent(this.txtY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(this.lblY))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(this.lblR)
						.addComponent(this.txtR, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	}
	
	/**
	 * Returns the selected translation.
	 * @return Vector2
	 */
	public Vector2 getTranslation() {
		return this.translation.copy();
	}
	
	/**
	 * Returns the selected rotation (in degrees).
	 * @return double
	 */
	public double getRotation() {
		return Math.toRadians(this.rotation);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#isValidInput()
	 */
	@Override
	public boolean isValidInput() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.sandbox.panels.InputPanel#showInvalidInputMessage(java.awt.Window)
	 */
	@Override
	public void showInvalidInputMessage(Window owner) {}
}
