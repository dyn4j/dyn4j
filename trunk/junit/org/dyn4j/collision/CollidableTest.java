/*
 * Copyright (c) 2010-2014 William Bittle  http://www.dyn4j.org/
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Transformable;
import org.dyn4j.geometry.Vector2;

/**
 * Test {@link Collidable} class for junit test cases.
 * @author William Bittle
 * @version 3.1.4
 * @since 1.0.0
 */
public class CollidableTest implements Collidable, Transformable {
	/** The unique identifier */
	protected UUID id = UUID.randomUUID();
	
	/** The {@link BodyFixture}s list */
	protected List<BodyFixture> fixtures;
	
	/** The {@link Transform} */
	protected Transform transform;
	
	/**
	 * Full constructor.
	 * @param fixtures the {@link BodyFixture}s list
	 */
	public CollidableTest(List<BodyFixture> fixtures) {
		this.fixtures = fixtures;
		this.transform = new Transform();
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * Uses default {@link BodyFixture} settings.
	 * @param shape the shape to use
	 */
	public CollidableTest(Convex shape) {
		this.fixtures = new ArrayList<BodyFixture>();
		this.fixtures.add(new BodyFixture(shape));
		this.transform = new Transform();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#createAABB()
	 */
	@Override
	public AABB createAABB() {
		// get the number of fixtures
		int size = this.fixtures.size();
		// make sure there is at least one
		if (size > 0) {
			// create the aabb for the first fixture
			AABB aabb = this.fixtures.get(0).getShape().createAABB(this.transform);
			// loop over the remaining fixtures, unioning the aabbs
			for (int i = 1; i < size; i++) {
				// create the aabb for the current fixture
				AABB faabb = this.fixtures.get(i).getShape().createAABB(this.transform);
				// union the aabbs
				aabb.union(faabb);
			}
			// return the aabb
			return aabb;
		}
		return new AABB(0.0, 0.0, 0.0, 0.0);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getId()
	 */
	@Override
	public UUID getId() {
		return this.id;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getFixture(int)
	 */
	@Override
	public Fixture getFixture(int index) {
		int size = this.fixtures.size();
		if (size > 0 && index < size) {
			return this.fixtures.get(index);
		}
		throw new ArrayIndexOutOfBoundsException();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getFixtureCount()
	 */
	@Override
	public int getFixtureCount() {
		return this.fixtures.size();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getFixtures()
	 */
	@Override
	public List<BodyFixture> getFixtures() {
		return this.fixtures;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getTransform()
	 */
	@Override
	public Transform getTransform() {
		return transform;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#rotate(double)
	 */
	@Override
	public void rotate(double theta) {
		this.transform.rotate(theta);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#rotate(double, org.dyn4j.geometry.Vector)
	 */
	@Override
	public void rotate(double theta, Vector2 point) {
		this.transform.rotate(theta, point.x, point.y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#rotate(double, double, double)
	 */
	@Override
	public void rotate(double theta, double x, double y) {
		this.transform.rotate(theta, x, y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		this.transform.translate(x, y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#translate(org.dyn4j.geometry.Vector)
	 */
	@Override
	public void translate(Vector2 vector) {
		this.transform.translate(vector);
	}
}
