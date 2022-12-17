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
 * Represents a joint with a linear motor.
 * @author William Bittle
 * @version 5.0.0
 * @since 5.0.0
 */
public interface LinearMotorJoint {
	/**
	 * Returns true if the motor is enabled.
	 * @return boolean
	 */
	public boolean isMotorEnabled();
	
	/**
	 * Enables or disables the motor.
	 * @param motorEnabled true if the motor should be enabled
	 */
	public void setMotorEnabled(boolean motorEnabled);
	
	/**
	 * Returns the target motor speed in meters / second.
	 * @return double
	 */
	public double getMotorSpeed();
	
	/**
	 * Sets the target motor speed.
	 * @param motorSpeed the target motor speed in meters / second
	 * @see #setMaximumMotorForce(double)
	 */
	public void setMotorSpeed(double motorSpeed);
	
	/**
	 * Returns the maximum force the motor can apply to the joint
	 * to achieve the target speed.
	 * @return double
	 */
	public double getMaximumMotorForce();
	
	/**
	 * Sets the maximum force the motor can apply to the joint
	 * to achieve the target speed.
	 * @param maximumMotorForce the maximum force in newtons; must be greater than zero
	 * @throws IllegalArgumentException if maxMotorForce is less than zero
	 * @see #setMotorSpeed(double)
	 */
	public void setMaximumMotorForce(double maximumMotorForce);

	/**
	 * Sets whether the maximum motor force is enabled.
	 * @param enabled true if the maximum motor force should be enabled
	 * @since 5.0.0
	 */
	public void setMaximumMotorForceEnabled(boolean enabled);
	
	/**
	 * Returns true if the maximum motor force is enabled.
	 * @return boolean
	 * @since 5.0.0
	 */
	public boolean isMaximumMotorForceEnabled();
	
	/**
	 * Returns the applied motor force.
	 * @param invdt the inverse delta time
	 * @return double
	 */
	public double getMotorForce(double invdt);
	
}
