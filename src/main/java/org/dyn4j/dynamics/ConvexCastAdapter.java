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
package org.dyn4j.dynamics;

import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.geometry.Convex;

/**
 * Default implementation of the {@link ConvexCastListener} interface.
 * <p>
 * Inherit from this class to only implement the desired methods.
 * <p>
 * By default all methods return true.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.1.5
 */
public class ConvexCastAdapter implements ConvexCastListener {
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.ConvexCastListener#allow(org.dyn4j.geometry.Convex, org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture)
	 */
	@Override
	public boolean allow(Convex convex, Body body, BodyFixture fixture) { return true; }

	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.ConvexCastListener#allow(org.dyn4j.geometry.Convex, org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture, org.dyn4j.collision.continuous.TimeOfImpact)
	 */
	@Override
	public boolean allow(Convex convex, Body body, BodyFixture fixture, TimeOfImpact toi) { return true; }
}
