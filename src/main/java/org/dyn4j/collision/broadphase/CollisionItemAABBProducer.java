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
package org.dyn4j.collision.broadphase;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.CollisionItem;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Transform;

/**
 * {@link AABBProducer} for {@link CollisionItem}s (a {@link CollisionBody} and {@link Fixture} pair).
 * <p>
 * This class produces a tight fitting AABB around the {@link CollisionItem}'s {@link Fixture} using the
 * {@link CollisionBody}'s current transform.
 * @author William Bittle
 * @version 4.1.0
 * @since 4.1.0
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 */
public final class CollisionItemAABBProducer<T extends CollisionBody<E>, E extends Fixture> implements AABBProducer<CollisionItem<T, E>> {
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.AABBProducer#compute(java.lang.Object)
	 */
	@Override
	public AABB compute(CollisionItem<T, E> object) {
		CollisionBody<?> body = object.getBody();
		Fixture fixture = object.getFixture();
		Transform tx = body.getTransform();
		AABB aabb = fixture.getShape().createAABB(tx);
		return aabb;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.AABBProducer#compute(java.lang.Object, org.dyn4j.geometry.AABB)
	 */
	@Override
	public void compute(CollisionItem<T, E> object, AABB result) {
		CollisionBody<?> body = object.getBody();
		Fixture fixture = object.getFixture();
		Transform tx = body.getTransform();
		fixture.getShape().computeAABB(tx, result);
		
	}
}
