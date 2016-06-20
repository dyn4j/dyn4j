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
package org.dyn4j.geometry.hull;

import java.util.Comparator;

import org.dyn4j.geometry.Vector2;

/**
 * Comparator class to compare points by their angle from the positive
 * x-axis with reference from a given point.
 * @author William Bittle
 * @version 3.2.0
 * @since 2.2.0
 */
final class ReferencePointComparator implements Comparator<Vector2> {
	/** The positive x-axis */
	private static final Vector2 X_AXIS = new Vector2(1.0, 0.0);
	
	/** The reference point for testing polar angles */
	final Vector2 reference;
	
	/**
	 * Full constructor.
	 * @param reference the reference point for finding angles
	 */
	public ReferencePointComparator(Vector2 reference) {
		this.reference = reference;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Vector2 p1, Vector2 p2) {
		// get the vectors from p to the points
		Vector2 v1 = reference.to(p1);
		Vector2 v2 = reference.to(p2);
		// compare the vector's angles with the x-axis
		return (int) Math.signum(v2.getAngleBetween(ReferencePointComparator.X_AXIS) - v1.getAngleBetween(ReferencePointComparator.X_AXIS));
	}
}
