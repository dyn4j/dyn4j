package org.dyn4j.sandbox.dialogs;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import org.dyn4j.collision.RectangularBounds;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.panels.RectanglePanel;
import org.dyn4j.sandbox.panels.TransformPanel;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Dialog to create a new body with an initial fixture/shape.
 * @author William Bittle
 * @version 1.0.0
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
		super(owner, "Set Bounds", ModalityType.APPLICATION_MODAL);
		
		this.setIconImage(Icons.SET_BOUNDS.getImage());
		
		Container container = this.getContentPane();
		
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		JTabbedPane tabs = new JTabbedPane();
		
		Rectangle r = new Rectangle(10.0, 10.0);
		Transform t = new Transform();
		if (bounds != null) {
			r = bounds.getBounds();
			t = bounds.getTransform();
		}		
		
		this.pnlRectangle = new RectanglePanel(r);
		this.pnlTransform = new TransformPanel(t);
		
		tabs.addTab("Bounds", this.pnlRectangle);
		tabs.addTab("Transform", this.pnlTransform);
		
		JButton btnCancel = new JButton("Cancel");
		JButton btnCreate = new JButton("Set");
		btnCancel.setActionCommand("cancel");
		btnCreate.setActionCommand("set");
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
