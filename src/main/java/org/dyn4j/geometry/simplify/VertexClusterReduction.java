/*
 * Copyright (c) 2010-2022 William Bittle  http://www.dyn4j.org/
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
import java.util.List;

import org.dyn4j.exception.ValueOutOfRangeException;
import org.dyn4j.geometry.Vector2;

/**
 * Simple polygon (without holes) simplifier that reduces the number of vertices by 
 * inspecting the distance between adjacent vertices.  If the distance between two 
 * adjacent vertices is less than the given tolerance, it's removed.
 * <p>
 * This algorithm is typically used to pre-process a simple polygon before another
 * simplification algorithm is used. Both the {@link Visvalingam} and {@link DouglasPeucker}
 * implementations run this step before running their algorithm.
 * <p>
 * This algorithm has O(n log n) complexity where n is the number of vertices in the source
 * polygon (due to self-intersection prevention).  This algorithm prevents self-intersections 
 * arising from the simplification process by skipping the simplification.
 * <p>
 * This method does not require the polygon to have any defined winding, but does assume
 * that it does not have holes and is not self-intersecting.
 * <p>
 * This method handles null/empty lists, null elements, and all null elements.  In these
 * cases it's possible the returned list will be empty or have less than 3 vertices.
 * <p>
 * NOTE: This algorithm's result is highly dependent on the given cluster tolerance and the
 * input polygon.  There's no guarantee that the result will have 3 or more vertices.
 * @author William Bittle
 * @version 5.0.0
 * @since 4.2.0
 * @see <a href="http://geomalgorithms.com/a16-_decimate-1.html">Vertex Cluster Reduction</a>
 */
public class VertexClusterReduction extends AbstractSimplifier implements Simplifier {
	/** The tolerated distance between adjacent vertices */
	private final double clusterTolerance;
	
	/**
	 * Minimal constructor.
	 * @param clusterTolerance the minimal distance allowed between adjacent vertices; must be zero or greater
	 * @throws IllegalArgumentException if clusterTolerance is less than zero
	 */
	public VertexClusterReduction(double clusterTolerance) {
		if (clusterTolerance < 0) 
			throw new ValueOutOfRangeException("clusterTolerance", clusterTolerance, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0);
		
		this.clusterTolerance = clusterTolerance;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.simplify.Simplifier#simplify(java.util.List)
	 */
	public List<Vector2> simplify(List<Vector2> vertices) {
		// check for null vertices list
		if (vertices == null) return vertices;
		
		int size = vertices.size();
		
		// check for 0 vertices
		if (size == 0) return vertices;

		List<Vector2> reduced = new ArrayList<Vector2>();
		List<SimplePolygonVertex> all = this.buildVertexList(vertices);
		
		// check the size again
		size = all.size();
		if (size == 0) return reduced;
		if (size == 1) {
			reduced.add(all.get(0).point);
			return reduced;
		}
		
		// get the first
		SimplePolygonVertex start = all.get(0);
		reduced.add(start.point);
		
		// build the anti-self-intersection tree
		SegmentTree tree = this.buildSegmentTree(start);
		
		// start at the next index past the start index and do the simplification
		final double toleranceSquared = clusterTolerance * clusterTolerance;
		for (int i = 1; i <= size; i++) {
			SimplePolygonVertex v = all.get(i == size ? 0 : i);
			
			// ignore any null elements
			if (v == null) continue;
			
			// get the squared distance between the vertices
			double dist2 = start.point.distanceSquared(v.point);
			if (dist2 < toleranceSquared ||
				// special case for zero tolerance
				(dist2 == toleranceSquared && toleranceSquared == 0.0)) {
				// if it's close enough, then ignore it
				if (!this.isSelfIntersectionProduced(v, tree)) {
					// no self-intersection, so remove the vertex from the tree
					this.removeVertex(v, tree);
					continue;
				}
			}
			
			// otherwise add it to the result
			// and start comparing against the
			// new one
			if (i < size) {
				reduced.add(v.point);
			}
			start = v;
		}
		
		return reduced;
	}
	
	/**
	 * Builds a list of vertices without nulls. 
	 * @param polygon the polygon vertices
	 * @return List&lt;{@link SimplePolygonVertex}&gt;
	 */
	protected final List<SimplePolygonVertex> buildVertexList(List<Vector2> polygon) {
		int size = polygon.size();
		List<SimplePolygonVertex> list = new ArrayList<SimplePolygonVertex>();
		
		int n = 0;
		SimplePolygonVertex first = null;
		SimplePolygonVertex prev = null;
		for (int i = 0; i < size; i++) {
			Vector2 v1 = polygon.get(i);
			if (v1 == null) {
				continue;
			}
			
			Vector2 v2 = null;
			for (int j = i + 1; j <= size; j++) {
				v2 = polygon.get(j == size ? 0 : j);
				if (v2 != null) {
					break;
				}
			}
			
			SimplePolygonVertex vertex = new SimplePolygonVertex(n, v1);
			vertex.next = null;
			vertex.prev = prev;
			
			if (v2 != null) {
				vertex.nextSegment = new SegmentTreeLeaf(v1, v2, n, n+1);
			}
			
			if (first == null) {
				first = vertex;
			}
			
			if (prev != null) {
				vertex.prevSegment = prev.nextSegment;
				prev.next = vertex;
			}
			
			prev = vertex;
			
			n++;
			list.add(vertex);
		}
		
		if (first != null) {
			first.prev = prev;
			first.prevSegment = prev.nextSegment = new SegmentTreeLeaf(prev.point, first.point, prev.index, first.index);
		}
		
		if (prev != null) {
			prev.next = first;
		}
		
		return list;
	}
	
	/**
	 * Removes the given vertex from the queue and segment tree.
	 * @param v the vertex to remove
	 * @param list the list to remove the vertex from
	 * @param tree the segment tree to remove the vertex from
	 */
	private final void removeVertex(SimplePolygonVertex v, SegmentTree tree) {
		Vector2 v1 = null;
		Vector2 v2 = null;
		
		SimplePolygonVertex tprev = v.prev;
		SimplePolygonVertex tnext = v.next;
		
		SegmentTreeLeaf sprev = v.prevSegment;
		SegmentTreeLeaf snext = v.nextSegment;
		
		tprev.next = tnext;
		tnext.prev = tprev;
		
		// build a new segment with the given vertex removed
		v1 = tprev.point;
		v2 = tnext.point;
		
		// update the segment tree to account for the removed segments/vertex
		tprev.nextSegment = new SegmentTreeLeaf(v1, v2, tprev.index, tnext.index);
		tnext.prevSegment = tprev.nextSegment;
		// remove the two segments attached to the removed vertex
		tree.remove(sprev);
		tree.remove(snext);
		// add the new segment to the segment tree
		tree.add(tprev.nextSegment);
	}
}
