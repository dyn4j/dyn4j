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

import java.util.List;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.geometry.Shiftable;

/**
 * Maintains {@link ContactConstraint}s between {@link Body}s and notifies {@link ContactListener}s
 * of various events related to the life-cycle of a contact.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.2.0
 */
public interface ContactManager extends Shiftable {
	// preparation stage
	
	/**
	 * Queues a new {@link ContactConstraint} to be added to this {@link ContactManager}.
	 * <p>
	 * The {@link #updateAndNotify(List, Settings)} method should be called after all {@link ContactConstraint}s
	 * have been queued.
	 * @param constraint the {@link ContactConstraint}
	 */
	public void queue(ContactConstraint constraint);

	// notification stage
	
	/**
	 * Updates this {@link ContactManager} with the queued {@link ContactConstraint}s and notifying
	 * the given {@link ContactListener}s of the respective events.
	 * <p>
	 * This method does not notify the {@link ContactListener#preSolve(ContactPoint)} or
	 * {@link ContactListener#postSolve(SolvedContactPoint)} events.
	 * <p>
	 * If any {@link ContactListener} method returns false, the contact will not continue to the
	 * next stage.  In the event that all the contacts of a {@link ContactConstraint} do not
	 * continue to the next stage, the {@link ContactConstraint} itself will not continue.
	 * @param listeners the {@link ContactListener} to notify
	 * @param settings the world {@link Settings}
	 * @see ContactListener
	 */
	public void updateAndNotify(List<ContactListener> listeners, Settings settings);

	/**
	 * Notifies the given {@link ContactListener}s of the pre-solve event for all 
	 * {@link ContactConstraint}s that reached this stage.
	 * <p>
	 * If any {@link ContactListener} method returns false, the contact will not continue to the
	 * next stage.  In the event that all the contacts of a {@link ContactConstraint} do not
	 * continue to the next stage, the {@link ContactConstraint} itself will not continue.
	 * @param listeners the {@link ContactListener} to notify
	 */
	public void preSolveNotify(List<ContactListener> listeners);
	
	/**
	 * Notifies the given {@link ContactListener}s of the post-solve event for all 
	 * {@link ContactConstraint}s that reached this stage.
	 * @param listeners the {@link ContactListener} to notify
	 */
	public void postSolveNotify(List<ContactListener> listeners);
	
	// post-notification stage
	
	/**
	 * Manually ends the contacts associated with the given {@link ContactConstraint}.
	 * <p>
	 * This method does not call the {@link ContactListener#end(ContactPoint)} method for
	 * the contacts in the given {@link ContactConstraint}.
	 * @param constraint the {@link ContactConstraint}
	 * @return true if the {@link ContactConstraint} was found
	 */
	public boolean end(ContactConstraint constraint);
	
	/**
	 * Clears the contact manager.
	 */
	public void clear();
}
