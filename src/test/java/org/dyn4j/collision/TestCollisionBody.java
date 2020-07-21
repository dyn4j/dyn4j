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

import java.util.List;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Transformable;
import org.dyn4j.geometry.Vector2;

/**
 * Test {@link CollisionBody} class for junit test cases.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 */
public class TestCollisionBody extends AbstractCollisionBody<Fixture> implements CollisionBody<Fixture>, Transformable {
	/**
	 * Default constructor.
	 */
	public TestCollisionBody() {
	}
	
	/**
	 * Full constructor.
	 * @param fixtures the {@link Fixture}s list
	 */
	public TestCollisionBody(List<Fixture> fixtures) {
		this.fixtures.addAll(fixtures);
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * Uses default {@link Fixture} settings.
	 * @param shape the shape to use
	 */
	public TestCollisionBody(Convex shape) {
		this.fixtures.add(new Fixture(shape));
	}
	
	/**
	 * Optional constructor.
	 * @param fixture the fixture to use
	 */
	public TestCollisionBody(Fixture fixture) {
		this.fixtures.add(fixture);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#addFixture(org.dyn4j.geometry.Convex)
	 */
	@Override
	public Fixture addFixture(Convex convex) {
		Fixture bf = new Fixture(convex);
		super.addFixture(bf);
		return bf;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#getLocalCenter()
	 */
	@Override
	public Vector2 getLocalCenter() {
		throw new UnsupportedOperationException();
	}
}
