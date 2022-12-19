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
 * removing points that are less than epsilon distance away from a guide line.
 * <p>
 * NOTE: This algorithm is designed for polylines, but has been adapted for simple
 * polygons without holes by first sub-dividing the polygon into two polylines.
 * This first sub-division process will always be from the first vertex in the polygon
 * to the vertex farthest from the first vertex.
 * <p>
 * The guide line is defined by the line from the start to the end of the polyline
 * being processed.  If all points between the start and end point of the polyline are
 * epsilon or more distant from the guide line, the algorithm splits the polyline
 * and processes each part.  This continues recursively until the algorithm is complete.
 * <p>
 * This algorithm has O(n log n) complexity where n is the number of vertices in the source
 * polygon.  This algorithm prevents self-intersections arising from the simplification
 * process by skipping the simplification.
 * <p>
 * This method does not require the polygon to have any defined winding, but does assume
 * that it does not have holes and is not self-intersecting.
 * <p>
 * This method handles null/empty lists, null elements, and all null elements.  In these
 * cases it's possible the returned list will be empty or have less than 3 vertices.
 * <p>
 * NOTE: This algorithm's result is highly dependent on the given cluster tolerance, epsilon
 * and the input polygon.  There's no guarantee that the result will have 3 or more vertices.
 * @author William Bittle
 * @version 5.0.0
 * @since 4.2.0
 * @see <a href="https://bost.ocks.org/mike/simplify/">Vertex Cluster Reduction</a>
 */
public final class DouglasPeucker extends VertexClusterReduction implements Simplifier {
	/** The minimum distance epsilon */
	private final double epsilon;

