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
package org.dyn4j.sandbox;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.dynamics.Step;
import org.dyn4j.dynamics.StepListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;
import org.dyn4j.geometry.Vector2;

/**
 * Class to count the number of added, removed, and persisted contacts.
 * @author William Bittle
 * @version 1.0.4
 * @since 1.0.0
 */
public class ContactCounter implements ContactListener, StepListener {
	/** Array for counting contact events */
	private int[] counters = new int[] {0, 0, 0, 0, 0};

	/** The current contact points */
	private List<ContactPoint> contacts = new ArrayList<ContactPoint>();
	
	/** Array for the last cached counters */
	private int[] cachedCounters = new int[] {0, 0, 0, 0, 0};
	
	/** The cached contact points */
	private List<ContactPoint> cachedContacts = new ArrayList<ContactPoint>();
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactListener#sensed(org.dyn4j.dynamics.contact.SensedContactPoint)
	 */
	@Override
	public void sensed(ContactPoint p) {
		this.counters[0]++;
		this.contacts.add(p);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactListener#begin(org.dyn4j.dynamics.contact.ContactPoint)
	 */
	@Override
	public boolean begin(ContactPoint c) {
		this.counters[1]++;
		this.contacts.add(c);
		// all contacts should be enabled
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactListener#persist(org.dyn4j.dynamics.contact.PersistedContactPoint)
	 */
	@Override
	public boolean persist(PersistedContactPoint c) {
		this.counters[2]++;
		// all contacts should be enabled
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactListener#end(org.dyn4j.dynamics.contact.ContactPoint)
	 */
	@Override
	public void end(ContactPoint c) {
		this.counters[3]++;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactListener#preSolve(org.dyn4j.dynamics.contact.ContactPoint)
	 */
	@Override
	public boolean preSolve(ContactPoint point) {
		// all contacts should be enabled
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactListener#postSolve(org.dyn4j.dynamics.contact.SolvedContactPoint)
	 */
	@Override
	public void postSolve(SolvedContactPoint c) {
		this.counters[4]++;
		this.contacts.add(c);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.StepListener#begin()
	 */
	@Override
	public void begin(Step step, World world) {}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.StepListener#preUpdateContacts(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.World)
	 */
	@Override
	public void updatePerformed(Step step, World world) {
		this.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.StepListener#postSolve(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.World)
	 */
	@Override
	public void postSolve(Step step, World world) {}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.StepListener#end(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.World)
	 */
	@Override
	public void end(Step step, World world) {
		this.clear();
	}
	
	/**
	 * Clears the state of the contact counter.
	 */
	public void clear() {
		// reset the values
		for (int i = 0; i < this.counters.length; i++) {
			this.cachedCounters[i] = this.counters[i];
			this.counters[i] = 0;
		}
		this.cachedContacts.clear();
		this.cachedContacts.addAll(this.contacts);
		this.contacts.clear();
	}

	/**
	 * Returns the number of sensed contacts.
	 * @return int the number of sensed contacts
	 */
	public int getSensed() {
		return this.cachedCounters[0];
	}
	
	/**
	 * Returns the number of new contacts.
	 * @return int the number of new contacts
	 */
	public int getAdded() {
		return this.cachedCounters[1];
	}

	/**
	 * Returns the number of contacts retained from the last simulation step.
	 * @return int the number of retained contacts
	 */
	public int getPersisted() {
		return this.cachedCounters[2];
	}

	/**
	 * Returns the number of removed contacts.
	 * @return int the number of removed contacts
	 */
	public int getRemoved() {
		return this.cachedCounters[3];
	}
	
	/**
	 * Returns the number of solved contacts.
	 * <p>
	 * This is also the number of total contacts.
	 * @return int the number of solved contacts.
	 */
	public int getSolved() {
		return this.cachedCounters[4];
	}
	
	/**
	 * Returns the list of contact points.
	 * @return List&lt;{@link Vector2}&gt;
	 */
	public List<ContactPoint> getContacts() {
		return this.cachedContacts;
	}
}
