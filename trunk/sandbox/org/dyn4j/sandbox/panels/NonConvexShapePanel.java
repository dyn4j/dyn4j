package org.dyn4j.sandbox.panels;

import java.util.List;

import javax.swing.JPanel;

import org.dyn4j.geometry.Convex;

/**
 * Abstract panel used to configure a non-convex shape.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class NonConvexShapePanel extends JPanel implements InputPanel {
	/** The version id */
	private static final long serialVersionUID = -2123887648523056197L;
	
	/**
	 * Returns the current decomposition of the non-convex shape.
	 * <p>
	 * If nothing has been set, null is returned.
	 * @return List&lt;Convex&gt;
	 */
	public abstract List<Convex> getShapes();
}
