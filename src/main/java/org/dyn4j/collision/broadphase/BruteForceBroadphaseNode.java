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
package org.dyn4j.collision.broadphase;

import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Transform;

/**
 * Simple helper class that holds information for each item in the {@link BruteForceBroadphase}.
 * 
 * @author Manolis Tsamis
 * @version 3.4.0
 * @since 3.4.0
 * @param <E> the {@link Collidable} type
 * @param <T> the {@link Fixture} type
 */
class BruteForceBroadphaseNode<E extends Collidable<T>, T extends Fixture> {
	/** The collidable */
	public final E collidable;
	
	/** The fixture */
	public final T fixture;
	
	/** The AABB */
	public AABB aabb;
	
	/** Whether the node has been tested or not */
	boolean tested;

	/**
	 * Minimal constructor.
	 * @param collidable the collidable
	 * @param fixture the fixture
	 */
	BruteForceBroadphaseNode(E collidable, T fixture) {
		this.collidable = collidable;
		this.fixture = fixture;
		
		// calculate the initial AABB
		this.updateAABB();
	}
	
	/**
	 * Updates the AABB of this node
	 */
	void updateAABB() {
		// Remember, we don't expand the AABB
		Transform tx = collidable.getTransform();
		this.aabb = fixture.getShape().createAABB(tx);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PlainBroadphaseNode[AABB=").append(this.aabb.toString())
		.append("|Fixture=").append(this.fixture.hashCode())
		.append("]");
		return sb.toString();
	}
}