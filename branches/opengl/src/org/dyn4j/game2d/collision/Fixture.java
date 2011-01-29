/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d.collision;

import java.util.UUID;

import org.dyn4j.game2d.collision.Filter;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Shape;

/**
 * Represents a part of a {@link Collidable}.
 * @author William Bittle
 * @version 2.2.3
 * @since 2.0.0
 */
public class Fixture {
	/** The id for the fixture */
	protected String id = UUID.randomUUID().toString();
	
	/** The convex shape for this fixture */
	protected Convex shape;
	
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
		if (shape == null) throw new NullPointerException("The shape cannot be null.");
		this.shape = shape;
		this.filter = Filter.DEFAULT_FILTER;
		this.sensor = false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FIXTURE[")
		.append(this.id).append("|")
		.append(this.shape).append("|")
		.append(this.filter).append("|")
		.append(this.sensor).append("|")
		.append(this.userData).append("|")
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the id for this fixture.
	 * @return String
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * The {@link Convex} {@link Shape} representing the
	 * geometry of this fixture.
	 * @return {@link Convex}
	 */
	public Convex getShape() {
		return shape;
	}
	
	/**
	 * Returns the collision filter for this fixture.
	 * @return {@link Filter}
	 */
	public Filter getFilter() {
		return filter;
	}
	
	/**
	 * Sets the collision filter for this fixture.
	 * @param filter the collision filter
	 * @throws NullPointerException if filter is null; Use {@link Filter#DEFAULT_FILTER} instead
	 */
	public void setFilter(Filter filter) {
		// check if the given filter is null
		if (filter == null) throw new NullPointerException("Cannot set a null filter.  Use the Filter.DEFAULT_FILTER instead.");
		// use the given filter
		this.filter = filter;
	}
	
	/**
	 * Returns true if this fixture only senses contact and
	 * does not react to contact.
	 * @return boolean
	 */
	public boolean isSensor() {
		return sensor;
	}
	
	/**
	 * Sets this fixture to only sense contacts if the given
	 * flag is true.
	 * @param flag true if this fixture should only sense contacts
	 */
	public void setSensor(boolean flag) {
		this.sensor = flag;
	}
	
	/**
	 * Returns the user data associated with this fixture.
	 * @return Object
	 */
	public Object getUserData() {
		return userData;
	}
	
	/**
	 * Sets the user data associated with this fixture.
	 * @param userData the user data
	 */
	public void setUserData(Object userData) {
		this.userData = userData;
	}
}
