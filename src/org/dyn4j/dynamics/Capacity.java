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
package org.dyn4j.dynamics;

import org.dyn4j.Listener;
import org.dyn4j.dynamics.joint.Joint;

/**
 * Represents the estimated number of objects of different types.
 * <p>
 * This class is used to initially size internal structures to improve performance.
 * These same structures will grow larger than the given sizes if necessary.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.2.0
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
	private final int bodyCount;
	
	/** The estimated {@link Joint} count */
	private final int jointCount;
	
	/** The estimated {@link Listener} (all listener types) count */
	private final int listenerCount;
	
	/**
	 * Default constructor.
	 * <p>
	 * Creates a default capacity with the default counts.
	 */
	public Capacity() {
		this(
			Capacity.DEFAULT_BODY_COUNT,
			Capacity.DEFAULT_JOINT_COUNT,
			Capacity.DEFAULT_LISTENER_COUNT
		);
	}
	
	/**
	 * Full constructor.
	 * @param bodyCount the estimated number of bodies
	 * @param jointCount the estimated number of joints
	 * @param listenerCount the estimated number of listeners
	 * @throws IllegalArgumentException if any count is less than zero
	 */
	public Capacity(int bodyCount, int jointCount, int listenerCount) {
		this.bodyCount = bodyCount > 0 ? bodyCount : Capacity.DEFAULT_BODY_COUNT;
		this.jointCount = jointCount > 0 ? jointCount : Capacity.DEFAULT_JOINT_COUNT;
		this.listenerCount = listenerCount > 0 ? listenerCount : Capacity.DEFAULT_LISTENER_COUNT;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Capacity) {
			Capacity capacity = (Capacity)obj;
			return capacity.bodyCount == this.bodyCount && 
				   capacity.jointCount == this.jointCount &&
				   capacity.listenerCount == this.listenerCount;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + this.bodyCount;
		hash = hash * 31 + this.jointCount;
		hash = hash * 31 + this.listenerCount;
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Capacity[BodyCount=").append(this.bodyCount)
		  .append("|JointCount=").append(this.jointCount)
		  .append("|ListenerCount=").append(this.listenerCount)
		  .append("]");
		return sb.toString();
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
