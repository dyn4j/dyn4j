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

import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * Represents an iterator for {@link Fixture}s in a {@link CollisionBody}.
 * @author William Bittle
 * @version 4.0.0
 * @since 3.2.0
 * @param <T> the {@link Fixture} type
 * @deprecated Deprecated in 4.0.0. Use the FixtureIterator in the AbstractCollisionBody class instead.
 */
@Deprecated
final class FixtureIterator<T extends Fixture> implements Iterator<T> {
	/** The body to iterate over */
	private final CollisionBody<T> body;
	
	/** The current index */
	private int index;
	
	/**
	 * Minimal constructor.
	 * @param body the body to iterate over
	 */
	public FixtureIterator(CollisionBody<T> body) {
		this.body = body;
		this.index = -1;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.index + 1 < this.body.getFixtureCount();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next() {
		if (this.index >= this.body.getFixtureCount()) {
			throw new IndexOutOfBoundsException();
		}
		try {
			this.index++;
			T fixture = this.body.getFixture(this.index);
			return fixture;
		} catch (IndexOutOfBoundsException ex) {
			throw new ConcurrentModificationException();
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		if (this.index < 0) {
			throw new IllegalStateException();
		}
		if (this.index >= this.body.getFixtureCount()) {
			throw new IndexOutOfBoundsException();
		}
		try {
			this.body.removeFixture(this.index);
			this.index--;
		} catch (IndexOutOfBoundsException ex) {
			throw new ConcurrentModificationException();
		}
	}
}
