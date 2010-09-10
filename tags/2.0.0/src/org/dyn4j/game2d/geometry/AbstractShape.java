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
package org.dyn4j.game2d.geometry;

import java.util.UUID;

/**
 * Base implementation of the {@link Shape} interface.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public abstract class AbstractShape implements Shape, Transformable {
	/** The shape's unique identifier */
	protected String id = UUID.randomUUID().toString();
	
	/** The center of this {@link Shape} */
	protected Vector2 center;
	
	/** The maximum radius */
	protected double radius;
	
	/** Custom user data object */
	protected Object userData;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.id).append("|")
		.append(this.center);
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Shape#isType(org.dyn4j.game2d.geometry.Shape.Type)
	 */
	@Override
	public boolean isType(Type type) {
		// return the result
		return this.getType().is(type);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Shape#getId()
	 */
	@Override
	public String getId() {
		return this.id;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Shape#getCenter()
	 */
	@Override
	public Vector2 getCenter() {
		return this.center;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Shape#getRadius()
	 */
	@Override
	public double getRadius() {
		return this.radius;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Shape#getUserData()
	 */
	@Override
	public Object getUserData() {
		return this.userData;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Shape#setUserData(java.lang.Object)
	 */
	@Override
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Shape#rotate(double)
	 */
	@Override
	public void rotate(double theta) {
		this.rotate(theta, this.center);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#rotate(double, org.dyn4j.game2d.geometry.Vector)
	 */
	@Override
	public void rotate(double theta, Vector2 point) {
		this.rotate(theta, point.x, point.y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#rotate(double, double, double)
	 */
	@Override
	public void rotate(double theta, double x, double y) {
		// only rotate the center if the point about which
		// we are rotating is not the center
		if (!this.center.equals(x, y)) {
			this.center.rotate(theta, x, y);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#translate(double, double)
	 */
	@Override
	public void translate(double x, double y) {
		this.center.add(x, y);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.geometry.Transformable#translate(org.dyn4j.game2d.geometry.Vector)
	 */
	@Override
	public void translate(Vector2 vector) {
		this.translate(vector.x, vector.y);
	}
}
