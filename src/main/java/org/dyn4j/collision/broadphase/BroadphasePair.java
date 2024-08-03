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
package org.dyn4j.collision.broadphase;

import org.dyn4j.collision.AbstractCollisionPair;
import org.dyn4j.collision.CollisionPair;

/**
 * An implementation of the {@link CollisionPair} interface used by the {@link BroadphaseDetector}s.
 * @author William Bittle
 * @version 6.0.0
 * @since 1.0.0
 * @param <T> the object type
 */
final class BroadphasePair<T> implements CollisionPair<T> {
	
	// the first
	
	/** The first object */
	T first;
	
	/** The second object */
	T second;
	
	/**
	 * Default constructor.
	 */
	BroadphasePair() {}
	
	/**
	 * Full constructor.
	 * @param first the first object
	 * @param second the second object
	 */
	public BroadphasePair(T first, T second) {
		this.first = first;
		this.second = second;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals()
	 */
	@Override
	public boolean equals(Object obj) {
		return AbstractCollisionPair.equals(this, obj);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return AbstractCollisionPair.getHashCode(this.first, this.second);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BroadphasePair[First=").append(this.first)
		.append("|Second=").append(this.second)
		.append("]");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getFirst()
	 */
	public T getFirst() {
		return this.first;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getSecond()
	 */
	public T getSecond() {
		return this.second;
	}
	
	/**
	 * Returns a shallow copy of this object.
	 * @return {@link BroadphasePair}
	 */
	@Override
	public BroadphasePair<T> copy() {
		return new BroadphasePair<T>(this.first, this.second);
	}
}
