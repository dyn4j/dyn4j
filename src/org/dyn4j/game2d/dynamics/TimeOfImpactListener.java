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
package org.dyn4j.game2d.dynamics;

/**
 * Interface to listen for TOI events.
 * @author William Bittle
 * @version 1.2.0
 * @since 1.2.0
 */
public interface TimeOfImpactListener {
	/**
	 * Called when a time of impact has been detected between a dynamic
	 * {@link Body} and a kinematic/static {@link Body}.
	 * <p>
	 * Returning true from this method indicates that the collision of these
	 * two {@link Body}s should be processed.
	 * @param dynamic the dynamic {@link Body}
	 * @param other the other {@link Body}
	 * @param toi the time of impact between the two {@link Body}s
	 * @return boolean true if the collision should be handled
	 */
	public abstract boolean dynamic(Body dynamic, Body other, double toi);
	
	/**
	 * Called when a time of impact has been detected between a bullet
	 * {@link Body} and another {@link Body}.
	 * <p>
	 * Returning true from this method indicates that the collision of these
	 * two {@link Body}s should be processed.
	 * @param bullet the bullet {@link Body}
	 * @param other the other {@link Body}
	 * @param toi the time of impact between the two {@link Body}s
	 * @return boolean true if the collision should be handled
	 */
	public abstract boolean bullet(Body bullet, Body other, double toi);
}
