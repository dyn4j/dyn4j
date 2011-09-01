package org.dyn4j.sandbox.utilities;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 * Utility class to interact with the current UI settings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class UIUtilities {
	/**
	 * Convenience method to print all the UI settings to System.out.
	 */
	public static final void printDefaults() {
		UIDefaults defaults = UIManager.getDefaults();
		Enumeration<Object> keys = defaults.keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			System.out.println(key + ": " + defaults.get(key));
		}
	}
	
	/**
	 * Gets the default font for labels.
	 * @return Font
	 */
	public static final Font getDefaultLabelFont() {
		return UIManager.getDefaults().getFont("Label.font");
	}
}
