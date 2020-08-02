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
package org.dyn4j.world;

import org.dyn4j.collision.CollisionPair;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.TimeStep;

/**
 * Full implementation of both the {@link CollisionWorld} and {@link PhysicsWorld} interfaces.
 * <p>
 * <b>NOTE</b>: This class uses the {@link Body#setOwner(Object)} and 
 * {@link Body#setFixtureModificationHandler(org.dyn4j.collision.FixtureModificationHandler)}
 * methods to handle certain scenarios like fixture removal on a body or bodies added to
 * more than one world. Callers should <b>NOT</b> use the methods.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 */
public class World extends AbstractPhysicsWorld<Body, WorldCollisionData<Body>> {
	/**
	 * Default constructor.
	 */
	public World() {
		this(CollisionWorld.DEFAULT_INITIAL_BODY_CAPACITY, PhysicsWorld.DEFAULT_INITIAL_JOINT_CAPACITY);
	}
	
	/**
	 * Optional constructor.
	 * @param initialBodyCapacity the initial body capacity
	 * @param initialJointCapacity the initial joint capacity
	 */
	public World(int initialBodyCapacity, int initialJointCapacity) {
		super(initialBodyCapacity, initialJointCapacity);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.AbstractCollisionWorld#createCollisionData(org.dyn4j.collision.CollisionPair)
	 */
	@Override
	protected WorldCollisionData<Body> createCollisionData(CollisionPair<Body, BodyFixture> pair) {
		return new WorldCollisionData<Body>(pair);
	}

	/**
	 * Returns the current time step information.
	 * @return {@link TimeStep}
	 * @deprecated Deprecated in 4.0.0. Use the {@link #getTimeStep()} method instead.
	 */
	@Deprecated
	public TimeStep getStep() {
		return this.timeStep;
	}
}
