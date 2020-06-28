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
package org.dyn4j.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.joint.Joint;

/**
 * Represents a node in the constraint graph.
 * <p>
 * Each node is a {@link PhysicsBody} with the {@link ContactConstraint}s and
 * {@link Joint}s being the edges to the other nodes.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public final class ConstraintGraphNode<T extends PhysicsBody> {
	// node data
	
	/** The body */
	protected final T body;
	
	// edges
	
	/** The contact constraints connecting this body and other bodies */
	protected final List<ContactConstraint<T>> contactConstraints;
	
	/** The joints connecting this body and other bodies */
	protected final List<Joint<T>> joints;

	/** An unmodifiable view of the contacts list */
	protected final List<ContactConstraint<T>> contactConstraintsUnmodifiable;
	
	/** An unmodifiable view of the joints list */
	protected final List<Joint<T>> jointsUnmodifiable;
	
	/**
	 * Minimal constructor.
	 * @param body the body
	 */
	public ConstraintGraphNode(T body) {
		this.body = body;
		this.contactConstraints = new ArrayList<ContactConstraint<T>>();
		this.joints = new ArrayList<Joint<T>>();
		
		this.jointsUnmodifiable = Collections.unmodifiableList(this.joints);
		this.contactConstraintsUnmodifiable = Collections.unmodifiableList(this.contactConstraints);
	}
	
	/**
	 * Returns the body at this node.
	 * @return T
	 */
	public T getBody() {
		return this.body;
	}
	
	/**
	 * Returns the list of joints this body is connected with.
	 * @return List&lt;{@link Joint}&lt;T&gt;&gt;
	 */
	public List<Joint<T>> getJoints() {
		return this.jointsUnmodifiable;
	}
	
	/**
	 * Returns the list of contact constraints this body is connected with.
	 * @return List&lt;{@link ContactConstraint}&lt;T&gt;&gt;
	 */
	public List<ContactConstraint<T>> getContactConstraints() {
		return this.contactConstraintsUnmodifiable;
	}
}
