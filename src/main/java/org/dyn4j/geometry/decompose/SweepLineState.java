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

import java.util.PriorityQueue;

import org.dyn4j.BinarySearchTree;
import org.dyn4j.Reference;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.resources.Messages;

/**
 * Represents the current state of the SweepLine algorithm.
 * <p>
 * The SweepLine algorithm maintains a DCEL to hold the triangulation, a binary tree for edge
 * searching and the current sweepline intercept value.
 * @author William Bittle
 * @version 3.2.0
 * @since 3.2.0
 */
final class SweepLineState {
	/** The current sweepline y-intercept value */
	final Reference<Double> referenceY;
	
	/** The edge binary tree */
	final BinarySearchTree<SweepLineEdge> tree;
	
	/** The DCEL */
	DoubleEdgeList dcel;
	
	/**
	 * Default constructor.
	 */
	public SweepLineState() {
		this.referenceY = new Reference<Double>(0.0);
		this.tree = new BinarySearchTree<SweepLineEdge>(true);
	}
	
	/**
	 * Returns a priority queue of the points in the given array and initializes
	 * the Binary Tree and DCEL for the SweepLine algorithm.
	 * @param points the array of polygon points
	 * @return PriorityQueue&lt;{@link SweepLineVertex}&gt;
	 */
	final PriorityQueue<SweepLineVertex> initialize(Vector2[] points) {
		// initialize the DCEL
		this.dcel = new DoubleEdgeList(points);
		
		// get the number points
		int size = points.length;
		
		// create a priority queue for the vertices
		PriorityQueue<SweepLineVertex> queue = new PriorityQueue<SweepLineVertex>(size);
		
		SweepLineVertex rootVertex = null;
		SweepLineVertex prevVertex = null;
		
		SweepLineEdge rootEdge = null;
		SweepLineEdge prevEdge = null;
		
		// build the vertices and edges
		for (int i = 0; i < size; i++) {
			// get this vertex point
			Vector2 point = points[i];
			
			// create the vertex for this point
			SweepLineVertex vertex = new SweepLineVertex(point, i);
			// default the type to regular
			vertex.type = SweepLineVertexType.REGULAR;
			vertex.prev = prevVertex;
			
			// set the previous vertex's next pointer
			if (prevVertex != null) {
				prevVertex.next = vertex;
			}
			
			// make sure we save the first vertex so we
			// can wire up the last and first to create
			// a cyclic list
			if (rootVertex == null) {
				rootVertex = vertex;
			}
			
			// get the neighboring points
			Vector2 point1 = points[i + 1 == size ? 0 : i + 1];
			Vector2 point0 = points[i == 0 ? size - 1 : i - 1];
			
			// get the vertex type
			vertex.type = this.getType(point0, point, point1);
			
			// set the previous vertex to this vertex
			prevVertex = vertex;
			// add the vertex to the priority queue
			queue.offer(vertex);
			
			// create the next edge
			SweepLineEdge e = new SweepLineEdge(this.referenceY);
			// the first vertex is this vertex
			e.v0 = vertex;
			
			// compute the slope
			double my = point.y - point1.y;
			if (my == 0.0) {
				e.slope = Double.POSITIVE_INFINITY;
			} else {
				double mx = point.x - point1.x;
				e.slope = (mx / my);
			}
			
			// set the previous edge's end vertex and
			// next edge pointers
			if (prevEdge != null) {
				prevEdge.v1 = vertex;
			}
			
			// make sure we save the first edge so we
			// can wire up the last and first to create
			// a cyclic list
			if (rootEdge == null) {
				rootEdge = e;
			}
			
			// set the vertex's left and right edges
			vertex.left = e;
			vertex.right = prevEdge;
			
			// set the previous edge to this edge
			prevEdge = e;
		}
		
		// set the last edge's end vertex pointer to
		// the first edge's start vertex
		prevEdge.v1 = rootEdge.v0;
		
		// set the previous edge of the first vertex
		rootVertex.right = prevEdge;
		// set the previous vertex of the first vertex
		rootVertex.prev = prevVertex;
		// set the last vertex's next pointer to the
		// first vertex
		prevVertex.next = rootVertex;
		
		// return the priority queue
		return queue;
	}
	
	/**
	 * Returns the vertex type given the previous and next points.
	 * @param point0 the previous point
	 * @param point the vertex point
	 * @param point1 the next point
	 * @return {@link SweepLineVertexType}
	 */
	final SweepLineVertexType getType(Vector2 point0, Vector2 point, Vector2 point1) {
		// create the edge vectors
		Vector2 v1 = point0.to(point);
		Vector2 v2 = point.to(point1);
		
		// check for coincident points
		if (v1.isZero() || v2.isZero()) throw new IllegalArgumentException(Messages.getString("geometry.decompose.coincident"));
		
		// get the angle between the two edges (we assume CCW winding)
		double cross = v1.cross(v2);
		
		boolean pBelowP0 = this.isBelow(point, point0);
		boolean pBelowP1 = this.isBelow(point, point1);
		
		// where is p relative to its neighbors?
		if (pBelowP0 && pBelowP1) {
			// then check if the 
			// if its below both of them then we need
			// to check the interior angle
			if (cross > 0.0) {
				// if the cross product is greater than zero
				// this indicates that the angle is < pi
				
				// this vertex is an end vertex
				return SweepLineVertexType.END;
			} else {
				// this indicates that the angle is pi or greater
				
				// this vertex is a merge vertex
				return SweepLineVertexType.MERGE;
			}
		} else if (!pBelowP0 && !pBelowP1) {
			// if its above both of them then we need
			// to check the interior angle
			if (cross > 0.0) {
				// if the cross product is greater than zero
				// this indicates that the angle is < pi
				
				// this vertex is a start vertex
				return SweepLineVertexType.START;
			} else {
				// this indicates that the angle is pi or greater
				
				// this vertex is a split vertex
				return SweepLineVertexType.SPLIT;
			}
		}
		
		return SweepLineVertexType.REGULAR;
	}
	
	/**
	 * Returns true if the given point p is below the given point q.
	 * <p>
	 * If the point p and q form a horizontal line then p is considered
	 * below if its x coordinate is greater than q's x coordinate.
	 * @param p the point
	 * @param q another point
	 * @return boolean true if p is below q; false if p is above q
	 */
	public boolean isBelow(Vector2 p, Vector2 q) {
		double diff = p.y - q.y;
		if (diff == 0.0) {
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
}
