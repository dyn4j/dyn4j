/*
 * Copyright (c) 2010-2017 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.geometry;

/**
 * This class provides geometric routines that have guarantees about some properties
 * of their floating point results and operations.
 * 
 * @author Manolis Tsamis
 * @version 3.4.0
 * @since 3.4.0
 */
public final class RobustGeometry {
	/** Constant that {@link AdaptiveDecimal} uses to split doubles when calculation multiplication error */
	static final int SPLITTER;
	
	/** Error bounds used to adaptively use as much precision is required for a correct result */
	private static final double RESULT_ERROR_BOUND;
	private static final double ERROR_BOUND_A, ERROR_BOUND_B, ERROR_BOUND_C;
	
	/**
	 * Initializer that computes the necessary splitter value and error bounds based on the machine epsilon.
	 * Also instantiates the internal {@link AdaptiveDecimal} variables.
	 */
	static {
		// calculate the splitter and epsilon as described in the paper
		boolean everyOther = true;
		double epsilon = 1.0;
		int splitterMut = 1;
		
		while (1.0 + epsilon > 1.0) {
			if (everyOther) {
				splitterMut *= 2;
			}
			
			epsilon *= 0.5;
			everyOther = !everyOther;
		}
		
		splitterMut += 1.0;
		
		SPLITTER = splitterMut;
		
		// compute bounds as described in the paper
		RESULT_ERROR_BOUND = (3 + 8 * epsilon) * epsilon;
		ERROR_BOUND_A = (3 + 16 * epsilon) * epsilon;
		ERROR_BOUND_B = (2 + 12 * epsilon) * epsilon;
		ERROR_BOUND_C = (9 + 64 * epsilon) * epsilon * epsilon;
	}
	
	/**
	 * Performs cross product on four primitives and also allocates a new {@link AdaptiveDecimal}
	 * with the appropriate capacity to store the result.
	 * 
	 * @param ax The x value of the vector a
	 * @param ay The y value of the vector a
	 * @param bx The x value of the vector b
	 * @param by The y value of the vector b
	 * @return The result
	 * @see #cross(double, double, double, double, AdaptiveDecimal)
	 */
	public static AdaptiveDecimal cross(double ax, double ay, double bx, double by) {
		return cross(ax, ay, bx, by, null);
	}
	
	/**
	 * Performs the cross product of two vectors a, b, that is ax * by - ay * bx but with extended precision
	 * and stores the 4 component result in the given {@link AdaptiveDecimal} {@code result}.
	 * In the same way as with {@link AdaptiveDecimal#sum(AdaptiveDecimal, AdaptiveDecimal)} if {@code result} is null
	 * a new one is allocated, otherwise the existing is cleared and used.
	 * 
	 * @param ax The x value of the vector a
	 * @param ay The y value of the vector a
	 * @param bx The x value of the vector b
	 * @param by The y value of the vector b
	 * @param result The {@link AdaptiveDecimal} in which the cross product is stored
	 * @return The result
	 */
	public static AdaptiveDecimal cross(double ax, double ay, double bx, double by, AdaptiveDecimal result) {
		double axby = ax * by;
		double aybx = bx * ay;
		double axbyTail = AdaptiveDecimal.getErrorComponentFromProduct(ax, by, axby);
		double aybxTail = AdaptiveDecimal.getErrorComponentFromProduct(bx, ay, aybx);
		
		// result can be null in which case AdaptiveDecimal.fromDiff will allocate a new one
		AdaptiveDecimal newResult = AdaptiveDecimal.fromDiff(axbyTail, axby, aybxTail, aybx, result);
		
		return newResult;
	}
	
