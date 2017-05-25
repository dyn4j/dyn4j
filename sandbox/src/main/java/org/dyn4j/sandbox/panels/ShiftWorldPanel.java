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

import java.awt.Dimension;
import java.awt.Window;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.listeners.SelectTextFocusListener;
import org.dyn4j.sandbox.resources.Messages;
import org.dyn4j.sandbox.utilities.ControlUtilities;

/**
 * Panel used to shift the world coordinates.
 * @author William Bittle
 * @version 1.0.2
 * @since 1.0.2
 */
public class ShiftWorldPanel extends JPanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = 2359226860458900772L;

	/** The x value of the force input */
	private JFormattedTextField txtX;
	
	/** The y value of the force input */
	private JFormattedTextField txtY;
	
	/**
	 * Default constructor.
	 */
	public ShiftWorldPanel() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		JTextPane lblText = new JTextPane();
		lblText.setContentType("text/html");
		lblText.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		lblText.setText(Messages.getString("panel.shift.detail"));
		lblText.setEditable(false);
		lblText.setPreferredSize(new Dimension(350, 100));
		
		JLabel lblShift = new JLabel(Messages.getString("panel.shift"), Icons.INFO, JLabel.LEFT);
		lblShift.setToolTipText(MessageFormat.format(Messages.getString("panel.shift.tooltip"), Messages.getString("unit.length")));
		JLabel lblX = new JLabel(Messages.getString("x"));
		JLabel lblY = new JLabel(Messages.getString("y"));
		
		this.txtX = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.shift.format")));
		this.txtY = new JFormattedTextField(new DecimalFormat(Messages.getString("panel.shift.format")));
		this.txtX.addFocusListener(new SelectTextFocusListener(this.txtX));
		this.txtY.addFocusListener(new SelectTextFocusListener(this.txtY));
		this.txtX.setColumns(7);
		this.txtY.setColumns(7);
		this.txtX.setValue(0.0);
		this.txtY.setValue(0.0);
		
		JLabel lblFiller = new JLabel();
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(lblText)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
							.addComponent(lblShift)
							.addComponent(lblFiller))
					.addGroup(layout.createParallelGroup()
							.addComponent(this.txtX)
							.addComponent(this.txtY))
					.addGroup(layout.createParallelGroup()
							.addComponent(lblX)
							.addComponent(lblY))));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(lblText)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblShift)
						.addComponent(this.txtX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblX))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblFiller)
						.addComponent(this.txtY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblY)));
	}
	
	/**
	 * Returns the shift values.
	 * @return Vector2
	 */
	public Vector2 getShift() {
		Vector2 s = new Vector2(
				ControlUtilities.getDoubleValue(this.txtX),
				ControlUtilities.getDoubleValue(this.txtY));
		return s;
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
