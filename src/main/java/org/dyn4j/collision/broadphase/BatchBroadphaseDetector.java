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

/**
 * Represents a broad-phase collision detection algorithm that also can perform a batch update operation that is more efficient than updating all the collidable and fixtures one by one.
 * This is initially added because it makes a very big difference for the implementation of LazyAABBTree, but it can be useful in other future broadphase detectors as well.
 * When the World.java class uses a BatchBroadphaseDetector it will just call batchUpdate() once instead of updating each body seperately.
 * 
 * Note that operations of batch addition or deletion did not seem important or comonplace, so the interface just provides a batch update method.
 * 
 * @author Manolis Tsamis
 * @param <E> the {@link Collidable} type
 * @param <T> the {@link Fixture} type
 */
public interface BatchBroadphaseDetector<E extends Collidable<T>, T extends Fixture> extends BroadphaseDetector<E, T> {
	
	/**
	 * Updates all the {@link Fixture}s on all the {@link Collidable}s that have been added to this {@link BroadphaseDetector} up until now.
	 */
	public abstract void batchUpdate();
	
}