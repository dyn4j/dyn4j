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
package org.dyn4j.dynamics.joint;

/**
 * Represents a joint that has a single set of linear limits.
 * @author William Bittle
 * @version 5.0.0
 * @since 5.0.0
 */
public interface LinearLimitsJoint {
	/**
	 * Sets whether the upper limit is enabled.
	 * @param flag true if the upper limit should be enabled
	 */
	public void setUpperLimitEnabled(boolean flag);
	
	/**
	 * Returns true if the upper limit is enabled.
	 * @return boolean true if the upper limit is enabled
	 */
	public boolean isUpperLimitEnabled();
	
	/**
	 * Sets whether the lower limit is enabled.
	 * @param flag true if the lower limit should be enabled
	 */
	public void setLowerLimitEnabled(boolean flag);

	/**
	 * Returns true if the lower limit is enabled.
	 * @return boolean true if the lower limit is enabled
	 */
	public boolean isLowerLimitEnabled();
	
	/**
	 * Returns the upper limit in meters.
	 * @return double
	 */
	public double getUpperLimit();
	
	/**
	 * Sets the upper limit in meters.
	 * @param upperLimit the upper limit in meters
	 * @throws IllegalArgumentException if upperLimit is less than the current lower limit
	 */
	public void setUpperLimit(double upperLimit);
	
	/**
	 * Returns the lower limit in meters.
	 * @return double
	 */
	public double getLowerLimit();
	
	/**
	 * Sets the lower limit in meters.
	 * @param lowerLimit the lower limit in meters
	 * @throws IllegalArgumentException if lowerLimit is greater than the current upper limit
	 */
	public void setLowerLimit(double lowerLimit);

	/**
	 * Sets both the lower and upper limits.
	 * @param lowerLimit the lower limit in meters
	 * @param upperLimit the upper limit in meters
	 * @throws IllegalArgumentException if lowerLimit is greater than upperLimit
	 */
	public void setLimits(double lowerLimit, double upperLimit);

	/**
	 * Sets both the lower and upper limits and enables both.
	 * @param lowerLimit the lower limit in meters
	 * @param upperLimit the upper limit in meters
	 * @throws IllegalArgumentException if lowerLimit is greater than upperLimit
	 */
	public void setLimitsEnabled(double lowerLimit, double upperLimit);
	
	/**
	 * Enables or disables both the lower and upper limits.
	 * @param flag true if both limits should be enabled
	 */
	public void setLimitsEnabled(boolean flag);

	/**
	 * Sets both the lower and upper limits to the given limit.
	 * @param limit the desired limit in meters
	 */
	public void setLimits(double limit);
	
	/**
	 * Sets both the lower and upper limits to the given limit and
	 * enables both.
	 * @param limit the desired limit in meters
	 */
	public void setLimitsEnabled(double limit);
}
