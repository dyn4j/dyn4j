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
package org.dyn4j.dynamics;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.DataContainer;
import org.dyn4j.Ownable;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Transformable;

/**
 * Full implementation of the {@link CollisionBody} and {@link PhysicsBody} interfaces.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 */
@SuppressWarnings("deprecation")
public class Body extends AbstractPhysicsBody implements PhysicsBody, CollisionBody<BodyFixture>, Transformable, DataContainer, Ownable {
	/** 
	 * The {@link Body}'s contacts
	 * @deprecated Deprecated in 4.0.0. No longer needed.
	 */
	@Deprecated
	final List<ContactEdge> contacts;
	
	/** 
	 * The {@link Body}'s joints
	 * @deprecated Deprecated in 4.0.0. No longer needed. 
	 */
	@Deprecated
	final List<JointEdge> joints;
	
	/**
	 * True if this body has been added to an island.
	 * @deprecated Deprecated in 4.0.0. No longer needed.
	 */
	@Deprecated
	boolean onIsland;
	
	/**
	 * Default constructor.
	 */
	public Body() {
		this.contacts = new ArrayList<ContactEdge>();
		this.joints = new ArrayList<JointEdge>();
		this.onIsland = false;
	}
	
	/**
	 * Returns true if this body is already on an island.
	 * @return boolean
	 * @deprecated Deprecated in 4.0.0. No longer needed.
	 */
	@Deprecated
	public boolean isOnIsland() {
		return this.onIsland;
	}
	
	/**
	 * Sets whether this body is on an island.
	 * @param flag true if the body is on an island
	 * @deprecated Deprecated in 4.0.0. No longer needed.
	 */
	@Deprecated
	public void setOnIsland(boolean flag) {
		this.onIsland = flag;
	}

	/**
	 * Returns the previous frame's transform.
	 * @return {@link Transform}
	 * @deprecated Deprecated in 4.0.0. Use the {@link #getPreviousTransform()} method instead.
	 */
	@Deprecated
	public Transform getInitialTransform() {
		return this.transform0;
	}
	
	/**
	 * Returns true if the given {@link Body} is connected
	 * to this {@link Body} by a {@link Joint}.
	 * <p>
	 * Returns false if the given body is null.
	 * @param body the suspect connected body
	 * @return boolean
	 * @deprecated Deprecated in 4.0.0. Use the isJoined method in PhysicsWorld instead.
	 */
	@Deprecated
	public boolean isConnected(Body body) {
		// check for a null body
		if (body == null) return false;
		int size = this.joints.size();
		// check the size
		if (size == 0) return false;
		// loop over all the joints
		for (int i = 0; i < size; i++) {
			JointEdge je = this.joints.get(i);
			// testing object references should be sufficient
			if (je.other == body) {
				// if it is then return true
				return true;
			}
		}
		// not found, so return false
		return false;
	}
	
	/**
	 * Returns true if the given {@link Body} is connected to this
	 * {@link Body}, given the collision flag, via a {@link Joint}.
	 * <p>
	 * If the given collision flag is true, this method will return true
	 * only if collision is allowed between the two joined {@link Body}s.
	 * <p>
	 * If the given collision flag is false, this method will return true
	 * only if collision is <b>NOT</b> allowed between the two joined {@link Body}s.
	 * <p>
	 * If the {@link Body}s are connected by more than one joint, if any allows
	 * collision, then the bodies are considered connected AND allowing collision.
	 * <p>
	 * Returns false if the given body is null.
	 * @param body the suspect connected body
	 * @param collisionAllowed the collision allowed flag
	 * @return boolean
	 * @deprecated Deprecated in 4.0.0.  Use the isJoined method in PhysicsWorld instead.
	 */
	@Deprecated
	public boolean isConnected(Body body, boolean collisionAllowed) {
		// check for a null body
		if (body == null) return false;
		int size = this.joints.size();
		// check the size
		if (size == 0) return false;
		// loop over all the joints
		boolean allowed = false;
		boolean connected = false;
		for (int i = 0; i < size; i++) {
			JointEdge je = this.joints.get(i);
			// testing object references should be sufficient
			if (je.other == body) {
				// get the joint
				Joint<?> joint = je.interaction;
				// set that they are connected
				connected = true;
				// check if collision is allowed
				// we do an or here to find if there is at least one
				// joint joining the two bodies that allows collision
				allowed |= joint.isCollisionAllowed();
			}
		}
		// if they are not connected at all we can ignore the collision
		// allowed flag passed in and return false
		if (!connected) return false;
		// if at least one joint between the two bodies allow collision
		// then the allowed variable will be true, check this against 
		// the desired flag passed in
		if (allowed == collisionAllowed) {
			return true;
		}
		// not found, so return false
		return false;
	}
	
	/**
	 * Returns true if the given {@link Body} is in collision with this {@link Body}.
	 * <p>
	 * Returns false if the given body is null.
	 * @param body the {@link Body} to test
	 * @return boolean true if the given {@link Body} is in collision with this {@link Body}
	 * @since 1.2.0
	 * @deprecated Deprecated in 4.0.0.  Use the isInContact method in PhysicsWorld instead.
	 */
	@Deprecated
	public boolean isInContact(Body body) {
		// check for a null body
		if (body == null) return false;
		// get the number of contacts
		int size = this.contacts.size();
		// check for zero contacts
		if (size == 0) return false;
		// loop over the contacts
		for (int i = 0; i < size; i++) {
			ContactEdge ce = this.contacts.get(i);
			// is the other body equal to the given body?
			if (ce.other == body) {
				// if so then return true
				return true;
			}
		}
		// if we get here then we know no contact exists
		return false;
	}
	
