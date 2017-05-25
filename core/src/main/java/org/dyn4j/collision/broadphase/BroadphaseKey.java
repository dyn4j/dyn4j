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

import java.util.UUID;

import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Fixture;

/**
 * Represents a key for a {@link BroadphaseItem} used for fast look ups in
 * the {@link BroadphaseDetector}s.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.2.0
 */
final class BroadphaseKey {
	/** The {@link Collidable}s id */
	final UUID collidable;
	
	/** The {@link Fixture}s id */
	final UUID fixture;
	
	/** The pre-computed hashcode */
	private final int hashCode;
	
	/**
	 * Minimal constructor.
	 * @param collidable the collidable id
	 * @param fixture the fixture id
	 */
	public BroadphaseKey(UUID collidable, UUID fixture) {
		this.collidable = collidable;
		this.fixture = fixture;
		// pre compute the hash
		this.hashCode = this.computeHashCode();
	}
	
	/**
	 * Creates and returns a new key for the given {@link Collidable} and {@link Fixture}.
	 * @param collidable the collidable
	 * @param fixture the fixture
	 * @return {@link BroadphaseKey}
	 */
	public static final BroadphaseKey get(Collidable<?> collidable, Fixture fixture) {
		return new BroadphaseKey(collidable.getId(), fixture.getId());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return false;
		if (obj instanceof BroadphaseKey) {
			BroadphaseKey key = (BroadphaseKey)obj;
			return key.collidable.equals(this.collidable) &&
				   key.fixture.equals(this.fixture);
		}
		return false;
	}
	
	/**
	 * Computes the hashcode from the collidable and fixture ids.
	 * @return int
	 */
	protected final int computeHashCode() {
		int hash = 17;
		hash = hash * 31 + this.collidable.hashCode();
		hash = hash * 31 + this.fixture.hashCode();
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.hashCode;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BroadphaseKey[CollidableId=").append(this.collidable)
		.append("|FixtureId=").append(this.fixture)
		.append("]");
		return sb.toString();
	}
}
