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
import org.dyn4j.collision.Filter;
import org.dyn4j.collision.Fixture;

/**
 * The default filter for {@link CollisionWorld}s that filters {@link Fixture}s by 
 * their {@link Filter}s and their enable flags.
 * <p>
 * It's recommended that this class be extended when creating custom {@link BroadphaseCollisionDataFilter}s to ensure
 * the default functionality is retained. 
 * @author William Bittle
 * @param <T> the {@link CollisionBody} type
 * @param <E> the {@link Fixture} type
 * @version 4.1.0
 * @since 4.1.0
 */
public class CollisionBodyBroadphaseCollisionDataFilter<T extends CollisionBody<E>, E extends Fixture> implements BroadphaseCollisionDataFilter<T, E> {
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseFilter#isAllowed(org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture, org.dyn4j.collision.CollisionBody, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean isAllowed(T body1, E fixture1, T body2, E fixture2) {
		// inactive objects don't have collision detection/response
		if (!body1.isEnabled() || !body2.isEnabled()) {
			return false;
		}
		
		// compare the filters
		Filter filter1 = fixture1.getFilter();
		Filter filter2 = fixture2.getFilter();
		return filter1.isAllowed(filter2);
	}
}
