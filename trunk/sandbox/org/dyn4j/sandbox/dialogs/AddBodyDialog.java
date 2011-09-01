package org.dyn4j.sandbox.dialogs;

import java.awt.Color;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import org.dyn4j.geometry.Mass;
import org.dyn4j.sandbox.ApplicationSettings;
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.panels.BodyPanel;
import org.dyn4j.sandbox.panels.TransformPanel;
import org.dyn4j.sandbox.utilities.ColorUtilities;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Dialog to add a new body without any fixtures.
 * @author William Bittle
 * @version 1.0.0
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
	 * @param title the dialog title
	 */
	public AddBodyDialog(Window owner, String title) {
		super(owner, title, ModalityType.APPLICATION_MODAL);
		
		body = new SandboxBody();
		body.getMass().setType(Mass.Type.NORMAL);
		body.setAutoSleepingEnabled(false);
		body.setName("Body" + N);
		
		// check if we need to randomize colors
		if (ApplicationSettings.isColorRandom()) {
			Color fc = ColorUtilities.getRandomColor(0.5f, 1.0f);
			Color oc = fc.darker();
			body.setOutlineColor(ColorUtilities.convertColor(oc));
			body.setFillColor(ColorUtilities.convertColor(fc));
		}
		
		Container container = this.getContentPane();
		
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		JTabbedPane tabs = new JTabbedPane();
		
		pnlBody = new BodyPanel(this, this.body);
		pnlTransform = new TransformPanel();
		
		tabs.addTab("Body", this.pnlBody);
		tabs.addTab("Transform", this.pnlTransform);
		
		JButton btnCancel = new JButton("Cancel");
		JButton btnAdd = new JButton("Add");
		btnCancel.setActionCommand("cancel");
		btnAdd.setActionCommand("add");
		btnCancel.addActionListener(this);
		btnAdd.addActionListener(this);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(tabs)
						.addGroup(layout.createSequentialGroup()
								.addComponent(btnCancel)
								.addComponent(btnAdd))));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(tabs)
				.addGroup(layout.createParallelGroup()
						.addComponent(btnCancel)
						.addComponent(btnAdd)));
		
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
	 * @param title the dialog title
	 * @return {@link SandboxBody}
	 */
	public static final SandboxBody show(Window owner, String title) {
		AddBodyDialog dialog = new AddBodyDialog(owner, title);
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
