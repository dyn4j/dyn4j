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

import java.util.Iterator;

import org.dyn4j.DataContainer;

/**
 * Represents a shape that is defined by vertices with line segment connections
 * with counter-clockwise winding.
 * @author William Bittle
 * @version 3.2.0
 * @since 1.0.0
 */
public interface Wound extends Shape, Transformable, DataContainer {
	/**
	 * Returns an iterator for the vertices.
	 * <p>
	 * The iterator does not support the remove method and will return a new
	 * {@link Vector2} in the next method.
	 * <p>
	 * This method is safer than the {@link #getVertices()} since its not
	 * possible to modify the array or its elements.
	 * @return Iterator&lt;{@link Vector2}&gt;
	 * @since 3.2.0
	 */
	public abstract Iterator<Vector2> getVertexIterator();
	
	/**
	 * Returns an iterator for the normals.
	 * <p>
	 * The iterator does not support the remove method and will return a new
	 * {@link Vector2} in the next method rather than the underlying value.
	 * <p>
	 * This method is safer than the {@link #getNormals()} since its not
	 * possible to modify the array or its elements.
	 * @return Iterator&lt;{@link Vector2}&gt;
	 * @since 3.2.0
	 */
	public abstract Iterator<Vector2> getNormalIterator();
	
	/**
	 * Returns the array of vertices in local coordinates.
	 * <p>
	 * For performance, this array may be the internal storage array of the shape.
	 * Both the array elements and their properties should not be modified via this
	 * method.
	 * <p>
	 * It's possible that this method will be deprecated and/or removed in later versions.
	 * @return {@link Vector2}[]
	 * @see #getVertexIterator()
	 */
	public abstract Vector2[] getVertices();
	
	/**
	 * Returns the array of edge normals in local coordinates.
	 * <p>
	 * For performance, this array may be the internal storage array of the shape.
	 * Both the array elements and their properties should not be modified via this
	 * method.
	 * <p>
	 * It's possible that this method will be deprecated and/or removed in later versions.
	 * @return {@link Vector2}[]
	 * @see #getNormalIterator()
	 */
	public abstract Vector2[] getNormals();
}