	/**
	 * Returns a list of {@link Body}s connected
	 * by {@link Joint}s.
	 * <p>
	 * If a body is connected to another body with more
	 * than one joint, this method will return just one
	 * entry for the connected body.
	 * @return List&lt;{@link Body}&gt;
	 * @since 1.0.1
	 * @deprecated Deprecated as of 4.0.0.  Use the getJoinedBodies method in PhysicsWorld instead.
	 */
	@Deprecated
	public List<Body> getJoinedBodies() {
		int size = this.joints.size();
		// create a list of the correct capacity
		List<Body> bodies = new ArrayList<Body>(size);
		// add all the joint bodies
		for (int i = 0; i < size; i++) {
			JointEdge je = this.joints.get(i);
			// get the other body
			Body other = je.other;
			// make sure that the body hasn't been added
			// to the list already
			if (!bodies.contains(other)) {
				bodies.add(other);
			}
		}
		// return the connected bodies
		return bodies;
	}

	/**
	 * Returns a list of {@link Joint}s that this 
	 * {@link Body} is connected with.
	 * @return List&lt;{@link Joint}&gt;
	 * @since 1.0.1
	 * @deprecated Deprecated as of 4.0.0.  Use the getJoints method in PhysicsWorld instead.
	 */
	@SuppressWarnings("rawtypes")
	@Deprecated
	public List<Joint> getJoints() {
		int size = this.joints.size();
		// create a list of the correct capacity
		List<Joint> joints = new ArrayList<Joint>(size);
		// add all the joints
		for (int i = 0; i < size; i++) {
			JointEdge je = this.joints.get(i);
			joints.add(je.interaction);
		}
		// return the connected joints
		return joints;
	}
	
	/**
	 * Returns a list of {@link Body}s that are in
	 * contact with this {@link Body}.
	 * <p>
	 * Passing a value of true results in a list containing only
	 * the sensed contacts for this body.  Passing a value of false
	 * results in a list containing only normal contacts.
	 * <p>
	 * Calling this method from any of the {@link CollisionListener} methods
	 * may produce incorrect results.
	 * <p>
	 * If this body has multiple contact constraints with another body (which can
	 * happen when either body has multiple fixtures), this method will only return
	 * one entry for the in contact body.
	 * @param sensed true for only sensed contacts; false for only normal contacts
	 * @return List&lt;{@link Body}&gt;
	 * @since 1.0.1
	 * @deprecated Deprecated as of 4.0.0.  Use the getInContactBodies method in PhysicsWorld instead.
	 */
	@Deprecated
	public List<Body> getInContactBodies(boolean sensed) {
		int size = this.contacts.size();
		// create a list of the correct capacity
		List<Body> bodies = new ArrayList<Body>(size);
		// add all the contact bodies
		for (int i = 0; i < size; i++) {
			ContactEdge ce = this.contacts.get(i);
			// check for sensor contact
			ContactConstraint<?> constraint = ce.interaction;
			if (sensed == constraint.isSensor()) {
				// get the other body
				Body other = ce.other;
				// make sure the body hasn't been added to 
				// the list already
				if (!bodies.contains(other)) {
					// add it to the list
					bodies.add(other);
				}
			}
		}
		// return the connected bodies
		return bodies;
	}
	
	/**
	 * Returns a list of {@link ContactPoint}s 
	 * <p>
	 * Passing a value of true results in a list containing only
	 * the sensed contacts for this body.  Passing a value of false
	 * results in a list containing only normal contacts.
	 * <p>
	 * Calling this method from any of the {@link CollisionListener} methods
	 * may produce incorrect results.
	 * <p>
	 * Modifying the {@link ContactPoint}s returned is not advised.  Use the
	 * {@link ContactListener} methods instead.
	 * @param sensed true for only sensed contacts; false for only normal contacts
	 * @return List&lt;{@link ContactPoint}&gt;
	 * @since 1.0.1
	 * @deprecated Deprecated as of 4.0.0. Use the getContacts method in PhysicsWorld instead.
	 */
	@Deprecated
	public List<ContactPoint> getContacts(boolean sensed) {
		int size = this.contacts.size();
		// create a list to store the contacts (worst case initial capacity)
		List<ContactPoint> contactPoints = new ArrayList<ContactPoint>(size * 2);
		// add all the contact points
		for (int i = 0; i < size; i++) {
			ContactEdge ce = this.contacts.get(i);
			// check for sensor contact
			ContactConstraint<?> constraint = ce.interaction;
			if (sensed == constraint.isSensor()) {
				// loop over the contacts
				List<? extends SolvedContact> contacts = constraint.getContacts();
				int csize = contacts.size();
				for (int j = 0; j < csize; j++) {
					// get the contact
					Contact contact = contacts.get(j);
					// create the contact point
					ContactPoint contactPoint = new ContactPoint(constraint, contact);
					// add the point
					contactPoints.add(contactPoint);
				}
			}
		}
		// return the connected bodies
		return contactPoints;
	}
}