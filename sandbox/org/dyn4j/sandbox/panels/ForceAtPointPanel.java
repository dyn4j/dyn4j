/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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
import java.text.DecimalFormat;
import java.text.MessageFormat;

import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.resources.Messages;
import org.dyn4j.sandbox.utilities.ControlUtilities;

/**
 * Panel used to apply a force to a body.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class ForceAtPointPanel extends JPanel implements InputPanel {	
	/** The version id */
	private static final long serialVersionUID = 2036550577745400369L;

	/** The x value of the force input */
	private JFormattedTextField txtFX;
	
	/** The y value of the force input */
	private JFormattedTextField txtFY;
	
	/** The x value of the application point input */
	private JFormattedTextField txtPX;
	
	/** The y value of the application point input */
	private JFormattedTextField txtPY;
	
	/**
	 * Default constructor.
	 */
	public ForceAtPointPanel() {
		JLabel lblPoint = new JLabel(Messages.getString("panel.forceAtPoint.point"), Icons.INFO, JLabel.LEFT);
		lblPoint.setToolTipText(Messages.getString("panel.forceAtPoint.point.tooltip"));
		JLabel lblPX = new JLabel(Messages.getString("x"));
		JLabel lblPY = new JLabel(Messages.getString("y"));
		
		this.txtPX = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.forceAtPoint.point.format")));
		this.txtPY = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.forceAtPoint.point.format")));
		this.txtPX.addFocusListener(new SelectTextFocusListener(this.txtPX));
		this.txtPY.addFocusListener(new SelectTextFocusListener(this.txtPY));
		this.txtPX.setColumns(7);
		this.txtPY.setColumns(7);
		this.txtPX.setValue(0.0);
		this.txtPY.setValue(0.0);
		
		JLabel lblForce = new JLabel(Messages.getString("panel.forceAtPoint.force"), Icons.INFO, JLabel.LEFT);
		lblForce.setToolTipText(MessageFormat.format(Messages.getString("panel.forceAtPoint.force.tooltip"), Messages.getString("unit.force")));
		JLabel lblFX = new JLabel(Messages.getString("x"));
		JLabel lblFY = new JLabel(Messages.getString("y"));
		
		this.txtFX = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.forceAtPoint.force.format")));
		this.txtFY = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.forceAtPoint.force.format")));
		this.txtFX.addFocusListener(new SelectTextFocusListener(this.txtFX));
		this.txtFY.addFocusListener(new SelectTextFocusListener(this.txtFY));
		this.txtFX.setColumns(7);
		this.txtFY.setColumns(7);
		this.txtFX.setValue(0.0);
		this.txtFY.setValue(0.0);
		
		// create some dummy panels to create the layout we want
		
		JLabel lblFiller1 = new JLabel();
		JLabel lblFiller2 = new JLabel();
		
		// setup the force at point panel
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblForce)
						.addComponent(lblFiller1)
						.addComponent(lblPoint)
						.addComponent(lblFiller2))
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtFX)
								.addComponent(lblFX))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtFY)
								.addComponent(lblFY))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtPX)
								.addComponent(lblPX))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtPY)
								.addComponent(lblPY))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblForce)
						.addComponent(this.txtFX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblFX))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblFiller1)
						.addComponent(this.txtFY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblFY))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblPoint)
						.addComponent(this.txtPX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPX))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblFiller2)
						.addComponent(this.txtPY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPY)));
	}
	
	/**
	 * Returns the force given by the user.
	 * @return Vector2
	 */
	public Vector2 getForce() {
		Vector2 f = new Vector2(
				ControlUtilities.getDoubleValue(this.txtFX),
				ControlUtilities.getDoubleValue(this.txtFY));
		return f;
	}
	
	/**
	 * Returns the point given by the user.
	 * @return Vector2
	 */
	public Vector2 getPoint() {
		Vector2 p = new Vector2(
				ControlUtilities.getDoubleValue(this.txtPX),
				ControlUtilities.getDoubleValue(this.txtPY));
		return p;
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
