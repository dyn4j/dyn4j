/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.testbed;

import java.util.List;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Mass;

/**
 * Class used to control a body directly by translation
 * and rotation using the mouse.
 * @author William Bittle
 * @version 3.0.1
 * @since 1.0.0
 */
public class DirectControl {
	/**
	 * Class containing the state that must be restored when
	 * the controled body is released.
	 * @author William Bittle
	 * @version 1.2.0
	 * @since 1.0.0
	 */
	public static class State {
		/** Whether the body can be automatically put to sleep */
		public boolean autoSleepingEnabled;
		
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
		
		// wake up all the connected bodies (by non-sensed contact)
		List<Body> bodies = body.getInContactBodies(false);
		int size = bodies.size();
		for (int i = 0; i < size; i++) {
			Body b = bodies.get(i);
			b.setAsleep(false);
		}
		// wake up all the connected bodies (by joints)
		bodies = body.getJoinedBodies();
		size = bodies.size();
		for (int i = 0; i < size; i++) {
			Body b = bodies.get(i);
			b.setAsleep(false);
		}
		
		// save the mass type
		state.massType = body.getMass().getType();
		// save the sleep state
		state.autoSleepingEnabled = body.isAutoSleepingEnabled();
		
		// set the mass to infinite
		body.getMass().setType(Mass.Type.INFINITE);
		// make sure this body is awake
		body.setAsleep(false);
		body.setAutoSleepingEnabled(false);
		// stop any movement
		body.setAngularVelocity(0.0);
		body.getVelocity().zero();
		// clear the accumulators
		body.clearAccumulatedForce();
		body.clearAccumulatedTorque();
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
		body.setAutoSleepingEnabled(state.autoSleepingEnabled);
	}
}
