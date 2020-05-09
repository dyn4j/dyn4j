/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.world;

import java.util.Iterator;
import java.util.List;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactConstraintSolver;
import org.dyn4j.dynamics.contact.TimeOfImpactSolver;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.listener.ContactListener;
import org.dyn4j.world.listener.DestructionListener;
import org.dyn4j.world.listener.StepListener;
import org.dyn4j.world.listener.TimeOfImpactListener;

/**
 * Represents a {@link CollisionWorld} that resolves collision using Newton's laws of physics.
 * This interface also expands on the {@link CollisionWorld} adding other features like joints, gravity,
 * etc.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 * @param <T> the {@link PhysicsBody} type
 * @param <V> the {@link ContactCollisionData} type
 */
public interface PhysicsWorld<T extends PhysicsBody, V extends ContactCollisionData<T>> extends CollisionWorld<T, BodyFixture, V> {
	/** Earths gravity constant */
	public static final Vector2 EARTH_GRAVITY = new Vector2(0.0, -9.8);
	
	/** Zero gravity constant */
	public static final Vector2 ZERO_GRAVITY = new Vector2(0.0, 0.0);

	/**
	 * Updates the {@link PhysicsWorld}.
	 * <p>
	 * This method will only update the world given the step frequency contained
	 * in the {@link Settings} object.  You can use the {@link StepListener} interface
	 * to listen for when a step is actually performed.  In addition, this method will
	 * return true if a step was performed.
	 * <p>
	 * This method performs, at maximum, one simulation step.  Any remaining time from 
	 * the previous call of this method is added to the given elapsed time to determine
	 * if a step needs to be performed.  If the given elapsed time is usually greater 
	 * than the step frequency, consider using the {@link #update(double, int)} method
	 * instead.
	 * <p>
	 * Alternatively you can call the {@link #updatev(double)} method to use a variable
	 * time step.
	 * @see #update(double, int)
	 * @see #updatev(double)
	 * @see #getAccumulatedTime()
	 * @param elapsedTime the elapsed time in seconds
	 * @return boolean true if the {@link World} performed a simulation step
	 */
	public boolean update(double elapsedTime);

	/**
	 * Updates the {@link World}.
	 * <p>
	 * This method will only update the world given the step frequency contained
	 * in the {@link Settings} object.  You can use the {@link StepListener} interface
	 * to listen for when a step is actually performed.
	 * <p>
	 * Unlike the {@link #update(double)} method, this method will perform more than one
	 * step based on the given elapsed time.  For example, if the given elapsed time + the
	 * remaining time from the last call of this method is 2 * step frequency, then 2 steps 
	 * will be performed.  Use the maximumSteps parameter to put an upper bound on the 
	 * number of steps performed.
	 * <p>
	 * Alternatively you can call the {@link #updatev(double)} method to use a variable
	 * time step.
	 * @see #update(double)
	 * @see #updatev(double)
	 * @see #getAccumulatedTime()
	 * @param elapsedTime the elapsed time in seconds
	 * @param maximumSteps the maximum number of steps to perform
	 * @return boolean true if the {@link World} performed at least one simulation step
	 * @since 3.1.10
	 */
	public boolean update(double elapsedTime, int maximumSteps);
	
	/**
	 * Updates the {@link World}.
	 * <p>
	 * This method will only update the world given the step frequency contained
	 * in the {@link Settings} object.  You can use the {@link StepListener} interface
	 * to listen for when a step is actually performed.  In addition, this method will
	 * return true if a step was performed.
	 * <p>
	 * This method performs, at maximum, one simulation step.  Any remaining time from 
	 * the previous call of this method is added to the given elapsed time to determine
	 * if a step needs to be performed.  If the given elapsed time is usually greater 
	 * than the step frequency, consider using the {@link #update(double, int)} method
	 * instead.
	 * <p>
	 * The stepElapsedTime parameter provides a way for the {@link World} to continue to 
	 * update at the frequency defined in the {@link Settings} object, but advance the
	 * simulation by the given time.
	 * <p>
	 * Alternatively you can call the {@link #updatev(double)} method to use a variable
	 * time step.
	 * @see #update(double)
	 * @see #updatev(double)
	 * @see #getAccumulatedTime()
	 * @param elapsedTime the elapsed time in seconds
	 * @param stepElapsedTime the time, in seconds, that the simulation should be advanced
	 * @return boolean true if the {@link World} performed at least one simulation step
	 * @since 3.2.4
	 */
	public boolean update(double elapsedTime, double stepElapsedTime);
	
