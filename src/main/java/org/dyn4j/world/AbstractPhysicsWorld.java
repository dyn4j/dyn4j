/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dyn4j.DataContainer;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Collisions;
import org.dyn4j.collision.Filter;
import org.dyn4j.collision.broadphase.AABBExpansionMethod;
import org.dyn4j.collision.broadphase.AABBProducer;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.broadphase.BroadphaseFilter;
import org.dyn4j.collision.broadphase.CollisionBodyBroadphaseFilter;
import org.dyn4j.collision.broadphase.DynamicAABBTree;
import org.dyn4j.collision.broadphase.StaticValueAABBExpansionMethod;
import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.ContinuousDetectionMode;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactConstraintSolver;
import org.dyn4j.dynamics.contact.ContactUpdateHandler;
import org.dyn4j.dynamics.contact.ForceCollisionTimeOfImpactSolver;
import org.dyn4j.dynamics.contact.SequentialImpulses;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.dynamics.contact.TimeOfImpactSolver;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.exception.ArgumentNullException;
import org.dyn4j.exception.ObjectAlreadyExistsException;
import org.dyn4j.exception.ObjectAlreadyOwnedException;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.listener.ContactListener;
import org.dyn4j.world.listener.DestructionListener;
import org.dyn4j.world.listener.StepListener;
import org.dyn4j.world.listener.TimeOfImpactListener;

/**
 * Abstract implementation of the {@link PhysicsWorld} interface.
 * <p>
 * This class builds on top of the {@link AbstractCollisionWorld} class adding a physics
 * pipeline to the collision detection pipeline. This class implements the {@link #processCollisions(Iterator)}
 * method and uses it to build a {@link ConstraintGraph} which is then used to solve
 * {@link ContactConstraint}s and {@link Joint}s.
 * <p>
 * Extenders only need to implement the {@link #createCollisionData(org.dyn4j.collision.CollisionPair)} method
 * to ensure the correct type of collision data is used for tracking.
 * <p>
 * <b>NOTE</b>: This class uses the {@link Body#setOwner(Object)} and 
 * {@link Body#setFixtureModificationHandler(org.dyn4j.collision.FixtureModificationHandler)}
 * methods to handle certain scenarios like fixture removal on a body or bodies added to
 * more than one world. Likewise, the {@link Joint#setOwner(Object)} method is used to handle
 * joints being added to the world. Callers should <b>NOT</b> use the methods.
 * @author William Bittle
 * @version 6.0.0
 * @since 4.0.0
 * @param <T> the {@link PhysicsBody} type
 * @param <V> the {@link ContactCollisionData} type
 */
public abstract class AbstractPhysicsWorld<T extends PhysicsBody, V extends ContactCollisionData<T>> extends AbstractCollisionWorld<T, BodyFixture, V> implements PhysicsWorld<T, V>, Shiftable, DataContainer {
	/** The dynamics settings for this world */
	protected final Settings settings;
	
	/** The {@link TimeStep} used by the dynamics calculations */
	protected final TimeStep timeStep;
	
	/** The world gravity vector */
	protected final Vector2 gravity;
	
	/** The {@link ValueMixer} */
	protected ValueMixer valueMixer;
	
	/** The {@link ContactConstraintSolver} */
	protected ContactConstraintSolver<T> contactConstraintSolver;
	
	/** The {@link TimeOfImpactSolver} */
	protected TimeOfImpactSolver<T> timeOfImpactSolver;

	/** The CCD {@link BroadphaseDetector} */
	protected BroadphaseDetector<T> ccdBroadphase;
	
	/** The {@link Joint} list */
	protected final List<Joint<T>> joints;

	/** The unmodifiable {@link Joint} list */
	protected final List<Joint<T>> jointsUnmodifiable;
	
	// listeners
	
	/** The list of {@link ContactListener}s */
	protected final List<ContactListener<T>> contactListeners;

	/** The unmodifiable list of {@link ContactListener}s */
	protected final List<ContactListener<T>> contactListenersUnmodifiable;
	
	/** The list of {@link DestructionListener}s */
	protected final List<DestructionListener<T>> destructionListeners;
	
	/** The unmodifiable list of {@link DestructionListener}s */
	protected final List<DestructionListener<T>> destructionListenersUnmodifiable;
	
	/** The list of {@link TimeOfImpactListener}s */
	protected final List<TimeOfImpactListener<T>> timeOfImpactListeners;
	
	/** The unmodifiable list of {@link TimeOfImpactListener}s */
	protected final List<TimeOfImpactListener<T>> timeOfImpactListenersUnmodifiable;
	
	/** The list of {@link StepListener}s */
	protected final List<StepListener<T>> stepListeners;
	
	/** The unmodifiable list of {@link StepListener}s */
	protected final List<StepListener<T>> stepListenersUnmodifiable;
	
	// state data

	/** The accumulated time */
	protected double time;

	/** True if an update to the collision data or interaction graph is needed before a step of the engine */
	protected boolean updateRequired;
	
	// collision tracking
	
	/** The constraint graph between bodies */
	protected final ConstraintGraph<T> constraintGraph;
	
	/** A temporary list of only the {@link ContactConstraint} collisions from the last detection; cleared and refilled each step */
	protected final List<V> contactCollisions;

	/** The full set of tracked CCD collision data */
	protected final Set<CollisionPair<T>> ccdCollisionData;
	
	/**
	 * Default constructor.
	 * <p>
	 * Uses the {@link CollisionWorld#DEFAULT_INITIAL_BODY_CAPACITY} and
	 * {@link PhysicsWorld#DEFAULT_INITIAL_JOINT_CAPACITY} as the initial capacity.
	 */
	public AbstractPhysicsWorld() {
		this(DEFAULT_INITIAL_BODY_CAPACITY, DEFAULT_INITIAL_JOINT_CAPACITY);
	}

