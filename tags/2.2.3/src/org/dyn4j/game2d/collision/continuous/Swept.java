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
package org.dyn4j.game2d.collision.continuous;

import org.dyn4j.game2d.collision.Collidable;
import org.dyn4j.game2d.geometry.Transform;

/**
 * Represents a {@link Collidable} that can take part in continuous collision detection.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.2.0
 */
public interface Swept extends Collidable {
	/**
	 * Returns the initial transformation.  This is the
	 * transformation before integration.
	 * @return {@link Transform} the initial transform
	 */
	public abstract Transform getInitialTransform();
	
	/**
	 * Returns the final transformation.  This is the
	 * transformation after integration.
	 * <p>
	 * This method may return the same transform as
	 * the {@link #getTransform()} method.
	 * @return {@link Transform} the final transform
	 */
	public abstract Transform getFinalTransform();
	
	/**
	 * Returns the maximum radius of the disk that the
	 * {@link Collidable} creates if rotated 360 degrees.
	 * @return double the maximum radius of the rotation disk
	 */
	public abstract double getRotationDiscRadius();
}