	/**
	 * Updates the {@link World}.
	 * <p>
	 * This method will only update the world given the step frequency contained
	 * in the {@link Settings} object.  You can use the {@link StepListener} interface
	 * to listen for when a step is actually performed.
	 * <p>
	 * Unlike the {@link #update(double)} method, this method will perform more than one
	 * step based on the given elapsed time.  For example, if the given elapsed time + the
	 * remaining time from the last call of this method is 2 * step frequency, then 2 steps 
	 * will be performed.  Use the maximumSteps parameter to put an upper bound on the 
	 * number of steps performed.
	 * <p>
	 * The stepElapsedTime parameter provides a way for the {@link World} to continue to 
	 * update at the frequency defined in the {@link Settings} object, but advance the
	 * simulation by the given time.
	 * <p>
	 * Alternatively you can call the {@link #updatev(double)} method to use a variable
	 * time step.
	 * @see #update(double)
	 * @see #updatev(double)
	 * @see #getAccumulatedTime()
	 * @param elapsedTime the elapsed time in seconds
	 * @param stepElapsedTime the time, in seconds, that the simulation should be advanced for each step; if less than or equal to zero {@link Settings#getStepFrequency()} will be used
	 * @param maximumSteps the maximum number of steps to perform
	 * @return boolean true if the {@link World} performed at least one simulation step
	 * @since 3.2.4
	 */
	public boolean update(double elapsedTime, double stepElapsedTime, int maximumSteps);
	
	/**
	 * Updates the {@link World}.
	 * <p>
	 * This method will update the world on every call.  Unlike the {@link #update(double)}
	 * method, this method uses the given elapsed time and does not attempt to update the world
	 * on a set interval.
	 * <p>
	 * This method immediately returns if the given elapsedTime is less than or equal to
	 * zero.
	 * @see #update(double)
	 * @see #update(double, int)
	 * @param elapsedTime the elapsed time in seconds
	 */
	public void updatev(double elapsedTime);
	
	/**
	 * Performs the given number of simulation steps using the step frequency in {@link Settings}.
	 * <p>
	 * This method immediately returns if the given step count is less than or equal to
	 * zero.
	 * @param steps the number of simulation steps to perform
	 */
	public void step(int steps);
	
	/**
	 * Performs the given number of simulation steps using the given elapsed time for each step.
	 * <p>
	 * This method immediately returns if the given elapsedTime or step count is less than or equal to
	 * zero.
	 * @param steps the number of simulation steps to perform
	 * @param elapsedTime the elapsed time for each step
	 */
	public void step(int steps, double elapsedTime);
	
	/**
	 * Adds the given {@link Joint} to the {@link World}.
	 * @param joint the {@link Joint} to add
	 * @throws NullPointerException if joint is null
	 * @throws IllegalArgumentException if joint has already been added to this world or if its a member of another world instance
	 * @since 3.1.1
	 */
	public void addJoint(Joint<T> joint);

	/**
	 * Returns true if this world contains the given joint.
	 * @param joint the {@link Joint} to test for
	 * @return boolean true if the joint is contained in this world
	 * @since 3.1.1
	 */
	public boolean containsJoint(Joint<T> joint);
	
	/**
	 * Removes the {@link Joint} at the given index from this {@link World}.
	 * <p>
	 * No other objects are implicitly destroyed with joints are removed.
	 * @param index the index of the {@link Joint} to remove
	 * @return boolean true if the {@link Joint} was removed
	 * @since 3.2.0
	 */
	public boolean removeJoint(int index);
	
