/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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
 * Represents a pair of {@link Collidable} {@link Fixture}s that have been detected as
 * colliding in the {@link BroadphaseDetector}.
 * @author William Bittle
 * @param <E> the {@link Collidable} type
 * @param <T> the {@link Fixture} type
 * @version 4.0.0
 * @since 1.0.0
 */
public class BroadphasePair<E extends Collidable<T>, T extends Fixture> {
	/** The first {@link Collidable} */
	public final E collidable1;
	
	/** The first {@link Collidable}'s {@link Fixture} */
	public final T fixture1;
	
	/** The second {@link Collidable} */
	public final E collidable2;
	
	/** The second {@link Collidable}'s {@link Fixture} */
	public final T fixture2;
	
	/**
	 * Minimal constructor.
	 * @param collidable1 the first collidable
	 * @param fixture1 the first collidable's fixture
	 * @param collidable2 the second collidable
	 * @param fixture2 the second collidable's fixture
	 */
	public BroadphasePair(E collidable1, T fixture1, E collidable2, T fixture2) {
		this.collidable1 = collidable1;
		this.fixture1 = fixture1;
		this.collidable2 = collidable2;
		this.fixture2 = fixture2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof BroadphasePair) {
			BroadphasePair<?, ?> pair = (BroadphasePair<?, ?>)obj;
			if (pair.collidable1 == this.collidable1 &&
				pair.fixture1 == this.fixture1 &&
				pair.collidable2 == this.collidable2 &&
				pair.fixture2 == this.fixture2) {
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
		hash = hash * 31 + this.collidable1.hashCode();
		hash = hash * 31 + this.fixture1.hashCode();
		hash = hash * 31 + this.collidable2.hashCode();
		hash = hash * 31 + this.fixture2.hashCode();
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BroadphasePair[Collidable1=").append(this.collidable1.getId())
		.append("|Fixture1=").append(this.fixture1.getId())
		.append("|Collidable2=").append(this.collidable2.getId())
		.append("|Fixture2=").append(this.fixture2.getId())
		.append("]");
		return sb.toString();
	}
}
