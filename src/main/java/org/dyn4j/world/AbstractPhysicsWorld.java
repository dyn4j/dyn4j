package org.dyn4j.world;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dyn4j.collision.Collisions;
import org.dyn4j.collision.Filter;
import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.ContinuousDetectionMode;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactConstraintSolver;
import org.dyn4j.dynamics.contact.ContactUpdateHandler;
import org.dyn4j.dynamics.contact.SequentialImpulses;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.dynamics.contact.TimeOfImpactSolver;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;
import org.dyn4j.world.listener.ContactListener;
import org.dyn4j.world.listener.DestructionListener;
import org.dyn4j.world.listener.StepListener;
import org.dyn4j.world.listener.TimeOfImpactListener;

public abstract class AbstractPhysicsWorld<T extends PhysicsBody, V extends ContactCollisionData<T>> extends AbstractCollisionWorld<T, BodyFixture, V> implements PhysicsWorld<T, V> {
	/** The dynamics settings for this world */
	protected Settings settings;
	
	/** The {@link TimeStep} used by the dynamics calculations */
	protected TimeStep step;
	
	/** The world gravity vector */
	protected Vector2 gravity;
	
	/** The {@link CoefficientMixer} */
	protected CoefficientMixer coefficientMixer;
	
	/** The {@link ContactConstraintSolver} */
	protected ContactConstraintSolver<T> contactConstraintSolver;
	
	/** The {@link TimeOfImpactSolver} */
	protected TimeOfImpactSolver<T> timeOfImpactSolver;

	/** The {@link Joint} list */
	protected final List<Joint<T>> joints;
	
	// listeners
	
	protected final List<ContactListener<T>> contactListeners;
	
	protected final List<DestructionListener<T>> destructionListeners;
	
	protected final List<TimeOfImpactListener<T>> timeOfImpactListeners;
	
	protected final List<StepListener<T, V>> stepListeners;
	
	// temp data

	// TODO rename to fullUpdateRequired?
	protected boolean updateRequired;
	
	/** The accumulated time */
	protected double time;
	
	protected final Map<T, InteractionGraphNode<T>> interactionGraph;
	
	public AbstractPhysicsWorld() {
		this(64);
	}

	public AbstractPhysicsWorld(int initialBodyCapacity) {
		super(initialBodyCapacity);
		
		this.updateRequired = true;
		
		// initialize all the classes with default values
		this.settings = new Settings();
		this.step = new TimeStep(this.settings.getStepFrequency());
		this.gravity = PhysicsWorld.EARTH_GRAVITY.copy();
		
		// override the broadphase filter
		// the CollisionWorld uses the DefaultBroadphaseFilter
		this.detectBroadphaseFilter = new DetectBroadphaseFilter<T>(this);
		this.coefficientMixer = CoefficientMixer.DEFAULT_MIXER;
		this.contactConstraintSolver = new SequentialImpulses<T>();
		this.timeOfImpactSolver = new TimeOfImpactSolver<T>();
		
		this.joints = new ArrayList<Joint<T>>(16);
		
		this.contactListeners = new ArrayList<ContactListener<T>>();
		this.destructionListeners = new ArrayList<DestructionListener<T>>();
		this.timeOfImpactListeners = new ArrayList<TimeOfImpactListener<T>>();
		this.stepListeners = new ArrayList<StepListener<T,V>>();
		
		this.time = 0.0;
		
		this.interactionGraph = new LinkedHashMap<T, InteractionGraphNode<T>>(Collisions.getEstimatedCollisionPairs(initialBodyCapacity));
	}
	
	@Override
	public boolean update(double elapsedTime) {
		return this.update(elapsedTime, -1.0, 1);
	}

	@Override
	public boolean update(double elapsedTime, int maximumSteps) {
		return this.update(elapsedTime, -1.0, maximumSteps);
	}

