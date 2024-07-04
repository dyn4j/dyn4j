/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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
import org.dyn4j.Unsafe;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.exception.InvalidIndexException;
import org.dyn4j.geometry.Shiftable;

/**
 * Represents an abstract implementation of constrained motion with a single 
 * {@link PhysicsBody}.
 * @author William Bittle
 * @version 6.0.0
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
	
	/**
	 * Copy constructor that uses the given bodies instead of the
	 * bodies on the joint being copied.
	 * @param joint the joint to copy; null if the joint body should be copied
	 * @param body the body
	 * @since 6.0.0
	 */
	protected AbstractSingleBodyJoint(AbstractSingleBodyJoint<T> joint, T body) {
		super(joint, body != null ? Arrays.asList(body) : Arrays.asList(Unsafe.copy(joint.body)));
		this.body = this.bodies.get(0);
	}
	
	/**
	 * Returns a deep copy of this joint, but uses the given bodies
	 * instead of the bodies associated to this joint.
	 * <p>
	 * Imagine the following scenario:
	 * <p style="white-space: pre;"> Body b1 = ...;
	 * Joint j1 = new Joint(b1);
	 * Joint j2 = new Joint(b1);
	 * Joint j1copy = j1.copy();
	 * Joint j2copy = j2.copy();</p>
	 * In this scenario, <code>j1</code> and <code>j2</code> have copied their related body, but this
	 * means that we now have two copies of <code>b1</code>, since it was referenced by both
	 * <code>j1</code> and <code>j2</code>.  Instead, this method allows you to do this:
	 * <p style="white-space: pre;"> Body b1 = ...;
	 * Joint j1 = new Joint(b1);
	 * Joint j2 = new Joint(b1);
	 * Body b1copy = b1.copy();
	 * Joint j1copy = j1.copy(b1copy);
	 * Joint j2copy = j2.copy(b1copy);</p>
	 * In this modified code, you manually copy <code>b1</code> first.
	 * Then you supply <code>b1copy</code> to the copy method of the joints so that 
	 * <code>b1</code> isn't copied twice.
	 * @param body the body
	 * @return {@link AbstractSingleBodyJoint}
	 * @since 6.0.0
	 */
	public abstract AbstractSingleBodyJoint<T> copy(T body);
	
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
		throw new InvalidIndexException(i);
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
