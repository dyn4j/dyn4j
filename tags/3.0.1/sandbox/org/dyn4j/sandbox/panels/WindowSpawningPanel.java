package org.dyn4j.sandbox.panels;

import java.awt.Window;

import javax.swing.JPanel;

/**
 * Represents a panel that can spawn other windows.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class WindowSpawningPanel extends JPanel {
	/** The version id */
	private static final long serialVersionUID = -981961470430136255L;
	
	/** The window that contains this editor panel */
	protected Window parent;
	
	/**
	 * Full constructor.
	 * @param parent the window containing this panel
	 */
	public WindowSpawningPanel(Window parent) {
		this.parent = parent;
	}
	
	/**
	 * Returns the window containing this panel
	 * @return Window
	 */
	public Window getParentWindow() {
		return this.parent;
	}
}