	/**
	 * Removes the given {@link Joint} from this {@link World}.
	 * <p>
	 * No other objects are implicitly destroyed with joints are removed.
	 * @param joint the {@link Joint} to remove
	 * @return boolean true if the {@link Joint} was removed
	 */
	public boolean removeJoint(Joint<T> joint);
	
	/**
	 * Removes all the joints and bodies from this world.
	 * <p>
	 * This method does <b>not</b> notify of destroyed objects.
	 * @see #removeAllBodiesAndJoints(boolean)
	 * @since 3.1.1
	 */
	public void removeAllBodiesAndJoints();
	
	/**
	 * Removes all the joints and bodies from this world.
	 * @param notify true if destruction of joints and contacts should be notified of by the {@link DestructionListener}
	 * @since 3.1.1
	 */
	public void removeAllBodiesAndJoints(boolean notify);
	
	/**
	 * Removes all {@link Joint}s from this {@link World}.
	 * <p>
	 * This method does not notify of the joints removed.
	 * @see #removeAllJoints(boolean)
	 * @since 3.0.1
	 */
	public void removeAllJoints();
	
	/**
	 * Removes all {@link Joint}s from this {@link World}.
	 * @param notify true if destruction of joints should be notified of by the {@link DestructionListener}
	 * @since 3.0.1
	 */
	public void removeAllJoints(boolean notify);
	
	/**
	 * Returns the settings for this world.
	 * @return {@link Settings}
	 * @since 3.0.3
	 */
	public Settings getSettings();
	
	/**
	 * Sets the dynamics settings for this world.
	 * @param settings the desired settings
	 * @throws NullPointerException if the given settings is null
	 * @since 3.0.3
	 */
	public void setSettings(Settings settings);
	
	/**
	 * Sets the acceleration due to gravity.
	 * @param gravity the gravity in meters/second<sup>2</sup>
	 * @throws NullPointerException if gravity is null
	 */
	public void setGravity(Vector2 gravity);
	
	/**
	 * Returns the acceleration due to gravity.
	 * @return {@link Vector2} the gravity in meters/second<sup>2</sup>
	 */
	public Vector2 getGravity();

	/**
	 * Returns the list of {@link ContactListener}s.
	 * <p>
	 * Use the returned list to add/remove listeners.
	 * @return List&lt;{@link ContactListener}&gt;
	 */
	public List<ContactListener<T>> getContactListeners();
	
	/**
	 * Returns the list of {@link DestructionListener}s.
	 * <p>
	 * Use the returned list to add/remove listeners.
	 * @return List&lt;{@link DestructionListener}&gt;
	 */
	public List<DestructionListener<T>> getDestructionListeners();
	
	/**
	 * Returns the list of {@link TimeOfImpactListener}s.
	 * <p>
	 * Use the returned list to add/remove listeners.
	 * @return List&lt;{@link TimeOfImpactListener}&gt;
	 */
	public List<TimeOfImpactListener<T>> getTimeOfImpactListeners();
	
	/**
	 * Returns the list of {@link StepListener}s.
	 * <p>
	 * Use the returned list to add/remove listeners.
	 * @return List&lt;{@link StepListener}&gt;
	 */
	public List<StepListener<T, V>> getStepListeners();
	
	/**
	 * Returns the {@link CoefficientMixer}.
	 * @return {@link CoefficientMixer}
	 * @see #setCoefficientMixer(CoefficientMixer)
	 */
	public CoefficientMixer getCoefficientMixer();
	
	/**
	 * Sets the {@link CoefficientMixer}.
	 * <p>
	 * A {@link CoefficientMixer} is an implementation of mixing functions for various
	 * coefficients used in contact solving.  Common coefficients are restitution and 
	 * friction.  Since each {@link BodyFixture} can have it's own value for these 
	 * coefficients, the {@link CoefficientMixer} is used to mathematically combine them
	 * into one coefficient to be used in contact resolution.
	 * <p>
	 * {@link CoefficientMixer#DEFAULT_MIXER} is the default.
	 * @param coefficientMixer the coefficient mixer
	 * @throws NullPointerException if coefficientMixer is null
	 * @see CoefficientMixer
	 */
	public void setCoefficientMixer(CoefficientMixer coefficientMixer);

