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
import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.utilities.ControlUtilities;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel used to apply a force to a body.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ForcePanel extends JPanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = 2359226860458900772L;

	/** The x value of the force input */
	private JFormattedTextField txtX;
	
	/** The y value of the force input */
	private JFormattedTextField txtY;
	
	/**
	 * Default constructor.
	 */
	public ForcePanel() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JLabel lblForce = new JLabel("Force", Icons.INFO, JLabel.LEFT);
		lblForce.setToolTipText("The force in Newtons to apply to the center of the body.");
		JLabel lblX = new JLabel("x");
		JLabel lblY = new JLabel("y");
		
		this.txtX = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtY = new JFormattedTextField(new DecimalFormat("0.000"));
		this.txtX.addFocusListener(new SelectTextFocusListener(this.txtX));
		this.txtY.addFocusListener(new SelectTextFocusListener(this.txtY));
		this.txtX.setColumns(7);
		this.txtY.setColumns(7);
		this.txtX.setValue(0.0);
		this.txtY.setValue(0.0);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblForce))
				.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtX)
								.addComponent(lblX))
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtY)
								.addComponent(lblY))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblForce)
						.addGroup(layout.createSequentialGroup()
								.addComponent(this.txtX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(this.txtY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(layout.createSequentialGroup()
								.addComponent(lblX)
								.addComponent(lblY))));
	}
	
	/**
	 * Returns the force given by the user.
	 * @return Vector2
	 */
	public Vector2 getForce() {
		Vector2 f = new Vector2(
				ControlUtilities.getDoubleValue(this.txtX),
				ControlUtilities.getDoubleValue(this.txtY));
		return f;
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
