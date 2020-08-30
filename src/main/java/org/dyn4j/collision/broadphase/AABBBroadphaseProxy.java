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
package org.dyn4j.collision.broadphase;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;

/**
 * Represents a sortable proxy for a {@link CollisionBody} {@link Fixture} in the {@link Sap} {@link BroadphaseDetector}.
 * <p>
 * Note: This class has a natural ordering that is inconsistent with equals.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
final class AABBBroadphaseProxy<T extends CollisionBody<E>, E extends Fixture> implements Comparable<AABBBroadphaseProxy<T, E>> {
	/** The collision item */
	final BroadphaseItem<T, E> item;
	
	/** The body's aabb */
	final AABB aabb;
	
	/**
	 * Full constructor.
	 * @param item the collision item
	 * @param aabb the {@link AABB}
	 */
	public AABBBroadphaseProxy(BroadphaseItem<T, E> item, AABB aabb) {
		this.item = item;
		this.aabb = aabb;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(AABBBroadphaseProxy<T, E> o) {
		// check if the objects are the same instance
		if (this == o) return 0;
		// compute the difference in the minimum x values of the aabbs
		double diff = this.aabb.getMinX() - o.aabb.getMinX();
		if (diff != 0) {
			return (int)Math.signum(diff);
		} else {
			// if the x values are the same then compare on the y values
			diff = this.aabb.getMinY() - o.aabb.getMinY();
			return (int)Math.signum(diff);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SapProxy[Body=").append(this.item.body.hashCode())
		  .append("|Fixture=").append(this.item.fixture.hashCode())
		  .append("|AABB=").append(this.aabb.toString())
		  .append("]");
		return sb.toString();
	}
}
