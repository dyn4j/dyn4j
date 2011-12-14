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
import javax.swing.JTabbedPane;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Transform;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.controls.BottomButtonPanel;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.panels.BodyPanel;
import org.dyn4j.sandbox.panels.TransformPanel;
import org.dyn4j.sandbox.resources.Messages;

/**
 * Dialog to create a new body without any fixtures.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class EditBodyDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -1809110047704548125L;

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
	 * @param body the body to edit
	 */
	private EditBodyDialog(Window owner, SandboxBody body) {
		super(owner, Messages.getString("dialog.body.edit.title"), ModalityType.APPLICATION_MODAL);
		
		this.body = new SandboxBody();
		
		// copy over the force/torque
		this.body.apply(body.getAccumulatedForce());
		this.body.apply(body.getAccumulatedTorque());
		
		// add the fixtures to the body copy
		// its possible that the mass will be reset here on
		// each fixture add in the future, so set the mass to
		// the mass of the body after this
		int fSize = body.getFixtureCount();
		for (int i = 0; i < fSize; i++) {
			BodyFixture bf = body.getFixture(i);
			this.body.addFixture(bf);
		}
		
		this.body.setMass(new Mass(body.getMass()));
		
		this.body.setOutlineColor(body.getOutlineColor());
		this.body.setFillColor(body.getFillColor());
		this.body.setActive(body.isActive());
		this.body.setAngularDamping(body.getAngularDamping());
		this.body.setAngularVelocity(body.getAngularVelocity());
		this.body.setAsleep(body.isAsleep());
		this.body.setAutoSleepingEnabled(body.isAutoSleepingEnabled());
		this.body.setBullet(body.isBullet());
		this.body.setGravityScale(body.getGravityScale());
		this.body.setLinearDamping(body.getLinearDamping());
		this.body.setName(body.getName());
		this.body.setVelocity(body.getVelocity().copy());
		this.body.setMassExplicit(body.isMassExplicit());
		
		JTabbedPane tabs = new JTabbedPane();
		
		this.pnlBody = new BodyPanel(this.body);
		this.pnlTransform = new TransformPanel(body.getTransform(), null);
		
		tabs.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
		tabs.addTab(Messages.getString("dialog.body.tab.body"), this.pnlBody);
		tabs.addTab(Messages.getString("dialog.body.tab.transform"), this.pnlTransform);
		
		JButton btnCancel = new JButton(Messages.getString("button.cancel"));
		JButton btnSave = new JButton(Messages.getString("button.save"));
		btnSave.setActionCommand("save");
		btnCancel.setActionCommand("cancel");
		btnSave.addActionListener(this);
		btnCancel.addActionListener(this);
		
		JPanel pnlButtons = new BottomButtonPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlButtons.add(btnCancel);
		pnlButtons.add(btnSave);

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
	 * Shows an Edit Body Dialog using the values in the current body.
	 * @param owner the dialog owner
	 * @param body the body to edit
	 */
	public static final void show(Window owner, SandboxBody body) {
		EditBodyDialog dialog = new EditBodyDialog(owner, body);
		dialog.setLocationRelativeTo(owner);
		dialog.setIconImage(Icons.EDIT_BODY.getImage());
		dialog.setVisible(true);
		// control returns to this method when the dialog is closed
		
		// check the canceled flag
		if (!dialog.canceled) {
			// get the body and fixture
			SandboxBody bodyChanges = dialog.body;
			
			body.setOutlineColor(bodyChanges.getOutlineColor());
			body.setFillColor(bodyChanges.getFillColor());
			body.setActive(bodyChanges.isActive());
			body.setAngularDamping(bodyChanges.getAngularDamping());
			body.setAngularVelocity(bodyChanges.getAngularVelocity());
			body.setAsleep(bodyChanges.isAsleep());
			body.setAutoSleepingEnabled(bodyChanges.isAutoSleepingEnabled());
			body.setBullet(bodyChanges.isBullet());
			body.setGravityScale(bodyChanges.getGravityScale());
			body.setLinearDamping(bodyChanges.getLinearDamping());
			body.setMass(new Mass(bodyChanges.getMass()));
			body.setName(bodyChanges.getName());
			body.setVelocity(bodyChanges.getVelocity().copy());
			body.setMassExplicit(bodyChanges.isMassExplicit());
			
			// apply the transform
			Transform transform = body.getTransform();
			
			transform.setRotation(dialog.pnlTransform.getRotation());
			transform.setTranslation(dialog.pnlTransform.getTranslation());
		}
	}
}
