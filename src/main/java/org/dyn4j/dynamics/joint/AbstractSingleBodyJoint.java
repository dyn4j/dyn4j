/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.dynamics.joint;

import java.util.Arrays;

import org.dyn4j.DataContainer;
import org.dyn4j.Ownable;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.geometry.Shiftable;

/**
 * Represents an abstract implementation of constrained motion with a single 
 * {@link PhysicsBody}.
 * @author William Bittle
 * @version 5.0.0
 * @since 5.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public abstract class AbstractSingleBodyJoint<T extends PhysicsBody> extends AbstractJoint<T> implements SingleBodyJoint<T>, Joint<T>, Shiftable, DataContainer, Ownable {
	/** The body */
	protected final T body;
	
	/**
	 * Default constructor.
	 * @param body the body
	 * @throws NullPointerException if body is null
	 */
	public AbstractSingleBodyJoint(T body) {
		super(Arrays.asList(body));
		this.body = body;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.UniaryJoint#getBody()
	 */
	@Override
	public final T getBody() {
		return this.body;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getBodyCount()
	 */
	@Override
	public final int getBodyCount() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getBody(int)
	 */
	@Override
	public final T getBody(int i) {
		if (i == 0) return this.body;
		throw new IndexOutOfBoundsException();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#isMember(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public final boolean isMember(CollisionBody<?> body) {
		return body == this.body;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#isEnabled()
	 */
	@Override
	public final boolean isEnabled() {
		return this.body.isEnabled();
	}
}
