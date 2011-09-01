package org.dyn4j.sandbox.listeners;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Focus adapter used to select the text inside a JTextField when the
 * focus is given to it.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SelectTextFocusListener extends FocusAdapter {
	/** The text field */
	private JTextField field;
	
	/**
	 * Full constructor.
	 * @param field the text field
	 */
	public SelectTextFocusListener(JTextField field) {
		this.field = field;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.FocusAdapter#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent e) {
		// add a runnable to be executed later
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				field.selectAll();
			}
		});
		super.focusGained(e);
	}
}
