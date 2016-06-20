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

import org.dyn4j.DataContainer;

/**
 * This class is a specialization of the {@link Segment} class that provides smooth sliding across
 * a chain of line segments.  This is achieved by storing the connectivity information between the
 * links.  With this, a correction process is performed to avoid the 'internal edge' problem.
 * <p>
 * A {@link Link} is an infinitely thin line segment and will behave like the {@link Segment} class in
 * collision response.
 * <p>
 * Like the {@link Segment} class, this class can be locally rotated or translated.  However, doing
 * so will also translate/rotated the next or previous {@link Link}s.
 * <p>
 * For ease of use, it's recommended to use the Geometry class to create chains of {@link Link}s.
 * @author William Bittle
 * @version 3.2.2
 * @since 3.2.2
 */
public class Link extends Segment implements Convex, Wound, Shape, Transformable, DataContainer {
	/** The previous link in the chain */
	Link previous;
	
	/** The next link in the chain */
	Link next;
	
	/**
	 * Creates a new link.
	 * @param point1 the first vertex
	 * @param point2 the last vertex
	 */
	public Link(Vector2 point1, Vector2 point2) {
		super(point1, point2);
	}
	
	/**
	 * Returns the last vertex of the previous segment.
	 * @return Vector2
	 */
	public Vector2 getPoint0() {
		return this.previous != null ? this.previous.getPoint1() : null;
	}
	
	/**
	 * Returns the first vertex of the next segment.
	 * @return Vector2
	 */
	public Vector2 getPoint3() {
		return this.next != null ? this.next.getPoint2() : null;
	}
	
	/**
	 * Returns the next link in the chain.
	 * @return {@link Link}
	 */
	public Link getNext() {
		return this.next;
	}

	/**
	 * Returns the previous link in the chain.
	 * @return {@link Link}
	 */
	public Link getPrevious() {
		return this.previous;
	}
	
	/**
	 * Sets the next link in the chain.
	 * <p>
	 * This method will also:
	 * <ol>
	 * <li>Unlink the current next (if applicable) and this link
	 * <li>Link the given next and this link
	 * </ol>
	 * @param next the next link
	 */
	public void setNext(Link next) {
		if (this.next != null) {
			this.next.previous = null;
		}
		this.next = next;
		if (next != null) {
			next.previous = this;
		}
	}
	
	/**
	 * Sets the previous link in the chain.
	 * <p>
	 * This method will also:
	 * <ol>
	 * <li>Unlink the current previous (if applicable) and this link
	 * <li>Link the given previous and this link
	 * </ol>
	 * @param previous the previous link
	 */
	public void setPrevious(Link previous) {
		if (this.previous != null) {
			this.previous.next = null;
		}
		this.previous = previous;
		if (previous != null) {
			previous.next = this;
		}
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
	
	// NOTE: local rotation and translation will modify the next and previous links
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Segment#rotate(double, double, double)
	 */
	@Override
	public final void rotate(double theta, double x, double y) {
		super.rotate(theta, x, y);
		// we need to update the next/prev links to reflect
		// the change in this link's vertices
		if (this.next != null) {
			this.next.vertices[0].set(this.vertices[1]);
			// update normals
			updateNormals(this.next);
			updateLength(this.next);
		}
		if (this.previous != null) {
			this.previous.vertices[1].set(this.vertices[0]);
			// update normals
			updateNormals(this.previous);
			updateLength(this.previous);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.Segment#translate(double, double)
	 */
	@Override
	public final void translate(double x, double y) {
		super.translate(x, y);
		// we need to update the next/prev links to reflect
		// the change in this link's vertices
		if (this.next != null) {
			this.next.vertices[0].set(this.vertices[1]);
			updateLength(this.next);
		}
		if (this.previous != null) {
			this.previous.vertices[1].set(this.vertices[0]);
			updateLength(this.previous);
		}
	}
	
	/**
	 * Updates the normals of the given {@link Segment}.
	 * <p>
	 * When rotating a link in a link chain, the connected links
	 * will need their normals recomputed to match the change.
	 * @param segment the segment to update
	 */
	private static final void updateNormals(Segment segment) {
		Vector2 v = segment.vertices[0].to(segment.vertices[1]);
		segment.normals[0] = v.copy();
		segment.normals[0].normalize();
		segment.normals[1] = v.right();
		segment.normals[1].normalize();
	}
	
	/**
	 * Updates the length and radius of the given {@link Segment}.
	 * <p>
	 * When rotating or translating a link in a link chain, the connected links
	 * will need their lengths and maximum radius recomputed to match the change.
	 * @param segment the segment to update
	 */
	private static final void updateLength(Segment segment) {
		double length = segment.vertices[0].distance(segment.vertices[1]);
		segment.length = length;
		segment.radius = length * 0.5;
	}
}
