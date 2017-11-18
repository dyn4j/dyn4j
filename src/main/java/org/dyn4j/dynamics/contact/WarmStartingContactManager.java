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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dyn4j.collision.Collisions;
import org.dyn4j.collision.manifold.ManifoldPointId;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Capacity;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.geometry.Shiftable;

/**
 * Represents a {@link ContactManager} that performs warm starting of contacts
 * based on the previous iteration.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.2.0
 */
public class WarmStartingContactManager extends SimpleContactManager implements ContactManager, Shiftable {
	/** Another map that will be reused */
	Map<ContactConstraintId, ContactConstraint> constraints1 = null;
	
	/**
	 * Default constructor.
	 * @since 3.2.0
	 */
	public WarmStartingContactManager()  {
		// use the default capacity
		this(Capacity.DEFAULT_CAPACITY);
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * The initial capacity is used to help performance in the event that the developer
	 * knows the number of bodies the world will contain.  The {@link WarmStartingContactManager}
	 * will grow past the initial capacity if necessary.
	 * @param initialCapacity the estimated number of {@link Body}s
	 * @throws NullPointerException if initialCapacity is null
	 * @since 3.2.0
	 */
	public WarmStartingContactManager(Capacity initialCapacity)  {
		super(initialCapacity);
		// estimate the number of contact constraints
		int eSize = Collisions.getEstimatedCollisionPairs(initialCapacity.getBodyCount());
		this.constraints1 = new HashMap<ContactConstraintId, ContactConstraint>(eSize * 4 / 3 + 1, 0.75f);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.SimpleContactManager#updateAndNotify(java.util.List, org.dyn4j.dynamics.Settings)
	 */
	public void updateAndNotify(List<ContactListener> listeners, Settings settings) {
		// get the size of the list
		int size = this.constraintQueue.size();
		int lsize = listeners != null ? listeners.size() : 0;
		
		// get the warm start distance from the settings
		double warmStartDistanceSquared = settings.getWarmStartDistanceSquared();
		
		// create a new map for the new contacts constraints
		Map<ContactConstraintId, ContactConstraint> newMap = this.constraints1;
		
		// loop over the new contact constraints
		// and attempt to persist contacts
		for (int i = 0; i < size; i++) {
			// get the new contact constraint
			ContactConstraint newContactConstraint = this.constraintQueue.get(i);
			// define the old contact constraint
			ContactConstraint oldContactConstraint = null;
			
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
			
			// get the old contact constraint
			// doing a remove here will ensure that the remaining contact
			// constraints in the map will be contacts that need to be notified of
			// removal
			oldContactConstraint = this.constraints.remove(newContactConstraint.id);
			
			// check if the contact constraint exists
			if (oldContactConstraint != null) {
				List<Contact> ocontacts = oldContactConstraint.contacts;
				int osize = ocontacts.size();
				// create an array for removed contacts
				boolean[] persisted = new boolean[osize];
				// warm start the constraint
				for (int j = nsize - 1; j >= 0; j--) {
					// get the new contact
					Contact newContact = contacts.get(j);
					// loop over the old contacts
					boolean found = false;
					for (int k = 0; k < osize; k++) {
						// get the old contact
						Contact oldContact = ocontacts.get(k);
						// check if the id type is distance, if so perform a distance check using the warm start distance
						// else just compare the ids
						if ((newContact.id == ManifoldPointId.DISTANCE && newContact.p.distanceSquared(oldContact.p) <= warmStartDistanceSquared) 
						  || newContact.id.equals(oldContact.id)) {
							// warm start by setting the new contact constraint
							// accumulated impulses to the old contact constraint
							newContact.jn = oldContact.jn;
							newContact.jt = oldContact.jt;
							// notify of a persisted contact
							PersistedContactPoint point = new PersistedContactPoint(
									new ContactPointId(newContactConstraint.id, newContact.id),
									newContactConstraint.getBody1(),
									newContactConstraint.fixture1,
									newContactConstraint.getBody2(),
									newContactConstraint.fixture2,
									//true,
									newContact.p,
									newContactConstraint.normal,
									newContact.depth,
									oldContact.p,
									oldContactConstraint.normal,
									oldContact.depth);
							// call the listeners and set the enabled flag to the result
							boolean allow = true;
							for (int l = 0; l < lsize; l++) {
								ContactListener listener = listeners.get(l);
								if (!listener.persist(point)) {
									allow = false;
								}
							}
							if (!allow) {
								contacts.remove(j);
							}
							// flag that the contact was persisted
							persisted[k] = true;
							found = true;
							break;
						}
					}
					// check for persistence, if it wasn't persisted its a new contact
					if (!found) {
						// notify of new contact (begin of contact)
						ContactPoint point = new ContactPoint(
								new ContactPointId(newContactConstraint.id, newContact.id),
								newContactConstraint.getBody1(),
								newContactConstraint.fixture1,
								newContactConstraint.getBody2(),
								newContactConstraint.fixture2,
								//false,
								newContact.p,
								newContactConstraint.normal,
								newContact.depth);
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
				}
				
				// check for removed contacts
				// if the contact was not persisted then it was removed
				int rsize = persisted.length;
				for (int j = 0; j < rsize; j++) {
					// check the boolean array
					if (!persisted[j]) {
						// get the contact
						Contact contact = ocontacts.get(j);
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
						// call the listeners
						for (int l = 0; l < lsize; l++) {
							ContactListener listener = listeners.get(l);
							listener.end(point);
						}
					}
				}
			} else {
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
							//false,
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
			}
			// add the contact constraint to the map
			if (newContactConstraint.contacts.size() > 0) {
				newMap.put(newContactConstraint.id, newContactConstraint);
			}
		}
		
		// check the map and its size
		if (!this.constraints.isEmpty()) {
			// now loop over the remaining contacts in the map to notify of any removed contacts
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
		}
		
		// finally overwrite the contact constraint map with the new map
		if (size > 0) {
			// swap the maps so we can reuse
			this.constraints.clear();
			this.constraints1 = this.constraints;
			this.constraints = newMap;
		} else {
			// if no contact constraints exist, just clear the old map
			this.constraints.clear();
		}
		
		this.constraintQueue.clear();
	}
}
