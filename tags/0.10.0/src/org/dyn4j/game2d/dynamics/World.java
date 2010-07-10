/*
 * Copyright (c) 2010, William Bittle
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
package org.dyn4j.game2d.dynamics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.dyn4j.game2d.collision.Bounds;
import org.dyn4j.game2d.collision.BoundsAdapter;
import org.dyn4j.game2d.collision.BoundsListener;
import org.dyn4j.game2d.collision.Filter;
import org.dyn4j.game2d.collision.broadphase.BroadphaseDetector;
import org.dyn4j.game2d.collision.broadphase.BroadphasePair;
import org.dyn4j.game2d.collision.broadphase.Sap;
import org.dyn4j.game2d.collision.manifold.ClippingManifoldSolver;
import org.dyn4j.game2d.collision.manifold.Manifold;
import org.dyn4j.game2d.collision.manifold.ManifoldPoint;
import org.dyn4j.game2d.collision.manifold.ManifoldSolver;
import org.dyn4j.game2d.collision.narrowphase.Gjk;
import org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.game2d.collision.narrowphase.Penetration;
import org.dyn4j.game2d.dynamics.contact.Contact;
import org.dyn4j.game2d.dynamics.contact.ContactAdapter;
import org.dyn4j.game2d.dynamics.contact.ContactConstraint;
import org.dyn4j.game2d.dynamics.contact.ContactEdge;
import org.dyn4j.game2d.dynamics.contact.ContactListener;
import org.dyn4j.game2d.dynamics.contact.ContactManager;
import org.dyn4j.game2d.dynamics.contact.ContactPoint;
import org.dyn4j.game2d.dynamics.contact.SensedContactPoint;
import org.dyn4j.game2d.dynamics.joint.Joint;
import org.dyn4j.game2d.dynamics.joint.JointEdge;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Manages the logic of collision detection, resolution, and reporting.
 * @author William Bittle
 */
public class World {
	/** Earths gravity constant */
	public static final Vector2 EARTH_GRAVITY = new Vector2(0.0, -9.8);
	
	/** Zero gravity constant */
	public static final Vector2 ZERO_GRAVITY = new Vector2(0.0, 0.0);
		
	/** The {@link Step} used by the dynamics calculations */
	protected Step step;
	
	/** The world gravity vector */
	protected Vector2 gravity;
	
	/** The world {@link Bounds} */
	protected Bounds bounds;
	
	/** The {@link BroadphaseDetector} */
	protected BroadphaseDetector broadphaseDetector;
	
	/** The {@link NarrowphaseDetector} */
	protected NarrowphaseDetector narrowphaseDetector;
	
	/** The {@link ManifoldSolver} */
	protected ManifoldSolver manifoldSolver;
	
	/** The {@link CollisionListener} */
	protected CollisionListener collisionListener;
	
	/** The {@link ContactManager} */
	protected ContactManager contactManager;

	/** The {@link BoundsListener} */
	protected BoundsListener boundsListener;
	
	/** The {@link DestructionListener} */
	protected DestructionListener destructionListener;
	
	/** The {@link StepListener} */
	protected StepListener stepListener;
	
	/** The {@link CoefficientMixer} */
	protected CoefficientMixer coefficientMixer;
	
	/** The {@link Body} list */
	protected List<Body> bodies;
	
	/** The {@link Joint} list */
	protected List<Joint> joints;
	
	/** The reusable island */
	protected Island island;
	
	/** The accumulated time */
	protected double time;
	
