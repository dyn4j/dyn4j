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

import org.dyn4j.dynamics.World;
import org.dyn4j.sandbox.controls.BottomButtonPanel;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.panels.WorldPanel;
import org.dyn4j.sandbox.resources.Messages;

/**
 * Dialog used to modify world properties.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class EditWorldDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = 2956890217721148447L;
	
	/** The dialog canceled flag */
	private boolean canceled = true;
	
	/** The world panel */
	private WorldPanel pnlWorld;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 * @param world the current world
	 */
	public EditWorldDialog(Window owner, World world) {
		super(owner, Messages.getString("dialog.world.edit.title"), ModalityType.APPLICATION_MODAL);
		
		this.setIconImage(Icons.EDIT_WORLD.getImage());
		
		this.pnlWorld = new WorldPanel(world);
		this.pnlWorld.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
		
		JButton btnCancel = new JButton(Messages.getString("button.cancel"));
		JButton btnApply = new JButton(Messages.getString("button.apply"));
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
		container.add(this.pnlWorld, BorderLayout.CENTER);
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
	 * Shows an edit world dialog and sets the properties if the user clicks apply.
	 * @param owner the dialog owner
	 * @param world the current world
	 */
	public static final void show(Window owner, World world) {
		EditWorldDialog ewd = new EditWorldDialog(owner, world);
		ewd.setLocationRelativeTo(owner);
		ewd.setVisible(true);
		
		if (!ewd.canceled) {
			synchronized (world) {
				// set the properties
				ewd.pnlWorld.setWorld(world);
			}
		}
	}
}
