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
 * Represents a {@link TypedFallbackCondition} that filters on a single type.
 * <p>
 * If either {@link Convex} type matches this type, then the condition is met.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.1.5
 */
public class SingleTypedFallbackCondition extends TypedFallbackCondition implements FallbackCondition, Comparable<FallbackCondition> {
	/** The type to compare to */
	private final Class<? extends Convex> type;
	
	/** True if a strict class equals should be used */
	private final boolean strict;
	
	/**
	 * Minimal constructor.
	 * @param type the type
	 */
	public SingleTypedFallbackCondition(Class<? extends Convex> type) {
		this(type, 0, true);
	}
	
	/**
	 * Optional constructor.
	 * @param type the type
	 * @param sortIndex the sort index of this condition
	 */
	public SingleTypedFallbackCondition(Class<? extends Convex> type, int sortIndex) {
		this(type, sortIndex, true);
	}
	
	
	/**
	 * Optional constructor.
	 * @param type the type
	 * @param strict true if a strict type comparison should be performed
	 */
	public SingleTypedFallbackCondition(Class<? extends Convex> type, boolean strict) {
		this(type, 0, strict);
	}

	/**
	 * Full constructor.
	 * @param type the type
	 * @param sortIndex the sort index of this condition
	 * @param strict true if a strict type comparison should be performed
	 */
	public SingleTypedFallbackCondition(Class<? extends Convex> type, int sortIndex, boolean strict) {
		super(sortIndex);
		this.type = type;
		this.strict = strict;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.narrowphase.AbstractFallbackCondition#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (this.strict ? 1231 : 1237);
		result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.narrowphase.AbstractFallbackCondition#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj instanceof SingleTypedFallbackCondition) {
			SingleTypedFallbackCondition other = (SingleTypedFallbackCondition) obj;
			if (this.strict == other.strict
			 && this.type == other.type
			 && this.sortIndex == other.sortIndex) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SingleTypedFallbackCondition[")
		  .append("Type=").append(this.type.getName())
		  .append("|IsStrict=").append(this.strict)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.extras.TypedFallbackCondition#isMatch(java.lang.Class, java.lang.Class)
	 */
	@Override
	public boolean isMatch(Class<? extends Convex> type1, Class<? extends Convex> type2) {
		if (this.strict) {
			// it must be exactly equal to type 1 or 2
			return this.type == type1 || this.type == type2;
		}
		// otherwise it must be assignable to type
		return this.type.isAssignableFrom(type1) || this.type.isAssignableFrom(type2);
	}
	
	/**
	 * Returns the type for this fallback condition.
	 * @return Class&lt;? extends {@link Convex}&gt;
	 */
	public Class<? extends Convex> getType() {
		return this.type;
	}
	
	/**
	 * Returns true if this condition uses a strict type comparison.
	 * @return boolean
	 */
	public boolean isStrict() {
		return this.strict;
	}
}
