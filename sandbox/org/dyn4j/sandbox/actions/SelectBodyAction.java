package org.dyn4j.sandbox.actions;

import org.dyn4j.sandbox.SandboxBody;

/**
 * Action to store information about selecting a body.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SelectBodyAction extends Action {
	/** The body selected */
	private SandboxBody selectedBody;
	
	/**
	 * Begins the select body action by supplying the selected body.
	 * @param selectedBody the selected body
	 */
	public synchronized void begin(SandboxBody selectedBody) {
		this.active = true;
		this.selectedBody = selectedBody;
	}
	
	/**
	 * Ends the select body action.
	 */
	public synchronized void end() {
		this.active = false;
		this.selectedBody = null;
	}
	
	/**
	 * Returns the currently selected body.
	 * <p>
	 * Returns null if the action is not active.
	 * @return SandboxBody
	 * @see #isActive()
	 */
	public synchronized SandboxBody getBody() {
		return this.selectedBody;
	}
}
