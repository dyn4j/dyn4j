/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.dynamics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.dyn4j.DataContainer;
import org.dyn4j.Listener;
import org.dyn4j.collision.Bounds;
import org.dyn4j.collision.BoundsListener;
import org.dyn4j.collision.Filter;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.BroadphaseFilter;
import org.dyn4j.collision.broadphase.BroadphaseItem;
import org.dyn4j.collision.broadphase.BroadphasePair;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.continuous.ConservativeAdvancement;
import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.collision.continuous.TimeOfImpactDetector;
import org.dyn4j.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.Gjk;
import org.dyn4j.collision.narrowphase.LinkPostProcessor;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.NarrowphasePostProcessor;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.collision.narrowphase.Raycast;
import org.dyn4j.collision.narrowphase.RaycastDetector;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactConstraintSolver;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactManager;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.ContactPointId;
import org.dyn4j.dynamics.contact.SequentialImpulses;
import org.dyn4j.dynamics.contact.TimeOfImpactSolver;
import org.dyn4j.dynamics.contact.WarmStartingContactManager;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Manages the logic of collision detection, resolution, and reporting.
 * <p>
 * Interfacing with dyn4j starts with this class.  Create a new instance of this class
 * and add bodies and joints.  Then call one of the update or step methods in your game
 * loop to move the physics engine forward in time.
 * <p>
 * Via the {@link #addListener(Listener)} method, a {@link World} instance can have multiple listeners for all the listener types.
 * Some listener types return a boolean to indicate continuing or allowing something, like {@link CollisionListener}.  If, for example,
 * there are multiple {@link CollisionListener}s and <b>any</b> one of them returns false for an event, the collision is skipped.  However,
 * all listeners will still be called no matter if the first returned false.
 * @author William Bittle
 * @version 3.2.2
 * @since 1.0.0
 */
public class World implements Shiftable, DataContainer {
	/** Earths gravity constant */
	public static final Vector2 EARTH_GRAVITY = new Vector2(0.0, -9.8);
	
	/** Zero gravity constant */
	public static final Vector2 ZERO_GRAVITY = new Vector2(0.0, 0.0);

	/** The world id */
	protected final UUID id = UUID.randomUUID();
	
	// settings
	
	/** The dynamics settings for this world */
	protected Settings settings;
	
	/** The {@link Step} used by the dynamics calculations */
	protected Step step;
	
	/** The world gravity vector */
	protected Vector2 gravity;
	
	/** The world {@link Bounds} */
	protected Bounds bounds;
	
	// algorithms
	
	/** The {@link BroadphaseDetector} */
	protected BroadphaseDetector<Body, BodyFixture> broadphaseDetector;
	
	/** The {@link BroadphaseFilter} for detection */
	protected BroadphaseFilter<Body, BodyFixture> detectBroadphaseFilter;
	
	/** The {@link NarrowphaseDetector} */
	protected NarrowphaseDetector narrowphaseDetector;
	
	/** The {@link NarrowphasePostProcessor} */
	protected NarrowphasePostProcessor narrowphasePostProcessor;
	
	/** The {@link ManifoldSolver} */
	protected ManifoldSolver manifoldSolver;
	
	/** The {@link TimeOfImpactDetector} */
	protected TimeOfImpactDetector timeOfImpactDetector;
	
	/** The {@link RaycastDetector} */
	protected RaycastDetector raycastDetector;

	/** The {@link ContactManager} */
	protected ContactManager contactManager;

	/** The {@link CoefficientMixer} */
	protected CoefficientMixer coefficientMixer;
	
	/** The {@link ContactConstraintSolver} */
	protected ContactConstraintSolver contactConstraintSolver;
	
	/** The {@link TimeOfImpactSolver} */
	protected TimeOfImpactSolver timeOfImpactSolver;

	/** The application data associated */
	protected Object userData;
	
	// internal
	
	// listeners and config
	
	/** The list of listeners for this world */
	private final List<Listener> listeners;
	
	// bodies/joints
	
	/** The {@link Body} list */
	private final List<Body> bodies;
	
	/** The {@link Joint} list */
	private final List<Joint> joints;
	
	// temp data
	
	/** The reusable island */
	private Island island;
	
	/** The accumulated time */
	private double time;
	
	/** Flag to find new contacts */
	private boolean updateRequired;
	
	/**
	 * Default constructor.
	 * <p>
	 * Builds a simulation {@link World} without bounds.
	 * <p>
	 * Defaults to using {@link #EARTH_GRAVITY}, {@link DynamicAABBTree} broad-phase,
	 * {@link Gjk} narrow-phase, and {@link ClippingManifoldSolver}.
	 * <p>
	 * Uses the {@link Capacity#DEFAULT_CAPACITY} capacity object for initialization.
	 */
	public World() {
		this(Capacity.DEFAULT_CAPACITY, null);
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * Defaults to using {@link #EARTH_GRAVITY}, {@link DynamicAABBTree} broad-phase,
	 * {@link Gjk} narrow-phase, and {@link ClippingManifoldSolver}.
	 * <p>
	 * The initial capacity specifies the estimated number of bodies that the simulation
	 * will have at any one time.  This is used to size internal structures to improve
	 * performance.  The internal structures can grow past the initial capacity.
	 * @param initialCapacity the initial capacity settings
	 * @since 3.1.1
	 */
	public World(Capacity initialCapacity) {
		this(initialCapacity, null);
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * Defaults to using {@link #EARTH_GRAVITY}, {@link DynamicAABBTree} broad-phase,
	 * {@link Gjk} narrow-phase, and {@link ClippingManifoldSolver}.
	 * @param bounds the bounds of the {@link World}; can be null
	 */
	public World(Bounds bounds) {
		this(Capacity.DEFAULT_CAPACITY, bounds);
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Defaults to using {@link #EARTH_GRAVITY}, {@link DynamicAABBTree} broad-phase,
	 * {@link Gjk} narrow-phase, and {@link ClippingManifoldSolver}.
	 * <p>
	 * The initial capacity specifies the estimated number of bodies that the simulation
	 * will have at any one time.  This is used to size internal structures to improve
	 * performance.  The internal structures can grow past the initial capacity.
	 * @param initialCapacity the initial capacity settings
	 * @param bounds the bounds of the {@link World}; can be null
	 * @throws NullPointerException if initialCapacity is null
	 * @since 3.1.1
	 */
	public World(Capacity initialCapacity, Bounds bounds) {
		// check for null capacity
		if (initialCapacity == null) initialCapacity = new Capacity();
		
		// initialize all the classes with default values
		this.settings = new Settings();
		this.step = new Step(this.settings.getStepFrequency());
		this.gravity = World.EARTH_GRAVITY;
		this.bounds = bounds;
		
		this.broadphaseDetector = new DynamicAABBTree<Body, BodyFixture>(initialCapacity.getBodyCount());
		this.detectBroadphaseFilter = new DetectBroadphaseFilter();
		this.narrowphaseDetector = new Gjk();
		this.narrowphasePostProcessor = new LinkPostProcessor();
		this.manifoldSolver = new ClippingManifoldSolver();
		this.timeOfImpactDetector = new ConservativeAdvancement();
		this.raycastDetector = new Gjk();
		this.coefficientMixer = CoefficientMixer.DEFAULT_MIXER;
		this.contactManager = new WarmStartingContactManager(initialCapacity);
		this.contactConstraintSolver = new SequentialImpulses();
		this.timeOfImpactSolver = new TimeOfImpactSolver();
		
		this.bodies = new ArrayList<Body>(initialCapacity.getBodyCount());
		this.joints = new ArrayList<Joint>(initialCapacity.getJointCount());
		this.listeners = new ArrayList<Listener>(initialCapacity.getListenerCount());
		
		this.island = new Island(initialCapacity);
		
		this.time = 0.0;
		this.updateRequired = true;
	}
	
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
	 * Alternatively you can call the {@link #updatev(double)} method to use a variable
	 * time step.
	 * @see #update(double, int)
	 * @see #updatev(double)
	 * @see #getAccumulatedTime()
	 * @param elapsedTime the elapsed time in seconds
	 * @return boolean true if the {@link World} performed a simulation step
	 */
	public boolean update(double elapsedTime) {
		// check for negative elapsed time
		if (elapsedTime < 0.0) elapsedTime = 0.0;
		// update the time
		this.time += elapsedTime;
		// check the frequency in settings
		double invhz = this.settings.getStepFrequency();
		// see if we should update or not
		if (this.time >= invhz) {
			// update the step
			this.step.update(invhz);
			// reset the time
			this.time = this.time - invhz;
			// step the world
			this.step();
			// return true indicating we performed a simulation step
			return true;
		}
		return false;
	}
	
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
	public boolean update(double elapsedTime, int maximumSteps) {
		// make sure the update time is greater than zero
		if (elapsedTime < 0.0) elapsedTime = 0.0;
		// update the time
		this.time += elapsedTime;
		// check the frequency in settings
		double invhz = this.settings.getStepFrequency();
		// see if we should update or not
		int steps = 0;
		while (this.time >= invhz && steps < maximumSteps) {
			// update the step
			this.step.update(invhz);
			// reset the time
			this.time = this.time - invhz;
			// step the world
			this.step();
			// increment the number of steps
			steps++;
		}
		return steps > 0;
	}
	
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
	public void updatev(double elapsedTime) {
		// make sure the update time is greater than zero
		if (elapsedTime <= 0.0) return;
		// update the step
		this.step.update(elapsedTime);
		// step the world
		this.step();
	}
	
	/**
	 * Performs the given number of simulation steps using the step frequency in {@link Settings}.
	 * <p>
	 * This method immediately returns if the given step count is less than or equal to
	 * zero.
	 * @param steps the number of simulation steps to perform
	 */
	public void step(int steps) {
		// get the frequency from settings
		double invhz = this.settings.getStepFrequency();
		// perform the steps
		this.step(steps, invhz);
	}
	
	/**
	 * Performs the given number of simulation steps using the given elapsed time for each step.
	 * <p>
	 * This method immediately returns if the given elapsedTime or step count is less than or equal to
	 * zero.
	 * @param steps the number of simulation steps to perform
	 * @param elapsedTime the elapsed time for each step
	 */
	public void step(int steps, double elapsedTime) {
		// make sure the number of steps is greather than zero
		if (steps <= 0) return;
		// make sure the update time is greater than zero
		if (elapsedTime <= 0.0) return;
		// perform the steps
		for (int i = 0; i < steps; i++) {
			// update the step object
			this.step.update(elapsedTime);
			// step the world
			this.step();
		}
	}
	
	/**
	 * Performs one time step of the {@link World} using the current {@link Step}.
	 * <p>
	 * This method advances the world by the elapsed time in the {@link Step} object
	 * and performs collision resolution and constraint solving.
	 * <p>
	 * This method will perform a collision detection sweep at the end to ensure that
	 * callers of the world have the latest collision information. If the {@link #isUpdateRequired()}
	 * method returns true, a collision detection sweep will be performed before doing
	 * collision resolution.  See the {@link #setUpdateRequired(boolean)} method for details
	 * on when this flag should be set.
	 * <p>
	 * Use the various listeners to listen for events during the execution of
	 * this method.
	 * <p>
	 * If possible use the {@link StepListener#postSolve(Step, World)} method to update any
	 * bodies or joints to increase performance.
	 * <p>
	 * Most {@link Listener}s do not allow modification of the world, bodies, joints, etc in
	 * there methods. It's recommended that any of modification be performed in a {@link StepListener}
	 * or after this method has returned.
	 */
	protected void step() {
		// get all the step listeners
		List<StepListener> stepListeners = this.getListeners(StepListener.class);
		List<ContactListener> contactListeners = this.getListeners(ContactListener.class);
		
		int sSize = stepListeners.size();
		
		// notify the step listeners
		for (int i = 0; i < sSize; i++) {
			StepListener sl = stepListeners.get(i);
			sl.begin(this.step, this);
		}
		
		// check if we need to update the contacts first
		if (this.updateRequired) {
			// if so then update the contacts
			this.detect();
			// notify that an update was performed
			for (int i = 0; i < sSize; i++) {
				StepListener sl = stepListeners.get(i);
				sl.updatePerformed(this.step, this);
			}
			// set the update required flag to false
			this.updateRequired = false;
		}
		
		// notify of all the contacts that will be solved and all the sensed contacts
		this.contactManager.preSolveNotify(contactListeners);
		
		// check for CCD
		ContinuousDetectionMode continuousDetectionMode = this.settings.getContinuousDetectionMode();
		
		// get the number of bodies
		int size = this.bodies.size();
		
		// test for out of bounds objects
		// clear the body contacts
		// clear the island flag
		// save the current transform for CCD
		for (int i = 0; i < size; i++) {
			Body body = this.bodies.get(i);
			// remove the island flag
			body.setOnIsland(false);
			// we only need to save the old transform for CCD so don't
			// bother if its completely disabled
			if (continuousDetectionMode != ContinuousDetectionMode.NONE) {
				// save the current transform into the previous transform
				body.transform0.set(body.getTransform());
			}
		}
		
		// clear the joint island flags
		int jSize = this.joints.size();
		for (int i = 0; i < jSize; i++) {
			// get the joint
			Constraint joint = this.joints.get(i);
			// set the island flag to false
			joint.setOnIsland(false);
		}
		
		// perform a depth first search of the contact graph
		// to create islands for constraint solving
		Deque<Body> stack = new ArrayDeque<Body>(size);

		// temp storage
		// we put these here so we can implicitly convert from joint and
		// contact constraint to constraint so that we have package private
		// access to the isOnIsland and setOnIsland methods
		Joint joint;
		ContactConstraint contactConstraint;
		Constraint constraint;
		
		// loop over the bodies and their contact edges to create the islands
		for (int i = 0; i < size; i++) {
			Body seed = this.bodies.get(i);
			// skip if asleep, in active, static, or already on an island
			if (seed.isOnIsland() || seed.isAsleep() || !seed.isActive() || seed.isStatic()) continue;
			
			// set the island to the reusable island
			Island island = this.island;
			
			island.clear();
			stack.clear();
			stack.push(seed);
			
			while (stack.size() > 0) {
				// get the next body
				Body body = stack.pop();
				// add it to the island
				island.add(body);
				// flag that it has been added
				body.setOnIsland(true);
				// make sure the body is awake
				body.setAsleep(false);
				// if its static then continue since we dont want the
				// island to span more than one static object
				// this keeps the size of the islands small
				if (body.isStatic()) continue;
				// loop over the contact edges of this body
				int ceSize = body.contacts.size();
				for (int j = 0; j < ceSize; j++) {
					ContactEdge contactEdge = body.contacts.get(j);
					// get the contact constraint
					constraint = contactConstraint = contactEdge.interaction;
					// skip sensor contacts
					// check if the contact constraint has already been added to an island
					if (contactConstraint.isSensor() || constraint.isOnIsland()) continue;
					// get the other body
					Body other = contactEdge.other;
					// add the contact constraint to the island list
					island.add(contactConstraint);
					// set the island flag on the contact constraint
					constraint.setOnIsland(true);
					// has the other body been added to an island yet?
					if (!other.isOnIsland()) {
						// if not then add this body to the stack
						stack.push(other);
						other.setOnIsland(true);
					}
				}
				// loop over the joint edges of this body
				int jeSize = body.joints.size();
				for (int j = 0; j < jeSize; j++) {
					// get the joint edge
					JointEdge jointEdge = body.joints.get(j);
					// get the joint
					constraint = joint = jointEdge.interaction;
					// check if the joint is inactive
					if (!joint.isActive() || constraint.isOnIsland()) continue;
					// get the other body
					Body other = jointEdge.other;
					// check if the joint has already been added to an island
					// or if the other body is not active
					if (!other.isActive()) continue;
					// add the joint to the island
					island.add(joint);
					// set the island flag on the joint
					constraint.setOnIsland(true);
					// check if the other body has been added to an island
					if (!other.isOnIsland()) {
						// if not then add the body to the stack
						stack.push(other);
						other.setOnIsland(true);
					}
				}
			}
			
			// solve the island
			island.solve(this.contactConstraintSolver, this.gravity, this.step, this.settings);
			
			// allow static bodies to participate in other islands
			for (int j = 0; j < size; j++) {
				Body body = this.bodies.get(j);
				if (body.isStatic()) {
					body.setOnIsland(false);
				}
			}
		}
		
		// allow memory to be reclaimed
		stack.clear();
		this.island.clear();
		
		// notify of the all solved contacts
		this.contactManager.postSolveNotify(contactListeners);
		
		// make sure CCD is enabled
		if (continuousDetectionMode != ContinuousDetectionMode.NONE) {
			// solve time of impact
			this.solveTOI(continuousDetectionMode);
		}
		
		// notify the step listener
		for (int i = 0; i < sSize; i++) {
			StepListener sl = stepListeners.get(i);
			sl.postSolve(this.step, this);
		}
		
		// after all has been updated find new contacts
		// this is done so that the user has the latest contacts
		// and the broadphase has the latest AABBs, etc.
		this.detect();
		
		// set the update required flag to false
		this.updateRequired = false;
		
		// notify the step listener
		for (int i = 0; i < sSize; i++) {
			StepListener sl = stepListeners.get(i);
			sl.end(this.step, this);
		}
	}
	
	/**
	 * Finds new contacts for all bodies in this world.
	 * <p>
	 * This method performs the following:
	 * <ol>
	 * 	<li>Checks for out of bound bodies</li>
	 * 	<li>Updates the broad-phase using the current body positions</li>
	 * 	<li>Performs broad-phase collision detection</li>
	 * 	<li>Performs narrow-phase collision detection</li>
	 * 	<li>Performs manifold solving</li>
	 * 	<li>Adds contacts to the contact manager</li>
	 * 	<li>Warm starts the contacts</li>
	 * </ol>
	 * <p>
	 * This method will notify all bounds and collision listeners.  If any {@link CollisionListener}
	 * returns false, the collision is ignored.
	 * <p>
	 * This method also notifies any {@link ContactListener}s.
	 * @since 3.0.0
	 */
	protected void detect() {
		// get the bounds listeners
		List<BoundsListener> boundsListeners = this.getListeners(BoundsListener.class);
		List<CollisionListener> collisionListeners = this.getListeners(CollisionListener.class);
		
		// get the number of bodies
		int size = this.bodies.size();
		int blSize = boundsListeners.size();
		int clSize = collisionListeners.size();
		
		// test for out of bounds objects
		// clear the body contacts
		// update the broadphase
		for (int i = 0; i < size; i++) {
			Body body = this.bodies.get(i);
			// skip if already not active
			if (!body.isActive()) continue;
			// clear all the old contacts
			body.contacts.clear();
			// check if bounds have been set
			// check if the body is out of bounds
			if (this.bounds != null && this.bounds.isOutside(body)) {
				// set the body to inactive
				body.setActive(false);
				// if so, notify via the listeners
				for (int j = 0; j < blSize; j++) {
					BoundsListener bl = boundsListeners.get(j);
					bl.outside(body);
				}
			}
			// update the broadphase with the new position/orientation
			this.broadphaseDetector.update(body);
		}
		
		// make sure there are some bodies
		if (size > 0) {
			// test for collisions via the broad-phase
			List<BroadphasePair<Body, BodyFixture>> pairs = this.broadphaseDetector.detect(this.detectBroadphaseFilter);
			int pSize = pairs.size();
			boolean allow = true;
			
			// using the broad-phase results, test for narrow-phase
			for (int i = 0; i < pSize; i++) {
				BroadphasePair<Body, BodyFixture> pair = pairs.get(i);
				
				// get the bodies
				Body body1 = pair.getCollidable1();
				Body body2 = pair.getCollidable2();
				BodyFixture fixture1 = pair.getFixture1();
				BodyFixture fixture2 = pair.getFixture2();
				
				allow = true;
				for (int j = 0; j < clSize; j++) {
					CollisionListener cl = collisionListeners.get(j);
					if (!cl.collision(body1, fixture1, body2, fixture2)) {
						// if any collision listener returned false then skip this collision
						// we must allow all the listeners to get notified first, then skip
						// the collision
						allow = false;
					}
				}
				if (!allow) continue;
				
				// get their transforms
				Transform transform1 = body1.getTransform();
				Transform transform2 = body2.getTransform();
				
				Convex convex2 = fixture2.getShape();
				Convex convex1 = fixture1.getShape();
				
				Penetration penetration = new Penetration();
				// test the two convex shapes
				if (this.narrowphaseDetector.detect(convex1, transform1, convex2, transform2, penetration)) {
					// check for zero penetration
					if (penetration.getDepth() == 0.0) {
						// this should only happen if numerical error occurs
						continue;
					}
					// perform post processing
					if (this.narrowphasePostProcessor != null) {
						this.narrowphasePostProcessor.process(convex1, transform1, convex2, transform2, penetration);
					}
					// notify of the narrow-phase collision
					allow = true;
					for (int j = 0; j < clSize; j++) {
						CollisionListener cl = collisionListeners.get(j);
						if (!cl.collision(body1, fixture1, body2, fixture2, penetration)) {
							// if any collision listener returned false then skip this collision
							// we must allow all the listeners to get notified first, then skip
							// the collision
							allow = false;
						}
					}
					if (!allow) continue;
					Manifold manifold = new Manifold();
					// if there is penetration then find a contact manifold
					// using the filled in penetration object
					if (this.manifoldSolver.getManifold(penetration, convex1, transform1, convex2, transform2, manifold)) {
						// check for zero points
						if (manifold.getPoints().size() == 0) {
							// this should only happen if numerical error occurs
							continue;
						}
						// notify of the manifold solving result
						allow = true;
						for (int j = 0; j < clSize; j++) {
							CollisionListener cl = collisionListeners.get(j);
							if (!cl.collision(body1, fixture1, body2, fixture2, manifold)) {
								// if any collision listener returned false then skip this collision
								// we must allow all the listeners to get notified first, then skip
								// the collision
								allow = false;
							}
						}
						if (!allow) continue;
						// create a contact constraint
						ContactConstraint contactConstraint = new ContactConstraint(body1, fixture1, 
								                                                    body2, fixture2, 
								                                                    manifold,
								                                                    this.coefficientMixer.mixFriction(fixture1.getFriction(), fixture2.getFriction()),
								                                                    this.coefficientMixer.mixRestitution(fixture1.getRestitution(), fixture2.getRestitution()));
						
						allow = true;
						// notify of the created contact constraint
						for (int j = 0; j < clSize; j++) {
							CollisionListener cl = collisionListeners.get(j);
							if (!cl.collision(contactConstraint)) {
								// if any collision listener returned false then skip this collision
								// we must allow all the listeners to get notified first, then skip
								// the collision
								allow = false;
							}
						}
						if (!allow) continue;
						
						// add a contact edge to both bodies
						ContactEdge contactEdge1 = new ContactEdge(body2, contactConstraint);
						ContactEdge contactEdge2 = new ContactEdge(body1, contactConstraint);
						body1.contacts.add(contactEdge1);
						body2.contacts.add(contactEdge2);
						// add the contact constraint to the contact manager
						this.contactManager.queue(contactConstraint);
					}
				}
			}
		}
		
		// warm start the contact constraints
		this.contactManager.updateAndNotify(this.getListeners(ContactListener.class), this.settings);
	}
	
	/**
	 * Solves the time of impact for all the {@link Body}s in this {@link World}.
	 * <p>
	 * This method solves for the time of impact for each {@link Body} iteratively
	 * and pairwise.
	 * <p>
	 * The cases considered are dependent on the given collision detection mode.
	 * <p>
	 * Cases skipped (including the converse of the above):
	 * <ul>
	 * <li>Inactive, asleep, or non-moving bodies</li>
	 * <li>Bodies connected via a joint with the collision flag set to false</li>
	 * <li>Bodies already in contact</li>
	 * <li>Fixtures whose filters return false</li>
	 * <li>Sensor fixtures</li>
	 * </ul>
	 * @param mode the continuous collision detection mode
	 * @see ContinuousDetectionMode
	 * @since 1.2.0
	 */
	protected void solveTOI(ContinuousDetectionMode mode) {
		List<TimeOfImpactListener> listeners = this.getListeners(TimeOfImpactListener.class);
		// get the number of bodies
		int size = this.bodies.size();
		
		// check the CCD mode
		boolean bulletsOnly = (mode == ContinuousDetectionMode.BULLETS_ONLY);
		
		// loop over all the bodies and find the minimum TOI for each
		// dynamic body
		for (int i = 0; i < size; i++) {
			// get the body
			Body body = this.bodies.get(i);
			
			// if we are only doing CCD on bullets only, then check
			// to make sure that the current body is a bullet
			if (bulletsOnly && !body.isBullet()) continue;
			
			// otherwise we process all dynamic bodies
				
			// we don't process kinematic or static bodies except with
			// dynamic bodies (in other words b1 must always be a dynamic
			// body)
			if (body.isKinematic() || body.isStatic()) continue;
			
			// don't bother with bodies that did not have their
			// positions integrated, if they were not added to an island then
			// that means they didn't move
			
			// we can also check for sleeping bodies and skip those since
			// they will only be asleep after being stationary for a set
			// time period
			if (!body.isOnIsland() || body.isAsleep()) continue;

			// solve for time of impact
			this.solveTOI(body, listeners);
		}
	}
	
	/**
	 * Solves the time of impact for the given {@link Body}.
	 * <p>
	 * This method will find the first {@link Body} that the given {@link Body}
	 * collides with unless ignored via the {@link TimeOfImpactListener}.
	 * <p>
	 * If any {@link TimeOfImpactListener} doesn't allow the collision then the collision
	 * is ignored.
	 * <p>
	 * After the first {@link Body} is found the two {@link Body}s are interpolated
	 * to the time of impact.
	 * <p>
	 * Then the {@link Body}s are position solved using the {@link TimeOfImpactSolver}
	 * to force the {@link Body}s into collision.  This causes the discrete collision
	 * detector to detect the collision on the next time step.
	 * @param body1 the {@link Body}
	 * @param listeners the list of {@link TimeOfImpactListener}s
	 * @since 3.1.0
	 */
	protected void solveTOI(Body body1, List<TimeOfImpactListener> listeners) {
		int size = this.bodies.size();
		
		// generate a swept AABB for this body
		AABB aabb1 = body1.createSweptAABB();
		boolean bullet = body1.isBullet();
		
		// setup the initial time bounds [0, 1]
		double t1 = 0.0;
		double t2 = 1.0;
		
		// save the minimum time of impact and body
		TimeOfImpact minToi = null;
		Body minBody = null;
		
		// loop over all the other bodies to find the minimum TOI
		for (int i = 0; i < size; i++) {
			// get the other body
			Body body2 = this.bodies.get(i);

			// skip this test if they are the same body
			if (body1 == body2) continue;
			
			// make sure the other body is active
			if (!body2.isActive()) continue;

			// skip other dynamic bodies; we only do TOI for
			// dynamic vs. static/kinematic unless its a bullet
			if (body2.isDynamic() && !bullet) continue;
			
			// check for connected pairs who's collision is not allowed
			if (body1.isConnected(body2, false)) continue;
			
			// check for bodies already in collision
			if (body1.isInContact(body2)) continue;

			// create a swept AABB for the other body
			AABB aabb2 = body2.createSweptAABB();
			// if the swept AABBs don't overlap then don't bother testing them
			if (!aabb1.overlaps(aabb2)) continue; 

			TimeOfImpact toi = new TimeOfImpact();
			int fc1 = body1.getFixtureCount();
			int fc2 = body2.getFixtureCount();
			
			// get the velocities for the time step since we want
			// [t1, t2] to be bound to this time step
			double dt = this.step.getDeltaTime();
			// the linear and angular velocities should match what 
			// we did when we advanced the position. alternatively
			// we could calculate these from the start and end transforms
			// but this has the problem of not knowing which direction
			// the angular velocity is going (clockwise or anti-clockwise).
			// however, this also has the problem of being different that
			// the way the bodies are advanced in the Island solving
			// (for now they are the same, but could be changed in the
			// future).
			Vector2 v1 = body1.getLinearVelocity().product(dt);
			Vector2 v2 = body2.getLinearVelocity().product(dt);
			double av1 = body1.getAngularVelocity() * dt;
			double av2 = body2.getAngularVelocity() * dt;
			
			Transform tx1 = body1.getInitialTransform();
			Transform tx2 = body2.getInitialTransform();
			
			// test against all fixture pairs taking the fixture
			// with the smallest time of impact
			for (int j = 0; j < fc1; j++) {
				BodyFixture f1 = body1.getFixture(j);
				
				// skip sensor fixtures
				if (f1.isSensor()) continue;
				
				for (int k = 0; k < fc2; k++) {
					BodyFixture f2 = body2.getFixture(k);
					
					// skip sensor fixtures
					if (f2.isSensor()) continue;

					Filter filter1 = f1.getFilter();
					Filter filter2 = f2.getFilter();
					
					// make sure the fixture filters allow the collision
					if (!filter1.isAllowed(filter2)) {
						continue;
					}
					
					Convex c1 = f1.getShape();
					Convex c2 = f2.getShape();
					
					// get the time of impact for the fixture pair
					if (this.timeOfImpactDetector.getTimeOfImpact(c1, tx1, v1, av1, c2, tx2, v2, av2, t1, t2, toi)) {
						// get the time of impact
						double t = toi.getTime();
						// check if the time of impact is less than
						// the current time of impact
						if (t < t2) {
							// if it is then ask the listeners if we should use this collision
							boolean allow = true;
							for (TimeOfImpactListener tl : listeners) {
								if (!tl.collision(body1, f1, body2, f2, toi)) {
									// if any toi listener doesnt allow it, then don't allow it
									// we need to allow all listeners to be notified before we continue
									allow = false;
								}
							}
							if (allow) {
								// set the new upper bound
								t2 = t;
								// save the minimum toi and body
								minToi = toi;
								minBody = body2;
							}
						}
					}
				}
			}
			// if the bodies are intersecting or do not intersect
			// within the range of motion then skip this body
			// and move to the next
		}
		
		// make sure the time of impact is not null
		if (minToi != null) {
			// get the time of impact info
			double t = minToi.getTime();
			
			// move the dynamic body to the time of impact
			body1.transform0.lerp(body1.getTransform(), t, body1.getTransform());
			// check if the other body is dynamic
			if (minBody.isDynamic()) {
				// if the other body is dynamic then interpolate its transform also
				minBody.transform0.lerp(minBody.getTransform(), t, minBody.getTransform());
			}
			// this should bring the bodies within d distance from one another
			// we need to move the bodies more so that they are in collision
			// so that on the next time step they are solved by the discrete
			// collision detector
			
			// performs position correction on the body/bodies so that they are
			// in collision and will be detected in the next time step
			this.timeOfImpactSolver.solve(body1, minBody, minToi, this.settings);
			
			// this method does not conserve time
		}
	}

	/**
	 * Performs a raycast against all the {@link Body}s in the {@link World}.
	 * <p>
	 * The given {@link RaycastResult} list, results, will be filled with the raycast results
	 * if the given ray intersected any bodies.
	 * <p>
	 * The {@link RaycastResult} class implements the Comparable interface to allow sorting by
	 * distance from the ray's origin.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Bodies that contain the start of the ray will not be included in the results.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * @param start the start point
	 * @param end the end point
	 * @param ignoreSensors true if sensor {@link BodyFixture}s should be ignored
	 * @param all true if all intersected {@link Body}s should be returned; false if only the closest {@link Body} should be returned
	 * @param results a list to contain the results of the raycast
	 * @return boolean true if at least one {@link Body} was intersected by the {@link Ray}
	 * @throws NullPointerException if start, end, or results is null
	 * @see #raycast(Ray, double, boolean, boolean, List)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 2.0.0
	 */
	public boolean raycast(Vector2 start, Vector2 end, boolean ignoreSensors, boolean all, List<RaycastResult> results) {
		return this.raycast(start, end, null, ignoreSensors, true, all, results);
	}
	
	/**
	 * Performs a raycast against all the {@link Body}s in the {@link World}.
	 * <p>
	 * The given {@link RaycastResult} list, results, will be filled with the raycast results
	 * if the given ray intersected any bodies.
	 * <p>
	 * The {@link RaycastResult} class implements the Comparable interface to allow sorting by
	 * distance from the ray's origin.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Bodies that contain the start of the ray will not be included in the results.
	 * @param start the start point
	 * @param end the end point
	 * @param ignoreSensors true if sensor {@link BodyFixture}s should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param all true if all intersected {@link Body}s should be returned; false if only the closest {@link Body} should be returned
	 * @param results a list to contain the results of the raycast
	 * @return boolean true if at least one {@link Body} was intersected by the {@link Ray}
	 * @throws NullPointerException if start, end, or results is null
	 * @see #raycast(Ray, double, boolean, boolean, boolean, List)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 3.1.9
	 */
	public boolean raycast(Vector2 start, Vector2 end, boolean ignoreSensors, boolean ignoreInactive, boolean all, List<RaycastResult> results) {
		return this.raycast(start, end, null, ignoreSensors, ignoreInactive, all, results);
	}
	
	/**
	 * Performs a raycast against all the {@link Body}s in the {@link World}.
	 * <p>
	 * The given {@link RaycastResult} list, results, will be filled with the raycast results
	 * if the given ray intersected any bodies.
	 * <p>
	 * The {@link RaycastResult} class implements the Comparable interface to allow sorting by
	 * distance from the ray's origin.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Bodies that contain the start of the ray will not be included in the results.
	 * @param start the start point
	 * @param end the end point
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor {@link BodyFixture}s should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param all true if all intersected {@link Body}s should be returned; false if only the closest {@link Body} should be returned
	 * @param results a list to contain the results of the raycast
	 * @return boolean true if at least one {@link Body} was intersected by the {@link Ray}
	 * @throws NullPointerException if start, end, or results is null
	 * @see #raycast(Ray, double, Filter, boolean, boolean, boolean, List)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 3.1.9
	 */
	public boolean raycast(Vector2 start, Vector2 end, Filter filter, boolean ignoreSensors, boolean ignoreInactive, boolean all, List<RaycastResult> results) {
		// create the ray and obtain the maximum length
		Vector2 d = start.to(end);
		double maxLength = d.normalize();
		Ray ray = new Ray(start, d);
		// call the raycast method
		return this.raycast(ray, maxLength, filter, ignoreSensors, ignoreInactive, all, results);
	}
	
	/**
	 * Performs a raycast against all the {@link Body}s in the {@link World}.
	 * <p>
	 * The given {@link RaycastResult} list, results, will be filled with the raycast results
	 * if the given ray intersected any bodies.
	 * <p>
	 * The {@link RaycastResult} class implements the Comparable interface to allow sorting by
	 * distance from the ray's origin.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * Pass 0 into the maxLength field to specify an infinite length {@link Ray}.
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Bodies that contain the start of the ray will not be included in the results.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * @param ray the {@link Ray}
	 * @param maxLength the maximum length of the ray; 0 for infinite length
	 * @param ignoreSensors true if sensor {@link BodyFixture}s should be ignored
	 * @param all true if all intersected {@link Body}s should be returned; false if only the closest {@link Body} should be returned
	 * @param results a list to contain the results of the raycast
	 * @return boolean true if at least one {@link Body} was intersected by the given {@link Ray}
	 * @throws NullPointerException if ray or results is null
	 * @see #raycast(Vector2, Vector2, boolean, boolean, List)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 2.0.0
	 */
	public boolean raycast(Ray ray, double maxLength, boolean ignoreSensors, boolean all, List<RaycastResult> results) {
		return this.raycast(ray, maxLength, null, ignoreSensors, true, all, results);
	}

	/**
	 * Performs a raycast against all the {@link Body}s in the {@link World}.
	 * <p>
	 * The given {@link RaycastResult} list, results, will be filled with the raycast results
	 * if the given ray intersected any bodies.
	 * <p>
	 * The {@link RaycastResult} class implements the Comparable interface to allow sorting by
	 * distance from the ray's origin.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * Pass 0 into the maxLength field to specify an infinite length {@link Ray}.
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Bodies that contain the start of the ray will not be included in the results.
	 * @param ray the {@link Ray}
	 * @param maxLength the maximum length of the ray; 0 for infinite length
	 * @param ignoreSensors true if sensor {@link BodyFixture}s should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param all true if all intersected {@link Body}s should be returned; false if only the closest {@link Body} should be returned
	 * @param results a list to contain the results of the raycast
	 * @return boolean true if at least one {@link Body} was intersected by the given {@link Ray}
	 * @throws NullPointerException if ray or results is null
	 * @see #raycast(Vector2, Vector2, boolean, boolean, boolean, List)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 3.1.9
	 */
	public boolean raycast(Ray ray, double maxLength, boolean ignoreSensors, boolean ignoreInactive, boolean all, List<RaycastResult> results) {
		return this.raycast(ray, maxLength, null, ignoreSensors, ignoreInactive, all, results);
	}
	
	/**
	 * Performs a raycast against all the {@link Body}s in the {@link World}.
	 * <p>
	 * The given {@link RaycastResult} list, results, will be filled with the raycast results
	 * if the given ray intersected any bodies.
	 * <p>
	 * The {@link RaycastResult} class implements the Comparable interface to allow sorting by
	 * distance from the ray's origin.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * Pass 0 into the maxLength field to specify an infinite length {@link Ray}.
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Bodies that contain the start of the ray will not be included in the results.
	 * @param ray the {@link Ray}
	 * @param maxLength the maximum length of the ray; 0 for infinite length
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor {@link BodyFixture}s should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param all true if all intersected {@link Body}s should be returned; false if only the closest {@link Body} should be returned
	 * @param results a list to contain the results of the raycast
	 * @return boolean true if at least one {@link Body} was intersected by the given {@link Ray}
	 * @throws NullPointerException if ray or results is null
	 * @see #raycast(Vector2, Vector2, Filter, boolean, boolean, boolean, List)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 3.1.9
	 */
	public boolean raycast(Ray ray, double maxLength, Filter filter, boolean ignoreSensors, boolean ignoreInactive, boolean all, List<RaycastResult> results) {
		List<RaycastListener> listeners = this.getListeners(RaycastListener.class);
		int rlSize = listeners.size();
		// check for the desired length
		double max = 0.0;
		if (maxLength > 0.0) {
			max = maxLength;
		}
		// create a raycast result
		RaycastResult result = null;
		RaycastBroadphaseFilter bpFilter = new RaycastBroadphaseFilter(ignoreInactive, ignoreSensors, filter);
		// filter using the broadphase first
		List<BroadphaseItem<Body, BodyFixture>> items = this.broadphaseDetector.raycast(ray, maxLength, bpFilter);
		// loop over the list of bodies testing each one
		int size = items.size();
		boolean found = false;
		boolean allow = true;
		for (int i = 0; i < size; i++) {
			// get a body to test
			BroadphaseItem<Body, BodyFixture> item = items.get(i);
			Body body = item.getCollidable();
			BodyFixture fixture = item.getFixture();
			Transform transform = body.getTransform();

			// create a raycast object to store the result
			Raycast raycast = new Raycast();

			// notify the listeners to see if we should test this fixture
			allow = true;
			for (int j = 0; j < rlSize; j++) {
				RaycastListener rl = listeners.get(j);
				// see if we should test this fixture
				if (!rl.allow(ray, body, fixture)) {
					allow = false;
				}
			}
			if (!allow) continue;
			// get the convex shape
			Convex convex = fixture.getShape();
			// perform the raycast
			if (this.raycastDetector.raycast(ray, max, convex, transform, raycast)) {
				// notify the listeners to see if we should allow this result
				allow = true;
				for (int j = 0; j < rlSize; j++) {
					RaycastListener rl = listeners.get(j);
					// see if we should test this fixture
					if (!rl.allow(ray, body, fixture, raycast)) {
						allow = false;
					}
				}
				if (!allow) continue;
				
				if (!all) {
					if (result == null) {
						result = new RaycastResult(body, fixture, raycast);
						results.add(result);
						found = true;
					} else {
						result.body = body;
						result.fixture = fixture;
						result.raycast = raycast;
					}
					// we are only looking for the closest so
					// set the new maximum
					max = result.raycast.getDistance();
				} else {
					// add this result to the results
					results.add(new RaycastResult(body, fixture, raycast));
					found = true;
				}
			}
		}
		
		return found;
	}

	/**
	 * Performs a raycast against the given {@link Body} and returns true
	 * if the ray intersects the body.
	 * <p>
	 * The given {@link RaycastResult} object, result, will be filled with the raycast result
	 * if the given ray intersected the given body. 
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Returns false if the start position of the ray lies inside the given body.
	 * @param start the start point
	 * @param end the end point
	 * @param body the {@link Body} to test
	 * @param ignoreSensors whether or not to ignore sensor {@link BodyFixture}s
	 * @param result the raycast result
	 * @return boolean true if the {@link Ray} intersects the {@link Body}
	 * @throws NullPointerException if start, end, body, or result is null
	 * @see #raycast(Ray, Body, double, boolean, RaycastResult)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 2.0.0
	 */
	public boolean raycast(Vector2 start, Vector2 end, Body body, boolean ignoreSensors, RaycastResult result) {
		return this.raycast(start, end, body, null, ignoreSensors, result);
	}
	
	/**
	 * Performs a raycast against the given {@link Body} and returns true
	 * if the ray intersects the body.
	 * <p>
	 * The given {@link RaycastResult} object, result, will be filled with the raycast result
	 * if the given ray intersected the given body. 
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Returns false if the start position of the ray lies inside the given body.
	 * @param start the start point
	 * @param end the end point
	 * @param body the {@link Body} to test
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors whether or not to ignore sensor {@link BodyFixture}s
	 * @param result the raycast result
	 * @return boolean true if the {@link Ray} intersects the {@link Body}
	 * @throws NullPointerException if start, end, body, or result is null
	 * @see #raycast(Ray, Body, double, Filter, boolean, RaycastResult)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 3.1.9
	 */
	public boolean raycast(Vector2 start, Vector2 end, Body body, Filter filter, boolean ignoreSensors, RaycastResult result) {
		// create the ray and obtain the maximum length
		Vector2 d = start.to(end);
		double maxLength = d.normalize();
		Ray ray = new Ray(start, d);
		// call the raycast method
		return this.raycast(ray, body, maxLength, filter, ignoreSensors, result);
	}
	
	/**
	 * Performs a raycast against the given {@link Body} and returns true
	 * if the ray intersects the body.
	 * <p>
	 * The given {@link RaycastResult} object, result, will be filled with the raycast result
	 * if the given ray intersected the given body.
	 * <p>
	 * Pass 0 into the maxLength field to specify an infinite length {@link Ray}.
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Returns false if the start position of the ray lies inside the given body.
	 * @param ray the {@link Ray} to cast
	 * @param body the {@link Body} to test
	 * @param maxLength the maximum length of the ray; 0 for infinite length
	 * @param ignoreSensors whether or not to ignore sensor {@link BodyFixture}s
	 * @param result the raycast result
	 * @return boolean true if the {@link Ray} intersects the {@link Body}
	 * @throws NullPointerException if ray, body, or result is null
	 * @see #raycast(Vector2, Vector2, Body, boolean, RaycastResult)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 2.0.0
	 */
	public boolean raycast(Ray ray, Body body, double maxLength, boolean ignoreSensors, RaycastResult result) {
		return this.raycast(ray, body, maxLength, null, ignoreSensors, result);
	}
	
	/**
	 * Performs a raycast against the given {@link Body} and returns true
	 * if the ray intersects the body.
	 * <p>
	 * The given {@link RaycastResult} object, result, will be filled with the raycast result
	 * if the given ray intersected the given body.
	 * <p>
	 * Pass 0 into the maxLength field to specify an infinite length {@link Ray}.
	 * <p>
	 * All raycasts pass through the {@link RaycastListener}s before being tested.  If <b>any</b>
	 * {@link RaycastListener} doesn't allow the raycast then the body will not be tested.
	 * <p>
	 * Returns false if the start position of the ray lies inside the given body.
	 * @param ray the {@link Ray} to cast
	 * @param body the {@link Body} to test
	 * @param maxLength the maximum length of the ray; 0 for infinite length
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors whether or not to ignore sensor {@link BodyFixture}s
	 * @param result the raycast result
	 * @return boolean true if the {@link Ray} intersects the {@link Body}
	 * @throws NullPointerException if ray, body, or result is null
	 * @see #raycast(Vector2, Vector2, Body, Filter, boolean, RaycastResult)
	 * @see RaycastListener#allow(Ray, Body, BodyFixture)
	 * @since 3.1.9
	 */
	public boolean raycast(Ray ray, Body body, double maxLength, Filter filter, boolean ignoreSensors, RaycastResult result) {
		List<RaycastListener> listeners = this.getListeners(RaycastListener.class);
		int rlSize = listeners.size();
		boolean allow = true;
		// get the number of fixtures
		int size = body.getFixtureCount();
		// get the body transform
		Transform transform = body.getTransform();
		// set the maximum length
		double max = 0.0;
		if (maxLength > 0.0) {
			max = maxLength;
		}
		// create a raycast object to store the result
		Raycast raycast = new Raycast();
		// loop over the fixtures finding the closest one
		boolean found = false;
		for (int i = 0; i < size; i++) {
			// get the fixture
			BodyFixture fixture = body.getFixture(i);
			// check for sensor
			if (ignoreSensors && fixture.isSensor()) {
				// skip this fixture
				continue;
			}
			// check against the filter
			if (filter != null && !filter.isAllowed(fixture.getFilter())) {
				continue;
			}
			// notify the listeners to see if we should test this fixture
			allow = true;
			for (int j = 0; j < rlSize; j++) {
				RaycastListener rl = listeners.get(j);
				// see if we should test this fixture
				if (!rl.allow(ray, body, fixture)) {
					allow = false;
				}
			}
			if (!allow) continue;
			// get the convex shape
			Convex convex = fixture.getShape();
			// perform the raycast
			if (this.raycastDetector.raycast(ray, max, convex, transform, raycast)) {
				// notify the listeners to see if we should allow this result
				allow = true;
				for (int j = 0; j < rlSize; j++) {
					RaycastListener rl = listeners.get(j);
					// see if we should test this fixture
					if (!rl.allow(ray, body, fixture, raycast)) {
						allow = false;
					}
				}
				if (!allow) continue;
				// if the raycast detected a collision then set the new
				// maximum distance
				max = raycast.getDistance();
				// assign the fixture
				result.fixture = fixture;
				// the last raycast will always be the minimum raycast
				// flag that we did get a successful raycast
				found = true;
			}
		}
		
		// we only want to populate the
		// result object if a result was found
		if (found) {
			result.body = body;
			result.raycast = raycast;
		}
		
		return found;
	}
	
	/**
	 * Performs a linear convex cast on the world, placing any detected collisions into the given results list.
	 * <p>
	 * This method does a static test of bodies (in other words, does not take into account the bodies linear
	 * or angular velocity, but rather assumes they are stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.
	 * <p>
	 * The {@link ConvexCastResult} class implements the Comparable interface to allow sorting by
	 * the time of impact.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, only the fixture that has the minimum time of impact will be added to the
	 * results list.
	 * <p>
	 * Bodies in collision with the given convex at the beginning of the cast are not included in the results.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param all true if all hits should be returned; false if only the first should be returned
	 * @param results the list to add the results to
	 * @return boolean true if a collision was found
	 * @since 3.1.5
	 * @see #convexCast(Convex, Transform, Vector2, double, boolean, boolean, boolean, List)
	 */
	public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, boolean ignoreSensors, boolean all, List<ConvexCastResult> results) {
		return this.convexCast(convex, transform, deltaPosition, 0.0, null, ignoreSensors, true, all, results);
	}
	
	/**
	 * Performs a linear convex cast on the world, placing any detected collisions into the given results list.
	 * <p>
	 * This method does a static test of bodies (in other words, does not take into account the bodies linear
	 * or angular velocity, but rather assumes they are stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.
	 * <p>
	 * The {@link ConvexCastResult} class implements the Comparable interface to allow sorting by
	 * the time of impact.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, only the fixture that has the minimum time of impact will be added to the
	 * results list.
	 * <p>
	 * Bodies in collision with the given convex at the beginning of the cast are not included in the results.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param ignoreInactive true if inactive bodies should be ignored in the tests
	 * @param all true if all hits should be returned; false if only the first should be returned
	 * @param results the list to add the results to
	 * @return boolean true if a collision was found
	 * @since 3.1.9
	 * @see #convexCast(Convex, Transform, Vector2, double, boolean, boolean, boolean, List)
	 */
	public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, boolean ignoreSensors, boolean ignoreInactive, boolean all, List<ConvexCastResult> results) {
		return this.convexCast(convex, transform, deltaPosition, 0.0, null, ignoreSensors, ignoreInactive, all, results);
	}

	/**
	 * Performs a linear convex cast on the world, placing any detected collisions into the given results list.
	 * <p>
	 * This method does a static test of bodies (in other words, does not take into account the bodies linear
	 * or angular velocity, but rather assumes they are stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.  
	 * The <code>deltaAngle</code> parameter is the change in angle over the linear cast and is interpolated linearly 
	 * during detection.
	 * <p>
	 * The {@link ConvexCastResult} class implements the Comparable interface to allow sorting by
	 * the time of impact.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, only the fixture that has the minimum time of impact will be added to the
	 * results list.
	 * <p>
	 * Bodies in collision with the given convex at the beginning of the cast are not included in the results.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param deltaAngle &Delta;angle; the change in the angle; this is the change in the angle over the linear period
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param all true if all hits should be returned; false if only the first should be returned
	 * @param results the list to add the results to
	 * @return boolean true if a collision was found
	 * @see #convexCast(Convex, Transform, Vector2, double, boolean, boolean, boolean, List)
	 * @since 3.1.5
	 */
	public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, boolean ignoreSensors, boolean all, List<ConvexCastResult> results) {
		return this.convexCast(convex, transform, deltaPosition, deltaAngle, null, ignoreSensors, true, all, results);
	}

	/**
	 * Performs a linear convex cast on the world, placing any detected collisions into the given results list.
	 * <p>
	 * This method does a static test of bodies (in other words, does not take into account the bodies linear
	 * or angular velocity, but rather assumes they are stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.  
	 * The <code>deltaAngle</code> parameter is the change in angle over the linear cast and is interpolated linearly 
	 * during detection.
	 * <p>
	 * The {@link ConvexCastResult} class implements the Comparable interface to allow sorting by
	 * the time of impact.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, only the fixture that has the minimum time of impact will be added to the
	 * results list.
	 * <p>
	 * Bodies in collision with the given convex at the beginning of the cast are not included in the results.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param deltaAngle &Delta;angle; the change in the angle; this is the change in the angle over the linear period
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param ignoreInactive true if inactive bodies should be ignored in the tests
	 * @param all true if all hits should be returned; false if only the first should be returned
	 * @param results the list to add the results to
	 * @return boolean true if a collision was found
	 * @since 3.1.9
	 */
	public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, boolean ignoreSensors, boolean ignoreInactive, boolean all, List<ConvexCastResult> results) {
		return this.convexCast(convex, transform, deltaPosition, deltaAngle, null, ignoreSensors, ignoreInactive, all, results);
	}
	
	/**
	 * Performs a linear convex cast on the world, placing any detected collisions into the given results list.
	 * <p>
	 * This method does a static test of bodies (in other words, does not take into account the bodies linear
	 * or angular velocity, but rather assumes they are stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.  
	 * The <code>deltaAngle</code> parameter is the change in angle over the linear cast and is interpolated linearly 
	 * during detection.
	 * <p>
	 * The {@link ConvexCastResult} class implements the Comparable interface to allow sorting by
	 * the time of impact.
	 * <p>
	 * If the all flag is false, the results list will only contain the closest result (if any).
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, only the fixture that has the minimum time of impact will be added to the
	 * results list.
	 * <p>
	 * Bodies in collision with the given convex at the beginning of the cast are not included in the results.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param deltaAngle &Delta;angle; the change in the angle; this is the change in the angle over the linear period
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param ignoreInactive true if inactive bodies should be ignored in the tests
	 * @param all true if all hits should be returned; false if only the first should be returned
	 * @param results the list to add the results to
	 * @return boolean true if a collision was found
	 * @since 3.1.9
	 */
	public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, Filter filter, boolean ignoreSensors, boolean ignoreInactive, boolean all, List<ConvexCastResult> results) {
		// get the listeners
		List<ConvexCastListener> listeners = this.getListeners(ConvexCastListener.class);
		int clSize = listeners.size();
		
		// compute a conservative AABB for the motion of the convex
		double radius = convex.getRadius();
		Vector2 startWorldCenter = transform.getTransformed(convex.getCenter());
		AABB startAABB = new AABB(startWorldCenter, radius);
		// linearlly interpolate to get the final transform given the
		// change in position and angle
		Transform finalTransform = transform.lerped(deltaPosition, deltaAngle, 1.0);
		// get the end AABB
		Vector2 endWorldCenter = finalTransform.getTransformed(convex.getCenter());
		AABB endAABB = new AABB(endWorldCenter, radius);
		// union the AABBs to get the swept AABB
		AABB aabb = startAABB.getUnion(endAABB);
		
		ConvexCastResult min = null;
		final Vector2 dp2 = new Vector2();
		double t2 = 1.0;
		boolean found = false;
		boolean allow = true;
		AABBBroadphaseFilter bpFilter = new AABBBroadphaseFilter(ignoreInactive, ignoreSensors, filter);
		// use the broadphase to filter first
		List<BroadphaseItem<Body, BodyFixture>> items = this.broadphaseDetector.detect(aabb, bpFilter);
		// loop over the potential collisions
		for (BroadphaseItem<Body, BodyFixture> item : items) {
			Body body = item.getCollidable();
			BodyFixture fixture = item.getFixture();
			
			// only get the minimum fixture
			double ft2 = t2;
			// find the minimum time of impact for the given convex
			// and the current body
			TimeOfImpact bodyMinToi = null;
			BodyFixture bodyMinFixture = null;
			Transform bodyTransform = body.getTransform();
			
			// notify the listeners to see if we should test this fixture
			allow = true;
			for (int j = 0; j < clSize; j++) {
				ConvexCastListener ccl = listeners.get(j);
				// see if we should test this fixture
				if (!ccl.allow(convex, body, fixture)) {
					allow = false;
				}
			}
			if (!allow) continue;
			
			// get the time of impact
			Convex c = fixture.getShape();
			TimeOfImpact timeOfImpact = new TimeOfImpact();
			// we pass the zero vector and 0 for the change in position and angle for the body
			// since we assume that it is not moving since this is a static test
			if (this.timeOfImpactDetector.getTimeOfImpact(convex, transform, deltaPosition, deltaAngle, c, bodyTransform, dp2, 0.0, 0.0, ft2, timeOfImpact)) {
				// notify the listeners to see if we should test this fixture
				allow = true;
				for (int j = 0; j < clSize; j++) {
					ConvexCastListener ccl = listeners.get(j);
					// see if we should test this fixture
					if (!ccl.allow(convex, body, fixture, timeOfImpact)) {
						allow = false;
					}
				}
				if (!allow) continue;
				
				// only save the minimum for the body
				if (bodyMinToi == null || timeOfImpact.getTime() < bodyMinToi.getTime()) {
					ft2 = timeOfImpact.getTime();
					bodyMinToi = timeOfImpact;
					bodyMinFixture = fixture;
				}
			}
			if (bodyMinToi != null) {
				if (!all) {
					t2 = bodyMinToi.getTime();
					if (min == null || bodyMinToi.getTime() < min.timeOfImpact.getTime()) {
						min = new ConvexCastResult(body, bodyMinFixture, bodyMinToi);
					}
				} else {
					ConvexCastResult result = new ConvexCastResult(body, fixture, timeOfImpact);
					results.add(result);
				}
				found = true;
			}
		}
		
		if (min != null) {
			results.add(min);
		}
		
		// if something is in the list then we know we found a collision
		return found;
	}
	
	/**
	 * Performs a linear convex cast on the given body, placing a detected collision into the given result object.
	 * <p>
	 * This method does a static test of the body (in other words, does not take into account the body's linear
	 * or angular velocity, but rather assumes it is stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, the fixture that has the minimum time of impact will be the result.
	 * <p>
	 * Returns false if the given body and convex are in collision at the beginning of the cast.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param body the body to cast against
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param result the convex cast result
	 * @return boolean true if a collision was found
	 * @since 3.1.5
	 */
	public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, Body body, boolean ignoreSensors, ConvexCastResult result) {
		return this.convexCast(convex, transform, deltaPosition, 0, body, null, ignoreSensors, result);
	}

	/**
	 * Performs a linear convex cast on the given body, placing a detected collision into the given result object.
	 * <p>
	 * This method does a static test of the body (in other words, does not take into account the body's linear
	 * or angular velocity, but rather assumes it is stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.  
	 * The <code>deltaAngle</code> parameter is the change in angle over the linear cast and is interpolated linearly 
	 * during detection.
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, the fixture that has the minimum time of impact will be the result.
	 * <p>
	 * Returns false if the given body and convex are in collision at the beginning of the cast.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param deltaAngle &Delta;angle; the change in the angle; this is the change in the angle over the linear period
	 * @param body the body to cast against
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param result the convex cast result
	 * @return boolean true if a collision was found
	 * @since 3.1.5
	 */
	public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, Body body, boolean ignoreSensors, ConvexCastResult result) {
		return this.convexCast(convex, transform, deltaPosition, deltaAngle, body, null, ignoreSensors, result);
	}
	
	/**
	 * Performs a linear convex cast on the given body, placing a detected collision into the given result object.
	 * <p>
	 * This method does a static test of the body (in other words, does not take into account the body's linear
	 * or angular velocity, but rather assumes it is stationary).
	 * <p>
	 * The <code>deltaPosition</code> parameter is the linear cast vector determining the direction and magnitude of the cast.  
	 * The <code>deltaAngle</code> parameter is the change in angle over the linear cast and is interpolated linearly 
	 * during detection.
	 * <p>
	 * All convex casts pass through the {@link ConvexCastListener}s before being tested.  If <b>any</b>
	 * {@link ConvexCastListener} doesn't allow the convex cast, then the body will not be tested.
	 * <p>
	 * For multi-fixtured bodies, the fixture that has the minimum time of impact will be the result.
	 * <p>
	 * Returns false if the given body and convex are in collision at the beginning of the cast.
	 * @param convex the convex to cast
	 * @param transform the initial position and orientation of the convex
	 * @param deltaPosition &Delta;position; the change in position (the cast length and direction basically)
	 * @param deltaAngle &Delta;angle; the change in the angle; this is the change in the angle over the linear period
	 * @param body the body to cast against
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored in the tests
	 * @param result the convex cast result
	 * @return boolean true if a collision was found
	 * @since 3.1.9
	 */
	public boolean convexCast(Convex convex, Transform transform, Vector2 deltaPosition, double deltaAngle, Body body, Filter filter, boolean ignoreSensors, ConvexCastResult result) {
		// get the listeners
		List<ConvexCastListener> listeners = this.getListeners(ConvexCastListener.class);
		int clSize = listeners.size();
		
		boolean allow = true;
		boolean found = false;
		final Vector2 dp2 = new Vector2();
		double t2 = 1.0;

		// find the minimum time of impact for the given convex
		// and the current body
		int bSize = body.getFixtureCount();
		Transform bodyTransform = body.getTransform();
		
		// loop through all the body fixtures until we find
		// a the fixture that has the smallest time of impact
		for (int i = 0; i < bSize; i++) {
			BodyFixture bodyFixture = body.getFixture(i);
			// filter out sensors if desired
			if (ignoreSensors && bodyFixture.isSensor()) continue;
			// check the filter
			if (filter != null && !filter.isAllowed(bodyFixture.getFilter())) continue;
			
			allow = true;
			for (int j = 0; j < clSize; j++) {
				ConvexCastListener ccl = listeners.get(j);
				// see if we should test this body
				if (!ccl.allow(convex, body, bodyFixture)) {
					allow = false;
				}
			}
			if (!allow) return false;
			
			// get the time of impact
			Convex c = bodyFixture.getShape();
			TimeOfImpact toi = new TimeOfImpact();
			// we pass the zero vector and 0 for the change in position and angle for the body
			// since we assume that it is not moving since this is a static test
			if (this.timeOfImpactDetector.getTimeOfImpact(convex, transform, deltaPosition, deltaAngle, c, bodyTransform, dp2, 0.0, 0.0, t2, toi)) {
				// notify the listeners to see if we should test this fixture
				allow = true;
				for (int j = 0; j < clSize; j++) {
					ConvexCastListener ccl = listeners.get(j);
					// see if we should test this fixture
					if (!ccl.allow(convex, body, bodyFixture, toi)) {
						allow = false;
					}
				}
				if (!allow) continue;
				
				// set the new maximum time
				t2 = toi.getTime();
				// save the min time of impact
				result.fixture = bodyFixture;
				result.timeOfImpact = toi;
				result.body = body;
				// set the found flag
				found = true;
			}
		}
		
		return found;
	}
	
	/**
	 * Returns true if the given AABB overlaps a {@link Body} in this {@link World}.
	 * <p>
	 * If any part of a body is overlaping the AABB, the body is added to the list.
	 * <p>
	 * This performs a static collision test of the world using the {@link BroadphaseDetector}.
	 * <p>
	 * This may return bodies who only have sensor fixtures overlapping.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * @param aabb the world space {@link AABB}
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if the AABB overlaps any body
	 * @since 3.1.9
	 */
	public boolean detect(AABB aabb, List<DetectResult> results) {
		return this.detect(aabb, null, false, true, results);
	}
	
	/**
	 * Returns true if the given AABB overlaps a {@link Body} {@link Fixture} in this {@link World}.
	 * <p>
	 * If any part of a body is overlaping the AABB, the body and that respective fixture is added 
	 * to the returned list.
	 * <p>
	 * This performs a static collision test of the world using the {@link BroadphaseDetector}.
	 * <p>
	 * This may return bodies who only have sensor fixtures overlapping.
	 * @param aabb the world space {@link AABB}
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if the AABB overlaps any body
	 * @since 3.1.9
	 */
	public boolean detect(AABB aabb, boolean ignoreInactive, List<DetectResult> results) {
		return this.detect(aabb, null, false, ignoreInactive, results);
	}
	
	/**
	 * Returns true if the given AABB overlaps a {@link Body} in this {@link World}.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * @param aabb the world space {@link AABB}
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if the AABB overlaps any fixture
	 * @since 3.1.9
	 */
	public boolean detect(AABB aabb, boolean ignoreSensors, boolean ignoreInactive, List<DetectResult> results) {
		return this.detect(aabb, null, ignoreSensors, ignoreInactive, results);
	}

	/**
	 * Returns true if the given AABB overlaps a {@link Body} in this {@link World}.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * @param aabb the world space {@link AABB}
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if the AABB overlaps any fixture
	 * @since 3.1.9
	 */
	public boolean detect(AABB aabb, Filter filter, boolean ignoreSensors, boolean ignoreInactive, List<DetectResult> results) {
		List<DetectListener> listeners = this.getListeners(DetectListener.class);
		int dlSize = listeners.size();
		
		AABBBroadphaseFilter bpFilter = new AABBBroadphaseFilter(ignoreInactive, ignoreSensors, filter);
		List<BroadphaseItem<Body, BodyFixture>> collisions = this.broadphaseDetector.detect(aabb, bpFilter);
		boolean found = false;
		
		int bSize = collisions.size();
		boolean allow;
		for (int i = 0; i < bSize; i++) {
			BroadphaseItem<Body, BodyFixture> item = collisions.get(i);
			Body body = item.getCollidable();
			BodyFixture fixture = item.getFixture();
			// check body's fixtures next
			Transform transform = body.getTransform();
			// pass through the listeners
			allow = true;
			for (int j = 0; j < dlSize; j++) {
				DetectListener dl = listeners.get(j);
				if (!dl.allow(aabb, body, fixture)) {
					allow = false;
				}
			}
			if (!allow) {
				continue;
			}
			// create an AABB for the fixture
			AABB faabb = fixture.getShape().createAABB(transform);
			// test the aabbs
			if (aabb.overlaps(faabb)) {
				// add this fixture to the results list
				DetectResult result = new DetectResult(body, fixture);
				results.add(result);
				found = true;
			}
		}
		
		return found;
	}
	
	/**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The returned results may include sensor fixutres.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in world coordinates
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, boolean, List)
	 */
	public boolean detect(Convex convex, List<DetectResult> results) {
		return this.detect(convex, Transform.IDENTITY, null, false, true, false, results);
	}

	/**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in world coordinates
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, boolean, boolean, List)
	 */
	public boolean detect(Convex convex, boolean ignoreSensors, List<DetectResult> results) {
		return this.detect(convex, Transform.IDENTITY, null, ignoreSensors, true, false, results);
	}

	/**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in world coordinates
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, Transform, Filter, boolean, boolean, List)
	 */
	public boolean detect(Convex convex, boolean ignoreSensors, boolean ignoreInactive, List<DetectResult> results) {
		return this.detect(convex, Transform.IDENTITY, null, ignoreSensors, ignoreInactive, false, results);
	}
	
	/**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in world coordinates
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, Transform, Filter, boolean, boolean, boolean, List)
	 */
	public boolean detect(Convex convex, Filter filter, boolean ignoreSensors, boolean ignoreInactive, List<DetectResult> results) {
		return this.detect(convex, Transform.IDENTITY, filter, ignoreSensors, ignoreInactive, false, results);
	}
	
	/**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * Use the <code>includeCollisionData</code> parameter to have the {@link Penetration} object
	 * filled in the {@link DetectResult}s.  Including this information will have a performance impact.
	 * @param convex the convex shape in world coordinates
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param includeCollisionData true if the overlap {@link Penetration} should be returned
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
	public boolean detect(Convex convex, Filter filter, boolean ignoreSensors, boolean ignoreInactive, boolean includeCollisionData, List<DetectResult> results) {
		return this.detect(convex, Transform.IDENTITY, filter, ignoreSensors, ignoreInactive, includeCollisionData, results);
	}
	
	/**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The returned results may include sensor fixutres.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in local coordinates
	 * @param transform the convex shape's world transform
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, Transform, boolean, List)
	 */
	public boolean detect(Convex convex, Transform transform, List<DetectResult> results) {
		return this.detect(convex, transform, null, false, true, false, results);
	}
	
	/**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * Inactive bodies are ignored in this test.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in local coordinates
	 * @param transform the convex shape's world transform
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, Transform, boolean, boolean, List)
	 */
	public boolean detect(Convex convex, Transform transform, boolean ignoreSensors, List<DetectResult> results) {
		return this.detect(convex, transform, null, ignoreSensors, true, false, results);
	}
	
	/**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in local coordinates
	 * @param transform the convex shape's world transform
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, Transform, Filter, boolean, boolean, List)
	 */
	public boolean detect(Convex convex, Transform transform, boolean ignoreSensors, boolean ignoreInactive, List<DetectResult> results) {
		return this.detect(convex, transform, null, ignoreSensors, ignoreInactive, false, results);
	}
	
	/**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the convex shape in local coordinates
	 * @param transform the convex shape's world transform
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 * @see #detect(Convex, Transform, Filter, boolean, boolean, boolean, List)
	 */
	public boolean detect(Convex convex, Transform transform, Filter filter, boolean ignoreSensors, boolean ignoreInactive, List<DetectResult> results) {
		return this.detect(convex, transform, filter, ignoreSensors, ignoreInactive, false, results);
	}
	
	/**
	 * Returns true if the given {@link Convex} overlaps a body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * Use the <code>includeCollisionData</code> parameter to have the {@link Penetration} object
	 * filled in the {@link DetectResult}s.  Including this information will have a performance impact.
	 * @param convex the convex shape in local coordinates
	 * @param transform the convex shape's world transform
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param ignoreInactive true if inactive bodies should be ignored
	 * @param includeCollisionData true if the overlap {@link Penetration} should be returned
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
	public boolean detect(Convex convex, Transform transform, Filter filter, boolean ignoreSensors, boolean ignoreInactive, boolean includeCollisionData, List<DetectResult> results) {
		List<DetectListener> listeners = this.getListeners(DetectListener.class);
		int dlSize = listeners.size();
		boolean allow = true;
		
		// create an aabb for the given convex
		AABB aabb = convex.createAABB(transform);
		AABBBroadphaseFilter bpFilter = new AABBBroadphaseFilter(ignoreInactive, ignoreSensors, filter);
		// test using the broadphase to rule out as many bodies as we can
		List<BroadphaseItem<Body, BodyFixture>> items = this.broadphaseDetector.detect(aabb, bpFilter);
		// now perform a more accurate test
		int bSize = items.size();
		boolean found = false;
		for (int i = 0; i < bSize; i++) {
			BroadphaseItem<Body, BodyFixture> item = items.get(i);
			Body body = item.getCollidable();
			BodyFixture fixture = item.getFixture();
			// get the body transform
			Transform bt = body.getTransform();
			
			// pass through the listeners
			allow = true;
			for (int j = 0; j < dlSize; j++) {
				DetectListener dl = listeners.get(j);
				if (!dl.allow(convex, transform, body, fixture)) {
					allow = false;
				}
			}
			if (!allow) {
				continue;
			}
			
			// just perform a boolean test since its typically faster
			Convex bc = fixture.getShape();
			boolean collision = false;
			// should we use the fast method or the one that returns the collision info
			Penetration penetration = (includeCollisionData ? new Penetration() : null); 
			if (includeCollisionData) {
				collision = this.narrowphaseDetector.detect(convex, transform, bc, bt, penetration);
			} else {
				collision = this.narrowphaseDetector.detect(convex, transform, bc, bt);
			}
			if (collision) {
				// add this fixture to the results list
				DetectResult result = new DetectResult(body, fixture, penetration);
				results.add(result);
				found = true;
			}
		}
		// return the bodies in collision
		return found;
	}
	
	/**
	 * Returns true if the given {@link AABB} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the AABB overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * @param aabb the {@link AABB} in world coordinates
	 * @param body the {@link Body} to test against
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
	public boolean detect(AABB aabb, Body body, boolean ignoreSensors, List<DetectResult> results) {
		return this.detect(aabb, body, null, ignoreSensors, results);
	}
	
	/**
	 * Returns true if the given {@link AABB} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the AABB overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * @param aabb the {@link AABB} in world coordinates
	 * @param body the {@link Body} to test against
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
	public boolean detect(AABB aabb, Body body, Filter filter, boolean ignoreSensors, List<DetectResult> results) {
		List<DetectListener> listeners = this.getListeners(DetectListener.class);
		int dlSize = listeners.size();
		boolean allow = true;
		// test the AABBs
		boolean found = false;
		AABB baabb = this.broadphaseDetector.getAABB(body);
		if (baabb == null) {
			baabb = body.createAABB();
		}
		if (aabb.overlaps(baabb)) {
			// check body's fixtures next
			Transform transform = body.getTransform();
			int fSize = body.getFixtureCount();
			for (int j = 0; j < fSize; j++) {
				BodyFixture fixture = body.getFixture(j);
				// test for sensors
				if (ignoreSensors && fixture.isSensor()) continue;
				// test the filter
				if (filter != null && !filter.isAllowed(fixture.getFilter())) continue;
				// pass through the listeners
				allow = true;
				for (int k = 0; k < dlSize; k++) {
					DetectListener dl = listeners.get(k);
					if (!dl.allow(aabb, body, fixture)) {
						allow = false;
					}
				}
				if (!allow) {
					continue;
				}
				// create an AABB for the fixture
				AABB faabb = fixture.getShape().createAABB(transform);
				// test the aabbs
				if (aabb.overlaps(faabb)) {
					// add this fixture to the results list
					DetectResult result = new DetectResult(body, fixture);
					results.add(result);
					found = true;
				}
			}
		}
		
		return found;
	}
	
	/**
	 * Returns true if the given {@link Convex} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the {@link Convex} in world coordinates
	 * @param body the {@link Body} to test against
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
	public boolean detect(Convex convex, Body body, boolean ignoreSensors, List<DetectResult> results) {
		return this.detect(convex, Transform.IDENTITY, body, null, ignoreSensors, false, results);
	}
	
	/**
	 * Returns true if the given {@link Convex} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the {@link Convex} in world coordinates
	 * @param body the {@link Body} to test against
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
	public boolean detect(Convex convex, Body body, Filter filter, boolean ignoreSensors, List<DetectResult> results) {
		return this.detect(convex, Transform.IDENTITY, body, filter, ignoreSensors, false, results);
	}
	
	/**
	 * Returns true if the given {@link Convex} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * Use the <code>includeCollisionData</code> parameter to have the {@link Penetration} object
	 * filled in the {@link DetectResult}s.  Including this information negatively impacts performance.
	 * @param convex the {@link Convex} in world coordinates
	 * @param body the {@link Body} to test against
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param includeCollisionData true if the overlap {@link Penetration} should be returned
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
	public boolean detect(Convex convex, Body body, Filter filter, boolean ignoreSensors, boolean includeCollisionData, List<DetectResult> results) {
		return this.detect(convex, Transform.IDENTITY, body, filter, ignoreSensors, includeCollisionData, results);
	}
	
	/**
	 * Returns true if the given {@link Convex} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the {@link Convex} in local coordinates
	 * @param transform the convex shape's world {@link Transform}
	 * @param body the {@link Body} to test against
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
	public boolean detect(Convex convex, Transform transform, Body body, boolean ignoreSensors, List<DetectResult> results) {
		return this.detect(convex, transform, body, null, ignoreSensors, false, results);
	}
	
	/**
	 * Returns true if the given {@link Convex} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * The results from this test will not include {@link Penetration} objects.
	 * @param convex the {@link Convex} in local coordinates
	 * @param transform the convex shape's world {@link Transform}
	 * @param body the {@link Body} to test against
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
	public boolean detect(Convex convex, Transform transform, Body body, Filter filter, boolean ignoreSensors, List<DetectResult> results) {
		return this.detect(convex, transform, body, filter, ignoreSensors, false, results);
	}
	
	/**
	 * Returns true if the given {@link Convex} overlaps the given body in the world.
	 * <p>
	 * If this method returns true, the results list will contain the bodies and
	 * fixtures that the convex overlaps.
	 * <p>
	 * Use the {@link Body#isInContact(Body)} method instead if you want to test if two bodies
	 * are colliding.
	 * <p>
	 * Use the <code>includeCollisionData</code> parameter to have the {@link Penetration} object
	 * filled in the {@link DetectResult}s.  Including this information negatively impacts performance.
	 * @param convex the {@link Convex} in local coordinates
	 * @param transform the convex shape's world {@link Transform}
	 * @param body the {@link Body} to test against
	 * @param filter the {@link Filter} to use against the fixtures; can be null
	 * @param includeCollisionData true if the overlap {@link Penetration} should be returned
	 * @param ignoreSensors true if sensor fixtures should be ignored
	 * @param results the list of overlapping bodies and fixtures
	 * @return boolean true if an overlap was found
	 * @since 3.1.9
	 */
	public boolean detect(Convex convex, Transform transform, Body body, Filter filter, boolean ignoreSensors, boolean includeCollisionData, List<DetectResult> results) {
		List<DetectListener> listeners = this.getListeners(DetectListener.class);
		int dlSize = listeners.size();
		// make sure we can test the body
		boolean allow = true;
		for (int i = 0; i < dlSize; i++) {
			DetectListener dl = listeners.get(i);
			if (!dl.allow(convex, transform, body)) {
				allow = false;
			}
		}
		if (!allow) return false;
		// create an aabb for the given convex
		AABB aabb = convex.createAABB(transform);
		// test using the broadphase to rule out as many bodies as we can
		AABB baabb = this.broadphaseDetector.getAABB(body);
		if (baabb == null) {
			baabb = body.createAABB();
		}
		// now perform an AABB test first
		boolean found = false;
		if (aabb.overlaps(baabb)) {
			// get the body transform
			Transform bt = body.getTransform();
			// test all the fixtures
			int fSize = body.getFixtureCount();
			for (int i = 0; i < fSize; i++) {
				BodyFixture fixture = body.getFixture(i);
				// check against the sensor flag
				if (ignoreSensors && fixture.isSensor()) continue;
				// check against the filter if given
				Filter ff = fixture.getFilter();
				if (filter != null && !ff.isAllowed(filter)) continue;

				// pass through the listeners
				allow = true;
				for (int j = 0; j < dlSize; j++) {
					DetectListener dl = listeners.get(j);
					if (!dl.allow(convex, transform, body, fixture)) {
						allow = false;
					}
				}
				if (!allow) {
					continue;
				}
				
				// just perform a boolean test since its typically faster
				Convex bc = fixture.getShape();
				boolean collision = false;
				// should we use the fast method or the one that returns the collision info
				Penetration penetration = (includeCollisionData ? new Penetration() : null); 
				if (includeCollisionData) {
					collision = this.narrowphaseDetector.detect(convex, transform, bc, bt, penetration);
				} else {
					collision = this.narrowphaseDetector.detect(convex, transform, bc, bt);
				}
				if (collision) {
					// add this fixture to the results list
					DetectResult result = new DetectResult(body, fixture, penetration);
					results.add(result);
					found = true;
				}
			}
		}
		// return the bodies in collision
		return found;
	}
	
	/**
	 * Shifts the coordinates of the entire world by the given amount.
	 * <pre>
	 * NewPosition = OldPosition + shift
	 * </pre>
	 * This method is useful in situations where the world is very large
	 * causing very large numbers to be used in the computations.  Shifting
	 * the coordinate system allows the computations to be localized and 
	 * retain accuracy.
	 * <p>
	 * This method modifies the coordinates of every body and joint in the world.
	 * <p>
	 * Adding joints or bodies after this method is called should consider that
	 * everything has been shifted.
	 * <p>
	 * This method does <b>NOT</b> require a call to {@link #setUpdateRequired(boolean)}.
	 * @param shift the distance to shift along the x and y axes
	 * @since 3.2.0
	 */
	public void shift(Vector2 shift) {
		// update the bodies
		int bSize = this.bodies.size();
		for (int i = 0; i < bSize; i++) {
			Body body = this.bodies.get(i);
			body.shift(shift);
		}
		// update the joints
		int jSize = this.joints.size();
		for (int i = 0; i < jSize; i++) {
			Joint joint = this.joints.get(i);
			joint.shift(shift);
		}
		// update the broadphase
		this.broadphaseDetector.shift(shift);
		// update the bounds
		if (this.bounds != null) {
			this.bounds.shift(shift);
		}
		// update contact manager
		this.contactManager.shift(shift);
	}
	
	/**
	 * Adds the given {@link Body} to the {@link World}.
	 * @param body the {@link Body} to add
	 * @throws NullPointerException if body is null
	 * @throws IllegalArgumentException if body has already been added to this world or if its a member of another world instance
	 * @since 3.1.1
	 */
	public void addBody(Body body) {
		// check for null body
		if (body == null) throw new NullPointerException(Messages.getString("dynamics.world.addNullBody"));
		// dont allow adding it twice
		if (body.world == this) throw new IllegalArgumentException(Messages.getString("dynamics.world.addExistingBody"));
		// dont allow a body that already is assigned to another world
		if (body.world != null) throw new IllegalArgumentException(Messages.getString("dynamics.world.addOtherWorldBody"));
		// add it to the world
		this.bodies.add(body);
		// set the world property on the body
		body.world = this;
		// add it to the broadphase
		this.broadphaseDetector.add(body);
	}
	
	/**
	 * Adds the given {@link Joint} to the {@link World}.
	 * @param joint the {@link Joint} to add
	 * @throws NullPointerException if joint is null
	 * @throws IllegalArgumentException if joint has already been added to this world or if its a member of another world instance
	 * @since 3.1.1
	 */
	public void addJoint(Joint joint) {
		// check for null joint
		if (joint == null) throw new NullPointerException(Messages.getString("dynamics.world.addNullJoint"));
		// implicitly cast to constraint
		Constraint constraint = joint;
		// dont allow adding it twice
		if (constraint.world == this) throw new IllegalArgumentException(Messages.getString("dynamics.world.addExistingBody"));
		// dont allow a joint that already is assigned to another world
		if (constraint.world != null) throw new IllegalArgumentException(Messages.getString("dynamics.world.addOtherWorldBody"));
		// add the joint to the joint list
		this.joints.add(joint);
		// set that its attached to this world
		constraint.world = this;
		// get the associated bodies
		Body body1 = joint.getBody1();
		Body body2 = joint.getBody2();
		// create a joint edge from the first body to the second
		JointEdge jointEdge1 = new JointEdge(body2, joint);
		// add the edge to the body
		body1.joints.add(jointEdge1);
		// create a joint edge from the second body to the first
		JointEdge jointEdge2 = new JointEdge(body1, joint);
		// add the edge to the body
		body2.joints.add(jointEdge2);
	}
	
	/**
	 * Returns true if this world contains the given body.
	 * @param body the {@link Body} to test for
	 * @return boolean true if the body is contained in this world
	 * @since 3.1.1
	 */
	public boolean containsBody(Body body) {
		return this.bodies.contains(body);
	}
	
	/**
	 * Returns true if this world contains the given joint.
	 * @param joint the {@link Joint} to test for
	 * @return boolean true if the joint is contained in this world
	 * @since 3.1.1
	 */
	public boolean containsJoint(Joint joint) {
		return this.joints.contains(joint);
	}
	
	/**
	 * Removes the {@link Body} at the given index from this {@link World}.
	 * <p>
	 * Use the {@link #removeBody(int, boolean)} method to enable implicit
	 * destruction notification.
	 * @param index the index of the body to remove.
	 * @return boolean true if the body was removed
	 * @since 3.2.0
	 */
	public boolean removeBody(int index) {
		return removeBody(index, false);
	}

	/**
	 * Removes the {@link Body} at the given index from this {@link World}.
	 * <p>
	 * When a body is removed, joints and contacts may be implicitly destroyed.
	 * Pass true to the notify parameter to be notified of the destruction of these objects
	 * via the {@link DestructionListener}s.
	 * <p>
	 * This method does not trigger {@link ContactListener#end(ContactPoint)} events
	 * for the contacts that are being removed.
	 * @param index the index of the body to remove.
	 * @param notify true if implicit destruction should be notified
	 * @return boolean true if the body was removed
	 * @since 3.2.0
	 */
	public boolean removeBody(int index, boolean notify) {
		Body body = this.bodies.get(index);
		return removeBody(body, notify);
	}
	
	/**
	 * Removes the given {@link Body} from this {@link World}.
	 * <p>
	 * Use the {@link #removeBody(Body, boolean)} method to enable implicit
	 * destruction notification.
	 * @param body the {@link Body} to remove.
	 * @return boolean true if the body was removed
	 */
	public boolean removeBody(Body body) {
		return removeBody(body, false);
	}
	
	/**
	 * Removes the given {@link Body} from this {@link World}.
	 * <p>
	 * When a body is removed, joints and contacts may be implicitly destroyed.
	 * Pass true to the notify parameter to be notified of the destruction of these objects
	 * via the {@link DestructionListener}s.
	 * <p>
	 * This method does not trigger {@link ContactListener#end(ContactPoint)} events
	 * for the contacts that are being removed.
	 * @param body the {@link Body} to remove
	 * @param notify true if implicit destruction should be notified
	 * @return boolean true if the body was removed
	 * @since 3.1.1
	 */
	public boolean removeBody(Body body, boolean notify) {
		List<DestructionListener> listeners = null;
		if (notify) {
			listeners = this.getListeners(DestructionListener.class);
		}
		// check for null body
		if (body == null) return false;
		// remove the body from the list
		boolean removed = this.bodies.remove(body);
		
		// only remove joints and contacts if the body was removed
		if (removed) {
			// set the world property to null
			body.world = null;
			
			// remove the body from the broadphase
			this.broadphaseDetector.remove(body);
			
			// wake up any bodies connected to this body by a joint
			// and destroy the joints and remove the edges
			Iterator<JointEdge> aIterator = body.joints.iterator();
			while (aIterator.hasNext()) {
				// get the joint edge
				JointEdge jointEdge = aIterator.next();
				// remove the joint edge from the given body
				aIterator.remove();
				// get the joint
				Joint joint = jointEdge.interaction;
				// get the other body
				Body other = jointEdge.other;
				// wake up the other body
				other.setAsleep(false);
				// remove the joint edge from the other body
				Iterator<JointEdge> bIterator = other.joints.iterator();
				while (bIterator.hasNext()) {
					// get the joint edge
					JointEdge otherJointEdge = bIterator.next();
					// get the joint
					Joint otherJoint = otherJointEdge.interaction;
					// are the joints the same object reference
					if (otherJoint == joint) {
						// remove the joint edge
						bIterator.remove();
						// we can break from the loop since there should
						// not be more than one joint edge per joint per body
						break;
					}
				}
				// notify of the destroyed joint
				if (notify) {
					for (DestructionListener dl : listeners) {
						dl.destroyed(joint);
					}
				}
				// remove the joint from the world
				this.joints.remove(joint);
			}
			
			// remove any contacts this body had with any other body
			Iterator<ContactEdge> acIterator = body.contacts.iterator();
			while (acIterator.hasNext()) {
				// get the contact edge
				ContactEdge contactEdge = acIterator.next();
				// remove the contact edge from the given body
				acIterator.remove();
				// get the contact constraint
				ContactConstraint contactConstraint = contactEdge.interaction;
				// get the other body
				Body other = contactEdge.other;
				// wake up the other body
				other.setAsleep(false);
				// remove the contact edge connected from the other body
				// to this body
				Iterator<ContactEdge> iterator = other.contacts.iterator();
				while (iterator.hasNext()) {
					ContactEdge otherContactEdge = iterator.next();
					// get the contact constraint
					ContactConstraint otherContactConstraint = otherContactEdge.interaction;
					// check if the contact constraint is the same reference
					if (otherContactConstraint == contactConstraint) {
						// remove the contact edge
						iterator.remove();
						// break from the loop since there should only be
						// one contact edge per body pair
						break;
					}
				}
				// remove the contact constraint from the contact manager
				this.contactManager.end(contactConstraint);
				// loop over the contact points
				List<Contact> contacts = contactConstraint.getContacts();
				int size = contacts.size();
				for (int j = 0; j < size; j++) {
					// get the contact
					Contact contact = contacts.get(j);
					// create a contact point for notification
					ContactPoint contactPoint = new ContactPoint(
							new ContactPointId(contactConstraint.getId(), contact.getId()),
							contactConstraint.getBody1(), 
							contactConstraint.getFixture1(), 
							contactConstraint.getBody2(), 
							contactConstraint.getFixture2(),
							contact.getPoint(), 
							contactConstraint.getNormal(), 
							contact.getDepth());
					// call the destruction listeners
					if (notify) {
						for (DestructionListener dl : listeners) {
							dl.destroyed(contactPoint);
						}
					}
				}
			}
		}
		
		return removed;
	}

	/**
	 * Removes the {@link Joint} at the given index from this {@link World}.
	 * <p>
	 * No other objects are implicitly destroyed with joints are removed.
	 * @param index the index of the {@link Joint} to remove
	 * @return boolean true if the {@link Joint} was removed
	 * @since 3.2.0
	 */
	public boolean removeJoint(int index) {
		Joint joint = this.joints.get(index);
		return removeJoint(joint);
	}
	
	/**
	 * Removes the given {@link Joint} from this {@link World}.
	 * <p>
	 * No other objects are implicitly destroyed with joints are removed.
	 * @param joint the {@link Joint} to remove
	 * @return boolean true if the {@link Joint} was removed
	 */
	public boolean removeJoint(Joint joint) {
		// check for null joint
		if (joint == null) return false;
		// remove the joint from the joint list
		boolean removed = this.joints.remove(joint);
		
		// see if the given joint was removed
		if (removed) {
			// get the involved bodies
			Body body1 = joint.getBody1();
			Body body2 = joint.getBody2();
			
			// remove the joint edges from body1
			Iterator<JointEdge> iterator = body1.joints.iterator();
			while (iterator.hasNext()) {
				// see if this is the edge we want to remove
				JointEdge jointEdge = iterator.next();
				if (jointEdge.interaction == joint) {
					// then remove this joint edge
					iterator.remove();
					// joints should only have one joint edge
					// per body
					break;
				}
			}
			// remove the joint edges from body2
			iterator = body2.joints.iterator();
			while (iterator.hasNext()) {
				// see if this is the edge we want to remove
				JointEdge jointEdge = iterator.next();
				if (jointEdge.interaction == joint) {
					// then remove this joint edge
					iterator.remove();
					// joints should only have one joint edge
					// per body
					break;
				}
			}
			
			// finally wake both bodies
			body1.setAsleep(false);
			body2.setAsleep(false);
		}
		
		return removed;
	}
	
	/**
	 * Removes all the joints and bodies from this world.
	 * <p>
	 * This method does <b>not</b> notify of destroyed objects.
	 * @see #removeAllBodiesAndJoints(boolean)
	 * @since 3.1.1
	 */
	public void removeAllBodiesAndJoints() {
		this.removeAllBodiesAndJoints(false);
	}
	
	/**
	 * Removes all the joints and bodies from this world.
	 * @param notify true if destruction of joints and contacts should be notified of by the {@link DestructionListener}
	 * @since 3.1.1
	 */
	public void removeAllBodiesAndJoints(boolean notify) {
		List<DestructionListener> listeners = null;
		if (notify) {
			listeners = this.getListeners(DestructionListener.class);
		}
		// loop over the bodies and clear the
		// joints and contacts
		int bsize = this.bodies.size();
		for (int i = 0; i < bsize; i++) {
			// get the body
			Body body = this.bodies.get(i);
			// clear the joint edges
			body.joints.clear();
			// do we need to notify?
			if (notify) {
				// notify of all the destroyed contacts
				Iterator<ContactEdge> aIterator = body.contacts.iterator();
				while (aIterator.hasNext()) {
					// get the contact edge
					ContactEdge contactEdge = aIterator.next();
					// get the other body involved
					Body other = contactEdge.other;
					// get the contact constraint
					ContactConstraint contactConstraint = contactEdge.interaction;
					// find the other contact edge
					Iterator<ContactEdge> bIterator = other.contacts.iterator();
					while (bIterator.hasNext()) {
						// get the contact edge
						ContactEdge otherContactEdge = bIterator.next();
						// get the contact constraint on the edge
						ContactConstraint otherContactConstraint = otherContactEdge.interaction;
						// are the constraints the same object reference
						if (otherContactConstraint == contactConstraint) {
							// if so then remove it
							bIterator.remove();
							// there should only be one contact edge
							// for each body-body pair
							break;
						}
					}
					// notify of all the contacts on the contact constraint
					List<Contact> contacts = contactConstraint.getContacts();
					int csize = contacts.size();
					for (int j = 0; j < csize; j++) {
						Contact contact = contacts.get(j);
						// create a contact point for notification
						ContactPoint contactPoint = new ContactPoint(
								new ContactPointId(contactConstraint.getId(), contact.getId()),
								contactConstraint.getBody1(), 
								contactConstraint.getFixture1(), 
								contactConstraint.getBody2(), 
								contactConstraint.getFixture2(),
								contact.getPoint(), 
								contactConstraint.getNormal(), 
								contact.getDepth());
						// call the destruction listeners
						for (DestructionListener dl : listeners) {
							dl.destroyed(contactPoint);
						}
					}
				}
				
				// notify of the destroyed body
				for (DestructionListener dl : listeners) {
					dl.destroyed(body);
				}
			}
			// clear all the contacts
			body.contacts.clear();
			// set the world to null
			body.world = null;
		}
		// do we need to notify?
		if (notify) {
			// notify of all the destroyed joints
			int jsize = this.joints.size();
			for (int i = 0; i < jsize; i++) {
				// get the joint
				Joint joint = this.joints.get(i);
				// call the destruction listeners
				for (DestructionListener dl : listeners) {
					dl.destroyed(joint);
				}
			}
		}
		// clear all the broadphase bodies
		this.broadphaseDetector.clear();
		// clear all the joints
		this.joints.clear();
		// clear all the bodies
		this.bodies.clear();
		// clear the contact manager of cached contacts
		this.contactManager.clear();
	}
	
	/**
	 * This is a convenience method for the {@link #removeAllBodiesAndJoints()} method since all joints will be removed
	 * when all bodies are removed anyway.
	 * <p>
	 * This method does not notify of the destroyed contacts, joints, etc.
	 * @see #removeAllBodies(boolean)
	 * @since 3.0.1
	 */
	public void removeAllBodies() {
		this.removeAllBodiesAndJoints(false);
	}
	
	/**
	 * This is a convenience method for the {@link #removeAllBodiesAndJoints(boolean)} method since all joints will be removed
	 * when all bodies are removed anyway.
	 * @param notify true if destruction of joints and contacts should be notified of by the {@link DestructionListener}
	 * @since 3.0.1
	 */
	public void removeAllBodies(boolean notify) {
		this.removeAllBodiesAndJoints(notify);
	}
	
	/**
	 * Removes all {@link Joint}s from this {@link World}.
	 * <p>
	 * This method does not notify of the joints removed.
	 * @see #removeAllJoints(boolean)
	 * @since 3.0.1
	 */
	public void removeAllJoints() {
		this.removeAllJoints(false);
	}
	
	/**
	 * Removes all {@link Joint}s from this {@link World}.
	 * @param notify true if destruction of joints should be notified of by the {@link DestructionListener}
	 * @since 3.0.1
	 */
	public void removeAllJoints(boolean notify) {
		List<DestructionListener> listeners = null;
		if (notify) {
			listeners = this.getListeners(DestructionListener.class);
		}
		// get the number of joints
		int jSize = this.joints.size();
		// remove all the joints
		for (int i = 0; i < jSize; i++) {
			// remove the joint from the joint list
			Joint joint = this.joints.get(i);
			
			// get the involved bodies
			Body body1 = joint.getBody1();
			Body body2 = joint.getBody2();
			
			// remove the joint edges from body1
			Iterator<JointEdge> iterator = body1.joints.iterator();
			while (iterator.hasNext()) {
				// see if this is the edge we want to remove
				JointEdge jointEdge = iterator.next();
				if (jointEdge.interaction == joint) {
					// then remove this joint edge
					iterator.remove();
					// joints should only have one joint edge
					// per body
					break;
				}
			}
			// remove the joint edges from body2
			iterator = body2.joints.iterator();
			while (iterator.hasNext()) {
				// see if this is the edge we want to remove
				JointEdge jointEdge = iterator.next();
				if (jointEdge.interaction == joint) {
					// then remove this joint edge
					iterator.remove();
					// joints should only have one joint edge
					// per body
					break;
				}
			}
			
			// finally wake both bodies
			body1.setAsleep(false);
			body2.setAsleep(false);
			
			// notify of the destruction if required
			if (notify) {
				for (DestructionListener dl : listeners) {
					dl.destroyed(joint);
				}
			}
		}
		
		// remove all the joints from the joint list
		this.joints.clear();
	}
	
	/**
	 * Returns true if upon the next time step the contacts must be updated.
	 * <p>
	 * This is typically set via user code when something about the simulation changes
	 * that can affect collision detection.
	 * @return boolean
	 * @see #setUpdateRequired(boolean)
	 */
	public boolean isUpdateRequired() {
		return this.updateRequired;
	}
	
	/**
	 * Sets the update required flag.
	 * <p>
	 * Set this flag to true if any of the following conditions have been met:
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
	 * @param flag the flag
	 */
	public void setUpdateRequired(boolean flag) {
		this.updateRequired = flag;
	}
	
	/**
	 * Returns the world id.
	 * @return UUID
	 * @since 3.2.0
	 */
	public UUID getId() {
		return this.id;
	}
	
	/**
	 * Returns the settings for this world.
	 * @return {@link Settings}
	 * @since 3.0.3
	 */
	public Settings getSettings() {
		return this.settings;
	}
	
	/**
	 * Sets the dynamics settings for this world.
	 * @param settings the desired settings
	 * @throws NullPointerException if the given settings is null
	 * @since 3.0.3
	 */
	public void setSettings(Settings settings) {
		if (settings == null) throw new NullPointerException(Messages.getString("dynamics.world.nullSettings"));
		this.settings = settings;
	}
	
	/**
	 * Sets the acceleration due to gravity.
	 * @param gravity the gravity in meters/second<sup>2</sup>
	 * @throws NullPointerException if gravity is null
	 */
	public void setGravity(Vector2 gravity) {
		if (gravity == null) throw new NullPointerException(Messages.getString("dynamics.world.nullGravity"));
		this.gravity = gravity;
	}
	
	/**
	 * Returns the acceleration due to gravity.
	 * @return {@link Vector2} the gravity in meters/second<sup>2</sup>
	 */
	public Vector2 getGravity() {
		return this.gravity;
	}

	/**
	 * Sets the bounds of this {@link World}.
	 * @param bounds the bounds; can be null
	 */
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
	
	/**
	 * Returns the bounds of this world.
	 * <p>
	 * This will return null if no bounds were initially set
	 * or if it was set to null via the {@link #setBounds(Bounds)}
	 * method.
	 * @return {@link Bounds} the bounds or null
	 */
	public Bounds getBounds() {
		return this.bounds;
	}
	
	/**
	 * Returns the listeners that are of the given type (or sub types)
	 * of the given type.
	 * <p>
	 * Returns an empty list if no listeners for the given type are found.
	 * <p>
	 * Returns null if clazz is null.
	 * <p>
	 * Example usage:
	 * <pre>
	 * world.getListeners(ContactListener.class);
	 * </pre>
	 * @param <T> the listener type
	 * @param clazz the type of listener to get
	 * @return List&lt;T&gt;
	 * @since 3.1.0
	 */
	public <T extends Listener> List<T> getListeners(Class<T> clazz) {
		// check for null
		if (clazz == null) return null;
		// create a new list and loop over the listeners
		List<T> listeners = new ArrayList<T>();
		int lSize = this.listeners.size();
		for (int i = 0; i < lSize; i++) {
			Listener listener = this.listeners.get(i);
			// check if the listener is of the given type
			if (clazz.isInstance(listener)) {
				// if so, add it to the new list
				listeners.add(clazz.cast(listener));
			}
		}
		// return the new list
		return listeners;
	}
	
	/**
	 * Returns the listeners of the given type (or sub types) in the given list.
	 * <p>
	 * This method does <b>not</b> clear the given listeners list before
	 * adding the listeners.
	 * <p>
	 * If clazz or listeners is null, this method immediately returns.
	 * <p>
	 * Example usage:
	 * <pre>
	 * List&lt;ContactListener&gt; list = ...;
	 * world.getListeners(ContactListener.class, list);
	 * </pre>
	 * @param <T> the listener type
	 * @param clazz the type of listener to get
	 * @param listeners the list to add the listeners to
	 * @since 3.1.1
	 */
	public <T extends Listener> void getListeners(Class<T> clazz, List<T> listeners) {
		// check for null
		if (clazz == null || listeners == null) return;
		// create a new list and loop over the listeners
		int lSize = this.listeners.size();
		for (int i = 0; i < lSize; i++) {
			Listener listener = this.listeners.get(i);
			// check if the listener is of the given type
			if (clazz.isInstance(listener)) {
				// if so, add it to the new list
				listeners.add(clazz.cast(listener));
			}
		}
	}
	
	/**
	 * Adds the given listener to the list of listeners.
	 * @param listener the listener
	 * @throws NullPointerException if the given listener is null
	 * @throws IllegalArgumentException if the given listener has already been added to this world
	 * @since 3.1.0
	 */
	public void addListener(Listener listener) {
		// make sure its not null
		if (listener == null) throw new NullPointerException(Messages.getString("dynamics.world.nullListener"));
		// make sure its not already been added
		if (this.listeners.contains(listener)) throw new IllegalArgumentException("dynamics.world.addExistingListener");
		// then add the listener
		this.listeners.add(listener);
	}
	
	/**
	 * Returns true if the given listener is already attached to this world.
	 * @param listener the listener
	 * @return boolean
	 * @since 3.1.1
	 */
	public boolean containsListener(Listener listener) {
		return this.listeners.contains(listener);
	}
	
	/**
	 * Removes the given listener from this world.
	 * @param listener the listener to remove
	 * @return boolean true if the listener was removed
	 * @since 3.1.0
	 */
	public boolean removeListener(Listener listener) {
		return this.listeners.remove(listener);
	}
	
	/**
	 * Removes all the listeners.
	 * @return int the number of listeners removed
	 * @since 3.1.1
	 */
	public int removeAllListeners() {
		int count = this.listeners.size();
		this.listeners.clear();
		return count;
	}
	
	/**
	 * Removes all the listeners of the specified type (or sub types).
	 * <p>
	 * Returns zero if the given type is null or there are zero listeners
	 * attached.
	 * <p>
	 * Example usage:
	 * <pre>
	 * world.removeAllListeners(ContactListener.class);
	 * </pre>
	 * @param <T> the listener type
	 * @param clazz the listener type
	 * @return int the number of listeners removed
	 * @since 3.1.1
	 */
	public <T extends Listener> int removeAllListeners(Class<T> clazz) {
		// if null, just return
		if (clazz == null) return 0;
		// if empty list, return
		if (this.listeners.isEmpty()) return 0;
		// loop over the list of listeners
		int count = 0;
		Iterator<Listener> listenerIterator = this.listeners.iterator();
		while (listenerIterator.hasNext()) {
			Listener listener = listenerIterator.next();
			if (clazz.isInstance(listener)) {
				listenerIterator.remove();
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Returns the total number of listeners attached to this world.
	 * @return int
	 * @since 3.1.1
	 */
	public int getListenerCount() {
		return this.listeners.size();
	}
	
	/**
	 * Returns the total number of listeners of the given type (or sub types) 
	 * attached to this world.
	 * <p>
	 * Returns zero if the given class type is null.
	 * <p>
	 * Example usage:
	 * <pre>
	 * world.getListenerCount(BoundsListener.class);
	 * </pre>
	 * @param <T> the listener type
	 * @param clazz the listener type
	 * @return int
	 * @since 3.1.1
	 */
	public <T extends Listener> int getListenerCount(Class<T> clazz) {
		// check for null
		if (clazz == null) return 0;
		// loop over the listeners
		int count = 0;
		int lSize = this.listeners.size();
		for (int i = 0; i < lSize; i++) {
			Listener listener = this.listeners.get(i);
			// check if the listener is of the given type
			if (clazz.isInstance(listener)) {
				// if so, increment
				count++;
			}
		}
		// return the count
		return count;
	}
	
	/**
	 * Sets the broad-phase collision detection algorithm.
	 * @param broadphaseDetector the broad-phase collision detection algorithm
	 * @throws NullPointerException if broadphaseDetector is null
	 */
	public void setBroadphaseDetector(BroadphaseDetector<Body, BodyFixture> broadphaseDetector) {
		if (broadphaseDetector == null) throw new NullPointerException(Messages.getString("dynamics.world.nullBroadphaseDetector"));
		// set the new broadphase
		this.broadphaseDetector = broadphaseDetector;
		// re-add all bodies to the broadphase
		int size = this.bodies.size();
		for (int i = 0; i < size; i++) {
			this.broadphaseDetector.add(this.bodies.get(i));
		}
	}
	
	/**
	 * Returns the broad-phase collision detection algorithm.
	 * @return {@link BroadphaseDetector} the broad-phase collision detection algorithm
	 */
	public BroadphaseDetector<Body, BodyFixture> getBroadphaseDetector() {
		return this.broadphaseDetector;
	}
	
	/**
	 * Sets the {@link BroadphaseFilter} used when detecting collisions for each time step.
	 * <p>
	 * This should always be an instance of a class that extends the {@link DetectBroadphaseFilter}
	 * so that the standard filters are retained.
	 * @param filter the filter
	 * @since 3.2.2
	 */
	public void setDetectBroadphaseFilter(BroadphaseFilter<Body, BodyFixture> filter) {
		if (filter == null) {
			this.detectBroadphaseFilter = new DetectBroadphaseFilter();
		} else {
			this.detectBroadphaseFilter = filter;
		}
	}
	
	/**
	 * Returns the {@link BroadphaseFilter} used when detecting collisions for each time step.
	 * @return {@link BroadphaseFilter}
	 * @since 3.2.2
	 */
	public BroadphaseFilter<Body, BodyFixture> getDetectBroadphaseFilter() {
		return this.detectBroadphaseFilter;
	}
	
	/**
	 * Sets the narrow-phase collision detection algorithm.
	 * @param narrowphaseDetector the narrow-phase collision detection algorithm
	 * @throws NullPointerException if narrowphaseDetector is null
	 */
	public void setNarrowphaseDetector(NarrowphaseDetector narrowphaseDetector) {
		if (narrowphaseDetector == null) throw new NullPointerException(Messages.getString("dynamics.world.nullNarrowphaseDetector"));
		this.narrowphaseDetector = narrowphaseDetector;
	}
	
	/**
	 * Returns the narrow-phase collision detection algorithm.
	 * @return {@link NarrowphaseDetector} the narrow-phase collision detection algorithm
	 */
	public NarrowphaseDetector getNarrowphaseDetector() {
		return this.narrowphaseDetector;
	}
	
	/**
	 * Sets the manifold solver.
	 * @param manifoldSolver the manifold solver
	 * @throws NullPointerException if manifoldSolver is null
	 */
	public void setManifoldSolver(ManifoldSolver manifoldSolver) {
		if (manifoldSolver == null) throw new NullPointerException(Messages.getString("dynamics.world.nullManifoldSolver"));
		this.manifoldSolver = manifoldSolver;
	}
	
	/**
	 * Returns the manifold solver.
	 * @return {@link ManifoldSolver} the manifold solver
	 */
	public ManifoldSolver getManifoldSolver() {
		return this.manifoldSolver;
	}
	
	/**
	 * Sets the time of impact detector.
	 * @param timeOfImpactDetector the time of impact detector
	 * @throws NullPointerException if timeOfImpactDetector is null
	 * @since 1.2.0
	 */
	public void setTimeOfImpactDetector(TimeOfImpactDetector timeOfImpactDetector) {
		if (timeOfImpactDetector == null) throw new NullPointerException(Messages.getString("dynamics.world.nullTimeOfImpactDetector"));
		this.timeOfImpactDetector = timeOfImpactDetector;
	}
	
	/**
	 * Returns the time of impact detector.
	 * @return {@link TimeOfImpactDetector} the time of impact detector
	 * @since 1.2.0
	 */
	public TimeOfImpactDetector getTimeOfImpactDetector() {
		return this.timeOfImpactDetector;
	}
	
	/**
	 * Sets the raycast detector.
	 * @param raycastDetector the raycast detector
	 * @throws NullPointerException if raycastDetector is null
	 * @since 2.0.0
	 */
	public void setRaycastDetector(RaycastDetector raycastDetector) {
		if (raycastDetector == null) throw new NullPointerException(Messages.getString("dynamics.world.nullRaycastDetector"));
		this.raycastDetector = raycastDetector;
	}
	
	/**
	 * Returns the raycast detector.
	 * @return {@link RaycastDetector} the raycast detector
	 * @since 2.0.0
	 */
	public RaycastDetector getRaycastDetector() {
		return this.raycastDetector;
	}

	/**
	 * Returns the {@link CoefficientMixer}.
	 * @return {@link CoefficientMixer}
	 * @see #setCoefficientMixer(CoefficientMixer)
	 */
	public CoefficientMixer getCoefficientMixer() {
		return this.coefficientMixer;
	}
	
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
	public void setCoefficientMixer(CoefficientMixer coefficientMixer) {
		if (coefficientMixer == null) throw new NullPointerException(Messages.getString("dynamics.world.nullCoefficientMixer"));
		this.coefficientMixer = coefficientMixer;
	}
	
	/**
	 * Sets the {@link ContactManager}.
	 * <p>
	 * A {@link ContactManager} manages the contacts detected in the {@link World#detect()} method
	 * and performs notification of {@link ContactListener}s.  {@link ContactManager}s can also contain
	 * specialized logic for improving performance and simulation quality.
	 * <p>
	 * Changing the contact manager requires an update to be performed on the next update of this
	 * world and any cached information will be lost.
	 * <p>
	 * The default is the {@link WarmStartingContactManager}.
	 * @param contactManager the contact manager
	 * @throws NullPointerException if contactManager is null
	 * @see ContactManager
	 * @since 3.2.0
	 */
	public void setContactManager(ContactManager contactManager) {
		if (contactManager == null) throw new NullPointerException(Messages.getString("dynamics.world.nullContactManager"));
		this.contactManager = contactManager;
		this.updateRequired = true;
	}
	
	/**
	 * Returns the {@link ContactManager}.
	 * @return {@link ContactManager}
	 * @since 1.0.2
	 * @see #setContactManager(ContactManager)
	 */
	public ContactManager getContactManager() {
		return this.contactManager;
	}
	
	/**
	 * Sets the {@link ContactConstraintSolver} for this world.
	 * @param constraintSolver the contact constraint solver
	 * @throws NullPointerException if contactManager is null
	 * @see ContactConstraintSolver
	 * @since 3.2.0
	 */
	public void setContactConstraintSolver(ContactConstraintSolver constraintSolver) {
		if (constraintSolver == null) throw new NullPointerException(Messages.getString("dynamics.world.nullContactConstraintSolver"));
		this.contactConstraintSolver = constraintSolver;
	}
	
	/**
	 * Returns the {@link ContactConstraintSolver}.
	 * @return {@link ContactConstraintSolver}
	 * @since 3.2.0
	 * @see #setContactConstraintSolver(ContactConstraintSolver)
	 */
	public ContactConstraintSolver getContactConstraintSolver() {
		return this.contactConstraintSolver;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.DataContainer#getUserData()
	 */
	public Object getUserData() {
		return this.userData;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.DataContainer#setUserData(java.lang.Object)
	 */
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
	/**
	 * Returns the number of {@link Body}s in this {@link World}.
	 * @return int the number of bodies
	 */
	public int getBodyCount() {
		return this.bodies.size();
	}
	
	/**
	 * Returns the {@link Body} at the given index.
	 * @param index the index
	 * @return {@link Body}
	 */
	public Body getBody(int index) {
		return this.bodies.get(index);
	}
	
	/**
	 * Returns an unmodifiable list containing all the bodies in this world.
	 * <p>
	 * The returned list is backed by the internal list, therefore adding or removing bodies while 
	 * iterating through the returned list is not permitted.  Use the {@link #getBodyIterator()}
	 * method instead.
	 * @return List&lt;{@link Body}&gt;
	 * @since 3.1.5
	 * @see #getBodyIterator()
	 */
	public List<Body> getBodies() {
		return Collections.unmodifiableList(this.bodies);
	}

	/**
	 * Returns an iterator for iterating over the bodies in this world.
	 * <p>
	 * The returned iterator supports the <code>remove</code> method.
	 * @return Iterator&lt;{@link Body}&gt;
	 * @since 3.2.0
	 */
	public Iterator<Body> getBodyIterator() {
		return new BodyIterator(this);
	}
	
	/**
	 * Returns the number of {@link Joint}s in this {@link World}.
	 * @return int the number of joints
	 */
	public int getJointCount() {
		return this.joints.size();
	}
	
	/**
	 * Returns the {@link Joint} at the given index.
	 * @param index the index
	 * @return {@link Joint}
	 */
	public Joint getJoint(int index) {
		return this.joints.get(index);
	}
	
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
	public List<Joint> getJoints() {
		return Collections.unmodifiableList(this.joints);
	}

	/**
	 * Returns an iterator for iterating over the joints in this world.
	 * <p>
	 * The returned iterator supports the <code>remove</code> method.
	 * @return Iterator&lt;{@link Joint}&gt;
	 * @since 3.2.0
	 */
	public Iterator<Joint> getJointIterator() {
		return new JointIterator(this);
	}
	
	/**
	 * Returns the {@link Step} object used to advance
	 * the simulation.
	 * <p>
	 * The returned object contains the step information (elapsed time)
	 * for the last and the previous time step.
	 * @return {@link Step} the current step object
	 */
	public Step getStep() {
		return this.step;
	}
	
	/**
	 * Returns true if this world doesn't contain any
	 * bodies or joints.
	 * @return boolean
	 * @since 3.0.1
	 */
	public boolean isEmpty() {
		int bSize = this.bodies.size();
		int jSize = this.joints.size();
		return bSize == 0 && jSize == 0;
	}
	
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
	public double getAccumulatedTime() {
		return this.time;
	}
	
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
	public void setAccumulatedTime(double elapsedTime) {
		if (elapsedTime < 0.0) return;
		this.time = elapsedTime;
	}
}
