/*
 * Copyright (c) 2010-2023 William Bittle  http://www.dyn4j.org/
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

import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.CollisionPair;
import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.geometry.Vector2;

/**
 * Represents the collision data for the {@link World} class.
 * <p>
 * This will track the broadphase, narrowphase, manifold generation, and contact constraint stages of
 * collision detection. Use the {@link #isBroadphaseCollision()} and similar methods to determine the
 * progress of the collision.
 * @author William Bittle
 * @version 5.0.2
 * @since 4.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public class WorldCollisionData<T extends PhysicsBody> implements ContactCollisionData<T> {
	/** Is it a broadphase collision? */
	private boolean broadphaseCollision;
	
	/** Is it a narrowphase collision? */
	private boolean narrowphaseCollision;
	
	/** Is it a manifold collision? */
	private boolean manifoldCollision;
	
	/** Is it a contact constraint collision? */
	private boolean contactConstraintCollision;
	
	/** The collision pair */
	private final CollisionPair<CollisionItem<T, BodyFixture>> pair;
	
	/** The narrowphase data */
	private final Penetration penetration;
	
	/** The manifold data */
	private final Manifold manifold;
	
	/** The contact data */
	private final ContactConstraint<T> contactConstraint;
	
	// for memory locality
	
	/** The first body */
	private final T body1;
	
	/** The second body */
	private final T body2;
	
	/** The first body's fixture */
	private final BodyFixture fixture1;
	
	/** The second body's fixture */
	private final BodyFixture fixture2;
	
	/**
	 * Minimal constructor.
	 * @param pair the collision pair
	 */
	public WorldCollisionData(CollisionPair<CollisionItem<T, BodyFixture>> pair) {
		this.pair = pair;
		this.penetration = new Penetration();
		this.manifold = new Manifold();
		this.contactConstraint = new ContactConstraint<T>(pair);
		
		this.broadphaseCollision = false;
		this.narrowphaseCollision = false;
		this.manifoldCollision = false;
		this.contactConstraintCollision = false;

		this.body1 = pair.getFirst().getBody();
		this.body2 = pair.getSecond().getBody();
		this.fixture1 = pair.getFirst().getFixture();
		this.fixture2 = pair.getSecond().getFixture();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.BroadphaseCollisionData#getPair()
	 */
	@Override
	public CollisionPair<CollisionItem<T, BodyFixture>> getPair() {
		return this.pair;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.BroadphaseCollisionData#getBody1()
	 */
	@Override
	public T getBody1() {
		return this.body1;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.BroadphaseCollisionData#getFixture1()
	 */
	@Override
	public BodyFixture getFixture1() {
		return this.fixture1;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.BroadphaseCollisionData#getBody2()
	 */
	@Override
	public T getBody2() {
		return this.body2;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.BroadphaseCollisionData#getFixture2()
	 */
	@Override
	public BodyFixture getFixture2() {
		return this.fixture2;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void shift(Vector2 shift) {
		this.manifold.shift(shift);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.ManifoldCollisionData#getManifold()
	 */
	@Override
	public Manifold getManifold() {
		return this.manifold;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.NarrowphaseCollisionData#getPenetration()
	 */
	@Override
	public Penetration getPenetration() {
		return this.penetration;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.ContactCollisionData#getContactConstraint()
	 */
	@Override
	public ContactConstraint<T> getContactConstraint() {
		return this.contactConstraint;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionData#isBroadphaseCollision()
	 */
	@Override
	public boolean isBroadphaseCollision() {
		return this.broadphaseCollision;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionData#setBroadphaseCollision(boolean)
	 */
	@Override
	public void setBroadphaseCollision(boolean flag) {
		this.broadphaseCollision = flag;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionData#isNarrowphaseCollision()
	 */
	@Override
	public boolean isNarrowphaseCollision() {
		return this.narrowphaseCollision;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionData#setNarrowphaseCollision(boolean)
	 */
	@Override
	public void setNarrowphaseCollision(boolean flag) {
		this.narrowphaseCollision = flag;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionData#isManifoldCollision()
	 */
	@Override
	public boolean isManifoldCollision() {
		return this.manifoldCollision;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionData#setManifoldCollision(boolean)
	 */
	@Override
	public void setManifoldCollision(boolean flag) {
		this.manifoldCollision = flag;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.ContactCollisionData#isContactConstraintCollision()
	 */
	@Override
	public boolean isContactConstraintCollision() {
		return this.contactConstraintCollision;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.world.ContactCollisionData#setContactConstraintCollision(boolean)
	 */
	@Override
	public void setContactConstraintCollision(boolean flag) {
		this.contactConstraintCollision = flag;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.world.CollisionData#reset()
	 */
	@Override
	public void reset() {
		this.broadphaseCollision = false;
		this.narrowphaseCollision = false;
		this.manifoldCollision = false;
		this.contactConstraintCollision = false;
		this.penetration.clear();
		this.manifold.clear();
		// don't clear the contact constraint because we need to report
		// ending of contacts based on the old data
	}
}
