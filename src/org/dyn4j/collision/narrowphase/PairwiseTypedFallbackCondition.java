/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
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
 * Represents a {@link TypedFallbackCondition} that filters on a particular pair of types.
 * <p>
 * If the pair {@link Convex} types match this pair, then the condition is met.
 * @author William Bittle
 * @version 3.1.5
 * @since 3.1.5
 */
public class PairwiseTypedFallbackCondition extends TypedFallbackCondition implements FallbackCondition, Comparable<FallbackCondition> {
	/** The first type to compare to */
	protected Class<? extends Convex> type1;
	
	/** The second type to compare to */
	protected Class<? extends Convex> type2;
	
	/**
	 * Default constructor.
	 * <p>
	 * The ordering of the types doesn't matter.
	 * @param type1 the first type of the pair
	 * @param type2 the second type of the pair
	 */
	public PairwiseTypedFallbackCondition(Class<? extends Convex> type1, Class<? extends Convex> type2) {
		this(type1, type2, 0, true);
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * The ordering of the types doesn't matter.
	 * @param type1 the first type of the pair
	 * @param type2 the second type of the pair
	 * @param sortIndex the sort index of this condition
	 */
	public PairwiseTypedFallbackCondition(Class<? extends Convex> type1, Class<? extends Convex> type2, int sortIndex) {
		this(type1, type2, sortIndex, true);
	}
	
	
	/**
	 * Optional constructor.
	 * <p>
	 * The ordering of the types doesn't matter.
	 * @param type1 the first type of the pair
	 * @param type2 the second type of the pair
	 * @param strict true if a strict type comparison should be performed
	 */
	public PairwiseTypedFallbackCondition(Class<? extends Convex> type1, Class<? extends Convex> type2, boolean strict) {
		this(type1, type2, 0, strict);
	}

	/**
	 * Full constructor.
	 * <p>
	 * The ordering of the types doesn't matter.
	 * @param type1 the first type of the pair
	 * @param type2 the second type of the pair
	 * @param sortIndex the sort index of this condition
	 * @param strict true if a strict type comparison should be performed
	 */
	public PairwiseTypedFallbackCondition(Class<? extends Convex> type1, Class<? extends Convex> type2, int sortIndex, boolean strict) {
		super(sortIndex, strict);
		this.type1 = type1;
		this.type2 = type2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof PairwiseTypedFallbackCondition) {
			PairwiseTypedFallbackCondition pfc = (PairwiseTypedFallbackCondition)obj;
			// the types must be equal
			if (((pfc.type1 == this.type1 && pfc.type2 == this.type2) || 
				 (pfc.type1 == this.type2 && pfc.type2 == this.type1)) &&
				 // and their strictness must be equal
			      pfc.strict == this.strict &&
			      pfc.sortIndex == this.sortIndex) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.extras.TypedFallbackCondition#isMatch(java.lang.Class, java.lang.Class)
	 */
	public boolean isMatch(Class<? extends Convex> type1, Class<? extends Convex> type2) {
		if (this.strict) {
			// don't do subclass matching
			return (this.type1 == type1 && this.type2 == type2) || 
				   (this.type1 == type2 && this.type2 == type1);
		} else {
			// allow subclass matching
			return (this.type1.isAssignableFrom(type1) && this.type2.isAssignableFrom(type2)) ||
				   (this.type1.isAssignableFrom(type2) && this.type2.isAssignableFrom(type1));
		}
	}
}
