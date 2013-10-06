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

/**
 * This package contains geometric objects used by the collision detection package.
 * <p>
 * Includes many classes used by all portions of the library primarily for representing geometric objects.
 * <p>
 * The {@link org.dyn4j.geometry.Convex} interface and subclasses and
 * {@link org.dyn4j.geometry.Vector2} class are the main focus for this package.
 * <p>
 * All concrete implementations of the {@link org.dyn4j.geometry.Convex} interface are available for 
 * use in the collision detection system and by extension the dynamics system.
 * <p>
 * Current supported convex shapes:
 * <ul>
 * <li>{@link org.dyn4j.geometry.Circle}</li>
 * <li>{@link org.dyn4j.geometry.Polygon} both concrete class and base class for 
 * {@link org.dyn4j.geometry.Rectangle} and {@link org.dyn4j.geometry.Triangle}</li>
 * <li>{@link org.dyn4j.geometry.Rectangle} polygon with more efficient methods than its base class
 * </li>
 * <li>{@link org.dyn4j.geometry.Triangle} polygon with more efficient methods than its base class</li>
 * <li>{@link org.dyn4j.geometry.Segment} special case shape; take care when using this class</li>
 * </ul>
 * To add more {@link org.dyn4j.geometry.Convex} {@link org.dyn4j.geometry.Shape}s extend the
 * {@link org.dyn4j.geometry.AbstractShape} class.
 * <p>
 * All shapes can be constructed using their respective constructors or via the 
 * {@link org.dyn4j.geometry.Geometry} class.  This class gives other options that make generating
 * specific types of shapes easier and safer.  It's recommended to use the methods contained in this class
 * instead of the shape constructors.
 * @author William Bittle 
 * @version 2.2.2
 * @since 1.0.0
 */
package org.dyn4j.geometry;