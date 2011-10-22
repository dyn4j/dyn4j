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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.dyn4j.sandbox.controls.BottomButtonPanel;
import org.dyn4j.sandbox.panels.PreferencesPanel;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Dialog used to configure the application preferences.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class PreferencesDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -1615642743117999224L;

	/** The dialog canceled flag */
	private boolean canceled = true;
	
	/** The preferences panel */
	private PreferencesPanel pnlPreferences;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 */
	private PreferencesDialog(Window owner) {
		super(owner, "Preferences", ModalityType.APPLICATION_MODAL);
		
		this.setIconImage(Icons.PREFERENCES.getImage());
		
		this.pnlPreferences = new PreferencesPanel();
		this.pnlPreferences.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
		
		JButton btnCancel = new JButton("Cancel");
		JButton btnApply = new JButton("Apply");
		btnCancel.setActionCommand("cancel");
		btnApply.setActionCommand("apply");
		btnCancel.addActionListener(this);
		btnApply.addActionListener(this);
		
		JPanel pnlButtons = new BottomButtonPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlButtons.add(btnCancel);
		pnlButtons.add(btnApply);

		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(this.pnlPreferences, BorderLayout.CENTER);
		container.add(pnlButtons, BorderLayout.PAGE_END);
		
		this.pack();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		// check the action command
		if ("cancel".equals(event.getActionCommand())) {
			// if its canceled then set the canceled flag and
			// close the dialog
			this.canceled = true;
			this.setVisible(false);
		} else {
			this.canceled = false;
			this.setVisible(false);
		}
	}
	
	/**
	 * Shows an edit preferences dialog and sets the properties if the user clicks apply.
	 * @param owner the dialog owner
	 */
	public static final void show(Window owner) {
		PreferencesDialog dialog = new PreferencesDialog(owner);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		if (!dialog.canceled) {
			// set the settings
			dialog.pnlPreferences.setPreferences();
		}
	}
}
