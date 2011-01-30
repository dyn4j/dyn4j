/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d.collision;

/**
 * Interface representing a filter for collision detection.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public interface Filter {
	/** The default filter which always returns true */
	public static final Filter DEFAULT_FILTER = new Filter() {
		/* (non-Javadoc)
		 * @see org.dyn4j.game2d.collision.Filter#isAllowed(org.dyn4j.game2d.collision.Filter)
		 */
		@Override
		public boolean isAllowed(Filter filter) {
			// always return true
			return true;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "DEFAULT_FILTER[]";
		}
	};
	
	/**
	 * Returns true if the given {@link Filter} and this {@link Filter}
	 * allow the objects to interact.
	 * <p>
	 * If the given {@link Filter} is not the same type as this {@link Filter}
	 * its up to the implementing class to specify the behavior.
	 * <p>
	 * In addition, if the given {@link Filter} is null its up to the implementing 
	 * class to specify the behavior.
	 * @param filter the other {@link Filter}
	 * @return boolean
	 */
	public abstract boolean isAllowed(Filter filter);
}
