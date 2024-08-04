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

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.narrowphase.Penetration;

/**
 * Represents a reusable {@link DetectResult} for convex shape detection.
 * @author William Bittle
 * @version 6.0.0
 * @since 4.0.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public class ConvexDetectResult<T extends CollisionBody<E>, E extends Fixture> extends DetectResult<T, E> {
	/** The penetration data */
	protected final Penetration penetration;
	
	/**
	 * Default constructor.
	 */
	public ConvexDetectResult() {
		this.penetration = new Penetration();
	}
	
	/**
	 * Full constructor.
	 * @param body the body
	 * @param fixture the fixture
	 * @param penetration the penetration data
	 */
	protected ConvexDetectResult(T body, E fixture, Penetration penetration) {
		super(body, fixture);
		this.penetration = penetration.copy();
	}
	
	/**
	 * Copy constructor.
	 * @param result the result to copy
	 * @since 6.0.0
	 */
	protected ConvexDetectResult(ConvexDetectResult<T, E> result) {
		super(result);
		this.penetration = result.penetration.copy();
	}

	/**
	 * Returns the penetration data.
	 * @return {@link Penetration}
	 */
	public Penetration getPenetration() {
		return this.penetration;
	}
	
	/**
	 * Sets the penetration data.
	 * @param penetration the penetration data
	 */
	public void setPenetration(Penetration penetration) {
		this.penetration.set(penetration);
	}
	
	/**
	 * Copies (deep) the given result data to this result.
	 * @param result the result to copy
	 * @deprecated Deprecated in 6.0.0. Use {@link #set(ConvexDetectResult)} instead.
	 */
	@Deprecated
	public void copy(ConvexDetectResult<T, E> result) {
		super.set(result);
		this.penetration.set(result.penetration);
	}
	
	/**
	 * Sets this result to the given result.
	 * @param result the result to use
	 * @since 6.0.0
	 */
	public void set(ConvexDetectResult<T, E> result) {
		super.set(result);
		this.penetration.set(result.penetration);
	}

	/**
	 * Returns a copy of this object.
	 * <p>
	 * NOTE: The {@link CollisionBody} and {@link Fixture} are not copied, 
	 * but the {@link Penetration} is.
	 * @return {@link ConvexDetectResult}
	 */
	public ConvexDetectResult<T, E> copy() {
		return new ConvexDetectResult<T, E>(this);
	}
}
