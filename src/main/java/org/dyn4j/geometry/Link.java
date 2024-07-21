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

import org.dyn4j.DataContainer;

/**
 * This class is a specialization of the {@link Segment} class that provides smooth sliding across
 * a chain of line segments.  This is achieved by storing the connectivity information between the
 * links.  With this, a correction process is performed to avoid the 'internal edge' problem.
 * <p>
 * A {@link Link} is an infinitely thin line segment and will behave like the {@link Segment} class in
 * collision response.
 * <p>
 * Unlike the {@link Segment} class, this class cannot be locally rotated or translated. All the
 * <code>rotate</code> and <code>translate</code> methods throw <code>UnsupportedOperationException</code>s
 * as of 6.0.0.
 * <p>
 * For ease of use, it's recommended to use the Geometry class to create chains of {@link Link}s.
 * @author William Bittle
 * @version 6.0.0
 * @since 3.2.2
 */
public class Link extends Segment implements Convex, Wound, Shape, Transformable, DataContainer {
	/** The point before to point 1 */
	final Vector2 point0;
	
	/** The point after to point 2 */
	final Vector2 point3;
	
	/**
	 * Creates a new link.
	 * @param point0 the point before the first vertex
	 * @param point1 the first vertex
	 * @param point2 the last vertex
	 * @param point3 the point after the second vertex
	 */
	public Link(Vector2 point0, Vector2 point1, Vector2 point2, Vector2 point3) {
		super(point1, point2);
		this.point0 = point0;
		this.point3 = point3;
	}
	
	/**
	 * Copy constructor.
	 * @param link the link to copy
	 * @since 6.0.0
	 */
	protected Link(Link link) {
		super(link);
		this.point0 = link.point0 != null ? link.point0.copy() : null;
		this.point3 = link.point3 != null ? link.point3.copy() : null;
	}
	
	/**
	 * {@inheritDoc}
	 * @return {@link Link}
	 * @since 6.0.0
	 */
	@Override
	public Link copy() {
		return new Link(this);
	}
	
	/**
	 * Returns the last vertex of the previous segment.
	 * @return Vector2
	 */
	public Vector2 getPoint0() {
		return this.point0;
	}
	
	/**
	 * Returns the first vertex of the next segment.
	 * @return Vector2
	 */
	public Vector2 getPoint3() {
		return this.point3;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Segment#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Link[").append(super.toString())
		.append("|Length=").append(this.length)
		.append("]");
		return sb.toString();
	}
	
	// NOTE: local rotation and translation throw
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#rotate(double)
	 */
	@Override
	public void rotate(double theta) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Segment#rotate(org.dyn4j.geometry.Rotation, double, double)
	 */
	@Override
	public void rotate(Rotation rotation, double x, double y) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#rotate(double, double, double)
	 */
	@Override
	public final void rotate(double theta, double x, double y) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#rotate(double, org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void rotate(double theta, Vector2 point) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#rotate(org.dyn4j.geometry.Rotation)
	 */
	@Override
	public void rotate(Rotation rotation) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#rotate(org.dyn4j.geometry.Rotation, org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void rotate(Rotation rotation, Vector2 point) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#rotateAboutCenter(double)
	 */
	@Override
	public void rotateAboutCenter(double theta) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.AbstractShape#translate(org.dyn4j.geometry.Vector2)
	 */
	@Override
	public void translate(Vector2 vector) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Segment#translate(double, double)
	 */
	@Override
	public final void translate(double x, double y) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the edge vector for the previous segment.
	 * @return {@link Vector2}
	 * @since 6.0.0
	 */
	public Vector2 getPreviousEdgeVector() {
		Vector2 edge = this.point0.to(this.vertices[0]);
		edge.normalize();
		return edge;
	}
	
	/**
	 * Returns the edge vector for the next segment.
	 * @return {@link Vector2}
	 * @since 6.0.0
	 */
	public Vector2 getNextEdgeVector() {
		Vector2 edge = this.vertices[1].to(this.point3);
		edge.normalize();
		return edge;
	}
}
