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
package org.dyn4j.dynamics;

import org.dyn4j.Listener;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.resources.Messages;

/**
 * Represents the estimated number of objects of different types.
 * <p>
 * This class is used to initially size internal structures to improve performance.
 * These same structures will grow larger than the given sizes if necessary.
 * @author William Bittle
 * @version 3.1.1
 * @since 3.1.1
 */
public class Capacity {
	/** The default {@link Body} count */
	public static final int DEFAULT_BODY_COUNT = 32;
	
	/** The default {@link Joint} count */
	public static final int DEFAULT_JOINT_COUNT = 16;
	
	/** The default {@link Listener} count */
	public static final int DEFAULT_LISTENER_COUNT = 16;
	
	/** The default capacity */
	public static final Capacity DEFAULT_CAPACITY = new Capacity();
	
	// counts
	
	/** The estimated {@link Body} count */
	protected int bodyCount = Capacity.DEFAULT_BODY_COUNT;
	
	/** The estimated {@link Joint} count */
	protected int jointCount = Capacity.DEFAULT_JOINT_COUNT;
	
	/** The estimated {@link Listener} (all listener types) count */
	protected int listenerCount = Capacity.DEFAULT_LISTENER_COUNT;
	
	/**
	 * Default constructor.
	 * <p>
	 * Creates a default capacity with the default counts.
	 */
	public Capacity() {}
	
	/**
	 * Full constructor.
	 * @param bodyCount the estimated number of bodies
	 * @param jointCount the estimated number of joints
	 * @param listenerCount the estimated number of listeners
	 * @throws IllegalArgumentException if any count is less than zero
	 */
	public Capacity(int bodyCount, int jointCount, int listenerCount) {
		if (bodyCount < 0 || jointCount < 0 || listenerCount < 0) throw new IllegalArgumentException(Messages.getString("dynamics.capacity.invalidCapacity"));
		this.bodyCount = bodyCount;
		this.jointCount = jointCount;
		this.listenerCount = listenerCount;
	}
	
	/**
	 * Returns the estimated number of bodies.
	 * @return int
	 */
	public int getBodyCount() {
		return this.bodyCount;
	}
	
	/**
	 * Returns the estimated number of joints.
	 * @return int
	 */
	public int getJointCount() {
		return this.jointCount;
	}
	
	/**
	 * Returns the estimated number of listeners.
	 * @return int
	 */
	public int getListenerCount() {
		return this.listenerCount;
	}
}
