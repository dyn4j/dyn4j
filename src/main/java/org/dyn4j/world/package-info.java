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

/**
 * Package containing the dynamics engine.
 * <p>
 * The dynamics engine is an impulse based rigid body physics simulator.  The simulation
 * by default uses SI (m/kg/s) units.  This can be changed, but all settings within the
 * {@link org.dyn4j.dynamics.Settings} singleton must be set to the new units.  Instead
 * its recommended to use the {@link org.dyn4j.UnitConversion} class to convert all
 * values to MKS.
 * <p>
 * A constraint based approach is used to solving contacts and joints.
 * <p>
 * Create a {@link org.dyn4j.world.World} object to gain access the dynamics engine.  Create 
 * {@link org.dyn4j.dynamics.Body}s and add them to the {@link org.dyn4j.world.World}.  
 * Add the {@link org.dyn4j.world.World#update(double)} method to your game loop (notice the 
 * {@link org.dyn4j.world.World#update(double)} method requires the elapsed time to 
 * be in seconds).
 * <p>
 * Upon creating a {@link org.dyn4j.world.World} a {@link org.dyn4j.collision.Bounds} 
 * object may be supplied.  The {@link org.dyn4j.collision.Bounds} will determine when 
 * {@link org.dyn4j.dynamics.Body}s go out of bounds and deactivate them.  Using the 
 * {@link org.dyn4j.world.World#World()} constructor will create a world that has no bounds,
 * in which case the {@link org.dyn4j.world.World#getBounds()} method will return null.
 * <p>
 * A {@link org.dyn4j.world.World} object also contains a number listeners that can be used to 
 * respond to events that happen within the world, contact for instance.
 * <ul>
 * <li>{@link org.dyn4j.world.listener.BoundsListener} for responding to out of bounds bodies</li>
 * <li>{@link org.dyn4j.world.listener.DestructionListener} for responding to bodies and joints that are 
 * implicitly destroyed by some other action</li>
 * <li>{@link org.dyn4j.world.listener.StepListener} for performing logic at the end/beginning of a time 
 * step</li>
 * <li>{@link org.dyn4j.world.listener.CollisionListener} for responding to collision detection events</li>
 * <li>{@link org.dyn4j.world.listener.ContactListener} for responding to contact events</li>
 * <li>{@link org.dyn4j.world.listener.TimeOfImpactListener} for responding to time of impact events</li>
 * </ul>
 * Please read the respective documentation on each listener.  Certain operations on the 
 * {@link org.dyn4j.world.World} object may not be allowed inside the listener methods.
 * <p>
 * The gravity of the {@link org.dyn4j.world.World} object can be changed, and can be in any 
 * direction via the {@link org.dyn4j.world.World#setGravity(org.dyn4j.geometry.Vector2)} method.
 * <p>
 * The dynamics engine may require some configuration.  The defaults should cover most applications, 
 * however, they can be changed using the {@link org.dyn4j.dynamics.Settings} class on the 
 * {@link org.dyn4j.world.World}.  Any setting can be changed at runtime so that no source code 
 * modification is needed.  Refer to the source of {@link org.dyn4j.dynamics.Settings} and the 
 * Sandbox for details on what each individual setting controls.
 * @author William Bittle
 * @version 4.0.0
 * @since 4.0.0
 */
package org.dyn4j.world;