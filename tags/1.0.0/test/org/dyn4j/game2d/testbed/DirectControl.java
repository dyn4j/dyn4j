package org.dyn4j.game2d.testbed;

import java.util.List;

import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.joint.Joint;
import org.dyn4j.game2d.geometry.Mass;

/**
 * Class used to control a body directly by translation
 * and rotation using the mouse.
 * @author William Bittle
 */
public class DirectControl {
	/**
	 * Class containing the state that must be restored when
	 * the controled body is released.
	 * @author William Bittle
	 */
	public static class State {
		/** Whether the body is allowed to sleep or not */
		public boolean canSleep;
		
		/** The mass type */
		public Mass.Type massType;
	}
	
	/**
	 * Wakes up any {@link Body}s attached by {@link Joint}s and any 
	 * {@link Body}s that are currently in contact with the given 
	 * {@link Body} and sets the {@link Mass} of the given {@link Body}
	 * to infinite.
	 * <p>
	 * This method is used to directly control a {@link Body} by
	 * translation and rotation instead of velocity/force.
	 * @param body the {@link Body} to control
	 * @return {@link DirectControl.State} the state of the body used for releasing
	 */
	public static DirectControl.State control(Body body) {
		// check for null body
		if (body == null) throw new NullPointerException("Cannot control a null body.");
		// create a saved state
		DirectControl.State state = new DirectControl.State();
		
		// wake up all attached bodies
		List<Body> bodies = body.getConnnectedBodies();
		int size = bodies.size();
		for (int i = 0; i < size; i++) {
			Body b = bodies.get(i);
			b.setAsleep(false);
		}
		
		// save the mass type
		state.massType = body.getMass().getType();
		// save the sleep state
		state.canSleep = body.canSleep();
		
		// set the mass to infinite
		body.getMass().setType(Mass.Type.INFINITE);
		// make sure this body is awake
		body.setAsleep(false);
		body.setCanSleep(false);
		// stop any movement
		body.setAngularVelocity(0.0);
		body.getVelocity().zero();
		// clear the accumulators
		body.clearForces();
		body.clearTorques();
		// clear the forces and torques
		body.clearForce();
		body.clearTorque();
		
		// return the saved state
		return state;
	}
	
	/**
	 * Releases control of the given {@link Body}.
	 * @param body the {@link Body} to release control of
	 * @param state the original state of the body to restore
	 */
	public static void release(Body body, DirectControl.State state) {
		if (body == null) throw new NullPointerException("Cannot release control of a null body.");
		if (state == null) throw new NullPointerException("The control state cannot be null.");
		// awaken the body and make sure its not frozen
		body.setAsleep(false);
		body.setActive(true);
		// get the body's mass
		Mass mass = body.getMass();
		// set the mass type back to the previous type
		mass.setType(state.massType);
		body.setCanSleep(state.canSleep);
	}
}
