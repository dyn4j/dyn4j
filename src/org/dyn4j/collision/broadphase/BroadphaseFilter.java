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
import org.dyn4j.geometry.Ray;

/**
 * Represents a class that defines rules to exclude results from a {@link BroadphaseDetector}'s query methods. Some examples
 * include the {@link BroadphaseDetector#detect(BroadphaseFilter)}, {@link BroadphaseDetector#raycast(Ray, double, BroadphaseFilter)} 
 * and {@link BroadphaseDetector#detect(AABB, BroadphaseFilter)} methods.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.2.0
 * @param <E> the {@link Collidable} type
 * @param <T> the {@link Fixture} type
 * @see DefaultBroadphaseFilter
 */
public interface BroadphaseFilter<E extends Collidable<T>, T extends Fixture> {
	/**
	 * Returns true if this result should be added to the results list.
	 * <p>
	 * This method is called from the {@link BroadphaseDetector#detect()} and
	 * {@link BroadphaseDetector#detect(BroadphaseFilter)} methods.
	 * @param collidable1 the first {@link Collidable}
	 * @param fixture1 the first {@link Collidable}s {@link Fixture}
	 * @param collidable2 the second {@link Collidable}
	 * @param fixture2 the second {@link Collidable}s {@link Fixture}
	 * @return boolean
	 */
	public abstract boolean isAllowed(E collidable1, T fixture1, E collidable2, T fixture2);
	
	/**
	 * Returns true if this result should be added to the results list.
	 * <p>
	 * This method is called from the {@link BroadphaseDetector#detect(AABB)} and
	 * {@link BroadphaseDetector#detect(AABB, BroadphaseFilter)} methods.
	 * @param aabb the AABB using to test
	 * @param collidable the {@link Collidable}
	 * @param fixture the {@link Collidable}s {@link Fixture}
	 * @return boolean
	 */
	public abstract boolean isAllowed(AABB aabb, E collidable, T fixture);
	
	/**
	 * Returns true if this result should be added to the results list.
	 * <p>
	 * This method is called from the {@link BroadphaseDetector#raycast(Ray, double)} and
	 * {@link BroadphaseDetector#raycast(Ray, double, BroadphaseFilter)} methods.
	 * @param ray the ray
	 * @param length the length of the ray
	 * @param collidable the {@link Collidable}
	 * @param fixture the {@link Collidable}s {@link Fixture}
	 * @return boolean
	 */
	public abstract boolean isAllowed(Ray ray, double length, E collidable, T fixture);
}
