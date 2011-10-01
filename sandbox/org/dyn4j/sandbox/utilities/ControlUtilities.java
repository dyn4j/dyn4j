package org.dyn4j.sandbox.utilities;

import javax.swing.JFormattedTextField;

/**
 * Utility class to help working with controls.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ControlUtilities {
	/**
	 * Returns the double value of the number stored in the given text field.
	 * @param field the text field
	 * @return double the double value
	 */
	public static final double getDoubleValue(JFormattedTextField field) {
		Number number = (Number)field.getValue();
		return number.doubleValue();
	}
	
	/**
	 * Returns the int value of the number stored in the given text field.
	 * @param field the text field
	 * @return int the integer value
	 */
	public static final int getIntValue(JFormattedTextField field) {
		Number number = (Number)field.getValue();
		return number.intValue();
	}
}