	/**
	 * Robust side-of-line test.
	 * Computes the same value with {@link Segment#getLocation(Vector2, Vector2, Vector2)} but with
	 * enough precision so the sign of the result is correct for any {@link Vector2}s pa, pb, pc.
	 * This implementation uses more precision as-needed only for the hardest cases.
	 * For the majority of inputs this will be only slightly slower than the corresponding call
	 * to {@link Segment#getLocation(Vector2, Vector2, Vector2)} but in the hard cases can be 5-25 times slower.
	 * 
	 * @param point the point
	 * @param linePoint1 the first point of the line
	 * @param linePoint2 the second point of the line
	 * @return double
	 * @see Segment#getLocation(Vector2, Vector2, Vector2)
	 */
	public static double getLocation(Vector2 point, Vector2 linePoint1, Vector2 linePoint2) {
		// This code is based on the original code by Jonathan Richard Shewchuk
		// For more details about the correctness and error bounds check the note
		// in the AdaptiveDecimal class and the corresponding paper of the author.
		
		// In the beginning try the simple-straightforward computation with floating point values
		// and no extra precision, as in Segment#getLocation
		double detLeft = (point.x - linePoint2.x) * (linePoint1.y - linePoint2.y);
		double detRight = (point.y - linePoint2.y) * (linePoint1.x - linePoint2.x);
		double det = detLeft - detRight;
		
		if (detLeft == 0 || detRight == 0 || (detLeft > 0) != (detRight > 0)) {
			return det;
		}
		
		double detSum = Math.abs(detLeft + detRight);
		if (Math.abs(det) >= ERROR_BOUND_A * detSum) {
			// This will cover the vast majority of cases
			return det;
		}
		
		// For the few harder cases we need to use the adaptive precision implementation
		return getLocation(point, linePoint1, linePoint2, detSum);
	}
	
	/**
	 * The extended precision implementation for the side-of-line test.
	 * 
	 * @param point the point
	 * @param linePoint1 the first point of the line
	 * @param linePoint2 the second point of the line
	 * @return double
	 * @see #getLocation(Vector2, Vector2, Vector2)
	 */
	private static double getLocation(Vector2 point, Vector2 linePoint1, Vector2 linePoint2, double detSum) {
		double acx = point.x - linePoint2.x;
		double acy = point.y - linePoint2.y;
		double bcx = linePoint1.x - linePoint2.x;
		double bcy = linePoint1.y - linePoint2.y;
		
		// Calculate the cross product but with more precision than before
		// But don't bother yet to perform the differences acx, acy, bcx, bcy
		// with full precision
		AdaptiveDecimal B = RobustGeometry.cross(acx, acy, bcx, bcy);
		
		double det = B.getEstimation();
		double errorBound = ERROR_BOUND_B * detSum;
		if (Math.abs(det) >= errorBound) {
			return det;
		}
		
		// Since we need more precision to produce the result at this point
		// we have to calculate the differences with full precision
		double acxTail = AdaptiveDecimal.getErrorComponentFromDifference(point.x, linePoint2.x, acx);
		double acyTail = AdaptiveDecimal.getErrorComponentFromDifference(point.y, linePoint2.y, acy);
		double bcxTail = AdaptiveDecimal.getErrorComponentFromDifference(linePoint1.x, linePoint2.x, bcx);
		double bcyTail = AdaptiveDecimal.getErrorComponentFromDifference(linePoint1.y, linePoint2.y, bcy);
		
		if (acxTail == 0 && acyTail == 0 && bcxTail == 0 && bcyTail == 0) {
			// trivial case: the extra precision was not needed after all
			return det;
		}
		
		errorBound = ERROR_BOUND_C * detSum + RESULT_ERROR_BOUND * Math.abs(det);
		// But don't use full precision to calculate the following cross products with the tail values
		det += (acx * bcyTail + bcy * acxTail) - (acy * bcxTail + bcx * acyTail);
		
		if (Math.abs(det) >= errorBound) {
			return det;
		}
		
		// This case is so rare that we don't know if there are any inputs going into it
		// At this point we have to go full out and calculate all the products with full precision
		
		// Re-usable buffer to store the results of the 3 cross products needed below
		AdaptiveDecimal buffer = new AdaptiveDecimal(4);
		
		RobustGeometry.cross(acxTail, bcx, acyTail, bcy, buffer);
		AdaptiveDecimal C1 = B.sum(buffer);
		
		RobustGeometry.cross(acx, bcxTail, acy, bcyTail, buffer);
		AdaptiveDecimal C2 = C1.sum(buffer);
		
		RobustGeometry.cross(acxTail, bcxTail, acyTail, bcyTail, buffer);
		AdaptiveDecimal D = C2.sum(buffer);
		
		// return the most significant component of the last buffer D.
		// reminder: components are non-overlapping so this is ok
		return D.get(D.size() - 1);
	}
	
}