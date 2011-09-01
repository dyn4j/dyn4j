package org.dyn4j.sandbox;

/**
 * Simple thread safe class to manage application settings.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ApplicationSettings {
	/** True if a static color should be used or a random color for bodies */
	private static boolean colorRandom = false;
	
	/** True if the body should be stenciled or rendered normally */
	private static boolean stenciled = false;
	
	/** True if labels should be shown for bodies */
	private static boolean labeled = false;
	
	/** True if anti-aliasing should be used (2X MSAA) */
	private static boolean antiAliasing = false;
	
	/** True if vertical sync should be enabled */
	private static boolean verticalSync = true;
	
	/** True if the origin point should be shown */
	private static boolean originLabeled = true;
	
	/**
	 * Returns true if random colors should be used for new bodies.
	 * @return boolean
	 */
	public static synchronized boolean isColorRandom() {
		return colorRandom;
	}
	
	/**
	 * Sets the random color flag.
	 * @param flag true if new bodies should have a randomly generated color
	 */
	public static synchronized void setColorRandom(boolean flag) {
		colorRandom = flag;
	}
	
	/**
	 * Returns true if bodies should be stenciled instead of rendered normally.
	 * @return boolean
	 */
	public static synchronized boolean isStenciled() {
		return stenciled;
	}
	
	/**
	 * Sets the stencil flag.
	 * @param flag true if bodies should be stenciled instead of rendered normally
	 */
	public static synchronized void setStenciled(boolean flag) {
		stenciled = flag;
	}
	
	/**
	 * Returns true if body labels should be shown.
	 * @return boolean
	 */
	public static synchronized boolean isLabeled() {
		return labeled;
	}
	
	/**
	 * Sets the body labels flag.
	 * @param flag true if body labels should be shown
	 */
	public static synchronized void setLabeled(boolean flag) {
		labeled = flag;
	}
	
	/**
	 * Returns true if anti-aliasing should be used.
	 * @return boolean
	 */
	public static synchronized boolean isAntiAliasingEnabled() {
		return antiAliasing;
	}
	
	/**
	 * Sets the anti-aliasing flag.
	 * @param flag true if anti-aliasing should be used
	 */
	public static synchronized void setAntiAliasingEnabled(boolean flag) {
		antiAliasing = flag;
	}
	
	/**
	 * Returns true if vertical sync should be enabled.
	 * @return boolean
	 */
	public static synchronized boolean isVerticalSyncEnabled() {
		return verticalSync;
	}
	
	/**
	 * Sets the vertical sync flag.
	 * @param flag true if vertical sync should be enabled
	 */
	public static synchronized void setVerticalSyncEnabled(boolean flag) {
		verticalSync = flag;
	}
	
	/**
	 * Returns true if the origin and origin label should be shown.
	 * @return boolean
	 */
	public static synchronized boolean isOriginLabeled() {
		return originLabeled;
	}
	
	/**
	 * Sets the origin label flag.
	 * @param flag true if the origin and origin label should be shown
	 */
	public static synchronized void setOriginLabeled(boolean flag) {
		originLabeled = flag;
	}
}
