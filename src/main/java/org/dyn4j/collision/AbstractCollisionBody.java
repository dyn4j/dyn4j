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
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.dyn4j.DataContainer;
import org.dyn4j.Ownable;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Rotation;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Transformable;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * A base implementation of the {@link CollisionBody} interface.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 * @param <T> the {@link Fixture} type
 */
public abstract class AbstractCollisionBody<T extends Fixture> implements CollisionBody<T>, Transformable, DataContainer, Ownable {
	/** The current {@link Transform} */
	protected Transform transform;

	/** The {@link Fixture} list */
	protected final List<T> fixtures;
	
	/** An unmodifiable view of the fixtures on this body */
	protected final List<T> fixturesUnmodifiable;
	
	/** The the rotation disk radius */
	protected double radius;
	
	/** The user data associated to this {@link CollisionBody} */
	protected Object userData;
	
	/** True if the body is enabled */
	protected boolean enabled;
	
	/** [INTERNAL] Used for notifcation of fixture modification events */
	protected FixtureModificationHandler<T> fixtureModificationHandler;
	
	/** [INTERNAL] User for ownership by another object */
	protected Object owner;
	
	/**
	 * Default constructor.
	 */
	public AbstractCollisionBody() {
		this(CollisionBody.TYPICAL_FIXTURE_COUNT);
	}
	