	/**
	 * Minimal constructor.
	 * @param clusterTolerance the cluster tolerance; must be zero or greater
	 * @param epsilon the minimum distance epsilon; must be zero or greater
	 * @throws IllegalArgumentException if clusterTolerance is less than zero or epsilon is less than zero
	 */
	public DouglasPeucker(double clusterTolerance, double epsilon) {
		super(clusterTolerance);
		
		if (epsilon < 0) 
			throw new ValueOutOfRangeException("epsilon", epsilon, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0);
		
		this.epsilon = epsilon;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.simplify.VertexClusterReduction#simplify(java.util.List)
	 */
	public List<Vector2> simplify(List<Vector2> vertices) {
		if (vertices == null) {
			return vertices;
		}
		
		// first simplify via vertex clustering
		vertices = super.simplify(vertices);
		
		if (vertices.size() < 4) {
			return vertices;
		}
		
		// build a linked list of vertices
		List<SimplePolygonVertex> verts = this.buildVertexList(vertices);
		SegmentTree tree = this.buildSegmentTree(verts.get(0));
		
		// 1. find two points to split the poly into two halves
		// for example:
		// 0-------------0        A-------------0  |                0
		// |              \       |                |                 \
		// |               \  =>  |                &                  \
		// |                \     |                |                   \
		// 0-----------------0    0                |  0-----------------B
		int startIndex = 0;
		int endIndex = this.getFartherestVertexFromVertex(startIndex, vertices);
		
		// 2. split into two polylines to simplify
		List<Vector2> aReduced = this.douglasPeucker(verts.subList(startIndex, endIndex + 1), tree);
		List<Vector2> bReduced = this.douglasPeucker(verts.subList(endIndex, vertices.size()), tree);
		
		// 3. merge the two polylines back together
		List<Vector2> result = new ArrayList<Vector2>();
		result.addAll(aReduced.subList(0, aReduced.size() - 1));
		result.addAll(bReduced);
		
		return result;
	}
	
	/**
	 * Recursively sub-divide the given polyline performing the douglas Peucker algorithm.
	 * <p>
	 * O(mn) in worst case, O(n log m) in best case, where n is the number of vertices in the
	 * original polyline and m is the number of vertices in the reduced polyline.
	 * @param polyline
	 * @return List&lt;{@link Vector2}&gt;
	 */
	private final List<Vector2> douglasPeucker(List<SimplePolygonVertex> polyline, SegmentTree tree) {
		int size = polyline.size();
		List<Vector2> result = new ArrayList<Vector2>();
		
		// can't do anything with 1 or 2 points - we just have to keep them
		if (size < 3) {
			for (int i = 0; i < size; i++) {
				result.add(polyline.get(i).point);
			}
			return result;
		}
		
		// get the start/end vertices of the polyline
		SimplePolygonVertex sv = polyline.get(0);
		SimplePolygonVertex ev = polyline.get(size - 1);
		
		// get the farthest vertex from the line created from the start to the end
		// vertex on the polyline
		FarthestVertex fv = this.getFarthestVertexFromLine(sv, ev, polyline);
		
		// check the farthest point's distance - if it's higher than the minimum
		// distance epsilon, then we need to subdivide the polyline since we can't
		// reduce here (we might be able to reduce elsewhere)
		if (fv.distance >= epsilon) {
			// sub-divide and run the algo on each half
			List<Vector2> aReduced = this.douglasPeucker(polyline.subList(0, fv.index + 1), tree);
			List<Vector2> bReduced = this.douglasPeucker(polyline.subList(fv.index, size), tree);
			
			// recombine the reduced polylines
			result.addAll(aReduced.subList(0, aReduced.size() - 1));
			result.addAll(bReduced);
		} else {
			// check for self-intersection
			if (this.isSelfIntersectionProduced(sv, ev, tree)) {
				// if removing all the points between v1 and v2 produces self-intersection
				// then we can either stop and all points between v1 and v2 to the result
				// or we can split the polyline by the farthest point and try to simplify
				// those sub-polylines
				
				// sub-divide and run the algo on each half
				List<Vector2> aReduced = this.douglasPeucker(polyline.subList(0, fv.index + 1), tree);
				List<Vector2> bReduced = this.douglasPeucker(polyline.subList(fv.index, size), tree);
				
				// recombine the reduced polylines
				result.addAll(aReduced.subList(0, aReduced.size() - 1));
				result.addAll(bReduced);
				
				return result;
			}
			
			// if there's no self-intersection, then we need to remove
			// all segments from the segment tree in between these vertices
			SimplePolygonVertex b = sv;
			while (b != ev) {
				tree.remove(b.nextSegment);
				b = b.next;
			}
			
			// remove all the vertices between sv/ev
			sv.next = ev;
			ev.prev = sv;
			
			// create a new segment between sv/ev
			sv.nextSegment = new SegmentTreeLeaf(sv.point, ev.point, sv.index, ev.index);
			ev.prevSegment = sv.nextSegment;
			
			// add the new segment to the segment tree
			tree.add(sv.nextSegment);
			
			// just use the start/end vertices
			// as the result
			result.add(sv.point);
			result.add(ev.point);
		}
		
		return result;
	}
	
	/**
	 * Returns the vertex farthest from the given vertex.
	 * <p>
	 * O(n)
	 * @param index the vertex index
	 * @param polygon the entire polygon
	 * @return int
	 */
	private final int getFartherestVertexFromVertex(int index, List<Vector2> polygon) {
		double dist2 = 0.0;
		int max = -1;
		int size = polygon.size();
		Vector2 vertex = polygon.get(index);
		for (int i = 0; i < size; i++) {
			Vector2 vert = polygon.get(i);
			double test = vertex.distanceSquared(vert);
			if (test > dist2) {
				dist2 = test;
				max = i;
			}
		}
		
		return max;
	}
	
	/**
	 * Returns the farthest vertex in the polyline from the line created by lineVertex1 and lineVertex2.
	 * <p>
	 * O(n)
	 * @param lineVertex1 the first vertex of the line
	 * @param lineVertex2 the second vertex of the line
	 * @param polyline the entire polyline
	 * @return {@link FarthestVertex}
	 */
	private final FarthestVertex getFarthestVertexFromLine(SimplePolygonVertex lineVertex1, SimplePolygonVertex lineVertex2, List<SimplePolygonVertex> polyline) {
		int index = -1;
		double distance = 0.0;
		
		Vector2 lp1 = lineVertex1.point;
		Vector2 lp2 = lineVertex2.point;
		
		// find the vertex on the polyline that's farthest from the line created
		// by lineVertex1 and lineVertex2
		int size = polyline.size();
		Vector2 line = lp1.to(lp2);
		Vector2 lineNormal = line.getLeftHandOrthogonalVector();
		lineNormal.normalize();
		for (int i = 0; i < size; i++) {
			Vector2 vert = polyline.get(i).point;
			double test = Math.abs(lp1.to(vert).dot(lineNormal));
			if (test > distance) {
				distance = test;
				index = i;
			}
		}
		
		// make sure we found a winner
		if (index < 0) {
			// then they were all colinear, so take the middle one
			// NOTE: integer division here
			index = size / 2;
			distance = 0.0;
		}
		
		return new FarthestVertex(index, distance);
	}
	
	/**
	 * Represents the farthest vertex from a line.
	 * @author William Bittle
	 * @version 4.2.0
	 * @since 4.2.0
	 */
	private final class FarthestVertex {
		/** The index */
		final int index;
		
		/** The distance */
		final double distance;
		
		/**
		 * Minimal constructor.
		 * @param index the index in the polyline
		 * @param distance the distance from the line
		 */
		public FarthestVertex(int index, double distance) {
			this.index = index;
			this.distance = distance;
		}
	}
}
