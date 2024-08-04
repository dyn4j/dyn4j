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
package org.dyn4j.world.result;

import org.dyn4j.Copyable;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;

/**
 * Represents a reusable result for query-based collision detection.
 * @author William Bittle
 * @version 6.0.0
 * @since 4.0.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public class DetectResult<T extends CollisionBody<E>, E extends Fixture> implements Copyable<DetectResult<T, E>> {
	/** The body */
	protected T body;
	
	/** The fixture */
	protected E fixture;
	
	/**
	 * Default constructor.
	 */
	public DetectResult() {}
	
	/**
	 * Full constructor.
	 * @param body the body
	 * @param fixture the fixture
	 */
	protected DetectResult(T body, E fixture) {
		this.body = body;
		this.fixture = fixture;
	}
	
	/**
	 * Copy constructor.
	 * @param result the result to copy
	 * @since 6.0.0
	 */
	protected DetectResult(DetectResult<T, E> result) {
		this.body = result.body;
		this.fixture = result.fixture;
	}
	
	/**
	 * Returns the body.
	 * @return T
	 */
	public T getBody() {
		return this.body;
	}
	
	/**
	 * Returns the fixture.
	 * @return E
	 */
	public E getFixture() {
		return this.fixture;
	}
	
	/**
	 * Sets the body.
	 * @param body the body
	 */
	public void setBody(T body) {
		this.body = body;
	}

	/**
	 * Sets the fixture.
	 * @param fixture the fixture
	 */
	public void setFixture(E fixture) {
		this.fixture = fixture;
	}

	/**
	 * Copies (deep) the given result to this result.
	 * @param result the result to copy
	 * @deprecated Deprecated in 6.0.0.  Use {@link #set(DetectResult)} instead.
	 */
	@Deprecated
	public void copy(DetectResult<T, E> result) {
		this.body = result.body;
		this.fixture = result.fixture;
	}
	
	/**
	 * Sets this result to the given result.
	 * @param result the result to use
	 * @since 6.0.0
	 */
	public void set(DetectResult<T, E> result) {
		this.body = result.body;
		this.fixture = result.fixture;
	}
	
	/**
	 * Returns a shallow copy of this object.
	 * @return {@link DetectResult}
	 */
	public DetectResult<T, E> copy() {
		return new DetectResult<T, E>(this);
	}
}
