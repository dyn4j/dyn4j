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

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.dyn4j.DataContainer;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Shiftable;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Transformable;
import org.dyn4j.geometry.Vector2;

/**
 * Represents an object that can collide with other objects.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 * @param <T> the {@link Fixture} type
 * @see AbstractCollidable
 */
public interface Collidable<T extends Fixture> extends Transformable, Shiftable, DataContainer {
	/** Number of fixtures typically attached to a {@link Collidable} */
	public static final int TYPICAL_FIXTURE_COUNT = 1;
	
	/**
	 * Returns a unique identifier for this {@link Collidable}.
	 * <p>
	 * This identifier is constant for the life of this {@link Collidable}.
	 * @return UUID the unique id
	 * @since 3.0.0
	 */
	public abstract UUID getId();
	
	/**
	 * Creates an {@link AABB} from this {@link Collidable}'s attached {@link Fixture}s.
	 * <p>
	 * If there are no fixtures attached, a degenerate AABB, (0.0, 0.0) to (0.0, 0.0), is returned.
	 * @return {@link AABB}
	 * @since 3.0.0
	 */
	public abstract AABB createAABB();
	
	/**
	 * Creates an {@link AABB} from this {@link Collidable}'s attached {@link Fixture}s using the given 
	 * world space {@link Transform}.
	 * <p>
	 * If there are no fixtures attached, a degenerate AABB, (0.0, 0.0) to (0.0, 0.0), is returned.
	 * @param transform the world space {@link Transform}
	 * @return {@link AABB}
	 * @throws NullPointerException if the given transform is null
	 * @since 3.2.0
	 */
	public abstract AABB createAABB(Transform transform);
	
	/**
	 * Adds the given {@link Fixture} to this {@link Collidable}.
	 * @param fixture the {@link Fixture} to add
	 * @return {@link Collidable} this collidable
	 * @since 3.2.0
	 * @throws NullPointerException if fixture is null
	 */
	public abstract Collidable<T> addFixture(T fixture);
	
	/**
	 * Creates a {@link Fixture} for the given {@link Convex} {@link Shape},
	 * adds it to this {@link Collidable}, and returns it.
	 * @param convex the {@link Convex} {@link Shape} to add
	 * @return T the fixture created
	 * @since 3.2.0
	 * @throws NullPointerException if convex is null
	 */
	public abstract T addFixture(Convex convex);
	
	/**
	 * Returns the {@link Fixture} at the given index.
	 * @param index the index of the {@link Fixture}
	 * @return T the fixture
	 * @throws IndexOutOfBoundsException if index is out of bounds
	 * @since 2.0.0
	 */
	public abstract T getFixture(int index);
	
	/**
	 * Returns true if this {@link Collidable} contains the given {@link Fixture}.
	 * @param fixture the fixture
	 * @return boolean
	 * @since 3.2.0
	 */
	public abstract boolean containsFixture(T fixture);
	
	/**
	 * Returns the first {@link Fixture} in this {@link Collidable}, determined by the order in 
	 * which they were added, that contains the given point.
	 * <p>
	 * Returns null if the point is not contained in any fixture in this {@link Collidable}.
	 * @param point a world space point
	 * @return T the fixture or null
	 * @throws NullPointerException if point is null
	 * @since 3.2.0
	 */
	public abstract T getFixture(Vector2 point);

	/**
	 * Returns all the {@link Fixture}s in this {@link Collidable} that contain the given point.
	 * <p>
	 * Returns an empty list if the point is not contained in any fixture in this {@link Collidable}.
	 * @param point a world space point
	 * @return List&lt;T&gt;
	 * @throws NullPointerException if point is null
	 * @since 3.2.0
	 */
	public abstract List<T> getFixtures(Vector2 point);
	
	/**
	 * Removes the given {@link Fixture} from this {@link Collidable}.
	 * @param fixture the {@link Fixture}
	 * @return boolean true if the {@link Fixture} was removed from this {@link Collidable}
	 * @since 3.2.0
	 */
	public abstract boolean removeFixture(T fixture);
	
	/**
	 * Removes the {@link Fixture} at the given index.
	 * @param index the index
	 * @return T the fixture removed
	 * @throws IndexOutOfBoundsException if index is out of bounds
	 * @since 3.2.0
	 */
	public abstract T removeFixture(int index);
	
	/**
	 * Removes all fixtures from this {@link Collidable} and returns them.
	 * @return List&lt;T&gt;
	 * @since 3.2.0
	 */
	public abstract List<T> removeAllFixtures();
	
	/**
	 * Removes the first {@link Fixture} in this {@link Collidable}, determined by the order in
	 * which they were added, that contains the given point and returns it.
	 * <p>
	 * Returns null if the point is not contained in any {@link Fixture} in this {@link Collidable}.
	 * @param point a world space point
	 * @return T the fixture or null
	 * @throws NullPointerException if point is null
	 * @since 3.2.0
	 */
	public abstract T removeFixture(Vector2 point);
	
