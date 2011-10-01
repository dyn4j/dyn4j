package org.dyn4j.sandbox.dialogs;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import org.dyn4j.dynamics.Settings;
import org.dyn4j.sandbox.panels.SettingsPanel;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Dialog used to maintain global dynamics settings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SettingsDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -5534026582417596016L;

	/** The dialog canceled flag */
	private boolean canceled = true;
	
	/** The world panel */
	private SettingsPanel pnlSettings;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 * @param settings the current dynamics settings
	 */
	private SettingsDialog(Window owner, Settings settings) {
		super(owner, "Settings", ModalityType.APPLICATION_MODAL);
		
		this.setIconImage(Icons.SETTINGS.getImage());
		
		this.pnlSettings = new SettingsPanel(settings);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Settings", this.pnlSettings);
		
		JButton btnCancel = new JButton("Cancel");
		JButton btnApply = new JButton("Apply");
		btnCancel.setActionCommand("cancel");
		btnApply.setActionCommand("apply");
		btnCancel.addActionListener(this);
		btnApply.addActionListener(this);
		
		Container container = this.getContentPane();
		
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(tabbedPane)
						.addGroup(layout.createSequentialGroup()
								.addComponent(btnCancel)
								.addComponent(btnApply))));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(tabbedPane)
				.addGroup(layout.createParallelGroup()
						.addComponent(btnCancel)
						.addComponent(btnApply)));
		
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
			if (this.pnlSettings.isValidInput()) {
				this.canceled = false;
				this.setVisible(false);
			} else {
				this.pnlSettings.showInvalidInputMessage(this);
			}
		}
	}
	
	/**
	 * Shows an edit settings dialog and sets the properties if the user clicks apply.
	 * @param owner the dialog owner
	 * @param settings the current dynamics settings
	 */
	public static final void show(Window owner, Settings settings) {
		SettingsDialog dialog = new SettingsDialog(owner, settings);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		
		if (!dialog.canceled) {
			// set the settings
			dialog.pnlSettings.setSettings(settings);
		}
	}
}
