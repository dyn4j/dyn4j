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
package org.dyn4j.collision;

import java.util.List;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Transformable;
import org.dyn4j.geometry.Vector2;

/**
 * Test {@link CollisionBody} class for junit test cases.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public class CollidableTest extends AbstractCollisionBody<Fixture> implements CollisionBody<Fixture>, Transformable {
	/**
	 * Full constructor.
	 * @param fixtures the {@link Fixture}s list
	 */
	public CollidableTest(List<Fixture> fixtures) {
		this.fixtures.addAll(fixtures);
		this.transform = new Transform();
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * Uses default {@link Fixture} settings.
	 * @param shape the shape to use
	 */
	public CollidableTest(Convex shape) {
		this.fixtures.add(new Fixture(shape));
		this.transform = new Transform();
	}
	
	/**
	 * Optional constructor.
	 * @param fixture the fixture to use
	 */
	public CollidableTest(Fixture fixture) {
		this.fixtures.add(fixture);
		this.transform = new Transform();
	}

	@Override
	public CollidableTest addFixture(Fixture fixture) {
		this.fixtures.add(fixture);
		return this;
	}

	@Override
	public Fixture addFixture(Convex convex) {
		Fixture bf = new Fixture(convex);
		this.fixtures.add(bf);
		return bf;
	}

	@Override
	public Vector2 getLocalCenter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Vector2 getWorldCenter() {
		throw new UnsupportedOperationException();
	}
}