	/**
	 * Optional constructor.
	 * <p>
	 * Creates a new {@link AbstractCollisionBody} using the given estimated fixture count.
	 * Assignment of the initial fixture count allows sizing of internal structures
	 * for optimal memory/performance.  This estimated fixture count is <b>not</b> a
	 * limit on the number of fixtures.
	 * @param fixtureCount the estimated number of fixtures
	 */
	public AbstractCollisionBody(int fixtureCount) {
		int size = fixtureCount <= 0 ? CollisionBody.TYPICAL_FIXTURE_COUNT : fixtureCount;
		this.fixtures = new ArrayList<T>(size);
		this.fixturesUnmodifiable = Collections.unmodifiableList(this.fixtures);
		this.radius = 0.0;
		this.transform = new Transform();
		this.enabled = true;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#addFixture(org.dyn4j.collision.Fixture)
	 */
	@Override
	public CollisionBody<T> addFixture(T fixture) {
		// make sure neither is null
		if (fixture == null) throw new NullPointerException(Messages.getString("dynamics.body.addNullFixture"));
		// add the shape and mass to the respective lists
		this.fixtures.add(fixture);
		if (this.fixtureModificationHandler != null) {
			this.fixtureModificationHandler.onFixtureAdded(fixture);
		}
		// return this body to facilitate chaining
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#removeFixture(org.dyn4j.collision.Fixture)
	 */
	public boolean removeFixture(T fixture) {
		// because the fixture list contains no nulls, this handles the case fixture == null as well
		boolean wasRemoved = this.fixtures.remove(fixture);
		if (wasRemoved && this.fixtureModificationHandler != null) {
			this.fixtureModificationHandler.onFixtureRemoved(fixture);
		}
		return wasRemoved;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#removeFixture(int)
	 */
	public T removeFixture(int index) {
		T fixture = this.fixtures.remove(index);
		if (fixture != null && this.fixtureModificationHandler != null) {
			this.fixtureModificationHandler.onFixtureRemoved(fixture);
		}
		return fixture;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#removeAllFixtures()
	 */
	public List<T> removeAllFixtures() {
		// return the current list
		List<T> fixtures = new ArrayList<T>(this.fixtures);
		// create a new list to replace the current list
		this.fixtures.clear();
		if (this.fixtureModificationHandler != null) {
			this.fixtureModificationHandler.onAllFixturesRemoved();
		}
		// return the current list
		return fixtures;
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#removeFixture(org.dyn4j.geometry.Vector2)
	 */
	public T removeFixture(Vector2 point) {
		int size = this.fixtures.size();
		for (int i = 0; i < size; i++) {
			T fixture = this.fixtures.get(i);
			Convex convex = fixture.getShape();
			if (convex.contains(point, this.transform)) {
				this.fixtures.remove(i);
				if (this.fixtureModificationHandler != null) {
					this.fixtureModificationHandler.onFixtureRemoved(fixture);
				}
				return fixture;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#removeFixtures(org.dyn4j.geometry.Vector2)
	 */
	public List<T> removeFixtures(Vector2 point) {
		List<T> fixtures = new ArrayList<T>();
		Iterator<T> it = this.fixtures.iterator();
		while (it.hasNext()) {
			T fixture = it.next();
			Convex convex = fixture.getShape();
			if (convex.contains(point, this.transform)) {
				it.remove();
				if (this.fixtureModificationHandler != null) {
					this.fixtureModificationHandler.onFixtureRemoved(fixture);
				}
				fixtures.add(fixture);
			}
		}
		return fixtures;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#containsFixture(org.dyn4j.collision.Fixture)
	 */
	public boolean containsFixture(T fixture) {
		// because the fixture list contains no nulls, this handles the case fixture == null as well
		return this.fixtures.contains(fixture);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#getFixture(int)
	 */
	public T getFixture(int index) {
		return this.fixtures.get(index);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#getFixture(org.dyn4j.geometry.Vector2)
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
	 * @see org.dyn4j.collision.CollisionBody#getFixtures(org.dyn4j.geometry.Vector2)
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
	 * @see org.dyn4j.collision.CollisionBody#getFixtureCount()
	 */
	public int getFixtureCount() {
		return this.fixtures.size();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#getFixtures()
	 */
	public List<T> getFixtures() {
		return this.fixturesUnmodifiable;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#getFixtureIterator()
	 */
	@Override
	public Iterator<T> getFixtureIterator() {
		return new FixtureIterator();
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Rotatable#rotate(double, double, double)
	 */
	@Override
	public void rotate(double theta, double x, double y) {
		this.transform.rotate(theta, x, y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Rotatable#rotate(org.dyn4j.geometry.Rotation, double, double)
	 */
	@Override
	public void rotate(Rotation rotation, double x, double y) {
		this.transform.rotate(rotation, x, y);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Rotatable#rotate(double, org.dyn4j.geometry.Vector)
	 */
	@Override
	public void rotate(double theta, Vector2 point) {
		this.transform.rotate(theta, point);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Rotatable#rotate(org.dyn4j.geometry.Rotation, org.dyn4j.geometry.Vector)
	 */
	@Override
	public void rotate(Rotation rotation, Vector2 point) {
		this.transform.rotate(rotation, point);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Rotatable#rotate(double)
	 */
	@Override
	public void rotate(double theta) {
		this.transform.rotate(theta);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Rotatable#rotate(org.dyn4j.geometry.Rotation)
	 */
	@Override
	public void rotate(Rotation rotation) {
		this.transform.rotate(rotation);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#rotateAboutCenter(double)
	 */
	public void rotateAboutCenter(double theta) {
		Vector2 center = this.getWorldCenter();
		this.rotate(theta, center);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Translatable#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		this.transform.translate(x, y);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Translatable#translate(org.dyn4j.geometry.Vector)
	 */
	@Override
	public void translate(Vector2 vector) {
		this.transform.translate(vector);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#translateToOrigin()
	 */
	public void translateToOrigin() {
		// get the world space center of mass
		Vector2 wc = this.getWorldCenter();
		// translate the body negative that much to put it at the origin
		this.transform.translate(-wc.x, -wc.y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Shiftable#shift(org.dyn4j.geometry.Vector2)
	 */
	public void shift(Vector2 shift) {
		this.transform.translate(shift);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#getTransform()
	 */
	public Transform getTransform() {
		return this.transform;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#getRotationDiscRadius()
	 */
	public double getRotationDiscRadius() {
		return this.radius;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#setTransform(org.dyn4j.geometry.Transform)
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
	 * @see org.dyn4j.collision.CollisionBody#createAABB()
	 */
	@Override
	public AABB createAABB() {
		return this.createAABB(this.transform);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#createAABB(org.dyn4j.geometry.Transform)
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
		return new AABB(0.0, 0.0, 0.0, 0.0);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#getLocalPoint(org.dyn4j.geometry.Vector2)
	 */
	public Vector2 getLocalPoint(Vector2 worldPoint) {
		return this.transform.getInverseTransformed(worldPoint);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#getWorldPoint(org.dyn4j.geometry.Vector2)
	 */
	public Vector2 getWorldPoint(Vector2 localPoint) {
		return this.transform.getTransformed(localPoint);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#getLocalVector(org.dyn4j.geometry.Vector2)
	 */
	public Vector2 getLocalVector(Vector2 worldVector) {
		return this.transform.getInverseTransformedR(worldVector);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#getWorldVector(org.dyn4j.geometry.Vector2)
	 */
	public Vector2 getWorldVector(Vector2 localVector) {
		return this.transform.getTransformedR(localVector);
	}

	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#getWorldCenter()
	 */
	@Override
	public Vector2 getWorldCenter() {
		return this.transform.getTransformed(this.getLocalCenter());
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#contains(org.dyn4j.geometry.Vector2)
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
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Ownable#getOwner()
	 */
	@Override
	public Object getOwner() {
		return this.owner;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Ownable#setOwner(java.lang.Object)
	 */
	@Override
	public void setOwner(Object owner) {
		this.owner = owner;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#getFixtureModificationHandler()
	 */
	@Override
	public FixtureModificationHandler<T> getFixtureModificationHandler() {
		return this.fixtureModificationHandler;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.CollisionBody#setFixtureModificationHandler(org.dyn4j.collision.FixtureModificationHandler)
	 */
	@Override
	public void setFixtureModificationHandler(FixtureModificationHandler<T> handler) {
		this.fixtureModificationHandler = handler;
	}
	
	/**
	 * Represents an iterator for {@link Fixture}s in a {@link CollisionBody}.
	 * @author William Bittle
	 * @version 4.0.0
	 * @since 4.0.0
	 */
	private final class FixtureIterator implements Iterator<T> {
		/** The current index */
		private int index;
		
		/**
		 * Default constructor.
		 */
		public FixtureIterator() {
			this.index = -1;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.index + 1 < AbstractCollisionBody.this.fixtures.size();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {
			if (this.index >= AbstractCollisionBody.this.fixtures.size()) {
				throw new IndexOutOfBoundsException();
			}
			try {
				this.index++;
				T fixture = AbstractCollisionBody.this.fixtures.get(this.index);
				return fixture;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			if (this.index < 0) {
				throw new IllegalStateException();
			}
			if (this.index >= AbstractCollisionBody.this.fixtures.size()) {
				throw new IndexOutOfBoundsException();
			}
			try {
				AbstractCollisionBody.this.removeFixture(this.index);
				this.index--;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}
}
