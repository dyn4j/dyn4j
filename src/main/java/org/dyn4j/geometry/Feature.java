/*
 * Copyright (c) 2010-2024 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.geometry;

import org.dyn4j.Copyable;

/**
 * Represents an indexed feature of a {@link Shape}.
 * @author William Bittle
 * @version 6.0.0
 * @since 1.0.0
 */
public abstract class Feature implements Copyable<Feature> {
	/** Index for non-indexed vertices */
	public static final int NOT_INDEXED = -1;

	/** The index of the edge on the shape */
	final int index;
	
	/**
	 * Minimal constructor.
	 * @param index the index of the feature in the {@link Shape}
	 */
	public Feature(int index) {
		this.index = index;
	}

	/**
	 * Copy constructor.
	 * @param feature the feature to copy
	 */
	protected Feature(Feature feature) {
		this.index = feature.index;
	}
	
	/**
	 * Returns the edge index.
	 * <p>
	 * If the index == {@link #NOT_INDEXED} then
	 * this feature represents a curved shape feature.
	 * @return int
	 */
	public int getIndex() {
		return this.index;
	}
}
