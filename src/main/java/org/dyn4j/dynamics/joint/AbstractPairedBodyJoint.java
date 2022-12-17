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
import org.dyn4j.resources.Messages;

/**
 * Represents an abstract implementation of constrained motion between two 
 * {@link PhysicsBody}s.
 * @author William Bittle
 * @version 5.0.0
 * @since 5.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public abstract class AbstractPairedBodyJoint<T extends PhysicsBody> extends AbstractJoint<T> implements PairedBodyJoint<T>, Joint<T>, Shiftable, DataContainer, Ownable {
	/** The first linked body */
	protected final T body1;
	
	/** The second linked body */
	protected final T body2;
	
	/**
	 * Optional constructor.
	 * <p>
	 * Assumes that the joined bodies do not participate 
	 * in collision detection and resolution.
	 * @param body1 the first {@link PhysicsBody}
	 * @param body2 the second {@link PhysicsBody}
	 * @throws NullPointerException if body1 or body2 is null
	 * @throws IllegalArgumentException if body1 and body2 are the same object reference
	 */
	public AbstractPairedBodyJoint(T body1, T body2) {
		super(Arrays.asList(body1, body2));
		
		// verify the bodies are not the same instance
		if (body1 == body2) throw new IllegalArgumentException(Messages.getString("dynamics.joint.sameBody"));
		
		this.body1 = body1;
		this.body2 = body2;
	}
	
	/**
	 * Returns the reduced mass of this pair of bodies.
	 * <p>
	 * The reduced mass is used to solve spring/damper problems as a single body rather
	 * than as a system of two bodies.
	 * <p>
	 * <a href="https://en.wikipedia.org/wiki/Reduced_mass">https://en.wikipedia.org/wiki/Reduced_mass</a>
	 * @return double
	 */
	protected final double getReducedMass() {
		// https://en.wikipedia.org/wiki/Reduced_mass
		double m1 = this.body1.getMass().getMass();
		double m2 = this.body2.getMass().getMass();
		
		// compute the mass
		if (m1 > 0.0 && m2 > 0.0) {
			return m1 * m2 / (m1 + m2);
		} else if (m1 > 0.0) {
			return m1;
		} else {
			return m2;
		}
	}
	
	/**
	 * Returns the reduced inertia of this pair of bodies.
	 * @return double
	 * @see #getReducedMass()
	 */
	protected final double getReducedInertia() {
		double i1 = this.body1.getMass().getInertia();
		double i2 = this.body2.getMass().getInertia();
		
		// compute the mass
		if (i1 > 0.0 && i2 > 0.0) {
			return i1 * i2 / (i1 + i2);
		} else if (i1 > 0.0) {
			return i1;
		} else {
			return i2;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#isEnabled()
	 */
	public final boolean isEnabled() {
		return this.body1.isEnabled() && this.body2.isEnabled();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getBody(int)
	 */
	@Override
	public final T getBody(int i) {
		if (i == 0) return this.body1;
		if (i == 1) return this.body2;
		throw new IndexOutOfBoundsException();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#getBodyCount()
	 */
	@Override
	public final int getBodyCount() {
		return 2;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.Joint#isMember(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public final boolean isMember(CollisionBody<?> body) {
		if (body == this.body1 || body == this.body2) {
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.AbstractJoint#setCollisionAllowed(boolean)
	 */
	@Override
	public final void setCollisionAllowed(boolean flag) {
		if (this.collisionAllowed != flag) {
			this.body1.setAtRest(false);
			this.body2.setAtRest(false);
		}
		super.setCollisionAllowed(flag);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.BinaryJoint#getOtherBody(org.dyn4j.collision.CollisionBody)
	 */
	public final T getOtherBody(CollisionBody<?> body) {
		if (this.body1 == body) {
			return this.body2;
		} else if (this.body2 == body) {
			return this.body1;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.BinaryJoint#getBody1()
	 */
	public final T getBody1() {
		return this.body1;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.joint.BinaryJoint#getBody2()
	 */
	public final T getBody2() {
		return this.body2;
	}
}
