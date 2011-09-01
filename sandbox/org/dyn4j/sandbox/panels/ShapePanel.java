package org.dyn4j.sandbox.panels;

import javax.swing.JPanel;

import org.dyn4j.geometry.Convex;

/**
 * Abstract panel used to configure a shape.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class ShapePanel extends JPanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = -2123887648523056197L;

	/**
	 * Returns the currently configured shape.
	 * <p>
	 * If nothing has been set, the default shape is returned.
	 * @return Convex
	 */
	public abstract Convex getShape();
	
	/**
	 * Returns the default shape for the panel.
	 * @return Convex
	 */
	public abstract Convex getDefaultShape();
}
