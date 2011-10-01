package org.dyn4j.sandbox.panels;

import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.dyn4j.sandbox.ContactCounter;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Panel to show the number of contacts and their respective types.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ContactPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = 1878435102775553243L;
	
	/** The contact counter used by the current world object */
	private ContactCounter counter;
	
	/** The text box to store the total number of contacts */
	private JFormattedTextField txtTotalContacts;
	
	/** The text box to store the number of sensed contacts */
	private JFormattedTextField txtSensedContacts;
	
	/** The text box to store the number of added contacts */
	private JFormattedTextField txtAddedContacts;
	
	/** The text box to store the number of persisted contacts */
	private JFormattedTextField txtPersistedContacts;
	
	/** The text box to store the number of removed contacts */
	private JFormattedTextField txtRemovedContacts;
	
	/** The text box to store the number of solved contacts */
	private JFormattedTextField txtSolvedContacts;
	
	/**
	 * Full constructor.
	 * @param counter the contact counter object used by the current world
	 */
	public ContactPanel(ContactCounter counter) {
		this.counter = counter;
		
		JLabel lblTotalContacts = new JLabel("Total", Icons.INFO, JLabel.LEFT);
		JLabel lblSensedContacts = new JLabel("Sensed", Icons.INFO, JLabel.LEFT);
		JLabel lblAddedContacts = new JLabel("Added", Icons.INFO, JLabel.LEFT);
		JLabel lblPersistedContacts = new JLabel("Persisted", Icons.INFO, JLabel.LEFT);
		JLabel lblRemovedContacts = new JLabel("Removed", Icons.INFO, JLabel.LEFT);
		JLabel lblSolvedContacts = new JLabel("Solved", Icons.INFO, JLabel.LEFT);
		
		lblTotalContacts.setToolTipText("<html>Added + Persisted + Sensed</html>");
		lblSensedContacts.setToolTipText("<html>Contacts produced by fixtures flagged as sensors.</html>");
		lblAddedContacts.setToolTipText("<html>New contacts created during this timestep.</html>");
		lblPersistedContacts.setToolTipText("<html>Contacts that have been keep from the previous timestep.</html>");
		lblRemovedContacts.setToolTipText("<html>Contacts that are not valid from the last timestep.</html>");
		lblSolvedContacts.setToolTipText("<html>Added + Persisted</html>");
		
		this.txtTotalContacts = new JFormattedTextField(new DecimalFormat("0"));
		this.txtSensedContacts = new JFormattedTextField(new DecimalFormat("0"));
		this.txtAddedContacts = new JFormattedTextField(new DecimalFormat("0"));
		this.txtPersistedContacts = new JFormattedTextField(new DecimalFormat("0"));
		this.txtRemovedContacts = new JFormattedTextField(new DecimalFormat("0"));
		this.txtSolvedContacts = new JFormattedTextField(new DecimalFormat("0"));
		
		this.txtTotalContacts.setEditable(false);
		this.txtSensedContacts.setEditable(false);
		this.txtAddedContacts.setEditable(false);
		this.txtPersistedContacts.setEditable(false);
		this.txtRemovedContacts.setEditable(false);
		this.txtSolvedContacts.setEditable(false);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblTotalContacts)
						.addComponent(lblSensedContacts)
						.addComponent(lblAddedContacts)
						.addComponent(lblPersistedContacts)
						.addComponent(lblRemovedContacts)
						.addComponent(lblSolvedContacts))
				.addGroup(layout.createParallelGroup()
						.addComponent(this.txtTotalContacts)
						.addComponent(this.txtSensedContacts)
						.addComponent(this.txtAddedContacts)
						.addComponent(this.txtPersistedContacts)
						.addComponent(this.txtRemovedContacts)
						.addComponent(this.txtSolvedContacts)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(lblTotalContacts)
						.addComponent(this.txtTotalContacts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblSensedContacts)
						.addComponent(this.txtSensedContacts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblAddedContacts)
						.addComponent(this.txtAddedContacts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblPersistedContacts)
						.addComponent(this.txtPersistedContacts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblRemovedContacts)
						.addComponent(this.txtRemovedContacts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup()
						.addComponent(lblSolvedContacts)
						.addComponent(this.txtSolvedContacts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		this.update();
	}
	
	/**
	 * Sets the contact counter.
	 * <p>
	 * This can be used if the world was changed.
	 * <p>
	 * This will immediately update the panel.
	 * @param counter the new contact counter
	 */
	public void setContactCounter(ContactCounter counter) {
		this.counter = counter;
		this.update();
	}
	
	/**
	 * Updates the text boxes with the new values from the contact counter.
	 */
	public void update() {
		int added = this.counter.getAdded();
		int persisted = this.counter.getPersisted();
		int removed = this.counter.getRemoved();
		int solved = this.counter.getSolved();
		int sensed = this.counter.getSensed();
		int total = added + persisted + sensed;
		
		this.txtTotalContacts.setValue(total);
		this.txtAddedContacts.setValue(added);
		this.txtPersistedContacts.setValue(persisted);
		this.txtRemovedContacts.setValue(removed);
		this.txtSolvedContacts.setValue(solved);
		this.txtSensedContacts.setValue(sensed);
	}
}
