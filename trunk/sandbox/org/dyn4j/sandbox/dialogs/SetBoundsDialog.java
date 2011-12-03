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

import org.dyn4j.collision.RectangularBounds;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.Resources;
import org.dyn4j.sandbox.controls.BottomButtonPanel;
import org.dyn4j.sandbox.panels.RectanglePanel;
import org.dyn4j.sandbox.panels.TransformPanel;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Dialog to create a new body with an initial fixture/shape.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class SetBoundsDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -1809110047704548125L;

	/** The dialog canceled flag */
	private boolean canceled = true;
	
	/** The rectangle config panel */
	private RectanglePanel pnlRectangle;
	
	/** The transform config panel */
	private TransformPanel pnlTransform;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 * @param bounds the current bounds object
	 */
	private SetBoundsDialog(Window owner, RectangularBounds bounds) {
		super(owner, Resources.getString("dialog.bounds.set.title"), ModalityType.APPLICATION_MODAL);
		
		this.setIconImage(Icons.SET_BOUNDS.getImage());
		
		JTabbedPane tabs = new JTabbedPane();
		
		Rectangle r = new Rectangle(10.0, 10.0);
		Transform t = new Transform();
		if (bounds != null) {
			r = bounds.getBounds();
			t = bounds.getTransform();
		}		
		
		this.pnlRectangle = new RectanglePanel(r);
		this.pnlTransform = new TransformPanel(t);
		
		tabs.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
		tabs.addTab(Resources.getString("dialog.bounds.tab.bounds"), this.pnlRectangle);
		tabs.addTab(Resources.getString("dialog.bounds.tab.transform"), this.pnlTransform);
		
		JButton btnCancel = new JButton(Resources.getString("button.cancel"));
		JButton btnCreate = new JButton(Resources.getString("button.set"));
		btnCancel.setActionCommand("cancel");
		btnCreate.setActionCommand("set");
		btnCreate.addActionListener(this);
		btnCancel.addActionListener(this);
		
		JPanel pnlButtons = new BottomButtonPanel();
		pnlButtons.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlButtons.add(btnCancel);
		pnlButtons.add(btnCreate);

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
			if (this.pnlRectangle.isValidInput()) {
				// check the transform input
				if (this.pnlTransform.isValidInput()) {
					// if its valid then close the dialog
					this.canceled = false;
					this.setVisible(false);
				} else {
					this.pnlTransform.showInvalidInputMessage(this);
				}
			} else {
				// if its not valid then show an error message
				this.pnlRectangle.showInvalidInputMessage(this);
			}
		}
	}
	
	/**
	 * Shows a set bounds dialog and returns a new bounds object if the user clicks the set button.
	 * @param owner the dialog owner
	 * @param bounds the current bounds object
	 * @return RectangularBounds
	 */
	public static final RectangularBounds show(Window owner, RectangularBounds bounds) {
		SetBoundsDialog dialog = new SetBoundsDialog(owner, bounds);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		// control returns to this method when the dialog is closed
		
		// check the canceled flag
		if (!dialog.canceled) {
			// get the shape
			Convex convex = dialog.pnlRectangle.getShape();
			
			// apply any local transform
			Vector2 tx = dialog.pnlTransform.getTranslation();
			double a = dialog.pnlTransform.getRotation();
			
			RectangularBounds b = new RectangularBounds((Rectangle)convex);
			b.translate(tx);
			b.rotate(a, tx);
			
			// return the bounds
			return b;
		}
		
		// if it was canceled then return null
		return null;
	}
}
