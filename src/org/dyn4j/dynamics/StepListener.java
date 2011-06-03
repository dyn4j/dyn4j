/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.dynamics;

import org.dyn4j.collision.broadphase.BroadphaseDetector;
import org.dyn4j.collision.narrowphase.NarrowphaseDetector;

/**
 * Listener notified before and after a simulation step by the {@link World}.
 * @author William Bittle
 * @version 3.0.0
 * @since 1.0.0
 */
public interface StepListener {
	/**
	 * Called before a simulation step is performed.
	 * @param step the step information
	 * @param world the simulation {@link World}
	 */
	public void begin(Step step, World world);
	
	/**
	 * Called after contacts and joints have been solved but <b>before</b> new contact points 
	 * have been found and before the broadphase has been updated.
	 * <p>
	 * This method is intended to be used so that the {@link World#setUpdateRequired(boolean)}
	 * method does not have to be called, therefore saving time in the {@link World#update(double)}
	 * and {@link World#updatev(double)} methods.
	 * <p>
	 * Raycasts at this time can be incorrect since the broadphase is out of date.
	 * Use the {@link World#getBroadphaseDetector()} method to get access to the 
	 * {@link BroadphaseDetector#update(org.dyn4j.collision.Collidable)} method.  
	 * Call this on all bodies to update the broadphase only.
	 * <p>
	 * The {@link BroadphaseDetector#detect(org.dyn4j.geometry.AABB)} also relies on the
	 * broadphase being up to date.  Use the {@link World#getBroadphaseDetector()} method
	 * to get access to the {@link BroadphaseDetector#update(org.dyn4j.collision.Collidable)}
	 * method.  Call this on all bodies to update the broadphase only.
	 * <p>
	 * Using the {@link BroadphaseDetector#detect(org.dyn4j.collision.Collidable, org.dyn4j.collision.Collidable)},
	 * {@link BroadphaseDetector#detect(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform)},
	 * {@link NarrowphaseDetector#detect(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform)},
	 * or {@link NarrowphaseDetector#detect(org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.geometry.Convex, org.dyn4j.geometry.Transform, org.dyn4j.collision.narrowphase.Penetration)}
	 * method is guaranteed to be accurate.
	 * @param step the step information
	 * @param world the simulation {@link World}
	 * @since 3.0.0
	 */
	public void preDetect(Step step, World world);
	
	/**
	 * Called after a simulation step has been performed.
	 * @param step the step information
	 * @param world the simulation {@link World}
	 */
	public void end(Step step, World world);
}
