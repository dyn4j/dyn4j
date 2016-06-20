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

import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;

/**
 * Represents a sortable proxy for a {@link Collidable} {@link Fixture} in the {@link Sap} {@link BroadphaseDetector}.
 * <p>
 * Note: This class has a natural ordering that is inconsistent with equals.
 * @author William Bittle
 * @since 3.2.0
 * @version 3.2.0
 * @param <E> the {@link Collidable} type
 * @param <T> the {@link Fixture} type
 */
final class SapProxy<E extends Collidable<T>, T extends Fixture> implements Comparable<SapProxy<E, T>> {
	/** The collidable */
	final E collidable;
	
	/** The fixture */
	final T fixture;
	
	/** The collidable's aabb */
	AABB aabb;
	
	/** Whether the proxy has been tested or not */
	boolean tested;
	
	/**
	 * Full constructor.
	 * @param collidable the collidable
	 * @param fixture the fixture
	 * @param aabb the aabb
	 */
	public SapProxy(E collidable, T fixture, AABB aabb) {
		this.collidable = collidable;
		this.fixture = fixture;
		this.aabb = aabb;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(SapProxy<E, T> o) {
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
				if (this.isSearch()) {
					return -1;
				} else if (o.isSearch()) {
					return 1;
				}
				// finally if their y values are the same then compare on the ids
				diff = this.collidable.getId().compareTo(o.collidable.getId());
				if (diff == 0) {
					return this.fixture.getId().compareTo(o.fixture.getId());
				}
				return (int)Math.signum(diff);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SapProxy) {
			SapProxy<?, ?> pair = (SapProxy<?, ?>)obj;
			if (pair.collidable == this.collidable &&
				pair.fixture == this.fixture) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + this.collidable.hashCode();
		hash = hash * 31 + this.fixture.hashCode();
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SapProxy[Collidable=").append(this.collidable != null ? this.collidable.getId() : "null")
		  .append("|Fixture=").append(this.fixture != null ? this.fixture.getId() : "null")
		  .append("|AABB=").append(this.aabb.toString())
		  .append("|Tested=").append(this.tested)
		  .append("]");
		return sb.toString();
	}
	
	/**
	 * Returns true if the given proxy is a search
	 * proxy.
	 * <p>
	 * These should not be stored in the broad phase, but
	 * instead used to do searching.
	 * @return boolean
	 */
	public final boolean isSearch() {
		return (this.collidable == null ||
				this.fixture == null);
	}
}
