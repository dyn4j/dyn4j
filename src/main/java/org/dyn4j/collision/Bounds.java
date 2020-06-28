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
package org.dyn4j.collision;

import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Rotatable;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Translatable;
import org.dyn4j.geometry.Vector2;

/**
 * Represents the {@link Bounds} of a simulation.
 * <p>
 * By default all bounds are {@link Translatable} but not {@link Rotatable}.
 * <p>
 * Though not part of the bounds contract, a bounds object should only return true
 * from the {@link #isOutside(CollisionBody)} method when a {@link CollisionBody} is
 * <strong>fully</strong> outside the bounds.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 */
public interface Bounds extends Translatable, Shiftable {
	/**
	 * Returns the translation of the bounds.
	 * @return {@link Vector2}
	 * @since 3.2.0
	 */
	public abstract Vector2 getTranslation();
	
	/**
	 * Returns true if the given {@link CollisionBody} is <strong>fully</strong> outside the bounds.
	 * <p>
	 * If the {@link CollisionBody} contains zero {@link Fixture}s then 
	 * {@link CollisionBody} is considered to be outside the bounds.
	 * @param body the {@link CollisionBody} to test
	 * @return boolean true if outside the bounds
	 */
	public abstract boolean isOutside(CollisionBody<?> body);
	
	/**
	 * Returns true if the given {@link AABB} is <strong>fully</strong> outside the bounds.
	 * <p>
	 * If the {@link AABB} is a degenerate {@link AABB} (has zero area) then it's 
	 * considered to be outside the bounds.
	 * @param aabb the {@link AABB} to test
	 * @return boolean true if outside the bounds
	 * @since 4.0.0
	 */
	public abstract boolean isOutside(AABB aabb);
}
