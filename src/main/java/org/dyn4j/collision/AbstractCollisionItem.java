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

import org.dyn4j.Copyable;

/**
 * Abstract implementation of the {@link CollisionItem} interface.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public abstract class AbstractCollisionItem<T extends CollisionBody<E>, E extends Fixture> implements CollisionItem<T, E>, Copyable<CollisionItem<T, E>> {
	
	// NOTE: if we ever move to Java 8 or higher, move these methods to the CollisionItem interface
	
	/**
	 * Returns the hashcode for a collision item.
	 * @param body the first body
	 * @param fixture the first body's fixture
	 * @return int
	 */
	public static int getHashCode(CollisionBody<?> body, Fixture fixture) {
		final int prime = 17;
		int h1 = 1;
		h1 = h1 * prime + body.hashCode();
		h1 = h1 * prime + fixture.hashCode();
		return h1;
	}
	
	/**
	 * Returns true if the given item and object are equal.
	 * @param item the item
	 * @param obj the other object
	 * @return boolean
	 */
	public static boolean equals(CollisionItem<?, ?> item, Object obj) {
		if (obj == item) return true;
		if (obj == null || item == null) return false;
		if (obj instanceof CollisionItem) {
			CollisionItem<?, ?> other = (CollisionItem<?, ?>)obj;
			return other.getBody() == item.getBody() && other.getFixture() == item.getFixture();
		}
		return false;
	}
}
