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

import java.util.Arrays;

/**
 * This is an implementation of multi-precision decimals based on the original work by Jonathan Richard Shewchuk,
 * "Routines for Arbitrary Precision Floating-point Arithmetic and Fast Robust Geometric Predicates".
 * <p>
 * More information about the algorithms, the original code in C and proofs of correctness can all
 * be found at <a href="http://www.cs.cmu.edu/~quake/robust.html">http://www.cs.cmu.edu/~quake/robust.html</a>
 * <p>
 * Short description:
 * The value of this {@link AdaptiveDecimal} is represented as the sum of some components,
 * where each component is a double value.
 * The components must be stored in increasing magnitude order, but there can be any amount of
 * zeros between components. The components must also satisfy the non-overlapping property, that is
 * the corresponding bit representation of adjacent components must not overlap. See {@link #checkInvariants()}
 * and the corresponding paper for more info.
 * <p>
 * This code <b>requires</b> that the floating point model is IEEE-754 with round-to-even in order
 * to work properly in all cases and fulfill the above properties. This is not a problem because this
 * is the default and only model the Java specification describes.
 * 
 * @author Manolis Tsamis
 * @version 3.4.0
 * @since 3.4.0
 */
class AdaptiveDecimal {
	/** The array storing this {@link AdaptiveDecimal}'s component values */
	private final double[] components;
	
	/** The number of components this {@link AdaptiveDecimal} currently contains */
	private int size;
	
	/**
	 * Creates a new {@link AdaptiveDecimal} with the specified length.
	 * The initial {@link AdaptiveDecimal} created does not contains any components.
	 * 
	 * @param length The maximum number of components this {@link AdaptiveDecimal} can store
	 */
	public AdaptiveDecimal(int length) {
		if (length <= 0) {
			throw new IllegalArgumentException();
		}
		
		this.components = new double[length];
		this.size = 0;
	}
	
	/**
	 * Deep copy constructor.
	 * @param other the {@link AdaptiveDecimal} to copy from
	 */
	public AdaptiveDecimal(AdaptiveDecimal other) {
		this.components = Arrays.copyOf(other.components, other.capacity());
		this.size = other.size;
	}
	
	/**
	 * Internal helper constructor to create a {@link AdaptiveDecimal} with two components
	 * @param a0 the component with the smallest magnitude
	 * @param a1 the component with the largest magnitude
	 */
	protected AdaptiveDecimal(double a0, double a1) {
		this.components = new double[] {a0, a1};
		this.size = 2;
	}
	
	/**
	 * @return The number of components this {@link AdaptiveDecimal} currently has
	 */
	public int size() {
		return this.size;
	}
	
	/**
	 * @return The maximum number of components this {@link AdaptiveDecimal} can hold
	 */
	public int capacity() {
		return this.components.length;
	}
	
	/**
	 * @return A deep copy of this {@link AdaptiveDecimal}
	 */
	public AdaptiveDecimal copy() {
		return new AdaptiveDecimal(this);
	}
	
	/**
	 * Copies the components of another {@link AdaptiveDecimal} into this.
	 * The capacity of the this {@link AdaptiveDecimal} is not modified and it should
	 * be enough to hold all the components.
	 * 
	 * @param other The {@link AdaptiveDecimal} to copy from
	 */
	public void copyFrom(AdaptiveDecimal other) {
		System.arraycopy(other.components, 0, this.components, 0, other.size());
		this.size = other.size;
	}
	
	/**
	 * @param index index of the component to return
	 * @return the component at the specified position
	 * @throws IndexOutOfBoundsException if the index is not in the range [0, size)
	 */
	public double get(int index) {
		if (index < 0 || index >= this.size()) {
			throw new IndexOutOfBoundsException();
		}
		
		return this.components[index];
	}
	
	/**
	 * Appends a new component after all the existing components.
	 * 
	 * @param value The component
	 * @return this {@link AdaptiveDecimal}
	 * @throws IndexOutOfBoundsException if this {@link AdaptiveDecimal} has no capacity for more components
	 */
	public AdaptiveDecimal append(double value) {
		if (this.size >= this.capacity()) {
			throw new IndexOutOfBoundsException();
		}
		
		this.components[this.size++] = value;
		return this;
	}
	
