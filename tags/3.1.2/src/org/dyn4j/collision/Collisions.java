/*
 * Copyright (c) 2010-2012 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision;

import org.dyn4j.collision.Collidable;

/**
 * Class used to estimate collision counts and other one-off collision tasks.
 * @author William Bittle
 * @version 3.1.1
 * @since 3.1.1
 */
public class Collisions {
	/**
	 * The estimated collisions per body.
	 * <p>
	 * Worst Case:
	 * <pre>size * size(every body colliding with every body) - size(remove self collisions)</pre>
	 * Which is just way too large.  Dividing by a factor is still grossly over estimated so
	 * I opted to do an estimate on the test results found in the Sandbox application.
	 * <p>
	 * Test Results:
	 * <pre>
	 * +----------+------------+-----------------+-----------------+
	 * | Test     | Body Count | Collision Pairs | Collisions/Body |
	 * +----------+------------+-----------------+-----------------+
	 * | Bucket   |        200 |            ~500 |             2.5 |
	 * | Funnel   |        200 |            ~400 |             2.0 |
	 * | Parallel |        300 |             600 |             2.0 |
	 * +----------+------------+-----------------+-----------------+</pre>
	 * Therefore a good estimate could be 4 collisions per body.
	 * <p>
	 * Unfortunately a better estimate would be to also count the number of fixtures among all bodies since
	 * each fixture on each body could be in contact with one other fixture of another body.
	 * <p>
	 * This field may not exist (may get deprecated) in future versions if a better estimate technique is found.
	 */
	protected static final int ESTIMATED_COLLISIONS_PER_BODY = 4;
	
	/**
	 * Returns an estimate on the number of collision pairs given the number of collidables.
	 * @param n the number of {@link Collidable}s
	 * @return int
	 */
	public static final int getEstimatedCollisionPairs(int n) {
		return n * Collisions.ESTIMATED_COLLISIONS_PER_BODY;
	}
	
	/**
	 * Returns an estimate on the number of collisions per collidable.
	 * @return int
	 */
	public static final int getEstimatedCollisions() {
		// at this time it just returns the static field above
		// this could change at any time so we keep the original method
		// and make the static field private
		return Collisions.ESTIMATED_COLLISIONS_PER_BODY;
	}
}
