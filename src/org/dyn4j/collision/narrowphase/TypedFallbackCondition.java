/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision.narrowphase;

import org.dyn4j.geometry.Convex;

/**
 * Represents a {@link FallbackCondition} that uses the {@link Convex}'s class type to determine
 * which pairs will be detected by the fallback {@link NarrowphaseDetector}.
 * @author William Bittle
 * @version 3.1.5
 * @since 3.1.5
 */
public abstract class TypedFallbackCondition extends AbstractFallbackCondition implements FallbackCondition, Comparable<FallbackCondition> {
	/**
	 * Default constructor.
	 */
	public TypedFallbackCondition() {
		this(0);
	}
	
	/**
	 * Optional constructor.
	 * @param sortIndex the sort index of this condition
	 */
	public TypedFallbackCondition(int sortIndex) {
		super(sortIndex);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.extras.FallbackCondition#isMatch(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Convex)
	 */
	@Override
	public boolean isMatch(Convex convex1, Convex convex2) {
		// delegate to type matching
		return this.isMatch(convex1.getClass(), convex2.getClass());
	}

	/**
	 * Returns true if the given types match this condition.
	 * @param type1 the type of the first {@link Convex}
	 * @param type2 the type of the second {@link Convex}
	 * @return boolean
	 */
	public abstract boolean isMatch(Class<? extends Convex> type1, Class<? extends Convex> type2);
}
