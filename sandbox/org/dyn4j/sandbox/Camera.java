package org.dyn4j.sandbox;

import org.dyn4j.geometry.Vector2;

/**
 * Class used to encapsulate camera properties.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Camera {
	/** The zoom factor */
	protected double scale;
	
	/** The translation from 0,0 */
	protected Vector2 translation;
	
	/**
	 * Default constructor.
	 * <p>
	 * Defaults to a 1 to 1 scale and zero translation.
	 */
	public Camera() {
		this.scale = 1.0;
		this.translation = new Vector2();
	}
	
	/**
	 * Full constructor.
	 * @param scale the scale factor (pixels per meter)
	 * @param translation the translation (in world coordinates) from 0,0
	 */
	public Camera(double scale, Vector2 translation) {
		this.scale = scale;
		this.translation = translation;
	}
	
	/**
	 * Zooms out the camera.
	 */
	public void zoomOut() {
		this.scale *= 2.0;
	}
	
	/**
	 * Zooms in the camera.
	 */
	public void zoomIn() {
		this.scale *= 0.5;
	}
	
	/**
	 * Moves the camera back to the origin.
	 */
	public void toOrigin() {
		this.translation.zero();
	}
	
	/**
	 * Translates the camera the given amount along the x and y axes.
	 * @param tx the translation
	 */
	public void translate(Vector2 tx) {
		this.translate(tx.x, tx.y);
	}
	
	/**
	 * Translates the camera the given amount along the x and y axes.
	 * @param x the x translation
	 * @param y the y translation
	 */
	public void translate(double x, double y) {
		this.translation.x += x;
		this.translation.y += y;
	}
	
	// getter/setters
	
	/**
	 * Returns the scale factor.
	 * @return double
	 */
	public double getScale() {
		return scale;
	}
	
	/**
	 * Sets the scale factor in pixels per meter.
	 * @param scale the desired scale factor
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}
	
	/**
	 * Returns the translation.
	 * @return Vector2
	 */
	public Vector2 getTranslation() {
		return translation;
	}
	
	/**
	 * Sets the translation from the origin in world coordinates.
	 * @param translation the translation
	 */
	public void setTranslation(Vector2 translation) {
		this.translation = translation;
	}
}