	/**
	 * Sets the {@link ContactConstraintSolver} for this world.
	 * @param constraintSolver the contact constraint solver
	 * @throws NullPointerException if contactManager is null
	 * @see ContactConstraintSolver
	 * @since 3.2.0
	 */
	public void setContactConstraintSolver(ContactConstraintSolver<T> constraintSolver);
	
	/**
	 * Returns the {@link ContactConstraintSolver}.
	 * @return {@link ContactConstraintSolver}
	 * @since 3.2.0
	 * @see #setContactConstraintSolver(ContactConstraintSolver)
	 */
	public ContactConstraintSolver<T> getContactConstraintSolver();

	/**
	 * Sets the {@link TimeOfImpactSolver} for this world.
	 * @param timeOfImpactSolver the time of impact solver
	 * @throws NullPointerException if timeOfImpactSolver is null
	 * @since 4.0.0
	 */
	public void setTimeOfImpactSolver(TimeOfImpactSolver<T> timeOfImpactSolver);
	
	/**
	 * Returns the {@link TimeOfImpactSolver}.
	 * @return {@link TimeOfImpactSolver}
	 * @since 4.0.0
	 */
	public TimeOfImpactSolver<T> getTimeOfImpactSolver();
	
	/**
	 * Returns the number of {@link Joint}s in this {@link World}.
	 * @return int the number of joints
	 */
	public int getJointCount();
	
	/**
	 * Returns the {@link Joint} at the given index.
	 * @param index the index
	 * @return {@link Joint}
	 */
	public Joint<T> getJoint(int index);
	
	/**
	 * Returns an unmodifiable list containing all the joints in this world.
	 * <p>
	 * The returned list is backed by the internal list, therefore adding or removing joints while 
	 * iterating through the returned list is not permitted.  Use the {@link #getJointIterator()}
	 * method instead.
	 * @return List&lt;{@link Joint}&gt;
	 * @since 3.1.5
	 * @see #getJointIterator()
	 */
	public List<Joint<T>> getJoints();

	/**
	 * Returns an iterator for iterating over the joints in this world.
	 * <p>
	 * The returned iterator supports the <code>remove</code> method.
	 * @return Iterator&lt;{@link Joint}&gt;
	 * @since 3.2.0
	 */
	public Iterator<Joint<T>> getJointIterator();
	
	/**
	 * Returns the {@link TimeStep} object used to advance
	 * the simulation.
	 * <p>
	 * The returned object contains the step information (elapsed time)
	 * for the last and the previous time step.
	 * @return {@link TimeStep} the current step object
	 */
	public TimeStep getStep();

	/**
	 * Returns the current accumulated time.
	 * <p>
	 * This is the time that has elapsed since the last step
	 * of the engine.
	 * <p>
	 * This time is used and/or accumulated on each call of the 
	 * {@link #update(double)} and {@link #update(double, int)} methods.
	 * <p>
	 * This time is reduced by the step frequency for each step
	 * of the engine.
	 * @return double
	 * @since 3.1.10
	 */
	public double getAccumulatedTime();
	
	/**
	 * Sets the current accumulated time.
	 * <p>
	 * A typical use case would be to throw away any remaining time
	 * that the {@link #update(double)} or {@link #update(double, int)}
	 * methods didn't use:
	 * <pre>
	 * boolean updated = world.update(elapsedTime);
	 * // the check if the world actually updated is crutial in this example
	 * if (updated) {
	 * 	// throw away any remaining time we didnt use
	 * 	world.setAccumulatedTime(0);
	 * }
	 * </pre>
	 * Or, in the case of reusing the same World object, you could use this
	 * method to clear any accumulated time.
	 * <p>
	 * If elapsedTime is less than zero, this method immediately returns.
	 * @see #getAccumulatedTime()
	 * @param elapsedTime the desired elapsed time
	 * @since 3.1.10
	 */
	public void setAccumulatedTime(double elapsedTime);

	// other
	
