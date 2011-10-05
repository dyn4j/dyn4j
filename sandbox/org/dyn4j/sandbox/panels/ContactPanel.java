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
package org.dyn4j.sandbox.panels;

import java.text.DecimalFormat;

import javax.swing.GroupLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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

	/** The last time the panel was updated */
	private long lastUpdate;
	
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

		this.lastUpdate = System.nanoTime();
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
		// check if its time to update
		long time = System.nanoTime();
		long diff = time - this.lastUpdate;
		// update 4 times every second
		if (diff > 250000000) {
			this.lastUpdate = time;
			this.updateEDT();
		}
	}
	
	/**
	 * Updates the textboxes on the EDT thread.
	 */
	private void updateEDT() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				int added = counter.getAdded();
				int persisted = counter.getPersisted();
				int removed = counter.getRemoved();
				int solved = counter.getSolved();
				int sensed = counter.getSensed();
				int total = added + persisted + sensed;
				
				txtTotalContacts.setValue(total);
				txtAddedContacts.setValue(added);
				txtPersistedContacts.setValue(persisted);
				txtRemovedContacts.setValue(removed);
				txtSolvedContacts.setValue(solved);
				txtSensedContacts.setValue(sensed);
			}
		});
	}
}