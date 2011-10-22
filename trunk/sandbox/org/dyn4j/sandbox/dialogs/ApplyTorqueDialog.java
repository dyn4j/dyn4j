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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.dyn4j.sandbox.controls.BottomButtonPanel;
import org.dyn4j.sandbox.panels.TorquePanel;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Dialog used to accept a torque input from the user.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class ApplyTorqueDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -4163908262191446380L;

	/** True if the user canceled or closed the dialog */
	private boolean canceled = true;

	/** The panel to accept the torque input */
	private TorquePanel torquePanel;
	
	/** The cancel button */
	private JButton btnCancel;
	
	/** The apply/accept button */
	private JButton btnApply;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 */
	private ApplyTorqueDialog(Window owner) {
		super(owner, "Apply Torque", ModalityType.APPLICATION_MODAL);
		
		this.setIconImage(Icons.TORQUE.getImage());
		this.torquePanel = new TorquePanel();
		
		this.btnCancel = new JButton("Cancel");
		this.btnCancel.setActionCommand("cancel");
		this.btnCancel.addActionListener(this);
		
		this.btnApply = new JButton("Apply");
		this.btnApply.setActionCommand("apply");
		this.btnApply.addActionListener(this);
		
		JPanel pnlButtons = new BottomButtonPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlButtons.add(this.btnCancel);
		pnlButtons.add(this.btnApply);

		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(this.torquePanel, BorderLayout.CENTER);
		container.add(pnlButtons, BorderLayout.PAGE_END);
		
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
		} else if ("apply".equals(command)) {
			// set the canceled flag
			this.canceled = false;
			this.setVisible(false);
		}
	}
	
	/**
	 * Shows a dialog used to accept input for applying a torque to a body.
	 * <p>
	 * Returns zero if the dialog is closed or canceled.
	 * @param owner the dialog owner
	 * @return double the torque to apply
	 */
	public static final double show(Window owner) {
		ApplyTorqueDialog atd = new ApplyTorqueDialog(owner);
		atd.setLocationRelativeTo(owner);
		atd.setVisible(true);
		
		if (!atd.canceled) {
			double t = atd.torquePanel.getTorque();
			return t;
		}
		
		return 0.0;
	}
}
