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
package org.dyn4j.geometry;

/**
 * Represents a feature of a {@link Shape}. In two dimensions there are 
 * two types of features: vertex and edge.
 * <p>
 * This class does not handle curved edges.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public abstract class Feature {
	/**
	 * Enumeration of {@link Feature} types.
	 * @author William Bittle
	 * @version $Revision: 440 $
	 */
	public static enum Type {
		/** A vertex feature */
		VERTEX,
		/** An edge feature */
		EDGE
	}
	
	/** Index for non-indexed vertices */
	public static final int NOT_INDEXED = -1;
	
	/** The {@link Feature.Type} */
	protected Feature.Type type;
	
	/**
	 * Full constructor.
	 * @param type the feature type
	 */
	public Feature(Feature.Type type) {
		this.type = type;
	}
	
	/**
	 * Returns true if this {@link Feature} is an edge.
	 * @return boolean
	 */
	public boolean isEdge() {
		return this.type == Feature.Type.EDGE;
	}
	
	/**
	 * Returns true if this {@link Feature} is a vertex.
	 * @return boolean
	 */
	public boolean isVertex() {
		return this.type == Feature.Type.VERTEX;
	}
}
