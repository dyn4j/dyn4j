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
package org.dyn4j.geometry.decompose;

import java.util.List;

import org.dyn4j.Epsilon;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Implementation of the Ear Clipping convex decomposition algorithm for simple polygons.
 * <p>
 * This algorithm operates only on simple polygons.  A simple polygon is a polygon that
 * has vertices that are connected by edges where:
 * <ul>
 * <li>Edges can only intersect at vertices</li>
 * <li>Vertices have at most two edge connections</li>
 * </ul>
 * <p>
 * This implementation does not handle polygons with holes, but accepts both counter-clockwise
 * and clockwise polygons.
 * <p>
 * The polygon to decompose must be 4 or more vertices.
 * <p>
 * This algorithm creates a valid triangulation (N - 2) triangles, then employs the Hertel-Mehlhorn
 * algorithm to reduce the number of convex pieces.
 * <p>
 * This algorithm is O(n<sup>2</sup>).
 * @author William Bittle
 * @version 3.2.0
 * @since 2.2.0
 */
public class EarClipping implements Decomposer, Triangulator {
	/** Epsilon for checking for near containment of vertices within triangles */
	private static final double CONTAINS_EPSILON = Math.sqrt(Epsilon.E);
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.decompose.Decomposer#decompose(org.dyn4j.geometry.Vector2[])
	 */
	@Override
	public List<Convex> decompose(Vector2... points) {
		// triangulate
		DoubleEdgeList dcel = this.createTriangulation(points);
				
		// perform the Hertel-Mehlhorn algorithm to reduce the number
		// of convex pieces
		dcel.hertelMehlhorn();
		
		// return the convex pieces
		return dcel.getConvexDecomposition();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.decompose.Triangulator#triangulate(org.dyn4j.geometry.Vector2[])
	 */
	@Override
	public List<Triangle> triangulate(Vector2... points) {
		// triangulate
		DoubleEdgeList dcel = this.createTriangulation(points);
		
		// return the triangulation
		return dcel.getTriangulation();
	}
	
	/**
	 * Creates a triangulation of the given simple polygon and places it into the returned
	 * doubly-connected edge list (DCEL).
	 * @param points the simple polygon vertices
	 * @return {@link DoubleEdgeList}
	 * @since 3.1.9
	 */
	final DoubleEdgeList createTriangulation(Vector2... points) {
		// check for null array
		if (points == null) throw new NullPointerException(Messages.getString("geometry.decompose.nullArray"));
		// get the number of points
		int size = points.length;
		// check the size
		if (size < 4) throw new IllegalArgumentException(Messages.getString("geometry.decompose.invalidSize"));
		
		// get the winding order
		double winding = Geometry.getWinding(points);
		
		// reverse the array if the points are in clockwise order
		if (winding < 0.0) {
			Geometry.reverseWinding(points);
		}
		
		// create a DCEL to store the decomposition
		DoubleEdgeList dcel = new DoubleEdgeList(points);
		
		// create a doubly link list for the vertices
		EarClippingVertex root = null;
		EarClippingVertex curr = null;
		EarClippingVertex prev = null;
		for (int i = 0; i < size; i++) {
			// get the current point
			Vector2 p = points[i];
			// create the vertex
			curr = new EarClippingVertex(p);
			// get the vertices around the current point
			Vector2 p0 = points[i == 0 ? size - 1 : i - 1];
			Vector2 p1 = points[i + 1 == size ? 0 : i + 1];
			// create the vectors representing the V
			Vector2 v1 = p.to(p0);
			Vector2 v2 = p.to(p1);
			// check for coincident vertices
			if (v2.isZero()) {
				throw new IllegalArgumentException(Messages.getString("geometry.decompose.coincident"));
			}
			// check the angle between the two vectors
			if (v1.cross(v2) >= 0.0) {
				// this means this vertex is a reflex vertex
				curr.reflex = true;
			} else {
				// otherwise its a convex vertex
				curr.reflex = false;
			}
			// set the previous
			curr.prev = prev;
			// set the previous node's next to the current node
			if (prev != null) {
				prev.next = curr;
			}
			// set the current point's reference vertex
			curr.index = i;
			// set the new previous to the current
			prev = curr;
			
			if (root == null) {
				root = curr;
			}
		}
		// finally wire up the first and last nodes
		root.prev = prev;
		prev.next = root;
		
		// set the ear flag
		EarClippingVertex node = root;
		for (int i = 0; i < size; i++) {
			// set the ear flag
			node.ear = this.isEar(node, size);
			// go to the next vertex
			node = node.next;
		}
		
		// decompose the linked list into the triangles
		node = root;
		int n = size;
		// stop when we only have 3 vertices left
		for (;n > 3;) {
			// is the node an ear node?
			if (node.ear) {
				// create a diagonal for this ear
				dcel.addHalfEdges(node.next.index, node.prev.index);
				// get the previous and next nodes
				EarClippingVertex pNode = node.prev;
				EarClippingVertex nNode = node.next;
				// remove this node from the list
				pNode.next = node.next;
				nNode.prev = node.prev;
				// re-evaluate the adjacent vertices reflexive-ness only if its reflex
				// (convex vertices will remain convex)
				if (pNode.reflex) {
					// determine if it is still reflex
					pNode.reflex = this.isReflex(pNode);
				}
				if (nNode.reflex) {
					// determine if it is still reflex
					nNode.reflex = this.isReflex(nNode);
				}
				// re-evaluate the ear-ness of the adjacent vertices
				if (!pNode.reflex) {
					pNode.ear = this.isEar(pNode, n);
				}
				// re-evaluate the ear-ness of the adjacent vertices
				if (!nNode.reflex) {
					nNode.ear = this.isEar(nNode, n);
				}
				n--;
			}
			node = node.next;
		}
		
		return dcel;
	}
	
	/**
	 * Returns true if the given vertex is a reflex vertex.
	 * <p>
	 * A reflex vertex is a vertex who's adjacent vertices create an
	 * an angle greater than 180 degrees (or the cross product is 
	 * positive) for CCW vertex winding.
	 * @param vertex the vertex to test
	 * @return boolean true if the given vertex is considered a reflex vertex
	 */
	final boolean isReflex(EarClippingVertex vertex) {
		// get the triangle points
		Vector2 p = vertex.point;
		Vector2 p0 = vertex.prev.point;
		Vector2 p1 = vertex.next.point;
		// create vectors from the current point
		Vector2 v1 = p.to(p0);
		Vector2 v2 = p.to(p1);
		// check for reflex
		if (v1.cross(v2) < 0.0) {
			// its not reflex any more
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns true if the given vertex is considered an ear vertex.
	 * <p>
	 * A vertex is an ear vertex if the triangle created by the adjacent vertices
	 * of the given vertex does not contain any other vertices within it.
	 * <p>
	 * A reflex vertex cannot be an ear.
	 * @param vertex the vertex to test for ear-ness
	 * @param n the number of vertices
	 * @return boolean true if the given vertex is considered an ear vertex
	 */
	final boolean isEar(EarClippingVertex vertex, int n) {
		// reflex vertices cannot be ears
		if (vertex.reflex) return false;
		
		boolean ear = true;
		// get the triangle created by this point and its adjacent vertices
		Vector2 a = vertex.point;
		Vector2 b = vertex.next.point;
		Vector2 c = vertex.prev.point;
		
		// check if any other points in the linked list are contained within
		// this triangle
		
		// don't check any points on the triangle for containment
		EarClippingVertex tNode = vertex.next.next;
		for (int j = 0; j < n - 3; j++) {
			// we only need to test reflex nodes
			if (tNode.reflex) {
				// then check for containment
				if (this.contains(a, b, c, tNode.point)) {
					// if there exists a vertex that is contained in the triangle
					// then we can immediately exit the loop
					ear = false;
					break;
				}
			}
			// test the next vertex
			tNode = tNode.next;
		}
		
		return ear;
	}
	
	/**
	 * Returns true if the given point, p, is contained in the triangle created
	 * by a, b, and c.
	 * @param a the first point of the triangle
	 * @param b the second point of the triangle
	 * @param c the third point of the triangle
	 * @param p the point to test for containment
	 * @return boolean true if the given point is contained in the given triangle
	 */
	protected boolean contains(Vector2 a, Vector2 b, Vector2 c, Vector2 p) {
		// create a vector representing edge ab
		Vector2 ab = a.to(b);
		// create a vector representing edge ac
		Vector2 ac = a.to(c);
		// create a vector from a to the point
		Vector2 pa = a.to(p);
		
		double dot00 = ac.dot(ac);
		double dot01 = ac.dot(ab);
		double dot02 = ac.dot(pa);
		double dot11 = ab.dot(ab);
		double dot12 = ab.dot(pa);

		double denominator = dot00 * dot11 - dot01 * dot01;
		double u = (dot11 * dot02 - dot01 * dot12) / denominator;
		double v = (dot00 * dot12 - dot01 * dot02) / denominator;
		
		return u > 0 && v > 0 && (u + v <= 1 + EarClipping.CONTAINS_EPSILON);
	}
}
