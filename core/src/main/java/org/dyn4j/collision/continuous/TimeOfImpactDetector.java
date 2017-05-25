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
package org.dyn4j.collision.continuous;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

/**
 * Interface representing a time of impact algorithm.
 * <p>
 * Sometimes called Continuous Collision Detection, implementing classes are used to detect collisions
 * between {@link Convex} {@link Shape}s given their initial and final state of a time step. This information 
 * can then be used to solve a collision that was missed during that period.
 * <p>
 * These algorithms are generally very computationally expensive.
 * @author William Bittle
 * @version 3.1.5
 * @since 1.2.0
 */
public interface TimeOfImpactDetector {
	/**
	 * Detects whether the given {@link Convex} {@link Shape}s collide given their current positions and orientation
	 * and the rate of change their position and orientation, returning the time of impact within an epsilon.
	 * <p>
	 * If a collision is detected, the <code>toi</code> parameter will be filled with the time of impact and the 
	 * separation at the time of impact.
	 * <p>
	 * If a time of impact is detected, the time will be in the range [0, 1].  This can be used, along with the change
	 * and position and orientation, to place to the shapes at the time of impact.  Note that the shapes will still
	 * be separated, by a small amount, at the time of impact.
	 * <p>
	 * This method returns false if the shape do not collide.
	 * @param convex1 the first convex shape
	 * @param transform1 the first convex shape's transform
	 * @param dp1 the change in position of the first shape
	 * @param da1 the change in orientation of the first shape
	 * @param convex2 the second convex shape
	 * @param transform2 the second convex shape's transform
	 * @param dp2 the change in position of the second shape
	 * @param da2 the change in orientation of the second shape
	 * @param toi the {@link TimeOfImpact} object to be filled in the case of a collision
	 * @return boolean true if a collision was detected
	 * @since 3.1.5
	 */
	public boolean getTimeOfImpact(Convex convex1, Transform transform1, Vector2 dp1, double da1, Convex convex2, Transform transform2, Vector2 dp2, double da2, TimeOfImpact toi);
	
	/**
	 * Detects whether the given {@link Convex} {@link Shape}s collide given their current positions and orientation
	 * and the rate of change their position and orientation in the time range of [t1, t2] and returning the time of 
	 * impact within an epsilon.
	 * <p>
	 * If a collision is detected, the <code>toi</code> parameter will be filled with the time of impact and the 
	 * separation at the time of impact.
	 * <p>
	 * If a time of impact is detected, the time will be in the range [0, 1].  This can be used, along with the change
	 * and position and orientation, to place to the shapes at the time of impact.  Note that the shapes will still
	 * be separated, by a small amount, at the time of impact.
	 * <p>
	 * This method returns false if the shape do not collide.
	 * @param convex1 the first convex shape
	 * @param transform1 the first convex shape's transform
	 * @param dp1 the change in position of the first shape
	 * @param da1 the change in orientation of the first shape
	 * @param convex2 the second convex shape
	 * @param transform2 the second convex shape's transform
	 * @param dp2 the change in position of the second shape
	 * @param da2 the change in orientation of the second shape
	 * @param toi the {@link TimeOfImpact} object to be filled in the case of a collision
	 * @param t1 the lower time bound
	 * @param t2 the upper time bound
	 * @return boolean true if a collision was detected
	 * @since 3.1.5
	 */
	public boolean getTimeOfImpact(Convex convex1, Transform transform1, Vector2 dp1, double da1, Convex convex2, Transform transform2, Vector2 dp2, double da2, double t1, double t2, TimeOfImpact toi);
}
