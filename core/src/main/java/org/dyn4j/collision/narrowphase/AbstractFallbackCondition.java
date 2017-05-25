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

/**
 * Abstract implementation of the {@link FallbackCondition} interface.
 * <p>
 * By default conditions will be remain in the order they are added unless alternate sort indices are supplied.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.1.5
 */
public abstract class AbstractFallbackCondition implements FallbackCondition, Comparable<FallbackCondition> {
	/** The sort index */
	final int sortIndex;
	
	/**
	 * Minimal constructor.
	 * @param sortIndex the sort index
	 */
	public AbstractFallbackCondition(int sortIndex) {
		this.sortIndex = sortIndex;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(FallbackCondition o) {
		return this.getSortIndex() - o.getSortIndex();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.hashCode#equals()
	 */
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + sortIndex;
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (obj instanceof AbstractFallbackCondition) {
			AbstractFallbackCondition other = (AbstractFallbackCondition) obj;
			if (this.sortIndex == other.sortIndex) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.narrowphase.FallbackCondition#getSortIndex()
	 */
	@Override
	public int getSortIndex() {
		return this.sortIndex;
	}
}
