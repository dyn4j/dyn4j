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
import org.dyn4j.game2d.dynamics.joint.Joint;
import org.dyn4j.game2d.dynamics.joint.JointEdge;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Manages the logic of collision detection, resolution, and reporting.
 * @author William Bittle
 */
public class World {
	/** Earths gravity constant */
	public static final Vector EARTH_GRAVITY = new Vector(0.0, -9.8);
	
	/** Zero gravity constant */
	public static final Vector ZERO_GRAVITY = new Vector(0.0, 0.0);

	/** The {@link Step} used by the dynamics calculations */
	protected Step step;
	
	/** The world gravity vector */
	protected Vector gravity;
	
	/** The world {@link Bounds} */
	protected Bounds bounds;
	
	/** The {@link BroadphaseDetector} */
	protected BroadphaseDetector broadphaseDetector;
	
	/** The {@link NarrowphaseDetector} */
	protected NarrowphaseDetector narrowphaseDetector;
	
	/** The {@link ManifoldSolver} */
	protected ManifoldSolver manifoldSolver;

	/** The {@link ContactManager} */
	protected ContactManager contactManager;

	/** The {@link BoundsListener} */
	protected BoundsListener boundsListener;
	
	/** The {@link DestructionListener} */
	protected DestructionListener destructionListener;
	
	/** The {@link StepListener} */
	protected StepListener stepListener;
	
	/** The {@link Body} list */
	protected List<Body> bodies;
	
	/** The {@link Joint} list */
	protected List<Joint> joints;
	
	/** The reusable island */
	protected Island island;
	
	/** The accumulated time */
	protected double time;
	
	/**
	 * Full constructor.
	 * <p>
	 * Defaults to using {@link #EARTH_GRAVITY}, {@link Sap} broad-phase,
	 * {@link Gjk} narrow-phase, and {@link ClippingManifoldSolver}.
	 * @param bounds the bounds of the {@link World}
	 */
	public World(Bounds bounds) {
		super();
		this.step = new Step();
		this.gravity = World.EARTH_GRAVITY;
		this.bounds = bounds;
		this.broadphaseDetector = new Sap();
		this.narrowphaseDetector = new Gjk();
		this.manifoldSolver = new ClippingManifoldSolver();
		// create empty listeners
		this.contactManager = new ContactManager(new ContactAdapter());
		this.boundsListener = new BoundsAdapter();
		this.destructionListener = new DestructionAdapter();
		this.stepListener = new StepAdapter();
		this.bodies = new ArrayList<Body>();
		this.joints = new ArrayList<Joint>();
		this.island = new Island();
		this.time = 0.0;
	}
	
