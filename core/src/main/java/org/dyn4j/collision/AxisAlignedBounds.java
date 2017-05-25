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

import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Translatable;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Represents a bounding region that is an Axis-Aligned bounding box.
 * <p>
 * This class compares its AABB with the AABB of the given body and returns true
 * if they do not overlap.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.1.1
 */
public class AxisAlignedBounds extends AbstractBounds implements Bounds, Translatable {
	/** The local coordinates AABB */
	protected final AABB aabb;
	
	/**
	 * Minimal constructor.
	 * @param width the width of the bounds; must be greater than zero
	 * @param height the height of the bounds; must be greater than zero
	 * @throws IllegalArgumentException if either width or height are less than or equal to zero
	 */
	public AxisAlignedBounds(double width, double height) {
		if (width <= 0.0 || height <= 0.0) throw new IllegalArgumentException(Messages.getString("collision.bounds.axisAligned.invalidArgument"));
		double w2 = width * 0.5;
		double h2 = height * 0.5;
		this.aabb = new AABB(-w2, -h2, w2, h2);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AxisAlignedBounds[Width=").append(this.aabb.getWidth())
		.append("|Height=").append(this.aabb.getHeight())
		.append("|Translation=").append(this.getTranslation())
		.append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.collision.Bounds#isOutside(org.dyn4j.collision.Collidable)
	 */
	@Override
	public boolean isOutside(Collidable<?> collidable) {
		Vector2 tx = this.transform.getTranslation();
		
		AABB aabbBounds = this.aabb.getTranslated(tx);
		AABB aabbBody = collidable.createAABB();
		
		// test the projections for overlap
		if (aabbBounds.overlaps(aabbBody)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns the world space Axis-Aligned bounding box for this
	 * bounds object.
	 * @return {@link AABB}
	 */
	public AABB getBounds() {
		// return the AABB in world coordinates
		return this.aabb.getTranslated(this.transform.getTranslation());
	}
	
	/**
	 * Returns the width of the bounds.
	 * @return double
	 */
	public double getWidth() {
		return this.aabb.getWidth();
	}
	
	/**
	 * Returns the height of the bounds.
	 * @return double
	 */
	public double getHeight() {
		return this.aabb.getHeight();
	}
}
