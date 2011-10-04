package org.dyn4j.sandbox.panels;

import java.awt.Window;

/**
 * A panel that accepts input that must be validated.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface InputPanel {
	/**
	 * Returns true if the input on the panel is valid.
	 * @return boolean
	 */
	public abstract boolean isValidInput();
	
	/**
	 * Shows a JOptionPane displaying to the user the invalid input.
	 * @param owner the owner of the message
	 */
	public abstract void showInvalidInputMessage(Window owner);
}
