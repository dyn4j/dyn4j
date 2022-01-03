/*
 * Copyright (c) 2010-2021 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.geometry.simplify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.dyn4j.Epsilon;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.Vector2;

/**
 * Abstract simplifier providing some shared logic for all simplifiers.
 * @author William Bittle
 * @version 4.2.0
 * @since 4.2.0
 */
public abstract class AbstractSimplifier implements Simplifier {
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.simplify.Simplifier#simplify(org.dyn4j.geometry.Vector2[])
	 */
	@Override
	public Vector2[] simplify(Vector2... vertices) {
		if (vertices == null) return vertices;
		
		List<Vector2> verts = Arrays.asList(vertices);
		verts = this.simplify(verts);
		return verts.toArray(new Vector2[verts.size()]);
	}

	/**
	 * Builds a {@link SegmentTree} used to prevent self-intersection during simplification.
	 * @param start the start vertex
	 * @return {@link SegmentTree}
	 */
	protected final SegmentTree buildSegmentTree(SimplePolygonVertex start) {
		SegmentTree tree = new SegmentTree();
		SimplePolygonVertex s = start;
		SimplePolygonVertex v = s.next;
		while (v != s) {
			tree.add(v.prevSegment);
			v = v.next;
		}
		tree.add(v.prevSegment);
		return tree;
	}

	/**
	 * Builds a polygon from the remaining vertices in the queue.
	 * @param start the vertex to begin building the result from
	 * @return List&lt;{@link Vector2}&gt;
	 */
	protected final List<Vector2> buildResult(SimplePolygonVertex start) { 
		List<Vector2> result = new ArrayList<Vector2>();
		// use the linked list of vertices
		// to build the simplified polygon
		result.add(start.point);
		SimplePolygonVertex n = start.next;
		while (n != start) {
			result.add(n.point);
			n = n.next;
		}
		return result;
	}
	
	/**
	 * Returns true if removing the given vertex will generate a line segment
	 * that causes self intersection.
	 * @param vertex the vertex that will be removed
	 * @param tree the segment tree for accelerated detection
	 * @return boolean
	 */
	protected final boolean isSelfIntersectionProduced(SimplePolygonVertex vertex, SegmentTree tree) {
		Vector2 v1 = vertex.prev.point;
		Vector2 v2 = vertex.next.point;
		
		// get all segments that this segment may itersect with
		AABB aabb = AABB.createFromPoints(v1, v2);
		Iterator<SegmentTreeLeaf> bp = tree.getAABBDetectIterator(aabb);
		while (bp.hasNext()) {
			SegmentTreeLeaf leaf = bp.next();
			
			// ignore segments adjacent to the vertex itself
			if (vertex.index == leaf.index1 || vertex.index == leaf.index2) {
				continue;
			}
			
			// ignore segments adjacent to the previous vertex
			if (vertex.prev.index == leaf.index1 || vertex.prev.index == leaf.index2) {
				continue;
			}
			
			// ignore segments adjacent to the next vertex
			if (vertex.next.index == leaf.index1 || vertex.next.index == leaf.index2) {
				continue;
			}
			
			// we need to verify the segments truly overlap
			if (intersects(v1, v2, leaf.point1, leaf.point2)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if the given segment will generate a self-intersection assuming all vertices
	 * between the two given vertices are removed.
	 * @param vertex1 the first vertex of the segment to be created
	 * @param vertex2 the second vertex of the segment to be created
	 * @param tree the segment tree for accelerated detection
	 * @return boolean
	 */
	protected final boolean isSelfIntersectionProduced(SimplePolygonVertex vertex1, SimplePolygonVertex vertex2, SegmentTree tree) {
		Vector2 v1 = vertex1.point;
		Vector2 v2 = vertex2.point;

		int min = vertex1.index < vertex2.index ? vertex1.index : vertex2.index;
		int max = vertex1.index > vertex2.index ? vertex1.index : vertex2.index;
		
		AABB aabb = AABB.createFromPoints(v1, v2);
		Iterator<SegmentTreeLeaf> bp = tree.getAABBDetectIterator(aabb);
		while (bp.hasNext()) {
			SegmentTreeLeaf leaf = bp.next();

			// ignore any segments in between the segment being created
			if (leaf.index1 >= min && leaf.index2 <= max ||
				leaf.index1 <= max && leaf.index2 >= min) {
				continue;
			}
			
			// we need to verify the segments truly overlap
			if (intersects(v1, v2, leaf.point1, leaf.point2)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if the given segments intersect each other.
	 * @param a1 the first point of the first segment
	 * @param a2 the second point of the first segment
	 * @param b1 the first point of the second segment
	 * @param b2 the second point of the second segment
	 * @return boolean
	 */
	protected final boolean intersects(Vector2 a1, Vector2 a2, Vector2 b1, Vector2 b2) {
		Vector2 A = a1.to(a2);
		Vector2 B = b1.to(b2);

		// compute the bottom
		double BxA = B.cross(A);
		// compute the top
		double ambxA = a1.difference(b1).cross(A);
		
		// if the bottom is zero, then the segments are either parallel or coincident
		if (Math.abs(BxA) <= Epsilon.E) {
			// if the top is zero, then the segments are coincident
			if (Math.abs(ambxA) <= Epsilon.E) {
				// project the segment points onto the segment vector (which
				// is the same for A and B since they are coincident)
				A.normalize();
				double ad1 = a1.dot(A);
				double ad2 = a2.dot(A);
				double bd1 = b1.dot(A);
				double bd2 = b2.dot(A);
				
				// then compare their location on the number line for intersection
				Interval ia = new Interval(ad1, ad2);
				Interval ib = new Interval(bd1 < bd2 ? bd1 : bd2, bd1 > bd2 ? bd1 : bd2);
				
				if (ia.overlaps(ib)) {
					return true;
				}
			}
			
			// otherwise they are parallel
			return false;
		}
		
		// if just the top is zero, then there's no intersection
		if (Math.abs(ambxA) <= Epsilon.E) {
			return false;
		}
		
		// compute tb
		double tb = ambxA / BxA;
		if (tb <= 0.0 || tb >= 1.0) {
			// no intersection
			return false;
		}
		
		// compute the intersection point
		Vector2 ip = B.product(tb).add(b1);
		
		// since both are segments we need to verify that
		// ta is also valid.
		// compute ta
		double ta = ip.difference(a1).dot(A) / A.dot(A);
		if (ta <= 0.0 || ta >= 1.0) {
			// no intersection
			return false;
		}
		
		return true;
	}
}
