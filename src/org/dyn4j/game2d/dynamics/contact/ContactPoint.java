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
package org.dyn4j.game2d.dynamics.contact;

import org.dyn4j.game2d.dynamics.Body;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Shape;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Represents a contact point and used to report events via the {@link ContactListener}.
 * @author William Bittle
 */
public class ContactPoint {
	/** The world space contact point */
	protected Vector point;
	
	/** The world space contact normal */
	protected Vector normal;
	
	/** The penetration depth */
	protected double depth;
	
	/** The first {@link Body} in contact */
	protected Body body1;
	
	/** The second {@link Body} in contact */
	protected Body body2;
	
	/** The first {@link Body}'s {@link Convex} {@link Shape} that is in contact */
	protected Convex convex1;
	
	/** The second {@link Body}'s {@link Convex} {@link Shape} that is in contact */
	protected Convex convex2;
	
	/** Default constructor */
	public ContactPoint() {}
	
	/**
	 * Full constructor.
	 * @param point the world space contact point
	 * @param normal the world space contact normal
	 * @param depth the penetration depth
	 * @param body1 the first {@link Body} in contact
	 * @param convex1 the first {@link Body}'s {@link Convex} {@link Shape} in contact
	 * @param body2 the second {@link Body} in contact
	 * @param convex2 the second {@link Body}'s {@link Convex} {@link Shape} in contact
	 */
	public ContactPoint(Vector point, Vector normal, double depth,
			Body body1, Convex convex1, Body body2, Convex convex2) {
		this.point = point;
		this.normal = normal;
		this.depth = depth;
		this.body1 = body1;
		this.convex1 = convex1;
		this.body2 = body2;
		this.convex2 = convex2;
	}
	
	/**
	 * Returns the contact point.
	 * @return {@link Vector}
	 */
	public Vector getPoint() {
		return point;
	}
	
	/**
	 * Returns the normal.
	 * @return {@link Vector}
	 */
	public Vector getNormal() {
		return normal;
	}
	
	/**
	 * Returns the depth.
	 * @return double
	 */
	public double getDepth() {
		return depth;
	}
	
	/**
	 * Returns the first {@link Body}.
	 * @return {@link Body}
	 */
	public Body getBody1() {
		return body1;
	}
	
	/**
	 * Returns the second {@link Body}.
	 * @return {@link Body}
	 */
	public Body getBody2() {
		return body2;
	}
	
	/**
	 * Returns the first {@link Body}'s {@link Convex} {@link Shape}.
	 * @return {@link Convex}
	 */
	public Convex getConvex1() {
		return convex1;
	}
	
	/**
	 * Returns the second {@link Body}'s {@link Convex} {@link Shape}.
	 * @return {@link Convex}
	 */
	public Convex getConvex2() {
		return convex2;
	}
}
