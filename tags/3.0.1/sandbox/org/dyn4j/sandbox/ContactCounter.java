package org.dyn4j.sandbox;

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
 * @version 1.0.0
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
		this.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.StepListener#preUpdateContacts(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.World)
	 */
	@Override
	public void updatePerformed(Step step, World world) {
		this.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.StepListener#end(org.dyn4j.dynamics.Step, org.dyn4j.dynamics.World)
	 */
	@Override
	public void end(Step step, World world) {}
	
	/**
	 * Clears the state of the contact counter.
	 * @since 3.0.1
	 */
	private void clear() {
		// reset the values
		this.sensed = 0;
		this.added = 0;
		this.persisted = 0;
		this.removed = 0;
		this.solved = 0;
		this.contacts.clear();
	}
	
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
