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
package org.dyn4j.world;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.Fixture;
import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.manifold.ManifoldSolver;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.world.listener.CollisionListener;

/**
 * Represents a container for all the collision information between a {@link CollisionPair}.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.0.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public interface CollisionData<T extends CollisionBody<E>, E extends Fixture> extends ManifoldCollisionData<T, E>, NarrowphaseCollisionData<T, E>, BroadphaseCollisionData<T, E>, Shiftable {
	/**
	 * Returns true if the {@link CollisionPair} is a broadphase collision.
	 * @see #setBroadphaseCollision(boolean)
	 * @return boolean
	 */
	public boolean isBroadphaseCollision();
	
	/**
	 * Set to true if the {@link CollisionPair} was detected by the {@link BroadphaseDetector} and it was
	 * allowed to continue to the {@link NarrowphaseDetector} stage.
	 * <p>
	 * More specifically, set to true if all of the following conditions were met:
	 * <ul>
	 * <li>The {@link CollisionPair} was detected by the {@link BroadphaseDetector} or was detected in a prior iteration</li>
	 * <li>The {@link CollisionPair} was confirmed to have their {@link AABB}s still overlapping</li>
	 * <li>The {@link CollisionPair} was NOT filtered by the {@link BroadphaseCollisionDataFilter}</li>
	 * <li>The {@link CollisionPair} was NOT filtered by any of the {@link CollisionListener#collision(BroadphaseCollisionData)} method calls</li>
	 * </ul>
	 * @param flag true if the above conditions are met
	 */
	public void setBroadphaseCollision(boolean flag);
	
	/**
	 * Returns true if the {@link CollisionPair} is a narrowphase collision.
	 * @see #setNarrowphaseCollision(boolean)
	 * @return boolean
	 */
	public boolean isNarrowphaseCollision();
	
	/**
	 * Set to true if the {@link CollisionPair} was detected by the {@link NarrowphaseDetector} and it was
	 * allowed to continue to the {@link ManifoldSolver} stage.
	 * <p>
	 * More specifically, set to true if all of the following conditions were met:
	 * <ul>
	 * <li>The {@link CollisionPair} was detected by the {@link NarrowphaseDetector} to be overlapping</li>
	 * <li>The {@link CollisionPair}'s {@link Penetration#getDepth()} is greater than zero</li>
	 * <li>The {@link CollisionPair} was NOT filtered by any of the {@link CollisionListener#collision(NarrowphaseCollisionData)} method calls</li>
	 * </ul>
	 * @param flag true if the above conditions are met
	 */
	public void setNarrowphaseCollision(boolean flag);
	
	/**
	 * Returns true if the {@link CollisionPair} is a manifold collision.
	 * @see #setManifoldCollision(boolean)
	 * @return boolean
	 */
	public boolean isManifoldCollision();
	
	/**
	 * Set to true if the {@link CollisionPair} was detected by the {@link ManifoldSolver}.
	 * <p>
	 * More specifically, set to true if all of the following conditions were met:
	 * <ul>
	 * <li>The {@link CollisionPair} was detected by the {@link ManifoldSolver}</li>
	 * <li>The {@link CollisionPair}'s {@link Manifold} has at least one point</li>
	 * <li>The {@link CollisionPair} was NOT filtered by any of the {@link CollisionListener#collision(ManifoldCollisionData)} method calls</li>
	 * </ul>
	 * @param flag true if the above conditions are met
	 */
	public void setManifoldCollision(boolean flag);
	
	/**
	 * Resets the data in this object to prepare for use in the next detection step.
	 */
	public void reset();
}
