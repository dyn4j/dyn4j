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
import java.util.PriorityQueue;
import java.util.Queue;

import org.dyn4j.exception.ValueOutOfRangeException;
import org.dyn4j.geometry.Vector2;

/**
 * Simple polygon (without holes) simplifier that reduces the number of vertices by 
 * inspecting the area created by adjacent vertices.  If the area created by three
 * adjacent vertices is less than the given minimum, it's removed.
 * <p>
 * This algorithm is typically used to pre-process a simple polygon before another
 * simplification algorithm is used.
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
 * NOTE: This algorithm's result is highly dependent on the given cluster tolerance, minimum
 * area and the input polygon.  There's no guarantee that the result will have 3 or more 
 * vertices.
 * @author William Bittle
 * @version 5.0.0
 * @since 4.2.0
 * @see <a href="https://bost.ocks.org/mike/simplify/">Visvalingam</a>
 */
public final class Visvalingam extends VertexClusterReduction implements Simplifier {
	/** The minimum allowed triangular area */
	private final double minimumTriangleArea;
	
	/**
	 * Minimal constructor.
	 * @param clusterTolerance the minimum distance between adjacent points
	 * @param minimumTriangleArea the minimum triangular area at each vertex
	 * @throws IllegalArgumentException if clusterTolerance is less than zero or minimumTriangleArea is less than zero
	 */
	public Visvalingam(double clusterTolerance, double minimumTriangleArea) {
		super(clusterTolerance);
		
		if (minimumTriangleArea < 0) 
			throw new ValueOutOfRangeException("minimumTriangleArea", minimumTriangleArea, ValueOutOfRangeException.MUST_BE_GREATER_THAN_OR_EQUAL_TO, 0);
		
		this.minimumTriangleArea = minimumTriangleArea;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.geometry.simplify.VertexClusterReduction#simplify(java.util.List)
	 */
	public List<Vector2> simplify(List<Vector2> vertices) {
		if (vertices == null) {
			return vertices;
		}
		
		// reduce based on vertex clustering first
		List<Vector2> reduced =  super.simplify(vertices);
		
		// check the total vertex size
		if (reduced.size() < 4) {
			return reduced;
		}
		
		// now perform visvalingam
		reduced = this.visvalingam(reduced);
		
		return reduced;
	}
	
	/**
	 * Compute the triangular area of each vertex and place them in a priority queue
	 * from least area to greatest. Then, iterate the queue until a vertex is removed
	 * who's area is greater than or equal to the configured area.
	 * @param polygon the polygon to simplify
	 * @return List&lt;{@link Vector2}&gt;
	 */
	private final List<Vector2> visvalingam(List<Vector2> polygon) {
		// 1. compute the triangle area of each triplet of vertices
		// 2. put all of them into a priority queue sorted by least area
		// 3. iterate through all triangles
		//		a. Pop triangle from queue
		//		b. if triangle area > epsilon then we're done
		//		c. if triangle area <= epsilon
		//			i. remove the middle point
		//			ii. recompute the areas of the two adjacent triangles
		
		// build the priority queue of all triangles of the polygon
		Queue<AreaTrackedVertex> queue = this.buildTriangleAreaQueue(polygon);
		
		// build the segment tree
		SegmentTree tree = this.buildSegmentTree(queue.peek());
		
		// begin evaluating the triangles
		do {
			// get a vertex (triangle)
			AreaTrackedVertex v = queue.poll();
			
			// if the vertex with the minimum area is 
			// greater than or equal to the minimum area
			// we want, then we can stop, build the resulting
			// simplified polygon and exit
			if (v.area >= this.minimumTriangleArea) {
				// use the linked list of vertices
				// to build the simplified polygon
				return this.buildResult(v);
			}
			
			// next, check if removing this triangle will
			// introduce a self intersection
			if (isSelfIntersectionProduced(v, tree)) {
				// if it does, then keep the vertex and
				// continue the process
				continue;
			}
			
			// otherwise, remove this vertex from the linked
			// list of vertices
			if (this.removeVertex(v, queue, tree)) {
				break;
			}
		} while (!queue.isEmpty());
		
		// return the result
		return new ArrayList<Vector2>();
	}
	
	/**
	 * Builds a queue of vertices sorted by their triangular area. 
	 * @param polygon the polygon vertices
	 * @return Queue&lt;{@link AreaTrackedVertex}&gt;
	 */
	private final Queue<AreaTrackedVertex> buildTriangleAreaQueue(List<Vector2> polygon) {
		int size = polygon.size();
		Queue<AreaTrackedVertex> queue = new PriorityQueue<AreaTrackedVertex>();
		
		Vector2 v0 = polygon.get(size - 1);
		Vector2 v1 = polygon.get(0);
		Vector2 v2 = polygon.get(1);
		
		AreaTrackedVertex vertex = new AreaTrackedVertex(0, v1);
		vertex.area = this.getTriangleArea(v0, v1, v2);
		vertex.next = null;
		vertex.prev = null;
		vertex.prevSegment = null;
		vertex.nextSegment = new SegmentTreeLeaf(v1, v2, 0, 1);
		queue.add(vertex);
		
		// reference the segments on the vertices (so we can remove/add when we remove add vertices)
		AreaTrackedVertex first = vertex;
		AreaTrackedVertex prev = vertex;
		for (int i = 1; i < size; i++) {
			int i0 = i - 1;
			int i2 = i + 1 == size ? 0 : i + 1;
			
			v0 = polygon.get(i0);
			v1 = polygon.get(i);
			v2 = polygon.get(i2);
			
			vertex = new AreaTrackedVertex(i, v1);
			vertex.area = this.getTriangleArea(v0, v1, v2);
			vertex.next = null;
			vertex.prev = prev;
			vertex.prevSegment = prev.nextSegment;
			vertex.nextSegment = new SegmentTreeLeaf(v1, v2, i, i2);
			
			prev.next = vertex;
			prev = vertex;
			queue.add(vertex);			
		}
		
		first.prev = prev;
		prev.next = first;
		first.prevSegment = prev.nextSegment;
		
		return queue;
	}
	
	/**
	 * Removes the given vertex from the queue and segment tree.
	 * @param v the vertex to remove
	 * @param queue the queue to remove the vertex from
	 * @param tree the segment tree to remove the vertex from
	 */
	private final boolean removeVertex(AreaTrackedVertex v, Queue<AreaTrackedVertex> queue, SegmentTree tree) {
		Vector2 v0 = null;
		Vector2 v1 = null;
		Vector2 v2 = null;
		
		AreaTrackedVertex tprev = (AreaTrackedVertex)v.prev;
		AreaTrackedVertex tnext = (AreaTrackedVertex)v.next;
		SegmentTreeLeaf tprevSegment = v.prevSegment;
		SegmentTreeLeaf tnextSegment = v.nextSegment;
		
		tprev.next = tnext;
		tnext.prev = tprev;
		
		// recompute the previous segment's triangular area
		v0 = tprev.prev.point;
		v1 = tprev.point;
		v2 = tnext.point;
		tprev.area = getTriangleArea(v0, v1, v2);

		// recompute the next segment's triangular area
		v0 = tprev.point;
		v1 = tnext.point;
		v2 = tnext.next.point;
		tnext.area = getTriangleArea(v0, v1, v2);
		
		// build a new segment with the given vertex removed
		v1 = tprev.point;
		v2 = tnext.point;
		
		// update the segment tree to account for the removed segments/vertex
		tprev.nextSegment = new SegmentTreeLeaf(v1, v2, tprev.index, tnext.index);
		tnext.prevSegment = tprev.nextSegment;
		// remove the two segments attached to the removed vertex
		tree.remove(tprevSegment);
		tree.remove(tnextSegment);
		// add the new segment to the segment tree
		tree.add(tprev.nextSegment);
		
		// remove the adjacent vertices from the queue
		queue.remove(tprev);
		queue.remove(tnext);
		
		// add them back to the queue so they are sorted in the correct place
		queue.add(tprev);
		queue.add(tnext);
		
		return tprev == tnext;
	}
	
	/**
	 * Returns the triangle area for the given vertices.
	 * @param v0 the first vertex
	 * @param v1 the second vertex
	 * @param v2 the third vertex
	 * @return double
	 */
	private final double getTriangleArea(Vector2 v0, Vector2 v1, Vector2 v2) {
		double area = v0.x * (v1.y - v2.y) + v1.x * (v2.y - v0.y) + v2.x * (v0.y - v1.y);
		area *= 0.5;
		// abs to account for winding
		return Math.abs(area);
	}
	
	/**
	 * Represents a vertex of a simple polygon with a linked list
	 * running through it.  It also contains the area produced by this
	 * vertex and the adjacent vertices. We also use it to track the
	 * vertex index (for comparing adjacent segments) and the adjacent
	 * segments.
	 * @author William Bittle
	 * @version 4.2.0
	 * @since 4.2.0
	 */
	private final class AreaTrackedVertex extends SimplePolygonVertex implements Comparable<AreaTrackedVertex> {
		/** The triangle area */
		double area;
		
		/**
		 * Minimal constructor.
		 * @param index the vertex index
		 * @param point the vertex point
		 */
		public AreaTrackedVertex(int index, Vector2 point) {
			super(index, point);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(AreaTrackedVertex o) {
			// order based on triangle area
			double diff = this.area - o.area;
			if (diff < 0) {
				return -1;
			} else if (diff > 0) {
				return 1;
			}
			return 0;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return this.point.toString();
		}
	}
}
