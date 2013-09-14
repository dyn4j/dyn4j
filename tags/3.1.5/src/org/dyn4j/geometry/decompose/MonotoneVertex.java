/*
 * Copyright (c) 2010-2013 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.geometry.decompose;

/**
 * Represents a vertex of a monotone polygon.
 * @author William Bittle
 * @version 3.0.2
 * @since 2.2.0
 * @param <E> the vertex data type
 */
public class MonotoneVertex<E> {
	/** The vertex data */
	protected E data;
	
	/** The next vertex in CCW winding */
	protected MonotoneVertex<E> next;
	
	/** The prev vertex in CCW winding */
	protected MonotoneVertex<E> prev;
	
	/** The monotone chain indicator */
	protected MonotoneChain.Type chain;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MonotoneVertex[Data=").append(this.data)
		.append("|ChainType=").append(this.chain)
		.append("]");
		return sb.toString();
	}
	
	/**
	 * Returns true if the given vertex is adjacent to this vertex.
	 * @param vertex the vertex to test
	 * @return boolean
	 */
	public boolean isAdjacent(MonotoneVertex<E> vertex) {
		return vertex == prev || vertex == next;
	}
	
	/**
	 * Returns the vertex data.
	 * @return E
	 */
	public E getData() {
		return this.data;
	}
	
	/**
	 * Sets the data for this vertex.
	 * @param data the vertex data
	 */
	public void setData(E data) {
		this.data = data;
	}
	
	/**
	 * Returns the next vertex in CCW winding order.
	 * @return {@link MonotoneVertex}
	 */
	public MonotoneVertex<E> getNext() {
		return next;
	}
	
	/**
	 * Sets the next vertex in CCW winding order.
	 * @param next the next vertex
	 */
	public void setNext(MonotoneVertex<E> next) {
		this.next = next;
	}
	
	/**
	 * Returns the previous vertex in CCW winding order.
	 * @return {@link MonotoneVertex}
	 */
	public MonotoneVertex<E> getPrev() {
		return prev;
	}
	
	/**
	 * Sets the previous vertex in CCW winding order.
	 * @param prev the previous vertex
	 */
	public void setPrev(MonotoneVertex<E> prev) {
		this.prev = prev;
	}
	
	/**
	 * Returns the monotone chain type.
	 * @return {@link MonotoneChain.Type}
	 */
	public MonotoneChain.Type getChain() {
		return chain;
	}
	
	/**
	 * Sets the monotone chain type.
	 * @param chain the monotone chain type
	 */
	public void setChain(MonotoneChain.Type chain) {
		this.chain = chain;
	}
}
