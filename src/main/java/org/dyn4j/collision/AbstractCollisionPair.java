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
 * Abstract implementation of the {@link CollisionPair} interface.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public abstract class AbstractCollisionPair<T extends CollisionBody<E>, E extends Fixture> implements CollisionPair<T, E>, Copyable<CollisionPair<T, E>> {

	// NOTE: if we ever move to Java 8 or higher, move these methods to the CollisionPair interface
	
	/**
	 * Returns the hashcode for a collision pair.
	 * @param body1 the first body
	 * @param fixture1 the first body's fixture
	 * @param body2 the second body
	 * @param fixture2 the second body's fixture
	 * @return int
	 */
	public static int getHashCode(CollisionBody<?> body1, Fixture fixture1, CollisionBody<?> body2, Fixture fixture2) {
		int h1 = AbstractCollisionItem.getHashCode(body1, fixture1);
		int h2 = AbstractCollisionItem.getHashCode(body2, fixture2);
		// the total can be in any order
		return h1 + h2;
	}
	
	/**
	 * Returns true if the given pair and object are equal.
	 * @param pairA the first pair
	 * @param obj the other object
	 * @return boolean
	 */
	public static boolean equals(CollisionPair<?, ?> pairA, Object obj) {
		if (obj == pairA) return true;
		if (obj == null || pairA == null) return false;
		if (obj instanceof CollisionPair) {
			CollisionPair<?, ?> pairB = (CollisionPair<?, ?>)obj;
			
			CollisionBody<?> c1a = pairA.getBody1();
			Fixture f1a = pairA.getFixture1();
			CollisionBody<?> c2a = pairA.getBody2();
			Fixture f2a = pairA.getFixture2();
			
			CollisionBody<?> c1b = pairB.getBody1();
			Fixture f1b = pairB.getFixture1();
			CollisionBody<?> c2b = pairB.getBody2();
			Fixture f2b = pairB.getFixture2();
			
			return (c1b == c1a && f1b == f1a && c2b == c2a && f2b == f2a) || 
				   (c1b == c2a && f1b == f2a && c2b == c1a && f2b == f1a);
		}
		return false;
	}
}
