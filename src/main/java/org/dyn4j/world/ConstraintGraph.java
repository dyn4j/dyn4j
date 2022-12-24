/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dyn4j.collision.Collisions;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.TimeStep;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactConstraintSolver;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Vector2;

/**
 * Represents an undirected graph of constraints involving {@link PhysicsBody}s with the desire
 * to split the simulation into smaller, solvable chunks.
 * <p>
 * The graph is maintained by using the various add/remove methods. The {@link PhysicsWorld}
 * is a consumer of this object, adding nodes and edges when bodies/joints are added. During
 * the collision detection process, contact edges are cleared and recreated.
 * <p>
 * Solving of the graph happens internally by performing depth-first traversal and 
 * the building of {@link Island}s separated by static {@link PhysicsBody}s.
 * @author William Bittle
 * @version 5.0.0
 * @since 4.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public final class ConstraintGraph<T extends PhysicsBody> {
	/** The constraint graph storage mechanism */
	private final Map<T, ConstraintGraphNode<T>> graph;

	// for solving
	
	/** Stack for depth-first traversal of the graph */
	private final Deque<ConstraintGraphNode<T>> stack;

	/** Fast lookup of the objects (Body, Joint, ContactConstraint) that have been added to an island */
	private final Set<Object> onIsland;
	
	/** Fast lookup of static bodies that have been added to the current island */
	private final Set<Object> staticOnIsland;
	
	/** A reusable island instance for solving */
	private final Island<T> island;
	
	/**
	 * Minimal constructor.
	 */
	public ConstraintGraph() {
		this(64, 16);
	}
	
	/**
	 * Full constructor.
	 * @param initialBodyCount the initial body count
	 * @param initialJointCount the initial joint count
	 */
	public ConstraintGraph(int initialBodyCount, int initialJointCount) {
		if (initialBodyCount <= 0) initialBodyCount = 64;
		if (initialJointCount <= 0) initialJointCount = 16;
		
		int contactConstraintCount = Collisions.getEstimatedCollisionPairs(initialBodyCount);
		int stackSize = Math.max((int)Math.log(initialBodyCount), 8);
		int totalSize = Math.max(initialBodyCount + initialJointCount + contactConstraintCount, 64);
		
		this.graph = new LinkedHashMap<T, ConstraintGraphNode<T>>(initialBodyCount);
		this.stack = new ArrayDeque<ConstraintGraphNode<T>>(stackSize);
		this.onIsland = new HashSet<Object>(totalSize);
		this.staticOnIsland = new HashSet<Object>(totalSize / 2);
		this.island = new Island<T>(initialBodyCount, initialJointCount);
	}
	
	/**
	 * Adds an interaction graph node for the given body.
	 * @param body the body
	 */
	public void addBody(T body) {
		ConstraintGraphNode<T> node = this.graph.get(body);
		
		if (node == null) {
			node = new ConstraintGraphNode<T>(body);
			this.graph.put(body, node);
		}
	}
	
	/**
	 * Adds an interaction graph edge for the given {@link ContactConstraint}.
	 * @param contactConstraint the contact constraint
	 */
	public void addContactConstraint(ContactConstraint<T> contactConstraint) {
		T body1 = contactConstraint.getBody1();
		T body2 = contactConstraint.getBody2();
		
		ConstraintGraphNode<T> node1 = this.graph.get(body1);
		ConstraintGraphNode<T> node2 = this.graph.get(body2);
		
		// NOTE: node1/node2 shouldn't ever be null since
		// the we shouldn't generate a contact constraint
		// for bodies that don't already exist in the world
		// but it's here just in case
		if (node1 == null) {
			node1 = new ConstraintGraphNode<T>(body1);
			this.graph.put(body1, node1);
		}
		if (node2 == null) {
			node2 = new ConstraintGraphNode<T>(body2);
			this.graph.put(body2, node2);
		}
		
		node1.contactConstraints.add(contactConstraint);
		node2.contactConstraints.add(contactConstraint);
	}
	
	/**
	 * Adds an interaction graph edge for the given {@link Joint}.
	 * @param joint the joint
	 */
	public void addJoint(Joint<T> joint) {
		int n = joint.getBodyCount();
		for (int i = 0; i < n; i++) {
			T body = joint.getBody(i);
			ConstraintGraphNode<T> node = this.graph.get(body);	
			if (node == null) {
				node = new ConstraintGraphNode<T>(body);
				this.graph.put(body, node);
			}
			node.joints.add(joint);
		}
	}

	/**
	 * Returns true if the given body is part of the interaction graph.
	 * @param body the body
	 * @return boolean
	 */
	public boolean containsBody(T body) {
		return this.graph.containsKey(body);
	}
	
	/**
	 * Removes the given body from the graph.
	 * <p>
	 * A body represents a node in the graph, therefore removing the node
	 * will remove the edges between the given node and other nodes as well.
	 * @param body the body to remove
	 * @return {@link ConstraintGraphNode}&lt;T&gt;
	 */
	public ConstraintGraphNode<T> removeBody(T body) {
		// remove the node
		ConstraintGraphNode<T> node = this.graph.remove(body);
		
		// remove any joints edges
		int jSize = node.joints.size();
		for (int i = 0; i < jSize; i++) {
			Joint<T> joint = node.joints.get(i);
			// we need to remove it from all bodies involved in the joint
			int bSize = joint.getBodyCount();
			for (int j = 0; j < bSize; j++) {
				T other = joint.getBody(j);
				// remove the joint edge from the other body
				ConstraintGraphNode<T> otherNode = this.graph.get(other);
				// NOTE: some joints are unary and body1 == body2, and
				// at this point, the body node has already been removed
				if (otherNode != null) {
					otherNode.joints.remove(joint);
				}
			}
		}
		
		// remove any contact constraint edges
		int cSize = node.contactConstraints.size();
		for (int i = 0; i < cSize; i++) {
			ContactConstraint<T> contactConstraint = node.contactConstraints.get(i);
			// get the other body involved
			T other = contactConstraint.getOtherBody(body);
			// find the other contact edge
			ConstraintGraphNode<T> otherNode = this.graph.get(other);
			otherNode.contactConstraints.remove(contactConstraint);
		}
		
		return node;
	}
	
	/**
	 * Returns true if the given joint exists in this interaction graph.
	 * @param joint the joint
	 * @return boolean
	 */
	public boolean containsJoint(Joint<T> joint) {
		// for the joint to exist, ALL bodies must exist
		// AND all body nodes must exist, AND all body nodes
		// must have a link to the joint
		
		int bSize = joint.getBodyCount();
		
		boolean bodiesExist = false;
		boolean jointLinksExist = false;
		
		ConstraintGraphNode<T> node = this.graph.get(joint.getBody(0));
		if (node != null) {
			bodiesExist = true;
			if (node.joints.contains(joint)) {
				jointLinksExist = true;
			}
		}
		
		for (int i = 1; i < bSize; i++) {
			T body = joint.getBody(i);
			node = this.graph.get(body);
			bodiesExist &= node != null;
			if (node != null) {
				jointLinksExist &= node.joints.contains(joint);
			}
		}
		
		return bodiesExist && jointLinksExist;
	}
	
	/**
	 * Returns true if the given contact constraint exists in this interaction graph.
	 * @param contactConstraint the contact constraint
	 * @return boolean
	 */
	public boolean containsContactConstraint(ContactConstraint<T> contactConstraint) {
		T body1 = contactConstraint.getBody1();
		T body2 = contactConstraint.getBody2();
		
		boolean atob = false;
		boolean btoa = false;
		if (body1 != null) {
			ConstraintGraphNode<T> node = this.graph.get(body1);
			if (node != null) {
				atob = node.contactConstraints.contains(contactConstraint);
			}
		}
		
		if (body2 != null) {
			ConstraintGraphNode<T> node = this.graph.get(body2);
			if (node != null) {
				btoa = node.contactConstraints.contains(contactConstraint);
			}
		}
		
		return atob && btoa;
	}
	
	/**
	 * Removes the given joint from the graph.
	 * <p>
	 * A joint is an edge connecting body nodes. This method removes the edges
	 * related to the given joint.
	 * @param joint the joint to remove
	 */
	public void removeJoint(Joint<T> joint) {
		// remove the interaction edges
		int bSize = joint.getBodyCount();
		for (int i = 0; i < bSize; i++) {
			T body = joint.getBody(i);
			ConstraintGraphNode<T> node = this.graph.get(body);
			if (node != null) {
				node.joints.remove(joint);
			}
		}
	}
	
	/**
	 * Removes the given contact constraint from the graph.
	 * <p>
	 * A contact constraint is an edge connecting body nodes. This method removes the edges
	 * related to the given contact constraint.
	 * @param contactConstraint the contact constraint to remove
	 */
	public void removeContactConstraint(ContactConstraint<T> contactConstraint) {
		// remove the interaction edges
		T body1 = contactConstraint.getBody1();
		T body2 = contactConstraint.getBody2();
		
		if (body1 != null) {
			ConstraintGraphNode<T> node = this.graph.get(body1);
			if (node != null) {
				node.contactConstraints.remove(contactConstraint);
			}
		}
		
		if (body2 != null) {
			ConstraintGraphNode<T> node = this.graph.get(body2);
			if (node != null) {
				node.contactConstraints.remove(contactConstraint);
			}
		}
	}
	
	/**
	 * Returns the node for the given body on the graph.
	 * <p>
	 * Returns null if the body is not on the graph.
	 * @param body the body to remove
	 * @return {@link ConstraintGraphNode}&lt;T&gt;
	 */
	public ConstraintGraphNode<T> getNode(T body) {
		return this.graph.get(body);
	}
	
	/**
	 * Removes all edges in the graph related to contact constraints.
	 */
	public void removeAllContactConstraints() {
		for (ConstraintGraphNode<T> node : this.graph.values()) {
			node.contactConstraints.clear();
		}
	}
	
	/**
	 * Removes all edges in the graph related to joints.
	 */
	public void removeAllJoints() {
		for (ConstraintGraphNode<T> node : this.graph.values()) {
			node.joints.clear();
		}
	}
	
	/**
	 * Clears the graph of all nodes and edges.
	 */
	public void clear() {
		this.graph.clear();
	}
	
	/**
	 * Returns the number of nodes in the graph.
	 * @return int
	 */
	public int size() {
		return this.graph.size();
	}
	
	// helpers
	
	/**
	 * Returns true if the given {@link PhysicsBody}s are currently in collision.
	 * <p>
	 * Collision is defined as the two bodies interacting to the level of {@link ContactConstraint}
	 * generation and solving.
	 * @param body1 the first body
	 * @param body2 the second body
	 * @return boolean
	 */
	public boolean isInContact(T body1, T body2) {
		ConstraintGraphNode<T> node = this.graph.get(body1);
		if (node != null) {
			int size = node.contactConstraints.size();
			for (int i = 0; i < size; i++) {
				ContactConstraint<T> cc = node.contactConstraints.get(i);
				if (cc.getBody1() == body2 || cc.getBody2() == body2) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns the {@link ContactConstraint}s for the given {@link PhysicsBody}.
	 * <p>
	 * These represent the contact pairs between the given body and the others it's colliding with.
	 * Each {@link ContactConstraint} could have 1 or 2 contacts associated with it.
	 * @param body the body
	 * @return List&lt;{@link ContactConstraint}&gt;
	 */
	public List<ContactConstraint<T>> getContacts(T body) {
		List<ContactConstraint<T>> contacts = new ArrayList<ContactConstraint<T>>();
		ConstraintGraphNode<T> node = this.graph.get(body);
		if (node != null) {
			return node.contactConstraintsUnmodifiable;
		}
		return contacts;
	}

	/**
	 * Returns a list of all {@link PhysicsBody}s that are in contact with the given {@link PhysicsBody}.
	 * @param body the body
	 * @param includeSensedContact true if sensed contacts should be included in the results
	 * @return List&lt;{@link PhysicsBody}&gt;
	 */
	public List<T> getInContactBodies(T body, boolean includeSensedContact) {
		List<T> bodies = new ArrayList<T>();
		ConstraintGraphNode<T> node = this.graph.get(body);
		if (node != null) {
			int size = node.contactConstraints.size();
			for (int i = 0; i < size; i++) {
				ContactConstraint<T> cc = node.contactConstraints.get(i);
				if (!includeSensedContact && cc.isSensor()) {
					continue;
				}
				
				T other = cc.getOtherBody(body);
				// basic dup detection
				if (!bodies.contains(other)) {
					bodies.add(other);
				}
			}
		}
		return bodies;
	}

	/**
	 * Returns true if the two {@link PhysicsBody}s are joined by at least one {@link Joint}
	 * where the collision allowed property is true.
	 * @param body1 the first body
	 * @param body2 the second body
	 * @return boolean
	 */
	public boolean isJointCollisionAllowed(T body1, T body2) {
		// check for a null body
		if (body1 == null || body2 == null) {
			return false;
		}
		
		// check that both bodies are part of this graph
		ConstraintGraphNode<T> node1 = this.graph.get(body1);
		ConstraintGraphNode<T> node2 = this.graph.get(body2);
		if (node1 == null || node2 == null) {
			return false;
		}
		
		int size = node1.joints.size();
		
		// if there are no joints on this body, then the
		// collision is allowed
		if (size == 0) return true;
		
		// if any joint connecting body1 and body2 allows collision
		// then the collision is allowed
		boolean connectedWithBody2 = false;
		for (int i = 0; i < size; i++) {
			Joint<T> joint = node1.joints.get(i);
			// testing object references should be sufficient
			if (joint.isMember(body2)) {
				connectedWithBody2 = true;
				// check if collision is allowed
				// we do an "OR" here to find if there is at least one
				// joint joining the two bodies that allows collision
				if (joint.isCollisionAllowed()) {
					return true;
				}
			}
		}
		
		// if body1 has joints, but none are with body2
		// then connectedWithBody2 = false
		
		// if body1 has joints with body2, but none of them
		// allow collision connectedWithBody2 = true
		return !connectedWithBody2;
	}
	
	/**
	 * Returns true if the two {@link PhysicsBody}s are joined via a {@link Joint}.
	 * @param body1 the first body
	 * @param body2 the second body
	 * @return boolean
	 */
	public boolean isJoined(T body1, T body2) {
		// check for a null body
		if (body1 == null || body2 == null) {
			return false;
		}
		
		ConstraintGraphNode<T> node = this.graph.get(body1);
		if (node != null) {
			int size = node.joints.size();
			// check the size
			if (size == 0) return false;
			// loop over all the joints
			for (int i = 0; i < size; i++) {
				Joint<T> joint = node.joints.get(i);
				// testing object references should be sufficient
				if (joint.isMember(body2)) {
					return true;
				}
			}
		}
		
		// not found, so return false
		return false;
		
	}

	/**
	 * Returns the {@link Joint}s the given {@link PhysicsBody} is a member of.
	 * @param body the body
	 * @return List&lt;{@link Joint}&gt;
	 */
	public List<Joint<T>> getJoints(T body) {
		List<Joint<T>> contacts = new ArrayList<Joint<T>>();
		ConstraintGraphNode<T> node = this.graph.get(body);
		if (node != null) {
			return node.jointsUnmodifiable;
		}
		return contacts;
	}

	/**
	 * Returns the {@link PhysicsBody}s joined to the given {@link PhysicsBody} via {@link Joint}s.
	 * @param body the body
	 * @return List&lt;{@link PhysicsBody}&gt;
	 */
	public List<T> getJoinedBodies(T body) {
		List<T> bodies = new ArrayList<T>();
		ConstraintGraphNode<T> node = this.graph.get(body);
		if (node != null) {
			int size = node.joints.size();
			for (int i = 0; i < size; i++) {
				Joint<T> joint = node.joints.get(i);
				int bSize = joint.getBodyCount();
				for (int j = 0; j < bSize; j++) {
					T other = joint.getBody(j);
					// skip the input body
					if (other == body) {
						continue;
					}
					// basic dup detection (reference equals)
					if (!bodies.contains(other)) {
						bodies.add(other);
					}
				}
			}
		}
		return bodies;
	}
	
	/**
	 * Solves the interation graph constraints (Joints/Contacts) by splitting the graph into
	 * {@link Island}s. Each {@link Island} represents a segment of the constraint graph that
	 * can be solved in isolation.
	 * @param solver the contact constraint solver
	 * @param gravity the world gravity
	 * @param step the time step information
	 * @param settings the settings
	 */
	public void solve(ContactConstraintSolver<T> solver, Vector2 gravity, TimeStep step, Settings settings) {
		// perform a depth first search of the contact graph
		// to create islands for constraint solving
		// and solve them sequentially
		
		this.stack.clear();
		this.onIsland.clear();
		this.staticOnIsland.clear();
		
		// create an island to reuse
		for (ConstraintGraphNode<T> seed : this.graph.values()) {
			T seedBody = seed.body;
			
			// skip if asleep, in active, static, or already on an island
			if (this.onIsland.contains(seedBody) || seedBody.isAtRest() || !seedBody.isEnabled() || seedBody.isStatic()) {
				continue;
			}
			
			this.island.clear();
			this.stack.clear();
			this.stack.push(seed);
			
			while (this.stack.size() > 0) {
				ConstraintGraphNode<T> node = this.stack.pop();
				T body = node.body;
				
				// a body may be added to the stack many times in the case
				// of more than one/two contacts per body
				if (this.onIsland.contains(body) || this.staticOnIsland.contains(body)) {
					continue;
				}
				
				// add it to the island
				this.island.add(body);
				// make sure the body is awake
				body.setAtRest(false);
				
				// if its static then continue since we don't want the
				// island to span more than one static object
				// this keeps the size of the islands small
				if (body.isStatic()) {
					this.staticOnIsland.add(body);
					continue;
				} else {
					this.onIsland.add(body);
				}
				
				// loop over the contact edges of this body
				int ceSize = node.contactConstraints.size();
				for (int j = 0; j < ceSize; j++) {
					ContactConstraint<T> contactConstraint = node.contactConstraints.get(j);
					
					// skip disabled or sensor contacts or contacts already on the island
					if (!contactConstraint.isEnabled() || contactConstraint.isSensor() || this.onIsland.contains(contactConstraint)) {
						continue;
					}
					
					// get the other body
					T other = contactConstraint.getOtherBody(body);
					// add the contact constraint to the island list
					this.island.add(contactConstraint);
					this.onIsland.add(contactConstraint);
					
					// has the other body been added to an island yet?
					if (!this.onIsland.contains(other) && !this.staticOnIsland.contains(other)) {
						// if not then add this body to the stack
						this.stack.push(this.graph.get(other));
					}
				}
				
				// loop over the joint edges of this body
				int jeSize = node.joints.size();
				for (int j = 0; j < jeSize; j++) {
					Joint<T> joint = node.joints.get(j);
					
					// check if the joint is enabled (all bodies must be enabled)
					// check if the joint has already been added to an island
					if (!joint.isEnabled() || this.onIsland.contains(joint)) {
						continue;
					}

					// add the joint to the island
					this.island.add(joint);
					this.onIsland.add(joint);
					
					// now add all other bodies to this island
					int bSize = joint.getBodyCount();
					for (int k = 0; k < bSize; k++) {
						// get the other body
						T other = joint.getBody(k);
						
						// don't re-process the same body
						if (other == body) {
							continue;
						}

						// check if this body is not enabled (shouldn't happen since
						// we check for this above and the joint wouldn't be enable
						// in that scenario)
						if (!other.isEnabled()) {
							continue;
						}
						
						// check if the other body has been added to an island
						if (!this.onIsland.contains(other) && !this.staticOnIsland.contains(other)) {
							// if not then add the body to the stack
							this.stack.push(this.graph.get(other));
						}
					}
				}
			}
			
			// solve the island
			this.island.solve(solver, gravity, step, settings);
			
			// islands can reuse static bodies because these are what split the
			// whole constraint graph into islands
			this.staticOnIsland.clear();
		}
		
		// allow memory to be reclaimed
		this.stack.clear();
		this.island.clear();
		this.onIsland.clear();
	}
}