	/**
	 * Optional constructor.
	 * @param initialBodyCapacity the initial body capacity
	 * @param initialJointCapacity the initial joint capacity
	 */
	public AbstractPhysicsWorld(int initialBodyCapacity, int initialJointCapacity) {
		super(initialBodyCapacity);
		
		if (initialBodyCapacity <= 0) {
			initialBodyCapacity = DEFAULT_INITIAL_BODY_CAPACITY;
		}
		
		if (initialJointCapacity <= 0) {
			initialJointCapacity = DEFAULT_INITIAL_JOINT_CAPACITY;
		}
		
		// initialize all the classes with default values
		this.settings = new Settings();
		this.timeStep = new TimeStep(this.settings.getStepFrequency());
		this.gravity = PhysicsWorld.EARTH_GRAVITY.copy();
		
		// override the broadphase filter
		// the CollisionWorld uses the DefaultBroadphaseFilter but 
		// the PhysicsWorld needs to use the DetectBroadphaseFilter
		this.broadphaseFilter = new PhysicsBodyBroadphaseCollisionDataFilter<T>(this);
		this.valueMixer = ValueMixer.DEFAULT_MIXER;
		this.contactConstraintSolver = new SequentialImpulses<T>();
		this.timeOfImpactSolver = new ForceCollisionTimeOfImpactSolver<T>();
		
		// build the CCD broadphase detector
		final BroadphaseFilter<T> broadphaseFilter = new CollisionBodyBroadphaseFilter<T>();
		final AABBProducer<T> aabbProducer = new PhysicsBodySweptAABBProducer<T>();
		final AABBExpansionMethod<T> expansionMethod = new StaticValueAABBExpansionMethod<T>(0.2);
		this.ccdBroadphase = new DynamicAABBTree<T>(
				broadphaseFilter,
				aabbProducer, 
				expansionMethod, 
				initialBodyCapacity);
		this.ccdBroadphase.setUpdateTrackingEnabled(true);
		
		this.joints = new ArrayList<Joint<T>>(initialJointCapacity);
		this.jointsUnmodifiable = Collections.unmodifiableList(this.joints);
		
		this.contactListeners = new ArrayList<ContactListener<T>>();
		this.destructionListeners = new ArrayList<DestructionListener<T>>();
		this.timeOfImpactListeners = new ArrayList<TimeOfImpactListener<T>>();
		this.stepListeners = new ArrayList<StepListener<T>>();
		
		this.contactListenersUnmodifiable = Collections.unmodifiableList(this.contactListeners);
		this.destructionListenersUnmodifiable = Collections.unmodifiableList(this.destructionListeners);
		this.timeOfImpactListenersUnmodifiable = Collections.unmodifiableList(this.timeOfImpactListeners);
		this.stepListenersUnmodifiable = Collections.unmodifiableList(this.stepListeners);
		
		this.time = 0.0;
		
		int estimatedCollisionPairs = Collisions.getEstimatedCollisionPairs(initialBodyCapacity);
		this.constraintGraph = new ConstraintGraph<T>(initialBodyCapacity, initialJointCapacity);
		this.contactCollisions = new ArrayList<V>(estimatedCollisionPairs);
		this.ccdCollisionData = new LinkedHashSet<CollisionPair<T>>();
		this.updateRequired = true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#update(double)
	 */
	@Override
	public boolean update(double elapsedTime) {
		return this.update(elapsedTime, -1.0, 1);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#update(double, int)
	 */
	@Override
	public boolean update(double elapsedTime, int maximumSteps) {
		return this.update(elapsedTime, -1.0, maximumSteps);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#update(double, double)
	 */
	@Override
	public boolean update(double elapsedTime, double stepElapsedTime) {
		return this.update(elapsedTime, stepElapsedTime, 1);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#update(double, double, int)
	 */
	@Override
	public boolean update(double elapsedTime, double stepElapsedTime, int maximumSteps) {
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
			this.timeStep.update(stepElapsedTime <= 0 ? invhz : stepElapsedTime);
			// reset the time
			this.time = this.time - invhz;
			// step the world
			this.step();
			// increment the number of steps
			steps++;
		}
		return steps > 0;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#updatev(double)
	 */
	@Override
	public void updatev(double elapsedTime) {
		// make sure the update time is greater than zero
		if (elapsedTime <= 0.0) return;
		// update the step
		this.timeStep.update(elapsedTime);
		// step the world
		this.step();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#step(int)
	 */
	@Override
	public void step(int steps) {
		// get the frequency from settings
		double invhz = this.settings.getStepFrequency();
		// perform the steps
		this.step(steps, invhz);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#step(int, double)
	 */
	@Override
	public void step(int steps, double elapsedTime) {
		// make sure the number of steps is greather than zero
		if (steps <= 0) return;
		// make sure the update time is greater than zero
		if (elapsedTime <= 0.0) return;
		// perform the steps
		for (int i = 0; i < steps; i++) {
			// update the step object
			this.timeStep.update(elapsedTime);
			// step the world
			this.step();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.AbstractCollisionWorld#addBody(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public void addBody(T body) {
		super.addBody(body);
		this.constraintGraph.addBody(body);
		this.ccdBroadphase.add(body);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#addJoint(org.dyn4j.dynamics.joint.Joint)
	 */
	@Override
	public void addJoint(Joint<T> joint) {
		// check for null joint
		if (joint == null) 
			throw new ArgumentNullException("joint");
		
		// dont allow adding it twice
		if (joint.getOwner() == this) 
			throw new ObjectAlreadyExistsException("joint", joint, joint.getOwner());
		
		// dont allow a joint that already is assigned to another world
		if (joint.getOwner() != null) 
			throw new ObjectAlreadyOwnedException("joint", joint, joint.getOwner());
		
		// dont allow someone to add a joint to the world when the joined bodies dont exist yet
		int bSize = joint.getBodyCount();
		for (int i = 0; i < bSize; i++) {
			T body = joint.getBody(i);
			if (!this.constraintGraph.containsBody(body)) {
				throw new IllegalArgumentException(String.format("All bodies must be added to the world before the joint is added. The body %1$s doesn't exist.", body));
			}
		}
		
		// add the joint to the joint list
		this.joints.add(joint);
		// set that its attached to this world
		joint.setOwner(this);
		// get the associated bodies
		this.constraintGraph.addJoint(joint);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#containsJoint(org.dyn4j.dynamics.joint.Joint)
	 */
	@Override
	public boolean containsJoint(Joint<T> joint) {
		return this.joints.contains(joint);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeJoint(int)
	 */
	@Override
	public boolean removeJoint(int index) {
		Joint<T> joint = this.joints.get(index);
		return removeJoint(joint);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.AbstractCollisionWorld#removeAllBodies()
	 */
	@Override
	public void removeAllBodies() {
		this.removeAllBodiesAndJoints(false);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.AbstractCollisionWorld#removeBody(int)
	 */
	@Override
	public boolean removeBody(int index) {
		return this.removeBody(index, false);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeBody(int, boolean)
	 */
	@Override
	public boolean removeBody(int index, boolean notify) {
		T body = this.bodies.get(index);
		return this.removeBody(body, notify);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.AbstractCollisionWorld#removeBody(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public boolean removeBody(T body) {
		return this.removeBody(body, false);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeAllBodiesAndJoints()
	 */
	@Override
	public void removeAllBodiesAndJoints() {
		this.removeAllBodiesAndJoints(false);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeAllJoints()
	 */
	@Override
	public void removeAllJoints() {
		this.removeAllJoints(false);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeAllBodiesAndJoints(boolean)
	 */
	@Override
	public void removeAllBodiesAndJoints(boolean notify) {
		this.removeAllBodies(notify);
	}
	
	/**
	 * Destroys all joints associated with the given constraint graph node.
	 * @param node the node
	 * @param notify true if destruction should emit notifications
	 */
	protected void destroyJoints(ConstraintGraphNode<T> node, boolean notify) {
		T body = node.body;
		
		// JOINT CLEANUP
		
		// wake up any bodies connected to this body by a joint
		// and destroy the joints and remove the edges
		int jSize = node.joints.size();
		for (int j = 0; j < jSize; j++) {
			Joint<T> joint = node.joints.get(j);
			
			int bSize = joint.getBodyCount();
			for (int i = 0; i < bSize; i++) {
				// get the other body
				T other = joint.getBody(i);
				
				// no need to process the same body
				if (other == body) {
					continue;
				}
				
				// wake up the other body
				other.setAtRest(false);
				// remove the joint from the constraint list of the other node
				ConstraintGraphNode<T> otherNode = this.constraintGraph.getNode(other);
				if (otherNode != null) {
					otherNode.joints.remove(joint);
				}
			}
			
			// remove the ownership
			joint.setOwner(null);
			
			// notify of the destroyed joint
			if (notify) {
				for (DestructionListener<T> dl : this.destructionListeners) {
					dl.destroyed(joint);
				}
			}
			
			// remove the joint from the world
			this.joints.remove(joint);
		}
		
		// clear the node's joints
		node.joints.clear();
	}
	
	/**
	 * Destroys the contacts for the given graph node.
	 * @param node the node
	 * @param fixture the fixture of the contacts to destroy; null means to destroy all
	 * @param notify true if destruction should emit notifications
	 */
	protected void destroyContacts(ConstraintGraphNode<T> node, BodyFixture fixture, boolean notify) {
		T body = node.body;
		
		// CONTACT CLEANUP

		// NOTE: I've opted to remove any collision data in the next collision 
		// detection phase except for the contact-constraint collision data. 
		// The effect is that users of the stored collisionData need 
		// to understand that the data stored there could include collision information 
		// for bodies that no longer exist in the world. The alternative is to iterate 
		// the entire set of pairs checking for this body - which isn't particularly
		// efficient.
		
		Iterator<ContactConstraint<T>> it = node.contactConstraints.iterator();
		while (it.hasNext()) {
			ContactConstraint<T> contactConstraint = it.next();

			// don't do anything with contact constraints that aren't involving the
			// given fixture
			if (fixture != null && 
				contactConstraint.getFixture1() != fixture && 
				contactConstraint.getFixture2() != fixture) {
				continue;
			}
			
			// get the other body involved
			T other = contactConstraint.getOtherBody(body);
			
			// clear the at-rest state of the connected body if the constraint
			// is NOT a sensor and is enabled
			if (!contactConstraint.isSensor() && contactConstraint.isEnabled()) {
				other.setAtRest(false);
			}
			
			// remove the stored collision data
			V data = this.collisionData.remove(contactConstraint.getCollisionPair());
			
			if (notify) {
				// notify of contact destruction
				List<? extends SolvedContact> contacts = contactConstraint.getContacts();
				int cSize = contacts.size();
				for (int k = 0; k < cSize; k++) {
					Contact contact = contacts.get(k);
					// call the destruction listeners
					for (ContactListener<T> cl : this.contactListeners) {
						cl.destroyed(data, contact);
					}
				}
				
				// notify destruction of collision info
				for (DestructionListener<T> dl : this.destructionListeners) {
					dl.destroyed(contactConstraint);
				}
			}
			
			// if we're trying to remove contacts for only a single fixture
			// then remove them during the iteration, otherwise they are
			// all cleared at the end of the iteration
			if (fixture != null) {
				it.remove();
			}
			
			// remove it from the other node
			ConstraintGraphNode<T> otherNode = this.constraintGraph.getNode(other);
			if (otherNode != null) {
				otherNode.contactConstraints.remove(contactConstraint);
			}
		}
		
		// if we were not given a fixture, then we need to remove all
		// contacts from the node
		if (fixture == null) {
			node.contactConstraints.clear();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeBody(org.dyn4j.dynamics.PhysicsBody, boolean)
	 */
	@Override
	public boolean removeBody(T body, boolean notify) {
		// check for null body
		if (body == null) return false;

		// remove the body from the list
		boolean removed = this.bodies.remove(body);
		
		// only remove joints and contacts if the body was removed
		if (removed) {
			// set the world property to null
			body.setFixtureModificationHandler(null);
			body.setOwner(null);
			body.setAtRest(false);
			body.setEnabled(true);
			
			// remove the body from the broadphase
			this.broadphaseDetector.remove(body);
			// remove the body from the ccd broadphase
			this.ccdBroadphase.remove(body);
			// remove from the interaction graph
			ConstraintGraphNode<T> node = this.constraintGraph.removeBody(body);
			
			// JOINT CLEANUP
			this.destroyJoints(node, notify);

			// CONTACT CLEANUP
			this.destroyContacts(node, null, notify);
		}
		
		return removed;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeJoint(org.dyn4j.dynamics.joint.Joint)
	 */
	@Override
	public boolean removeJoint(Joint<T> joint) {
		// NOTE: there's nothing to notify when removing a Joint
		boolean removed = this.joints.remove(joint);
		
		// removing a joint is pretty easy, we just need to make sure
		// we remove the joint from the interaction graph nodes
		if (removed) {
			joint.setOwner(null);
			
			// wake the bodies
			int bSize = joint.getBodyCount();
			for (int i = 0; i < bSize; i++) {
				T body = joint.getBody(i);
				body.setAtRest(false);
			}
			
			this.constraintGraph.removeJoint(joint);
		}
		
		return removed;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeAllBodies(boolean)
	 */
	@Override
	public void removeAllBodies(boolean notify) {
		int bsize = this.bodies.size();
		int jsize = this.joints.size();
		
		// if we don't need to notify of anything being
		// destroyed, then we can just clear everything
		if (!notify) {
			// remove ownership and modification handler
			for (int i = 0; i < bsize; i++) {
				T body = this.bodies.get(i);
				body.setFixtureModificationHandler(null);
				body.setOwner(null);
				body.setAtRest(false);
				body.setEnabled(true);
			}
			
			// remove ownership
			for (int i = 0; i < jsize; i++) {
				Joint<T> joint = this.joints.get(i);
				joint.setOwner(null);
			}
			
			// then just clear everything
			this.clear();
			return;
		}
		
		// loop over the bodies and clear all
		// joints and contacts
		for (int i = 0; i < bsize; i++) {
			// get the body
			T body = this.bodies.get(i);
			
			// set the world property to null
			body.setFixtureModificationHandler(null);
			body.setOwner(null);
			body.setAtRest(false);
			body.setEnabled(true);
			
			// notify of all the destroyed contacts
			// NOTE: we do a remove here because this will remove the edges
			// from the graph so that we don't report destruction for joints
			// and contact constraints twice
			ConstraintGraphNode<T> node = this.constraintGraph.removeBody(body);
			
			// JOINT CLEANUP
			this.destroyJoints(node, notify);
			
			// CONTACT CLEANUP
			this.destroyContacts(node, null, notify);

			// notify of the destroyed body
			for (DestructionListener<T> dl : this.destructionListeners) {
				dl.destroyed(body);
			}
		}
		
		// clear it all
		this.clear();
	}
	
	/**
	 * Helper method to clear the world of bodies and joints.
	 */
	protected void clear() {
		this.bodies.clear();
		this.broadphaseDetector.clear();
		this.ccdBroadphase.clear();
		this.collisionData.clear();
		this.constraintGraph.clear();
		this.contactCollisions.clear();
		this.joints.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeAllJoints(boolean)
	 */
	@Override
	public void removeAllJoints(boolean notify) {
		int size = this.joints.size();
		for (int i = 0; i < size; i++) {
			Joint<T> joint = this.joints.get(i);
			
			// clear the owner
			joint.setOwner(null);
			
			// wake the bodies
			int bSize = joint.getBodyCount();
			for (int j = 0; j < bSize; j++) {
				T body = joint.getBody(j);
				body.setAtRest(false);
			}
//			T b1 = joint.getBody1();
//			T b2 = joint.getBody2();
//			
//			b1.setAtRest(false);
//			b2.setAtRest(false);

			if (notify) {
				for (DestructionListener<T> dl : this.destructionListeners) {
					dl.destroyed(joint);
				}
			}
		}
		
		this.constraintGraph.removeAllJoints();
		this.joints.clear();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.AbstractCollisionWorld#handleFixtureRemoved(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	protected void handleFixtureRemoved(T body, BodyFixture fixture) {
		super.handleFixtureRemoved(body, fixture);

		// check the constraint graph for contacts to end
		ConstraintGraphNode<T> node = this.constraintGraph.getNode(body);
		if (node != null) {
			// CONTACT CLEANUP
			this.destroyContacts(node, fixture, true);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.AbstractCollisionWorld#handleAllFixturesRemoved(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	protected void handleAllFixturesRemoved(T body) {
		super.handleAllFixturesRemoved(body);
		
		// check the constraint graph for contacts to end
		ConstraintGraphNode<T> node = this.constraintGraph.getNode(body);
		if (node != null) {
			// CONTACT CLEANUP
			this.destroyContacts(node, null, true);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getSettings()
	 */
	@Override
	public Settings getSettings() {
		return this.settings;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#setSettings(org.dyn4j.dynamics.Settings)
	 */
	@Override
	public void setSettings(Settings settings) {
		if (settings == null) {
			return;
		}
		this.settings.set(settings);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#setGravity(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void setGravity(Vector2 gravity) {
		if (gravity == null) {
			return;
		}
		this.gravity.x = gravity.x;
		this.gravity.y = gravity.y;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#setGravity(double, double)
	 */
	@Override
	public void setGravity(double x, double y) {
		this.gravity.x = x;
		this.gravity.y = y;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getGravity()
	 */
	@Override
	public Vector2 getGravity() {
		return this.gravity;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getValueMixer()
	 */
	@Override
	public ValueMixer getValueMixer() {
		return this.valueMixer;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#setValueMixer(org.dyn4j.world.ValueMixer)
	 */
	@Override
	public void setValueMixer(ValueMixer valueMixer) {
		if (valueMixer == null) 
			throw new ArgumentNullException("valueMixer");
		
		this.valueMixer = valueMixer;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#setContactConstraintSolver(org.dyn4j.dynamics.contact.ContactConstraintSolver)
	 */
	@Override
	public void setContactConstraintSolver(ContactConstraintSolver<T> constraintSolver) {
		if (constraintSolver == null) 
			throw new ArgumentNullException("constraintSolver");
		
		this.contactConstraintSolver = constraintSolver;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getContactConstraintSolver()
	 */
	@Override
	public ContactConstraintSolver<T> getContactConstraintSolver() {
		return this.contactConstraintSolver;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getContinuousCollisionDetectionBroadphaseDetector()
	 */
	@Override
	public BroadphaseDetector<T> getContinuousCollisionDetectionBroadphaseDetector() {
		return this.ccdBroadphase;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#setContinuousCollisionDetectionBroadphaseDetector(org.dyn4j.collision.broadphase.BroadphaseDetector)
	 */
	@Override
	public void setContinuousCollisionDetectionBroadphaseDetector(BroadphaseDetector<T> broadphaseDetector) {
		// check for null
		if (broadphaseDetector == null) 
			throw new ArgumentNullException("broadphaseDetector");
		
		// set the new detector
		this.ccdBroadphase = broadphaseDetector;
		
		// re-add all bodies to the broadphase
		int size = this.bodies.size();
		for (int i = 0; i < size; i++) {
			T body = this.bodies.get(i);
			this.ccdBroadphase.add(body);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#setTimeOfImpactSolver(org.dyn4j.dynamics.contact.TimeOfImpactSolver)
	 */
	@Override
	public void setTimeOfImpactSolver(TimeOfImpactSolver<T> timeOfImpactSolver) {
		if (timeOfImpactSolver == null) 
			throw new ArgumentNullException("timeOfImpactSolver");
		
		this.timeOfImpactSolver = timeOfImpactSolver;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getTimeOfImpactSolver()
	 */
	@Override
	public TimeOfImpactSolver<T> getTimeOfImpactSolver() {
		return this.timeOfImpactSolver;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getJointCount()
	 */
	@Override
	public int getJointCount() {
		return this.joints.size();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getJoint(int)
	 */
	@Override
	public Joint<T> getJoint(int index) {
		return this.joints.get(index);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getJoints()
	 */
	@Override
	public List<Joint<T>> getJoints() {
		return this.jointsUnmodifiable;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getJointIterator()
	 */
	@Override
	public Iterator<Joint<T>> getJointIterator() {
		return new JointIterator();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getStep()
	 */
	@Override
	public TimeStep getTimeStep() {
		return this.timeStep;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getAccumulatedTime()
	 */
	@Override
	public double getAccumulatedTime() {
		return this.time;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#setAccumulatedTime(double)
	 */
	@Override
	public void setAccumulatedTime(double elapsedTime) {
		if (elapsedTime < 0.0) return;
		this.time = elapsedTime;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#setUpdateRequired(boolean)
	 */
	@Override
	public void setUpdateRequired(boolean flag) {
		this.updateRequired = flag;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#isUpdateRequired()
	 */
	@Override
	public boolean isUpdateRequired() {
		return this.updateRequired;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.AbstractCollisionWorld#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		int nb = this.bodies.size();
		int nj = this.joints.size();
		return nb <= 0 && nj <= 0;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.AbstractCollisionWorld#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		super.shift(shift);
		
		// update the joints
		int jSize = this.joints.size();
		for (int i = 0; i < jSize; i++) {
			Joint<T> joint = this.joints.get(i);
			joint.shift(shift);
		}
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#isInContact(org.dyn4j.dynamics.PhysicsBody, org.dyn4j.dynamics.PhysicsBody)
	 */
	@Override
	public boolean isInContact(T body1, T body2) {
		return this.constraintGraph.isInContact(body1, body2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getContacts(org.dyn4j.dynamics.PhysicsBody)
	 */
	@Override
	public List<ContactConstraint<T>> getContacts(T body) {
		return this.constraintGraph.getContacts(body);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getInContactBodies(org.dyn4j.dynamics.PhysicsBody, boolean)
	 */
	@Override
	public List<T> getInContactBodies(T body, boolean includeSensedContact) {
		return this.constraintGraph.getInContactBodies(body, includeSensedContact);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#isJointCollisionAllowed(org.dyn4j.dynamics.PhysicsBody, org.dyn4j.dynamics.PhysicsBody)
	 */
	@Override
	public boolean isJointCollisionAllowed(T body1, T body2) {
		return this.constraintGraph.isJointCollisionAllowed(body1, body2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#isJoined(org.dyn4j.dynamics.PhysicsBody, org.dyn4j.dynamics.PhysicsBody, boolean)
	 */
	@Override
	public boolean isJoined(T body1, T body2) {
		return this.constraintGraph.isJoined(body1, body2);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getJoints(org.dyn4j.dynamics.PhysicsBody)
	 */
	@Override
	public List<Joint<T>> getJoints(T body) {
		return this.constraintGraph.getJoints(body);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getJoinedBodies(org.dyn4j.dynamics.PhysicsBody)
	 */
	@Override
	public List<T> getJoinedBodies(T body) {
		return this.constraintGraph.getJoinedBodies(body);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getContactListeners()
	 */
	@Override
	public List<ContactListener<T>> getContactListeners() {
		return this.contactListenersUnmodifiable;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getDestructionListeners()
	 */
	@Override
	public List<DestructionListener<T>> getDestructionListeners() {
		return this.destructionListenersUnmodifiable;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getStepListeners()
	 */
	@Override
	public List<StepListener<T>> getStepListeners() {
		return this.stepListenersUnmodifiable;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#getTimeOfImpactListeners()
	 */
	@Override
	public List<TimeOfImpactListener<T>> getTimeOfImpactListeners() {
		return this.timeOfImpactListenersUnmodifiable;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.AbstractCollisionWorld#removeAllListeners()
	 */
	@Override
	public void removeAllListeners() {
		super.removeAllListeners();
		
		this.stepListeners.clear();
		this.contactListeners.clear();
		this.destructionListeners.clear();
		this.timeOfImpactListeners.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeAllContactListeners()
	 */
	@Override
	public void removeAllContactListeners() {
		this.contactListeners.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeAllDestructionListeners()
	 */
	@Override
	public void removeAllDestructionListeners() {
		this.destructionListeners.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeAllStepListeners()
	 */
	@Override
	public void removeAllStepListeners() {
		this.stepListeners.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeAllTimeOfImpactListeners()
	 */
	@Override
	public void removeAllTimeOfImpactListeners() {
		this.timeOfImpactListeners.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeContactListener(org.dyn4j.world.listener.ContactListener)
	 */
	@Override
	public boolean removeContactListener(ContactListener<T> listener) {
		return this.contactListeners.remove(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeDestructionListener(org.dyn4j.world.listener.DestructionListener)
	 */
	@Override
	public boolean removeDestructionListener(DestructionListener<T> listener) {
		return this.destructionListeners.remove(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeStepListener(org.dyn4j.world.listener.StepListener)
	 */
	@Override
	public boolean removeStepListener(StepListener<T> listener) {
		return this.stepListeners.remove(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#removeTimeOfImpactListener(org.dyn4j.world.listener.TimeOfImpactListener)
	 */
	@Override
	public boolean removeTimeOfImpactListener(TimeOfImpactListener<T> listener) {
		return this.timeOfImpactListeners.remove(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#addContactListener(org.dyn4j.world.listener.ContactListener)
	 */
	@Override
	public boolean addContactListener(ContactListener<T> listener) {
		return this.contactListeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#addDestructionListener(org.dyn4j.world.listener.DestructionListener)
	 */
	@Override
	public boolean addDestructionListener(DestructionListener<T> listener) {
		return this.destructionListeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#addStepListener(org.dyn4j.world.listener.StepListener)
	 */
	@Override
	public boolean addStepListener(StepListener<T> listener) {
		return this.stepListeners.add(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.PhysicsWorld#addTimeOfImpactListener(org.dyn4j.world.listener.TimeOfImpactListener)
	 */
	@Override
	public boolean addTimeOfImpactListener(TimeOfImpactListener<T> listener) {
		return this.timeOfImpactListeners.add(listener);
	}
	
	/**
	 * Performs a full step of the engine.
	 */
	protected void step() {
		// get all the step listeners
		List<StepListener<T>> stepListeners = this.stepListeners;
		List<ContactListener<T>> contactListeners = this.contactListeners;
		
		int sSize = stepListeners.size();
		
		// notify the step listeners
		for (int i = 0; i < sSize; i++) {
			StepListener<T> sl = stepListeners.get(i);
			sl.begin(this.timeStep, this);
		}
		
		// check if we need to update the contacts first
		if (this.updateRequired) {
			// if so then update the contacts
			this.detect();
			// notify that an update was performed
			for (int i = 0; i < sSize; i++) {
				StepListener<T> sl = stepListeners.get(i);
				sl.updatePerformed(this.timeStep, this);
			}
			// set the update required flag to false
			this.updateRequired = false;
		}
		
		// notify of all the contacts that will be solved and all the sensed contacts
		if (contactListeners.size() > 0) {
			for (ContactCollisionData<T> data : this.contactCollisions) {
				ContactConstraint<T> cc = data.getContactConstraint();
				for (Contact contact : cc.getContacts()) {
					for (ContactListener<T> listener : contactListeners) {
						listener.preSolve(data, contact);
					}
				}
			}
		}
		
		// check for CCD
		ContinuousDetectionMode continuousDetectionMode = this.settings.getContinuousDetectionMode();
		
		// get the number of bodies
		int size = this.bodies.size();
		
		// save the current transform
		for (int i = 0; i < size; i++) {
			T body = this.bodies.get(i);
			// save the current transform into the previous transform
			body.getPreviousTransform().set(body.getTransform());
		}
		
		// solve the world by using the interaction graph to produce a set of islands
		this.constraintGraph.solve(this.contactConstraintSolver, this.gravity, this.timeStep, this.settings);
		
		// notify of the all solved contacts
		if (contactListeners.size() > 0) {
			for (ContactCollisionData<T> data : this.contactCollisions) {
				ContactConstraint<T> cc = data.getContactConstraint();
				for (SolvedContact contact : cc.getContacts()) {
					for (ContactListener<T> listener : contactListeners) {
						listener.postSolve(data, contact);
					}
				}
			}
		}
		
		// notify the step listener
		for (int i = 0; i < sSize; i++) {
			StepListener<T> sl = stepListeners.get(i);
			sl.postSolve(this.timeStep, this);
		}

		// make sure CCD is enabled
		if (continuousDetectionMode != ContinuousDetectionMode.NONE) {
			// update the CCD broadphase
			this.ccdBroadphase.update();
			
			// solve any time of impact events that were missed
			this.solveTOI(continuousDetectionMode);

			// clear the ccd broadphase updates so that on the next iteration
			this.ccdBroadphase.clearUpdates();
		}

		// after all has been updated find new contacts
		// this is done so that the user has the latest contacts
		// and the broadphase has the latest AABBs, etc.
		this.detect();
		
		// set the update required flag to false
		this.updateRequired = false;
		
		// notify the step listener
		for (int i = 0; i < sSize; i++) {
			StepListener<T> sl = stepListeners.get(i);
			sl.end(this.timeStep, this);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.AbstractCollisionWorld#detectCollisions(java.util.Iterator)
	 */
	@Override
	protected void processCollisions(Iterator<V> iterator) {
		// clear the contact interactions since we'll be recreating them
		this.constraintGraph.removeAllContactConstraints();
		
		// this is rebuilt every time so clear it
		this.contactCollisions.clear();
		
		// a resuable handler for begin/persist/end contact event handling
		WarmStartHandler wsh = new WarmStartHandler();
		
		// if the iterator has returned something then it's made it past
		// the manifold stage and is ready to become a contact constraint
		while (iterator.hasNext()) {
			V collision = iterator.next();

			// get the current contact constraint data
			ContactConstraint<T> contactConstraint = collision.getContactConstraint();
			
			// we can exit early if the collision didn't make it to the manifold stage
			// and there's no existing contacts to report ending
			if (!collision.isManifoldCollision() && contactConstraint.getContacts().size() == 0) {
				continue;
			}
			
			// setup the handler
			wsh.data = collision;
			
			// update the contact constraint
			// NOTE: in the case of an empty manifold, this method will also report
			//       end notifications for any existing contacts
			contactConstraint.update(collision.getManifold(), this.settings, wsh);
			
			// we only want to add interaction edges if the collision actually made it past
			// the manifold generation stage and all listeners allowed it to proceed
			if (collision.isManifoldCollision()) {
				// set the flag for contact constraint
				collision.setContactConstraintCollision(true);
				
				// build the contact edges
				this.constraintGraph.addContactConstraint(contactConstraint);
				
				// let any contact listeners churn on it
				for (ContactListener<T> listener : this.contactListeners) {
					listener.collision(collision);
				}
				
				// add it to a list of contact-constraint only collisions for
				// quicker post/pre solve notification if it's enabled and
				// not a sensor collision
				if (contactConstraint.isEnabled() && !contactConstraint.isSensor()) {
					this.contactCollisions.add(collision);
				}
			}
		}
	}
	
	/**
	 * Solves any Time-of-Impact events (collision events that were missed by the
	 * discrete collision detection algorithms).
	 * <p>
	 * Returns true if any TOI event was resolved. When true, the bodies involved
	 * had their transforms modified an another discrete collision detection is required.
	 * @param mode the mode
	 * @return boolean 
	 */
	protected boolean solveTOI(ContinuousDetectionMode mode) {
		// handled scenarios:
		// 1. Dynamic vs. Static
		// 2. Dynamic vs. Bullet
		// 3. Bullet vs. Static
		
		List<TimeOfImpactListener<T>> listeners = this.timeOfImpactListeners;

		// check the CCD mode
		boolean bulletsOnly = (mode == ContinuousDetectionMode.BULLETS_ONLY);
		
		Iterator<CollisionPair<T>> pairIterator = this.ccdBroadphase.detectIterator();
		while(pairIterator.hasNext()) {
			// NOTE: since the broadphase reuses the pair object, make sure to make a copy of it
			CollisionPair<T> pair = pairIterator.next().copy();
			this.ccdCollisionData.add(pair);
		}
		
		Map<T, List<T>> pairMapping = new HashMap<T, List<T>>();
		Iterator<CollisionPair<T>> iterator = this.ccdCollisionData.iterator();
		while (iterator.hasNext()) {
			CollisionPair<T> pair = iterator.next();
			T body1 = pair.getFirst();
			T body2 = pair.getSecond();
			
			// since the broadphase is a new-overlap-only detection
			// we need to check every item in the stored set of collisions:
			//		1. check if they were updated
			// 		2. if so, then check if their AABBs still overlap

			// we need to remove the pair if either body/fixture doesn't exist anymore too
			if (!this.ccdBroadphase.contains(body1) ||
				!this.ccdBroadphase.contains(body2)) {
				iterator.remove();
				continue;
			}
			
			if (this.ccdBroadphase.isUpdated(body1) || this.ccdBroadphase.isUpdated(body2)) {
				// then we need to verify the pair is still valid
				boolean overlaps = this.ccdBroadphase.detect(body1, body2);
				if (!overlaps) {
					// remove the collision from the set of collisions
					iterator.remove();
					continue;
				}
			}
			
			// if we are only doing CCD on bullets only, then check
			// to make sure that the current body is a bullet
			if (bulletsOnly && !body1.isBullet() && !body2.isBullet()) continue;
			
			// otherwise we process all dynamic bodies

			// we don't want to mess with disabled bodies
			if (!body1.isEnabled() || !body2.isEnabled()) continue;
			
			// we don't allow dynamic vs. dynamic unless one (or both) is a bullet
			if (body1.isDynamic() && body2.isDynamic()) {
				// one of them has to be a bullet
				if (!body1.isBullet() && !body2.isBullet()) {
					continue;
				}
			}
			
			// if both are infinite, then there's no way to resolve them anyway
			if (body1.getMass().isInfinite() && body2.getMass().isInfinite()) continue;
			
			// we can also check for sleeping bodies and skip those since
			// they will only be asleep after being stationary for a set
			// time period
			if (body1.isAtRest() && body2.isAtRest()) continue;

			// check for joints who's collision is not allowed
			if (!this.isJointCollisionAllowed(body1, body2)) continue;
			
			// check for bodies already in collision
			if (this.isInContact(body1, body2)) continue;
			
			// check listeners
			boolean allow = true;
			for (TimeOfImpactListener<T> tl : listeners) {
				if (!tl.collision(body1, body2)) {
					// if any toi listener doesnt allow it, then don't allow it
					// we need to allow all listeners to be notified before we continue
					allow = false;
				}
			}
			if (!allow) continue;
			
			// group the remaining events together based on the first
			// dynamic body found
			
			// NOTE: this has the effect of solving only one TOI event per body
			// NOTE: I opted to not do sub-stepping due to performance
			if (body1.isDynamic()) {
				List<T> list = pairMapping.get(body1);
				if (list != null) {
					list.add(body2);
				} else {
					list = new ArrayList<T>();
					list.add(body2);
					pairMapping.put(body1, list);
				}
			} else if (body2.isDynamic()) {
				List<T> list = pairMapping.get(body2);
				if (list != null) {
					list.add(body1);
				} else {
					list = new ArrayList<T>();
					list.add(body1);
					pairMapping.put(body2, list);
				}
			}
		}
		
		// solve the individual groups
		boolean solved = false;
		for (T body1 : pairMapping.keySet()) {
			List<T> others = pairMapping.get(body1);
			
			// solve for time of impact
			boolean ss = this.solveTOI(body1, others, listeners);
			
			// track if anything was solved
			solved |= ss;
		}
		
		return solved;
	}
	
	/**
	 * Solves the time of impact for the given {@link PhysicsBody}.
	 * <p>
	 * This method will find the first {@link PhysicsBody} that the given {@link PhysicsBody}
	 * collides with unless ignored via the {@link TimeOfImpactListener}.
	 * <p>
	 * If any {@link TimeOfImpactListener} doesn't allow the collision then the collision
	 * is ignored.
	 * <p>
	 * After the first {@link PhysicsBody} is found the two {@link PhysicsBody}s are interpolated
	 * to the time of impact.
	 * <p>
	 * Then the {@link PhysicsBody}s are position solved using the {@link TimeOfImpactSolver}
	 * to force the {@link PhysicsBody}s into collision.  This causes the discrete collision
	 * detector to detect the collision on the next time step.
	 * @param body1 the {@link PhysicsBody}
	 * @param others the other bodies to test against
	 * @param listeners the list of {@link TimeOfImpactListener}s
	 * @return boolean true if a time of impact event was resolved
	 * @since 3.1.0
	 */
	protected boolean solveTOI(T body1, List<T> others, List<TimeOfImpactListener<T>> listeners) {
		int size = others.size();
		
		// setup the initial time bounds [0, 1]
		double t1 = 0.0;
		double t2 = 1.0;
		
		// save the minimum time of impact and body
		TimeOfImpact minToi = null;
		T minBody = null;
		
		// used to save a few allocations
		CollisionItemAdapter<T, BodyFixture> reusableItem = new CollisionItemAdapter<T, BodyFixture>();
		
		// loop over all the other bodies to find the minimum TOI
		for (int i = 0; i < size; i++) {
			// get the other body
			T body2 = others.get(i);

			TimeOfImpact toi = new TimeOfImpact();
			int fc1 = body1.getFixtureCount();
			int fc2 = body2.getFixtureCount();

			// small acceleration for multi-fixture static bodies
			boolean b2IsStaticAndMultiFixture = body2.isStatic() && fc2 > 1;
			
			// get the velocities for the time step since we want
			// [t1, t2] to be bound to this time step
			double dt = this.timeStep.getDeltaTime();
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
			
			Transform tx1 = body1.getPreviousTransform();
			Transform tx2 = body2.getPreviousTransform();
			
			// test against all fixture pairs taking the fixture
			// with the smallest time of impact
			for (int k = 0; k < fc2; k++) {
				BodyFixture fixture2 = body2.getFixture(k);
				
				// if the second body is static, we can accelerate a bit
				// more by checking the swept AABB of the first body
				// against the non-swept AABB of the second body's fixture
				// this is primarily for multi-fixture static bodies
				// NOTE: a static body is not moving and has infinite mass
				// NOTE: the first body will always be a dynamic body
				if (b2IsStaticAndMultiFixture) {
					// use body1 SWEPT AABB
					AABB b1SweptAABB = this.ccdBroadphase.getAABB(body1);
					
					// use body2-fixture2 STATIC AABB
					reusableItem.set(body2, fixture2);
					AABB b2StaticAABB = this.broadphaseDetector.getAABB(reusableItem);
					
					// if they don't overlap, then they can't possibly have a TOI
					if (!b1SweptAABB.overlaps(b2StaticAABB)) {
						continue;
					}
				}
				
				// skip sensor fixtures
				if (fixture2.isSensor()) continue;
				
				for (int j = 0; j < fc1; j++) {
					BodyFixture f1 = body1.getFixture(j);

					// skip sensor fixtures
					if (fixture2.isSensor()) continue;

					Filter filter1 = f1.getFilter();
					Filter filter2 = fixture2.getFilter();
					
					// make sure the fixture filters allow the collision
					if (!filter1.isAllowed(filter2)) {
						continue;
					}
					
					// check listeners
					boolean allow = true;
					for (TimeOfImpactListener<T> tl : listeners) {
						if (!tl.collision(body1, f1, body2, fixture2)) {
							// if any toi listener doesnt allow it, then don't allow it
							// we need to allow all listeners to be notified before we continue
							allow = false;
						}
					}
					if (!allow) continue;
					
					Convex c1 = f1.getShape();
					Convex c2 = fixture2.getShape();
					
					// get the time of impact for the fixture pair
					if (this.timeOfImpactDetector.getTimeOfImpact(c1, tx1, v1, av1, c2, tx2, v2, av2, t1, t2, toi)) {
						// get the time of impact
						double t = toi.getTime();
						
						// if we detect a time of impact at the beginning
						// we can't handle this, we need to give up now
						if (t == 0.0) {
							return false;
						}
						
						// check if the time of impact is less than
						// the current time of impact
						if (t < t2) {
							// if it is then ask the listeners if we should use this collision
							allow = true;
							for (TimeOfImpactListener<T> tl : listeners) {
								if (!tl.collision(body1, f1, body2, fixture2, toi)) {
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
			// test the time of impact listeners
			boolean allow = true;
			for (TimeOfImpactListener<T> tl : listeners) {
				if (!tl.collision(body1, minBody, minToi)) {
					// if any toi listener doesnt allow it, then don't allow it
					// we need to allow all listeners to be notified before we continue
					allow = false;
				}
			}
			if (!allow) {
				return false;
			}
			
			// get the time of impact info
			double t = minToi.getTime();
			
			// move the dynamic body to the time of impact
			body1.getPreviousTransform().lerp(body1.getTransform(), t, body1.getTransform());
			// check if the other body is dynamic
			if (minBody.isDynamic()) {
				// if the other body is dynamic then interpolate its transform also
				minBody.getPreviousTransform().lerp(minBody.getTransform(), t, minBody.getTransform());
			}
			// this should bring the bodies within d distance from one another
			// we need to move the bodies more so that they are in collision
			// so that on the next time step they are solved by the discrete
			// collision detector
			
			// sometimes we get the exact location and the distance is zero
			// the solver won't do anything with zero, so let's go ahead and exit
			if (minToi.getSeparation().getDistance() <= 0.0) {
				return true;
			}
			
			// performs position correction on the body/bodies so that they are
			// in collision and will be detected in the next time step
			this.timeOfImpactSolver.solve(body1, minBody, minToi, this.settings);
			
			// this method does not conserve time
			return true;
		}
		
		return false;
	}
	
	/**
	 * A {@link ContactUpdateHandler} that uses the local mixers and listeners.
	 * @author William Bittle
	 * @version 4.2.0
	 * @since 4.0.0
	 */
	private final class WarmStartHandler implements ContactUpdateHandler {
		private ContactCollisionData<T> data;
		private final List<ContactListener<T>> listeners;
		
		public WarmStartHandler() {
			this.listeners = AbstractPhysicsWorld.this.contactListeners;
		}

		/* (non-Javadoc)
		 * @see org.dyn4j.dynamics.contact.ContactUpdateHandler#getFriction(org.dyn4j.dynamics.BodyFixture, org.dyn4j.dynamics.BodyFixture)
		 */
		@Override
		public double getFriction(BodyFixture fixture1, BodyFixture fixture2) {
			return AbstractPhysicsWorld.this.valueMixer.mixFriction(fixture1.getFriction(), fixture2.getFriction());
		}

		/* (non-Javadoc)
		 * @see org.dyn4j.dynamics.contact.ContactUpdateHandler#getRestitution(org.dyn4j.dynamics.BodyFixture, org.dyn4j.dynamics.BodyFixture)
		 */
		@Override
		public double getRestitution(BodyFixture fixture1, BodyFixture fixture2) {
			return AbstractPhysicsWorld.this.valueMixer.mixRestitution(fixture1.getRestitution(), fixture2.getRestitution());
		}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.dynamics.contact.ContactUpdateHandler#getRestitutionVelocity(org.dyn4j.dynamics.BodyFixture, org.dyn4j.dynamics.BodyFixture)
		 */
		@Override
		public double getRestitutionVelocity(BodyFixture fixture1, BodyFixture fixture2) {
			return AbstractPhysicsWorld.this.valueMixer.mixRestitutionVelocity(fixture1.getRestitutionVelocity(), fixture2.getRestitutionVelocity());
		}

		/* (non-Javadoc)
		 * @see org.dyn4j.dynamics.contact.ContactUpdateHandler#begin(org.dyn4j.dynamics.contact.Contact)
		 */
		@Override
		public void begin(Contact contact) {
			int size = this.listeners.size();
			for (int i = 0; i < size; i++) {
				ContactListener<T> listener = this.listeners.get(i);
				listener.begin(this.data, contact);
			}
		}

		/* (non-Javadoc)
		 * @see org.dyn4j.dynamics.contact.ContactUpdateHandler#persist(org.dyn4j.dynamics.contact.Contact, org.dyn4j.dynamics.contact.Contact)
		 */
		@Override
		public void persist(Contact oldContact, Contact newContact) {
			int size = this.listeners.size();
			for (int i = 0; i < size; i++) {
				ContactListener<T> listener = this.listeners.get(i);
				listener.persist(this.data, oldContact, newContact);
			}
		}

		/* (non-Javadoc)
		 * @see org.dyn4j.dynamics.contact.ContactUpdateHandler#end(org.dyn4j.dynamics.contact.Contact)
		 */
		@Override
		public void end(Contact contact) {
			int size = this.listeners.size();
			for (int i = 0; i < size; i++) {
				ContactListener<T> listener = this.listeners.get(i);
				listener.end(this.data, contact);
			}
		}
	}

	/**
	 * Represents an iterator for {@link Joint}s in a world.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	private final class JointIterator implements Iterator<Joint<T>> {
		/** The current index */
		private int index;
		
		/** True if the current element has been removed */
		private boolean removed;
		
		/**
		 * Minimal constructor.
		 */
		public JointIterator() {
			this.index = -1;
			this.removed = false;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.index + 1 < AbstractPhysicsWorld.this.joints.size();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Joint<T> next() {
			if (this.index + 1 >= AbstractPhysicsWorld.this.joints.size()) {
				throw new IndexOutOfBoundsException();
			}
			try {
				this.index++;
				this.removed = false;
				Joint<T> joint = AbstractPhysicsWorld.this.joints.get(this.index);
				return joint;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			if (this.index < 0 || this.removed) {
				throw new IllegalStateException();
			}
			if (this.index >= AbstractPhysicsWorld.this.joints.size()) {
				throw new IndexOutOfBoundsException();
			}
			try {
				AbstractPhysicsWorld.this.removeJoint(this.index);
				this.index--;
				this.removed = true;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}
}
