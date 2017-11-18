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
package org.dyn4j.dynamics.contact;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Vector2;

/**
 * Represents a solved contact point.
 * <p>
 * A solved contact point is a contact that has been handled by the
 * contact constraint solver and therefore has the normal and tangential
 * impulses computed.
 * @author William Bittle
 * @see ContactPoint
 * @version 3.2.0
 * @since 1.0.0
 */
public class SolvedContactPoint extends ContactPoint {
	/** The accumulated normal impulse */
	protected double normalImpulse;
	
	/** The accumulated tangential impulse */
	protected double tangentialImpulse;
	
	/**
	 * Full constructor.
	 * @param id the contact point id
	 * @param body1 the first {@link Body} in contact
	 * @param fixture1 the first {@link Body}'s {@link BodyFixture}
	 * @param body2 the second {@link Body} in contact
	 * @param fixture2 the second {@link Body}'s {@link BodyFixture}
	 * @param point the world space contact point
	 * @param normal the world space contact normal
	 * @param depth the penetration depth
	 * @param normalImpulse the accumulated normal impulse
	 * @param tangentialImpulse the accumulated tangential impulse
	 */
	public SolvedContactPoint(ContactPointId id,
			Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, 
			Vector2 point, Vector2 normal, double depth,
			double normalImpulse, double tangentialImpulse) {
		super(id, body1, fixture1, body2, fixture2, point, normal, depth);
		this.normalImpulse = normalImpulse;
		this.tangentialImpulse = tangentialImpulse;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SolvedContactPoint[Id=").append(this.id)
		  .append("|Body1=").append(this.body1.getId())
		  .append("|Fixture1=").append(this.fixture1.getId())
		  .append("|Body2=").append(this.body2.getId())
		  .append("|Fixture2=").append(this.fixture2.getId())
		  .append("|Point=").append(this.point)
		  .append("|Normal=").append(this.normal)
		  .append("|Depth=").append(this.depth)
		  .append("|NormalImpulse=").append(this.normalImpulse)
		  .append("|TangentImpulse=").append(this.tangentialImpulse)
		  .append("]");
		return sb.toString();
	}
	
	/**
	 * Copy constructor (shallow).
	 * @param scp the {@link SolvedContactPoint} to copy
	 */
	public SolvedContactPoint(SolvedContactPoint scp) {
		super(scp);
		this.normalImpulse = scp.normalImpulse;
		this.tangentialImpulse = scp.tangentialImpulse;
	}
	
	/**
	 * Returns the accumulated normal impulse.
	 * @return double
	 */
	public double getNormalImpulse() {
		return this.normalImpulse;
	}
	
	/**
	 * Returns the accumulated tangential impulse.
	 * @return double
	 */
	public double getTangentialImpulse() {
		return this.tangentialImpulse;
	}
}
