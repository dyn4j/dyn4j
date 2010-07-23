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
package org.dyn4j.game2d.geometry;

/**
 * Represents an object that is {@link Transformable}.
 * @author William Bittle
 * @version 1.0.3
 * @since 1.0.0
 */
public interface Transformable {
	/**
	 * Rotates the object about the origin.
	 * @param theta the angle of rotation in radians
	 */
	public abstract void rotate(double theta);
	
	/**
	 * Rotates the object about the given point.
	 * @param theta the angle of rotation in radians
	 * @param point the point to rotate about
	 */
	public abstract void rotate(double theta, Vector2 point);
	
	/**
	 * Rotates the object about the given coordinates.
	 * @param theta the angle of rotation in radians
	 * @param x the x coordinate to rotate about
	 * @param y the y coordinate to rotate about
	 */
	public abstract void rotate(double theta, double x, double y);
	
	/**
	 * Translates the object the given amounts in the respective directions.
	 * @param x the translation in the x direction
	 * @param y the translation in the y direction
	 */
	public abstract void translate(double x, double y);
	
	/**
	 * Translates the object along the given vector.
	 * @param vector the translation along a vector
	 */
	public abstract void translate(Vector2 vector);
}

