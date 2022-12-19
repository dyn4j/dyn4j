/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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
 *   * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.exception;

/**
 * Represents an exception when two arguments are the same object.
 * @author William Bittle
 * @version 5.0.0
 * @since 5.0.0
 */
public class ValueOutOfRangeException extends IllegalArgumentException {
	private static final long serialVersionUID = -7115378379246433066L;
	
	/** Greater than */
	public static final String MUST_BE_GREATER_THAN = "greater than";
	
	/** Greater than or equal */
	public static final String MUST_BE_GREATER_THAN_OR_EQUAL_TO = "greater than or equal to";
	
	/** Less than */
	public static final String MUST_BE_LESS_THAN = "less than";
	
	/** Less than or equal */
	public static final String MUST_BE_LESS_THAN_OR_EQUAL_TO = "less than or equal to";

	/**
	 * Minimal constructor.
	 * @param argumentName the name of the argument
	 * @param value the value given
	 * @param bound the bound for the value
	 * @param boundType the bound type (less than, greater than, etc)
	 */
	public ValueOutOfRangeException(String argumentName, double value, String boundType, double bound) {
		super(String.format("%2$f was supplied for %1$s: %1$s must be %4$s %3$f", argumentName, value, bound, boundType));
	}
	
	/**
	 * Minimal constructor.
	 * @param argumentName the name of the argument
	 * @param value the value given
	 * @param bound the bound for the value
	 * @param boundType the bound type (less than, greater than, etc)
	 */
	public ValueOutOfRangeException(String argumentName, int value, String boundType, int bound) {
		super(String.format("%2$d was supplied for %1$s: %1$s must be %4$s %3$d", argumentName, value, bound, boundType));
	}
	
	/**
	 * Minimal constructor.
	 * @param argumentName1 the name of the first argument
	 * @param value1 the value of the first argument
	 * @param argumentName2 the name of the second argument
	 * @param value2 the value of the second argument
	 * @param boundType the bound type (less than, greater than, etc)
	 */
	public ValueOutOfRangeException(String argumentName1, double value1, String boundType, String argumentName2, double value2) {
		super(String.format("%1$s must be %5$s %3$s: %1$s was %2$f and %3$s was %4$f", argumentName1, value1, argumentName2, value2, boundType));
	}
	
	/**
	 * Minimal constructor.
	 * @param argumentName1 the name of the first argument
	 * @param value1 the value of the first argument
	 * @param argumentName2 the name of the second argument
	 * @param value2 the value of the second argument
	 * @param boundType the bound type (less than, greater than, etc)
	 */
	public ValueOutOfRangeException(String argumentName1, int value1, String boundType, String argumentName2, int value2) {
		super(String.format("%1$s must be %5$s %3$s: %1$s was %2$d and %3$s was %4$d", argumentName1, value1, argumentName2, value2, boundType));
	}
	
	/**
	 * Minimal constructor.
	 * @param argumentName the name of the argument
	 * @param value the value given
	 */
	public ValueOutOfRangeException(String argumentName, double value) {
		super(String.format("%1$s cannot be %2$f", argumentName, value));
	}
	
	/**
	 * Minimal constructor.
	 * @param argumentName the name of the argument
	 * @param value the value given
	 */
	public ValueOutOfRangeException(String argumentName, int value) {
		super(String.format("%1$s cannot be %2$d", argumentName, value));
	}
}
