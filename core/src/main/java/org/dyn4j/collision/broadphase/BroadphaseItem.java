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

/**
 * Represents a single {@link Collidable} {@link Fixture} that has been detected by a 
 * {@link BroadphaseDetector}.
 * <p>
 * A broad-phase item is a {@link Collidable}-{@link Fixture} pair.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.2.0
 * @param <E> the {@link Collidable} type
 * @param <T> the {@link Fixture} type
 */
public final class BroadphaseItem<E extends Collidable<T>, T extends Fixture> {
	/** The {@link Collidable} */
	final E collidable;
	
	/** The {@link Fixture} */
	final T fixture;
	
	/**
	 * Minimal constructor.
	 * @param collidable the collidable
	 * @param fixture the fixture
	 */
	public BroadphaseItem(E collidable, T fixture) {
		this.collidable = collidable;
		this.fixture = fixture;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof BroadphaseItem) {
			BroadphaseItem<?, ?> pair = (BroadphaseItem<?, ?>)obj;
			if (pair.collidable == this.collidable &&
				pair.fixture == this.fixture) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + this.collidable.getId().hashCode();
		hash = hash * 31 + this.fixture.getId().hashCode();
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BroadphaseItem[Collidable=").append(this.collidable.getId())
		.append("|Fixture=").append(this.fixture.getId())
		.append("]");
		return sb.toString();
	}

	/**
	 * Returns the {@link Collidable}.
	 * @return E
	 */
	public E getCollidable() {
		return this.collidable;
	}

	/**
	 * Returns the {@link Fixture}.
	 * @return T
	 */
	public T getFixture() {
		return this.fixture;
	}
}
