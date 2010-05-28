/*
 * Copyright (c) 2010, William Bittle
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
package org.dyn4j.game2d.collision.broadphase;

import org.dyn4j.game2d.collision.Collidable;
import org.dyn4j.game2d.geometry.Convex;

/**
 * Represents a pair of {@link Collidable}s that have been detected as
 * colliding in the {@link BroadphaseDetector}.
 * @author William Bittle
 * @param <E> the object type; intended to be of type {@link Collidable} or {@link Convex}
 */
public class BroadphasePair<E> {
	/** The first object */
	protected E object1;
	
	/** The second object */
	protected E object2;
	
	/**
	 * Default constructor.
	 */
	public BroadphasePair() {}
	
	/**
	 * Full constructor.
	 * @param object1 the first object
	 * @param object2 the second object
	 */
	public BroadphasePair(E object1, E object2) {
		this.object1 = object1;
		this.object2 = object2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BROADPHASE_PAIR[").append(object1).append("|").append(object2).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the first object.
	 * @return E the first object
	 */
	public E getObject1() {
		return object1;
	}
	
	/**
	 * Returns the second object.
	 * @return E the second object
	 */
	public E getObject2() {
		return object2;
	}
	
	/**
	 * Sets the first object.
	 * @param object1 the first object
	 */
	public void setObject1(E object1) {
		this.object1 = object1;
	}
	
	/**
	 * Sets the second object.
	 * @param object2 the second object
	 */
	public void setObject2(E object2) {
		this.object2 = object2;
	}
}
