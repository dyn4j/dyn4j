/*
 * Copyright (c) 2010-2020 William Bittle  http://www.dyn4j.org/
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
 * Package containing classes that handle physical interaction.
 * <p>
 * There are three constructs that this and sub packages handle:
 * <ul>
 * <li>Physical Bodies</li>
 * <li>Contacts</li>
 * <li>Joints</li>
 * </ul>
 * <b>Physical Bodies</b>
 * <p>
 * Physical bodies are respresented by the {@link org.dyn4j.dynamics.PhysicsBody}
 * interface and the {@link org.dyn4j.dynamics.Body} class. These add the concepts
 * of velocity, force, impulse, etc. to the {@link org.dyn4j.collision.CollisionBody}.
 * <p>
 * <b>Contacts</b>
 * <p>
 * Contacts are represented by the {@link org.dyn4j.dynamics.contact.Contact} interface
 * and the {@link org.dyn4j.dynamics.contact.ContactConstraint} class. Together these
 * define contact between two {@link org.dyn4j.dynamics.PhysicsBody}s that needs to be
 * resolved. The {@link org.dyn4j.dynamics.contact.ContactConstraintSolver} defines the
 * process to solve a set of contacts.
 * <p>
 * <b>Joints</b>
 * <p>
 * Joints are rules placed between two {@link org.dyn4j.dynamics.PhysicsBody}s that 
 * governs their motion. For example the {@link org.dyn4j.dynamics.joint.DistanceJoint}
 * is used to keep two {@link org.dyn4j.dynamics.PhysicsBody}s within some distance
 * of each other.
 * @author William Bittle
 * @version 4.0.0
 * @since 1.0.0
 */
package org.dyn4j.dynamics;