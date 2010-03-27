/*
 * Copyright (c) 2010, William Bittle
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
 * Package containing the dynamics engine.
 * <p>
 * The dynamics engine is an impulse based rigid body physics simulator.  The simulation
 * uses SI (m/kg/s) units.
 * <p>
 * A constraint based approach is used to solving contacts and joints.
 * <p>
 * Create a {@link World} object to gain access the dynamics engine.  Create {@link Body}s
 * and add them to the {@link World} and add the {@link World#update(double)} method to your
 * game loop (notice the {@link World#update(double)} method requires the elapsed time to 
 * be in seconds).
 * <p>
 * Upon creating a {@link World} a {@link Bounds} object must be supplied.  The {@link Bounds}
 * will determine when {@link Body}s go out of bounds and freeze them.
 * <p>
 * A {@link World} object also contains a few listeners:<br />
 * {@link BoundsListener}: notified when a {@link Body} is out of bounds.<br />
 * {@link DestructionListener}: notified of events when a {@link Body} is removed from the {@link World}.<br />
 * {@link ContactListener}: notified of events involving {@link ContactConstraint}s.<br />
 * <p>
 * The gravity of the {@link World} object can be changed, and can be in any direction.
 * <p>
 * Since the engine uses SI units the visual representation will need to be scaled.
 * <p>
 * The dynamics engine requires some configuration, the defaults should cover most applications,
 * that can be changed using the {@link Settings} singleton.  Any setting can be changed at runtime
 * so that no source code modification is needed.  Refer to the source of {@link Settings} and the TestBed
 * for details on what each individual setting controls.
 * <p>
 * When {@link Body}s are created at least one {@link Convex} {@link Shape} and {@link Mass} must be
 * supplied.  Most applications will use this one to one relationship.  However, if the {@link Body}
 * requires more {@link Convex} {@link Shape}s to be added you can use the 
 * <code>Body.addShape(Convex, Mass)</code> method.
 */
package org.dyn4j.game2d.dynamics;

import org.dyn4j.game2d.collision.Bounds;
import org.dyn4j.game2d.collision.BoundsListener;
import org.dyn4j.game2d.dynamics.contact.ContactConstraint;
import org.dyn4j.game2d.dynamics.contact.ContactListener;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Shape;


