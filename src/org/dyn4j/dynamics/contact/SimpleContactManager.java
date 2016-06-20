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
package org.dyn4j.dynamics.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dyn4j.collision.Collisions;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Capacity;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Represents a basic {@link ContactManager} that reports new and old contacts.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.2.0
 */
public class SimpleContactManager implements ContactManager, Shiftable {
	/** The contact constraint queue */
	protected List<ContactConstraint> constraintQueue;
	
	/** Map for fast look up of  {@link ContactConstraint}s */
	protected Map<ContactConstraintId, ContactConstraint> constraints;
	
	/**
	 * Default constructor.
	 * @since 3.2.0
	 */
	public SimpleContactManager()  {
		// use the default capacity
		this(Capacity.DEFAULT_CAPACITY);
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * The initial capacity is used to help performance in the event that the developer
	 * knows the number of bodies the world will contain. The {@link ContactManager}
	 * will grow past the initial capacity if necessary.
	 * @param initialCapacity the estimated number of {@link Body}s
	 * @throws NullPointerException if initialCapacity is null
	 * @since 3.2.0
	 */
	public SimpleContactManager(Capacity initialCapacity)  {
		// check for null capacity
		if (initialCapacity == null) throw new NullPointerException(Messages.getString("dynamics.nullCapacity"));
		// estimate the number of contact constraints
		int eSize = Collisions.getEstimatedCollisionPairs(initialCapacity.getBodyCount());
		// initialize the members
		// 0.75 = 3/4, we can garuantee that the hashmap will not need to be rehashed
		// if we take capacity / load factor
		// the default load factor is 0.75 according to the javadocs, but lets assign it to be sure
		this.constraints = new HashMap<ContactConstraintId, ContactConstraint>(eSize * 4 / 3 + 1, 0.75f);
		this.constraintQueue = new ArrayList<ContactConstraint>(eSize);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactManager#queue(org.dyn4j.dynamics.contact.ContactConstraint)
	 */
	@Override
	public void queue(ContactConstraint constraint) {
		this.constraintQueue.add(constraint);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactManager#clear()
	 */
	@Override
	public void clear() {
		this.constraintQueue.clear();
		this.constraints.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactManager#end(org.dyn4j.dynamics.contact.ContactConstraint)
	 */
	public boolean end(ContactConstraint contactConstraint) {
		// remove the contact from the cache
		return this.constraints.remove(contactConstraint.id) != null;
	}
		
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	public void shift(Vector2 shift) {
		// update all the contacts
		Iterator<ContactConstraint> it = this.constraints.values().iterator();
		while (it.hasNext()) {
			ContactConstraint cc = it.next();
			cc.shift(shift);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactManager#updateAndNotify(java.util.List, org.dyn4j.dynamics.Settings)
	 */
	public void updateAndNotify(List<ContactListener> listeners, Settings settings) {
		// get the size of the list
		int size = this.constraintQueue.size();
		int lsize = listeners != null ? listeners.size() : 0;
		
		// notify of the removed contacts
		Iterator<ContactConstraint> icc = this.constraints.values().iterator();
		while (icc.hasNext()) {
			ContactConstraint contactConstraint = icc.next();
			// loop over the contact points
			int rsize = contactConstraint.contacts.size();
			for (int i = 0; i < rsize; i++) {
				// get the contact
				Contact contact = contactConstraint.contacts.get(i);
				// set the contact point values
				ContactPoint point = new ContactPoint(
						new ContactPointId(contactConstraint.id, contact.id),
						contactConstraint.getBody1(),
						contactConstraint.fixture1,
						contactConstraint.getBody2(),
						contactConstraint.fixture2,
						contact.p,
						contactConstraint.normal,
						contact.depth);
				// call the listeners
				for (int l = 0; l < lsize; l++) {
					ContactListener listener = listeners.get(l);
					listener.end(point);
				}
			}
		}
		
		this.constraints.clear();
		
		// loop over the new contact constraints
		// and attempt to persist contacts
		for (int i = 0; i < size; i++) {
			// get the new contact constraint
			ContactConstraint newContactConstraint = this.constraintQueue.get(i);
			
			List<Contact> contacts = newContactConstraint.contacts;
			int nsize = contacts.size();
			
			// check if this contact constraint is a sensor
			if (newContactConstraint.sensor) {
				// notify of the sensed contacts
				for (int j = 0; j < nsize; j++) {
					// get the contact
					Contact contact = contacts.get(j);
					// notify of the sensed contact
					ContactPoint point = new ContactPoint(
							new ContactPointId(newContactConstraint.id, contact.id),
							newContactConstraint.getBody1(),
							newContactConstraint.fixture1,
							newContactConstraint.getBody2(),
							newContactConstraint.fixture2,
							contact.p,
							newContactConstraint.normal,
							contact.depth);
					// call the listeners
					for (int l = 0; l < lsize; l++) {
						ContactListener listener = listeners.get(l);
						listener.sensed(point);
					}
				}
				// we don't need to perform any warm starting for
				// sensed contacts so continue to the next contact constraint
				
				// since sensed contact constraints are never added to the new
				// map, they will not be warm starting if the fixtures ever
				// change from sensors to normal fixtures
				continue;
			}
			
			// notify new contacts
			// if the old contact point was not found notify of the new contact
			for (int j = nsize - 1; j >= 0; j--) {
				// get the contact
				Contact contact = contacts.get(j);
				// notify of new contact (begin of contact)
				ContactPoint point = new ContactPoint(
						new ContactPointId(newContactConstraint.id, contact.id),
						newContactConstraint.getBody1(),
						newContactConstraint.fixture1,
						newContactConstraint.getBody2(),
						newContactConstraint.fixture2,
						contact.p,
						newContactConstraint.normal,
						contact.depth);
				// call the listeners and set the enabled flag to the result
				boolean allow = true;
				for (int l = 0; l < lsize; l++) {
					ContactListener listener = listeners.get(l);
					if (!listener.begin(point)) {
						allow = false;
					}
				}
				if (!allow) {
					contacts.remove(j);
				}
			}
			// only add contact constraints that have contacts remaining
			if (newContactConstraint.contacts.size() > 0) {
				// add the contact constraint to the map
				this.constraints.put(newContactConstraint.id, newContactConstraint);
			}
		}
		
		this.constraintQueue.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactManager#preSolveNotify(java.util.List)
	 */
	public void preSolveNotify(List<ContactListener> listeners) {
		int lsize = listeners != null ? listeners.size() : 0;
		
		// loop through the list of contacts that were solved
		Iterator<ContactConstraint> itContactConstraints = this.constraints.values().iterator();
		while (itContactConstraints.hasNext()) {
			// get the contact constraint
			ContactConstraint contactConstraint = itContactConstraints.next();
			// loop over the contacts
			int csize = contactConstraint.contacts.size();
			// iterate backwards so we can remove
			for (int j = csize - 1; j >= 0; j--) {
				// get the contact
				Contact contact = contactConstraint.contacts.get(j);
				// notify of the contact that will be solved
				ContactPoint point = new ContactPoint(
						new ContactPointId(contactConstraint.id, contact.id),
						contactConstraint.getBody1(),
						contactConstraint.fixture1,
						contactConstraint.getBody2(),
						contactConstraint.fixture2,
						contact.p,
						contactConstraint.normal,
						contact.depth);
				// call the listeners and set the enabled flag to the result
				boolean allow = true;
				for (int l = 0; l < lsize; l++) {
					ContactListener listener = listeners.get(l);
					if (!listener.preSolve(point)) {
						allow = false;
					}
				}
				// if any of the listeners flagged it as not allowed then
				// remove the contact from the list
				if (!allow) {
					contactConstraint.contacts.remove(j);
				}
			}
			// check if all the contacts were not allowed
			if (contactConstraint.contacts.size() == 0) {
				// remove the constraint
				itContactConstraints.remove();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactManager#postSolveNotify(java.util.List)
	 */
	public void postSolveNotify(List<ContactListener> listeners) {
		int lsize = listeners != null ? listeners.size() : 0;
		
		// loop through the list of contacts that were solved
		for (ContactConstraint contactConstraint : this.constraints.values()) {
			// loop over the contacts
			int rsize = contactConstraint.contacts.size();
			for (int j = 0; j < rsize; j++) {
				// get the contact
				Contact contact = contactConstraint.contacts.get(j);
				// set the contact point values
				SolvedContactPoint point = new SolvedContactPoint(
						new ContactPointId(contactConstraint.id, contact.id),
						contactConstraint.getBody1(),
						contactConstraint.fixture1,
						contactConstraint.getBody2(),
						contactConstraint.fixture2,
						contact.p,
						contactConstraint.normal,
						contact.depth,
						contact.jn,
						contact.jt);
				// notify of them being solved
				for (int l = 0; l < lsize; l++) {
					ContactListener listener = listeners.get(l);
					listener.postSolve(point);
				}
			}
		}
	}
}
