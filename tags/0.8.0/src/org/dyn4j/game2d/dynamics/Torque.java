/*
 * Copyright (c) 2010, William Bittle
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
package org.dyn4j.game2d.dynamics;

import org.dyn4j.game2d.geometry.Vector;

/**
 * Represents a torque; a {@link Force} applied at a point on a {@link Body}.
 * @author William Bittle
 */
public class Torque extends Force {
	/** The point where the {@link Force} is applied in world coordinates */
	protected Vector point;
	
	/**
	 * Default constructor.
	 */
	public Torque() {
		super();
		this.point = new Vector();
	}
	
	/**
	 * Creates a {@link Torque} using the force components 
	 * and world point coordinates.
	 * @param fx the x component of the force
	 * @param fy the y component of the force
	 * @param px the world space x coordinate of the world space application point
	 * @param py the world space y coordinate of the world space application point
	 */
	public Torque(double fx, double fy, double px, double py) {
		super(fx, fy);
		this.point = new Vector(px, py);
	}
	
	/**
	 * Creates a {@link Torque} using the given force and world
	 * space point.
	 * @param force the force
	 * @param point the world space application point
	 */
	public Torque(Vector force, Vector point) {
		super(force);
		if (point == null) throw new NullPointerException("The torque application point cannot be null.");
		this.point = point;
	}
	
	/**
	 * Copy constructor.
	 * @param torque the {@link Torque} to copy
	 */
	public Torque(Torque torque) {
		if (torque == null) throw new NullPointerException("Cannot copy a null torque.");
		this.force = torque.force.copy();
		this.point = torque.point.copy();
	}
	
	/**
	 * Sets this {@link Torque} to the given force a the given point.
	 * @param fx the x component of the force
	 * @param fy the y component of the force
	 * @param px the x coordinate of the world space application point
	 * @param py the y coordinate of the world space application point
	 */
	public void set(double fx, double fy, double px, double py) {
		this.force.set(fx, fy);
		this.point.set(px, py);
	}
	
	/**
	 * Sets this {@link Torque} to the given force at the given point.
	 * @param force the force vector
	 * @param point the world space application point
	 */
	public void set(Vector force, Vector point) {
		if (force == null) throw new NullPointerException("Cannot set this torque's force vector to a null vector.");
		if (point == null) throw new NullPointerException("Cannot set this torque's application point to a null point.");
		this.force.set(force);
		this.point.set(point);
	}
	
	/**
	 * Sets this {@link Torque} to the given {@link Torque}.
	 * @param torque the {@link Torque} to copy
	 */
	public void set(Torque torque) {
		if (torque == null) throw new NullPointerException("Cannot set this torque to a null torque.");
		this.force.set(torque.force);
		this.point.set(torque.point);
	}
	
	/**
	 * Applies this {@link Torque} to the given {@link Body}.
	 * @param body the {@link Body} to apply the {@link Torque} to
	 */
	public void apply(Body body) {
		super.apply(body);
		body.torque += this.point.difference(body.getWorldCenter()).cross(this.force);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.dynamics.Force#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("TORQUE[")
		.append(this.force).append("|")
		.append(this.point).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns this {@link Torque}'s application point.
	 * @return {@link Vector}
	 */
	public Vector getPoint() {
		return point;
	}
}
