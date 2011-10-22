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
package org.dyn4j.sandbox.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;

import org.dyn4j.sandbox.panels.ColorPanel;
import org.dyn4j.sandbox.utilities.ControlUtilities;

/**
 * Dialog used to edit a color.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class ColorDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -848124582932087026L;
	
	/** True if the user canceled or closed the dialog */
	private boolean canceled = true;
	
	/** The panel to configure the color */
	private ColorPanel pnlColor;
	
	/** The cancel button */
	private JButton btnCancel;
	
	/** The select/accept button */
	private JButton btnSelect;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 * @param initialColor the initial color
	 * @param alpha true if the alpha slider should be shown
	 */
	private ColorDialog(Window owner, Color initialColor, boolean alpha) {
		super(owner, "Select Color", ModalityType.APPLICATION_MODAL);
		
		this.pnlColor = new ColorPanel(initialColor, alpha);
		
		this.btnCancel = new JButton("Cancel");
		this.btnCancel.setActionCommand("cancel");
		this.btnCancel.addActionListener(this);
		
		this.btnSelect = new JButton("Select");
		this.btnSelect.setActionCommand("select");
		this.btnSelect.addActionListener(this);
		
		Container container = this.getContentPane();
		GroupLayout layout = new GroupLayout(container);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.pnlColor)
				.addGroup(layout.createSequentialGroup()
						.addComponent(this.btnCancel)
						.addComponent(this.btnSelect)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.pnlColor)
				.addGroup(layout.createParallelGroup()
						.addComponent(this.btnCancel)
						.addComponent(this.btnSelect)));
		
		this.pack();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("cancel".equals(command)) {
			// just close the dialog
			this.canceled = true;
			this.setVisible(false);
		} else if ("select".equals(command)) {
			// set the canceled flag
			this.canceled = false;
			this.setVisible(false);
		}
	}
	
	/**
	 * Shows a color dialog using the initial color and returns a new color if it was changed
	 * and not canceled.
	 * @param parent the dialog owner
	 * @param initialColor the initial color
	 * @param alpha true if the alpha slider should be shown
	 * @return Color null if the dialog was closed or canceled
	 */
	public static final Color show(Component parent, Color initialColor, boolean alpha) {
		Window owner = ControlUtilities.getParentWindow(parent);
		ColorDialog dialog = new ColorDialog(owner, initialColor, alpha);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		if (!dialog.canceled) {
			return dialog.pnlColor.getColor();
		}
		
		return null;
	}
}
