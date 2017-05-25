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
package org.dyn4j.dynamics;

import org.dyn4j.Listener;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.joint.Joint;

/**
 * Interface to listen for implicit destruction events.
 * <p>
 * These events can happen when, for example, a {@link Body} is removed from a {@link World}
 * where it was attached to a {@link Joint}.  The joint must be removed as well.  These methods
 * will be called when any such implicit destruction events happen.
 * @author William Bittle
 * @version 3.1.1
 * @since 1.0.0
 */
public interface DestructionListener extends Listener {
	/**
	 * Called when implicit destruction of a {@link Joint} has occurred.
	 * <p>
	 * Modification of the {@link World} is permitted during this method.
	 * @see World#removeBody(Body)
	 * @see World#removeAllBodiesAndJoints(boolean)
	 * @param joint the {@link Joint} that was destroyed
	 */
	public void destroyed(Joint joint);
	
	/**
	 * Called when implicit destruction of a {@link ContactConstraint} has occurred.
	 * <p>
	 * Modification of the {@link World} is not permitted during this method.
	 * @see World#removeBody(Body)
	 * @see World#removeAllBodiesAndJoints(boolean)
	 * @param contactPoint the {@link ContactPoint} that was destroyed
	 */
	public void destroyed(ContactPoint contactPoint);
	
	/**
	 * Called when implicit destruction of a {@link Body} has occurred.
	 * <p>
	 * Modification of the {@link World} is not permitted during this method.
	 * @see World#removeAllBodiesAndJoints(boolean)
	 * @param body the {@link Body} that was destroyed
	 * @since 1.0.2
	 */
	public void destroyed(Body body);
}
