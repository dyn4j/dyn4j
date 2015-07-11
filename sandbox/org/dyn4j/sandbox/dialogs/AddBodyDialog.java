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
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.dyn4j.geometry.MassType;
import org.dyn4j.sandbox.Preferences;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.controls.BottomButtonPanel;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.panels.BodyPanel;
import org.dyn4j.sandbox.panels.TransformPanel;
import org.dyn4j.sandbox.resources.Messages;
import org.dyn4j.sandbox.utilities.ColorUtilities;

/**
 * Dialog to add a new body without any fixtures.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class AddBodyDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -1809110047704548125L;
	
	/** Used for automatic naming */
	private static int N = 1;

	/** The dialog canceled flag */
	private boolean canceled = true;
	
	/** The body config panel */
	private BodyPanel pnlBody;
	
	/** The transform config panel */
	private TransformPanel pnlTransform;
	
	/** The body using in configuration */
	private SandboxBody body;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 */
	private AddBodyDialog(Window owner) {
		super(owner, Messages.getString("dialog.body.add.title"), ModalityType.APPLICATION_MODAL);
		
		this.body = new SandboxBody();
		this.body.getMass().setType(MassType.NORMAL);
		this.body.setName(MessageFormat.format(Messages.getString("dialog.body.add.name.default"), N));
		
		// check if we need to randomize colors
		if (Preferences.isBodyColorRandom()) {
			Color fc = ColorUtilities.getRandomColor(0.5f, 1.0f);
			Color oc = fc.darker();
			this.body.setOutlineColor(ColorUtilities.convertColor(oc));
			this.body.setFillColor(ColorUtilities.convertColor(fc));
		}
		
		JTabbedPane tabs = new JTabbedPane();
		
		this.pnlBody = new BodyPanel(this.body);
		this.pnlTransform = new TransformPanel();
		
		tabs.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
		tabs.addTab(Messages.getString("dialog.body.tab.body"), this.pnlBody);
		tabs.addTab(Messages.getString("dialog.body.tab.transform"), this.pnlTransform);
		
		JButton btnCancel = new JButton(Messages.getString("button.cancel"));
		JButton btnAdd = new JButton(Messages.getString("button.add"));
		btnCancel.setActionCommand("cancel");
		btnAdd.setActionCommand("add");
		btnCancel.addActionListener(this);
		btnAdd.addActionListener(this);
		
		JPanel pnlButtons = new BottomButtonPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlButtons.add(btnCancel);
		pnlButtons.add(btnAdd);

		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(tabs, BorderLayout.CENTER);
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
			this.setVisible(false);
			this.canceled = true;
		} else {
			// check the body input
			if (this.pnlBody.isValidInput()) {
				// check the transform input
				if (this.pnlTransform.isValidInput()) {
					// if its valid then close the dialog
					this.canceled = false;
					this.setVisible(false);
				} else {
					this.pnlTransform.showInvalidInputMessage(this);
				}
			} else {
				this.pnlBody.showInvalidInputMessage(this);
			}
		}
	}
	
	/**
	 * Shows an add new body dialog and returns a new body if the user clicked the add button.
	 * <p>
	 * Returns null if the user clicked the cancel button or closed the dialog.
	 * @param owner the dialog owner
	 * @return {@link SandboxBody}
	 */
	public static final SandboxBody show(Window owner) {
		AddBodyDialog dialog = new AddBodyDialog(owner);
		dialog.setLocationRelativeTo(owner);
		dialog.setIconImage(Icons.ADD_BODY.getImage());
		dialog.setVisible(true);
		// control returns to this method when the dialog is closed
		
		// check the canceled flag
		if (!dialog.canceled) {
			// get the body and fixture
			SandboxBody body = dialog.body;
			
			// apply the transform
			body.translate(dialog.pnlTransform.getTranslation());
			body.rotateAboutCenter(dialog.pnlTransform.getRotation());
			
			// increment the body number
			synchronized (AddBodyDialog.class) {
				N++;
			}
			
			// return the body
			return body;
		}
		
		// if it was canceled then return null
		return null;
	}
}
