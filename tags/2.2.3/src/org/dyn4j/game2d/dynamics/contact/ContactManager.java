/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d.dynamics.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dyn4j.game2d.collision.manifold.ManifoldPointId;
import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.dynamics.Settings;

/**
 * Maintains {@link ContactConstraint}s between {@link Body}s.
 * <p>
 * This class performs the {@link ContactConstraint} warm starting and manages contact
 * listening.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class ContactManager {
	/** Map for fast look up of  {@link ContactConstraint}s */
	protected Map<ContactConstraintId, ContactConstraint> map;

	/** The current list of contact constraints */
	protected List<ContactConstraint> list;
	
	/**
	 * Default constructor.
	 * @since 1.0.2
	 */
	public ContactManager()  {
		// initialize the members
		this.map = new HashMap<ContactConstraintId, ContactConstraint>();
		this.list = new ArrayList<ContactConstraint>();
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
	 */
	public void remove(ContactConstraint contactConstraint) {
		// remove the contact from the cache
		this.map.remove(contactConstraint.id);
	}
	
	/**
	 * Clears the list of {@link ContactConstraint}s.
	 */
	public void clear() {
		this.list.clear();
	}
	
	/**
	 * Resets the contact manager, removing all {@link ContactConstraint}s.
	 */
	public void reset() {
		// clear the current contact constraints
		this.map.clear();
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
	 * @param listener the {@link ContactListener} to use for event notification
	 * @since 1.0.2
	 */
	public void updateContacts(ContactListener listener) {
		// check the given list
		if (this.list.isEmpty()) {
			return;
		}

		// get the warm start distance from the settings
		double warmStartDistanceSquared = Settings.getInstance().getWarmStartDistanceSquared();
		
		// create a new map for the new contacts constraints
		Map<ContactConstraintId, ContactConstraint> newMap = new HashMap<ContactConstraintId, ContactConstraint>(this.list.size() * 2);
		
		// loop over the new contact constraints
		// and attempt to persist contacts
		int size = this.list.size();
		for (int i = 0; i < size; i++) {
			// get the new contact constraint
			ContactConstraint newContactConstraint = this.list.get(i);
			// define the old contact constraint
			ContactConstraint oldContactConstraint = null;
			
			// check if this contact constraint is a sensor
			if (newContactConstraint.isSensor()) {
				// notify of the sensed contacts
				Contact[] contacts = newContactConstraint.contacts;
				int psize = contacts.length;
				for (int j = 0; j < psize; j++) {
					// get the contact
					Contact contact = contacts[j];
					// notify of the sensed contact
					ContactPoint point = new ContactPoint();
					point.body1 = newContactConstraint.getBody1();
					point.body2 = newContactConstraint.getBody2();
					point.fixture1 = newContactConstraint.fixture1;
					point.fixture2 = newContactConstraint.fixture2;
					point.normal = newContactConstraint.normal;
					point.depth = contact.depth;
					point.point = contact.p;
					point.enabled = false;
					// call the listener method
					listener.sensed(point);
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
				// create an array for removed contacts
				boolean[] persisted = new boolean[oldContactConstraint.contacts.length];
				// warm start the constraint
				int nsize = newContactConstraint.contacts.length;
				for (int j = 0; j < nsize; j++) {
					// get the new contact
					Contact newContact = newContactConstraint.contacts[j];
					// loop over the old contacts
					int osize = oldContactConstraint.contacts.length;
					boolean found = false;
					for (int k = 0; k < osize; k++) {
						// get the old contact
						Contact oldContact = oldContactConstraint.contacts[k];
						// check if the id type is distance, if so perform a distance check using the warm start distance
						// else just compare the ids
						if ((newContact.id == ManifoldPointId.DISTANCE && newContact.p.distanceSquared(oldContact.p) <= warmStartDistanceSquared) || newContact.id.equals(oldContact.id)) {
							// warm start by setting the new contact constraint
							// accumulated impulses to the old contact constraint
							newContact.jn = oldContact.jn;
							newContact.jt = oldContact.jt;
							// notify of a persisted contact
							PersistedContactPoint point = new PersistedContactPoint();
							// set the values for the new point
							point.normal = newContactConstraint.normal;
							point.point = newContact.p;
							point.depth = newContact.depth;
							point.body1 = newContactConstraint.getBody1();
							point.body2 = newContactConstraint.getBody2();
							point.fixture1 = newContactConstraint.getFixture1();
							point.fixture2 = newContactConstraint.getFixture2();
							// set the values for the old point
							point.oldNormal = oldContactConstraint.normal;
							point.oldPoint = oldContact.p;
							point.oldDepth = oldContact.depth;
							// call the listener and set the enabled flag to the result
							newContact.enabled = listener.persist(point);
							// flag that the contact was persisted
							persisted[k] = true;
							found = true;
							break;
						}
					}
					// check for persistence
					if (!found) {
						// notify of new contact (begin of contact)
						ContactPoint point = new ContactPoint();
						// set the values
						point.normal = newContactConstraint.normal;
						point.point = newContact.p;
						point.depth = newContact.depth;
						point.body1 = newContactConstraint.getBody1();
						point.body2 = newContactConstraint.getBody2();
						point.fixture1 = newContactConstraint.getFixture1();
						point.fixture2 = newContactConstraint.getFixture2();
						// call the listener and set the enabled flag to the result
						newContact.enabled = listener.begin(point);
					}
				}
				
				// check for removed contacts
				// if the contact was not persisted then it was removed
				int rsize = persisted.length;
				for (int j = 0; j < rsize; j++) {
					// check the boolean array
					if (!persisted[j]) {
						// get the contact
						Contact contact = oldContactConstraint.contacts[j];
						// notify of new contact (begin of contact)
						ContactPoint point = new ContactPoint();
						// set the values
						point.normal = newContactConstraint.normal;
						point.point = contact.p;
						point.depth = contact.depth;
						point.body1 = newContactConstraint.getBody1();
						point.body2 = newContactConstraint.getBody2();
						point.fixture1 = newContactConstraint.getFixture1();
						point.fixture2 = newContactConstraint.getFixture2();
						// call the listener and set the enabled flag to the result
						contact.enabled = listener.end(point);
					}
				}
			} else {
				// notify new contacts
				// if the old contact point was not found notify of the new contact
				int nsize = newContactConstraint.contacts.length;
				for (int j = 0; j < nsize; j++) {
					// get the contact
					Contact contact = newContactConstraint.contacts[j];
					// notify of new contact (begin of contact)
					ContactPoint point = new ContactPoint();
					// set the values
					point.normal = newContactConstraint.normal;
					point.point = contact.p;
					point.depth = contact.depth;
					point.body1 = newContactConstraint.getBody1();
					point.body2 = newContactConstraint.getBody2();
					point.fixture1 = newContactConstraint.getFixture1();
					point.fixture2 = newContactConstraint.getFixture2();
					// call the listener and set the enabled flag to the result
					contact.enabled = listener.begin(point);
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
				int rsize = contactConstraint.contacts.length;
				for (int i = 0; i < rsize; i++) {
					// get the contact
					Contact contact = contactConstraint.contacts[i];
					// set the contact point values
					ContactPoint point = new ContactPoint();
					point.normal = contactConstraint.normal;
					point.point = contact.p;
					point.depth = contact.depth;
					point.body1 = contactConstraint.getBody1();
					point.body2 = contactConstraint.getBody2();
					point.fixture1 = contactConstraint.getFixture1();
					point.fixture2 = contactConstraint.getFixture2();
					// call the listener and set the enabled flag to the result
					contact.enabled = listener.end(point);
				}
			}
		}
		// finally overwrite the contact constraint map with the new map
		this.map = newMap;
	}
	
	/**
	 * Called before the contact constraints are solved.
	 * @param listener the {@link ContactListener} to use for event notification
	 */
	public void preSolveNotify(ContactListener listener) {
		// loop through the list of contacts that were solved
		int size = this.list.size();
		for (int i = 0; i < size; i++) {
			// get the contact constraint
			ContactConstraint contactConstraint = this.list.get(i);
			// sensed contacts are not solved
			if (contactConstraint.isSensor()) continue;
			// loop over the contacts
			int rsize = contactConstraint.contacts.length;
			for (int j = 0; j < rsize; j++) {
				// get the contact
				Contact contact = contactConstraint.contacts[j];
				// notify of the contact that will be solved
				ContactPoint point = new ContactPoint();
				point.normal = contactConstraint.normal;
				point.point = contact.p;
				point.depth = contact.depth;
				point.body1 = contactConstraint.getBody1();
				point.body2 = contactConstraint.getBody2();
				point.fixture1 = contactConstraint.getFixture1();
				point.fixture2 = contactConstraint.getFixture2();
				// call the listener and set the enabled flag to the result
				contact.enabled = listener.preSolve(point);
			}
		}
	}
	
	/**
	 * Called after the contact constraints have been solved.
	 * @param listener the {@link ContactListener} to use for event notification
	 */
	public void postSolveNotify(ContactListener listener) {
		// loop through the list of contacts that were solved
		int size = this.list.size();
		for (int i = 0; i < size; i++) {
			// get the contact constraint
			ContactConstraint contactConstraint = this.list.get(i);
			// sensed contacts are not solved
			if (contactConstraint.isSensor()) continue;
			// loop over the contacts
			int rsize = contactConstraint.contacts.length;
			for (int j = 0; j < rsize; j++) {
				// get the contact
				Contact contact = contactConstraint.contacts[j];
				// set the contact point values
				SolvedContactPoint point = new SolvedContactPoint();
				point.normal = contactConstraint.normal;
				point.point = contact.p;
				point.depth = contact.depth;
				point.body1 = contactConstraint.getBody1();
				point.body2 = contactConstraint.getBody2();
				point.fixture1 = contactConstraint.getFixture1();
				point.fixture2 = contactConstraint.getFixture2();
				// set the solved attributes
				point.normalImpulse = contact.jn;
				point.tangentialImpulse = contact.jt;
				// notify of them being solved
				listener.postSolve(point);
			}
		}
	}
}
