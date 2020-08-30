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
 * @version 4.0.0
 * @since 4.0.0
 * @param <T> the body type
 * @param <E> the fixture type
 */
public final class BasicCollisionPair<T extends CollisionBody<E>, E extends Fixture> extends AbstractCollisionPair<T, E> implements CollisionPair<T, E> {
	/** the first body */
	private final T body1;
	
	/** the first fixture */
	private final E fixture1;
	
	/** the second body */
	private final T body2;
	
	/** the second fixture */
	private final E fixture2;
	
	/**
	 * Minimal constructor.
	 * @param body1 the first body
	 * @param fixture1 the first fixture
	 * @param body2 the second body
	 * @param fixture2 the second fixture
	 */
	public BasicCollisionPair(T body1, E fixture1, T body2, E fixture2) {
		this.body1 = body1;
		this.fixture1 = fixture1;
		this.body2 = body2;
		this.fixture2 = fixture2;
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
		return AbstractCollisionPair.getHashCode(this.body1, this.fixture1, this.body2, this.fixture2);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.Copyable#copy()
	 */
	@Override
	public BasicCollisionPair<T, E> copy() {
		return new BasicCollisionPair<T, E>(this.body1, this.fixture1, this.body2, this.fixture2);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getBody1()
	 */
	@Override
	public T getBody1() {
		return this.body1;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getFixture1()
	 */
	@Override
	public E getFixture1() {
		return this.fixture1;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getBody2()
	 */
	@Override
	public T getBody2() {
		return this.body2;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionPair#getFixture2()
	 */
	@Override
	public E getFixture2() {
		return this.fixture2;
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
