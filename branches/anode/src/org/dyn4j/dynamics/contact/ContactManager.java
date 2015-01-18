/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Maintains {@link ContactConstraint}s between {@link Body}s.
 * <p>
 * This class performs the {@link ContactConstraint} warm starting and manages contact
 * listening.
 * @author William Bittle
 * @version 3.1.11
 * @since 1.0.0
 */
public class ContactManager implements Shiftable {
	/** The world this contact manager belongs to */
	protected World world;
	
	/** Map for fast look up of  {@link ContactConstraint}s */
	protected Map<ContactConstraintId, ContactConstraint> map;

	/** The current list of contact constraints */
	protected List<ContactConstraint> list;
	
	/** The list of contact listeners (this is reassigned each time {@link #updateContacts()} is called) */
	protected List<ContactListener> listeners;
	
	/**
	 * Optional constructor.
	 * @param world the {@link World} this contact manager belongs to
	 * @throws NullPointerException if world is null
	 * @since 3.0.3
	 */
	public ContactManager(World world)  {
		// use the default capacity
		this(world, Capacity.DEFAULT_CAPACITY);
	}
	
	/**
	 * Full constructor.
	 * <p>
	 * The initial capacity is used to help performance in the event that the developer
	 * knows the number of bodies the world will contain.  The {@link ContactManager}
	 * will grow past the initial capacity if necessary.
	 * @param world the {@link World} this contact manager belongs to
	 * @param initialCapacity the estimated number of {@link Body}s
	 * @throws NullPointerException if world or initialCapacity is null
	 * @since 3.1.1
	 */
	public ContactManager(World world, Capacity initialCapacity)  {
		// check for null world
		if (world == null) throw new NullPointerException(Messages.getString("dynamics.nullWorld"));
		// check for null capacity
		if (initialCapacity == null) throw new NullPointerException(Messages.getString("dynamics.nullCapacity"));
		this.world = world;
		// estimate the number of contact constraints
		int eSize = Collisions.getEstimatedCollisionPairs(initialCapacity.getBodyCount());
		// initialize the members
		// 0.75 = 3/4, we can garuantee that the hashmap will not need to be rehashed
		// if we take capacity / load factor
		// the default load factor is 0.75 according to the javadocs, but lets assign it to be sure
		this.map = new HashMap<ContactConstraintId, ContactConstraint>(eSize * 4 / 3 + 1, 0.75f);
		this.list = new ArrayList<ContactConstraint>(eSize);
		this.listeners = null;
	}
	
	/**
	 * Adds a {@link ContactConstraint} to the contact manager.
	 * @param contactConstraint the {@link ContactConstraint}
	 */
	public void add(ContactConstraint contactConstraint) {
		this.list.add(contactConstraint);
	}
	
	/**
	 * Removes a {@link ContactConstraint} from the contact manager.
	 * <p>
	 * This method does not notify the {@link ContactListener}.
	 * @param contactConstraint the {@link ContactConstraint}
	 * @return boolean true if the contact was removed
	 * @since 3.1.1
	 */
	public boolean remove(ContactConstraint contactConstraint) {
		// remove the contact from the cache
		return this.map.remove(contactConstraint.id) != null;
	}
	
	/**
	 * Clears the list of {@link ContactConstraint}s.
	 */
	public void clear() {
		// only clear the list
		this.list.clear();
	}
	
	/**
	 * Resets the contact manager, removing all {@link ContactConstraint}s
	 * from the warm starting cache and {@link ContactConstraint} list.
	 */
	public void reset() {
		// clear the list
		this.list.clear();
		// clear the current contact constraints warm start cache
		this.map.clear();
	}
	
	/**
	 * Shifts stored contacts by the given coordinate shift.
	 * <p>
	 * Typically this method should not be called directly.  Instead 
	 * use the {@link World#shift(Vector2)} method to move the 
	 * entire world.
	 * @param shift the distance to shift along the x and y axes
	 * @since 3.1.0
	 */
	public void shift(Vector2 shift) {
		// update all the contacts
		Iterator<ContactConstraint> it = this.map.values().iterator();
		while (it.hasNext()) {
			ContactConstraint cc = it.next();
			cc.shift(shift);
		}
	}
	
