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
package org.dyn4j.geometry;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * Represents an iterator of {@link Vector2}s for the vertices and normals
 * of a {@link Wound} shape.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.2.0
 */
final class WoundIterator implements Iterator<Vector2> {
	/** The array to iterate over */
	final Vector2[] vectors;
	
	/** The current index */
	int index;
	
	/**
	 * Minimal constructor.
	 * @param vectors the array to iterate over
	 */
	public WoundIterator(Vector2[] vectors) {
		this.vectors = vectors;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.index + 1 < this.vectors.length;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Vector2 next() {
		if (this.index >= this.vectors.length) {
			throw new IndexOutOfBoundsException();
		}
		try {
			this.index++;
			Vector2 v = this.vectors[this.index];
			return v.copy();
		} catch (IndexOutOfBoundsException ex) {
			throw new ConcurrentModificationException();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
