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
import org.dyn4j.collision.Filter;
import org.dyn4j.collision.Fixture;

/**
 * A default filter for the {@link BroadphaseDetector#detect(BroadphaseFilter)} method that 
 * filters {@link Fixture}s by their {@link Filter}s.
 * <p>
 * This is the default {@link BroadphaseFilter} used in the {@link BroadphaseDetector}s.  Use the methods
 * in the {@link BroadphaseDetector} that accept {@link BroadphaseFilter}a to override the filter.
 * <p>
 * It's recommended that this class be extended when creating custom {@link BroadphaseFilter}s to ensure
 * the default functionality is retained. 
 * @author William Bittle
 * @param <E> the {@link Collidable} type
 * @param <T> the {@link Fixture} type
 * @version 3.2.0
 * @since 3.2.0
 */
public class DefaultBroadphaseFilter<E extends Collidable<T>, T extends Fixture> extends BroadphaseFilterAdapter<E, T> implements BroadphaseFilter<E, T> {
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.broadphase.BroadphaseFilter#isAllowed(org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture, org.dyn4j.collision.Collidable, org.dyn4j.collision.Fixture)
	 */
	@Override
	public boolean isAllowed(E collidable1, T fixture1, E collidable2, T fixture2) {
		Filter filter1 = fixture1.getFilter();
		Filter filter2 = fixture2.getFilter();
		return filter1.isAllowed(filter2);
	}
}
