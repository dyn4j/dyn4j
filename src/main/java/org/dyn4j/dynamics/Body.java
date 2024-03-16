/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.dynamics;

import org.dyn4j.DataContainer;
import org.dyn4j.Ownable;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Transformable;

/**
 * Represents an object in a simulation that reacts as defined by newtonian mechanics.
 * <p>
 * A {@link Body} typically has at least one {@link BodyFixture} attached to it. 
 * the {@link BodyFixture}s represent the shape of the body.  When a body 
 * is first created the body is a shapeless infinite mass body.  Add fixtures to
 * the body using the <code>addFixture</code> methods.
 * <p>
 * Use the {@link #setMass(org.dyn4j.geometry.MassType)} methods to calculate the 
 * mass of the entire {@link Body} given the currently attached
 * {@link BodyFixture}s.  The {@link #setMass(Mass)} method can be used to set
 * the mass directly.  Use the {@link #setMassType(org.dyn4j.geometry.MassType)}
 * method to toggle the mass type between the special types.
 * <p>
 * The coefficient of friction and restitution and the linear and angular damping
 * are all defaulted but can be changed via the accessor and mutator methods.
 * <p>
 * By default {@link Body}s will be flagged as at-rest automatically. This occurs when 
 * their linear or angular velocity is low enough (as determined by the 
 * {@link Settings#getMaximumAtRestLinearVelocity()} and 
 * {@link Settings#getMaximumAtRestAngularVelocity()} methods) and they have been this way
 * for a period of time (as determined by {@link Settings#getMinimumAtRestTime()}).  Applying 
 * any force, torque, or impulse will reset the at-rest state to not at-rest. Adding or removing fixtures,
 * changing the mass or mass type, setting linear/angular damping, and setting gravity scale
 * will also reset the at-rest state to not at-rest.  Changing the transform, rotating, translating,
 * or setting the linear or angular velocities will <b>NOT</b> reset the at-rest state. 
 * <p>
 * A {@link Body} becomes disabled when the {@link Body} has left the boundary of
 * the world.
 * <p>
 * A {@link Body} is dynamic if either its inertia or mass is greater than zero.
 * A {@link Body} is static if both its inertia and mass are close to zero.
 * <p>
 * A {@link Body} flagged as a bullet {@link #setBullet(boolean)} will be checked for 
 * tunneling depending on the CCD setting in the world's {@link Settings}.  Use this if the body 
 * is a fast moving body, but be careful as this will incur a performance hit.
 * @author William Bittle
 * @version 5.0.2
 * @since 1.0.0
 */
public class Body extends AbstractPhysicsBody implements PhysicsBody, CollisionBody<BodyFixture>, Transformable, DataContainer, Ownable {
	
}