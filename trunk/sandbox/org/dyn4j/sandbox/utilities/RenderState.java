package org.dyn4j.sandbox.utilities;

import java.awt.Dimension;

import org.dyn4j.geometry.Vector2;

/**
 * Class to store some state information about the application that
 * is relevant to rendering.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class RenderState {
	/** The screen coordinates to world coordinates scale factor */
	public double scale;
	
	/** The render area size in screen coordinates */
	public Dimension size;
	
	/** The world coordinate offset */
	public Vector2 offset;
	
	/** The elapsed time */
	public double dt;
	
	/** The inverse elapsed time */
	public double invDt;
}
