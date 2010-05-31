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
import org.dyn4j.game2d.dynamics.World;

/**
 * Maintains {@link ContactConstraint}s between {@link Body}s.
 * <p>
 * This class performs the {@link ContactConstraint} warm starting.
 * @author William Bittle
 */
public class ContactManager {
	/** Map for fast look up of  {@link ContactConstraint}s */
	protected Map<ContactConstraintId, ContactConstraint> map = null;

	/** The current list of contact constraints */
	protected List<ContactConstraint> list = null;
	
	/** The contact listener */
	protected ContactListener listener = null;
	
	/**
	 * Full constructor.
	 * @param listener the {@link ContactListener}
	 */
	public ContactManager(ContactListener listener) {
		this.list = new ArrayList<ContactConstraint>();
		this.listener = listener;
	}

	/**
	 * Adds a {@link ContactConstraint} to the contact manager.
	 * @param cc the {@link ContactConstraint}
	 */
	public void add(ContactConstraint cc) {
		this.list.add(cc);
	}
	
	/**
	 * Removes a {@link ContactConstraint} from the contact manager.
	 * <p>
	 * This method does not notify the {@link ContactListener}.
	 * @param cc the {@link ContactConstraint}
	 */
	public void remove(ContactConstraint cc) {
		// remove the contact from the cache
		this.map.remove(cc.id);
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
	 * Warm start the {@link ContactConstraint}s using the previous {@link ContactConstraint}s
	 * accumulated impulses.
	 * <p>
	 * This method will notify using the {@link ContactListener} of any added, persisted, or
	 * removed {@link Contact}s.
	 * <p>
	 * Use the {@link #solved()} method after solving the {@link ContactConstraint}s to
	 * notify using the {@link ContactListener} of the solved {@link Contact}s.
	 */
	public void warm() {
		// check the given list
		if (this.list == null || this.list.isEmpty()) {
			return;
		}

		// get the warm start distance from the settings
		double warmStartDistance = Settings.getInstance().getWarmStartDistance();
		// square it for comparison later
		warmStartDistance *= warmStartDistance;
		
		// create a new map for the new contacts constraints
		Map<ContactConstraintId, ContactConstraint> newMap = new HashMap<ContactConstraintId, ContactConstraint>(this.list.size() * 2);
		
		// create a contact point and a persisted contact point to be used by the listener
		ContactPoint cp = new ContactPoint();
		PersistedContactPoint pcp = new PersistedContactPoint();
		
		// loop over the new contact constraints
		int size = this.list.size();
		for (int i = 0; i < size; i++) {
			// get the new contact constraint
			ContactConstraint ncc = this.list.get(i);
			// define the old contact constraint
			ContactConstraint occ = null;
			
			// doing a remove here will ensure that the remaining contact
			// constraints in the map will be contacts that need to be notified of
			// removal
			if (this.map != null) {
				// get the old contact constraint
				occ = this.map.remove(ncc.id);
			}
			// set the body, shapes, and normal
			cp.normal = ncc.normal;
			cp.body1 = ncc.getBody1();
			cp.body2 = ncc.getBody2();
			cp.convex1 = ncc.c1;
			cp.convex2 = ncc.c2;
			// check if the contact constraint exists
			if (occ != null) {
				// create an array for removed contacts
				boolean[] persisted = new boolean[occ.contacts.length];
				// warm start the constraint
				int nsize = ncc.contacts.length;
				for (int j = 0; j < nsize; j++) {
					// get the new contact
					Contact n = ncc.contacts[j];
					// loop over the old contacts
					int osize = occ.contacts.length;
					boolean found = false;
					for (int k = 0; k < osize; k++) {
						// get the old contact
						Contact o = occ.contacts[k];
						// check if the id type is distance, if so perform a distance check using the warm start distance
						// else just compare the ids
						if ((n.id == ManifoldPointId.DISTANCE && n.p.distanceSquared(o.p) <= warmStartDistance) || n.id.equals(o.id)) {
							// warm start by setting the new contact constraint
							// accumulated impulses to the old contact constraint
							n.jn = o.jn;
							n.jt = o.jt;
							// set the contact point values
							pcp.normal = ncc.normal;
							pcp.point = n.p;
							pcp.depth = n.depth;
							pcp.oldNormal = occ.normal;
							pcp.oldPoint = o.p;
							pcp.oldDepth = o.depth;
							pcp.body1 = ncc.getBody1();
							pcp.body2 = ncc.getBody2();
							pcp.convex1 = ncc.c1;
							pcp.convex2 = ncc.c2;
							// notify of persisted contact
							this.listener.persist(pcp);
							// flag that the contact was persisted
							persisted[k] = true;
							found = true;
							break;
						}
					}
					// check for persistence
					if (!found) {
						// set the contact point values
						cp.point = n.p;
						cp.depth = n.depth;
						// notify of new contact
						this.listener.added(cp);
					}
				}
				// check for removed contacts
				int rsize = persisted.length;
				for (int j = 0; j < rsize; j++) {
					// check the boolean array
					if (!persisted[j]) {
						// get the contact
						Contact c = occ.contacts[j];
						// set the contact point values
						cp.point = c.p;
						cp.depth = c.depth;
						// if the contact was not persisted then it was removed
						this.listener.removed(cp);
					}
				}
			} else {
				// notify new contacts
				int nsize = ncc.contacts.length;
				for (int j = 0; j < nsize; j++) {
					// get the contact
					Contact c = ncc.contacts[j];
					// set the contact point values
					cp.point = c.p;
					cp.depth = c.depth;
					// if the old contact point was not found notify of the new contact
					this.listener.added(cp);
				}
			}
			// add the contact constraint to the map
			newMap.put(ncc.id, ncc);
		}
		
		// check the map and its size
		if (this.map != null && !this.map.isEmpty()) {
			// now loop over the remaining contacts in the map to notify of any removed contacts
			Iterator<ContactConstraint> icc = this.map.values().iterator();
			while (icc.hasNext()) {
				ContactConstraint cc = icc.next();
				// set the body, shapes, and normal
				cp.normal = cc.normal;
				cp.body1 = cc.getBody1();
				cp.body2 = cc.getBody2();
				cp.convex1 = cc.c1;
				cp.convex2 = cc.c2;
				// loop over the contact points
				int rsize = cc.contacts.length;
				for (int i = 0; i < rsize; i++) {
					// get the contact
					Contact c = cc.contacts[i];
					// set the contact point values
					cp.point = c.p;
					cp.depth = c.depth;
					// notify via the listener
					this.listener.removed(cp);
				}
			}
		}
		// finally overwrite the contact constraint map with the new map
		this.map = newMap;
	}
	
	/**
	 * Calls the contact listener for all {@link ContactConstraint}s.
	 * <p>
	 * This method should be called after the {@link ContactConstraint}s have been solved.
	 * <p>
	 * This method must be called before the next {@link World} step.
	 */
	public void solved() {
		// create the solved contact point
		SolvedContactPoint scp = new SolvedContactPoint();
		// loop through the list of contacts that were solved
		if (this.list != null && !this.list.isEmpty()) {
			int size = this.list.size();
			for (int i = 0; i < size; i++) {
				// get the contact constraint
				ContactConstraint cc = this.list.get(i);
				// set the body, shapes, and normal
				scp.normal = cc.normal;
				scp.body1 = cc.getBody1();
				scp.body2 = cc.getBody2();
				scp.convex1 = cc.c1;
				scp.convex2 = cc.c2;
				// loop over the contacts
				int rsize = cc.contacts.length;
				for (int j = 0; j < rsize; j++) {
					// get the contact
					Contact c = cc.contacts[j];
					// set the contact point values
					scp.point = c.p;
					scp.depth = c.depth;
					scp.normalImpulse = c.jn;
					scp.tangentialImpulse = c.jt;
					scp.resting = (c.vb == 0.0);
					// notify of them being solved
					this.listener.solved(scp);
				}
			}
		}
	}
	
	/**
	 * Sets the {@link ContactListener}.
	 * @param listener the {@link ContactListener}
	 */
	public void setContactListener(ContactListener listener) {
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
