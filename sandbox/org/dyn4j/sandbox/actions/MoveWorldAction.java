package org.dyn4j.sandbox.actions;

import java.awt.Cursor;
import java.awt.Window;

import org.dyn4j.geometry.Vector2;

/**
 * Action to store information about moving (translating) the world.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class MoveWorldAction extends Action {
	/** The current offset when the action was started */
	private Vector2 offset;
	
	/** The begin position of the translation */
	private Vector2 beginPosition;
	
	/**
	 * Begins the move action with the specified begin position in world coordinates.
	 * @param currentOffset the current offset
	 * @param mousePosition the begin position in world coordinates
	 * @param window the window
	 */
	public synchronized void begin(Vector2 currentOffset, Vector2 mousePosition, Window window) {
		this.active = true;
		this.offset = currentOffset;
		window.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
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
	 * Ends the move body action.
	 * @param window the window
	 */
	public synchronized void end(Window window) {
		this.active = false;
		this.offset = null;
		this.beginPosition = null;
		window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	/**
	 * Returns the begin position of the move body action in world coordinates.
	 * <p>
	 * Returns null if the action is inactive.
	 * @return Vector2
	 * @see #isActive()
	 */
	public synchronized Vector2 getBeginPosition() {
		return this.beginPosition;
	}
	
	/**
	 * Returns the offset from the beginning of the action.
	 * @return Vector2
	 * @see #isActive()
	 */
	public synchronized Vector2 getOffset() {
		return this.offset;
	}
}
