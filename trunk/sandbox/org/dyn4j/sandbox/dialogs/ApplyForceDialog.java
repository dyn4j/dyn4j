package org.dyn4j.sandbox.dialogs;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;

import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.panels.ForcePanel;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Dialog used to accept a force input from the user.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ApplyForceDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -4163908262191446380L;

	/** True if the user canceled or closed the dialog */
	private boolean canceled = true;

	/** The panel to accept the force input */
	private ForcePanel forcePanel;
	
	/** The cancel button */
	private JButton btnCancel;
	
	/** The apply/accept button */
	private JButton btnApply;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 */
	private ApplyForceDialog(Window owner) {
		super(owner, "Apply Force", ModalityType.APPLICATION_MODAL);
		
		this.setIconImage(Icons.FORCE.getImage());
		this.forcePanel = new ForcePanel();
		
		this.btnCancel = new JButton("Cancel");
		this.btnCancel.setActionCommand("cancel");
		this.btnCancel.addActionListener(this);
		
		this.btnApply = new JButton("Apply");
		this.btnApply.setActionCommand("apply");
		this.btnApply.addActionListener(this);
		
		Container container = this.getContentPane();
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup()
				.addComponent(this.forcePanel)
				.addGroup(layout.createSequentialGroup()
						.addComponent(this.btnCancel)
						.addComponent(this.btnApply)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this.forcePanel)
				.addGroup(layout.createParallelGroup()
						.addComponent(this.btnCancel)
						.addComponent(this.btnApply)));
		
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
		} else if ("apply".equals(command)) {
			// set the canceled flag
			this.canceled = false;
			this.setVisible(false);
		}
	}
	
	/**
	 * Shows a dialog used to accept input for applying a force to a body.
	 * <p>
	 * Returns null if the dialog is closed or canceled.
	 * @param owner the dialog owner
	 * @return Vector2
	 */
	public static final Vector2 show(Window owner) {
		ApplyForceDialog afd = new ApplyForceDialog(owner);
		afd.setLocationRelativeTo(owner);
		afd.setVisible(true);
		
		if (!afd.canceled) {
			return afd.forcePanel.getForce();
		}
		
		return null;
	}
}
