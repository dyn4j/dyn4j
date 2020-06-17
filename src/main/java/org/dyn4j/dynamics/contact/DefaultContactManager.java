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
package org.dyn4j.dynamics.contact;

import java.util.ArrayList;
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
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Represents the default contact manager that reports beginning, persisted, and
 * ending contacts and performs warm starting by default.
 * @author William Bittle
 * @version 4.0.0
 * @since 3.3.0
 * @deprecated Deprecated in 4.0.0. No longer needed.
 */
@Deprecated
@SuppressWarnings({"rawtypes"})
public class DefaultContactManager implements ContactManager, Shiftable {
	/** The contact constraint queue */
	private final List<ContactConstraint> constraintQueue;
	
	/** Map for fast look up of  {@link ContactConstraint}s */
	private Map<ContactConstraintId, ContactConstraint> constraints;
	
	/** Another map that will be reused */
	private Map<ContactConstraintId, ContactConstraint> constraints1;
	
	/** True if warm starting is enabled */
	private boolean warmStartingEnabled;
	
	/**
	 * Default constructor.
	 */
	public DefaultContactManager()  {
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
	 */
	public DefaultContactManager(Capacity initialCapacity)  {
		// check for null capacity
		if (initialCapacity == null) throw new NullPointerException(Messages.getString("dynamics.nullCapacity"));
		// estimate the number of contact constraints
		int eSize = Collisions.getEstimatedCollisionPairs(initialCapacity.getBodyCount());
		// initialize the members
		this.constraintQueue = new ArrayList<ContactConstraint>(eSize);
		// 0.75 = 3/4, we can garuantee that the hashmap will not need to be rehashed
		// if we take capacity / load factor
		// the default load factor is 0.75 according to the javadocs, but lets assign it to be sure
		this.constraints = new HashMap<ContactConstraintId, ContactConstraint>(eSize * 4 / 3 + 1, 0.75f);
		this.constraints1 = new HashMap<ContactConstraintId, ContactConstraint>(eSize * 4 / 3 + 1, 0.75f);
		// enabled by default
		this.warmStartingEnabled = true;
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
	 * @see org.dyn4j.dynamics.contact.SimpleContactManager#updateAndNotify(java.util.List, org.dyn4j.dynamics.Settings)
	 */
	@SuppressWarnings("unchecked")
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
			
			List<SolvableContact> contacts = newContactConstraint.contacts;
			int nsize = contacts.size();
			
			// get the old contact constraint
			// doing a remove here will ensure that the remaining contact
			// constraints in the map will be contacts that need to be notified of
			// removal
			oldContactConstraint = this.constraints.remove(newContactConstraint.id);
			
			// check if the contact constraint exists
			if (oldContactConstraint != null) {
				List<SolvableContact> ocontacts = oldContactConstraint.contacts;
				int osize = ocontacts.size();
				// create an array for removed contacts
				boolean[] persisted = new boolean[osize];
				// warm start the constraint
				for (int j = nsize - 1; j >= 0; j--) {
					// get the new contact
					SolvableContact newContact = contacts.get(j);
					// loop over the old contacts
					boolean found = false;
					for (int k = 0; k < osize; k++) {
						// get the old contact
						SolvableContact oldContact = ocontacts.get(k);
						// check if the id type is distance, if so perform a distance check using the warm start distance
						// else just compare the ids
						if ((newContact.id == ManifoldPointId.DISTANCE && newContact.p.distanceSquared(oldContact.p) <= warmStartDistanceSquared) 
						  || newContact.id.equals(oldContact.id)) {
							// warm start by setting the new contact constraint
							// accumulated impulses to the old contact constraint
							if (this.warmStartingEnabled) {
								newContact.jn = oldContact.jn;
								newContact.jt = oldContact.jt;
							}
							// notify of a persisted contact
							PersistedContactPoint point = new PersistedContactPoint(newContactConstraint, newContact, oldContactConstraint, oldContact);
							// call the listeners and set the enabled flag to the result
							boolean allow = true;
							for (int l = 0; l < lsize; l++) {
								ContactListener listener = listeners.get(l);
								if (!listener.persist(point)) {
									allow = false;
								}
							}
							if (!allow) {
								newContactConstraint.enabled = false;
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
						ContactPoint point = new ContactPoint(newContactConstraint, newContact);
						// call the listeners and set the enabled flag to the result
						boolean allow = true;
						for (int l = 0; l < lsize; l++) {
							ContactListener listener = listeners.get(l);
							if (!listener.begin(point)) {
								allow = false;
							}
						}
						if (!allow) {
							newContactConstraint.enabled = false;
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
						SolvableContact contact = ocontacts.get(j);
						// notify of new contact (begin of contact)
						ContactPoint point = new ContactPoint(newContactConstraint, contact);
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
					SolvableContact contact = contacts.get(j);
					// notify of new contact (begin of contact)
					ContactPoint point = new ContactPoint(newContactConstraint, contact);
					// call the listeners and set the enabled flag to the result
					boolean allow = true;
					for (int l = 0; l < lsize; l++) {
						ContactListener listener = listeners.get(l);
						if (!listener.begin(point)) {
							allow = false;
						}
					}
					if (!allow) {
						newContactConstraint.enabled = false;
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
					SolvableContact contact = (SolvableContact) contactConstraint.contacts.get(i);
					// set the contact point values
					ContactPoint point = new ContactPoint(contactConstraint, contact);
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactManager#preSolveNotify(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	public void preSolveNotify(List<ContactListener> listeners) {
		int lsize = listeners != null ? listeners.size() : 0;
		
		// loop through the list of contacts that were solved
		Iterator<ContactConstraint> itContactConstraints = this.constraints.values().iterator();
		while (itContactConstraints.hasNext()) {
			// get the contact constraint
			ContactConstraint contactConstraint = itContactConstraints.next();
			// don't report preSolve of disabled contact constraints
			if (!contactConstraint.enabled || contactConstraint.sensor) continue;
			// loop over the contacts
			int csize = contactConstraint.contacts.size();
			// iterate backwards so we can remove
			for (int j = csize - 1; j >= 0; j--) {
				// get the contact
				SolvableContact contact = (SolvableContact) contactConstraint.contacts.get(j);
				// notify of the contact that will be solved
				ContactPoint point = new ContactPoint(contactConstraint, contact);
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
			// don't report postSolve of disabled contact constraints
			if (!contactConstraint.enabled || contactConstraint.sensor) continue;
			// loop over the contacts
			int rsize = contactConstraint.contacts.size();
			for (int j = 0; j < rsize; j++) {
				// get the contact
				SolvableContact contact = (SolvableContact) contactConstraint.contacts.get(j);
				// set the contact point values
				SolvedContactPoint point = new SolvedContactPoint(contactConstraint, contact);
				// notify of them being solved
				for (int l = 0; l < lsize; l++) {
					ContactListener listener = listeners.get(l);
					listener.postSolve(point);
				}
			}
		}
	}
	
	/**
	 * Returns true if warm starting is enabled.
	 * @return boolean
	 */
	public boolean isWarmStartingEnabled() {
		return this.warmStartingEnabled;
	}
	
	/**
	 * Toggles warm starting.
	 * @param flag true if warm starting should be enabled
	 */
	public void setWarmStartingEnabled(boolean flag) {
		this.warmStartingEnabled = flag;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactManager#getContactCount()
	 */
	@Override
	public int getContactCount() {
		return this.constraints.size();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactManager#getQueueCount()
	 */
	@Override
	public int getQueueCount() {
		return this.constraintQueue.size();
	}
}
