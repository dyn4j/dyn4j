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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.panels.FixturePanel;
import org.dyn4j.sandbox.panels.TransformPanel;

/**
 * Dialog to create a new body with an initial fixture/shape.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class EditFixtureDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -1809110047704548125L;

	/** The dialog canceled flag */
	private boolean canceled = true;
	
	/** The fixture config panel */
	private FixturePanel pnlFixture;
	
	/** The transform config panel */
	private TransformPanel pnlTransform;
	
	/** The fixture used during configuration */
	private BodyFixture fixture;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 * @param icon the icon image
	 * @param title the dialog title
	 * @param fixture the fixture being edited
	 */
	private EditFixtureDialog(Window owner, Image icon, String title, BodyFixture fixture) {
		super(owner, title, ModalityType.APPLICATION_MODAL);
		
		if (icon != null) {
			this.setIconImage(icon);
		}
		
		// copy over all the settings
		this.fixture = new BodyFixture(fixture.getShape());
		this.fixture.setUserData(fixture.getUserData());
		this.fixture.setDensity(fixture.getDensity());
		this.fixture.setFilter(fixture.getFilter());
		this.fixture.setFriction(fixture.getFriction());
		this.fixture.setRestitution(fixture.getRestitution());
		this.fixture.setSensor(fixture.isSensor());
		
		Container container = this.getContentPane();
		
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		// create a text pane for the local transform tab
		JTextPane lblText = new JTextPane();
		lblText = new JTextPane();
		lblText.setContentType("text/html");
		lblText.setText(
				"<html>The local transform is used to move and rotate a fixture within " +
				"body coordinates, i.e. relative to the body's center of mass.  Unlike " +
				"the transform on the body, this transform is applied directly to the fixture's " +
				"shape data and therefore not 'saved' directly.</html>");
		lblText.setEditable(false);
		lblText.setPreferredSize(new Dimension(350, 120));
		
		JTabbedPane tabs = new JTabbedPane();
		
		this.pnlFixture = new FixturePanel(this, this.fixture);
		this.pnlTransform = new TransformPanel(lblText);
		
		tabs.addTab("Fixture", this.pnlFixture);
		tabs.addTab("Local Transform", this.pnlTransform);
		
		JButton btnCancel = new JButton("Cancel");
		JButton btnCreate = new JButton("Save");
		btnCreate.setActionCommand("save");
		btnCancel.setActionCommand("cancel");
		btnCreate.addActionListener(this);
		btnCancel.addActionListener(this);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(tabs)
						.addGroup(layout.createSequentialGroup()
								.addComponent(btnCancel)
								.addComponent(btnCreate))));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(tabs)
				.addGroup(layout.createParallelGroup()
						.addComponent(btnCancel)
						.addComponent(btnCreate)));
		
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
			// check the fixture input
			if (this.pnlFixture.isValidInput()) {
				// check the transform input
				if (this.pnlTransform.isValidInput()) {
					// if its valid then close the dialog
					this.canceled = false;
					this.setVisible(false);
				} else {
					this.pnlTransform.showInvalidInputMessage(this);
				}
			} else {
				this.pnlFixture.showInvalidInputMessage(this);
			}
		}
	}
	
	/**
	 * Shows an Edit Fixture Dialog..
	 * @param owner the dialog owner
	 * @param icon the icon image
	 * @param title the dialog title
	 * @param body the body the fixture belongs to
	 * @param fixture the fixture to edit
	 */
	public static final void show(Window owner, Image icon, String title, SandboxBody body, BodyFixture fixture) {
		EditFixtureDialog dialog = new EditFixtureDialog(owner, icon, title, fixture);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		// control returns to this method when the dialog is closed
		
		// check the canceled flag
		if (!dialog.canceled) {
			// get the fixture
			BodyFixture newFixture = dialog.fixture;
			
			// get the shape
			Convex convex = fixture.getShape();
			
			// apply any local transform
			Vector2 tx = dialog.pnlTransform.getTranslation();
			double a = dialog.pnlTransform.getRotation();
			if (!tx.isZero()) {
				convex.translate(tx);
			}
			if (a != 0.0) {
				convex.rotate(a);
			}
			
			// copy the other properties over
			fixture.setFilter(newFixture.getFilter());
			fixture.setFriction(newFixture.getFriction());
			fixture.setRestitution(newFixture.getRestitution());
			fixture.setSensor(newFixture.isSensor());
			fixture.setDensity(newFixture.getDensity());
			// check if the mass is set explicitly or not
			if (!body.isMassExplicit()) {
				// recompute the mass
				// we must do this if the density or position of the fixture has changed
				body.setMass(body.getMass().getType());
			}
		}
	}
}