	/**
	 * Default constructor.
	 * <p>
	 * Builds a simulation {@link World} without bounds.
	 */
	public World() {
		this(Bounds.UNBOUNDED);
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * Defaults to using {@link #EARTH_GRAVITY}, {@link Sap} broad-phase,
	 * {@link Gjk} narrow-phase, and {@link ClippingManifoldSolver}.
	 * @param bounds the bounds of the {@link World}
	 */
	public World(Bounds bounds) {
		if (bounds == null) throw new NullPointerException("The bounds cannot be null.  Use the Bounds.UNBOUNDED object to have no bounds.");
		this.step = new Step();
		this.gravity = World.EARTH_GRAVITY;
		this.bounds = bounds;
		this.broadphaseDetector = new Sap();
		this.narrowphaseDetector = new Gjk();
		this.manifoldSolver = new ClippingManifoldSolver();
		// create empty listeners
		this.collisionListener = new CollisionAdapter();
		this.contactManager = new ContactManager(new ContactAdapter());
		this.boundsListener = new BoundsAdapter();
		this.destructionListener = new DestructionAdapter();
		this.stepListener = new StepAdapter();
		this.coefficientMixer = CoefficientMixer.DEFAULT_MIXER;
		this.bodies = new ArrayList<Body>();
		this.joints = new ArrayList<Joint>();
		this.island = new Island();
		this.time = 0.0;
	}
	
	/**
	 * Updates the {@link World}.
	 * <p>
	 * This method will only update the world given the step frequency contained
	 * in the {@link Settings} object.  You can use the {@link StepListener} interface
	 * to listen for when a step is actually performed.  In addition, this method will
	 * return true if a step was performed.
	 * <p>
	 * Alternatively you can call the {@link #update(double)} method to use a variable
	 * time step.
	 * <p>
	 * This method immediately returns if the given elapsedTime is less than or equal to
	 * zero.
	 * @see #update(double)
	 * @param elapsedTime the elapsed time in seconds
	 * @return boolean true if the {@link World} performed a simulation step
	 */
	public boolean update(double elapsedTime) {
		// make sure the update time is greater than zero
		if (elapsedTime <= 0.0) return false;
		// update the time
		this.time += elapsedTime;
		// check the frequency in settings
		double invhz = Settings.getInstance().getStepFrequency();
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
	 * This method will update the world on every call.  Unlike the {@link #update(double)}
	 * method, this method uses the given elapsed time and does not attempt to update the world
	 * in a set interval.
	 * <p>
	 * This method immediately returns if the given elapsedTime is less than or equal to
	 * zero.
	 * @see #update(double)
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
	 * @param steps the number of simulation steps to perform
	 */
	public void step(int steps) {
		// get the frequency from settings
		double invhz = Settings.getInstance().getStepFrequency();
		// perform the steps
		this.step(steps, invhz);
	}
	
	/**
	 * Performs the given number of simulation steps using the given step frequency.
	 * <p>
	 * This method immediately returns if the given elapsedTime is less than or equal to
	 * zero.
	 * @param steps the number of simulation steps to perform
	 * @param elapsedTime the elapsed time for each step
	 */
	public void step(int steps, double elapsedTime) {
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
	 * Steps the {@link World} using the current {@link Step}.
	 * <p>
	 * Performs collision detection and resolution.
	 * <p>
	 * Use the various listeners to listen for events during the execution of
	 * this method.
	 * <p>
	 * Take care when performing methods on the {@link World} object in any
	 * event listeners tied to this method.
	 */
	protected void step() {
		// notify the step listener
		this.stepListener.begin(this.step, this);
		
		// clear the old contact list (does NOT clear the contact map
		// which is used to warm start)
		this.contactManager.clear();
		
		// get the number of bodies
		int size = this.bodies.size();
		
		// test for out of bounds objects
		// clear the body contacts
		// clear the island flag
		for (int i = 0; i < size; i++) {
			Body body = this.bodies.get(i);
			// skip if already not active
			if (!body.isActive()) continue;
			// check if the body is out of bounds
			if (this.bounds.isOutside(body)) {
				// set the body to inactive
				body.setActive(false);
				// if so, notify via the listener
				this.boundsListener.outside(body);
			}
			// clear all the old contacts
			body.contacts.clear();
			// remove the island flag
			body.setOnIsland(false);
		}
		
		// clear the joint island flags
		int jSize = this.joints.size();
		for (int i = 0; i < jSize; i++) {
			// get the joint
			Joint joint = this.joints.get(i);
			// set the island flag to false
			joint.setOnIsland(false);
		}
		
		// make sure there are some bodies
		if (size > 0) {
			// test for collisions via the broad-phase
			List<BroadphasePair<Body>> pairs = this.broadphaseDetector.detect(this.bodies);
			int pSize = pairs.size();		
			
			// using the broad-phase results, test for narrow-phase
			for (int i = 0; i < pSize; i++) {
				BroadphasePair<Body> pair = pairs.get(i);
				
				// get the bodies
				Body body1 = pair.getObject1();
				Body body2 = pair.getObject2();
				
				// inactive objects don't have collision detection/response
				if (!body1.isActive() || !body2.isActive()) continue;
				// one body must be dynamic
				if (!body1.isDynamic() && !body2.isDynamic()) continue;
				// check for connected pairs who's collision is not allowed
				if (body1.isConnected(body2, false)) continue;
				
				// notify of the broadphase collision
				if (!this.collisionListener.collision(body1, body2)) {
					// if the collision listener returned false then skip this collision
					continue;
				}
	
				// get their transforms
				Transform transform1 = body1.transform;
				Transform transform2 = body2.transform;
				
				// create a reusable penetration object
				Penetration penetration = new Penetration();
				// create a reusable manifold object
				Manifold manifold = new Manifold();
				
				// loop through the fixtures of body 1
				int b1Size = body1.getFixtureCount();
				int b2Size = body2.getFixtureCount();
				for (int j = 0; j < b1Size; j++) {
					Fixture fixture1 = body1.getFixture(j);
					Filter filter1 = fixture1.getFilter();
					Convex convex1 = fixture1.getShape();
					// test against each fixture of body 2
					for (int k = 0; k < b2Size; k++) {
						Fixture fixture2 = body2.getFixture(k);
						Filter filter2 = fixture2.getFilter();
						Convex convex2 = fixture2.getShape();
						// test the filter
						if (!filter1.isAllowed(filter2)) {
							// if the collision is not allowed then continue
							continue;
						}
						// test the two convex shapes
						if (this.narrowphaseDetector.detect(convex1, transform1, convex2, transform2, penetration)) {
							// notify of the narrowphase collision
							if (!this.collisionListener.collision(body1, fixture1, body2, fixture2, penetration)) {
								// if the collision listener returned false then skip this collision
								continue;
							}
							// if there is penetration then find a contact manifold
							// using the filled in penetration object
							if (this.manifoldSolver.getManifold(penetration, convex1, transform1, convex2, transform2, manifold)) {
								// notify of the manifold solving result
								if (!this.collisionListener.collision(body1, fixture1, body2, fixture2, manifold)) {
									// if the collision listener returned false then skip this collision
									continue;
								}
								// get the manifold points
								List<ManifoldPoint> points = manifold.getPoints();
								// a valid manifold was found
								int mSize = points.size();
								// don't add sensor manifolds to the contact constraints list
								if (!fixture1.isSensor() && !fixture2.isSensor()) {
									// compute the friction and restitution
									double friction = this.coefficientMixer.mixFriction(fixture1.getFriction(), fixture2.getFriction());
									double restitution = this.coefficientMixer.mixRestitution(fixture1.getRestitution(), fixture2.getRestitution());
									// create a contact constraint
									ContactConstraint contactConstraint = new ContactConstraint(body1, fixture1, body2, fixture2, manifold, friction, restitution);
									// add a contact edge to both bodies
									ContactEdge contactEdge1 = new ContactEdge(body2, contactConstraint);
									ContactEdge contactEdge2 = new ContactEdge(body1, contactConstraint);
									body1.contacts.add(contactEdge1);
									body2.contacts.add(contactEdge2);
									// add the contact constraint to the contact manager
									this.contactManager.add(contactConstraint);
								} else {
									// add the sensed contacts to the contact manager
									for (int l = 0; l < mSize; l++) {
										// get the manifold point
										ManifoldPoint manifoldPoint = points.get(l);
										// create the sensed contact
										SensedContactPoint point = new SensedContactPoint(
																			manifoldPoint.getPoint(),
																			manifold.getNormal(),
																			manifoldPoint.getDepth(),
																			body1,
																			fixture1,
																			body2,
																			fixture2);
										// add the sensed contact
										this.contactManager.add(point);
									}
								}
							}
						}
					}
				}
			}
		}
		
		// warm start the contact constraints
		this.contactManager.warm();
		
		// notify of all the contacts that will be solved and all the sensed contacts
		this.contactManager.preSolveNotify();
		
		// perform a depth first search of the contact graph
		// to create islands for constraint solving
		Stack<Body> stack = new Stack<Body>();
		stack.ensureCapacity(size);
		// loop over the bodies and their contact edges to create the islands
		for (int i = 0; i < size; i++) {
			Body seed = this.bodies.get(i);
			// skip if asleep, in active, static, or already on an island
			if (seed.isOnIsland() || seed.isAsleep() || !seed.isActive() || seed.isStatic()) continue;
			
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
					ContactConstraint contactConstraint = contactEdge.getContactConstraint();
					// get the other body
					Body other = contactEdge.getOther();
					// check if the contact constraint has already been added to an island
					if (contactConstraint.isOnIsland()) continue;
					// add the contact constraint to the island list
					island.add(contactConstraint);
					// set the island flag on the contact constraint
					contactConstraint.setOnIsland(true);
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
					Joint joint = jointEdge.getJoint();
					// check if the joint is inactive
					if (!joint.isActive()) continue;
					// get the other body
					Body other = jointEdge.getOther();
					// check if the joint has already been added to an island
					// or if the other body is not active
					if (joint.isOnIsland() || !other.isActive()) continue;
					// add the joint to the island
					island.add(joint);
					// set the island flag on the joint
					joint.setOnIsland(true);
					// check if the other body has been added to an island
					if (!other.isOnIsland()) {
						// if not then add the body to the stack
						stack.push(other);
						other.setOnIsland(true);
					}
				}
			}
			
			// solve the island
			island.solve(this.gravity, this.step);
			
			// allow static bodies to participate in other islands
			for (int j = 0; j < size; j++) {
				Body body = this.bodies.get(j);
				if (body.isStatic()) {
					body.setOnIsland(false);
				}
			}
		}
		
		// notify of the all solved contacts
		this.contactManager.postSolveNotify();
		
		// notify the step listener
		this.stepListener.end(this.step, this);
	}
	
	/**
	 * Adds a {@link Body} to the {@link World}.
	 * @param body the {@link Body} to add
	 */
	public void add(Body body) {
		// check for null body
		if (body == null) throw new NullPointerException("Cannot add a null body to the world.");
		// add it to the world
		this.bodies.add(body);
	}
	
	/**
	 * Adds a {@link Joint} to the {@link World}.
	 * @param joint the {@link Joint} to add
	 */
	public void add(Joint joint) {
		// check for null joint
		if (joint == null) throw new NullPointerException("Cannot add a null joint to the world.");
		// add the joint to the joint list
		this.joints.add(joint);
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
	 * Removes the given {@link Body} from the {@link World}.
	 * @param body the {@link Body} to remove
	 * @return boolean true if the body was removed
	 */
	public boolean remove(Body body) {
		// check for null joint
		if (body == null) return false;
		// remove the body from the list
		boolean removed = this.bodies.remove(body);
		
		// only remove joints and contacts if the body was removed
		if (removed) {
			// wake up any bodies connected to this body by a joint
			// and destroy the joints
			int jsize = body.joints.size();
			for (int i = 0; i < jsize; i++) {
				// get the joint edge
				JointEdge jointEdge = body.joints.get(i);
				// get the joint
				Joint joint = jointEdge.getJoint();
				// get the other body
				Body other = jointEdge.getOther();
				// wake up the other body
				other.setAsleep(false);
				// remove the contact edge from the other body
				Iterator<JointEdge> iterator = other.joints.iterator();
				while (iterator.hasNext()) {
					// get the joint edge
					JointEdge otherJointEdge = iterator.next();
					// get the joint
					Joint otherJoint = otherJointEdge.getJoint();
					if (otherJoint == joint) {
						// remove the joint edge
						iterator.remove();
						// since they are the same contact constraint object
						// we only need to notify of this bodies destroyed contacts
					}
				}
				// notify of the destroyed joint
				this.destructionListener.destroyed(joint);
			}
			
			// remove any contacts this body had with any other body
			int csize = body.contacts.size();
			for (int i = 0; i < csize; i++) {
				// get the contact edge
				ContactEdge contactEdge = body.contacts.get(i);
				// get the contact constraint
				ContactConstraint contactConstraint = contactEdge.getContactConstraint();
				// get the other body
				Body other = contactEdge.getOther();
				// wake up the other body
				other.setAsleep(false);
				// remove the contact edge connected from the other body
				// to this body
				Iterator<ContactEdge> iterator = other.contacts.iterator();
				while (iterator.hasNext()) {
					ContactEdge ceOther = iterator.next();
					// get the contact constraint
					ContactConstraint ccOther = ceOther.getContactConstraint();
					// check if the contact constraint is the same
					if (ccOther == contactConstraint) {
						// remove the contact edge
						iterator.remove();
						// since they are the same contact constraint object
						// we only need to notify of this bodies destroyed contacts
					}
				}
				// remove the contact constraint from the contact manager
				this.contactManager.remove(contactConstraint);
				// loop over the contact points
				int size = contactConstraint.getContacts().length;
				for (int j = 0; j < size; j++) {
					// get the contact
					Contact contact = contactConstraint.getContacts()[j];
					// create a contact point for notification
					ContactPoint contactPoint = new ContactPoint(
													contactConstraint.getBody1(), 
													contactConstraint.getFixture1(), 
													contactConstraint.getBody2(), 
													contactConstraint.getFixture2(),
													contact.isEnabled(),
													contact.getPoint(), 
													contactConstraint.getNormal(), 
													contact.getDepth());
					// call the destruction listener
					this.destructionListener.destroyed(contactPoint);
				}
			}
		}
		
		return removed;
	}
	
	/**
	 * Removes the given {@link Joint} from the {@link World}.
	 * @param joint the {@link Joint} to remove
	 * @return boolean true if the {@link Joint} was removed
	 */
	public boolean remove(Joint joint) {
		// check for null joint
		if (joint == null) return false;
		// remove the joint from the joint list
		boolean removed = this.joints.remove(joint);
		
		// see if the given joint was removed
		if (removed) {
			// remove the joint edges from body1
			Iterator<JointEdge> iterator = joint.getBody1().joints.iterator();
			while (iterator.hasNext()) {
				// see if this is the edge we want to remove
				JointEdge jointEdge = iterator.next();
				if (jointEdge.getJoint() == joint) {
					// then remove this joint edge
					iterator.remove();
				}
			}
			// remove the joint edges from body2
			iterator = joint.getBody2().joints.iterator();
			while (iterator.hasNext()) {
				// see if this is the edge we want to remove
				JointEdge jointEdge = iterator.next();
				if (jointEdge.getJoint() == joint) {
					// then remove this joint edge
					iterator.remove();
				}
			}
			
			// finally wake both bodies
			joint.getBody1().setAsleep(false);
			joint.getBody2().setAsleep(false);
		}
		
		return removed;
	}
	
	/**
	 * Sets the gravity.
	 * <p>
	 * Setting the gravity vector to the zero vector eliminates gravity.
	 * <p>
	 * A NullPointerException is thrown if the given gravity vector is null.
	 * @param gravity the gravity in meters/second<sup>2</sup>
	 */
	public void setGravity(Vector2 gravity) {
		if (gravity == null) throw new NullPointerException("The gravity vector cannot be null.");
		this.gravity = gravity;
	}
	
	/**
	 * Returns the gravity.
	 * @return {@link Vector2} the gravity in meters/second<sup>2</sup>
	 */
	public Vector2 getGravity() {
		return this.gravity;
	}

	/**
	 * Sets the bounds of the {@link World}.
	 * @param bounds the bounds
	 */
	public void setBounds(Bounds bounds) {
		// check for null bounds
		if (bounds == null) throw new NullPointerException("The bounds cannot be null.  To creat an unbounded world use the Bounds.UNBOUNDED static member.");
		this.bounds = bounds;
	}
	
	/**
	 * Returns the bounds of the world.
	 * @return {@link Bounds} the bounds
	 */
	public Bounds getBounds() {
		return this.bounds;
	}
	
	/**
	 * Sets the bounds listener.
	 * @param boundsListener the bounds listener
	 */
	public void setBoundsListener(BoundsListener boundsListener) {
		if (boundsListener == null) throw new NullPointerException("The bounds listener cannot be null.  Create an instance of the BoundsAdapter class to set it to the default.");
		this.boundsListener = boundsListener;
	}
	
	/**
	 * Returns the bounds listener.
	 * @return {@link BoundsListener} the bounds listener
	 */
	public BoundsListener getBoundsListener() {
		return this.boundsListener;
	}
	
	/**
	 * Sets the {@link ContactListener}.
	 * @param contactListener the contact listener
	 */
	public void setContactListener(ContactListener contactListener) {
		if (contactListener == null) throw new NullPointerException("The contact listener cannot be null.  Create an instance of the ContactAdapter class to set it to the default.");
		this.contactManager.setContactListener(contactListener);
	}
	
	/**
	 * Returns the contact listener.
	 * @return {@link ContactListener} the contact listener
	 */
	public ContactListener getContactListener() {
		return this.contactManager.getContactListener();
	}
	
	/**
	 * Sets the {@link DestructionListener}.
	 * @param destructionListener the {@link DestructionListener}
	 */
	public void setDestructionListener(DestructionListener destructionListener) {
		if (destructionListener == null) throw new NullPointerException("The destruction listener cannot be null.  Create an instance of the DestructionAdapter class to set it to the default.");
		this.destructionListener = destructionListener;
	}
	
	/**
	 * Returns the {@link DestructionListener}.
	 * @return {@link DestructionListener} the destruction listener
	 */
	public DestructionListener getDestructionListener() {
		return this.destructionListener;
	}
	
	/**
	 * Sets the {@link StepListener}.
	 * @param stepListener the {@link StepListener}
	 */
	public void setStepListener(StepListener stepListener) {
		if (stepListener == null) throw new NullPointerException("The step listener cannot be null.  Create an instance of the StepAdapter class to set it to the default.");
		this.stepListener = stepListener;
	}
	
	/**
	 * Returns the {@link StepListener}
	 * @return {@link StepListener}
	 */
	public StepListener getStepListener() {
		return this.stepListener;
	}
	
	/**
	 * Sets the broad-phase collision detection algorithm.
	 * <p>
	 * If the given detector is null then the default {@link Sap}
	 * {@link BroadphaseDetector} is set as the current broad phase.
	 * @param broadphaseDetector the broad-phase collision detection algorithm
	 */
	public void setBroadphaseDetector(BroadphaseDetector broadphaseDetector) {
		if (broadphaseDetector == null) throw new NullPointerException("The broadphase detector cannot be null.");
		this.broadphaseDetector = broadphaseDetector;
	}
	
	/**
	 * Returns the broad-phase collision detection algorithm.
	 * @return {@link BroadphaseDetector} the broad-phase collision detection algorithm
	 */
	public BroadphaseDetector getBroadphaseDetector() {
		return this.broadphaseDetector;
	}
	
	/**
	 * Sets the narrow-phase collision detection algorithm.
	 * <p>
	 * If the given detector is null then the default {@link Gjk}
	 * {@link NarrowphaseDetector} is set as the current narrow phase.
	 * @param narrowphaseDetector the narrow-phase collision detection algorithm
	 */
	public void setNarrowphaseDetector(NarrowphaseDetector narrowphaseDetector) {
		if (narrowphaseDetector == null) throw new NullPointerException("The narrowphase detector cannot be null.");
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
	 */
	public void setManifoldSolver(ManifoldSolver manifoldSolver) {
		if (manifoldSolver == null) throw new NullPointerException("The manifold solver cannot be null.");
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
	 * Sets the collision listener.
	 * @param collisionListener the collision listener
	 */
	public void setCollisionListener(CollisionListener collisionListener) {
		if (collisionListener == null) throw new NullPointerException("The collision listener cannot be null.  Create an instance of the CollisionAdapter class to set it to the default.");
		this.collisionListener = collisionListener;
	}
	
	/**
	 * Returns the collision listener.
	 * @return {@link CollisionListener} the collision listener
	 */
	public CollisionListener getCollisionListener() {
		return this.collisionListener;
	}
	
	/**
	 * Returns the {@link CoefficientMixer}.
	 * @return {@link CoefficientMixer}
	 */
	public CoefficientMixer getCoefficientMixer() {
		return coefficientMixer;
	}
	
	/**
	 * Sets the {@link CoefficientMixer}.
	 * @param coefficientMixer the coefficient mixer
	 */
	public void setCoefficientMixer(CoefficientMixer coefficientMixer) {
		if (coefficientMixer == null) throw new NullPointerException("The coefficient mixer cannot be null.");
		this.coefficientMixer = coefficientMixer;
	}
	
	/**
	 * Clears the joints and bodies from the world.
	 */
	public void clear() {
		this.joints.clear();
		this.bodies.clear();
	}
	
	/**
	 * Returns the number of {@link Body} objects.
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
	 * Returns the number of {@link Joint} objects.
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
	 * Returns the {@link Step} object used to advance
	 * the simulation.
	 * @return {@link Step} the current step object
	 */
	public Step getStep() {
		return this.step;
	}
}
