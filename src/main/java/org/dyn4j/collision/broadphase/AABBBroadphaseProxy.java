/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision.broadphase;

import org.dyn4j.geometry.AABB;

/**
 * Represents a sortable proxy for an object in the {@link Sap} / {@link BruteForceBroadphase} {@link BroadphaseDetector}s.
 * <p>
 * Note: This class has a natural ordering that is inconsistent with equals.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.0.0
 * @param <T> the object type
 */
final class AABBBroadphaseProxy<T> implements Comparable<AABBBroadphaseProxy<T>> {
	/** The collision item */
	final T item;
	
	/** The body's aabb */
	final AABB aabb;
	
	/**
	 * Full constructor.
	 * @param item the collision item
	 */
	public AABBBroadphaseProxy(T item) {
		this.item = item;
		this.aabb = new AABB(0,0,0,0);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(AABBBroadphaseProxy<T> o) {
		// check if the objects are the same instance
		if (this == o) return 0;
		// compute the difference in the minimum x values of the aabbs
		double diff = this.aabb.getMinX() - o.aabb.getMinX();
		if (diff != 0) {
			return (int)Math.signum(diff);
		} else {
			// if the x values are the same then compare on the y values
			diff = this.aabb.getMinY() - o.aabb.getMinY();
			if (diff != 0) {
				return (int)Math.signum(diff);
			} else {
				// try the max x
				diff = this.aabb.getMaxX() - o.aabb.getMaxX();
				if (diff != 0) {
					return (int)Math.signum(diff);
				} else {
					// now the max y
					diff = this.aabb.getMaxY() - o.aabb.getMaxY();
					if (diff != 0) {
						return (int)Math.signum(diff);
					} else {
						// at this point we've discovered that the AABBs are identical
						// but we still need something that makes them different because
						// the BinarySearchTree they'll be put in cannot have duplicate
						// values
						
						// since we don't know the type of T, then hashcode is the only
						// thing we have to go on
						return this.item.hashCode() - o.item.hashCode();
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SapProxy[Item=").append(this.item)
		  .append("|AABB=").append(this.aabb.toString())
		  .append("]");
		return sb.toString();
	}
}