	/**
	 * Updates the contact manager with the new contacts, performs warm starting, and notifies 
	 * of any contact events.
	 * <p>
	 * Warm starts the {@link ContactConstraint}s using the previous {@link ContactConstraint}s
	 * accumulated impulses if available given their {@link ManifoldPointId}s.
	 * <p>
	 * This method will notify using the {@link ContactListener} of any contact events excluding
	 * the {@link ContactListener#preSolve(ContactPoint)} and {@link ContactListener#postSolve(SolvedContactPoint)}
	 * methods.
	 * @see ContactListener
	 * @since 1.0.2
	 */
	public void updateContacts() {
		// get the size of the list
		int size = this.list.size();
		
		// re-assign listeners
		this.listeners = this.world.getListeners(ContactListener.class);
		
		Settings settings = this.world.getSettings();
		// get the warm start distance from the settings
		double warmStartDistanceSquared = settings.getWarmStartDistanceSquared();
		
		// create a new map for the new contacts constraints
		Map<ContactConstraintId, ContactConstraint> newMap = null;
		
		// check if any new contact constraints were found
		if (size > 0) {
			// if so then create a new map to contain the new contacts
			// 0.75 = 3/4, we can garuantee that the hashmap will not need to be rehashed
			// if we take capacity / load factor
			// the default load factor is 0.75 according to the javadocs, but lets assign it to be sure
			newMap = new HashMap<ContactConstraintId, ContactConstraint>(size * 4 / 3 + 1, 0.75f);
		}
		
		// loop over the new contact constraints
		// and attempt to persist contacts
		for (int i = 0; i < size; i++) {
			// get the new contact constraint
			ContactConstraint newContactConstraint = this.list.get(i);
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
							false,
							contact.p,
							newContactConstraint.normal,
							contact.depth);
					// call the listeners
					for (ContactListener cl : this.listeners) {
						cl.sensed(point);
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
			oldContactConstraint = this.map.remove(newContactConstraint.id);
			
			// check if the contact constraint exists
			if (oldContactConstraint != null) {
				List<Contact> ocontacts = oldContactConstraint.contacts;
				int osize = ocontacts.size();
				// create an array for removed contacts
				boolean[] persisted = new boolean[osize];
				// warm start the constraint
				for (int j = 0; j < nsize; j++) {
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
									true,
									newContact.p,
									newContactConstraint.normal,
									newContact.depth,
									oldContact.p,
									oldContactConstraint.normal,
									oldContact.depth);
							// call the listeners and set the enabled flag to the result
							boolean allow = true;
							for (ContactListener cl : this.listeners) {
								if (!cl.persist(point)) {
									allow = false;
								}
							}
							newContact.enabled = allow;
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
								false,
								newContact.p,
								newContactConstraint.normal,
								newContact.depth);
						// call the listeners and set the enabled flag to the result
						boolean allow = true;
						for (ContactListener cl : this.listeners) {
							if (!cl.begin(point)) {
								allow = false;
							}
						}
						newContact.enabled = allow;
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
								false,
								contact.p,
								newContactConstraint.normal,
								contact.depth);
						// call the listeners
						for (ContactListener cl : this.listeners) {
							cl.end(point);
						}
					}
				}
			} else {
				// notify new contacts
				// if the old contact point was not found notify of the new contact
				for (int j = 0; j < nsize; j++) {
					// get the contact
					Contact contact = contacts.get(j);
					// notify of new contact (begin of contact)
					ContactPoint point = new ContactPoint(
							new ContactPointId(newContactConstraint.id, contact.id),
							newContactConstraint.getBody1(),
							newContactConstraint.fixture1,
							newContactConstraint.getBody2(),
							newContactConstraint.fixture2,
							false,
							contact.p,
							newContactConstraint.normal,
							contact.depth);
					// call the listeners and set the enabled flag to the result
					boolean allow = true;
					for (ContactListener cl : this.listeners) {
						if (!cl.begin(point)) {
							allow = false;
						}
					}
					contact.enabled = allow;
				}
			}
			// add the contact constraint to the map
			newMap.put(newContactConstraint.id, newContactConstraint);
		}
		
		// check the map and its size
		if (!this.map.isEmpty()) {
			// now loop over the remaining contacts in the map to notify of any removed contacts
			Iterator<ContactConstraint> icc = this.map.values().iterator();
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
							false,
							contact.p,
							contactConstraint.normal,
							contact.depth);
					// call the listeners
					for (ContactListener cl : this.listeners) {
						cl.end(point);
					}
				}
			}
		}
		
		// finally overwrite the contact constraint map with the new map
		if (size > 0) {
			this.map = newMap;
		} else {
			// if no contact constraints exist, just clear the old map
			this.map.clear();
		}
	}
	
	/**
	 * Called before the contact constraints are solved.
	 */
	public void preSolveNotify() {
		int size = this.list.size();
		
		// loop through the list of contacts that were solved
		for (int i = 0; i < size; i++) {
			// get the contact constraint
			ContactConstraint contactConstraint = this.list.get(i);
			// sensed contacts are not solved
			if (contactConstraint.sensor) continue;
			// loop over the contacts
			int rsize = contactConstraint.contacts.size();
			for (int j = 0; j < rsize; j++) {
				// get the contact
				Contact contact = contactConstraint.contacts.get(j);
				// notify of the contact that will be solved
				ContactPoint point = new ContactPoint(
						new ContactPointId(contactConstraint.id, contact.id),
						contactConstraint.getBody1(),
						contactConstraint.fixture1,
						contactConstraint.getBody2(),
						contactConstraint.fixture2,
						false,
						contact.p,
						contactConstraint.normal,
						contact.depth);
				// call the listeners and set the enabled flag to the result
				boolean allow = true;
				for (ContactListener cl : this.listeners) {
					if (!cl.preSolve(point)) {
						allow = false;
					}
				}
				contact.enabled = allow;
			}
		}
	}
	
	/**
	 * Called after the contact constraints have been solved.
	 */
	public void postSolveNotify() {
		int size = this.list.size();
		
		// loop through the list of contacts that were solved
		for (int i = 0; i < size; i++) {
			// get the contact constraint
			ContactConstraint contactConstraint = this.list.get(i);
			// sensed contacts are not solved
			if (contactConstraint.sensor) continue;
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
						false,
						contact.p,
						contactConstraint.normal,
						contact.depth,
						contact.jn,
						contact.jt);
				// notify of them being solved
				for (ContactListener cl : this.listeners) {
					cl.postSolve(point);
				}
			}
		}
	}
	
	/**
	 * Returns true if there are no contacts in this {@link ContactManager}'s list.
	 * <p>
	 * Contacts are added to this contact manager via the {@link #add(ContactConstraint)}
	 * method.  When the {@link #updateContacts()} method is called these contacts are updated
	 * with the cached {@link ContactConstraint}'s impulses to warm start the solution.
	 * @return boolean
	 * @since 3.1.1
	 */
	public boolean isListEmpty() {
		return this.list.isEmpty();
	}
	
	/**
	 * Returns true if the {@link ContactConstraint} warm starting cache is empty.
	 * <p>
	 * The warm starting cache contains the previous time step's {@link ContactConstraint}s and
	 * their computed impulses.  These impulses from the last time step are used to warm
	 * start the new contacts added via the {@link #add(ContactConstraint)} method.  Warm
	 * starting of the {@link ContactConstraint}s allow the global solution to converge quicker
	 * and eliminate jitter.
	 * @return boolean
	 * @since 3.1.1
	 */
	public boolean isCacheEmpty() {
		return this.map.isEmpty();
	}
	
	/**
	 * Returns true if there are no contacts in this contact manager.
	 * @return boolean
	 * @since 3.0.3
	 */
	@Deprecated
	public boolean isEmpty() {
		return this.map.isEmpty();
	}
}