	/**
	 * Appends a new component after all the existing components, but only
	 * if it has a non zero value.
	 * 
	 * @param value The component
	 * @return this {@link AdaptiveDecimal}
	 */
	public AdaptiveDecimal appendNonZero(double value) {
		if (value != 0.0) {
			this.append(value);
		}
		
		return this;
	}
	
	/** The mask to get the mantissa of a double as per the standard; Taken from {@link DoubleConsts#SIGN_BIT_MASK} */
    private static final long SIGNIF_BIT_MASK = 0x000FFFFFFFFFFFFFL;
    
    /** The implicit bit in the mantissa of a double */
    private static final long IMPLICIT_MANTISSA_BIT = 0x0010000000000000L;
    
    /**
     * Returns a boolean value describing if this {@link AdaptiveDecimal} is a valid
     * representation as described in the header of this class.
     * Checks for the magnitude and non-overlapping property.
     * The invariants can be violated if bad input components are appended to this {@link AdaptiveDecimal}.
     * The append methods do not check for those conditions because there is a big overhead for the check.
     * The output of the exposed operations must satisfy the invariants, given that their input also does so.
     * 
     * @return true iff this {@link AdaptiveDecimal} satisfies the described invariants
     */
	public boolean checkInvariants() {
		if (this.size == 0) {
			return true;
		}
		
		// Holds the last value that needs to be checked
		// This skips all 0 except for mabe the first component (which is ok to be 0)
		double lastValue = this.get(0);
		
		for (int i = 1; i < this.size; i++) {
			double currentValue = this.get(i);
			if (currentValue != 0) {
				// the magnitude of previous non-zero elements must be smaller
				if (Math.abs(currentValue) < Math.abs(lastValue)) {
					return false;
				}
				
				// A number n in the floating point representation can be written as
				// n = +/- 0.1xxxxx...x * 2 ^ exp
				//   mantissa ^~~~~~~~^       ^~~ exponent
				// where the above x are the binary digits either 0 or 1 except for the first digit after the decimal point
				// which is always 1. The exponent part is essentially a shift of the decimal point
				
				// If we have two numbers a, b with a > b then they're non-overlapping if a's lower set bit to
				// be to the left of b's higher set bit *after those have been scaled by their exponents accordingly*.
				// The sign is irrelevant for this.
				// a = -0.10101011 * 2^5 = -10101.011
				// b = 0.111001 * 2^-1 = 0.0111001
				
				// if we align a and b we can see that they overlap
				// -10101.011
				//      0.0111001
				//         ^^ overlap
				// They would also overlap if b's exponent was -2 (for a single bit)
				// only if b's exponent where less than -2 then there would be no overlap
				
				// get the value of the exponents
				int exp1 = Math.getExponent(lastValue);
				int exp2 = Math.getExponent(currentValue);
				
				// get the significants (the binary representation of the mantissa part)
				// The first, always 1 bit is not actually stored, so we'll add it ourselves
				long mantissa1 = (Double.doubleToLongBits(lastValue) & SIGNIF_BIT_MASK) | IMPLICIT_MANTISSA_BIT;
				long mantissa2 = (Double.doubleToLongBits(currentValue) & SIGNIF_BIT_MASK) | IMPLICIT_MANTISSA_BIT;
				
				// We want to find the logical location of the most significant bit in the smallest component
				// and of the least significant bit in the largest component, accounting for the exponents as well
				// In the following convention bit numbering is done from the higher to the lowest bit.
				// Note that the first bit of the double representation won't be the first in the long below.
				// This is logical since the mantissa is fewer bits wide than a long, but it's not a problem
				// since both the msd and lsd will have the same difference.
				int msd1 = Long.numberOfLeadingZeros(mantissa1);
				int lsd2 = Long.SIZE - Long.numberOfTrailingZeros(mantissa2) - 1;
				
				// Apply the exponents
				// The exponents are essentially shifts in the bit positions
				msd1 -= exp1;
				lsd2 -= exp2;
				
				// Finally check for the non-overlapping property
				// We want the lower bit of the currentValue's representation to be higher than
				// lastValue's higher bit
				if (!(lsd2 < msd1)) {
					return false;
				}
				
				// Update the last non-zero value
				lastValue = currentValue;
			}
		}
		
		return true;
	}
	
	/**
	 * @throws IllegalStateException iff {@link #checkInvariants()} returns false
	 */
	public void ensureInvariants() {
		if (!this.checkInvariants()) {
			throw new IllegalStateException();
		}
	}
	
