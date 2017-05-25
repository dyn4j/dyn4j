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
package org.dyn4j.collision.broadphase;

import org.dyn4j.BinarySearchTree;
import org.dyn4j.BinarySearchTreeSearchCriteria;
import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;

/**
 * Represents a search method for finding the comparable element in a {@link BinarySearchTree} that is the
 * proxy whose min x is the least that is overlapping the query AABB.
 * <p>
 * This search criteria relies on the {@link BinarySearchTree} being sorted based on the min x of the proxies.
 * @author William Bittle
 * @version 3.2.3
 * @since 3.2.3
 * @param <E> the {@link Collidable} type
 * @param <T> the {@link Fixture} type
 */
class SapQuerySearchCriteria<E extends Collidable<T>, T extends Fixture> implements BinarySearchTreeSearchCriteria<SapProxy<E, T>> {
	/** The query AABB */
	private final AABB query;
	
	/** The minimum x */
	double min;
	
	/** The minimum proxy */
	SapProxy<E, T> lowest;
	
	/**
	 * Minimal constructor.
	 * @param query the query AABB
	 */
	public SapQuerySearchCriteria(AABB query) {
		this.query = query;
		this.min = Double.MAX_VALUE;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.BinarySearchTreeSearchCriteria#evaluate(java.lang.Comparable)
	 */
	@Override
	public int evaluate(SapProxy<E, T> comparable) {
		// if the current AABB's max is less than the query AABB's
		// min, then we need to traverse the tree to the right
		if (comparable.aabb.getMaxX() < query.getMinX()) {
			return 1;
		}
		
		// otherwise it's less than or equal to this AABB's min x
		// this indicates that the current AABB is either touching
		// or overlapping the query AABB
		
		// check if the current AABB's min x is less than the current
		// minimum x
		if (this.min >= comparable.aabb.getMinX()) {
			// if it is, then we've found an AABB in the tree that either
			// overlaps or touches the query AABB that has a smaller min x
			this.min = comparable.aabb.getMinX();
			this.lowest = comparable;
			
			// now we need to continue traversing left to see if there are
			// any other AABB nodes that overlap or touch the query AABB
			// with a smaller min x
			return -1;
		}
		
		// otherwise just stop
		return 0;
	}
}
