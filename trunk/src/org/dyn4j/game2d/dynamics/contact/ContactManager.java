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
 */
public class ContactManager {
	/** Map for fast look up of  {@link ContactConstraint}s */
	protected Map<ContactConstraintId, ContactConstraint> map;

	/** The current list of contact constraints */
	protected List<ContactConstraint> list;
	
	/** The list of sensed contacts for notification */
	protected List<SensedContactPoint> sensed;
	
	/** The contact listener */
	protected ContactListener listener;
	
	/**
	 * Full constructor.
	 * @param listener the {@link ContactListener}
	 */
	public ContactManager(ContactListener listener) {
		if (listener == null) throw new NullPointerException("The contact listener cannot be null.");
		this.map = new HashMap<ContactConstraintId, ContactConstraint>();
		this.list = new ArrayList<ContactConstraint>();
		this.sensed = new ArrayList<SensedContactPoint>();
		this.listener = listener;
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
	 * Adds a sensed contact to the contact manager.
	 * @param sensedContact the sensed contact
	 */
	public void add(SensedContactPoint sensedContact) {
		this.sensed.add(sensedContact);
	}
	
	/**
	 * Clears the list of {@link ContactConstraint}s.
	 */
	public void clear() {
		this.list.clear();
		this.sensed.clear();
	}
	
	/**
	 * Resets the contact manager, removing all {@link ContactConstraint}s.
	 */
	public void reset() {
		// clear the current contact constraints
		this.map.clear();
	}
	
	/**
	 * Warm start the {@link ContactConstraint}s using the previous {@link ContactConstraint}s
	 * accumulated impulses.
	 * <p>
	 * This method will notify using the {@link ContactListener} of any added, persisted, or
	 * removed {@link Contact}s.
	 */
	public void warm() {
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
							this.listener.persist(point);
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
						this.listener.begin(point);
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
						this.listener.end(point);
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
					this.listener.begin(point);
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
					// notify via the listener
					this.listener.end(point);
				}
			}
		}
		// finally overwrite the contact constraint map with the new map
		this.map = newMap;
	}
	
	/**
	 * Called before the contact constraints are solved.
	 * <p>
	 * This method notifies of both sensed and normal contacts.
	 * <p>
	 * Sensed contacts are not solved.
	 */
	public void preSolveNotify() {
		// loop through the list of contacts that were solved
		int size = this.list.size();
		for (int i = 0; i < size; i++) {
			// get the contact constraint
			ContactConstraint contactConstraint = this.list.get(i);
			// loop over the contacts
			int rsize = contactConstraint.contacts.length;
			for (int j = 0; j < rsize; j++) {
				// get the contact
				Contact contact = contactConstraint.contacts[j];
				// set the contact point values
				ContactPoint point = new ContactPoint();
				point.normal = contactConstraint.normal;
				point.point = contact.p;
				point.depth = contact.depth;
				point.body1 = contactConstraint.getBody1();
				point.body2 = contactConstraint.getBody2();
				point.fixture1 = contactConstraint.getFixture1();
				point.fixture2 = contactConstraint.getFixture2();
				// notify of them being solved
				this.listener.preSolve(point);
			}
		}
		
		// notify of sensed contacts
		size = this.sensed.size();
		for (int i = 0; i < size; i++) {
			// notify of the sensed contact
			this.listener.sensed(this.sensed.get(i));
		}
	}
	
	/**
	 * Called after the contact constraints have been solved.
	 */
	public void postSolveNotify() {
		// loop through the list of contacts that were solved
		int size = this.list.size();
		for (int i = 0; i < size; i++) {
			// get the contact constraint
			ContactConstraint contactConstraint = this.list.get(i);
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
				this.listener.postSolve(point);
			}
		}
	}
	
	/**
	 * Sets the {@link ContactListener}.
	 * @param listener the {@link ContactListener}
	 */
	public void setContactListener(ContactListener listener) {
		if (listener == null) throw new NullPointerException("The contact listener cannot be null.");
		this.listener = listener;
	}
	
	/**
	 * Returns the {@link ContactListener}.
	 * @return {@link ContactListener}
	 */
	public ContactListener getContactListener() {
		return this.listener;
	}
}