	/**
	 * Removes all the {@link Fixture}s in this {@link Collidable} that contain the given point and
	 * returns them.
	 * <p>
	 * Returns an empty list if the point is not contained in any {@link Fixture} in this {@link Collidable}.
	 * @param point a world space point
	 * @return List&lt;T&gt;
	 * @throws NullPointerException if point is null
	 * @since 3.2.0
	 */
	public abstract List<T> removeFixtures(Vector2 point);
	
	/**
	 * Returns the number of {@link Fixture}s attached
	 * to this {@link Collidable} object.
	 * @return int
	 * @since 2.0.0
	 */
	public abstract int getFixtureCount();
	
	/**
	 * Returns an unmodifiable list containing the {@link Fixture}s attached to this {@link Collidable}.
	 * <p>
	 * The returned list is backed by the internal list, therefore adding or removing fixtures while 
	 * iterating through the returned list is not permitted.  Use the {@link #getFixtureIterator()}
	 * method instead.
	 * @return List&lt;T&gt;
	 * @since 3.1.5
	 * @see #getFixtureIterator()
	 */
	public abstract List<T> getFixtures();
	
	/**
	 * Returns an iterator for this collidable's fixtures.
	 * <p>
	 * The returned iterator supports the <code>remove</code> method.
	 * @return Iterator&lt;T&gt;
	 * @since 3.2.0
	 */
	public abstract Iterator<T> getFixtureIterator();
	
	/**
	 * Returns true if the given world space point is contained in this {@link Collidable}.
	 * <p>
	 * The point is contained in this {@link Collidable} if and only if the point is contained
	 * in one of this {@link Collidable}'s {@link Fixture}s.
	 * @param point the world space test point
	 * @return boolean
	 * @throws NullPointerException if point is null
	 * @since 3.2.0
	 */
	public abstract boolean contains(Vector2 point);

	/**
	 * Returns the center for this {@link Collidable} in local coordinates.
	 * @return {@link Vector2} the center in local coordinates
	 * @since 3.2.0
	 */
	public abstract Vector2 getLocalCenter();
	
	/**
	 * Returns the center for this {@link Collidable} in world coordinates.
	 * @return {@link Vector2} the center in world coordinates
	 * @since 3.2.0
	 */
	public abstract Vector2 getWorldCenter();
	
	/**
	 * Returns a new point in local coordinates of this {@link Collidable} given
	 * a point in world coordinates.
	 * @param worldPoint a world space point
	 * @return {@link Vector2} local space point
	 * @throws NullPointerException if the given point is null
	 * @since 3.2.0
	 */
	public abstract Vector2 getLocalPoint(Vector2 worldPoint);
	
	/**
	 * Returns a new point in world coordinates given a point in the
	 * local coordinates of this {@link Collidable}.
	 * @param localPoint a point in the local coordinates of this {@link Collidable}
	 * @return {@link Vector2} world space point
	 * @throws NullPointerException if the given point is null
	 * @since 3.2.0
	 */
	public abstract Vector2 getWorldPoint(Vector2 localPoint);
	
	/**
	 * Returns a new vector in local coordinates of this {@link Collidable} given
	 * a vector in world coordinates.
	 * @param worldVector a world space vector
	 * @return {@link Vector2} local space vector
	 * @throws NullPointerException if the given vector is null
	 * @since 3.2.0
	 */
	public abstract Vector2 getLocalVector(Vector2 worldVector);
	
	/**
	 * Returns a new vector in world coordinates given a vector in the
	 * local coordinates of this {@link Collidable}.
	 * @param localVector a vector in the local coordinates of this {@link Collidable}
	 * @return {@link Vector2} world space vector
	 * @throws NullPointerException if the given vector is null
	 * @since 3.2.0
	 */
	public abstract Vector2 getWorldVector(Vector2 localVector);

	/**
	 * Returns the maximum radius of the disk that the
	 * {@link Collidable} creates if rotated 360 degrees about its center.
	 * @return double the maximum radius of the rotation disk
	 */
	public abstract double getRotationDiscRadius();
	
	/**
	 * Returns the local to world space {@link Transform} of this {@link Collidable}.
	 * @return {@link Transform}
	 */
	public abstract Transform getTransform();
	
	/**
	 * Sets this {@link Collidable}'s local to world space {@link Transform}.
	 * <p>
	 * If the given transform is null, this method returns immediately.
	 * @param transform the transform
	 * @since 3.2.0
	 */
	public abstract void setTransform(Transform transform);
	
	/**
	 * Rotates the {@link Collidable} about its center.
	 * @param theta the angle of rotation in radians
	 */
	public abstract void rotateAboutCenter(double theta);

	/**
	 * Translates the center of the {@link Collidable} to the world space origin (0,0).
	 * <p>
	 * This method is useful if bodies have a number of fixtures and the center
	 * is not at the origin.  This method will reposition this {@link Collidable} so 
	 * that the center is at the origin.
	 * @since 3.2.0
	 */
	public abstract void translateToOrigin();
}
