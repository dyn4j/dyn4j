/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d.geometry.decompose;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.dyn4j.game2d.Epsilon;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Triangle;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Represents a monotone polygon.
 * <p>
 * A monotone polygon can be triangulated in O(n) time.  Algorithms within this package may decompose
 * a polygon into monotone pieces, which are then used to decompose into triangles.
 * @author William Bittle
 * @version 2.2.0
 * @since 2.2.0
 */
public class MonotonePolygon {
	/**
	 * Enumeration of the types of monotone polygons supported.
	 * <p>
	 * The type of monotone polygon is the axis in which the polygon
	 * is monotone with respect to.
	 * @author William Bittle
	 * @version 2.2.0
	 * @since 2.2.0
	 */
	public enum Type {
		/** Represents a y-monotone polygon */
		Y,
		/** Represents a x-monotone polygon */
		X
	}
	
	/** The type of monotone polygon */
	protected MonotonePolygon.Type type;
	
	/** The sorted array of vertices */
	protected MonotoneVertex[] vertices;
	
	/**
	 * Full constructor.
	 * @param type the monotone polygon type
	 * @param vertices the sorted array of vertices; descending order
	 */
	public MonotonePolygon(MonotonePolygon.Type type, MonotoneVertex[] vertices) {
		this.type = type;
		this.vertices = vertices;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MONOTONE_POLYGON[")
		.append(this.type);
		
		int size = this.vertices.length;
		for (int i = 0; i < size; i++) {
			sb.append("|").append(this.vertices[i]);
		}
		
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * Decomposes this monotone polygon into a list of triangles.
	 * @return List&lt;{@link Convex}&gt;
	 */
	public List<Convex> decompose() {
		Stack<MonotoneVertex> stack = new Stack<MonotoneVertex>();
		
		// push the first two onto the stack
		stack.push(vertices[0]);
		stack.push(vertices[1]);
		
		List<Convex> triangles = new ArrayList<Convex>();
		
		int i = 2;
		while (!stack.isEmpty()) {
			// get the next vertex in the sorted list
			MonotoneVertex v = vertices[i];
			
			// get the bottom and top elements of the stack
			MonotoneVertex vBot = stack.firstElement();
			MonotoneVertex vTop = stack.lastElement();
			
			// is the current vertex adjacent to the bottom element
			// but not to the top element?
			if (v.isAdjacent(vBot) && !v.isAdjacent(vTop)) {
				// create the triangles and pop all the points
				int size = stack.size();
				MonotoneVertex vt = stack.pop();
				for (int j = 0; j < size - 1; j++) {
					MonotoneVertex vt1 = stack.pop();
					Triangle triangle = this.createTriangle(v.point, vt.point, vt1.point);
					triangles.add(triangle);
					vt = vt1;
				}
				
				// push the remaining edge
				stack.push(vTop);
				stack.push(v);
			} else if (v.isAdjacent(vTop) && !v.isAdjacent(vBot)) {
				double angle = 0;
				
				while (stack.size() > 1) {
					MonotoneVertex vt = stack.lastElement();
					MonotoneVertex vt1 = stack.elementAt(stack.size() - 2);
					
					Vector2 p1 = v.point;
					Vector2 p2 = vt.point;
					Vector2 p3 = vt1.point;
					
					// what chain is the current vertex on
					if (v.chain == MonotoneChain.Type.LEFT || v.chain == MonotoneChain.Type.BOTTOM) {
						angle = p2.to(p3).getAngleBetween(p2.to(p1));
					} else {
						angle = p1.to(p2).getAngleBetween(p3.to(p2));
					}
					
					// make sure the angle is less than pi before we create
					// a triangle from the points
					if (Math.abs(angle) < Math.PI) {
						Triangle triangle = this.createTriangle(p1, p2, p3);
						triangles.add(triangle);
						stack.pop();
					} else {
						// once we find an angle that is greater than pi then
						// we can quit and move to the next vertex in the sorted list
						break;
					}
				}
				stack.push(v);
			} else if (v.isAdjacent(vTop) && v.isAdjacent(vBot)) {
				// create the triangles and pop all the points
				int size = stack.size();
				MonotoneVertex vt = stack.pop();
				for (int j = 0; j < size - 1; j++) {
					MonotoneVertex vt1 = stack.pop();
					Triangle triangle = this.createTriangle(v.point, vt.point, vt1.point);
					triangles.add(triangle);
					vt = vt1;
				}
				// we are done
				break;
			}
			i++;
		}
		
		return triangles;
	}
	
	/**
	 * Creates a triangle from the given points with the correct winding.
	 * <p>
	 * The winding requirement is a requirement of engine, not of the algorithm.
	 * @param p1 the first point
	 * @param p2 the second point
	 * @param p3 the third point
	 * @return {@link Triangle} the triangle formed from the given points
	 */
	protected Triangle createTriangle(Vector2 p1, Vector2 p2, Vector2 p3) {
		// determine the passed in winding
		Vector2 v1 = p1.to(p2);
		Vector2 v2 = p2.to(p3);
		double cross = v1.cross(v2);
		try {
		// check the winding
		if (cross > 0.0) {
			// the winding is already counter-clockwise
			return new Triangle(p1, p2, p3);
		} else {
			// reverse the winding so that we create a counter-clockwise triangle
			return new Triangle(p1, p3, p2);
		}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns true if this monotone polygon is valid.
	 * <p>
	 * A monotone polygon is a polygon that does not have any merge or split
	 * vertices.  A merge vertex is a vertex that is below both its neighbors
	 * and has an interior angle greater than &pi;.  A split vertex is a vertex
	 * that is above both its neighbors and has an interior angle greater than
	 * &pi;
	 * <p>
	 * This method also verifies that the array of vertices is sorted.
	 * @return boolean
	 */
	public boolean isValid() {
		// get the number of vertices
		int size = this.vertices.length;
		// loop over all the vertices testing for merge or split vertices
		for (int i = 0; i < size; i++) {
			// get the current vertex
			MonotoneVertex v = this.vertices[i];
			// get its neighbors
			MonotoneVertex next = v.next;
			MonotoneVertex prev = v.prev;
			
			// get the points
			Vector2 p = v.point;
			Vector2 p0 = prev.point;
			Vector2 p1 = next.point;
			
			// get the interior angle
			Vector2 v1 = p0.to(p);
			Vector2 v2 = p.to(p1);
			double cross = v1.cross(v2);
			
			if (this.type == MonotonePolygon.Type.Y) {
				// above or below of both + angle > pi
				if (this.isBelow(p, p0) && this.isBelow(p, p1)) {
					// p is below both its neighbors
					if (cross < 0.0) {
						// the internal angle is greater than pi
						// we can immediately return false
						return false;
					}
				} else if (!this.isBelow(p, p0) && !this.isBelow(p, p1)) {
					// p is above both its neighbors
					if (cross < 0.0) {
						// the internal angle is greater than pi
						// we can immediately return false
						return false;
					}
				}
			} else {
				// left or right of both + angle > pi
				if (this.isLeft(p, p0) && this.isLeft(p, p1)) {
					// p is left of both its neighbors
					if (cross < 0.0) {
						// the internal angle is greater than pi
						// we can immediately return false
						return false;
					}
				} else if (!this.isLeft(p, p0) && !this.isLeft(p, p1)) {
					// p is right of both its neighbors
					if (cross < 0.0) {
						// the internal angle is greater than pi
						// we can immediately return false
						return false;
					}
				}
			}
			
			// also we need to make sure that we are sorted
			if (i > 0) {
				// check the monotone type
				if (this.type == MonotonePolygon.Type.Y) {
					// the previous point should be above the current point
					if (this.isBelow(p0, p)) {
						return false;
					}
				} else {
					// the previous point should be left of the current point
					if (!this.isLeft(p0, p)) {
						return false;
					}
				}
			}
		}
		
		// if we make it here then the polygon is monotone
		return true;
	}
	
	/**
	 * Returns true if the given point p is below the given point q.
	 * <p>
	 * If the point p and q form a horizontal line then p is considered
	 * below if its x coordinate is greater than q's x coordinate.
	 * @param p a point
	 * @param q another point
	 * @return boolean true if p is below q; false if p is above q
	 */
	protected boolean isBelow(Vector2 p, Vector2 q) {
		double diff = p.y - q.y;
		if (Math.abs(diff) < Epsilon.E) {
			if (p.x > q.x) {
				return true;
			} else {
				return false;
			}
		} else {
			if (diff < 0.0) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * Returns true if the given point p is left of the given point q.
	 * <p>
	 * If the point p and q form a vertical line then p is considered
	 * left if its y coordinate is greater than q's y coordinate.
	 * @param p a point
	 * @param q another point
	 * @return boolean true if p is left of q; false if p is right of q
	 */
	protected boolean isLeft(Vector2 p, Vector2 q) {
		double diff = p.x - q.x;
		if (Math.abs(diff) < Epsilon.E) {
			if (p.y > q.y) {
				return true;
			} else {
				return false;
			}
		} else {
			if (diff < 0.0) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * Returns the maximum vertex in the sorted array.
	 * @return {@link MonotoneVertex}
	 */
	public MonotoneVertex getMaximum() {
		return this.vertices[0];
	}
	
	/**
	 * Returns the minimum vertex in the sorted array.
	 * @return {@link MonotoneVertex}
	 */
	public MonotoneVertex getMinimum() {
		return this.vertices[this.vertices.length - 1];
	}
	
	/**
	 * Returns the type of monotone polygon.
	 * @return {@link MonotonePolygon.Type}
	 */
	public MonotonePolygon.Type getType() {
		return this.type;
	}
	
	/**
	 * Returns the sorted array of vertices.
	 * @return {@link MonotoneVertex}[]
	 */
	public MonotoneVertex[] getVertices() {
		return this.vertices;
	}
	
	/**
	 * Sets the sorted array of vertices.
	 * @param vertices the sorted array of vertices; descending order
	 */
	public void setVertices(MonotoneVertex[] vertices) {
		this.vertices = vertices;
	}
}
