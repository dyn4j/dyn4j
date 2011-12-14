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

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.dyn4j.sandbox.ContactCounter;
import org.dyn4j.sandbox.icons.Icons;
import org.dyn4j.sandbox.resources.Messages;

/**
 * Panel to show the number of contacts and their respective types.
 * @author William Bittle
 * @version 1.0.1
 * @since 1.0.0
 */
public class ContactPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = 1878435102775553243L;
	
	/** The contact counter used by the current world object */
	private ContactCounter counter;
	
	/** The text box to store the total number of contacts */
	private JTextField txtTotalContacts;
	
	/** The text box to store the number of sensed contacts */
	private JTextField txtSensedContacts;
	
	/** The text box to store the number of added contacts */
	private JTextField txtAddedContacts;
	
	/** The text box to store the number of persisted contacts */
	private JTextField txtPersistedContacts;
	
	/** The text box to store the number of removed contacts */
	private JTextField txtRemovedContacts;
	
	/** The text box to store the number of solved contacts */
	private JTextField txtSolvedContacts;

	/** The last time the panel was updated */
	private long lastUpdate;
	
	/**
	 * Full constructor.
	 * @param counter the contact counter object used by the current world
	 */
	public ContactPanel(ContactCounter counter) {
		this.counter = counter;
		
		JLabel lblTotalContacts = new JLabel(Messages.getString("panel.contact.total"), Icons.INFO, JLabel.LEFT);
		JLabel lblSensedContacts = new JLabel(Messages.getString("panel.contact.sensed"), Icons.INFO, JLabel.LEFT);
		JLabel lblAddedContacts = new JLabel(Messages.getString("panel.contact.added"), Icons.INFO, JLabel.LEFT);
		JLabel lblPersistedContacts = new JLabel(Messages.getString("panel.contact.persisted"), Icons.INFO, JLabel.LEFT);
		JLabel lblRemovedContacts = new JLabel(Messages.getString("panel.contact.removed"), Icons.INFO, JLabel.LEFT);
		JLabel lblSolvedContacts = new JLabel(Messages.getString("panel.contact.solved"), Icons.INFO, JLabel.LEFT);
		
		lblTotalContacts.setToolTipText(Messages.getString("panel.contact.total.tooltip"));
		lblSensedContacts.setToolTipText(Messages.getString("panel.contact.sensed.tooltip"));
		lblAddedContacts.setToolTipText(Messages.getString("panel.contact.added.tooltip"));
		lblPersistedContacts.setToolTipText(Messages.getString("panel.contact.persisted.tooltip"));
		lblRemovedContacts.setToolTipText(Messages.getString("panel.contact.removed.tooltip"));
		lblSolvedContacts.setToolTipText(Messages.getString("panel.contact.solved.tooltip"));
		
		this.txtTotalContacts = new JTextField();
		this.txtSensedContacts = new JTextField();
		this.txtAddedContacts = new JTextField();
		this.txtPersistedContacts = new JTextField();
		this.txtRemovedContacts = new JTextField();
		this.txtSolvedContacts = new JTextField();
		
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
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblTotalContacts)
						.addComponent(this.txtTotalContacts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblSensedContacts)
						.addComponent(this.txtSensedContacts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblAddedContacts)
						.addComponent(this.txtAddedContacts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblPersistedContacts)
						.addComponent(this.txtPersistedContacts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(lblRemovedContacts)
						.addComponent(this.txtRemovedContacts, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
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
				
				txtTotalContacts.setText(String.valueOf(total));
				txtAddedContacts.setText(String.valueOf(added));
				txtPersistedContacts.setText(String.valueOf(persisted));
				txtRemovedContacts.setText(String.valueOf(removed));
				txtSolvedContacts.setText(String.valueOf(solved));
				txtSensedContacts.setText(String.valueOf(sensed));
			}
		});
	}
}
