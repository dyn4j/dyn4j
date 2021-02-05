/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.dynamics.contact;

import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.collision.narrowphase.Separation;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;

/**
 * Represents a position solver for a pair of {@link Body}s who came in
 * contact during a time step but where not detected by the discrete
 * collision detectors.
 * <p>
 * This class will translate and rotate the {@link Body}s into a collision.
 * @author William Bittle
 * @version 4.1.0
 * @since 2.0.0
 * @param <T> the {@link PhysicsBody} type
 */
public class ForceCollisionTimeOfImpactSolver<T extends PhysicsBody> implements TimeOfImpactSolver<T> {
	/* (non-Javadoc)
	 * @see org.dyn4j.dynamics.contact.TimeOfImpactSolver#solve(org.dyn4j.dynamics.PhysicsBody, org.dyn4j.dynamics.PhysicsBody, org.dyn4j.collision.continuous.TimeOfImpact, org.dyn4j.dynamics.Settings)
	 */
	public void solve(T body1, T body2, TimeOfImpact timeOfImpact, Settings settings) {
		double linearTolerance = settings.getLinearTolerance();
		double maxLinearCorrection = settings.getMaximumLinearCorrection();

		Mass m1 = body1.getMass();
		Mass m2 = body2.getMass();
		
		double mass1 = m1.getMass();
		double mass2 = m2.getMass();
		
		double invMass1 = mass1 * m1.getInverseMass();
		double invMass2 = mass2 * m2.getInverseMass();
		
		Separation separation = timeOfImpact.getSeparation();
		
		Vector2 n = separation.getNormal();
		double d = separation.getDistance();
		
		// setup a simple, linear only, position based, distance constraint to move the 
		// bodies into collision
		// C = d - l = 0
		
		// the distance should be greater than zero (indicating they are separated) and
		// should not exceed maxLinearCorrection (in other words don't move them half way
		// across the screen - but should never happen - the distance should always be
		// very small)
		
		// in addition, we add linearTolerance to the computed distance to ensure that
		// the distance between the bodies is zero and they are in collision.
		double C = Interval.clamp(d + linearTolerance, 0.0, maxLinearCorrection);
		
		// compute the effective mass
		double K = invMass1 + invMass2;
		
		// compute the impulse
		double impulse = 0.0;
		if (K > 0.0) {
			impulse = C / K;
		}
		
		Vector2 J = n.product(impulse);

		// NOTE: previously I was moving the bodies to collision AND rotating them
		// Now I'm just translating them to remove the gap instead.  This loses some
		// very small amount of rotation, but should behave much better
		
		// translate and rotate the objects
		Vector2 tx1 = J.product(invMass1);
		body1.translate(tx1);
		
		Vector2 tx2 = J.product(-invMass2);
		body2.translate(tx2);
	}
}