	/**
	 * Returns true if upon the next time step the contacts must be updated.
	 * @return boolean
	 * @see #setUpdateRequired(boolean)
	 */
	public boolean isUpdateRequired();
	
	/**
	 * Sets the update required flag.
	 * <p>
	 * This flag indicates that changes have been made to the bodies, fixtures, joints, etc. of
	 * this {@link PhysicsWorld} that require another collision detection cycle. The following
	 * conditions outline the common reasons a user would set this flag to true:
	 * <ul>
	 * 	<li>If a Body has been added or removed from the World</li>
	 * 	<li>If a Body has been translated or rotated</li>
	 * 	<li>If a Body's state has been manually changed via the Body.setActive(boolean) method</li>
	 * 	<li>If a BodyFixture has been added or removed from a Body</li>
	 * 	<li>If a BodyFixture's sensor flag has been manually changed via the BodyFixture.setSensor(boolean) method</li>
	 * 	<li>If a BodyFixture's filter has been manually changed via the BodyFixture.setFilter(boolean) method</li>
	 * 	<li>If a BodyFixture's restitution or friction coefficient has changed</li>
	 * 	<li>If a BodyFixture's Shape has been translated or rotated</li>
	 * 	<li>If a BodyFixture's Shape has been changed (vertices, radius, etc.)</li>
	 * 	<li>If a Body's type has changed to or from Static (this is caused by the using setMassType(Mass.INFINITE/Mass.NORMAL) method)</li>
	 * 	<li>If a Joint has been added or removed from the World in which the joined bodies should not be allowed to collide</li>
	 * 	<li>If the World's CoefficientMixer has been changed</li>
	 * </ul>
	 * In most cases this flag should not be set. Running another collision detection cycle is a time
	 * consuming process and any changes will be reflected in the next run. The intended use-case for
	 * this flag are situations where a significant portion of the objects in the world have been 
	 * modified or the user cannot wait a cycle before their changes are reflected.
	 * @param flag the flag
	 */
	public void setUpdateRequired(boolean flag);
	
	/**
	 * Returns true if the given {@link PhysicsBody}s are currently in collision.
	 * <p>
	 * Collision is defined as the two bodies interacting to the level of {@link ContactConstraint}
	 * generation and solving.
	 * @param body1 the first body
	 * @param body2 the second body
	 * @return boolean
	 */
	public boolean isInContact(T body1, T body2);
	
	/**
	 * Returns a list of all {@link PhysicsBody}s that are in contact with the given {@link PhysicsBody}.
	 * @param body the body
	 * @param includeSensedContact true if sensed contacts should be included in the results
	 * @return List&lt;{@link PhysicsBody}&gt;
	 */
	public List<T> getInContactBodies(T body, boolean includeSensedContact);
	
	/**
	 * Returns the {@link ContactConstraint}s for the given {@link PhysicsBody}.
	 * <p>
	 * These represent the contact pairs between the given body and the others it's colliding with.
	 * Each {@link ContactConstraint} could have 1 or 2 contacts associated with it.
	 * @param body the body
	 * @return List&lt;{@link ContactConstraint}&gt;
	 */
	public List<ContactConstraint<T>> getContacts(T body);
	
	/**
	 * Returns true if the two {@link PhysicsBody}s are joined via a {@link Joint}.
	 * @param body1 the first body
	 * @param body2 the second body
	 * @param includeCollisionNotAllowed true to include joints where collision is not allowed
	 * @return boolean
	 */
	public boolean isJoined(T body1, T body2, boolean includeCollisionNotAllowed);
	
	/**
	 * Returns the {@link PhysicsBody}s joined to the given {@link PhysicsBody} via {@link Joint}s.
	 * @param body the body
	 * @return List&lt;{@link PhysicsBody}&gt;
	 */
	public List<T> getJoinedBodies(T body);
	
	/**
	 * Returns the {@link Joint}s the given {@link PhysicsBody} is a member of.
	 * @param body the body
	 * @return List&lt;{@link Joint}&gt;
	 */
	public List<Joint<T>> getJoints(T body);
}
