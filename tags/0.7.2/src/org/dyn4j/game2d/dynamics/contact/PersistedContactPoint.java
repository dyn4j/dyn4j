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
 * Represents a persisted contact point.
 * @author William Bittle
 * @see ContactPoint
 */
public class PersistedContactPoint extends ContactPoint {
	/** The previous contact point */
	protected Vector oldPoint;
	
	/** The previous contact normal */
	protected Vector oldNormal;
	
	/** The previous penetration depth */
	protected double oldDepth;
	
	/** Default constructor */
	public PersistedContactPoint() {}
	
	/**
	 * Full constructor.
	 * @param point the world space contact point
	 * @param normal the world space contact normal
	 * @param depth the penetration depth
	 * @param oldPoint the previous world space contact point
	 * @param oldNormal the previous world space contact normal
	 * @param oldDepth the previous penetration depth
	 * @param body1 the first {@link Body} in contact
	 * @param convex1 the first {@link Body}'s {@link Convex} {@link Shape} in contact
	 * @param body2 the second {@link Body} in contact
	 * @param convex2 the second {@link Body}'s {@link Convex} {@link Shape} in contact
	 */
	public PersistedContactPoint(Vector point, Vector normal, double depth,
			Vector oldPoint, Vector oldNormal, double oldDepth,
			Body body1, Convex convex1, Body body2, Convex convex2) {
		super(point, normal, depth, body1, convex1, body2, convex2);
		this.oldPoint = oldPoint;
		this.oldNormal = oldNormal;
		this.oldDepth = oldDepth;
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This performs a shallow copy.
	 * @param pcp the {@link PersistedContactPoint} to copy
	 */
	public PersistedContactPoint(PersistedContactPoint pcp) {
		super(pcp);
		this.oldPoint = pcp.oldPoint;
		this.oldNormal = pcp.oldNormal;
		this.oldDepth = pcp.oldDepth;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CONTACT_POINT[")
		.append(this.point).append("|")
		.append(this.normal).append("|")
		.append(this.depth).append("|")
		.append(this.oldPoint).append("|")
		.append(this.oldNormal).append("|")
		.append(this.oldDepth).append("|")
		.append(this.body1).append("|")
		.append(this.body2).append("|")
		.append(this.convex1).append("|")
		.append(this.convex2).append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the old contact point.
	 * @return {@link Vector}
	 */
	public Vector getOldPoint() {
		return oldPoint;
	}
	
	/**
	 * Returns the old contact normal.
	 * @return {@link Vector}
	 */
	public Vector getOldNormal() {
		return oldNormal;
	}
	
	/**
	 * Returns the old depth.
	 * @return double
	 */
	public double getOldDepth() {
		return oldDepth;
	}
}
