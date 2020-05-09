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
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Fixture;

/**
 * An implementation of the {@link CollisionPair} interface used by the {@link BroadphaseDetector}s.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public final class BroadphasePair<T extends CollisionBody<E>, E extends Fixture> implements CollisionPair<T, E> {
	
	// the first
	
	/** The first {@link CollisionBody} */
	T body1;
	
	/** The first {@link CollisionBody}'s {@link Fixture} */
	E fixture1;
	
	// the second
	
	/** The second {@link CollisionBody} */
	T body2;
	
	/** The second {@link CollisionBody}'s {@link Fixture} */
	E fixture2;
	
	/**
	 * Default constructor.
	 */
	public BroadphasePair() {}
	
	/**
	 * Full constructor.
	 * @param body1 the first body
	 * @param fixture1 the first body's fixture
	 * @param body2 the second body
	 * @param fixture2 the second body's fixture
	 */
	public BroadphasePair(T body1, E fixture1, T body2, E fixture2) {
		this.body1 = body1;
		this.fixture1 = fixture1;
		this.body2 = body2;
		this.fixture2 = fixture2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals()
	 */
	@Override
	public boolean equals(Object obj) {
		return CollisionPair.equals(this, obj);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return CollisionPair.getHashCode(this.body1, this.fixture1, this.body2, this.fixture2);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BroadphasePair[Body1=").append(this.body1.hashCode())
		.append("|Fixture1=").append(this.fixture1.hashCode())
		.append("|Body2=").append(this.body2.hashCode())
		.append("|Fixture2=").append(this.fixture2.hashCode())
		.append("]");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getBody1()
	 */
	public T getBody1() {
		return this.body1;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getFixture1()
	 */
	public E getFixture1() {
		return this.fixture1;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getBody2()
	 */
	public T getBody2() {
		return this.body2;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getFixture2()
	 */
	public E getFixture2() {
		return this.fixture2;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#copy()
	 */
	@Override
	public CollisionPair<T, E> copy() {
		return new BroadphasePair<T, E>(this.body1, this.fixture1, this.body2, this.fixture2);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getBody(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public T getBody(CollisionBody<?> body) {
		if (this.body1 == body) {
			return this.body1;
		} else if (this.body2 == body) {
			return this.body2;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getFixture(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public E getFixture(CollisionBody<?> body) {
		if (this.body1 == body) {
			return this.fixture1;
		} else if (this.body2 == body) {
			return this.fixture2;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getOtherBody(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public T getOtherBody(CollisionBody<?> body) {
		if (this.body1 == body) {
			return this.body2;
		} else if (this.body2 == body) {
			return this.body1;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getOtherFixture(org.dyn4j.collision.CollisionBody)
	 */
	@Override
	public E getOtherFixture(CollisionBody<?> body) {
		if (this.body1 == body) {
			return this.fixture2;
		} else if (this.body2 == body) {
			return this.fixture1;
		}
		return null;
	}
}
