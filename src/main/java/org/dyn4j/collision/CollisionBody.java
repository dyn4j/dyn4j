/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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

import java.util.Iterator;
import java.util.List;

import org.dyn4j.Copyable;
import org.dyn4j.DataContainer;
import org.dyn4j.Ownable;
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
 * @version 6.0.0
 * @since 4.0.0
 * @param <T> the {@link Fixture} type
 * @see AbstractCollisionBody
 */
public interface CollisionBody<T extends Fixture> extends Transformable, Shiftable, DataContainer, Ownable, Copyable<CollisionBody<T>> {
	/** Number of fixtures typically attached to a {@link CollisionBody} */
	public static final int TYPICAL_FIXTURE_COUNT = 1;
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * NOTE: The userData, fixtureModificationHandler, and owner fields are 
	 * not copied and left null in the copy.  The fixtureModificationHandler
	 * and owner fields are used internally to when the body is added to a
	 * World.  If you want the copy to be added to a World, you must add it
	 * manually after copying it.
	 * @since 6.0.0
	 */
	@Override
	public CollisionBody<T> copy();

	/**
	 * Adds the given {@link Fixture} to this {@link CollisionBody}.
	 * @param fixture the {@link Fixture} to add
	 * @return {@link CollisionBody} this body
	 * @since 3.2.0
	 * @throws NullPointerException if fixture is null
	 */
	public abstract CollisionBody<T> addFixture(T fixture);
	
	/**
	 * Creates a {@link Fixture} for the given {@link Convex} {@link Shape},
	 * adds it to this {@link CollisionBody}, and returns it.
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
	 * Returns true if this {@link CollisionBody} contains the given {@link Fixture}.
	 * @param fixture the fixture
	 * @return boolean
	 * @since 3.2.0
	 */
	public abstract boolean containsFixture(T fixture);
	
	/**
	 * Returns the first {@link Fixture} in this {@link CollisionBody}, determined by the order in 
	 * which they were added, that contains the given point.
	 * <p>
	 * Returns null if the point is not contained in any fixture in this {@link CollisionBody}.
	 * @param point a world space point
	 * @return T the fixture or null
	 * @throws NullPointerException if point is null
	 * @since 3.2.0
	 */
	public abstract T getFixture(Vector2 point);

	/**
	 * Returns all the {@link Fixture}s in this {@link CollisionBody} that contain the given point.
	 * <p>
	 * Returns an empty list if the point is not contained in any fixture in this {@link CollisionBody}.
	 * @param point a world space point
	 * @return List&lt;T&gt;
	 * @throws NullPointerException if point is null
	 * @since 3.2.0
	 */
	public abstract List<T> getFixtures(Vector2 point);
	
	/**
	 * Removes the given {@link Fixture} from this {@link CollisionBody}.
	 * @param fixture the {@link Fixture}
	 * @return boolean true if the {@link Fixture} was removed from this {@link CollisionBody}
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
	 * Removes all fixtures from this {@link CollisionBody} and returns them.
	 * @return List&lt;T&gt;
	 * @since 3.2.0
	 */
	public abstract List<T> removeAllFixtures();
	
	/**
	 * Removes the first {@link Fixture} in this {@link CollisionBody}, determined by the order in
	 * which they were added, that contains the given point and returns it.
	 * <p>
	 * Returns null if the point is not contained in any {@link Fixture} in this {@link CollisionBody}.
	 * @param point a world space point
	 * @return T the fixture or null
	 * @throws NullPointerException if point is null
	 * @since 3.2.0
	 */
	public abstract T removeFixture(Vector2 point);
	
	/**
	 * Removes all the {@link Fixture}s in this {@link CollisionBody} that contain the given point and
	 * returns them.
	 * <p>
	 * Returns an empty list if the point is not contained in any {@link Fixture} in this {@link CollisionBody}.
	 * @param point a world space point
	 * @return List&lt;T&gt;
	 * @throws NullPointerException if point is null
	 * @since 3.2.0
	 */
	public abstract List<T> removeFixtures(Vector2 point);
	
	/**
	 * Returns the number of {@link Fixture}s attached
	 * to this {@link CollisionBody} object.
	 * @return int
	 * @since 2.0.0
	 */
	public abstract int getFixtureCount();
	
