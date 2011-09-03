package org.dyn4j.sandbox.actions;

/**
 * Action to store information about selecting an object.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 * @param <E> the object type
 */
public class SelectAction<E> extends Action {
	/** The selected object */
	private E selectedObject;
	
	/**
	 * Begins the select object action by supplying the selected object.
	 * @param selectedObject the selected object
	 */
	public synchronized void begin(E selectedObject) {
		this.active = true;
		this.selectedObject = selectedObject;
	}
	
	/**
	 * Ends the select object action.
	 */
	public synchronized void end() {
		this.active = false;
		this.selectedObject = null;
	}
	
	/**
	 * Returns the currently selected object.
	 * <p>
	 * Returns null if the action is not active.
	 * @return SandboxBody
	 * @see #isActive()
	 */
	public synchronized E getObject() {
		return this.selectedObject;
	}
}
