/*
 * Copyright (c) 2010-2023 William Bittle  http://www.dyn4j.org/
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
 * Abstract implementation of the {@link CollisionPair} interface.
 * @author William Bittle
 * @version 5.0.2
 * @since 4.0.0
 * @param <T> the object type
 */
public abstract class AbstractCollisionPair<T> implements CollisionPair<T>, Copyable<CollisionPair<T>> {

	// NOTE: if we ever move to Java 8 or higher, move these methods to the CollisionPair interface
	
	/**
	 * Returns the hashcode for a collision pair.
	 * @param body1 the first body
	 * @param fixture1 the first body's fixture
	 * @param body2 the second body
	 * @param fixture2 the second body's fixture
	 * @return int
	 */
	public static final int getHashCode(CollisionBody<?> body1, Fixture fixture1, CollisionBody<?> body2, Fixture fixture2) {
		int h1 = AbstractCollisionItem.getHashCode(body1, fixture1);
		int h2 = AbstractCollisionItem.getHashCode(body2, fixture2);
		// the total can be in any order
		return h1 + h2;
	}
	
	/**
	 * Returns the hashcode for a pair of objects assuming order doesn't matter.
	 * @param item1 the first object
	 * @param item2 the second object
	 * @return int
	 * @since 4.1.0
	 */
	public static final int getHashCode(Object item1, Object item2) {
		int h1 = item1.hashCode();
		int h2 = item2.hashCode();
		// the total can be in any order
		return h1 + h2;
	}
	
	/**
	 * Returns true if the given pair and object are equal.
	 * @param pairA the first pair
	 * @param obj the other object
	 * @return boolean
	 */
	public static final boolean equals(CollisionPair<?> pairA, Object obj) {
		if (obj == pairA) return true;
		if (obj == null || pairA == null) return false;
		if (obj instanceof CollisionPair) {
			CollisionPair<?> pairB = (CollisionPair<?>)obj;
			
			Object a1 = pairA.getFirst();
			Object a2 = pairA.getSecond();
			
			Object b1 = pairB.getFirst();
			Object b2 = pairB.getSecond();
			
			return (a1.equals(b1) && a2.equals(b2)) ||
				   (a1.equals(b2) && a2.equals(b1));
		}
		return false;
	}
}
