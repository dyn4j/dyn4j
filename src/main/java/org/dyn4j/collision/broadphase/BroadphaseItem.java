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

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.Fixture;

/**
 * An implementation of the {@link CollisionItem} interface used by the {@link BroadphaseDetector}s.
 * @author William Bittle
 * @version 4.0.0
 * @since 3.2.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public final class BroadphaseItem<T extends CollisionBody<E>, E extends Fixture> implements CollisionItem<T, E> {
	/** The {@link CollisionBody} */
	T body;
	
	/** The {@link Fixture} */
	E fixture;
	
	/**
	 * Constructor for reuse.
	 */
	public BroadphaseItem() {}

	/**
	 * Full constructor.
	 * @param body the {@link CollisionBody}
	 * @param fixture the {@link Fixture}
	 */
	public BroadphaseItem(T body, E fixture) {
		this.body = body;
		this.fixture = fixture;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return CollisionItem.equals(this, obj);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return CollisionItem.getHashCode(this.body, this.fixture);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BroadphaseItem[Body=").append(this.body.hashCode())
		.append("|Fixture=").append(this.fixture.hashCode())
		.append("]");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionItem#getBody()
	 */
	public T getBody() {
		return this.body;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionItem#getFixture()
	 */
	public E getFixture() {
		return this.fixture;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionItem#copy()
	 */
	@Override
	public CollisionItem<T, E> copy() {
		return new BroadphaseItem<T, E>(this.body, this.fixture);
	}
}
