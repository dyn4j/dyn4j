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
 * Represents a basic, immutable implementation of the {@link CollisionItem} interface.
 * @author William Bittle
 * @version 6.0.0
 * @since 4.0.0
 * @param <T> the body type
 * @param <E> the fixture type
 */
public final class BasicCollisionItem<T extends CollisionBody<E>, E extends Fixture> extends AbstractCollisionItem<T, E> implements CollisionItem<T, E> {
	/** the body */
	private final T body;
	
	/** the fixture */
	private final E fixture;
	
	/**
	 * Minimal constructor.
	 * @param body the body
	 * @param fixture the fixture
	 */
	public BasicCollisionItem(T body, E fixture) {
		this.body = body;
		this.fixture = fixture;
	}
	
	/**
	 * Copy constructor.
	 * @param item the item to copy
	 * @since 6.0.0
	 */
	protected BasicCollisionItem(BasicCollisionItem<T, E> item) {
		this.body = item.body;
		this.fixture = item.fixture;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return AbstractCollisionItem.equals(this, obj);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return AbstractCollisionItem.getHashCode(this.body, this.fixture);
	}
	
	/**
	 * Returns a shallow copy of this object.
	 * @return {@link BasicCollisionItem}
	 */
	@Override
	public BasicCollisionItem<T, E> copy() {
		return new BasicCollisionItem<T, E>(this);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionItem#getBody()
	 */
	@Override
	public T getBody() {
		return this.body;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionItem#getFixture()
	 */
	@Override
	public E getFixture() {
		return this.fixture;
	}
}
