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
package org.dyn4j.collision;

/**
 * Represents a basic, immutable implementation of the {@link CollisionPair} interface.
 * @author William Bittle
 * @version 6.0.0
 * @since 4.0.0
 * @param <T> the object type
 */
public final class BasicCollisionPair<T> extends AbstractCollisionPair<T> implements CollisionPair<T> {
	/** The first object */
	private final T first;
	
	/** The second object */
	private final T second;
	
	/**
	 * Minimal constructor.
	 * @param first the first object
	 * @param second the second object
	 */
	public BasicCollisionPair(T first, T second) {
		this.first = first;
		this.second = second;
	}
	
	/**
	 * Copy constructor.
	 * @param pair the pair to copy
	 * @since 6.0.0
	 */
	protected BasicCollisionPair(BasicCollisionPair<T> pair) {
		this.first = pair.first;
		this.second = pair.second;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
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

	/**
	 * Returns a shallow copy of this object.
	 * @return {@link BasicCollisionPair}
	 */
	@Override
	public BasicCollisionPair<T> copy() {
		return new BasicCollisionPair<T>(this);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getFirst()
	 */
	@Override
	public T getFirst() {
		return this.first;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getSecond()
	 */
	@Override
	public T getSecond() {
		return this.second;
	}
}
