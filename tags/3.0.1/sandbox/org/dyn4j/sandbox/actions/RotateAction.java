package org.dyn4j.sandbox.actions;

import org.dyn4j.geometry.Vector2;

/**
 * Action to store information about rotating an object.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class RotateAction extends Action {
	/** The begin position of the rotation */
	private Vector2 beginPosition;
	
	/**
	 * Begins the rotate action with the specified begin position in world coordinates.
	 * @param mousePosition the begin position in world coordinates
	 */
	public synchronized void begin(Vector2 mousePosition) {
		this.active = true;
		this.beginPosition = mousePosition;
	}
	
	/**
	 * Updates the action with the new begin position in world coordinates.
	 * <p>
	 * This is used if the action is carried out over a time period in which the
	 * user would like visual feedback.
	 * @param mousePosition the new begin position in world coordinates
	 */
	public synchronized void update(Vector2 mousePosition) {
		this.beginPosition = mousePosition;
	}
	
	/**
	 * Ends the rotate action.
	 */
	public synchronized void end() {
		this.active = false;
		this.beginPosition = null;
	}
	
	/**
	 * Returns the begin position of the rotate action in world coordinates.
	 * <p>
	 * Returns null if the action is inactive.
	 * @return Vector2
	 * @see #isActive()
	 */
	public synchronized Vector2 getBeginPosition() {
		return this.beginPosition;
	}
}
