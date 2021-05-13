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
package org.dyn4j.world;

import org.dyn4j.dynamics.BodyFixture;

/**
 * Interface used to customize the way friction and restitution coefficients are mixed.
 * <p>
 * The {@link #DEFAULT_MIXER} performs the following operations for friction and
 * restitution mixing respectively:
 * <pre> sqrt(friction1 * friction2)
 * max(restitution1, restitution2)</pre>
 * <p>
 * Renamed from CoefficientMixer in 4.2.0.
 * @author William Bittle
 * @version 4.2.0
 * @since 1.0.0
 */
public interface ValueMixer {
	/** The default dynamics mixer */
	public static final ValueMixer DEFAULT_MIXER = new ValueMixer() {
		/* (non-Javadoc)
		 * @see org.dyn4j.dynamics.DynamicsMixer#mixFriction(double, double)
		 */
		@Override
		public double mixFriction(double friction1, double friction2) {
			return Math.sqrt(friction1 * friction2);
		}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.dynamics.DynamicsMixer#mixRestitution(double, double)
		 */
		@Override
		public double mixRestitution(double restitution1, double restitution2) {
			return Math.max(restitution1, restitution2);
		}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.world.ValueMixer#mixRestitutionVelocity(double, double)
		 */
		@Override
		public double mixRestitutionVelocity(double restitutionVelocity1, double restitutionVelocity2) {
			return Math.min(restitutionVelocity1, restitutionVelocity2);
		}
	};
	
	/**
	 * Method used to mix the coefficients of friction of two {@link BodyFixture}s.
	 * @param friction1 the coefficient of friction for the first {@link BodyFixture}
	 * @param friction2 the coefficient of friction for the second {@link BodyFixture}
	 * @return double
	 */
	public abstract double mixFriction(double friction1, double friction2);
	
	/**
	 * Method used to mix the coefficients of restitution of two {@link BodyFixture}s.
	 * @param restitution1 the coefficient of restitution for the first {@link BodyFixture}
	 * @param restitution2 the coefficient of restitution for the second {@link BodyFixture}
	 * @return double
	 */
	public abstract double mixRestitution(double restitution1, double restitution2);
	
	
	/**
	 * Method used to mix the restitution velocity of two {@link BodyFixture}s.
	 * @param restitutionVelocity1 the restitution velocity for the first {@link BodyFixture}
	 * @param restitutionVelocity2 the restitution velocity for the second {@link BodyFixture}
	 * @return double
	 * @since 4.2.0
	 */
	public abstract double mixRestitutionVelocity(double restitutionVelocity1, double restitutionVelocity2);
}
