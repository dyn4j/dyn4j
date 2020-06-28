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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.world.listener;

import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.joint.Joint;

/**
 * Convenience class for implementing the {@link DestructionListener} interface.
 * <p>
 * This class can be used to implement only the methods desired instead of all
 * the methods contained in the {@link DestructionListener} interface.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public class DestructionListenerAdapter<T extends PhysicsBody> implements DestructionListener<T> {
	/* (non-Javadoc)
	 * @see org.dyn4j.world.listener.DestructionListener#destroyed(org.dyn4j.dynamics.joint.Joint)
	 */
	@Override
	public void destroyed(Joint<T> joint) {}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.listener.DestructionListener#destroyed(org.dyn4j.dynamics.PhysicsBody)
	 */
	@Override
	public void destroyed(T body) {}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.listener.DestructionListener#destroyed(org.dyn4j.dynamics.contact.ContactConstraint)
	 */
	@Override
	public void destroyed(ContactConstraint<T> contactConstraint) {}

}
