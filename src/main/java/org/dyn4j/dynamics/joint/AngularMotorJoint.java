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
 * Represents a joint with an angular motor.
 * @author William Bittle
 * @version 5.0.0
 * @since 5.0.0
 */
public interface AngularMotorJoint {
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
	 * Returns the target motor speed in radians / second.
	 * @return double
	 */
	public double getMotorSpeed();
	
	/**
	 * Sets the target motor speed.
	 * @param motorSpeed the target motor speed in radians / second
	 * @see #setMaximumMotorTorque(double)
	 */
	public void setMotorSpeed(double motorSpeed);
	
	/**
	 * Returns the maximum torque the motor can apply to the joint
	 * to achieve the target speed.
	 * @return double
	 */
	public double getMaximumMotorTorque();
	
	/**
	 * Sets the maximum torque the motor can apply to the joint
	 * to achieve the target speed.
	 * @param maximumMotorTorque the maximum torque in newtons-meters; in the range (0, &infin;]
	 * @throws IllegalArgumentException if maximumMotorTorque is less than or equal to zero
	 * @see #setMotorSpeed(double)
	 */
	public void setMaximumMotorTorque(double maximumMotorTorque);
	
	/**
	 * Sets whether the maximum motor torque is enabled.
	 * @param enabled true if the maximum motor torque should be enabled
	 * @since 5.0.0
	 */
	public void setMaximumMotorTorqueEnabled(boolean enabled);
	
	/**
	 * Returns true if the maximum motor torque is enabled.
	 * @return boolean
	 * @since 5.0.0
	 */
	public boolean isMaximumMotorTorqueEnabled();
	
	/**
	 * Returns the applied motor torque.
	 * @param invdt the inverse delta time from the time step
	 * @return double
	 */
	public double getMotorTorque(double invdt);
}
