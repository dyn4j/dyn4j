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

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Ray;

/**
 * Represents a class that defines rules to exclude results from a {@link BroadphaseDetector}'s query methods.
 * <p>
 * The intent is that instances of this class would be used to help filter {@link CollisionPair}s and 
 * {@link CollisionItem}s emitted from the {@link BroadphaseDetector}s.
 * @author William Bittle
 * @version 4.0.0
 * @since 3.2.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 * @deprecated Deprecated in 4.0.0. Use the BroadphaseFilter in the world package instead.
 */
@Deprecated
public interface BroadphaseFilter<T extends CollisionBody<E>, E extends Fixture> {
	/**
	 * Returns true if this result should be added to the results list.
	 * @param body1 the first {@link CollisionBody}
	 * @param fixture1 the first {@link CollisionBody}s {@link Fixture}
	 * @param body2 the second {@link CollisionBody}
	 * @param fixture2 the second {@link CollisionBody}s {@link Fixture}
	 * @return boolean
	 */
	public abstract boolean isAllowed(T body1, E fixture1, T body2, E fixture2);
	
	/**
	 * Returns true if this result should be added to the results list.
	 * @param aabb the AABB using to test
	 * @param body the {@link CollisionBody}
	 * @param fixture the {@link CollisionBody}s {@link Fixture}
	 * @return boolean
	 * @deprecated Deprecated in 4.0.0. This was replaced with a specific AABB filter interface.
	 */
	@Deprecated
	public abstract boolean isAllowed(AABB aabb, T body, E fixture);
	
	/**
	 * Returns true if this result should be added to the results list.
	 * @param ray the ray
	 * @param length the length of the ray
	 * @param body the {@link CollisionBody}
	 * @param fixture the {@link CollisionBody}s {@link Fixture}
	 * @return boolean
	 * @deprecated Deprecated in 4.0.0. This was replaced with a specific Ray filter interface.
	 */
	@Deprecated
	public abstract boolean isAllowed(Ray ray, double length, T body, E fixture);
}
