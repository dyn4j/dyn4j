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
package org.dyn4j.testbed;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.dynamics.Body;
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
 * @version 3.0.0
 * @since 1.0.0
 */
public class ContactCounter implements ContactListener, StepListener {
	/** The number of contacts between sensor {@link Body}s */
	private int sensed = 0;
	
	/** The number of new contacts */
	private int added = 0;
	
	/** The number of retained contacts */
	private int persisted = 0;
	
	/** The number of removed contacts */
	private int removed = 0;
	
	/** The number of solved contacts (or the total number of contacts) */
	private int solved = 0;
	
	/** The current contact points */
	private List<ContactPoint> contacts = new ArrayList<ContactPoint>();
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactListener#sensed(org.dyn4j.dynamics.contact.SensedContactPoint)
	 */
	@Override
	public void sensed(ContactPoint p) {
		this.sensed++;
		this.contacts.add(p);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactListener#begin(org.dyn4j.dynamics.contact.ContactPoint)
	 */
	@Override
	public boolean begin(ContactPoint c) {
		this.added++;
		this.contacts.add(c);
		// all contacts should be enabled
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactListener#persist(org.dyn4j.dynamics.contact.PersistedContactPoint)
	 */
	@Override
	public boolean persist(PersistedContactPoint c) {
		this.persisted++;
		// all contacts should be enabled
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.ContactListener#end(org.dyn4j.dynamics.contact.ContactPoint)
	 */
	@Override
	public boolean end(ContactPoint c) {
		this.removed++;
		// all contacts should be enabled
		return true;
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
		this.contacts.add(c);
		this.solved++;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.StepListener#begin()
	 */
	@Override
	public void begin(Step step, World world) {
		// reset the values
		this.sensed = 0;
		this.added = 0;
		this.persisted = 0;
		this.removed = 0;
		this.solved = 0;
		this.contacts.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.StepListener#end(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.World)
	 */
	@Override
	public void end(Step step, World world) {}

	/**
	 * Returns the number of new contacts.
	 * @return int the number of new contacts
	 */
	public int getAdded() {
		return added;
	}

	/**
	 * Returns the number of contacts retained from the last simulation step.
	 * @return int the number of retained contacts
	 */
	public int getPersisted() {
		return persisted;
	}

	/**
	 * Returns the number of removed contacts.
	 * @return int the number of removed contacts
	 */
	public int getRemoved() {
		return removed;
	}
	
	/**
	 * Returns the number of sensed contacts.
	 * @return int the number of sensed contacts
	 * @since 1.1.0
	 */
	public int getSensed() {
		return sensed;
	}
	
	/**
	 * Returns the number of solved contacts.
	 * <p>
	 * This is also the number of total contacts.
	 * @return int the number of solved contacts.
	 */
	public int getSolved() {
		return this.solved;
	}
	
	/**
	 * Returns the list of contact points.
	 * @return List&lt;{@link Vector2}&gt;
	 */
	public List<ContactPoint> getContacts() {
		return this.contacts;
	}
}