	/**
	 * Returns an unmodifiable list containing the {@link Fixture}s attached to this {@link CollisionBody}.
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
	 * Returns an iterator for this body's fixtures.
	 * <p>
	 * The returned iterator supports the <code>remove</code> method.
	 * @return Iterator&lt;T&gt;
	 * @since 3.2.0
	 */
	public abstract Iterator<T> getFixtureIterator();

	/**
	 * Returns true if the given world space point is contained in this {@link CollisionBody}.
	 * <p>
	 * The point is contained in this {@link CollisionBody} if and only if the point is contained
	 * in one of this {@link CollisionBody}'s {@link Fixture}s.
	 * @param point the world space test point
	 * @return boolean
	 * @throws NullPointerException if point is null
	 * @since 3.2.0
	 */
	public abstract boolean contains(Vector2 point);

	/**
	 * Returns the center for this {@link CollisionBody} in local coordinates.
	 * @return {@link Vector2} the center in local coordinates
	 * @since 3.2.0
	 */
	public abstract Vector2 getLocalCenter();
	
	/**
	 * Returns the center for this {@link CollisionBody} in world coordinates.
	 * @return {@link Vector2} the center in world coordinates
	 * @since 3.2.0
	 */
	public abstract Vector2 getWorldCenter();
	
	/**
	 * Returns a new point in local coordinates of this {@link CollisionBody} given
	 * a point in world coordinates.
	 * @param worldPoint a world space point
	 * @return {@link Vector2} local space point
	 * @throws NullPointerException if the given point is null
	 * @since 3.2.0
	 */
	public abstract Vector2 getLocalPoint(Vector2 worldPoint);
	
	/**
	 * Converts the given <code>worldPoint</code> into local coordinates of this {@link CollisionBody}
	 * and places the result in the given <code>destination</code>.
	 * @param worldPoint a point in world coordinates
	 * @param destination the vector to put the result
	 * @throws NullPointerException if the given vector is null
	 * @since 6.0.0
	 */
	public abstract void getLocalPoint(Vector2 worldPoint, Vector2 destination);
	
	/**
	 * Returns a new point in world coordinates given a point in the
	 * local coordinates of this {@link CollisionBody}.
	 * @param localPoint a point in the local coordinates of this {@link CollisionBody}
	 * @return {@link Vector2} world space point
	 * @throws NullPointerException if the given point is null
	 * @since 3.2.0
	 */
	public abstract Vector2 getWorldPoint(Vector2 localPoint);
	
	/**
	 * Converts the given <code>localPoint</code> into world coordinates of this {@link CollisionBody}
	 * and places the result in the given <code>destination</code>.
	 * @param localPoint a point in the local coordinates of this {@link CollisionBody}
	 * @param destination the vector to put the result
	 * @throws NullPointerException if the given vector is null
	 * @since 6.0.0
	 */
	public abstract void getWorldPoint(Vector2 localPoint, Vector2 destination);
	
	/**
	 * Returns a new vector in local coordinates of this {@link CollisionBody} given
	 * a vector in world coordinates.
	 * @param worldVector a world space vector
	 * @return {@link Vector2} local space vector
	 * @throws NullPointerException if the given vector is null
	 * @since 3.2.0
	 */
	public abstract Vector2 getLocalVector(Vector2 worldVector);
	
	/**
	 * Converts the given <code>worldVector</code> into local coordinates of this {@link CollisionBody}
	 * and places the result in the given <code>destination</code>.
	 * @param worldVector a vector in world coordinates
	 * @param destination the vector to put the result
	 * @throws NullPointerException if the given vector is null
	 * @since 6.0.0
	 */
	public abstract void getLocalVector(Vector2 worldVector, Vector2 destination);
	
	/**
	 * Returns a new vector in world coordinates given a vector in the
	 * local coordinates of this {@link CollisionBody}.
	 * @param localVector a vector in the local coordinates of this {@link CollisionBody}
	 * @return {@link Vector2} world space vector
	 * @throws NullPointerException if the given vector is null
	 * @since 3.2.0
	 */
	public abstract Vector2 getWorldVector(Vector2 localVector);
	
	/**
	 * Converts the given <code>localVector</code> into world coordinates of this {@link CollisionBody}
	 * and places the result in the given <code>destination</code>.
	 * @param localVector a vector in the local coordinates of this {@link CollisionBody}
	 * @param destination the vector to put the result
	 * @throws NullPointerException if the given vector is null
	 * @since 6.0.0
	 */
	public abstract void getWorldVector(Vector2 localVector, Vector2 destination);

