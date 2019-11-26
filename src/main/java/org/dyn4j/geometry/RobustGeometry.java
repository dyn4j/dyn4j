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
	/** Constant that {@link CompoundDecimal} uses to split doubles when calculation multiplication error */
	static final int splitter;
	
	/** Error bounds used to adaptively use as much precision is required for a correct result */
	private static final double resultErrorBound;
	private static final double errorBoundA, errorBoundB, errorBoundC;
	
	/** Internal {@link CompoundDecimal} used to store incremental approximations of the result */
	private static final CompoundDecimal B, C1, C2, D;
	
	/** Internal {@link CompoundDecimal} buffer to store the immediate result of sums */
	private static final CompoundDecimal buffer;
	
	/**
	 * Initializer that computes the necessary splitter value and error bounds based on the machine epsilon.
	 * Also instantiates the internal {@link CompoundDecimal} variables.
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
		
		splitter = splitterMut;
		
		// compute bounds as described in the paper
		resultErrorBound = (3 + 8 * epsilon) * epsilon;
		errorBoundA = (3 + 16 * epsilon) * epsilon;
		errorBoundB = (2 + 12 * epsilon) * epsilon;
		errorBoundC = (9 + 64 * epsilon) * epsilon * epsilon;
		
		// allocate the {@link CompoundDecimal}s based on the max amount of components
		// that will possibly be needed for each
		B = new CompoundDecimal(4);
		C1 = new CompoundDecimal(8);
		C2 = new CompoundDecimal(12);
		D = new CompoundDecimal(16);
		buffer = new CompoundDecimal(4);
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
		// in the CompoundDecimal class and the corresponding paper of the author.
		
		// In the beginning try the simple-straightforward computation with floating point values
		// and no extra precision, as in Segment#getLocation
		double detLeft = (point.x - linePoint2.x) * (linePoint1.y - linePoint2.y);
		double detRight = (point.y - linePoint2.y) * (linePoint1.x - linePoint2.x);
		double det = detLeft - detRight;
		
		if (detLeft == 0 || detRight == 0 || (detLeft > 0) != (detRight > 0)) {
			return det;
		}
		
		double detSum = Math.abs(detLeft + detRight);
		if (Math.abs(det) >= errorBoundA * detSum) {
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
		CompoundDecimal.Cross_Product(acx, acy, bcx, bcy, B);
		
		double det = B.getEstimation();
		double errorBound = errorBoundB * detSum;
		if (Math.abs(det) >= errorBound) {
			return det;
		}
		
		// Since we need more precision to produce the result at this point
		// we have to calculate the differences with full precision
		double acxTail = CompoundDecimal.fromDiff(point.x, linePoint2.x, acx);
		double acyTail = CompoundDecimal.fromDiff(point.y, linePoint2.y, acy);
		double bcxTail = CompoundDecimal.fromDiff(linePoint1.x, linePoint2.x, bcx);
		double bcyTail = CompoundDecimal.fromDiff(linePoint1.y, linePoint2.y, bcy);
		
		if (acxTail == 0 && acyTail == 0 && bcxTail == 0 && bcyTail == 0) {
			// trivial case: the extra precision was not needed after all
			return det;
		}
		
		errorBound = errorBoundC * detSum + resultErrorBound * Math.abs(det);
		// But don't use full precision to calculate the following cross products with the tail values
		det += (acx * bcyTail + bcy * acxTail) - (acy * bcxTail + bcx * acyTail);
		
		if (Math.abs(det) >= errorBound) {
			return det;
		}
		
		// This case is so rare that we don't know if there are any inputs going into it
		// At this point we have to go full out and calculate all the products with full precision
		
		CompoundDecimal.Cross_Product(acxTail, bcx, acyTail, bcy, buffer);
		B.sum(buffer, C1);
		
		CompoundDecimal.Cross_Product(acx, bcxTail, acy, bcyTail, buffer);
		C1.sum(buffer, C2);
		
		CompoundDecimal.Cross_Product(acxTail, bcxTail, acyTail, bcyTail, buffer);
		C2.sum(buffer, D);
		
		// return the most significant component of the last buffer D.
		// reminder: components are non-overlapping so this is ok
		return D.get(D.size() - 1);
	}
	
}