	/**
	 * Removes the components of this {@link AdaptiveDecimal}.
	 * 
	 * @return this {@link AdaptiveDecimal}
	 */
	public AdaptiveDecimal clear() {
		this.size = 0;
		return this;
	}
	
	/**
	 * Removes all the components with zero value from this {@link AdaptiveDecimal}.
	 * 
	 * @return this {@link AdaptiveDecimal}
	 */
	public AdaptiveDecimal removeZeros() {
		int oldSize = this.size;
		this.clear();
		
		for (int i = 0; i < oldSize; i++) {
			this.appendNonZero(this.components[i]);
		}
		
		return this;
	}
	
	/**
	 * Ensures this {@link AdaptiveDecimal} has at least one component.
	 * That is, appends the zero value if there are currently zero components.
	 * 
	 * @return this {@link AdaptiveDecimal}
	 */
	public AdaptiveDecimal normalize() {
		if (this.size == 0) {
			append(0.0);
		}
		
		return this;
	}
	
	/**
	 * Negates the logical value of this {@link AdaptiveDecimal}.
	 * This can be used with sum to perform subtraction .
	 * 
	 * @return this {@link AdaptiveDecimal}
	 */
	public AdaptiveDecimal negate() {
		for (int i = 0; i < this.size; i++) {
			this.components[i] = -this.components[i];
		}
		
		return this;
	}
	
	/**
	 * Computes an approximation for the value of this {@link AdaptiveDecimal} that fits in a double.
	 * 
	 * @return The approximation
	 */
	public double getEstimation() {
		double value = 0.0;
		
		for (int i = 0; i < this.size; i++) {
			value += this.components[i];
		}
		
		return value;
	}
	
	/**
	 * Performs addition and also allocates a new {@link AdaptiveDecimal} with the 
	 * appropriate capacity to store the result.
	 * 
	 * @param f The {@link AdaptiveDecimal} to sum with this {@link AdaptiveDecimal}
	 * @return A new {@link AdaptiveDecimal} that holds the result of the addition
	 * @see #sum(AdaptiveDecimal, AdaptiveDecimal)
	 */
	public AdaptiveDecimal sum(AdaptiveDecimal f) {
		return this.sum(f, null);
	}
	
	/**
	 * Helper method to implement the sum procedure.
	 * Sums the remaining components of a single {@link AdaptiveDecimal} to the result
	 * and the initial carry value from previous computations
	 * 
	 * @param carry The carry from previous computations
	 * @param e The {@link AdaptiveDecimal} that probably has more components
	 * @param eIndex The index to the next component of e that has to be examined
	 * @param result The {@link AdaptiveDecimal} in which the result is stored
	 * @return The result
	 */
	AdaptiveDecimal sumEpilogue(double carry, AdaptiveDecimal e, int eIndex, AdaptiveDecimal result) {
		for (; eIndex < e.size(); eIndex++) {
			double enow = e.get(eIndex);
			double sum = carry + enow;
			double error = getErrorComponentFromSum(carry, enow, sum);
			
			carry = sum;
			result.appendNonZero(error);
		}
		
		result.appendNonZero(carry);
		result.normalize();
		
		return result;
	}
	
