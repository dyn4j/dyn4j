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
 * Represents a linear spring joint.
 * @author William Bittle
 * @version 5.0.0
 * @since 5.0.0
 */
public interface LinearSpringJoint {
	/**
	 * Sets whether the spring is enabled or not.
	 * @param enabled true if the spring should be enabled
	 */
	public void setSpringEnabled(boolean enabled);

	/**
	 * Returns true if the spring is enabled.
	 * @return boolean
	 */
	public boolean isSpringEnabled();
	
	/**
	 * Sets whether the spring's damper is enabled or not.
	 * <p>
	 * NOTE: for the damper to have any effect, the spring must be enabled.
	 * @param enabled true if the damper should be enabled
	 * @see #setSpringEnabled(boolean)
	 */
	public void setSpringDamperEnabled(boolean enabled);
	
	/**
	 * Returns true if the damper is enabled for the spring.
	 * <p>
	 * NOTE: for the damper to have any effect, the spring must be enabled.
	 * @return boolean
	 * @since 4.0.0
	 * @see #isSpringEnabled()
	 */
	public boolean isSpringDamperEnabled();
	
	/**
	 * Returns the damping ratio for the spring's damper.
	 * @return double
	 */
	public double getSpringDampingRatio();
	
	/**
	 * Sets the damping ratio.
	 * <p>
	 * Larger values reduce the oscillation of the spring.
	 * @param dampingRatio the damping ratio; in the range (0, 1]
	 * @throws IllegalArgumentException if damping ratio is less than or equal to zero or greater than 1
	 */
	public void setSpringDampingRatio(double dampingRatio);

	/**
	 * Returns the spring frequency.
	 * @return double
	 */
	public double getSpringFrequency();
	
	/**
	 * Sets the spring frequency.
	 * <p>
	 * Larger values increase the stiffness of the spring.
	 * <p>
	 * Calling this method puts the spring into fixed frequency mode.  In this mode, the spring
	 * stiffness is computed based on the frequency.
	 * @param frequency the spring frequency in hz; must be greater than zero
	 * @throws IllegalArgumentException if frequency is less than or equal to zero
	 */
	public void setSpringFrequency(double frequency);

	/**
	 * Returns the spring stiffness.
	 * @return double
	 */
	public double getSpringStiffness();
	
	/**
	 * Sets the spring stiffness.
	 * <p>
	 * Larger values increase the stiffness of the spring.
	 * <p>
	 * Calling this method puts the spring into fixed stiffness mode.  In this mode, the spring
	 * frequency is computed based on the stiffness.
	 * @param stiffness the spring stiffness (k); must be greater than zero
	 * @throws IllegalArgumentException if stiffness is less than or equal to zero
	 */
	public void setSpringStiffness(double stiffness);
	
	/**
	 * Returns true if the spring force is limited to the maximum.
	 * @return boolean
	 */
	public boolean isMaximumSpringForceEnabled();
	
	/**
	 * Sets whether the spring force is limited to the maximum.
	 * @param enabled true if the spring force should be limited
	 */
	public void setMaximumSpringForceEnabled(boolean enabled);

	/**
	 * Returns the maximum spring force that will be applied.
	 * @return double
	 */
	public double getMaximumSpringForce();
	
	/**
	 * Sets the maximum force the spring can apply.
	 * @param maximum the maximum force
	 * @throws IllegalArgumentException if maximum is less than or equal to zero
	 */
	public void setMaximumSpringForce(double maximum);
	
	/**
	 * Returns the force in netwons applied by the spring in the last timestep.
	 * @param invdt the inverse delta time
	 * @return double
	 */
	public double getSpringForce(double invdt);
	
	/**
	 * Returns the current spring mode.
	 * <p>
	 * NOTE: The spring mode is set automatically when you call either
	 * {@link #setSpringFrequency(double)} or {@link #setSpringStiffness(double)}.
	 * Use this method to store the spring mode when saving the simulation state
	 * and use the value to call either {@link #setSpringFrequency(double)} or
	 * {@link #setSpringStiffness(double)}.
	 * @return int
	 * @see Joint#SPRING_MODE_FREQUENCY
	 * @see Joint#SPRING_MODE_STIFFNESS
	 */
	public int getSpringMode();
}