	@Override
	public boolean update(double elapsedTime, double stepElapsedTime) {
		return this.update(elapsedTime, stepElapsedTime, 1);
	}

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
			this.step.update(stepElapsedTime <= 0 ? invhz : stepElapsedTime);
			// reset the time
			this.time = this.time - invhz;
			// step the world
			this.step();
			// increment the number of steps
			steps++;
		}
		return steps > 0;
	}

	@Override
	public void updatev(double elapsedTime) {
		// make sure the update time is greater than zero
		if (elapsedTime <= 0.0) return;
		// update the step
		this.step.update(elapsedTime);
		// step the world
		this.step();
	}

	@Override
	public void step(int steps) {
		// get the frequency from settings
		double invhz = this.settings.getStepFrequency();
		// perform the steps
		this.step(steps, invhz);
	}

	@Override
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
	
	@Override
	public void addBody(T body) {
		super.addBody(body);
		this.addInteractionGraphNode(body);
	}
	
	@Override
	public void addJoint(Joint<T> joint) {
		// check for null joint
		if (joint == null) throw new NullPointerException(Messages.getString("dynamics.world.addNullJoint"));
		// TODO what to do with this?
//		// implicitly cast to constraint
//		Constraint constraint = joint;
//		// dont allow adding it twice
//		if (constraint.world == this) throw new IllegalArgumentException(Messages.getString("dynamics.world.addExistingBody"));
//		// dont allow a joint that already is assigned to another world
//		if (constraint.world != null) throw new IllegalArgumentException(Messages.getString("dynamics.world.addOtherWorldBody"));
		// add the joint to the joint list
		this.joints.add(joint);
		// set that its attached to this world
//		constraint.world = this;
		// get the associated bodies
		this.addInteractionGraphEdge(joint);
		
//		PhysicsPipelineBody body1 = (PhysicsPipelineBody)joint.getBody1();
//		PhysicsPipelineBody body2 = (PhysicsPipelineBody)joint.getBody2();
//		// create a joint edge from the first body to the second
//		JointEdge<PhysicsPipelineBody> jointEdge1 = new JointEdge(body2, joint);
//		// add the edge to the body
//		body1.getJoints().add(jointEdge1);
//		// create a joint edge from the second body to the first
//		JointEdge jointEdge2 = new JointEdge(body1, joint);
//		// add the edge to the body
//		body2.joints.add(jointEdge2);
	}

	@Override
	public boolean containsJoint(Joint<T> joint) {
		return this.joints.contains(joint);
	}

	@Override
	public boolean removeJoint(int index) {
		Joint<T> joint = this.joints.get(index);
		return removeJoint(joint);
	}
	
	@Override
	public void removeAllBodies() {
		this.removeAllBodiesAndJoints(false);
	}
	
	@Override
	public boolean removeBody(int index) {
		return this.removeBody(index, false);
	}
	
	@Override
	public boolean removeBody(int index, boolean notify) {
		T body = this.bodies.get(index);
		return this.removeBody(body, notify);
	}
	
	@Override
	public boolean removeBody(T body) {
		return this.removeBody(body, false);
	}

	@Override
	public void removeAllBodiesAndJoints() {
		this.removeAllBodiesAndJoints(false);
	}

	@Override
	public void removeAllJoints() {
		this.removeAllJoints(false);
	}

	@Override
	public void removeAllBodiesAndJoints(boolean notify) {
		this.removeAllBodies(notify);
	}
	
	@Override
	public boolean removeBody(T body, boolean notify) {
		// check for null body
		if (body == null) return false;
		
		List<DestructionListener<T>> destructionListeners = null;
		List<ContactListener<T>> contactListeners = null;
		if (notify) {
			destructionListeners = this.destructionListeners;
			contactListeners = this.contactListeners;
		}
		
		// remove the body from the list
		boolean removed = this.bodies.remove(body);
		
		// only remove joints and contacts if the body was removed
		if (removed) {
			// set the world property to null
			body.setFixtureModificationHandler(null);
			// remove the body from the broadphase
			this.broadphaseDetector.remove(body);
			// remove from the interaction graph
			InteractionGraphNode<T> node = this.interactionGraph.remove(body);
			
			// JOINT CLEANUP
			
			// wake up any bodies connected to this body by a joint
			// and destroy the joints and remove the edges
			Iterator<Joint<T>> jointIterator = node.joints.iterator();
			while (jointIterator.hasNext()) {
				// get the joint edge
				Joint<T> joint = jointIterator.next();
				// get the other body
				T other = joint.getOtherBody(body);
				// wake up the other body
				other.setAtRest(false);
				// remove the joint edge from the other body
				InteractionGraphNode<T> otherNode = this.interactionGraph.get(other);
				otherNode.joints.remove(joint);
				
				// notify of the destroyed joint
				if (notify) {
					for (DestructionListener<T> dl : destructionListeners) {
						dl.destroyed(joint);
					}
				}
				
				// remove the joint from the world
				this.joints.remove(joint);
			}
			
			// CONTACT CLEANUP
			
			Iterator<ContactConstraint<T>> contactConstraintIterator = node.contacts.iterator();
			while (contactConstraintIterator.hasNext()) {
				// get the contact edge
				ContactConstraint<T> contactConstraint = contactConstraintIterator.next();

				// get the other body involved
				T other = contactConstraint.getOtherBody(body);
				
				// wake the other body
				other.setAtRest(false);
				
				// find the other contact edge
				InteractionGraphNode<T> otherNode = this.interactionGraph.get(other);
				otherNode.contacts.remove(contactConstraint);
				
				// remove the stored collision data
				V data = this.collisionData.remove(contactConstraint.getCollisionPair());
				
				if (notify) {
					// notify of contact end
					List<? extends SolvedContact> contacts = contactConstraint.getContacts();
					int csize = contacts.size();
					for (int j = 0; j < csize; j++) {
						Contact contact = contacts.get(j);
						// call the destruction listeners
						for (ContactListener<T> cl : contactListeners) {
							cl.end(data, contact);
						}
					}
					
					// notify destruction of collision info
					for (DestructionListener<T> dl : destructionListeners) {
						dl.destroyed(contactConstraint);
					}
				}
			}
		}
		
		return removed;
	}

	@Override
	public boolean removeJoint(Joint<T> joint) {
		// NOTE: there's nothing to notify when removing a Joint
		boolean removed = this.joints.remove(joint);
		
		// removing a joint is pretty easy, we just need to make sure
		// we remove the joint from the interaction graph nodes
		if (removed) {
			// remove the interaction edges
			T body1 = joint.getBody1();
			T body2 = joint.getBody2();
			
			if (body1 != null) {
				InteractionGraphNode<T> node = this.interactionGraph.get(body1);
				node.joints.remove(joint);
			}
			
			if (body2 != null) {
				InteractionGraphNode<T> node = this.interactionGraph.get(body2);
				node.joints.remove(joint);
			}
		}
		return removed;
	}
	
	@Override
	public void removeAllBodies(boolean notify) {
		int bsize = this.bodies.size();
		
		// if we don't need to notify of anything being
		// destroyed, then we can just clear everything
		if (!notify) {
			for (int i = 0; i < bsize; i++) {
				// get the body
				T body = this.bodies.get(i);
				// set the world property to null
				body.setFixtureModificationHandler(null);
			}
			
			this.clear();
			return;
		}
		
		List<DestructionListener<T>> destructionListeners = null;
		List<ContactListener<T>> contactListeners = null;
		if (notify) {
			destructionListeners = this.destructionListeners;
			contactListeners = this.contactListeners;
		}
		
		// loop over the bodies and clear all
		// joints and contacts
		for (int i = 0; i < bsize; i++) {
			// get the body
			T body = this.bodies.get(i);
			// set the world property to null
			body.setFixtureModificationHandler(null);
			// notify of all the destroyed contacts
			InteractionGraphNode<T> node = this.interactionGraph.get(body);
			
			// JOINT CLEANUP
			
			// destroy the joints and remove the edges
			Iterator<Joint<T>> jointIterator = node.joints.iterator();
			while (jointIterator.hasNext()) {
				// get the joint edge
				Joint<T> joint = jointIterator.next();
				// get the other body
				T other = joint.getOtherBody(body);
				// remove the joint edge from the other body
				InteractionGraphNode<T> otherNode = this.interactionGraph.get(other);
				otherNode.joints.remove(joint);
				
				// notify of the destroyed joint
				for (DestructionListener<T> dl : destructionListeners) {
					dl.destroyed(joint);
				}
			}
			
			// CONTACT CLEANUP
			
			Iterator<ContactConstraint<T>> contactConstraintIterator = node.contacts.iterator();
			while (contactConstraintIterator.hasNext()) {
				// get the contact edge
				ContactConstraint<T> contactConstraint = contactConstraintIterator.next();
				// get the other body involved
				T other = contactConstraint.getOtherBody(body);
				// find the other contact edge
				InteractionGraphNode<T> otherNode = this.interactionGraph.get(other);
				otherNode.contacts.remove(contactConstraint);
				
				// remove the stored collision data
				V data = this.collisionData.remove(contactConstraint.getCollisionPair());
				
				// notify of contact end
				List<? extends SolvedContact> contacts = contactConstraint.getContacts();
				int csize = contacts.size();
				for (int j = 0; j < csize; j++) {
					Contact contact = contacts.get(j);
					// call the destruction listeners
					for (ContactListener<T> cl : contactListeners) {
						cl.end(data, contact);
					}
				}
				
				// notify destruction of collision info
				for (DestructionListener<T> dl : destructionListeners) {
					dl.destroyed(contactConstraint);
				}
			}
			
			// notify of the destroyed body
			for (DestructionListener<T> dl : destructionListeners) {
				dl.destroyed(body);
			}
		}
		
		// clear it all
		this.clear();
	}
	
	protected void clear() {
		this.bodies.clear();
		this.broadphaseDetector.clear();
		this.collisionData.clear();
		this.interactionGraph.clear();
		this.joints.clear();
	}
	
	@Override
	public void removeAllJoints(boolean notify) {
		List<DestructionListener<T>> destructionListeners = null;
		if (notify) {
			destructionListeners = this.destructionListeners;
		}
		
		int size = this.joints.size();
		for (int i = 0; i < size; i++) {
			Joint<T> joint = this.joints.get(i);			

			// remove the interaction edges
			T body1 = joint.getBody1();
			T body2 = joint.getBody2();
			
			if (body1 != null) {
				InteractionGraphNode<T> node = this.interactionGraph.get(body1);
				node.joints.remove(joint);
			}
			
			if (body2 != null) {
				InteractionGraphNode<T> node = this.interactionGraph.get(body2);
				node.joints.remove(joint);
			}
			
			if (notify) {
				for (DestructionListener<T> dl : destructionListeners) {
					dl.destroyed(joint);
				}
			}
		}
	}

	@Override
	public Settings getSettings() {
		return this.settings;
	}

	@Override
	public void setSettings(Settings settings) {
		if (settings == null) throw new NullPointerException(Messages.getString("dynamics.world.nullSettings"));
		this.settings = settings;
	}

	@Override
	public void setGravity(Vector2 gravity) {
		if (gravity == null) throw new NullPointerException(Messages.getString("dynamics.world.nullGravity"));
		this.gravity = gravity;
	}

	@Override
	public Vector2 getGravity() {
		return this.gravity;
	}

	@Override
	public CoefficientMixer getCoefficientMixer() {
		return this.coefficientMixer;
	}

	@Override
	public void setCoefficientMixer(CoefficientMixer coefficientMixer) {
		if (coefficientMixer == null) throw new NullPointerException(Messages.getString("dynamics.world.nullCoefficientMixer"));
		this.coefficientMixer = coefficientMixer;
	}

	@Override
	public void setContactConstraintSolver(ContactConstraintSolver<T> constraintSolver) {
		if (constraintSolver == null) throw new NullPointerException(Messages.getString("dynamics.world.nullContactConstraintSolver"));
		this.contactConstraintSolver = constraintSolver;
	}

	@Override
	public ContactConstraintSolver<T> getContactConstraintSolver() {
		return this.contactConstraintSolver;
	}
	
	@Override
	public void setTimeOfImpactSolver(TimeOfImpactSolver<T> timeOfImpactSolver) {
		if (timeOfImpactSolver == null) throw new NullPointerException(Messages.getString("dynamics.world.nullTimeOfImpactSolver"));
		this.timeOfImpactSolver = timeOfImpactSolver;
	}
	
	@Override
	public TimeOfImpactSolver<T> getTimeOfImpactSolver() {
		return this.timeOfImpactSolver;
	}
	

	@Override
	public int getJointCount() {
		return this.joints.size();
	}

	@Override
	public Joint<T> getJoint(int index) {
		return this.joints.get(index);
	}

	@Override
	public List<Joint<T>> getJoints() {
		return Collections.unmodifiableList(this.joints);
	}

	@Override
	public Iterator<Joint<T>> getJointIterator() {
		return new JointIterator();
	}

	@Override
	public TimeStep getStep() {
		return this.step;
	}

	@Override
	public double getAccumulatedTime() {
		return this.time;
	}

	@Override
	public void setAccumulatedTime(double elapsedTime) {
		if (elapsedTime < 0.0) return;
		this.time = elapsedTime;
	}

	@Override
	public void setUpdateRequired(boolean flag) {
		this.updateRequired = flag;
	}
	
	@Override
	public boolean isUpdateRequired() {
		return this.updateRequired;
	}
	
	@Override
	public boolean isEmpty() {
		int nb = this.bodies.size();
		int nj = this.joints.size();
		return nb <= 0 && nj <= 0;
	}
	
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

	@Override
	public boolean isInContact(T body1, T body2) {
		InteractionGraphNode<T> node = this.interactionGraph.get(body1);
		if (node != null) {
			int size = node.contacts.size();
			for (int i = 0; i < size; i++) {
				ContactConstraint<T> cc = node.contacts.get(i);
				if (cc.getBody1() == body2 || cc.getBody2() == body2) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public List<ContactConstraint<T>> getContacts(T body) {
		List<ContactConstraint<T>> contacts = new ArrayList<ContactConstraint<T>>();
		InteractionGraphNode<T> node = this.interactionGraph.get(body);
		if (node != null) {
			return Collections.unmodifiableList(node.contacts);
		}
		return contacts;
	}
	
	@Override
	public List<T> getInContactBodies(T body, boolean includeSensedContact) {
		List<T> bodies = new ArrayList<T>();
		InteractionGraphNode<T> node = this.interactionGraph.get(body);
		if (node != null) {
			int size = node.contacts.size();
			for (int i = 0; i < size; i++) {
				ContactConstraint<T> cc = node.contacts.get(i);
				T other = cc.getOtherBody(body);
				// basic dup detection
				if (!bodies.contains(other)) {
					bodies.add(other);
				}
			}
		}
		return bodies;
	}

	@Override
	public boolean isJoined(T body1, T body2, boolean includeCollisionNotAllowed) {
		InteractionGraphNode<T> node = this.interactionGraph.get(body1);
		if (node != null) {
			int size = node.joints.size();
			for (int i = 0; i < size; i++) {
				Joint<T> cc = node.joints.get(i);
				if (cc.isCollisionAllowed() || includeCollisionNotAllowed) {
					if (cc.getBody1() == body2 || cc.getBody2() == body2) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public List<Joint<T>> getJoints(T body) {
		List<Joint<T>> contacts = new ArrayList<Joint<T>>();
		InteractionGraphNode<T> node = this.interactionGraph.get(body);
		if (node != null) {
			return Collections.unmodifiableList(node.joints);
		}
		return contacts;
	}

	@Override
	public List<T> getJoinedBodies(T body) {
		List<T> bodies = new ArrayList<T>();
		InteractionGraphNode<T> node = this.interactionGraph.get(body);
		if (node != null) {
			int size = node.contacts.size();
			for (int i = 0; i < size; i++) {
				Joint<T> cc = node.joints.get(i);
				T other = cc.getOtherBody(body);
				// basic dup detection
				if (!bodies.contains(other)) {
					bodies.add(other);
				}
			}
		}
		return bodies;
	}
	
	@Override
	public List<ContactListener<T>> getContactListeners() {
		return this.contactListeners;
	}
	
	@Override
	public List<DestructionListener<T>> getDestructionListeners() {
		return this.destructionListeners;
	}
	
	@Override
	public List<TimeOfImpactListener<T>> getTimeOfImpactListeners() {
		return this.timeOfImpactListeners;
	}
	
	@Override
	public List<StepListener<T, V>> getStepListeners() {
		return this.stepListeners;
	}
	
	protected void step() {
		// get all the step listeners
		List<StepListener<T, V>> stepListeners = this.stepListeners;
		List<ContactListener<T>> contactListeners = this.contactListeners;
		
		int sSize = stepListeners.size();
		
		// notify the step listeners
		for (int i = 0; i < sSize; i++) {
			StepListener<T, V> sl = stepListeners.get(i);
			sl.begin(this.step, this);
		}
		
		// check if we need to update the contacts first
		if (this.updateRequired) {
			// if so then update the contacts
			this.detect();
			// notify that an update was performed
			for (int i = 0; i < sSize; i++) {
				StepListener<T, V> sl = stepListeners.get(i);
				sl.updatePerformed(this.step, this);
			}
			// set the update required flag to false
			this.updateRequired = false;
		}
		
		// notify of all the contacts that will be solved and all the sensed contacts
		if (contactListeners.size() > 0) {
			for (ContactCollisionData<T> data : this.collisionData.values()) {
				if (data.isManifoldCollision()) {
					ContactConstraint<T> cc = data.getContactConstraint();
					for (Contact contact : cc.getContacts()) {
						for (ContactListener<T> listener : contactListeners) {
							listener.preSolve(data, contact);
						}
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
			body.getInitialTransform().set(body.getTransform());
		}
		
		// perform a depth first search of the contact graph
		// to create islands for constraint solving
		Deque<InteractionGraphNode<T>> stack = new ArrayDeque<InteractionGraphNode<T>>(size);

		// create an island to reuse
		Island<T> island = new Island<T>(size, this.joints.size());
		for (InteractionGraphNode<T> seed : this.interactionGraph.values()) {
			T seedBody = seed.body;
			
			// skip if asleep, in active, static, or already on an island
			if (island.isOnIsland(seedBody) || seedBody.isAtRest() || !seedBody.isEnabled() || seedBody.isStatic()) {
				continue;
			}
			
			island.clear();
			stack.clear();
			stack.push(seed);
			
			while (stack.size() > 0) {
				InteractionGraphNode<T> node = stack.pop();
				T body = node.body;
				// add it to the island
				island.add(body);
				// make sure the body is awake
				body.setAtRest(false);
				
				// if its static then continue since we don't want the
				// island to span more than one static object
				// this keeps the size of the islands small
				if (body.isStatic()) {
					continue;
				}
				
				// loop over the contact edges of this body
				int ceSize = node.contacts.size();
				for (int j = 0; j < ceSize; j++) {
					ContactConstraint<T> contactConstraint = node.contacts.get(j);
					
					// skip disabled or sensor contacts or contacts already on the island
					if (!contactConstraint.isEnabled() || contactConstraint.isSensor() || island.isOnIsland(contactConstraint)) {
						continue;
					}
					
					// get the other body
					T other = contactConstraint.getOtherBody(body);
					// add the contact constraint to the island list
					island.add(contactConstraint);
					
					// has the other body been added to an island yet?
					if (!island.isOnIsland(other)) {
						// if not then add this body to the stack
						stack.push(this.interactionGraph.get(other));
					}
				}
				
				// loop over the joint edges of this body
				int jeSize = node.joints.size();
				for (int j = 0; j < jeSize; j++) {
					Joint<T> joint = node.joints.get(j);
					
					// check if the joint is inactive
					if (!joint.isEnabled() || island.isOnIsland(joint)) {
						continue;
					}
					
					// get the other body
					T other = joint.getOtherBody(body);
					
					// check if the joint has already been added to an island
					// or if the other body is not active
					if (!other.isEnabled()) {
						continue;
					}
					
					// add the joint to the island
					island.add(joint);
					// check if the other body has been added to an island
					if (!island.isOnIsland(other)) {
						// if not then add the body to the stack
						stack.push(this.interactionGraph.get(other));
					}
				}
			}
			
			// solve the island
			island.solve(this.contactConstraintSolver, this.gravity, this.step, this.settings);
		}
		
		// allow memory to be reclaimed
		stack.clear();
		island.clear();
		
		// notify of the all solved contacts
		if (contactListeners.size() > 0) {
			for (ContactCollisionData<T> data : this.collisionData.values()) {
				if (data.isManifoldCollision()) {
					ContactConstraint<T> cc = data.getContactConstraint();
					for (SolvedContact contact : cc.getContacts()) {
						for (ContactListener<T> listener : contactListeners) {
							listener.postSolve(data, contact);
						}
					}
				}
			}
		}
		
		// make sure CCD is enabled
		if (continuousDetectionMode != ContinuousDetectionMode.NONE) {
			// solve time of impact
			this.solveTOI(continuousDetectionMode);
		}
		
		// notify the step listener
		for (int i = 0; i < sSize; i++) {
			StepListener<T, V> sl = stepListeners.get(i);
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
			StepListener<T, V> sl = stepListeners.get(i);
			sl.end(this.step, this);
		}
	}
	
	protected void detectCollisions(Iterator<V> iterator) {
		// clear the contact interactions
		for (InteractionGraphNode<T> node : this.interactionGraph.values()) {
			node.contacts.clear();
		}
		
		WarmStartHandler wsh = new WarmStartHandler();
		
		// if the iterator has returned something then it's made it past
		// the manifold stage and is ready to become a contact constraint
//		Iterator<V> iterator = this.detectIterator();
		while (iterator.hasNext()) {
			V data = iterator.next();

			// get the current contact constraint data
			ContactConstraint<T> cc = data.getContactConstraint();
			
			// we can exit early if the collision didn't make it to the manifold stage
			// and there's no existing contacts to report ending
			if (!data.isManifoldCollision() && cc.getContacts().size() == 0) {
				continue;
			}
			
			// setup the handler
			wsh.data = data;
			
			// update the contact constraint
			// NOTE: in the case of an empty manifold, this method will also report
			//       end notifications for any existing contacts
			cc.update(data.getManifold(), this.settings, wsh);
			
			// we only want to add interaction edges if the collision actually made it past
			// the manifold generation stage
			if (data.isManifoldCollision()) {
				// build the contact edges
				this.addInteractionGraphEdge(cc);
			}
		}
	}
	
	private final void addInteractionGraphNode(T body) {
		InteractionGraphNode<T> node = this.interactionGraph.get(body);
		
		if (node == null) {
			node = new InteractionGraphNode<T>(body);
			this.interactionGraph.put(body, node);
		}
	}
	
	private final void addInteractionGraphEdge(ContactConstraint<T> cc) {
		T body1 = cc.getBody1();
		T body2 = cc.getBody2();
		
		InteractionGraphNode<T> node1 = this.interactionGraph.get(body1);
		InteractionGraphNode<T> node2 = this.interactionGraph.get(body2);
		
		if (node1 == null) {
			node1 = new InteractionGraphNode<T>(body1);
			this.interactionGraph.put(body1, node1);
		}
		if (node2 == null) {
			node2 = new InteractionGraphNode<T>(body2);
			this.interactionGraph.put(body1, node2);
		}
		
		node1.contacts.add(cc);
		node2.contacts.add(cc);
	}
	
	private final void addInteractionGraphEdge(Joint<T> joint) {
		T body1 = joint.getBody1();
		T body2 = joint.getBody2();
		
		// some joints (one really) is a uni-body joint
		// all others are pairwise
		
		if (body1 != null) {
			InteractionGraphNode<T> node1 = this.interactionGraph.get(body1);	
			if (node1 == null) {
				node1 = new InteractionGraphNode<T>(body1);
				this.interactionGraph.put(body1, node1);
			}
			node1.joints.add(joint);
		}
		
		if (body1 != null) {
			InteractionGraphNode<T> node2 = this.interactionGraph.get(body2);
			if (node2 == null) {
				node2 = new InteractionGraphNode<T>(body2);
				this.interactionGraph.put(body1, node2);
			}
			node2.joints.add(joint);
		}
	}

	/**
	 * Solves the time of impact for all the {@link PhysicsBody}s in this world.
	 * <p>
	 * This method solves for the time of impact for each {@link PhysicsBody} iteratively
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
		List<TimeOfImpactListener<T>> listeners = this.timeOfImpactListeners;
		// get the number of bodies
		int size = this.bodies.size();
		
		// check the CCD mode
		boolean bulletsOnly = (mode == ContinuousDetectionMode.BULLETS_ONLY);
		
		// loop over all the bodies and find the minimum TOI for each
		// dynamic body
		for (int i = 0; i < size; i++) {
			// get the body
			T body = this.bodies.get(i);
			
			// if we are only doing CCD on bullets only, then check
			// to make sure that the current body is a bullet
			if (bulletsOnly && !body.isBullet()) continue;
			
			// otherwise we process all dynamic bodies

			// we don't want to mess with disabled bodies
			if (!body.isEnabled()) continue;
				
			// we don't process kinematic or static bodies except with
			// dynamic bodies (in other words b1 must always be a dynamic
			// body)
			if (body.getMass().isInfinite()) continue;
			
			// don't bother with bodies that did not have their
			// positions integrated, if they were not added to an island then
			// that means they didn't move
			
			// we can also check for sleeping bodies and skip those since
			// they will only be asleep after being stationary for a set
			// time period
			if (body.isAtRest()) continue;

			// solve for time of impact
			this.solveTOI(body, listeners);
		}
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
	 * @param listeners the list of {@link TimeOfImpactListener}s
	 * @since 3.1.0
	 */
	protected void solveTOI(T body1, List<TimeOfImpactListener<T>> listeners) {
		int size = this.bodies.size();
		
		// generate a swept AABB for this body
		AABB aabb1 = body1.createSweptAABB();
		boolean bullet = body1.isBullet();
		
		// setup the initial time bounds [0, 1]
		double t1 = 0.0;
		double t2 = 1.0;
		
		// save the minimum time of impact and body
		TimeOfImpact minToi = null;
		T minBody = null;
		
		// loop over all the other bodies to find the minimum TOI
		for (int i = 0; i < size; i++) {
			// get the other body
			T body2 = this.bodies.get(i);

			// skip this test if they are the same body
			if (body1 == body2) continue;
			
			// make sure the other body is active
			if (!body2.isEnabled()) continue;

			// skip other dynamic bodies; we only do TOI for
			// dynamic vs. static/kinematic unless its a bullet
			if (body2.isDynamic() && !bullet) continue;
			
			// check for connected pairs who's collision is not allowed
			if (this.isJoined(body1, body2, false)) continue;
			
			// check for bodies already in collision
			if (this.isInContact(body1, body2)) continue;

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
							for (TimeOfImpactListener<T> tl : listeners) {
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
			body1.getInitialTransform().lerp(body1.getTransform(), t, body1.getTransform());
			// check if the other body is dynamic
			if (minBody.isDynamic()) {
				// if the other body is dynamic then interpolate its transform also
				minBody.getInitialTransform().lerp(minBody.getTransform(), t, minBody.getTransform());
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
	
	private final class WarmStartHandler implements ContactUpdateHandler {
		private ContactCollisionData<T> data;
		private final List<ContactListener<T>> listeners;
		
		public WarmStartHandler() {
			this.listeners = AbstractPhysicsWorld.this.contactListeners;
		}

		@Override
		public double getFriction(BodyFixture fixture1, BodyFixture fixture2) {
			return AbstractPhysicsWorld.this.coefficientMixer.mixFriction(fixture1.getFriction(), fixture2.getFriction());
		}

		@Override
		public double getRestitution(BodyFixture fixture1, BodyFixture fixture2) {
			return AbstractPhysicsWorld.this.coefficientMixer.mixFriction(fixture1.getRestitution(), fixture2.getRestitution());
		}

		@Override
		public void begin(Contact contact) {
			int size = this.listeners.size();
			for (int i = 0; i < size; i++) {
				ContactListener<T> listener = this.listeners.get(i);
				listener.begin(this.data, contact);
			}
		}

		@Override
		public void persist(Contact oldContact, Contact newContact) {
			int size = this.listeners.size();
			for (int i = 0; i < size; i++) {
				ContactListener<T> listener = this.listeners.get(i);
				listener.persist(this.data, oldContact, newContact);
			}
		}

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
		
		/**
		 * Minimal constructor.
		 */
		public JointIterator() {
			this.index = -1;
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
			if (this.index >= AbstractPhysicsWorld.this.joints.size()) {
				throw new IndexOutOfBoundsException();
			}
			try {
				this.index++;
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
			if (this.index < 0) {
				throw new IllegalStateException();
			}
			if (this.index >= AbstractPhysicsWorld.this.joints.size()) {
				throw new IndexOutOfBoundsException();
			}
			try {
				AbstractPhysicsWorld.this.removeJoint(this.index);
				this.index--;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}

}