	/**
	 * Updates the {@link World}.
	 * @param elapsedTime in seconds
	 * @return boolean true if the {@link World} performed a simulation step
	 */
	public boolean update(double elapsedTime) {
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
	 * Performs n simulation steps using the step frequency in {@link Settings}.
	 * @param n the number of simulation steps to perform
	 */
	public void step(int n) {
		// get the frequency from settings
		double invhz = Settings.getInstance().getStepFrequency();
		// perform the steps
		for (int i = 0; i < n; i++) {
			// update the step object
			this.step.update(invhz);
			// step the world
			this.step();
		}
	}
	
	/**
	 * Steps the {@link World} using the current {@link Step}.
	 */
	protected void step() {
		// notify the step listener
		this.stepListener.step(this);
		
		// clear the old contact list (does NOT clear the contact map
		// which is used to warm start)
		this.contactManager.clear();
		
		// get the number of bodies
		int size = this.bodies.size();
		
		// test for out of bounds objects
		// clear the body contacts
		// clear the island flag
		for (int i = 0; i < size; i++) {
			Body b = this.bodies.get(i);
			// skip if already frozen
			if (b.isFrozen()) continue;
			// check if the body is out of bounds
			if (this.bounds.isOutside(b)) {
				// set the body to frozen
				b.freeze();
				// if so, notify via the listener
				this.boundsListener.outside(b);
			}
			// clear all the old contacts
			b.contacts.clear();
			// remove the island flags
			b.setIsland(false);
		}
		
		// clear the joint island flags
		int jSize = this.joints.size();
		for (int i = 0; i < jSize; i++) {
			// get the joint
			Joint joint = this.joints.get(i);
			// set the island flag to false
			joint.setIsland(false);
		}
		
		// test for collisions via the broad-phase
		List<BroadphasePair<Body>> pairs = this.broadphaseDetector.detect(this.bodies);
		int pSize = pairs.size();		
		
		// using the broad-phase results, test for narrow-phase
		for (int i = 0; i < pSize; i++) {
			BroadphasePair<Body> pair = pairs.get(i);
			
			// get the bodies
			Body b1 = pair.getObject1();
			Body b2 = pair.getObject2();
			
			// frozen pairs don't have collision detection/response
			if (b1.isFrozen() || b2.isFrozen()) continue;
			// static pairs don't have collision detection/response
			if (b1.isStatic() && b2.isStatic()) continue;
			// check for connected pairs who are not allowed to collide
			if (b1.isConnectedNoCollision(b2)) continue;
			
			// determine using the collision filter whether to allow this one
			if (!b1.getFilter().isAllowed(b2.getFilter())) {
				continue;
			}

			// get their transforms
			Transform t1 = b1.transform;
			Transform t2 = b2.transform;
			// get their geometry
			List<Convex> g1 = b1.shapes;
			List<Convex> g2 = b2.shapes;
			
			// create a reusable penetration object
			Penetration p = new Penetration();
			// create a reusable manifold object
			Manifold m = new Manifold();
			
			// loop through the geometries of body 1
			int b1Size = g1.size();
			int b2Size = g2.size();
			for (int j = 0; j < b1Size; j++) {
				Convex c1 = g1.get(j);
				// test against each geometry of body 2
				for (int k = 0; k < b2Size; k++) {
					Convex c2 = g2.get(k);
					// test the two convex shapes
					if (this.narrowphaseDetector.detect(c1, t1, c2, t2, p)) {
						// if there is penetration then find a contact manifold
						// using the filled in penetration object
						if (this.manifoldSolver.getManifold(p, c1, t1, c2, t2, m)) {
							// get the manifold points
							List<ManifoldPoint> points = m.getPoints();
							// a valid manifold was found
							int mSize = points.size();
							// don't add sensor manifolds to the contact constraints list
							if (!b1.isSensor() && !b2.isSensor()) {
								// create a contact constraint
								ContactConstraint contactConstraint = new ContactConstraint(b1, c1, b2, c2, m);
								// add a contact edge to both bodies
								ContactEdge ce1 = new ContactEdge(b2, contactConstraint);
								ContactEdge ce2 = new ContactEdge(b1, contactConstraint);
								b1.contacts.add(ce1);
								b2.contacts.add(ce2);
								// add the contact constraint to the contact manager
								this.contactManager.add(contactConstraint);
							} else {
								// notify the contact manager's contact listener of the
								// sensed contact points
								ContactListener cl = contactManager.getContactListener();
								for (int l = 0; l < mSize; l++) {
									// get the manifold point
									ManifoldPoint mp = points.get(l);
									// notify of the contact point
									cl.sensed(new ContactPoint(mp.getPoint(), m.getNormal(), mp.getDepth(), b1, c1, b2, c2));
								}
							}
						}
					}
				}
			}
		}
		
		// warm start the contact constraints
		this.contactManager.warm();
		
		// perform a depth first search of the contact graph
		// to create islands for constraint solving
		Stack<Body> stack = new Stack<Body>();
		stack.ensureCapacity(size);
		// loop over the bodies and their contact edges to create the islands
		for (int i = 0; i < size; i++) {
			Body seed = this.bodies.get(i);
			// skip if asleep, frozen, static, or already on an island
			if (seed.isAsleep() || seed.isFrozen() || seed.isStatic() || seed.onIsland()) continue;
			
			island.clear();
			stack.clear();
			stack.push(seed);
			while (stack.size() > 0) {
				// get the next body
				Body b = stack.pop();
				// add it to the island
				island.add(b);
				// flag that it has been added
				b.setIsland(true);
				// make sure the body is awake but dont reset the sleep time
				b.state &= ~Body.ASLEEP;
				// if its static then continue since we dont want the
				// island to span more than one static object
				// this keeps the size of the islands small
				if (b.isStatic()) continue;
				// loop over the contact edges of this body
				int ceSize = b.contacts.size();
				for (int j = 0; j < ceSize; j++) {
					ContactEdge e = b.contacts.get(j);
					// get the contact constraint
					ContactConstraint cc = e.getContactConstraint();
					// get the other body
					Body other = e.getOther();
					// check if the contact constraint has already been added to an island
					if (cc.onIsland()) continue;
					// add the contact constraint to the island list
					island.add(cc);
					// set the island flag on the contact constraint
					cc.setIsland(true);
					// has the other body been added to an island yet?
					if (!other.onIsland()) {
						// if not then add this body to the stack
						stack.push(other);
						other.setIsland(true);
					}
				}
				// loop over the joint edges of this body
				int jeSize = b.joints.size();
				for (int j = 0; j < jeSize; j++) {
					// get the joint edge
					JointEdge e = b.joints.get(j);
					// get the joint
					Joint joint = e.getJoint();
					// get the other body
					Body other = e.getOther();
					// check if the joint has already been added to an island
					if (joint.onIsland()) continue;
					// add the joint to the island
					island.add(joint);
					// set the island flag on the joint
					joint.setIsland(true);
					// check if the other body has been added to an island
					if (!other.onIsland()) {
						// if not then add the body to the stack
						stack.push(other);
						other.setIsland(true);
					}
				}
			}
			
			// solve the island
			island.solve(this.gravity, this.step);
			
			// allow static bodies to participate in other islands
			for (int j = 0; j < size; j++) {
				Body b = this.bodies.get(j);
				if (b.isStatic()) {
					b.setIsland(false);
				}
			}
		}
		
		// notify of the solved contacts
		this.contactManager.solved();
	}
	
	/**
	 * Adds a {@link Body} to the {@link World}.
	 * @param body the {@link Body} to add
	 */
	public void add(Body body) {
		// make sure its setup
		if (body.mass == null || body.shapes.size() == 0) {
			throw new IllegalArgumentException("A body must have at least one shape and its mass set before being added to the world.");
		}
		this.bodies.add(body);
	}
	
	/**
	 * Adds a {@link Joint} to the {@link World}.
	 * @param joint the {@link Joint} to add
	 */
	public void add(Joint joint) {
		// add the joint to the joint list
		this.joints.add(joint);
		// get the associated bodies
		Body b1 = joint.getBody1();
		Body b2 = joint.getBody2();
		// create a joint edge from the first body to the second
		JointEdge je1 = new JointEdge(b2, joint);
		// add the edge to the body
		b1.joints.add(je1);
		// create a joint edge from the second body to the first
		JointEdge je2 = new JointEdge(b1, joint);
		// add the edge to the body
		b2.joints.add(je2);
	}
	
	/**
	 * Removes the given {@link Body} from the {@link World}.
	 * @param body the {@link Body} to remove
	 */
	public void remove(Body body) {
		// remove the body from the list
		this.bodies.remove(body);
		
		// wake up any bodies connected to this body by a joint
		for (JointEdge je : body.joints) {
			// get the joint
			Joint joint = je.getJoint();
			// get the other body
			Body other = je.getOther();
			// wake up the other body
			other.awaken();
			// remove the contact edge from the other body
			Iterator<JointEdge> iterator = other.joints.iterator();
			while (iterator.hasNext()) {
				// get the joint edge
				JointEdge jeOther = iterator.next();
				// get the joint
				Joint jOther = jeOther.getJoint();
				if (jOther == joint) {
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
		for (ContactEdge ce : body.contacts) {
			// get the contact constraint
			ContactConstraint cc = ce.getContactConstraint();
			// get the other body
			Body other = ce.getOther();
			// wake up the other body
			other.awaken();
			// remove the contact edge connected from the other body
			// to this body
			Iterator<ContactEdge> iterator = other.contacts.iterator();
			while (iterator.hasNext()) {
				ContactEdge ceOther = iterator.next();
				// get the contact constraint
				ContactConstraint ccOther = ceOther.getContactConstraint();
				// check if the contact constraint is the same
				if (ccOther == cc) {
					// remove the contact edge
					iterator.remove();
					// since they are the same contact constraint object
					// we only need to notify of this bodies destroyed contacts
				}
			}
			// remove the contact constraint from the contact manager
			this.contactManager.remove(cc);
			// loop over the contact points
			int size = cc.getContacts().length;
			for (int i = 0; i < size; i++) {
				// get the contact
				Contact c = cc.getContacts()[i];
				// create a contact point for notification
				ContactPoint cp = new ContactPoint(c.getPoint(), cc.getNormal(), c.getDepth(), cc.getBody1(), cc.getConvex1(), cc.getBody2(), cc.getConvex2());
				// call the destruction listener
				this.destructionListener.destroyed(cp);
			}
		}
	}
	
	/**
	 * Removes the given {@link Joint} from the {@link World}.
	 * @param joint the {@link Joint} to remove
	 */
	public void remove(Joint joint) {
		// remove the joint from the joint list
		this.joints.remove(joint);
		
		// removed the joint edges from body1
		Iterator<JointEdge> iterator = joint.getBody1().joints.iterator();
		while (iterator.hasNext()) {
			// see if this is the edge we want to remove
			JointEdge je = iterator.next();
			if (je.getJoint() == joint) {
				// then remove this joint edge
				iterator.remove();
			}
		}
		// remove the joint edges from body2
		iterator = joint.getBody2().joints.iterator();
		while (iterator.hasNext()) {
			// see if this is the edge we want to remove
			JointEdge je = iterator.next();
			if (je.getJoint() == joint) {
				// then remove this joint edge
				iterator.remove();
			}
		}
		
		// finally wake both bodies
		joint.getBody1().awaken();
		joint.getBody2().awaken();
	}
	
	/**
	 * Wakes up any {@link Body}s attached by {@link Joint}s and any 
	 * {@link Body}s that are currently in contact with the given 
	 * {@link Body} and sets the {@link Mass} of the given {@link Body}
	 * to infinite.
	 * <p>
	 * This method is used to directly control a {@link Body} by
	 * translation and rotation instead of velocity/force.  The
	 * returned {@link Mass} object should be stored so that it can 
	 * be restored to the {@link Body} when the relinquish method is
	 * called.
	 * @param body the {@link Body} to control
	 * @return {@link Mass} the original {@link Body}'s {@link Mass}
	 */
	public Mass control(Body body) {
		// wake up all attached bodies
		int jSize = body.joints.size();
		for (int i = 0; i < jSize; i++) {
			JointEdge je = body.joints.get(i);
			je.getOther().awaken();
		}
		// wake up bodies in contact
		int cSize = body.contacts.size();
		for (int i = 0; i < cSize; i++) {
			ContactEdge ce = body.contacts.get(i);
			ce.getOther().awaken();
		}
		// save the original mass
		Mass m = body.getMass();
		// set the mass to infinite
		body.setMass(m, Mass.Type.INFINITE);
		// make sure this body is awake
		body.awaken();
		body.setSleep(false);
		// stop any movement
		body.av = 0.0;
		body.v.zero();
		// return the mass
		return m;
	}
	
	/**
	 * Releases control of the given {@link Body} and sets the
	 * {@link Mass} to the given {@link Mass}.
	 * @param body the {@link Body} to release control of
	 * @param mass the {@link Body}'s original {@link Mass}
	 */
	public void relinquish(Body body, Mass mass) {
		body.awaken();
		body.thaw();
		body.setMass(mass);
		body.setSleep(true);
	}
	
	/**
	 * Sets the gravity.
	 * @param gravity the gravity in meters/second<sup>2</sup>
	 */
	public void setGravity(Vector gravity) {
		this.gravity = gravity;
	}
	
	/**
	 * Returns the gravity.
	 * @return {@link Vector} the gravity in meters/second<sup>2</sup>
	 */
	public Vector getGravity() {
		return this.gravity;
	}

	/**
	 * Sets the bounds of the {@link World}.
	 * @param bounds the bounds
	 */
	public void setBounds(Bounds bounds) {
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
	 * @param broadphaseDetector the broad-phase collision detection algorithm
	 */
	public void setBroadphaseDetector(BroadphaseDetector broadphaseDetector) {
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
	 * @param narrowphaseDetector the narrow-phase collision detection algorithm
	 */
	public void setNarrowphaseDetector(NarrowphaseDetector narrowphaseDetector) {
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
	 * Returns the list of {@link Body} objects.
	 * @return List&lt;{@link Body}&gt; the list of bodies
	 */
	public List<Body> getBodies() {
		return this.bodies;
	}
	
	/**
	 * Returns the number of {@link Body} objects.
	 * @return int the number of bodies
	 */
	public int getNumberOfBodies() {
		return this.bodies.size();
	}
}
