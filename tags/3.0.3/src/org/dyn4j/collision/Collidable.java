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
package org.dyn4j.collision;

import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Transformable;

/**
 * Represents an object that can collide with other objects.
 * @author William Bittle
 * @version 3.0.0
 * @since 1.0.0
 */
public interface Collidable extends Transformable {
	/**
	 * Returns a unique identifier for this {@link Collidable}.
	 * @return String the unique id
	 * @since 3.0.0
	 */
	public abstract String getId();
	
	/**
	 * Creates an {@link AABB} from this {@link Collidable}.
	 * <p>
	 * This method returns a degenerate AABB, (0.0, 0.0) to (0.0, 0.0),
	 * for {@link Collidable}s that have no fixtures.
	 * @return {@link AABB}
	 * @since 3.0.0
	 */
	public abstract AABB createAABB();
	
	/**
	 * Returns the {@link Fixture} at the given index.
	 * <p>
	 * Renamed from getShape(int).
	 * @param index the index of the {@link Fixture}
	 * @return {@link Fixture}
	 * @since 2.0.0
	 */
	public abstract Fixture getFixture(int index);
	
	/**
	 * Returns the number of {@link Fixture}s attached
	 * to this {@link Collidable} object.
	 * <p>
	 * Renamed from getShapeCount.
	 * @return int
	 * @since 2.0.0
	 */
	public abstract int getFixtureCount();
	
	/**
	 * Returns the {@link Transform} of the object.
	 * @return {@link Transform}
	 */
	public abstract Transform getTransform();
}
