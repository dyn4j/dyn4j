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
package org.dyn4j.game2d.dynamics;

/**
 * Contains the current and previous time step information
 * used to update the {@link World}.
 * @author William Bittle
 * @version 2.2.3
 * @since 1.0.0
 */
public class Step {
	/** The last elapsed time */
	protected double dt0;
	
	/** The last inverse elapsed time */
	protected double invdt0;
	
	/** The elapsed time */
	protected double dt;
	
	/** The inverse elapsed time */
	protected double invdt;
	
	/** The elapsed time ratio from the last to the current */
	protected double dtRatio;

	/**
	 * Default constructor.
	 */
	public Step() {
		super();
		// get the current settings
		Settings settings = Settings.getInstance();
		// 1.0 / hz
		this.dt = settings.getStepFrequency();
		this.invdt = 1.0 / dt;
		this.dt0 = this.dt;
		this.invdt0 = this.invdt;
		this.dtRatio = 1.0;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("STEP[")
		.append(this.dt).append("|")
		.append(this.invdt).append("|")
		.append(this.dt0).append("|")
		.append(this.invdt0).append("|")
		.append(this.dtRatio).append("]");
		return sb.toString();
	}
	
	/**
	 * Updates the current {@link Step} using the new elapsed time.
	 * @param dt in seconds.
	 */
	public void update(double dt) {
		this.dt0 = this.dt;
		this.invdt0 = this.invdt;
		this.dt = dt;
		this.invdt = 1.0 / dt;
		this.dtRatio = this.invdt0 * dt;
	}
	
	/**
	 * Returns the elapsed time since the last time step.
	 * @return double
	 */
	public double getDeltaTime() {
		return this.dt;
	}
	
	/**
	 * Returns the inverse of the elapsed time since the last time step.
	 * @return double
	 */
	public double getInverseDeltaTime() {
		return this.invdt;
	}
	
	/**
	 * Returns the ratio of the last elapsed time to the current
	 * elapsed time.
	 * <p>
	 * This is used to cope with a variable time step.
	 * @return double
	 */
	public double getDeltaTimeRatio() {
		return this.dtRatio;
	}
	
	/**
	 * Returns the previous frame's elapsed time.
	 * @return double
	 */
	public double getPrevousDeltaTime() {
		return this.dt0;
	}

	/**
	 * Returns the previous frame's inverse elapsed time.
	 * @return double
	 */
	public double getPreviousInverseDeltaTime() {
		return this.invdt0;
	}
}
