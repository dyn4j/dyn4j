/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d.collision.continuous;

import org.dyn4j.game2d.collision.Collidable;

/**
 * Interface representing a time of impact algorithm.
 * @author William Bittle
 * @version 1.2.0
 * @since 1.2.0
 */
public interface TimeOfImpactDetector {
	/**
	 * Returns true if the given {@link Swept} {@link Collidable}s have a time of impact within
	 * the range [0, 1].
	 * <p>
	 * This method places the result of the time of impact in the toi input parameter.
	 * @see #getTimeOfImpact(Swept, Swept, double, double, TimeOfImpact)
	 * @param swept1 the first {@link Swept} {@link Collidable}
	 * @param swept2 the second {@link Swept} {@link Collidable}
	 * @param toi the time of impact information
	 * @return boolean true if the two have a time of impact within the given range
	 */
	public boolean getTimeOfImpact(Swept swept1, Swept swept2, TimeOfImpact toi);
	
	/**
	 * Returns true if the given {@link Swept} {@link Collidable}s have a time of impact within
	 * the given range.
	 * <p>
	 * This method places the result of the time of impact in the toi input parameter.
	 * @param swept1 the first {@link Swept} {@link Collidable}
	 * @param swept2 the second {@link Swept} {@link Collidable}
	 * @param t1 the time lower bound; must be greater than or equal zero
	 * @param t2 the time upper bound; must be less than or equal to one
	 * @param toi the time of impact information
	 * @return boolean true if the two have a time of impact within the given range
	 */
	public boolean getTimeOfImpact(Swept swept1, Swept swept2, double t1, double t2, TimeOfImpact toi);
}
