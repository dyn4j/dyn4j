package org.dyn4j.sandbox.events;

import java.awt.event.ActionEvent;

import org.dyn4j.sandbox.SandboxBody;

/**
 * An action event involving a body.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BodyActionEvent extends ActionEvent {
	/** The version id */
	private static final long serialVersionUID = -5041631963145918442L;
	
	/** The effected body */
	private SandboxBody body;
	
	/**
	 * Full constructor.
	 * @param source the source object of the event
	 * @param id the action event id
	 * @param command the action command
	 * @param body the effected body
	 */
	public BodyActionEvent(Object source, int id, String command, SandboxBody body) {
		super(source, id, command);
		this.body = body;
	}
	
	/**
	 * Returns the effected body.
	 * @return SandboxBody
	 */
	public SandboxBody getBody() {
		return body;
	}
}
