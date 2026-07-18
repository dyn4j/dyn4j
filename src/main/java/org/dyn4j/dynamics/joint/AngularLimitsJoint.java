/*
 * Copyright (c) 2010-2026 William Bittle  http://www.dyn4j.org/
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
 * Represents a joint with a single set of angular limits.
 * @author William Bittle
 * @version 6.0.0
 * @since 5.0.0
 */
public interface AngularLimitsJoint {
	/**
	 * Returns true if the angular limits are enabled.
	 * @return boolean
	 */
	public boolean isLimitsEnabled();
	
	/**
	 * Enables or disables the angular limits.
	 * @param flag true if the limit should be enabled
	 */
	public void setLimitsEnabled(boolean flag);
	
	/**
	 * Returns the upper angular limit in radians.
	 * @return double
	 */
	public double getUpperLimit();
	
	/**
	 * Sets the upper angular limit.
	 * <p>
	 * The limit is relative to the vector from the anchor to body 1. The limit 
	 * can be any value, but behavior will change based on the values of both 
	 * the lower and upper limits. See the class documentation for more 
	 * details on the different ways the limits can be set.
	 * <p>
	 * The limits can be equal. A WeldJoint can provide more stability over 
	 * equal limits if you want the bodies pinned to each other.
	 * @param upperLimit the counter-clockwise angular limit in radians
	 */
	public void setUpperLimit(double upperLimit);
	
	/**
	 * Returns the lower angular limit in radians.
	 * @return double
	 */
	public double getLowerLimit();

	/**
	 * Sets the lower angular limit.
	 * <p>
	 * The limit is relative to the vector from the anchor to body 1. The limit 
	 * can be any value, but behavior will change based on the values of both 
	 * the lower and upper limits. See the class documentation for more 
	 * details on the different ways the limits can be set.
	 * <p>
	 * The limits can be equal. A WeldJoint can provide more stability over 
	 * equal limits if you want the bodies pinned to each other.
	 * @param lowerLimit the lower angular limit in radians
	 */
	public void setLowerLimit(double lowerLimit);
	
	/**
	 * Sets the upper and lower angular limits.
	 * <p>
	 * The limit is relative to the vector from the anchor to body 1. The limit 
	 * can be any value, but behavior will change based on the values of both 
	 * the lower and upper limits. See the class documentation for more 
	 * details on the different ways the limits can be set.
	 * <p>
	 * The limits can be equal. A WeldJoint can provide more stability over 
	 * equal limits if you want the bodies pinned to each other.
	 * @param lowerLimit the lower limit in radians
	 * @param upperLimit the upper limit in radians
	 */
	public void setLimits(double lowerLimit, double upperLimit);
	
	/**
	 * Sets both the lower and upper limits and enables them.
	 * <p>
	 * The limit is relative to the vector from the anchor to body 1. The limit 
	 * can be any value, but behavior will change based on the values of both 
	 * the lower and upper limits. See the class documentation for more 
	 * details on the different ways the limits can be set.
	 * <p>
	 * The limits can be equal. A WeldJoint can provide more stability over 
	 * equal limits if you want the bodies pinned to each other.
	 * @param lowerLimit the lower limit in radians
	 * @param upperLimit the upper limit in radians
	 * @since 4.2.0
	 */
	public void setLimitsEnabled(double lowerLimit, double upperLimit);

	/**
	 * Sets both the lower and upper limits to the given value and enables them.
	 * <p>
	 * The limit is relative to the vector from the anchor to body 1. The limit 
	 * can be any value, but behavior will change based on the values of both 
	 * the lower and upper limits. See the class documentation for more 
	 * details on the different ways the limits can be set.
	 * <p>
	 * The limits can be equal. A WeldJoint can provide more stability over 
	 * equal limits if you want the bodies pinned to each other.
	 * @param limit the lower limit in radians
	 * @since 4.2.0
	 */
	public void setLimitsEnabled(double limit);
	
	/**
	 * Sets both the lower and upper limits to the given limit.
	 * <p>
	 * The limit is relative to the vector from the anchor to body 1. The limit 
	 * can be any value, but behavior will change based on the values of both 
	 * the lower and upper limits. See the class documentation for more 
	 * details on the different ways the limits can be set.
	 * <p>
	 * The limits can be equal. A WeldJoint can provide more stability over 
	 * equal limits if you want the bodies pinned to each other.
	 * @param limit the lower limit in radians
	 * @since 4.2.0
	 */
	public void setLimits(double limit);

	/**
	 * Returns the limits reference angle.
	 * <p>
	 * The reference angle is the angle calculated when the joint was created from the
	 * two joined bodies.  The reference angle is the angular difference between the
	 * bodies.
	 * @return double
	 * @since 3.0.1
	 */
	public double getLimitsReferenceAngle();
	
	/**
	 * Sets the limits reference angle.
	 * <p>
	 * This method can be used to set the reference angle to override the computed
	 * reference angle from the constructor.  This is useful in recreating the joint
	 * from a current state or when adjusting the limits.
	 * <p>
	 * See the class documentation for more details.
	 * @param angle the reference angle; in radians
	 * @see #getLimitsReferenceAngle()
	 * @since 3.0.1
	 */
	public void setLimitsReferenceAngle(double angle);
}
