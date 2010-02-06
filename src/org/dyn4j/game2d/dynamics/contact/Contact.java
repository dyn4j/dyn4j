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
import org.dyn4j.game2d.geometry.Vector;

/**
 * Represents a contact point between two {@link Body} objects.
 * @author William Bittle
 */
public class Contact {
	/** The contact point in world space */
	protected Vector p = null;
	
	/** The contact penetration depth */
	protected double depth = 0.0;
	
	/** The contact point in {@link Body}1 space */
	protected Vector p1 = null;
	
	/** The contact point in {@link Body}2 space */
	protected Vector p2 = null;
	
	/** The {@link Vector} from the center of {@link Body}1 to the contact point */
	protected Vector r1 = null;
	
	/** The {@link Vector} from the center of {@link Body}2 to the contact point */
	protected Vector r2 = null;
	
	/** The accumulated normal impulse */
	protected double jn;
	
	/** The accumulated tangent impulse */
	protected double jt;
	
	/** The accumulated position impulse */
	protected double jp;
	
	/** The mass normal */
	protected double massN;
	
	/** The mass tangent */
	protected double massT;
	
	/** The equalized mass */
	protected double massE;
	
	/** The velocity bias */
	protected double vb;
	
	/**
	 * Full constructor.
	 * @param point the world space collision point
	 * @param depth the penetration depth of this point
	 * @param p1 the collision point in {@link Body}1's local space
	 * @param p2 the collision point in {@link Body}2's local space
	 */
	public Contact(Vector point, double depth, Vector p1, Vector p2) {
		this.p = point;
		this.depth = depth;
		this.p1 = p1;
		this.p2 = p2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CONTACT[")
		.append("POINT[").append(p).append("]")
		.append("|").append("POINT1[").append(p1).append("]")
		.append("|").append("POINT2[").append(p2).append("]")
		.append("|").append("DEPTH[").append(depth).append("]")
		.append("|").append("R1[").append(r1).append("]")
		.append("|").append("R2[").append(r2).append("]")
		.append("|").append("NORMAL_IMPULSE[").append(jn).append("]")
		.append("|").append("TANGENTIAL_IMPULSE[").append(jt).append("]")
		.append("|").append("POSITION_IMPULSE[").append(jp).append("]")
		.append("|").append("NORMAL_MASS[").append(massN).append("]")
		.append("|").append("TANGENTIAL_MASS[").append(massT).append("]")
		.append("|").append("EQUALIZED_MASS[").append(massE).append("]")
		.append("|").append("VELOCITY_BIAS[").append(vb).append("]")
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the world space collision point.
	 * @return {@link Vector} the collision point in world space
	 */
	public Vector getPoint() {
		return this.p;
	}
	
	/**
	 * Returns the penetration depth of this point.
	 * @return double the penetration depth
	 */
	public double getDepth() {
		return this.depth;
	}
	
	/**
	 * Returns the accumulated normal impulse applied at this point.
	 * @return double the accumulated normal impulse
	 */
	public double getNormalImpulse() {
		return this.jn;
	}
	
	/**
	 * Returns the accumulated tangential impulse applied at this point.
	 * @return double the accumulated tangential impulse
	 */
	public double getTangentialImpulse() {
		return this.jt;
	}
	
	/**
	 * Returns the accumulated position impulse applied at this point.
	 * @return double the accumulated position impulse
	 */
	public double getPositionImpulse() {
		return this.jp;
	}
}
