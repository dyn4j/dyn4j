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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.dyn4j.DataContainer;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Transformable;
import org.dyn4j.geometry.Vector2;

/**
 * A base implementation of the {@link Collidable} interface.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.2.0
 * @param <T> the {@link Fixture} type
 */
public abstract class AbstractCollidable<T extends Fixture> implements Collidable<T>, Transformable, DataContainer {
	/** The {@link Collidable}'s unique identifier */
	protected final UUID id;
	
	/** The current {@link Transform} */
	protected Transform transform;

	/** The {@link Fixture} list */
	protected List<T> fixtures;
	
	/** The the rotation disk radius */
	protected double radius;
	
	/** The user data associated to this {@link Collidable} */
	protected Object userData;
	
	/**
	 * Default constructor.
	 */
	public AbstractCollidable() {
		this(Collidable.TYPICAL_FIXTURE_COUNT);
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * Creates a new {@link AbstractCollidable} using the given estimated fixture count.
	 * Assignment of the initial fixture count allows sizing of internal structures
	 * for optimal memory/performance.  This estimated fixture count is <b>not</b> a
	 * limit on the number of fixtures.
	 * @param fixtureCount the estimated number of fixtures
	 */
	public AbstractCollidable(int fixtureCount) {
		int size = fixtureCount <= 0 ? Collidable.TYPICAL_FIXTURE_COUNT : fixtureCount;
		this.id = UUID.randomUUID();
		this.fixtures = new ArrayList<T>(size);
		this.radius = 0.0;
		this.transform = new Transform();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof AbstractCollidable) {
			return this.id.equals(((AbstractCollidable<?>)obj).id);
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#removeFixture(org.dyn4j.collision.Fixture)
	 */
	public boolean removeFixture(T fixture) {
		// make sure the passed in fixture is not null
		if (fixture == null) return false;
		// get the number of fixtures
		int size = this.fixtures.size();
		// check fixtures size
		if (size > 0) {
			return this.fixtures.remove(fixture);
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#removeFixture(int)
	 */
	public T removeFixture(int index) {
		return this.fixtures.remove(index);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#removeAllFixtures()
	 */
	public List<T> removeAllFixtures() {
		// return the current list
		List<T> fixtures = this.fixtures;
		// create a new list to replace the current list
		this.fixtures = new ArrayList<T>(fixtures.size());
		// return the current list
		return fixtures;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#containsFixture(org.dyn4j.collision.Fixture)
	 */
	public boolean containsFixture(T fixture) {
		if (fixture == null) return false;
		return this.fixtures.contains(fixture);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#rotate(double, double, double)
	 */
	@Override
	public void rotate(double theta, double x, double y) {
		this.transform.rotate(theta, x, y);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#rotate(double, org.dyn4j.geometry.Vector)
	 */
	@Override
	public void rotate(double theta, Vector2 point) {
		this.transform.rotate(theta, point);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Transformable#rotate(double)
	 */
	@Override
	public void rotate(double theta) {
		this.transform.rotate(theta);
	}
	
	/**
	 * Rotates the {@link Collidable} about its center of mass.
	 * @param theta the angle of rotation in radians
	 */
	public void rotateAboutCenter(double theta) {
		Vector2 center = this.getWorldCenter();
		this.rotate(theta, center);
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#translateToOrigin()
	 */
	public void translateToOrigin() {
		// get the world space center of mass
		Vector2 wc = this.getWorldCenter();
		// translate the collidable negative that much to put it at the origin
		this.transform.translate(-wc.x, -wc.y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	public void shift(Vector2 shift) {
		this.transform.translate(shift);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getFixture(int)
	 */
	public T getFixture(int index) {
		return this.fixtures.get(index);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getFixture(org.dyn4j.geometry.Vector2)
	 */
	public T getFixture(Vector2 point) {
		int size = this.fixtures.size();
		for (int i = 0; i < size; i++) {
			T fixture = this.fixtures.get(i);
			Convex convex = fixture.getShape();
			if (convex.contains(point, this.transform)) {
				return fixture;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getFixtures(org.dyn4j.geometry.Vector2)
	 */
	public List<T> getFixtures(Vector2 point) {
		List<T> fixtures = new ArrayList<T>();
		int size = this.fixtures.size();
		for (int i = 0; i < size; i++) {
			T fixture = this.fixtures.get(i);
			Convex convex = fixture.getShape();
			if (convex.contains(point, this.transform)) {
				fixtures.add(fixture);
			}
		}
		return fixtures;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#removeFixture(org.dyn4j.geometry.Vector2)
	 */
	public T removeFixture(Vector2 point) {
		int size = this.fixtures.size();
		for (int i = 0; i < size; i++) {
			T fixture = this.fixtures.get(i);
			Convex convex = fixture.getShape();
			if (convex.contains(point, this.transform)) {
				this.fixtures.remove(i);
				return fixture;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#removeFixtures(org.dyn4j.geometry.Vector2)
	 */
	public List<T> removeFixtures(Vector2 point) {
		List<T> fixtures = new ArrayList<T>();
		Iterator<T> it = this.fixtures.iterator();
		while (it.hasNext()) {
			T fixture = it.next();
			Convex convex = fixture.getShape();
			if (convex.contains(point, this.transform)) {
				it.remove();
				fixtures.add(fixture);
			}
		}
		return fixtures;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getFixtureCount()
	 */
	public int getFixtureCount() {
		return this.fixtures.size();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getFixtures()
	 */
	public List<T> getFixtures() {
		return Collections.unmodifiableList(this.fixtures);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getFixtureIterator()
	 */
	@Override
	public Iterator<T> getFixtureIterator() {
		return new FixtureIterator<T>(this);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getTransform()
	 */
	public Transform getTransform() {
		return this.transform;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getRotationDiscRadius()
	 */
	public double getRotationDiscRadius() {
		return this.radius;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#setTransform(org.dyn4j.geometry.Transform)
	 */
	public void setTransform(Transform transform) {
		if (transform == null) return;
		this.transform.set(transform);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.DataContainer#getUserData()
	 */
	public Object getUserData() {
		return this.userData;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.DataContainer#setUserData(java.lang.Object)
	 */
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getId()
	 */
	public UUID getId() {
		return this.id;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#createAABB()
	 */
	@Override
	public AABB createAABB() {
		return this.createAABB(this.transform);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#createAABB(org.dyn4j.geometry.Transform)
	 */
	public AABB createAABB(Transform transform) {
		// get the number of fixtures
		int size = this.fixtures.size();
		// make sure there is at least one
		if (size > 0) {
			// create the aabb for the first fixture
			AABB aabb = this.fixtures.get(0).getShape().createAABB(transform);
			// loop over the remaining fixtures, unioning the aabbs
			for (int i = 1; i < size; i++) {
				// create the aabb for the current fixture
				AABB faabb = this.fixtures.get(i).getShape().createAABB(transform);
				// union the aabbs
				aabb.union(faabb);
			}
			// return the aabb
			return aabb;
		}
		return new AABB(new Vector2(0.0, 0.0), new Vector2(0.0, 0.0));
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getLocalPoint(org.dyn4j.geometry.Vector2)
	 */
	public Vector2 getLocalPoint(Vector2 worldPoint) {
		return this.transform.getInverseTransformed(worldPoint);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getWorldPoint(org.dyn4j.geometry.Vector2)
	 */
	public Vector2 getWorldPoint(Vector2 localPoint) {
		return this.transform.getTransformed(localPoint);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getLocalVector(org.dyn4j.geometry.Vector2)
	 */
	public Vector2 getLocalVector(Vector2 worldVector) {
		return this.transform.getInverseTransformedR(worldVector);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#getWorldVector(org.dyn4j.geometry.Vector2)
	 */
	public Vector2 getWorldVector(Vector2 localVector) {
		return this.transform.getTransformedR(localVector);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Collidable#contains(org.dyn4j.geometry.Vector2)
	 */
	public boolean contains(Vector2 point) {
		int size = this.fixtures.size();
		for (int i = 0; i < size; i++) {
			T fixture = this.fixtures.get(i);
			Convex convex = fixture.getShape();
			if (convex.contains(point, this.transform)) {
				return true;
			}
		}
		return false;
	}
}