	/**
	 * Performs the addition of this {@link AdaptiveDecimal} with the given {@link AdaptiveDecimal} f
	 * and stores the result in the provided {@link AdaptiveDecimal} {@code result}.
	 * If {@code result} is null it allocates a new {@link AdaptiveDecimal} with the 
	 * appropriate capacity to store the result. Otherwise the components of {@code result}
	 * are cleared and the resulting value is stored there, assuming there is enough capacity.
	 * 
	 * Be careful that it must be {@code f} &ne; {@code result} &ne; {@code this}.
	 * 
	 * @param f The {@link AdaptiveDecimal} to sum with this {@link AdaptiveDecimal}
	 * @param result The {@link AdaptiveDecimal} in which the sum is stored or null to allocate a new one
	 * @return The result
	 */
	public AdaptiveDecimal sum(AdaptiveDecimal f, AdaptiveDecimal result) {
		// The following algorithm performs addition of two AdaptiveDecimals
		// It is based on the original fast_expansion_sum_zeroelim function written
		// by the author of the said paper
		
		// allocate a new instance of sufficient size if result is null or just clear
		if (result == null) {
			result = new AdaptiveDecimal(this.size() + f.size());
		} else {
			result.clear();	
		}
		
		AdaptiveDecimal e = this;
		
		// eIndex and fIndex are used to iterate the components of e and f accordingly
		int eIndex = 0, fIndex = 0;
		// enow = e[eIndex] and fnow = f[fIndex] is the current component examined for e and f
		double enow = e.get(eIndex);
		double fnow = f.get(fIndex);
		
		// sum will be used to store the sum needed for the getErrorComponentFromSum method
		// error will store the error as returned from getErrorComponentFromSum method
		// carry will store the value that will be summed in the next sum
		double carry, sum, error;
		
		// each time we need the next component in increasing magnitude
		// (fnow > enow) == (fnow > -enow)
		if (Math.abs(enow) <= Math.abs(fnow)) {
			carry = enow;
			eIndex++;
			
			if (eIndex >= e.size()) {
				return sumEpilogue(carry, f, fIndex, result);
			}
			
			enow = e.get(eIndex);
		} else {
			carry = fnow;
			fIndex++;
			
			if (fIndex >= f.size()) {
				return sumEpilogue(carry, e, eIndex, result);
			}
			
			fnow = f.get(fIndex);
		}
		
		while (true) {
			if (Math.abs(enow) <= Math.abs(fnow)) {
				// perform the addition with the carry from the previous iterarion
				error = getErrorComponentFromSum(carry, enow, sum = carry + enow);
				eIndex++;
				carry = sum;
				
				// append + zero elimination
				result.appendNonZero(error);
				
				// if this AdaptiveDecimal has no more components then move to the epilogue
				if (eIndex >= e.size()) {
					return sumEpilogue(carry, f, fIndex, result);
				}
				
				enow = e.get(eIndex);
			} else {
				// perform the addition with the carry from the previous iterarion
				error = getErrorComponentFromSum(carry, fnow, sum = carry + fnow);
				fIndex++;
				carry = sum;
				
				// append + zero elimination
				result.appendNonZero(error);
				
				// if this AdaptiveDecimal has no more components then move to the epilogue
				if (fIndex >= f.size()) {
					return sumEpilogue(carry, e, eIndex, result);
				}
				
				fnow = f.get(fIndex);
			}				
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(this.size * 10);
		sb.append('[');
		
		for (int i = 0; i < this.size; i++) {
			sb.append(this.components[i]);
			
			if (i < this.size - 1) {
				sb.append(", ");
			}
		}
		
		sb.append("] ~= ");
		sb.append(this.getEstimation());
		
		return sb.toString();
	}
	
	/**
	 * Creates a {@link AdaptiveDecimal} with only a single component.
	 * 
	 * @param value The component
	 * @return {@link AdaptiveDecimal}
	 */
	public static AdaptiveDecimal valueOf(double value) {
		return new AdaptiveDecimal(1).append(value);
	}
	
	/**
	 * Creates a {@link AdaptiveDecimal} that holds the result of the
	 * addition of two double values.
	 * 
	 * @param a The first value
	 * @param b The second value
	 * @return A new {@link AdaptiveDecimal} that holds the resulting sum
	 */
	public static AdaptiveDecimal fromSum(double a, double b) {
		double sum = a + b;
		return new AdaptiveDecimal(getErrorComponentFromSum(a, b, sum), sum);
	}
	
	/**
	 * Creates a {@link AdaptiveDecimal} that holds the result of the
	 * difference of two double values.
	 * 
	 * @param a The first value
	 * @param b The second value
	 * @return A new {@link AdaptiveDecimal} that holds the resulting difference
	 */
	public static AdaptiveDecimal fromDiff(double a, double b) {
		double diff = a - b;
		return new AdaptiveDecimal(getErrorComponentFromDifference(a, b, diff), diff);
	}
	
	/**
	 * Given two unrolled expansions (a0, a1) and (b0, b1) performs the difference
	 * (a0, a1) - (b0, b1) and stores the 4 component result in the given {@link AdaptiveDecimal} {@code result}.
	 * In the same way as with {@link AdaptiveDecimal#sum(AdaptiveDecimal, AdaptiveDecimal)} if {@code result} is null
	 * a new one is allocated, otherwise the existing is cleared and used.
	 * Does not perform zero elimination.
	 * This is also a helper method to allow fast computation of the cross product
	 * without the overhead of creating new {@link AdaptiveDecimal} and performing
	 * the generalized sum procedure.
	 * 
	 * @param a0 The first component of a
	 * @param a1 The second component of a
	 * @param b0 The first component of b
	 * @param b1 The second component of b
	 * @param result The {@link AdaptiveDecimal} in which the difference is stored or null to allocate a new one
	 * @return The result
	 */
	static AdaptiveDecimal fromDiff(double a0, double a1, double b0, double b1, AdaptiveDecimal result) {
		// the exact order of those operations is necessary for correct functionality 
		// This is a rewrite of the corresponding Two_Two_Diff macro in the original code
		
		// allocate a new instance of sufficient size if result is null or just clear
		if (result == null) {
			result = new AdaptiveDecimal(4);
		} else {
			result.clear();	
		}
		
		// x0-x1-x2-x3 store the resulting components with increasing magnitude
		double x0, x1, x2, x3;
		
		// variable to store immediate results for each pair of Diff/Sum
		double imm;
		
		// variables to store immediate results across the two pairs 
		double imm1, imm2;
		
		// Diff (a0, a1) - b0, result = (x0, imm1, imm2)
		x0 = AdaptiveDecimal.getErrorComponentFromDifference(a0, b0, imm = a0 - b0);
		imm1 = AdaptiveDecimal.getErrorComponentFromSum(a1, imm, imm2 = a1 + imm);
		
		// Diff (imm1, imm2) - b1, result = (x1, x2, x3)
		x1 = AdaptiveDecimal.getErrorComponentFromDifference(imm1, b1, imm = imm1 - b1);
		x2 = AdaptiveDecimal.getErrorComponentFromSum(imm2, imm, x3 = imm2 + imm);
		
		result.append(x0);
		result.append(x1);
		result.append(x2);
		result.append(x3);
		
		return result;
	}
	
	/**
	 * Creates a {@link AdaptiveDecimal} that holds the result of the
	 * product of two double values.
	 * 
	 * @param a The first value
	 * @param b The second value
	 * @return A new {@link AdaptiveDecimal} that holds the resulting product
	 */
	public static AdaptiveDecimal fromProduct(double a, double b) {
		double product = a * b;
		return new AdaptiveDecimal(getErrorComponentFromProduct(a, b, product), a * b);
	}
	
	/**
	 * Given two values a, b and their sum = fl(a + b) calculates the value error for which
	 * fl(a) + fl(b) = fl(a + b) + fl(error).
	 * 
	 * @param a The first value
	 * @param b The second value
	 * @param sum Their sum, must always be sum = fl(a + b)
	 * @return The error described above
	 */
	static double getErrorComponentFromSum(double a, double b, double sum) {
		// the exact order of those operations is necessary for correct functionality 
		double bvirt = sum - a;
		double avirt = sum - bvirt;
		double bround = b - bvirt;
		double around = a - avirt;
		double error = around + bround;
		
		return error;
	}
	
	/**
	 * Given two values a, b and their difference = fl(a - b) calculates the value error for which
	 * fl(a) - fl(b) = fl(a - b) + fl(error).
	 * 
	 * @param a The first value
	 * @param b The second value
	 * @param diff Their difference, must always be diff = fl(a - b)
	 * @return The error described above
	 */
	static double getErrorComponentFromDifference(double a, double b, double diff) {
		// the exact order of those operations is necessary for correct functionality 
		double bvirt = a - diff;
		double avirt = diff + bvirt;
		double bround = bvirt - b;
		double around = a - avirt;
		double error = around + bround;
		
		return error;
	}
	
	/**
	 * Given two values a, b and their product = fl(a * b) calculates the value error for which
	 * fl(a) * fl(b) = fl(a * b) + fl(error).
	 * 
	 * @param a The first value
	 * @param b The second value
	 * @param product Their product, must always be product = fl(a * b)
	 * @return The error described above
	 */
	public static double getErrorComponentFromProduct(double a, double b, double product) {
		// the exact order of those operations is necessary for correct functionality 
		
		// split a in two parts
		double ac = RobustGeometry.SPLITTER * a;
		double abig = ac - a;
		double ahi = ac - abig;
		double alo = a - ahi;
		
		// split b in two parts
		double bc = RobustGeometry.SPLITTER * b;
		double bbig = bc - b;
		double bhi = bc - bbig;
		double blo = b - bhi;
		
		double error1 = product - (ahi * bhi);
		double error2 = error1 - (alo * bhi);
		double error3 = error2 - (ahi * blo);
		double error = alo * blo - error3;
		
		return error;
	}
}