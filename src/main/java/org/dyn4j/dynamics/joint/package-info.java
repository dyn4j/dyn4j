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

/**
 * Sub package of the Dynamics package containing joints.
 * <p>
 * Joints are connections between bodies that limit their relative motion.  
 * <p>
 * Joints are solved using an iterative constraint based approach in the same manner as contacts.
 * <p>
 * All joints, with exception of the {@link org.dyn4j.dynamics.joint.PinJoint} join a pair
 * of {@link org.dyn4j.dynamics.Body}s.
 * <p>
 * Current joint implementations:
 * <ul>
 * <li>{@link org.dyn4j.dynamics.joint.AngleJoint} for constraining the rotation of two bodies</li>
 * <li>{@link org.dyn4j.dynamics.joint.DistanceJoint} for fixed length distance and spring/damper</li>
 * <li>{@link org.dyn4j.dynamics.joint.FrictionJoint} for applying friction, air resistance, joint
 * friction, etc</li>
 * <li>{@link org.dyn4j.dynamics.joint.MotorJoint} primarily for character control</li>
 * <li>{@link org.dyn4j.dynamics.joint.PinJoint} specifically to connect a distance joint with
 * spring/damper to one body</li>
 * <li>{@link org.dyn4j.dynamics.joint.PrismaticJoint} for only allowing relative linear motion
 * along an axis with or without a motor and limits</li>
 * <li>{@link org.dyn4j.dynamics.joint.PulleyJoint} for creating a pulley with or without block-and-
 * tackle</li>
 * <li>{@link org.dyn4j.dynamics.joint.RevoluteJoint} for only allowing relative rotation with or
 * without a motor</li>
 * <li>{@link org.dyn4j.dynamics.joint.RopeJoint} for min/max distance between bodies</li>
 * <li>{@link org.dyn4j.dynamics.joint.WeldJoint} for connecting two bodies together completely</li>
 * <li>{@link org.dyn4j.dynamics.joint.WheelJoint} for connecting two bodies in a frame-wheel type
 * configuration with linear spring/damper and angular motor</li>
 * </ul>
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
package org.dyn4j.dynamics.joint;