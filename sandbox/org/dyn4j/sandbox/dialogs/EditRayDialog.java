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

import org.dyn4j.sandbox.SandboxRay;
import org.dyn4j.sandbox.controls.BottomButtonPanel;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.panels.RayPanel;
import org.dyn4j.sandbox.resources.Messages;

/**
 * Dialog used to edit a ray to the world.
 * @author William Bittle
 * @version 1.0.7
 * @since 1.0.1
 */
public class EditRayDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = 2651817329368510967L;

	/** True if the user canceled or closed the dialog */
	private boolean canceled = true;

	/** The panel to accept the ray input */
	private RayPanel rayPanel;
	
	/** The cancel button */
	private JButton btnCancel;
	
	/** The save button */
	private JButton btnSave;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 * @param ray the ray to edit
	 */
	private EditRayDialog(Window owner, SandboxRay ray) {
		super(owner, Messages.getString("dialog.ray.edit.title"), ModalityType.APPLICATION_MODAL);
		
		this.setIconImage(Icons.FORCE.getImage());
		this.rayPanel = new RayPanel(ray);
		
		this.btnCancel = new JButton(Messages.getString("button.cancel"));
		this.btnCancel.setActionCommand("cancel");
		this.btnCancel.addActionListener(this);
		
		this.btnSave = new JButton(Messages.getString("button.save"));
		this.btnSave.setActionCommand("save");
		this.btnSave.addActionListener(this);
		
		JPanel pnlButtons = new BottomButtonPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlButtons.add(this.btnCancel);
		pnlButtons.add(this.btnSave);

		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(this.rayPanel, BorderLayout.CENTER);
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
		} else if ("save".equals(command)) {
			// check the input
			if (this.rayPanel.isValidInput()) {
				// set the canceled flag
				this.canceled = false;
				this.setVisible(false);
			} else {
				this.rayPanel.showInvalidInputMessage(this);
			}
		}
	}
	
	/**
	 * Shows a dialog used to accept input for editing a ray.
	 * <p>
	 * Returns null if the dialog is closed or canceled.
	 * @param owner the dialog owner
	 * @param ray the ray to edit
	 * @return {@link SandboxRay} the new ray
	 */
	public static final SandboxRay show(Window owner, SandboxRay ray) {
		EditRayDialog ard = new EditRayDialog(owner, ray);
		ard.setLocationRelativeTo(owner);
		ard.setVisible(true);
		
		if (!ard.canceled) {
			return ard.rayPanel.getRay();
		}
		
		return ray;
	}
}
