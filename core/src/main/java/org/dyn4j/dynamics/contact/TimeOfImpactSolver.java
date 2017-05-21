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
package org.dyn4j.dynamics.contact;

import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.collision.narrowphase.Separation;
import org.dyn4j.dynamics.Body;
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
 * @version 3.2.0
 * @since 2.0.0
 */
public class TimeOfImpactSolver {
	/**
	 * Moves the given {@link Body}s into collision given the {@link TimeOfImpact}
	 * information.
	 * @param body1 the first {@link Body}
	 * @param body2 the second {@link Body}
	 * @param timeOfImpact the {@link TimeOfImpact}
	 * @param settings the current world settings
	 */
	public void solve(Body body1, Body body2, TimeOfImpact timeOfImpact, Settings settings) {
		double linearTolerance = settings.getLinearTolerance();
		double maxLinearCorrection = settings.getMaximumLinearCorrection();
		
		Vector2 c1 = body1.getWorldCenter();
		Vector2 c2 = body2.getWorldCenter();
		
		Mass m1 = body1.getMass();
		Mass m2 = body2.getMass();
		
		double mass1 = m1.getMass();
		double mass2 = m2.getMass();
		
		double invMass1 = mass1 * m1.getInverseMass();
		double invI1 = mass1 * m1.getInverseInertia();
		double invMass2 = mass2 * m2.getInverseMass();
		double invI2 = mass2 * m2.getInverseInertia();
		
		Separation separation = timeOfImpact.getSeparation();
		
		// solve the constraint
		Vector2 p1w = separation.getPoint1();
		Vector2 p2w = separation.getPoint2();
		
		Vector2 r1 = c1.to(p1w);
		Vector2 r2 = c2.to(p2w);
		
		Vector2 n = separation.getNormal();
		double d = separation.getDistance();
		
		double C = Interval.clamp(d - linearTolerance, -maxLinearCorrection, 0.0);
		
		double rn1 = r1.cross(n);
		double rn2 = r2.cross(n);
		
		double K = invMass1 + invMass2 + invI1 * rn1 * rn1 + invI2 * rn2 * rn2;
		
		double impulse = 0.0;
		if (K > 0.0) {
			impulse = -C / K;
		}
		
		Vector2 J = n.product(impulse);

		// translate and rotate the objects
		body1.translate(J.product(invMass1));
		body1.rotate(invI1 * r1.cross(J), c1.x, c1.y);
		
		body2.translate(J.product(-invMass2));
		body2.rotate(-invI2 * r2.cross(J), c2.x, c2.y);
	}
}
