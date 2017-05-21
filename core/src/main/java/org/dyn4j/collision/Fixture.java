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

import java.util.UUID;

import org.dyn4j.DataContainer;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;
import org.dyn4j.resources.Messages;

/**
 * Represents a geometric piece of a {@link Collidable}.
 * <p>
 * A {@link Fixture} has a one-to-one relationship with a {@link Convex} {@link Shape}, storing
 * additional collision specific information.
 * <p>
 * A {@link Collidable} is composed of many {@link Fixture}s to represent its physical shape. While
 * the only shapes supported by the collision detection system are {@link Convex} shapes, the composition
 * of multiple {@link Fixture}s in a {@link Collidable} allows the collidables to be non-convex.
 * <p>
 * The {@link Fixture}'s {@link Shape} should be translated and rotated using the {@link Shape}'s methods
 * to move the shape relative to the containing {@link Collidable}.  Other modifications to the shape is
 * not recommended after adding it to a {@link Fixture}. To change the shape of a fixture, remove the existing 
 * {@link Fixture} from the {@link Collidable} and add a new {@link Fixture} with an updated shape instead.
 * <p>
 * There's no restriction on reusing {@link Shape}s and {@link Fixture}s between {@link Collidable}s, but 
 * this is also discouraged to reduce confusion and unexpected behavior (primarily local translations and
 * rotations).
 * <p>
 * A {@link Fixture} can have a {@link Filter} assigned to enable filtering of collisions between it
 * and other fixtures.
 * <p>
 * A {@link Fixture} can be flagged as a sensor fixture to enable standard collision detection, but disable
 * collision resolution (response).
 * @author William Bittle
 * @version 3.2.0
 * @since 2.0.0
 */
public class Fixture implements DataContainer {
	/** The id for the fixture */
	protected final UUID id;
	
	/** The convex shape for this fixture */
	protected final Convex shape;
	
	/** The collision filter */
	protected Filter filter;
	
	/** Whether the fixture only senses contact */
	protected boolean sensor;
	
	/** The user data */
	protected Object userData;
	
	/**
	 * Minimal constructor.
	 * @param shape the {@link Convex} {@link Shape} for this fixture
	 * @throws NullPointerException if shape is null
	 */
	public Fixture(Convex shape) {
		if (shape == null) throw new NullPointerException(Messages.getString("collision.fixture.nullShape"));
		this.id = UUID.randomUUID();
		this.shape = shape;
		this.filter = Filter.DEFAULT_FILTER;
		this.sensor = false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Fixture) {
			return this.id.equals(((Fixture)obj).id);
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Fixture[Id=").append(this.id)
		.append("|Shape=").append(this.shape)
		.append("|Filter=").append(this.filter)
		.append("|IsSensor=").append(this.sensor)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the id for this fixture.
	 * <p>
	 * This identifier is constant for the life of this {@link Fixture}.
	 * @return UUID
	 */
	public UUID getId() {
		return this.id;
	}
	
	/**
	 * The {@link Convex} {@link Shape} representing the geometry of this fixture.
	 * @return {@link Convex}
	 */
	public Convex getShape() {
		return this.shape;
	}
	
	/**
	 * Returns the collision filter for this fixture.
	 * @return {@link Filter}
	 */
	public Filter getFilter() {
		return this.filter;
	}
	
	/**
	 * Sets the collision filter for this fixture.
	 * @param filter the collision filter
	 * @throws NullPointerException if filter is null; Use {@link Filter#DEFAULT_FILTER} instead
	 */
	public void setFilter(Filter filter) {
		// check if the given filter is null
		if (filter == null) throw new NullPointerException(Messages.getString("collision.fixture.nullFilter"));
		// use the given filter
		this.filter = filter;
	}
	
	/**
	 * Returns true if this fixture is a sensor.
	 * <p>
	 * A sensor fixture is a fixture that participates in collision detection but does not
	 * participate in collision resolution (response).
	 * @return boolean
	 */
	public boolean isSensor() {
		return this.sensor;
	}
	
	/**
	 * Toggles this fixture as a sensor fixture.
	 * <p>
	 * A sensor fixture is a fixture that participates in collision detection but does not
	 * participate in collision resolution (response).
	 * @param flag true if this fixture should only sense contacts
	 */
	public void setSensor(boolean flag) {
		this.sensor = flag;
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
}
