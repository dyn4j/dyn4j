package org.dyn4j.sandbox.actions;

/**
 * Simple abstract class containing the state for user actions.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Action {
	/** True if the action is active or being performed */
	protected boolean active;
	
	/**
	 * Sets the action to active or inactive.
	 * @param flag true if active
	 */
	public synchronized void setActive(boolean flag) {
		this.active = flag;
	}
	
	/**
	 * Returns true if this action is active.
	 * @return boolean
	 */
	public synchronized boolean isActive() {
		return this.active;
	}
}
