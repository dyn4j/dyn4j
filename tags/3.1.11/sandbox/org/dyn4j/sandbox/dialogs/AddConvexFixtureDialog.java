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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.controls.BottomButtonPanel;
import org.dyn4j.sandbox.panels.ConvexShapePanel;
import org.dyn4j.sandbox.panels.FixturePanel;
import org.dyn4j.sandbox.panels.TransformPanel;
import org.dyn4j.sandbox.resources.Messages;

/**
 * Dialog to add a new fixture to an existing body.
 * @author William Bittle
 * @version 1.0.4
 * @since 1.0.0
 */
public class AddConvexFixtureDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -1809110047704548125L;

	/** Used for automatic naming */
	protected static int N = 1;

	/** The dialog canceled flag */
	private boolean canceled = true;
	
	/** The shape config panel */
	private ConvexShapePanel pnlShape;
	
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
	 * @param shapePanel the shape panel
	 */
	private AddConvexFixtureDialog(Window owner, Image icon, ConvexShapePanel shapePanel) {
		super(owner, Messages.getString("dialog.fixture.add.title"), ModalityType.APPLICATION_MODAL);
		
		if (icon != null) {
			this.setIconImage(icon);
		}
		
		this.pnlShape = shapePanel;
		
		this.fixture = new BodyFixture(this.pnlShape.getDefaultShape());
		this.fixture.setUserData(MessageFormat.format(Messages.getString("dialog.fixture.add.name.default"), N));
		
		// create a text pane for the local transform tab
		JTextPane lblText = new JTextPane();
		lblText = new JTextPane();
		lblText.setContentType("text/html");
		lblText.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		lblText.setText(Messages.getString("label.transform.warning"));
		lblText.setEditable(false);
		lblText.setPreferredSize(new Dimension(350, 120));
		
		JTabbedPane tabs = new JTabbedPane();
		
		this.pnlFixture = new FixturePanel(this.fixture);
		this.pnlTransform = new TransformPanel(lblText);
		
		tabs.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
		tabs.addTab(Messages.getString("dialog.fixture.tab.shape"), this.pnlShape);
		tabs.addTab(Messages.getString("dialog.fixture.tab.fixture"), this.pnlFixture);
		tabs.addTab(Messages.getString("dialog.fixture.tab.transform"), this.pnlTransform);
		
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
			// check the shape panel's input
			if (this.pnlShape.isValidInput()) {
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
			} else {
				// if its not valid then show an error message
				this.pnlShape.showInvalidInputMessage(this);
			}
		}
	}
	
	/**
	 * Shows an add new fixture dialog and returns the new fixture if the user clicked the add button.
	 * <p>
	 * Returns null if the user canceled or closed the dialog.
	 * @param owner the dialog owner
	 * @param icon the icon image
	 * @param shapePanel the shape panel to use
	 * @return {@link SandboxBody}
	 */
	public static final BodyFixture show(Window owner, Image icon, ConvexShapePanel shapePanel) {
		AddConvexFixtureDialog dialog = new AddConvexFixtureDialog(owner, icon, shapePanel);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		// control returns to this method when the dialog is closed
		
		// check the canceled flag
		if (!dialog.canceled) {
			// get the fixture
			BodyFixture fixture = dialog.fixture;
			
			// get the shape
			Convex convex = dialog.pnlShape.getShape();
			
			// apply any local transform
			Vector2 tx = dialog.pnlTransform.getTranslation();
			double a = dialog.pnlTransform.getRotation();
			if (!tx.isZero()) {
				convex.translate(tx);
			}
			if (a != 0.0) {
				convex.rotateAboutCenter(a);
			}
			
			// create the fixture so we can set the shape
			BodyFixture newFixture = new BodyFixture(convex);
			// copy the other properties over
			newFixture.setUserData(fixture.getUserData());
			newFixture.setDensity(fixture.getDensity());
			newFixture.setFilter(fixture.getFilter());
			newFixture.setFriction(fixture.getFriction());
			newFixture.setRestitution(fixture.getRestitution());
			newFixture.setSensor(fixture.isSensor());
			
			// increment the fixture number
			synchronized (AddConvexFixtureDialog.class) {
				N++;
			}
			
			// return the body
			return newFixture;
		}
		
		// if it was canceled then return null
		return null;
	}
}
