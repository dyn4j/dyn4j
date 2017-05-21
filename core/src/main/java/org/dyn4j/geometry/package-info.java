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

/**
 * This package contains geometric and mathematical constructs.
 * <p>
 * The main focus of this package are {@link org.dyn4j.geometry.Convex} {@link org.dyn4j.geometry.Shape}s.
 * The classes implementing the {@link org.dyn4j.geometry.Convex} interface can be used in collision
 * detection.
 * <p>
 * Current supported convex shapes:
 * <ul>
 * <li>{@link org.dyn4j.geometry.Circle}
 * <li>{@link org.dyn4j.geometry.Polygon} an arbitrary convex polygon
 * <li>{@link org.dyn4j.geometry.Rectangle}
 * <li>{@link org.dyn4j.geometry.Triangle}
 * <li>{@link org.dyn4j.geometry.Slice} a piece of a circle
 * <li>{@link org.dyn4j.geometry.Ellipse}
 * <li>{@link org.dyn4j.geometry.HalfEllipse}
 * <li>{@link org.dyn4j.geometry.Segment} special case shape; take care when using this class
 * </ul>
 * Some {@link org.dyn4j.geometry.Shape}s, particularly {@link org.dyn4j.geometry.Ellipse} and
 * {@link org.dyn4j.geometry.HalfEllipse} are not compatible with all collision detection algorithms.
 * <p>
 * To create more {@link org.dyn4j.geometry.Shape} types extend the {@link org.dyn4j.geometry.AbstractShape} class
 * and implement the {@link org.dyn4j.geometry.Convex} interface to allow participation in collision detection.
 * <p>
 * All shapes can be constructed using their respective constructors or via the {@link org.dyn4j.geometry.Geometry} 
 * class. The {@link org.dyn4j.geometry.Shape} constructors do not duplicate the input information whereas the
 * {@link org.dyn4j.geometry.Geometry} class methods do.  The {@link org.dyn4j.geometry.Geometry} class methods can
 * also place the newly created shape at the origin for easier manipulation.
 * <p>
 * While the {@link org.dyn4j.geometry.Shape} classes allow for some manipulation, but it's not recommend to do so
 * after creation, except for the rotate and translate methods.
 * <p>
 * Vectors, points and vertices are all represented by the {@link org.dyn4j.geometry.Vector2} class.
 * @author William Bittle 
 * @version 3.2.0
 * @since 1.0.0
 */
package org.dyn4j.geometry;