	/**
	 * Returns the maximum radius of the disk that the
	 * {@link CollisionBody} creates if rotated 360 degrees about its center.
	 * @return double the maximum radius of the rotation disk
	 */
	public abstract double getRotationDiscRadius();
	
	/**
	 * Returns the local to world space {@link Transform} of this {@link CollisionBody}.
	 * @return {@link Transform}
	 */
	public abstract Transform getTransform();

	/**
	 * Returns the transform of the last iteration.
	 * <p>
	 * This transform represents the last frame's position and
	 * orientation.
	 * @return {@link Transform}
	 */
	public Transform getPreviousTransform();
	
	/**
	 * Sets this {@link CollisionBody}'s local to world space {@link Transform}.
	 * <p>
	 * If the given transform is null, nothing is done and this method returns immediately.
	 * @param transform the transform
	 * @since 3.2.0
	 */
	public abstract void setTransform(Transform transform);
	
	/**
	 * Rotates the {@link CollisionBody} about its center.
	 * @param theta the angle of rotation in radians
	 */
	public abstract void rotateAboutCenter(double theta);

	/**
	 * Translates the center of the {@link CollisionBody} to the world space origin (0,0).
	 * <p>
	 * This method is useful if bodies have a number of fixtures and the center
	 * is not at the origin.  This method will reposition this {@link CollisionBody} so 
	 * that the center is at the origin.
	 * @since 3.2.0
	 */
	public abstract void translateToOrigin();
	
	/**
	 * Creates an {@link AABB} from this {@link CollisionBody}'s attached {@link Fixture}s.
	 * <p>
	 * If there are no fixtures attached, a degenerate AABB, (0.0, 0.0) to (0.0, 0.0), is returned.
	 * @return {@link AABB}
	 * @since 3.0.0
	 */
	public abstract AABB createAABB();
	
	/**
	 * Creates an {@link AABB} from this {@link CollisionBody}'s attached {@link Fixture}s using the given 
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
	 * Computes an {@link AABB} from this {@link CollisionBody}'s attached {@link Fixture}s and places
	 * the result in the given AABB.
	 * <p>
	 * If there are no fixtures attached, the result is set to a degenerate AABB, (0.0, 0.0) to (0.0, 0.0).
	 * @param result the AABB to set
	 * @throws NullPointerException if the given AABB is null
	 * @since 4.1.0
	 */
	public abstract void computeAABB(AABB result);
	
	/**
	 * Computes an {@link AABB} from this {@link CollisionBody}'s attached {@link Fixture}s using the given 
	 * world space {@link Transform} and places the result in the given AABB.
	 * <p>
	 * If there are no fixtures attached, the result is set to a degenerate AABB, (0.0, 0.0) to (0.0, 0.0).
	 * @param transform the world space {@link Transform}
	 * @param result the AABB to set
	 * @throws NullPointerException if the given transform or AABB is null
	 * @since 4.1.0
	 */
	public abstract void computeAABB(Transform transform, AABB result);
	
	/**
	 * Sets the {@link CollisionBody} enabled or not.
	 * <p>
	 * A disabled {@link CollisionBody} is completely ignored by the engine.
	 * <p>
	 * A {@link CollisionBody} will be disabled by only one condition from within
	 * the engine - when it's out of bounds. If there's no bounds detection
	 * set, then this state will never be set automatically.
	 * @param enabled true if the {@link CollisionBody} should be enabled
	 * @since 4.0.0
	 */
	public abstract void setEnabled(boolean enabled);
	
	/**
	 * Returns true if this {@link CollisionBody} is enabled.
	 * @return boolean
	 * @since 4.0.0
	 */
	public abstract boolean isEnabled();

	// internal API
	
	/**
	 * Returns the {@link FixtureModificationHandler} for this body.
	 * @return {@link FixtureModificationHandler}
	 * @since 4.0.0
	 */
	public abstract FixtureModificationHandler<T> getFixtureModificationHandler();
	
	/**
	 * Sets the {@link FixtureModificationHandler} for this body.
	 * @param handler the handler
	 * @since 4.0.0
	 */
	public abstract void setFixtureModificationHandler(FixtureModificationHandler<T> handler);
}
