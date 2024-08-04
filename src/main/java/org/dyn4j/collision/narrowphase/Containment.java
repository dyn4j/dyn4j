/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision.narrowphase;

import org.dyn4j.Copyable;
import org.dyn4j.geometry.Convex;

/**
 * Represents the {@link Containment} of one {@link Convex} shape in another.
 * <p>
 * Since the containment test is pair-wise, we can detect whether A is contained by B or
 * B is contained by A in the same detection cycle.  
 * <br>
 * NOTE: The first shape in the detection test is considered A and the second shape is B.
 * @author William Bittle
 * @version 4.2.1
 * @since 4.2.1
 */
public class Containment implements Copyable<Containment> {
	/** True if A is contained in B */
	protected boolean aContainedInB;
	
	/** True if B is contained in A */
	protected boolean bContainedInA;
	
	/**
	 * Default constructor.
	 */
	public Containment() {
		this.aContainedInB = false;
		this.bContainedInA = false;
	}
	
	/**
	 * Full constructor.
	 * @param aContainedInB true if A is contained in B
	 * @param bContainedInA true if B is contained in A
	 */
	protected Containment(boolean aContainedInB, boolean bContainedInA) {
		this.aContainedInB = aContainedInB;
		this.bContainedInA = bContainedInA;
	}
	
	/**
	 * Copy constructor.
	 * @param containment the containment to copy
	 * @since 6.0.0
	 */
	protected Containment(Containment containment) {
		this.aContainedInB = containment.aContainedInB;
		this.bContainedInA = containment.bContainedInA;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Containment[IsAContainedInB=").append(this.aContainedInB)
		.append("|IsBContainedInA=").append(this.bContainedInA)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Clears the separation information.
	 */
	public void clear() {
		this.aContainedInB = false;
		this.bContainedInA = false;
	}
	
	/**
	 * Copies (deep) the given {@link Containment} information to this {@link Containment}.
	 * @param containment the containment to copy
	 * @deprecated Use {@link #set(Containment)} instead
	 */
	@Deprecated
	public void copy(Containment containment) {
		this.set(containment);
	}
	
	/**
	 * Sets this containment to the given containment.
	 * @param containment the containment to use
	 * @since 6.0.0
	 */
	public void set(Containment containment) {
		this.aContainedInB = containment.aContainedInB;
		this.bContainedInA = containment.bContainedInA;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.Copyable#copy()
	 */
	@Override
	public Containment copy() {
		return new Containment(this);
	}

	/**
	 * Returns true if A is contained in B.
	 * @return boolean
	 */
	public boolean isAContainedInB() {
		return this.aContainedInB;
	}

	/**
	 * Sets if A is contained in B.
	 * @param flag true if A is contained in B
	 */
	public void setAContainedInB(boolean flag) {
		this.aContainedInB = flag;
	}

	/**
	 * Returns true if B is contained in A.
	 * @return boolean
	 */
	public boolean isBContainedInA() {
		return this.bContainedInA;
	}

	/**
	 * Sets if B is contained in A.
	 * @param flag true if B is contained in A
	 */
	public void setBContainedInA(boolean flag) {
		this.bContainedInA = flag;
	}
}
